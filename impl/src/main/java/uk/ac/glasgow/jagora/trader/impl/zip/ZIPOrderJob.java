package uk.ac.glasgow.jagora.trader.impl.zip;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.TargetPriceAction.INCREASE;
import static uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.TargetPriceAction.NOTHING;
import static uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.TargetPriceAction.REDUCE;
import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;

/**
 * Provides the functionality for managing a job according to the ZIP algorithm (Cliff 97) for buying and selling.
 * @author Tim
 *
 * @param <T>
 */
public abstract class ZIPOrderJob<T extends Order> {
	
	protected enum TargetPriceAction {REDUCE, INCREASE, NOTHING}

	public final ZIPTrader zipTrader;
	public final MarketDatum marketDatum;
	
	private final Long lowLimit;
	private final Long highLimit;
					
	private Long targetPrice;
	private Long lastTargetPrice;
	
	protected T managedOrder;
	
	protected ZIPOrderJob (
		ZIPTrader zipTrader, MarketDatum marketDatum, Long lowLimit, Long highLimit, Long initialTargetPrice){
		
		this.zipTrader = zipTrader;
		this.marketDatum = marketDatum;	
		
		marketDatum.registerOrderJob(this);
		
		this.lowLimit = lowLimit;
		this.highLimit = highLimit;
		
		targetPrice =  initialTargetPrice;
		lastTargetPrice = targetPrice;
		
		managedOrder = createNewOrder(targetPrice);	
	}
			
	protected void updateOrder (StockExchangeLevel1View level1View) {
		
		Long unconstrainedPrice = zipTrader.getNextOrderPrice(lastTargetPrice, targetPrice, managedOrder.getPrice());

		Long constrainedPrice = max (lowLimit,  min (unconstrainedPrice, highLimit));
		
		T newOrder = createNewOrder(constrainedPrice);
		placeOrder(newOrder, level1View);
		managedOrder = newOrder;
	}
	
	public Long getTargetPrice() {
		return targetPrice;
	}
		
	protected abstract T createNewOrder (Long price);
	
	protected abstract void placeOrder (T order, StockExchangeLevel1View level1View) ;
			
	protected void updateTargetPrice (){

		TargetPriceAction targetPriceAction = getTargetPriceAction ();			
		
		if (targetPriceAction == NOTHING) return;
		else {
			lastTargetPrice = targetPrice;
			Long basePrice = marketDatum.lastPriceReportedOnTheMarket;
			targetPrice = zipTrader.computeTargetPrice (basePrice, targetPriceAction);
		}
	}
			
	protected abstract TargetPriceAction getTargetPriceAction ();

	protected Boolean isFilled() {
		return managedOrder.isFilled();
	}
	
	@Override
	public String toString (){
		String template = 
			"[managing=%s,target=%2d]";
		
		return format(
			template, managedOrder, targetPrice);
	}

	public static class ZIPBuyOrderJob extends ZIPOrderJob<BuyOrder> {

		protected ZIPBuyOrderJob(
			ZIPTrader zipTrader, MarketDatum marketDatum, Long floorPrice, Long limitPrice, Long initialTargetPrice) {
			super(zipTrader, marketDatum, floorPrice, limitPrice, initialTargetPrice);
		}

		@Override
		protected TargetPriceAction getTargetPriceAction() {
			Boolean priceIsCompetitive = 
				managedOrder.getPrice() >= marketDatum.lastPriceReportedOnTheMarket;
						
			if (marketDatum.lastQuoteWasAccepted){
				if (priceIsCompetitive)
					return REDUCE;
				else if (marketDatum.lastQuoteWasOffer())
					return INCREASE;
					
			} else if (marketDatum.lastQuoteWasBid() && !priceIsCompetitive)
				return INCREASE;
			
			return NOTHING;
		}

		@Override
		protected BuyOrder createNewOrder(Long price) {
			return new LimitBuyOrder(zipTrader, marketDatum.stock, 1, price);			
		}

		@Override
		protected void placeOrder(BuyOrder order, StockExchangeLevel1View level1View) {
			if (managedOrder != null) 
				level1View.cancelBuyOrder(managedOrder);
			level1View.placeBuyOrder(order);	
		}

	}

	public static class ZIPSellOrderJob extends ZIPOrderJob<SellOrder> {

		protected ZIPSellOrderJob(
			ZIPTrader zipTrader, MarketDatum marketDatum, Long limitPrice, Long ceilPrice, Long initialTargetPrice) {
			super(zipTrader, marketDatum, limitPrice, ceilPrice, initialTargetPrice);
		}

		@Override
		protected SellOrder createNewOrder(Long price) {
			return new LimitSellOrder(zipTrader, marketDatum.stock, 1, price);
		}

		@Override
		protected TargetPriceAction getTargetPriceAction() {
						
			Boolean priceIsCompetitive = 
				managedOrder.getPrice() <= marketDatum.lastPriceReportedOnTheMarket;
						
			if (marketDatum.lastQuoteWasAccepted){
				
				if (priceIsCompetitive)
					return INCREASE;
				else if (marketDatum.lastQuoteWasBid()) //how is it going to be a bid and the price not competitive??
					return REDUCE;
					
			} else if (marketDatum.lastQuoteWasOffer() && !priceIsCompetitive)
				return REDUCE;
			
			return NOTHING;
		}

		@Override
		protected void placeOrder(SellOrder order, StockExchangeLevel1View level1View) {
			if (managedOrder != null)
				level1View.cancelSellOrder(managedOrder);			
			level1View.placeSellOrder(order);
		}
	}

}