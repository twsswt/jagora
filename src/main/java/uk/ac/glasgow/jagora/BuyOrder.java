package uk.ac.glasgow.jagora;

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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		return true;
	}
}
