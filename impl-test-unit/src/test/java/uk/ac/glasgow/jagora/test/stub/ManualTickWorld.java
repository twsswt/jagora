package uk.ac.glasgow.jagora.test.stub;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class ManualTickWorld implements World {
	
	private Collection<TickEvent<?>> tickEvents = new ArrayList<TickEvent<?>>();

	@Override
	public <T> TickEvent<T> getTick(T event) {
		for (TickEvent<?> tickEvent : tickEvents)
			if (tickEvent.event == event)
				return (TickEvent<T>)tickEvent;
		return null;
	}

	@Override
	public Boolean isAlive() {
		return true;
	}

	public <T> void setTickForEvent(Long tick, T event) {
		TickEvent<T> tickEvent = new TickEvent<T>(event, tick);
		tickEvents.add(tickEvent);
	}

	@Override
	public Long getCurrentTick() {
		return 0l;
	}

}
