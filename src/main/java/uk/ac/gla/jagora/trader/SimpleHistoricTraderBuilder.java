package uk.ac.gla.jagora.trader;

import java.util.HashMap;
import java.util.Map;

import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.util.Random;

public class SimpleHistoricTraderBuilder {
	
	private String name;
	private Double cash;
	private Integer seed;
	
	private Map<Stock, Integer> inventory;
		
	public SimpleHistoricTraderBuilder(String name, Double cash, Integer seed){
		this.name = name;
		this.cash = cash;
		this.seed = seed;
		this.inventory = new HashMap<Stock,Integer>();
	}
	
	public SimpleHistoricTraderBuilder addStock(Stock stock, Integer quantity){
		inventory.put(stock, quantity);
		return this;
	}
	
	public SimpleHistoricTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public SimpleHistoricTraderBuilder setCash(Double cash){
		this.cash = cash;
		return this;
	}
	
	public SimpleHistoricTraderBuilder setSeed(Integer seed){
		this.seed = seed;
		return this;
	}
	
	public SimpleHistoricTrader build(){
		return 
			new SimpleHistoricTrader(
				name, cash, inventory, new Random(seed));
	}
	
}
