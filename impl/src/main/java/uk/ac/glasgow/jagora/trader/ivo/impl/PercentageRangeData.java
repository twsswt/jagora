package uk.ac.glasgow.jagora.trader.ivo.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.random.RangeData;

class PercentageRangeData extends RangeData {

	public final Double low, high;

	public PercentageRangeData(
			Stock stock, Integer minQuantity, Integer maxQuantity, Double lowPricePercentage, Double highPrice){
			super(stock, minQuantity, maxQuantity);
			this.low = lowPricePercentage;
			this.high = highPrice;

		}
}
