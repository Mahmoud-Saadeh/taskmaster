package com.example.taskmaster;

public class Task {
    private String body;
    private String title;
    private TaskStates state;

    public Task(String body, String title, TaskStates state) {
        this.body = body;
        this.title = title;
        this.state = state;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TaskStates getState() {
        return state;
    }

    public void setState(TaskStates state) {
        this.state = state;
    }
}
