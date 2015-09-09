package uk.ac.glasgow.jagora.impl.orderbook;

import uk.ac.glasgow.jagora.Market;
import uk.ac.glasgow.jagora.MarketBuyOrder;
import uk.ac.glasgow.jagora.MarketSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.pricer.impl.MarketOrderTradePricer;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class MarketOrderBookClearer extends AbstractOrderBookClearer<MarketSellOrder, MarketBuyOrder>{

	private Market market;
	
	public MarketOrderBookClearer(
		OrderBook<MarketSellOrder> sellOrderBook,
		OrderBook<MarketBuyOrder> buyOrderBook,
		Stock stock,
		World world,
		Market market) {
		super(sellOrderBook, buyOrderBook, stock, world, new MarketOrderTradePricer(market));
		this.market = market;
	}

	@Override
	public boolean aTradeCanBeExecuted(
		TickEvent<MarketSellOrder> sellOrderEvent,
		TickEvent<MarketBuyOrder> buyOrderEvent) {

		boolean bestOfferIsNotNull = market.getLastKnownBestOfferPrice() != null;		
		boolean bestBidIsNotNull = market.getLastKnownBestBidPrice() != null;
				
		if (sellOrderEvent.tick < buyOrderEvent.tick) {
			return bestOfferIsNotNull;
		} else if (sellOrderEvent.tick > buyOrderEvent.tick) {
			return bestBidIsNotNull;
		} else 
			return bestOfferIsNotNull && bestBidIsNotNull;
	}

}
