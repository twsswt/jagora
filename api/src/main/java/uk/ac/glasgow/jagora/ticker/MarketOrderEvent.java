package uk.ac.glasgow.jagora.ticker;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class MarketOrderEvent extends OrderEvent {

	public MarketOrderEvent(
		Long tick, Trader trader, Stock stock, Integer quantity, 
		OrderDirection orderDirection) {
		super(tick, trader, stock, quantity, orderDirection);
	}

}
