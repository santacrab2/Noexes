package me.mdbell.noexs.ui.models;

public enum RangeType {
    ALL("All (R/W)"), RANGE("Range"), RANGE2("Main and Heap Range"), HEAP("Heap"), TLS("Thread Local Storage");

    String str;

    RangeType(String str) {
        this.str = str;
    }

    public String toString() {
        return str;
    }
}
