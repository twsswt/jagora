package uk.ac.glasgow.jagora.impl.orderbook;

import java.util.Comparator;

import uk.ac.glasgow.jagora.MarketOrder;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class MarketOrderBook<O extends MarketOrder> extends OrderBook<O>{

	private static class MarketOrderComparator<MO extends MarketOrder> implements Comparator<TickEvent<MO>> {

		@Override
		public int compare(TickEvent<MO> o1, TickEvent<MO> o2) {
			return o1.tick.compareTo(o2.tick);
		}

	}

	public MarketOrderBook(World world) {
		super(world, new MarketOrderComparator<O>());
	}
	
}
