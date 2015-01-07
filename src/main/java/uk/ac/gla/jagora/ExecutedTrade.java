package uk.ac.gla.jagora;


public class ExecutedTrade implements TickableEvent {
	
	public final Trade trade;
	public final Long tick;

	public ExecutedTrade(Trade trade, World world) {
		this.tick = world.getTick(this);
		this.trade = trade;
	}
	
	@Override
	public String toString (){
		return String.format("%s:t=%d", trade, tick);
	}

	@Override
	public Long getTick() {
		return tick;
	}
}
