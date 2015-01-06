package uk.ac.gla.jagora.orderdrivenmarket;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import uk.ac.gla.jagora.World;

public class OrderBook<T extends Order>  {
	
	private PriorityQueue<ReceivedOrder<T>> receivedOrders;
	private World world;
	
	public OrderBook (World world){
		this.world = world;
		this.receivedOrders = new PriorityQueue<ReceivedOrder<T>>();
	}
	
	public void recordOrder (T order){
		receivedOrders.add(createReceivedOrder(order));
	}
	
	public void cancelOrder(T order) {
		for (ReceivedOrder<T> receivedOrder : receivedOrders)
			if (receivedOrder.order.equals(order))
				receivedOrders.remove(receivedOrder);
	}
	
	private ReceivedOrder<T> createReceivedOrder (T order) {
		return new ReceivedOrder<T>(order, world);
	}

	public T getBestOrder() {
		ReceivedOrder<T> receivedOrder = receivedOrders.peek();
		
		while (receivedOrder != null && receivedOrder.order.getRemainingQuantity() <= 0){
			receivedOrders.poll();
			receivedOrder = receivedOrders.peek();
		}
		
		return receivedOrder == null? null : receivedOrder.order;		
	}

	
	@Override
	public String toString (){
		return receivedOrders.toString();
	}

	public List<T> getOpenOrders() {
		
		List<T> result = new ArrayList<T>();
		
		receivedOrders
			.stream()
			.forEach(receivedOrder -> result.add(receivedOrder.order));
	
		return result;
	}
}
