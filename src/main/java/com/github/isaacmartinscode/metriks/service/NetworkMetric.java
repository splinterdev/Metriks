package com.github.isaacmartinscode.metriks.service;

import com.github.isaacmartinscode.metriks.model.entities.hardware.networkAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import oshi.hardware.NetworkIF;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class NetworkMetric {

    private static final Map<String, networkAdapter> networkHashMap = new HashMap<>();
    private static final Map<String, NetworkIF> prevNetworkIF = new HashMap<>();
    private static final ObservableList<networkAdapter> adapterList = FXCollections.observableArrayList();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static boolean updating = false;

    private void refreshAdapterList() {
        List<NetworkIF> networkIFList = SystemService.hardware.getNetworkIFs();
        List<String> activeAdapters = networkIFList
                .stream()
                .map(NetworkIF::getName)
                .toList();
        networkIFList.removeIf(adapter -> adapter.getIPv4addr().length == 0 || adapter.getIPv4addr()[0].isEmpty() && adapter.getIPv6addr().length == 0 || adapter.getIPv6addr()[0].isEmpty());
        prevNetworkIF.keySet().removeIf(name -> !activeAdapters.contains(name));
        networkHashMap.keySet().removeIf(name -> !activeAdapters.contains(name));

        for(NetworkIF adapter : networkIFList) {
            networkAdapter networkAdapter;
            if(updating && networkHashMap.containsKey(adapter.getName())) {
                networkAdapter = networkHashMap.get(adapter.getName());
                networkAdapter.calcSentSpeed(prevNetworkIF.get(adapter.getName()).getBytesSent(), adapter.getBytesSent());
                networkAdapter.calcReceivedSpeed(prevNetworkIF.get(adapter.getName()).getBytesRecv(), adapter.getBytesRecv());
                networkAdapter.setPacketsSent(adapter.getPacketsSent());
                networkAdapter.setPacketsReceived(adapter.getPacketsRecv());
            } else {
                String[] interfaceName = adapter.getName().split("_");
                networkAdapter = new networkAdapter(adapter.getDisplayName(), interfaceName[0], adapter.getBytesSent(), adapter.getBytesRecv(), adapter.getIPv4addr()[0], adapter.getIPv6addr()[0], adapter.getMacaddr(), adapter.getPacketsSent(), adapter.getPacketsRecv());
                networkHashMap.put(adapter.getName(), networkAdapter);
            }
            prevNetworkIF.put(adapter.getName(), adapter);
        }
        adapterList.setAll(networkHashMap.values()
                .stream()
                .sorted(Comparator.comparing(networkAdapter::getPacketsSent).reversed())
                .collect(Collectors.toList())
        );
    }

    public void initScheduledRefresh() {
        Runnable refresh = () -> {
            refreshAdapterList();
            updating = true;
        };
        scheduler.scheduleAtFixedRate(refresh, 0, (long) 1.5, TimeUnit.SECONDS);
    }

    public void endScheduledRefresh() {
        scheduler.shutdownNow();
    }

    public static ObservableList<networkAdapter> getAdapterList() {
        return adapterList;
    }
}
