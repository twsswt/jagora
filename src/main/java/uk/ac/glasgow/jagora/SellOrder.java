package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;

public abstract class SellOrder extends Order implements Comparable<SellOrder> {

	public SellOrder(Trader trader, Stock stock, Integer quantity) {
		super(trader, stock, quantity);
	}

	@Override
	public void satisfyTrade(TickEvent<Trade> executedTrade) throws TradeExecutionException {
		trader.sellStock(executedTrade.event);		
		tradeHistory.add(executedTrade);
	}

	@Override
	public void rollBackTrade(TickEvent<Trade> executedTrade) throws TradeExecutionException {
		if (tradeHistory.remove(executedTrade))
			trader.buyStock(executedTrade.event);
	}
	
	@Override
	public int compareTo(SellOrder order) {
		return this.getPrice().compareTo(order.getPrice());
	}

	public abstract Double getPrice();

}