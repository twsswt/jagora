package uk.ac.glasgow.jagora.experiment;

import uk.ac.glasgow.jagora.ticker.TickerTapeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

public class StdOutTickerTapeListener implements TickerTapeListener {

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		System.out.println(tradeExecutionEvent);
	}

}
