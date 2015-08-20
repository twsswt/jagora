package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.OrderEvent;
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
	public void notifyOrderListenerOfOrder(OrderEvent orderEvent, OrderListener orderListener) {
		orderListener.orderEntered(orderEvent);
	}

}
