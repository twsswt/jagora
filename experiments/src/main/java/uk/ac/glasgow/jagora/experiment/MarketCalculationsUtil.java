package uk.ac.glasgow.jagora.experiment;

public class MarketCalculationsUtil {
	
	
	public static double calculateEquilibriumPrice(
		Long maxPrice, Long minPrice, Long maxBidLimit, Long minOfferLimit, Integer numberOfBuyers, Integer numberOfSellers) {
		
		Double bidRate = ( maxBidLimit - minPrice ) / (1.0 * numberOfBuyers);
		
		Double sellRate = (maxPrice  - minOfferLimit) / (1.0 * numberOfSellers);
		
		Double equilibrium = 
			 (maxBidLimit * sellRate - minOfferLimit * bidRate ) / (sellRate + bidRate);
		
		return equilibrium;
	}
	
		 
}
