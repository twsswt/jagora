package uk.ac.glasgow.jagora.trader.impl.random;


import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.ticker.OrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;
import uk.ac.glasgow.jagora.util.Random;

import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

public class HighFrequencyRandomTrader extends SafeAbstractTrader implements Level2Trader,TradeListener, OrderListener {

	private final PercentageRangeData buyRangeData;
	private final PercentageRangeData sellRangeData;

	protected final Random random;

	private final Stock stock;

	private BuyOrder currentBuyOrder;
	private SellOrder currentSellOrder;

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

		BuyOrder buyOrder = new LimitBuyOrder(this,stock, quantity, price);
		currentBuyOrder = placeSafeBuyOrder(level2View,buyOrder) ? buyOrder : null;
	}

	private void performRandomSellAction( StockExchangeLevel2View level2View) {

		Long price = createRandomPrice(level2View.getLastKnownBestOfferPrice(stock),sellRangeData);
		Integer quantity = createRandomQuantity(inventory.get(stock), sellRangeData);

		SellOrder sellOrder = new LimitSellOrder(this,stock,quantity,price);
		currentSellOrder = placeSafeSellOrder(level2View,sellOrder) ? sellOrder :null;
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
	public void orderEntered(OrderEvent orderEvent) {

	}

	@Override
	public void orderCancelled(OrderEvent orderEvent) {

	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		lastPriceTraded = tradeExecutionEvent.price;
	}
}
