package com.doesnotscale.android.getdonetoday;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by ezaneski on 3/29/16.
 */
public class TodoItem extends RealmObject {
    @Required private String text;
    private boolean completed;

    public TodoItem() {
        this.completed = false;
    }

    public TodoItem(String text) {
        this();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
