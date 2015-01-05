package uk.ac.gla.jagora;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;

public abstract class Trader {
	
	public final String name;
	
	private Double cash; 
	private final Map<Stock,Integer> inventory;
	

	public Trader(String name, Double cash, Map<Stock,Integer> inventory) {
		this.name = name;
		this.cash = cash;
		this.inventory = new HashMap<Stock,Integer>();
		this.inventory.putAll(inventory);
	}
	
	public Double getCash (){
		return cash;
	}
	
	@Override
	public String toString (){
		return format("%s:$%d:%s",name, cash, inventory);
	}

	public void sellStock(Trade trade) throws TradeExecutionException {
		Integer currentQuantity = inventory.getOrDefault(trade.stock, 0);

		if (currentQuantity < trade.quantity){ 
			String message = format("Seller [%s] cannot satisfy trade [%s].", name, trade);
			throw new TradeExecutionException (message, trade, this);
		} else {
			inventory.put(trade.stock, currentQuantity - trade.quantity);
			cash += trade.price * trade.quantity;
		}
	}
	
	public void buyStock(Trade trade) throws TradeExecutionException {
		Double totalPrice = trade.price * trade.quantity;
		
		if (totalPrice > cash){
			String message = format("Buyer [%s] cannot satisfy trade [%s].", name, trade);
			throw new TradeExecutionException (message, trade, this);		
		} else {
			cash -= totalPrice;
			Integer currentQuantity = inventory.getOrDefault(trade.stock, 0);
			inventory.put(trade.stock, currentQuantity + trade.quantity);
		}
	}
	
	public abstract void speak (Market market);
}
