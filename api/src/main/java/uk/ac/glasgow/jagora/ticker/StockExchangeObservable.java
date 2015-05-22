package uk.ac.glasgow.jagora.ticker;

import java.util.List;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.world.TickEvent;

public interface StockExchangeObservable {

	/**
	 * Registers the listener to receive notifications of trades 
	 * @param tradeListener
	 */
	public abstract void registerTradeListener(TradeListener tradeListener);

	/**
	 * Notifies all registered ticker tape listeners of the occurrence of a new
	 * trade for a particular stock.
	 * @param executedTrade
	 */
	public abstract void notifyTradeListeners(List<TickEvent<Trade>> list);

	public abstract void registerOrderListener(OrderListener orderListener);
	
	public abstract void notifyOrderListeners(TickEvent<? extends Order> orderEvent);

}