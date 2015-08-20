package uk.ac.glasgow.jagora.trader.impl.random;

import uk.ac.glasgow.jagora.Stock;

class RangeData {

	public final Stock stock;
		public final Long low, high;
		public final Integer minQuantity, maxQuantity;

		public RangeData(
			Stock stock, Long lowPrice, Long highPrice, Integer minQuantity, Integer maxQuantity){
			this.stock = stock;
			this.low = lowPrice;
			this.high = highPrice;
			this.minQuantity = minQuantity;
			this.maxQuantity = maxQuantity;
		}
}
