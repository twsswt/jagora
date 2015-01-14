package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.Market;
import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.world.World;

public class ContinuousOrderDrivenMarketFactory implements MarketFactory {
	
	@Override
	public Market createOrderDrivenMarket(Stock stock, World world) {
		return new ContinuousOrderDrivenMarket(stock, world);
	}

}
