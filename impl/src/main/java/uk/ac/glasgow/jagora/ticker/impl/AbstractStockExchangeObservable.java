package uk.ac.glasgow.jagora.ticker.impl;


import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;
import static uk.ac.glasgow.jagora.ticker.LimitOrderEvent.Action.CANCELLED;
import static uk.ac.glasgow.jagora.ticker.LimitOrderEvent.Action.PLACED;
import static uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection.BUY;
import static uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection.SELL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.glasgow.jagora.LimitOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.MarketOrder;
import uk.ac.glasgow.jagora.MarketSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.ticker.LimitOrderEvent;
import uk.ac.glasgow.jagora.ticker.LimitOrderEvent.Action;
import uk.ac.glasgow.jagora.ticker.MarketOrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.StockExchangeObservable;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.world.TickEvent;

public abstract class AbstractStockExchangeObservable implements StockExchangeObservable {

	private final Set<TradeListener> tradeListeners;
	
	private final Set<OrderListener> orderListeners;

	private final List<TickEvent<Trade>> executedTrades;

	private final List<LimitOrderEvent> limitOrderEvents;

	private final List<MarketOrderEvent> marketOrderEvents;	

	public AbstractStockExchangeObservable() {

		tradeListeners = new HashSet<TradeListener>();
		orderListeners = new HashSet<OrderListener>();

		executedTrades = new ArrayList<TickEvent<Trade>>();
		limitOrderEvents = new ArrayList<LimitOrderEvent>();
		marketOrderEvents = new ArrayList<MarketOrderEvent>();
	}
	
	@Override
	public List<TickEvent<Trade>> getTradeHistory(Stock stock) {
		
		List<TickEvent<Trade>> result = new ArrayList<TickEvent<Trade>>();
		
		executedTrades
			.stream()
			.filter(executedTrade -> executedTrade.event.getStock().equals(stock))
			.forEach(executedTrade -> result.add(executedTrade));

		return result;
	}

	@Override
	public List<LimitOrderEvent> getLimitSellOrderHistory(Stock stock){
		return filterEvents(stock, PLACED, SELL);
	}
	
	public List<LimitOrderEvent> getCancelledSellOrderHistory(Stock stock){
		return filterEvents(stock, CANCELLED, SELL);
	}

	@Override
	public List<LimitOrderEvent> getLimitBuyOrderHistory(Stock stock){
		return filterEvents(stock, PLACED, BUY);
	}

	public List<LimitOrderEvent> getCancelledBuyOrderHistory(Stock stock){
		return filterEvents(stock, CANCELLED, BUY);
	}
	
	private List<LimitOrderEvent> filterEvents (Stock stock, Action action, OrderDirection orderDirection){
		return limitOrderEvents.stream()
			.filter(limitOrderEvent -> limitOrderEvent.orderDirection.equals(orderDirection))
			.filter(limitOrderEvent -> limitOrderEvent.action.equals(action))
			.filter(limitOrderEvent -> limitOrderEvent.stock.equals(stock))
			.collect(toList());
	}

	@Override
	public void registerTradeListener(TradeListener tradeListener) {
		if (tradeListeners.contains(tradeListener))
			return;

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
			getRandomisedTradeListeners();
		
		for (TradeListener tradeListener: randomisedTickerTapeListeners)
			notifyTradeListenerOfTrade(tradeExecutedEvent, tradeListener);
	}

	public abstract void notifyTradeListenerOfTrade(
		TradeExecutionEvent tradeExecutedEvent, TradeListener tradeListener);
	
	private List<TradeListener> getRandomisedTradeListeners() {
		List<TradeListener> randomisedTickerTapeListeners =
			new ArrayList<TradeListener>(tradeListeners);

		shuffle(randomisedTickerTapeListeners);
		return randomisedTickerTapeListeners;
	}
	
	@Override
	public void registerOrderListener(OrderListener orderListener) {
		if (orderListeners.contains(orderListener))
			return;

		orderListeners.add(orderListener);

	}

	@Override
	public void notifyOrderListenersOfLimitOrder(TickEvent<? extends LimitOrder> orderEvent){

		List<OrderListener> randomisedOrderListeners = 
			getRandomisedOrderListeners();
		
		LimitOrder order = orderEvent.event;
		
		OrderDirection direction = order instanceof LimitSellOrder ? SELL : BUY;
		
		LimitOrderEvent limitOrderEvent =
			new LimitOrderEvent(
				orderEvent.tick,
				order.getTrader(), 
				order.getStock(), 
				order.getRemainingQuantity(),
				direction, 
				order.getLimitPrice(),
				PLACED);
		
		limitOrderEvents.add(limitOrderEvent);

		for (OrderListener orderListener : randomisedOrderListeners)
			notifyOrderListenerOfOrderEvent(limitOrderEvent, orderListener);
	}

	@Override
	public void notifyOrderListenersOfLimitOrderCancellation(
		TickEvent<? extends LimitOrder> orderEvent) {

		LimitOrder order = orderEvent.event;

		OrderDirection direction = 
			order instanceof LimitSellOrder ? SELL : BUY;

		LimitOrderEvent limitOrderEvent =
			new LimitOrderEvent(
				orderEvent.tick,
				order.getTrader(),
				order.getStock(),
				order.getRemainingQuantity(),
				direction,
				order.getLimitPrice(),
				CANCELLED);

		limitOrderEvents.add(limitOrderEvent);
		
		for (OrderListener orderListener: orderListeners)
			notifyOrderListenerOfOrderEvent(limitOrderEvent, orderListener);
	}
	
	@Override
	public void notifyOrderListenersOfMarketOrder(TickEvent<? extends MarketOrder> orderEvent) {
		
		MarketOrder order = orderEvent.event;
		
		OrderDirection direction = 
			order instanceof MarketSellOrder ? SELL : BUY;
		
		MarketOrderEvent marketOrderEvent =
			new MarketOrderEvent(
				orderEvent.tick,
				order.getTrader(),
				order.getStock(),
				order.getRemainingQuantity(),
				direction);

		marketOrderEvents.add(marketOrderEvent);
		
		for (OrderListener orderListener: orderListeners)
			notifyOrderListenerOfMarketOrderEvent(marketOrderEvent, orderListener);
	}
	

	public abstract void notifyOrderListenerOfMarketOrderEvent(
		MarketOrderEvent marketOrderEvent, OrderListener orderListener);

	public abstract void notifyOrderListenerOfOrderEvent(
		LimitOrderEvent limitOrderEvent, OrderListener orderListener);
	
	private List<OrderListener> getRandomisedOrderListeners() {
		List<OrderListener> randomisedOrderListeners = 
			new ArrayList<OrderListener>(orderListeners);
		
		shuffle(randomisedOrderListeners);
		return randomisedOrderListeners;
	}
}