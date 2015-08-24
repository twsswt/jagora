package uk.ac.glasgow.jagora.pricer;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.world.TickEvent;

public interface TradePricer<S extends SellOrder, B extends BuyOrder> {
	
	public Long priceTrade (TickEvent<B> buyOrder, TickEvent<S> sellOrder);

}
