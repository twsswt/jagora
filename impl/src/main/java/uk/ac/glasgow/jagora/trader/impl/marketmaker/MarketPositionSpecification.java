package uk.ac.glasgow.jagora.trader.impl.marketmaker;

import uk.ac.glasgow.jagora.Stock;

public class MarketPositionSpecification {
	public final Stock stock;
	public final Integer targetQuantity;
	public final Integer targetLiquidity;

	public MarketPositionSpecification(Stock stock,
		Integer targetQuantity, Integer targetLiquidity) {
		this.stock = stock;
		this.targetQuantity = targetQuantity;
		this.targetLiquidity = targetLiquidity;
	}
}