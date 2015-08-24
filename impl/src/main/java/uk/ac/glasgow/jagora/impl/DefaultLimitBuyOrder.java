package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public class DefaultLimitBuyOrder extends AbstractBuyOrder implements LimitBuyOrder {

	public final Long price;
	
	public DefaultLimitBuyOrder(Trader trader, Stock stock, Integer quantity, Long price) {
		super(trader, stock, quantity);
		this.price = price;
	}

	@Override
	public Long getLimitPrice() {
		return price;
	}

	@Override
	public int compareTo(LimitOrder o) {
		return o.getLimitPrice().compareTo(this.getLimitPrice());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((price == null) ? 0 : price.hashCode());
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
		DefaultLimitBuyOrder other = (DefaultLimitBuyOrder) obj;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		return true;
	}
	
	
}
