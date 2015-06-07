package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;

public class FilterOnDirectionOrderListener implements OrderListener {

	private OrderListener downStreamOrderListener;
	private Boolean isOffer;

	public FilterOnDirectionOrderListener(OrderListener downStreamOrderListener, Boolean isOffer) {
		this.downStreamOrderListener = downStreamOrderListener;
		this.isOffer = isOffer;
	}

	@Override
	public void orderEntered(OrderEntryEvent orderEntryEvent) {
		if (orderEntryEvent.isOffer == this.isOffer)
			downStreamOrderListener.orderEntered(orderEntryEvent);

	}

}
