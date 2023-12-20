package com.mobigen.snet.NeAgent.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.mobigen.snet.NeAgent.crypto.AESCryptography;
import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by osujin12 on 2016. 2. 18..
 */
public class CommonUtils {
	private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

	public static String makeFileName() {
		Random random = new Random();
		long rand = random.nextLong();

		if (rand < 0) rand = rand * -1;

		return String.valueOf(rand);
	}


	/**
	 * 진단,장비 처리 결과 파일 체크
	 * 2021.1.15 - Hoyoung
	 * @param jobEntity
	 * @return
	 * @throws Throwable
	 */
	public static boolean getResultFileCheck(JobEntity3 jobEntity) {
		boolean result = false;
		boolean diagSvr = false;

		String sendPath ="";
		String sendLog ="";
		String fileNm = "";
		try {
			fileNm = jobEntity.getAuditFileCd();
			sendLog = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType()) + fileNm + INMEMORYDB.LOG;
			logger.debug("==== makeZipFile Log ==== : " +sendLog );
			if (new File(sendLog).exists()) {
				logger.debug("==== makeZipFile Log result ==== : OK " );
			}
			sendPath = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType()) + fileNm + jobEntity.getFileType();
			logger.debug("==== makeZipFile XML/DAT ==== : " +sendPath );
			if (new File(sendPath).exists()) {
				logger.debug("==== makeZipFile  XML/DAT result ==== : OK " );
				result = true;
			} else {
				// 수집/진단 분리일 때
				if (jobEntity.getDiagInfoUse().toLowerCase().equals("y")) {
					result = checkDiagnosisForSvr(jobEntity);
					INMEMORYDB.DiagSvr = true;
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			logger.info("Diagnosis result BackFile Result : " + result);
			makeResultBackupFiles(jobEntity);
		}
		return result;
	}

	/**
	 * 수집 / 진단 중 실패 났을 때 로그파일만 업로드
	 */
	public static String getLastLogFileCheck(JobEntity3 jobEntity) {
		String logFileNm = "";
		String sendLog ="";
		try {
			sendLog = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType());

			File f = new File(sendLog);
			for(int i=0; i< f.listFiles().length; i++){

				String temp = f.listFiles()[i].getAbsolutePath();
				if(temp.indexOf(INMEMORYDB.LOG) > -1) {
					logFileNm = temp;
					break;
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			logger.info("==== Diagnosis Log File Only - Exist ==== : OK");
		}

		return logFileNm;
	}

	// 수집/진단 분리시 결과 파일 체크 및 백업디렉터리에 결과 옮겨두기
	public static boolean checkDiagnosisForSvr(JobEntity3 jobEntity) throws Exception {
		boolean ret = false;

		String diagPath = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType());
		String targetPath = INMEMORYDB.jobTypeAbsolutePath("BACKUPDG", "");
		logger.debug("==== Diagnosis For Svr ==== diagPath : " +diagPath + ", targetPath : " + targetPath );

		File diagPathDir = new File(diagPath);
		File[] txtFiles = diagPathDir.listFiles();
		if(txtFiles.length > 0) {
			for(int i=0;i<txtFiles.length;i++) {
				String temp = txtFiles[i].getName();
				String sourcePath = txtFiles[i].getCanonicalPath();

				int pos = temp.lastIndexOf(".txt");
				if(pos > 0 && txtFiles[i].isFile()) {
					INMEMORYDB.fileCopy(sourcePath, targetPath, temp);
					logger.debug("==== Diagnosis For Svr ====  sourcePath : " + sourcePath + ", targetPath : " + targetPath );
					ret = true;
				}
			}
		}

		return ret;
	}

