package uk.ac.glasgow.jagora.orderdriven.impl;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.TickEvent;
import uk.ac.glasgow.jagora.World;


public class ReceivedOrder<O extends Order> extends TickEvent<O> {

	public ReceivedOrder(O order, World world) {
		super(order, world);
	}

	@Override
	public int compareTo(TickEvent<O> receivedOrder) {
		Integer orderComparison =
			event.compareTo(receivedOrder.event);
		
		if (orderComparison == 0)
			return tick.compareTo(receivedOrder.tick);
		else return orderComparison;
	}	
	
	@Override
	public String toString (){
		return String.format("%s:t=%s", event, tick);
	}
}
