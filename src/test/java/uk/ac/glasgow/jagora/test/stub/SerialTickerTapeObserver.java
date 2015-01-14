package uk.ac.glasgow.jagora.test.stub;

import java.util.ArrayList;
import java.util.List;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.ticker.TickerTapeListener;
import uk.ac.glasgow.jagora.ticker.TickerTapeObserver;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.impl.AbstractTickerTapeObserver;
import uk.ac.glasgow.jagora.world.TickEvent;

public class SerialTickerTapeObserver extends AbstractTickerTapeObserver {
	
	@Override
	protected void notifyTickerTapeListenerOfTrade(
		TradeExecutionEvent tradeExecutionEvent, TickerTapeListener tickerTapeListener) {
		tickerTapeListener.tradeExecuted(tradeExecutionEvent);
		
	}

}
