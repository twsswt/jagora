package uk.ac.glasgow.jagora.world.impl;

import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class SimpleSerialWorld implements World{
	
	private Long tickCount;
	private Long maxTickCount;

	public SimpleSerialWorld(Long maxTickCount){
		tickCount = 0l;
		this.maxTickCount = maxTickCount;
	}
		
	@Override
	public synchronized <T> TickEvent<T> getTick(T event) {
		return new TickEvent<T>(event, tickCount++);
	}

	@Override
	public Boolean isAlive() {
		return tickCount < maxTickCount;
	}
}
