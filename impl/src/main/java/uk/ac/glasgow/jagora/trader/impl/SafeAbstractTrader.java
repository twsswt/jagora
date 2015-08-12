package uk.ac.glasgow.jagora.trader.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;

public abstract class SafeAbstractTrader extends AbstractTrader {

	protected final List<BuyOrder> openBuyOrders;
	protected final List<SellOrder> openSellOrders;

	public SafeAbstractTrader(String name, Long cash, Map<Stock, Integer> inventory) {
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
	
	protected void cancelSafeSellOrder(	StockExchangeLevel1View stockExchangeLevel1View, SellOrder sellOrder) {
		
		stockExchangeLevel1View.cancelSellOrder(sellOrder);			
		
		Integer indexToCancel = null;
		for (int i = 0 ; i < openSellOrders.size(); i++)
			if (openSellOrders.get(i) == sellOrder)
				indexToCancel = i;
		if (indexToCancel != null)
			openSellOrders.remove(indexToCancel.intValue());
	}

	protected void cancelSafeBuyOrder(StockExchangeLevel1View stockExchangeLevel1View, BuyOrder buyOrder) {
		
		stockExchangeLevel1View.cancelBuyOrder(buyOrder);
		
		Integer indexToCancel = null;
		for (int i = 0 ; i < openBuyOrders.size(); i++)
			if (openBuyOrders.get(i) == buyOrder)
				indexToCancel = i;
		
		if (indexToCancel != null)
			openBuyOrders.remove(indexToCancel.intValue());
	}

	protected Long getAvailableCash() {
		Long committedCash =
			openBuyOrders.stream()
			.mapToLong(buyOrder -> (buyOrder.getPrice() * buyOrder.getRemainingQuantity()))
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