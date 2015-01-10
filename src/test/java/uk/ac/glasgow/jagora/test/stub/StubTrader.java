package uk.ac.glasgow.jagora.test.stub;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.trader.AbstractTrader;

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
				market.placeSellOrder((SellOrder)nextOrder);
			else if (nextOrder instanceof BuyOrder)
				market.placeBuyOrder((BuyOrder)nextOrder);
		
	}
	
	public void supplyOrder (Order order){
		orders.offer(order);
	}
}
