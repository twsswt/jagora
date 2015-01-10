package uk.ac.glasgow.jagora.engine;

import java.util.HashSet;
import java.util.Set;

import uk.ac.glasgow.jagora.ExecutionEngine;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.Trader;
import uk.ac.glasgow.jagora.World;
import uk.ac.glasgow.jagora.util.Random;

public class SerialRandomMatchingEngine implements ExecutionEngine {
	
	private final Set<StockExchange> markets;
	private final Set<Trader> traders;
	private final World world;
	private final Random random;
	
	public SerialRandomMatchingEngine (World world, Set<StockExchange> markets, Set<Trader> traders, Random random){
		this.world = world;
		this.markets = new HashSet<StockExchange>(markets);
		this.traders = new HashSet<Trader>(traders);
		this.random = random;
	}
	
	@Override
	public void run() {
		while (world.isAlive()) {
			StockExchange market = random.chooseElement(markets);
			StockExchangeTraderView traderMarket = market.createTraderStockExchangeView();
			Trader trader = random.chooseElement(traders);
			trader.speak(traderMarket);
			market.doClearing();
		}
	}

}
