package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.MarketBuyOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.trader.StopLossOrder;
import uk.ac.glasgow.jagora.trader.Trader;

/**
 * @author Ivelin
 * @author tws
 *
 */
public class StopLossBuyOrder implements StopLossOrder {

	private final Trader trader;

	private final Long stopPrice;
	private final Stock stock;
	private final Integer quantity;

	private Boolean orderExecuted = false;

	public StopLossBuyOrder(
		Trader trader, Long stopPrice, Stock stock, Integer quantity) {
		this.trader = trader;
		this.stopPrice = stopPrice;
		this.stock = stock;
		this.quantity = quantity;
		;
	}

	@Override
	public void executeOrder(StockExchangeLevel1View traderView) {
		if (!orderExecuted){
			MarketBuyOrder marketBuyOrder = 
				new DefaultMarketBuyOrder(trader, stock, quantity);
		
			traderView.placeMarketBuyOrder(marketBuyOrder);
			
			orderExecuted = true;
		}
	}

	@Override
	public Boolean priceThresholdCrossed(Long marketPrice) {		
		return marketPrice >= stopPrice;
	}

	@Override
	public Stock getStock() {
		return stock;
	}

}
