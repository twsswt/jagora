package uk.ac.glasgow.jagora.trader.impl;

import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.trader.impl.ZIPTrader.BuyOrderJob;
import uk.ac.glasgow.jagora.trader.impl.ZIPTrader.OrderJob;
import uk.ac.glasgow.jagora.trader.impl.ZIPTrader.SellOrderJob;

public abstract class OrderJobSpecification<T extends OrderJob<?>> {

	public final Stock stock;
	public final Long limitPrice;
	
	public OrderJobSpecification (Stock stock, Long limitPrice){
		this.stock = stock;
		this.limitPrice = limitPrice;
	}
	
	public abstract T createOrderJob(ZIPTrader zipTrader);

	public static class SellOrderJobSpecification extends OrderJobSpecification<SellOrderJob> {

		private final Long ceilPrice;
		
		public SellOrderJobSpecification(Stock stock, Long limitPrice, Long ceilPrice) {
			super(stock, limitPrice);
			this.ceilPrice = ceilPrice;
		}

		@Override
		public SellOrderJob createOrderJob(ZIPTrader zipTrader) {
			return zipTrader.new SellOrderJob(stock, limitPrice, ceilPrice);
		}

	}

	public static class BuyOrderJobSpecification extends OrderJobSpecification<BuyOrderJob> {

		public final Long floorPrice;
		
		public BuyOrderJobSpecification(Stock stock, Long floorPrice,  Long limitPrice) {
			super(stock, limitPrice);
			this.floorPrice = floorPrice;
		}

		@Override
		public BuyOrderJob createOrderJob(ZIPTrader zipTrader) {
			return zipTrader.new BuyOrderJob(stock, floorPrice, limitPrice);
		}

	}


}
