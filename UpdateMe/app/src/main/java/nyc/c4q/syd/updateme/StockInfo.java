package nyc.c4q.syd.updateme;

/**
 * Created by fattyduck on 6/27/15.
 */
public class StockInfo {
    private String daysLow = "";
    private String daysHigh = "";
    private String yearLow = "";
    private String YearHigh = "";
    private String name = "";
    private String lastTradePriceOnly = "";
    private String change = "";
    private String DaysRange = "";

    public StockInfo(String lastTradePriceOnly, String yearLow, String daysHigh, String daysLow, String name, String yearHigh, String daysRange, String change) {
        this.lastTradePriceOnly = lastTradePriceOnly;
        this.yearLow = yearLow;
        this.daysHigh = daysHigh;
        this.daysLow = daysLow;
        this.name = name;
        YearHigh = yearHigh;
        DaysRange = daysRange;
        this.change = change;
    }

    public String getDaysLow() {
        return daysLow;
    }

    public void setDaysLow(String daysLow) {
        this.daysLow = daysLow;
    }


    public String getDaysRange() {
        return DaysRange;
    }

    public void setDaysRange(String daysRange) {
        DaysRange = daysRange;
    }

    public String getChange() {

        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getLastTradePriceOnly() {

        return lastTradePriceOnly;
    }

    public void setLastTradePriceOnly(String lastTradePriceOnly) {
        this.lastTradePriceOnly = lastTradePriceOnly;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYearHigh() {

        return YearHigh;
    }

    public void setYearHigh(String yearHigh) {
        YearHigh = yearHigh;
    }

    public String getDaysHigh() {

        return daysHigh;
    }

    public void setDaysHigh(String daysHigh) {
        this.daysHigh = daysHigh;
    }

    public String getYearLow() {

        return yearLow;
    }

    public void setYearLow(String yearLow) {
        this.yearLow = yearLow;
    }
}
