package com.doesnotscale.android.getdonetoday.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;

import com.doesnotscale.android.getdonetoday.R;
import com.doesnotscale.android.getdonetoday.models.TodoItem;
import com.doesnotscale.android.getdonetoday.models.TodoItemFactory;

import java.util.Calendar;
import java.util.GregorianCalendar;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

public class TodayListActivity extends AppCompatActivity {
    private static final String TAG = TodayListActivity.class.getSimpleName();
    private static final String NOTIFICATION_HOUR_SETTING = "NOTIFICATION_HOUR";
    private static final String NOTIFICATION_MINUTE_SETTING = "NOTIFICATION_MINUTE";
    private static final String SHOW_NOTIFICATION_SETTING = "SHOW_NOTIFICATION";

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
        fab.setOnClickListener(new AddTodoFabClickListener());

        mRealm = Realm.getDefaultInstance();

        // TODO: Remove
        mRealm.beginTransaction();
        mRealm.clear(TodoItem.class);
        mRealm.commitTransaction();

        mTodoItems = mRealm.where(TodoItem.class).equalTo(TodoItem.TODAY, true).findAll();

        mTodoItemFactory = new TodoItemFactory(mTodoItems.max("id"));

//        for (int i = 0; i < 100; i++) {
//            mRealm.beginTransaction();
//            TodoItem item = mTodoItemFactory.create();
//            item.setText("TodoItem: " + item.getId());
//            mRealm.copyToRealm(item);
//            mRealm.commitTransaction();
//        }

        mRealmRecyclerView  = (RealmRecyclerView) findViewById(R.id.realm_recycler_view);
        mTodoRealmAdapter = new TodoRealmAdapter(this, mTodoItems, true, true);
        mRealmRecyclerView.setAdapter(mTodoRealmAdapter);

        mSharedPreferences = getSharedPreferences(TAG, MODE_PRIVATE);
        setAlarmIfNotSet();
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
        boolean showNotification = mSharedPreferences.getBoolean(SHOW_NOTIFICATION_SETTING, true);
        menu.findItem(R.id.show_notification_setting).setChecked(showNotification);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        int pickerHour = mSharedPreferences.getInt(NOTIFICATION_HOUR_SETTING, 9);
        int pickerMinute = mSharedPreferences.getInt(NOTIFICATION_MINUTE_SETTING, 0);

        if (id == R.id.notification_time_setting) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new NotificationTimePickerListener(), pickerHour, pickerMinute, false);

            timePickerDialog.show();

            // TODO: Trigger alarm

            return true;
        }

        if (id == R.id.show_notification_setting) {
            boolean showNotification = !item.isChecked();
            mSharedPreferences.edit().putBoolean(SHOW_NOTIFICATION_SETTING, showNotification).apply();
            if (showNotification) {
                scheduleAlarm();
            } else {
                cancelAlarm();
            }
            item.setChecked(showNotification);
           return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class AddTodoFabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(TodoListActivity.newIntent(getApplicationContext()));
        }
    }

    private class NotificationTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mSharedPreferences.edit().putInt(NOTIFICATION_HOUR_SETTING, hourOfDay).commit();
            mSharedPreferences.edit().putInt(NOTIFICATION_MINUTE_SETTING, minute).commit();
            scheduleAlarm();
        }
    }

    private void setAlarmIfNotSet() {
        if (!mSharedPreferences.contains(NOTIFICATION_HOUR_SETTING) || !mSharedPreferences.contains(NOTIFICATION_MINUTE_SETTING)) {
            scheduleAlarm();
        }
    }

    private void scheduleAlarm() {
        int hour = mSharedPreferences.getInt(NOTIFICATION_HOUR_SETTING, 9);
        int minute = mSharedPreferences.getInt(NOTIFICATION_MINUTE_SETTING, 0);
        PendingIntent pendingIntent = DailyNotificationAlarmReceiver.newIntent(this);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(GregorianCalendar.HOUR_OF_DAY, hour);
        calendar.set(GregorianCalendar.MINUTE, minute);

        alarmManager.setRepeating(alarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = DailyNotificationAlarmReceiver.newIntent(this);
        alarmManager.cancel(pendingIntent);
    }
}
