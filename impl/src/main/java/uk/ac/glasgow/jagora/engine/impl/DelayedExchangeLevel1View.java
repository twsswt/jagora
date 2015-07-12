package uk.ac.glasgow.jagora.engine.impl;

import uk.ac.glasgow.jagora.*;
import uk.ac.glasgow.jagora.ticker.PriceListener;
import uk.ac.glasgow.jagora.ticker.TradeListener;


public class DelayedExchangeLevel1View implements StockExchangeLevel1View,Comparable<DelayedExchangeLevel1View> {

    interface ProxyToRealView {
        void execute();
    }

    private ProxyToRealView proxy;
    private StockExchangeLevel1View realView;
    private Long delayedTick;



    public DelayedExchangeLevel1View(StockExchangeLevel1View view, Long delayTicks) {
        this.realView = view;
        this.delayedTick = delayTicks;
    }

    public void invoke ()      {
        if (proxy == null) return;

        proxy.execute();
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
        this.proxy = () -> realView.placeBuyOrder(buyOrder);
    }

    @Override
    public void placeSellOrder(SellOrder sellOrder) {
        this.proxy = () -> realView.placeSellOrder(sellOrder);
    }

    @Override
    public void cancelBuyOrder(BuyOrder buyOrder) {
        this.proxy = () ->realView.cancelBuyOrder(buyOrder);
    }

    @Override
    public void cancelSellOrder(SellOrder sellOrder) {
        this.proxy = () -> realView.cancelSellOrder(sellOrder);
    }

    //TODO for now these are not slowed down at all - should we change this?
    @Override
    public Long getBestOfferPrice(Stock stock) {
        return realView.getBestOfferPrice(stock);
    }

    @Override
    public Long getBestBidPrice(Stock stock) {
        return realView.getBestBidPrice(stock);
    }

    @Override
    public Long getLastKnownBestOfferPrice(Stock stock) {
        return realView.getLastKnownBestOfferPrice(stock);
    }

    @Override
    public Long getLastKnownBestBidPrice(Stock stock) {
        return realView.getLastKnownBestBidPrice(stock);
    }

    @Override
    public void registerTradeListener(TradeListener tradeListener) {
        realView.registerTradeListener(tradeListener);
    }

    @Override
    public void registerPriceListener(PriceListener tradePriceListener) {
        realView.registerPriceListener(tradePriceListener);
    }
}
