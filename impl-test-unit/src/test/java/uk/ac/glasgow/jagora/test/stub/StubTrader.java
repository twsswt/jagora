package uk.ac.glasgow.jagora.test.stub;

import uk.ac.glasgow.jagora.LimitOrder;
import uk.ac.glasgow.jagora.MarketOrder;
import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
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
			if (nextOrder instanceof DefaultLimitSellOrder)
				market.placeLimitSellOrder((DefaultLimitSellOrder)nextOrder);
			else if (nextOrder instanceof DefaultLimitBuyOrder )
				market.placeLimitBuyOrder((DefaultLimitBuyOrder)nextOrder);
		if (nextOrder instanceof MarketSellOrder)
			market.placeMarketSellOrder((MarketSellOrder)nextOrder);
		else if (nextOrder instanceof MarketBuyOrder )
			market.placeMarketBuyOrder((MarketBuyOrder)nextOrder);
	
	}

	public void supplyOrder (LimitOrder order){
		orders.offer(order);
	}

	public void supplyOrder(MarketOrder marketBuyOrder) {
		orders.offer(marketBuyOrder);
	}

}
