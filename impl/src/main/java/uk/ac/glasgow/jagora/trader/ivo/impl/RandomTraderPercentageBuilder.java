package uk.ac.glasgow.jagora.trader.ivo.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;

public class RandomTraderPercentageBuilder extends AbstractTraderBuilder {
		
	private Integer seed;
	private Map<Stock, PercentageRangeData> sellRangeData;
	private Map<Stock, PercentageRangeData> buyRangeData;
	
	public RandomTraderPercentageBuilder() {
		super();
		sellRangeData = new HashMap<Stock, PercentageRangeData>();
		buyRangeData = new HashMap<Stock, PercentageRangeData>();
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
