package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Stock;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTraderBuilder {

	private String name;
	private Long cash;
	
	private Map<Stock, Integer> inventory;
		
	public AbstractTraderBuilder() {	
		this.inventory = new HashMap<Stock,Integer>();
	}

	public AbstractTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getName (){
		return name;
	}
	
	public AbstractTraderBuilder setCash(Long cash){
		this.cash = cash;
		return this;
	}
	
	public Long getCash (){
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
