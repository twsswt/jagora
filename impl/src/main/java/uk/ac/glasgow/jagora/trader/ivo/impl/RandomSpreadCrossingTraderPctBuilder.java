package uk.ac.glasgow.jagora.trader.ivo.impl;


import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTraderBuilder;
import uk.ac.glasgow.jagora.trader.ivo.impl.RandomSpreadCrossingTraderPct.TradeRangePct;
import uk.ac.glasgow.jagora.util.Random;

import java.util.HashMap;
import java.util.Map;

public class RandomSpreadCrossingTraderPctBuilder extends  RandomSpreadCrossingTraderBuilder {

	private Map<Stock, TradeRangePct> tradeRangesPct = new HashMap<>();

	public RandomSpreadCrossingTraderPctBuilder() {
		super();
	}

	@Override
	public RandomSpreadCrossingTraderPctBuilder setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public RandomSpreadCrossingTraderPctBuilder setCash (Long cash) {
		super.setCash(cash);
		return this;
	}

	@Override
	public RandomSpreadCrossingTraderPctBuilder setSeed(Integer seed) {
		super.setSeed(seed);
		return this;
	}

	@Override
	public RandomSpreadCrossingTraderPctBuilder addStock (Stock stock, Integer quantity){
		
		super.addStock(stock,quantity);
		return this;
	}

	public RandomSpreadCrossingTraderPctBuilder addTradeRangePct(
		Stock stock, Integer minQuantity, Integer maxQuantity,Double pricePct) {

		tradeRangesPct.put(stock, new TradeRangePct(stock, minQuantity, maxQuantity, pricePct) );
		return this;
	}

	@Override
	public RandomSpreadCrossingTraderPct build() {
		return
			new RandomSpreadCrossingTraderPct(
				getName(),getCash(),
				getInventory(),
				new Random(seed),
				tradeRangesPct);
	}
}
