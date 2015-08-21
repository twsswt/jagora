package uk.ac.glasgow.jagora.trader.impl.random;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.trader.Level1Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;
import uk.ac.glasgow.jagora.util.Random;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Places buy and sell limit orders on markets randomly. Buy
 * (sell) orders prices are selected at within a relative
 * range of the current best offer (bid) for a traded stock.
 * Quantities are selected randomly from a specified range
 * for the traded stock, constrained within the agent's
 * available inventory (sell orders) and available cash for
 * the random price (buy orders).
 * 
 * @author Tim
 *
 */
public class RandomTrader extends SafeAbstractTrader implements Level1Trader {

	
	private final Map<Stock,RelativeRangeData> sellRangeData;
	private final Map<Stock,RelativeRangeData> buyRangeData;

	protected final Random random;

	
	public RandomTrader(
		String name, Long cash, Map<Stock, Integer> inventory,
		Random random,
		Map<Stock,RelativeRangeData> sellRangeDatas, Map<Stock,RelativeRangeData> buyRangeDatas) {
		
		super(name, cash, inventory);
		this.random = random;
		this.sellRangeData = new HashMap<Stock,RelativeRangeData>(sellRangeDatas);
		this.buyRangeData = new HashMap<Stock,RelativeRangeData>(buyRangeDatas);
	}

	@Override
	public void speak(StockExchangeLevel1View traderMarketView) {
		Stock randomStock = random.chooseElement(sellRangeData.keySet());


		if (random.nextBoolean())
			performRandomSellAction(randomStock, traderMarketView);
		else 
			performRandomBuyAction(randomStock, traderMarketView);
	}

	private void performRandomSellAction(
		Stock stock, StockExchangeLevel1View stockExchangeLevel1View) {
		
		Integer uncommittedQuantity = 
			getAvailableQuantity(stock);
		RelativeRangeData relativeRangeData = sellRangeData.get(stock);
		
		Integer quantity = createRandomQuantity(uncommittedQuantity, relativeRangeData);

		if (quantity > 0){
			
			Long offerPrice = stockExchangeLevel1View.getLastKnownBestOfferPrice(stock);
			
			if (offerPrice == null) return;
			Long price = createRandomPrice(offerPrice, relativeRangeData);

			SellOrder sellOrder =
				new LimitSellOrder(this, stock, quantity, price);
						
			placeSafeSellOrder(stockExchangeLevel1View, sellOrder);
			
		} else {
			SellOrder randomSellOrder = random.chooseElement(openSellOrders);

			if (randomSellOrder != null)
				cancelSafeSellOrder(stockExchangeLevel1View, randomSellOrder);
		}
	}

	private void performRandomBuyAction(
		Stock stock, StockExchangeLevel1View stockExchangeLevel1View) {
		
		Long bestBidPrice = stockExchangeLevel1View.getLastKnownBestBidPrice(stock);
		if (bestBidPrice == null) return;
		RelativeRangeData relativeRangeData = buyRangeData.get(stock);
		Long price = createRandomPrice( bestBidPrice, relativeRangeData);

		Long availableCash = getAvailableCash();
		
		Integer quantity = createRandomQuantity( (int)(availableCash/price), relativeRangeData);
		if (quantity > 0){
			
			BuyOrder buyOrder =
				new LimitBuyOrder(this, stock, quantity, price);

			placeSafeBuyOrder(stockExchangeLevel1View, buyOrder);
			
		} else {
			BuyOrder buyOrder = random.chooseElement(openBuyOrders);
			if (buyOrder != null)
				cancelSafeBuyOrder(stockExchangeLevel1View, buyOrder);
		}
	}

    /**
     *
     * @param midPoint
     * @param relativeRangeData
     * @return a random price deviating around midpoint within stock's range of prices
     */
	private Long createRandomPrice( Long midPoint, RelativeRangeData relativeRangeData) {

		Long relativePriceRange = relativeRangeData.high - relativeRangeData.low;
		Long randomPrice = 
			(long)(random.nextDouble() *  relativePriceRange) + relativeRangeData.low + midPoint;
		
		return max(randomPrice, 0l);
	}

    /**
     *
     *
     * @param ceiling
     * @param relativeRangeData
     * @return random quantity within stock's RangeData
     */
	private Integer createRandomQuantity( Integer ceiling, RangeData relativeRangeData) {
		
		Integer relativeRange = 
			relativeRangeData.maxQuantity-relativeRangeData.minQuantity;
		
		Integer randomQuantity = 
			random.nextInt(relativeRange) + relativeRangeData.minQuantity;

		return min(randomQuantity, ceiling);
	}
}