package uk.ac.glasgow.jagora.ticker.impl;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.ticker.*;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection;
import uk.ac.glasgow.jagora.world.TickEvent;

import java.util.*;

import static uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection.BUY;
import static uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection.SELL;



public abstract class AbstractStockExchangeObservable implements StockExchangeObservable {

	private final Set<TradeListener> tradeListeners;
	private final Set<OrderListener> orderListeners;

	private final Map<Stock, PriorityQueue<BuyTradePriceListener>> buyPriceListeners;
	private final Map<Stock, PriorityQueue<SellTradePriceListener>> sellPriceListeners;

	private final List<TickEvent<Trade>> executedTrades;

	private final List<OrderEntryEvent> submittedSellOrders;
	private final List<OrderEntryEvent> submittedBuyOrders;

	private final List<OrderEntryEvent> cancelledSellOrders;
	private final List<OrderEntryEvent> cancelledBuyOrders;



	public AbstractStockExchangeObservable() {
		tradeListeners = new HashSet<TradeListener>();
		orderListeners = new HashSet<OrderListener>();

		buyPriceListeners = new HashMap<Stock, PriorityQueue<BuyTradePriceListener>>();
		sellPriceListeners = new HashMap<Stock, PriorityQueue<SellTradePriceListener>>();

		executedTrades = new ArrayList<TickEvent<Trade>>();

		submittedSellOrders = new ArrayList();
		submittedBuyOrders = new ArrayList<>();
		
		cancelledBuyOrders = new ArrayList<>();
		cancelledSellOrders= new ArrayList<>();

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
				executedTrade.event.getQuantity(),
				executedTrade.event.isAggressiveSell());
		
		List<TradeListener> randomisedTickerTapeListeners =
			getRandomisedTicketTapeListeners();
		
		for (TradeListener tradeListener: randomisedTickerTapeListeners)
			notifyTradeListenerOfTrade(tradeExecutedEvent, tradeListener);

		//notify the price listeners
		notifyPriceTradeListeners(executedTrade.event.getStock(), executedTrade.event.getPrice());
	}

	private void notifyPriceTradeListeners(Stock stock, Long price) {

		PriorityQueue queue = buyPriceListeners.get(stock);

		if (queue != null && queue.size() != 0){

			TradePriceListener listener = buyPriceListeners.get(stock).peek();
			if (listener == null) return;
			//check if one of the buyListeners is activated
			if (listener.getPrice() <= price){
				while (listener != null && listener.getPrice() <= price ) {
					listener.priceReached(); //execute the order on the market
					buyPriceListeners.get(stock).poll();
					listener = buyPriceListeners.get(stock).peek();
				}
			}
		}
		else {

			queue = sellPriceListeners.get(stock);
			if (queue == null || queue.size() == 0) return;
			//else check if one of the sellPriceListeners is activated
			TradePriceListener listener = sellPriceListeners.get(stock).peek();
			if (listener.getPrice() >= price){
				while (listener != null && listener.getPrice() >= price ) {
					listener.priceReached(); //execute the order on the market
					sellPriceListeners.get(stock).poll();
					listener = sellPriceListeners.get(stock).peek();
				}
			}
		}
		//currently the method doesn't support interfering PriceListeners(sell stop price higher than buy stop)
	}

	@Override
	public void registerPriceListener(PriceListener tradePriceListener) {
		//Maybe change implementation so that we don't use instanceof

		if (tradePriceListener instanceof BuyTradePriceListener)
			registerPriceTradeListener(buyPriceListeners, (TradePriceListener) tradePriceListener);

		else if (tradePriceListener instanceof SellTradePriceListener)
			registerPriceTradeListener(sellPriceListeners, (TradePriceListener) tradePriceListener);

	}

	private void registerPriceTradeListener  (Map map,TradePriceListener tradePriceListener){
		PriorityQueue queue =
				(PriorityQueue) map.get(( tradePriceListener).getStock());

		if (queue == null) {
			queue = new PriorityQueue<TradePriceListener>();
		}

		queue.add(tradePriceListener);
		map.put(tradePriceListener.getStock(), queue);
	}

	/**
	 * Left for implementation in child classes.
	 * @param tradeExecutedEvent
	 * @param tradeListener
	 */
	protected abstract void notifyTradeListenerOfTrade(
		TradeExecutionEvent tradeExecutedEvent, TradeListener tradeListener);


	private List<TradeListener> getRandomisedTicketTapeListeners() {
		List<TradeListener> randomisedTickerTapeListeners =
			new ArrayList<TradeListener>(tradeListeners);

		Collections.shuffle(randomisedTickerTapeListeners);
		return randomisedTickerTapeListeners;
	}
	
	@Override
	public void registerOrderListener(OrderListener orderListener) {
		if (orderListeners.contains(orderListener))
			return;

		orderListeners.add(orderListener);
	}

	public  Integer orderCount = 0;

	@Override
	public void notifyOrderListeners(TickEvent<? extends Order> orderEvent){


		List<OrderListener> randomisedOrderListeners = 
			new ArrayList<OrderListener>(orderListeners);
		
		Collections.shuffle(randomisedOrderListeners);//why don't you just call getRandomisedTicketTapeListeners?
		
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
		orderCount ++;
		//add submitted orders in the book
		if (orderEvent.event instanceof SellOrder)
			submittedSellOrders.add(orderEntryEvent);
		else
			submittedBuyOrders.add(orderEntryEvent);

	}

	/**
	 * Left for implementation in child classes.
	 * @param orderEntryEvent
	 * @param orderListener
	 */
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

		//TODO bug remaining quantity is not updated
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