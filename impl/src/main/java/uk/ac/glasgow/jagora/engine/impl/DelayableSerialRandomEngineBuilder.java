package uk.ac.glasgow.jagora.engine.impl;

import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;

import java.util.HashSet;
import java.util.Set;

public class DelayableSerialRandomEngineBuilder {

	private World world;
	private Integer seed;

	private Set<StockExchange> stockExchanges;
	private Set<Level1Trader> traders;
	private Long standardDelay = 100l;
	private Set<Level2Trader> privilegedTraders = new HashSet<>();

	public DelayableSerialRandomEngineBuilder() {
		stockExchanges = new HashSet<StockExchange>();
		traders = new HashSet<Level1Trader>();
	}
	
	public DelayableSerialRandomEngineBuilder setWorld(World world) {
		this.world = world;
		return this;
	}
	
	public DelayableSerialRandomEngineBuilder setSeed (Integer seed){
		this.seed = seed;
		return this;
	}
	
	public DelayableSerialRandomEngineBuilder addStockExchange(StockExchange stockExchange){
		stockExchanges.add(stockExchange);
		return this;
	}
	
	public DelayableSerialRandomEngineBuilder addTrader(Level1Trader trader){
		traders.add(trader);
		return this;
	}
	
	public DelayableSerialRandomEngineBuilder addTraders(Set<Level1Trader> traders){
		this.traders.addAll(traders);
		return this;
	}
	
	public DelayableSerialRandomEngineBuilder addPrivilegedTrader(Level2Trader trader){
		privilegedTraders.add(trader);
		return this;
	}

	public DelayableSerialRandomEngineBuilder addPrivilegedTraders(Set<Level2Trader> privilegedTraders) {
		this.privilegedTraders.addAll(privilegedTraders);
		return this;
	}

	public DelayableSerialRandomEngineBuilder setStandardDelay(Long delay){
		this.standardDelay = delay;
		return this;
	}

	public SerialDelayEngine build() {
		return new SerialDelayEngine(
				world, stockExchanges, traders, new Random(seed), standardDelay, privilegedTraders);
	}



	
}
