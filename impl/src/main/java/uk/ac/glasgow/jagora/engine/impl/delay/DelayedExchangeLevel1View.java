package uk.ac.glasgow.jagora.engine.impl.delay;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.MarketBuyOrder;
import uk.ac.glasgow.jagora.MarketSellOrder;
import uk.ac.glasgow.jagora.ticker.TradeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * A transparent delay layer between a trader and an
 * underlying stock exchange view. The class is useful for
 * simulating differential network latency experienced by
 * different trading agents in different locales.
 * 
 * @author Ivelin
 * @author tws
 */
public class DelayedExchangeLevel1View implements StockExchangeLevel1View {

	public abstract class DelayedOrderExecutor implements Comparable<DelayedOrderExecutor> {
				
		public final Long delayTick = DelayedExchangeLevel1View.this.delayedTick;;

		@Override
		public int compareTo(DelayedOrderExecutor delayedOrderExecutor){
			return this.delayTick.compareTo(delayedOrderExecutor.delayTick);
		}
		
		public abstract void execute() ;

	}

	private final Long delayedTick;

	private List<DelayedOrderExecutor> orderExecutors = new ArrayList<DelayedOrderExecutor>();
	private StockExchangeLevel1View wrappedView;


	public DelayedExchangeLevel1View(
		StockExchangeLevel1View wrappedView,
		Long delayedTick,
		Queue<DelayedOrderExecutor> orderExecutorQueue) {
		
		this.wrappedView = wrappedView;
		this.delayedTick = delayedTick;
	}

	@Override
	public void placeLimitBuyOrder(LimitBuyOrder limitBuyOrder) {
		this.orderExecutors.add(
				new DelayedOrderExecutor() {
					@Override
					public void execute() {
						wrappedView.placeLimitBuyOrder(limitBuyOrder);
					}
				 }
		);
	}

	@Override
	public void placeLimitSellOrder(LimitSellOrder limitSellOrder) {
		this.orderExecutors.add(
				new DelayedOrderExecutor() {
					@Override
					public void execute() {
						wrappedView.placeLimitSellOrder(limitSellOrder);
					}
				}
		);
	}

	@Override
	public void cancelLimitBuyOrder(LimitBuyOrder limitBuyOrder) {
		this.orderExecutors.add(
				new DelayedOrderExecutor() {
					@Override
					public void execute() {
						wrappedView.cancelLimitBuyOrder(limitBuyOrder);
					}
				}
		);
	}

	@Override
	public void cancelLimitSellOrder(LimitSellOrder limitSellOrder) {
		this.orderExecutors.add (
				new DelayedOrderExecutor() {
					@Override
					public void execute() {
						wrappedView.cancelLimitSellOrder(limitSellOrder);
					}
				}
		);
	}

	@Override
	public void placeMarketBuyOrder(MarketBuyOrder marketBuyOrder) {
		this.orderExecutors.add (
			new DelayedOrderExecutor() {
				@Override
				public void execute() {
					wrappedView.placeMarketBuyOrder(marketBuyOrder);
				}
			}
		);
	}

	@Override
	public void placeMarketSellOrder(MarketSellOrder marketSellOrder) {
		this.orderExecutors.add (
			new DelayedOrderExecutor() {
				@Override
				public void execute() {
					wrappedView.placeMarketSellOrder(marketSellOrder);
				}
			}
		);
	}
	
	@Override
	public Long getBestOfferPrice(Stock stock) {
		return wrappedView.getBestOfferPrice(stock);
	}

	@Override
	public Long getBestBidPrice(Stock stock) {
		return wrappedView.getBestBidPrice(stock);
	}

	@Override
	public Long getLastKnownBestOfferPrice(Stock stock) {
		return wrappedView.getLastKnownBestOfferPrice(stock);
	}

	@Override
	public Long getLastKnownBestBidPrice(Stock stock) {
		return wrappedView.getLastKnownBestBidPrice(stock);
	}

	@Override
	public void registerTradeListener(TradeListener tradeListener) {
		wrappedView.registerTradeListener(tradeListener);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
			* result
			+ ((delayedTick == null) ? 0 : delayedTick
				.hashCode());
		result = prime
			* result
			+ ((orderExecutors == null) ? 0
				: orderExecutors.hashCode());
		result = prime
			* result
			+ ((wrappedView == null) ? 0 : wrappedView
				.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DelayedExchangeLevel1View other = (DelayedExchangeLevel1View) obj;
		if (delayedTick == null) {
			if (other.delayedTick != null)
				return false;
		} else if (!delayedTick.equals(other.delayedTick))
			return false;
		if (orderExecutors == null) {
			if (other.orderExecutors != null)
				return false;
		} else if (!orderExecutors
			.equals(other.orderExecutors))
			return false;
		if (wrappedView == null) {
			if (other.wrappedView != null)
				return false;
		} else if (!wrappedView.equals(other.wrappedView))
			return false;
		return true;
	}
	
}
