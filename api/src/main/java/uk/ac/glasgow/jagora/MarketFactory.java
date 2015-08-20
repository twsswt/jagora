package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.world.World;

public interface MarketFactory {
	public Market createOrderDrivenMarket(Stock stock, World world);
}
