package uk.ac.glasgow.jagora.test;


import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.pricer.impl.SellOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTrader;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.impl.BuyTradePriceListener;
import uk.ac.glasgow.jagora.ticker.impl.SellTradePriceListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

import static org.junit.Assert.assertEquals;

public class StopLossTradeTest {

    private StubTrader alice;
    private StubTrader george;
    private StubTrader bruce;

    private Stock lemons = new Stock("lemons");

    private SimpleSerialWorld world;
    private MarketFactory marketFactory;

    @Before
    public void setUp() throws  Exception{

        alice = new StubTraderBuilder("alice",50000l)
                .addStock(lemons, 100)
                .build();

        george = new StubTraderBuilder("george", 50000l)
                .addStock(lemons,1000)
                .build();

        bruce = new StubTraderBuilder("bruce", 50000l)
                .build();

        world = new SimpleSerialWorld(1000l);
        marketFactory = new ContinuousOrderDrivenMarketFactory(new SellOrderPricer());

    }

    @Test
    public void testStopLoss (){

        DefaultStockExchange market =
                new DefaultStockExchange(world,	new SerialTickerTapeObserver(),	marketFactory);

        SellTradePriceListener listener = new SellTradePriceListener(99l,alice,lemons,market.createLevel1View());
        StockExchangeLevel1View level1View = market.createLevel1View() ;
        level1View.registerPriceListener(listener);

        SellOrder sellOrder1 = new LimitSellOrder(george,lemons, 100, 100l);
        george.supplyOrder(sellOrder1);
        george.speak(market.createLevel1View());

        BuyOrder buyOrder1 = new LimitBuyOrder(bruce,lemons,100, 100l);
        george.supplyOrder(buyOrder1);
        george.speak(market.createLevel1View());

        //create one more buy order to check stoploss is not activated
        BuyOrder buyOrder2 = new LimitBuyOrder(bruce,lemons, 200,98l );
        bruce.supplyOrder(buyOrder2);
        bruce.speak(market.createLevel1View());

        market.doClearing();
        //first market transaction to check that a false order is not triggered
        assertEquals( "", alice.getInventory(lemons) ,(Integer) 100);

        SellOrder sellOrder2 = new LimitSellOrder(george,lemons, 100, 97l);
        george.supplyOrder(sellOrder2);
        george.speak(market.createLevel1View());
        //do the first trade
        market.doClearing();
        //now deal with alice order
        market.doClearing();
        assertEquals("", alice.getInventory(lemons), (Integer) 0);
        assertEquals("", alice.getCash(), (Long) 59800l);


    }

    @Test
    public void testStopLossBuy () {

        DefaultStockExchange market =
                new DefaultStockExchange(world,	new SerialTickerTapeObserver(),	marketFactory);

        BuyTradePriceListener listener = new BuyTradePriceListener(110l,alice,lemons,market.createLevel1View());
        StockExchangeLevel1View level1View = market.createLevel1View() ;
        level1View.registerPriceListener(listener);

        BuyOrder buyOrder1 = new LimitBuyOrder(bruce,lemons, 100, 100l);
        bruce.supplyOrder(buyOrder1);
        bruce.speak(market.createLevel1View());

        SellOrder sellOrder1 = new LimitSellOrder(george, lemons, 100, 100l);
        george.supplyOrder(sellOrder1);
        george.speak(market.createLevel1View());

        SellOrder sellOrder2 = new LimitSellOrder(george, lemons, 200,120l);
        george.supplyOrder(sellOrder2);
        george.speak(market.createLevel1View());

        market.doClearing();

        assertEquals("", alice.getInventory(lemons), (Integer) 100);

        BuyOrder buyOrder2 = new LimitBuyOrder(bruce,lemons, 100, 120l);
        bruce.supplyOrder(buyOrder2);
        bruce.speak(market.createLevel1View());

        market.doClearing();
        //now alice order
        market.doClearing();

        assertEquals("", alice.getInventory(lemons), (Integer) 200);
        assertEquals("", alice.getCash(), (Long) 38000l);

    }
}
