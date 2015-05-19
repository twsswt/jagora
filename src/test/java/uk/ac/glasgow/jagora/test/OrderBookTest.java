package uk.ac.glasgow.jagora.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.impl.DefaultTrade;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.impl.OrderBook;
import uk.ac.glasgow.jagora.test.stub.ManualTickWorld;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.AbstractTrader;

public class OrderBookTest {
	
	private static Stock lemons = new Stock("lemons");
	
	private AbstractTrader alice;
	private AbstractTrader bob;
		
	private ManualTickWorld manualTickWorld;

	private OrderBook<SellOrder> sellBook;
	private OrderBook<BuyOrder> buyBook;
	
	
	@Before
	public void setUp() throws Exception {
	
		alice = new StubTraderBuilder("alice", 1000000.00).addStock(lemons, 10000).build();
		bob   = new StubTraderBuilder("bob", 50000.00).addStock(lemons, 200).build();
		
		manualTickWorld = new ManualTickWorld();
				
		sellBook = new OrderBook<SellOrder>(manualTickWorld);
		buyBook = new OrderBook<BuyOrder>(manualTickWorld);
	}

	@Test
	public void testSellOrderBook() throws Exception{
		
		List<LimitSellOrder> limitSellOrders =
			Arrays.asList(
				createSellOrder(alice, lemons, 500,  50.00, 4l), 
				createSellOrder(bob,   lemons,  15, 110.00, 1l),
				createSellOrder(alice, lemons,  10, 110.00, 2l),
				createSellOrder(alice, lemons,   5, 200.00, 3l),
				createSellOrder(bob,   lemons,  10, 250.00, 0l)
			);
		
		List<LimitSellOrder> randomisedSellOrders =
			new ArrayList<LimitSellOrder>(limitSellOrders);
		
		Collections.shuffle(randomisedSellOrders);
		
		for (LimitSellOrder limitSellOrder : randomisedSellOrders)
			sellBook.recordOrder(limitSellOrder);
		
		Integer tradeTick = limitSellOrders.size();

		for (SellOrder expected : limitSellOrders){
			SellOrder actual = sellBook.getBestOrder().event;
			assertEquals(expected,actual);
			
			Trade satisfyingTrade = new DefaultTrade(lemons, expected.getRemainingQuantity(),  expected.getPrice(), actual, null);
			
			manualTickWorld.setTickForEvent(Long.valueOf(tradeTick++), satisfyingTrade);
			actual.satisfyTrade(manualTickWorld.getTick(satisfyingTrade));
		}		
	}

	private LimitSellOrder createSellOrder(AbstractTrader trader, Stock stock, Integer quantity, Double price, Long tick) {
		LimitSellOrder limitSellOrder = new LimitSellOrder(trader, stock, quantity, price);
		manualTickWorld.setTickForEvent(tick, limitSellOrder);
		return limitSellOrder;
	}
	
	@Test
	public void testBuyOrderBook() throws Exception {
		
		List<LimitBuyOrder> limitBuyOrders =
			Arrays.asList(
				createBuyOrder(alice, lemons, 500, 900.00, 4l),
				createBuyOrder(alice, lemons,  10, 220.00, 2l),
				createBuyOrder(alice, lemons,  15, 220.00, 3l),
				createBuyOrder(alice, lemons,   5, 150.00, 0l),
				createBuyOrder(bob,   lemons,  10, 110.00, 1l)
			);

		
		List<LimitBuyOrder> randomisedbuyOrders =
			new ArrayList<LimitBuyOrder>(limitBuyOrders);
			
		Collections.shuffle(randomisedbuyOrders);
			
		for (BuyOrder limitBuyOrder : randomisedbuyOrders)
			buyBook.recordOrder(limitBuyOrder);
		
		List<BuyOrder> actualBestBuyOrders = new ArrayList<BuyOrder>();
		
		Integer tradeTick = limitBuyOrders.size();
		
		for (BuyOrder expected : limitBuyOrders){
			BuyOrder actual = buyBook.getBestOrder().event;
			actualBestBuyOrders.add(actual);
			
			Trade satisfyingTrade =
				new DefaultTrade(lemons, expected.getRemainingQuantity(),  expected.getPrice(), null, actual);	
			manualTickWorld.setTickForEvent(Long.valueOf(tradeTick++), satisfyingTrade);

			actual.satisfyTrade(manualTickWorld.getTick(satisfyingTrade));
		}
		
		assertEquals(limitBuyOrders, actualBestBuyOrders);
	}
	
	private LimitBuyOrder createBuyOrder(
		AbstractTrader trader, Stock stock, Integer quantity, Double price, Long tick) {
		
		LimitBuyOrder limitBuyOrder = new LimitBuyOrder(trader, stock, quantity, price);
		manualTickWorld.setTickForEvent(tick, limitBuyOrder);
		return limitBuyOrder;
	}

}
