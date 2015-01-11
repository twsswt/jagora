package uk.ac.glasgow.jagora;

import static java.lang.String.format;

public class Trade {

	public final Stock stock;
	public final Integer quantity;
	public final Double price;
	
	private final SellOrder sellOrder;
	private final BuyOrder buyOrder;
	
	public Trade(Stock stock, Integer quantity, Double price, SellOrder sellOrder, BuyOrder buyOrder) {
		this.stock = stock;
		this.quantity = quantity;
		this.price = price;
		this.sellOrder = sellOrder;
		this.buyOrder = buyOrder;
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
		return format("%s:%d:$%.2f", stock, quantity, price);
	} 
}
