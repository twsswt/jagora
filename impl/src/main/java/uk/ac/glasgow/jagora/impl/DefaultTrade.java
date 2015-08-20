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
	
	private final SellOrder sellOrder;
	private final BuyOrder buyOrder;
	
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
		return format("%s->(%s:%d:%d)->%s", getSeller(), getStock(), getQuantity(), getPrice(), getBuyer());
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
}
