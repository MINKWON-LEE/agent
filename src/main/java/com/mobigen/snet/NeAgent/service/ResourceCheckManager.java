package com.mobigen.snet.NeAgent.service;

import com.igloosec.sigar.utill.SigarUtil;
import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.entity.RequestParams;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ResourceCheckManager implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(ResourceCheckManager.class);
	private static HashMap params = new HashMap();
	private static NotiService processNoti = new NotiService();
	private static JobEntity3 jobEntity = new JobEntity3();

	private int agentCpuMax;
	private int agentMemoryMax;

	private final int failCount = 5;

	public ResourceCheckManager(int agentCpuMax, int agentMemoryMax) {
		this.agentCpuMax = agentCpuMax;
		this.agentMemoryMax = agentMemoryMax;
	}

	public ResourceCheckManager(String agentCpuMax, String agentMemoryMax ) {
		this.agentCpuMax = Integer.parseInt(agentCpuMax);
		this.agentMemoryMax = Integer.parseInt(agentMemoryMax);
	}


	public ResourceCheckManager(String agentCpuMax, String agentMemoryMax ,JobEntity3 entity ) {
		this.agentCpuMax = Integer.parseInt(agentCpuMax);
		this.agentMemoryMax = Integer.parseInt(agentMemoryMax);
		this.jobEntity = entity;
	}
	public void run() {

		int cpuMaxCount = 0;
		int memMaxCount = 0;

		try {
			while (true){
				Thread.sleep(5000);
				CpuPerc cpu = SigarUtil.getSigar().getCpuPerc();
				Mem mem = SigarUtil.getSigar().getMem();
				double cpuStatus = resourceFomat(cpu.getUser());
				logger.debug("Server CPU Check : "+ cpuStatus+  " SmartGuard AgentCpuMax : " +  agentCpuMax);
				if(cpuStatus > agentCpuMax){
					cpuMaxCount++;
					logger.debug("Exceeded Max Server CPU... Count : "+ cpuMaxCount);
				}

				double memStatus = resourceFomat(((double) mem.getActualUsed() / (double)mem.getTotal()));
				logger.debug("Server Mem Check : "+ memStatus+  " SmartGuard AgentMemoryMax : " +  agentMemoryMax);
				if(memStatus > agentMemoryMax){
					memMaxCount++;
					logger.debug("Exceeded Max Server Memory... Count : "+ memMaxCount);
				}

				if(cpuMaxCount > failCount || memMaxCount > failCount){

					logger.info("Exceeded Max Server CPU or Memory ... RESTART AGENT");
                    // 진단파일 Agent 실행 실패 자원 문제
                    params = new RequestParams().setParams("NOTI");
                    jobEntity.setNotiType("AN055");
                    processNoti.doProcessNoti(jobEntity,params);
                    logger.info("=====================================================");
					CommandLine cmdLine = null;

					String exex = "";
					if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
						cmdLine = CommandLine.parse("cmd");
						cmdLine.addArgument("/c");

						exex = INMEMORYDB.AGENT_ROOT_DIR+"\\bin\\runSrv.bat";
					} else {
						cmdLine = CommandLine.parse("/bin/sh");
						cmdLine.addArgument("-c");

						//exex = "/usr/local/snet/agent/bin/run.sh";
						exex = INMEMORYDB.AGENT_ROOT_DIR+"/bin/run.sh";
					}

					cmdLine.addArgument(exex, false);

					DefaultExecutor executor = new DefaultExecutor();

					try {
						logger.debug(String.valueOf(cmdLine));
						executor.execute(new CommandLine(cmdLine), new DefaultExecuteResultHandler());
					} catch (Exception e) {
						logger.error("FAILED TO RESTART AGENT" + e.getMessage(), e);
					}
					break;
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	private double resourceFomat(double val){
		String p = String.valueOf(val * 100.0D);
		int ix = p.indexOf(".") + 1;
		String percent = p.substring(0, ix) + p.substring(ix, ix + 1);

		return Double.parseDouble(percent);
	}

}




