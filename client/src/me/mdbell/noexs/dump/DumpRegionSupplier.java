package me.mdbell.noexs.dump;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import me.mdbell.noexs.core.Debugger;
import me.mdbell.noexs.core.MemoryInfo;
import me.mdbell.util.HexUtils;

public abstract class DumpRegionSupplier implements Supplier<DumpRegion> {

    public abstract String getDescription();

    public abstract long getStart();

    public abstract long getEnd();

    public long getSize() {
        return getEnd() - getStart();
    }

    public static DumpRegionSupplier createSupplier(long start, long end, List<DumpRegion> regions) {
        return createSupplier(start, end, regions, end - start);
    }

    public static DumpRegionSupplier createSupplier(long start, long end, List<DumpRegion> regions, long size) {
        return new DumpRegionSupplier() {
            int i = 0;

            @Override
            public long getStart() {
                return start;
            }

            @Override
            public long getEnd() {
                return end;
            }

            @Override
            public long getSize() {
                return size;
            }

            @Override
            public String getDescription() {
                return "Region list (size:" + regions.size() + ")";
            }

            @Override
            public DumpRegion get() {
                if (i >= regions.size()) {
                    return null;
                }
                return regions.get(i++);
            }
        };
    }

    public static DumpRegionSupplier createSupplierFromInfo(Debugger conn, Function<MemoryInfo, Boolean> filter) {
        return new DumpRegionSupplier() {
            long start = 0;
            long end = 0;
            long size;

            @Override
            public long getStart() {
                init();
                return start;
            }

            @Override
            public long getEnd() {
                init();
                return end;
            }

            @Override
            public String getDescription() {
                return "Memory Info";
            }

            @Override
            public long getSize() {
                init();
                return size;
            }

            MemoryInfo[] info;
            int i = 0;

            private void init() {
                if (info == null) {
                    info = conn.query(0, 10000);
                    for (int i = 0; i < info.length; i++) {
                        MemoryInfo in = info[i];
                        if (!filter.apply(in)) {
                            continue;
                        }
                        long addr = in.getAddress();
                        long next = in.getNextAddress();
                        if (start == 0) {
                            start = addr;
                        }
                        if (next > end) {
                            end = next;
                        }
                        size += in.getSize();
                    }
                }
            }

            @Override
            public DumpRegion get() {
                init();
                MemoryInfo curr;
                do {
                    if (i >= info.length) {
                        return null;
                    }
                    curr = info[i++];
                } while (!filter.apply(curr));
                return new DumpRegion(curr.getAddress(), curr.getNextAddress());
            }
        };
    }

    public static DumpRegionSupplier createSupplierFromRange(Debugger conn, long start, long end) {
        return new DumpRegionSupplier() {
            @Override
            public long getStart() {
                return start;
            }

            @Override
            public long getEnd() {
                return end;
            }

            MemoryInfo[] info;
            int i = 0;

            @Override
            public String getDescription() {
                return "Address Range [" + HexUtils.formatAddress(start) + "," + HexUtils.formatAddress(end) + "]";
            }

            @Override
            public DumpRegion get() {
                if (info == null) {
                    info = conn.query(start, 10000);
                }
                MemoryInfo curr;
                do {
                    if (i >= info.length) {
                        return null;
                    }
                    curr = info[i++];
                } while (!curr.isReadable() || curr.getNextAddress() < start);
                if (curr.getAddress() >= end) {
                    return null;
                }
                return new DumpRegion(Math.max(curr.getAddress(), start), Math.min(curr.getNextAddress(), end));
            }
        };
    }

    // TODO : ca peut etre l"inmainstart, mainend, heapstart, heapend
    public static DumpRegionSupplier createSupplierFrom2Range(Debugger conn, long zone1start, long zone1end,
            long zone2start, long zone2end) {
        return new DumpRegionSupplier() {

            @Override
            public long getStart() {
                return zone1start;
            }

            @Override
            public long getEnd() {
                return zone2end;
            }

            @Override
            public long getSize() {
                return ((zone2end - zone2start) + (zone1end - zone1start));
            }

            @Override
            public String getDescription() {
                return "2 ranges:zone1[" + HexUtils.formatAddress(zone1start) + "," + HexUtils.formatAddress(zone1end)
                        + "],zone2[" + HexUtils.formatAddress(zone2start) + "," + HexUtils.formatAddress(zone2end)
                        + "], size="+getSize();
            }

            MemoryInfo[] info;
            int i = 0;

            @Override
            public DumpRegion get() {
                if (info == null) {
                    info = conn.query(zone1start, 10000);
                }
                MemoryInfo curr;
                do {
                    if (i >= info.length) {
                        return null;
                    }
                    curr = info[i++];
                } while (!curr.isReadable() || !curr.isWriteable() || curr.getNextAddress() < zone1start
                        || ((curr.getAddress() >= zone1end) && (curr.getNextAddress() < zone2start)));
                if (curr.getAddress() >= zone2end) {
                    return null;
                }
                return new DumpRegion(
                        Math.min(Math.max(curr.getAddress(), zone1start), Math.max(zone2start, curr.getAddress())),
                        Math.max(Math.min(curr.getNextAddress(), zone2end), Math.min(zone1end, curr.getNextAddress())));
            }
        };
    }
}
