package uk.ac.glasgow.jagora.trader.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.test.stub.StubStockExchange;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.trader.impl.RandomTraderBuilder;

public class RandomTraderTest {
	
	private final Integer numberOfTraderActions = 10;
	
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
		BuyOrder seedBuyOrder = new LimitBuyOrder(trader, lemons, 10, 5.0);
		marketForLemons.createTraderStockExchangeView().placeBuyOrder(seedBuyOrder);
		SellOrder seedSellOrder = new LimitSellOrder(trader, lemons, 10, 5.0);
		marketForLemons.createTraderStockExchangeView().placeSellOrder(seedSellOrder);
		
		for (Integer i = 0; i < numberOfTraderActions; i++)
			trader.speak(marketForLemons.createTraderStockExchangeView());
		
		List<BuyOrder> BuyOrders = 
			marketForLemons.getBuyOrders(lemons);		
		
		List<SellOrder> SellOrders = 
				marketForLemons.getSellOrders(lemons);
		
		Double actualAverageBuyPrice = 
			BuyOrders.stream()
			.mapToDouble(buyOrder -> buyOrder.getPrice())
			.average()
			.getAsDouble();
				
		Double actualAverageSellPrice = 
			SellOrders.stream()
			.mapToDouble(sellOrder -> sellOrder.getPrice())
			.average()
			.getAsDouble();
		
		assertEquals("", numberOfTraderActions.intValue() + 2, SellOrders.size() + BuyOrders.size());

		assertEquals("", 5, actualAverageBuyPrice, 0.1);		
			
		assertEquals("", 5, actualAverageSellPrice, 0.1);		

	}

}
