package me.mdbell.noexs.ui.services;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import me.mdbell.noexs.dump.DumpIndex;
import me.mdbell.noexs.dump.MemoryDump;
import me.mdbell.noexs.ui.NoexesFiles;
import me.mdbell.util.HexUtils;

public class PointerSearchService extends Service<List<PointerSearchResult>> {

    private static final Logger logger = LogManager.getLogger(PointerSearchService.class);

    private static final int MAX_ITEMS_PER_NODE = 10;

    private Path dumpPath;
    private long maxOffset, address;
    private int maxDepth, threadCount;
    private boolean positiveOffset = true;
    private LocalDateTime time;
    private boolean dumpSearch = false;

    public void setDumpSearch(boolean dumpSearch) {
        this.dumpSearch = dumpSearch;
    }

    public void setDumpPath(Path dumpPath) {
        this.dumpPath = dumpPath;
    }

    public void setMaxOffset(long maxOffset) {
        this.maxOffset = maxOffset;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setAddress(long addr) {
        this.address = addr;
    }

    public void setThreadCount(int count) {
        this.threadCount = count;
    }

    public void setPositiveOffset(boolean positiveOffset) {
        this.positiveOffset = positiveOffset;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    protected Task<List<PointerSearchResult>> createTask() {
        return new SearchTask();
    }

    private MemoryDump openDump(Path dumpPath) throws IOException {
        return new MemoryDump(dumpPath.toFile());
    }

    private class SearchTask extends Task<List<PointerSearchResult>> {

        private int depth = 0;
        private Semaphore readLock = new Semaphore(1);
        private long read = 0;
        private long total = 0;

        public void add(long read) throws InterruptedException {
            readLock.acquire();
            long l = this.read += read;
            readLock.release();
            updateMessage("Searching for pointers... (" + (depth + 1) + "/" + maxDepth + ") (" + l + "/" + total + ")");
            updateProgress(l, total);
        }

        @Override
        protected List<PointerSearchResult> call() throws Exception {
            logger.info("Starting pointer search");
            ForkJoinPool pool = new ForkJoinPool(threadCount);

            try {
                MemoryDump dump = openDump(dumpPath);
                List<PointerSearchResult>[] results = new List[maxDepth]; // TODO MAPEDLIST
                PointerSearchResultNode[] resultsNode = new PointerSearchResultNode[maxDepth];
                total = dump.getSize() * maxDepth;
                for (depth = 0; depth < maxDepth && !isCancelled(); depth++) {
                    results[depth] = Collections.synchronizedList(new ArrayList<>());
                    logger.info("Invode compute root node at depth : {}, nb results : {}", depth,
                            results[depth].size());
                    pool.invoke(new PointerRecursiveTask(this, dump, dump.getIndices(), results, resultsNode, depth,
                            positiveOffset));
                    logger.info("Compute root node at depth : {}, nb results : {}", depth, results[depth].size());
                    resultsNode[depth] = PointerSearchResultNode.buildRootTree(results[depth], MAX_ITEMS_PER_NODE);

                    dumpPtrsToFile(depth, results);
                }

                List<PointerSearchResult> res = new ArrayList<>();
                for (List<PointerSearchResult> lst : results) {
                    res.addAll(lst);
                }

                return res;
            } finally {
                pool.shutdown();
            }
        }
    }

    private void dumpPtrsToFile(int depth, List<PointerSearchResult>[] results) throws IOException {
        if (dumpSearch) {
            File tmpFile = NoexesFiles.createTempFile(time, "debug_ptr_" + depth, "dptr");
            logger.info("Dumping file [depth={}]: {}", depth, tmpFile.getPath());
            FileUtils.write(tmpFile, "Ptr search : " + HexUtils.formatAddress(address) + " Depth : " + depth + "\n",
                    "UTF-8", true);
            FileUtils.writeLines(tmpFile, "UTF-8", results[depth], "\n", true);
        }
    }

    private class PointerRecursiveTask extends RecursiveTask<Void> {

        private final int depth;
        private final SearchTask owner;
        private final MemoryDump dump;
        private List<DumpIndex> indices;
        private DumpIndex idx;
        private List<PointerSearchResult>[] results;
        private boolean onlyPositiveOffset;
        PointerSearchResultNode[] resultsNode = new PointerSearchResultNode[maxDepth];

        public PointerRecursiveTask(SearchTask owner, MemoryDump dump, List<DumpIndex> indices,
                List<PointerSearchResult>[] results, PointerSearchResultNode[] resultsNode, int depth,
                boolean onlyPositiveOffset) {
            this.owner = owner;
            this.dump = dump;
            this.depth = depth;
            this.indices = indices;
            this.results = results;
            this.resultsNode = resultsNode;
            this.onlyPositiveOffset = onlyPositiveOffset;
        }

        PointerRecursiveTask(SearchTask owner, MemoryDump dump, DumpIndex idx, List<PointerSearchResult>[] results,
                PointerSearchResultNode[] resultsNode, int depth, boolean onlyPositiveOffset) {
            this.owner = owner;
            this.dump = dump;
            this.depth = depth;
            this.idx = idx;
            this.results = results;
            this.resultsNode = resultsNode;
            this.onlyPositiveOffset = onlyPositiveOffset;
        }

        @Override
        protected Void compute() {
            if (idx == null) {
                computeForked();
            } else {
                try {
                    computeSingle();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private void computeSingle() throws IOException, InterruptedException {
            // logger.info("Computed Single : idx={}, depth={}", idx, depth);
            List<PointerSearchResult> res = new ArrayList<>();
            ByteBuffer buffer = dump.getBuffer(idx);
            if (depth == 0) {
                search(res, idx.getAddress(), buffer, address);
            } else {
                PointerSearchResultNode rootNodeAtDepth = resultsNode[depth - 1];
                if (rootNodeAtDepth == null) {
                    logger.error("Node null at depth : {}", depth - 1);
                }
                searchDepth(depth, rootNodeAtDepth, res, idx.getAddress(), buffer);
            }
            results[depth].addAll(res);
            owner.add(buffer.capacity());
        }

        private void search(List<PointerSearchResult> results, long base, ByteBuffer buffer, long address) {
            while (buffer.hasRemaining() && !owner.isCancelled()) {
                long addr = base + buffer.position();
                int remaining = buffer.remaining();
                byte[] byteLong = new byte[remaining];
                buffer.get(byteLong);

                int cursor = 0;
                for (long test : convertToLongs(byteLong)) {
                    long offset = address - test;
                    long absOffset = (offset < 0) ? -offset : offset;
                    if (absOffset <= maxOffset && (!onlyPositiveOffset || offset >= 0)) {
                        results.add(new PointerSearchResult(addr + cursor, offset, test));
                    }
                    cursor += 8;

                }
            }
        }

        private void searchDepth(int depth, PointerSearchResultNode toSearch, List<PointerSearchResult> toAdd,
                long base, ByteBuffer buffer) {
            while (buffer.hasRemaining() && !owner.isCancelled()) {
                long addr = base + buffer.position();

                int remaining = buffer.remaining();
                byte[] byteLong = new byte[remaining];// buffer.remaining()];
                buffer.get(byteLong);

                int cursor = 0;
                for (long test : convertToLongs(byteLong)) {
                    List<PointerSearchResult> candidates = toSearch.getPointersMatching(test, maxOffset,
                            onlyPositiveOffset);
                    if (candidates != null) {
                        for (PointerSearchResult res : candidates) {
                            long offset = res.address - test;
                            PointerSearchResult newResult = new PointerSearchResult((addr + cursor), offset, test);
                            // PointerSearchResult oldResult = (PointerSearchResult) res.clone();
                            newResult.depth = depth;
                            newResult.prev = res;
                            toAdd.add(newResult);
                        }
                    }
                    cursor += 8;
                }
            }
        }

        static long convertToLong(byte[] bytes, int index) {
            long value = 0l;

            // Iterating through for loop
            for (int i = 7; i >= 0; i--) {
                byte b = bytes[index + i];
                // Shifting previous value 8 bits to right and
                // add it with next value
                value = (value << 8) + (b & 255);
            }

            return value;
        }

        static long[] convertToLongs(byte[] bytes) {

            int arraySize = bytes.length / 8;
            long[] res = new long[arraySize];

            for (int index = 0, longIdx = 0; index < bytes.length; index += 8, longIdx++) {

                long value = 0l;
                // Iterating through for loop
                for (int i = 7; i >= 0; i--) {
                    byte b = bytes[index + i];
                    // Shifting previous value 8 bits to right and
                    // add it with next value
                    value = (value << 8) + (b & 255);
                }
                res[longIdx] = value;
            }

            return res;
        }

        private void computeForked() {
            logger.info("Computed Forked");
            List<PointerRecursiveTask> tasks = new ArrayList<>();
            for (DumpIndex idx : indices) {
                PointerRecursiveTask t = new PointerRecursiveTask(owner, dump, idx, results, resultsNode, depth,
                        positiveOffset);
                t.fork();
                tasks.add(t);
            }
            for (PointerRecursiveTask t : tasks) {
                t.join();
            }
        }
    }
}
