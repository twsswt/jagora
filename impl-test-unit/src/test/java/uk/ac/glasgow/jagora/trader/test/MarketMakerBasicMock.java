package uk.ac.glasgow.jagora.trader.test;

import org.easymock.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.impl.*;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasic;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasicBuilder;

import java.util.Scanner;

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
        lemonsWarehouse = new StockWarehouse(lemons, 10000);

        traderBuilder = new MarketMakerBasicBuilder("Goldman")
                .addStockWarehouse(lemonsWarehouse)
                .setCash(100000000l)
                .setMarketShare(0.1f)
                .setSeed(1)
                .setSpread(0.01)
                .addStock(lemons, Math.round(lemonsWarehouse.getInitialQuantity() * 0.1f));


    }


    /**
     * In the algorithm an order is placed and subsequently
     * cancelled on every speak() turn of the marketMaker.
     * In this test the second place of an order should be
     * at a lower price because of the inventory excess
     * - more inventory of a stock than the one aimed for
     */
    @Test
    public void testInventoryPriceAdjustment() throws Exception {
        mockExchange = createMock(StockExchangeLevel2View.class);

        MarketMakerBasic marketMaker = traderBuilder.build();

        //just necessities for operation
        mockExchange.registerOrderListener(marketMaker);
        mockExchange.registerTradeListener(marketMaker);

        SellOrder sellOrder1 = new LimitSellOrder(marketMaker, lemons, 1000, 10100l);
        SellOrder sellOrder2 = new LimitSellOrder(marketMaker, lemons, 2000, 10067l);
        mockExchange.placeSellOrder(sellOrder1);
        mockExchange.cancelSellOrder(sellOrder1);
        mockExchange.placeSellOrder(sellOrder2);


        BuyOrder buyOrder1 = new LimitBuyOrder(marketMaker, lemons, 1000, 9900l);
        BuyOrder buyOrder2 = new LimitBuyOrder(marketMaker, lemons, 1000, 9867l);
        mockExchange.placeBuyOrder(buyOrder1);
        mockExchange.cancelBuyOrder(buyOrder1);
        mockExchange.placeBuyOrder(buyOrder2);


        replay(mockExchange);


        //Put some orders in the marketMaker so that liquidity calculations are not affected
        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 10000l, OrderEntryEvent.OrderDirection.BUY));
        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 10000l, OrderEntryEvent.OrderDirection.SELL));

        //Put at least one trade executed so the pricing algorithm can function properly
        marketMaker.tradeExecuted(new TradeExecutionEvent(lemons, null, null, null, 10000l, 500, true));


        marketMaker.speak(mockExchange);

        //balance of inventory is broken with the buy of this stock
        marketMaker.buyStock(new DefaultTrade(lemons, 1000, 10l, null, new LimitBuyOrder(marketMaker, lemons, null, null), true));

        //now as there is more stock than the targeted market share, prices should be going down
        marketMaker.speak(mockExchange);

        verify(mockExchange);


    }

    /**
     * In the algorithm an order is placed and subsequently
     * cancelled on every speak() turn of the marketMaker.
     * In this test buy liquidity is subsequently increased,
     * which should increase the spread on the side that
     * was last active - Sell in this case. The second
     * sell order should have a higher price than the first one
     */
    @Test
    public void testBuyLiquidityImbalancePriceAdjustment() {


        mockExchange = createMock(StockExchangeLevel2View.class);

        MarketMakerBasic marketMaker = traderBuilder.build();

        mockExchange.registerOrderListener(marketMaker);
        mockExchange.registerTradeListener(marketMaker);


        SellOrder sellOrder1 = new LimitSellOrder(marketMaker, lemons, 1000, 10100l);
        SellOrder sellOrder2 = new LimitSellOrder(marketMaker, lemons, 1000, 10114l);
        mockExchange.placeSellOrder(sellOrder1);
        mockExchange.cancelSellOrder(sellOrder1);
        mockExchange.placeSellOrder(sellOrder2);

        BuyOrder buyOrder1 = new LimitBuyOrder(marketMaker, lemons, 1000, 9900l);
        mockExchange.placeBuyOrder(buyOrder1);
        mockExchange.cancelBuyOrder(buyOrder1);
        mockExchange.placeBuyOrder(buyOrder1);

        replay(mockExchange);

        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 10000l, OrderEntryEvent.OrderDirection.BUY));
        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 10000l, OrderEntryEvent.OrderDirection.SELL));

        //Algorithm for spread increase will work only if last trade was
        marketMaker.tradeExecuted(new TradeExecutionEvent(lemons, null, null, null, 10000l, 500, false));

        marketMaker.speak(mockExchange);

        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 1000l, OrderEntryEvent.OrderDirection.BUY));

        //Now that there is imbalance in liquidity - more buy desire, spread should increase
        // Sell price should be going up
        marketMaker.speak(mockExchange);

        verify(mockExchange);
    }

    /**
     * In the algorithm an order is placed and subsequently
     * cancelled on every speak() turn of the marketMaker.
     * In this test Sell Liquidity is subsequently increased,
     * which should increase the spread on the side that was
     * last active - buy in this case. The second buy order should
     * have a lower price than the first one.
     */
    @Test
    public void testSellLiquidityImbalancePriceAdjustment() {
        mockExchange = createMock(StockExchangeLevel2View.class);

        MarketMakerBasic marketMaker = traderBuilder.build();

        mockExchange.registerOrderListener(marketMaker);
        mockExchange.registerTradeListener(marketMaker);

        BuyOrder buyOrder1 = new LimitBuyOrder(marketMaker, lemons, 1000, 9900l);
        BuyOrder buyOrder2 = new LimitBuyOrder(marketMaker, lemons, 1000, 9858l);
        mockExchange.placeBuyOrder(buyOrder1);
        mockExchange.cancelBuyOrder(buyOrder1);
        mockExchange.placeBuyOrder(buyOrder2);


        SellOrder sellOrder1 = new LimitSellOrder(marketMaker, lemons, 1000, 10100l);
        mockExchange.placeSellOrder(sellOrder1);
        mockExchange.cancelSellOrder(sellOrder1);
        mockExchange.placeSellOrder(sellOrder1);

        replay(mockExchange);

        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 10000l, OrderEntryEvent.OrderDirection.BUY));
        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 10000l, OrderEntryEvent.OrderDirection.SELL));

        marketMaker.tradeExecuted(new TradeExecutionEvent(lemons, null, null, null, 10000l, 500, true));

        marketMaker.speak(mockExchange);

        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 1000l, OrderEntryEvent.OrderDirection.SELL));

        //Now that there is imbalance in liquidity - more buy desire, spread should increase
        // Buy  price should be going down
        marketMaker.speak(mockExchange);

        verify(mockExchange);
    }


    /**
     * In the algorithm an order is placed and subsequently
     * cancelled on every speak() turn of the marketMaker.
     * In this test the influence on liquidity imbalance is
     * increased to a point where the sell order, that is affected
     * should cross the buyOrder. This should not be possible and in
     * a mechanism which increases the price of the sell order to 1l
     * more than the buy order.
     */
    @Test
    public void testFixPriceAnomalies() {

        mockExchange = createMock(StockExchangeLevel2View.class);

        MarketMakerBasic marketMaker = traderBuilder
                .setLiquidityAdjustmentInfluence(5.0) //This being the changing factor
                .setInventoryAdjustmnetInfluence(1.0)
                .build();

        mockExchange.registerOrderListener(marketMaker);
        mockExchange.registerTradeListener(marketMaker);


        SellOrder sellOrder1 = new LimitSellOrder(marketMaker, lemons, 1000, 10100l);
        SellOrder sellOrder2 = new LimitSellOrder(marketMaker, lemons, 1000, 9901l);
        mockExchange.placeSellOrder(sellOrder1);
        mockExchange.cancelSellOrder(sellOrder1);
        mockExchange.placeSellOrder(sellOrder2);

        BuyOrder buyOrder1 = new LimitBuyOrder(marketMaker, lemons, 1000, 9900l);
        mockExchange.placeBuyOrder(buyOrder1);
        mockExchange.cancelBuyOrder(buyOrder1);
        mockExchange.placeBuyOrder(buyOrder1);

        replay(mockExchange);

        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 10000l, OrderEntryEvent.OrderDirection.BUY));
        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 10000l, OrderEntryEvent.OrderDirection.SELL));

        marketMaker.tradeExecuted(new TradeExecutionEvent(lemons, null, null, null, 10000l, 500, false));

        marketMaker.speak(mockExchange);

        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 1000l, OrderEntryEvent.OrderDirection.SELL));

        marketMaker.speak(mockExchange);

        verify(mockExchange);
    }

    /**
     * In the algorithm an order is placed and subsequently
     * cancelled on every speak() turn of the marketMaker.
     * In this test inventory is imbalanced after the
     * first turn to a point, where MarketMaker will provide
     * a stub quote - A buyOrder of only 0.1 of the shares
     * it is usually trying to buy is put
     */
    @Test
    public void testStubQuoteAdjustment() throws Exception {
        mockExchange = createMock(StockExchangeLevel2View.class);
        
        MarketMakerBasic marketMaker = traderBuilder
                .setLiquidityAdjustmentInfluence(5.0) //This being the changing factor
                .setInventoryAdjustmnetInfluence(1.0)
                .build();

        mockExchange.registerOrderListener(marketMaker);
        mockExchange.registerTradeListener(marketMaker);

        BuyOrder buyOrder1 = new LimitBuyOrder(marketMaker, lemons, 1000, 9900l);
        BuyOrder buyOrder2 = new LimitBuyOrder(marketMaker, lemons, 10, 9830l);
        mockExchange.placeBuyOrder(buyOrder1);
        mockExchange.cancelBuyOrder(buyOrder1);
        mockExchange.placeBuyOrder(buyOrder2);

        SellOrder sellOrder1 = new LimitSellOrder(marketMaker, lemons, 1000, 10100l);
        SellOrder sellOrder2 = new LimitSellOrder(marketMaker, lemons, 3100, 10030l);
        mockExchange.placeSellOrder(sellOrder1);
        mockExchange.cancelSellOrder(sellOrder1);
        mockExchange.placeSellOrder(sellOrder2);


        replay(mockExchange);

        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 10000l, OrderEntryEvent.OrderDirection.BUY));
        marketMaker.orderEntered(new OrderEntryEvent(null, null, lemons, 1000, 10000l, OrderEntryEvent.OrderDirection.SELL));

        marketMaker.tradeExecuted(new TradeExecutionEvent(lemons, null, null, null, 10000l, 500, false));

        marketMaker.speak(mockExchange);

        //balance of inventory is broken with the buy of this stock
        marketMaker.buyStock(new DefaultTrade(lemons, 2100, 10l, null, new LimitBuyOrder(marketMaker, lemons, null, null), true));

        marketMaker.speak(mockExchange);

        verify(mockExchange);

    }

}

