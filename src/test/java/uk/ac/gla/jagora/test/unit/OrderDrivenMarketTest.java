package uk.ac.gla.jagora.test.unit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.OrderDrivenMarket;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.Trader;
import uk.ac.gla.jagora.test.stub.StubTraderBuilder;
import uk.ac.gla.jagora.worlds.SimpleSerialWorld;

public class OrderDrivenMarketTest {

	private Stock apples = new Stock("apples");
	
	private Trader alice = new StubTraderBuilder("alice", 10000.00).addStock(apples, 100).build();
	private Trader bob   = new StubTraderBuilder("bob", 500.00).addStock(apples, 200).build();
	
	private OrderDrivenMarket orderDrivenMarket;

	@Before
	public void setUp() throws Exception {
		orderDrivenMarket = new OrderDrivenMarket(new SimpleSerialWorld());
	}

	@Test
	public void test() {
		
		SellOrder sellOrder1 = new SellOrder(bob, apples, 50, 55.0);
		orderDrivenMarket.registerSellOrder(sellOrder1);

		BuyOrder buyOrder1 = new BuyOrder(alice, apples, 25, 45.0);
		orderDrivenMarket.registerBuyOrder(buyOrder1);

		orderDrivenMarket.doClearing();
				
		assertEquals("", 500.0, bob.getCash().doubleValue(), 0.0);
		
		SellOrder sellOrder2 = new SellOrder(bob, apples, 10, 55.9);
		orderDrivenMarket.registerSellOrder(sellOrder2);
		
		BuyOrder buyOrder2 = new BuyOrder(alice, apples, 60, 56.0);
		orderDrivenMarket.registerBuyOrder(buyOrder2);
		
		orderDrivenMarket.doClearing();
		
		Double price = 50 * 55.0 + 10 * 55.9;
		
		assertEquals("", 500.0 + price, bob.getCash().doubleValue(), 0.0);
		assertEquals("", 10000.0 - price, alice.getCash().doubleValue(), 0.0);
		
		//fail("Not yet implemented");
	}

}
