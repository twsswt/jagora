package uk.ac.gla.jagora.test.stub;

import uk.ac.gla.jagora.TickerTapeListener;
import uk.ac.gla.jagora.TradeExecutionEvent;

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
