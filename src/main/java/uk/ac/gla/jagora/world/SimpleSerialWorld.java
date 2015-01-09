package uk.ac.gla.jagora.world;

import uk.ac.gla.jagora.World;

public class SimpleSerialWorld implements World{
	
	private Long tickCount = 0l;

	@Override
	public synchronized Long getTick(Object tickEvent) {
		return tickCount++;
	}

	@Override
	public boolean isAlive() {
		return true;
	}

}
