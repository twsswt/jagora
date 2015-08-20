package uk.ac.glasgow.jagora.trader.impl.marketmaker;

import static java.lang.Math.round;
import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.ticker.OrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * This implementation of MarketMaker will only work for a single stock
 * on a single exchange!- multiple stocks and exchanges are  confusing the algorithm.
 * The provided stockWarehouse should be
 * consistent throughout the whole usage of the environment to ensure proper
 * functioning of the algorithm.
 */
public class MarketMaker extends SafeAbstractTrader implements Level2Trader,TradeListener, OrderListener {

	private final Stock stock;

	private Random random;

	private MarketDatum marketDatum;
	private StockPositionDatum positionDatum ;

	private Set<StockExchangeLevel2View> registered;

	private Double spreadPercentage;

	private Double inventoryAdjustmentInfluence;
	private Double liquidityAdjustmentInfluence;


	MarketMaker (
		String name, Long cash, Map<Stock, Integer> inventory,
		Stock stock, Integer targetQuantity,
		Random random, Double spreadPercentage,
		Double inventoryAdjustmentInfluence,
		Double liquidityAdjustmentInfluence){

		super(name,cash,inventory);

		this.spreadPercentage = spreadPercentage;
		this.random = random;

		this.stock = stock;
		
		marketDatum = new MarketDatum();
		
		positionDatum =
			new StockPositionDatum(stock, targetQuantity);

		registered = new HashSet<StockExchangeLevel2View>();

		this.inventoryAdjustmentInfluence = inventoryAdjustmentInfluence;
		this.liquidityAdjustmentInfluence = liquidityAdjustmentInfluence;

	}

	/**
	 * At the moments some trades must have occurred before speak works properly
	 * @param level2View
	 */
	@Override
	public void speak(StockExchangeLevel2View level2View){
		if (!registered.contains(level2View)) register (level2View);

		//update all positions on market
		updateMarketPositions();

		changeMarketPosition(level2View);
	}


	private void register(StockExchangeLevel2View level2View) {
		level2View.registerOrderListener(this);
		level2View.registerTradeListener(this);
		registered.add(level2View);
	}

	private void changeMarketPosition(StockExchangeLevel1View level1View) {

		//if some of these positions are not set yet don't place anything on the market
		if (positionDatum.newBuyPrice == 0l || positionDatum.newSellPrice == 0l)
			return;


		if (positionDatum.currentBuyOrder != null)
			cancelSafeBuyOrder(level1View,positionDatum.currentBuyOrder);

		Integer buyQuantity = positionDatum.targetQuantity;
		
		if (positionDatum.inventoryAdjustment < -2) {
			//if there' a big imbalance provide a stub quote to preserve inventory
			buyQuantity = Math.round(positionDatum.targetQuantity*0.01f);
		}
		
		Integer cashLimit = (int) (getAvailableCash().doubleValue()/positionDatum.newBuyPrice.doubleValue());
		buyQuantity = Math.min(buyQuantity,cashLimit);

		BuyOrder buyOrder = new LimitBuyOrder
				(this,positionDatum.stock,buyQuantity,positionDatum.newBuyPrice);

		positionDatum.currentBuyOrder = placeSafeBuyOrder(level1View,buyOrder) ? buyOrder: null;
		//TODO make some sort of exception if null

		if (positionDatum.currentSellOrder != null)
			cancelSafeSellOrder(level1View,positionDatum.currentSellOrder);

		Integer sellQuantity = inventory.get(stock);
		if (positionDatum.inventoryAdjustment > 0.66){
			//if there' a big imbalance provide a stub quote to preserve inventory
			sellQuantity = Math.round(inventory.get(stock)*0.1f);
		}

		SellOrder sellOrder = new LimitSellOrder
				(this, positionDatum.stock,sellQuantity,positionDatum.newSellPrice);

		positionDatum.currentSellOrder = placeSafeSellOrder(level1View,sellOrder) ?sellOrder :null;
	}


	private void updateMarketPositions () {

		//if no available information about last trade, don't place orders yet
		if (marketDatum.getLastPriceTraded() == null)
			return;

		positionDatum.spread = Math.round(spreadPercentage*marketDatum.getLastPriceTraded().doubleValue());


		Long priceLiquidityAdjustment = liquidityPriceCalculation();


		Long inventoryPriceAdjustment = inventoryPriceCalculation();

		if (marketDatum.lastTradeWasSell()) {
			positionDatum.setNewBuyPrice (marketDatum.getLastPriceTraded() - positionDatum.spread
					+ priceLiquidityAdjustment + inventoryPriceAdjustment);

			positionDatum.setNewSellPrice (marketDatum.getLastPriceTraded() + positionDatum.spread
			+ inventoryPriceAdjustment);
		}
		else {
			positionDatum.setNewBuyPrice (marketDatum.getLastPriceTraded() - positionDatum.spread
			+ inventoryPriceAdjustment);

			positionDatum.setNewSellPrice(marketDatum.getLastPriceTraded() + positionDatum.spread +
					priceLiquidityAdjustment + inventoryPriceAdjustment);
		}

		if (positionDatum.newBuyPrice >= positionDatum.newSellPrice)
			fixPriceAnomalies();

	}

	private Long liquidityPriceCalculation () {
	
		Integer totalBuySideLiquidity = marketDatum.getTotalBuySideDepth();
		Integer totalSellSideLiquidity = marketDatum.getTotalSellSideDepth();
		
		Double liquidityAdjustment = (totalBuySideLiquidity - totalSellSideLiquidity) /
			(double) totalBuySideLiquidity;
				

		return round(
			liquidityAdjustment * positionDatum.spread * random.nextDouble() * liquidityAdjustmentInfluence);
	}

	private Long inventoryPriceCalculation () {
		
		Double inventoryAdjustment =
				(positionDatum.targetQuantity - inventory.get(positionDatum.stock) )
						/ positionDatum.targetQuantity.doubleValue();
		
		positionDatum.setInventoryAdjustment(inventoryAdjustment);
		
		Double toReturn =
			inventoryAdjustment * positionDatum.spread.doubleValue() * random.nextDouble() * inventoryAdjustmentInfluence;

		return  round(toReturn);
	}

	private void fixPriceAnomalies () {
		//isolate the intervening price and fix it
		if (positionDatum.newSellPrice < marketDatum.getLastPriceTraded())
			positionDatum.newSellPrice = positionDatum.newBuyPrice + 1l;
		else
			positionDatum.newBuyPrice = positionDatum.newSellPrice - 1l;

	}

	@Override
	public void orderEntered(OrderEvent orderEvent) {
		if (orderEvent.orderDirection == OrderEvent.OrderDirection.BUY) {
			marketDatum.addBuySideLiquidity(orderEvent.quantity,orderEvent.price);
		}
		else {
			marketDatum.addSellSideLiquidity(orderEvent.quantity,orderEvent.price);
		}
	}

	@Override
	public void orderCancelled(OrderEvent orderEvent) {
		if (orderEvent.orderDirection == OrderEvent.OrderDirection.SELL)
			marketDatum.removeSellSideLiquidity(orderEvent.quantity, orderEvent.price);
		else
			marketDatum.removeBuySideLiquidity(orderEvent.quantity, orderEvent.price);
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		marketDatum.setLastPriceTraded(tradeExecutionEvent.price);
		marketDatum.removeBuySideLiquidity(tradeExecutionEvent.quantity, tradeExecutionEvent.price);
		marketDatum.removeSellSideLiquidity(tradeExecutionEvent.quantity, tradeExecutionEvent.price);

		//TODO - marketDatum.setLastTradeDirection();
	}

}
