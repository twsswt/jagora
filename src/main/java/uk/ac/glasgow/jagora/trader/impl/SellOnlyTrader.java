package uk.ac.glasgow.jagora.trader.impl;

import java.util.HashMap;
import java.util.Map;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;

public class SellOnlyTrader extends SafeAbstractTrader {

	private Stock stock;
	private Integer sellQuantity;
	private Double price;

	public SellOnlyTrader(String name, Integer quantity, Stock stock, Double price, Integer sellQuantity) {
		super(name, 0.0, createStockMap(quantity, stock));
		this.stock = stock;
		this.price = price;
		this.sellQuantity = sellQuantity;
		
	}

	private static Map<Stock, Integer> createStockMap(Integer quantity, Stock stock) {
		Map<Stock,Integer> stockMap =  new HashMap<Stock,Integer>();
		stockMap.put(stock, quantity);
		return stockMap;
	}

	@Override
	public void speak(StockExchangeTraderView traderView) {
		LimitSellOrder limitSellOrder = new LimitSellOrder(this, stock, sellQuantity, price);
		this.placeSafeSellOrder(traderView, limitSellOrder);
	}

}
