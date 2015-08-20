package uk.ac.glasgow.jagora.ticker.impl;


import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.ticker.*;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection;
import uk.ac.glasgow.jagora.world.TickEvent;

import java.util.*;

import static java.util.Collections.shuffle;
import static uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection.BUY;
import static uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection.SELL;

public abstract class AbstractStockExchangeObservable implements StockExchangeObservable {

	private final Set<TradeListener> tradeListeners;
	
	private final Set<OrderListener> orderListeners;

	private final List<TickEvent<Trade>> executedTrades;

	private final List<OrderEntryEvent> submittedSellOrders;
	private final List<OrderEntryEvent> submittedBuyOrders;

	private final List<OrderEntryEvent> cancelledSellOrders;
	private final List<OrderEntryEvent> cancelledBuyOrders;
	

	public AbstractStockExchangeObservable() {
		tradeListeners = new HashSet<TradeListener>();
		orderListeners = new HashSet<OrderListener>();

		executedTrades = new ArrayList<TickEvent<Trade>>();

		submittedSellOrders = new ArrayList<OrderEntryEvent>();
		submittedBuyOrders = new ArrayList<OrderEntryEvent>();
		
		cancelledBuyOrders = new ArrayList<OrderEntryEvent>();
		cancelledSellOrders= new ArrayList<OrderEntryEvent>();

	}
	
	public List<TickEvent<Trade>> getTradeHistory(Stock stock) {
		
		List<TickEvent<Trade>> result = new ArrayList<TickEvent<Trade>>();
		
		executedTrades
			.stream()
			.filter(executedTrade -> executedTrade.event.getStock().equals(stock))
			.forEach(executedTrade -> result.add(executedTrade));

		return result;
	}

	public List<OrderEntryEvent> getSellOrderHistory(Stock stock){
		List<OrderEntryEvent> result = new ArrayList<>();

		for (OrderEntryEvent event: submittedSellOrders){
			if (event.stock.equals(stock))
				result.add(event);
		}

		return result;
	}

	public List<OrderEntryEvent> getBuyOrderHistory(Stock stock){
		List<OrderEntryEvent> result = new ArrayList<>();

		for (OrderEntryEvent event: submittedBuyOrders){
			if (event.stock.equals(stock))
				result.add(event);
		}


		return result;
	}

	public List<OrderEntryEvent> getCancelledBuyOrderHistory(Stock stock){
		return getCancelledHistory(stock,cancelledBuyOrders);
	}

	public List<OrderEntryEvent> getCancelledSellOrderHistory(Stock stock){
		return getCancelledHistory(stock, cancelledSellOrders);
	}

	private List<OrderEntryEvent> getCancelledHistory(Stock stock, List<OrderEntryEvent> list){
		List<OrderEntryEvent> result = new ArrayList<>();

		for (OrderEntryEvent event: list) {
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
	public void notifyOrderListeners(TickEvent<? extends Order> orderEvent){

		List<OrderListener> randomisedOrderListeners = 
			new ArrayList<OrderListener>(orderListeners);
		
		shuffle(randomisedOrderListeners);//why don't you just call getRandomisedTicketTapeListeners?
		
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

		//add submitted orders in the book
		if (orderEvent.event instanceof SellOrder)
			submittedSellOrders.add(orderEntryEvent);
		else
			submittedBuyOrders.add(orderEntryEvent);

	}

	public abstract void notifyOrderListenerOfOrder(
			OrderEntryEvent orderEntryEvent, OrderListener orderListener);

	@Override
	public void notifyOrderListenersOfCancellation(TickEvent<? extends Order> orderEvent) {

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


		if (event instanceof SellOrder) {
			cancelledSellOrders.add(orderEntryEvent);
		}
		else {
			cancelledBuyOrders.add(orderEntryEvent);
		}

		for (OrderListener orderListener: orderListeners)
			notifyOrderListenerOfCancelledOrder(orderEntryEvent, orderListener);
	}

	public abstract void notifyOrderListenerOfCancelledOrder(
			OrderEntryEvent orderEntryEvent, OrderListener orderListener);
	
}