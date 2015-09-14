package uk.ac.glasgow.jagora.engine.test;

import static org.easymock.Capture.newInstance;
import static org.easymock.EasyMock.expect;

import java.util.Queue;
import java.util.Random;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngine;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngineBuilder;
import uk.ac.glasgow.jagora.engine.impl.delay.DelayedExchangeLevel1View;
import uk.ac.glasgow.jagora.engine.impl.delay.DelayedExchangeLevel1View.DelayedOrderExecutor;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.world.World;

public class SerialEngineTest extends EasyMockSupport{
	
	private Stock lemons = new Stock("lemons");
	
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	@Mock
	private World world;
	
	@Mock
	private StockExchange stockExchange;
		
	@Mock(name="alice")
	private Level1Trader alice;
	
	@Mock(name="bob")
	private Level1Trader bob;
	
	@Mock
	private Random random;
	
	@Mock
	private StockExchangeLevel1View traderView;
	
	@Mock
	private DelayedExchangeLevel1View delayedView;
	
	
	@Mock
	private Queue<DelayedOrderExecutor> orderExecutorQueue;
	
	private SerialRandomEngineBuilder serialRandomEngineBuilder;

	@Before
	public void setUp() throws Exception {

		serialRandomEngineBuilder = new SerialRandomEngineBuilder()
			.setWorld(world)
			.setRandom(random)
			.addStockExchange(stockExchange);
	}

	@Test
	public void testEngineGivesTradersOpportunityToSpeak(){
		
		SerialRandomEngine engine = serialRandomEngineBuilder
			.addTraderStockExchangeView(alice, stockExchange)
			.addTraderStockExchangeView(bob, stockExchange)
			.build();
		
		expect(world.isAlive()).andReturn(true);
		expect(random.nextInt(2)).andReturn(0);
		expect(stockExchange.createLevel1View()).andReturn(traderView);
		alice.speak(traderView);
		stockExchange.doClearing();
		expect(world.isAlive()).andReturn(true);
		expect(random.nextInt(2)).andReturn(1);
		expect(stockExchange.createLevel1View()).andReturn(traderView);
		bob.speak(traderView);
		stockExchange.doClearing();
		expect(world.isAlive()).andReturn(false);
		
		replayAll ();
		
		engine.run();
		
		verifyAll();
	}
	
	@Test
	public void testDelayedOrderExecution (){
		
		SerialRandomEngine engine = serialRandomEngineBuilder
			.addDelayedTraderView(alice, 1l, stockExchange)
			.build();
		
		LimitBuyOrder limitBuyOrder = 
			new DefaultLimitBuyOrder(alice, lemons, 1, 1l);
		
		Capture<DelayedExchangeLevel1View> capturedArgument = newInstance();
		
		// Turn one, ending in Alice placing a delayed order.
		expect(world.isAlive()).andReturn(true);
		expect(random.nextInt(1)).andReturn(0);
		expect(stockExchange.createLevel1View()).andReturn(traderView);
		expect(world.getCurrentTick()).andReturn(1l);
		alice.speak(EasyMock.capture(capturedArgument));
		stockExchange.doClearing();
		
		expect(world.isAlive()).andReturn(false);

		// Loop two, ending with the execution of Alice's delayed order.
		expect(world.isAlive()).andReturn(true);
		expect(random.nextInt(1)).andReturn(0);
		expect(stockExchange.createLevel1View()).andReturn(traderView);
		alice.speak(EasyMock.anyObject());	
		stockExchange.doClearing();
		
		// Processing delayed actions
		expect(world.getCurrentTick()).andReturn(2l);
		expect(world.getCurrentTick()).andReturn(2l);
		traderView.placeLimitBuyOrder(limitBuyOrder);
		
		// Loop three
		expect(world.isAlive()).andReturn(false);
		
		replayAll ();
		
		engine.run();
		
		DelayedExchangeLevel1View delayedView = 
			capturedArgument.getValue();
		
		delayedView.placeLimitBuyOrder(limitBuyOrder);
		
		engine.run();
		
		verifyAll();
	}
}
