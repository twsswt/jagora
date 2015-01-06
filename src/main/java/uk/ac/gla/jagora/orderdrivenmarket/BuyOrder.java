package uk.ac.gla.jagora.orderdrivenmarket;

import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.TradeExecutionException;
import uk.ac.gla.jagora.trader.AbstractTrader;



public class BuyOrder extends Order {

	public BuyOrder(AbstractTrader trader, Stock stock, Integer quantity, Double price) {
		super(trader, stock, quantity, price);
	}

	public void satisfyTrade(ExecutedTrade executedTrade) throws TradeExecutionException {
		trader.buyStock(executedTrade.trade);		
		tradeHistory.add(executedTrade);
	}

	public void rollBackTrade(ExecutedTrade executedTrade) throws TradeExecutionException {
		if (tradeHistory.remove(executedTrade))
			trader.sellStock(executedTrade.trade);
	}

	@Override
	public int compareTo(Order order) {
		return order.price.compareTo(this.price);
	}
}
