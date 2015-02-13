package uk.ac.glasgow.jagora.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.AbstractSellOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;

public class SellOrderTest {
	
	private static final Stock lemons = new Stock("lemons");
	
	private AbstractSellOrder[] limitSellOrders;

	@Before
	public void setUp() throws Exception {
		limitSellOrders = new AbstractSellOrder[3];
		
		limitSellOrders[0] = new LimitSellOrder(null, lemons, 10, 50.0);
		limitSellOrders[1] = new LimitSellOrder(null, lemons, 10, 50.0);
		limitSellOrders[2] = new LimitSellOrder(null, lemons, 10, 55.0);
	}

	@Test
	public void testEquals() {
		
		assertTrue(limitSellOrders[0].equals(limitSellOrders[1]));
		
		assertFalse(limitSellOrders[0].equals(limitSellOrders[2]));
		
		assertFalse(limitSellOrders[2].equals(limitSellOrders[0]));
	}
	
	@Test
	public void testCompareTo() {

		assertEquals(0, limitSellOrders[0].compareTo(limitSellOrders[1]));
		
		assertThat(limitSellOrders[0].compareTo(limitSellOrders[2]), lessThan(0));
		
		assertThat(limitSellOrders[2].compareTo(limitSellOrders[0]), greaterThan(0));

	}


}
