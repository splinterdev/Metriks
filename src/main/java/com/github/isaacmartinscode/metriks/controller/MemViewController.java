package com.github.isaacmartinscode.metriks.controller;

import com.github.isaacmartinscode.metriks.model.entities.process.Process;
import com.github.isaacmartinscode.metriks.service.MemMetric;
import com.github.isaacmartinscode.metriks.util.ChangeView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class MemViewController implements Initializable {

    MemMetric memMetric = new MemMetric();

    @FXML
    private Label totalMemory;

    @FXML
    private Label usableMemory;

    @FXML
    private Label usedGbMemory;

    @FXML
    private Label usedMemoryPercentage;

    @FXML
    private Label hardwareReserved;

    @FXML
    private Label frequency;

    @FXML
    private Label usedSlots;

    @FXML
    private Label type;

    @FXML
    private TableView<Process> tableView;

    @FXML
    private TableColumn<Process, String> tCName;

    @FXML
    private TableColumn<Process, Integer> tCPid;

    @FXML
    private TableColumn<Process, String> tCUsagePercentage;

    @FXML
    private TableColumn<Process, Integer> tCThreads;

    @FXML
    private TableColumn<Process, String> tCUser;

    @FXML
    private AreaChart<Number, Number> areaChart;
    private final ObservableList<Process> processList = memMetric.getProcessList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
        initUIRefresh();
        totalMemory.setText(String.format("%.1f", memMetric.getTotalMemory()) + " GB");
        usableMemory.setText(String.format("%.1f",memMetric.getUsableMemory()) + " GB");
        usedGbMemory.setText(String.format("%.1f",memMetric.getUsedMemory()) + " GB");
        usedMemoryPercentage.setText(memMetric.getUsedMemoryPercentage() + " %");
        hardwareReserved.setText(String.format("%.1f",memMetric.getHardwareReserved()) + " GB");
        frequency.setText(memMetric.getFrequency() + " MHz");
        usedSlots.setText(String.valueOf(memMetric.getTotalSlotsUsed()));
        type.setText(memMetric.getType());
    }

    private void initializeNodes() {
        tableView.setItems(processList);
        areaChart.getData().addAll(memMetric.getSerie());
        tCName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tCPid.setCellValueFactory(new PropertyValueFactory<>("pId"));
        tCUsagePercentage.setCellValueFactory(new PropertyValueFactory<>("formattedUsagePercentage"));
        tCThreads.setCellValueFactory(new PropertyValueFactory<>("threads"));
        tCUser.setCellValueFactory(new PropertyValueFactory<>("user"));
    }

    private void refreshUI() {
        usedGbMemory.setText(String.format("%.1f",memMetric.getUsedMemory()) + " GB");
        usedMemoryPercentage.setText(memMetric.getUsedMemoryPercentage() + " %");
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

    public void onDiskButtonAction() {
        ChangeView.change("/com/github/isaacmartinscode/metriks/views/DiskView.fxml");
    }

    public void onNetworkButtonAction() {
        ChangeView.change("/com/github/isaacmartinscode/metriks/views/NetworkView.fxml");
    }
}
