package uk.ac.glasgow.jagora.test;

import org.junit.Before;
import org.junit.Test;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.AbstractBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class BuyOrderTest {
	
	private static final Stock lemons = new Stock("lemons");
	
	private AbstractBuyOrder[] limitBuyOrders;

	@Before
	public void setUp() throws Exception {
		limitBuyOrders = new AbstractBuyOrder[3];

		limitBuyOrders[0] = new LimitBuyOrder(null, lemons, 10, 5000l);
		limitBuyOrders[1] = new LimitBuyOrder(null, lemons, 10, 5000l);
		limitBuyOrders[2] = new LimitBuyOrder(null, lemons, 10, 4500l);
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
