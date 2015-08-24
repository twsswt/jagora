package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SafeAbstractTrader extends AbstractTrader {

	protected final List<LimitBuyOrder> openBuyOrders;
	protected final List<LimitSellOrder> openSellOrders;

	public SafeAbstractTrader(String name, Long cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
		this.openBuyOrders = new ArrayList<LimitBuyOrder>();
		this.openSellOrders = new ArrayList<LimitSellOrder>();
	}

	protected Boolean placeSafeBuyOrder(StockExchangeLevel1View traderView, LimitBuyOrder limitBuyOrder) {
		//if you have enough money, you can place the order(that's safe)

		if (limitBuyOrder.getLimitPrice() * limitBuyOrder.getRemainingQuantity() <= getAvailableCash()){
			traderView.placeLimitBuyOrder(limitBuyOrder); //put it in the exchange book as order
			openBuyOrders.add(limitBuyOrder);
			return true;
		}
		return false;
	}

	protected Boolean placeSafeSellOrder(StockExchangeLevel1View traderView, LimitSellOrder limitSellOrder) {
		if (limitSellOrder.getRemainingQuantity() <= getAvailableQuantity(limitSellOrder.getStock())){
			openSellOrders.add(limitSellOrder);
			
			traderView.placeLimitSellOrder(limitSellOrder);
			return true;
		}
		return false;
	}
	
	protected void cancelSafeSellOrder(	StockExchangeLevel1View stockExchangeLevel1View, LimitSellOrder limitSellOrder) {
		
		stockExchangeLevel1View.cancelLimitSellOrder(limitSellOrder);			
		
		Integer indexToCancel = null;
		for (int i = 0 ; i < openSellOrders.size(); i++)
			if (openSellOrders.get(i) == limitSellOrder)
				indexToCancel = i;
		if (indexToCancel != null)
			openSellOrders.remove(indexToCancel.intValue());
	}

	protected void cancelSafeBuyOrder(StockExchangeLevel1View stockExchangeLevel1View, LimitBuyOrder limitBuyOrder) {
		
		stockExchangeLevel1View.cancelLimitBuyOrder(limitBuyOrder);
		
		Integer indexToCancel = null;
		for (int i = 0 ; i < openBuyOrders.size(); i++)
			if (openBuyOrders.get(i) == limitBuyOrder)
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
			.mapToLong(buyOrder -> (buyOrder.getLimitPrice() * buyOrder.getRemainingQuantity()))
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