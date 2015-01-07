package uk.ac.gla.jagora.test.unit;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.Order;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.Trader;
import uk.ac.gla.jagora.orderdriven.OrderDrivenStockExchangeTraderView;
import uk.ac.gla.jagora.test.stub.StubOrderDrivenMarket;
import uk.ac.gla.jagora.test.stub.StubTraderBuilder;
import uk.ac.gla.jagora.trader.MarginalOrderDrivenTraderBuilder;

public class MarginalOrderDrivenTraderTest {
	
	private final Integer numberOfTraderActions = 10000;
	private final Double initialTraderCash = 1000000.00;
	
	private Stock apples;
	
	private Trader alice;
	private Trader bob;
	
	private StubOrderDrivenMarket orderDrivenMarket;

	@Before
	public void setUp() throws Exception {
				
		apples = new Stock("apples");

		alice = new MarginalOrderDrivenTraderBuilder("alice",initialTraderCash,1)
			.addStock(apples, 10000)
			.build();
		
		bob = new StubTraderBuilder("bob", initialTraderCash)
			.addStock(apples, 10000)
			.build();
		
		orderDrivenMarket = new StubOrderDrivenMarket();
	}

	@Test
	public void test() {
		
		OrderDrivenStockExchangeTraderView traderOrderDrivenMarketView = 
			orderDrivenMarket.createTraderMarketView();
		
		BuyOrder buyOrder = new BuyOrder(bob, apples, 10, 5.1);
		traderOrderDrivenMarketView.registerBuyOrder(buyOrder);
		
		SellOrder sellOrder = new SellOrder(bob, apples, 10, 4.9);
		traderOrderDrivenMarketView.registerSellOrder(sellOrder);
		
		for (Integer i = 0; i < numberOfTraderActions; i++) 
			alice.speak(traderOrderDrivenMarketView);
		
		// Given at least one bid and ask in the market, the marginal
		// trader should continually add slightly better trades.
		
		List<SellOrder> sellOrders = orderDrivenMarket.getSellOrders(apples);
		List<BuyOrder> buyOrders = orderDrivenMarket.getBuyOrders(apples);
		
		assertEquals("", numberOfTraderActions + 2, sellOrders.size() + buyOrders.size());
		
		checkTotalPrice(sellOrders, 4.9, 5.1);
				
		checkTotalPrice(buyOrders, 5.1, 4.9);

	}
	
	private void checkTotalPrice(
		List<? extends Order> orders, Double counterPartyPrice, Double marginalPrice) {
		
		Double expected =
			computeExpectedPrice(counterPartyPrice, orders, marginalPrice);
		
		Double actual = computeTotalPrice(orders);

		assertEquals("", expected, actual, 0.000001);
	}
	
	private Double computeExpectedPrice(
			Double counterPartyPrice, List<? extends Order> orders, Double marginalPrice){
			
			Double totalMarginalDifference = 
					computeMarginalDifference(orders);
			
			return 
				counterPartyPrice + (orders.size() - 1) * marginalPrice + totalMarginalDifference;
		}


	private Double computeTotalPrice(List<? extends Order> orders) {
		return
			orders.stream()
				.mapToDouble(order -> order.price)
				.sum();
	}

	private Double computeMarginalDifference(List<? extends Order> orders) {
		return orders.size() * orders.size() * Double.MIN_NORMAL / 2;
	}

}
