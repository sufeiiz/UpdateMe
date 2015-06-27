package nyc.c4q.syd.updateme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity{

    public final static String STOCK_SYMBOL = "fattyduck.stockquote.STOCK";
    private SharedPreferences stockSymbolsEntered;
    private LinearLayout stockTableScrollView;
    private EditText stockSymbolEditText;
    Button deleteStockSymbolButton;
    Button getStockSymbolButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stockSymbolsEntered = getSharedPreferences("stocklist", MODE_PRIVATE);
        initializeViews();

        getStockSymbolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (stockSymbolEditText.getText().length() > 0) {
                    saveStockSymbol(stockSymbolEditText.getText().toString());
                    stockSymbolEditText.setText("");
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(stockSymbolEditText.getWindowToken(), 0);
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.invalid_stock_symbol).setPositiveButton(R.string.ok
                            , null).setMessage(R.string.missing_stock_symbol);

                    AlertDialog theAlertDialog = builder.create();
                    theAlertDialog.show();
                }

            }
        });

        deleteStockSymbolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllStocks();
                SharedPreferences.Editor editor = stockSymbolsEntered.edit();
                editor.clear();
                editor.apply();
            }
        });
        updateSavedStockList(null);

    }


    private void saveStockSymbol(String newStock){
        String isTheStockNew = stockSymbolsEntered.getString(newStock,null);
        SharedPreferences.Editor editor = stockSymbolsEntered.edit();
        editor.putString(newStock, newStock);
        editor.apply();

        if(isTheStockNew == null){
            updateSavedStockList(newStock);
        }

    }

    public void updateSavedStockList(String newStockSymbol){

        String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);
        Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER);

        if(newStockSymbol!=null){

            inserStockInScrollview(newStockSymbol,Arrays.binarySearch(stocks, newStockSymbol));

        }else {
            for(int i = 0; i<stocks.length; i++){
                inserStockInScrollview(stocks[i], i);
            }
        }

    }

    public void inserStockInScrollview(String stock, int arrayIndex){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newStockRow = inflater.inflate(R.layout.stock_quote_row, null);

        TextView newStockTextView = (TextView) newStockRow.findViewById(R.id.stockSymbolTextView);

        newStockTextView.setText(stock);
        Button stockQuoteButton = (Button)newStockRow.findViewById(R.id.stockQuoteButton);
        stockQuoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout tableRow = (LinearLayout) view.getParent();
                TextView textView = (TextView)tableRow.findViewById(R.id.stockSymbolTextView);
                String stockSymbol = textView.getText().toString();
                Intent intent = new Intent(MainActivity.this, StockInfoActivity.class);
                intent.putExtra(STOCK_SYMBOL, stockSymbol);
                startActivity(intent);
            }
        });

        stockTableScrollView.addView(newStockRow, arrayIndex);

    }


    private void initializeViews(){

        stockTableScrollView =(LinearLayout) findViewById(R.id.stockScrollView);
        stockSymbolEditText = (EditText)findViewById(R.id.stockSymbolEditText);
        getStockSymbolButton = (Button)findViewById(R.id.enterStockSymbolButton);
        deleteStockSymbolButton = (Button)findViewById(R.id.deleteStockSymbolButton);

    }

    private void deleteAllStocks(){
        stockTableScrollView.removeAllViews();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
