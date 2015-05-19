package uk.ac.glasgow.jagora.pricer.impl;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.pricer.Pricer;
import uk.ac.glasgow.jagora.world.TickEvent;

public class OldestOrderPricer implements Pricer {

	@Override
	public Double priceTrade(TickEvent<BuyOrder> highestBid,TickEvent<SellOrder> lowestSell) {
		if (highestBid.tick < lowestSell.tick)
			return highestBid.event.getPrice();
		else return lowestSell.event.getPrice();
	}

}
