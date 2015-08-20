package uk.ac.glasgow.jagora.trader.impl.marketmaker;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

/**
 * Used to hold market information about a particular stock.
 */
public class MarketDatum {

	private Long lastPriceTraded = null;
	private Boolean lastTradeWasSell;

	Map<Long,Integer> buySideLiquidity = new HashMap<Long,Integer>();
	Map<Long,Integer> sellSideLiquidity = new HashMap<Long,Integer>();

	public MarketDatum() {}

	protected void addBuySideLiquidity(Integer quantity, Long price){
		Integer currentLiquidityAtPrice = buySideLiquidity.getOrDefault(price, 0);
		currentLiquidityAtPrice += quantity;
		buySideLiquidity.put(price, currentLiquidityAtPrice);
	}

	protected void addSellSideLiquidity(Integer quantity, Long price){
		Integer currentLiquidityAtPrice = sellSideLiquidity.getOrDefault(price, 0);
		currentLiquidityAtPrice += quantity;
		sellSideLiquidity.put(price, currentLiquidityAtPrice);
	}

	protected void removeBuySideLiquidity(Integer quantity, Long price){
		Integer currentLiquidityAtPrice = buySideLiquidity.getOrDefault(price, 0);
		currentLiquidityAtPrice -= quantity;
		buySideLiquidity.put(price, currentLiquidityAtPrice);
	}
	
	protected void removeSellSideLiquidity (Integer quantity, Long price){
		Integer currentLiquidityAtPrice = sellSideLiquidity.getOrDefault(price, 0);
		currentLiquidityAtPrice -= quantity;
		sellSideLiquidity.put(price, currentLiquidityAtPrice);
	}

	protected void setLastPriceTraded(Long lastPriceTraded) {
		this.lastPriceTraded = lastPriceTraded;
	}
	
	protected Long getLastPriceTraded() {
		return lastPriceTraded;
	}

	protected void setLastTradeDirection(Boolean lastTradeWasSell) {this.lastTradeWasSell = lastTradeWasSell;}

	@Override
	public String toString() {
		
		String template = "[buySideLiquidity=%d, sellSideLiquidity=%d]";
		
		return format(template, this.buySideLiquidity, this.sellSideLiquidity);
	}

	public Boolean lastTradeWasSell() {
		return lastTradeWasSell;
	}

	public Integer getTotalBuySideDepth (){
		return getBuySideDepth(0l);
	}
	
	public Integer getTotalSellSideDepth (){
		return getSellSideDepth(Long.MAX_VALUE);
	}
	
	public Integer getBuySideDepth(Long price) {			
		return getMarketLiquidity(buySideLiquidity, entry -> entry.getKey() >= price);
	}
	
	public Integer getSellSideDepth(Long price) {		
		return getMarketLiquidity(sellSideLiquidity, entry -> entry.getKey() <= price);
	}	

	private Integer getMarketLiquidity(Map<Long, Integer> side, Predicate<? super Entry<Long, Integer>> predicate) {
		return side
			.entrySet()
			.stream()
			.filter(predicate)
			.mapToInt(entry-> entry.getValue())
			.sum();
	}

}
