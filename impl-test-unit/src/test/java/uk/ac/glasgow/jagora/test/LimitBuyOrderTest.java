package uk.ac.glasgow.jagora.test;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class LimitBuyOrderTest {
	
	private static final Stock lemons = new Stock("lemons");
	
	private LimitBuyOrder[] limitBuyOrders;

	@Before
	public void setUp() throws Exception {
		limitBuyOrders = new LimitBuyOrder[3];

		limitBuyOrders[0] = new DefaultLimitBuyOrder(null, lemons, 10, 5000l);
		limitBuyOrders[1] = new DefaultLimitBuyOrder(null, lemons, 10, 5000l);
		limitBuyOrders[2] = new DefaultLimitBuyOrder(null, lemons, 10, 4500l);
	}

	@Test
	public void testEquals() {		
		assertTrue(limitBuyOrders[0].equals(limitBuyOrders[1]));
	}

	@Test
	public void testNotEqualsWhenLessThan() {
		assertFalse(limitBuyOrders[0].equals(limitBuyOrders[2]));
	}

	@Test
	public void testNotEqualsWhenGreaterThan() {		
		assertFalse(limitBuyOrders[2].equals(limitBuyOrders[0]));
	}

	
	@Test
	public void testCompareToWhenEqual() {
		assertThat(limitBuyOrders[0].compareTo(limitBuyOrders[1]), equalTo(0));
	}

	@Test
	public void testCompareToWhenLessThan() {
		assertThat(limitBuyOrders[0].compareTo(limitBuyOrders[2]), lessThan(0));
	}
	
	@Test
	public void testCompareToWhenGreaterThan() {		
		assertThat(limitBuyOrders[2].compareTo(limitBuyOrders[0]), greaterThan(0));
	}


}
