package uk.ac.glasgow.jagora.test.stub;

import uk.ac.glasgow.jagora.TickerTapeListener;
import uk.ac.glasgow.jagora.TradeExecutionEvent;

public class StubTickerTapeListener implements TickerTapeListener {
	
	private TradeExecutionEvent lastTradeExecutionEvent;

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		this.lastTradeExecutionEvent = tradeExecutionEvent;
	}

	public TradeExecutionEvent getLastTradeExecutionEvent() {
		return lastTradeExecutionEvent;
	}

}
