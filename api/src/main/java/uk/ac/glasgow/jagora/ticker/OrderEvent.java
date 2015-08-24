package uk.ac.glasgow.jagora.ticker;

import static java.lang.String.format;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class OrderEvent {

	public enum OrderDirection {BUY, SELL}

	public final Long tick;
	public final Trader trader;
	public final Stock stock;
	public final Integer quantity;
	public final OrderDirection orderDirection;


	public OrderEvent(Long tick, Trader trader, Stock stock, Integer quantity, OrderDirection orderDirection) {
		this.tick = tick;
		this.trader = trader;
		this.stock = stock;
		this.quantity = quantity;
		this.orderDirection = orderDirection;
	}

	@Override
	public String toString() {
		String template = "[tick=%d,trader=%s,stock=%s,direction=%s,quantity=%d]";
		return format(template, tick, trader, stock, orderDirection, quantity);
	}

}