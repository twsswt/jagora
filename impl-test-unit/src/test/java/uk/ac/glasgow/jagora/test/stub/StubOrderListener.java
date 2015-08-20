package uk.ac.glasgow.jagora.test.stub;

import uk.ac.glasgow.jagora.ticker.OrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;

public class StubOrderListener implements OrderListener {
	
	public OrderEvent lastOrderReceived;

	@Override
	public void orderEntered(OrderEvent orderEvent) {
		this.lastOrderReceived = orderEvent;
	}

	@Override
	public void orderCancelled(OrderEvent orderEvent) {

	}
}
