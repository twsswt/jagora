package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;

/**
 * Defines order management facilities for all types of
 * order that are placed with a stock exchange.
 * 
 * @author tws
 *
 */
public interface Order {
		
	public Trader getTrader ();
	
	public Stock getStock();

	public Integer getRemainingQuantity();

	public void satisfyTrade(TickEvent<Trade> trade)
			throws TradeExecutionException;

	public void rollBackTrade(TickEvent<Trade> trade)
			throws TradeExecutionException;
	
	public Boolean isFilled();

}