package uk.ac.gla.jagora;

public interface TraderMarketView {
	public Double getLastExecutionSellPrice(Stock stock);
	
	public Double getLastExecutionBuyPrice(Stock stock);
}
