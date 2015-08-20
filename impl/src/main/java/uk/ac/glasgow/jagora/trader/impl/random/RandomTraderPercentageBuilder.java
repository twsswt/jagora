package uk.ac.glasgow.jagora.trader.impl.random;


import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.util.Random;

public class RandomTraderPercentageBuilder extends RandomTraderBuilder {

	public RandomTraderPercentageBuilder() {
		super();
	}
	
	public RandomTraderBuilder setSellOrderRange(
		Stock stock, Integer minQuantity, Integer maxQuantity,
		Long sellLow, Long sellHigh) {
				
		sellRangeData.put(stock, new RangeData(stock, sellLow, sellHigh, minQuantity, maxQuantity));
		return this;
	}
	
	public RandomTraderBuilder setBuyOrderRange(
		Stock stock, Integer minQuantity, Integer maxQuantity, Long buyLow, Long buyHigh){
		
		buyRangeData.put(stock, new RangeData(stock, buyLow, buyHigh, minQuantity, maxQuantity));
		return this;
	}

	@Override
	public RandomTraderPercentageBuilder setSeed(Integer seed) {
		this.seed = seed;
		return this;
	}

	@Override
	public RandomTraderPercentageBuilder addStock(Stock stock, Integer quantity) {
		inventory.put(stock,quantity);
		return this;
	}

	@Override
	public RandomTraderPercentageBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public RandomTraderPercentageBuilder setCash(Long cash) {
		this.cash = cash;
		return this;
	}

	@Override
	public RandomTraderPercentage build() {
		return new RandomTraderPercentage(name, cash, inventory, new Random(seed), sellRangeData, buyRangeData);
	}
}
