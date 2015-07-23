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
import uk.ac.glasgow.jagora.pricer.impl.SellOrderPricer;
import uk.ac.glasgow.jagora.test.stub.StubTickerTapeListener;
import uk.ac.glasgow.jagora.test.stub.StubTrader;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.StockExchangeObservable;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
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
			new StubTraderBuilder("bob", 50000l)
				.addStock(lemons, 200)
				.addStock(oranges,400)
				.build();
		
		alice = 
			new StubTraderBuilder("alice", 1000000l)
				.addStock(lemons, 100)
				.addStock(oranges, 2000)
				.build();
		
		marketFactory = new ContinuousOrderDrivenMarketFactory(new SellOrderPricer());		
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
		
		SellOrder sellOrder1 = new LimitSellOrder(bob, lemons, 50, 550l);
		bob.supplyOrder(sellOrder1);
		bob.speak(defaultStockExchange.createLevel1View());

		BuyOrder buyOrder1 = new LimitBuyOrder(alice, lemons, 25, 450l);
		alice.supplyOrder(buyOrder1);
		alice.speak(defaultStockExchange.createLevel1View());

		defaultStockExchange.doClearing();
		
		//No satisfying trades at this stage.
		
		assertEquals("", 50000, bob.getCash().longValue());
		
		SellOrder sellOrder2 = new LimitSellOrder(bob, lemons, 10, 559l);
		bob.supplyOrder(sellOrder2);
		bob.speak(defaultStockExchange.createLevel1View());
		
		BuyOrder buyOrder2 = new LimitBuyOrder(alice, lemons, 60, 560l);
		alice.supplyOrder(buyOrder2);
		alice.speak(defaultStockExchange.createLevel1View());
		
		defaultStockExchange.doClearing();
		
		//sellOrder 1 and 2, and buyOrder 2 should now be fully executed.
		
		Long trade1Cost = 50 * 550l + 10 * 559l;
		
		assertEquals("", 50000l + trade1Cost, bob.getCash().longValue());
		assertEquals("", 1000000l - trade1Cost, alice.getCash().longValue());
		
		SellOrder sellOrder3 = new LimitSellOrder(alice, oranges, 20, 265l);
		alice.supplyOrder(sellOrder3);
		alice.speak(defaultStockExchange.createLevel1View());
		
		SellOrder sellOrder4 = new LimitSellOrder(alice, oranges, 20, 250l);
		alice.supplyOrder(sellOrder4);
		alice.speak(defaultStockExchange.createLevel1View());

		
		BuyOrder buyOrder3 = new LimitBuyOrder(bob, oranges, 30, 270l);
		bob.supplyOrder(buyOrder3);
		bob.speak(defaultStockExchange.createLevel1View());
		
		defaultStockExchange.doClearing();
		
		//Sell order 3, buy order 3 and partially sell order 4 should be executed.
		
		Long trade2Cost = 20 * 250l + 10 * 265l;

		assertEquals("", 50000l + trade1Cost - trade2Cost, bob.getCash().longValue());
		assertEquals("", 1000000 - trade1Cost + trade2Cost, alice.getCash().longValue());

		List<TickEvent<Trade>> tradeHistory = tickerTapeObserver.getTradeHistory(oranges);
		assertEquals ("", 2, tradeHistory.size());
		
		Trade firstOrangeTrade = tradeHistory.get(0).event;
		assertEquals("", 250, firstOrangeTrade.getPrice(), 0.0);
		assertEquals("", 20, firstOrangeTrade.getQuantity()+0);
		
		Trade secondOrangeTrade = tradeHistory.get(1).event;
		assertEquals("", 265, secondOrangeTrade.getPrice(), 0.0);
		assertEquals("", 10, secondOrangeTrade.getQuantity()+0);

		List<TickEvent<SellOrder>> SellOrderHistory = tickerTapeObserver.getSellOrderHistory(oranges);
		assertEquals("",2,SellOrderHistory.size());
		assertEquals("", (Long) 265l,SellOrderHistory.get(0).event.getPrice());
		assertEquals("", (Long) 250l,SellOrderHistory.get(1).event.getPrice());

		List<TickEvent<BuyOrder>> buyOrderHistory = tickerTapeObserver.getBuyOrderHistory(oranges);
		assertEquals("", 1,buyOrderHistory.size());
		assertEquals("", (Long) 270l,buyOrderHistory.get(0).event.getPrice());

		List<SellOrder> orangeSellOrders = 
			defaultStockExchange.getSellOrders(oranges);
		
		List<BuyOrder> orangeBuyOrders = 
			defaultStockExchange.getBuyOrders(oranges);
		
		assertEquals("", 1, orangeSellOrders.size());
		assertEquals("", 0, orangeBuyOrders.size());
		
		assertEquals("", 10, orangeSellOrders.get(0).getRemainingQuantity().intValue());
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

		StockExchangeObservable stockExchangeObservable = new ThreadedTickerTapeObserver ();

		DefaultStockExchange defaultStockExchange = 
			new DefaultStockExchange(world,	stockExchangeObservable, marketFactory);
		
		StubTickerTapeListener stubTickerTapeListener = 
			new StubTickerTapeListener();
		
		defaultStockExchange.createLevel1View().registerTradeListener(stubTickerTapeListener);
		
		SellOrder limitSellOrder = new LimitSellOrder(bob, lemons, 10, 1000l);
		bob.supplyOrder(limitSellOrder);
		bob.speak(defaultStockExchange.createLevel1View());
		
		BuyOrder limitBuyOrder = new LimitBuyOrder(alice, lemons, 10, 1100l);
		alice.supplyOrder(limitBuyOrder);
		alice.speak(defaultStockExchange.createLevel1View());
		
		defaultStockExchange.doClearing();
				
		TradeExecutionEvent lastTradeExecutionEvent = 
			stubTickerTapeListener.getLastTradeExecutionEvent();
		
		while(lastTradeExecutionEvent == null){
			Thread.sleep(100);
			lastTradeExecutionEvent = 
				stubTickerTapeListener.getLastTradeExecutionEvent();
		}
		
		TradeExecutionEvent expected = 
			new TradeExecutionEvent(lemons, alice, bob, 2l, 1000l, 10);
		
		assertEquals ("", expected, lastTradeExecutionEvent);
	}

}
