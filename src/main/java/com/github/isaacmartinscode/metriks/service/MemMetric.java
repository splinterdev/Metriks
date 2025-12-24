package com.github.isaacmartinscode.metriks.service;

import com.github.isaacmartinscode.metriks.model.entities.process.MemProcess;
import com.github.isaacmartinscode.metriks.model.entities.process.Process;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import oshi.hardware.PhysicalMemory;
import oshi.software.os.OSProcess;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MemMetric {

    private static final List<PhysicalMemory> physicalMemories = SystemService.hardware.getMemory().getPhysicalMemory();
    private static final double totalMemory = convertTotalMemory();
    private static final double usableMemory = SystemService.hardware.getMemory().getTotal() / Math.pow(1024, 3);
    private static final double hardwareReserved = totalMemory - usableMemory;
    private static double usedMemory;
    private static int usedMemoryPercentage;
    private static final int frequency = convertMemoryFrequency();
    private static final int totalSlotsUsed = physicalMemories.size();
    private static final String type = physicalMemories.getFirst().getMemoryType();
    private static final Map<Integer, MemProcess> processHashMap = new HashMap<>();
    private static final ObservableList<Process> processList = FXCollections.observableArrayList();
    private static final ObservableList<XYChart.Data<Number, Number>> seriePointList = FXCollections.observableArrayList();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static boolean updating = false;
    private static int tickCounter = 0;

    private void convertMemoryUsages() {
        usedMemory = usableMemory - SystemService.hardware.getMemory().getAvailable() / Math.pow(1024, 3);
        usedMemoryPercentage = (int) (usedMemory * 100 / usableMemory);
    }

    private static double convertTotalMemory() {
        long total = 0;
        for(PhysicalMemory memory : physicalMemories) {
            total += memory.getCapacity();
        }
        return total / Math.pow(1024, 3);
    }

    private static int convertMemoryFrequency() {
        long totalHertz = physicalMemories.stream()
                .sorted(Comparator.comparing(PhysicalMemory::getClockSpeed))
                .toList().getFirst().getClockSpeed();
        return (int) (totalHertz / 1_000_000.0);
    }

    private void refreshProcessList() {
        List<OSProcess> processes = SystemService.os.getProcesses();
        processes.removeIf(p -> p.getState() == OSProcess.State.INVALID || p.getName().equalsIgnoreCase("Idle") || p.getName().equalsIgnoreCase("Memory Compression"));
        List<Integer> activeProcesses = processes
                .stream()
                .map(OSProcess::getProcessID)
                .toList();
        processHashMap.keySet().removeIf(pid -> !activeProcesses.contains(pid));

        for(OSProcess p : processes) {
            MemProcess memProcess;
            int pid = p.getProcessID();
            String user = p.getUser().equals("unknown") ? "Desconhecido" : p.getUser();
            if (updating && processHashMap.containsKey(pid)) {
                memProcess = processHashMap.get(pid);
                memProcess.calcUsagePercentage(p.getResidentSetSize(), usableMemory);
            } else {
                memProcess = new MemProcess(p.getName(), pid, p.getThreadCount(), user);
                processHashMap.put(pid, memProcess);
            }
        }
        processList.setAll(processHashMap.values()
                .stream()
                .sorted(Comparator.comparing(MemProcess::getMemUsage).reversed())
                .collect(Collectors.toList())
        );
    }

    private void refreshSeriePointList() {
        seriePointList.forEach(data -> data.setXValue(data.getXValue().intValue() + 1));
        if(updating) {
            seriePointList.addFirst(new XYChart.Data<>(0, usedMemoryPercentage));
        } else {
            seriePointList.addFirst(new XYChart.Data<>(0, 0));
        }
        if (tickCounter > 60) {
            seriePointList.removeLast();
        }
        tickCounter++;
    }

    public void initScheduledRefresh() {
        Runnable refresh = () -> {
            convertMemoryUsages();
            refreshProcessList();
            refreshSeriePointList();
            updating = true;
        };
        scheduler.scheduleAtFixedRate(refresh, 0, (long) 1.5, TimeUnit.SECONDS);
    }

    public void endScheduledRefresh() {
        scheduler.shutdownNow();
    }

    public double getTotalMemory() {
        return totalMemory;
    }

    public double getUsedMemory() {
        return usedMemory;
    }

    public int getUsedMemoryPercentage() {
        return usedMemoryPercentage;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getTotalSlotsUsed() {
        return totalSlotsUsed;
    }

    public String getType() {
        return type;
    }

    public static double getUsableMemory() {
        return usableMemory;
    }

    public static double getHardwareReserved() {
        return hardwareReserved;
    }

    public ObservableList<Process> getProcessList() {
        return processList;
    }

    public XYChart.Series<Number, Number> getSerie() {
        return new XYChart.Series<>(seriePointList);
    }
}
