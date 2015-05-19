package uk.ac.glasgow.jagora.trader;

import uk.ac.glasgow.jagora.StockExchangeLevel2View;

public interface Level2Trader extends Level1Trader {
	public void speak(StockExchangeLevel2View level2View);
}
