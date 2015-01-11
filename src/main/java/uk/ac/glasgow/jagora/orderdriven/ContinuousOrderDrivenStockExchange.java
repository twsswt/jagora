package uk.ac.glasgow.jagora.orderdriven;

import java.util.List;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.TickEvent;
import uk.ac.glasgow.jagora.Trade;

/**
 * 
 * @author Tim
 *
 */
public interface ContinuousOrderDrivenStockExchange extends StockExchange {

	@Override
	public abstract ContinuousOrderDrivenStockExchangeTraderView createTraderStockExchangeView();

	public abstract List<TickEvent<Trade>> getTradeHistory(Stock stocks);

}