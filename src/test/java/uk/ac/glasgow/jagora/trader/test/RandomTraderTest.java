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
		
	private Trader trader;
	private Stock lemons;
	
	private StubStockExchange stockExchange;

	@Before
	public void setUp() throws Exception {
		
		stockExchange = new StubStockExchange();
		
		lemons  = new Stock("lemons");
		
		// A trader that can create many small buy and sell orders 
		// without needing to cancel due to lack of liquidity.
		trader = new RandomTraderBuilder("alice",10000000.0,1)
			.addStock(lemons, 500000)
			.setTradeRange(lemons, 1, 100, -.1, +.1, -.1, +.1)
			.build();
		
	}

	@Test
	public void test() {
		//Seed the exchange with initial buys and sells.
		BuyOrder seedBuyOrder = new LimitBuyOrder(trader, lemons, 10, 5.0);
		stockExchange.createTraderStockExchangeView().placeBuyOrder(seedBuyOrder);
		SellOrder seedSellOrder = new LimitSellOrder(trader, lemons, 10, 5.0);
		stockExchange.createTraderStockExchangeView().placeSellOrder(seedSellOrder);
		
		trader.speak(stockExchange.createTraderStockExchangeView());
		trader.speak(stockExchange.createTraderStockExchangeView());
		
		List<BuyOrder> buyOrders = 
			stockExchange.getBuyOrders(lemons);		
		
		List<SellOrder> sellOrders = 
				stockExchange.getSellOrders(lemons);
		
		Double actualAverageBuyPrice = 
			buyOrders.stream()
			.mapToDouble(buyOrder -> buyOrder.getPrice())
			.average()
			.getAsDouble();
				
		Double actualAverageSellPrice = 
			sellOrders.stream()
			.mapToDouble(sellOrder -> sellOrder.getPrice())
			.average()
			.getAsDouble();
		
		assertEquals("", 2 + 2, sellOrders.size() + buyOrders.size());

		assertEquals("", 5, actualAverageBuyPrice, 0.1);		
			
		assertEquals("", 5, actualAverageSellPrice, 0.1);		

	}

}
