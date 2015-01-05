package uk.ac.gla.jagora;

import java.util.PriorityQueue;

public class OrderBook<T extends Order>  {
	
	private PriorityQueue<ReceivedOrder<T>> orders;
	private World world;
	
	public OrderBook (World world){
		this.world = world;
		this.orders = new PriorityQueue<ReceivedOrder<T>>();
	}
	
	public void recordOrder (T order){
		orders.add(createReceivedOrder(order));
	}
	
	private ReceivedOrder<T> createReceivedOrder (T order) {
		return new ReceivedOrder<T>(order, world);
	}

	public T seeBestOrder() {
		ReceivedOrder<T> receivedOrder = orders.peek();
		
		while (receivedOrder != null && receivedOrder.order.getRemainingQuantity() <= 0){
			orders.poll();
			receivedOrder = orders.peek();
		}
		
		return receivedOrder == null? null : receivedOrder.order;		
	}
	
	public Integer size (){
		return orders.size();
	}
	
	@Override
	public String toString (){
		return orders.toString();
	}

	public void cancelOrder(T order) {
		orders.remove(order);
	}
}
