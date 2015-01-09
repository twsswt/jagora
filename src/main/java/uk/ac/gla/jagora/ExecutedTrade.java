package uk.ac.gla.jagora;


public class ExecutedTrade extends TickEvent<Trade> {

	public ExecutedTrade(Trade trade, World world) {
		super(trade, world);
	}	
}
