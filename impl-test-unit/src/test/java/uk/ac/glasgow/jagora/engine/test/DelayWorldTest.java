package uk.ac.glasgow.jagora.engine.test;

import static org.easymock.EasyMock.expect;

import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.engine.impl.DelayableSerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.engine.impl.SerialDelayEngine;
import uk.ac.glasgow.jagora.engine.impl.delay.DelayedExchangeLevel1View;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.world.World;

public class DelayWorldTest extends EasyMockSupport{
		
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	@Mock
	private World world;
	
	@Mock
	private StockExchange stockExchange;
	
	@Mock
	private Level1Trader dan;
	
	private SerialDelayEngine engine;

	@Mock
	private StockExchangeLevel1View traderView;
	

	@Before
	public void setUp() throws Exception {

		Set<Level1Trader> traders = new HashSet<Level1Trader>();
		traders.add(dan);

		engine = new DelayableSerialRandomEngineBuilder()
			.setWorld(world)
			.setStockExchange(stockExchange)
			.addTraders(traders)
			.setStandardDelay(5l)
			.build();
	}

	@Test
	public void testEngineRunningCorrectly(){
		
		expect(world.isAlive()).andReturn(true);
		expect(stockExchange.createLevel1View()).andReturn(traderView);
		
		expect(world.getCurrentTick()).andReturn(1l);
		dan.speak(new DelayedExchangeLevel1View(traderView, 6l));
		stockExchange.doClearing();
		expect(world.isAlive()).andReturn(false);
		
		replayAll ();
		
		engine.run();
		
		verifyAll();
	}
}
