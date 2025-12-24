package com.github.isaacmartinscode.metriks.service;

import com.github.isaacmartinscode.metriks.model.entities.process.CpuProcess;
import com.github.isaacmartinscode.metriks.model.entities.process.Process;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import oshi.hardware.CentralProcessor;
import oshi.software.os.OSProcess;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CpuMetric {

    private static final CentralProcessor.ProcessorIdentifier cpuIdentifier = SystemService.cpu.getProcessorIdentifier();
    private static final String cpuName = cpuIdentifier.getName();
    private static final double baseClock = cpuIdentifier.getVendorFreq() / 1_000_000_000.0;
    private static double userPercentage = 0, systemPercentage = 0;
    private static final int totalCore = SystemService.cpu.getPhysicalProcessorCount();
    private static final int totalLogicCore = SystemService.cpu.getLogicalProcessorCount();
    private static int totalProcesses = 0, totalProcessesThreads = 0;
    private static long[] prevTicks = SystemService.cpu.getSystemCpuLoadTicks();
    private static List<OSProcess> processes = new ArrayList<>();
    private static final Map<Integer, OSProcess> prevOSProcess = new HashMap<>();
    private static final Map<Integer, CpuProcess> processHashMap =  new HashMap<>();
    private static final ObservableList<Process> processList = FXCollections.observableArrayList();
    private static final ObservableList<XYChart.Data<Number, Number>> userSeriePointList = FXCollections.observableArrayList();
    private static final ObservableList<XYChart.Data<Number, Number>> systemSeriePointList = FXCollections.observableArrayList();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static int tickCounter = 0;
    private static boolean updating = false;

    private void calcUsagePercentage() {
        if(updating) {
            long[] currentTicks = SystemService.cpu.getSystemCpuLoadTicks();
            long userTimeVariation = currentTicks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
            long systemTimeVariation = currentTicks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
            long totalTimeVariation = 0;

            for (int i = 0; i < currentTicks.length; i++) {
                totalTimeVariation += (currentTicks[i] - prevTicks[i]);
            }

            if(totalTimeVariation > 0 && userTimeVariation >= 0 && systemTimeVariation >= 0) {
                userPercentage = (double) userTimeVariation / totalTimeVariation * 100.0;
                systemPercentage = (double) systemTimeVariation / totalTimeVariation * 100.0;
            } else {
                userPercentage = 0;
                systemPercentage = 0;
            }

            prevTicks = currentTicks;
        }
    }

    private void calcProcessInfo() {
        totalProcesses = 0;
        totalProcessesThreads = 0;
        for(OSProcess p : processes) {
            totalProcessesThreads += p.getThreadCount();
            totalProcesses++;
        }
    }

    private void refreshProcessList() {
        processes = SystemService.os.getProcesses();
        calcProcessInfo();
        processes.removeIf(p -> p.getState() == OSProcess.State.INVALID || p.getName().equalsIgnoreCase("Idle") || p.getName().equalsIgnoreCase("Memory Compression"));
        List<Integer> activeProcesses = processes
                .stream()
                .map(OSProcess::getProcessID)
                .toList();
        processHashMap.keySet().removeIf(pid -> !activeProcesses.contains(pid));
        prevOSProcess.keySet().removeIf(pid -> !activeProcesses.contains(pid));

        for (OSProcess p : processes) {
            CpuProcess cpuProcess;
            int pid = p.getProcessID();
            String user = p.getUser().equals("unknown") ? "Desconhecido" : p.getUser();

            if (updating && processHashMap.containsKey(pid)) {
                cpuProcess = processHashMap.get(pid);
                cpuProcess.calcUsagePercentage(prevOSProcess.get(pid), p, totalLogicCore);
                cpuProcess.convertCpuUsageTime(p.getUserTime() + p.getKernelTime());
            } else {
                cpuProcess = new CpuProcess(p.getName(), pid, p.getThreadCount(), user, p.getUserTime() + p.getKernelTime());
                processHashMap.put(pid, cpuProcess);
            }
            prevOSProcess.put(pid, p);
        }
        processList.setAll(processHashMap.values()
                        .stream()
                        .sorted(Comparator.comparing(CpuProcess::getCpuUsage).reversed())
                        .collect(Collectors.toList())
        );
    }


    private void refreshSeriePointLists() {
        userSeriePointList.forEach(data -> data.setXValue(data.getXValue().intValue() + 1));
        systemSeriePointList.forEach(data -> data.setXValue(data.getXValue().intValue() + 1));
        userSeriePointList.addFirst(new XYChart.Data<>(0, userPercentage));
        systemSeriePointList.addFirst(new XYChart.Data<>(0, systemPercentage));
        if (tickCounter > 60) {
            userSeriePointList.removeLast();
            systemSeriePointList.removeLast();
        }
        tickCounter++;
    }

    public void initScheduledRefresh() {
        Runnable refresh = () -> {
            calcUsagePercentage();
            refreshProcessList();
            refreshSeriePointLists();
            updating = true;
        };
        scheduler.scheduleAtFixedRate(refresh, 0, (long) 1.5, TimeUnit.SECONDS);
    }

    public void endScheduledRefresh() {
        scheduler.shutdownNow();
    }

    public String getCpuName() {
        return cpuName;
    }

    public double getBaseClock() {
        return baseClock;
    }

    public double getUserPercentage() {
        return userPercentage;
    }

    public double getSystemPercentage() {
        return systemPercentage;
    }

    public int getTotalCore() {
        return totalCore;
    }

    public int getTotalLogicCore() {
        return totalLogicCore;
    }

    public int getTotalProcesses() {
        return totalProcesses;
    }

    public int getTotalProcessesThreads() {
        return totalProcessesThreads;
    }

    public ObservableList<Process> getProcessList() {
        return processList;
    }

    public XYChart.Series<Number, Number> getUserSerie() {
        return new XYChart.Series<>(userSeriePointList);
    }

    public XYChart.Series<Number, Number> getSystemSerie() {
        return new XYChart.Series<>(systemSeriePointList);
    }
}
