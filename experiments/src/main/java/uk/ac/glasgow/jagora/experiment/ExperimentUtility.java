package uk.ac.glasgow.jagora.experiment;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.pricer.impl.OldestOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.ticker.impl.StdOutTradeListener;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasic;
import uk.ac.glasgow.jagora.trader.impl.MarketMakerBasic.MarketMakerBasicBuilder;
import uk.ac.glasgow.jagora.trader.impl.RandomTraders.HighFrequencyRandomTrader;
import uk.ac.glasgow.jagora.trader.impl.RandomTraders.HighFrequencyRandomTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.RandomTraders.RandomTrader;
import uk.ac.glasgow.jagora.trader.impl.RandomTraders.RandomTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTrader;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTraderBuilder;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;



public class ExperimentUtility {

    // experimental parameters

    protected Long numberOfTraderActions = 10000l;
    protected Integer seed = 1;
    protected Long standartDelay = 7l;

    protected Long initialTraderCash = 100000000l;
    protected Long initialLevel2TraderCash = 1000000l;
    protected Integer lemonsQuantity = 1000000;

    protected Integer numberOfRandomTraders = 100;
    protected Integer numberOfMarketMakers = 10;
    protected Integer numberOfHighFrequencyTraders = 10;
    protected Integer numberofRandomSpreadCrossingTraders = 100;
    protected Integer numberOfSimpleHistoricTraders = 100;

    protected Float   marketMakerShare = 0.05f;
    protected Double marketMakerInventoryAdjustmentInfluence = 1.0;
    protected Double marketMakerLiquidityAdjustmentInfluence = 1.0;
    protected Double marketMakerSpread = 0.003;
    protected Double hFTSpread = 0.001;
    protected Double randomTradersSpread = 0.001;
    protected Double randomSpreadCrossingTraderSpread = 0.001;
    protected Integer quantityTradeRangeLow = 1;
    protected Integer quantityTradeRangeHigh = 300;

    protected final String pricesDatFilePath = "reports/jagora/default/prices.dat";


    // experimental fixture

    protected World world;
    protected Stock lemons;
    protected StockWarehouse lemonsWarehouse;
    protected StockExchange stockExchange;
    protected TradingEngine engine;

    protected SerialTickerTapeObserver tickerTapeObserver;

    protected Random random;

    protected Set<Level1Trader> level1Traders;
    protected Set<Level2Trader> level2Traders;

    protected Integer stockQuantity = 0;


    public void createExperiment() throws Exception{
        random = new Random(seed);

        lemons = new Stock("lemons");
        lemonsWarehouse = new StockWarehouse(lemons,lemonsQuantity);

        createStockExchange();

        calculateNumberOfShares();

        level1Traders = new HashSet<>();
        level2Traders = new HashSet<>();

        addRandomTraders(level1Traders);
        addSimpleHistoricTraders(level1Traders);

        addHighFrequencyTraders (level2Traders);
        addMarketMakers (level2Traders);


        engine = new SerialRandomEngineBuilder(world,seed)
                .addStockExchange(stockExchange)
                .setStandartDelay(standartDelay)
                .addTraders(level1Traders)
                .addPrivilegedTraders(level2Traders)
                .build();

        configureFirstTrade();
    }

    protected void calculateNumberOfShares() {
        Double quantityForLevel1Traders = lemonsWarehouse.getInitialQuantity()*
                (1.0 - numberOfMarketMakers*marketMakerShare);

        Double quantityForLevel1Trader =
                quantityForLevel1Traders/
                        (numberOfRandomTraders + numberOfSimpleHistoricTraders
                                +numberofRandomSpreadCrossingTraders);
        stockQuantity = quantityForLevel1Trader.intValue();

    }

    protected void addMarketMakers(Set<Level2Trader> level2Traders) {
        Integer marketMakerQuantity = Math.round(lemonsWarehouse.getInitialQuantity()*marketMakerShare);

        for (Integer i : range(0,numberOfMarketMakers).toArray()) {
            String name = createTraderName(MarketMakerBasic.class,i);

            Level2Trader trader =
                    new MarketMakerBasicBuilder(name)
                            .setCash(initialLevel2TraderCash)
                            .setSeed(seed)
                            .setInventoryAdjustmentInfluence(marketMakerInventoryAdjustmentInfluence)
                            .setLiquidityAdjustmentInfluence(marketMakerLiquidityAdjustmentInfluence)
                            .setMarketShare(marketMakerShare)
                            .setSpread(marketMakerSpread)
                            .addStockWarehouse(lemonsWarehouse)
                            .addStock(lemons, marketMakerQuantity)
                            .build();

            level2Traders.add(trader);

        }
    }

