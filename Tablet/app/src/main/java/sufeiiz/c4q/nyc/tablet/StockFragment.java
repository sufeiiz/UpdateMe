package sufeiiz.c4q.nyc.tablet;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sufeizhao on 7/24/15.
 */
public class StockFragment extends Fragment {

    private ArrayList<String> stocks;
    private ArrayAdapter<String> stockAdapter;
    private ListView lvStocks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.stock_layout, container, false);

        lvStocks = (ListView) v.findViewById(R.id.stockList);
        stocks = new ArrayList<String>();
        readItems(v.getContext());
        stockAdapter = new ArrayAdapter<String>(v.getContext(), R.layout.todo_list, stocks);
        lvStocks.setAdapter(stockAdapter);
        if (stocks.size() == 0)
            stocks.add("MSFT");

        ImageButton addStock = (ImageButton) v.findViewById(R.id.addStock);
        addStock.setOnClickListener(new addStockListener(v.getContext()));
        lvStocks.setOnItemClickListener(new lvItemClickListener(v.getContext()));
        lvStocks.setOnItemLongClickListener(new lvItemLongClickListener(v.getContext()));

        return v;
    }

    // save and load items from stock list
    private void readItems(Context context) {
        File filesDir = context.getFilesDir();
        File todoFile = new File(filesDir, "stock.txt");
        try {
            stocks = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            stocks = new ArrayList<String>();
        }
    }

    private void writeItems(Context context) {
        File filesDir = context.getFilesDir();
        File todoFile = new File(filesDir, "stock.txt");
        try {
            FileUtils.writeLines(todoFile, stocks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // add item to stock list & adjust view size
    public class addStockListener implements View.OnClickListener {
        private Context context;

        public addStockListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            final EditText todoET = new EditText(context);
            dialogBuilder.setTitle("Enter Stock")
                    .setMessage("Which stock would you like to add today?")
                    .setView(todoET)
                    .setPositiveButton("Add Stock", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String itemText = todoET.getText().toString().toUpperCase();
                            stocks.add(itemText);
                        }
                    });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            writeItems(context);
        }
    };

    public class lvItemClickListener implements AdapterView.OnItemClickListener {
        private Context context;

        public lvItemClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onItemClick(AdapterView<?> adapter, View item, final int pos, long id) {
            String stockSymbol = stocks.get(pos);
            Intent i = new Intent(context, StockInfoActivity.class);
            i.putExtra("stock", stockSymbol);
            context.startActivity(i);
        }
    };

    // option to delete item from stock list
    public class lvItemLongClickListener implements AdapterView.OnItemLongClickListener {
        private Context context;

        public lvItemLongClickListener(Context context) {
            this.context = context;
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setTitle("Remove Stock?")
                    .setMessage("Are you sure?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int pos) {
                            stocks.remove(i);
                            stockAdapter.notifyDataSetChanged();
                            writeItems(context);
                            Toast.makeText(context, "Stock Removed!", Toast.LENGTH_LONG).show();
                        }
                    });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            return true;
        }

    };
}
