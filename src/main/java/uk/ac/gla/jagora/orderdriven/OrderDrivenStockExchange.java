package uk.ac.gla.jagora.orderdriven;

import java.util.List;

import uk.ac.gla.jagora.ExecutedTrade;
import uk.ac.gla.jagora.StockExchange;
import uk.ac.gla.jagora.Stock;

public interface OrderDrivenStockExchange extends StockExchange {

	@Override
	public abstract OrderDrivenStockExchangeTraderView createTraderStockExchangeView();

	public abstract List<ExecutedTrade> getTradeHistory(Stock oranges);

}