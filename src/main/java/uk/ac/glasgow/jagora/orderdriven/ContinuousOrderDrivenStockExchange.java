package uk.ac.glasgow.jagora.orderdriven;

import java.util.List;

import uk.ac.glasgow.jagora.ExecutedTrade;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;

/**
 * 
 * @author Tim
 *
 */
public interface ContinuousOrderDrivenStockExchange extends StockExchange {

	@Override
	public abstract ContinuousOrderDrivenStockExchangeTraderView createTraderStockExchangeView();

	public abstract List<ExecutedTrade> getTradeHistory(Stock stocks);

}