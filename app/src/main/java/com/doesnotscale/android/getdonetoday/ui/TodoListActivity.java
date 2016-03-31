package com.doesnotscale.android.getdonetoday.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.doesnotscale.android.getdonetoday.R;
import com.doesnotscale.android.getdonetoday.models.TodoItem;
import com.doesnotscale.android.getdonetoday.models.TodoItemFactory;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

public class TodoListActivity extends AppCompatActivity {
    private Realm mRealm;
    private RealmResults<TodoItem> mTodoItems;
    private TodoItemFactory mTodoItemFactory;
    private TodoRealmAdapter mTodoRealmAdapter;
    private RealmRecyclerView mRealmRecyclerView;


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, TodoListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buildAndShowAddTodoDialog();
//                addTodo();
//                mRealmRecyclerView.smoothScrollToPosition(mTodoItems.size() - 1);
//                Snackbar.make(view, "Added to list", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        mRealm = Realm.getDefaultInstance();

        mTodoItems = mRealm.allObjects(TodoItem.class);
        mTodoItemFactory = new TodoItemFactory(mTodoItems.max("id"));

        mRealmRecyclerView  = (RealmRecyclerView) findViewById(R.id.realm_recycler_view);
        mTodoRealmAdapter = new TodoRealmAdapter(this, mTodoItems, true, true);
        mRealmRecyclerView.setAdapter(mTodoRealmAdapter);
    }

    private void addTodo(String todoText) {
        mRealm.beginTransaction();
        TodoItem item = mTodoItemFactory.create();
        item.setText(todoText);
        item.setToday((item.getId() % 2) == 0);
        mRealm.copyToRealm(item);
        mRealm.commitTransaction();
    }

    private void buildAndShowAddTodoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogContent = inflater.inflate(R.layout.add_todo_dialog, null);

        builder.setTitle("Add a new todo")
                .setView(dialogContent)
                .setPositiveButton(R.string.add_todo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) dialogContent.findViewById(R.id.add_todo_dialog_text);
                        String todoText = editText.getText().toString();
                        addTodo(todoText);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
