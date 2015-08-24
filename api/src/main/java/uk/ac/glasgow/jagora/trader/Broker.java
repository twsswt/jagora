package uk.ac.glasgow.jagora.trader;

/**
 * A broker acts an intermediary between traders and a stock
 * exchange. This allows traders to place stop loss orders
 * with the broker.
 * 
 * @author tws
 *
 */
public interface Broker {
	
	public void placeStopLossOrder(StopLossOrder stopLossOrder);
	
}
