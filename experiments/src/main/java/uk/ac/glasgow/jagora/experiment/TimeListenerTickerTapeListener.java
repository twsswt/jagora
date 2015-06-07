package uk.ac.glasgow.jagora.experiment;

import static java.lang.String.format;

import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;

public class TimeListenerTickerTapeListener implements TradeListener {

	private Long maxTicks;
		
	private  Double nextPercentage = 1.0;

	public TimeListenerTickerTapeListener(Long maxTicks) {
		this.maxTicks = maxTicks;
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		Long tick = tradeExecutionEvent.tick;
		Double percentage = 100.0 * tick / maxTicks;
		if (percentage > nextPercentage){
			System.out.println(format("%3.0f%% completed.",nextPercentage));
			nextPercentage += 1.0;
		}
			
	}
	
	

}
