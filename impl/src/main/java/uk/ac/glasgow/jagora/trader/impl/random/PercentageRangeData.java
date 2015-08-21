package uk.ac.glasgow.jagora.trader.impl.random;

import uk.ac.glasgow.jagora.Stock;

class PercentageRangeData extends RangeData {

	public final Double low, high;

	public PercentageRangeData(
			Stock stock, Integer minQuantity, Integer maxQuantity, Double lowPricePercentage, Double highPrice){
			super(stock, minQuantity, maxQuantity);
			this.low = lowPricePercentage;
			this.high = highPrice;

		}
}
