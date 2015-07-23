package uk.ac.glasgow.jagora.engine.impl;

import java.util.List;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.ticker.PriceListener;
import uk.ac.glasgow.jagora.ticker.TradeListener;

//TODO make order executors comparable - this may mean not using lamda expressions.

public class DelayedExchangeLevel1View implements StockExchangeLevel1View {

    interface DelayedOrderExecutor extends Comparable<DelayedOrderExecutor>{
        void execute();
    }
    
    public abstract class AbstractDelayedOrderExecutor implements DelayedOrderExecutor {
    	
    }

    private List<DelayedOrderExecutor> orderExecutors;
    private StockExchangeLevel1View wrappedView;
    private Long delayedTick;



    public DelayedExchangeLevel1View(StockExchangeLevel1View wrappedView, Long delayTicks) {
        this.wrappedView = wrappedView;
        this.delayedTick = delayTicks;
    }

    /**
     *
     * @param otherView
     * @return used for prioritising objects according to their delay
     */
    @Override
    public int compareTo(DelayedExchangeLevel1View otherView) {
        return this.getDelayedTick().compareTo(otherView.getDelayedTick());
    }

    /**
     *
     * @return Tick at which the view is scheduled to operate
     */
    public Long getDelayedTick() {
        return delayedTick;
    }

    @Override
    public void placeBuyOrder(BuyOrder buyOrder) {
        this.orderExecutors.add( () -> wrappedView.placeBuyOrder(buyOrder) );
    }

    @Override
    public void placeSellOrder(SellOrder sellOrder) {
        this.orderExecutors.add(  () -> wrappedView.placeSellOrder(sellOrder) );
    }

    @Override
    public void cancelBuyOrder(BuyOrder buyOrder) {
        this.orderExecutors.add( () ->wrappedView.cancelBuyOrder(buyOrder) );        
    }

    @Override
    public void cancelSellOrder(SellOrder sellOrder) {
        this.orderExecutors.add ( () -> wrappedView.cancelSellOrder(sellOrder) );
    }

    //TODO for now these are not slowed down at all - should we change this?
    @Override
    public Long getBestOfferPrice(Stock stock) {
        return wrappedView.getBestOfferPrice(stock);
    }

    @Override
    public Long getBestBidPrice(Stock stock) {
        return wrappedView.getBestBidPrice(stock);
    }

    @Override
    public Long getLastKnownBestOfferPrice(Stock stock) {
        return wrappedView.getLastKnownBestOfferPrice(stock);
    }

    @Override
    public Long getLastKnownBestBidPrice(Stock stock) {
        return wrappedView.getLastKnownBestBidPrice(stock);
    }

    @Override
    public void registerTradeListener(TradeListener tradeListener) {
        wrappedView.registerTradeListener(tradeListener);
    }

    @Override
    public void registerPriceListener(PriceListener tradePriceListener) {
        wrappedView.registerPriceListener(tradePriceListener);
    }
    
    public List<DelayedOrderExecutor> getOrderExecutors (){
    	return orderExecutors;
    }
}
