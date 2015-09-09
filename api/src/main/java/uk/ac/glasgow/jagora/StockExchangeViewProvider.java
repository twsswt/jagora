package uk.ac.glasgow.jagora;

/**
 * Provides features for traders to obtain information about
 * and interact with a stock exchange via different levels
 * of access. Underlying implementations may be the actual
 * stock exchange or intermediate infrastructure.
 * 
 * @author Tim
 *
 */
public interface StockExchangeViewProvider {

	public abstract StockExchangeLevel1View createLevel1View();

	public abstract StockExchangeLevel2View createLevel2View();

}