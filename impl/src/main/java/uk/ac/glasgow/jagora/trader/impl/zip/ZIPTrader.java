package uk.ac.glasgow.jagora.trader.impl.zip;
import static uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.TargetPriceAction.INCREASE;
import static uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.TargetPriceAction.REDUCE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent;
import uk.ac.glasgow.jagora.ticker.OrderEntryEvent.OrderDirection;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.TargetPriceAction;

public class ZIPTrader extends SafeAbstractTrader implements Level2Trader, TradeListener, OrderListener {
		
	private Random random;
	
	private Double maximumRelativeChange;
	private Long maximumAbsoluteChange;
	
	private final Double learningRate;
	private final Double momentum;
	
	private final Double minInitialProfit;
	private final Double maxInitialProfit;

	
	private ZIPOrderJob<? extends Order> currentOrderJob;
	
	private Queue<ZIPOrderJobSpecification<? extends ZIPOrderJob<?>>> zIPOrderJobSpecifications;

	private Set<StockExchangeLevel2View> registered;

	private Map<Stock, MarketDatum> marketData;

	public ZIPTrader(
		String name,
		Long cash,
		Map<Stock, Integer> inventory,
		Random random,
		Double maximumRelativeChange,
		Long maximumAbsoluteChange, 
		Double learningRate,
		Double momentum,
		Double minInitialProfit,
		Double maxInitialProfit,
		List<ZIPOrderJobSpecification<? extends ZIPOrderJob<?>>> orderJobSpecifications) {

		super(name, cash, inventory);
		
		this.random = random;

		this.maximumRelativeChange = maximumRelativeChange;
		this.maximumAbsoluteChange = maximumAbsoluteChange;
		
		this.learningRate = learningRate;
		this.momentum = momentum;
		
		this.minInitialProfit = minInitialProfit;
		this.maxInitialProfit = maxInitialProfit;
		
		this.zIPOrderJobSpecifications =
			new LinkedList<ZIPOrderJobSpecification<? extends ZIPOrderJob<?>>>(orderJobSpecifications);
		
		registered = new HashSet<StockExchangeLevel2View>();
		
		marketData = new HashMap<Stock,MarketDatum>();
		
		updateCurrentOrderJob();
	}

	@Override
	public void speak(StockExchangeLevel2View level2View) {
		register (level2View);
		
		updateCurrentOrderJob ();
		if (!currentOrderJobIsFinished())
			currentOrderJob.updateOrder(level2View);
		
	}
		
	private void register(StockExchangeLevel2View level2View) {
		level2View.registerOrderListener(this);
		level2View.registerTradeListener(this);
	}

	private void updateCurrentOrderJob() {
		while (
				currentOrderJobIsFinished() && 
				!zIPOrderJobSpecifications.isEmpty()
			){
			
			ZIPOrderJobSpecification<? extends ZIPOrderJob<?>> nextSpecification = 
				zIPOrderJobSpecifications.poll();
			
			MarketDatum marketDatum = getMarketDatum(nextSpecification.stock);
			
			if (currentOrderJob != null){
				// Bug - assumes only one stock type.  Should track target price in marketdatum.
				Long targetPrice = currentOrderJob.getTargetPrice();
				currentOrderJob = 
					nextSpecification.createOrderJob(this, marketDatum, targetPrice);

			} else {				
				Double scale = random.nextDouble() * (maxInitialProfit-minInitialProfit) + minInitialProfit;
				currentOrderJob =
					nextSpecification.createOrderJob(this, marketDatum, scale);
			}
		}
	}

	private Boolean currentOrderJobIsFinished() {
		return currentOrderJob == null || currentOrderJob.isFilled();
	}
	
	public Long getNextOrderPrice(Long lastTargetPrice, Long newTargetPrice, Long lastOrderPrice) {
		
		Long undampenedPrice = (long)((1 - learningRate) * lastOrderPrice + learningRate * newTargetPrice);
		Long dampenedPrice = (long)( momentum * lastTargetPrice + (1 - momentum) * undampenedPrice);

		return dampenedPrice;
	}
	
	public Long computeTargetPrice(Long basePrice, TargetPriceAction targetPriceAction) {
		Double relativeChange = null;
		Long absoluteChange = null;
		
		if (targetPriceAction.equals(REDUCE)){
				
			relativeChange = random.nextDouble() * -maximumRelativeChange;
			absoluteChange = (long)(random.nextDouble() * -maximumAbsoluteChange);
			
		} else if (targetPriceAction.equals(INCREASE)){

			relativeChange = random.nextDouble() * maximumRelativeChange;
			absoluteChange = (long) (random.nextDouble() * maximumAbsoluteChange);
			
		} 
		
		return (long)(basePrice * (1.0 + relativeChange)) + absoluteChange;
	}


	@Override
	public void orderEntered(OrderEntryEvent orderEntryEvent) {
		MarketDatum marketDatum = getMarketDatum(orderEntryEvent.stock);
		
		Boolean wasOffer = orderEntryEvent.orderDirection == OrderDirection.SELL;
		
		marketDatum.updateMarketInformationFollowingOrder(orderEntryEvent.price, wasOffer);
	}

	@Override
	public void tradeExecuted(TradeExecutionEvent tradeExecutionEvent) {
		MarketDatum marketDatum = getMarketDatum(tradeExecutionEvent.stock);
		marketDatum.updateMarketInformationFollowingTrade();
	}
	
	private MarketDatum getMarketDatum (Stock stock){
		MarketDatum marketDatum = marketData.getOrDefault(stock, new MarketDatum(stock));
		marketData.put(stock, marketDatum);
		return marketDatum;
	}
	
	public ZIPOrderJob<? extends Order> getCurrentOrderJob() {
		return this.currentOrderJob;
	}

	//tobe implemented
	@Override
	public void orderCancelled(OrderEntryEvent orderEntryEvent) {

	}
}
