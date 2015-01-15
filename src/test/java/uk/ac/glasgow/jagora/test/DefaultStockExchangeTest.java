package uk.ac.glasgow.jagora.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.impl.ContinuousOrderDrivenMarketFactory;
import uk.ac.glasgow.jagora.impl.DefaultStockExchange;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.test.stub.StubTickerTapeListener;
import uk.ac.glasgow.jagora.test.stub.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.test.stub.StubTrader;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.TickerTapeObserver;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.impl.ThreadedTickerTapeObserver;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.impl.SimpleSerialWorld;

public class DefaultStockExchangeTest {

	private Stock lemons = new Stock("lemons");
	private Stock oranges = new Stock("oranges");
	
	private StubTrader alice;
	
	private StubTrader bob;
		
	private ContinuousOrderDrivenMarketFactory marketFactory;
	private SimpleSerialWorld world;
	

	@Before
	public void setUp() throws Exception {
	
		bob   = 
			new StubTraderBuilder("bob", 500.00)
				.addStock(lemons, 200)
				.addStock(oranges,400)
				.build();
		
		alice = 
			new StubTraderBuilder("alice", 10000.00)
				.addStock(lemons, 100)
				.addStock(oranges, 2000)
				.build();
		
		marketFactory = new ContinuousOrderDrivenMarketFactory();		
		world = new SimpleSerialWorld(1000l);		
	}

	/**
	 * Executes a short order sequence for two agents on two stocks.
	 */
	@Test
	public void testClearing() {
		
		SerialTickerTapeObserver tickerTapeObserver = new SerialTickerTapeObserver ();

		DefaultStockExchange defaultStockExchange = 
			new DefaultStockExchange(world,	tickerTapeObserver,	marketFactory);
		
		SellOrder sellOrder1 = new LimitSellOrder(bob, lemons, 50, 55.0);
		bob.supplyOrder(sellOrder1);
		bob.speak(defaultStockExchange.createTraderStockExchangeView());

		BuyOrder buyOrder1 = new LimitBuyOrder(alice, lemons, 25, 45.0);
		alice.supplyOrder(buyOrder1);
		alice.speak(defaultStockExchange.createTraderStockExchangeView());

		defaultStockExchange.doClearing();
		
		//No satisfying trades at this stage.
		
		assertEquals("", 500.0, bob.getCash(), 0.0);
		
		SellOrder sellOrder2 = new LimitSellOrder(bob, lemons, 10, 55.9);
		bob.supplyOrder(sellOrder2);
		bob.speak(defaultStockExchange.createTraderStockExchangeView());
		
		BuyOrder buyOrder2 = new LimitBuyOrder(alice, lemons, 60, 56.0);
		alice.supplyOrder(buyOrder2);
		alice.speak(defaultStockExchange.createTraderStockExchangeView());
		
		defaultStockExchange.doClearing();
		
		//sellOrder 1 and 2, and buyOrder 2 should now be fully executed.
		
		Double trade1Cost = 50 * 55.0 + 10 * 55.9;
		
		assertEquals("", 500.0 + trade1Cost, bob.getCash(), 0.0);
		assertEquals("", 10000.0 - trade1Cost, alice.getCash(), 0.0);
		
		SellOrder sellOrder3 = new LimitSellOrder(alice, oranges, 20, 26.5);
		alice.supplyOrder(sellOrder3);
		alice.speak(defaultStockExchange.createTraderStockExchangeView());
		
		SellOrder sellOrder4 = new LimitSellOrder(alice, oranges, 20, 25.0);
		alice.supplyOrder(sellOrder4);
		alice.speak(defaultStockExchange.createTraderStockExchangeView());

		
		BuyOrder buyOrder3 = new LimitBuyOrder(bob, oranges, 30, 27.0);
		bob.supplyOrder(buyOrder3);
		bob.speak(defaultStockExchange.createTraderStockExchangeView());
		
		defaultStockExchange.doClearing();
		
		//Sell order 3, buy order 3 and partially sell order 4 should be executed.
		
		Double trade2Cost = 20 * 25 + 10 * 26.5;

		assertEquals("", 500.0 + trade1Cost - trade2Cost, bob.getCash(), 0.0);
		assertEquals("", 10000.0 - trade1Cost + trade2Cost, alice.getCash(), 0.0);

		List<TickEvent<Trade>> tradeHistory = tickerTapeObserver.getTradeHistory(oranges);
		assertEquals ("", 2, tradeHistory.size());
		
		Trade firstOrangeTrade = tradeHistory.get(0).event;
		assertEquals("", 25.0, firstOrangeTrade.price, 0.0);
		assertEquals("", 20, firstOrangeTrade.quantity+0);
		
		Trade secondOrangeTrade = tradeHistory.get(1).event;
		assertEquals("", 26.5, secondOrangeTrade.price, 0.0);
		assertEquals("", 10, secondOrangeTrade.quantity+0);

		List<SellOrder> orangeSellOrders = 
			defaultStockExchange.getSellOrders(oranges);
		
		List<BuyOrder> orangeBuyOrders = 
			defaultStockExchange.getBuyOrders(oranges);
		
		assertEquals("", 1, orangeSellOrders.size());
		assertEquals("", 0, orangeBuyOrders.size());
		
		assertEquals("", 10, orangeSellOrders.get(0).getRemainingQuantity().intValue());
		
		//fail("Not yet implemented");
	}
	
	/**
	 * Checks for the arrival of completed trade events with registered
	 * listeners. Notification is asynchronous on this exchange to prevent
	 * blocking by traders. Therefore we need to wait for the notification to
	 * arrive. The timeout ensures that the test completes if the notification
	 * doesn't arrive within a generous period (indicating a bug).
	 * 
	 * @throws Exception
	 */
	@Test(timeout=20000)
	public void testTickerTapeNotification () throws Exception {
		
		TickerTapeObserver tickerTapeObserver = new ThreadedTickerTapeObserver ();

		DefaultStockExchange defaultStockExchange = 
			new DefaultStockExchange(world,	tickerTapeObserver,	marketFactory);

		
		StubTickerTapeListener stubTickerTapeListener = 
			new StubTickerTapeListener();
		
		defaultStockExchange.addTicketTapeListener(stubTickerTapeListener, lemons);
		
		SellOrder limitSellOrder = new LimitSellOrder(bob, lemons, 10, 10.0);
		bob.supplyOrder(limitSellOrder);
		bob.speak(defaultStockExchange.createTraderStockExchangeView());
		
		BuyOrder limitBuyOrder = new LimitBuyOrder(alice, lemons, 10, 11.0);
		alice.supplyOrder(limitBuyOrder);
		alice.speak(defaultStockExchange.createTraderStockExchangeView());
		
		defaultStockExchange.doClearing();
				
		TradeExecutionEvent lastTradeExecutionEvent = 
			stubTickerTapeListener.getLastTradeExecutionEvent();
		
		while(lastTradeExecutionEvent == null){
			Thread.sleep(100);
			lastTradeExecutionEvent = 
				stubTickerTapeListener.getLastTradeExecutionEvent();
		}
		assertEquals ("", 10.0, lastTradeExecutionEvent.price, 0.0);
		assertEquals ("", 10, lastTradeExecutionEvent.quantity.intValue());
		assertEquals ("", 2, lastTradeExecutionEvent.tick.longValue());

	}

}
