package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.Market;
import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.pricer.LimitOrderTradePricer;
import uk.ac.glasgow.jagora.world.World;

public class ContinuousOrderDrivenMarketFactory implements MarketFactory {
	
	private final LimitOrderTradePricer limitOrderTradePricer;
	
	public ContinuousOrderDrivenMarketFactory(LimitOrderTradePricer limitOrderTradePricer){
		this.limitOrderTradePricer = limitOrderTradePricer;
	}
	
	@Override
	public Market createOrderDrivenMarket(Stock stock, World world) {
		return new ContinuousOrderDrivenMarket(stock, world, limitOrderTradePricer);
	}

}
