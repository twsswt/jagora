package uk.ac.glasgow.jagora.engine.test;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.DelayableSerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.pricer.impl.SellOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.impl.OutputStreamTradeListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.random.RandomTrader;
import uk.ac.glasgow.jagora.trader.impl.random.RandomTraderBuilder;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DelayWorldTest {
	private World world;
	private Stock lemons;
	private StockExchange stockExchange;

	private SerialTickerTapeObserver tickerTapeObserver;

	private Long numberOfTraderActions = 50l;
	private Integer seed = 1;
	private int numberOfTraders = 10;
	private Long initialTraderCash = 10000000l;
	private TradingEngine engine;

	@Before
	public void setUp() throws Exception {
		world = new SimpleSerialWorld(numberOfTraderActions);
		lemons = new Stock("lemons");

		MarketFactory marketFactory = new ContinuousOrderDrivenMarketFactory(new SellOrderPricer());

		tickerTapeObserver = new SerialTickerTapeObserver();

		stockExchange = new DefaultStockExchange(world, tickerTapeObserver, marketFactory);

		Random r = new Random(seed);

		Trader dan = new StubTraderBuilder()
			.setName("stub")
			.setCash(initialTraderCash)
			.addStock(lemons, 10).build();

		StockExchangeLevel1View danView = stockExchange.createLevel1View();
		danView.placeBuyOrder(new LimitBuyOrder(dan, lemons, 5, 1001l));
		danView.placeSellOrder(new LimitSellOrder(dan, lemons, 5, 999l));

		Set<Level1Trader> traders = new HashSet<Level1Trader>();

		for (int i = 0 ; i < numberOfTraders ; i++){
			RandomTrader randomTrader =
					new RandomTraderBuilder()
						.setName("trader["+i+"]")
						.setCash(initialTraderCash)
						.setSeed(r.nextInt())
						.setBuyOrderRange(lemons, 1, 100, -5l, +5l)
						.setSellOrderRange(lemons, 1, 100, -5l, +5l)
						.addStock(lemons, 1000)
						.build();

			traders.add(randomTrader);
		}

		stockExchange.createLevel1View().registerTradeListener(new OutputStreamTradeListener(System.out));

		engine = new DelayableSerialRandomEngineBuilder()
				.setWorld(world)
				.setSeed(seed)
				.addStockExchange(stockExchange)
				.addTraders(traders)
				.setStandardDelay(5l)
				.build();
	}

	@Test
	public void testEngineRunningCorrectly(){
		engine.run();
		//shows a bug in the system if debugging internals of the priority queue of executors,
		//but it is actually just the priority queue implementation - no actual problem will ever occur
	}
}
