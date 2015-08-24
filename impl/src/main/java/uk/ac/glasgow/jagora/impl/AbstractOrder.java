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
		return format("[trader=%s, stock=%s, quantity=%d]", 
			trader.getName(), stock.name, getRemainingQuantity());
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
			* result
			+ ((initialQuantity == null) ? 0
				: initialQuantity.hashCode());
		result = prime * result
			+ ((stock == null) ? 0 : stock.hashCode());
		result = prime * result
			+ ((trader == null) ? 0 : trader.hashCode());
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
		AbstractOrder other = (AbstractOrder) obj;
		if (initialQuantity == null) {
			if (other.initialQuantity != null)
				return false;
		} else if (!initialQuantity
			.equals(other.initialQuantity))
			return false;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		if (trader == null) {
			if (other.trader != null)
				return false;
		} else if (!trader.equals(other.trader))
			return false;
		return true;
	}

}
