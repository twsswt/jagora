package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;

public class SerialTickerTapeObserver extends AbstractStockExchangeObservable {
	
	@Override
	public void notifyTradeListenerOfTrade(
			TradeExecutionEvent tradeExecutionEvent, TradeListener tradeListener) {
			tradeListener.tradeExecuted(tradeExecutionEvent);
	}

	@Override
	public void notifyOrderListenerOfOrder(OrderEntryEvent orderEntryEvent, OrderListener orderListener) {
		orderListener.orderEntered(orderEntryEvent);
	}

	@Override
	public void notifyOrderListenerOfCancelledOrder(OrderEntryEvent orderEntryEvent, OrderListener orderListener) {
		orderListener.orderCancelled(orderEntryEvent);
	}
}
