package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.TradeExecutionException;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;

public abstract class AbstractBuyOrder extends AbstractOrder implements BuyOrder {

	public AbstractBuyOrder(Trader trader, Stock stock, Integer quantity) {
		super(trader, stock, quantity);
	}

	@Override
	public void satisfyTrade(TickEvent<Trade> executedTrade) throws TradeExecutionException {
		getTrader().buyStock(executedTrade.event);		
		tradeHistory.add(executedTrade);
	}

	@Override
	public void rollBackTrade(TickEvent<Trade> executedTrade) throws TradeExecutionException {
		if (tradeHistory.remove(executedTrade))
			getTrader().sellStock(executedTrade.event);
	}
	

	@Override
	public int compareTo(BuyOrder order) {
		return order.getPrice().compareTo(this.getPrice());
	}

}