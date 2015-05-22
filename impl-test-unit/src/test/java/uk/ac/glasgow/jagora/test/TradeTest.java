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

	@Before
	public void setUp() throws Exception {
		alice = new StubTraderBuilder("alice", 1000000.00)
			.addStock(lemons, 10000)
			.build();
		
		bob = new StubTraderBuilder("alice", 1000000.00)
			.addStock(lemons, 10000)
			.build();
	
		
		limitBuyOrder = new LimitBuyOrder(alice, lemons, 500, 50.0);
		limitSellOrder = new LimitSellOrder(bob, lemons, 1000, 45.0);
		
		trade = new DefaultTrade(lemons, 500, 45.0, limitSellOrder, limitBuyOrder);
		
		world = new ManualTickWorld();
		world.setTickForEvent(0l, trade);
	}

	@Test
	public void test() throws Exception {
		TickEvent<Trade> executedTrade = trade.execute(world);
		
		assertEquals("", 0l, executedTrade.tick.longValue());
		assertEquals("", 1000000.0-45*500, alice.getCash(), 0.0);
		
		assertEquals("", 1000000.0+45*500, bob.getCash(), 0.0);
		
		assertEquals("", 10500, alice.getInventory(lemons).intValue());
		assertEquals("",  9500, bob.getInventory(lemons).intValue());
		
		assertEquals("", valueOf(0), limitBuyOrder.getRemainingQuantity());
		assertEquals("", valueOf(500), limitSellOrder.getRemainingQuantity());
	}

}