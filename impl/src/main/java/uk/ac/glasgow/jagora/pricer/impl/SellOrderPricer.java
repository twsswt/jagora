package uk.ac.glasgow.jagora.pricer.impl;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.pricer.TradePricer;
import uk.ac.glasgow.jagora.world.TickEvent;

public class SellOrderPricer implements TradePricer {

	@Override
	public Double priceTrade(
		TickEvent<BuyOrder> highestBid, TickEvent<SellOrder> lowestSell) {
		return lowestSell.event.getPrice();
	}

}