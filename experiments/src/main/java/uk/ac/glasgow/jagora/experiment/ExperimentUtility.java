package uk.ac.glasgow.jagora.experiment;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.DelayableSerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.pricer.impl.OldestLimitOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.impl.OutputStreamTradeListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.InstitutionalInvestorTrader;
import uk.ac.glasgow.jagora.trader.impl.InstitutionalInvestorTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMaker;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMakerBuilder;
import uk.ac.glasgow.jagora.trader.impl.random.*;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTrader;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTraderBuilder;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;


/**
 *  Utility class to be used for setting up experiments
 *  Experiments should be child classes.
 *  Methods can be overridden.
 *  Only works for a single stock
 *  
 *  @author Ivelin
 */
public class ExperimentUtility {

	// experimental parameters

	protected Long numberOfTraderActions = 10000l;
	protected Integer seed = 1;
	protected Long standardDelay = 10l;

	protected Long initialTraderCash = 100000l;
	protected Long initialLevel2TraderCash = 100000000l;
	protected Integer lemonsQuantity = 1000000;

	protected Integer numberOfRandomTraders = 0;
	protected Integer numberOfMarketMakers = 0;
	protected Integer numberOfHighFrequencyTraders = 0;
	protected Integer numberOfRandomSpreadCrossingTraders = 0;
	protected Integer numberOfSimpleHistoricTraders = 0;

	protected Double institutionalInvestorStockPercentage = 0.0;
	protected Float   marketMakerShare = 0.05f;
	protected Double marketMakerInventoryAdjustmentInfluence = 0.0;
	protected Double marketMakerLiquidityAdjustmentInfluence = 0.0;
	protected Double marketMakerSpread = 0.003;
	protected Double hFTSpread = 0.001;
	protected Double randomTradersSpread = 0.001;
	protected Double randomSpreadCrossingTraderSpread = 0.001;
	protected Integer quantityTradeRangeLow = 1;
	protected Integer quantityTradeRangeHigh = 300;

	protected Long firstTradePrice = 1000l;

	protected final String pricesDatFilePath = "reports/jagora/default/prices.dat";

	// experimental fixture

	protected World world;
	protected Stock lemons;

	protected StockExchange stockExchange;
	protected TradingEngine engine;

	protected SerialTickerTapeObserver tickerTapeObserver;

	protected Random random;

	protected Set<Level1Trader> level1Traders;
	protected Set<Level2Trader> level2Traders;

	protected Map<Long,Integer> delayedBuyOrders = new HashMap<>();
	protected Map<Long,Integer> delayedSellOrders = new HashMap<>();

	protected Integer stockQuantity = 0;
	protected Integer lemonsLiquidity = 0;


	public void createExperiment() throws Exception{
		random = new Random(seed);

		lemons = new Stock("lemons");

		createStockExchange();

		calculateNumberOfShares();

		level1Traders = new HashSet<>();
		level2Traders = new HashSet<>();

		addRandomTraders(level1Traders);
		addRandomSpreadCrossingTraders(level1Traders);
		addSimpleHistoricTraders(level1Traders);
		addInstitutionalInvestorTrader(level1Traders);

		addHighFrequencyTraders (level2Traders);
		addMarketMakers (level2Traders);


		engine = new DelayableSerialRandomEngineBuilder()
			.setWorld(world)
			.setSeed(seed)
			.addStockExchange(stockExchange)
			.setStandardDelay(standardDelay)
			.addTraders(level1Traders)
			.addPrivilegedTraders(level2Traders)
			.build();

		configureFirstTrade();
	}


	protected void calculateNumberOfShares() {
		Double quantityForLevel1Traders = this.lemonsQuantity *
				(1.0 - numberOfMarketMakers*marketMakerShare - institutionalInvestorStockPercentage);

		Double quantityForLevel1Trader =
				quantityForLevel1Traders/
						(numberOfRandomTraders + numberOfSimpleHistoricTraders
								+ numberOfRandomSpreadCrossingTraders);
		stockQuantity = quantityForLevel1Trader.intValue();

	}

