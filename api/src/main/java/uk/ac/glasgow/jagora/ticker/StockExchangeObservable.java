package uk.ac.glasgow.jagora.ticker;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.world.TickEvent;

import java.util.List;


public interface StockExchangeObservable {

	/**
	 * Registers the listener to receive notifications of trades 
	 * @param tradeListener
	 */
	public abstract void registerTradeListener(TradeListener tradeListener);

	/**
	 * Notifies all registered ticker tape listeners of the occurrence of a new
	 * trade for a particular stock.
	 *
	 */
	public abstract void notifyTradeListeners(List<TickEvent<Trade>> list);

	public abstract void registerOrderListener(OrderListener orderListener);
	
	public abstract void notifyOrderListeners(TickEvent<? extends Order> orderEvent);

	void notifyOrderListenersOfCancellation(TickEvent<? extends Order> orderEvent);

	void registerPriceListener (PriceListener tradePriceListener);



	//notifyPriceTradeListener is left as a private method to be implemented


}