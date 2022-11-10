package me.mdbell.noexs.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import me.mdbell.javafx.control.AddressSpinner;
import me.mdbell.javafx.control.HexSpinner;
import me.mdbell.noexs.ui.Settings;
import me.mdbell.noexs.ui.services.PointerSearchResult;
import me.mdbell.noexs.ui.services.PointerSearchService;

public class PointerSearchController implements IController {

    private static final Logger logger = LogManager.getLogger(PointerSearchController.class);

    @FXML
    AddressSpinner addressSpinner;

    @FXML
    Spinner<Integer> depthSpinner;

    @FXML
    Spinner<Integer> threadsSpinner;

    @FXML
    HexSpinner offsetSpinner;

    @FXML
    Button dumpFileButton;

    @FXML
    TextField dumpFilePath;

    @FXML
    TextField resultText;

    @FXML
    ListView<PointerSearchResult> resultList;

    @FXML
    Button searchButton;

    @FXML
    Button cancelButton;

    @FXML
    AddressSpinner filterMaxAddress;
    @FXML
    AddressSpinner filterMinAddress;

    @FXML
    CheckBox filterCheckbox;

    @FXML
    CheckBox autoOffsetCheckbox;

    @FXML
    AddressSpinner relativeAddress;

    private List<PointerSearchResult> unfilteredResults = new ArrayList<>();

    private ObservableList<PointerSearchResult> results;

    private MainController mc;

    private final PointerSearchService searchService = new PointerSearchService();

    private String formatPointer(PointerSearchResult item) {
        String text = "";
        if (autoOffsetCheckbox.isSelected()) {
            text = item.formattedRegion(mc.tools());
        } else {
            Long relativeAddressValue = relativeAddress.getValue();
            if (relativeAddressValue == null || relativeAddressValue == 0) {
                text = item.formattedRaw();
            } else {
                text = item.formattedMain(relativeAddressValue);
            }
        }
        return text;
    }

