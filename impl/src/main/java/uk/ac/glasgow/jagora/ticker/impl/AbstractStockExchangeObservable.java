package uk.ac.glasgow.jagora.ticker.impl;


import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.ticker.*;
import uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection;
import uk.ac.glasgow.jagora.world.TickEvent;

import java.util.*;

import static java.util.Collections.shuffle;
import static uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection.BUY;
import static uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection.SELL;

public abstract class AbstractStockExchangeObservable implements StockExchangeObservable {

	private final Set<TradeListener> tradeListeners;
	
	private final Set<OrderListener> orderListeners;

	private final List<TickEvent<Trade>> executedTrades;

	private final List<OrderEvent> submittedSellOrders;
	private final List<OrderEvent> submittedBuyOrders;

	private final List<OrderEvent> cancelledSellOrders;
	private final List<OrderEvent> cancelledBuyOrders;
	

	public AbstractStockExchangeObservable() {
		tradeListeners = new HashSet<TradeListener>();
		orderListeners = new HashSet<OrderListener>();

		executedTrades = new ArrayList<TickEvent<Trade>>();

		submittedSellOrders = new ArrayList<OrderEvent>();
		submittedBuyOrders = new ArrayList<OrderEvent>();
		
		cancelledBuyOrders = new ArrayList<OrderEvent>();
		cancelledSellOrders= new ArrayList<OrderEvent>();

	}
	
	public List<TickEvent<Trade>> getTradeHistory(Stock stock) {
		
		List<TickEvent<Trade>> result = new ArrayList<TickEvent<Trade>>();
		
		executedTrades
			.stream()
			.filter(executedTrade -> executedTrade.event.getStock().equals(stock))
			.forEach(executedTrade -> result.add(executedTrade));

		return result;
	}

	public List<OrderEvent> getSellOrderHistory(Stock stock){
		List<OrderEvent> result = new ArrayList<>();

		for (OrderEvent event: submittedSellOrders){
			if (event.stock.equals(stock))
				result.add(event);
		}

		return result;
	}

	public List<OrderEvent> getBuyOrderHistory(Stock stock){
		List<OrderEvent> result = new ArrayList<>();

		for (OrderEvent event: submittedBuyOrders){
			if (event.stock.equals(stock))
				result.add(event);
		}


		return result;
	}

	public List<OrderEvent> getCancelledBuyOrderHistory(Stock stock){
		return getCancelledHistory(stock,cancelledBuyOrders);
	}

	public List<OrderEvent> getCancelledSellOrderHistory(Stock stock){
		return getCancelledHistory(stock, cancelledSellOrders);
	}

	private List<OrderEvent> getCancelledHistory(Stock stock, List<OrderEvent> list){
		List<OrderEvent> result = new ArrayList<>();

		for (OrderEvent event: list) {
			if (event != null && event.stock.equals(stock))
				result.add(event);
		}

		return result;
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
			getRandomisedTickerTapeListeners();
		
		for (TradeListener tradeListener: randomisedTickerTapeListeners)
			notifyTradeListenerOfTrade(tradeExecutedEvent, tradeListener);
	}

	public abstract void notifyTradeListenerOfTrade(
		TradeExecutionEvent tradeExecutedEvent, TradeListener tradeListener);

	
	private List<TradeListener> getRandomisedTickerTapeListeners() {
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
	public void notifyOrderListeners(TickEvent<? extends Order> orderTickEvent){

		List<OrderListener> randomisedOrderListeners = 
			getRandomisedOrderListeners();
		
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

		//add submitted orders in the book
		if (orderTickEvent.event instanceof SellOrder)
			submittedSellOrders.add(orderEvent);
		else
			submittedBuyOrders.add(orderEvent);

	}

	public abstract void notifyOrderListenerOfOrder(
			OrderEvent orderEvent, OrderListener orderListener);

	@Override
	public void notifyOrderListenersOfCancellation(TickEvent<? extends Order> orderTickEvent) {

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


		if (event instanceof SellOrder) {
			cancelledSellOrders.add(orderEvent);
		}
		else {
			cancelledBuyOrders.add(orderEvent);
		}

		for (OrderListener orderListener: orderListeners)
			notifyOrderListenerOfCancelledOrder(orderEvent, orderListener);
	}

	public abstract void notifyOrderListenerOfCancelledOrder(
			OrderEvent orderEvent, OrderListener orderListener);
	
	
	private List<OrderListener> getRandomisedOrderListeners() {
		List<OrderListener> randomisedOrderListeners = 
			new ArrayList<OrderListener>(orderListeners);
		
		shuffle(randomisedOrderListeners);
		return randomisedOrderListeners;
	}
}