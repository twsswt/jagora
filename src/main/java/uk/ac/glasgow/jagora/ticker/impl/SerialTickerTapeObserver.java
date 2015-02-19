package uk.ac.glasgow.jagora.ticker.impl;

import java.util.List;

import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.ticker.TickerTapeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.world.TickEvent;

public class SerialTickerTapeObserver extends AbstractTickerTapeObserver {
	
	@Override
	protected void notifyTickerTapeListenerOfTrade(TradeExecutionEvent tradeExecutionEvent, TickerTapeListener tickerTapeListener) {
		tickerTapeListener.tradeExecuted(tradeExecutionEvent);
		
	}

}
