package uk.ac.gla.jagora.test.stub;

import java.util.HashMap;
import java.util.Map;

import uk.ac.gla.jagora.Order;
import uk.ac.gla.jagora.TickableEvent;
import uk.ac.gla.jagora.World;
import uk.ac.gla.jagora.orderdriven.impl.ReceivedOrder;

public class StubWorld implements World {
	
	private Map<Order,Long> ticks;
	
	public StubWorld() {
		ticks = new HashMap<Order,Long>();
	}
	
	public void registerTickableEventForTick(Order event, Long tick){
		
		ticks.put(event, tick);
	}

	@Override
	public Long getTick(TickableEvent tickableEvent) {
		if (tickableEvent instanceof ReceivedOrder){
			
			ReceivedOrder<? extends Order> receivedOrder = 
				(ReceivedOrder<? extends Order>)tickableEvent;
			
			return ticks.get(receivedOrder.order);

		} else return -1l;
	}

	@Override
	public boolean isAlive() {
		return true;
	}

}
