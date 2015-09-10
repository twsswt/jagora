package uk.ac.glasgow.jagora.engine.impl.delay;

import java.util.Queue;

import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.StockExchangeViewProvider;
import uk.ac.glasgow.jagora.engine.impl.delay.DelayedExchangeLevel1View.DelayedOrderExecutor;

public class DelayViewProvider implements StockExchangeViewProvider {

	private final Long delay;
	private final StockExchangeViewProvider underlying;
	
	private Queue<DelayedOrderExecutor> orderExecutorQueue;
	
	public DelayViewProvider(StockExchangeViewProvider underlying, Long delay, Queue<DelayedOrderExecutor> orderExecutorQueue){
		this.delay = delay;
		this.underlying = underlying;
		this.orderExecutorQueue = orderExecutorQueue;
	}
	
	@Override
	public StockExchangeLevel1View createLevel1View() {
		return new DelayedExchangeLevel1View(underlying.createLevel1View(), delay, orderExecutorQueue);
	}

	@Override
	public StockExchangeLevel2View createLevel2View() {
		// TODO Auto-generated method stub
		return null;
	}

}
