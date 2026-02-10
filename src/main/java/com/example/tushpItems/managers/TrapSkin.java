package com.example.tushpItems.managers;

public enum TrapSkin {
    DEFAULT("Default", "&#FFFFFF"),
    NORMAL("Обычная", "&#AAAAAA"),
    HELL("Адская", "&#FF5555"),
    ICE("Ледяная", "&#55FFFF"),
    ABANDONED("Заброшенная", "&#888888"),
    MURZIK("Мурзик", "&#FFD700");

    private final String displayName;
    private final String color;

    TrapSkin(String displayName, String color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }
}