package uk.ac.gla.jagora.test.unit;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.Trader;
import uk.ac.gla.jagora.orderdrivenmarket.BuyOrder;
import uk.ac.gla.jagora.orderdrivenmarket.OrderDrivenMarket;
import uk.ac.gla.jagora.orderdrivenmarket.SellOrder;
import uk.ac.gla.jagora.test.stub.StubOrderDrivenMarket;
import uk.ac.gla.jagora.trader.RandomOrderTraderBuilder;

public class RandomOrderTraderTest {
	
	private final Integer numberOfTraderActions = 10000;

	
	private Trader trader;
	private Stock apples;
	
	private OrderDrivenMarket orderDrivenMarket;

	@Before
	public void setUp() throws Exception {
		
		orderDrivenMarket = new StubOrderDrivenMarket();
		
		apples  = new Stock("apples");
		
		// A trader that can create many small buy and sell orders 
		// without needing to cancel due to lack of liquidity.
		trader = new RandomOrderTraderBuilder("alice",10000000.0,1)
			.addStock(apples, 500000)
			.addTradeRange(apples, 0.0, 10.0, 1, 100)
			.build();
	}

	@Test
	public void test() {

		for (Integer i = 0; i < numberOfTraderActions; i++)
			trader.speak(orderDrivenMarket.createTraderMarketView());
		
		List<BuyOrder> buyOrders = 
			orderDrivenMarket.getBuyOrders(apples);		
		
		List<SellOrder> sellOrders = 
				orderDrivenMarket.getSellOrders(apples);
		
		Double actualAverageBuyPrice = 
			buyOrders.stream()
			.mapToDouble(buyOrder -> buyOrder.price)
			.average()
			.getAsDouble();
		
		
		Double actualAverageSellPrice = 
				sellOrders.stream()
				.mapToDouble(sellOrder -> sellOrder.price)
				.average()
				.getAsDouble();
		
		assertEquals("", numberOfTraderActions.intValue(), sellOrders.size() + buyOrders.size());

		assertEquals("", 5, actualAverageBuyPrice, 0.1);		
			
		assertEquals("", 5, actualAverageSellPrice, 0.1);		

	}

}
