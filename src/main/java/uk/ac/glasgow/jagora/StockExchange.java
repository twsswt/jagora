package uk.ac.glasgow.jagora;

/**
 * Represents the basic functionality of a stock exchange.
 * @author Tim
 *
 */
public interface StockExchange {

	public void doClearing ();

	public StockExchangeTraderView createTraderStockExchangeView();
}
