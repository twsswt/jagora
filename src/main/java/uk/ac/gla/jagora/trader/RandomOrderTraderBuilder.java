package uk.ac.gla.jagora.trader;

import java.util.HashMap;
import java.util.Map;

import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.trader.RandomOrderTrader.TradeRange;

public class RandomOrderTraderBuilder {
	private String name;
	private Double cash;
	
	private Map<Stock, Integer> inventory;
	
	private Integer seed;
	private Map<Stock, TradeRange> tradeRanges;
	
	public RandomOrderTraderBuilder(String name, Double cash, Integer seed){
		this.name = name;
		this.cash = cash;
		this.inventory = new HashMap<Stock,Integer>();
		this.seed = seed;
		tradeRanges = new HashMap<Stock,TradeRange>();
	}
	
	public RandomOrderTraderBuilder addStock(Stock stock, Integer quantity){
		inventory.put(stock, quantity);
		return this;
	}
	
	public RandomOrderTraderBuilder addTradeRange(
		Stock stock, Double low, Double high, Integer minQuantity, Integer maxQuantity){
		
		tradeRanges.put(stock, new TradeRange(low, high, minQuantity, maxQuantity));
		return this;
	}
	
	public RandomOrderTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public RandomOrderTraderBuilder setCash(Double cash){
		this.cash = cash;
		return this;
	}
	
	public RandomOrderTrader build(){
		return new RandomOrderTrader(name, cash, inventory, seed, tradeRanges);
	}}
