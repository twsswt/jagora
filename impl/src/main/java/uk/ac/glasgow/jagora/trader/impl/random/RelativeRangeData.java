package uk.ac.glasgow.jagora.trader.impl.random;

import uk.ac.glasgow.jagora.Stock;

class RelativeRangeData extends RangeData {

	public final Long low, high;

	public RelativeRangeData(
			Stock stock, Integer minQuantity, Integer maxQuantity, Long lowPrice, Long highPrice){
			super(stock, minQuantity, maxQuantity);
			this.low = lowPrice;
			this.high = highPrice;

		}
}
