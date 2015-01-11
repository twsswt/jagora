package uk.ac.glasgow.jagora.test.unit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Stock;

public class BuyOrderTest {
	
	private static final Stock lemons = new Stock("lemons");
	
	private BuyOrder[] buyOrders;

	@Before
	public void setUp() throws Exception {
		buyOrders = new BuyOrder[3];
		
		buyOrders[0] = new BuyOrder(null, lemons, 10, 50.0);
		buyOrders[1] = new BuyOrder(null, lemons, 10, 50.0);
		buyOrders[2] = new BuyOrder(null, lemons, 10, 45.0);
	}

	@Test
	public void testEquals() {
		
		assertTrue(buyOrders[0].equals(buyOrders[1]));
		
		assertFalse(buyOrders[0].equals(buyOrders[2]));
		
		assertFalse(buyOrders[2].equals(buyOrders[0]));
	}
	
	@Test
	public void testCompareTo() {

		assertEquals(0, buyOrders[0].compareTo(buyOrders[1]));
		
		assertThat(buyOrders[0].compareTo(buyOrders[2]), lessThan(0));
		
		assertThat(buyOrders[2].compareTo(buyOrders[0]), greaterThan(0));

	}


}
