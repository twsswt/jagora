package uk.ac.glasgow.jagora.trader.impl;

import java.util.HashMap;
import java.util.Map;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.RandomSpreadCrossingTrader.TradeRange;
import uk.ac.glasgow.jagora.util.Random;

public class RandomSpreadCrossingTraderBuilder {
	private String name;
	private Double cash;
	
	private Map<Stock, Integer> inventory;
	
	private Integer seed;
	private Map<Stock, TradeRange> tradeRanges;
	
	public RandomSpreadCrossingTraderBuilder(String name, Double cash, Integer seed){
		this.name = name;
		this.cash = cash;
		this.inventory = new HashMap<Stock,Integer>();
		this.seed = seed;
		tradeRanges = new HashMap<Stock,TradeRange>();
	}
	
	public RandomSpreadCrossingTraderBuilder addStock(Stock stock, Integer quantity){
		inventory.put(stock, quantity);
		return this;
	}
	
	public RandomSpreadCrossingTraderBuilder addTradeRange(
		Stock stock, Integer quantity, Double price){
		
		tradeRanges.put(stock, new TradeRange(quantity, price));
		return this;
	}
	
	public RandomSpreadCrossingTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public RandomSpreadCrossingTraderBuilder setCash(Double cash){
		this.cash = cash;
		return this;
	}
	
	public RandomSpreadCrossingTrader build(){
		return new RandomSpreadCrossingTrader(name, cash, inventory, new Random(seed), tradeRanges);
	}
}
