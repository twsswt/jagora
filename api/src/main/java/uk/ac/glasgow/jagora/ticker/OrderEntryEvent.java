package uk.ac.glasgow.jagora.ticker;

import static java.lang.String.format;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class OrderEntryEvent {
	
	public enum OrderDirection {BUY, SELL}
	
	public final Long tick;
	public final Trader trader;
	public final Stock stock;
	public final Integer quantity;
	public final Long price;
	public final OrderDirection orderDirection;
	
	public OrderEntryEvent(Long tick, Trader trader, Stock stock, Integer quantity, Long price, OrderDirection orderDirection) {
		this.tick = tick;
		this.trader = trader;
		this.stock = stock;
		this.quantity = quantity;
		this.price = price;
		this.orderDirection = orderDirection;
	}
	
	@Override
	public String toString (){
		String template = "[tick=%d,trader=%s,stock=%s,direction=%s,quantity=%d,price=%d]";
		return format(template, tick, trader, stock, orderDirection, quantity, price);
	}
}
