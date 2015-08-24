package uk.ac.glasgow.jagora.trader;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;

public interface StopLossOrder {
	
	public void executeOrder(StockExchangeLevel1View traderView);
	
	public Boolean priceThresholdCrossed(Long marketPrice);

	public Stock getStock();

}
