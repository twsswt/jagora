package uk.ac.glasgow.jagora.trader.impl;

import java.util.HashMap;
import java.util.Map;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.RandomSpreadCrossingTrader.TradeRange;
import uk.ac.glasgow.jagora.util.Random;

public class RandomSpreadCrossingTraderBuilder extends AbstractTraderBuilder {

	private Map<Stock, TradeRange> tradeRanges;
	
	public RandomSpreadCrossingTraderBuilder(String name, Double cash, Integer seed){
		super (name, cash, seed);
		tradeRanges = new HashMap<Stock,TradeRange>();
	}
	
	public RandomSpreadCrossingTraderBuilder addTradeRange(
		Stock stock, Integer minQuantity, int maxQuantity, Double price){
		
		tradeRanges.put(stock, new TradeRange(minQuantity, maxQuantity, price));
		return this;
	}
		
	public RandomSpreadCrossingTrader build(){
		return new RandomSpreadCrossingTrader(
			getName(), getCash(), getInventory(), new Random(getSeed()), tradeRanges);
	}
	
	@Override
	public RandomSpreadCrossingTraderBuilder addStock (Stock stock, Integer quantity){
		super.addStock(stock, quantity);
		return this;
	}
}
