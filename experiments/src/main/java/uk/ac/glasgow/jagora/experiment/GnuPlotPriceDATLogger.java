package uk.ac.glasgow.jagora.experiment;

import static java.lang.String.format;
import static uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection.SELL;

import java.io.PrintStream;

import uk.ac.glasgow.jagora.ticker.OrderEvent;
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
	public void orderEntered(OrderEvent orderEvent) {
		String template = null;
		Long price = orderEvent.price;
		if (orderEvent.orderDirection == SELL)
			template = "%d,%d,,";
		 else 
			template = "%d,,%d,";

		printStream.println(
			format(template,orderEvent.tick, price));
	}

	@Override
	public void orderCancelled(OrderEvent orderEvent) {
		// TODO Auto-generated method stub
	}

} 
