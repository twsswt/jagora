package uk.ac.glasgow.jagora.experiment;

import static java.lang.String.format;
import static uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection.SELL;

import java.io.PrintStream;

import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

/**
 * 
 * @author Tim
 *
 */
public class GnuPlotPriceDATLogger implements TradeListener, OrderListener {
		
	private PrintStream printStream;
		
	public GnuPlotPriceDATLogger (PrintStream printStream){
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
		Long price = orderEntryEvent.price;
		if (orderEntryEvent.orderDirection == SELL)
			template = "%d,%d,,";
		 else 
			template = "%d,,%d,";

		printStream.println(
			format(template,orderEntryEvent.tick, price));
	}

} 
