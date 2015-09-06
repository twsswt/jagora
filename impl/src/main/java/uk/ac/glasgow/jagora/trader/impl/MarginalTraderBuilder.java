package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.util.Random;


public class MarginalTraderBuilder extends AbstractTraderBuilder {
	
	private Integer seed;
	
	public MarginalTraderBuilder(){
	}
	
	public MarginalTraderBuilder addStock(Stock stock, Integer quantity){
		super.addStock(stock, quantity);
		return this;
	}
	
	@Override
	public MarginalTraderBuilder setName(String name) {
		super.setName(name);
		return this;
	}
	
	@Override
	public MarginalTraderBuilder setCash(Long cash){
		super.setCash(cash);
		return this;
	}
	
	public MarginalTraderBuilder setSeed(Integer seed){
		this.seed = seed;
		return this;
	}
	
	public MarginalTrader build(){
		return new MarginalTrader(getName(), getCash(), getInventory(), new Random(seed));
	}

}
