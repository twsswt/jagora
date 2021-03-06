package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;

import java.io.PrintStream;

public class OutputStreamTradeListener implements TradeListener {

	private PrintStream printStream;

	public OutputStreamTradeListener(PrintStream printStream){this.printStream = printStream;}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		printStream.println(tradeExecutionEvent);
	}

}
