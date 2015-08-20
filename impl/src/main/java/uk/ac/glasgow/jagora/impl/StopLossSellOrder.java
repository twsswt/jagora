package uk.ac.glasgow.jagora.impl;

import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.trader.Trader;

/**
 * 
 * @author Ivelin
 *
 */
public class StopLossSellOrder implements TradeListener {
	
	private final Trader trader;
	private final StockExchangeLevel1View stockExchangeLevel1View;

	private final Long stopPrice;
	private final Stock stock;
	private final Integer quantity;

	private Boolean orderExecuted = false;

	public StopLossSellOrder(
		Trader trader, StockExchangeLevel1View stockExchangeLevel1View, Long stopPrice, Stock stock, Integer quantity) {
		this.trader = trader;
		this.stockExchangeLevel1View = stockExchangeLevel1View;
		this.stopPrice = stopPrice;
		this.stock = stock;
		this.quantity = quantity;
		;
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		Long eventPrice = tradeExecutionEvent.price;
		
		if (!orderExecuted && eventPrice <= stopPrice){
			SellOrder sellOrder = 
				new MarketSellOrder(trader, stock, quantity, stockExchangeLevel1View);
			
			stockExchangeLevel1View.placeSellOrder(sellOrder);
			
			orderExecuted = true;

		}

	}

}
