package sufeiiz.c4q.nyc.tablet;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by sufeizhao on 7/24/15.
 */
public class StockInfoActivity extends FragmentActivity {

    private static final String TAG = "STOCKQUOTE";
    TextView companyNameTextView;
    TextView yearLowTextView;
    TextView yearHighTextView;
    TextView daysLowTextView;
    TextView daysHighTextView;
    TextView lastTradePriceOnlyTextView;
    TextView changeTextView;
    TextView daysRangeTextView;
    static final String KEY_ITEM = "quote";
    static final String KEY_NAME = "Name";
    static final String KEY_YEAR_LOW = "YearLow";
    static final String KEY_YEAR_HIGH = "YearHigh";
    static final String KEY_DAYS_LOW = "DaysLow";
    static final String KEY_DAYS_HIGH = "DaysHigh";
    static final String KEY_PRICE = "LastTradePriceOnly";
    static final String KEY_CHANGE = "Change";
    static final String KEY_DAYS_RANGE = "Volume";
    String daysLow = "";
    String daysHigh = "";
    String yearLow = "";
    String yearHigh = "";
    String name = "";
    String lastTradePriceOnly = "";
    String change = "";
    String daysRange = "";
    Button moreInfoButton;
    String yahooURLFirst = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22";
    String yahooURLSecond = "%22)&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_info);
        Intent i = getIntent();
        final String stockSymbol = i.getStringExtra("stock");
        initializeViews();

        Log.d(TAG, "Before URL Creation " + stockSymbol);
        moreInfoButton = (Button)findViewById(R.id.moreInfoButton);
        moreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stockURL = getString(R.string.yahoo_stock_url) + stockSymbol;
                Intent getStockWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse(stockURL));
                startActivity(getStockWebPage);
            }
        });

        final String yqlURL = yahooURLFirst + stockSymbol +yahooURLSecond;
        new MyAsyncTask().execute(yqlURL);
    }
    public void initializeViews(){
        companyNameTextView = (TextView)findViewById(R.id.companyNameTextView);
        yearLowTextView=(TextView)findViewById(R.id.yearLowTextView);
        yearHighTextView=(TextView)findViewById(R.id.yearHighTextView);
        daysLowTextView=(TextView)findViewById(R.id.daysLowTextView);
        daysHighTextView=(TextView)findViewById(R.id.daysHighTextView);
        lastTradePriceOnlyTextView =(TextView)findViewById(R.id.lastTradePriceTextView);
        changeTextView=(TextView)findViewById(R.id.changeTextView);
        daysRangeTextView=(TextView)findViewById(R.id.daysRangeTextView);
    }

    private class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            try{
                URL url = new URL(strings[0]);
                URLConnection urlConnection;
                urlConnection = url.openConnection();

                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

                int responceCode = httpURLConnection.getResponseCode();

                if(responceCode == HttpURLConnection.HTTP_OK){
                    InputStream in = httpURLConnection.getInputStream();
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document dom = db.parse(in);
                    Element docEle = dom.getDocumentElement();
                    NodeList nl = docEle.getElementsByTagName(KEY_ITEM);

                    if(nl != null && nl.getLength()>0){
                        for (int i = 0; i<nl.getLength(); i++){
                            StockInfo theStock = getStockInformation(docEle);
                            name = theStock.getName();
                            yearLow = theStock.getYearLow();
                            yearHigh = theStock.getYearHigh();
                            daysLow = theStock.getYearHigh();
                            daysHigh = theStock.getYearHigh();
                            lastTradePriceOnly = theStock.getLastTradePriceOnly();
                            change = theStock.getChange();
                            daysRange = theStock.getDaysRange();
                        }
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            finally {

            }

            return null;
        }



        @Override
        protected void onPostExecute(String s) {
            companyNameTextView.setText(name);
            yearLowTextView.append(" " + yearLow);
            yearHighTextView.append(" "+yearHigh);
            daysHighTextView.append(" "+daysHigh);
            daysLowTextView.append(" "+daysLow);
            lastTradePriceOnlyTextView.append(" "+lastTradePriceOnly);
            changeTextView.append(" "+change);
            daysRangeTextView.append(" "+daysRange);
        }

        private StockInfo getStockInformation(Element entry){
            String stockName = getTextValue(entry, KEY_NAME);
            String stockYearLow = getTextValue(entry, KEY_YEAR_LOW);
            String stockYearHigh = getTextValue(entry, KEY_YEAR_HIGH);
            String stockDaysHigh= getTextValue(entry, KEY_DAYS_HIGH);
            String stockDaysLow= getTextValue(entry, KEY_DAYS_LOW);
            String stockLastTradePrice = getTextValue(entry, KEY_PRICE);
            String stockChange= getTextValue(entry, KEY_CHANGE);
            String stockDaysRage= getTextValue(entry, KEY_DAYS_RANGE);

            StockInfo theStock = new StockInfo(stockLastTradePrice, stockYearLow, stockYearHigh, stockDaysLow, stockName, stockYearHigh, stockDaysRage, stockChange);

            return theStock;
        }

        private String getTextValue(Element entry, String tagName){
            String tagValueToRetrun = null;
            NodeList nl = entry.getElementsByTagName(tagName);
            if(nl != null && nl.getLength()>0){
                Element element= (Element)nl.item(0);
                tagValueToRetrun = element.getFirstChild().getNodeValue();
            }
            return tagValueToRetrun;
        }

    }

}
