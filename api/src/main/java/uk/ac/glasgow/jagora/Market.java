package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.impl.MarketBuyOrder;
import uk.ac.glasgow.jagora.impl.MarketSellOrder;
import uk.ac.glasgow.jagora.world.TickEvent;

import java.util.List;

/**
 * Defines features of a general purpose market for a single stock.
 * @author tws
 *
 */
public interface Market {
	
	/**
	 * @return the stock traded on this market.
	 */
	public Stock getStock();

	public TickEvent<MarketBuyOrder> recordMarketBuyOrder(MarketBuyOrder marketBuyOrder);
	
	public TickEvent<MarketSellOrder> recordMarketSellOrder(MarketSellOrder marketSellOrder);

	public TickEvent<LimitBuyOrder> recordLimitBuyOrder(LimitBuyOrder order);

	public TickEvent<LimitSellOrder> recordLimitSellOrder(LimitSellOrder order);
	
	public TickEvent<LimitBuyOrder> cancelLimitBuyOrder(LimitBuyOrder order);

	public TickEvent <LimitSellOrder> cancelLimitSellOrder(LimitSellOrder order);
	
	public List<TickEvent<Trade>> doClearing();

	public List<LimitBuyOrder> getBuyLimitOrders();

	public List<LimitSellOrder> getSellLimitOrders();

	public Long getBestBidPrice();
	
	public Long getBestOfferPrice();
	
	public Long getLastKnownBestBidPrice();
	
	public Long getLastKnownBestOfferPrice();

}