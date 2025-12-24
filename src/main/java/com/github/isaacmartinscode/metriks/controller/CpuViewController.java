package com.github.isaacmartinscode.metriks.controller;

import com.github.isaacmartinscode.metriks.model.entities.process.Process;
import com.github.isaacmartinscode.metriks.service.CpuMetric;
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

public class CpuViewController implements Initializable {

    CpuMetric cpuMetric = new CpuMetric();

    @FXML
    private Label cpuName;

    @FXML
    private Label baseClock;

    @FXML
    private Label totalCore;

    @FXML
    private Label totalLogicCore;

    @FXML
    private Label totalProcesses;

    @FXML
    private Label totalProcessesThreads;

    @FXML
    private Label userPercentage;

    @FXML
    private Label systemPercentage;

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
    private TableColumn<Process, String> tCCpuUsageTime;

    @FXML
    private TableColumn<Process, String> tCUser;

    @FXML
    private AreaChart<Number, Number> areaChart;
    private final ObservableList<Process> processList = cpuMetric.getProcessList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
        initUIRefresh();
        cpuName.setText(cpuMetric.getCpuName());
        baseClock.setText(String.format("%.2f", cpuMetric.getBaseClock()) + " GHz");
        totalCore.setText(String.valueOf(cpuMetric.getTotalCore()));
        totalLogicCore.setText(String.valueOf(cpuMetric.getTotalLogicCore()));
        totalProcesses.setText(String.valueOf(cpuMetric.getTotalProcesses()));
        totalProcessesThreads.setText(String.valueOf(cpuMetric.getTotalProcessesThreads()));
        userPercentage.setText(String.format("%.1f", cpuMetric.getUserPercentage()) + " %");
        systemPercentage.setText(String.format("%.1f", cpuMetric.getSystemPercentage()) + " %");
    }

    private void initializeNodes() {
        tableView.setItems(processList);
        areaChart.getData().addAll(cpuMetric.getUserSerie(), cpuMetric.getSystemSerie());
        tCName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tCPid.setCellValueFactory(new PropertyValueFactory<>("pId"));
        tCUsagePercentage.setCellValueFactory(new PropertyValueFactory<>("formattedUsagePercentage"));
        tCThreads.setCellValueFactory(new PropertyValueFactory<>("threads"));
        tCCpuUsageTime.setCellValueFactory(new PropertyValueFactory<>("cpuUsageTime"));
        tCUser.setCellValueFactory(new PropertyValueFactory<>("user"));
    }

    private void refreshUI() {
        totalProcesses.setText(String.valueOf(cpuMetric.getTotalProcesses()));
        totalProcessesThreads.setText(String.valueOf(cpuMetric.getTotalProcessesThreads()));
        userPercentage.setText(String.format("%.1f", cpuMetric.getUserPercentage()) + " %");
        systemPercentage.setText(String.format("%.1f", cpuMetric.getSystemPercentage()) + " %");
    }

    private void initUIRefresh() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1.5), event -> refreshUI())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void onMemButtonAction() {
        ChangeView.change("/com/github/isaacmartinscode/metriks/views/MemView.fxml");
    }

    public void onDiskButtonAction() {
        ChangeView.change("/com/github/isaacmartinscode/metriks/views/DiskView.fxml");
    }

    public void onNetworkButtonAction() {
        ChangeView.change("/com/github/isaacmartinscode/metriks/views/NetworkView.fxml");
    }
}
