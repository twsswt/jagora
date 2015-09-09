package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.ticker.TradeListener;

public interface StockExchangeLevel1View {
	
	public Long getBestOfferPrice(Stock stock);
	
	public Long getBestBidPrice(Stock stock);
	
	public Long getLastKnownBestOfferPrice(Stock stock);

	public Long getLastKnownBestBidPrice(Stock stock);
	
	public void placeLimitBuyOrder (LimitBuyOrder limitBuyOrder);
	
	public void placeLimitSellOrder (LimitSellOrder limitSellOrder);
	
	public void cancelLimitBuyOrder(LimitBuyOrder limitBuyOrder);
	
	public void cancelLimitSellOrder(LimitSellOrder limitSellOrder);
	
	public void placeMarketBuyOrder(MarketBuyOrder marketBuyOrder);
	
	public void placeMarketSellOrder(MarketSellOrder marketSellOrder);
	
	public void registerTradeListener(TradeListener tradeListener);

}
