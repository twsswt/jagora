package uk.ac.glasgow.jagora.test;


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
    private StubTrader dean;
    private StubTrader jojo;

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

        dean = new StubTraderBuilder("dean", 50000l)
                .addStock(lemons,50)
                .build();

        jojo = new StubTraderBuilder("jojo", 50000l)
                .addStock(lemons,80)
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


    @Test
    public void testSeveralStopLossSellOrders(){
        SerialTickerTapeObserver tickerTapeObserver =new SerialTickerTapeObserver();

        DefaultStockExchange market =
                new DefaultStockExchange(world,tickerTapeObserver,marketFactory);

        SellTradePriceListener listener1 = new SellTradePriceListener(99l,alice,lemons,market.createLevel1View());
        StockExchangeLevel1View level1View = market.createLevel1View() ;
        level1View.registerPriceListener(listener1);

        SellTradePriceListener listener2 = new SellTradePriceListener(95l,dean,lemons,market.createLevel1View());
        level1View.registerPriceListener(listener2);

        SellTradePriceListener listener3 = new SellTradePriceListener(96l,jojo,lemons,market.createLevel1View());
        level1View.registerPriceListener(listener3);

        SellOrder sellOrder1 = new LimitSellOrder(george,lemons, 100, 100l);
        george.supplyOrder(sellOrder1);
        george.speak(market.createLevel1View());

        BuyOrder buyOrder1 = new LimitBuyOrder(bruce,lemons,100, 100l);
        george.supplyOrder(buyOrder1);
        george.speak(market.createLevel1View());

        //create one more buy order to check stoploss is not activated
        BuyOrder buyOrder2 = new LimitBuyOrder(bruce,lemons, 150,98l );
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

        assertEquals("", alice.getInventory(lemons), (Integer) 50);
        assertEquals("", alice.getCash(), (Long) 54900l);//50000 + 50*98
        assertEquals("", dean.getInventory(lemons), (Integer) 50);
        assertEquals("", dean.getCash(), (Long) 50000l);
        assertEquals("", jojo.getInventory(lemons), (Integer) 80);
        assertEquals("", jojo.getCash(), (Long) 50000l);


        BuyOrder buyOrder3 = new LimitBuyOrder(bruce,lemons, 100, 95l);
        bruce.supplyOrder(buyOrder3);
        bruce.speak(market.createLevel1View());

        BuyOrder buyOrder4 = new LimitBuyOrder(george,lemons,300,94l);
        george.supplyOrder(buyOrder4);
        george.speak(market.createLevel1View());

        //do the rest of alice market order
        market.doClearing();
        //now deal with dean order
        market.doClearing();

        assertEquals("", alice.getInventory(lemons), (Integer) 0); //all should be sold out now
        assertEquals("", alice.getCash(), (Long)( 54900l + 50l*95l)); //alice should sell 50 at 95
        assertEquals("", jojo.getInventory(lemons), (Integer) 0); //all should be sold
        assertEquals("", jojo.getCash(), (Long) (50000l + 50l*95l + 30l*94l)); //jojo should get the other 50 at 95 and 30 at 97
        assertEquals("", dean.getInventory(lemons), (Integer) 0);//only 50 should be bought
        assertEquals("", dean.getCash(), (Long)( 50000l + 50l*94l)); //this should be the rest at the lowest price

//        assertEquals("",tickerTapeObserver.getBuyOrderHistory(lemons).size(),4);
//        assertEquals("",tickerTapeObserver.getSellOrderHistory(lemons).size(),5);

    }

    //depends on the implementation of the stoplosssBuy order (at the moment is doubling the inventory)
    @Test
    public void testSeveralStopLossBuyOrders () {
        DefaultStockExchange market =
                new DefaultStockExchange(world,	new SerialTickerTapeObserver(),	marketFactory);

        BuyTradePriceListener listener = new BuyTradePriceListener(110l,alice,lemons,market.createLevel1View());
        StockExchangeLevel1View level1View = market.createLevel1View() ;
        level1View.registerPriceListener(listener);

        BuyTradePriceListener listener1 = new BuyTradePriceListener(130l,dean,lemons,market.createLevel1View());
        level1View.registerPriceListener(listener1);

        BuyTradePriceListener listener2 = new BuyTradePriceListener(125l,jojo,lemons,market.createLevel1View());
        level1View.registerPriceListener(listener2);

        BuyOrder buyOrder1 = new LimitBuyOrder(bruce,lemons, 100, 100l);
        bruce.supplyOrder(buyOrder1);
        bruce.speak(market.createLevel1View());

        SellOrder sellOrder1 = new LimitSellOrder(george, lemons, 100, 100l);
        george.supplyOrder(sellOrder1);
        george.speak(market.createLevel1View());

        SellOrder sellOrder2 = new LimitSellOrder(george, lemons, 150,120l);
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

        assertEquals("", alice.getInventory(lemons), (Integer) 150);
        assertEquals("", alice.getCash(), (Long) 44000l);//50000 - 50*120

        SellOrder sellOrder3 = new LimitSellOrder(george,lemons,100,130l);
        george.supplyOrder(sellOrder3);
        george.speak(market.createLevel1View());

        SellOrder sellOrder4 = new LimitSellOrder(george,lemons,100,140l);
        george.supplyOrder(sellOrder4);
        george.speak(market.createLevel1View());

        market.doClearing();
        //now the others' orders
        market.doClearing();


        assertEquals("", alice.getInventory(lemons), (Integer) 200);
        assertEquals("", alice.getCash(), (Long)( 50000l - 50l*120l - 50l*130l));
        assertEquals("", jojo.getInventory(lemons), (Integer)160); //doubled
        assertEquals("", jojo.getCash(), (Long) (50000l -50l*130l-30l*140l));
        assertEquals("", dean.getInventory(lemons), (Integer) 100);//doubled
        assertEquals("", dean.getCash(), (Long)( 50000l -50l*140l));

    }

}
