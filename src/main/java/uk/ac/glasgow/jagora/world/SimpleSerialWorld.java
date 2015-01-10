package uk.ac.glasgow.jagora.world;

import uk.ac.glasgow.jagora.World;

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
