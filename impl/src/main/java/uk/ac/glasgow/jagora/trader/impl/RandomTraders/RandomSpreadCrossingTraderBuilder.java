package uk.ac.glasgow.jagora.trader.impl.RandomTraders;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.AbstractTraderBuilder;
import uk.ac.glasgow.jagora.trader.impl.RandomTraders.RandomSpreadCrossingTrader.TradeRange;
import uk.ac.glasgow.jagora.util.Random;

import java.util.HashMap;
import java.util.Map;

public class RandomSpreadCrossingTraderBuilder extends AbstractTraderBuilder {

	private Map<Stock, TradeRange> tradeRanges;
	
	private Integer seed;
	
	public RandomSpreadCrossingTraderBuilder(){
		super ();
		tradeRanges = new HashMap<Stock,TradeRange>();
	}
	
	@Override
	public RandomSpreadCrossingTraderBuilder setName(String name){
		super.setName(name);
		return this;
	}
	
	@Override
	public RandomSpreadCrossingTraderBuilder setCash(Long cash){
		super.setCash(cash);
		return this;
	}
	
	public RandomSpreadCrossingTraderBuilder setSeed(Integer seed) {
		this.seed = seed;
		return this;
	}

	
	public RandomSpreadCrossingTraderBuilder addTradeRange(
		Stock stock, Integer minQuantity, Integer maxQuantity, Long price){
		
		tradeRanges.put(stock, new TradeRange(minQuantity, maxQuantity, price));
		return this;
	}
		
	public RandomSpreadCrossingTrader build(){
		return new RandomSpreadCrossingTrader(
			getName(), getCash(), getInventory(), new Random(seed), tradeRanges);
	}
	
	@Override
	public RandomSpreadCrossingTraderBuilder addStock (Stock stock, Integer quantity){
		super.addStock(stock, quantity);
		return this;
	}
}
