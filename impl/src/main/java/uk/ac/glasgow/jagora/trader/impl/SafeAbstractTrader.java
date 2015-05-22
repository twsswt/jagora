package uk.ac.glasgow.jagora.trader.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;

public abstract class SafeAbstractTrader extends AbstractTrader {

	protected final Collection<BuyOrder> openBuyOrders;
	protected final Collection<SellOrder> openSellOrders;

	public SafeAbstractTrader(String name, Double cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
		this.openBuyOrders = new ArrayList<BuyOrder>();
		this.openSellOrders = new ArrayList<SellOrder>();
	}
	
	protected void placeSafeBuyOrder(StockExchangeLevel1View traderView, BuyOrder buyOrder) {
		if (buyOrder.getPrice() * buyOrder.getRemainingQuantity() <= getAvailableCash()){
			traderView.placeBuyOrder(buyOrder);
			openBuyOrders.add(buyOrder);
		}
	}
	
	protected void placeSafeSellOrder(StockExchangeLevel1View traderView, SellOrder sellOrder) {
		if (sellOrder.getRemainingQuantity() <= getAvailableQuantity(sellOrder.getStock())){
			openSellOrders.add(sellOrder);
			traderView.placeSellOrder(sellOrder);
		}
	}
	
	protected void cancelSafeSellOrder(
			StockExchangeLevel1View stockExchangeLevel1View, SellOrder sellOrder) {
		stockExchangeLevel1View.cancelSellOrder(sellOrder);
		openSellOrders.remove(sellOrder);
	}

	protected void cancelSafeBuyOrder(StockExchangeLevel1View traderMarketView,	BuyOrder buyOrder) {
		traderMarketView.cancelBuyOrder(buyOrder);
		openBuyOrders.remove(buyOrder);
	}

	protected Double getAvailableCash() {
		Double committedCash =
			openBuyOrders.stream()
			.mapToDouble(buyOrder -> (buyOrder.getPrice() * buyOrder.getRemainingQuantity()))
			.sum();
		
		return getCash() - committedCash;
	}

	protected Integer getAvailableQuantity(Stock stock) {
		
		Integer committedQuantity = 
			openSellOrders.stream()
			.mapToInt(sellOrder -> sellOrder.getRemainingQuantity())
			.sum();
		
		return inventory.getOrDefault(stock, 0) - committedQuantity;
	}
}