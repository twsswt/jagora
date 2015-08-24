package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
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

	public LimitBuyOrder createBuyOrder(Trader trader) {
		return new DefaultLimitBuyOrder(trader, stock, quantity, limitPrice);
	}

	public Boolean shouldBeExecuted() {
		return world.getCurrentTick() > delay;
	}
}