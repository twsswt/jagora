package uk.ac.glasgow.jagora.test.stub;

import java.util.Collection;
import java.util.HashSet;

import uk.ac.glasgow.jagora.TickEvent;
import uk.ac.glasgow.jagora.World;

public class StubWorld implements World {
	
	private Collection<TickEvent<?>> tickEvents = new HashSet<TickEvent<?>>();

	@Override
	public <T> TickEvent<T> getTick(T event) {
		for (TickEvent<?> tickEvent : tickEvents)
			if (tickEvent.event == event)
				return (TickEvent<T>)tickEvent;
		return null;
	}
	

	@Override
	public boolean isAlive() {
		return true;
	}

	public <T> void setTickForEvent(Long tick, T event) {
		TickEvent<T> tickEvent = new TickEvent<T>(event, tick);
		tickEvents.add(tickEvent);
	}

}
