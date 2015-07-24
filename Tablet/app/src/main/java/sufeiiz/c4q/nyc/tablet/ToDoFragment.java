package sufeiiz.c4q.nyc.tablet;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sufeizhao on 7/23/15.
 */
public class ToDoFragment extends Fragment {

    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static ToDoFragment newInstance(int sectionNumber) {
        ToDoFragment fragment = new ToDoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ToDoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.todo_layout, container, false);

        lvItems = (ListView) rootView.findViewById(R.id.list);
        items = new ArrayList<String>();
        readItems(rootView.getContext());
        itemsAdapter = new ArrayAdapter<String>(rootView.getContext(), R.layout.todo_list, R.id.text, items);
        lvItems.setAdapter(itemsAdapter);
        setListViewHeightBasedOnChildren(lvItems);
        if (items.size() == 0)
            items.add("Add your first to do list now!");

        ImageButton add = (ImageButton) rootView.findViewById(R.id.add);
        add.setOnClickListener(new addTODOListener(rootView.getContext()));
        lvItems.setOnItemClickListener(new lvItemClickListener(rootView.getContext()));
        lvItems.setOnItemLongClickListener(new lvItemLongClickListener(rootView.getContext()));


        return rootView;
    }

    // save and load items from to-do list
    private void readItems(Context c) {
        File filesDir = c.getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }

    private void writeItems(Context c) {
        File filesDir = c.getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // add item to to-do list & adjust view size
    public class addTODOListener implements View.OnClickListener {
        private Context c;

        public addTODOListener(Context c) {
            this.c = c;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(c);
            final EditText todoET = new EditText(c);
            dialogBuilder.setTitle("Add Todo Task Item")
                    .setMessage("What is on your list today?")
                    .setView(todoET)
                    .setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String itemText = todoET.getText().toString();
                            items.add(itemText);
                            setListViewHeightBasedOnChildren(lvItems);
                        }
                    });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            writeItems(c);
        }
    };

    // option to delete item from to-do list
    public class lvItemClickListener implements AdapterView.OnItemClickListener {
        private Context c;

        public lvItemClickListener(Context c) {
            this.c = c;
        }

        @Override
        public void onItemClick(AdapterView<?> adapter, View item, final int pos, long id) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(c);
            dialogBuilder.setTitle("Remove Task")
                    .setMessage("Have you completed this task?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            items.remove(pos);
                            itemsAdapter.notifyDataSetChanged();
                            writeItems(c);
                            setListViewHeightBasedOnChildren(lvItems);
                            Toast.makeText(c, "Well done!", Toast.LENGTH_LONG).show();
                        }
                    });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    };

    public class lvItemLongClickListener implements AdapterView.OnItemLongClickListener {
        private Context c;

        public lvItemLongClickListener(Context c) {
            this.c = c;
        }

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(c);
            final DatePicker setDate = new DatePicker(c);
            setDate.setSpinnersShown(false);
            dialogBuilder.setTitle("Set Reminder on Date")
                    .setView(setDate)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            notificationDate(items.get(position),
                                    setDate.getYear(), setDate.getMonth(), setDate.getDayOfMonth(),
                                    parent, position, c);
                        }
                    });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            return true;
        }
    };

    public void notificationDate(final String task, final int year, final int month, final int day,
                                 final AdapterView<?> parent, final int position, final Context c) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(c);
        final TimePicker setTime = new TimePicker(c);
        dialogBuilder.setTitle("Set Reminder at Time")
                .setView(setTime)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Set Reminder", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setNotification(task, year, month, day,
                                setTime.getCurrentHour(), setTime.getCurrentMinute(), c);
                        //TODO: make invisible again?
                        View image = parent.getChildAt(position);
                        image.findViewById(R.id.icon).setVisibility(View.VISIBLE);
                    }
                });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void setNotification(String task, int year, int month, int day, int hour, int min, Context c) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        long millis = cal.getTimeInMillis();

        Intent intent = new Intent(c, AlarmReceiver.class);
        intent.putExtra("task", task);
        PendingIntent mAlarmSender = PendingIntent.getBroadcast(c, 0, intent, 0);
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, millis, mAlarmSender);
    }

    // adjust listview height for to-to list
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }






}


