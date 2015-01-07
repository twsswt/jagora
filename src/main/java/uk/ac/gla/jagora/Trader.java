package uk.ac.gla.jagora;

public interface Trader {
	
	public abstract Double getCash();

	public abstract void sellStock(Trade trade) throws TradeExecutionException;

	public abstract void buyStock(Trade trade) throws TradeExecutionException;

	public abstract void speak(StockExchangeTraderView traderMarket);
	
	public Integer getInventory(Stock stock);
}