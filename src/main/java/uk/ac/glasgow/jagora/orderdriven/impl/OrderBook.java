package uk.ac.glasgow.jagora.orderdriven.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.World;

/**
 * Manages buy or sell orders for a single stock type.
 * @author tws
 *
 * @param <O> the order type of this order book (either BuyOrder or SellOrder).
 */
public class OrderBook<O extends Order>  {
	
	private PriorityQueue<ReceivedOrder<O>> receivedOrders;
	private World world;
	
	public OrderBook (World world){
		this.world = world;
		this.receivedOrders = new PriorityQueue<ReceivedOrder<O>>();
	}
	
	public void recordOrder (O order){
		receivedOrders.add(createReceivedOrder(order));
	}
	
	public void cancelOrder(O order) {
		
		ReceivedOrder<O> toRemove = null;
				
		for (ReceivedOrder<O> receivedOrder : receivedOrders)
			if (receivedOrder.event.equals(order)){
				toRemove = receivedOrder;
				break;
			}
		
		if (toRemove != null)
			receivedOrders.remove(toRemove);
	}
	
	private ReceivedOrder<O> createReceivedOrder (O order) {
		return new ReceivedOrder<O>(order, world);
	}

	public O getBestOrder() {
		ReceivedOrder<O> receivedOrder = receivedOrders.peek();
		
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