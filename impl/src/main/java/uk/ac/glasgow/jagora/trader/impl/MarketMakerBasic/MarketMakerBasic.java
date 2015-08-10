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

    private Double spreadPercentage;

    private Double inventoryAdjustmentInfluence;
    private Double liquidityAdjustmnetInfluence;


    public MarketMakerBasic (String name, Long cash, Map<Stock, Integer> inventory,
                             StockWarehouse stockWarehouse, Float marketShare,
                             Random random, Double spreadPercentage,
                             Double inventoryAdjustmentInfluence,
                             Double liquidityAdjustmnetInfluence){

        super(name,cash,inventory);

        this.marketShare= marketShare;
        this.spreadPercentage = spreadPercentage;
        this.random = random;

        this.stock = stockWarehouse.getStock();
        marketDatum = new MarketDatum(stockWarehouse);
        positionDatum =
                new StockPositionDatum(
                        marketShare,stockWarehouse.getInitialQuantity(),stockWarehouse.getStock());

        registered = new HashSet<StockExchangeLevel2View>();

        this.inventoryAdjustmentInfluence = inventoryAdjustmentInfluence;
        this.liquidityAdjustmnetInfluence = liquidityAdjustmnetInfluence;

    }

    /**
     * At the moments some trades must have occurred before speak works properly
     * @param level2View
     */
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


    private void updateMarketPositions () {


        //if no available information about last trade,don't place orders yet
        if (marketDatum.lastPriceTraded == 0l)
            return;

        positionDatum.spread = Math.round(spreadPercentage*marketDatum.lastPriceTraded.doubleValue());


        Long PriceLiquidityAdjustment = liquidityPriceCalculation();


        Long inventoryPriceAdjustment = inventoryPriceCalculation();

        if (marketDatum.lastTradeWasSell) {
            positionDatum.setNewBuyPrice (marketDatum.lastPriceTraded - positionDatum.spread
                    + PriceLiquidityAdjustment + inventoryPriceAdjustment);

            positionDatum.setNewSellPrice (marketDatum.lastPriceTraded + positionDatum.spread
            + inventoryPriceAdjustment);
        }
        else {
            positionDatum.setNewBuyPrice (marketDatum.lastPriceTraded - positionDatum.spread
            + inventoryPriceAdjustment);

            positionDatum.setNewSellPrice(marketDatum.lastPriceTraded + positionDatum.spread -
                    PriceLiquidityAdjustment + inventoryPriceAdjustment);
        }

        if (positionDatum.newBuyPrice >= positionDatum.newSellPrice)
            fixPriceAnomalities();

    }

    private Long liquidityPriceCalculation () {
        Double liquidityAdjustment = 0.0;
        //if there isn't information regarding liquidity, don't adjust for it
        if(marketDatum.liquidityInformation()) {
            liquidityAdjustment =
                    ((double) marketDatum.buySideLiquidity - (double) marketDatum.sellSideLiquidity)
                            / (double) marketDatum.buySideLiquidity; //experiment with this
        }

        return Math.round(
                liquidityAdjustment*positionDatum.spread.doubleValue()
                        * random.nextDouble()*liquidityAdjustmnetInfluence);
    }

    private Long inventoryPriceCalculation () {
        Double inventoryAdjustment =
                (positionDatum.sharesAimed.doubleValue() - inventory.get(positionDatum.stock).doubleValue())
                        /positionDatum.sharesAimed.doubleValue();
        positionDatum.setInventoryAdjustment(inventoryAdjustment);
        Double toReturn =
                inventoryAdjustment*positionDatum.spread.doubleValue()*
                        random.nextDouble()*inventoryAdjustmentInfluence;
        return  Math.round(toReturn);
    }

    private void fixPriceAnomalities () {
        //isolate the intervening price and fix it
        if (positionDatum.newSellPrice < marketDatum.lastPriceTraded)
            positionDatum.newSellPrice = positionDatum.newBuyPrice + 1l;
        else
            positionDatum.newBuyPrice = positionDatum.newSellPrice - 1l;

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
