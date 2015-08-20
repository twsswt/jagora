package uk.ac.glasgow.jagora.ticker.impl;

import java.io.PrintStream;

import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

public class OutputStreamTraderListener implements TradeListener {

	private final PrintStream printStream;
	
	public OutputStreamTraderListener(PrintStream printStream) {
		this.printStream = printStream;
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		printStream.println(tradeExecutionEvent);
	}

}
