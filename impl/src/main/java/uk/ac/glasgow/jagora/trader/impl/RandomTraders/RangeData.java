package uk.ac.glasgow.jagora.trader.impl.RandomTraders;

import uk.ac.glasgow.jagora.Stock;

class RangeData {

    public final Stock stock;
		public final Long low, high;
		public final Double lowPct,highPct;
		public final Integer minQuantity, maxQuantity;

		public RangeData(Stock stock, Long lowPrice, Long highPrice,
                         Integer minQuantity, Integer maxQuantity,
                         Double lowPct, Double highPct){
			this.stock = stock;
			this.low = lowPrice;
			this.high = highPrice;
			this.minQuantity = minQuantity;
			this.maxQuantity = maxQuantity;
            this.lowPct = lowPct;
            this.highPct = highPct;
		}
}
