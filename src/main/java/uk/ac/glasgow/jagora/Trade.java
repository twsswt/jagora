package uk.ac.glasgow.jagora;

import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

public interface Trade {
	
	public Trader getBuyer ();
	
	public Trader getSeller ();
	
	public TickEvent<Trade> execute (World world) throws TradeExecutionException ;

	public Stock getStock() ;

	public Integer getQuantity() ;

	public Double getPrice() ;
}
