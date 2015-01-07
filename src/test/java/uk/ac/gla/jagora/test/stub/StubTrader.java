package uk.ac.gla.jagora.test.stub;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.Order;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.StockExchangeTraderView;
import uk.ac.gla.jagora.trader.AbstractTrader;

public class StubTrader extends AbstractTrader{
	
	private final Queue<Order> orders;
	
	public StubTrader(String name, Double cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
		this.orders = new LinkedList<Order>();
	}

	@Override
	public void speak(StockExchangeTraderView market) {
		Order nextOrder = orders.poll();
		if (nextOrder != null)
			if (nextOrder instanceof SellOrder)
				market.registerSellOrder((SellOrder)nextOrder);
			else if (nextOrder instanceof BuyOrder)
				market.registerBuyOrder((BuyOrder)nextOrder);
		
	}
	
	public void supplyOrder (Order order){
		orders.offer(order);
	}
}
