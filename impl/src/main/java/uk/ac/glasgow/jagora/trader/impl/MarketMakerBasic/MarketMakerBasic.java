package uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This implementation of MarketMaker will only work for a single stock
 * on a single or multiple exchanges. The provided stockWarehouse should be
 * consistent throughout the whole usage of the environment to ensure proper
 * functioning of the algorithm.
 */
public class MarketMakerBasic extends SafeAbstractTrader implements Level2Trader,TradeListener, OrderListener {

    private final Stock stock;

    private Random random;

    private Float marketShare;

    private MarketDatum marketDatum;
    private StockPositionDatum positionDatum ;

    private Set<StockExchangeLevel2View> registered;

    private Long spread;



    public MarketMakerBasic (String name, Long cash, Map<Stock, Integer> inventory,
                             StockWarehouse stockWarehouse, Float marketShare,
                             Random random, Long spread){

        super(name,cash,inventory);

        this.marketShare= marketShare;
        this.spread = spread;
        this.random = random;

        this.stock = stockWarehouse.getStock();
        marketDatum = new MarketDatum(stockWarehouse);
        positionDatum =
                new StockPositionDatum(
                        marketShare,stockWarehouse.getInitialQuantity(),stockWarehouse.getStock());

        registered = new HashSet<StockExchangeLevel2View>();

    }

    @Override
    public void speak(StockExchangeLevel2View level2View){
        if (!registered.contains(level2View)) register (level2View);

        //update all positions on market
        updateMarketPositions();
        //Check out the weirdest bug ever?!??!?
        changeMarketPosition(level2View);
    }


    private void register(StockExchangeLevel2View level2View) {
        level2View.registerOrderListener(this);
        level2View.registerTradeListener(this);
        registered.add(level2View);
    }

    private void changeMarketPosition(StockExchangeLevel1View level1View) {

        //if some of these positions are not set yet don't place anything on the market
        if (positionDatum.newBuyPrice == 0l || positionDatum.newSellPrice == 0l)
            return;


        if (positionDatum.currentBuyOrder != null)
            cancelSafeBuyOrder(level1View,positionDatum.currentBuyOrder);

        Integer buyQuantity = positionDatum.sharesAimed;
        if (positionDatum.inventoryAdjustment < -1) {
            //if there' a big imbalance provide a stub quote to preserve inventory
            buyQuantity = Math.round(positionDatum.sharesAimed*0.01f);
        }

        BuyOrder buyOrder = new LimitBuyOrder
                (this,positionDatum.stock,buyQuantity,positionDatum.newBuyPrice);

        positionDatum.currentBuyOrder = placeSafeBuyOrder(level1View,buyOrder) ? buyOrder: null;
        //TODO make some sort of exception if null

        if (positionDatum.currentSellOrder != null)
            cancelSafeSellOrder(level1View,positionDatum.currentSellOrder);

        Integer sellQuantity = inventory.get(stock);
        if (positionDatum.inventoryAdjustment > 1){
            //if there' a big imbalance provide a stub quote to preserve inventory
            sellQuantity = Math.round(inventory.get(stock)*0.1f);
        }

        SellOrder sellOrder = new LimitSellOrder
                (this, positionDatum.stock,sellQuantity,positionDatum.newSellPrice);

        positionDatum.currentSellOrder = placeSafeSellOrder(level1View,sellOrder) ?sellOrder :null;
    }

    //TODO make adjustments work appropriately
    private void updateMarketPositions () {


        //if no available information about last trade,don't place orders yet
        if (marketDatum.lastPriceTraded == 0l)
            return;

        Double liquidityAdjustment = 0.0;
        //if there isn't information regarding liquidity, don't adjust for it
//        if(marketDatum.liquidityInformation()) {
//            liquidityAdjustment =
//                    ((double) marketDatum.buySideLiquidity - (double) marketDatum.sellSideLiquidity)
//                            / (double) marketDatum.buySideLiquidity; //experiment with this
//        }
//
//        Long PriceLiquidityAdjustment =
//                Math.round(liquidityAdjustment*(double)marketDatum.lastPriceTraded* random.nextDouble());
        Long PriceLiquidityAdjustment = 0l;


        Double inventoryAdjustment =
                (positionDatum.sharesAimed.doubleValue() - inventory.get(positionDatum.stock).doubleValue())
                /positionDatum.sharesAimed.doubleValue();
        positionDatum.setInventoryAdjustment(inventoryAdjustment);
        Long inventoryPriceAdjustment =
                Math.round(inventoryAdjustment*marketDatum.lastPriceTraded*random.nextDouble());
       // Long inventoryPriceAdjustment = 0l;

        if (marketDatum.lastTradeWasSell) {
            positionDatum.setNewBuyPrice (marketDatum.lastPriceTraded - this.spread
                    + PriceLiquidityAdjustment + inventoryPriceAdjustment);

            positionDatum.setNewSellPrice (marketDatum.lastPriceTraded + this.spread
            + inventoryPriceAdjustment);
        }
        else {
            positionDatum.setNewBuyPrice (marketDatum.lastPriceTraded - this.spread
            + inventoryPriceAdjustment);

            positionDatum.setNewSellPrice(marketDatum.lastPriceTraded + this.spread -
                    PriceLiquidityAdjustment + inventoryPriceAdjustment);
        }

    }

    @Override
    public void orderEntered(OrderEntryEvent orderEntryEvent) {
        if (orderEntryEvent.orderDirection == OrderEntryEvent.OrderDirection.BUY) {
            marketDatum.addBuySideLiquidity(orderEntryEvent.quantity,orderEntryEvent.price);
        }
        else {
            marketDatum.addSellSideLiquidity(orderEntryEvent.quantity,orderEntryEvent.price);
        }
    }


    @Override
    public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
        marketDatum.setLastPriceTraded(tradeExecutionEvent.price);
        marketDatum.removeLiquidity(tradeExecutionEvent.quantity,tradeExecutionEvent.price);
        marketDatum.setLastTradeDirection(tradeExecutionEvent.isAggressiveSell);
    }


    @Override
    public void orderCancelled(OrderEntryEvent orderEntryEvent) {
        if (orderEntryEvent.orderDirection == OrderEntryEvent.OrderDirection.SELL)
            marketDatum.removeSellSideLiquidity(orderEntryEvent.quantity);
        else
            marketDatum.removeBuySideLiquidity(orderEntryEvent.quantity);
    }

//    public Integer getBuySideLiquidity() {return marketDatum.buySideLiquidity;}
//    public Integer getSellSideLiquidity() {return marketDatum.sellSideLiquidity;}
}
