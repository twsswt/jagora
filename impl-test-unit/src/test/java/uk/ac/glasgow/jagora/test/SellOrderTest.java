package uk.ac.glasgow.jagora.test;

import org.junit.Before;
import org.junit.Test;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class SellOrderTest {
	
	private static final Stock lemons = new Stock("lemons");
	
	private SellOrder[] sellOrders;

	@Before
	public void setUp() throws Exception {
		sellOrders = new SellOrder[3];
		
		sellOrders[0] = new LimitSellOrder(null, lemons, 10, 500l);
		sellOrders[1] = new LimitSellOrder(null, lemons, 10, 500l);
		sellOrders[2] = new LimitSellOrder(null, lemons, 10, 550l);
	}

	@Test
	public void testEquals() {		
		assertTrue(sellOrders[0].equals(sellOrders[1]));
	}

	@Test
	public void testNotEqualsWhenLessThan() {
		assertFalse(sellOrders[0].equals(sellOrders[2]));
	}

	@Test
	public void testNotEqualsWhenGreaterThan() {		
		assertFalse(sellOrders[2].equals(sellOrders[0]));
	}

	
	@Test
	public void testCompareToWhenEqual() {
		assertThat(sellOrders[0].compareTo(sellOrders[1]), equalTo(0));
	}

	@Test
	public void testCompareToWhenLessThan() {
		assertThat(sellOrders[0].compareTo(sellOrders[2]), lessThan(0));
	}
	
	@Test
	public void testCompareToWhenGreaterThan() {		
		assertThat(sellOrders[2].compareTo(sellOrders[0]), greaterThan(0));
	}


}
