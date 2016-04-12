package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides 'safe' mechanisms for a trading that prevent a
 * trader from creating liabilities (through placed limit orders)
 * that couldn't be filled if they were all executed. A safe
 * trader cannot place more buy orders than it has cash to
 * pay for, or more sell orders than it has stock on
 * inventory.
 * 
 * @author tws
 *
 */
public abstract class SafeAbstractTrader extends AbstractTrader {

	protected final List<LimitBuyOrder> openBuyOrders;
	protected final List<LimitSellOrder> openSellOrders;

	public SafeAbstractTrader(String name, Long cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
		this.openBuyOrders = new ArrayList<LimitBuyOrder>();
		this.openSellOrders = new ArrayList<LimitSellOrder>();
	}

	protected Boolean placeSafeBuyOrder(StockExchangeLevel1View traderView, LimitBuyOrder limitBuyOrder) {

		if (limitBuyOrder.getLimitPrice() * limitBuyOrder.getRemainingQuantity() <= getAvailableCash()){
			traderView.placeLimitBuyOrder(limitBuyOrder);
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
	 * @return Available cash after cash committed for open
	 * buy orders is taken into account.
	 */
	protected Long getAvailableCash() {
		Long committedCash =
			openBuyOrders.stream()
			.mapToLong(buyOrder -> (buyOrder.getLimitPrice() * buyOrder.getRemainingQuantity()))
			.sum();
		
		return getCash() - committedCash;
	}

	/**
	 * 
	 * @param stock
	 * @return Available quantity of the specified stock,
	 * accounting to stock committed to open sell orders.
	 */
	protected Integer getAvailableQuantity(Stock stock) {
		Integer committedQuantity = 
			openSellOrders.stream()
			.filter(sellOrder -> stock.equals(sellOrder.getStock()))
			.mapToInt(sellOrder -> sellOrder.getRemainingQuantity())
			.sum();
		
		return inventory.getOrDefault(stock, 0) - committedQuantity;
	}
}