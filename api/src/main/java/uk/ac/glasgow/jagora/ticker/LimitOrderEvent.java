package uk.ac.glasgow.jagora.ticker;

import static java.lang.String.format;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class LimitOrderEvent extends OrderEvent {
	
	public enum Action {PLACED, CANCELLED}
	
	public final Long price;
	
	public final Action action;
	
	public LimitOrderEvent(
		Long tick, Trader trader,
		Stock stock, Integer quantity,
		OrderDirection orderDirection, Long price, Action action) {
		
		super(tick, trader, stock, quantity, orderDirection);
		
		this.price = price;
		this.action = action;
	}
	
	@Override
	public String toString() {
		String template = "[tick=%d,trader=%s,stock=%s,direction=%s,quantity=%d,price=%d]";
		return format(template, tick, trader, stock, orderDirection, quantity, price);
	}

}
