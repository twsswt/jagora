package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.TickerTapeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

public class SerialTickerTapeObserver extends AbstractTickerTapeObservable {
	
	@Override
	protected void notifyTickerTapeListenerOfTrade(
			TradeExecutionEvent tradeExecutionEvent, TickerTapeListener tickerTapeListener) {
		tickerTapeListener.tradeExecuted(tradeExecutionEvent);
		
	}

}
