package uk.ac.glasgow.jagora.world;

public interface World {

	public <T> TickEvent<T> getTick(T event);

	public Boolean isAlive();

	public Long getCurrentTick();

}
