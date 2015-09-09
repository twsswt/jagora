package uk.ac.glasgow.jagora;

/**
 * Represents the basic functionality of a stock exchange.
 * @author Tim
 *
 */
public interface StockExchange extends StockExchangeViewProvider {

	public void doClearing ();
	
}
