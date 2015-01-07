package uk.ac.gla.jagora.trader;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;

import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.Trade;
import uk.ac.gla.jagora.TradeExecutionException;
import uk.ac.gla.jagora.Trader;
import uk.ac.gla.jagora.StockExchangeTraderView;

public abstract class AbstractTrader implements Trader {
	
	public final String name;
	
	private Double cash; 
	protected final Map<Stock,Integer> inventory;

	public AbstractTrader(String name, Double cash, Map<Stock,Integer> inventory) {
		this.name = name;
		this.cash = cash;
		this.inventory = new HashMap<Stock,Integer>();
		this.inventory.putAll(inventory);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.gla.jagora.Trader#getCash()
	 */
	@Override
	public Double getCash (){
		return cash;
	}
	
	@Override
	public String toString (){
		return format("trader[%s:$%.2f:%s]",name, cash, inventory);
	}

	/* (non-Javadoc)
	 * @see uk.ac.gla.jagora.Trader#sellStock(uk.ac.gla.jagora.Trade)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see uk.ac.gla.jagora.Trader#buyStock(uk.ac.gla.jagora.Trade)
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see uk.ac.gla.jagora.Trader#speak(uk.ac.gla.jagora.TraderMarketView)
	 */
	@Override
	public abstract void speak (StockExchangeTraderView traderMarket);
}
