package uk.ac.glasgow.jagora.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.*;
import uk.ac.glasgow.jagora.pricer.impl.SellOrderPricer;
import uk.ac.glasgow.jagora.test.stub.ManualTickWorld;
import uk.ac.glasgow.jagora.test.stub.StubTrader;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

public class MarketOrderTest {

    private static final Stock lemons = new Stock("lemons");

    private StubTrader alice;
    private StubTrader bruce;
    private StubTrader george;

    private SimpleSerialWorld world;
    private MarketFactory marketFactory;


    @Before
    public void setUp() throws Exception {

        alice =
                new StubTraderBuilder("alice", 50000l)
                .build();

        bruce =
                new StubTraderBuilder("bruce", 50000l)
                .addStock(lemons, 5000)
                .build();

        george =
                new StubTraderBuilder("george", 50000l)
                .addStock(lemons, 5000)
                .build();

        world = new SimpleSerialWorld(1000l);
        marketFactory = new ContinuousOrderDrivenMarketFactory(new SellOrderPricer());


    }

    @Test
    public void testMarketBuyOrder(){

        DefaultStockExchange market =
                new DefaultStockExchange(world,	new SerialTickerTapeObserver(),	marketFactory);

        //produce two sell orders and make sure that market order is buying from the lower one
        SellOrder sellOrder1 = new LimitSellOrder(bruce, lemons, 3000, 100l);
        bruce.supplyOrder(sellOrder1);
        bruce.speak(market.createLevel1View());

        SellOrder sellOrder2 = new LimitSellOrder(george, lemons, 1000, 150l);
        george.supplyOrder(sellOrder2);
        george.speak(market.createLevel1View());

        MarketBuyOrder buyOrder = new MarketBuyOrder(alice, lemons, 100, market.createLevel1View());
        alice.supplyOrder(buyOrder);
        alice.speak(market.createLevel1View());

        market.doClearing();

        Long aliceCash= alice.getCash();
        assertEquals( "", aliceCash ,(Long) 40000l);
        assertEquals("", alice.getInventory(lemons),(Integer) 100 );

        assertEquals("", bruce.getInventory(lemons) , (Integer) 4900);

        assertEquals("", george.getInventory(lemons), (Integer) 5000);

    }

    @Test
    public void testMarketSellOrder (){

        DefaultStockExchange market =
                new DefaultStockExchange(world,	new SerialTickerTapeObserver(),	marketFactory);

        BuyOrder buyOrder = new LimitBuyOrder(alice, lemons, 100, 100l);
        alice.supplyOrder(buyOrder);
        alice.speak(market.createLevel1View());

        BuyOrder buyOrder1 = new LimitBuyOrder(george, lemons, 100, 50l);
        george.supplyOrder(buyOrder1);
        george.speak(market.createLevel1View());

        SellOrder sellOrder = new MarketSellOrder(bruce, lemons, 150, market.createLevel1View());
        bruce.supplyOrder(sellOrder);
        bruce.speak(market.createLevel1View());

        market.doClearing();

        assertEquals("", bruce.getCash(), (Long) 62500l );
        assertEquals("", bruce.getInventory(lemons),(Integer) 4850);

        assertEquals("", alice.getInventory(lemons), (Integer) 100);
    }


    @Test
    public void testTwoMarketOrders() {

        DefaultStockExchange market =
                new DefaultStockExchange(world,	new SerialTickerTapeObserver(),	marketFactory);

        MarketBuyOrder buyOrder = new MarketBuyOrder(alice, lemons, 100, market.createLevel1View());
        alice.supplyOrder(buyOrder);
        alice.speak(market.createLevel1View());

        market.doClearing();

        assertEquals("", alice.getCash(),(Long) 50000l);

        SellOrder sellOrder = new MarketSellOrder(bruce, lemons, 150, market.createLevel1View());
        bruce.supplyOrder(sellOrder);
        bruce.speak(market.createLevel1View());

        market.doClearing();

        assertEquals("", alice.getCash(),(Long) 50000l); //trade occurs at no price!
        assertEquals("", alice.getInventory(lemons), (Integer) 100);

    }


    @Test
    public void testMarketOrderAfterSomeTrades() {
        DefaultStockExchange market =
                new DefaultStockExchange(world,	new SerialTickerTapeObserver(),	marketFactory);

        SellOrder sellOrder = new MarketSellOrder(bruce, lemons, 150, market.createLevel1View());
        //SellOrder sellOrder = new LimitSellOrder(bruce, lemons, 150,120l);
        bruce.supplyOrder(sellOrder);
        bruce.speak(market.createLevel1View());

        market.doClearing();

        SellOrder sellOrder1 = new LimitSellOrder(george, lemons, 50, 150l);
        george.supplyOrder(sellOrder1);
        george.speak(market.createLevel1View());

        BuyOrder buyOrder1 = new LimitBuyOrder(alice, lemons, 100, 100l);
        alice.supplyOrder(buyOrder1);
        alice.speak(market.createLevel1View());


        market.doClearing();
        //Test to see if marketSellOrder price is updated and the order is able to be executed
        assertEquals("", bruce.getInventory(lemons), (Integer) 4900);
        assertEquals("", alice.getCash(), (Long) 40000l);
    }
}
