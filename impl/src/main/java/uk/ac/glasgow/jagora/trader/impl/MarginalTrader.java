package uk.ac.glasgow.jagora.trader.impl;

import java.util.Map;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.util.Random;

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

	private void performMarginalSellOrder(
		StockExchangeLevel1View traderMarketView) {

		Stock randomStock = random.chooseElement(inventory.keySet());

		Long bestBidPrice = 
			traderMarketView.getBestBidPrice(randomStock);

		Long price = bestBidPrice - 1l;

		Integer quantity = random.nextInt(inventory.get(randomStock));

		LimitSellOrder limitSellOrder = new LimitSellOrder(this, randomStock, quantity, price);
		traderMarketView.placeSellOrder(limitSellOrder);

	}

	private void performMarginalBuyOrder(
			StockExchangeLevel1View traderMarketView) {
		
		Stock randomStock = random.chooseElement(inventory.keySet());

		Long bestOfferPrice = 
			traderMarketView.getBestOfferPrice(randomStock);

		Long buyPrice = bestOfferPrice + 1l;

		Integer quantity = (int) (getCash() / buyPrice);

		LimitBuyOrder limitBuyOrder = new LimitBuyOrder(this, randomStock, quantity, buyPrice);
		traderMarketView.placeBuyOrder(limitBuyOrder);

	}

}
