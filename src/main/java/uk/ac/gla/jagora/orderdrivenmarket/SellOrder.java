package uk.ac.gla.jagora.orderdrivenmarket;

import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.TradeExecutionException;
import uk.ac.gla.jagora.trader.AbstractTrader;


public class SellOrder extends Order {

	public SellOrder(AbstractTrader trader, Stock stock, Integer quantity, Double price) {
		super(trader, stock, quantity, price);
	}

	@Override
	public void satisfyTrade(ExecutedTrade executedTrade) throws TradeExecutionException {
		trader.sellStock(executedTrade.trade);		
		tradeHistory.add(executedTrade);
	}

	@Override
	public void rollBackTrade(ExecutedTrade executedTrade) throws TradeExecutionException {
		if (tradeHistory.remove(executedTrade))
			trader.buyStock(executedTrade.trade);
	}

	@Override
	public int compareTo(Order order) {
		return this.price.compareTo(order.price);
	}

}
