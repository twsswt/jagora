package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;
import static java.lang.String.format;

public class DefaultTrade implements Trade {

	private final Stock stock;
	private final Integer quantity;
	private final Long price;
	
	private final Order sellOrder;
	private final Order buyOrder;
	
	public DefaultTrade(Stock stock, Integer quantity, Long price, SellOrder sellOrder, BuyOrder buyOrder) {
		this.stock = stock;
		this.quantity = quantity;
		this.price = price;
		this.sellOrder = sellOrder;
		this.buyOrder = buyOrder;
	}

	@Override
	public Trader getBuyer (){
		return buyOrder.getTrader();
	}
	@Override
	public Trader getSeller (){
		return sellOrder.getTrader();
	}
	@Override
	public TickEvent<Trade> execute (World world) throws TradeExecutionException {
		
		TickEvent<Trade> executedTrade = world.getTick(this);
		
		sellOrder.satisfyTrade(executedTrade);		
		try {
			buyOrder.satisfyTrade(executedTrade);
		} catch (TradeExecutionException e){
			sellOrder.rollBackTrade(executedTrade);
			throw e;
		}
		
		return executedTrade;
	}

	@Override
	public String toString() {
		return format("%s->(%s:%d:%dp)->%s", getSeller(), getStock(), getQuantity(), getPrice(), getBuyer());
	}
	@Override
	public Stock getStock() {
		return stock;
	}
	@Override
	public Integer getQuantity() {
		return quantity;
	}
	@Override
	public Long getPrice() {
		return price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
			* result
			+ ((buyOrder == null) ? 0 : buyOrder.hashCode());
		result = prime * result
			+ ((price == null) ? 0 : price.hashCode());
		result = prime
			* result
			+ ((quantity == null) ? 0 : quantity.hashCode());
		result = prime
			* result
			+ ((sellOrder == null) ? 0 : sellOrder
				.hashCode());
		result = prime * result
			+ ((stock == null) ? 0 : stock.hashCode());
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
		DefaultTrade other = (DefaultTrade) obj;
		if (buyOrder == null) {
			if (other.buyOrder != null)
				return false;
		} else if (!buyOrder.equals(other.buyOrder))
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
		if (sellOrder == null) {
			if (other.sellOrder != null)
				return false;
		} else if (!sellOrder.equals(other.sellOrder))
			return false;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		return true;
	} 
	
	
}
