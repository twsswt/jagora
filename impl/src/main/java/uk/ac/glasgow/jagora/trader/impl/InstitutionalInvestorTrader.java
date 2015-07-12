package uk.ac.glasgow.jagora.trader.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.trader.Level1Trader;

/**
 * This type of trader implements ScheduledLimitBuyOrder,
 * which means that a couple of orders are first scheduled and will
 * execute if their time is right, otherwise it's just a normal
 * SafeAbstractTrader?
 */
public class InstitutionalInvestorTrader extends SafeAbstractTrader implements Level1Trader {
	
	private PriorityQueue<ScheduledLimitBuyOrder> scheduledOrders;	
	
	private Collection<Order> placedOrders = new ArrayList<Order>();
	
	public InstitutionalInvestorTrader(String name, Long cash, Map<Stock, Integer> inventory,
			List<ScheduledLimitBuyOrder> scheduledOrders) {
		super(name, cash, inventory);
		this.scheduledOrders = new PriorityQueue<ScheduledLimitBuyOrder>(scheduledOrders);		
	}


	@Override
	public void speak(StockExchangeLevel1View traderMarketView) {
		ScheduledLimitBuyOrder nextScheduledOrder = scheduledOrders.peek();
		
		while (nextScheduledOrder != null && nextScheduledOrder.shouldBeExecuted() ){

			scheduledOrders.poll();
			Order order = nextScheduledOrder.	createBuyOrder(this, getCash());
			//possible bug?? every time you create a buy order?
			if (order instanceof BuyOrder){
				traderMarketView.placeBuyOrder((BuyOrder)order);	
				placedOrders.add(order);

			}else 
				traderMarketView.placeSellOrder((SellOrder)order); //you don't keep them in your book?
			
			nextScheduledOrder = scheduledOrders.peek();
		}
	}

	@Override
	public Long getDelayDecrease() {
		return 0l;
	}
}
