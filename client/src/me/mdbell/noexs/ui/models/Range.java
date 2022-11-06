package me.mdbell.noexs.ui.models;

public class Range {
    long start = 0;
    long end = 0;

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public Range(long start, long end) {
        super();
        this.start = start;
        this.end = end;
    }

}
