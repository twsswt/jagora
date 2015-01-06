package uk.ac.gla.jagora.orderdrivenmarket;

import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.Trade;
import uk.ac.gla.jagora.TradeExecutionException;
import uk.ac.gla.jagora.World;

public class OrderTrade extends Trade {

	private final SellOrder sellOrder;
	private final BuyOrder buyOrder;
	
	public OrderTrade(Stock stock, Integer quantity, Double price, SellOrder sellOrder, BuyOrder buyOrder) {
		super(stock, quantity, price);
		this.sellOrder = sellOrder;
		this.buyOrder = buyOrder;
	}
	
	public ExecutedTrade execute (World world) throws TradeExecutionException {
		
		ExecutedTrade executedTrade = new ExecutedTrade(this, world);
		
		sellOrder.satisfyTrade(executedTrade);		
		try {
			buyOrder.satisfyTrade(executedTrade);
		} catch (TradeExecutionException e){
			sellOrder.rollBackTrade(executedTrade);
			throw e;
		}
		
		return executedTrade;
	} 
}
