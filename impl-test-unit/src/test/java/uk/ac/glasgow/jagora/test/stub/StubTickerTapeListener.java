package uk.ac.glasgow.jagora.test.stub;

import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;

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
