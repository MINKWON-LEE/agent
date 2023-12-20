package com.mobigen.snet.NeAgent.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Updator {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	public static String VERSION_RECV_FILE_AGENT_ROOT_DIR = "C:\\snet\\txFiles\\inbound\\version\\";
	public static String AGENT_ROOT_DIR = "C:\\snet\\agent\\";
	public static String AGENT_LIBS_DIR = "C:\\snet\\agent\\libs\\";
	public static String AGENT_BIN_DIR = "C:\\snet\\agent\\bin\\";
	public static String AGENT_JRE_DIR = "C:\\snet\\agent\\jre\\bin\\";
	public static String PATCH_SCRIPT="agentsetup_patchJar.bat";

	public void doPatch(String arg){
		 logger.info("+++++++++++++++ Updater start  ++++++++++++++++++++");
		 StringBuffer output = new StringBuffer();
         Process p;
         String line;
         String command = "cmd /c " + VERSION_RECV_FILE_AGENT_ROOT_DIR+ PATCH_SCRIPT + " " +arg;
         try {
        	 
        	 
         	logger.info("[ShellCommandHandler] cmd = " + command);
         	
         	p = Runtime.getRuntime().exec(command);
             BufferedReader br =new BufferedReader(new InputStreamReader(p.getInputStream()));
             while((line=br.readLine()) != null){
                 output.append(line);
                 logger.info("[ShellCommandHandler] result = " + line);
             }
             p.waitFor();
		
	}catch(Exception e)
         {
		System.exit(0);
         }
	}
	
	public static void main(String args[]){

		Updator u = new Updator();
		String ver = args[0];
		u.doPatch(ver);

	}
	
	

}
