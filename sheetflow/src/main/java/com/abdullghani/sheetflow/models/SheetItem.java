package com.abdullghani.sheetflow.models;


import androidx.annotation.DrawableRes;

public class SheetItem {
    private final String title;
    private final int iconRes;
    private Runnable action;

    public SheetItem(String title, @DrawableRes int iconRes) {
        this.title = title;
        this.iconRes = iconRes;
    }

    public SheetItem(String title, @DrawableRes int iconRes, Runnable action) {
        this.title = title;
        this.iconRes = iconRes;
        this.action = action;
    }

    public String getTitle() { return title; }
    public int getIconRes() { return iconRes; }
    public Runnable getAction() { return action; }
}