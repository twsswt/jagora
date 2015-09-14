package uk.ac.glasgow.jagora.trader.ivo.impl;


import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;

import java.util.Map;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

public class HighFrequencyRandomTrader extends SafeAbstractTrader implements Level2Trader,TradeListener {

	private final PercentageRangeData buyRangeData;
	private final PercentageRangeData sellRangeData;

	protected final Random random;

	private final Stock stock;

	private LimitBuyOrder currentBuyOrder;
	private LimitSellOrder currentSellOrder;

	private Long lastPriceTraded = null;

	HighFrequencyRandomTrader(
		String name, Long cash, Map<Stock, Integer> inventory,
		PercentageRangeData buyRangeData, PercentageRangeData sellRangeData,
		Random random) {
		
		super(name, cash, inventory);
		
		this.buyRangeData = buyRangeData;
		this.sellRangeData = sellRangeData;
		this.random = random;
		this.stock = buyRangeData.stock;
	}

	@Override
	public void speak(StockExchangeLevel2View level2View) {
		level2View.registerTradeListener(this);

		cancelOrders(level2View);

		if (random.nextBoolean())
			performRandomSellAction(level2View);
		else
			performRandomBuyAction(level2View);
	}

	private void cancelOrders(StockExchangeLevel2View level2View) {
		if (currentSellOrder != null){
			cancelSafeSellOrder(level2View,currentSellOrder);
			currentSellOrder = null;
		}

		if (currentBuyOrder != null ) {
			cancelSafeBuyOrder(level2View,currentBuyOrder);
			currentBuyOrder = null;
		}

	}

	private void performRandomBuyAction( StockExchangeLevel2View level2View) {
		Long price = createRandomPrice(level2View.getLastKnownBestBidPrice(stock),buyRangeData);
		Integer quantity = createRandomQuantity( (int) (getAvailableCash()/price),buyRangeData);

		LimitBuyOrder limitBuyOrder = new DefaultLimitBuyOrder(this,stock, quantity, price);
		currentBuyOrder = placeSafeBuyOrder(level2View,limitBuyOrder) ? limitBuyOrder : null;
	}

	private void performRandomSellAction( StockExchangeLevel2View level2View) {

		Long price = createRandomPrice(level2View.getLastKnownBestOfferPrice(stock),sellRangeData);
		Integer quantity = createRandomQuantity(inventory.get(stock), sellRangeData);

		LimitSellOrder limitSellOrder = new DefaultLimitSellOrder(this,stock,quantity,price);
		currentSellOrder = placeSafeSellOrder(level2View,limitSellOrder) ? limitSellOrder :null;
	}

	private Integer createRandomQuantity(Integer upperLimit, PercentageRangeData percentageRangeData) {
		Integer relativeRange =
				percentageRangeData.maxQuantity-percentageRangeData.minQuantity;

		Integer randomQuantity =
				random.nextInt(relativeRange) + percentageRangeData.minQuantity;

		return min(randomQuantity, upperLimit);
	}

	private Long createRandomPrice(Long midPoint, PercentageRangeData percentageRangeData) {
		Double relativePriceRange =
				(percentageRangeData.high - percentageRangeData.low) * midPoint * random.nextDouble();		
		
		Long randomPrice =
				round(relativePriceRange) + midPoint +
						round(percentageRangeData.low * midPoint);

		return max(randomPrice, 0l);
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		lastPriceTraded = tradeExecutionEvent.price;
	}


}
