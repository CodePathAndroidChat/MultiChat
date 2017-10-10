package com.example.jason.multichatapp.models;

public class ChatMessage {

    private String id;
    private String timestamp;
    private String text;
    private String name;
    private String language;

    public ChatMessage() {
    }

    public ChatMessage(String timestamp, String text, String name, String language) {
        this.timestamp = timestamp;
        this.text = text;
        this.name = name;
        this.language = language;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
