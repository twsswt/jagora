package uk.ac.glasgow.jagora.engine.impl;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.DelayedExchangeLevel1View.DelayedOrderExecutor;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.world.World;

/**
 * A simulation engine that simulates differential latency
 * between 'ordinary' and 'privileged' traders. All traders
 * are offered an opportunity to speak on the underlying
 * exchange during a round. However, ordinary traders orders
 * are subject to delay and may actually be executed in
 * subsequent rounds.
 * 
 * @author Ivelin
 * @author tws
 *
 */
public class SerialDelayEngine implements TradingEngine {

	private final StockExchange exchange;
	private final Set<Level1Trader> ordinaryTraders;
	private final World world;
	private final Set<Level2Trader> privilegedTraders = new HashSet<>();

	private Queue<DelayedOrderExecutor> orderExecutors;

	private Long delay;

	SerialDelayEngine(
		World world,
		StockExchange exchange,
		Set<Level1Trader> ordinaryTraders,
		Set<Level2Trader> privilegedTraders, 
		Long standardDelay){
		
		this.world = world;
		this.exchange = exchange;
		this.ordinaryTraders = new HashSet<Level1Trader>(ordinaryTraders);

		this.orderExecutors = new PriorityQueue<DelayedOrderExecutor>();
		
		this.delay = standardDelay;
		
		this.privilegedTraders.addAll(privilegedTraders);
	}

	
	@Override
	public void run() {
		while (world.isAlive()) {
			
			for (Level1Trader trader: ordinaryTraders){
			
				DelayedExchangeLevel1View delayedView =
					new DelayedExchangeLevel1View(
						exchange.createLevel1View(), world.getCurrentTick() + delay);

				trader.speak(delayedView);
			
				orderExecutors.addAll(delayedView.getOrderExecutors());
			}
			
			for (Level2Trader level2Trader : privilegedTraders){
				StockExchangeLevel2View level2View = exchange.createLevel2View();
				level2Trader.speak(level2View);
			}
			
			while (!orderExecutors.isEmpty()
				&& world.getCurrentTick() >= orderExecutors.peek().getDelayedTick()){
				
				DelayedOrderExecutor executor = orderExecutors.poll();
				executor.execute();
			}
			
			exchange.doClearing();
		}
	}

}
