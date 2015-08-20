package uk.ac.glasgow.jagora.ticker.impl;

import static java.util.Collections.shuffle;
import static uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection.BUY;
import static uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection.SELL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.ticker.OrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection;
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
		
		shuffle(randomisedTickerTapeListeners);
		return randomisedTickerTapeListeners;
	}
	
	@Override
	public void registerOrderListener(OrderListener orderListener) {
		orderListeners.add(orderListener);
	}
	
	@Override
	public void notifyOrderListeners(TickEvent<? extends Order> orderTickEvent){
		List<OrderListener> randomisedOrderListeners = getRandomisedOrderListeners();
		
		Order event = orderTickEvent.event;
		
		OrderDirection direction = event instanceof SellOrder ? SELL : BUY;
		
		OrderEvent orderEvent = 
			new OrderEvent(
				orderTickEvent.tick,
				event.getTrader(), 
				event.getStock(), 
				event.getRemainingQuantity(),
				event.getPrice(), 
				direction);
		
		for (OrderListener orderListener : randomisedOrderListeners)
			notifyOrderListenerOfOrder(orderEvent, orderListener);
	}

	public abstract void notifyOrderListenerOfOrder(
		OrderEvent orderEvent, OrderListener orderListener);
	
	@Override
	public void notifyOrderListenersOfCancellation(TickEvent<? extends Order> orderTickEvent) {

		List<OrderListener> randomisedOrderListeners = getRandomisedOrderListeners();
		
		Order event = orderTickEvent.event;
		
		OrderDirection direction = event instanceof SellOrder ? SELL : BUY;
		
		OrderEvent orderEvent = 
			new OrderEvent(
				orderTickEvent.tick,
				event.getTrader(), 
				event.getStock(), 
				event.getRemainingQuantity(),
				event.getPrice(), 
				direction);
		
		for (OrderListener orderListener : randomisedOrderListeners)
			notifyOrderListenerOfOrder(orderEvent, orderListener);
		
	}
	
	private List<OrderListener> getRandomisedOrderListeners() {
		List<OrderListener> randomisedOrderListeners = 
			new ArrayList<OrderListener>(orderListeners);
		
		shuffle(randomisedOrderListeners);
		return randomisedOrderListeners;
	}
}