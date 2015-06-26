package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection;
import uk.ac.glasgow.jagora.ticker.OrderListener;

public class FilterOnDirectionOrderListener implements OrderListener {

	private OrderListener downStreamOrderListener;
	private OrderDirection direction;

	public FilterOnDirectionOrderListener(OrderListener downStreamOrderListener, OrderDirection direction) {
		this.downStreamOrderListener = downStreamOrderListener;
		this.direction = direction;
	}

	@Override
	public void orderEntered(OrderEntryEvent orderEntryEvent) {
		if (orderEntryEvent.orderDirection == this.direction)
			downStreamOrderListener.orderEntered(orderEntryEvent);

	}

}
