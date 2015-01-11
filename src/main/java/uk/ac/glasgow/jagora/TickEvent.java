package uk.ac.glasgow.jagora;

public class TickEvent<T> implements Comparable<TickEvent<T>>{

	public final Long tick;
	public final T event;
	
	public TickEvent (T event, Long tick){
		this.tick = tick;
		this.event = event;
	}
	
	@Override
	public int compareTo(TickEvent<T> tickEvent) {
		return tick.compareTo(tickEvent.tick);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tick == null) ? 0 : tick.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		TickEvent<T> other = (TickEvent<T>) obj;
		if (tick == null) {
			if (other.tick != null)
				return false;
		} else if (!tick.equals(other.tick))
			return false;
		return true;
	}
	
	@Override
	public String toString (){
		return String.format("%s:t=%d", event, tick);
	}

}
