package uk.ac.glasgow.jagora.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

/**
 * Manages buy or sell orders for a single stock type.
 * @author tws
 *
 * @param <O> the order type of this order book (either BuyOrder or SellOrder).
 */
public class OrderBook<O extends Order & Comparable<O>>  {
		
	/**
	 * Overrides default tick event ordering by comparing the stored event first
	 * before comparing tick times.
	 * 
	 * @author tws
	 *
	 */
	private class ReceivedOrderComparator implements Comparator<TickEvent<O>> {

		@Override
		public int compare(TickEvent<O> tickEvent1, TickEvent<O> tickEvent2) {
			Integer eventComparison = tickEvent1.event.compareTo(tickEvent2.event);
			if (eventComparison == 0)
				return tickEvent1.tick.compareTo(tickEvent2.tick);
			else return eventComparison;
		}
		
	}

	private PriorityQueue<TickEvent<O>> receivedOrders;
	private World world;
	private Long lastKnownBestPrice;
	
	public OrderBook (World world){
		this.world = world;
		this.receivedOrders = new PriorityQueue<TickEvent<O>>(1, new ReceivedOrderComparator());
	}
	
	public TickEvent<O> recordOrder (O order){
		TickEvent<O> event = world.getTick(order);
		receivedOrders.add(event);
		updateLastKnownBestPrice();
		return event;
	}
	//silently cancels??
	public void cancelOrder(O order) {
		TickEvent<O> toRemove = null;
				
		for (TickEvent<O> receivedOrder : receivedOrders)
			if (receivedOrder.event == order){
				toRemove = receivedOrder;
				break;
			}
		
		if (toRemove != null){
			receivedOrders.remove(toRemove);
			updateLastKnownBestPrice();
		}

	}

    /**
     *
     * @return Best order is the one with most favourable price,which still has quantity to be executed
     */
	public TickEvent<O> getBestOrder() {
		TickEvent<O> receivedOrder = receivedOrders.peek();
	//	if (receivedOrder != null) receivedOrder.event.getPrice();


		while (receivedOrder != null && receivedOrder.event.getRemainingQuantity() <= 0){
			receivedOrders.poll();
			receivedOrder = receivedOrders.peek();
			//updateLastKnownBestPrice();
		}
		
		return receivedOrder;		
	}

	
	@Override
	public String toString (){
		return receivedOrders.toString();
	}

	/**
	 *
	 * @return List with all received orders
	 */
	public List<O> getOpenOrders() {
		
		List<O> result = new ArrayList<O>();

		receivedOrders
			.stream()
			.forEach(receivedOrder -> result.add(receivedOrder.event));
	
		return result;
	}

	public Long getBestPrice() {
		TickEvent<O> orderEvent = getBestOrder();
		if (orderEvent == null) return null;
		else return orderEvent.event.getPrice();
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
