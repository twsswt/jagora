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
import uk.ac.glasgow.jagora.impl.OrderBook;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.test.stub.ManualTickWorld;
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
		
		List<SellOrder> sellOrders =
			Arrays.asList(
				createSellOrder(alice, lemons, 500,  50.00, 4l), 
				createSellOrder(bob,   lemons,  15, 110.00, 1l),
				createSellOrder(alice, lemons,  10, 110.00, 2l),
				createSellOrder(alice, lemons,   5, 200.00, 3l),
				createSellOrder(bob,   lemons,  10, 250.00, 0l)
			);
		
		List<SellOrder> randomisedSellOrders =
			new ArrayList<SellOrder>(sellOrders);
		
		Collections.shuffle(randomisedSellOrders);
		
		for (SellOrder sellOrder : randomisedSellOrders)
			sellBook.recordOrder(sellOrder);
		
		Integer tradeTick = sellOrders.size();

		for (SellOrder expected : sellOrders){
			SellOrder actual = sellBook.getBestOrder();
			assertEquals(expected,actual);
			
			Trade satisfyingTrade = new Trade(lemons, expected.initialQuantity,  expected.price, actual, null);
			
			manualTickWorld.setTickForEvent(Long.valueOf(tradeTick++), satisfyingTrade);
			actual.satisfyTrade(manualTickWorld.getTick(satisfyingTrade));
		}		
	}

	private SellOrder createSellOrder(AbstractTrader trader, Stock stock, Integer quantity, Double price, Long tick) {
		SellOrder sellOrder = new SellOrder(trader, stock, quantity, price);
		manualTickWorld.setTickForEvent(tick, sellOrder);
		return sellOrder;
	}
	
	@Test
	public void testBuyOrderBook() throws Exception {
		
		List<BuyOrder> buyOrders =
			Arrays.asList(
				createBuyOrder(alice, lemons, 500, 900.00, 4l),
				createBuyOrder(alice, lemons,  10, 220.00, 2l),
				createBuyOrder(alice, lemons,  15, 220.00, 3l),
				createBuyOrder(alice, lemons,   5, 150.00, 0l),
				createBuyOrder(bob,   lemons,  10, 110.00, 1l)
			);

		
		List<BuyOrder> randomisedbuyOrders =
			new ArrayList<BuyOrder>(buyOrders);
			
		Collections.shuffle(randomisedbuyOrders);
			
		for (BuyOrder buyOrder : randomisedbuyOrders){
			buyBook.recordOrder(buyOrder);
		}
		
		Integer tradeTick = buyOrders.size();
		
		for (BuyOrder expected : buyOrders){
			BuyOrder actual = buyBook.getBestOrder();
			assertEquals(expected,actual);
			Trade satisfyingTrade =
				new Trade(lemons, expected.initialQuantity,  expected.price, null, actual);	
			
			manualTickWorld.setTickForEvent(Long.valueOf(tradeTick++), satisfyingTrade);

			actual.satisfyTrade(manualTickWorld.getTick(satisfyingTrade));

		}			
	}
	
	private BuyOrder createBuyOrder(
		AbstractTrader trader, Stock stock, Integer quantity, Double price, Long tick) {
		
		BuyOrder buyOrder = new BuyOrder(trader, stock, quantity, price);
		manualTickWorld.setTickForEvent(tick, buyOrder);
		return buyOrder;
	}

}