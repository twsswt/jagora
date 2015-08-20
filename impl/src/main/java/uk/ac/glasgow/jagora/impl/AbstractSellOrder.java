package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.TradeExecutionException;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;

public abstract class AbstractSellOrder extends AbstractOrder implements SellOrder {

	public AbstractSellOrder(Trader trader, Stock stock, Integer quantity) {
		super(trader, stock, quantity);
	}

	@Override
	public void satisfyTrade(TickEvent<Trade> executedTrade) throws TradeExecutionException {
		getTrader().sellStock(executedTrade.event);		
		tradeHistory.add(executedTrade);
	}

	@Override
	public void rollBackTrade(TickEvent<Trade> executedTrade) throws TradeExecutionException {
		if (tradeHistory.remove(executedTrade))
			getTrader().buyStock(executedTrade.event);
	}
	
	@Override
	public int compareTo(SellOrder order) {
		return this.getPrice().compareTo(order.getPrice());
	}



}