package uk.ac.glasgow.jagora.trader.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.test.stub.StubStockExchange;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.MarginalTraderBuilder;

public class MarginalTraderTest {
	
	private final Integer numberOfTraderActions = 10;
	private final Double initialTraderCash = 1000000.00;
	
	private Stock lemons;
	
	private Trader alice;
	private Trader bob;
	
	private StubStockExchange marketForLemons;

	@Before
	public void setUp() throws Exception {
				
		lemons = new Stock("lemons");

		alice = new MarginalTraderBuilder("alice",initialTraderCash,1)
			.addStock(lemons, 10000)
			.build();
		
		bob = new StubTraderBuilder("bob", initialTraderCash)
			.addStock(lemons, 10000)
			.build();
		
		marketForLemons = new StubStockExchange();
	}

	@Test
	public void test() {
		
		StockExchangeTraderView traderOrderDrivenMarketView = 
			marketForLemons.createTraderStockExchangeView();
		
		BuyOrder buyOrder = new LimitBuyOrder(bob, lemons, 10, 5.1);
		traderOrderDrivenMarketView.placeBuyOrder(buyOrder);
		
		SellOrder sellOrder = new LimitSellOrder(bob, lemons, 10, 4.9);
		traderOrderDrivenMarketView.placeSellOrder(sellOrder);
		
		for (Integer i = 0; i < numberOfTraderActions; i++) 
			alice.speak(traderOrderDrivenMarketView);
		
		// Given at least one bid and ask in the market, the marginal
		// trader should continually add slightly better trades.
		
		List<SellOrder> sellOrders = marketForLemons.getSellOrders(lemons);
		List<BuyOrder> buyOrders = marketForLemons.getBuyOrders(lemons);
		
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
				.mapToDouble(order -> order.getPrice())
				.sum();
	}

	private Double computeMarginalDifference(List<? extends Order> orders) {
		return orders.size() * orders.size() * Double.MIN_NORMAL / 2;
	}

}
