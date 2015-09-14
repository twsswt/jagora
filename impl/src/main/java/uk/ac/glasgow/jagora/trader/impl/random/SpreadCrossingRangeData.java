package uk.ac.glasgow.jagora.trader.impl.random;

import uk.ac.glasgow.jagora.Stock;

public class SpreadCrossingRangeData extends RangeData {

	public final Long priceRange;
	
	public SpreadCrossingRangeData(
		Stock stock, Integer minQuantity, Integer maxQuantity, Long priceRange){
		
		super (stock, minQuantity, maxQuantity);
		this.priceRange = priceRange;
	}
}