package me.mdbell.noexs.ui.services;

import java.util.ArrayList;
import java.util.List;

public class PointerSearchResultLeaf extends PointerSearchResultNode {

    private List<PointerSearchResult> psrl = new ArrayList<>();

    @Override
    public List<PointerSearchResult> getPointersMatching(long addressToTest, long maxOffset) {
        List<PointerSearchResult> res = null;
        if (((minAddress - addressToTest) < 0) && ((addressToTest - maxAddress) < maxOffset)) {
            for (PointerSearchResult psr : psrl) {
                long offset = psr.address - addressToTest;
                if (Math.abs(offset) <= maxOffset) {
                    if (res == null) {
                        res = new ArrayList<>();
                    }
                    res.add(psr);
                }

            }
        }

        return res;
    }

    public void addPointers(PointerSearchResult psr) {
        psrl.add(psr);
        minAddress = Math.min(psr.address, minAddress);
        maxAddress = Math.max(psr.address, maxAddress);
    }
}
