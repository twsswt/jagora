package uk.ac.glasgow.jagora;

public interface World {

	public <T> TickEvent<T> getTick(T event);

	public boolean isAlive();

}
