package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class LimitBuyOrder extends AbstractBuyOrder {

	public final Long price;
	
	public LimitBuyOrder(Trader trader, Stock stock, Integer quantity, Long price) {
		super(trader, stock, quantity);
		this.price = price;
	}

	@Override
	public Long getPrice() {
		return price;
	}
}
