package uk.ac.glasgow.jagora.test.unit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;

public class SellOrderTest {
	
	private static final Stock lemons = new Stock("lemons");
	
	private SellOrder[] sellOrders;

	@Before
	public void setUp() throws Exception {
		sellOrders = new SellOrder[3];
		
		sellOrders[0] = new SellOrder(null, lemons, 10, 50.0);
		sellOrders[1] = new SellOrder(null, lemons, 10, 50.0);
		sellOrders[2] = new SellOrder(null, lemons, 10, 55.0);
	}

	@Test
	public void testEquals() {
		
		assertTrue(sellOrders[0].equals(sellOrders[1]));
		
		assertFalse(sellOrders[0].equals(sellOrders[2]));
		
		assertFalse(sellOrders[2].equals(sellOrders[0]));
	}
	
	@Test
	public void testCompareTo() {

		assertEquals(0, sellOrders[0].compareTo(sellOrders[1]));
		
		assertThat(sellOrders[0].compareTo(sellOrders[2]), lessThan(0));
		
		assertThat(sellOrders[2].compareTo(sellOrders[0]), greaterThan(0));

	}


}
