package uk.ac.glasgow.jagora.trader.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchange;
import uk.ac.glasgow.jagora.util.Random;

public class SimpleHistoricTraderBuilder {
	
	private String name;
	private Double cash;
	private Integer seed;
	
	private Map<Stock, Integer> inventory;
	private Set<StockExchange> stockExchanges;
		
	public SimpleHistoricTraderBuilder(String name, Double cash, Integer seed){
		this.name = name;
		this.cash = cash;
		this.seed = seed;
		this.inventory = new HashMap<Stock,Integer>();
		this.stockExchanges = new HashSet<StockExchange>();
	}
	
	public SimpleHistoricTraderBuilder addStock(Stock stock, Integer quantity){
		inventory.put(stock, quantity);
		return this;
	}
	
	public SimpleHistoricTraderBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public SimpleHistoricTraderBuilder setCash(Double cash){
		this.cash = cash;
		return this;
	}
	
	public SimpleHistoricTraderBuilder setSeed(Integer seed){
		this.seed = seed;
		return this;
	}
	
	public SimpleHistoricTraderBuilder monitorStockExchange(StockExchange stockExchange) {
		stockExchanges.add(stockExchange);
		return this;
	}

	public SimpleHistoricTrader build(){
		SimpleHistoricTrader trader =  
			new SimpleHistoricTrader(
				name, cash, inventory, new Random(seed));
		for (StockExchange stockExchange: stockExchanges)
			stockExchange.createLevel1View().registerTradeListener(trader);
		return trader;
	}
	
}