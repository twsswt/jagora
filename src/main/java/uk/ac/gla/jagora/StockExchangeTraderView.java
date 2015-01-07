package uk.ac.gla.jagora;


public interface StockExchangeTraderView {
	
	public Double getBestOfferPrice(Stock stock);
	
	public Double getBestBidPrice(Stock stock);
	
	public void registerBuyOrder (BuyOrder buyOrder);
	
	public void registerSellOrder (SellOrder sellOrder);
	
	public void cancelBuyOrder(BuyOrder buyOrder);
	
	public void cancelSellOrder(SellOrder sellOrder);

}
