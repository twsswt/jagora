package uk.ac.glasgow.jagora.trader.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeTraderView;

public abstract class SafeAbstractTrader extends AbstractTrader {

	protected final Collection<BuyOrder> openBuyOrders;
	protected final Collection<SellOrder> openSellOrders;

	public SafeAbstractTrader(String name, Double cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
		this.openBuyOrders = new ArrayList<BuyOrder>();
		this.openSellOrders = new ArrayList<SellOrder>();
	}
	
	protected void placeSafeBuyOrder(StockExchangeTraderView traderView, BuyOrder buyOrder) {
		if (buyOrder.price * buyOrder.getRemainingQuantity() <= getAvailableCash()){
			traderView.placeBuyOrder(buyOrder);
			openBuyOrders.add(buyOrder);
		}
	}
	
	protected void placeSafeSellOrder(StockExchangeTraderView traderView, SellOrder sellOrder) {
		if (sellOrder.getRemainingQuantity() <= getAvailableQuantity(sellOrder.stock)){
			traderView.placeSellOrder(sellOrder);
			openSellOrders.add(sellOrder);
		}
	}
	
	protected void cancelSafeSellOrder(
			StockExchangeTraderView traderMarketView, SellOrder sellOrder) {

		traderMarketView.cancelSellOrder(sellOrder);
		openSellOrders.remove(sellOrder);
	}

	protected void cancelSafeBuyOrder(StockExchangeTraderView traderMarketView,
			BuyOrder randomBuyOrder) {
		traderMarketView.cancelBuyOrder(randomBuyOrder);
		openBuyOrders.remove(randomBuyOrder);
	}

	protected Double getAvailableCash() {
		Double committedCash =
			openBuyOrders.stream()
			.mapToDouble(buyOrder -> (buyOrder.price * buyOrder.getRemainingQuantity()))
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