package uk.ac.glasgow.jagora.trader.impl.marketmaker;


import java.util.HashSet;
import java.util.Set;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;

public class MarketMakerBuilder extends AbstractTraderBuilder{
	
	private Set<MarketPositionSpecification> marketPositionSpecifications;


	public MarketMakerBuilder () {
		marketPositionSpecifications = new HashSet<MarketPositionSpecification>();
	}

	@Override
	public MarketMakerBuilder setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public MarketMakerBuilder setCash(Long cash) {
		super.setCash(cash);
		return this;
	}

	public MarketMakerBuilder addStock(Stock stock, Integer quantity) {
		super.addStock(stock, quantity);
		return this;
	}

	public MarketMakerBuilder addMarketPositionSpecification(
		Stock stock, Integer targetQuantity, Integer targetLiquidity){
		marketPositionSpecifications.add(
			new MarketPositionSpecification(stock, targetQuantity, targetLiquidity));
		return this;
	}

	public MarketMaker build() {
		return new MarketMaker(
			super.getName(),
			super.getCash(),
			super.getInventory(),
			marketPositionSpecifications);
	}
}
