package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.TradeExecutionException;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public abstract class AbstractOrder implements Order {
	
	private final Trader trader;
	private final Stock stock;
			
	private final Integer initialQuantity;
	
	protected final List<TickEvent<Trade>> tradeHistory = new ArrayList<TickEvent<Trade>>();
	
	public AbstractOrder(Trader trader, Stock stock, Integer quantity) {
		this.trader = trader;
		this.stock = stock;
		this.initialQuantity = quantity;
	}

	@Override
	public Integer getInitialQuantity() {
		return initialQuantity;
	}

	@Override
	public Trader getTrader (){
		return trader;
	}
	
	@Override
	public Stock getStock (){
		return stock;
	}
	
	@Override
	public Integer getRemainingQuantity (){
		Integer tradeQuantity = 
			tradeHistory.stream()
					.mapToInt(executedTrade -> executedTrade.event.getQuantity())
					.sum();
		
		return initialQuantity - tradeQuantity;
	}

	@Override
	public String toString (){
		return format("[trader=%s, stock=%s, quantity=%d, price=%d]", 
			trader.getName(), stock.name, getRemainingQuantity(), getPrice());
	}
	
	@Override
	public Boolean isFilled (){
		return this.getRemainingQuantity() <= 0; 
	}
	
	@Override
	public abstract void satisfyTrade (TickEvent<Trade> trade) throws TradeExecutionException;
	
	@Override
	public abstract void rollBackTrade (TickEvent<Trade> trade) throws TradeExecutionException;
	
	@Override
	public abstract Long getPrice();

	@Override
	public int hashCode() {
		Long price = getPrice();
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		Long price = getPrice();
		Trader trader = getTrader();
		Stock stock = getStock();
		Integer initialQuantity = getInitialQuantity();
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (price == null) {
			if (other.getPrice() != null)
				return false;
		} else if (!price.equals(other.getPrice()))
			return false;
		if (!initialQuantity.equals(other.getInitialQuantity()))
			return false;
		if (!stock.equals(other.getStock()))
			return false;
		if (!trader.equals(other.getTrader()))
			return false;
		return true;
	}
}
