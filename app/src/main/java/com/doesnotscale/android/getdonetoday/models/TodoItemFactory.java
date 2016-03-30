package com.doesnotscale.android.getdonetoday.models;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ezaneski on 3/30/16.
 */
public class TodoItemFactory {
    private AtomicInteger mAtomicInteger;

    public TodoItemFactory(Number startingId) {
        if (startingId == null) {
            startingId = 0;
        }
        mAtomicInteger = new AtomicInteger(startingId.intValue() + 1);
    }

    public TodoItem create() {
        TodoItem todoItem = new TodoItem();
        todoItem.setId(mAtomicInteger.getAndIncrement());
        todoItem.setCompleted(false);
        todoItem.setToday(false);
        return todoItem;
    }
}
