package uk.ac.glasgow.jagora.trader.impl.zip;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJobSpecification.BuyOrderJobSpecification;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJobSpecification.SellOrderJobSpecification;

import java.util.*;
import java.util.stream.IntStream;

public class ZIPTraderBuilder {

		private String name;
		private Long cash = 0l;
		private Integer seed;

		private Map<Stock,Integer> inventory;
	
	private Long maximumAbsoluteChange;
	private Double maximumRelativeChange;
	
	private Double learningRate;
	private Double momentum;
	private Double minInitialProfit;
	private Double maxInitialProfit;
	
	private List<ZIPOrderJobSpecification<? extends ZIPOrderJob<?>>> zIPOrderJobSpecifications;

	public ZIPTraderBuilder(String name) {
		this.name = name;
		this.inventory = new HashMap<Stock,Integer>();
		this.zIPOrderJobSpecifications = new ArrayList<ZIPOrderJobSpecification<? extends ZIPOrderJob<?>>>();
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
	
	public ZIPTraderBuilder setMomentum(Double momentum) {
		this.momentum = momentum;
		return this;
	}

	public ZIPTraderBuilder addSellOrderJobSpecification(
		Stock stock, Long limitPrice, Long ceilPrice) {
		
		zIPOrderJobSpecifications.add(new SellOrderJobSpecification(stock, limitPrice, ceilPrice));
		return this;
	}
	
	public ZIPTraderBuilder addSellOrderJobSpecification(
		Stock stock, Long limitPrice, Long ceilPrice, Integer quantity) {
		
		IntStream.range(0,quantity).forEach(i -> 
			zIPOrderJobSpecifications.add(new SellOrderJobSpecification(stock, limitPrice, ceilPrice)));
		return this;
	}
	
	public ZIPTraderBuilder addBuyOrderJobSpecification(
		Stock stock, Long floorPrice, Long limitPrice) {
		
		zIPOrderJobSpecifications.add(new BuyOrderJobSpecification(stock, floorPrice, limitPrice));
		return this;
	}
	
	public ZIPTraderBuilder addBuyOrderJobSpecification(
		Stock stock, Long floorPrice, Long limitPrice, Integer quantity) {
		
		IntStream.range(0,quantity).forEach(i -> 
			zIPOrderJobSpecifications.add(new BuyOrderJobSpecification(stock, floorPrice, limitPrice)));
		return this;
	}

	public ZIPTrader build() {
		return new ZIPTrader(
			name, cash, inventory, 
			new Random(seed),
			maximumRelativeChange, maximumAbsoluteChange,
			learningRate, momentum, minInitialProfit, maxInitialProfit,
			zIPOrderJobSpecifications);
	}

	public ZIPTraderBuilder setMinInitialProfit(Double minInitialProfit) {
		this.minInitialProfit = minInitialProfit;
		return this;
	}
	
	public ZIPTraderBuilder setMaxInitialProfit(Double maxInitialProfit) {
		this.maxInitialProfit = maxInitialProfit;
		return this;
	}

	

}
