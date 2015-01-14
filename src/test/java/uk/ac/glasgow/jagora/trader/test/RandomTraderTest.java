package uk.ac.glasgow.jagora.trader.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.test.stub.StubStockExchange;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.RandomTraderBuilder;

public class RandomTraderTest {
	
	private final Integer numberOfTraderActions = 10000;
	
	private Trader trader;
	private Stock lemons;
	
	private StubStockExchange marketForLemons;

	@Before
	public void setUp() throws Exception {
		
		marketForLemons = new StubStockExchange();
		
		lemons  = new Stock("lemons");
		
		// A trader that can create many small buy and sell orders 
		// without needing to cancel due to lack of liquidity.
		trader = new RandomTraderBuilder("alice",10000000.0,1)
			.addStock(lemons, 500000)
			.addTradeRange(lemons, -.1, +.1, 1, 100)
			.build();
		
	}

	@Test
	public void test() {
		//Seed the exchange with initial buys and sells.
		BuyOrder seedBuyOrder = new BuyOrder(trader, lemons, 10, 5.0);
		marketForLemons.createTraderStockExchangeView().placeBuyOrder(seedBuyOrder);
		SellOrder seedSellOrder = new SellOrder(trader, lemons, 10, 5.0);
		marketForLemons.createTraderStockExchangeView().placeSellOrder(seedSellOrder);
		
		for (Integer i = 0; i < numberOfTraderActions; i++)
			trader.speak(marketForLemons.createTraderStockExchangeView());
		
		List<BuyOrder> buyOrders = 
			marketForLemons.getBuyOrders(lemons);		
		
		List<SellOrder> sellOrders = 
				marketForLemons.getSellOrders(lemons);
		
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
		
		assertEquals("", numberOfTraderActions.intValue() + 2, sellOrders.size() + buyOrders.size());

		assertEquals("", 5, actualAverageBuyPrice, 0.1);		
			
		assertEquals("", 5, actualAverageSellPrice, 0.1);		

	}

}
