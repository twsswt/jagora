package uk.ac.glasgow.jagora.world;

import uk.ac.glasgow.jagora.TickEvent;
import uk.ac.glasgow.jagora.World;

public class SimpleSerialWorld implements World{
	
	private Long tickCount = 0l;

	@Override
	public synchronized <T> TickEvent<T> getTick(T event) {
		return new TickEvent<T>(event, tickCount++);
	}

	@Override
	public boolean isAlive() {
		return true;
	}

}
