package uk.ac.glasgow.jagora.engine.impl;

import java.util.HashSet;
import java.util.Set;

import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.engine.ExecutionEngine;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;

public class SerialRandomEngine implements ExecutionEngine {
	
	private final Set<StockExchange> markets;
	private final Set<Trader> traders;
	private final World world;
	private final Random random;
	
	public SerialRandomEngine (World world, Set<StockExchange> markets, Set<Trader> traders, Random random){
		this.world = world;
		this.markets = new HashSet<StockExchange>(markets);
		this.traders = new HashSet<Trader>(traders);
		this.random = random;
	}
	
	@Override
	public void run() {
		while (world.isAlive()) {
			StockExchange exchange = random.chooseElement(markets);
			StockExchangeTraderView traderView = exchange.createTraderStockExchangeView();
			Trader trader = random.chooseElement(traders);
			trader.speak(traderView);
			exchange.doClearing();
		}
	}

}
