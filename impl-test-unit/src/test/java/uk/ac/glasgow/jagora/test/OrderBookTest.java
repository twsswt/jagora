package uk.ac.glasgow.jagora.test;

import static java.util.Collections.shuffle;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.impl.orderbook.LimitOrderBook;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class OrderBookTest extends EasyMockSupport {
	
	private static Stock lemons = new Stock("lemons");
	
	@Rule
	public EasyMockRule rule = new EasyMockRule(this);
	
	@Mock(name="alice")
	private Trader alice;
	
	@Mock(name="bob")
	private Trader bob;
	
	@Mock
	private World world;

	private LimitOrderBook<LimitSellOrder> sellBook;
	private LimitOrderBook<LimitBuyOrder> buyBook;
	
	
	@Before
	public void setUp() throws Exception {
				
		sellBook = new LimitOrderBook<LimitSellOrder>(world);
		buyBook = new LimitOrderBook<LimitBuyOrder>(world);
	}

	@Test
	public void testSellOrderBook() throws Exception{
		
		List<LimitSellOrder> expectedSellOrders = new ArrayList<LimitSellOrder>();

		expectedSellOrders.add(new DefaultLimitSellOrder(alice, lemons, 500,  50l));
		expectedSellOrders.add(new DefaultLimitSellOrder(bob,   lemons,  15, 110l));
		expectedSellOrders.add(new DefaultLimitSellOrder(alice, lemons,  10, 110l));
		expectedSellOrders.add(new DefaultLimitSellOrder(alice, lemons,   5, 200l));
		expectedSellOrders.add(new DefaultLimitSellOrder(bob,   lemons,  10, 250l));

		List<LimitSellOrder> randomisedSellOrders =
			new ArrayList<LimitSellOrder>(expectedSellOrders);
		
		shuffle(randomisedSellOrders);
		
		
		resetAll ();
		
		for (LimitSellOrder limitSellOrder : randomisedSellOrders){
			int index = 
				expectedSellOrders.indexOf(limitSellOrder);
			expect(
				world
					.getTick(limitSellOrder))
					.andReturn(new TickEvent<LimitSellOrder>(limitSellOrder, (long) index));
		}
				
		replayAll ();
		
		for (LimitSellOrder limitSellOrder : randomisedSellOrders)
			sellBook.recordOrder(limitSellOrder);
				
		List<LimitSellOrder> actualSellOrders = sellBook.getOpenOrders();
		assertEquals(expectedSellOrders, actualSellOrders);

		verifyAll ();
		
		
	}
	
	@Test
	public void testBuyOrderBook() throws Exception{
		
		List<LimitBuyOrder> expectedBuyOrders = new ArrayList<LimitBuyOrder>();

		expectedBuyOrders.add(new DefaultLimitBuyOrder(alice, lemons, 500,  5l));
		expectedBuyOrders.add(new DefaultLimitBuyOrder(alice, lemons,  15,  4l));
		expectedBuyOrders.add(new DefaultLimitBuyOrder(alice, lemons,  10,  3l));
		expectedBuyOrders.add(new DefaultLimitBuyOrder(alice, lemons,   5,  3l));
		expectedBuyOrders.add(new DefaultLimitBuyOrder(bob,   lemons,  10,  2l));

		List<LimitBuyOrder> randomisedBuyOrders =
			new ArrayList<LimitBuyOrder>(expectedBuyOrders);
		
		shuffle(randomisedBuyOrders);
		
		resetAll ();
		
		for (LimitBuyOrder limitBuyOrder : randomisedBuyOrders){
			int index = 
				expectedBuyOrders.indexOf(limitBuyOrder);
			expect(
				world
					.getTick(limitBuyOrder))
					.andReturn(new TickEvent<LimitBuyOrder>(limitBuyOrder, (long) index));
		}
				
		replayAll ();
		
		for (LimitBuyOrder limitBuyOrder : randomisedBuyOrders)
			buyBook.recordOrder(limitBuyOrder);
				
		List<LimitBuyOrder> actualBuyOrders = buyBook.getOpenOrders();
		assertEquals(expectedBuyOrders, actualBuyOrders);

		verifyAll ();
	
	}
}
