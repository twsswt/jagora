package uk.ac.glasgow.jagora.trader;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.TradeExecutionException;

/**
 * Specifies the behaviour of a trader who is able to speak on a stock exchange
 * (to determine its state and manage orders) and who must also satisfy demands
 * to sell or buy stocks as a result of trades.
 * 
 * @author Tim
 *
 */
public interface Trader {
	
	/**
	 * @return the cash currently held by this trader.
	 */
	public abstract Long getCash();
	
	/**
	 * @param stock
	 * @return the quantity of the specified stock held by this trader.
	 */
	public Integer getInventory(Stock stock);


	/**
	 * Deducts a specified amount of stock from this trader's inventory and
	 * reimburses them the cash according to the price and quantity of the
	 * trade.
	 * 
	 * @param trade
	 *            specifies the amount of and price for stock to be sold.
	 * @throws TradeExecutionException
	 *             if the trader cannot satisfy the trade.
	 */
	public abstract void sellStock(Trade trade) throws TradeExecutionException;

	/**
	 * Deducts a specified amount of cash from this trader's funds and supplies
	 * them with the quantity of stock indicated by the trade.
	 * 
	 * @param trade
	 *            specifies the amount of and price for stock to be bought.
	 * @throws TradeExecutionException
	 *             if the trader cannot satisfy the trade due to insufficient
	 *             funds.
	 */
	public abstract void buyStock(Trade trade) throws TradeExecutionException;

	public abstract String getName();
	
}