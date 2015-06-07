package uk.ac.glasgow.jagora.test;

import static java.lang.Integer.valueOf;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.impl.DefaultTrade;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.test.stub.StubTrader;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.test.stub.ManualTickWorld;
import uk.ac.glasgow.jagora.world.TickEvent;

public class TradeTest {
	
	private Stock lemons = new Stock ("lemons");
	
	private StubTrader alice, bob;
	
	private BuyOrder limitBuyOrder;
	private SellOrder limitSellOrder;
	
	private ManualTickWorld world;
	
	private DefaultTrade trade;

	private TickEvent<Trade> executedTrade;

	@Before
	public void setUp() throws Exception {
		alice = new StubTraderBuilder("alice", 100000000l)
			.addStock(lemons, 10000)
			.build();
		
		bob = new StubTraderBuilder("alice", 100000000l)
			.addStock(lemons, 10000)
			.build();
	
		
		limitBuyOrder = new LimitBuyOrder(alice, lemons, 500, 500l);
		limitSellOrder = new LimitSellOrder(bob, lemons, 1000, 450l);
		
		trade = new DefaultTrade(lemons, 500, 450l, limitSellOrder, limitBuyOrder);
		
		world = new ManualTickWorld();
		world.setTickForEvent(0l, trade);
		
		executedTrade = trade.execute(world);
	}

	@Test
	public void testThatExecutedTradeHasCorrectTick() throws Exception {		
		assertEquals("", 0l, executedTrade.tick.longValue());
	}

	@Test
	public void testThatAliceWasDebited() throws Exception {
		assertEquals("", 100000000l-450*500, alice.getCash(), 0.0);
	}

	@Test
	public void testThatBobWasCredited() throws Exception {

		assertEquals("", 100000000l+450*500, bob.getCash(), 0.0);

	}

	@Test
	public void testThatAliceBoughtLemons() throws Exception {
		assertEquals("", 10500, alice.getInventory(lemons).intValue());
	}

	@Test
	public void testThatBobSoldLemons() throws Exception {
		assertEquals("",  9500, bob.getInventory(lemons).intValue());
	}

	@Test
	public void testThatAlicesOrderWasReduced() throws Exception {
		assertEquals("", valueOf(0), limitBuyOrder.getRemainingQuantity());
	}

	@Test
	public void testThatBobsOrderWasReduced() throws Exception {
		assertEquals("", valueOf(500), limitSellOrder.getRemainingQuantity());
	}

}
