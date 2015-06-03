package uk.ac.glasgow.jagora.trader.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.OrderJobSpecification.BuyOrderJobSpecification;
import uk.ac.glasgow.jagora.trader.impl.OrderJobSpecification.SellOrderJobSpecification;

public class ZIPTraderBuilder {

	private String name;
	private Double cash;
	private Integer seed;
	private Map<Stock,Integer> inventory;
	private Double maximumAbsoluteChange;
	private Double maximumRelativeChange;
	private Double learningRate;
	private List<OrderJobSpecification<?>> orderJobs;

	public ZIPTraderBuilder(String name) {
		this.name = name;
		this.inventory = new HashMap<Stock,Integer>();
		this.orderJobs = new ArrayList<OrderJobSpecification<?>>();
	}

	public ZIPTraderBuilder setCash(Double cash) {
		this.cash = cash;
		return this;
	}

	public ZIPTraderBuilder setSeed(Integer seed) {
		this.seed = seed;
		return this;
	}

	public ZIPTraderBuilder addStock(Stock stock, Integer quantity) {
		inventory.put(stock, quantity);
		return this;
	}

	public ZIPTraderBuilder setMaximumAbsoluteChange(Double maximumAbsoluteChange) {
		this.maximumAbsoluteChange = maximumAbsoluteChange;
		return this;
	}
	
	public ZIPTraderBuilder setMaximumRelativeChange(Double maximumRelativeChange) {
		this.maximumRelativeChange = maximumRelativeChange;
		return this;
	}
	
	public ZIPTraderBuilder setLearningRate(Double learningRate) {
		this.learningRate = learningRate;
		return this;
	}

	public ZIPTraderBuilder addSellOrderJob(Stock stock, Double limitPrice, Double ceilPrice) {
		orderJobs.add(new SellOrderJobSpecification(stock, limitPrice, ceilPrice));
		return this;
	}
	
	public ZIPTraderBuilder addBuyOrderJob(Stock stock, Double floorPrice, Double limitPrice) {
		orderJobs.add(new BuyOrderJobSpecification(stock, floorPrice, limitPrice));
		return this;
	}

	public ZIPTrader build() {
		return new ZIPTrader(
			name, cash, inventory, 
			new Random(seed),
			maximumRelativeChange, maximumAbsoluteChange, learningRate,
			orderJobs);
	}

}
