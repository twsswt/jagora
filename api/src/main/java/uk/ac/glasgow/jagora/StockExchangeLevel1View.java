package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.ticker.TradeListener;

public interface StockExchangeLevel1View {
	
	public Long getBestOfferPrice(Stock stock);
	
	public Long getBestBidPrice(Stock stock);
	
	public Long getLastKnownBestOfferPrice(Stock stock);

	public Long getLastKnownBestBidPrice(Stock stock);
	
	public void placeBuyOrder (BuyOrder buyOrder);
	
	public void placeSellOrder (SellOrder sellOrder);
	
	public void cancelBuyOrder(BuyOrder buyOrder);
	
	public void cancelSellOrder(SellOrder sellOrder);
	
	public void registerTradeListener(TradeListener tradeListener);

}
