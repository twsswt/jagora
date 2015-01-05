package uk.ac.gla.jagora;



public class BuyOrder extends Order {

	public BuyOrder(Trader agent, Stock stock, Integer quantity, Double price) {
		super(agent, stock, quantity, price);
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