    protected void addHighFrequencyTraders(Set<Level2Trader> level2Traders) {
        for (Integer i: range(0,numberOfHighFrequencyTraders).toArray()){
            String name = createTraderName(HighFrequencyRandomTrader.class,i);

            Level2Trader trader =
                    new HighFrequencyRandomTraderBuilder()
                            .addStock(lemons,0)
                            .setName(name)
                            .setCash(initialLevel2TraderCash)
                            .setSeed(seed)
                            .setTradeRange(lemons, quantityTradeRangeLow, quantityTradeRangeHigh, -hFTSpread, hFTSpread, -hFTSpread,hFTSpread)
                            .build();

            level2Traders.add(trader);
        }
    }


    protected void addSimpleHistoricTraders(Set<Level1Trader> level1Traders) throws  Exception{
        for (Integer i : range(0, numberOfSimpleHistoricTraders).toArray()){

            String name = createTraderName(SimpleHistoricTrader.class, i);

            Level1Trader trader =
                    new SimpleHistoricTraderBuilder()
                            .setName(name)
                            .setCash(initialTraderCash)
                            .setSeed(seed)
                            .addStock(lemons, lemonsWarehouse.getStock(stockQuantity))
                            .monitorStockExchange(stockExchange)
                            .build();
            level1Traders.add(trader);
        }
    }


    protected void addRandomTraders(Set<Level1Trader> level1Traders) throws Exception{
        for (Integer i : range(0, numberOfRandomTraders).toArray()){

            String name = createTraderName(RandomTrader.class, i);

            RandomTrader trader =
                    new RandomTraderBuilder()
                            .setName(name)
                            .setCash(initialTraderCash)
                            .setSeed(random.nextInt())
                            .addStock(lemons, lemonsWarehouse.getStock(stockQuantity))
                            .setTradeRange(lemons, quantityTradeRangeLow, quantityTradeRangeHigh, -1l, 4l, -4l, 1l)
                            .build();

            level1Traders.add(trader);
        }
    }


    protected String createTraderName(Class<? extends Trader> clazz, Integer i) {
        String traderTypeName = clazz.getSimpleName();
        String nameFormat = "%s[%d]";
        return format(nameFormat, traderTypeName, i);
    }

    protected void createStockExchange() throws FileNotFoundException {
        world = new SimpleSerialWorld(numberOfTraderActions);

        configureTickerTapeObserver();

        MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(new OldestOrderPricer());

        stockExchange = new DefaultStockExchange(world,tickerTapeObserver,marketFactory);
        stockExchange.createMarket(lemonsWarehouse);


        stockExchange.createLevel1View().registerTradeListener(new StdOutTradeListener());



    }

    protected void configureFirstTrade () {
        Trader dan = new StubTraderBuilder("stub", initialTraderCash)
                .addStock(lemons, 10).build();

        StockExchangeLevel1View danView = stockExchange.createLevel1View();
        danView.placeBuyOrder(new LimitBuyOrder(dan, lemons, 5, 1001l));
        danView.placeSellOrder(new LimitSellOrder(dan, lemons, 7, 999l));

    }


    protected void configureTickerTapeObserver() throws FileNotFoundException {

        tickerTapeObserver = new SerialTickerTapeObserver();

        //registerFilteredStdOutOrderListener(OrderDirection.BUY);
        //registerFilteredStdOutOrderListener(OrderDirection.SELL);

        tickerTapeObserver.registerTradeListener(new StdOutTradeListener());

        PrintStream pricesDatFileStream = createPrintStreamToFile(pricesDatFilePath);

        GnuPlotPriceDATLogger priceTimeLogger =
                new GnuPlotPriceDATLogger(pricesDatFileStream);
        tickerTapeObserver.registerTradeListener(priceTimeLogger);
        tickerTapeObserver.registerOrderListener(priceTimeLogger);

    }

    protected PrintStream createPrintStreamToFile(String filePath) throws FileNotFoundException {
        File pricesDatFile = new File(filePath);
        pricesDatFile.getParentFile().mkdirs();
        PrintStream printStream = new PrintStream(new FileOutputStream(pricesDatFile));
        return printStream;
    }


//
//    @Test
//    public void test() {
//        engine.run();
//    }


}
