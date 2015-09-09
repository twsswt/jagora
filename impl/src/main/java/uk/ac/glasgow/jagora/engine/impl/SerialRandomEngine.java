package uk.ac.glasgow.jagora.engine.impl;

import java.util.HashSet;
import java.util.Set;

import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;

public class SerialRandomEngine implements TradingEngine {
	
	private final Set<StockExchange> exchanges;
	private final World world;
	private final Random random;
	
	private final Set<Level1Trader> traders;

	
	public SerialRandomEngine (World world, Set<StockExchange> exchanges, Set<Level1Trader> traders, Random random){
		this.world = world;
		this.exchanges = new HashSet<StockExchange>(exchanges);
		this.traders = new HashSet<Level1Trader>(traders);
		this.random = random;
	}
	
	@Override
	public void run() {
		while (world.isAlive()) {
			StockExchange exchange = random.chooseElement(exchanges);
			StockExchangeLevel1View traderView = exchange.createLevel1View();
			Level1Trader trader = random.chooseElement(traders);

			trader.speak(traderView);
			exchange.doClearing();
		}
	}

}
