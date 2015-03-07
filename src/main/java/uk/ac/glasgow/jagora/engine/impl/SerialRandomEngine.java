package uk.ac.glasgow.jagora.engine.impl;

import java.util.HashSet;
import java.util.Set;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.engine.TradingEngine;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.util.Random;
import uk.ac.glasgow.jagora.world.World;

public class SerialRandomEngine implements TradingEngine {
	
	private final Set<StockExchange> exchanges;
	private final Set<Trader> traders;
	private final World world;
	private final Random random;
	
	public SerialRandomEngine (World world, Set<StockExchange> exchanges, Set<Trader> traders, Random random){
		this.world = world;
		this.exchanges = new HashSet<StockExchange>(exchanges);
		this.traders = new HashSet<Trader>(traders);
		this.random = random;
	}
	
	@Override
	public void run() {
		while (world.isAlive()) {
			StockExchange exchange = random.chooseElement(exchanges);
			StockExchangeTraderView traderView = exchange.createTraderStockExchangeView();
			Trader trader = random.chooseElement(traders);

			trader.speak(traderView);
			exchange.doClearing();
		}
	}

}
