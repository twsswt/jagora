package uk.ac.glasgow.jagora.impl.orderbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class OrderBook<O extends Order> {

	private final PriorityQueue<TickEvent<O>> receivedOrders;
	private final World world;
	
	public OrderBook (World world, Comparator<TickEvent<O>> orderComparator){
		this.world = world;
		this.receivedOrders = new PriorityQueue<TickEvent<O>>(orderComparator);
	}
	
	public TickEvent<O> recordOrder (O order){
		TickEvent<O> event = world.getTick(order);
		receivedOrders.add(event);
		return event;
	}

	public TickEvent<O> cancelOrder(O order) {
		TickEvent<O> toRemove = null;
				
		for (TickEvent<O> receivedOrder : receivedOrders)
			if (receivedOrder.event == order){
				toRemove = receivedOrder;
				break;
			}
		
		if (toRemove != null){
			receivedOrders.remove(toRemove);
			TickEvent<O> event = world.getTick(order);
			return event;
		} else {
			return null;
		}
		
	}

	public TickEvent<O> getBestOrder() {
		TickEvent<O> receivedOrder = receivedOrders.peek();

		while (receivedOrder != null && receivedOrder.event.getRemainingQuantity() <= 0){
			receivedOrders.poll();
			receivedOrder = receivedOrders.peek();
		}
		
		return receivedOrder;		
	}

	public List<O> getOpenOrders() {
		
		List<O> result = new ArrayList<O>();
		
		List<TickEvent<O>> intermediate = new ArrayList<TickEvent<O>>(receivedOrders);
		Collections.sort(intermediate, receivedOrders.comparator());
				
		intermediate
			.stream()
			.forEach(receivedOrder -> result.add(receivedOrder.event));
	
		return result;
	}
	
	@Override
	public String toString (){
		return receivedOrders.toString();
	}

	
}