	public static void makeResultBackupFiles(JobEntity3 jobEntity) {

		try {
			String targetPath = "";
			String fileNm = jobEntity.getAuditFileCd();
			String path = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType());
			String sendLog = path + fileNm + INMEMORYDB.LOG;
			String sourcePath = path + fileNm + jobEntity.getFileType();
			// 이전 파일 삭제
			if (jobEntity.getJobType3().equals("AJ100")) {
				targetPath = INMEMORYDB.jobTypeAbsolutePath("BACKUPDG", "");
				INMEMORYDB.fileCopy(sendLog, targetPath, fileNm + INMEMORYDB.LOG);
			} else if (jobEntity.getJobType3().equals("AJ200")) {
				targetPath = INMEMORYDB.jobTypeAbsolutePath("BACKUPGET", "");
			} else {
				targetPath = INMEMORYDB.jobTypeAbsolutePath("BACKUP", "");
			}

			if (INMEMORYDB.DiagSvr) {
				logger.debug("BackUp result File : " + targetPath + "-------- .txt Files");
			} else {
				INMEMORYDB.fileCopy(sourcePath, targetPath, fileNm + jobEntity.getFileType());
				logger.debug("BackUp result File : " + targetPath + fileNm + jobEntity.getFileType());
			}

			/**Make Zip File**/
			logger.info("program backup delete..AJ100,AJ200...");

			CommonUtils.makeZipFile2(targetPath, jobEntity);
			new AESCryptography().encryptionBackupFile(targetPath, jobEntity);
			targetPath = INMEMORYDB.jobTypeAbsolutePath("BACKUPDG", "");
			INMEMORYDB.deleteDirFile(targetPath, INMEMORYDB.ENC);

			targetPath = INMEMORYDB.jobTypeAbsolutePath("BACKUPGET", "");
			INMEMORYDB.deleteDirFile(targetPath, INMEMORYDB.ENC);

			targetPath = INMEMORYDB.jobTypeAbsolutePath("BACKUP", "");
			INMEMORYDB.deleteFile(targetPath, jobEntity.getJobType3() + ".iv");
			INMEMORYDB.deleteFile(targetPath, jobEntity.getJobType3() + ".salt");
			INMEMORYDB.deleteFile(targetPath, jobEntity.getJobType3() + ".zip");

			logger.info("BackUp result AES make :" + targetPath + fileNm + ".des");
		} catch (Exception ex) {
			logger.error("failed to make backup files..");
			logger.error(ex.getMessage());
		}
	}

	/**진단스크립트 파일 백업**/
	/**백업 중 실패되도 무시**/
	public static void makeDiagBackupFiles(JobEntity3 jobEntity, String recvPath, String resp) {

		String sourcePath = recvPath + resp;
		String targetPath = "";
		if(jobEntity.getJobType3().equals("AJ100")) {
			targetPath = INMEMORYDB.jobTypeAbsolutePath("BACKUPDG", "");
			INMEMORYDB.deleteDirFile(targetPath, "");
			INMEMORYDB.fileCopy(sourcePath,targetPath,"AJ100_SCRT.des");

		} else if(jobEntity.getJobType3().equals("AJ200")) {
			targetPath = INMEMORYDB.jobTypeAbsolutePath("BACKUPGET", "");
			INMEMORYDB.deleteDirFile(targetPath, "");
			INMEMORYDB.fileCopy(sourcePath,targetPath,"AJ200_SCRT.des");
		} else {
			targetPath = INMEMORYDB.jobTypeAbsolutePath("BACKUP", "");

			INMEMORYDB.deleteDirFile(targetPath, "");
			INMEMORYDB.deleteFile(targetPath,jobEntity.getJobType3()+".zip");
		}

		logger.info("Script Download File Backup source File : " + recvPath+"," +targetPath);

		targetPath = INMEMORYDB.jobTypeAbsolutePath("BACKUP", "");
		INMEMORYDB.deleteFile(targetPath,jobEntity.getJobType3()+".zip");
	}

	public static void unZipFile(JobEntity3 jobEntity) throws Throwable {

		String recvPath = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.RECV, jobEntity.getJobType());

		String recvZipFile = recvPath + jobEntity.getAuditFileCd() + INMEMORYDB.ZIP;
		new UnzipUtil(recvZipFile, recvPath).unzip();
	}

	public static boolean fileCopy(String inFileName, String outFileName) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(inFileName);
			fos = new FileOutputStream(outFileName);

			int data = 0;
			while ((data = fis.read()) != -1) {
				fos.write(data);
			}

			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				fis.close();
				fos.close();
			} catch (IOException e) {
				logger.debug("Agent fileCopy IOException" +e);
			}

		}
	}

	/**
	 * 진결 결과 및 장비정보 수집 파일 압축
	 * 2021.07.30 - 수집/진단분리 파일 추가 (Hoyoung)
	 *
	 * @param jobEntity
	 * @throws Exception
	 */
	public static void makeZipFile(JobEntity3 jobEntity) throws Exception {
		String sendPath ="";
		String fileNm = "";
		try {
			ArrayList files = new ArrayList();
			fileNm = jobEntity.getAuditFileCd();
			//String sendPath = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType()) + jobEntity.getFileName() + jobEntity.getFileType();
			sendPath = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType()) + fileNm + jobEntity.getFileType();
			if (new File(sendPath).exists()) {
				files.add(new File(sendPath));
				logger.debug("==== makeZipFile sendPath ==== : " +sendPath );
			}

			String sendLog = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType()) + fileNm + INMEMORYDB.LOG;
			if (new File(sendLog).exists()) {
				files.add(new File(sendLog));
				logger.debug("==== makeZipFile sendLog ==== : " +sendLog );
			}

			String getFile = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType()) + fileNm + INMEMORYDB.DAT;
			if (new File(getFile).exists()) {
				files.add(new File(getFile));
				logger.debug("==== makeZipFile getFile ==== : " +getFile );
			}

			// 진단분리 수집 파일 추가
			String getDeviceDiagsInfo =  INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType());
			File path = new File(getDeviceDiagsInfo);
			String getfname = "";
			String ext = "";
			File[] fileList = path.listFiles();
			logger.debug("==== makeZipFile diagsInfo path ==== : " + path );
			if(fileList.length > 0) {
				for(int i=0;i<fileList.length;i++) {
					getfname = fileList[i].getCanonicalPath();
					if(getfname.toLowerCase().contains(INMEMORYDB.TXT)) {
						files.add(new File(getfname));
						logger.debug("==== makeZipFile diagsInfo ==== : " +getfname );
					}
				}
			}

			new ZipUtil().makeZip(files, jobEntity);
			jobEntity.setFileType(INMEMORYDB.ZIP);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {

		}
	}

	public static void makeZipFile2(String path, JobEntity3 jobEntity) throws Exception {

		try {

			ArrayList files = new ArrayList();
			File backuppath = new File(path);

			File[] fileList = backuppath.listFiles();
			if(fileList.length > 0) {
				for(int i=0;i<fileList.length;i++) {
					String temp = fileList[i].getName();

					int pos = temp.lastIndexOf(".");
					if(pos > 0 && fileList[i].isFile()) {
						if (temp.indexOf(INMEMORYDB.ENC) > -1 )
							continue;

						String b_targetFile = path+temp;
						files.add(new File(b_targetFile));
					}
				}
			}
			new ZipUtil().makeZip2(files, path, jobEntity.getJobType3(), jobEntity.getJobType3());
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {

		}
	}


	public static String printError(Exception e) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(os));

		String error = new String(os.toByteArray());

		try {
			if(os != null){
				os.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return error;
	}

	public static String toCS(String o) {
		String r = o;
		if(o.equals("q"))r="!";
		if(o.equals("w"))r="@";
		if(o.equals("e"))r="#";
		if(o.equals("r"))r="$";
		if(o.equals("t"))r="%";
		if(o.equals("y"))r="^";
		if(o.equals("u"))r="&";
		if(o.equals("i"))r="i";
		if(o.equals("o"))r="(";
		if(o.equals("p"))r=")";
		if(o.equals("u"))r="&";		
		if(o.equals("!"))r="q";
		if(o.equals("@"))r="w";
		if(o.equals("#"))r="e";
		if(o.equals("$"))r="r";
		if(o.equals("%"))r="t";
		if(o.equals("^"))r="y";
		if(o.equals("&"))r="u";
		if(o.equals("*"))r="*";
		if(o.equals("("))r="o";
		if(o.equals(")"))r="p";
		if(o.equals("&"))r="u";
		if(o.equals("a"))r="z";		
		if(o.equals("s"))r="x";		
		if(o.equals("d"))r="c";
	    if(o.equals("f"))r="v";
	    if(o.equals("g"))r="b";
	    if(o.equals("h"))r="n";
	    if(o.equals("j"))r="m";    
	    if(o.equals("z"))r="a";
	    if(o.equals("x"))r="s";
	    if(o.equals("c"))r="d";
	    if(o.equals("v"))r="f";
	    if(o.equals("b"))r="g";
	    if(o.equals("n"))r="h";
	    if(o.equals("m"))r="j"; 			    
	    if(o.equals(" "))r="`";
	    if(o.equals("`"))r=" ";
	    if(o.equals("k"))r="=";
	    if(o.equals("="))r="k";
	    if(o.equals("."))r="+";
	    if(o.equals("+"))r=".";
		return r;
	}

	public static String toMux(String o) {
		int total = o.length();
		String r = "";
		String[] temparr = new String[total];
		for (int i = 0; i < total; i++) {
			temparr[i] = String.valueOf(o.charAt(i));
			r = toCS(temparr[i]) + r;
		}
		return r;
	}

	public static void logDelete() {

		File path_AP_alarm = null;

		if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID)>-1){
			path_AP_alarm = new File(INMEMORYDB.AGENT_SYS_LOG_ROOT_DIR_WIN);
		}else {
			path_AP_alarm = new File(INMEMORYDB.LOG_FILE_AGENT_ROOT_DIR);
		}


		File[] list_AP_alarm = path_AP_alarm.listFiles();
		Calendar fileCal = Calendar.getInstance();
		long todayMil = fileCal.getTimeInMillis();     // 현재시간(밀리세컨드)
		long oneDayMil = 24 * 60 * 60 * 1000L;

		if (list_AP_alarm != null){
			for (int j = 0; j < list_AP_alarm.length; j++) {

				// 파일의 마지막 수정시간 가져오기
				Date fileDate_APalarm = new Date(list_AP_alarm[j].lastModified());

				// 현재시간과 파일 수정시간 시간차 계산(단위 : 밀리 세컨드)
				fileCal.setTime(fileDate_APalarm);
				long diffMil = todayMil - fileCal.getTimeInMillis();

				//날짜로 계산
				int diffDay = (int) (diffMil / oneDayMil);
				// 7일이 지난 파일 삭제
				if (diffDay >= 7 && list_AP_alarm[j].exists()) {
					list_AP_alarm[j].delete();
					System.out.println(list_AP_alarm[j].getName());
				}
			}
		}
	}


	public static String replace(String sStrString, String sStrOld, String sStrNew) {
		if (sStrString == null)return null;
		for (int iIndex = 0 ; (iIndex = sStrString.indexOf(sStrOld, iIndex)) >= 0 ; iIndex += sStrNew.length())
			sStrString = sStrString.substring(0, iIndex) + sStrNew + sStrString.substring(iIndex + sStrOld.length());

		return sStrString;
	}

	public static void main(String args[]) throws IOException, InterruptedException {
		INMEMORYDB memory = new INMEMORYDB();
		memory.reloadMemoryDB();
		memory.init();
		logDelete();
	}
	
	public static void close(Socket s){
		if(s!=null){
			try {
				s.close();
			} catch (Throwable e) {
				logger.error("[close error]", e);
			}
		}
	}
	
	public static void close(InputStream is){
		if(is!=null){
			try {
				is.close();
			} catch (Throwable e) {
				logger.error("[close error]", e);
			}
		}
	}
	
	public static void close(OutputStream os){
		if(os!=null){
			try {
				os.close();
			} catch (Throwable e) {
				logger.error("[close error]", e);
			}
		}
	}
	
	public static void close(Reader r){
		if(r!=null){
			try {
				r.close();
			} catch (Throwable e) {
				logger.error("[close error]", e);
			}
		}
	}
	
	public static void close(Writer w){
		if(w!=null){
			try {
				w.close();
			} catch (Throwable e) {
				logger.error("[close error]", e);
			}
		}
	}
	
	
	public static void sleep(int seconds){
		try {
			Thread.sleep(seconds * 1000L);
		} catch (Throwable e) {
			logger.error("sleep", e);
		}
	}

	public static String getCurrentAccount() {
		String cur = "";
		try {
			cur = System.getProperty("user.name");
			logger.info("[getCurrentAccount] - current user : " + cur);

			if (cur == null || cur.length() == 0) {
				String line;
				Process p = Runtime.getRuntime().exec("whoami");
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = br.readLine()) != null) {
					cur = line;
				}
				p.waitFor();
				br.close();
				logger.info("[getCurrentAccount] - current user - recheck: " + cur);
			}
		} catch (Throwable e) {
			logger.error("getCurrentAccount", e);
		}

		return cur;
	}

	public static String getRunProcess(String[] cmd) {
		String result = "";
		try {
			String line;
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				result = line;
			}
			p.waitFor();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Run Process Exception :: {}", e);
		}
		return result;
	}
}
