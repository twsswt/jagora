package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;

import java.util.HashMap;
import java.util.Map;

public class SellOnlyTrader extends SafeAbstractTrader implements Level1Trader {

	private Stock stock;
	private Integer sellQuantity;
	private Long price;

	public SellOnlyTrader(String name, Integer quantity, Stock stock, Long price, Integer sellQuantity) {
		super(name, 0l, createStockMap(quantity, stock));
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
	public void speak(StockExchangeLevel1View traderView) {
		LimitSellOrder limitSellOrder = new LimitSellOrder(this, stock, sellQuantity, price);
		this.placeSafeSellOrder(traderView, limitSellOrder);
	}

	@Override
	public Long getDelayDecrease() {
		return 0l;
	}
}
