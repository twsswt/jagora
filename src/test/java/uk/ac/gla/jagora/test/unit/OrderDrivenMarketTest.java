package uk.ac.gla.jagora.test.unit;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.Trade;
import uk.ac.gla.jagora.orderdriven.OrderDrivenStockExchange;
import uk.ac.gla.jagora.orderdriven.impl.OrderDrivenStockExchangeImpl;
import uk.ac.gla.jagora.test.stub.StubTrader;
import uk.ac.gla.jagora.test.stub.StubTraderBuilder;
import uk.ac.gla.jagora.world.SimpleSerialWorld;

public class OrderDrivenMarketTest {

	private Stock apples = new Stock("apples");
	private Stock oranges = new Stock("oranges");
	
	private StubTrader alice = 
			new StubTraderBuilder("alice", 10000.00)
			.addStock(apples, 100)
			.addStock(oranges, 2000)
			.build();
	
	private StubTrader bob   = 
		new StubTraderBuilder("bob", 500.00)
			.addStock(apples, 200)
			.addStock(oranges,400)
			.build();
	
	private OrderDrivenStockExchange orderDrivenStockExchange;

	@Before
	public void setUp() throws Exception {
		orderDrivenStockExchange = new OrderDrivenStockExchangeImpl(new SimpleSerialWorld());
	}

	@Test
	public void test() {
		
		SellOrder sellOrder1 = new SellOrder(bob, apples, 50, 55.0);
		bob.supplyOrder(sellOrder1);
		bob.speak(orderDrivenStockExchange.createTraderMarketView());

		BuyOrder buyOrder1 = new BuyOrder(alice, apples, 25, 45.0);
		alice.supplyOrder(buyOrder1);
		alice.speak(orderDrivenStockExchange.createTraderMarketView());

		orderDrivenStockExchange.doClearing();
				
		assertEquals("", 500.0, bob.getCash(), 0.0);
		
		SellOrder sellOrder2 = new SellOrder(bob, apples, 10, 55.9);
		bob.supplyOrder(sellOrder2);
		bob.speak(orderDrivenStockExchange.createTraderMarketView());
		
		BuyOrder buyOrder2 = new BuyOrder(alice, apples, 60, 56.0);
		alice.supplyOrder(buyOrder2);
		alice.speak(orderDrivenStockExchange.createTraderMarketView());
		
		orderDrivenStockExchange.doClearing();
		
		//sellOrder 1 and 2, and buyOrder 2 should now be fully executed.
		
		Double trade1Cost = 50 * 55.0 + 10 * 55.9;
		
		assertEquals("", 500.0 + trade1Cost, bob.getCash(), 0.0);
		assertEquals("", 10000.0 - trade1Cost, alice.getCash(), 0.0);
		
		SellOrder sellOrder3 = new SellOrder(alice, oranges, 20, 26.5);
		alice.supplyOrder(sellOrder3);
		alice.speak(orderDrivenStockExchange.createTraderMarketView());
		
		SellOrder sellOrder4 = new SellOrder(alice, oranges, 20, 25.0);
		alice.supplyOrder(sellOrder4);
		alice.speak(orderDrivenStockExchange.createTraderMarketView());

		
		BuyOrder buyOrder3 = new BuyOrder(bob, oranges, 30, 27.0);
		bob.supplyOrder(buyOrder3);
		bob.speak(orderDrivenStockExchange.createTraderMarketView());
		
		orderDrivenStockExchange.doClearing();
		
		//Sell order 3, buy order 3 and partially sell order 4 should be executed.
		
		Double trade2Cost = 20 * 25 + 10 * 26.5;

		assertEquals("", 500.0 + trade1Cost - trade2Cost, bob.getCash(), 0.0);
		assertEquals("", 10000.0 - trade1Cost + trade2Cost, alice.getCash(), 0.0);

		List<ExecutedTrade> tradeHistory = orderDrivenStockExchange.getTradeHistory(oranges);
		assertEquals ("", 2, tradeHistory.size());
		
		Trade firstOrangeTrade = tradeHistory.get(0).trade;
		assertEquals("", 25.0, firstOrangeTrade.price, 0.0);
		assertEquals("", 20, firstOrangeTrade.quantity+0);
		
		Trade secondOrangeTrade = tradeHistory.get(1).trade;
		assertEquals("", 26.5, secondOrangeTrade.price, 0.0);
		assertEquals("", 10, secondOrangeTrade.quantity+0);

		List<SellOrder> orangeSellOrders = 
			orderDrivenStockExchange.createTraderMarketView().getOpenSellOrders(oranges);
		
		List<BuyOrder> orangeBuyOrders = 
			orderDrivenStockExchange.createTraderMarketView().getOpenBuyOrders(oranges);
		
		assertEquals("", 1, orangeSellOrders.size());
		assertEquals("", 0, orangeBuyOrders.size());
		
		assertEquals("", 10, orangeSellOrders.get(0).getRemainingQuantity().intValue());
		
		//fail("Not yet implemented");
	}

}
