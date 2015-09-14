package uk.ac.glasgow.jagora.engine.impl.delay;

import java.util.Queue;

import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.StockExchangeViewProvider;
import uk.ac.glasgow.jagora.engine.impl.delay.DelayedExchangeLevel1View.DelayedOrderExecutor;
import uk.ac.glasgow.jagora.world.World;

/**
 * Simulates an interface to a stock exchange that delays
 * the arrival of orders after an agent speaks.
 * 
 * @author tws
 *
 */
public class DelayViewProvider implements StockExchangeViewProvider {

	private final StockExchangeViewProvider underlying;
	private final Long delay;
	private final World world;
	private final Queue<DelayedOrderExecutor> orderExecutorQueue;
	
	public DelayViewProvider(
		StockExchangeViewProvider underlying,
		Long delay,
		World world,
		Queue<DelayedOrderExecutor> orderExecutorQueue){

		this.underlying = underlying;
		this.delay = delay;
		this.world = world;
		this.orderExecutorQueue = orderExecutorQueue;
	}
	
	@Override
	public StockExchangeLevel1View createLevel1View() {
		
		Long currentTick = world.getCurrentTick();
		
		return new DelayedExchangeLevel1View(
			underlying.createLevel1View(), currentTick + delay, orderExecutorQueue);
	}

	@Override
	public StockExchangeLevel2View createLevel2View() {
		// TODO Auto-generated method stub
		return null;
	}

}
