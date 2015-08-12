package uk.ac.glasgow.jagora.ticker.impl;

import static java.lang.String.format;

import java.io.PrintStream;

import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

public class TimeListenerTickerTapeListener implements TradeListener {

	private Long maxTicks;
	
	private PrintStream printStream;
		
	private  Double nextPercentage = 1.0;

	public TimeListenerTickerTapeListener(Long maxTicks, PrintStream printStream) {
		this.printStream = printStream;
		this.maxTicks = maxTicks;
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		Long tick = tradeExecutionEvent.tick;
		Double percentage = 100.0 * tick / maxTicks;
		if (percentage > nextPercentage){
			printStream.println(format("%3.0f%% completed.",nextPercentage));
			nextPercentage += 1.0;
		}
			
	}
	
	

}
