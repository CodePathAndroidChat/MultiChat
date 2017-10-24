package com.example.jason.multichatapp.models;

import java.util.Map;

public class ChatMessage {

    private final String LANGUAGE_EN = "en";
    private final String LANGUAGE_JP = "ja";
    private final String LANGUAGE_RU = "ru";
    private final String LANGUAGE_ES = "es";
    private final String FIELD_TIMESTAMP = "timestamp";
    private final String FIELD_TEXT = "text";
    private final String FIELD_NAME = "name";
    private final String FIELD_ROOM = "room"; // this can't contain underscore!
    private final String FIELD_LANGUAGE = "language";


    private String id;
    private String timestamp;
    private String text;
    private String name;
    private String language;
    private String room;
    private String en;
    private String es;
    private String ru;
    private String jp;

    public ChatMessage() {
    }

    public ChatMessage(String room, String timestamp, String text, String name, String language, String en, String es, String ru, String jp) {
        this.timestamp = timestamp;
        this.text = text;
        this.room = room;
        this.name = name;
        this.language = language;
        this.en = en;
        this.es = es;
        this.ru = ru;
        this.jp = jp;
    }

    public ChatMessage fromObject(String id, Map<String, String> message) {
        this.id = id;
        this.timestamp = message.get(FIELD_TIMESTAMP);
        this.text = message.get(FIELD_TEXT);
        this.room = message.get(FIELD_ROOM);
        this.name = message.get(FIELD_NAME);
        this.language = message.get(FIELD_LANGUAGE);
        this.en = message.get(LANGUAGE_EN);
        this.es = message.get(LANGUAGE_ES);
        this.ru = message.get(LANGUAGE_RU);
        this.jp = message.get(LANGUAGE_JP);
        return this;
    }

    public String getId() {
        return id;
    }

    public String getRoom() { return this.room; }
    public void setRoom(String room) {
        this.room = room;
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
    public String getEn() {
        return en;
    }
    public void setEn(String en) {
        this.en = en;
    }
    public String getEs() {
        return es;
    }
    public void setEs(String es) {
        this.es = es;
    }
    public String getRu() {
        return ru;
    }
    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getJp() {
        return jp;
    }

    public void setJp(String jp) {
        this.jp = jp;
    }

    public String getTextByLanguage(String language) {
        switch (language) {
            case LANGUAGE_RU:
                return this.ru;
            case LANGUAGE_EN:
                return this.en;
            case LANGUAGE_ES:
                return this.es;
            case LANGUAGE_JP:
                return this.jp;
            default:
                return this.text;
        }
    }

    public String toString() {
        return "Message: {id: " + this.id + ", timestamp: " + timestamp + ", text: " + this.text + ", name: " + name +"}";
    }

    // modify message locally when firebase sends update
    public void updateMessage(ChatMessage chatMessage) {
        this.jp = chatMessage.jp;
        this.ru = chatMessage.ru;
        this.es = chatMessage.es;
    }
}
