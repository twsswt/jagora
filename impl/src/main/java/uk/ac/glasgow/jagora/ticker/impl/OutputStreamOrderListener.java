package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;

import java.io.PrintStream;

public class OutputStreamOrderListener implements OrderListener {

	private PrintStream printStream;
	
	public OutputStreamOrderListener(PrintStream printStream) {
		this.printStream = printStream;
	}

	@Override
	public void orderEntered(OrderEntryEvent orderEntryEvent) {
		printStream.println(orderEntryEvent);
	}

	@Override
	public void orderCancelled(OrderEntryEvent orderEntryEvent) {
		printStream.println(orderEntryEvent);
	}
}
