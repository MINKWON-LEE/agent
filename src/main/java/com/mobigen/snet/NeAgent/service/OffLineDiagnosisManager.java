package com.mobigen.snet.NeAgent.service;

import com.mobigen.snet.NeAgent.crypto.AESCryptography;
import com.mobigen.snet.NeAgent.main.CollectingLogOutputStream;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import com.mobigen.snet.NeAgent.utils.CommonUtils;
import com.mobigen.snet.NeAgent.utils.UnzipUtil;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Created by osujin12 on 2016. 4. 1..
 */
public class OffLineDiagnosisManager {

    Logger logger = LoggerFactory.getLogger(getClass());
    private String managerCd ="";
    private String dgType = "";
    private String exceDir = "";
	private String sw_nm = "";
	private String sw_info = "";
	private String gov_flag = "";
	private String user_id = "-";
	private String user_pw = "-";
	private String etc_name = "-";
	private String inst_id = "-";
	private String inst_path = "-";
	private String socketFilePath = "-";
	private String criteria = "-";		// 진단 기준
	private String param1 = "-";
	private String param2 = "-";
	private String param3 = "-";
	private String diagUse = "-";
	private String confNm = "-";

	public OffLineDiagnosisManager(String managerCd, String dgType, String exceDir,String gov_flag,String sw_nm, String sw_info,String user_id,String user_pw,String etc_name,String inst_id,String inst_path,String socketFilePath, String criteria, String param1, String param2, String param3, String diagUse, String confNm) {
        this.managerCd = managerCd;
        this.dgType = dgType;
        this.exceDir = replaceBlank(exceDir);
		this.gov_flag = gov_flag.toLowerCase();
//		this.sw_nm = replaceBlank(sw_nm.toLowerCase());
        this.sw_nm = sw_nm;
		this.sw_info = replaceBlank(sw_info);
		if(!user_id.equalsIgnoreCase(""))
			this.user_id = user_id;
		if(!user_pw.equalsIgnoreCase(""))
			this.user_pw = user_pw;
        if(!etc_name.equalsIgnoreCase(""))
            this.etc_name = etc_name;
		if(!inst_id.equalsIgnoreCase(""))
			this.inst_id = inst_id;
		if(!inst_path.equalsIgnoreCase(""))
			this.inst_path = replaceBlank(inst_path);
		if(!socketFilePath.equalsIgnoreCase(""))
			this.socketFilePath = socketFilePath;
		if(!criteria.equalsIgnoreCase(""))
			this.criteria = criteria;
		if(!param1.equalsIgnoreCase(""))
			this.param1 = param1;
		if(!param2.equalsIgnoreCase(""))
			this.param2 = param2;
		if(!param3.equalsIgnoreCase(""))
			this.param3 = param3;
		if(!diagUse.equalsIgnoreCase(""))
			this.diagUse = diagUse.trim();
		if(!confNm.equalsIgnoreCase(""))
			this.confNm = confNm.trim();
    }
    
    public String replaceBlank(String org){
    	String blank = "_SP1BL_";
    	String str = INMEMORYDB.cReplaceAll(org, blank, " ");
    	return str;
    } 

