package uk.ac.glasgow.jagora.engine.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.BuyOnlyTrader;
import uk.ac.glasgow.jagora.trader.impl.SellOnlyTrader;
import uk.ac.glasgow.jagora.world.World;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

public class SerialRandomEngineTest {
	
	private static final Integer seed = 1;
	private static final Long worldDuration = 30l;
	private static final Integer initialStock = 10;
	private static final Double initialCash = 10.0;
	private static final Double stockPrice = 1.0;
	private static final Integer orderQuantity = 1;

	private TradingEngine engine;
	
	private StockExchange stockExchange;
	private Stock lemons;
	private SerialTickerTapeObserver tickerTapeObserver;
	
	@Before
	public void setUp() throws Exception {
		
		lemons =
			new Stock("lemons");
		
		World world =
			new SimpleSerialWorld(worldDuration);
		
		MarketFactory marketFactory =
			new ContinuousOrderDrivenMarketFactory();
		
		tickerTapeObserver = new SerialTickerTapeObserver();
		
		stockExchange = 
			new DefaultStockExchange(world, tickerTapeObserver, marketFactory);
		
		Trader alice = 
			new BuyOnlyTrader(
				"alice", initialCash, lemons, stockPrice, orderQuantity);
		
		Trader bob =
			new SellOnlyTrader(
				"bob", initialStock, lemons, stockPrice, orderQuantity);
		
		engine = new SerialRandomEngineBuilder(world, seed)
			.addTrader(alice)
			.addTrader(bob)
			.addStockExchange(stockExchange)
			.build();
		
	}

	@Test
	public void testRun() throws InterruptedException {
		Thread t = new Thread(engine);
		t.start();
		t.join();
		
		assertEquals("", 10, tickerTapeObserver.getTradeHistory(lemons).size());
		
		
	}

}