	protected void addMarketMakers(Set<Level2Trader> level2Traders) {
		Integer marketMakerQuantity = Math.round(this.lemonsQuantity * marketMakerShare);

		for (Integer i : range(0,numberOfMarketMakers).toArray()) {
			String name = createTraderName(MarketMaker.class,i);

			Level2Trader trader =
					new MarketMakerBuilder()
							.setName(name)
							.setCash(initialLevel2TraderCash)
							.addMarketPositionSpecification(lemons, lemonsQuantity, lemonsLiquidity )
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
							.setBuyRangeDatum(lemons, quantityTradeRangeLow, quantityTradeRangeHigh, -hFTSpread, hFTSpread)
							.setSellRangeDatum(lemons, quantityTradeRangeLow, quantityTradeRangeHigh, -hFTSpread, hFTSpread)							
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
							.addStock(lemons, stockQuantity)
							.monitorStockExchange(stockExchange)
							.build();
			level1Traders.add(trader);
		}
	}


	protected void addRandomTraders(Set<Level1Trader> level1Traders) throws Exception{
		for (Integer i : range(0, numberOfRandomTraders).toArray()){

			String name = createTraderName(RandomTrader.class, i);

			RandomTraderPercentage trader =
				new RandomTraderPercentageBuilder()
					.setName(name)
					.setCash(initialTraderCash)
					.setSeed(random.nextInt())
					.addStock(lemons,stockQuantity)
					.setBuyOrderRange(lemons, quantityTradeRangeLow, quantityTradeRangeHigh, -randomTradersSpread,randomTradersSpread)
					.setSellOrderRange(lemons, quantityTradeRangeLow, quantityTradeRangeHigh, -randomTradersSpread,randomTradersSpread)							
					.build();

			level1Traders.add(trader);
		}
	}


	protected void addRandomSpreadCrossingTraders(Set<Level1Trader> level1Traders) {
		for (Integer i : range(0, numberOfRandomSpreadCrossingTraders).toArray()) {

			String name = createTraderName(RandomSpreadCrossingTrader.class,i);

			RandomSpreadCrossingTraderPct trader =
						new RandomSpreadCrossingTraderPctBuilder()
							.setName(name)
							.addStock(lemons, stockQuantity)
							.setSeed(seed)
							.setCash(initialTraderCash)
							.addTradeRangePct(lemons,quantityTradeRangeLow,
									quantityTradeRangeHigh,randomSpreadCrossingTraderSpread)
							.build();

			level1Traders.add(trader);
		}
	}

	protected void addInstitutionalInvestorTrader (Set<Level1Trader> level1Traders) throws Exception{

			if (delayedBuyOrders.isEmpty() && delayedSellOrders.isEmpty())
				return;

			String name = createTraderName(InstitutionalInvestorTrader.class, 1);
			Integer quantity = Math.round(lemonsQuantity* institutionalInvestorStockPercentage.floatValue());

			InstitutionalInvestorTraderBuilder traderBuilder =
					new InstitutionalInvestorTraderBuilder()
							.setName(name)
							.setCash(initialLevel2TraderCash)
							.addStock(lemons, quantity);

			for (Long delay : delayedBuyOrders.keySet())
				traderBuilder.addScheduledLimitBuyOrder(
						delay, world,lemons, delayedBuyOrders.get(delay), 100l );


		level1Traders.add(traderBuilder.build());

	}

	protected String createTraderName(Class<? extends Trader> clazz, Integer i) {
		String traderTypeName = clazz.getSimpleName();
		String nameFormat = "%s[%d]";
		return format(nameFormat, traderTypeName, i);
	}

	protected void createStockExchange() throws FileNotFoundException {
		world = new SimpleSerialWorld(numberOfTraderActions);

		configureTickerTapeObserver();

		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(new OldestLimitOrderPricer());

		stockExchange = new DefaultStockExchange(world,tickerTapeObserver,marketFactory);


	}

	protected void configureFirstTrade () {
		Trader dan = new StubTraderBuilder()
			.setName("stub")
			.setCash(initialTraderCash)
			.addStock(lemons, 10)
			.build();

		StockExchangeLevel1View danView = stockExchange.createLevel1View();
		danView.placeLimitBuyOrder(new DefaultLimitBuyOrder(dan, lemons, 5, firstTradePrice + 1));
		danView.placeLimitSellOrder(new DefaultLimitSellOrder(dan, lemons, 7, firstTradePrice));

	}


	protected void configureTickerTapeObserver() throws FileNotFoundException {

		tickerTapeObserver = new SerialTickerTapeObserver();

		//registerFilteredStdOutOrderListener(OrderDirection.BUY);
		//registerFilteredStdOutOrderListener(OrderDirection.SELL);

		tickerTapeObserver.registerTradeListener(new OutputStreamTradeListener(System.out));

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

}
