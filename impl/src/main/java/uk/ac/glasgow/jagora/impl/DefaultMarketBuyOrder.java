package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.MarketBuyOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class DefaultMarketBuyOrder extends AbstractBuyOrder implements MarketBuyOrder {

	public DefaultMarketBuyOrder (Trader trader, Stock stock, Integer quantity){
		super(trader, stock, quantity);
	}

}
