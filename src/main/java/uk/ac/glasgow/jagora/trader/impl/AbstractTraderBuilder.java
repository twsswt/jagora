package uk.ac.glasgow.jagora.trader.impl;

import java.util.HashMap;
import java.util.Map;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.Trader;

public abstract class AbstractTraderBuilder {

	private String name;
	private Double cash;
	
	private Map<Stock, Integer> inventory;
	
	private Integer seed;
	
	public AbstractTraderBuilder(String name, Double cash, Integer seed) {
		this.name = name;
		this.cash = cash;
		this.inventory = new HashMap<Stock,Integer>();
		this.seed = seed;
	}

	public AbstractTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getName (){
		return name;
	}
	
	public Integer getSeed (){
		return seed;
	}
	
	public AbstractTraderBuilder setCash(Double cash){
		this.cash = cash;
		return this;
	}
	
	public Double getCash (){
		return cash;
	}
	
	public AbstractTraderBuilder addStock(Stock stock, Integer quantity){
		inventory.put(stock, quantity);
		return this;
	}
	
	public Map<Stock,Integer> getInventory (){
		return inventory;
	}

}
