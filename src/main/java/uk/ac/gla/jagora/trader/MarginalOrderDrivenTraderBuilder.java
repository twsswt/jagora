package uk.ac.gla.jagora.trader;

import java.util.HashMap;
import java.util.Map;

import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.util.Random;


public class MarginalOrderDrivenTraderBuilder  {

	private String name;
	private Double cash;
	
	private Map<Stock, Integer> inventory;
	
	private Integer seed;
	
	public MarginalOrderDrivenTraderBuilder(String name, Double cash, Integer seed){
		this.name = name;
		this.cash = cash;
		this.inventory = new HashMap<Stock,Integer>();
		this.seed = seed;
	}
	
	public MarginalOrderDrivenTraderBuilder addStock(Stock stock, Integer quantity){
		inventory.put(stock, quantity);
		return this;
	}
	
	public MarginalOrderDrivenTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public MarginalOrderDrivenTraderBuilder setCash(Double cash){
		this.cash = cash;
		return this;
	}
	
	public MarginalOrderDrivenTrader build(){
		return new MarginalOrderDrivenTrader(name, cash, inventory, new Random(seed));
	}

}
