package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.OrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;

/**
 * Ordering of notification is randomised to prevent early registrants from
 * benefiting from earlier notification of trades. Notification is also
 * asynchronous to prevent blocking by registrants.
 * 
 * @author Tim
 *
 */
public class ThreadedTickerTapeObserver extends AbstractStockExchangeObservable {


	
	public void notifyTradeListenerOfTrade(
		TradeExecutionEvent tradeExecutedEvent, TradeListener tradeListener) {
		
		new Thread (){
			@Override
			public void run (){
				tradeListener.tradeExecuted(tradeExecutedEvent);
			}
		}.start();
	}

	@Override
	public void notifyOrderListenerOfOrder(
		OrderEvent orderEvent,	OrderListener orderListener) {
		
		new Thread (){
			@Override
			public void run (){
				orderListener.orderEntered(orderEvent);
			}
		}.start();
		
	}

	@Override
	public void notifyOrderListenerOfCancelledOrder(OrderEvent orderEvent, OrderListener orderListener) {
		new Thread (){
			@Override
			public void run (){
				orderListener.orderCancelled(orderEvent);
			}
		}.start();
	}
}
