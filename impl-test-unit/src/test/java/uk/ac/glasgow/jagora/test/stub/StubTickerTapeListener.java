package uk.ac.glasgow.jagora.test.stub;

import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

public class StubTickerTapeListener implements TradeListener {
	
	private TradeExecutionEvent lastTradeExecutionEvent;

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		this.lastTradeExecutionEvent = tradeExecutionEvent;
	}

	public TradeExecutionEvent getLastTradeExecutionEvent() {
		return lastTradeExecutionEvent;
	}

}
