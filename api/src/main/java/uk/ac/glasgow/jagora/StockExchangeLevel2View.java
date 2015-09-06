package uk.ac.glasgow.jagora;

import java.util.List;

import uk.ac.glasgow.jagora.ticker.OrderListener;

public interface StockExchangeLevel2View extends StockExchangeLevel1View {
	public void registerOrderListener (OrderListener orderListener);

	public List<? extends LimitOrder> getBuyLimitOrders(Stock stock);
	
	public List<? extends LimitOrder> getSellLimitOrders(Stock stock);
	
}
