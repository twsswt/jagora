package uk.ac.glasgow.jagora.experiment;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.pricer.impl.OldestLimitOrderPricer;
import uk.ac.glasgow.jagora.ticker.impl.OutputStreamTradeListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.InstitutionalInvestorTrader;
import uk.ac.glasgow.jagora.trader.impl.InstitutionalInvestorTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTrader;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMaker;
import uk.ac.glasgow.jagora.trader.impl.marketmaker.MarketMakerBuilder;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTrader;
import uk.ac.glasgow.jagora.trader.impl.random.RandomTrader;
import uk.ac.glasgow.jagora.trader.ivo.impl.HighFrequencyRandomTrader;
import uk.ac.glasgow.jagora.trader.ivo.impl.HighFrequencyRandomTraderBuilder;
import uk.ac.glasgow.jagora.trader.ivo.impl.RandomSpreadCrossingTraderPct;
import uk.ac.glasgow.jagora.trader.ivo.impl.RandomSpreadCrossingTraderPctBuilder;
import uk.ac.glasgow.jagora.trader.ivo.impl.RandomTraderPercentage;
import uk.ac.glasgow.jagora.trader.ivo.impl.RandomTraderPercentageBuilder;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;


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

	protected Set<Trader> traders;
	protected Set<Trader> level2Traders;

	protected Map<Long,Integer> delayedBuyOrders = new HashMap<>();
	protected Map<Long,Integer> delayedSellOrders = new HashMap<>();

	protected Integer stockQuantity = 0;
	protected Integer lemonsLiquidity = 0;


	public void createExperiment() throws Exception{
		random = new Random(seed);

		lemons = new Stock("lemons");

		createStockExchange();

		calculateNumberOfShares();

		traders = new HashSet<Trader>();
		level2Traders = new HashSet<>();

		addRandomTraders(traders);
		addRandomSpreadCrossingTraders(traders);
		addSimpleHistoricTraders(traders);
		addInstitutionalInvestorTrader(traders);

		addHighFrequencyTraders (level2Traders);
		addMarketMakers (level2Traders);


		engine = new SerialRandomEngineBuilder()
			.setWorld(world)
			.addStockExchange(stockExchange)
			.addDelayedTradersView(traders, standardDelay, stockExchange)
			.addTradersStockExchangeView(level2Traders, stockExchange)
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

	protected void addMarketMakers(Set<Trader> traders) {
		Integer marketMakerQuantity = Math.round(this.lemonsQuantity * marketMakerShare);

		for (Integer i : range(0,numberOfMarketMakers).toArray()) {
			String name = createTraderName(MarketMaker.class,i);

			Trader trader =
				new MarketMakerBuilder()
					.setName(name)
					.setCash(initialLevel2TraderCash)
					.addMarketPositionSpecification(lemons, lemonsQuantity, lemonsLiquidity )
					.addStock(lemons, marketMakerQuantity)
					.build();

			traders.add(trader);

		}
	}

	protected void addHighFrequencyTraders(Set<Trader> traders) {
		for (Integer i: range(0,numberOfHighFrequencyTraders).toArray()){
			String name = createTraderName(HighFrequencyRandomTrader.class,i);

			Trader trader =
				new HighFrequencyRandomTraderBuilder()
					.addStock(lemons,0)
					.setName(name)
					.setCash(initialLevel2TraderCash)
					.setSeed(seed)
					.setBuyRangeDatum(lemons, quantityTradeRangeLow, quantityTradeRangeHigh, -hFTSpread, hFTSpread)
					.setSellRangeDatum(lemons, quantityTradeRangeLow, quantityTradeRangeHigh, -hFTSpread, hFTSpread)							
					.build();

			traders.add(trader);
		}
	}

	protected void addSimpleHistoricTraders(Set<Trader> traders) throws  Exception{
		for (Integer i : range(0, numberOfSimpleHistoricTraders).toArray()){

			String name = createTraderName(SimpleHistoricTrader.class, i);

			Trader trader =
				new SimpleHistoricTraderBuilder()
					.setName(name)
					.setCash(initialTraderCash)
					.setSeed(seed)
					.addStock(lemons, stockQuantity)
					.monitorStockExchange(stockExchange)
					.build();
			traders.add(trader);
		}
	}


	protected void addRandomTraders(Set<Trader> level1Traders) throws Exception{
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


	protected void addRandomSpreadCrossingTraders(Set<Trader> level1Traders) {
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

	protected void addInstitutionalInvestorTrader (Set<Trader> level1Traders) throws Exception{

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
		
		Trader dan = new AbstractTraderBuilder(){

			public Trader build() {
				setName("stub");
				setCash(200l);
				addStock(lemons, 1);
				return new SafeAbstractTrader(getName(), getCash(), getInventory()){};
			}
			
		}.build();

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
