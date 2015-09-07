package uk.ac.glasgow.jagora.ticker.impl;

import java.util.HashSet;
import java.util.Set;

import uk.ac.glasgow.jagora.ticker.LimitOrderEvent;
import uk.ac.glasgow.jagora.ticker.MarketOrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;

/**
 * Ordering of notification is randomised to prevent early
 * registrants from benefiting from earlier notification of
 * trades. Notification is also asynchronous to prevent
 * blocking by registrants.
 * 
 * This implementation is rather naive and needs to be
 * properly re-written using executors.
 * 
 * @author Tim
 *
 */
public class ThreadedStockExchangeObservable extends AbstractStockExchangeObservable {
	
	private Set<Thread> threads;
	
	public ThreadedStockExchangeObservable (){
		threads = new HashSet<Thread>();
	}
	
	public void notifyTradeListenerOfTrade(
		TradeExecutionEvent tradeExecutedEvent, TradeListener tradeListener) {
		
		Thread t = new Thread (){
			@Override
			public void run (){
				tradeListener.tradeExecuted(tradeExecutedEvent);
			}
		};
		threads.add(t);
		t.start();

	}

	@Override
	public void notifyOrderListenerOfOrderEvent(
		LimitOrderEvent limitOrderEvent, OrderListener orderListener) {
		
		Thread t = new Thread (){
			@Override
			public void run (){
				orderListener.limitOrderEvent(limitOrderEvent);
			}
		};
		threads.add(t);
		t.start();
		
	}

	@Override
	public void notifyOrderListenerOfMarketOrderEvent(
		MarketOrderEvent marketOrderEvent, OrderListener orderListener) {
		
		Thread t = new Thread (){
			@Override
			public void run (){
				orderListener.marketOrderEntered(marketOrderEvent);
			}
		};
		threads.add(t);
		t.start();
		
	}

	public synchronized void waitForAllNotificationsToBeReceived() {
		
		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		threads = new HashSet<Thread>();
		
	}

}
