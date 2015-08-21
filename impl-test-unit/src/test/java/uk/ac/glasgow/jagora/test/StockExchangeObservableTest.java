package uk.ac.glasgow.jagora.test;


import org.junit.Before;
import org.junit.Test;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.impl.LimitBuyOrder;
import uk.ac.glasgow.jagora.impl.LimitSellOrder;
import uk.ac.glasgow.jagora.test.stub.StubTrader;
import uk.ac.glasgow.jagora.test.stub.StubTraderBuilder;
import uk.ac.glasgow.jagora.ticker.OrderEvent;
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
	Stock lemons = new Stock("LEMONS");

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

		SellOrder sellOrder = new LimitSellOrder(alice, lemons, 1000, 100l);
		BuyOrder buyOrder = new LimitBuyOrder(bruce,lemons, 1000, 100l );

		stockExchangeObservable.notifyOrderListeners(new TickEvent<>(sellOrder, 10l));
		stockExchangeObservable.notifyOrderListeners(new TickEvent<>(buyOrder, 10l));

		SellOrder sellOrder1 = new LimitSellOrder(bruce, lemons, 1000, 100l);

		stockExchangeObservable.notifyOrderListeners(new TickEvent<>(sellOrder1, 10l));

		stockExchangeObservable.notifyOrderListenersOfCancellation(new TickEvent<>(sellOrder1, 11l));

		List<OrderEvent> cancelledBuyOrders = stockExchangeObservable.getCancelledBuyOrderHistory(lemons);
		List<OrderEvent> cancelledSellOrders = stockExchangeObservable.getCancelledSellOrderHistory(lemons);
		assertEquals("", cancelledBuyOrders.size(), 0);
		assertEquals("",cancelledSellOrders.get(0).trader, bruce);

	}




}
