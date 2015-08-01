package uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;
import uk.ac.glasgow.jagora.trader.impl.zip.*;

import java.util.*;

/**
 * This version of MarketMaker only works for one Stock
 */
public class MarketMakerBasic extends SafeAbstractTrader implements Level2Trader,TradeListener, OrderListener {

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

        //the implementation currently doesn't support adding subsequents stocks on the market
        //also at the moment tww markets will be supported for the same stock only if they have the same warehouse
        marketDatum = new MarketDatum(stockWarehouse);
        positionDatum =
                new StockPositionDatum(marketShare,stockWarehouse.getInitialQuantity(),stockWarehouse.getStock());

        registered = new HashSet<StockExchangeLevel2View>();

    }

    @Override
    public void speak(StockExchangeLevel2View level2View) {
        if (!registered.contains(level2View)) register (level2View);

        //update all positions on market
        updateMarketPositions();

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
        //TODO think of the right amount of stock to put in a position NEED to do this!
        BuyOrder buyOrder = new LimitBuyOrder
                (this,positionDatum.stock,inventory.get(positionDatum.stock),positionDatum.newBuyPrice);
        placeSafeBuyOrder(level1View,buyOrder);
        positionDatum.currentBuyOrder = buyOrder;

        if (positionDatum.currentSellOrder != null)
            cancelSafeSellOrder(level1View,positionDatum.currentSellOrder);

        SellOrder sellOrder = new LimitSellOrder
                (this, positionDatum.stock,inventory.get(positionDatum.stock),positionDatum.newSellPrice);
        placeSafeSellOrder(level1View,sellOrder);
        positionDatum.currentSellOrder = sellOrder;
    }

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


//        Double inventoryAdjustment =
//                (positionDatum.sharesAimed.doubleValue() - inventory.get(positionDatum.stock).doubleValue())
//                /positionDatum.sharesAimed.doubleValue();
//        Long inventoryPriceAdjustment =
//                Math.round(inventoryAdjustment*marketDatum.lastPriceTraded*random.nextDouble());
        Long inventoryPriceAdjustment = 0l;

        if (marketDatum.lastTradeWasSell) {
            positionDatum.setNewBuyPrice (marketDatum.lastPriceTraded - this.spread
                    + PriceLiquidityAdjustment + inventoryPriceAdjustment);

            positionDatum.setNewSellPrice (marketDatum.lastPriceTraded + this.spread);
        }
        else {
            positionDatum.setNewBuyPrice (marketDatum.lastPriceTraded - this.spread);

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

    //TODO something more significant with the trade execution
    @Override
    public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
        marketDatum.setLastPriceTraded(tradeExecutionEvent.price);
        marketDatum.removeLiquidity(tradeExecutionEvent.quantity,tradeExecutionEvent.price);
        marketDatum.setLastTradeDirection(tradeExecutionEvent.isAggressiveSell);
    }


}
