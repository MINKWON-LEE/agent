package com.mobigen.snet.NeAgent.service;

import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;

public class Vmanager {
	Logger logger = LoggerFactory.getLogger(getClass());
	public void executeVerUp(JobEntity3 jobEntity){
		processCmd(jobEntity);
	}

	private void processCmd(JobEntity3 jobEntity){
		String OSTYPE = "unix";
		String oldVersion = getVersionInfo(INMEMORYDB.VERSIONPATH.trim());
    	
		logger.info("agent 업데이트 프로세싱 스타트. ");
		
    	if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) >= 0) {
    		OSTYPE = "win";    		
		}
        logger.info("INMEMORYDB.osType,version :"+INMEMORYDB.osType+","+oldVersion);
        String recvPath = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.RECV,INMEMORYDB.AGENTUPDATEREQ);
        StringBuffer output = new StringBuffer();
        String installFileName = "agentsetup_patchJar";
        Process p;
        String line;
         // String command ="nohup "+ recvPath+installFileName + " " +(String)fileInfoMap.get("NEWVER");
		String command ="";
         try {
        	 logger.info("OS 타입:"+OSTYPE);
        	if("win".equalsIgnoreCase(OSTYPE)){
				installFileName = installFileName+".bat";
				command ="nohup "+ recvPath+"" + installFileName+" " +jobEntity.getNewVerStr();
        		logger.info("EXECUTING UPDATOR_S.!!!!" + command);
        		//command = INMEMORYDB.AGENT_JRE_DIR+"java -cp "+ INMEMORYDB.VERSION_RECV_FILE_AGENT_ROOT_DIR +"NeAgent.jar "+ "Updator"+" " +(String)fileInfoMap.get("NEWVER");
				command = INMEMORYDB.AGENT_JRE_DIR+"java -cp "+ INMEMORYDB.VERSION_RECV_FILE_AGENT_ROOT_DIR +"NeAgent.jar "+ "com.mobigen.snet.NeAgent.utils.Updator"+" " +jobEntity.getNewVerStr();
				logger.info("command :"+command);
        		p = Runtime.getRuntime().exec(command);
        		logger.info("EXECUTING UPDATOR_E.!!!!");
        	}else{
				installFileName = installFileName+".sh";
				command ="nohup "+ recvPath+"" + installFileName+" " +jobEntity.getNewVerStr();
        		// Runtime.getRuntime().exec("chmod 755 "+recvPath+installFileName);
				Runtime.getRuntime().exec("chmod 755 "+command);
        		logger.info("[ShellCommandHandler] cmd = " + command);
             	
             	p = Runtime.getRuntime().exec(command);
                BufferedReader br =new BufferedReader(new InputStreamReader(p.getInputStream()));
                while((line=br.readLine()) != null){
                     output.append(line);
                     logger.info("[ShellCommandHandler] result = " + line);
                }
                p.waitFor();
        	}
         } catch (Exception e) {
          logger.error("ERROR DURING AGENT-UPDATE:"+e.getMessage());
         }
         // 패치 버젼 체크 임시 코드 (3.1 기능 수저정 필요)
		try {
			for(int i=0;i<10;i++) {
				INMEMORYDB.AGENT_VER = getNewVersionInfo(INMEMORYDB.VERSIONPATH);
				if(jobEntity.getNewVerStr().equals(INMEMORYDB.AGENT_VER.trim() )) {
					logger.info("Patch Version : " + INMEMORYDB.AGENT_VER);
					break;
				}
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			logger.error("ERROR InterruptedException:"+e);
		}


	}

	public String getVersionInfo(String fileName){
    	File f = new File(fileName);
    	if(!f.exists())return "";
    	String vStr = readFile(f);
    	logger.info("AGENT VER ="+vStr + ", leng :"+vStr.split("\\.").length);
    	if( vStr.split("\\.").length >= 3){
    		return vStr;	
    	}else{
    		logger.error("Version Context is NOT VALID : "+vStr);
    		return "";
    	}
     }

	/**
	 * Update Version 가죠오기
	 * 2020.10.23 - hoyoung
	 * @param fileName
	 * @return
	 */
	public String getNewVersionInfo(String fileName) {


		 File f = new File(fileName);
		 if(!f.exists())return "";
		 String vStr = readFile(f);
		logger.info("AGENT VER ="+vStr + ", leng :"+vStr.split("\\.").length);
		 if( vStr.split("\\.").length >= 3){
			 return vStr;
		 }else{
			 logger.error("Version Context is NOT VALID : "+vStr);
			 return "3.0.0.0";
		 }
	 }

	
	private String readFile(File f){
	
		String lineStr = "";
		int lineCnt = 0;
		
		try {
			BufferedReader input = new BufferedReader(new FileReader(f));
			
			try{
				String line = null;
				while( (line = input.readLine()) != null)
				{
					if(!line.startsWith("#")){
						lineStr = lineStr + line;	
					}					
					lineCnt++;
				}
				input.close();
			}catch(IOException ex){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(lineCnt != 1) lineStr = "";    	
		
		return lineStr;
	}
}
