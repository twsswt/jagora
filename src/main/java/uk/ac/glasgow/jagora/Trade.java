package uk.ac.glasgow.jagora;

import static java.lang.String.format;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class Trade {

	public final Stock stock;
	public final Integer quantity;
	public final Double price;
	
	private final SellOrder limitSellOrder;
	private final BuyOrder limitBuyOrder;
	
	public Trade(Stock stock, Integer quantity, Double price, SellOrder limitSellOrder, BuyOrder limitBuyOrder) {
		this.stock = stock;
		this.quantity = quantity;
		this.price = price;
		this.limitSellOrder = limitSellOrder;
		this.limitBuyOrder = limitBuyOrder;
	}
	
	public Trader getBuyer (){
		return limitBuyOrder.trader;
	}
	
	public Trader getSeller (){
		return limitSellOrder.trader;
	}
	
	public TickEvent<Trade> execute (World world) throws TradeExecutionException {
		
		TickEvent<Trade> executedTrade = world.getTick(this);
		
		limitSellOrder.satisfyTrade(executedTrade);		
		try {
			limitBuyOrder.satisfyTrade(executedTrade);
		} catch (TradeExecutionException e){
			limitSellOrder.rollBackTrade(executedTrade);
			throw e;
		}
		
		return executedTrade;
	}

	@Override
	public String toString() {
		return format("%s:%d:$%.2f", stock, quantity, price);
	} 
}
