package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;

import java.util.HashMap;

/**
 * Provides a trader with a simple 'strategy' for placing a
 * fixed buy order on the market when asked to speak.
 * 
 * @author tws
 */
public class BuyOnlyTrader extends SafeAbstractTrader implements Level1Trader{

	private Stock stock;
	private Integer quantity;
	private Long price;

	public BuyOnlyTrader(String name, Long cash, Stock stock, Long price, Integer quantity) {
		super(name, cash, new HashMap<Stock,Integer>());
		this.stock = stock;
		this.price = price;
		this.quantity = quantity;
		
	}

	@Override
	public void speak(StockExchangeLevel1View traderView) {
		DefaultLimitBuyOrder defaultLimitBuyOrder = new DefaultLimitBuyOrder(this, stock, quantity, price);
		this.placeSafeBuyOrder(traderView, defaultLimitBuyOrder);
	}
}
