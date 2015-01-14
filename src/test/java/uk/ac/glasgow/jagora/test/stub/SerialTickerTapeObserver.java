package uk.ac.glasgow.jagora.test.stub;

import uk.ac.glasgow.jagora.ticker.TickerTapeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.impl.AbstractTickerTapeObserver;

public class SerialTickerTapeObserver extends AbstractTickerTapeObserver {
	
	@Override
	protected void notifyTickerTapeListenerOfTrade(
		TradeExecutionEvent tradeExecutionEvent, TickerTapeListener tickerTapeListener) {
		tickerTapeListener.tradeExecuted(tradeExecutionEvent);
		
	}

}
