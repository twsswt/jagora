package uk.ac.glasgow.jagora.ticker;

import java.util.List;

import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.world.TickEvent;

public interface TickerTapeObservable {

	/**
	 * Registers the listener to receive notifications of trades 
	 * @param tickerTapeListener
	 */
	public abstract void addTicketTapeListener(TickerTapeListener tickerTapeListener);

	/**
	 * Notifies all registered ticker tape listeners of the occurrence of a new
	 * trade for a particular stock.
	 * @param executedTrade
	 */
	public abstract void notifyTickerTapeListeners(List<TickEvent<Trade>> list);

}