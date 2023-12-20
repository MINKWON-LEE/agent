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

public class AgentReStartService {
    private static Logger logger = LoggerFactory.getLogger(AgentReStartService.class);
    private static HashMap params = new HashMap();
    static final int waitTime = 1000 * 10;
    /**
     * Agent 재시작 처리
     * Hoyoung - 2020.06.18
     * @throws IOException
     * @throws InterruptedException
     */
    public void runAgent(JobEntity3 jobEntity) throws Exception {
        logger.debug("Restart agent");
        Thread.sleep(waitTime);

        boolean isWin = false;

        if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
            isWin = true;
        }

        String exex = INMEMORYDB.AGENT_ROOT_DIR+"bin/run.sh";

        if (isWin)
            exex = INMEMORYDB.AGENT_ROOT_DIR+"bin\\runSrv.bat";

        CommandLine cmdLine = null;
        if (isWin) {
            cmdLine = CommandLine.parse("cmd");
            cmdLine.addArgument("/c");
        } else {
            cmdLine = CommandLine.parse("/bin/sh");
            cmdLine.addArgument("-c");
        }
        cmdLine.addArgument(exex, false);

        DefaultExecutor executor = new DefaultExecutor();

        try {
            logger.debug(String.valueOf(cmdLine));

            executor.execute(new CommandLine(cmdLine), new DefaultExecuteResultHandler());

        } catch (Exception e) {
            logger.error("FAILED TO RESTART RUN.SH " + e.getMessage(), e);
            params = new RequestParams().setParams("NOTI");
            jobEntity.setNotiType("AN053");
            jobEntity.setJobType(jobEntity.getJobType3());
            new NotiService().doProcessNoti(jobEntity,params);
        }
    }
}
