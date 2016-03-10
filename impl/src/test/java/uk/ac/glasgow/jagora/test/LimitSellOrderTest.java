package uk.ac.glasgow.jagora.test;

import org.junit.Before;
import org.junit.Test;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class LimitSellOrderTest {
	
	private static final Stock lemons = new Stock("lemons");
	
	private LimitSellOrder[] limitSellOrders;

	@Before
	public void setUp() throws Exception {
		limitSellOrders = new LimitSellOrder[3];
		
		limitSellOrders[0] = new DefaultLimitSellOrder(null, lemons, 10, 500l);
		limitSellOrders[1] = new DefaultLimitSellOrder(null, lemons, 10, 500l);
		limitSellOrders[2] = new DefaultLimitSellOrder(null, lemons, 10, 550l);
	}

	@Test
	public void testEquals() {		
		assertTrue(limitSellOrders[0].equals(limitSellOrders[1]));
	}

	@Test
	public void testNotEqualsWhenLessThan() {
		assertFalse(limitSellOrders[0].equals(limitSellOrders[2]));
	}

	@Test
	public void testNotEqualsWhenGreaterThan() {		
		assertFalse(limitSellOrders[2].equals(limitSellOrders[0]));
	}

	
	@Test
	public void testCompareToWhenEqual() {
		assertThat(limitSellOrders[0].compareTo(limitSellOrders[1]), equalTo(0));
	}

	@Test
	public void testCompareToWhenLessThan() {
		assertThat(limitSellOrders[0].compareTo(limitSellOrders[2]), lessThan(0));
	}
	
	@Test
	public void testCompareToWhenGreaterThan() {		
		assertThat(limitSellOrders[2].compareTo(limitSellOrders[0]), greaterThan(0));
	}


}
