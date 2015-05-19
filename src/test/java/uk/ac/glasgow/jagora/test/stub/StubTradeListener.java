package uk.ac.glasgow.jagora.test.stub;

import java.util.ArrayList;
import java.util.List;

import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;

public class StubTradeListener implements TradeListener {

	
	private Integer tradeCount = 0;
	private List<TradeExecutionEvent> tradeExecutionEvents = new ArrayList<TradeExecutionEvent>();

	@Override
	public void tradeExecuted(
		TradeExecutionEvent tradeExecutionEvent) {
		this.tradeExecutionEvents.add(tradeExecutionEvent);
		tradeCount ++;

	}

	public TradeExecutionEvent getLastTradeExecutionEvent() {
		return tradeExecutionEvents.get(tradeExecutionEvents.size()-1);
	}
	
	public Double getAverageTradePrice (){
		return tradeExecutionEvents.stream().mapToDouble(event -> event.price).average().getAsDouble();
	}

	public Integer getTradeCount() {
		return tradeCount;
	}

}
