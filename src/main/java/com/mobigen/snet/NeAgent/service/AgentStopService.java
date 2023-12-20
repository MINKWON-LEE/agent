package com.mobigen.snet.NeAgent.service;

import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.entity.RequestParams;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

public class AgentStopService {
    private static Logger logger = LoggerFactory.getLogger(AgentStopService.class);
    private static HashMap params = new HashMap();
    /**
     * 3.0 Agent 중지
     * Hoyoung - 2020.06.18
     * @throws IOException
     * @throws InterruptedException
     */
    public void killAgent(JobEntity3 jobEntity) throws Exception {
        logger.debug("Kill agent");
        boolean isWin = false;
        if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
            isWin = true;
        }
        Thread.sleep(500);

        String exec = INMEMORYDB.AGENT_ROOT_DIR+"bin/kill.sh";
        if (isWin)
            exec = INMEMORYDB.AGENT_ROOT_DIR+"bin\\kill.bat";
        CommandLine cmdLine = null;
        if (isWin) {
            cmdLine = CommandLine.parse("cmd");
            cmdLine.addArgument("/c");
        } else {
            cmdLine = CommandLine.parse("/bin/sh");
            cmdLine.addArgument("-c");
        }
        cmdLine.addArgument(exec, false);
        DefaultExecutor executor = new DefaultExecutor();
        try {
            logger.debug(String.valueOf(cmdLine));
            executor.execute(new CommandLine(cmdLine), new DefaultExecuteResultHandler());
        } catch (Exception e) {
            logger.error("FAILED TO DELETE " + e.getMessage(), e);
            params = new RequestParams().setParams("NOTI");
            jobEntity.setNotiType("AN053");
            jobEntity.setJobType(jobEntity.getJobType3());
            new NotiService().doProcessNoti(jobEntity,params);
        }
    }
}
