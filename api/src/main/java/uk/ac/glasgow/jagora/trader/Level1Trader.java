package uk.ac.glasgow.jagora.trader;

import uk.ac.glasgow.jagora.StockExchangeLevel1View;

public interface Level1Trader extends Trader{
	/**
	 * Supplies this trader with an opportunity to speak on the stock exchange.
	 * Regulation of interaction with the stock exchange is implemented by the
	 * supplied trader view.
	 * 
	 * @param traderView
	 */
	public abstract void speak(StockExchangeLevel1View traderView);

	/**
	 *
	 * @return long value of decrease in delay for the particular trader
	 */
	Long getDelayDecrease();
}
