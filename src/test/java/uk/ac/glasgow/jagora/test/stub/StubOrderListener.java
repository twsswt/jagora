package uk.ac.glasgow.jagora.test.stub;

import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;

public class StubOrderListener implements OrderListener {
	
	public OrderEntryEvent lastOrderReceived;

	@Override
	public void orderEntered(OrderEntryEvent orderEntryEvent) {
		this.lastOrderReceived = orderEntryEvent;
	}

}
