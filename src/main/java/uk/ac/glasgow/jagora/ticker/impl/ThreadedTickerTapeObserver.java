package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.TickerTapeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

/**
 * Ordering of notification is randomised to prevent early registrants from
 * benefiting from earlier notification of trades. Notification is also
 * asynchronous to prevent blocking by registrants.
 * 
 * @author Tim
 *
 */
public class ThreadedTickerTapeObserver extends AbstractTickerTapeObservable {

	public ThreadedTickerTapeObserver() {
		super();
	}
	
	protected void notifyTickerTapeListenerOfTrade(
		TradeExecutionEvent tradeExecutedEvent, TickerTapeListener tickerTapeListener) {
		
		new Thread (){
			@Override
			public void run (){
				tickerTapeListener.tradeExecuted(tradeExecutedEvent);
			}
		}.start();
	}
}
