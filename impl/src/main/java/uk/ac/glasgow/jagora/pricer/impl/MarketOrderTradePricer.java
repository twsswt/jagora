package uk.ac.glasgow.jagora.pricer.impl;

import uk.ac.glasgow.jagora.Market;
import uk.ac.glasgow.jagora.impl.MarketBuyOrder;
import uk.ac.glasgow.jagora.impl.MarketSellOrder;
import uk.ac.glasgow.jagora.pricer.TradePricer;
import uk.ac.glasgow.jagora.world.TickEvent;

/**
 * Prices trades when both counter parties have placed
 * market orders. If the bid is older than the sell order
 * then the best bid price on the market is used (since the
 * seller was prepared to accept the best bid price and
 * placed their order later). If the sell is older than the
 * bid order then the best offer price is used. If both
 * orders were placed at the same time then the mid-point
 * between the best bid and offer is used.
 *
 */
public class MarketOrderTradePricer implements
	TradePricer<MarketSellOrder, MarketBuyOrder> {

	private Market market;

	public MarketOrderTradePricer(Market market) {
		this.market = market;
	}

	@Override
	public Long priceTrade(
		TickEvent<MarketBuyOrder> buyOrder,
		TickEvent<MarketSellOrder> sellOrder) {
		
		if (buyOrder.tick < sellOrder.tick)
			return market.getBestOfferPrice();
		else if (sellOrder.tick < buyOrder.tick)
			return market.getBestOfferPrice();
		else
			return (market.getBestBidPrice() + market.getBestOfferPrice()) / 2;
		}
}
