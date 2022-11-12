package me.mdbell.noexs.code.reverse;

public class CodePattern {

    private String pattern;
    private boolean capturing;

    public CodePattern(String pattern) {
        this(pattern, true);
    }

    public CodePattern(String pattern, boolean capturing) {
        super();
        this.pattern = pattern;
        this.capturing = capturing;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean isCapturing() {
        return capturing;
    }
    
    
    public String getRegExp() {
        String res = pattern;
        if (capturing) {
            res = "(" + res +")";
        }
        return res;
    }
}
