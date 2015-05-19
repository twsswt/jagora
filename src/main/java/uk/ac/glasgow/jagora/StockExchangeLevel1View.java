package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.ticker.TradeListener;

public interface StockExchangeLevel1View {
	
	public Double getBestOfferPrice(Stock stock);
	
	public Double getBestBidPrice(Stock stock);
	
	public Double getLastKnownBestOfferPrice(Stock stock);

	public Double getLastKnownBestBidPrice(Stock stock);
	
	public void placeBuyOrder (BuyOrder buyOrder);
	
	public void placeSellOrder (SellOrder sellOrder);
	
	public void cancelBuyOrder(BuyOrder buyOrder);
	
	public void cancelSellOrder(SellOrder sellOrder);
	
	public void registerTradeListener(TradeListener tradeListener);

}
