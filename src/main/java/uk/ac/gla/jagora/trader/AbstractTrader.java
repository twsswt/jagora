package uk.ac.gla.jagora.trader;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.StockExchangeTraderView;
import uk.ac.gla.jagora.Trade;
import uk.ac.gla.jagora.TradeExecutionException;
import uk.ac.gla.jagora.Trader;

public abstract class AbstractTrader implements Trader {
	
	public final String name;
	
	private Double cash; 
	protected final Map<Stock,Integer> inventory;
	
	private List<Trade> mySellTrades;
	private List<Trade> myBuyTrades;

	public AbstractTrader(String name, Double cash, Map<Stock,Integer> inventory) {
		this.name = name;
		this.cash = cash;
		this.inventory = new HashMap<Stock,Integer>();
		this.inventory.putAll(inventory);
		this.mySellTrades = new ArrayList<Trade>();
		this.myBuyTrades = new ArrayList<Trade>();
	}
	
	/**
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

	/**
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
			mySellTrades.add(trade);
		}
	}
	
	/**
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
			myBuyTrades.add(trade);
		}
	}

	public List<Trade> getMySellTrades() {
		return mySellTrades;
	}

	public List<Trade> getMyBuyTrades() {
		return myBuyTrades;
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.gla.jagora.Trader#speak(uk.ac.gla.jagora.TraderMarketView)
	 */
	@Override
	public abstract void speak (StockExchangeTraderView traderMarketView);
	
	public Integer getInventory(Stock stock) {
		return inventory.get(stock);
	}
}
