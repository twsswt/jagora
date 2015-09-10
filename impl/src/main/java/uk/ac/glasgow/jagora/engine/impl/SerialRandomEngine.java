package uk.ac.glasgow.jagora.engine.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeViewProvider;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.engine.impl.delay.DelayedExchangeLevel1View.DelayedOrderExecutor;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;

public class SerialRandomEngine implements TradingEngine {
	
	static class TraderViewSpecification {
		
		public final Trader trader;
		public final StockExchangeViewProvider stockExchangeViewProvider;
		
		public TraderViewSpecification (Trader trader, StockExchangeViewProvider stockExchangeViewProvider){
			this.trader = trader;
			this.stockExchangeViewProvider = stockExchangeViewProvider;
		}
		
	}
	
	private final World world;	
	private final Set<StockExchange> exchanges;
	private final Collection<TraderViewSpecification> traderViewSpecifications;
	
	private Queue<DelayedOrderExecutor> orderExecutors;
	
	private final Random random;
	
	public SerialRandomEngine (
		World world, Set<StockExchange> exchanges,
		Collection<TraderViewSpecification> traderViewSpecifications,
		Queue<DelayedOrderExecutor> orderExecutors,
		Random random){
		
		this.world = world;
		this.exchanges = new HashSet<StockExchange>(exchanges);
		
		this.traderViewSpecifications =
			new ArrayList<TraderViewSpecification>(traderViewSpecifications);
		
		this.random = random;
		
		this.orderExecutors = orderExecutors;

	}
	
	@Override
	public void run() {
		while (world.isAlive()) {
			
			TraderViewSpecification traderViewSpecification
				= random.chooseElement(traderViewSpecifications);
			
			Trader trader = traderViewSpecification.trader;
	
			StockExchangeViewProvider stockExchangeViewProvider = 
				traderViewSpecification.stockExchangeViewProvider;
						
			if (trader instanceof Level1Trader)
				((Level1Trader)trader).speak(stockExchangeViewProvider.createLevel1View());
			else if (trader instanceof Level2Trader)
				((Level1Trader)trader).speak(stockExchangeViewProvider.createLevel2View());				
				
			processDelayedActions();
			
			exchanges
				.stream()
				.forEach(stockExchange -> stockExchange.doClearing());
		}
	}

	private void processDelayedActions() {
		while (!orderExecutors.isEmpty()
			&& world.getCurrentTick() >= orderExecutors.peek().delayTick){
			
			DelayedOrderExecutor executor = orderExecutors.poll();
			executor.execute();
		}
	}

}
