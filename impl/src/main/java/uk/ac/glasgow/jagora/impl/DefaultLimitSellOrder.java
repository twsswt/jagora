package uk.ac.glasgow.jagora.impl;

import static java.lang.String.format;
import uk.ac.glasgow.jagora.LimitOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;


public class DefaultLimitSellOrder extends AbstractSellOrder implements LimitSellOrder {

	private final Long limitPrice;
	
	public DefaultLimitSellOrder(Trader trader, Stock stock, Integer quantity, Long price) {
		super(trader, stock, quantity);
		this.limitPrice = price;
	}

	@Override
	public Long getLimitPrice() {
		return limitPrice;
	}

	@Override
	public int compareTo(LimitOrder o) {
		return this.getLimitPrice().compareTo(o.getLimitPrice());
	}
	
	@Override
	public String toString (){
		return format("[trader=%s, stock=%s, quantity=%d, limitprice=%d]", 
			getTrader().getName(), getStock().name, getRemainingQuantity(), getLimitPrice());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
			+ ((limitPrice == null) ? 0 : limitPrice.hashCode());
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
		DefaultLimitSellOrder other = (DefaultLimitSellOrder) obj;
		if (limitPrice == null) {
			if (other.limitPrice != null)
				return false;
		} else if (!limitPrice.equals(other.limitPrice))
			return false;
		return true;
	}

}
