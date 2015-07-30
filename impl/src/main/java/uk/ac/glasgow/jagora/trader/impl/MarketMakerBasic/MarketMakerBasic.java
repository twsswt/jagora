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


public class MarketMakerBasic extends SafeAbstractTrader implements Level2Trader,TradeListener, OrderListener {

    private Random random;

    private Float marketShare;

    private Map<Stock, MarketDatum> marketData;
    private Map<Stock, StockPositionDatum> positionData = new HashMap<>();

    private Set<StockExchangeLevel2View> registered;

    private Long spread;



    public MarketMakerBasic (String name, Long cash, Map<Stock, Integer> inventory,
                             List<StockWarehouse> stockWarehouses, Float marketShare,
                             Random random, Long spread){

        super(name,cash,inventory);

        this.marketShare= marketShare;
        this.spread = spread;
        this.random = random;

        //the implementation currently doesn't support adding subsequents stocks on the market
        //also at the moment tww markets will be supported for the same stock only if they have the same warehouse
        marketData = new HashMap<Stock,MarketDatum>();
        for(StockWarehouse stockWarehouse : stockWarehouses) {
            marketData.put(stockWarehouse.getStock(), new MarketDatum(stockWarehouse));
            positionData.put(stockWarehouse.getStock(),
                    new StockPositionDatum
                            (marketShare,stockWarehouse.getInitialQuantity(),stockWarehouse.getStock()));
        }

        registered = new HashSet<StockExchangeLevel2View>();

    }

    @Override
    public void speak(StockExchangeLevel2View level2View) {
        if (!registered.contains(level2View)) register (level2View);

        //update all positions on market
        marketData.keySet().forEach((stock) -> updateMarketPositions(stock));

        for (StockPositionDatum positionDatum: positionData.values())
            changeMarketPosition(level2View, positionDatum);
    }



    private void register(StockExchangeLevel2View level2View) {
        level2View.registerOrderListener(this);
        level2View.registerTradeListener(this);
        registered.add(level2View);
    }

    //TODO change this to something more sensible in terms of not having to make new orders all the time?
    private void changeMarketPosition(StockExchangeLevel1View level1View, StockPositionDatum positionDatum) {

        //TODO check if you need to make the cancellation safe
        cancelSafeBuyOrder(level1View,positionDatum.currentBuyOrder);
        BuyOrder buyOrder = new LimitBuyOrder
                (this,positionDatum.stock,inventory.get(positionDatum.stock),positionDatum.newBuyPrice);
        placeSafeBuyOrder(level1View,buyOrder);

        cancelSafeSellOrder(level1View,positionDatum.currentSellOrder);
        SellOrder sellOrder = new LimitSellOrder
                (this, positionDatum.stock,inventory.get(positionDatum.stock),positionDatum.newSellPrice);
        placeSafeSellOrder(level1View,sellOrder);
    }

    private void updateMarketPositions (Stock stock) {

        MarketDatum marketDatum = marketData.get(stock);
        StockPositionDatum positionDatum = positionData.get(stock);

        Double liquidityAdjustment =
                ((double)marketDatum.buySideLiquidity - (double)marketDatum.sellSideLiquidity)
                /(double)marketDatum.buySideLiquidity; //experiment with this

        Long PriceLiquidityAdjustment =
                Math.round(liquidityAdjustment*(double)marketDatum.lastPriceTraded* random.nextDouble());


        Double inventoryAdjustment =
                (positionDatum.sharesAimed.doubleValue() - inventory.get(stock).doubleValue())
                /positionDatum.sharesAimed.doubleValue();
        Long inventoryPriceAdjustment =
                Math.round(inventoryAdjustment*marketDatum.lastPriceTraded*random.nextDouble());

        if (marketDatum.lastTradeWasSell) {
            positionDatum.setNewBuyPrice (marketDatum.lastPriceTraded - this.spread
                    + PriceLiquidityAdjustment + inventoryPriceAdjustment);

            positionDatum.setNewSellPrice (marketDatum.lastPriceTraded + this.spread);

        }
        else {
            positionDatum.setNewBuyPrice (marketDatum.lastPriceTraded - this.spread);

            positionDatum.setNewSellPrice(marketDatum.lastPriceTraded + this.spread -
                    PriceLiquidityAdjustment - inventoryPriceAdjustment);
        }

    }
    //TODO include the price of the order?
    @Override
    public void orderEntered(OrderEntryEvent orderEntryEvent) {
        MarketDatum marketDatum = marketData.get(orderEntryEvent.stock);
        if (orderEntryEvent.orderDirection == OrderEntryEvent.OrderDirection.BUY) {
            marketDatum.addBuySideLiquidity(orderEntryEvent.quantity);
        }
        else {
            marketDatum.addSellSideLiquidity(orderEntryEvent.quantity);
        }
    }

    //TODO something more significant with the trade execution
    @Override
    public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
        MarketDatum marketDatum = marketData.get(tradeExecutionEvent.stock);
        marketDatum.setLastPriceTraded(tradeExecutionEvent.price);
        marketDatum.removeLiquidity(tradeExecutionEvent.quantity);
    }


}
