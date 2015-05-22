package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

public class StdOutTradeListener implements TradeListener {

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		System.out.println(tradeExecutionEvent);
	}

}
