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
	
	public OrderBook (World world){
		this.world = world;
		this.receivedOrders = new PriorityQueue<TickEvent<O>>(1, new ReceivedOrderComparator());
	}
	
	public void recordOrder (O order){
		receivedOrders.add(world.getTick(order));
	}
	
	public void cancelOrder(O order) {
		
		TickEvent<O> toRemove = null;
				
		for (TickEvent<O> receivedOrder : receivedOrders)
			if (receivedOrder.event.equals(order)){
				toRemove = receivedOrder;
				break;
			}
		
		if (toRemove != null)
			receivedOrders.remove(toRemove);
	}

	public O getBestOrder() {
		TickEvent<O> receivedOrder = receivedOrders.peek();
		
		while (receivedOrder != null && receivedOrder.event.getRemainingQuantity() <= 0){
			receivedOrders.poll();
			receivedOrder = receivedOrders.peek();
		}
		
		return receivedOrder == null? null : receivedOrder.event;		
	}

	
	@Override
	public String toString (){
		return receivedOrders.toString();
	}

	public List<O> getOpenOrders() {
		
		List<O> result = new ArrayList<O>();

		receivedOrders
			.stream()
			.forEach(receivedOrder -> result.add(receivedOrder.event));
	
		return result;
	}
}
