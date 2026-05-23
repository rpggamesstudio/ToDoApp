package com.example.todoapp;

import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Task {
    private String title;
    private String description;

    // 1. Simple fields that Gson will actually save to the JSON file
    private String dateString;
    private boolean isDoneValue;

    // 2. Complex fields marked 'transient' so Gson ignores them
    private transient LocalDate date;
    private transient BooleanProperty isDone;

    // Standard constructor for creating a new Task
    public Task(String title, String description, LocalDate date) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.dateString = date.toString(); // e.g., "2026-02-22"
        this.isDoneValue = false;

        initializeTransientFields();
    }

    // 3. We call this after Gson loads the simple data to rebuild the complex objects
    public void initializeTransientFields() {
        // Rebuild the LocalDate from the saved String
        this.date = LocalDate.parse(this.dateString);

        // Rebuild the BooleanProperty from the saved boolean
        this.isDone = new SimpleBooleanProperty(this.isDoneValue);

        // Ensure the simple boolean stays updated if the UI changes the property!
        this.isDone.addListener((obs, oldVal, newVal) -> {
            this.isDoneValue = newVal;
        });
    }

    // Getters and Setters remain mostly the same
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