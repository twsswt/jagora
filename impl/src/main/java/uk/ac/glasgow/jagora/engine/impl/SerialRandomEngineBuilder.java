package uk.ac.glasgow.jagora.engine.impl;

import java.util.HashSet;
import java.util.Set;

import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;

public class SerialRandomEngineBuilder {

	private World world;
	private Set<StockExchange> stockExchanges;
	private Set<Level1Trader> traders;
	private Integer seed;
	private Long standardDelay = 100l;
	private Set<Level2Trader> privilegedTraders;

	public SerialRandomEngineBuilder(World world, Integer seed) {
		this.world = world;
		this.seed = seed;
		stockExchanges = new HashSet<StockExchange>();
		traders = new HashSet<Level1Trader>();
	}
	
	public SerialRandomEngineBuilder addStockExchange(StockExchange stockExchange){
		stockExchanges.add(stockExchange);
		return this;
	}
	
	public SerialRandomEngineBuilder addTrader(Level1Trader trader){
		traders.add(trader);
		return this;
	}
	
	public SerialRandomEngineBuilder addTraders(Set<Level1Trader> traders){
		this.traders.addAll(traders);
		return this;
	}

	public SerialRandomEngineBuilder addPrivilegedTrader(Level2Trader trader){
		privilegedTraders.add(trader);
		return this;
	}

	public SerialRandomEngineBuilder addPrivilegedTraders(Set<Level2Trader> privilegedTraders) {
		this.privilegedTraders.addAll(privilegedTraders);
		return this;
	}

	public SerialRandomEngineBuilder setStandartDelay(Long delay){
		this.standardDelay = delay;
		return this;
	}

	public SerialRandomEngine build() {
		return new SerialRandomEngine(world, stockExchanges, traders, new Random(seed),standardDelay);
	}

	public SerialRandomEngine buildNew() {
		return new SerialRandomEngine(world, stockExchanges, traders, new Random(seed),standardDelay,privilegedTraders);
	}
	
	
}
