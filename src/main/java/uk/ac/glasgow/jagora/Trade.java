package uk.ac.glasgow.jagora;

import static java.lang.String.format;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

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
	
	public Trader getBuyer (){
		return buyOrder.trader;
	}
	
	public Trader getSeller (){
		return sellOrder.trader;
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
