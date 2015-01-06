package uk.ac.gla.jagora;

public interface TraderMarketView {
	public Double getCurrentBestSellPrice(Stock stock);
	
	public Double getCurrentBestBuyPrice(Stock stock);
}