    public void doDiagnosis() throws Exception {
        logger.debug("[SYS] Run Diagnosis by Manual..");
        String full_path = "";
        String diagsFileName = "";
        String diagsPath = "";
        if (diagUse == null || !diagUse.equals("1")) {
			logger.debug("[SYS] Old Diagnosis <1> by Manual..");
			diagsPath = INMEMORYDB.OFFLINE_DIAGPATH_DINFONOTUSE;
		} else {
			logger.debug("[SYS] Run DiagInfo <<2>> by Manual..");
			diagsPath = INMEMORYDB.DIAGPATH;
			doDiagInfo();
		}
		String configPath = confNm;
		String diagsMuxFileName = "";

		logger.info("=====  diagnosis inform =====");
		logger.debug("dgType : "+dgType);
		logger.debug("gov_flag : "+gov_flag);
		logger.debug("sw_nm : "+sw_nm);
		logger.debug("sw_info : "+sw_info);
		logger.debug("user_id : " + user_id);
		logger.debug("user_pw : " + user_pw);
		logger.debug("etc_name : " + etc_name);
		logger.debug("inst_id : " + inst_id);
		logger.debug("inst_path : " + inst_path);
		logger.debug("sockeFilePath : " + socketFilePath);
		logger.debug("criteria : " + criteria);
		logger.debug("param1 : " + param1);
		logger.debug("param2 : " + param2);
		logger.debug("param3 : " + param3);

        if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID)>-1) {

			diagsPath = INMEMORYDB.DIAGPATH;
			diagsFileName = INMEMORYDB.selectDignosisFile(dgType,gov_flag,sw_nm,sw_info);
			diagsMuxFileName = CommonUtils.toMux(diagsFileName+INMEMORYDB.ZIP);
			logger.debug("[SYS] Run WINDOWS diagnosis ==> "+diagsFileName);

			//decryption
			new AESCryptography().decryptionShFile(diagsPath+diagsMuxFileName,diagsPath+diagsFileName+INMEMORYDB.ZIP);
			full_path = diagsPath + diagsFileName + INMEMORYDB.ZIP;

            try {
                new UnzipUtil(full_path,diagsPath).unzip();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            String candidateDiagsFileName = "";
            logger.debug("Searching For Window Diag Initiator.");
            File files = new File(diagsPath);
            for(int i=0; i< files.listFiles().length; i++){
            	candidateDiagsFileName = files.listFiles()[i].getName();            	
            	diagsFileName= candidateDiagsFileName;
            	if(candidateDiagsFileName.indexOf(INMEMORYDB.BAT)>-1 && candidateDiagsFileName.indexOf(diagsFileName) > -1) {
            		diagsFileName= candidateDiagsFileName;
            		break;
            	}else{
            		diagsFileName = "prog"+INMEMORYDB.BAT;
            	}
            }
            logger.debug("Do Start Diagnosis By : "+diagsFileName);
//            
//			if (dgType.equalsIgnoreCase("DB") || dgType.equalsIgnoreCase("WEB") || dgType.equalsIgnoreCase("WAS"))
//				diagsFileName = "prog";
//			diagsFileName += INMEMORYDB.BAT;
			
        } else {
            diagsFileName = INMEMORYDB.selectDignosisFile(dgType,gov_flag,sw_nm,sw_info);

			logger.debug("[SYS] Run UNIX diagnosis ==> "+diagsFileName);
			//decryption
			diagsMuxFileName = CommonUtils.toMux(diagsFileName+INMEMORYDB.SH);
			new AESCryptography().decryptionShFile(diagsPath+diagsMuxFileName,diagsPath+diagsFileName);

        }
        if (diagUse == null || !diagUse.equals("1")) {
			if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID)>1) {
				logger.debug("getunixagent file exist..");
			} else {
				File getunixFIle = new File(INMEMORYDB.DIAGPATH + INMEMORYDB.UNIX_GET_SH);
				if (getunixFIle.exists()) {
					INMEMORYDB.fileCopy(INMEMORYDB.DIAGPATH + INMEMORYDB.UNIX_GET_SH, INMEMORYDB.OFFLINE_DIAGPATH_DINFONOTUSE, INMEMORYDB.UNIX_GET_SH);
				}
			}

			configPath = makeConfig(diagsPath, dgType, user_id, user_pw, etc_name, inst_id, inst_path, sw_nm, sw_info, socketFilePath, criteria, param1, param2, param3);

		}
        excuteDiagnosis(diagsPath,diagsFileName,configPath);
    }

	private String makeConfig(String diagsPath,String dgType,String user_id,String user_pw,String etc_name,String inst_id,String inst_path, String sw_nm, String sw_info, String socketFilePath, String criteria, String param1, String param2, String param3){

		String configFile = "inform.conf";
		if(criteria.equalsIgnoreCase("MSIT"))
        {
            // MSIT 이면 conf.dat2 로...
            configFile = "conf.dat2";
        }
		String returnVal = "-";
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {


			fw = new FileWriter(diagsPath+configFile);
			bw = new BufferedWriter(fw);
			if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID)>-1){
				if (dgType.equalsIgnoreCase("OS")) {
					String chk = checkDatFile();
					if (!chk.equals(sw_info)) {
						sw_info = chk;
						logger.debug("[SYS] change sw_info from dat file");
					}
				}

				bw.write(user_id + "\n" + user_pw + "\n" + etc_name + "\n" + inst_id + "\n" + inst_path + "\n0\n5\n600\n" + sw_nm + "\n" + sw_info + "\n" + socketFilePath + "\n" + criteria + "\n" + param1 + "\n" + param2 + "\n" + param3 );
				logger.debug("[SYS] Create Win Parameter File.");
			} else {
                if (sw_nm.equalsIgnoreCase("Tibero4_SP1")) {
                    sw_nm = "Tibero";
                } else if (sw_nm.equalsIgnoreCase("Tibero5")) {
                    sw_nm = "Tibero";
                } else if (sw_nm.equalsIgnoreCase("Tibero5_SP1")) {
                    sw_nm = "Tibero";
                } else if (sw_nm.equalsIgnoreCase("Tibero6")) {
                    sw_nm = "Tibero";
                }

				bw.write(user_id + INMEMORYDB.DELIMITER + user_pw + INMEMORYDB.DELIMITER + etc_name + INMEMORYDB.DELIMITER + inst_id + INMEMORYDB.DELIMITER + inst_path + INMEMORYDB.DELIMITER
						+ "0" + INMEMORYDB.DELIMITER + "5" + INMEMORYDB.DELIMITER + "600" + INMEMORYDB.DELIMITER + sw_nm + INMEMORYDB.DELIMITER + sw_info + INMEMORYDB.DELIMITER
						+ socketFilePath + INMEMORYDB.DELIMITER + criteria + INMEMORYDB.DELIMITER + param1 + INMEMORYDB.DELIMITER + param2 + INMEMORYDB.DELIMITER + param3);
				logger.debug("[SYS] Create Unix Parameter File.");
			}

			returnVal = diagsPath+configFile;

			return returnVal;
		} catch (IOException e) {
			logger.debug("[ERR]" + e.getMessage());
			return returnVal;
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error("FAILED bw makeConfig " + e.getMessage(), e);
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					logger.error("FAILED fw makeConfig " + e.getMessage(), e);
				}
			}
		}
	}
    private void excuteDiagnosis(String diagsPath,String diagsFileName,String configPath) {

		try {
			long startTime = System.currentTimeMillis();
			CommandLine cmdLine = null;
			File file = new File(diagsPath);
			for (int i = 0; i < file.listFiles().length; i++) {
				if (file.listFiles()[i].getName().toLowerCase().indexOf(diagsFileName) > -1) {
					String exec = "";
					String cdDir = "cd " + diagsPath + " && ";
					if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
						exec = diagsPath + file.listFiles()[i].getName() + " " + configPath;
						exec = cdDir + exec;
						cmdLine=CommandLine.parse("cmd.exe");
						cmdLine.addArgument("/c");
						cmdLine.addArgument(exec, false);

					} else { //linux
						exec = "cd " + diagsPath + "; chmod 755 ./*; ";
						if (configPath.equalsIgnoreCase("-")) {
							exec += diagsPath + file.listFiles()[i].getName() + " " + INMEMORYDB.UNIX_GET_SH;
						} else {
							exec += diagsPath + file.listFiles()[i].getName() + " " + INMEMORYDB.UNIX_GET_SH + " " + configPath;
							logger.debug("offline file : " + diagsPath + file.listFiles()[i].getName() + " - " + INMEMORYDB.UNIX_GET_SH + " - " + configPath);
						}
						cmdLine=CommandLine.parse("/bin/sh");
						cmdLine.addArgument("-c");
						cmdLine.addArgument(exec, false);
					}

					int exitCode = -1;
					CollectingLogOutputStream los = null;
					DefaultExecutor executor = null;
					ExecuteWatchdog watchdog = null;
					// 2021.07.05(Hoyoung) - 구동(스트림처리 문제 제거) 방식 변경
					try {
						System.out.print("DefaultExecutor Process start.");
						executor = new DefaultExecutor();
						watchdog = new ExecuteWatchdog(60 * 1000 * Integer.parseInt(INMEMORYDB.EXEC_WAIT_TIME));
						executor.setWatchdog(watchdog);
						los = new CollectingLogOutputStream();
						executor.setStreamHandler(new PumpStreamHandler(los));
						exitCode = executor.execute(cmdLine);
						long duration = System.currentTimeMillis() - startTime;
						logger.info("exit : " + exitCode + ", Process completed in : " + duration + " millis, below is its output");

						if (watchdog.killedProcess()) {
							logger.error("Process timed out and was killed by watchdog.");
						}
						System.out.print("DefaultExecutor Process end.");
						String s = null;
						List lines = los.getLines();
						for (int idx = 0; idx < lines.size(); idx++) {
							s = lines.get(idx).toString();
							System.out.println(new Date().toString() + " : " + s);
							logger.info(new Date().toString() + " : " + s);
						}
					} catch (ExecuteException ex) {
						logger.error("DefaultExecutor Process :" + ex.getMessage());
					} catch (Exception e1) {
						logger.error("Exception Process :" + e1.getMessage());
					} finally {
						if (los != null) {
							los.close();
						}
					}

					if (!diagsFileName.equals(INMEMORYDB.DIAGINFO)) {
						logger.debug("[SYS] File move to " + exceDir);

						findReusltFile(INMEMORYDB.RESULT_FILE_AGENT_DIR);

						resultFileEnc(exceDir);

						logger.debug("resultFileEnc ... end");

						//스크립트 및 결과 파일들을 삭제한다.
						//1회진단용 하위 파일들을 삭제한다.
						INMEMORYDB.deleteFilesInFolder(INMEMORYDB.replaceDir(INMEMORYDB.AGENT_SYS_ROOT_DIR));

						logger.debug("deleteFilesInFolder ... end");

						if (confNm != null && confNm.isEmpty()) {
							File confFile = new File(confNm);
							INMEMORYDB.deleteFile(confFile.getCanonicalPath(), confFile.getName());
						}

						logger.debug("delete confFile... end - " + confNm);
					}
					break;
				}
			}
		}catch (Exception e) {
			logger.error(e.getMessage());
		}

    }

    private void doDiagInfo() throws Exception {

		String encDiagInfoFile =  INMEMORYDB.AGENT_LIBS_DIR + CommonUtils.toMux(INMEMORYDB.DIAGINFO);
		new AESCryptography().decryptionShFile(encDiagInfoFile,INMEMORYDB.AGENT_LIBS_DIR+INMEMORYDB.DIAGINFO);


		logger.debug("[SYS] Run UNIX diaginfo file first. (diaginfo)==> "+ encDiagInfoFile);

		excuteDiagnosis(INMEMORYDB.AGENT_LIBS_DIR, INMEMORYDB.DIAGINFO, confNm);

		Thread.sleep(500);
	}

    private String[] listToArray(List list){
    	String[] result = new String[list.size()];
    	for(int i=0; i<list.size();i++){
    		result[i] = list.get(i).toString();
    	}
    	return result;
    }

    private void findReusltFile(String path) throws IOException {

        File list = new File(path);
			for(int i =0; i< list.listFiles().length; i++){
				if (list.listFiles()[i].isDirectory()) {
					findReusltFile(list.listFiles()[i].getAbsolutePath());
				} else {
					if (list.listFiles()[i].getAbsolutePath().indexOf(INMEMORYDB.DAT)>-1) {
						logger.debug("[SYS] System Information file : " + list.listFiles()[i].getAbsolutePath());
						//Get , 진단 결과 파일을 옮겨준다.
						INMEMORYDB.fileCopy(list.listFiles()[i].getAbsolutePath(), exceDir, list.listFiles()[i].getName());
						INMEMORYDB.deleteOffLineFile(INMEMORYDB.replaceDir(INMEMORYDB.GETSCRIPT_RESULT_FILE_AGENT_DIR));
					} else if (list.listFiles()[i].getAbsoluteFile().toString().indexOf(INMEMORYDB.XML)>-1) {
						logger.debug("[SYS] Diagnosis Result file : " + list.listFiles()[i].getAbsolutePath());

						if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
								String resultXmlFile = list.listFiles()[i].getAbsolutePath();
//								if (!(sw_nm.equalsIgnoreCase("MySQL") && gov_flag.equalsIgnoreCase("MSIT")) || !(sw_nm.equalsIgnoreCase("IIS") && gov_flag.equalsIgnoreCase("Common"))) {
//									changeHostName(resultXmlFile);
//								}
							if (dgType.equalsIgnoreCase("DB") && sw_nm.equalsIgnoreCase("DB2") && gov_flag.equalsIgnoreCase("MSIT")) {
								changeSwEtc(resultXmlFile);
							}
						}

						INMEMORYDB.fileCopy(list.listFiles()[i].getAbsolutePath(), exceDir, list.listFiles()[i].getName());
						INMEMORYDB.deleteOffLineFile(INMEMORYDB.replaceDir(INMEMORYDB.DIAG_RESULT_FILE_AGENT_DIR));
					} else if (list.listFiles()[i].getAbsoluteFile().toString().indexOf(INMEMORYDB.TXT)>-1) {
						logger.debug("[SYS] System Information files for diaginfo (skipped) : " + list.listFiles()[i].getAbsolutePath());
					} else {
						logger.debug("[SYS] System Information isDirectory error : " + list.listFiles()[i].getAbsolutePath());
					}
				}
			}

    }
    /*
    1회용 암호화 처리.
    Hoyoung - 2020.11.24
     */
    private void resultFileEnc(String path) {
		if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
			path=path + "\\";
		} else {
			path=path + "/";
		}
		File list = new File(path);
		logger.info("1회용 암호화 처리.........(path - " + path + ")");
		try {
			for(int i =0; i< list.listFiles().length; i++){
				if (list.listFiles()[i].isDirectory()) {
					findReusltFile(list.listFiles()[i].getAbsolutePath());
				} else {
					int pos = list.listFiles()[i].getName().lastIndexOf(".");
					if (pos > -1) {
						String ext = list.listFiles()[i].getName().substring((pos + 1));
						String fineName = list.listFiles()[i].getName().substring(0, pos);
//						if (list.listFiles()[i].getAbsolutePath().lastIndexOf(INMEMORYDB.DAT) > -1) {
						if (ext.equals("dat")) {
							logger.info("1회용 진단결과 파일(DAT) 암호화 처리........." + fineName + "," + list.listFiles()[i].getName());
							new AESCryptography().encryptionOneTimeFile(path, fineName, "." + ext);
							INMEMORYDB.deleteFile(path, fineName + "." + ext);

//						} else if (list.listFiles()[i].getAbsoluteFile().toString().lastIndexOf(INMEMORYDB.XML) > -1) {
						} else if (ext.equals("xml")) {
							logger.info("1회용 진단결과 파일(XML) 암호화 처리........." + fineName + "," + list.listFiles()[i].getName());
							new AESCryptography().encryptionOneTimeFile(path, fineName, "." + ext);
							INMEMORYDB.deleteFile(path, fineName + "." + ext);

						} else {
							logger.info("1회용 1진단결과 파일 isDirectory 실패 or 대상아님 ........." + fineName + "," + list.listFiles()[i].getName());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private String checkDatFile() {

		String swInfo = "";

    	try {
			File list = new File(INMEMORYDB.GETSCRIPT_RESULT_FILE_AGENT_DIR);
			for(int i =0; i < list.listFiles().length; i++) {
				if (list.listFiles()[i].getAbsolutePath().indexOf(INMEMORYDB.DAT) > -1) {
					String[] result = INMEMORYDB.readFile(list.listFiles()[i].getCanonicalFile());
					for (int j = 0;  j < result.length; j++) {
						if (result[j].indexOf("OS=") > -1) {
							String[] infos = result[j].split("=");
							String[] swInfos = infos[1].split("\\^");
							if (swInfos.length > 1) {
								swInfo = swInfos[1];
							}
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

    	return swInfo;
	}

	private String checkHostNameDatFile() {

		String dathostName = "";

		try {
			File list = new File(INMEMORYDB.GETSCRIPT_RESULT_FILE_AGENT_DIR);
			for(int i =0; i < list.listFiles().length; i++) {
				if (list.listFiles()[i].getAbsolutePath().lastIndexOf(INMEMORYDB.DAT) > -1) {
					String[] result = INMEMORYDB.readFile(list.listFiles()[i].getCanonicalFile());
					for (int j = 0;  j < result.length; j++) {
						if (result[j].indexOf("HOSTNAME=") > -1) {
							logger.debug("#### HOSTNAME : " + result[j]);
							String[] hostnametmp = result[j].split("=");
							String hostNames = hostnametmp[1];
							if (hostNames.length() > 1) {
								dathostName = hostNames;
							}
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		return dathostName;
	}

	private void changeSwEtc(String resultXmlFile) throws IOException {

		String fileName = resultXmlFile;
		File inputFile = new File(fileName);
		File outputFile = new File(fileName + ".temp");
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		BufferedReader br = null;
		BufferedWriter bw = null;

		fileInputStream = new FileInputStream(inputFile);
		fileOutputStream = new FileOutputStream(outputFile);
		br = new BufferedReader(new InputStreamReader(fileInputStream,"UTF-8"));
		bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));

		String line;

		try {
			while ((line = br.readLine()) != null) {
				if (line.contains("<SW_ETC>")) {
					logger.debug("#### line : " + line);
					line = line.replaceAll("<SW_ETC>" + etc_name + "</SW_ETC>", "<SW_ETC>-</SW_ETC>");
					logger.debug("#### String replace : " + line);
					logger.debug("[SYS] change Windows DB2 SW_ETC in XML file");
				}

				bw.write(line + "\r\n");
				bw.flush();
			}

			br.close();
			bw.close();

			logger.debug("#### XML Name : " + fileName);
			inputFile.renameTo(new File(fileName + ".tt"));
			outputFile.renameTo(new File(fileName));

			logger.debug("#### Old XML Name change : " + fileName + ".tt");
			logger.debug("#### New XML Name change : " + fileName);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void changeHostName(String resultXmlFile) throws IOException {

		String fileName = resultXmlFile;
		File inputFile = new File(fileName);
		File outputFile = new File(fileName + ".temp");
		FileInputStream fileInputStream = null;
		FileOutputStream fileOutputStream = null;
		BufferedReader br = null;
		BufferedWriter bw = null;

		fileInputStream = new FileInputStream(inputFile);
		fileOutputStream = new FileOutputStream(outputFile);
		br = new BufferedReader(new InputStreamReader(fileInputStream,"UTF-8"));
		bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "euc-kr"));

		String line;

		try {

			String dathostName = checkHostNameDatFile();
			logger.debug("[SYS] change HostName from dat file");

			while ((line = br.readLine()) != null) {
				if (line.contains("<HOST_NM>")) {
					logger.debug("#### line : " + line);
					logger.debug("#### dathostName : " + dathostName);
					line = line.replaceAll(line, "<HOST_NM>" + dathostName + "</HOST_NM>");
					logger.debug("#### String replace : " + line);
					logger.debug("[SYS] change Windows HOSTNAME in XML file");
				}

				bw.write(line + "\r\n");
				bw.flush();
			}

			br.close();
			bw.close();

			logger.debug("#### XML Name : " + fileName);
			inputFile.renameTo(new File(fileName + ".tt"));
			outputFile.renameTo(new File(fileName));

			logger.debug("#### Old XML Name change : " + fileName + ".tt");
			logger.debug("#### New XML Name change : " + fileName);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

}