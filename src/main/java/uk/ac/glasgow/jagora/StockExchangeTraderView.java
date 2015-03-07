package uk.ac.glasgow.jagora;

public interface StockExchangeTraderView {
	
	public Double getBestOfferPrice(Stock stock);
	
	public Double getBestBidPrice(Stock stock);
	
	public void placeBuyOrder (BuyOrder buyOrder);
	
	public void placeSellOrder (SellOrder sellOrder);
	
	public void cancelBuyOrder(BuyOrder buyOrder);
	
	public void cancelSellOrder(SellOrder sellOrder);

	public Double getLastKnownBestOfferPrice(Stock stock);

	public Double getLastKnownBestBidPrice(Stock stock);	
}
