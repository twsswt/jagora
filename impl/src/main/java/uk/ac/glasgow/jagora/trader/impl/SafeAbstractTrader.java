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
	// no signal if the trade is not executed?
	protected void placeSafeBuyOrder(StockExchangeLevel1View traderView, BuyOrder buyOrder) {
		//if you have enough money, you can place the order(that's safe)

		if (buyOrder.getPrice() * buyOrder.getRemainingQuantity() <= getAvailableCash()){
			traderView.placeBuyOrder(buyOrder); //put it in the exchange book as order
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
		//isn't there an easier way to do this?
		openSellOrders.remove(sellOrder);
	}

	protected void cancelSafeBuyOrder(StockExchangeLevel1View stockExchangeLevel1View, BuyOrder buyOrder) {
		
		stockExchangeLevel1View.cancelBuyOrder(buyOrder);
		
		openBuyOrders.remove(buyOrder);
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