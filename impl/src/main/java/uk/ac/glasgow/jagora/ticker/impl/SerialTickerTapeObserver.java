package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

public class SerialTickerTapeObserver extends AbstractStockExchangeObservable {
	
	@Override
	protected void notifyTradeListenerOfTrade(
			TradeExecutionEvent tradeExecutionEvent, TradeListener tradeListener) {
		tradeListener.tradeExecuted(tradeExecutionEvent);
		
	}

	@Override
	public void notifyOrderListenerOfOrder(OrderEntryEvent orderEntryEvent, OrderListener orderListener) {
		orderListener.orderEntered(orderEntryEvent);
	}

}
