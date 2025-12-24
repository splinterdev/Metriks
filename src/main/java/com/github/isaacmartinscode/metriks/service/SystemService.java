package com.github.isaacmartinscode.metriks.service;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

public class SystemService {

    private static final SystemInfo systemInfo = new SystemInfo();
    protected static final HardwareAbstractionLayer hardware = systemInfo.getHardware();
    protected static final OperatingSystem os = systemInfo.getOperatingSystem();
    protected static final CentralProcessor cpu = hardware.getProcessor();
}
