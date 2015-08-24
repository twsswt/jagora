package uk.ac.glasgow.jagora.trader.impl;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.trader.Broker;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.StopLossOrder;

/**
 * Default implementation of Broker behaviour.  The DefaultBroker is not thread safe.
 * @author tws
 *
 */
public class DefaultBroker extends AbstractTrader implements Broker, Level1Trader, TradeListener {

	private Collection<StopLossOrder> monitoredOrders;
	private Queue<StopLossOrder> ordersToBeExecuted;
	
	
	public DefaultBroker(String name, Long cash, Map<Stock,Integer> inventory){
		super(name, cash, inventory);
		monitoredOrders = new ArrayList<StopLossOrder>();
		ordersToBeExecuted = new LinkedList<StopLossOrder>();
	}

	@Override
	public void speak(StockExchangeLevel1View traderView) {
		while (!ordersToBeExecuted.isEmpty())
			ordersToBeExecuted.poll().executeOrder(traderView);
	}

	@Override
	public void placeStopLossOrder(StopLossOrder stopLossBuyOrder) {
		monitoredOrders.add(stopLossBuyOrder);
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		Stock stock = tradeExecutionEvent.stock;		
		Long marketPrice = tradeExecutionEvent.price;
		
		Collection<StopLossOrder> triggeredOrders =
			monitoredOrders
				.stream()
				.filter(stopLossOrder -> stopLossOrder.getStock().equals(stock))
				.filter(stopLossOrder -> stopLossOrder.priceThresholdCrossed(marketPrice))
				.collect(toList());
		
		monitoredOrders.removeAll(triggeredOrders);
		ordersToBeExecuted.addAll(triggeredOrders);
	}
}
