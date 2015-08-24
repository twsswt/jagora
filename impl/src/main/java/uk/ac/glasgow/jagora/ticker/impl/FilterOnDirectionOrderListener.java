package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.LimitOrderEvent;
import uk.ac.glasgow.jagora.ticker.MarketOrderEvent;
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
	public void limitOrderEvent(LimitOrderEvent limitOrderEvent) {
		if (limitOrderEvent.orderDirection == this.direction)
			downStreamOrderListener.limitOrderEvent(limitOrderEvent);

	}

	@Override
	public void marketOrderEntered(MarketOrderEvent marketOrderEvent) {
		if (marketOrderEvent.orderDirection == this.direction)
			downStreamOrderListener.marketOrderEntered(marketOrderEvent);		
	}
}
