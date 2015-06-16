package uk.ac.glasgow.jagora.ticker.impl;

import java.io.PrintStream;

import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;

public class OutputStreamOrderListener implements OrderListener {

	private PrintStream printStream;
	
	public OutputStreamOrderListener(PrintStream printStream) {
		this.printStream = printStream;
	}

	@Override
	public void orderEntered(OrderEntryEvent orderEntryEvent) {
		printStream.println(orderEntryEvent);
	}

}
