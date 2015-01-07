package uk.ac.gla.jagora.trader;

import java.util.Map;

import uk.ac.gla.jagora.BuyOrder;
import uk.ac.gla.jagora.SellOrder;
import uk.ac.gla.jagora.Stock;
import uk.ac.gla.jagora.StockExchangeTraderView;
import uk.ac.gla.jagora.util.Random;

/**
 * Simulates an order driven market trader who places buy (often very large)
 * orders marginally above the cheapest offer price and sell orders marginally
 * below the highest bid price.
 * 
 * @author tws
 *
 */
public class MarginalTrader extends AbstractTrader {

	private Random random;

	protected MarginalTrader(
		String name, Double cash, Map<Stock, Integer> inventory, Random random) {
	
		super(name, cash, inventory);
		this.random = random;
	}

	@Override
	public void speak(StockExchangeTraderView traderMarketView) {
		if (random.nextBoolean()) {
			performMarginalBuyOrder(traderMarketView);
		} else
			performMarginalSellOrder(traderMarketView);
	}

	private void performMarginalSellOrder(
		StockExchangeTraderView traderMarketView) {

		Stock randomStock = random.chooseElement(inventory.keySet());

		Double bestBidPrice = 
			traderMarketView.getBestBidPrice(randomStock);

		Double price = bestBidPrice - Double.MIN_NORMAL;

		Integer quantity = random.nextInt(inventory.get(randomStock));

		SellOrder sellOrder = new SellOrder(this, randomStock, quantity, price);
		traderMarketView.placeSellOrder(sellOrder);

	}

	private void performMarginalBuyOrder(
			StockExchangeTraderView traderMarketView) {
		
		Stock randomStock = random.chooseElement(inventory.keySet());

		Double bestOfferPrice = 
			traderMarketView.getBestOfferPrice(randomStock);

		Double buyPrice = bestOfferPrice + Double.MIN_NORMAL;

		Integer quantity = (int) (getCash() / buyPrice);

		BuyOrder buyOrder = new BuyOrder(this, randomStock, quantity, buyPrice);
		traderMarketView.placeBuyOrder(buyOrder);

	}

}
