package uk.ac.glasgow.jagora.trader.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.OrderJobSpecification.BuyOrderJobSpecification;
import uk.ac.glasgow.jagora.trader.impl.OrderJobSpecification.SellOrderJobSpecification;
import uk.ac.glasgow.jagora.trader.impl.ZIPTrader.OrderJob;

public class ZIPTraderBuilder {

	private String name;
	private Long cash;
	private Integer seed;
	private Map<Stock,Integer> inventory;
	private Long maximumAbsoluteChange;
	private Double maximumRelativeChange;
	private Double learningRate;
	private List<OrderJobSpecification<? extends OrderJob<?>>> orderJobSpecifications;

	public ZIPTraderBuilder(String name) {
		this.name = name;
		this.inventory = new HashMap<Stock,Integer>();
		this.orderJobSpecifications = new ArrayList<OrderJobSpecification<? extends OrderJob<?>>>();
	}

	public ZIPTraderBuilder setCash(Long cash) {
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

	public ZIPTraderBuilder setMaximumAbsoluteChange(Long maximumAbsoluteChange) {
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

	public ZIPTraderBuilder addSellOrderJobSpecification(Stock stock, Long limitPrice, Long ceilPrice) {
		orderJobSpecifications.add(new SellOrderJobSpecification(stock, limitPrice, ceilPrice));
		return this;
	}
	
	public ZIPTraderBuilder addBuyOrderJobSpecification(Stock stock, Long floorPrice, Long limitPrice) {
		orderJobSpecifications.add(new BuyOrderJobSpecification(stock, floorPrice, limitPrice));
		return this;
	}

	public ZIPTrader build() {
		return new ZIPTrader(
			name, cash, inventory, 
			new Random(seed),
			maximumRelativeChange, maximumAbsoluteChange, learningRate,
			orderJobSpecifications);
	}

}
