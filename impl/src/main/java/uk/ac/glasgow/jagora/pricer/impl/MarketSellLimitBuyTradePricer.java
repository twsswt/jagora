package uk.ac.glasgow.jagora.pricer.impl;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.MarketSellOrder;
import uk.ac.glasgow.jagora.pricer.TradePricer;
import uk.ac.glasgow.jagora.world.TickEvent;

public class MarketSellLimitBuyTradePricer implements TradePricer<MarketSellOrder, LimitBuyOrder> {

	@Override
	public Long priceTrade(
		TickEvent<LimitBuyOrder> buyOrder,
		TickEvent<MarketSellOrder> sellOrder) {
		return buyOrder.event.getLimitPrice();
	}

}
