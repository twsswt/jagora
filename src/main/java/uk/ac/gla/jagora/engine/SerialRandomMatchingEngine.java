package uk.ac.gla.jagora.engine;

import java.util.HashSet;
import java.util.Set;

import uk.ac.gla.jagora.Market;
import uk.ac.gla.jagora.MatchingEngine;
import uk.ac.gla.jagora.Trader;
import uk.ac.gla.jagora.TraderMarketView;
import uk.ac.gla.jagora.World;
import uk.ac.gla.jagora.util.Random;

public class SerialRandomMatchingEngine implements MatchingEngine {
	
	private final Set<Market> markets;
	private final Set<Trader> traders;
	private final World world;
	private final Random random;
	
	public SerialRandomMatchingEngine (World world, Set<Market> markets, Set<Trader> traders, Random random){
		this.world = world;
		this.markets = new HashSet<Market>(markets);
		this.traders = new HashSet<Trader>(traders);
		this.random = random;
	}
	
	@Override
	public void run() {
		while (world.isAlive()) {
			Market market = random.chooseElement(markets);
			TraderMarketView traderMarket = market.createTraderMarketView();
			Trader trader = random.chooseElement(traders);
			trader.speak(traderMarket);
			market.doClearing();
		}
	}

}
