package me.mdbell.noexs.ui.services;

import java.util.ArrayList;
import java.util.List;

public class PointerSearchResultLeaf extends PointerSearchResultNode {

    private List<PointerSearchResult> intermediatePsrl = new ArrayList<>();

    private PointerSearchResult[] psrlToSearch = null;

    @Override
    public List<PointerSearchResult> getPointersMatching(long addressToTest, long maxOffset,
            boolean onlyPositiveOffset) {
        List<PointerSearchResult> res = null;
        if (((minAddress - addressToTest) < maxOffset) && ((addressToTest - maxAddress) < maxOffset)) {
            for (PointerSearchResult psrRoot : getPsrlToSearch()) {
                PointerSearchResult psr = psrRoot;//psrRoot.getLast();
                long offset = psr.address - addressToTest;
                long absOffset = (offset < 0) ? -offset : offset;
                if (absOffset <= maxOffset && (!onlyPositiveOffset || offset >= 0)) {
                    if (res == null) {
                        res = new ArrayList<>();
                    }
                    res.add(psrRoot);
                }

            }
        }

        return res;
    }

    public void addPointers(PointerSearchResult psr) {

        intermediatePsrl.add(psr);

        PointerSearchResult last = psr;//psr.getLast();
        minAddress = Math.min(last.address, minAddress);
        maxAddress = Math.max(last.address, maxAddress);
        psrlToSearch = null;
    }

    private PointerSearchResult[] getPsrlToSearch() {
        if (psrlToSearch == null) {
            psrlToSearch = intermediatePsrl.toArray(new PointerSearchResult[] {});
        }
        return psrlToSearch;
    }
}
