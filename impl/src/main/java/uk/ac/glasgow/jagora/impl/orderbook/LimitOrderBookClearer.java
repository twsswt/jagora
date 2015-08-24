package uk.ac.glasgow.jagora.impl.orderbook;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.pricer.TradePricer;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class LimitOrderBookClearer extends AbstractOrderBookClearer<LimitSellOrder,LimitBuyOrder>{

	public LimitOrderBookClearer(
		OrderBook<LimitSellOrder> sellOrderBook, 
		OrderBook<LimitBuyOrder> buyOrderBook,
		Stock stock,
		World world,
		TradePricer<LimitSellOrder, LimitBuyOrder> tradePricer) {
		
		super(sellOrderBook, buyOrderBook, stock, world, tradePricer);
	}

	@Override
	public boolean aTradeCanBeExecuted(
		TickEvent<LimitSellOrder> sellOrderEvent, TickEvent<LimitBuyOrder> buyOrderEvent) {
		
		return 
			buyOrderEvent.event.getLimitPrice() >= sellOrderEvent.event.getLimitPrice();
	}

}
