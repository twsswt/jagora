package uk.ac.gla.jagora.orderdrivenmarket;

import uk.ac.gla.jagora.TraderMarketView;

public interface TraderOrderDrivenMarketView extends TraderMarketView {
	
	public void registerBuyOrder (BuyOrder buyOrder);
	
	public void registerSellOrder (SellOrder sellOrder);
	
	public void cancelBuyOrder(BuyOrder buyOrder);
	
	public void cancelSellOrder(SellOrder sellOrder);

}
