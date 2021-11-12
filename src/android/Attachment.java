package com.cordova.smtp.client;

public class Attachment {

    // Object attributes

    private String path;
    private String name;

    // Constructor

    public Attachment(String path, String name) {
        this.path = path;
        this.name = name;
    }

    // Getters and setters

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}