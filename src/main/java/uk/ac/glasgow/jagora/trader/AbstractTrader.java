package uk.ac.glasgow.jagora.trader;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeTraderView;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.TradeExecutionException;
import uk.ac.glasgow.jagora.Trader;

/**
 * Implements basic trader functionality. Sub-classes implement trader specific
 * strategies in the speak () operation.
 * 
 * @author tws
 *
 */
public abstract class AbstractTrader implements Trader {
	
	/**
	 * A unique identifier for the trader.
	 */
	public final String name;

	private Double cash; 
	protected final Map<Stock,Integer> inventory;
	
	private List<Trade> mySellTrades;
	private List<Trade> myBuyTrades;

	public AbstractTrader(String name, Double cash, Map<Stock,Integer> inventory) {
		this.name = name;
		this.cash = cash;
		this.inventory = new HashMap<Stock,Integer>(inventory);
		this.mySellTrades = new ArrayList<Trade>();
		this.myBuyTrades = new ArrayList<Trade>();
	}
	
	/**
	 * @see uk.ac.glasgow.jagora.Trader#getCash()
	 */
	@Override
	public Double getCash (){
		return cash;
	}
	
	/**
	 * @see uk.ac.glasgow.jagora.Trader#getInventory(Stock)
	 */
	@Override
	public Integer getInventory(Stock stock) {
		return inventory.getOrDefault(stock, 0);
	}

	@Override
	public String toString (){
		return format("trader[%s:$%.2f:%s]",name, cash, inventory);
	}

	/**
	 * @see uk.ac.glasgow.jagora.Trader#sellStock(uk.ac.glasgow.jagora.Trade)
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
	 * @see uk.ac.glasgow.jagora.Trader#buyStock(uk.ac.glasgow.jagora.Trade)
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
	
	/**
	 * @see uk.ac.glasgow.jagora.Trader#speak(uk.ac.gla.jagora.TraderMarketView)
	 */
	@Override
	public abstract void speak (StockExchangeTraderView traderMarketView);
	
}