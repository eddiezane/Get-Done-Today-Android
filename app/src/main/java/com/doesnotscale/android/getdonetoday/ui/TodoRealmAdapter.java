package com.doesnotscale.android.getdonetoday.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doesnotscale.android.getdonetoday.R;
import com.doesnotscale.android.getdonetoday.models.TodoItem;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

public class TodoRealmAdapter extends RealmBasedRecyclerViewAdapter<TodoItem, TodoRealmAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private RealmResults<TodoItem> realmResults;

    public class ViewHolder extends RealmViewHolder {
        public TextView todayListItemText;

        public ViewHolder(LinearLayout layout) {
            super(layout);
            this.todayListItemText = (TextView) layout.findViewById(R.id.today_list_item_text);
        }
    }

    public TodoRealmAdapter(Context context, RealmResults<TodoItem> realmResults, boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
        mLayoutInflater = LayoutInflater.from(context);
        this.realmResults = realmResults;
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.today_list_item, viewGroup, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
        final TodoItem todoItem = realmResults.get(position);
        viewHolder.todayListItemText.setText(todoItem.getText());
        viewHolder.todayListItemText.setBackgroundColor(COLORS[(int) (todoItem.getId() % COLORS.length)]);
    }

    private static final int[] COLORS = new int[] {
            Color.argb(255, 28, 160, 170),
            Color.argb(255, 99, 161, 247),
            Color.argb(255, 13, 79, 139),
            Color.argb(255, 89, 113, 173),
            Color.argb(255, 200, 213, 219),
            Color.argb(255, 99, 214, 74),
            Color.argb(255, 205, 92, 92),
            Color.argb(255, 105, 5, 98)
    };
}
