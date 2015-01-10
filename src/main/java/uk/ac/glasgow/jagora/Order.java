package uk.ac.glasgow.jagora;

import java.util.ArrayList;
import java.util.List;

public abstract class Order implements Comparable<Order> {
	
	public final Trader trader;
	public final Stock stock;
		
	public final Double price;
	
	public final Integer initialQuantity;
	
	protected final List<ExecutedTrade> tradeHistory;
	
	public Order(Trader trader, Stock stock, Integer quantity, Double price) {
		this.trader = trader;
		this.stock = stock;
		this.initialQuantity = quantity;
		this.price = price;
		this.tradeHistory = new ArrayList<ExecutedTrade>();
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
	
	public abstract void satisfyTrade (ExecutedTrade trade) throws TradeExecutionException;
	
	public abstract void rollBackTrade (ExecutedTrade trade) throws TradeExecutionException;
}
