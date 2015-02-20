package uk.ac.glasgow.jagora.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.test.stub.ManualTickWorld;
import uk.ac.glasgow.jagora.world.TickEvent;

public class ReceivedSellOrderTest {
	
	private static final Stock lemons = new Stock("lemons");
	
	private static final ManualTickWorld world = new ManualTickWorld();
	
	private TickEvent<SellOrder>[] receivedOrders;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		receivedOrders = new TickEvent[5];
		
		receivedOrders[0] = createSellOrder(10, 50.0, 0l);
		receivedOrders[1] = createSellOrder(10, 50.0, 0l);
		receivedOrders[2] = createSellOrder(10, 50.0, 1l);
		receivedOrders[3] = createSellOrder(10, 55.0, 1l);
		receivedOrders[4] = createSellOrder(10, 65.0, 1l);

	}

	private TickEvent<SellOrder> createSellOrder(Integer quantity, Double price, Long tick) {
		SellOrder sellOrder = new LimitSellOrder(null, lemons, quantity, price);
		world.setTickForEvent(tick, sellOrder);
		return world.getTick(sellOrder);
	}

	@Test
	public void testEquals() {
		
		assertTrue(receivedOrders[0].equals(receivedOrders[1]));
		
		assertFalse(receivedOrders[0].equals(receivedOrders[2]));
		
		assertFalse(receivedOrders[2].equals(receivedOrders[0]));
	}
	
	@Test
	public void testCompareTo() {

		assertEquals(0, receivedOrders[0].compareTo(receivedOrders[1]));
		
		assertThat(receivedOrders[0].compareTo(receivedOrders[2]), lessThan(0));
		
		assertThat(receivedOrders[2].compareTo(receivedOrders[0]), greaterThan(0));

	}


}
