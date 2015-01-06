package uk.ac.gla.jagora;

public interface World {

	public Long getTick(TickableEvent tickableEvent);

	public boolean isAlive();

}
