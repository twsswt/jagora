package uk.ac.glasgow.jagora.engine.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeViewProvider;
import uk.ac.glasgow.jagora.engine.impl.SerialRandomEngine.TraderViewSpecification;
import uk.ac.glasgow.jagora.engine.impl.delay.DelayViewProvider;
import uk.ac.glasgow.jagora.engine.impl.delay.DelayedExchangeLevel1View.DelayedOrderExecutor;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;

public class SerialRandomEngineBuilder {

	private World world;
	private Integer seed;

	private Set<StockExchange> stockExchanges;
	
	private Collection<TraderViewSpecification> traderViewSpecifications;
	
	private Queue<DelayedOrderExecutor> orderExecutorQueue;
	

	public SerialRandomEngineBuilder() {
		stockExchanges = new HashSet<StockExchange>();
		orderExecutorQueue = new PriorityQueue<DelayedOrderExecutor>();
		traderViewSpecifications =
			new ArrayList<TraderViewSpecification>();
	}
	
	public SerialRandomEngineBuilder setWorld(World world) {
		this.world = world;
		return this;
	}
	
	public SerialRandomEngineBuilder setSeed (Integer seed){
		this.seed = seed;
		return this;
	}
	
	public SerialRandomEngineBuilder addStockExchange(StockExchange stockExchange){
		stockExchanges.add(stockExchange);
		return this;
	}
	
	public SerialRandomEngineBuilder addTraderStockExchangeView(
		Trader trader, StockExchangeViewProvider stockExchangeViewProvider){
		
		TraderViewSpecification traderViewSpecification = 
			new TraderViewSpecification(trader, stockExchangeViewProvider);
		
		traderViewSpecifications.add(traderViewSpecification);
		return this;
	}
		
	public SerialRandomEngineBuilder addTradersStockExchangeView(
		Set<Trader> level1Traders,
		StockExchangeViewProvider stockExchangeViewProvider) {
	
		level1Traders
			.stream()
			.forEach(level1Trader -> addTraderStockExchangeView(level1Trader, stockExchangeViewProvider));
		
		return this;
	}
	
	public SerialRandomEngineBuilder addDelayedTraderView(
		Trader level1Trader, Long delay, StockExchange stockExchange) {

		DelayViewProvider delayedViewProvider = 
			new DelayViewProvider(stockExchange, delay, orderExecutorQueue);
		
		addTraderStockExchangeView(level1Trader, delayedViewProvider);
		
		return this;
	}

	public SerialRandomEngineBuilder addDelayedTradersView(
		Set<Trader> level1Traders, Long delay, StockExchange stockExchange) {
		
		level1Traders
			.stream()
			.forEach(level1Trader -> addDelayedTraderView(level1Trader, delay, stockExchange));
		
		return this;
	}
	
	public SerialRandomEngine build() {
		return new SerialRandomEngine(
			world,
			stockExchanges,
			traderViewSpecifications,
			orderExecutorQueue,
			new Random(seed));
	}

	
}
