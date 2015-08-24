package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.util.Random;

import java.util.Map;

/**
 * Simulates an order driven market trader who places buy (often very large)
 * orders marginally above the cheapest offer price and sell orders marginally
 * below the highest bid price.
 * 
 * @author tws
 *
 */
public class MarginalTrader extends AbstractTrader implements Level1Trader {

	private Random random;

	protected MarginalTrader(
		String name, Long cash, Map<Stock, Integer> inventory, Random random) {
	
		super(name, cash, inventory);
		this.random = random;
	}

	@Override
	public void speak(StockExchangeLevel1View traderMarketView) {
		if (random.nextBoolean()) {
			performMarginalBuyOrder(traderMarketView);
		} else
			performMarginalSellOrder(traderMarketView);
	}

	/**
	 * Finds the best bid price (on a random stock) and places a limit sell order for a unit(1) less than price.
     * Quantity sold is a random amount.
	 * @param traderMarketView
	 */
	private void performMarginalSellOrder(
		StockExchangeLevel1View traderMarketView) {

		Stock randomStock = random.chooseElement(inventory.keySet());

		Long bestBidPrice = 
			traderMarketView.getBestBidPrice(randomStock);

		Long price = bestBidPrice - 1l;

		Integer quantity = random.nextInt(inventory.get(randomStock));

		DefaultLimitSellOrder defaultLimitSellOrder = new DefaultLimitSellOrder(this, randomStock, quantity, price);
		traderMarketView.placeLimitSellOrder(defaultLimitSellOrder);

	}

    /**
     * Finds the best sell price and then places a limit buy order for a unit (1) more than it.
     * Maximum quantity of the stock is bought.
     * @param traderMarketView
     */
	private void performMarginalBuyOrder(
			StockExchangeLevel1View traderMarketView) {
		
		Stock randomStock = random.chooseElement(inventory.keySet());

		Long bestOfferPrice = 
			traderMarketView.getBestOfferPrice(randomStock);

		Long buyPrice = bestOfferPrice + 1l;

		Integer quantity = (int) (getCash() / buyPrice);

		DefaultLimitBuyOrder defaultLimitBuyOrder = new DefaultLimitBuyOrder(this, randomStock, quantity, buyPrice);
		traderMarketView.placeLimitBuyOrder(defaultLimitBuyOrder);

	}

}
