package me.mdbell.noexs.ui.services;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import me.mdbell.noexs.dump.DumpIndex;
import me.mdbell.noexs.dump.MemoryDump;

public class PointerSearchService extends Service<Set<PointerSearchResult>> {

    private static final Logger logger = LogManager.getLogger(PointerSearchService.class);

    private Path dumpPath;
    private long maxOffset, address;
    private int maxDepth, threadCount;

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

    @Override
    protected Task<Set<PointerSearchResult>> createTask() {
        return new SearchTask();
    }

    private MemoryDump openDump(Path dumpPath) throws IOException {
        return new MemoryDump(dumpPath.toFile());
    }

    private class SearchTask extends Task<Set<PointerSearchResult>> {

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
        protected Set<PointerSearchResult> call() throws Exception {
            ForkJoinPool pool = new ForkJoinPool(threadCount);
            LocalDateTime time = LocalDateTime.now();
            try {
                MemoryDump dump = openDump(dumpPath);
                List<PointerSearchResult>[] results = new List[maxDepth]; // TODO MAPEDLIST
                total = dump.getSize() * maxDepth;
                for (depth = 0; depth < maxDepth && !isCancelled(); depth++) {
                    results[depth] = new ArrayList<>();
                    pool.invoke(new PointerRecursiveTask(this, dump, dump.getIndices(), results, depth));
                }
                Set<PointerSearchResult> res = new HashSet<>();
                for (List<PointerSearchResult> lst : results) {
                    res.addAll(lst);
                }
                return res;
            } finally {
                pool.shutdown();
            }
        }
    }

    private class PointerRecursiveTask extends RecursiveTask<Void> {

        private final int depth;
        private final SearchTask owner;
        private final MemoryDump dump;
        private List<DumpIndex> indices;
        private DumpIndex idx;
        private List<PointerSearchResult>[] results;
        private int logCount = 0;

        public PointerRecursiveTask(SearchTask owner, MemoryDump dump, List<DumpIndex> indices,
                List<PointerSearchResult>[] results, int depth) {
            this.owner = owner;
            this.dump = dump;
            this.depth = depth;
            this.indices = indices;
            this.results = results;
        }

        PointerRecursiveTask(SearchTask owner, MemoryDump dump, DumpIndex idx, List<PointerSearchResult>[] results,
                int depth) {
            this.owner = owner;
            this.dump = dump;
            this.depth = depth;
            this.idx = idx;
            this.results = results;
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
            List<PointerSearchResult> res = new ArrayList<>();
            ByteBuffer buffer = dump.getBuffer(idx);
            if (depth == 0) {
                search(res, idx.getAddress(), buffer, address);
            } else {
                searchDepth(depth, results[depth - 1], res, idx.getAddress(), buffer);
            }
            // logger.info("PointerSearchResult depth={} finished. Res (size={}): {} ",
            // depth, res.size(), res);
            results[depth].addAll(res);
            owner.add(buffer.capacity());
        }

        private void searchDepth(int depth, List<PointerSearchResult> toSearch, List<PointerSearchResult> toAdd,
                long base, ByteBuffer buffer) {
            /*
             * logger.info("PointerSearchResult depth={}, result size :{} to search in : {}"
             * , depth, toSearch.size(), toSearch);
             */

            while (buffer.hasRemaining() && !owner.isCancelled()) {
                long addr = base + buffer.position();
                int remaining = buffer.remaining();
                byte[] byteLong = new byte[remaining];// buffer.remaining()];
                buffer.get(byteLong);

                for (int cursor = 0; cursor < remaining; cursor += 8) {
                    long test = convertToLong(byteLong, cursor);
                    for (int i = 0; i < toSearch.size(); i++) {
                        PointerSearchResult res = toSearch.get(i);
                        long offset = res.address - test;
                        if (Math.abs(offset) <= maxOffset) {
                            PointerSearchResult newResult = new PointerSearchResult((addr+cursor), offset);
                            PointerSearchResult oldResult = (PointerSearchResult) res.clone();
                            newResult.depth = depth;
                            oldResult.prev = newResult;
                            toAdd.add(oldResult);
                        }
                    }
                    addr += 8;
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

        private void search(List<PointerSearchResult> results, long base, ByteBuffer buffer, long address) {
            while (buffer.hasRemaining() && !owner.isCancelled()) {
                long addr = base + buffer.position();
                int remaining = buffer.remaining();
                byte[] byteLong = new byte[remaining];// buffer.remaining()];
                buffer.get(byteLong);

                for (int cursor = 0; cursor < remaining; cursor += 8) {
                    long test = convertToLong(byteLong, cursor);
                    if (logCount++ < 10) {
                        //logger.info("test[{}]:{}=>addr:{}", logCount, test, (addr+cursor));
                    }
                    long offset = address - test;
                    if (Math.abs(offset) <= maxOffset) {
                        results.add(new PointerSearchResult(addr+ cursor, offset));
                    }
                    
                }
            }
        }

        private void searchOld(List<PointerSearchResult> results, long base, ByteBuffer buffer, long address) {
            while (buffer.hasRemaining() && !owner.isCancelled()) {
                long addr = base + buffer.position();
                long test = buffer.getLong();
                if (logCount++ < 10) {
                    logger.info("test[{}]:{}=>addr:{}", logCount, test, addr);
                }
                long offset = address - test;
                if (Math.abs(offset) <= maxOffset) {
                    results.add(new PointerSearchResult(addr, offset));
                }
            }
        }

        private void computeForked() {
            List<PointerRecursiveTask> tasks = new ArrayList<>();
            for (DumpIndex idx : indices) {
                PointerRecursiveTask t = new PointerRecursiveTask(owner, dump, idx, results, depth);
                t.fork();
                tasks.add(t);
            }
            for (PointerRecursiveTask t : tasks) {
                t.join();
            }
        }
    }
}
