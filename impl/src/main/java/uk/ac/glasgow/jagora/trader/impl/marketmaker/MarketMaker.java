package uk.ac.glasgow.jagora.trader.impl.marketmaker;

import static java.lang.Math.max;
import static java.lang.String.format;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;

/**
 * Implements an altruistic market making algorithm that (a)
 * 'guarantees' the presence of a bid-ask order pair on a
 * managed market and (b) attempts to maintain a constant
 * share on the market.
 * 
 * @author tws
 *
 */
public class MarketMaker extends SafeAbstractTrader  implements Level2Trader {

	/**
	 * Manages the market makers position for a single stock.
	 */
	class MarketPosition {

		public final MarketPositionSpecification marketPositionSpecification;

		private LimitBuyOrder currentLimitBuyOrder;
		private LimitSellOrder currentLimitSellOrder;

		protected MarketPosition(MarketPositionSpecification marketPositionSpecification) {
			this.marketPositionSpecification = marketPositionSpecification;
		}

		public void updateMarketPosition (StockExchangeLevel2View level2View){
			
			updateBuyPosition(level2View);
			updateSellPosition(level2View);
		}

		private void updateBuyPosition(
			StockExchangeLevel2View level2View) {

			Integer targetLiquidity = marketPositionSpecification.targetLiquidity;
			Integer targetInventory = marketPositionSpecification.targetQuantity;
			Stock stock = marketPositionSpecification.stock;
			
			Long bestBidPrice = level2View.getBestBidPrice(stock);
			if (bestBidPrice == null) bestBidPrice = 1l;
			
			Integer buySideDepth = getBuySideDepth(level2View, 1l);
						
			Integer quantityNeededForTargetLiquidity = targetLiquidity - buySideDepth;
			
			Integer quantityNeededForTargetInventory = targetInventory - inventory.get(stock);
			
			Integer quantity =
				max(1, max (quantityNeededForTargetLiquidity, quantityNeededForTargetInventory));
			
			Double priceFactor =
				1.0 - (double)(quantity - quantityNeededForTargetInventory) / targetLiquidity;
						
			Long price = 
				max(1l, (long)(bestBidPrice * priceFactor));

			if (currentLimitBuyOrder != null)
				level2View.cancelLimitBuyOrder(currentLimitBuyOrder);
			currentLimitBuyOrder = new DefaultLimitBuyOrder(MarketMaker.this, stock, quantity, price);
			level2View.placeLimitBuyOrder(currentLimitBuyOrder);
		}

		private void updateSellPosition(
			StockExchangeLevel2View level2View) {
			
			Integer targetLiquidity = marketPositionSpecification.targetLiquidity;
			Integer targetInventory = marketPositionSpecification.targetQuantity;
			Stock stock = marketPositionSpecification.stock;
			
			Long bestOfferPrice = level2View.getBestOfferPrice(stock);
			if (bestOfferPrice == null) bestOfferPrice = Long.MAX_VALUE;
			
			Integer sellSideDepth = getSellSideDepth(level2View, Long.MAX_VALUE);
						
			Integer quantityNeededForTargetLiquidity = targetLiquidity - sellSideDepth;

			Integer quantityNeededForTargetInventory = inventory.get(stock) - targetInventory;
			
			Integer quantity = 
				max( 1, max (quantityNeededForTargetLiquidity, quantityNeededForTargetInventory));
						
			Double priceFactor = 1.0 + (double)(quantity - quantityNeededForTargetInventory) / targetLiquidity;
						
			Long price = (long)(bestOfferPrice * priceFactor);
			
			if (currentLimitSellOrder != null)
				level2View.cancelLimitSellOrder(currentLimitSellOrder);

			currentLimitSellOrder = new DefaultLimitSellOrder(MarketMaker.this, stock, quantity, price);
			level2View.placeLimitSellOrder(currentLimitSellOrder);
		}
		
		@Override
		public String toString() {
			
			String template = "[buy at=%s, sell at=%s]";
			
			return format(template, currentLimitBuyOrder, currentLimitSellOrder);
		}
		
		private Integer getBuySideDepth(StockExchangeLevel2View level2View, Long price) {	
			
			Stock stock = marketPositionSpecification.stock;
			
			return getMarketDepth(
				level2View.getBuyLimitOrders(stock), entry -> entry.getLimitPrice() >= price);
		}
		
		private Integer getSellSideDepth(StockExchangeLevel2View level2View, Long price) {		
			
			Stock stock = marketPositionSpecification.stock;
			
			return getMarketDepth(
				level2View.getSellLimitOrders(stock), entry -> entry.getLimitPrice() <= price);
		}	

		private Integer getMarketDepth(
			List<? extends LimitOrder> side,
			Predicate<? super LimitOrder> predicate) {
			
			return side
				.stream()
				.filter(predicate)
				.mapToInt(entry-> entry.getRemainingQuantity())
				.sum();
		}

	}

	private Map<Stock,MarketPosition> marketPositions;

	MarketMaker (
		String name, Long cash, Map<Stock, Integer> inventory,
		Set<MarketPositionSpecification> marketPositionSpecifications){

		super(name,cash,inventory);
		
		marketPositions = new HashMap<Stock,MarketPosition>();
				
		marketPositionSpecifications
			.stream()
			.forEach(mps -> marketPositions.put(mps.stock, new MarketPosition(mps))); 
		
	}

	@Override
	public void speak(StockExchangeLevel2View level2View) {
				
		marketPositions
			.entrySet()
			.stream()
			.map(entry -> entry.getValue())
			.forEach(marketPosition -> marketPosition.updateMarketPosition(level2View));

	}

}

