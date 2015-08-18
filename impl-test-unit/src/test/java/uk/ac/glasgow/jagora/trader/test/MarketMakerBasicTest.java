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
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.ticker.impl.StdOutTradeListener;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketDatum;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasic;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasicBuilder;
import uk.ac.glasgow.jagora.trader.impl.RandomTraders.RandomTraderPercentage;
import uk.ac.glasgow.jagora.trader.impl.RandomTraders.RandomTraderPercentageBuilder;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class MarketMakerBasicTest {

    private MarketMakerBasic marketMaker;

    private Float marketShare = 0.1f;
    private Double spread = 0.003;


    private World world;
    private Stock lemons;
    private StockWarehouse lemonsWarehouse;
    private StockExchange stockExchange;

    private SerialTickerTapeObserver tickerTapeObserver;

    private Long numberOfTraderActions = 10000l;
    private Integer seed = 1;
    private Integer numberOfTraders = 10;
    private Long initialTraderCash = 100000000l;
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
        stockExchange.createMarket(lemonsWarehouse); //showing proper usage for stockwarehouse

        Random r = new Random(seed);

        Trader dan = new StubTraderBuilder("stub", initialTraderCash)
                .addStock(lemons, 10).build();

        StockExchangeLevel1View danView = stockExchange.createLevel1View();
        danView.placeBuyOrder(new LimitBuyOrder(dan, lemons, 5, 1001l));
        danView.placeSellOrder(new LimitSellOrder(dan, lemons, 5, 999l));

        Set<Level1Trader> traders = new HashSet<Level1Trader>();

        Integer lemonsToGet = Math.round(lemonsQuantity.floatValue() / (numberOfTraders.floatValue() + 1.0f));

        for (int i = 0 ; i < numberOfTraders ; i++){
            RandomTraderPercentage randomTrader =
                    new RandomTraderPercentageBuilder()
                            .setName("trader["+i+"]")
                            .setCash(initialTraderCash)
                            .setSeed(r.nextInt())
                            .setTradeRange(lemons, 1, 100, -0.005,+0.005,-0.005,0.005)
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
                .build();
    }

    @Test
    public void testLiquidityCalculation() throws Exception{

        engine.run();


        System.out.println("cash is " +marketMaker.getCash() );
        System.out.println("inventory is " +marketMaker.getInventory(lemons));

        Field marketDatum = marketMaker.getClass().getDeclaredField("marketDatum");
        marketDatum.setAccessible(true);
        MarketDatum marketData = (MarketDatum) marketDatum.get(marketMaker);

        Field buyField = marketData.getClass().getDeclaredField("buySideLiquidity");
        buyField.setAccessible(true);
        Integer buySideLiquidity = (Integer) buyField.get(marketData);

        Field sellField = marketData.getClass().getDeclaredField("sellSideLiquidity");
        sellField.setAccessible(true);
        Integer sellSideLiquidity = (Integer) sellField.get(marketData);


        System.out.println("Buy liquidity calculated by marketMaker is " + buySideLiquidity);
        System.out.println("Sell liquidity calculated by marketMaker is " + sellSideLiquidity);


        //Need to execute the following block to have the observer view of liquidity
        Integer realBuySideLiquidity = 0;
        Integer realSellSideLiquidity = 0;
        for (OrderEntryEvent event :tickerTapeObserver.getBuyOrderHistory(lemons))
            realBuySideLiquidity += event.quantity;

        for (OrderEntryEvent event: tickerTapeObserver.getSellOrderHistory(lemons))
            realSellSideLiquidity += event.quantity;


        for (OrderEntryEvent event : tickerTapeObserver.getCancelledBuyOrderHistory(lemons))
            realBuySideLiquidity -= event.quantity;

        for (OrderEntryEvent event: tickerTapeObserver.getCancelledSellOrderHistory(lemons))
            realSellSideLiquidity -= event.quantity;

        for (TickEvent<Trade> event :tickerTapeObserver.getTradeHistory(lemons)){
            realBuySideLiquidity -= event.event.getQuantity();
            realSellSideLiquidity -= event.event.getQuantity();
        }

        //this has to be adjusted around the point at which the market maker is let on the market
        Double permittedError = 400.0;

        assertThat (realBuySideLiquidity.doubleValue(),
                closeTo(buySideLiquidity.doubleValue(),permittedError));
        assertThat(realSellSideLiquidity.doubleValue(),
                closeTo(sellSideLiquidity.doubleValue(),permittedError));



        System.out.println("Observer calculated buy liquidity " + realBuySideLiquidity);
        System.out.println("Observer calculated sell liquidity " +realSellSideLiquidity);


    }
}
