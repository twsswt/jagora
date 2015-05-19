package uk.ac.glasgow.jagora.experiment;

import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

public class StdOutTickerTapeListener implements TradeListener {

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		System.out.println(tradeExecutionEvent);
	}

}
