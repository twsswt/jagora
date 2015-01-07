package uk.ac.gla.jagora.trader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.StockExchangeTraderView;

public abstract class SafeAbstractTrader extends AbstractTrader {

	protected final Collection<BuyOrder> openBuyOrders;
	protected final Collection<SellOrder> openSellOrders;

	public SafeAbstractTrader(String name, Double cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
		this.openBuyOrders = new ArrayList<BuyOrder>();
		this.openSellOrders = new ArrayList<SellOrder>();
	}
	
	protected void placeSafeBuyOrder(StockExchangeTraderView traderMarketView, BuyOrder buyOrder) {
		if (buyOrder.price * buyOrder.getRemainingQuantity() < getAvailableCash()){
			traderMarketView.placeBuyOrder(buyOrder);
			openBuyOrders.add(buyOrder);
		}
	}
	
	protected void placeSafeSellOrder(StockExchangeTraderView traderMarketView,	SellOrder sellOrder) {
		if (sellOrder.getRemainingQuantity() < getAvailableQuantity(sellOrder.stock)){
			traderMarketView.placeSellOrder(sellOrder);
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