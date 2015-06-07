package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;


public class LimitSellOrder extends AbstractSellOrder {

	private final Long price;
	
	public LimitSellOrder(Trader trader, Stock stock, Integer quantity, Long price) {
		super(trader, stock, quantity);
		this.price = price;
	}

	@Override
	public Long getPrice() {
		return price;
	}

}
