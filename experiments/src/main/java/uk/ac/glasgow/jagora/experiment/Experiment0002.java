package uk.ac.glasgow.jagora.experiment;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.pricer.impl.SellOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.impl.FilterOnDirectionOrderListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.ticker.impl.StdOutOrderListener;
import uk.ac.glasgow.jagora.ticker.impl.StdOutTradeListener;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.InstitutionalInvestorTrader;
import uk.ac.glasgow.jagora.trader.impl.InstitutionalInvestorTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.RandomSpreadCrossingTrader;
import uk.ac.glasgow.jagora.trader.impl.RandomSpreadCrossingTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.RandomTrader;
import uk.ac.glasgow.jagora.trader.impl.RandomTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTrader;
import uk.ac.glasgow.jagora.trader.impl.SimpleHistoricTraderBuilder;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.TimedWorld;

public class Experiment0002 {

	//Parameters.
	private Long durationInMilliSeconds = 120000l;
	private Integer seed = 1;

	private Integer numberOfRandomTraders = 50;
	private Integer numberOfSpreadCrossingRandomTraders = 50;
	private Integer numberOfSimpleHistoricTraders = 5;
	private Integer numberOfInstitutionalInvestorTraders = 1;

	private Long initialTraderCash = 100000l;
	private Integer initialTraderStock = 100;

	
	private World world;
	private Stock lemons;
	private StockExchange stockExchange;
	
	private SerialTickerTapeObserver tickerTapeObserver;
	
	private TradingEngine engine;
	
	@Before
	public void setUp() throws Exception {
		
		Date startTime = new Date();
		lemons = new Stock("lemons");
		Random r = new Random(seed);
		
		world = new TimedWorld(startTime, durationInMilliSeconds);
		//world = new SimpleSerialWorld(durationInMilliSeconds);
		
		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(new SellOrderPricer());
		
		tickerTapeObserver = new SerialTickerTapeObserver();
		
		stockExchange = new DefaultStockExchange(world, tickerTapeObserver, marketFactory);
		
		//registerFilteredStdOutOrderListener(false);
		//registerFilteredStdOutOrderListener(true);


		Set<Level1Trader> traders = new HashSet<Level1Trader>();
		
		Level1Trader dan = new StubTraderBuilder("stub", initialTraderCash)
			.addStock(lemons, initialTraderStock).build();
		
		StockExchangeLevel1View dansView = stockExchange.createLevel1View();
		dansView.placeBuyOrder(new LimitBuyOrder(dan, lemons, 5, 975l));
		dansView.placeSellOrder(new LimitSellOrder(dan, lemons, 5, 1025l));
		
		
		for (Integer i : range(0, numberOfRandomTraders).toArray()){
			
			String name = createTraderName(RandomTrader.class, i);

			RandomTrader trader = 
				new RandomTraderBuilder()
				.setName(name)
				.setCash(initialTraderCash)
				.setSeed(r.nextInt())
				.addStock(lemons, initialTraderStock)
				.setTradeRange(lemons, 1, 300, -1l, 4l, -4l, 1l)
				.build();
			
			traders.add(trader);
		}
		
		for (Integer i : range(0, numberOfSpreadCrossingRandomTraders).toArray()){
			
			String name =
				createTraderName(RandomSpreadCrossingTrader.class, i);
			
			RandomSpreadCrossingTrader trader = 
				new RandomSpreadCrossingTraderBuilder()
				.setName(name)
				.setCash(initialTraderCash)
				.setSeed(r.nextInt())
				.addStock(lemons, initialTraderStock)
				.addTradeRange(lemons, 1, 3, 1l)
				.build();
			
			traders.add(trader);
		}
		
		for (Integer i : range(0, numberOfSimpleHistoricTraders).toArray()){
			
			String name = createTraderName(SimpleHistoricTrader.class, i);
			
			Level1Trader trader = 
				new SimpleHistoricTraderBuilder(name,initialTraderCash, seed)
				.addStock(lemons, initialTraderStock)
				.monitorStockExchange(stockExchange)
				.build();
			traders.add(trader);
		}
			
		for (Integer i : range(0, numberOfInstitutionalInvestorTraders).toArray()){
			
			String name = createTraderName(InstitutionalInvestorTrader.class, i);
			
			Level1Trader trader = 
				new InstitutionalInvestorTraderBuilder()
				.setName(name)
				.setCash(20000001l)
				.addStock(lemons, 1100)
				.addScheduledLimitBuyOrder(5000l, world, lemons, 4000)
				.build();
			traders.add(trader);
		}


		PrintStream printStream = new PrintStream(new FileOutputStream("prices.txt"));
		
		tickerTapeObserver.registerTradeListener(
			new PriceTimeLoggerTickerTapeListener(printStream));
		
		tickerTapeObserver.registerTradeListener(
			new TimeListenerTickerTapeListener( durationInMilliSeconds));
		
		tickerTapeObserver.registerTradeListener(new StdOutTradeListener());
		
		//stockExchange.addTickerTapeListener(
		//	new StdOutTickerTapeListener());
				
		engine = new SerialRandomEngineBuilder(world, seed)
			.addStockExchange(stockExchange)
			.addTraders(traders)
			.build();
	}

	private void registerFilteredStdOutOrderListener(Boolean isOffer) {
		FilterOnDirectionOrderListener filteredOrderListener =
			new FilterOnDirectionOrderListener(new StdOutOrderListener(), isOffer);
		tickerTapeObserver.registerOrderListener(filteredOrderListener);
	}

	
	private String createTraderName(Class<? extends Level1Trader> clazz, Integer i) {
		String traderTypeName = clazz.getSimpleName();
		String nameFormat = "%s[%d]";
		return format(nameFormat, traderTypeName, i);
	}
	
	@Test
	public void test() {
		engine.run();
	}

}
