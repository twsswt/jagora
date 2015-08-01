package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.ticker.OrderListener;

public interface StockExchangeLevel2View extends StockExchangeLevel1View  {
	public void registerOrderListener (OrderListener orderListener);


}
