package uk.ac.glasgow.jagora.experiment;

import java.io.PrintStream;

import uk.ac.glasgow.jagora.ticker.TickerTapeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

public class PriceTimeLoggerTickerTapeListener implements TickerTapeListener {
	
	private PrintStream printStream;

	public PriceTimeLoggerTickerTapeListener (PrintStream printStream){
		this.printStream = printStream;
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		this.printStream.println(tradeExecutionEvent.tick+","+tradeExecutionEvent.price);

	}

}
