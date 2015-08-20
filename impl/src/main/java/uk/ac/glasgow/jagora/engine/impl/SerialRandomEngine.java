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

public class SerialRandomEngine implements TradingEngine {

	private final Set<StockExchange> exchanges;
	private final Set<Level1Trader> traders;
	private final World world;
	private final Random random;
	private final Set<Level2Trader> privilegedTraders = new HashSet<>();

	private Queue<DelayedOrderExecutor> orderExecutors;

	private Long delay;

	SerialRandomEngine(World world, Set<StockExchange> exchanges,
						   Set<Level1Trader> traders, Random random,
						   Long standardDelay, Set<Level2Trader> level2Traders){
		this.world = world;
		this.exchanges = new HashSet<StockExchange>(exchanges);
		this.traders = new HashSet<Level1Trader>(traders);
		this.random = random;
		this.orderExecutors = new PriorityQueue<DelayedOrderExecutor>();
		this.delay = standardDelay;
		this.privilegedTraders.addAll(level2Traders);
	}

	
	@Override
	public void run() {
		while (world.isAlive()) {
			StockExchange exchange = random.chooseElement(exchanges);
			Level1Trader trader = random.chooseElement(traders);
			DelayedExchangeLevel1View delayedView =
				new DelayedExchangeLevel1View(exchange.createLevel1View(),
					delay, world.getCurrentTick());

			trader.speak(delayedView);
			for(DelayedOrderExecutor executor: delayedView.getOrderExecutors()) {
				orderExecutors.add(executor);
			}


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
