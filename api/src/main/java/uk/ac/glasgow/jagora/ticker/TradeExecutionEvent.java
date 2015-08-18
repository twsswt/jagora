package uk.ac.glasgow.jagora.ticker;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

import static java.lang.String.format;

public class TradeExecutionEvent {
	
	public final Stock stock;
	public final Trader buyer;
	public final Trader seller;
	
	public final Long tick;
	public final Long price;
	public final Integer quantity;

	public final Boolean isAggressiveSell;
	
	public TradeExecutionEvent(Stock stock, Trader buyer, Trader seller, Long tick, Long price,
							   Integer quantity, Boolean isAggressiveSell) {
		this.stock = stock;
		this.buyer = buyer;
		this.seller = seller;
		this.price = price;
		this.tick = tick;
		this.quantity = quantity;
		this.isAggressiveSell = isAggressiveSell;
		
	}

	@Override
	public String toString() {
		return format("[tick=%d, stock=%s, %s->%s, quantity=%d, price=%d]", tick, stock, seller, buyer, quantity, price);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((buyer == null) ? 0 : buyer.hashCode());
		result = prime * result
			+ ((price == null) ? 0 : price.hashCode());
		result = prime
			* result
			+ ((quantity == null) ? 0 : quantity.hashCode());
		result = prime * result
			+ ((seller == null) ? 0 : seller.hashCode());
		result = prime * result
			+ ((stock == null) ? 0 : stock.hashCode());
		result = prime * result
			+ ((tick == null) ? 0 : tick.hashCode());
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
		TradeExecutionEvent other = (TradeExecutionEvent) obj;
		if (buyer == null) {
			if (other.buyer != null)
				return false;
		} else if (!buyer.equals(other.buyer))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		if (seller == null) {
			if (other.seller != null)
				return false;
		} else if (!seller.equals(other.seller))
			return false;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		if (tick == null) {
			if (other.tick != null)
				return false;
		} else if (!tick.equals(other.tick))
			return false;
		return true;
	}
}
