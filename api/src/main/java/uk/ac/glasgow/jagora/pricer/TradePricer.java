package uk.ac.glasgow.jagora.pricer;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.world.TickEvent;

public interface TradePricer {
	
	Long priceTrade(TickEvent<BuyOrder> highestBid, TickEvent<SellOrder> lowestSell);
}
