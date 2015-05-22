package uk.ac.glasgow.jagora.trader.zip.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.zip.impl.ZIPTrader.BuyOrderJob;
import uk.ac.glasgow.jagora.trader.zip.impl.ZIPTrader.OrderJob;
import uk.ac.glasgow.jagora.trader.zip.impl.ZIPTrader.SellOrderJob;

public abstract class OrderJobSpecification<T extends OrderJob<?>> {


	public final Stock stock;
	public final Double limitPrice;
	
	public OrderJobSpecification (Stock stock, Double limitPrice){
		this.stock = stock;
		this.limitPrice = limitPrice;
	}
	
	public abstract T createOrderJob(ZIPTrader zipTrader);

	public static class SellOrderJobSpecification extends OrderJobSpecification<SellOrderJob> {

		private final Double ceilPrice;
		
		public SellOrderJobSpecification(Stock stock, Double limitPrice, Double ceilPrice) {
			super(stock, limitPrice);
			this.ceilPrice = ceilPrice;
		}

		@Override
		public SellOrderJob createOrderJob(ZIPTrader zipTrader) {
			return zipTrader.new SellOrderJob(stock, limitPrice, ceilPrice);
		}

	}

	public static class BuyOrderJobSpecification extends OrderJobSpecification<BuyOrderJob> {

		public final Double floorPrice;
		
		public BuyOrderJobSpecification(Stock stock, Double limitPrice, Double floorPrice) {
			super(stock, limitPrice);
			this.floorPrice = floorPrice;
		}

		@Override
		public BuyOrderJob createOrderJob(ZIPTrader zipTrader) {
			return zipTrader.new BuyOrderJob(stock, limitPrice, floorPrice);
		}

	}


}
