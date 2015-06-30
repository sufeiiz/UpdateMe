package nyc.c4q.syd.updateme;

import java.util.List;

/**
 * Created by fattyduck on 6/27/15.
 */
public class StockCard extends Card {

    private String stockSymbol;
    private String stockPrice;
    private String stockChange;
    private List<StockInfo> stockArray;

    public  StockCard(String stockSymbol, String stockPrice, String stockChange, List<StockInfo> stockArray){
        this.stockSymbol = stockSymbol;
        this.stockPrice = stockPrice;
        this.stockChange = stockChange;
        this.stockArray = stockArray;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public List<StockInfo> getStockArray() {
        return stockArray;
    }

    public void setStockArray(List<StockInfo> stockArray) {
        this.stockArray = stockArray;
    }

    public String getStockChange() {
        return stockChange;
    }

    public void setStockChange(String stockChange) {
        this.stockChange = stockChange;
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    @Override
    int getType() {
        return 3;
    }
}

