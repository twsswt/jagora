package uk.ac.gla.jagora;


public class BuyOrder extends Order {

	public BuyOrder(Trader trader, Stock stock, Integer quantity, Double price) {
		super(trader, stock, quantity, price);
	}

	@Override
	public void satisfyTrade(ExecutedTrade executedTrade) throws TradeExecutionException {
		trader.buyStock(executedTrade.event);		
		tradeHistory.add(executedTrade);
	}

	@Override
	public void rollBackTrade(ExecutedTrade executedTrade) throws TradeExecutionException {
		if (tradeHistory.remove(executedTrade))
			trader.sellStock(executedTrade.event);
	}

	@Override
	public int compareTo(Order order) {
		return order.price.compareTo(this.price);
	}
}
