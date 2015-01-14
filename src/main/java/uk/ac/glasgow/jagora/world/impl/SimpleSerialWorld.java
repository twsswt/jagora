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
	public <T> TickEvent<T> getTick(T event) {
		synchronized(tickCount){
			return new TickEvent<T>(event, tickCount++);
		}
	}

	@Override
	public Boolean isAlive() {
		synchronized(tickCount){
			return tickCount < maxTickCount;
		}
	}
}
