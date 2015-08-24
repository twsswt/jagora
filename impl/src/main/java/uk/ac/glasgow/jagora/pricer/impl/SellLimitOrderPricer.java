package uk.ac.glasgow.jagora.pricer.impl;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.pricer.LimitOrderTradePricer;
import uk.ac.glasgow.jagora.world.TickEvent;


public class SellLimitOrderPricer implements LimitOrderTradePricer {

	@Override
	public Long priceTrade(
		TickEvent<LimitBuyOrder> highestBid, TickEvent<LimitSellOrder> lowestSell) {
		return lowestSell.event.getLimitPrice();
	}

}
