package uk.ac.glasgow.jagora.test;


import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.LimitBuyOrder;
import uk.ac.glasgow.jagora.LimitSellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.DefaultLimitBuyOrder;
import uk.ac.glasgow.jagora.impl.DefaultLimitSellOrder;
import uk.ac.glasgow.jagora.test.stub.StubTrader;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.LimitOrderEvent;
import uk.ac.glasgow.jagora.ticker.OrderListener;
import uk.ac.glasgow.jagora.ticker.impl.AbstractStockExchangeObservable;
import uk.ac.glasgow.jagora.ticker.impl.OutputStreamOrderListener;
import uk.ac.glasgow.jagora.ticker.impl.SerialTickerTapeObserver;
import uk.ac.glasgow.jagora.world.TickEvent;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class StockExchangeObservableTest {

	AbstractStockExchangeObservable stockExchangeObservable;

	StubTrader alice ;
	StubTrader bruce;
	Stock lemons = new Stock("lemons");

	OrderListener orderListener = new OutputStreamOrderListener(System.out);

	@Before
	public void setUp() {
		stockExchangeObservable = new SerialTickerTapeObserver();

		alice = new StubTraderBuilder()
				.setName("alice")
				.setCash(50000l)
				.addStock(lemons,1000)
				.build();

		bruce = new StubTraderBuilder()
				.setName("bruce")
				.setCash(50000l)
				.addStock(lemons,1000)
				.build();

	}

	@Test
	public void testCancellationBook () {

		stockExchangeObservable.registerOrderListener(orderListener);

		LimitSellOrder limitSellOrder = new DefaultLimitSellOrder(alice, lemons, 1000, 100l);
		LimitBuyOrder limitBuyOrder = new DefaultLimitBuyOrder(bruce,lemons, 1000, 100l );

		stockExchangeObservable.notifyOrderListenersOfLimitOrder(new TickEvent<>(limitSellOrder, 10l));
		stockExchangeObservable.notifyOrderListenersOfLimitOrder(new TickEvent<>(limitBuyOrder, 10l));

		LimitSellOrder sellOrder1 = new DefaultLimitSellOrder(bruce, lemons, 1000, 100l);

		stockExchangeObservable.notifyOrderListenersOfLimitOrder(new TickEvent<>(sellOrder1, 10l));

		stockExchangeObservable.notifyOrderListenersOfLimitOrderCancellation(new TickEvent<>(sellOrder1, 11l));

		List<LimitOrderEvent> cancelledBuyOrders = stockExchangeObservable.getCancelledBuyOrderHistory(lemons);
		List<LimitOrderEvent> cancelledSellOrders = stockExchangeObservable.getCancelledSellOrderHistory(lemons);
		assertEquals("", cancelledBuyOrders.size(), 0);
		assertEquals("",cancelledSellOrders.get(0).trader, bruce);

	}




}
