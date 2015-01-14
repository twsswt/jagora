package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.ticker.TickerTapeListener;

/**
 * Represents the basic functionality of a stock exchange.
 * @author Tim
 *
 */
public interface StockExchange {

	public void doClearing ();

	public StockExchangeTraderView createTraderStockExchangeView();

	public void addTicketTapeListener(
		TickerTapeListener tickerTapeListener, Stock stock);	
}
