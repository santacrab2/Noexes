package me.mdbell.noexs.ui.menus;

import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import me.mdbell.noexs.ui.controllers.MainController;
import me.mdbell.noexs.ui.models.AccessType;
import me.mdbell.noexs.ui.models.MemoryInfoTableModel;
import me.mdbell.noexs.ui.models.Range;

public class MemoryInfoContextMenu extends ContextMenu {

    public MemoryInfoContextMenu(Supplier<MainController> mc, TableView<MemoryInfoTableModel> memInfoTable) {
        MenuItem searchBoth = new MenuItem("Search (Start & End)");
        MenuItem searchStart = new MenuItem("Search(Start)");
        MenuItem searchEnd = new MenuItem("Search (End)");
        MenuItem mainsearchAuto = new MenuItem("Search Auto Main");
        MenuItem heapsearchAuto = new MenuItem("Search Auto Heap");
        MenuItem mainHeapSearchAuto = new MenuItem("Search Auto Main + Heap");
        MenuItem mainsearchBoth = new MenuItem("Main Search (Start & End)");
        MenuItem mainsearchStart = new MenuItem("Main Search(Start)");
        MenuItem mainsearchEnd = new MenuItem("Main Search (End)");
        MenuItem ptrMain = new MenuItem("Pointer Search (Main)");
        MenuItem ptrFilter = new MenuItem("Pointer Search (Filter Min & Max)");
        MenuItem ptrFilterStart = new MenuItem("Pointer Search (Filter Min)");
        MenuItem ptrFilterEnd = new MenuItem("Pointer Search (Filter Max)");
        MenuItem memoryView = new MenuItem("Memory Viewer");
        MenuItem disassembler = new MenuItem("Disassembler");
        searchBoth.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().search().setSearchRange(model.getAddr(), model.getEnd());
            mc.get().setTab(MainController.Tab.SEARCH);
        });
        searchStart.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().search().setStart(model.getAddr());
        });

        mainsearchAuto.setOnAction(event -> {
            Range range = searchWrtitableRange(memInfoTable.getItems(), "main");
            if (range != null) {
                mc.get().search().setSearchRange(range.getStart(), range.getEnd());
            }
        });

        heapsearchAuto.setOnAction(event -> {
            Range range = searchWrtitableRange(memInfoTable.getItems(), "heap");
            if (range != null) {
                mc.get().search().setSearchRange(range.getStart(), range.getEnd());
            }
            mc.get().setTab(MainController.Tab.SEARCH);
        });

        mainHeapSearchAuto.setOnAction(event -> {
            Range range = searchWrtitableRange(memInfoTable.getItems(), "main");
            if (range != null) {
                mc.get().search().mainsetSearchRange(range.getStart(), range.getEnd());
            }
            Range range2 = searchWrtitableRange(memInfoTable.getItems(), "heap");
            if (range != null) {
                mc.get().search().setSearchRange(range2.getStart(), range2.getEnd());
            }
            mc.get().setTab(MainController.Tab.SEARCH);
        });

        mainsearchBoth.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().search().mainsetSearchRange(model.getAddr(), model.getEnd());
        });
        mainsearchStart.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().search().mainsetStart(model.getAddr());
        });
        mainsearchEnd.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().search().mainsetEnd(model.getEnd());
        });

        ptrMain.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().pointer().setRelativeAddress(model.getAddr());
        });

        ptrFilter.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().pointer().setFilterMin(model.getAddr());
            mc.get().pointer().setFilterMax(model.getEnd());
            mc.get().setTab(MainController.Tab.POINTER_SEARCH);
        });

        ptrFilterStart.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().pointer().setFilterMin(model.getAddr());
        });

        ptrFilterEnd.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().pointer().setFilterMax(model.getEnd());
        });

        searchEnd.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().search().setEnd(model.getEnd());
        });
        memoryView.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().memory().setViewAddress(model.getAddr());
            mc.get().setTab(MainController.Tab.MEMORY_VIEWER);

        });
        disassembler.setOnAction(event -> {
            MemoryInfoTableModel model = memInfoTable.getSelectionModel().getSelectedItem();
            if (model == null) {
                return;
            }
            mc.get().disassembly().setDisassembleAddress(model.getAddr());
            mc.get().setTab(MainController.Tab.DISASSEMBLER);
        });
        getItems().addAll(searchBoth, searchStart, searchEnd, mainsearchAuto, heapsearchAuto, mainHeapSearchAuto,
                mainsearchStart, mainsearchEnd, ptrMain, ptrFilter, ptrFilterStart, ptrFilterEnd, memoryView,
                disassembler);
    }

    private Range searchWrtitableRange(List<MemoryInfoTableModel> memInfos, String regionNameToSearch) {

        boolean inRegion = false;
        Long startAddress = null;
        Long stopAddress = null;
        for (MemoryInfoTableModel memInfo : memInfos) {
            String regionName = memInfo.nameProperty().getValue();
            if (StringUtils.equalsIgnoreCase(regionName, regionNameToSearch)) {
                inRegion = true;
            } else if (inRegion && !StringUtils.equalsIgnoreCase(regionName, "-")) {
                inRegion = false;
                break;
            }

            boolean writeAccess = AccessType.WRITE.hasAcces(memInfo.accessProperty().getValue());

            if (writeAccess) {
                if (inRegion && startAddress == null) {
                    startAddress = memInfo.getAddr();
                }
                if (inRegion && startAddress != null) {
                    stopAddress = memInfo.getEnd();
                }
            }
        }

        if (startAddress == null) {
            return null;
        }
        Range range = new Range(startAddress, stopAddress);
        return range;
    }
}
