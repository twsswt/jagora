package uk.ac.glasgow.jagora.ticker.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.AbstractTrade;
import uk.ac.glasgow.jagora.ticker.TickerTapeListener;
import uk.ac.glasgow.jagora.ticker.TickerTapeObserver;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.world.TickEvent;

public abstract class AbstractTickerTapeObserver implements TickerTapeObserver {

	private final Map<Stock,Set<TickerTapeListener>> tickerTapeListenersByStock;

	private List<TickEvent<Trade>> executedTrades;

	public AbstractTickerTapeObserver() {
		tickerTapeListenersByStock = new HashMap<Stock,Set<TickerTapeListener>>();
		 executedTrades = new ArrayList<TickEvent<Trade>>();
	}
	

	public List<TickEvent<Trade>> getTradeHistory(Stock oranges) {
		
		List<TickEvent<Trade>> result = new ArrayList<TickEvent<Trade>>();
		
		executedTrades
			.stream()
			.filter(executedTrade -> executedTrade.event.getStock().equals(oranges))
			.forEach(executedTrade -> result.add(executedTrade));

		return result;
	}


	@Override
	public void addTicketTapeListener(TickerTapeListener tickerTapeListener, Stock stock) {
		getTickerTapeListeners(stock).add(tickerTapeListener);
	}

	@Override
	public void notifyTickerTapeListeners(List<TickEvent<Trade>> newlyExecutedTrades) {
		executedTrades.addAll(newlyExecutedTrades);
		
		for (TickEvent<Trade> executedTrade: executedTrades)
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
			new ArrayList<TickerTapeListener>(getTickerTapeListeners(stock));
		
		Collections.shuffle(randomisedTickerTapeListeners);
		return randomisedTickerTapeListeners;
	}

	private Collection<TickerTapeListener> getTickerTapeListeners(Stock stock) {
		Set<TickerTapeListener> tickerTapeListeners = 
			tickerTapeListenersByStock.get(stock);
		if (tickerTapeListeners == null){
			tickerTapeListeners = new HashSet<TickerTapeListener>();
			tickerTapeListenersByStock.put(stock, tickerTapeListeners);
		}
		return tickerTapeListeners;			
	}

}