package uk.ac.glasgow.jagora.test.unit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.ExecutedTrade;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.test.stub.StubTrader;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.test.stub.StubWorld;

public class TradeTest {
	
	private Stock lemons = new Stock ("lemons");
	
	private StubTrader alice, bob;
	
	private BuyOrder buyOrder;
	private SellOrder sellOrder;
	
	private StubWorld world;
	
	private Trade trade;

	@Before
	public void setUp() throws Exception {
		alice = new StubTraderBuilder("alice", 1000000.00)
			.addStock(lemons, 10000)
			.build();
		
		bob = new StubTraderBuilder("alice", 1000000.00)
			.addStock(lemons, 10000)
			.build();
	
		
		buyOrder = new BuyOrder(alice, lemons, 500, 50.0);
		sellOrder = new SellOrder(bob, lemons, 1000, 45.0);
		
		trade = new Trade(lemons, 500, 45.0, sellOrder, buyOrder);
		
		world = new StubWorld();
		world.registerEventForTick(trade, 0l);
	}

	@Test
	public void test() throws Exception {
		ExecutedTrade executedTrade = trade.execute(world);
		
		assertEquals("", 0l, executedTrade.tick.longValue());
		assertEquals("", 1000000.0-45*500, alice.getCash(), 0.0);
		
		assertEquals("", 1000000.0+45*500, bob.getCash(), 0.0);
		
		assertEquals("", 10500, alice.getInventory(lemons).intValue());
		assertEquals("",  9500, bob.getInventory(lemons).intValue());
		
		assertEquals("", new Integer(0), buyOrder.getRemainingQuantity());
		assertEquals("", new Integer(500), sellOrder.getRemainingQuantity());
	}

}
