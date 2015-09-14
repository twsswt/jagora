package uk.ac.glasgow.jagora.trader.impl.random;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static uk.ac.glasgow.jagora.util.CollectionsRandom.chooseElement;

/**
 * A trader that places spread crossing limit orders. When
 * the trader is given an opportunity to speak on the market
 * it randomly chooses to place a bid or offer. A random
 * stock is chosen and a random quantity (within the
 * selected range) is chosen. Finally, a random price
 * between the best bid (offer) and the configured price
 * range limit for that stock is chosen. An existing order
 * is cancelled in order to free up resources, if the
 * computed limit order cannot be placed safely. Otherwise
 * the proposed order is placed on the market.
 * 
 * @author tws
 *
 */
public class RandomSpreadCrossingTrader extends SafeAbstractTrader implements Level1Trader {
	
	private final Map<Stock,SpreadCrossingRangeData> tradeRanges;
	protected final Random random;
	
	protected RandomSpreadCrossingTrader(
		String name, Long cash, Map<Stock, Integer> inventory,
		Random random, Map<Stock,SpreadCrossingRangeData> tradeRanges) {
		
		super(name, cash, inventory);
		this.random = random;
		this.tradeRanges = new HashMap<Stock,SpreadCrossingRangeData>(tradeRanges);
	}

	@Override
	public void speak(StockExchangeLevel1View traderMarketView) {
		Stock randomStock = chooseElement(inventory.keySet(), random);
		
		if (random.nextBoolean())
			performRandomSellAction(randomStock, traderMarketView);
		else 
			performRandomBuyAction(randomStock, traderMarketView);
	}

	private void performRandomSellAction(
		Stock stock, StockExchangeLevel1View stockExchangeLevel1View) {
		
		Long bestBidPrice = stockExchangeLevel1View.getBestBidPrice(stock);
		if (bestBidPrice == null) return;
		
		Integer uncommittedQuantity = 
			getAvailableQuantity(stock);
		
		Integer quantity = 
			createRandomQuantity(stock, uncommittedQuantity);

		if (quantity > 0){
			
			Long price = createRandomPrice(stock, bestBidPrice, true);

			LimitSellOrder limitSellOrder =
				new DefaultLimitSellOrder(this, stock, quantity, price);

			placeSafeSellOrder(stockExchangeLevel1View, limitSellOrder);
			
		} else {
			LimitSellOrder randomSellOrder = chooseElement(openSellOrders, random);
			if (randomSellOrder != null){
				cancelSafeSellOrder(stockExchangeLevel1View, randomSellOrder);
			}
		}
	}

	private void performRandomBuyAction(
		Stock stock, StockExchangeLevel1View stockExchangeLevel1View) {
		
		Long bestOfferPrice = stockExchangeLevel1View.getBestOfferPrice(stock);
		if (bestOfferPrice == null) return;

		Long price = createRandomPrice(stock, bestOfferPrice, false);

		Long availableCash = getAvailableCash();
		
		Integer quantity = createRandomQuantity(stock, (int)(availableCash/price));
		
		if (quantity > 0){
			
			LimitBuyOrder limitBuyOrder =
				new DefaultLimitBuyOrder(this, stock, quantity, price);
			
			placeSafeBuyOrder(stockExchangeLevel1View, limitBuyOrder);
			
		} else {
			LimitBuyOrder limitBuyOrder = chooseElement(openBuyOrders, random);
			if (limitBuyOrder != null)
				cancelSafeBuyOrder(stockExchangeLevel1View, limitBuyOrder);
		}
		
	}

    /**
     *
     * @param stock
     * @param basePrice
     * @param isSell
     * @return returns random price for stock lower the best bid (for sell) or higher than lowest ask (for buy)
     */
	protected Long createRandomPrice(Stock stock, Long basePrice, boolean isSell) {

		SpreadCrossingRangeData tradeRange = tradeRanges.get(stock);
		Long randomPrice = 
			(isSell?-1:1) * (long)(random.nextDouble() *  tradeRange.priceRange) + basePrice;
		
		return max(randomPrice, 0l);
	}

    /**
     *
     * @param stock
     * @param ceiling maximum available quantity in the stock
     * @return random quantity of stock, which is in its tradeRange for the particular trader
     */
	protected Integer createRandomQuantity(Stock stock, Integer ceiling) {
		SpreadCrossingRangeData stockData = tradeRanges.get(stock);
		
		Integer tradeQuantityRange = stockData.maxQuantity - stockData.minQuantity;

		return min(random.nextInt(tradeQuantityRange) + stockData.minQuantity, ceiling);
	}

}