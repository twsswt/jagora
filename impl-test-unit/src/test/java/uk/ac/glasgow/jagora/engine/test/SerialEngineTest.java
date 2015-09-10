package uk.ac.glasgow.jagora.engine.test;

import static org.easymock.EasyMock.expect;

import java.util.Queue;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngine;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.engine.impl.delay.DelayedExchangeLevel1View.DelayedOrderExecutor;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.world.World;

public class SerialEngineTest extends EasyMockSupport{
		
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	@Mock
	private World world;
	
	@Mock
	private StockExchange stockExchange;
	
	@Mock
	private Level1Trader dan;
	
	@Mock
	private StockExchangeLevel1View traderView;
	
	@Mock
	private Queue<DelayedOrderExecutor> orderExecutorQueue;
	
	private SerialRandomEngine engine;

	@Before
	public void setUp() throws Exception {

		engine = new SerialRandomEngineBuilder()
			.setWorld(world)
			.setSeed(1)
			.addStockExchange(stockExchange)
			.addTraderStockExchangeView(dan, stockExchange)
			.build();
	}

	@Test
	public void testEngineRunningCorrectly(){
		
		expect(world.isAlive()).andReturn(true);
		expect(stockExchange.createLevel1View()).andReturn(traderView);
		dan.speak(traderView);
		stockExchange.doClearing();
		expect(world.isAlive()).andReturn(false);
				
		replayAll ();
		
		engine.run();
		
		verifyAll();
	}
}
