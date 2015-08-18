package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.world.World;

import java.util.ArrayList;
import java.util.List;

public class InstitutionalInvestorTraderBuilder extends AbstractTraderBuilder {

	private List<ScheduledLimitOrder> scheduledOrders = new ArrayList<ScheduledLimitOrder>();
		
	public InstitutionalInvestorTraderBuilder() {
		super();
	}

	public InstitutionalInvestorTrader build() {
		return new InstitutionalInvestorTrader(getName(), getCash(), getInventory(), scheduledOrders);
	}

	@Override
	public InstitutionalInvestorTraderBuilder setName(String name) {
		super.setName(name);
		return this;
	}

	@Override
	public InstitutionalInvestorTraderBuilder setCash(Long cash) {
		super.setCash(cash);
		return this;
	}

	@Override
	public InstitutionalInvestorTraderBuilder addStock(Stock stock, Integer quantity) {
		super.addStock(stock, quantity);
		return this;
	}

	public InstitutionalInvestorTraderBuilder addScheduledLimitBuyOrder(
			Long delay,  World world, Stock stock, Integer quantity) {
		scheduledOrders.add(new ScheduledLimitOrder(delay, world, stock, quantity,true));
		return this;
	}

	public InstitutionalInvestorTraderBuilder addScheduledLimitSellOrder
			(Long delay,World world,Stock stock, Integer quantity){
		scheduledOrders.add(new ScheduledLimitOrder(delay,world,stock,quantity,false));
		return this;
	}
	

}
