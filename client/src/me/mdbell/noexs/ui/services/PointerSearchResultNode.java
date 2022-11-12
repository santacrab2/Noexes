package me.mdbell.noexs.ui.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointerSearchResultNode {

    private List<PointerSearchResultNode> intermediatePsrnl = new ArrayList<>();
    private PointerSearchResultNode[] psrnlToSearch = null;
   

    long minAddress = Long.MAX_VALUE;
    long maxAddress = 0;

    private static final float MAX_ITEMS_PER_NODE_RATIO = 1.5f;

    public List<PointerSearchResult> getPointersMatching(long addressToTest, long maxOffset, boolean onlyPositiveOffset) {
        List<PointerSearchResult> res = null;
        if (((minAddress - addressToTest) < maxOffset) && ((addressToTest - maxAddress) < maxOffset)) {
            for (PointerSearchResultNode psrn : getPsrnlToSearch()) {
                List<PointerSearchResult> resInt = psrn.getPointersMatching(addressToTest, maxOffset, onlyPositiveOffset);
                if (resInt != null) {
                    if (res == null) {
                        res = new ArrayList<>();
                    }
                    res.addAll(resInt);
                }
            }
        }
        return res;
    }
    
    private PointerSearchResultNode[] getPsrnlToSearch() {
        if (psrnlToSearch == null) {
            psrnlToSearch = intermediatePsrnl.toArray(new PointerSearchResultNode[] {});
        }
        return psrnlToSearch;
    }

    public void addPointersNode(PointerSearchResultNode psr) {
        intermediatePsrnl.add(psr);
        minAddress = Math.min(psr.minAddress, minAddress);
        maxAddress = Math.max(psr.maxAddress, maxAddress);
        psrnlToSearch = null;
    }

    public void addPointersNodes(List<PointerSearchResultNode> psrnl) {
        for (PointerSearchResultNode psrn : psrnl) {
            addPointersNode(psrn);
        }
    }

    public static PointerSearchResultNode buildRootTree(List<PointerSearchResult> psrl, int maxItems) {
        PointerSearchResultNode res = null;
        Collections.sort(psrl, (p1, p2) -> (p1.address < p2.address ? -1 : (p1.address > p2.address ? 1 : 0)));
        List<PointerSearchResultNode> leafs = new ArrayList<>();
        PointerSearchResultLeaf currentLeaf = null;
        int counter = 0;
        for (PointerSearchResult psr : psrl) {
            if (currentLeaf == null) {
                currentLeaf = new PointerSearchResultLeaf();
                leafs.add(currentLeaf);
            }
            currentLeaf.addPointers(psr);
            if (++counter > maxItems) {
                currentLeaf = null;
                counter = 0;
            }
        }
        res = buildNodes(leafs, maxItems);
        
        
        return res;

    }

    public static PointerSearchResultNode buildNodes(List<PointerSearchResultNode> psrl, int maxItems) {
        PointerSearchResultNode res = new PointerSearchResultNode();
        if (psrl.size() <= maxItems) {
            res.addPointersNodes(psrl);
        } else {
            List<List<PointerSearchResultNode>> splitted = spitListTo(psrl, maxItems);
            for (List<PointerSearchResultNode> intList : splitted) {
                res.addPointersNode(buildNodes(intList, (int) (maxItems * MAX_ITEMS_PER_NODE_RATIO)));
            }
        }

        return res;
    }

    public static List<List<PointerSearchResultNode>> spitListTo(List<PointerSearchResultNode> psrl, int nb) {
        List<List<PointerSearchResultNode>> res = new ArrayList<>();
        int itemPerLot = Math.max(1, psrl.size() / nb);
        List<PointerSearchResultNode> currentList = null;
        int count = 0;
        for (int i = 0; i < psrl.size(); i++) {
            if (currentList == null) {
                currentList = new ArrayList<>();
                res.add(currentList);
            }
            currentList.add(psrl.get(i));
            if (++count > itemPerLot) {
                count = 0;
                currentList = null;
            }
        }

        return res;
    }

}
