package uk.ac.glasgow.jagora.test;

import static org.easymock.EasyMock.expect;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.TradeExecutionException;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.impl.DefaultTrade;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class TradeTest extends EasyMockSupport {
	
	private Stock lemons = new Stock ("lemons");
	
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	@Mock(name="alice")
	private Trader alice;
	
	@Mock(name="bob")
	private Trader bob;
	
	@Mock
	private World world;
	
	private LimitBuyOrder limitBuyOrder;
	private LimitSellOrder limitSellOrder;
		
	private Trade trade;

	@Before
	public void setUp() throws Exception {
			
		limitBuyOrder = new DefaultLimitBuyOrder(alice, lemons, 500, 500l);
		limitSellOrder = new DefaultLimitSellOrder(bob, lemons, 1000, 450l);
		
		trade = new DefaultTrade(lemons, 500, 450l, limitSellOrder, limitBuyOrder);
	}
	
	@Test
	public void testThatExecutedTradeHasExecuted() throws TradeExecutionException {
		
		resetAll ();
		
		expect(world.getTick(trade)).andReturn(new TickEvent<Trade>(trade, 0l));
		bob.sellStock(trade);
		alice.buyStock(trade);
		
		replayAll ();
		
		trade.execute(world);
		
		verifyAll();
	}
}
