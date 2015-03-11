package uk.ac.glasgow.jagora.trader.impl;

import java.util.Map;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeTraderView;

public class ZIPTrader extends SafeAbstractTrader {

	public ZIPTrader(String name, Double cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void speak(StockExchangeTraderView traderMarketView) {
		// TODO Auto-generated method stub

	}

}
