package uk.ac.glasgow.jagora;

import java.util.List;

import uk.ac.glasgow.jagora.world.TickEvent;

/**
 * Defines features of a market for a single stock.
 * @author tws
 *
 */
public interface Market {

	public abstract void recordBuyOrder(BuyOrder order);

	public abstract void recordSellOrder(SellOrder order);

	public abstract void cancelBuyOrder(BuyOrder order);

	public abstract void cancelSellOrder(SellOrder order);

	public abstract List<TickEvent<Trade>> doClearing();

	public abstract List<BuyOrder> getBuyOrders();

	public abstract List<SellOrder> getSellOrders();

	public abstract Double getBestBidPrice();
	
	public abstract Double getBestOfferPrice();
	
	public abstract Double getLastKnownBestBidPrice();
	
	public abstract Double getLastKnownBestOfferPrice();
}