package com.github.isaacmartinscode.metriks.controller;

import com.github.isaacmartinscode.metriks.model.entities.hardware.Storage;
import com.github.isaacmartinscode.metriks.service.DiskMetric;
import com.github.isaacmartinscode.metriks.util.ChangeView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class DiskViewController implements Initializable {

    DiskMetric diskMetric = new DiskMetric();

    @FXML
    private Label diskName;

    @FXML
    private Label writeSpeed;

    @FXML
    private Label readSpeed;

    @FXML
    private Label grossSize;

    @FXML
    private Label usableSize;

    @FXML
    private Label usedTotalSize;

    @FXML
    private TableView<Storage> tableView;

    @FXML
    private TableColumn<String, Storage> tCName;

    @FXML
    private TableColumn<String, Storage> tCUptime;

    @FXML
    private TableColumn<String, Storage> tCSize;

    @FXML
    private TableColumn<String, Storage> tCWrites;

    @FXML
    private TableColumn<String, Storage> tCReads;

    @FXML
    private ProgressBar progressBar;

    private final ObservableList<Storage> storageList = diskMetric.getStorageList();
    private Storage storage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
        initUIRefresh();
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                diskName.setText(newSel.getName());
                grossSize.setText(newSel.getFormattedGrossSize());
                usableSize.setText(String.format("%.1f", newSel.getUsableSize()) + " GB");
                usedTotalSize.setText(String.format("%.1f", newSel.getUsedSize()) + " GB / " + String.format("%.1f", newSel.getUsableSize()) + " GB");
                Platform.runLater(() -> {
                    writeSpeed.setText(newSel.getFormattedWriteSpeed());
                    readSpeed.setText(newSel.getFormattedReadSpeed());
                });
                progressBar.setProgress(newSel.getUsedSize() / newSel.getUsableSize());
                storage = newSel;
            }
        });
    }

    private void initializeNodes() {
        tableView.setItems(storageList);
        tCName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tCUptime.setCellValueFactory(new PropertyValueFactory<>("formattedUptimePercentage"));
        tCSize.setCellValueFactory(new PropertyValueFactory<>("formattedGrossSize"));
        tCWrites.setCellValueFactory(new PropertyValueFactory<>("formattedGbWrite"));
        tCReads.setCellValueFactory(new PropertyValueFactory<>("formattedGbRead"));
    }

    private void refreshUI() {
        if(storage != null) {
            writeSpeed.setText(storage.getFormattedWriteSpeed());
            readSpeed.setText(storage.getFormattedReadSpeed());
        }
    }

    private void initUIRefresh() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1.5), event -> refreshUI())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void onCpuButtonAction() {
        ChangeView.change("/com/github/isaacmartinscode/metriks/views/CpuView.fxml");
    }

    public void onMemButtonAction() {
        ChangeView.change("/com/github/isaacmartinscode/metriks/views/MemView.fxml");
    }

    public void onNetworkButtonAction() {
        ChangeView.change("/com/github/isaacmartinscode/metriks/views/NetworkView.fxml");
    }
}
