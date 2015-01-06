package uk.ac.gla.jagora.world;

import uk.ac.gla.jagora.TickableEvent;
import uk.ac.gla.jagora.World;

public class SimpleSerialWorld implements World{
	
	private Long tickCount = 0l;

	@Override
	public Long getTick(TickableEvent tickableEvent) {
		return tickCount++;
	}

	@Override
	public boolean isAlive() {
		return true;
	}

}
