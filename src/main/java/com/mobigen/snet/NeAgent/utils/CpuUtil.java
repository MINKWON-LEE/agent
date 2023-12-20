package com.mobigen.snet.NeAgent.utils;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CpuUtil {
    private static Logger logger = LoggerFactory.getLogger(CpuUtil.class);
    public String getCpuUsage() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
    //  logger.debug("availableProcessors - " + availableProcessors);

        long prevUpTime = runtimeMXBean.getUptime();
        long prevProcessCpuTime = operatingSystemMXBean.getProcessCpuTime();
        double cpuUsage;

     // logger.debug("prevUpTime - " + prevUpTime);
     // logger.debug("prevProcessCpuTime - " + prevProcessCpuTime);
        try {
            Thread.sleep(500);
        } catch (Exception ignored) {}

        operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long upTime = runtimeMXBean.getUptime();
     // logger.debug("upTime - " + upTime);
        long processCpuTime = operatingSystemMXBean.getProcessCpuTime();
    //  logger.debug("processCpuTime - " + processCpuTime);
        long elapsedCpu = processCpuTime - prevProcessCpuTime;
     // logger.debug("elapsedCpu - " + elapsedCpu);
        long elapsedTime = upTime - prevUpTime;
     // logger.debug("elapsedTime - " + elapsedTime);

        cpuUsage = Math.min(99F, elapsedCpu / (elapsedTime * 10000F /* * availableProcessors */ ));

        if (cpuUsage < 0) {
            cpuUsage = 100;
        }

        return Double.toString(cpuUsage);
    }
}
