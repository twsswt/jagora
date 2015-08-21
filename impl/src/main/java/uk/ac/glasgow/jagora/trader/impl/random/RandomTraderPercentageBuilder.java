package uk.ac.glasgow.jagora.trader.impl.random;


import java.util.Map;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;
import uk.ac.glasgow.jagora.util.Random;

public class RandomTraderPercentageBuilder extends AbstractTraderBuilder {
		
	protected Integer seed;
	protected Map<Stock, PercentageRangeData> sellRangeData;
	protected Map<Stock, PercentageRangeData> buyRangeData;
	
	public RandomTraderPercentageBuilder() {
		super();
	}
	
	public RandomTraderPercentageBuilder setSellOrderRange(Stock stock, Integer minQuantity, Integer maxQuantity,
		Double sellLow, Double sellHigh) {
				
		sellRangeData.put(stock, new PercentageRangeData(stock, minQuantity, maxQuantity, sellLow, sellHigh));
		return this;
	}
	
	public RandomTraderPercentageBuilder setBuyOrderRange(
		Stock stock, Integer minQuantity, Integer maxQuantity, Double buyLow, Double buyHigh){
		
		buyRangeData.put(stock, new PercentageRangeData(stock, minQuantity, maxQuantity, buyLow, buyHigh));
		return this;
	}

	public RandomTraderPercentageBuilder setSeed(Integer seed) {
		this.seed = seed;
		return this;
	}

	@Override
	public RandomTraderPercentageBuilder addStock(Stock stock, Integer quantity) {
		super.addStock(stock,quantity);
		return this;
	}

	@Override
	public RandomTraderPercentageBuilder setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public RandomTraderPercentageBuilder setCash(Long cash) {
		super.setCash(cash);
		return this;
	}

	public RandomTraderPercentage build() {
		return new RandomTraderPercentage(getName(), getCash(), getInventory(), new Random(seed), sellRangeData, buyRangeData);
	}
}
