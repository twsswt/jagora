package uk.ac.glasgow.jagora.engine.impl;

import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.DelayedExchangeLevel1View.DelayedOrderExecutor;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * A simulation engine that simulates differential latency between 'ordinary' and 'privileged' traders.
 * @author Ivelin
 * @author tws
 *
 */
public class SerialDelayEngine implements TradingEngine {

	private final Set<StockExchange> exchanges;
	private final Set<Level1Trader> ordinaryTraders;
	private final World world;
	private final Random random;
	private final Set<Level2Trader> privilegedTraders = new HashSet<>();

	private Queue<DelayedOrderExecutor> orderExecutors;

	private Long delay;

	SerialDelayEngine(
		World world,
		Set<StockExchange> exchanges,
		Set<Level1Trader> ordinaryTraders,
		Random random, Long standardDelay,
		Set<Level2Trader> privilegedTraders){
		
		this.world = world;
		this.exchanges = new HashSet<StockExchange>(exchanges);
		this.ordinaryTraders = new HashSet<Level1Trader>(ordinaryTraders);
		this.random = random;

		this.orderExecutors = new PriorityQueue<DelayedOrderExecutor>();
		
		this.delay = standardDelay;
		
		this.privilegedTraders.addAll(privilegedTraders);
	}

	
	@Override
	public void run() {
		while (world.isAlive()) {
			StockExchange exchange = random.chooseElement(exchanges);
			
			Level1Trader trader = random.chooseElement(ordinaryTraders);
			
			DelayedExchangeLevel1View delayedView =
				new DelayedExchangeLevel1View(
					exchange.createLevel1View(), world.getCurrentTick() + delay);

			trader.speak(delayedView);
			
			orderExecutors.addAll(delayedView.getOrderExecutors());

			while (!orderExecutors.isEmpty()
					&& world.getCurrentTick() >= orderExecutors.peek().getDelayedTick()){
				DelayedOrderExecutor executor = orderExecutors.poll();
						executor.execute();
			}

			if (world.getCurrentTick() > 10l) {
				Level2Trader level2Trader = random.chooseElement(privilegedTraders);
				if (level2Trader != null) {
					StockExchangeLevel2View level2View = exchange.createLevel2View();
					level2Trader.speak(level2View);
				}
			}
			exchange.doClearing();

			world.getTick(null);//used to enable running without Level2Traders
		}
	}

}
