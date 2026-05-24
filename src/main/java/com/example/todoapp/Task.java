package com.example.todoapp;

import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Task {
    private String title;
    private String description;
    private String dateString;
    private boolean isDoneValue;
    private transient LocalDate date;
    private transient BooleanProperty isDone;
    public Task(String title, String description, LocalDate date) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.dateString = date.toString(); // e.g., "2026-02-22"
        this.isDoneValue = false;

        initializeTransientFields();
    }

    public void initializeTransientFields() {
        this.date = LocalDate.parse(this.dateString);
        this.isDone = new SimpleBooleanProperty(this.isDoneValue);
        this.isDone.addListener((obs, oldVal, newVal) -> {
            this.isDoneValue = newVal;
        });
    }


    public BooleanProperty isDoneProperty() { return isDone; }
    public boolean isDone() { return isDone.get(); }
    public void setDone(boolean done) { this.isDone.set(done); }
    public LocalDate getDate() { return date; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}