package uk.ac.glasgow.jagora.test.stub;

import java.util.HashMap;
import java.util.Map;

import uk.ac.glasgow.jagora.Stock;

public class StubTraderBuilder {
	
	private String name;
	private Long cash;
	
	private Map<Stock, Integer> inventory;
		
	public StubTraderBuilder(String name){
		this.name = name;
		this.inventory = new HashMap<Stock,Integer>();
	}
	
	public StubTraderBuilder addStock(Stock stock, Integer quantity){
		inventory.put(stock, quantity);
		return this;
	}
	
	public StubTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public StubTraderBuilder setCash(Long cash){
		this.cash = cash;
		return this;
	}
	
	public StubTrader build(){
		return new StubTrader(name, cash, inventory);
	}
	
}
