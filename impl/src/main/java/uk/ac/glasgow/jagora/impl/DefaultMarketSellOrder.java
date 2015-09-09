package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.MarketSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class DefaultMarketSellOrder extends AbstractSellOrder implements MarketSellOrder{

	public DefaultMarketSellOrder (
		Trader trader, Stock stock, Integer quantity){
		
		super(trader, stock, quantity);
	}

}
