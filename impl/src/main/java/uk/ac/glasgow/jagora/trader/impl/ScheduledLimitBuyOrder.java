package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.World;

public class ScheduledLimitBuyOrder implements Comparable<ScheduledLimitBuyOrder> {
	
	private final Long delay;
	private final World world;
	private final Stock stock;
	private final Integer quantity;
	private final Long limitPrice;
	
	public ScheduledLimitBuyOrder(Long delay, World world, Stock stock,	Integer quantity, Long limitPrice){
		this.delay = delay;
		this.world = world;
		this.stock = stock;
		this.quantity = quantity;
		this.limitPrice = limitPrice;
	}

	@Override
	public int compareTo(ScheduledLimitBuyOrder scheduledLimitBuyOrder) {
		return this.delay.compareTo(scheduledLimitBuyOrder.getTime());
	}

	private Long getTime() {
		return delay;
	}

	public BuyOrder createBuyOrder(Trader trader) {
		return new LimitBuyOrder(trader, stock, quantity, limitPrice);
	}

	public Boolean shouldBeExecuted() {
		return world.getCurrentTick() > delay;
	}
}