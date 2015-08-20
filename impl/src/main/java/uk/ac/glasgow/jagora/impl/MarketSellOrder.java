package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.trader.Trader;

public class MarketSellOrder extends AbstractSellOrder {

	private StockExchangeLevel1View stockExchangeLevel1View;

	public MarketSellOrder (
		Trader trader, Stock stock, Integer quantity, StockExchangeLevel1View stockExchangeLevel1View){
		
		super(trader, stock, quantity);
		this.stockExchangeLevel1View = stockExchangeLevel1View;
	}

	@Override
	public Long getPrice(){
		return stockExchangeLevel1View.getBestBidPrice(this.getStock());
	}

}
