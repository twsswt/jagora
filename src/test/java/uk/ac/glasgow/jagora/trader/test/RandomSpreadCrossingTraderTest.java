package uk.ac.glasgow.jagora.trader.test;

import static java.util.stream.IntStream.range;
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
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.RandomSpreadCrossingTraderBuilder;

public class RandomSpreadCrossingTraderTest {
	
	private final Integer numberOfTraderActions = 2000;

	private final Double seedBuyPrice = 5.0;
	private final Double seedSellPrice = 5.0;
	
	private final Double spread = 1.0;
	
	private Level1Trader trader;
	private Stock lemons;
	
	private StubStockExchange stockExchange;

	@Before
	public void setUp() throws Exception {
		
		stockExchange = new StubStockExchange();
		
		lemons  = new Stock("lemons");
		
		// A trader that can create many small buy and sell orders 
		// without needing to cancel due to lack of liquidity.
		
		trader = new RandomSpreadCrossingTraderBuilder("alice",10000000.0,1)
			.addStock(lemons, 500000)
			.addTradeRange(lemons, 1, 10, spread)
			.build();
		
	}

	@Test
	public void test() {
		//Seed the exchange with initial buys and sells.
		BuyOrder seedBuyOrder = new LimitBuyOrder(trader, lemons, 10, seedBuyPrice);
		stockExchange.createLevel1View().placeBuyOrder(seedBuyOrder);
		SellOrder seedSellOrder = new LimitSellOrder(trader, lemons, 10, seedSellPrice);
		stockExchange.createLevel1View().placeSellOrder(seedSellOrder);
		
		for (Integer i : range(0, numberOfTraderActions).toArray())
			trader.speak(stockExchange.createLevel1View());		
			
		
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
		
		
		
		assertEquals("", numberOfTraderActions.intValue() + 2, sellOrders.size() + buyOrders.size());

		assertEquals("", seedBuyPrice + seedSellPrice - spread, actualAverageBuyPrice + actualAverageSellPrice, 0.1);		
			
	}

}
