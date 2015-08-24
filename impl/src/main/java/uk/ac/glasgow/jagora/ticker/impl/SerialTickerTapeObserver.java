package uk.ac.glasgow.jagora.ticker.impl;

import uk.ac.glasgow.jagora.ticker.LimitOrderEvent;
import uk.ac.glasgow.jagora.ticker.MarketOrderEvent;
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
	public void notifyOrderListenerOfOrderEvent(
		LimitOrderEvent limitOrderEvent, OrderListener orderListener) {
		orderListener.limitOrderEvent(limitOrderEvent);
	}

	@Override
	public void notifyOrderListenerOfMarketOrderEvent(
		MarketOrderEvent marketOrderEvent, OrderListener orderListener) {
		orderListener.marketOrderEntered(marketOrderEvent);
	}

}
