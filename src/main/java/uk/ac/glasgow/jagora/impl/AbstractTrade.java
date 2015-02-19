package uk.ac.glasgow.jagora.impl;

import static java.lang.String.format;
import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.TradeExecutionException;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class AbstractTrade implements Trade {

	private final Stock stock;
	private final Integer quantity;
	private final Double price;
	
	private final SellOrder sellOrder;
	private final BuyOrder buyOrder;
	
	public AbstractTrade(Stock stock, Integer quantity, Double price, SellOrder sellOrder, BuyOrder buyOrder) {
		this.stock = stock;
		this.quantity = quantity;
		this.price = price;
		this.sellOrder = sellOrder;
		this.buyOrder = buyOrder;
	}
	
	public Trader getBuyer (){
		return buyOrder.getTrader();
	}
	
	public Trader getSeller (){
		return sellOrder.getTrader();
	}
	
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
		return format("%s:%d:$%.2f", getStock(), getQuantity(), getPrice());
	}

	public Stock getStock() {
		return stock;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public Double getPrice() {
		return price;
	} 
}
