package uk.ac.gla.jagora;

import static java.lang.String.format;

public abstract class Trade {

	public final Stock stock;
	public final Integer quantity;
	public final Double price;

	public Trade(Stock stock, Integer quantity, Double price) {
		this.stock = stock;
		this.quantity = quantity;
		this.price = price;
	}
	
	@Override
	public String toString (){
		return format("%s:%d:$%.2f", stock, quantity, price);
	}

	public abstract ExecutedTrade execute (World world) throws TradeExecutionException;

}