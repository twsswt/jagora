package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.MarketOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class MarketSellOrder extends AbstractSellOrder implements MarketOrder, SellOrder{

	public MarketSellOrder (
		Trader trader, Stock stock, Integer quantity){
		
		super(trader, stock, quantity);
	}

}
