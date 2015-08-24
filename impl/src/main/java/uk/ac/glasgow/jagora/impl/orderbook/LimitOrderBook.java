package uk.ac.glasgow.jagora.impl.orderbook;

import java.util.Comparator;

import uk.ac.glasgow.jagora.LimitOrder;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

/**
 * Manages buy or sell limit orders for a single stock type.
 * @author tws
 *
 * @param <O> the order type of this order book (either LimitBuyOrder or LimitSellOrder).
 */
public class LimitOrderBook<O extends LimitOrder> extends OrderBook<O>  {
		
	/**
	 * Overrides default tick event ordering by comparing the stored limit order first
	 * before comparing tick times.
	 * 
	 * @author tws
	 *
	 */
	private static class LimitOrderComparator<LO extends LimitOrder> implements Comparator<TickEvent<LO>> {

		@Override
		public int compare(TickEvent<LO> tickEvent1, TickEvent<LO> tickEvent2) {
			Integer eventComparison = tickEvent1.event.compareTo(tickEvent2.event);
			if (eventComparison == 0)
				return tickEvent1.tick.compareTo(tickEvent2.tick);
			else return eventComparison;
		}
		
	}

	private Long lastKnownBestPrice;
	
	public LimitOrderBook (World world){
		super(world, new LimitOrderComparator<O>());
	}
	
	public TickEvent<O> recordOrder (O order){
		TickEvent<O> event = super.recordOrder(order);
		updateLastKnownBestPrice();
		return event;
	}

	public TickEvent<O> cancelOrder(O order) {
		TickEvent<O> toRemove = super.cancelOrder(order);
						
		if (toRemove != null)
			updateLastKnownBestPrice();
		
		return toRemove;
	}

	public Long getBestPrice() {
		TickEvent<O> orderEvent = getBestOrder();
		if (orderEvent == null) return null;
		else return orderEvent.event.getLimitPrice();
	}

	public Long getLastKnownBestPrice() {
		return lastKnownBestPrice;
	}
	
	private void updateLastKnownBestPrice() {
		Long currentBestPrice = getBestPrice();
		if (currentBestPrice != null)
			lastKnownBestPrice = currentBestPrice;
	}



}
