package uk.ac.glasgow.jagora.experiment;

import static java.lang.String.format;

import java.io.PrintStream;
import java.util.LinkedList;

import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

public class PriceTimeLoggerTickerTapeListener implements TradeListener, OrderListener {
	
	private LinkedList<Long> recentBids = new LinkedList<Long>();
	private LinkedList<Long> recentOffers = new LinkedList<Long>();
	
	private PrintStream printStream;
	
	public PriceTimeLoggerTickerTapeListener (PrintStream printStream){
		this.printStream = printStream;
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		printStream.println(format("%d,,,%d",
			tradeExecutionEvent.tick, tradeExecutionEvent.price));

	}

	@Override
	public void orderEntered(OrderEntryEvent orderEntryEvent) {
		String template = null;
		Long price = null;
		if (orderEntryEvent.isOffer) {
			template = "%d,%d";
			if (recentOffers.size() > 10) recentOffers.poll();
			recentOffers.offer(orderEntryEvent.price);
			price = (long)recentOffers.stream().mapToLong(p -> p).average().getAsDouble();
		} else {
			template = "%d,,%d";
			if (recentBids.size() > 10) recentBids.poll();
			recentBids.offer(orderEntryEvent.price);
			price = (long)recentBids.stream().mapToLong(p -> p).average().getAsDouble();
		}
		
		if (orderEntryEvent.tick % 25 == 0){	
			printStream.println(
				format(template,orderEntryEvent.tick, price));
		}
		
	}

}
