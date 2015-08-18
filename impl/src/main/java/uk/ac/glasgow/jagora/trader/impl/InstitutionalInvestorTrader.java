package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.trader.Level1Trader;

import java.util.*;

/**
 * This type of trader implements ScheduledLimitOrder,
 * which means that a couple of orders are first scheduled and will
 * execute if their time is right, otherwise it's just a normal
 * SafeAbstractTrader?
 */
public class InstitutionalInvestorTrader extends SafeAbstractTrader implements Level1Trader {
	
	private PriorityQueue<ScheduledLimitOrder> scheduledOrders;
	
	private Collection<Order> placedOrders = new ArrayList<Order>();
	
	public InstitutionalInvestorTrader(String name, Long cash, Map<Stock, Integer> inventory,
			List<ScheduledLimitOrder> scheduledOrders) {
		super(name, cash, inventory);
		this.scheduledOrders = new PriorityQueue<ScheduledLimitOrder>(scheduledOrders);
	}


	@Override
	public void speak(StockExchangeLevel1View traderMarketView) {
		ScheduledLimitOrder nextScheduledOrder = scheduledOrders.peek();
		
		while (nextScheduledOrder != null && nextScheduledOrder.shouldBeExecuted() ){

			scheduledOrders.poll();

			Order order;
			if (nextScheduledOrder.isBuyOrder())
			 	order = nextScheduledOrder.createBuyOrder(this, getCash());
			else
				order = nextScheduledOrder.createSellOrder(this,traderMarketView);

			if (order instanceof BuyOrder){
				traderMarketView.placeBuyOrder((BuyOrder)order);	
				placedOrders.add(order);

			}else {
				traderMarketView.placeSellOrder((SellOrder) order);
				placedOrders.add(order);
			}
			
			nextScheduledOrder = scheduledOrders.peek();
		}
	}

	@Override
	public Long getDelayDecrease() {
		return 0l;
	}
}
