package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;

public class StdOutTradeListener implements TradeListener {

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		System.out.println(tradeExecutionEvent);
	}

}
