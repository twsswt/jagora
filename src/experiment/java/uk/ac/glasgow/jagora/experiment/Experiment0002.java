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
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.Trader;
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
	private Long durationInMilliSeconds = 60000l;
	private Integer seed = 1;

	private Integer numberOfRandomTraders = 50;
	private Integer numberOfSpreadCrossingRandomTraders = 50;
	private Integer numberOfSimpleHistoricTraders = 5;
	private Integer numberOfInstitutionalInvestorTraders = 1;

	private Double initialTraderCash = 1000.0;
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
		
		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory();
		
		tickerTapeObserver = new SerialTickerTapeObserver();
		
		stockExchange = new DefaultStockExchange(world, tickerTapeObserver, marketFactory);

		Set<Trader> traders = new HashSet<Trader>();
		
		Trader dan = new StubTraderBuilder("stub", initialTraderCash)
			.addStock(lemons, initialTraderStock).build();
		
		StockExchangeTraderView dansView = stockExchange.createTraderStockExchangeView();
		dansView.placeBuyOrder(new LimitBuyOrder(dan, lemons, 5, 9.75));
		dansView.placeSellOrder(new LimitSellOrder(dan, lemons, 5, 10.25));
		
		
		for (Integer i : range(0, numberOfRandomTraders).toArray()){
			
			String name = createTraderName(RandomTrader.class, i);

			RandomTrader trader = 
				new RandomTraderBuilder(name ,initialTraderCash, r.nextInt())
				.addStock(lemons, initialTraderStock)
				.setTradeRange(lemons, 1, 3, -0.01, 0.04, -0.04, 0.01)
				.build();
			
			traders.add(trader);
		}
		
		for (Integer i : range(0, numberOfSpreadCrossingRandomTraders).toArray()){
			
			String name =
				createTraderName(RandomSpreadCrossingTrader.class, i);
			
			RandomSpreadCrossingTrader trader = 
				new RandomSpreadCrossingTraderBuilder(name,initialTraderCash, r.nextInt())
				.addStock(lemons, initialTraderStock)
				.addTradeRange(lemons, 1, 3, 0.01)
				.build();
			
			traders.add(trader);
		}
		
		for (Integer i : range(0, numberOfSimpleHistoricTraders).toArray()){
			
			String name = createTraderName(SimpleHistoricTrader.class, i);
			
			Trader trader = 
				new SimpleHistoricTraderBuilder(name,initialTraderCash, seed)
				.addStock(lemons, initialTraderStock)
				.monitorStockExchange(stockExchange)
				.build();
			traders.add(trader);
		}
			
		for (Integer i : range(0, numberOfInstitutionalInvestorTraders).toArray()){
			
			String name = createTraderName(InstitutionalInvestorTrader.class, i);
			
			Trader trader = 
				new InstitutionalInvestorTraderBuilder(name,200000.01, seed)
				.addStock(lemons, 1100)
				.addScheduledLimitBuyOrder(5000l, world, lemons, 4000)
				.build();
			traders.add(trader);
		}


		PrintStream printStream = new PrintStream(new FileOutputStream("prices.txt"));
		
		stockExchange.addTickerTapeListener(
			new PriceTimeLoggerTickerTapeListener(printStream));
		
		stockExchange.addTickerTapeListener(
			new TimeListenerTickerTapeListener( durationInMilliSeconds));
		
		//stockExchange.addTickerTapeListener(
		//	new StdOutTickerTapeListener());
				
		engine = new SerialRandomEngineBuilder(world, seed)
			.addStockExchange(stockExchange)
			.addTraders(traders)
			.build();
	}

	private String createTraderName(Class<? extends Trader> clazz, Integer i) {
		String traderTypeName = clazz.getSimpleName();
		String nameFormat = "%s[%d]";
		return format(nameFormat, traderTypeName, i);
	}
	
	@Test
	public void test() {
		engine.run();
	}

}
