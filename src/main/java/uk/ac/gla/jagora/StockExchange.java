package uk.ac.gla.jagora;

public interface StockExchange {

	public void doClearing ();

	public StockExchangeTraderView createTraderMarketView();
}
