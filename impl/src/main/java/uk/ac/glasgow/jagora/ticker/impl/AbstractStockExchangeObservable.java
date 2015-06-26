package uk.ac.glasgow.jagora.ticker.impl;

import static uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection.BUY;
import static uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection.SELL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.StockExchangeObservable;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.world.TickEvent;

public abstract class AbstractStockExchangeObservable implements StockExchangeObservable {

	private final Set<TradeListener> tradeListeners;
	private final Set<OrderListener> orderListeners;

	private final List<TickEvent<Trade>> executedTrades;

	public AbstractStockExchangeObservable() {
		tradeListeners = new HashSet<TradeListener>();
		orderListeners = new HashSet<OrderListener>();
		
		executedTrades = new ArrayList<TickEvent<Trade>>();
	}
	
	public List<TickEvent<Trade>> getTradeHistory(Stock stock) {
		
		List<TickEvent<Trade>> result = new ArrayList<TickEvent<Trade>>();
		
		executedTrades
			.stream()
			.filter(executedTrade -> executedTrade.event.getStock().equals(stock))
			.forEach(executedTrade -> result.add(executedTrade));

		return result;
	}


	@Override
	public void registerTradeListener(TradeListener tradeListener) {
		tradeListeners.add(tradeListener);
	}

	@Override
	public void notifyTradeListeners(List<TickEvent<Trade>> newlyExecutedTrades) {
		executedTrades.addAll(newlyExecutedTrades);
		
		for (TickEvent<Trade> executedTrade: newlyExecutedTrades)
			notifyTradeListenersOfTrade(executedTrade);
	}

	private void notifyTradeListenersOfTrade(TickEvent<Trade> executedTrade) {
		TradeExecutionEvent tradeExecutedEvent = 
			new TradeExecutionEvent(
				executedTrade.event.getStock(),
				executedTrade.event.getBuyer(),
				executedTrade.event.getSeller(),
				executedTrade.tick,
				executedTrade.event.getPrice(),
				executedTrade.event.getQuantity());
		
		List<TradeListener> randomisedTickerTapeListeners =
			getRandomisedTicketTapeListeners(executedTrade.event.getStock());
		
		for (TradeListener tradeListener: randomisedTickerTapeListeners)
			notifyTradeListenerOfTrade(tradeExecutedEvent, tradeListener);
	}	

	protected abstract void notifyTradeListenerOfTrade(
		TradeExecutionEvent tradeExecutedEvent, TradeListener tradeListener);
	
	private List<TradeListener> getRandomisedTicketTapeListeners(Stock stock) {
		List<TradeListener> randomisedTickerTapeListeners = 
			new ArrayList<TradeListener>(tradeListeners);
		
		Collections.shuffle(randomisedTickerTapeListeners);
		return randomisedTickerTapeListeners;
	}
	
	@Override
	public void registerOrderListener(OrderListener orderListener) {
		orderListeners.add(orderListener);
	}
	
	@Override
	public void notifyOrderListeners(TickEvent<? extends Order> orderEvent){
		List<OrderListener> randomisedOrderListeners = 
			new ArrayList<OrderListener>(orderListeners);
		
		Collections.shuffle(randomisedOrderListeners);
		
		Order event = orderEvent.event;
		
		OrderDirection direction = event instanceof SellOrder ? SELL : BUY;
		
		OrderEntryEvent orderEntryEvent = 
			new OrderEntryEvent(
				orderEvent.tick,
				event.getTrader(), 
				event.getStock(), 
				event.getRemainingQuantity(),
				event.getPrice(), 
				direction);
		
		for (OrderListener orderListener : randomisedOrderListeners)
			notifyOrderListenerOfOrder(orderEntryEvent, orderListener);
	}

	public abstract void notifyOrderListenerOfOrder(
		OrderEntryEvent orderEntryEvent, OrderListener orderListener);
	
}