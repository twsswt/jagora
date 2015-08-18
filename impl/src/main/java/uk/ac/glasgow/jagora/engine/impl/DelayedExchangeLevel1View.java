package uk.ac.glasgow.jagora.engine.impl;

import uk.ac.glasgow.jagora.BuyOrder;
import uk.ac.glasgow.jagora.SellOrder;
import uk.ac.glasgow.jagora.Stock;
import uk.ac.glasgow.jagora.StockExchangeLevel1View;
import uk.ac.glasgow.jagora.ticker.PriceListener;
import uk.ac.glasgow.jagora.ticker.TradeListener;

import java.util.ArrayList;
import java.util.List;


public class DelayedExchangeLevel1View implements StockExchangeLevel1View {

    interface DelayedOrderExecutor extends Comparable<DelayedOrderExecutor> {
        void execute() ;
        Long getDelayedTick ();

        @Override
        default int compareTo(DelayedOrderExecutor delayedOrderExecutor){
            return this.getDelayedTick().compareTo(delayedOrderExecutor.getDelayedTick());
        }
    }

    private final Long delayedTick;

    private List<DelayedOrderExecutor> orderExecutors = new ArrayList<DelayedOrderExecutor>();
    private StockExchangeLevel1View wrappedView;


    public DelayedExchangeLevel1View(StockExchangeLevel1View wrappedView, Long standartDelay,
                                     Long currentTick, Long delayDecrease) {
        this.wrappedView = wrappedView;
        this.delayedTick = standartDelay + currentTick - delayDecrease;
    }


    public List<DelayedOrderExecutor> getOrderExecutors (){
        return orderExecutors;
    }


    @Override
    public void placeBuyOrder(BuyOrder buyOrder) {
        this.orderExecutors.add(
                new DelayedOrderExecutor() {
                    @Override
                    public void execute() {
                        wrappedView.placeBuyOrder(buyOrder);
                    }

                    @Override
                    public Long getDelayedTick() {
                        return delayedTick;
                    }
                 }
        );
    }

    @Override
    public void placeSellOrder(SellOrder sellOrder) {
        this.orderExecutors.add(
                new DelayedOrderExecutor() {
                    @Override
                    public void execute() {
                        wrappedView.placeSellOrder(sellOrder);
                    }

                    @Override
                    public Long getDelayedTick() {
                        return delayedTick;
                    }
                }
        );
    }

    @Override
    public void cancelBuyOrder(BuyOrder buyOrder) {
        this.orderExecutors.add(
                new DelayedOrderExecutor() {
                    @Override
                    public void execute() {
                        wrappedView.cancelBuyOrder(buyOrder);
                    }

                    @Override
                    public Long getDelayedTick() {
                        return delayedTick;
                    }
                }
        );
    }

    @Override
    public void cancelSellOrder(SellOrder sellOrder) {
        this.orderExecutors.add (
                new DelayedOrderExecutor() {
                    @Override
                    public void execute() {
                        wrappedView.cancelSellOrder(sellOrder);
                    }

                    @Override
                    public Long getDelayedTick() {
                        return delayedTick;
                    }
                }
        );
    }

    //Possibility to i
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

}
