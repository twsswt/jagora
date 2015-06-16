package uk.ac.glasgow.jagora.trader.impl.zip;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.ZIPBuyOrderJob;
import uk.ac.glasgow.jagora.trader.impl.zip.ZIPOrderJob.ZIPSellOrderJob;


public abstract class ZIPOrderJobSpecification<T extends ZIPOrderJob<?>> {

	protected final Stock stock;
	protected final Long lowLimit;
	protected final Long highLimit;
	
	public ZIPOrderJobSpecification (Stock stock, Long lowLimit, Long highLimit){
		this.stock = stock;
		this.lowLimit = lowLimit;
		this.highLimit = highLimit;
	}
	
	public abstract T createOrderJob(ZIPTrader zipTrader, MarketDatum marketDatum, Double scale);
		
	public abstract T createOrderJob(ZIPTrader zipTrader, MarketDatum marketDatum, Long lastTargetPrice);
	
	public static class SellOrderJobSpecification extends ZIPOrderJobSpecification<ZIPSellOrderJob> {
		
		public SellOrderJobSpecification(Stock stock, Long limitPrice, Long ceilPrice) {
			super(stock, limitPrice, ceilPrice);
		}
		
		@Override
		public ZIPSellOrderJob createOrderJob(ZIPTrader zipTrader, MarketDatum marketDatum, Long initialTargetPrice) {
			return new ZIPSellOrderJob(zipTrader, marketDatum, lowLimit, highLimit, initialTargetPrice);
		}

		@Override
		public ZIPSellOrderJob createOrderJob(ZIPTrader zipTrader, MarketDatum marketDatum,	Double scale) {
			Long initialTargetPrice =
				(long)((highLimit - lowLimit) * scale) + lowLimit;
			
			return createOrderJob (zipTrader, marketDatum, initialTargetPrice);
		}
		
	}

	public static class BuyOrderJobSpecification extends ZIPOrderJobSpecification<ZIPBuyOrderJob> {
		
		public BuyOrderJobSpecification(Stock stock, Long floorPrice,  Long limitPrice) {
			super(stock, floorPrice, limitPrice);
		}
		
		@Override
		public ZIPBuyOrderJob createOrderJob(ZIPTrader zipTrader, MarketDatum marketDatum, Long initialTargetPrice) {
			return new ZIPBuyOrderJob(zipTrader, marketDatum, lowLimit, highLimit, initialTargetPrice);
		}
		
		@Override
		public ZIPBuyOrderJob createOrderJob(ZIPTrader zipTrader, MarketDatum marketDatum,	Double scale) {
			Long initialTargetPrice =
				(long)((highLimit - lowLimit) *( 1 - scale)) + lowLimit;
			
			return createOrderJob (zipTrader, marketDatum, initialTargetPrice);
		}


	}


}