    @FXML
    public void initialize() {
        depthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));

        threadsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Runtime.getRuntime().availableProcessors()));
        threadsSpinner.getValueFactory().setValue(Settings.getPointerThreadCount());

        depthSpinner.getValueFactory().setValue(Settings.getPointerDepth());
        offsetSpinner.getValueFactory().setValue(Settings.getPointerOffset());

        resultList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                resultText.setText(formatPointer(newValue));
            } else {
                resultText.setText("");
            }
        });

        resultList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PointerSearchResult item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String text = formatPointer(item);

                    setText(text);
                }
            }

        });

        dumpFilePath.textProperty().addListener((observable, oldValue, newValue) -> updateSearchButton());

        searchService.messageProperty().addListener((observable, oldValue, newValue) -> mc.setStatus(newValue));

        results = FXCollections.observableArrayList();
        resultList.setItems(results);
        resultList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        relativeAddress.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        autoOffsetCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter();
        });

        filterCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            filterMaxAddress.setDisable(!newValue);
            filterMinAddress.setDisable(!newValue);
            updateFilter();
        });

        filterMaxAddress.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());
        filterMinAddress.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        ContextMenu cm = new ContextMenu();
        MenuItem memoryView = new MenuItem("Memory Viewer");
        memoryView.setOnAction(event -> {
            PointerSearchResult psr = resultList.getSelectionModel().getSelectedItem();
            if (psr == null) {
                return;
            }
            mc.memory().setViewAddress(psr.getAddress());
            mc.setTab(MainController.Tab.MEMORY_VIEWER);
        });

        MenuItem exportPointers = new MenuItem("Export All Pointers Raw");
        exportPointers.setOnAction(event -> {
            String pointerType = "Raw";
            String extension = "*.rptr";

            Function<PointerSearchResult, String> pointerFormatter = psr -> psr.formattedRaw();

            savePointers(resultList.getItems(), pointerType, extension, pointerFormatter);

        });

        MenuItem exportSelectedPointers = new MenuItem("Export Selected Pointers Raw");
        exportSelectedPointers.setOnAction(event -> {
            String pointerType = "Raw";
            String extension = "*.rptr";

            Function<PointerSearchResult, String> pointerFormatter = psr -> psr.formattedRaw();

            savePointers(resultList.getSelectionModel().getSelectedItems(), pointerType, extension, pointerFormatter);

        });

        MenuItem exportRelativePointers = new MenuItem("Export All Pointers Relative");
        exportRelativePointers.setOnAction(event -> {
            String pointerType = "Relative";
            String extension = "*.ptr";

            Function<PointerSearchResult, String> pointerFormatter = psr -> psr.formattedRegion(mc.tools());
            savePointers(resultList.getItems(), pointerType, extension, pointerFormatter);
        });

        MenuItem exportSelectedRelativePointers = new MenuItem("Export Selected Pointers Relative");
        exportSelectedRelativePointers.setOnAction(event -> {
            String pointerType = "Relative";
            String extension = "*.ptr";

            Function<PointerSearchResult, String> pointerFormatter = psr -> psr.formattedRegion(mc.tools());
            savePointers(resultList.getSelectionModel().getSelectedItems(), pointerType, extension, pointerFormatter);
        });

        MenuItem preservePointers = new MenuItem("Preserve the pointers already found in file ...");
        preservePointers.setOnAction(event -> {
            File f = mc.browseFile(false, null, null, "Load", "Relative pointers", "*.ptr");
            if (f == null) {
                return;
            }

            List<PointerSearchResult> intersection = new ArrayList<>();

            try {
                List<String> pointersInFile = FileUtils.readLines(f, "UTF-8");
                for (PointerSearchResult psr : resultList.getItems()) {
                    String formattedPointer = psr.formattedRegion(mc.tools());
                    boolean found = pointersInFile.contains(formattedPointer);
                    logger.debug("Check pointer : {} -> found={}", formattedPointer, found);
                    if (found) {
                        intersection.add(psr);
                    }
                }
            } catch (IOException e) {
                logger.error("Error while loding pointers ", e);
            }

            logger.info("Found {} similar to file {}", intersection.size(), f.getPath());

            this.unfilteredResults.clear();
            this.unfilteredResults.addAll(intersection);
            updateFilter();

        });

        cm.getItems().addAll(memoryView, exportSelectedPointers, exportPointers, exportSelectedRelativePointers,
                exportRelativePointers, preservePointers);

        resultList.contextMenuProperty().set(cm);
    }

    private void savePointers(List<PointerSearchResult> lpsr, String pointerType, String extension,
            Function<PointerSearchResult, String> pointerFormatter) {
        if (lpsr == null) {
            return;
        }

        File f = mc.browseFile(true, null, null, "Save As...", pointerType + " pointers", extension);
        if (f == null) {
            return;
        }

        StringBuilder strPointers = new StringBuilder();
        for (PointerSearchResult psr : lpsr) {
            strPointers.append(pointerFormatter.apply(psr)).append("\n");
        }

        try {
            FileUtils.writeStringToFile(f, strPointers.toString(), "UTF-8");
        } catch (IOException e) {
            logger.error("Error while saving " + pointerType + " pointers ", e);
        }
    }

    private void updateFilter() {
        results.clear();
        long min = filterMinAddress.getValue();
        long max = filterMaxAddress.getValue();
        if (filterCheckbox.isSelected()) {
            List<PointerSearchResult> filtered = new ArrayList<>();
            for (PointerSearchResult result : unfilteredResults) {
                long addr = result.getAddress();
                if (addr <= max && addr >= min) {
                    filtered.add(result);
                }
            }
            results.addAll(filtered);
        } else {
            results.addAll(unfilteredResults);
        }
        // Collections.sort(results);
    }

    private void updateSearchButton() {
        String dump = dumpFilePath.getText();
        searchButton.setDisable(dump.length() == 0);
    }

    @Override
    public void setMainController(MainController c) {
        this.mc = c;
    }

    public void onBrowseDumpFile(ActionEvent event) {
        mc.browseFile(false, null, dumpFilePath.textProperty(), "Please select a memory dump", "Memory Dump Files",
                "*.dmp");
    }

    public void onSearchAction(ActionEvent event) {
        searchService.setDumpPath(Paths.get(dumpFilePath.getText()));
        searchService.setMaxDepth(depthSpinner.getValue());
        searchService.setMaxOffset(offsetSpinner.getValue());
        searchService.setAddress(addressSpinner.getValue());
        searchService.setThreadCount(threadsSpinner.getValue());

        searchService.setOnFailed(event1 -> {
            mc.setStatus("Search Failed!");
            event1.getSource().getException().printStackTrace();
            toggleInput(false);
        });

        searchService.setOnSucceeded(event1 -> {
            Set<PointerSearchResult> results = (Set<PointerSearchResult>) event1.getSource().getValue();
            this.unfilteredResults.clear();
            this.unfilteredResults.addAll(results);
            sortResultList(this.unfilteredResults);
            mc.setStatus("Search Completed!");
            toggleInput(false);
            updateFilter();
        });

        mc.getProgressBar().progressProperty().bind(searchService.progressProperty());
        searchService.restart();

        toggleInput(true);
    }

    private void sortResultList(List<PointerSearchResult> resultList) {
        Collections.sort(resultList,
                (p1, p2) -> (p1.getAddress() < p2.getAddress() ? -1 : (p1.getAddress() > p2.getAddress() ? 1 : 0)));
    }

    private List<PointerSearchResult> setToOrderedList(Set<PointerSearchResult> results) {
        List<PointerSearchResult> orederResults = new ArrayList<>(results);
        Collections.sort(orederResults);
        return orederResults;
    }

    public void onCancelAction(ActionEvent event) {
        if (searchService.cancel()) {
            toggleInput(false);
        }
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void stop() {
        Settings.setPointerOffset(offsetSpinner.getValue());
        Settings.setPointerDepth(depthSpinner.getValue());
        Settings.setPointerThreadCount(threadsSpinner.getValue());
    }

    private void toggleInput(boolean disabled) {
        addressSpinner.setDisable(disabled);
        depthSpinner.setDisable(disabled);
        threadsSpinner.setDisable(disabled);
        offsetSpinner.setDisable(disabled);
        dumpFileButton.setDisable(disabled);
        searchButton.setDisable(disabled);
        cancelButton.setDisable(!disabled);
    }

    public void setFilterMin(long address) {
        filterMinAddress.getValueFactory().setValue(address);
    }

    public void setFilterMax(long address) {
        filterMaxAddress.getValueFactory().setValue(address);
    }

    public void setRelativeAddress(long address) {
        relativeAddress.getValueFactory().setValue(address);
    }
}
