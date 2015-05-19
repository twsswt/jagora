package uk.ac.glasgow.jagora;

/**
 * Represents the basic functionality of a stock exchange.
 * @author Tim
 *
 */
public interface StockExchange {

	public void doClearing ();

	public StockExchangeLevel1View createLevel1View();
	
	public StockExchangeLevel2View createLevel2View();

}
