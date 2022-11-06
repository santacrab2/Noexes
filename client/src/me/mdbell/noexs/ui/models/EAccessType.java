package me.mdbell.noexs.ui.models;

public enum EAccessType {
    READ(1, "R"), WRITE(2, "W"), EXEXCUTE(4, "X");

    int byteMask;
    String shortDesc;

    private EAccessType(int byteMask, String shortDesc) {
        this.byteMask = byteMask;
        this.shortDesc = shortDesc;
    }

    public int getByteMask() {
        return byteMask;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public boolean hasAcces(int access) {
        return (access & byteMask) != 0;
    }
}
