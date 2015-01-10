package uk.ac.glasgow.jagora;

public class TickEvent<T> implements Comparable<TickEvent<T>>{
	
	public final Long tick;
	public final T event;
	
	public TickEvent (T event, World world){
		this.tick = world.getTick(event);
		this.event = event;
	}
	
	@Override
	public int compareTo(TickEvent<T> executedTrade) {
		return tick.compareTo(executedTrade.tick);
	}
	
	@Override
	public String toString (){
		return String.format("%s:t=%d", event, tick);
	}

}
