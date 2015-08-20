package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.MarketSellOrder;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.World;

public class ScheduledLimitOrder implements Comparable<ScheduledLimitOrder> {

	private final Long delay;
	private final World world;
	private final Stock stock;
	private final Integer quantity;
	private final Boolean isBuyOrder;

	public ScheduledLimitOrder(
		Long delay, World world, Stock stock, Integer quantity, Boolean isBuyOrder){

		this.delay = delay;
		this.world = world;
		this.stock = stock;
		this.quantity = quantity;
		this.isBuyOrder = isBuyOrder;
	}

	@Override
	public int compareTo(ScheduledLimitOrder scheduledLimitBuyOrder) {
		return this.delay.compareTo(scheduledLimitBuyOrder.getTime());
	}

	private Long getTime() {
		return delay;
	}

	public Order createBuyOrder(Trader trader, Long availableCash) {
		return new LimitBuyOrder(trader, stock, quantity, availableCash/quantity);
	}

	public Order createSellOrder(Trader trader, StockExchangeLevel1View level1View) {
		return new MarketSellOrder(trader,stock,quantity,level1View);
	}


	public boolean shouldBeExecuted() {
		return world.getCurrentTick() > delay;
	}

	public Boolean isBuyOrder() {return isBuyOrder;}
}