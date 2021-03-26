package com.taskadapter.webui.pages;

public class ValidationErrorTextWithProcessor {
    private final String text;
    private final Runnable processor;

    public ValidationErrorTextWithProcessor(String text, Runnable processor) {
        this.text = text;
        this.processor = processor;
    }

    public String getText() {
        return text;
    }

    public Runnable getProcessor() {
        return processor;
    }
}
