package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.impl.AbstractBuyOrder;
import uk.ac.glasgow.jagora.impl.AbstractSellOrder;

public interface StockExchangeTraderView {
	
	public Double getBestOfferPrice(Stock stock);
	
	public Double getBestBidPrice(Stock stock);
	
	public void placeBuyOrder (AbstractBuyOrder buyOrder);
	
	public void placeSellOrder (AbstractSellOrder sellOrder);
	
	public void cancelBuyOrder(AbstractBuyOrder buyOrder);
	
	public void cancelSellOrder(AbstractSellOrder sellOrder);	
}
