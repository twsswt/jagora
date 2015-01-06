package uk.ac.gla.jagora;

import static uk.ac.gla.jagora.util.RandomChoice.chooseRandomElement;

import java.util.HashSet;
import java.util.Set;

public class SerialRandomMatchingEngine implements MatchingEngine {
	
	private Set<Market> markets;
	private Set<AbstractTrader> traders;
	private World world;
	
	public SerialRandomMatchingEngine (World world, Set<Market> markets, Set<AbstractTrader> traders){
		this.world = world;
		this.markets = new HashSet<Market>(markets);
		this.traders = new HashSet<AbstractTrader>(traders);
	}
	
	@Override
	public void run() {
		while (world.isAlive()) {
			Market market = chooseRandomElement(markets);
			TraderMarketView traderMarket = market.createTraderMarket();
			Trader trader = chooseRandomElement(traders);
			trader.speak(traderMarket);
			market.doClearing();
		}
	}

}
