package uk.ac.glasgow.jagora.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;

public class BuyOrderTest {
	
	private static final Stock lemons = new Stock("lemons");
	
	private BuyOrder[] limitBuyOrders;

	@Before
	public void setUp() throws Exception {
		limitBuyOrders = new BuyOrder[3];
		
		limitBuyOrders[0] = new LimitBuyOrder(null, lemons, 10, 50.0);
		limitBuyOrders[1] = new LimitBuyOrder(null, lemons, 10, 50.0);
		limitBuyOrders[2] = new LimitBuyOrder(null, lemons, 10, 45.0);
	}

	@Test
	public void testEquals() {
		
		assertTrue(limitBuyOrders[0].equals(limitBuyOrders[1]));
		
		assertFalse(limitBuyOrders[0].equals(limitBuyOrders[2]));
		
		assertFalse(limitBuyOrders[2].equals(limitBuyOrders[0]));
	}
	
	@Test
	public void testCompareTo() {

		assertEquals(0, limitBuyOrders[0].compareTo(limitBuyOrders[1]));
		
		assertThat(limitBuyOrders[0].compareTo(limitBuyOrders[2]), lessThan(0));
		
		assertThat(limitBuyOrders[2].compareTo(limitBuyOrders[0]), greaterThan(0));

	}


}
