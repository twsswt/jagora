package uk.ac.gla.jagora;

public interface Market {

	public void doClearing ();

	public TraderMarketView createTraderMarket();
}
