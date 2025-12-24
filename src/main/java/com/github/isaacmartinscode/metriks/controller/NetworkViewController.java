package com.github.isaacmartinscode.metriks.controller;

import com.github.isaacmartinscode.metriks.model.entities.hardware.networkAdapter;
import com.github.isaacmartinscode.metriks.service.NetworkMetric;
import com.github.isaacmartinscode.metriks.util.ChangeView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class NetworkViewController implements Initializable {

    NetworkMetric networkMetric = new NetworkMetric();

    @FXML
    private Label adapterName;

    @FXML
    private Label ipvFour;

    @FXML
    private Label ipvSix;

    @FXML
    private Label mac;

    @FXML
    private Label packetsSent;

    @FXML
    private Label packetsReceived;

    @FXML
    private Label sent;

    @FXML
    private Label receive;

    @FXML
    private TableView<networkAdapter> tableView;

    @FXML
    private TableColumn<networkAdapter, String> tCName;

    @FXML
    private TableColumn<networkAdapter, String> tCInterface;

    @FXML
    private TableColumn<networkAdapter, String> tCGbSent;

    @FXML
    private TableColumn<networkAdapter, String> tCGbReceived;
    private final ObservableList<networkAdapter> adapterList = networkMetric.getAdapterList();
    private networkAdapter adapter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
        initUIRefresh();
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                adapterName.setText(newSel.getAdapterName());
                ipvFour.setText(newSel.getIpvFour());
                ipvSix.setText(newSel.getIpvSix());
                mac.setText(newSel.getMac());
                Platform.runLater(() -> {
                    packetsSent.setText(String.valueOf(newSel.getPacketsSent()));
                    packetsReceived.setText(String.valueOf(newSel.getPacketsReceived()));
                    sent.setText(newSel.getFormattedSendSpeed());
                    receive.setText(newSel.getFormattedReceiveSpeed());
                    adapter = newSel;
                });
            }
        });
    }

    private void initializeNodes() {
        tableView.setItems(adapterList);
        tCName.setCellValueFactory(new PropertyValueFactory<>("adapterName"));
        tCInterface.setCellValueFactory(new PropertyValueFactory<>("interfaceName"));
        tCGbSent.setCellValueFactory(new PropertyValueFactory<>("formattedGbSent"));
        tCGbReceived.setCellValueFactory(new PropertyValueFactory<>("formattedGbReceived"));
    }

    private void refreshUI() {
        if(adapter != null) {
            packetsSent.setText(String.valueOf(adapter.getPacketsSent()));
            packetsReceived.setText(String.valueOf(adapter.getPacketsReceived()));
            sent.setText(adapter.getFormattedSendSpeed());
            receive.setText(adapter.getFormattedReceiveSpeed());
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

    public void onDiskButtonAction() {
        ChangeView.change("/com/github/isaacmartinscode/metriks/views/DiskView.fxml");
    }

    public void onMemButtonAction() {
        ChangeView.change("/com/github/isaacmartinscode/metriks/views/MemView.fxml");
    }
}
