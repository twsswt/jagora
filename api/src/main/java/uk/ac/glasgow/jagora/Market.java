package uk.ac.glasgow.jagora;

import java.util.List;

import uk.ac.glasgow.jagora.world.TickEvent;

/**
 * Defines features of a market for a single stock.
 * @author tws
 *
 */
public interface Market {

	public TickEvent<BuyOrder> recordBuyOrder(BuyOrder order);

	public TickEvent<SellOrder> recordSellOrder(SellOrder order);

	public void cancelBuyOrder(BuyOrder order);

	public void cancelSellOrder(SellOrder order);

	public List<TickEvent<Trade>> doClearing();

	public List<BuyOrder> getBuyOrders();

	public List<SellOrder> getSellOrders();

	public Long getBestBidPrice();
	
	public Long getBestOfferPrice();
	
	public Long getLastKnownBestBidPrice();
	
	public Long getLastKnownBestOfferPrice();
}