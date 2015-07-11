package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.world.World;

public interface MarketFactory {
	Market createOrderDrivenMarket(StockWarehouse stockWarehouse, World world);
}
