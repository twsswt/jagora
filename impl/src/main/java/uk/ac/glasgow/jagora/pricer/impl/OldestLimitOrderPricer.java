package uk.ac.glasgow.jagora.pricer.impl;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.pricer.LimitOrderTradePricer;
import uk.ac.glasgow.jagora.world.TickEvent;

public class OldestLimitOrderPricer implements LimitOrderTradePricer {

	@Override
	public Long priceTrade(TickEvent<LimitBuyOrder> highestBid,TickEvent<LimitSellOrder> lowestSell) {
		if (highestBid.tick < lowestSell.tick)
			return highestBid.event.getLimitPrice();
		else return lowestSell.event.getLimitPrice();
	}

}
