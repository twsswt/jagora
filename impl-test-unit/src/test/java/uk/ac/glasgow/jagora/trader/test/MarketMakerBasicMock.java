package uk.ac.glasgow.jagora.trader.test;

import org.easymock.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.impl.AbstractBuyOrder;
import uk.ac.glasgow.jagora.impl.AbstractOrder;
import uk.ac.glasgow.jagora.impl.DefaultTrade;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasic;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasicBuilder;

import static org.easymock.EasyMock.*;


public class MarketMakerBasicMock {

    private StockWarehouse lemonsWarehouse;
    private Stock lemons;

    private MarketMakerBasicBuilder traderBuilder;

    @Rule
    public EasyMockRule rule = new EasyMockRule(this);

    @Mock
    StockExchangeLevel2View mockExchange;

    @Before
    public void setUp() {
        lemons = new Stock("lemons");
        lemonsWarehouse = new StockWarehouse(lemons, 10000 );

        traderBuilder = new MarketMakerBasicBuilder("Goldman")
                .addStockWarehouse(lemonsWarehouse)
                .setCash(1000000l)
                .setMarketShare(0.1f)
                .setSeed(1)
                .setSpread(0.01)
                .addStock(lemons, Math.round(lemonsWarehouse.getInitialQuantity() * 0.1f));
    }

    @Test
    public void testInventoryPriceAdjustment () throws Exception{
        mockExchange = createNiceMock(StockExchangeLevel2View.class);

        MarketMakerBasic marketMaker = traderBuilder.build();

        BuyOrder order = null;

        Capture<BuyOrder> captured = newCapture();
        //TODO can't figure how to make this work
        //mockExchange.placeBuyOrder(capture(captured));
        //mockExchange.placeBuyOrder(new LimitBuyOrder(marketMaker,lemons, 1000,1000l));

        replay(mockExchange);



        //Put some orders in the marketMaker so that liquidity calculations are not affected
        marketMaker.orderEntered(new OrderEntryEvent(null,null,lemons, 1000,10000l, OrderEntryEvent.OrderDirection.BUY));
        marketMaker.orderEntered(new OrderEntryEvent(null,null,lemons, 1000,10000l, OrderEntryEvent.OrderDirection.SELL));

        //Put at least one trade executed so the pricing algorithm can function properly
        marketMaker.tradeExecuted(new TradeExecutionEvent(lemons,null,null, null, 10000l, 500, true ));


        marketMaker.speak(mockExchange);

        //balance of inventory is broken with the buy of this stock
        marketMaker.buyStock(new DefaultTrade(lemons,10000,10l,null,new LimitBuyOrder(marketMaker,lemons,null,null),true));

        //now as there is more stock than the targeted market share, prices should be going down
        marketMaker.speak(mockExchange);

        verify(mockExchange);


    }

    @Test
    public void testBuyLiquidityImbalancePriceAdjustment() {
        MarketMakerBasic marketMaker = traderBuilder.build();

        marketMaker.orderEntered(new OrderEntryEvent(null,null,lemons, 1000,10000l, OrderEntryEvent.OrderDirection.BUY));
        marketMaker.orderEntered(new OrderEntryEvent(null,null,lemons, 1000,10000l, OrderEntryEvent.OrderDirection.SELL));

        //Algorithm for spread increase will work only if last trade was
        marketMaker.tradeExecuted(new TradeExecutionEvent(lemons,null,null, null, 10000l, 500, false ));

        marketMaker.speak(mockExchange);

        marketMaker.orderEntered(new OrderEntryEvent(null,null,lemons, 1000,1000l, OrderEntryEvent.OrderDirection.BUY));

        //Now that there is imbalance in liquidity - more buy desire, spread should increase
        // Sell price should be going up
        marketMaker.speak(mockExchange);
    }

    @Test
    public void testSellLiquidityImbalancePriceAdjustment () {

        MarketMakerBasic marketMaker = traderBuilder.build();

        marketMaker.orderEntered(new OrderEntryEvent(null,null,lemons, 1000,10000l, OrderEntryEvent.OrderDirection.BUY));
        marketMaker.orderEntered(new OrderEntryEvent(null,null,lemons, 1000,10000l, OrderEntryEvent.OrderDirection.SELL));

        marketMaker.tradeExecuted(new TradeExecutionEvent(lemons,null,null, null, 10000l, 500, true ));

        marketMaker.speak(mockExchange);

        marketMaker.orderEntered(new OrderEntryEvent(null,null,lemons, 1000,1000l, OrderEntryEvent.OrderDirection.SELL));

        //Now that there is imbalance in liquidity - more buy desire, spread should increase
        // Buy  price should be going down
        marketMaker.speak(mockExchange);
    }


}

