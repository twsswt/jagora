package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SafeAbstractTrader extends AbstractTrader {

	protected final List<BuyOrder> openBuyOrders;
	protected final List<SellOrder> openSellOrders;

	public SafeAbstractTrader(String name, Long cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
		this.openBuyOrders = new ArrayList<BuyOrder>();
		this.openSellOrders = new ArrayList<SellOrder>();
	}

	protected Boolean placeSafeBuyOrder(StockExchangeLevel1View traderView, BuyOrder buyOrder) {
		//if you have enough money, you can place the order(that's safe)

		if (buyOrder.getPrice() * buyOrder.getRemainingQuantity() <= getAvailableCash()){
			traderView.placeBuyOrder(buyOrder); //put it in the exchange book as order
			openBuyOrders.add(buyOrder);
			return true;
		}
		return false;
	}

	protected Boolean placeSafeSellOrder(StockExchangeLevel1View traderView, SellOrder sellOrder) {
		if (sellOrder.getRemainingQuantity() <= getAvailableQuantity(sellOrder.getStock())){
			openSellOrders.add(sellOrder);
			
			traderView.placeSellOrder(sellOrder);
			return true;
		}
		return false;
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

	/**
	 *
	 * @return Available cash after cash committed for openBuyOrders is taken out
	 */
	protected Long getAvailableCash() {
		Long committedCash =
			openBuyOrders.stream()
			.mapToLong(buyOrder -> (buyOrder.getPrice() * buyOrder.getRemainingQuantity()))
			.sum();
		
		return getCash() - committedCash;
	}

	protected Integer getAvailableQuantity(Stock stock) {
		//bug?? just works if you have one stock only?
		Integer committedQuantity = 
			openSellOrders.stream()
			.mapToInt(sellOrder -> sellOrder.getRemainingQuantity())
			.sum();
		
		return inventory.getOrDefault(stock, 0) - committedQuantity;
	}
}