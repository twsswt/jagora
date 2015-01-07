package uk.ac.gla.jagora;


public class SellOrder extends Order {

	public SellOrder(Trader trader, Stock stock, Integer quantity, Double price) {
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
