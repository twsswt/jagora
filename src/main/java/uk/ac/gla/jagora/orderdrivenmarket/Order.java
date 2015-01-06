package uk.ac.gla.jagora.orderdrivenmarket;

import java.util.ArrayList;
import java.util.List;

import uk.ac.gla.jagora.AbstractTrader;
import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.TradeExecutionException;

public abstract class Order implements Comparable<Order> {
	
	public final AbstractTrader trader;
	public final Stock stock;
		
	public final Double price;
	
	public final Integer initialQuantity;
	
	protected final List<ExecutedTrade> tradeHistory;
	
	public Order(AbstractTrader trader, Stock stock, Integer quantity, Double price) {
		this.trader = trader;
		this.stock = stock;
		this.initialQuantity = quantity;
		this.price = price;
		this.tradeHistory = new ArrayList<ExecutedTrade>();
	}
	
	public Integer getRemainingQuantity (){
		Integer tradeQuantity = 
			tradeHistory.stream().mapToInt(executedTrade -> executedTrade.trade.quantity).sum();
		
		return initialQuantity - tradeQuantity;
	}
	
	@Override
	public String toString (){
		return String.format("%s:%s:%d:$%.2f", trader.name, stock.name, getRemainingQuantity(), price);
	}
	
	public abstract void satisfyTrade (ExecutedTrade trade) throws TradeExecutionException;
	
	public abstract void rollBackTrade (ExecutedTrade trade) throws TradeExecutionException;
}
