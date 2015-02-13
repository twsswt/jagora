package uk.ac.glasgow.jagora.trader.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.impl.AbstractBuyOrder;
import uk.ac.glasgow.jagora.impl.AbstractSellOrder;

public abstract class SafeAbstractTrader extends AbstractTrader {

	protected final Collection<AbstractBuyOrder> openBuyOrders;
	protected final Collection<AbstractSellOrder> openSellOrders;

	public SafeAbstractTrader(String name, Double cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
		this.openBuyOrders = new ArrayList<AbstractBuyOrder>();
		this.openSellOrders = new ArrayList<AbstractSellOrder>();
	}
	
	protected void placeSafeBuyOrder(StockExchangeTraderView traderView, AbstractBuyOrder limitBuyOrder) {
		if (limitBuyOrder.getPrice() * limitBuyOrder.getRemainingQuantity() <= getAvailableCash()){
			traderView.placeBuyOrder(limitBuyOrder);
			openBuyOrders.add(limitBuyOrder);
		}
	}
	
	protected void placeSafeSellOrder(StockExchangeTraderView traderView, AbstractSellOrder sellOrder) {
		if (sellOrder.getRemainingQuantity() <= getAvailableQuantity(sellOrder.stock)){
			traderView.placeSellOrder(sellOrder);
			openSellOrders.add(sellOrder);
		}
	}
	
	protected void cancelSafeSellOrder(
			StockExchangeTraderView traderMarketView, AbstractSellOrder sellOrder) {

		traderMarketView.cancelSellOrder(sellOrder);
		openSellOrders.remove(sellOrder);
	}

	protected void cancelSafeBuyOrder(StockExchangeTraderView traderMarketView,	AbstractBuyOrder randomBuyOrder) {
		traderMarketView.cancelBuyOrder(randomBuyOrder);
		openBuyOrders.remove(randomBuyOrder);
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