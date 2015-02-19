package uk.ac.glasgow.jagora.ticker;

import java.util.List;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.impl.AbstractTrade;
import uk.ac.glasgow.jagora.world.TickEvent;

public interface TickerTapeObserver {

	/**
	 * Registers the listener to receive notifications of a trade in a particular stock.
	 * @param tickerTapeListener
	 * @param stock
	 */
	public abstract void addTicketTapeListener(TickerTapeListener tickerTapeListener, Stock stock);

	/**
	 * Notifies all registered ticker tape listeners of the occurrence of a new
	 * trade for a particular stock.
	 * @param executedTrade
	 */
	public abstract void notifyTickerTapeListeners(List<TickEvent<Trade>> list);

}