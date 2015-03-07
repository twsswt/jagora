package uk.ac.glasgow.jagora.ticker.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.ticker.TickerTapeListener;
import uk.ac.glasgow.jagora.ticker.TickerTapeObservable;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.world.TickEvent;

public abstract class AbstractTickerTapeObservable implements TickerTapeObservable {

	private final Set<TickerTapeListener> tickerTapeListeners;

	private List<TickEvent<Trade>> executedTrades;

	public AbstractTickerTapeObservable() {
		tickerTapeListeners = new HashSet<TickerTapeListener>();
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
	public void addTicketTapeListener(TickerTapeListener tickerTapeListener) {
		tickerTapeListeners.add(tickerTapeListener);
	}

	@Override
	public void notifyTickerTapeListeners(List<TickEvent<Trade>> newlyExecutedTrades) {
		executedTrades.addAll(newlyExecutedTrades);
		
		for (TickEvent<Trade> executedTrade: newlyExecutedTrades)
			notifyTickerTapeListenersOfTrade(executedTrade);
	}

	private void notifyTickerTapeListenersOfTrade(TickEvent<Trade> executedTrade) {
		TradeExecutionEvent tradeExecutedEvent = 
			new TradeExecutionEvent(
				executedTrade.event.getStock(),
				executedTrade.tick,
				executedTrade.event.getPrice(),
				executedTrade.event.getQuantity());
		
		List<TickerTapeListener> randomisedTickerTapeListeners =
			getRandomisedTicketTapeListeners(executedTrade.event.getStock());
		
		for (TickerTapeListener tickerTapeListener: randomisedTickerTapeListeners)
			notifyTickerTapeListenerOfTrade(tradeExecutedEvent, tickerTapeListener);
	}	

	protected abstract void notifyTickerTapeListenerOfTrade(
		TradeExecutionEvent tradeExecutedEvent, TickerTapeListener tickerTapeListener);
	
	private List<TickerTapeListener> getRandomisedTicketTapeListeners(Stock stock) {
		List<TickerTapeListener> randomisedTickerTapeListeners = 
			new ArrayList<TickerTapeListener>(tickerTapeListeners);
		
		Collections.shuffle(randomisedTickerTapeListeners);
		return randomisedTickerTapeListeners;
	}

}