package uk.ac.glasgow.jagora.trader.impl;

import java.util.HashMap;
import java.util.Map;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.RandomTrader.RangeData;
import uk.ac.glasgow.jagora.util.Random;

public class RandomTraderBuilder {
	private String name;
	private Long cash;
	
	private Map<Stock, Integer> inventory;
	
	private Integer seed;
	private Map<Stock, RangeData> sellRangeData;
	private Map<Stock, RangeData> buyRangeData;
	
	public RandomTraderBuilder(){
		this.inventory = new HashMap<Stock,Integer>();
		sellRangeData = new HashMap<Stock,RangeData>();
		buyRangeData = new HashMap<Stock,RangeData>();

	}
	
	public RandomTraderBuilder addStock(Stock stock, Integer quantity){
		inventory.put(stock, quantity);
		return this;
	}
	
	public RandomTraderBuilder setSellOrderRange(
		Stock stock, Integer minQuantity, Integer maxQuantity, Long sellLowVariance, Long sellHighVariance){
		
		sellRangeData.put(stock, new RangeData(stock, sellLowVariance, sellHighVariance, minQuantity, maxQuantity));
		return this;
	}
	
	public RandomTraderBuilder setBuyOrderRange(
		Stock stock, Integer minQuantity, Integer maxQuantity, Long buyLowVariance, Long buyHighVariance){
		
		buyRangeData.put(stock, new RangeData(stock, buyLowVariance, buyHighVariance, minQuantity, maxQuantity));
		return this;
	}
	
	public RandomTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
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
