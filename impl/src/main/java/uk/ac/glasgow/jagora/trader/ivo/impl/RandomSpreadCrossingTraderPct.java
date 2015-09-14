package uk.ac.glasgow.jagora.trader.ivo.impl;


import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.random.RandomSpreadCrossingTrader;
import uk.ac.glasgow.jagora.trader.impl.random.RangeData;
import uk.ac.glasgow.jagora.trader.impl.random.SpreadCrossingRangeData;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class RandomSpreadCrossingTraderPct extends RandomSpreadCrossingTrader {

	protected static class TradeRangePct extends RangeData {
		private Double pricePct;

		public TradeRangePct(Stock stock, Integer minQuantity, Integer maxQuantity, Double pricePercentage) {
			super(stock, minQuantity, maxQuantity);
			this.pricePct = pricePercentage;
		}
	}

	private  final Map<Stock,TradeRangePct> tradeRangePcts;

	protected RandomSpreadCrossingTraderPct(String name, Long cash, Map<Stock, Integer> inventory,
										 Random random, Map<Stock,TradeRangePct> tradeRangePcts) {
		super(name, cash, inventory, random, new HashMap<Stock,SpreadCrossingRangeData>());
		this.tradeRangePcts = tradeRangePcts;
	}

	@Override
	protected Long createRandomPrice(Stock stock, Long basePrice, boolean isSell) {
		TradeRangePct tradeRange = tradeRangePcts.get(stock);
		Long randomPrice =
				(isSell?-1:1) * (long)(random.nextDouble() *  tradeRange.pricePct *basePrice) + basePrice;

		return max(randomPrice, 0l);
	}

	@Override
	protected Integer createRandomQuantity(Stock stock, Integer ceiling) {
		TradeRangePct stockData = tradeRangePcts.get(stock);

		Integer tradeQuantityRange = stockData.maxQuantity - stockData.minQuantity;

		return min(random.nextInt(tradeQuantityRange) + stockData.minQuantity, ceiling);
	}
}
