package uk.ac.glasgow.jagora.trader.test;


import org.junit.Before;
import org.junit.Test;
import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.pricer.impl.OldestOrderPricer;
import uk.ac.glasgow.jagora.pricer.impl.SellOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.ticker.impl.StdOutTradeListener;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasic;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasicBuilder;
import uk.ac.glasgow.jagora.trader.impl.RandomTrader;
import uk.ac.glasgow.jagora.trader.impl.RandomTraderBuilder;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MarketMakerBasicTest {

    private MarketMakerBasic marketMaker;

    private Float marketShare = 0.05f;
    private Long spread = 1l;


    private World world;
    private Stock lemons;
    private StockWarehouse lemonsWarehouse;
    private StockExchange stockExchange;

    private SerialTickerTapeObserver tickerTapeObserver;

    private Long numberOfTraderActions = 5000l;
    private Integer seed = 1;
    private Integer numberOfTraders = 10;
    private Long initialTraderCash = 10000000l;
    private TradingEngine engine;
    private Integer lemonsQuantity = 100000;




    @Before
    public void setUp () throws  Exception{

        world = new SimpleSerialWorld(numberOfTraderActions);
        lemons = new Stock("lemons");
        lemonsWarehouse = new StockWarehouse(lemons, lemonsQuantity);

        MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(new OldestOrderPricer());

        tickerTapeObserver = new SerialTickerTapeObserver();

        stockExchange = new DefaultStockExchange(world, tickerTapeObserver, marketFactory);
        stockExchange.createMarket(lemonsWarehouse);

        Random r = new Random(seed);

        Trader dan = new StubTraderBuilder("stub", initialTraderCash)
                .addStock(lemons, 10).build();

        StockExchangeLevel1View danView = stockExchange.createLevel1View();
        danView.placeBuyOrder(new LimitBuyOrder(dan, lemons, 5, 1001l));
        danView.placeSellOrder(new LimitSellOrder(dan, lemons, 5, 999l));

        Set<Level1Trader> traders = new HashSet<Level1Trader>();

        Integer lemonsToGet = Math.round(lemonsQuantity.floatValue() / (numberOfTraders.floatValue() + 1.0f));

        for (int i = 0 ; i < numberOfTraders ; i++){
            RandomTrader randomTrader =
                    new RandomTraderBuilder()
                            .setName("trader["+i+"]")
                            .setCash(initialTraderCash)
                            .setSeed(r.nextInt())
                            .setTradeRange(lemons, 1, 100, -5l, +5l, -5l, +5l)
                            .addStock(lemons, lemonsWarehouse.getStock(lemonsToGet))
                            .build();

            //stockExchange.createLevel1View().registerTradeListener(historicTrader);
            traders.add(randomTrader);
        }

        marketMaker = new MarketMakerBasicBuilder("Goldman")
                    .addStock(lemons,lemonsWarehouse.getRemainingStock())
                    .addStockWarehouse(lemonsWarehouse)
                    .setCash(initialTraderCash)
                    .setSeed(r.nextInt())
                    .setMarketShare(marketShare)
                    .setSpread(spread)
                    .build();

        stockExchange.createLevel1View().registerTradeListener(new StdOutTradeListener());

        engine = new SerialRandomEngineBuilder(world, seed)
                .addStockExchange(stockExchange)
                .addTraders(traders)
                .addPrivilegedTrader(marketMaker)
                .setStandartDelay(6l)
                .buildNew();
    }

    @Test
    public void testInBigEnvironment() {

        engine.run();
        System.out.println(marketMaker.getCash() );
        System.out.println(marketMaker.getInventory(lemons));
       // System.out.println(tickerTapeObserver.getSellOrderHistory(lemons));
    }
}
