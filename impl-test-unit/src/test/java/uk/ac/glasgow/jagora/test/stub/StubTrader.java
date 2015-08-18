package uk.ac.glasgow.jagora.test.stub;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.impl.MarketBuyOrder;
import uk.ac.glasgow.jagora.impl.MarketSellOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.AbstractTrader;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class StubTrader extends AbstractTrader implements Level1Trader {
	
	private final Queue<Order> orders;
	
	public StubTrader(String name, Long cash, Map<Stock, Integer> inventory) {
		super(name, cash, inventory);
		this.orders = new LinkedList<Order>();
	}

	@Override
	public void speak(StockExchangeLevel1View market) {
		Order nextOrder = orders.poll();
		if (nextOrder != null)
			if (nextOrder instanceof LimitSellOrder)
				market.placeSellOrder((LimitSellOrder)nextOrder);
			else if (nextOrder instanceof LimitBuyOrder )
				market.placeBuyOrder((LimitBuyOrder)nextOrder);
			else if (nextOrder instanceof MarketSellOrder)
				market.placeSellOrder((MarketSellOrder)nextOrder);
			else if (nextOrder instanceof MarketBuyOrder)
				market.placeBuyOrder((MarketBuyOrder)nextOrder);
		
	}

	@Override
	public Long getDelayDecrease() {
		return 0l;
	}

	public void supplyOrder (Order order){
		orders.offer(order);
	}
}
