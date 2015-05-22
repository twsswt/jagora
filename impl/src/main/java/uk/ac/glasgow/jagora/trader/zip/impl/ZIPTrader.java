package uk.ac.glasgow.jagora.trader.zip.impl;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static uk.ac.glasgow.jagora.trader.zip.impl.ZIPTrader.TargetPriceAction.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;

public class ZIPTrader extends SafeAbstractTrader implements Level2Trader, TradeListener, OrderListener {

	protected enum TargetPriceAction {REDUCE, INCREASE, NOTHING}
	
	protected abstract class OrderJob<T extends Order> {
		
		public final Stock stock;
		
		private final Double lowLimit;
		private final Double highLimit;
						
		protected Double lastPriceReportedOnTheMarket;
		protected Boolean lastQuoteWasAccepted;
		
		private Boolean lastQuoteWasOffer;
		protected Boolean lastQuoteWasOffer (){return lastQuoteWasOffer;}
		protected Boolean lastQuoteWasBid (){return !lastQuoteWasOffer;}
		
		private Double targetPrice;
		
		protected T managedOrder;
		
		public OrderJob (Stock stock, Double lowLimit, Double highLimit){
			this.lowLimit = lowLimit;
			this.highLimit = highLimit;
			this.stock = stock;
			
			targetPrice =  random.nextDouble() * (highLimit - lowLimit) + lowLimit;	
			managedOrder = createNewOrder(targetPrice);
			
		}
		
		protected void updateOrder (StockExchangeLevel1View level1View){
			
			Double constrainedPrice = getNextOrderPrice();
			
			T newOrder = createNewOrder(constrainedPrice);
			placeOrder(newOrder, level1View);
			managedOrder = newOrder;
		}
		
		private Double getNextOrderPrice() {
			
			Double lastOrderPrice = managedOrder.getPrice();
			Double unconstrainedPrice = (1 - learningRate) * lastOrderPrice + learningRate * targetPrice;
			Double constrainedPrice = max (lowLimit,  min (unconstrainedPrice, highLimit));
			return constrainedPrice;
		}
		
		protected abstract T createNewOrder (Double price);
		
		protected abstract void placeOrder (T order, StockExchangeLevel1View level1View);
		
		protected void updateMarketInformationFollowingOrder(Double price, Boolean isOffer) {
			lastPriceReportedOnTheMarket = price;
			lastQuoteWasOffer = isOffer;
			lastQuoteWasAccepted = false;
			updateTargetPrice();
		}

		protected void updateMarketInformationFollowingTrade() {
			lastQuoteWasAccepted = true;
			updateTargetPrice();
		}
		
		private void updateTargetPrice (){

			TargetPriceAction targetPriceAction = getTargetPriceAction ();

			Double relativeChange = null;
			Double absoluteChange = null;
			Double basePrice = null;
			
			if (targetPriceAction.equals(REDUCE)){
		
				basePrice = lastPriceReportedOnTheMarket;
				relativeChange = random.nextDouble() * -maximumRelativeChange;
				absoluteChange = random.nextDouble() * -maximumAbsoluteChange;
				
			} else if (targetPriceAction.equals(INCREASE)){

				basePrice = lastPriceReportedOnTheMarket;
				relativeChange = random.nextDouble() * maximumRelativeChange;
				absoluteChange = random.nextDouble() * maximumAbsoluteChange;
				
			} else if (targetPriceAction.equals(NOTHING)){
				
				basePrice = targetPrice;				
				relativeChange = 0.0;
				absoluteChange = 0.0;				
			}

			targetPrice = basePrice * (1.0 + relativeChange) + absoluteChange;
		}
				
		protected abstract TargetPriceAction getTargetPriceAction ();

		protected Boolean isFilled() {
			return managedOrder.isFilled();
		}
		
		@Override
		public String toString (){
			String template = 
				"[managing=%s,target=%.2f]";
			
			return format(
				template, managedOrder, targetPrice);
		}
		
	}
	
	protected class BuyOrderJob extends OrderJob<BuyOrder> {
		
