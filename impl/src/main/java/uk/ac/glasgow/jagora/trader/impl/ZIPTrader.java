package uk.ac.glasgow.jagora.trader.impl;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static uk.ac.glasgow.jagora.trader.impl.ZIPTrader.TargetPriceAction.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

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

public class ZIPTrader extends SafeAbstractTrader implements Level2Trader, TradeListener, OrderListener {

	protected enum TargetPriceAction {REDUCE, INCREASE, NOTHING}
		
	protected abstract class OrderJob<T extends Order> {
		
		public final Stock stock;
		
		private final Long lowLimit;
		private final Long highLimit;
						
		protected Long lastPriceReportedOnTheMarket;
		protected Boolean lastQuoteWasAccepted;
		
		private Boolean lastQuoteWasOffer;
		protected Boolean lastQuoteWasOffer (){return lastQuoteWasOffer;}
		protected Boolean lastQuoteWasBid (){return !lastQuoteWasOffer;}
		
		private Long targetPrice;
		
		protected T managedOrder;
		
		public OrderJob (Stock stock, Long lowLimit, Long highLimit, OrderJob<? extends Order> lastOrderJob){
			this.lowLimit = lowLimit;
			this.highLimit = highLimit;
			this.stock = stock;
			
			targetPrice =  lastOrderJob.targetPrice;
			lastPriceReportedOnTheMarket = lastOrderJob.lastPriceReportedOnTheMarket;
			lastQuoteWasAccepted = lastOrderJob.lastQuoteWasAccepted;
			lastQuoteWasOffer = lastOrderJob.lastQuoteWasOffer;
			
			managedOrder = createNewOrder(targetPrice);	
		}
		
		public OrderJob (Stock stock, Long lowLimit, Long highLimit){this.lowLimit = lowLimit;
			this.highLimit = highLimit;
			this.stock = stock;
			targetPrice =   (long) (random.nextDouble() * (highLimit - lowLimit)) + lowLimit;
			
			managedOrder = createNewOrder(targetPrice);	

		}
		
		protected void updateOrder (StockExchangeLevel1View level1View){
			
			Long constrainedPrice = getNextOrderPrice();

			T newOrder = createNewOrder(constrainedPrice);
			placeOrder(newOrder, level1View);
			managedOrder = newOrder;
		}
		
		private Long getNextOrderPrice() {
			
			Long lastOrderPrice = managedOrder.getPrice();
			Long unconstrainedPrice = (long)((1 - learningRate) * lastOrderPrice + learningRate * targetPrice);
			Long constrainedPrice = max (lowLimit,  min (unconstrainedPrice, highLimit));
			return constrainedPrice;
		}
		
		protected abstract T createNewOrder (Long price);
		
		protected abstract void placeOrder (T order, StockExchangeLevel1View level1View);
		
		protected void updateMarketInformationFollowingOrder(Long price, Boolean isOffer) {
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
			Long absoluteChange = null;
			Long basePrice = null;
			
			if (targetPriceAction.equals(REDUCE)){
		
				basePrice = lastPriceReportedOnTheMarket;
				relativeChange = random.nextDouble() * -maximumRelativeChange;
				absoluteChange = (long)(random.nextDouble() * -maximumAbsoluteChange);
				
			} else if (targetPriceAction.equals(INCREASE)){

				basePrice = lastPriceReportedOnTheMarket;
				relativeChange = random.nextDouble() * maximumRelativeChange;
				absoluteChange = (long) (random.nextDouble() * maximumAbsoluteChange);
				
			} else if (targetPriceAction.equals(NOTHING)){
				
				basePrice = targetPrice;				
				relativeChange = 0.0;
				absoluteChange = 0l;				
			}

			targetPrice = (long)(basePrice * (1.0 + relativeChange)) + absoluteChange;
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
		
	}
	
	protected class BuyOrderJob extends OrderJob<BuyOrder> {
		
		public BuyOrderJob(Stock stock, Long floorPrice, Long limitPrice, OrderJob<? extends Order> orderJob) {
			super(stock, floorPrice, limitPrice, orderJob);
		}
		
		public BuyOrderJob(Stock stock, Long floorPrice, Long limitPrice) {
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
		protected BuyOrder createNewOrder(Long price) {
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

		public SellOrderJob(Stock stock, Long limitPrice, Long ceilPrice, OrderJob<? extends Order> orderJob) {
			super(stock, limitPrice, ceilPrice, orderJob);
		}
		
		public SellOrderJob(Stock stock, Long limitPrice, Long ceilPrice) {
			super(stock, limitPrice, ceilPrice);
		}

		@Override
		protected SellOrder createNewOrder(Long price) {
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
	private Long maximumAbsoluteChange;
	private Double learningRate;
	
	private OrderJob<?> currentOrderJob;
	
	private Queue<OrderJobSpecification<? extends OrderJob<?>>> orderJobSpecifications;

	private Set<StockExchangeLevel2View> registered;

	public ZIPTrader(
		String name,
		Long cash,
		Map<Stock, Integer> inventory,
		Random random,
		Double maximumRelativeChange,
		Long maximumAbsoluteChange, 
		Double learningRate,
		List<OrderJobSpecification<? extends OrderJob<?>>> orderJobSpecifications) {

		super(name, cash, inventory);
		
		this.random = random;

		this.maximumRelativeChange = maximumRelativeChange;
		this.maximumAbsoluteChange = maximumAbsoluteChange;
		this.learningRate = learningRate;
		
		this.orderJobSpecifications =
			new LinkedList<OrderJobSpecification<? extends OrderJob<?>>>(orderJobSpecifications);
		
		registered = new HashSet<StockExchangeLevel2View>();
		
		updateCurrentOrderJob();
	}

	@Override
	public void speak(StockExchangeLevel2View level2View) {
		if (!registered.contains(level2View)) register (level2View);
		
		updateCurrentOrderJob ();
		if (!currentOrderJobIsFinished())
			currentOrderJob.updateOrder(level2View);
		
	}
		
	private void register(StockExchangeLevel2View level2View) {
		level2View.registerOrderListener(this);
		level2View.registerTradeListener(this);
		registered.add(level2View);
	}

	private void updateCurrentOrderJob() {
		while (
				currentOrderJobIsFinished() && 
				!orderJobSpecifications.isEmpty()
			){
			
			OrderJobSpecification<? extends OrderJob<?>> nextSpecification = 
				orderJobSpecifications.poll();
			
			if (currentOrderJob == null)
				currentOrderJob = nextSpecification.createOrderJob(this);
			else {
				currentOrderJob = nextSpecification.createOrderJob(this, currentOrderJob);
			}
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
