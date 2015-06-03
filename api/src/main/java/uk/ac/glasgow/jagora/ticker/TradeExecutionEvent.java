package uk.ac.glasgow.jagora.ticker;

import static java.lang.String.format;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class TradeExecutionEvent {
	
	public final Stock stock;
	public final Trader buyer;
	public final Trader seller;
	
	public final Long tick;
	public final Double price;
	public final Integer quantity;
	
	public TradeExecutionEvent(Stock stock, Trader buyer, Trader seller, Long tick, Double price,  Integer quantity) {
		this.stock = stock;
		this.buyer = buyer;
		this.seller = seller;
		this.price = price;
		this.tick = tick;
		this.quantity = quantity;
		
	}

	@Override
	public String toString() {
		return format("[tick=%d, stock=%s, %s->%s, quantity=%d, price=%.2f]", tick, stock, seller, buyer, quantity, price);
	}
}
