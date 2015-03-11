package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.trader.Trader;
import uk.ac.glasgow.jagora.world.World;

public class ScheduledLimitBuyOrder implements Comparable<ScheduledLimitBuyOrder> {
	
	private final Long delay;
	private final World world;
	private final Stock stock;
	private final Integer quantity;
	
	public ScheduledLimitBuyOrder(Long delay, World world, Stock stock,	Integer quantity){
		this.delay = delay;
		this.world = world;
		this.stock = stock;
		this.quantity = quantity;
	}

	@Override
	public int compareTo(ScheduledLimitBuyOrder scheduledLimitBuyOrder) {
		return this.delay.compareTo(scheduledLimitBuyOrder.getTime());
	}

	private Long getTime() {
		return delay;
	}

	public Order createBuyOrder(Trader trader, Double availableCash) {
		return new LimitBuyOrder(trader, stock, quantity, availableCash/quantity);
	}

	public boolean shouldBeExecuted() {
		return world.getCurrentTick() > delay;
	}
}