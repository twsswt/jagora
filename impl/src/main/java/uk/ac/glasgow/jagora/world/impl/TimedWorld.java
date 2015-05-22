package uk.ac.glasgow.jagora.world.impl;

import java.util.Date;

import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public class TimedWorld implements World{
	
	private Date startTime;
	private Long maxTickCount;
	
	private Object syncObject = new Object();

	public TimedWorld(Date startTime, Long maxTickCount){
		this.startTime = startTime;
		this.maxTickCount = maxTickCount;
	}
		
	@Override
	public <T> TickEvent<T> getTick(T event) {
		synchronized(syncObject){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {// TODO Auto-generated catch block
			}
			return new TickEvent<T>(event, getCurrentTick());
		}
	}

	@Override
	public Boolean isAlive() {
		synchronized(syncObject){
			return new Date().before( new Date (startTime.getTime() + maxTickCount) );
		}
	}

	@Override
	public Long getCurrentTick() {
		return new Date().getTime() - startTime.getTime();
	}
}
