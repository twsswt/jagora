package uk.ac.glasgow.jagora.trader.impl.zip;

import uk.ac.glasgow.jagora.Order;
import uk.ac.glasgow.jagora.Stock;

import java.util.HashSet;
import java.util.Set;

public class MarketDatum {
	
	public Stock stock;
	public Long lastPriceReportedOnTheMarket;
	public Boolean lastQuoteWasAccepted;
	private Boolean lastQuoteWasOffer;
	
	private final Set<ZIPOrderJob<? extends Order>> zIPOrderJobs;

	public MarketDatum (Stock stock){
		this.stock = stock;
		zIPOrderJobs = new HashSet<ZIPOrderJob<? extends Order>>();
	}

	public Boolean lastQuoteWasOffer(){return lastQuoteWasOffer;}
	public Boolean lastQuoteWasBid(){return !lastQuoteWasOffer;}
	
	public void updateMarketInformationFollowingOrder(Long price, Boolean isOffer){
		lastPriceReportedOnTheMarket = price;
		lastQuoteWasOffer = isOffer;
		lastQuoteWasAccepted = false;
		updateTargetPrices ();
	}

	public void updateMarketInformationFollowingTrade(){
		lastQuoteWasAccepted = true;
		updateTargetPrices ();
	}
	
	private void updateTargetPrices(){
		zIPOrderJobs.stream().forEach(orderJob -> orderJob.updateTargetPrice());		
	}

	public void registerOrderJob (ZIPOrderJob <? extends Order> orderJob){
		zIPOrderJobs.add(orderJob);
	}

}