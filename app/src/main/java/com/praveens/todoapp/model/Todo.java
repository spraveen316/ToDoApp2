package com.praveens.todoapp.model;

import java.util.Date;

/**
 * Created by praveens on 1/17/17.
 */

public class Todo {
    public long id;
    public String text;
    public Date createdOn;
    public Date modifiedOn;

    public Todo() {

    }

    public Todo(String text) {
        this.text = text;
        this.createdOn = new Date();
    }

    @Override
    public String toString() {
        return text;
    }
}
