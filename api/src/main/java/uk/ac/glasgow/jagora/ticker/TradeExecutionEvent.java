package uk.ac.glasgow.jagora.ticker;

import static java.lang.String.format;
import uk.ac.glasgow.jagora.Stock;

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

	@Override
	public String toString() {
		return format("[tick=%d, stock=%s, quantity=%d, price=%.2f]", tick, stock, quantity, price);
	}
}
