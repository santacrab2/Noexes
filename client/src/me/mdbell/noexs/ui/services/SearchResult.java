package me.mdbell.noexs.ui.services;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.mdbell.noexs.dump.DumpRegion;
import me.mdbell.noexs.dump.MemoryDump;
import me.mdbell.noexs.io.MappedList;
import me.mdbell.noexs.ui.NoexesFiles;
import me.mdbell.noexs.ui.models.ConditionType;
import me.mdbell.noexs.ui.models.DataType;
import me.mdbell.noexs.ui.models.SearchType;
import me.mdbell.util.HexUtils;

public final class SearchResult implements Closeable {

    private static final int PAGE_SIZE = 1024;

    private static final Logger logger = LogManager.getLogger(SearchResult.class);

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private File location;
    List<Long> addresses;
    List<DumpRegion> regions;
    DataType dataType;
    SearchType type;

    ConditionType compareType;
    long knownValue;

    long start;
    long end;
    MemoryDump curr;

    private SearchResult prev;

    public SearchResult(TemporalAccessor time, String suffix) throws IOException {
        this.location = NoexesFiles.createTempFile(time, suffix, "dmp");
    }

    private SearchResult() {

    }

    public File getLocation() {
        return location;
    }

    public List<Long> getAddresses() {
        return addresses;
    }

    public long getStart() {
        return start;
    }

    public DataType getDataType() {
        return dataType;
    }

    public SearchType getType() {
        return type;
    }

    public long getEnd() {
        return end;
    }

    public long getCurr(long addr) throws IOException {
        return curr.getValue(addr, dataType.getSize());
    }

    public long getPrev(long addr) throws IOException {
        if (prev == null) {
            return getCurr(addr);
        }
        return prev.getCurr(addr);
    }

    public int size() {
        return addresses.size();
    }

    public List<Long> getPage(int idx) {
        logger.debug("Get page {}, sublist({},{})", idx, PAGE_SIZE * idx, Math.min(size(), PAGE_SIZE * (idx + 1)));
        return addresses.subList(PAGE_SIZE * idx, Math.min(size(), PAGE_SIZE * (idx + 1)));
    }

    public int getPageCount() {
        int size = size();
        if (size == 0) {
            return 0;
        }
        if (size % PAGE_SIZE != 0) {
            size += PAGE_SIZE;
        }
        return size / PAGE_SIZE;
    }

    @Override
    public void close() throws IOException {
        if (curr != null) {
            curr.close();
        }
        if (prev != null) {
            prev.close();
        }
        if (addresses instanceof Closeable) {
            ((Closeable) addresses).close();
        }
    }

    public void setPrev(SearchResult prev) {
        this.prev = prev;
    }

    public SearchResult getPrev() {
        return prev;
    }

    static class SerializedSearchResult {
        public String location;
        public long memoryDumpSize;
        public DataType dataType;
        public SearchType type;
        public long start;
        public long end;
        public String prevLocation;
        public String addressLocation;
        public long addressesSize;
        public ConditionType compareType;
        public String knownValue;

        protected SerializedSearchResult() {

        }
    }

    public String getFilename() {
        return FilenameUtils.removeExtension(location.getPath()) + ".srch";
    }

    public void save() {

        String searchFileName = getFilename();
        File savePath = new File(searchFileName);
        SerializedSearchResult ss = new SerializedSearchResult();
        ss.location = this.location.getPath();
        try {
            ss.memoryDumpSize = curr.getSize();
            ss.dataType = this.dataType;
            ss.type = this.type;
            ss.start = this.start;
            ss.end = this.end;
            ss.compareType = this.compareType;
            ss.knownValue = HexUtils.format(this.dataType, this.knownValue, true);
            if (this.prev != null && this.prev.location != null) {
                ss.prevLocation = this.prev.getFilename();
            }
            if (addresses instanceof MappedList<Long>) {
                MappedList<Long> mappedList = (MappedList<Long>) addresses;
                ss.addressLocation = mappedList.getFile().getPath();
                ss.addressesSize = mappedList.size();
                logger.debug("Address file : {}, size : {}", ss.addressLocation, ss.addressesSize);
            }

            String json = gson.toJson(ss);

            Files.write(savePath.toPath(), json.getBytes());
        } catch (IOException e) {
            logger.error("Error while saving search : {}", searchFileName, e);
        }
    }

    public static SearchResult load(File f) throws IOException {
        SearchResult res = null;
        try {
            logger.info("Reading search result from file : {}", f.getPath());
            FileReader reader = new FileReader(f);
            SerializedSearchResult ssr = gson.fromJson(reader, SerializedSearchResult.class);
            res = new SearchResult();
            res.location = new File(ssr.location);
            res.curr = new MemoryDump(res.location);
            res.dataType = ssr.dataType;
            res.type = ssr.type;
            res.start = ssr.start;
            res.end = ssr.end;
            res.compareType = ssr.compareType;
            res.knownValue = HexUtils.fromString(ssr.knownValue);
            if (StringUtils.isNotBlank(ssr.prevLocation)) {
                logger.debug("Reading previsou search : {}", ssr.prevLocation);
                res.prev = load(new File(ssr.prevLocation));

            }
            if (StringUtils.isNotBlank(ssr.addressLocation)) {
                logger.debug("Reading addresses from file : {}", ssr.addressLocation);
                res.addresses = MappedList.createLongList(new File(ssr.addressLocation), "rw", (int) ssr.addressesSize);
            } else {
                // TODO
            }

        } catch (FileNotFoundException e) {
            logger.error("Error while loding search : {}", f, e);
        }
        return res;
    }

    @Override
    public String toString() {
        return "SearchResult [location=" + location + ", dataType=" + dataType + ", type=" + type + ", start=" + start
                + ", end=" + end + ", knownValue=" + knownValue + ", compareType=" + compareType + "]";
    }

}
