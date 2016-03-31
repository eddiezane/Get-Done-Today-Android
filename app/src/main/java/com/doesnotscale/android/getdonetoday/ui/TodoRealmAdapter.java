package com.doesnotscale.android.getdonetoday.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doesnotscale.android.getdonetoday.R;
import com.doesnotscale.android.getdonetoday.models.TodoItem;

import java.util.ArrayList;

import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

public class TodoRealmAdapter extends RealmBasedRecyclerViewAdapter<TodoItem, TodoRealmAdapter.ViewHolder> {
    private static final String TAG = TodoRealmAdapter.class.getSimpleName();
    private RealmResults<TodoItem> realmResults;
    private ArrayList<TodoItem> selectedTodoItems;
    private boolean inSelectMode;

    public class ViewHolder extends RealmViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView todayListItemText;
        private TodoItem todoItem;

        public ViewHolder(LinearLayout layout) {
            super(layout);
            this.todayListItemText = (TextView) layout.findViewById(R.id.today_list_item_text);
            selectedTodoItems = new ArrayList<>();
            inSelectMode = false;
        }

        public void setTodoItem(TodoItem todoItem) {
            this.todoItem = todoItem;
            this.todayListItemText.setText(todoItem.getText());
            this.todayListItemText.setBackgroundColor(COLORS[(int) (todoItem.getId() % COLORS.length)]);
        }

        @Override
        public void onClick(View v) {
            handleSelectedTodo(this, this.todoItem);
        }

        @Override
        public boolean onLongClick(View v) {
            if (!inSelectMode) {
                inSelectMode = true;
            }
            handleSelectedTodo(this, this.todoItem);
            return true;
        }
    }

    public TodoRealmAdapter(Context context, RealmResults<TodoItem> realmResults, boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
        this.realmResults = realmResults;
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.today_list_item, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        v.setOnClickListener(vh);
        v.setOnLongClickListener(vh);
        return vh;
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
        TodoItem todoItem = realmResults.get(position);
        viewHolder.setTodoItem(todoItem);
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

    private void handleSelectedTodo(ViewHolder vh, TodoItem todoItem) {
        if (inSelectMode) {
            if (selectedTodoItems.contains(todoItem)) {
                selectedTodoItems.remove(todoItem);
                vh.todayListItemText.setBackgroundColor(COLORS[(int) (todoItem.getId() % COLORS.length)]);
            } else {
                selectedTodoItems.add(todoItem);
                vh.todayListItemText.setBackgroundColor(Color.BLACK);
            }
        }
        Log.d(TAG, "List is: " + selectedTodoItems.toString());
    }
}
