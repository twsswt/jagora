package uk.ac.glasgow.jagora;

import java.util.ArrayList;
import java.util.List;

import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;

public abstract class Order implements Comparable<Order> {
	
	public final Trader trader;
	public final Stock stock;
		
	public final Double price;
	
	public final Integer initialQuantity;
	
	protected final List<TickEvent<Trade>> tradeHistory;
	
	public Order(Trader trader, Stock stock, Integer quantity, Double price) {
		this.trader = trader;
		this.stock = stock;
		this.initialQuantity = quantity;
		this.price = price;
		this.tradeHistory = new ArrayList<TickEvent<Trade>>();
	}
	
	public Integer getRemainingQuantity (){
		Integer tradeQuantity = 
			tradeHistory.stream().mapToInt(executedTrade -> executedTrade.event.quantity).sum();
		
		return initialQuantity - tradeQuantity;
	}

	@Override
	public String toString (){
		return String.format("%s:%s:%d:$%.2f", trader, stock.name, getRemainingQuantity(), price);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		return true;
	}

	
	public abstract void satisfyTrade (TickEvent<Trade> trade) throws TradeExecutionException;
	
	public abstract void rollBackTrade (TickEvent<Trade> trade) throws TradeExecutionException;
}
