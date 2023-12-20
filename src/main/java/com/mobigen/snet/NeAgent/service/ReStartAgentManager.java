package com.mobigen.snet.NeAgent.service;


import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReStartAgentManager implements Runnable{

	private static Logger logger = LoggerFactory.getLogger(ReStartAgentManager.class);


	static final int waitTime = 1000 * 10;

	public void run() {

		try {
			logger.debug("Restart Agent !!!! Wait Time : " + 10 + "H");
			//waitTime 후 에이전트 재실행 로직 (default 10 분 후에 재시작 하기
			Thread.sleep(waitTime);

			CommandLine cmdLine = null;

			String exex = "";
			if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
				cmdLine = CommandLine.parse("cmd");
				cmdLine.addArgument("/c");
				exex = INMEMORYDB.AGENT_ROOT_DIR+"bin\\runSrv.bat";

			} else {
				cmdLine = CommandLine.parse("/bin/sh");
				cmdLine.addArgument("-c");

				exex = INMEMORYDB.AGENT_ROOT_DIR+ "bin/run.sh";
			}

			cmdLine.addArgument(exex, false);

			DefaultExecutor executor = new DefaultExecutor();

			try {
				logger.debug(String.valueOf(cmdLine));
				executor.execute(new CommandLine(cmdLine), new DefaultExecuteResultHandler());
			} catch (Exception e) {
				logger.error("FAILED TO RESTART AGENT" + e.getMessage(), e);
			}


		} catch (InterruptedException e) {
			logger.error("FAILED TO RESTART AGENT InterruptedException" + e.getMessage(), e);
		}

	}

}




