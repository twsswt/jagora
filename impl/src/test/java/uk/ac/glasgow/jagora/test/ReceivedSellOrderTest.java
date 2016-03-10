package uk.ac.glasgow.jagora.test;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.world.TickEvent;

public class ReceivedSellOrderTest extends EasyMockSupport{
	
	private static final Stock lemons = new Stock("lemons");
		
	private TickEvent<LimitSellOrder>[] receivedOrders;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		receivedOrders = new TickEvent[5];
		
		receivedOrders[0] = createSellOrder(10, 50l, 0l);
		receivedOrders[1] = createSellOrder(10, 50l, 0l);
		receivedOrders[2] = createSellOrder(10, 50l, 1l);
		receivedOrders[3] = createSellOrder(10, 55l, 1l);
		receivedOrders[4] = createSellOrder(10, 65l, 1l);

		
		
	}

	private TickEvent<LimitSellOrder> createSellOrder(Integer quantity, Long price, Long tick) {
		LimitSellOrder limitSellOrder = new DefaultLimitSellOrder(null, lemons, quantity, price);
		return new TickEvent<LimitSellOrder>(limitSellOrder, tick);
	}

	@Test
	public void testEqualsWhenEqual() {
		assertTrue(receivedOrders[0].equals(receivedOrders[1]));	
	}

	@Test
	public void testEqualsWhenLessThan() {
		assertFalse(receivedOrders[0].equals(receivedOrders[2]));
	}

	@Test
	public void testEqualsWhenGreaterThan() {
		assertFalse(receivedOrders[2].equals(receivedOrders[0]));
	}
	
	@Test
	public void testCompareToWhenEqualTo() {
		assertEquals(0, receivedOrders[0].compareTo(receivedOrders[1]));	
	}
	
	@Test
	public void testCompareToWhenLessThan() {
		assertThat(receivedOrders[0].compareTo(receivedOrders[2]), lessThan(0));	
	}
	
	@Test
	public void testCompareToWhenGreaterThan() {
		assertThat(receivedOrders[2].compareTo(receivedOrders[0]), greaterThan(0));
	}


}
