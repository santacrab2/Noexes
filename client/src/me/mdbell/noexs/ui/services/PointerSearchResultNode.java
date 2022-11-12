package me.mdbell.noexs.ui.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointerSearchResultNode {

    private List<PointerSearchResultNode> psrnl = new ArrayList<>();
    long minAddress = Long.MAX_VALUE;
    long maxAddress = 0;

    private static final int MAX_ITEM_PER_NODE = 10;

    public List<PointerSearchResult> getPointersMatching(long addressToTest, long maxOffset) {
        List<PointerSearchResult> res = null;
        if (((minAddress - addressToTest) < 0) && ((addressToTest - maxAddress) < maxOffset)) {
            for (PointerSearchResultNode psrn : psrnl) {
                List<PointerSearchResult> resInt = psrn.getPointersMatching(addressToTest, maxOffset);
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

    public void addPointersNode(PointerSearchResultNode psr) {
        psrnl.add(psr);
        minAddress = Math.min(psr.minAddress, minAddress);
        maxAddress = Math.max(psr.maxAddress, maxAddress);
    }

    public void addPointersNodes(List<PointerSearchResultNode> psrnl) {
        for (PointerSearchResultNode psrn : psrnl) {
            addPointersNode(psrn);
        }
    }

    public static PointerSearchResultNode buildRootTree(List<PointerSearchResult> psrl) {

        PointerSearchResultNode res = null;
        Collections.sort(psrl, (p1, p2) -> (p1.address < p2.address ? -1 : (p1.address > p2.address ? 1 : 0)));
        List<PointerSearchResultNode> leafs = new ArrayList<>();
        PointerSearchResultLeaf currentLeaf = null;
        int counter = 0;
        for (PointerSearchResult psr : psrl) {
            if (currentLeaf == null) {
                currentLeaf = new PointerSearchResultLeaf();
            }
            currentLeaf.addPointers(psr);
            if (++counter > MAX_ITEM_PER_NODE) {
                currentLeaf = null;
                counter = 0;
            }
        }

        return buildNodes(leafs);

    }

    public static PointerSearchResultNode buildNodes(List<PointerSearchResultNode> psrl) {
        PointerSearchResultNode res = new PointerSearchResultNode();
        if (psrl.size() <= MAX_ITEM_PER_NODE) {
            res.addPointersNodes(psrl);
        } else {
            List<List<PointerSearchResultNode>> splitted = spitListTo(psrl, MAX_ITEM_PER_NODE);
            for (List<PointerSearchResultNode> intList : splitted) {
                res.addPointersNode(buildNodes(intList));
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
