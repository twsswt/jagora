package uk.ac.glasgow.jagora.impl.orderbook;

import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.MarketBuyOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.pricer.impl.MarketBuyLimitSellTradePricer;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class MarketBuyLimitSellOrderBookClearer extends AbstractOrderBookClearer<LimitSellOrder,MarketBuyOrder> {

	public MarketBuyLimitSellOrderBookClearer(
		OrderBook<LimitSellOrder> sellOrderBook,
		OrderBook<MarketBuyOrder> buyOrderBook,
		Stock stock,
		World world) {
		super(sellOrderBook, buyOrderBook, stock, world,
			new MarketBuyLimitSellTradePricer());
	}

	@Override
	public boolean aTradeCanBeExecuted(
		TickEvent<LimitSellOrder> sellOrderEvent,
		TickEvent<MarketBuyOrder> buyOrderEvent) {
		return true;
	}

}
