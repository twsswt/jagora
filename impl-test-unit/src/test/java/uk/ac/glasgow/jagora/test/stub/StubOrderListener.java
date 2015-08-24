package uk.ac.glasgow.jagora.test.stub;

import uk.ac.glasgow.jagora.ticker.LimitOrderEvent;
import uk.ac.glasgow.jagora.ticker.MarketOrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;

public class StubOrderListener implements OrderListener {
	
	public OrderEvent lastOrderReceived;

	@Override
	public void limitOrderEvent(LimitOrderEvent limitOrderEvent) {
		this.lastOrderReceived = limitOrderEvent;
	}

	@Override
	public void marketOrderEntered(MarketOrderEvent marketOrderEvent) {
		this.lastOrderReceived = marketOrderEvent;
	}
}
