package uk.ac.glasgow.jagora.trader.impl.RandomTraders;

import java.util.HashMap;
import java.util.Map;


import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;


import uk.ac.glasgow.jagora.util.Random;

public class RandomTraderBuilder extends AbstractTraderBuilder {
	protected String name;
	protected Long cash;

	protected Double lowHighPct = 0.0; // just used for initialising the RangeData
	
	protected Map<Stock, Integer> inventory;
	
	protected Integer seed;
	protected Map<Stock, RangeData> sellRangeData;
	protected Map<Stock, RangeData> buyRangeData;
	
	public RandomTraderBuilder(){
		this.inventory = new HashMap<Stock,Integer>();
		sellRangeData = new HashMap<Stock,RangeData>();
		buyRangeData = new HashMap<Stock,RangeData>();

	}
	@Override
	public RandomTraderBuilder addStock(Stock stock, Integer quantity){
		inventory.put(stock, quantity);
		return this;
	}

	/**
	 * setTradeRange of a stock
	 * @param stock
	 * @param minQuantity
	 * @param maxQuantity
	 * @param sellLow needs to be a negative (meaning low end of the margin)
	 * @param sellHigh positive
	 * @param buyLow negative
	 * @param buyHigh positive
	 * @return
	 */
	public RandomTraderBuilder setTradeRange(
		Stock stock, Integer minQuantity, Integer maxQuantity,
		Long sellLow, Long sellHigh, Long buyLow, Long buyHigh){
		
		sellRangeData.put(stock, new RangeData(stock, sellLow, sellHigh, minQuantity, maxQuantity,lowHighPct,lowHighPct));
		buyRangeData.put(stock, new RangeData(stock, buyLow, buyHigh, minQuantity, maxQuantity,lowHighPct,lowHighPct));
		return this;
	}
	@Override
	public RandomTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}
	@Override
	public RandomTraderBuilder setCash(Long cash){
		this.cash = cash;
		return this;
	}
	
	public RandomTrader build(){
		return new RandomTrader(name, cash, inventory, new Random(seed), sellRangeData, buyRangeData);
	}

	public RandomTraderBuilder setSeed(Integer seed) {
		this.seed = seed;
		return this;
	}


}
