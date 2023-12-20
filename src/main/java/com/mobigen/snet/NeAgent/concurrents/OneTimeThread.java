
/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.concurrents.OneTimeThread.java
 * @author : Je Joong Lee
 * created at : 2016. 1. 5.
 * description : 
 */
package com.mobigen.snet.NeAgent.concurrents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OneTimeThread extends Thread {
    protected volatile boolean stop;
    private static Logger logger = LoggerFactory.getLogger(OneTimeThread.class);

    protected boolean isFinishRequested() {
        return stop;
    }

    public void finish() throws InterruptedException {
        stop = true;
        interrupt();
        join();
    }
    
    public void run() {
        
            try {
                task();
            } catch (InterruptedException ie) {
                logger.debug("Interrupted Error :" + ie.getLocalizedMessage() + ")");
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.debug("Interrupted :" + e + ")");
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        
    }
    public abstract void task() throws Exception;
}