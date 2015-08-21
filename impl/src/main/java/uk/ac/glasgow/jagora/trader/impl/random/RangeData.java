package uk.ac.glasgow.jagora.trader.impl.random;

import uk.ac.glasgow.jagora.Stock;

public class RangeData {

	public final Stock stock;
	public final Integer minQuantity;
	public final Integer maxQuantity;

	public RangeData(Stock stock, Integer minQuantity, Integer maxQuantity) {
		this.stock = stock;
		this.minQuantity = minQuantity;
		this.maxQuantity = maxQuantity;
	}

}