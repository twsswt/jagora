package uk.ac.glasgow.jagora.ticker.impl;

import java.io.PrintStream;

import uk.ac.glasgow.jagora.ticker.OrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;

public class OutputStreamOrderListener implements OrderListener {

	private PrintStream printStream;
	
	public OutputStreamOrderListener(PrintStream printStream) {
		this.printStream = printStream;
	}

	@Override
	public void orderEntered(OrderEvent orderEvent) {
		printStream.println(orderEvent);
	}

	@Override
	public void orderCancelled(OrderEvent orderEvent) {
		printStream.println(orderEvent);
	}

}
