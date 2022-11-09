package me.mdbell.noexs.ui.controllers;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import me.mdbell.javafx.control.AddressSpinner;
import me.mdbell.javafx.control.HexSpinner;
import me.mdbell.noexs.ui.Settings;
import me.mdbell.noexs.ui.models.SearchValueModel;
import me.mdbell.noexs.ui.services.PointerSearchResult;
import me.mdbell.noexs.ui.services.PointerSearchService;

public class PointerSearchController implements IController {

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
        cm.getItems().addAll(memoryView);
        resultList.contextMenuProperty().set(cm);
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
        //Collections.sort(results);
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
            
            //List<PointerSearchResult> orederResults = setToOrderedList(results);
            this.unfilteredResults.addAll(results);
            Collections.sort(this.unfilteredResults,(p1,p2) -> (p1.getAddress() < p2.getAddress() ? -1 : (p1.getAddress() > p2.getAddress() ? 1 : 0)));
            mc.setStatus("Search Completed!");
            toggleInput(false);
            updateFilter();
        });

        mc.getProgressBar().progressProperty().bind(searchService.progressProperty());
        searchService.restart();

        toggleInput(true);
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
