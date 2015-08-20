package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.OrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection;
import uk.ac.glasgow.jagora.ticker.OrderListener;

public class FilterOnDirectionOrderListener implements OrderListener {

	private OrderListener downStreamOrderListener;
	private OrderDirection direction;

	public FilterOnDirectionOrderListener(OrderListener downStreamOrderListener, OrderDirection direction) {
		this.downStreamOrderListener = downStreamOrderListener;
		this.direction = direction;
	}

	@Override
	public void orderEntered(OrderEvent orderEvent) {
		if (orderEvent.orderDirection == this.direction)
			downStreamOrderListener.orderEntered(orderEvent);

	}

	@Override
	public void orderCancelled(OrderEvent orderEvent) {
		if (orderEvent.orderDirection == this.direction)
			downStreamOrderListener.orderEntered(orderEvent);
	}
}
