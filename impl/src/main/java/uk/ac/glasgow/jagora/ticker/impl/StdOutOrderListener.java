package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;

public class StdOutOrderListener implements OrderListener {

	@Override
	public void orderEntered(OrderEntryEvent orderEntryEvent) {
		System.out.println(orderEntryEvent);
	}

}
