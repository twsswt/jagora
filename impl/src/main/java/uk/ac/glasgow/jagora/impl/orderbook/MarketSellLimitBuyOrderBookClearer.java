package uk.ac.glasgow.jagora.impl.orderbook;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.MarketSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.pricer.impl.MarketSellLimitBuyTradePricer;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class MarketSellLimitBuyOrderBookClearer extends AbstractOrderBookClearer<MarketSellOrder,LimitBuyOrder> {

	public MarketSellLimitBuyOrderBookClearer(
		OrderBook<MarketSellOrder> sellOrderBook,
		OrderBook<LimitBuyOrder> buyOrderBook,
		Stock stock,
		World world) {
		super(sellOrderBook, buyOrderBook, stock, world,
			new MarketSellLimitBuyTradePricer());
	}

	@Override
	public boolean aTradeCanBeExecuted(
		TickEvent<MarketSellOrder> sellOrderEvent,
		TickEvent<LimitBuyOrder> buyOrderEvent) {
		return true;
	}

}
