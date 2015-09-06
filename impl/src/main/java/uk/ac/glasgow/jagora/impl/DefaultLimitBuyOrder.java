package uk.ac.glasgow.jagora.impl;

import static java.lang.String.format;
import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class DefaultLimitBuyOrder extends AbstractBuyOrder implements LimitBuyOrder {

	private final Long limitPrice;
	
	public DefaultLimitBuyOrder(Trader trader, Stock stock, Integer quantity, Long price) {
		super(trader, stock, quantity);
		this.limitPrice = price;
	}

	@Override
	public Long getLimitPrice() {
		return limitPrice;
	}

	@Override
	public int compareTo(LimitOrder o) {
		return o.getLimitPrice().compareTo(this.getLimitPrice());
	}


	
	@Override
	public String toString (){
		String template = 
			"DefaultLimitBuyOrder[trader=%s, stock=%s, quantity=%d, price=%d]";
		
		return format(template, 
			getTrader().getName(), 
			getStock().name, 
			getRemainingQuantity(), 
			limitPrice);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
			* result
			+ ((limitPrice == null) ? 0 : limitPrice
				.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultLimitBuyOrder other = (DefaultLimitBuyOrder) obj;
		if (limitPrice == null) {
			if (other.limitPrice != null)
				return false;
		} else if (!limitPrice.equals(other.limitPrice))
			return false;
		return true;
	}
	
	
}
