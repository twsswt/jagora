package uk.ac.glasgow.jagora;


import uk.ac.glasgow.jagora.Stock;

public class StockWarehouse {

    private final Stock stock;

    private final int initialQuantity;
    private int remainingQuantity;

    public StockWarehouse (Stock stock,int initialQuantity) {
        this.stock = stock;
        this.initialQuantity = initialQuantity;
        this.remainingQuantity = initialQuantity;
    }

    public Stock getStock() {
        return stock;
    }
    public int getStock(int quantity) throws Exception{
        if (quantity > remainingQuantity)
            throw new Exception("Not enough of the stock left");

        remainingQuantity -= quantity;
        return quantity;
    }

    public int getRemainingStock() {
        int toReturn = remainingQuantity;
        remainingQuantity = 0;
        return toReturn;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }
}