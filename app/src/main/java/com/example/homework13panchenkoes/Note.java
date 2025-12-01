package com.example.homework13panchenkoes;

public class Note {

    private final String title;
    private final String text;

    public Note(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
