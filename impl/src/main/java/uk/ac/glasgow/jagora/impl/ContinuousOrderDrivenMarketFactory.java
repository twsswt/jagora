package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.Market;
import uk.ac.glasgow.jagora.MarketFactory;
import uk.ac.glasgow.jagora.StockWarehouse;
import uk.ac.glasgow.jagora.pricer.TradePricer;
import uk.ac.glasgow.jagora.world.World;

public class ContinuousOrderDrivenMarketFactory implements MarketFactory {
	
	private final TradePricer tradePricer;
	
	public ContinuousOrderDrivenMarketFactory(TradePricer tradePricer){
		this.tradePricer = tradePricer;
	}
	
	@Override
	public Market createOrderDrivenMarket(StockWarehouse stockWarehouse, World world) {
		return new ContinuousOrderDrivenMarket(stockWarehouse, world, tradePricer);
	}

}
