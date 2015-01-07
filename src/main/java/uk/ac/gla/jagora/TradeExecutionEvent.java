package uk.ac.gla.jagora;

public class TradeExecutionEvent {
	public final Stock stock;
	public final Long tick;
	public final Double price;
	public final Integer quantity;
	
	public TradeExecutionEvent(Stock stock, Long tick, Double price,  Integer quantity) {
		this.stock = stock;
		this.price = price;
		this.tick = tick;
		this.quantity = quantity;
	}
}
