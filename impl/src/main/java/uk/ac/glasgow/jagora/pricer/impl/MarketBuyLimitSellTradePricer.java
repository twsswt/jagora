package uk.ac.glasgow.jagora.pricer.impl;

import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.MarketBuyOrder;
import uk.ac.glasgow.jagora.pricer.TradePricer;
import uk.ac.glasgow.jagora.world.TickEvent;

public class MarketBuyLimitSellTradePricer implements TradePricer<LimitSellOrder, MarketBuyOrder> {

	@Override
	public Long priceTrade(
		TickEvent<MarketBuyOrder> buyOrder,
		TickEvent<LimitSellOrder> sellOrder) {
		return sellOrder.event.getLimitPrice();
	}

}