		public BuyOrderJob(Stock stock, Double floorPrice, Double limitPrice) {
			super(stock, floorPrice, limitPrice);
		}

		@Override
		protected TargetPriceAction getTargetPriceAction() {
			
			Boolean priceIsCompetitive = 
				managedOrder.getPrice() >= lastPriceReportedOnTheMarket;
						
			if (lastQuoteWasAccepted){
				
				if (priceIsCompetitive)
					return REDUCE;
				else if (lastQuoteWasOffer())
					return INCREASE;
					
			} else if (lastQuoteWasBid() && !priceIsCompetitive)
				return INCREASE;
			
			return NOTHING;
		}

		@Override
		protected BuyOrder createNewOrder(Double price) {
			return new LimitBuyOrder(ZIPTrader.this, stock, 1, price);			
		}

		@Override
		protected void placeOrder(BuyOrder order, StockExchangeLevel1View level1View) {
			if (managedOrder != null) 
				level1View.cancelBuyOrder(managedOrder);
			level1View.placeBuyOrder(order);	
		}
	
	}
	
	public class SellOrderJob extends OrderJob<SellOrder> {

		public SellOrderJob(Stock stock, Double limitPrice, Double ceilPrice) {
			super(stock, limitPrice, ceilPrice);
		}

		@Override
		protected SellOrder createNewOrder(Double price) {
			return new LimitSellOrder(ZIPTrader.this, stock, 1, price);
		}

		@Override
		protected TargetPriceAction getTargetPriceAction() {
						
			Boolean priceIsCompetitive = 
				managedOrder.getPrice() <= lastPriceReportedOnTheMarket;
						
			if (lastQuoteWasAccepted){
				
				if (priceIsCompetitive)
					return INCREASE;
				else if (lastQuoteWasBid())
					return REDUCE;
					
			} else if (lastQuoteWasOffer() && !priceIsCompetitive)
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

	private Random random;
	
	private Double maximumRelativeChange;
	private Double maximumAbsoluteChange;
	private Double learningRate;
	
	private OrderJob<?> currentOrderJob;
	
	private Queue<OrderJobSpecification<?>> orderJobsSpecifications;

	public ZIPTrader(
		String name,
		Double cash,
		Map<Stock, Integer> inventory,
		Random random,
		Double maximumRelativeChange,
		Double maximumAbsoluteChange, 
		Double learningRate,
		List<OrderJobSpecification<?>> orderJobs) {

		super(name, cash, inventory);
		
		this.random = random;

		this.maximumRelativeChange = maximumRelativeChange;
		this.maximumAbsoluteChange = maximumAbsoluteChange;
		this.learningRate = learningRate;
		
		this.orderJobsSpecifications =
			new LinkedList<OrderJobSpecification<?>>(orderJobs);
		
		updateCurrentOrderJob();
	}

	@Override
	public void speak(StockExchangeLevel2View level2View) {
		level2View.registerOrderListener(this);
		level2View.registerTradeListener(this);
		updateCurrentOrderJob ();
		if (!currentOrderJobIsFinished())
			currentOrderJob.updateOrder(level2View);
		
	}
		
	private void updateCurrentOrderJob() {
		while (
			currentOrderJobIsFinished() && 
			!orderJobsSpecifications.isEmpty()){
			currentOrderJob = orderJobsSpecifications.poll().createOrderJob(this);
		}
	}

	private Boolean currentOrderJobIsFinished() {
		return currentOrderJob == null || currentOrderJob.isFilled();
	}

	@Override
	public void orderEntered(OrderEntryEvent orderEntryEvent) {
		if (
			currentOrderJob != null && 
			currentOrderJob.stock.equals(orderEntryEvent.stock) && 
			!orderEntryEvent.trader.equals(this)){
			
			currentOrderJob.updateMarketInformationFollowingOrder(
				orderEntryEvent.price, orderEntryEvent.isOffer);
		}
		
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		if (currentOrderJob != null && 
			currentOrderJob.stock.equals(tradeExecutionEvent.stock))
			currentOrderJob.updateMarketInformationFollowingTrade();
	}

}
