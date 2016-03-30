package com.doesnotscale.android.getdonetoday.ui;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;

import com.doesnotscale.android.getdonetoday.R;
import com.doesnotscale.android.getdonetoday.models.TodoItem;
import com.doesnotscale.android.getdonetoday.models.TodoItemFactory;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

public class TodayListActivity extends AppCompatActivity {
    private static final String TAG = TodayListActivity.class.getSimpleName();

    private RealmRecyclerView mRealmRecyclerView;

    private Realm mRealm;
    private RealmResults<TodoItem> mTodoItems;
    private TodoItemFactory mTodoItemFactory;
    private TodoRealmAdapter mTodoRealmAdapter;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new addTodoFabClickListener());

        mRealm = Realm.getDefaultInstance();

        // TODO: Remove
        mRealm.beginTransaction();
        mRealm.clear(TodoItem.class);
        mRealm.commitTransaction();

        mTodoItems = mRealm.allObjects(TodoItem.class);

        mTodoItemFactory = new TodoItemFactory(mTodoItems.max("id"));

        for (int i = 0; i < 10; i++) {
            mRealm.beginTransaction();
            TodoItem item = mTodoItemFactory.create();
            item.setText("TodoItem: " + item.getId());
            mRealm.copyToRealm(item);
            mRealm.commitTransaction();
        }

        mRealmRecyclerView  = (RealmRecyclerView) findViewById(R.id.realm_recycler_view);
        mTodoRealmAdapter = new TodoRealmAdapter(this, mTodoItems, true, true);
        mRealmRecyclerView.setAdapter(mTodoRealmAdapter);

        mSharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_today_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        int pickerHour = mSharedPreferences.getInt("notificationHour", 9);
        int pickerMinute = mSharedPreferences.getInt("notificationMinute", 0);

        if (id == R.id.notification_time_setting) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mSharedPreferences.edit().putInt("notificationHour", hourOfDay).apply();
                    mSharedPreferences.edit().putInt("notificationMinute", minute).apply();
                }
            }, pickerHour, pickerMinute, false);

            timePickerDialog.show();

            // TODO: Trigger alarm

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addTodo() {
        mRealm.beginTransaction();
        TodoItem item = mTodoItemFactory.create();
        item.setText("TodoItem: " + item.getId());
        mRealm.copyToRealm(item);
        mRealm.commitTransaction();
    }

    private class addTodoFabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            addTodo();
            mRealmRecyclerView.smoothScrollToPosition(mTodoItems.size() - 1);
//                Snackbar.make(view, "Added to list", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
        }
    };
}
