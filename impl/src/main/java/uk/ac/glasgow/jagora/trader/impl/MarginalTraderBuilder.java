package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.util.Random;

import java.util.HashMap;
import java.util.Map;


public class MarginalTraderBuilder  {

	private String name;
	private Long cash;
	
	private Map<Stock, Integer> inventory;
	
	private Integer seed;
	
	public MarginalTraderBuilder(String name, Long cash, Integer seed){
		this.name = name;
		this.cash = cash;
		this.inventory = new HashMap<Stock,Integer>();
		this.seed = seed;
	}
	
	public MarginalTraderBuilder addStock(Stock stock, Integer quantity){
		inventory.put(stock, quantity);
		return this;
	}
	
	public MarginalTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public MarginalTraderBuilder setCash(Long cash){
		this.cash = cash;
		return this;
	}
	
	public MarginalTrader build(){
		return new MarginalTrader(name, cash, inventory, new Random(seed));
	}

}
