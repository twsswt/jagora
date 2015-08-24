package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.LimitOrderEvent;
import uk.ac.glasgow.jagora.ticker.MarketOrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;

import java.io.PrintStream;

public class OutputStreamOrderListener implements OrderListener {

	private PrintStream printStream;
	
	public OutputStreamOrderListener(PrintStream printStream) {
		this.printStream = printStream;
	}

	@Override
	public void limitOrderEvent(LimitOrderEvent limitOrderEvent) {
		printStream.println(limitOrderEvent);
	}

	@Override
	public void marketOrderEntered(MarketOrderEvent marketOrderEvent) {
		printStream.println(marketOrderEvent);
		
	}
}
