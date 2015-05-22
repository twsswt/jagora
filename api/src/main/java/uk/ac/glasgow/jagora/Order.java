package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;

public interface Order {
	
	public Trader getTrader ();
	
	public Stock getStock();

	public abstract Integer getRemainingQuantity();

	public abstract void satisfyTrade(TickEvent<Trade> trade)
			throws TradeExecutionException;

	public abstract void rollBackTrade(TickEvent<Trade> trade)
			throws TradeExecutionException;

	public abstract Double getPrice();
	
	public Boolean isFilled();


}