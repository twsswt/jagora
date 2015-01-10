package uk.ac.glasgow.jagora.test.stub;

import java.util.HashMap;
import java.util.Map;

import uk.ac.glasgow.jagora.World;

public class StubWorld implements World {
	
	private Map<Object,Long> ticks;
	
	public StubWorld() {
		ticks = new HashMap<Object,Long>();
	}
	
	public void registerEventForTick(Object event, Long tick){
		
		ticks.put(event, tick);
	}

	@Override
	public Long getTick(Object object) {
		return ticks.get(object);
	}

	@Override
	public boolean isAlive() {
		return true;
	}

}
