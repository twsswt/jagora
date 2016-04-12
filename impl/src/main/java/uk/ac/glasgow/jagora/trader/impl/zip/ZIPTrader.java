package uk.ac.glasgow.jagora.trader.impl.zip;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel2View;
import uk.ac.glasgow.jagora.ticker.LimitOrderEvent;
import uk.ac.glasgow.jagora.ticker.MarketOrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderEvent.OrderDirection;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.TradeExecutionEvent;
import uk.ac.glasgow.jagora.ticker.TradeListener;
import uk.ac.glasgow.jagora.trader.Level2Trader;
import uk.ac.glasgow.jagora.trader.impl.SafeAbstractTrader;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.TargetPriceAction;

import java.util.*;

import static uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.TargetPriceAction.INCREASE;
import static uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.TargetPriceAction.REDUCE;

/**
 * Implementation of Cliff's (1997) Zero Intelligence Plus
 * (ZIP) Trader.
 * 
 * The current implementation assumes a limit order driven
 * market. The algorithm does not account for the effect of
 * cancelled orders on an agent's stock price.
 * 
 * @author tws
 *
 */
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
				
		marketData = new HashMap<Stock,MarketDatum>();
		
		updateCurrentOrderJob();
	}

	@Override
	public void speak(StockExchangeLevel2View level2View) {

		updateCurrentOrderJob ();
		if (!currentOrderJobIsFinished()){
			level2View.registerOrderListener(this);
			level2View.registerTradeListener(this);
			currentOrderJob.updateOrder(level2View);
		} else {
			level2View.deRegisterOrderListener(this);
			level2View.deRegisterTradeListener(this);
		}
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
				// TODO Bug - assumes only one stock type.  Should track target price in marketdatum.
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
			
		} else {
			relativeChange = 0.0;
			absoluteChange = 0l;
		}
		
		return (long)(basePrice * (1.0 + relativeChange)) + absoluteChange;
	}


	@Override
	public void limitOrderEvent(LimitOrderEvent limitOrderEvent) {
		MarketDatum marketDatum = getMarketDatum(limitOrderEvent.stock);
		
		Boolean wasOffer = limitOrderEvent.orderDirection == OrderDirection.SELL;
		
		marketDatum.updateMarketInformationFollowingOrder(limitOrderEvent.price, wasOffer);
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

	@Override
	public void marketOrderEntered(MarketOrderEvent marketOrderEvent) {
		// TODO Account for the effect of market orders
		// (effectively spread crossing limit orders).
		
	}
}
