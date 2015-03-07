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
import uk.ac.glasgow.jagora.StockExchangeTraderView;

public class InstitutionalInvestorTrader extends SafeAbstractTrader {
	
	private PriorityQueue<ScheduledLimitBuyOrder> scheduledOrders;	
	
	private Collection<Order> placedOrders = new ArrayList<Order>();
	
	public InstitutionalInvestorTrader(String name, Double cash, Map<Stock, Integer> inventory,
			List<ScheduledLimitBuyOrder> scheduledOrders) {
		super(name, cash, inventory);
		this.scheduledOrders = new PriorityQueue<ScheduledLimitBuyOrder>(scheduledOrders);		
	}

	@Override
	public void speak(StockExchangeTraderView traderMarketView) {
		ScheduledLimitBuyOrder nextScheduledOrder = scheduledOrders.peek();
		
		while (nextScheduledOrder != null && nextScheduledOrder.shouldBeExecuted() ){

			scheduledOrders.poll();
			Order order = nextScheduledOrder.createBuyOrder(this, getCash());
			if (order instanceof BuyOrder){
				traderMarketView.placeBuyOrder((BuyOrder)order);	
				placedOrders.add(order);

			}else 
				traderMarketView.placeSellOrder((SellOrder)order);
			
			nextScheduledOrder = scheduledOrders.peek();
		}
	}
}
