package uk.ac.glasgow.jagora.impl;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Market;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.Trade;
import uk.ac.glasgow.jagora.impl.orderbook.LimitOrderBook;
import uk.ac.glasgow.jagora.impl.orderbook.LimitOrderBookClearer;
import uk.ac.glasgow.jagora.impl.orderbook.MarketBuyLimitSellOrderBookClearer;
import uk.ac.glasgow.jagora.impl.orderbook.MarketOrderBook;
import uk.ac.glasgow.jagora.impl.orderbook.MarketOrderBookClearer;
import uk.ac.glasgow.jagora.impl.orderbook.MarketSellLimitBuyOrderBookClearer;
import uk.ac.glasgow.jagora.pricer.LimitOrderTradePricer;
import uk.ac.glasgow.jagora.world.TickEvent;
import uk.ac.glasgow.jagora.world.World;

/**
 * Implements basic order driven market operations. The
 * market's market order books are cleared against one
 * another first using a MarketOrderBookClearer. Then, the
 * market sell order book is cleared against available limit
 * buy orders and similarly, market buy orders are cleared
 * against available limit sell orders.
 * 
 * @author tws
 *
 */
public class ContinuousOrderDrivenMarket implements Market {

	public final Stock stock;
	public final World world;

	private final LimitOrderBook<LimitSellOrder> limitSellOrderBook;
	private final LimitOrderBook<LimitBuyOrder> limitBuyOrderBook;

	private final MarketOrderBook<MarketSellOrder> marketSellOrderBook;
	private final MarketOrderBook<MarketBuyOrder> marketBuyOrderBook;

	private final MarketOrderBookClearer marketOrderBookClearer;
	private final MarketSellLimitBuyOrderBookClearer marketSellLimitBuyOrderBookClearer;
	private final MarketBuyLimitSellOrderBookClearer marketBuyLimitSellOrderBookClearer;

	private final LimitOrderBookClearer limitOrderBookClearer;

	public ContinuousOrderDrivenMarket(
		Stock stock,
		World world,
		LimitOrderTradePricer limitOrderTradePricer) {
		
		this.world = world;

		limitSellOrderBook = 
			new LimitOrderBook<LimitSellOrder>(world);
		
		limitBuyOrderBook = 
			new LimitOrderBook<LimitBuyOrder>(world);

		marketSellOrderBook = 
			new MarketOrderBook<MarketSellOrder>(world);
		marketBuyOrderBook = 
			new MarketOrderBook<MarketBuyOrder>(world);

		this.stock = stock;

		this.marketOrderBookClearer = 
			new MarketOrderBookClearer(
				marketSellOrderBook, 
				marketBuyOrderBook,
				stock,
				world,
				this);

		this.marketSellLimitBuyOrderBookClearer = 
			new MarketSellLimitBuyOrderBookClearer(
				marketSellOrderBook, 
				limitBuyOrderBook, 
				stock,
				world);

		this.marketBuyLimitSellOrderBookClearer =
			new MarketBuyLimitSellOrderBookClearer(
				limitSellOrderBook, 
				marketBuyOrderBook, 
				stock,
				world);

		this.limitOrderBookClearer = 
			new LimitOrderBookClearer(
				limitSellOrderBook, 
				limitBuyOrderBook, 
				stock,
				world, 
				limitOrderTradePricer);
	}

	@Override
	public TickEvent<LimitBuyOrder> recordLimitBuyOrder(
		LimitBuyOrder order) {
		return 
			limitBuyOrderBook.recordOrder(order);
	}

	@Override
	public TickEvent<LimitSellOrder> recordLimitSellOrder(
		LimitSellOrder order) {
		return 
			limitSellOrderBook.recordOrder(order);
	}

	@Override
	public TickEvent<LimitBuyOrder> cancelLimitBuyOrder(
		LimitBuyOrder order) {
		return 
			limitBuyOrderBook.cancelOrder(order);
	}

	@Override
	public TickEvent<LimitSellOrder> cancelLimitSellOrder(
		LimitSellOrder order) {
		return 
			limitSellOrderBook.cancelOrder(order);
	}

	@Override
	public TickEvent<MarketBuyOrder> recordMarketBuyOrder(
		MarketBuyOrder marketBuyOrder) {
		return 
			marketBuyOrderBook.recordOrder(marketBuyOrder);
	}

	@Override
	public TickEvent<MarketSellOrder> recordMarketSellOrder(
		MarketSellOrder marketSellOrder) {
		
		return 
			marketSellOrderBook.recordOrder(marketSellOrder);
	}

	@Override
	public List<TickEvent<Trade>> doClearing() {

		List<TickEvent<Trade>> executedTrades = new ArrayList<TickEvent<Trade>>();

		executedTrades.addAll(marketOrderBookClearer.clearOrderBooks());
		executedTrades.addAll(marketSellLimitBuyOrderBookClearer.clearOrderBooks());
		executedTrades.addAll(marketBuyLimitSellOrderBookClearer.clearOrderBooks());
		executedTrades.addAll(limitOrderBookClearer.clearOrderBooks());

		return executedTrades;
	}

	@Override
	public List<LimitBuyOrder> getBuyOrders() {
		return limitBuyOrderBook.getOpenOrders();
	}

	@Override
	public List<LimitSellOrder> getSellOrders() {
		return limitSellOrderBook.getOpenOrders();
	}

	@Override
	public String toString() {
		return format("best bid: %d, best offer: %d",
			getLastKnownBestBidPrice(),
			getLastKnownBestOfferPrice());
	}

	@Override
	public Long getBestBidPrice() {
		return limitBuyOrderBook.getBestPrice();
	}

	@Override
	public Long getBestOfferPrice() {
		return limitSellOrderBook.getBestPrice();
	}

	@Override
	public Long getLastKnownBestBidPrice() {
		return limitBuyOrderBook.getLastKnownBestPrice();
	}

	@Override
	public Long getLastKnownBestOfferPrice() {
		return limitSellOrderBook.getLastKnownBestPrice();
	}
}
