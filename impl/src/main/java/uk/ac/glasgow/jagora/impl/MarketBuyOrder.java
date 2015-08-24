package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.MarketOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class MarketBuyOrder extends AbstractBuyOrder implements MarketOrder, BuyOrder {

	public MarketBuyOrder (Trader trader, Stock stock, Integer quantity){
		super(trader, stock, quantity);
	}

}
