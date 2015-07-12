package uk.ac.glasgow.jagora.engine.impl;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;

public class SerialRandomEngine implements TradingEngine {

	private final Set<StockExchange> exchanges;
	private final Set<Level1Trader> traders;
	private  Set<Level2Trader> priviliigedTraders;
	private final World world;
	private final Random random;

	private Queue<DelayedExchangeLevel1View> level1ViewQueue;

	private Long standardDelay;

	/**
	 *
	 * @param world - set up the world
	 * @param exchanges - pass a set of exchanges
	 * @param traders - a fixed set of traders
	 * @param random - seed
	 */ SerialRandomEngine(World world, Set<StockExchange> exchanges,
						   Set<Level1Trader> traders, Random random, Long standardDelay){
		this.world = world;
		this.exchanges = new HashSet<StockExchange>(exchanges);
		this.traders = new HashSet<Level1Trader>(traders);
		this.random = random;
		this.level1ViewQueue = new PriorityQueue<DelayedExchangeLevel1View>();
		this.standardDelay = standardDelay;
	}
	
	@Override
	public void run() {
		while (world.isAlive()) {
			StockExchange exchange = random.chooseElement(exchanges);
			Level1Trader trader = random.chooseElement(traders);
			DelayedExchangeLevel1View delayedView =
					new DelayedExchangeLevel1View(exchange.createLevel1View(),
							standardDelay + world.getCurrentTick() - trader.getDelayDecrease());


			trader.speak(delayedView);
			level1ViewQueue.add(delayedView);

			while (!level1ViewQueue.isEmpty()
					&& world.getCurrentTick() >= level1ViewQueue.peek().getDelayedTick()){
				level1ViewQueue.poll().invoke();
			}

			exchange.doClearing();

			world.getTick(new Object());
		}
	}

}
