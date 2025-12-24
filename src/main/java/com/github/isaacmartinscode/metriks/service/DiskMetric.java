package com.github.isaacmartinscode.metriks.service;

import com.github.isaacmartinscode.metriks.model.entities.hardware.Storage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.software.os.OSFileStore;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DiskMetric {

    private static Map<String, Storage> storageHashMap = new HashMap<>();
    private static Map<String, HWDiskStore> prevHwDiskStore = new HashMap<>();
    private static List<HWDiskStore> hwDiskStoreList = new ArrayList<>();
    private static List<OSFileStore> osFileStoreList = new ArrayList<>();
    private static ObservableList<Storage> storageList = FXCollections.observableArrayList();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static boolean updating = false;

    private void refreshStorageList() {
        hwDiskStoreList = SystemService.hardware.getDiskStores();
        osFileStoreList = SystemService.os.getFileSystem().getFileStores();
        List<String> activeStorages = hwDiskStoreList
                .stream()
                .map(HWDiskStore::getSerial)
                .toList();
        prevHwDiskStore.keySet().removeIf(serial -> !activeStorages.contains(serial));
        storageHashMap.keySet().removeIf(serial -> !activeStorages.contains(serial));

        for(HWDiskStore diskStore : hwDiskStoreList) {
            Storage storage;
            if(updating && storageHashMap.containsKey(diskStore.getSerial())) {
                storage = storageHashMap.get(diskStore.getSerial());
                storage.calcWriteSpeed(prevHwDiskStore.get(diskStore.getSerial()).getWriteBytes(), diskStore.getWriteBytes());
                storage.calcReadSpeed(prevHwDiskStore.get(diskStore.getSerial()).getReadBytes(), diskStore.getReadBytes());
                storage.calcUptimePercentage(prevHwDiskStore.get(diskStore.getSerial()).getTransferTime(), diskStore.getTransferTime());
            } else {
                long usableSize = 0;
                long usedSize = 0;
                for (OSFileStore store : osFileStoreList) {
                    List<HWPartition> partitions = diskStore.getPartitions();
                    for (HWPartition part : partitions) {
                        if(store.getUUID().contains(part.getUuid())) {
                            usableSize = store.getTotalSpace();
                            usedSize = usableSize - store.getUsableSpace();
                        }
                    }
                }
                String[] fields = diskStore.getModel().split(" \\(");
                storage = new Storage(fields[0], usedSize, usableSize, diskStore.getSize(), diskStore.getWriteBytes(),diskStore.getReadBytes());
                storageHashMap.put(diskStore.getSerial(), storage);
            }
            prevHwDiskStore.put(diskStore.getSerial(), diskStore);
        }
        storageList.setAll(storageHashMap.values()
                .stream()
                .sorted(Comparator.comparing(Storage::getUptimePercentage).reversed())
                .collect(Collectors.toList())
        );
    }

    public void initScheduledRefresh() {
        Runnable refresh = () -> {
            refreshStorageList();
            updating = true;
        };
        scheduler.scheduleAtFixedRate(refresh, 0, (long) 1.5, TimeUnit.SECONDS);
    }

    public void endScheduledRefresh() {
        scheduler.shutdownNow();
    }

    public ObservableList<Storage> getStorageList() {
        return storageList;
    }
}
