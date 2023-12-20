/**
 * project : AgentManager
 * program name : com.mobigen.snet.agentmanager.memory.INMEMORYDB.java
 * company : Mobigen
 * @author : Je Joong Lee
 * created at : 2016. 2. 3.
 * description :
 */

package com.mobigen.snet.NeAgent.memory;

import com.mobigen.snet.NeAgent.crypto.AESCryptography;
import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.service.Vmanager;
import com.mobigen.snet.NeAgent.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;


public class INMEMORYDB {
    private static Logger logger = LoggerFactory.getLogger(INMEMORYDB.class);
    Vmanager vmanager = new Vmanager();

    public static boolean doOfflineDiagUse = false;
    public static boolean DiagSvr = false;

    public static String[] LISTENER_IP = null;
    public static String LISTENER_PORT = "10225";
    public static String RELAY_NOTIPORT = "10225";
    //public static String AgentManager_IP = "";
    public static String USE_NAT = "";
    // SG3.0 인증 등록 코드
    public static String AGENTCD ="";
    public static String ASSETCD ="";
    // SG3.0 인증 파일 다중 처리
    public static ArrayList ASSETCD_LISTS = new ArrayList();
    public static String AGENT_VER="";
    public static String AGENT_IPADDR = "";
    public static String AGENT_HOSTNAME = "";
    public static String HTTP_AUTHVALUE = "44d3ce3c1f9c547a041d3c6315bc4bbab3d2b63e67b80596f1ad9088b77bd7d655e993e03c6206585ebb32a4fb802669";
    public static String COMPANKEY="";

    /**
     * SG 3.0 Manager REST API - URI
     */
    // Manager REST URI
    public static String REST_SERVER="";
    // 전체 리스트 정보
    public static String REST_SERVERS="";
    public static String REST_PORT="";
    public static String REST_PROTOCOL="";
    public static String REST_VER="";
    public static String REST_CLIENT="";
    public static String MANAGER_IP="";

    // Manager REST API
    public static String AUTH_URI="";
    public static String JOB_URI="";
    public static String NOTI_URI="";
    public static String FILE_DN_URI="";
    public static String FILE_UP_URI="";
    public static String ASSET_CODE_PATH="";

    // Agent Patch path
    public static String AGENT_PATCH_PATH="";

    public static HashMap CONCURRENTHASHMAP;
    public static String EXEC_WAIT_TIME = "20"; //분 단위
    public static String CONNECTTIMEOUT = "15000";   // 초 단위
    public static String READTIMEOUT = "30000";

    /**
     * OS TYPE
     **/
    public static String osType = System.getProperty("os.name");

    /**
     * Common Code
     **/
    public static String RECV = "IN";
    public static String SEND = "OUT";

    /**
     * 파일 확장자
     **/
    public static String OTPTYPE = ".ooo";
    public static String GSRESULTTYPE = ".dat";
    public static String DGRESULTTYPE = ".xml";
    public static String JAR = ".jar";
    public static String ZIP = ".zip";
    public static String SH = ".class";
    public static String BAT = ".cmd";
    public static String DAT = ".dat";
    public static String XML = ".xml";
    public static String TXT = ".txt";
    public static String PASSWORDTYPE = ".dat2";
    public static String ENC = ".des";
    public static String LOG = ".log";
    public static String CONF = ".conf";

    /**
     * JobType
     **/
    public static String OTP = "OTP";
    public static String AGENTUPDATEREQ = "AGENTUPDATEREQ";
    public static String GSCRPTFILE = "GSCRPTFILE";
    public static String DGFILE = "DGFILE";
    public static String GSCRPTFIN = "GSCRPTFIN";//get script 결과 전송
    public static String DGFIN = "DGFIN";
    public static String KILLAGENT = "KILLAGENT";
    public static String RESTARTAGENT = "RESTARTAGENT";
    public static String AGENTLOG = "AGENTLOG";
    public static String AGENTLOGFIN = "AGENTLOGFIN";
    public static String ERRORFIN = "ERRORFIN";
    public static String IPUPD = "IPUPD";
    public static String BACKUP = "BACKUP";
    public static String BACKUPDG = "BACKUPDG";
    public static String BACKUPGET = "BACKUPGET";

    /**
     * 진단 타입
     **/
    public static String DB = "DB";

    /***
     * System Type Variables
     ***/
    public static String PATH_SLASH_WIN = "\\";
    public static String PATH_SLASH_UNIX = "/";

    /**
     * OS TYPE
     **/
    public static String WIN_ID = "WIN";

    /***
     * Agent SET-UP
     ***/
    public static String AGENT_SYS_ROOT_DIR = "[SLASH]usr[SLASH]local[SLASH]snet[SLASH]";
    //public static String AGENT_SYS_ROOT_DIR = "[SLASH]sysapp[SLASH]sguardagent[SLASH]";
    public static String AGENT_SYS_ROOT_DIR_WIN = "C:[SLASH]snet[SLASH]";
    public static String AGENT_SYS_LOG_ROOT_DIR_WIN = "C:[SLASH]usr[SLASH]local[SLASH]snet[SLASH]logs[SLASH]";
    public static String AGENT_SYS_LOG_ROOT_DIR_WIN2 = "C:[SLASH]snet[SLASH]logs[SLASH]";


    /**
     * GetUnixMain Setup
     */
    public static String OFFLINE_AGENT_LIBS_DIR_GUNIX = "/usr/local/snetOffline/agent/libs/";
    public static String OFFLINE_DIAGPATH_GUNIX = "/usr/local/snetOffline/agent/libs/diags/";

    /***
     * Offline Agent SET-UP
     ***/

    public static String OFFLINE_AGENT_SYS_ROOT_DIR = "[SLASH]usr[SLASH]local[SLASH]snetOffline[SLASH]";
    public static String OFFLINE_DIAGPATH_DINFONOTUSE = "[AGENT_SYS_ROOT_DIR]agent[SLASH]libs[SLASH]dinfonotuse[SLASH]";

    /***
     * Agent내 실행 결과 파일들의 경로
     ***/
    public static String RESULT_FILE_AGENT_DIR = "[AGENT_SYS_ROOT_DIR]txFiles[SLASH]outbound[SLASH]";
    public static String DIAG_RESULT_FILE_AGENT_DIR = "[AGENT_SYS_ROOT_DIR]txFiles[SLASH]outbound[SLASH]diag[SLASH]";
    public static String GETSCRIPT_RESULT_FILE_AGENT_DIR = "[AGENT_SYS_ROOT_DIR]txFiles[SLASH]outbound[SLASH]get[SLASH]";
    /***
     * Agent내 보낼OTP 파일 경로
     ***/
    public static String OTP_SEND_FILE_AGENT_ROOT_DIR = "[AGENT_SYS_ROOT_DIR]txFiles[SLASH]outbound[SLASH]otp[SLASH]";
    /***
     * Agent내 받은  파일들의 경로
     * 스크립트,진단결과 파일 백업경로 추가 - 2021.1.14 - Hoyoung
     ***/
    public static String DIAG_RECV_FILE_AGENT_ROOT_DIR = "[AGENT_SYS_ROOT_DIR]txFiles[SLASH]inbound[SLASH]diag[SLASH]";
    public static String GETSCRIPT_RECV_FILE_AGENT_ROOT_DIR = "[AGENT_SYS_ROOT_DIR]txFiles[SLASH]inbound[SLASH]get[SLASH]";
    public static String OTP_RECV_FILE_AGENT_ROOT_DIR = "[AGENT_SYS_ROOT_DIR]txFiles[SLASH]inbound[SLASH]otp[SLASH]";
    public static String VERSION_RECV_FILE_AGENT_ROOT_DIR = "[AGENT_SYS_ROOT_DIR]txFiles[SLASH]inbound[SLASH]version[SLASH]";
    public static String LOG_FILE_AGENT_ROOT_DIR = "[AGENT_SYS_ROOT_DIR]logs[SLASH]";
    public static String BACKUP_FILE_AGENT_ROOT_DIR = "[AGENT_SYS_ROOT_DIR]txFiles[SLASH]backup[SLASH]";
    public static String BACKUP_DG_FILE_AGENT_ROOT_DIR = "[AGENT_SYS_ROOT_DIR]txFiles[SLASH]backup[SLASH]diag[SLASH]";
    public static String BACKUP_GET_FILE_AGENT_ROOT_DIR = "[AGENT_SYS_ROOT_DIR]txFiles[SLASH]backup[SLASH]get[SLASH]";

    /***
     * Agent내 보낼OTP 파일 경로
     ***/
    public static String AGENT_ROOT_DIR = "[AGENT_SYS_ROOT_DIR]agent[SLASH]";
    public static String AGENT_LIBS_DIR = "[AGENT_SYS_ROOT_DIR]agent[SLASH]libs[SLASH]";
    public static String AGENT_BIN_DIR = "[AGENT_SYS_ROOT_DIR]agent[SLASH]bin[SLASH]";
    public static String AGENT_JRE_DIR = "[AGENT_SYS_ROOT_DIR]agent[SLASH]jre[SLASH]bin[SLASH]";

    public static String KEYSTOREPATH = "[AGENT_SYS_ROOT_DIR]agent[SLASH]libs[SLASH]secure[SLASH]snetsecure";
    public static String VERSIONPATH = "[AGENT_SYS_ROOT_DIR]agent[SLASH]libs[SLASH]version.info";
    public static String DIAGPATH = "[AGENT_SYS_ROOT_DIR]agent[SLASH]libs[SLASH]diags[SLASH]";

    public static String AGENT_CHK_FILE = "agent_chk.dat";
    public static String RESOURCE_CHK_FILE = "resource_chk.dat";
    public static String MONITER_DEILIMTER = "|";

    public static String UNIX_DIAGS_ZIP = "unixdiagnosis.zip";

    public static String UNIX_GET_SH = "getunixagent.class";
    public static String WIN_GET_JAR = "getwindows.jar";
    public static String DIAGINFO = "diaginfo.class";

    public static String AGENT_PROPERITES = "agent.context.properites";
    public static String NOTI_IP = "";
    public static String DELIMITER = "[^]";

    public static String CURRENTACCOUNT = "";
    public static String ROOTACCOUNT = "root";

    public static boolean isDebug = false;

    public void init() throws IOException, InterruptedException {
        CONCURRENTHASHMAP = new HashMap();
        // Agent 사용 경로 초기값 생성
        initDirs();
        //Asset Code 파일 체크
        initAuthFileCh();
        // 3.0 서버 API 값
        reloadURIDB();
        // 3.0 인증 키 값
        reloadAuthDB();
        // Agent Version 체크
        getVersionInfo();
        // 3.0 인증 Header 키 생성
        makeHttpheader();
        //에이전트 로그 파일 삭제
        deleteLogFiles();

        initManagerIP();
        CommonUtils.logDelete();
        CURRENTACCOUNT = CommonUtils.getCurrentAccount();
    }

    /**
     * 1회용 진단 초기값
     * @throws IOException
     * @throws InterruptedException
     */
    public void off_init() throws IOException, InterruptedException {
        CONCURRENTHASHMAP = new HashMap();
        doOfflineDiagUse = true;
        // Agent 사용 경로 초기값 생성
        initDirs();
        CommonUtils.logDelete();
        CURRENTACCOUNT = CommonUtils.getCurrentAccount();
    }
    /**
     * 3.0 Agent version.info 체크
     */
    private void getVersionInfo() {
        AGENT_VER = vmanager.getVersionInfo(INMEMORYDB.VERSIONPATH);
    }

    /**
     * 3.0 Http Head Authorization 생성
     */
    private void makeHttpheader() {
        try {
            String plantext = "AGENT@@vision@@"+ INMEMORYDB.AGENT_IPADDR+"@@"+ INMEMORYDB.AGENT_VER;
            AESCryptography authkey = new AESCryptography();
            HTTP_AUTHVALUE =  authkey.encrypt(plantext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 3.0 인증을 위한 AssetCd 코드 추출
     * 2020.12.22 - hoyoung
     */
    public void initAuthFileCh() {
        File path = new File(ASSET_CODE_PATH);
        String fname = "";
        File[] fileList = path.listFiles();
        if(fileList.length > 0) {
            for(int i=0;i<fileList.length;i++) {
                fname = fileList[i].getName();
                if(fname.startsWith("AC")) {
                    int pos = fname.lastIndexOf(".");
                    if(pos > 0) {
                        ASSETCD = fname.substring(0,pos);
                        ASSETCD_LISTS.add(ASSETCD.toString().trim());
                    } else {
                        ASSETCD = fname;
                        ASSETCD_LISTS.add(ASSETCD.toString().trim());
                    }
                }
            }
        }
    }
    /**
     * HostName 가져오기
     * @return
     */
    private static String getHostName() {
        String hostname = "";
        String lineStr = "";
        try {
            Process process = Runtime.getRuntime().exec("hostname");
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while((lineStr = br.readLine()) != null) {
                hostname = lineStr;
            }
        } catch (IOException e) {
            e.printStackTrace();
            hostname = "";
        }
        return hostname;
    }
    /*
     * Created by Hoyoung on 2020. 5. 21
     * Agent 디랙토리 초기화값 가져오기
     */
    private void initDirs() {
        osType = System.getProperty("os.name");
        InetAddress local = null;
        String hostname = null;
        try {
            local = InetAddress.getLocalHost();
            hostname = local.getHostName();
        } catch (UnknownHostException e ) {
            if(hostname == null) {
                int colon = e.getMessage().indexOf(':');
                if (colon>0) {
                    hostname = e.getMessage().substring(0,colon);
                } else {
                    hostname = getHostName();
                }
            } else {
                hostname = getHostName();
            }

            logger.debug("initDirs(OS,IP,Hostname)  UnknownHostException : " + e.getMessage() + " ===>" + hostname);
        }
        logger.debug("initDirs(OS,IP,Hostname  : " + hostname);
        //AGENT_IPADDR = local.getHostAddress();
        AGENT_IPADDR =  getLocalServerIp();
        AGENT_HOSTNAME =hostname;

        AGENT_ROOT_DIR = replaceDir(AGENT_ROOT_DIR);
        AGENT_LIBS_DIR = replaceDir(AGENT_LIBS_DIR);
        AGENT_BIN_DIR = replaceDir(AGENT_BIN_DIR);

        ASSET_CODE_PATH = replaceDir(AGENT_LIBS_DIR);
        AGENT_PATCH_PATH = replaceDir(VERSION_RECV_FILE_AGENT_ROOT_DIR);
        RESULT_FILE_AGENT_DIR = replaceDir(RESULT_FILE_AGENT_DIR);
        DIAG_RESULT_FILE_AGENT_DIR = replaceDir(DIAG_RESULT_FILE_AGENT_DIR);
        GETSCRIPT_RESULT_FILE_AGENT_DIR = replaceDir(GETSCRIPT_RESULT_FILE_AGENT_DIR);
        OTP_SEND_FILE_AGENT_ROOT_DIR = replaceDir(OTP_SEND_FILE_AGENT_ROOT_DIR);
        DIAG_RECV_FILE_AGENT_ROOT_DIR = replaceDir(DIAG_RECV_FILE_AGENT_ROOT_DIR);
        GETSCRIPT_RECV_FILE_AGENT_ROOT_DIR = replaceDir(GETSCRIPT_RECV_FILE_AGENT_ROOT_DIR);
        OTP_RECV_FILE_AGENT_ROOT_DIR = replaceDir(OTP_RECV_FILE_AGENT_ROOT_DIR);
        VERSION_RECV_FILE_AGENT_ROOT_DIR = replaceDir(VERSION_RECV_FILE_AGENT_ROOT_DIR);

        KEYSTOREPATH = replaceDir(KEYSTOREPATH);
        VERSIONPATH = replaceDir(VERSIONPATH);
        DIAGPATH = replaceDir(DIAGPATH);
        AGENT_JRE_DIR = replaceDir(AGENT_JRE_DIR);
        LOG_FILE_AGENT_ROOT_DIR = replaceDir(LOG_FILE_AGENT_ROOT_DIR);
        AGENT_SYS_LOG_ROOT_DIR_WIN = replaceDir(AGENT_SYS_LOG_ROOT_DIR_WIN);
        AGENT_SYS_LOG_ROOT_DIR_WIN2 = replaceDir(AGENT_SYS_LOG_ROOT_DIR_WIN2);
        BACKUP_FILE_AGENT_ROOT_DIR = replaceDir(BACKUP_FILE_AGENT_ROOT_DIR);
        BACKUP_DG_FILE_AGENT_ROOT_DIR = replaceDir(BACKUP_DG_FILE_AGENT_ROOT_DIR);
        BACKUP_GET_FILE_AGENT_ROOT_DIR = replaceDir(BACKUP_GET_FILE_AGENT_ROOT_DIR);

        OFFLINE_DIAGPATH_DINFONOTUSE = replaceDir(OFFLINE_DIAGPATH_DINFONOTUSE);
    }

    public void initManagerIP() throws IOException, InterruptedException {
        logger.debug("Manager Server IP : " + REST_SERVER);
        LISTENER_IP = REST_SERVER.split(",");
    }


    public static Properties loadPropertie(InputStream is) {
        Properties prop = null;
        try {
            prop = new Properties();
            prop.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prop;
    }

    public static String replaceDir(String org) {
        String asysroot = "";
        String aslash = "";
        if (osType.toUpperCase().indexOf(WIN_ID) > -1) {
            asysroot = AGENT_SYS_ROOT_DIR_WIN;
            aslash = PATH_SLASH_WIN;
        } else {
            asysroot = AGENT_SYS_ROOT_DIR;
            if (doOfflineDiagUse) {
                asysroot = OFFLINE_AGENT_SYS_ROOT_DIR;
            }
            aslash = PATH_SLASH_UNIX;
        }

        AGENT_SYS_ROOT_DIR = asysroot;

        String result = cReplaceAll(org, "[AGENT_SYS_ROOT_DIR]", asysroot);

        result = cReplaceAll(result, "[SLASH]", aslash);

        return result;
    }

    public static String cReplaceAll(String target, String from, String to) {
        int start = target.indexOf(from);
        if (start == -1) return target;
        int lf = from.length();
        char[] targetChars = target.toCharArray();
        StringBuffer buffer = new StringBuffer();
        int copyFrom = 0;
        while (start != -1) {
            buffer.append(targetChars, copyFrom, start - copyFrom);
            buffer.append(to);
            copyFrom = start + lf;
            start = target.indexOf(from, copyFrom);
        }
        buffer.append(targetChars, copyFrom, targetChars.length - copyFrom);
        return buffer.toString();
    }

    public static String jobTypeAbsolutePath(String pathType, String jobType) {
        if (pathType.equalsIgnoreCase(RECV)) {
            if (jobType.equalsIgnoreCase(OTP)) {
                return OTP_RECV_FILE_AGENT_ROOT_DIR;
            } else if (jobType.equalsIgnoreCase(GSCRPTFILE)) {
                return GETSCRIPT_RECV_FILE_AGENT_ROOT_DIR;
            } else if (jobType.equalsIgnoreCase(AGENTUPDATEREQ)) {
                return VERSION_RECV_FILE_AGENT_ROOT_DIR;
            } else if (jobType.equalsIgnoreCase(DGFILE)) {
                return DIAG_RECV_FILE_AGENT_ROOT_DIR;
            }  else {
                return AGENT_SYS_ROOT_DIR;
            }

        } else if (pathType.equalsIgnoreCase(SEND)) {
            if (jobType.equalsIgnoreCase(OTP)) {
                return OTP_SEND_FILE_AGENT_ROOT_DIR;
            } else if (jobType.equalsIgnoreCase(GSCRPTFIN)) {
                return GETSCRIPT_RESULT_FILE_AGENT_DIR;
            } else if (jobType.equalsIgnoreCase(DGFIN) || jobType.equalsIgnoreCase(AGENTLOGFIN)) {
                return DIAG_RESULT_FILE_AGENT_DIR;
            } else {
                return AGENT_SYS_ROOT_DIR;
            }
            // 백업 파일 패스 추가
        } else if (pathType.equalsIgnoreCase(BACKUP)) {
            return BACKUP_FILE_AGENT_ROOT_DIR;
        } else if (pathType.equalsIgnoreCase(BACKUPDG)) {
            return BACKUP_DG_FILE_AGENT_ROOT_DIR;
        } else if (pathType.equalsIgnoreCase(BACKUPGET)) {
            return BACKUP_GET_FILE_AGENT_ROOT_DIR;
        } else {
            return AGENT_SYS_ROOT_DIR;
        }
    }



    /**
     * 지정 파일 삭제 하기
     * HOYOUNG
     * 2020.12.23
     * @param fileName
     */
    public static void deleteFile(String path, String fileName) {
        String targetFile = path+fileName;
        File file = new File(targetFile);
        if( file.exists() ){
            if(file.delete()){
                logger.debug("[debug] deleteFile ok.." + fileName);
            }else{
                logger.debug("[debug] deleteFile fail.." + fileName);
            }
        } else {
            logger.debug("[debug] deleteFile not found : " + fileName);
        }
    }

    /**
     * 이전 백업 파일 삭제
     * @param path
     */
    public static void deleteDirFile(String path, String exceptFile) {
        File file = new File(path);
        File[] files = file.listFiles();
        int totalLen = files.length;
        try {
            for (int i = 0; i < totalLen; i++) {
                if (files[i].isDirectory()) {
                    File[] files1 = files[i].listFiles();
                    int totalLen1 = files1.length;
                    for (int j = 0; j < totalLen1; j++) {
                        if (exceptFile != null && !exceptFile.isEmpty()) {
                            if (files1[j].getName().contains(exceptFile)) {
                                continue;
                            }
                        }
                        files1[j].delete();
                    }
                }

                if (exceptFile != null && !exceptFile.isEmpty()) {
                    if (files[i].getName().contains(exceptFile)) {
                        continue;
                    }
                }
                files[i].delete();
            }
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }

    public static void deleteFile(JobEntity3 jobEntity, String pathType) {
        String path = INMEMORYDB.jobTypeAbsolutePath(pathType, jobEntity.getJobType());
        File file = new File(path);
        File[] files = file.listFiles();
        int totalLen = files.length;
        try {
            for (int i = 0; i < totalLen; i++) {
                if (files[i].isDirectory()) {
                    File[] files1 = files[i].listFiles();
                    int totalLen1 = files1.length;
                    for (int j = 0; j < totalLen1; j++) {
                        files1[j].delete();
                    }
                }
                files[i].delete();
            }
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }

    public static void deleteOffLineFile(String path) {

        File file = new File(path);

        File[] files = file.listFiles();
        int totalLen = files.length;

        try {
            for (int i = 0; i < totalLen; i++) {
                if (files[i].getName().indexOf("cfdkeyencrypted") > -1) {
                } else if (files[i].isDirectory()) {
                    deleteOffLineFile(files[i].getAbsolutePath());
                } else {
                    files[i].delete();
//					System.out.println("@@@ del : "+files[i].getName());
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    public static void deleteFilesInFolder(String path) {

        File folder = new File(path);

        if(folder.exists()) {
            File[] folderList = folder.listFiles();

            try {
                for (int i = 0; i < folderList.length; i++) {
                    if(folderList[i].isFile()) {
                        folderList[i].delete();
                    }
                    folderList[i].delete();
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    public static void deleteManualFile(String path) {
        File file = new File(path);

        if (file.listFiles() != null) {
            for (int i = 0; i < file.listFiles().length; i++) {
                if (file.listFiles()[i].getAbsolutePath().indexOf("jre") > -1 || file.listFiles()[i].getAbsolutePath().indexOf("bin") > -1 || file.listFiles()[i].getAbsolutePath().indexOf("secure") > -1 || file.listFiles()[i].getAbsolutePath().indexOf("cfdkeyencrypted") > -1) {

                } else if (file.listFiles()[i].isDirectory()) {
                    deleteManualFile(file.listFiles()[i].getAbsolutePath());
                } else {
                    if (!(file.listFiles()[i].getName().indexOf("NeAgent") > -1) || !(file.listFiles()[i].getName().indexOf("jodd") > -1) || !(file.listFiles()[i].getName().indexOf("log4j") > -1)) {
                    } else file.listFiles()[i].delete();
                }
            }
        }
    }


    /**
     *
     * @param jobEntity
     * @param pathType
     * @param resultType
     */

    public static void moveFileName(JobEntity3 jobEntity, String pathType, String resultType) {
        String path = INMEMORYDB.jobTypeAbsolutePath(pathType, jobEntity.getJobType());
        File file = new File(path);
        if (jobEntity.getJobType().equalsIgnoreCase(INMEMORYDB.GSCRPTFIN)) {
            for (int i = 0; i < file.listFiles().length; i++) {
                if (file.listFiles()[i].getName().indexOf(jobEntity.getAssetCd()) > -1) {
                    File orgiFile = new File(path + file.listFiles()[i].getName());
                    //File fileToMove = new File(path + jobEntity.getFileName() + jobEntity.getFileType());
                    File fileToMove = new File(path + jobEntity.getAuditFileCd() + jobEntity.getFileType());
                    logger.info("GSCRPTFIN File Move : "+path + file.listFiles()[i].getName()+"-->"+path + jobEntity.getAuditFileCd() + jobEntity.getFileType());
                    orgiFile.renameTo(fileToMove);
                }
            }
        } else {
            for (int i = 0; i < file.listFiles().length; i++) {
                if (file.listFiles()[i].getName().indexOf(jobEntity.getFileType()) > -1) {
                    File orgiFile = new File(path + file.listFiles()[i].getName());
                    //File fileToMove = new File(path + jobEntity.getFileName() + jobEntity.getFileType());
                    File fileToMove = new File(path + jobEntity.getAuditFileCd() + jobEntity.getFileType());
                    logger.info("Diagnosis File Move : "+path + file.listFiles()[i].getName()+"-->"+path + jobEntity.getAuditFileCd() + jobEntity.getFileType());
                    orgiFile.renameTo(fileToMove);
                }
            }
        }
    }

    /**
     * 	 파일 백업 처리
     * 	 - 스크립트,진단결과,장비수집결과 파일 백업(최근 파일)
     * 	 - 2021.1.14 - Hoyoung
     * @param inFileFullName
     * @param outFilePath
     * @param fileName
     * @return
     */
    public static boolean fileCopy(String inFileFullName, String outFilePath, String fileName) {

        String SLASH;
        if (osType.toUpperCase().indexOf(WIN_ID) > -1) {
            SLASH = PATH_SLASH_WIN;
        } else {
            SLASH = PATH_SLASH_UNIX;
        }

        String outFileFullPath = outFilePath + SLASH + fileName;

        try {
            FileInputStream fis = new FileInputStream(inFileFullName);
            FileOutputStream fos = new FileOutputStream(outFileFullPath);

            int data = 0;
            while ((data = fis.read()) != -1) {
                fos.write(data);
            }
            fis.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 진단 파일명 select
    public static String selectDignosisFile(String dgType, String gov_flag, String sw_nm, String sw_info) {
        logger.debug("[DEBUG] selectDignosisFile : " + dgType + "," + gov_flag +","+ sw_nm +","+sw_info);
        // Windows / Unix 구분
        if (osType.toUpperCase().indexOf(WIN_ID) > -1) {
            logger.debug("[DEBUG] Windows ..");
            // Windows SW 대상인 경우
            if (dgType.equalsIgnoreCase("OS")) {
                // 진단 기준에 따른 구분 _ COMMON / MSIT / MSIT_SKT
                if (gov_flag.equalsIgnoreCase("MSIT")) {
                    // MSIT 일때
                    // Server군 대상 1개 뿐임.
                    return "igloo_windows_server";

                } else if (gov_flag.equalsIgnoreCase("MSIT_SKT")){
                    // 기반시설_SKT 진단일때
                    // OS 는 대상 없음.
                    return sw_nm + " Not Supported.";
                } else {
                    if (sw_info.equalsIgnoreCase("7"))
                        return "skt_win7";
                    else if (sw_info.equals("10"))
                        return "skt_win10";
                    else if (sw_info.equals("2003"))
                        return "skt_win2003";
                    else if (sw_info.equals("2008"))
                        return "skt_win2008";
                    else if (sw_info.equals("2012"))
                        return "skt_win2012";
                    else if (sw_info.equals("2016"))
                        return "skt_win2016";
                    else if (sw_info.equals("2019"))
                        return "skt_win2019";
                    else
                        return sw_nm + sw_info + " Not Supported.";
                }
            } else if (dgType.equalsIgnoreCase("DB")) {
                // 진단 기준에 따른 구분
                if (gov_flag.equalsIgnoreCase("MSIT")) {
                    //기반시설 일 경우
                    if (sw_nm.equalsIgnoreCase("DB2")) {
                        return "igloo_db2";
                    } else if (sw_nm.equalsIgnoreCase("MariaDB")) {
                        return "igloo_mariadb";
                    } else if (sw_nm.equalsIgnoreCase("MSSQL")) {
                        return "igloo_mssql";
                    } else if (sw_nm.equalsIgnoreCase("MySQL")) {
                        return "igloo_mysql";
                    } else if (sw_nm.equalsIgnoreCase("ORACLE")) {
                        return "igloo_oracle";
                    } else if (sw_nm.equalsIgnoreCase("Tibero4_SP1")) {
                        return "igloo_tibero_4_sp1";
                    } else if (sw_nm.equalsIgnoreCase("Tibero5")) {
                        return "igloo_tibero_5";
                    } else if (sw_nm.equalsIgnoreCase("Tibero5_SP1")) {
                        return "igloo_tibero_5_sp1";
                    } else if (sw_nm.equalsIgnoreCase("Tibero6")) {
                        return "igloo_tibero_6";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else if (gov_flag.equalsIgnoreCase("MSIT_SKT")){
                    // 기반시설_SKT 진단일때
                    // DB 는 대상 없음.
                    return sw_nm + " Not Supported.";
                } else {
                    // SKT(Common기준)인 경우
                    if (sw_nm.equalsIgnoreCase("MSSQL")) {

                        if (sw_info.equalsIgnoreCase("2000"))
                            return "skt_mssql_2000";
                        else if (sw_info.equals("2005"))
                            return "skt_mssql_2005";
                        else if (sw_info.equals("2008"))
                            return "skt_mssql_2008";
                        else if (sw_info.equals("2012"))
                            return "skt_mssql_2012";
                        else if (sw_info.equals("2014"))
                            return "skt_mssql_2012";
                        else if (sw_info.equals("2016"))
                            return "skt_mssql_2012";
                        else if (sw_info.equals("2017"))
                            return "skt_mssql_2012";
                        else if (sw_info.equals("2019"))
                            return "skt_mssql_2012";
                        else
                            return sw_nm + sw_info + " Not Supported.";

                    } else if (sw_nm.equalsIgnoreCase("ORACLE")) {
                        return "skt_oracle";
                    } else if (sw_nm.equalsIgnoreCase("MySQL")) {
                        return "skt_mysql";
                    } else if (sw_nm.equalsIgnoreCase("MariaDB")) {
                        return "skt_mariadb";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                }
            } else if (dgType.equalsIgnoreCase("WAS")) {
                // 진단 기준에 따른 구분
                if (gov_flag.equalsIgnoreCase("MSIT")) {
                    //기반시설 일 경우
                    if (sw_nm.equalsIgnoreCase("JBoss")){
                        return "igloo_jboss";
                    } else if (sw_nm.equalsIgnoreCase("Jeus")){
                        return "igloo_jeus";
                    } else if (sw_nm.equalsIgnoreCase("Jeus7")){
                        return "igloo_jeus7";
                    } else if (sw_nm.equalsIgnoreCase("Tomcat")){
                        return "igloo_tomcat";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else if (gov_flag.equalsIgnoreCase("MSIT_SKT")){
                    // 기반시설_SKT 진단일때
                    // DB 는 대상 없음.
                    return sw_nm + " Not Supported.";
                } else {
                    // SKT(Common기준)인 경우
                    if (sw_nm.equalsIgnoreCase("Jeus")){
                        return "skt_jeus";
                    } else if (sw_nm.equalsIgnoreCase("Jeus7")){
                        return "skt_jeus7";
                    } else if (sw_nm.equalsIgnoreCase("Tomcat")){
                        return "skt_tomcat";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                }
            } else if (dgType.equalsIgnoreCase("WEB")) {
                // 진단 기준에 따른 구분
                if (gov_flag.equalsIgnoreCase("MSIT")) {
                    //기반시설 일 경우
                    if (sw_nm.equalsIgnoreCase("Apache")) {
                        return "igloo_apache";
                    } else if (sw_nm.equalsIgnoreCase("IIS")) {
                        return "igloo_iis";
                    } else if (sw_nm.equalsIgnoreCase("iPlanet")) {
                        return "igloo_iplanet";
                    } else if (sw_nm.equalsIgnoreCase("WebtoB")) {
                        return "igloo_webtob";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else if (gov_flag.equalsIgnoreCase("MSIT_SKT")){
                    // 기반시설_SKT 진단일때
                    // DB 는 대상 없음.
                    if (sw_nm.equalsIgnoreCase("IIS")) {
                        return "skinfra_iis";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else {
                    // SKT(Common기준)인 경우
                    if (sw_nm.equalsIgnoreCase("Apache")) {
                        return "skt_apache";
                    } else if (sw_nm.equalsIgnoreCase("IIS")) {
                        if (sw_info.equals("5"))
                            return "skt_iis5";
                        else if (sw_info.equals("6"))
                            return "skt_iis6";
                        else if (sw_info.equals("7"))
                            return "skt_iis7";
                        else if (sw_info.equals("10"))
                            return "skt_iis10";
                        else
                            return sw_nm + sw_info + " Not Supported.";
                    } else if (sw_nm.equalsIgnoreCase("WebtoB")) {
                        return "skt_webtob";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                }
            } else
                return sw_nm + " Not Supported.";
        } else {
            // UNIX 계열 일 경우 대상 스크립트
            logger.debug("[DEBUG] Unix .. ");
            if (dgType.equalsIgnoreCase("OS")) {
                if (gov_flag.equalsIgnoreCase("msit")) {
                    //기반시설 일 경우
                    if (sw_nm.equalsIgnoreCase("linux")) {
                        return "igloo_linux";
                    } else if (sw_nm.equalsIgnoreCase("aix")) {
                        return "igloo_aix";
                    } else if (sw_nm.equalsIgnoreCase("hp-ux")) {
                        return "igloo_hp-ux";
                    } else if (sw_nm.equalsIgnoreCase("solaris")) {
                        return "igloo_solaris";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else if (gov_flag.equalsIgnoreCase("msit_skt")){
                    // 기반시설_SKT 진단일때
                    if (sw_nm.equalsIgnoreCase("linux")) {
                        return "skinfra_linux";
                    } else if (sw_nm.equalsIgnoreCase("aix")) {
                        return "skinfra_aix";
                    } else if (sw_nm.equalsIgnoreCase("hp-ux")) {
                        return "skinfra_hpux";
                    } else if (sw_nm.equalsIgnoreCase("solaris")) {
                        return "skinfra_solaris";
                    } else if (sw_nm.equalsIgnoreCase("solaris10")) {
                        return "skinfra_solaris10";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else {
                    // SKT(Common기준)인 경우
                    if (sw_nm.equalsIgnoreCase("linux")) {
                        return "skt_linux";
                    } else if (sw_nm.equalsIgnoreCase("alpine")) {
                        return "skt_alpinelinux";
                    } else if (sw_nm.equalsIgnoreCase("aix")) {
                        return "skt_aix";
                    } else if (sw_nm.equalsIgnoreCase("hp-ux")) {
                        return "skt_hpux";
                    } else if (sw_nm.equalsIgnoreCase("solaris")) {
                        return "skt_solaris";
                    } else if (sw_nm.equalsIgnoreCase("solaris10")) {
                        return "skt_solaris10";
                    } else if (sw_nm.equalsIgnoreCase("freebsd")) {
                        return "skt_freebsd";
                    } else if (sw_nm.equalsIgnoreCase("ubuntu")) {
                        return "skt_ubuntu";
                    } else if (sw_nm.equalsIgnoreCase("suselinux")) {
                        return "skt_suselinux";
                    } else if (sw_nm.equalsIgnoreCase("rockylinux")) {
                        return "skt_rockylinux";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                }
            } else if (dgType.equalsIgnoreCase("DB")) {
                if (gov_flag.equalsIgnoreCase("msit")) {
                    //기반시설 일 경우
                    if (sw_nm.equalsIgnoreCase("db2")) {
                        return "igloo_db2";
                    } else if (sw_nm.equalsIgnoreCase("mariadb")) {
                        return "igloo_mariadb";
                    } else if (sw_nm.equalsIgnoreCase("mysql")) {
                        return "igloo_mysql";
                    } else if (sw_nm.equalsIgnoreCase("oracle")) {
                        return "igloo_oracle";
                    } else if (sw_nm.equalsIgnoreCase("postgresql")) {
                        return "igloo_postgresql";
                    } else if (sw_nm.equalsIgnoreCase("sybase")) {
                        return "igloo_sybase";
                    } else if (sw_nm.equalsIgnoreCase("tibero4(sp1)")) {
                        return "igloo_tibero_4(sp1)";
                    } else if (sw_nm.equalsIgnoreCase("tibero5(sp1)")) {
                        return "igloo_tibero_5(sp1)";
                    } else if (sw_nm.equalsIgnoreCase("tibero5")) {
                        return "igloo_tibero_5";
                    } else if (sw_nm.equalsIgnoreCase("tibero6")) {
                        return "igloo_tibero_6";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else if (gov_flag.equalsIgnoreCase("msit_skt")){
                    // SKT기준 기반시설 일 경우
                    if (sw_nm.equalsIgnoreCase("mysql")) {
                        return "skinfra_mysql";
                    } else if (sw_nm.equalsIgnoreCase("oracle")) {
                        return "skinfra_oracle";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else {
                    // Common (SKT) 기준
                    if (sw_nm.equalsIgnoreCase("altibase")) {
                        return "skt_altibase";
                    } else if (sw_nm.equalsIgnoreCase("db2")) {
                        return "skt_db2";
                    } else if (sw_nm.equalsIgnoreCase("informix")) {
                        return "skt_informix";
                    } else if (sw_nm.equalsIgnoreCase("mariadb")) {
                        return "skt_mariadb";
                    } else if (sw_nm.equalsIgnoreCase("mongodb")) {
                        return "skt_mongodb";
                    } else if (sw_nm.equalsIgnoreCase("mysql")) {
                        return "skt_mysql";
                    } else if (sw_nm.equalsIgnoreCase("oracle")) {
                        return "skt_oracle";
                    } else if (sw_nm.equalsIgnoreCase("postgresql")) {
                        return "skt_postgresql";
                    } else if (sw_nm.equalsIgnoreCase("sybase")) {
                        return "skt_sybase";
                    } else if (sw_nm.equalsIgnoreCase("sybaseiq")) {
                        return "skt_sybaseiq";
                    } else if (sw_nm.equalsIgnoreCase("telcobase")) {
                        return "skt_telco";
                    } else if (sw_nm.equalsIgnoreCase("tibero")) {
                        return "skt_tibero";
                    } else if (sw_nm.equalsIgnoreCase("vertica")) {
                        return "skt_vertica";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                }
            } else if (dgType.equalsIgnoreCase("WAS")) {
                if (gov_flag.equalsIgnoreCase("msit")) {
                    //기반시설 일 경우
                    if (sw_nm.equalsIgnoreCase("jbosseap")) {
                        return "igloo_jbosseap";
                    } else if (sw_nm.equalsIgnoreCase("jbosseap7")) {
                        return "igloo_jbosseap7";
                    } else if (sw_nm.equalsIgnoreCase("jeus6")) {
                        return "igloo_jeus";
                    } else if (sw_nm.equalsIgnoreCase("jeus7")) {
                        return "igloo_jeus7";
                    } else if (sw_nm.equalsIgnoreCase("tomcat")) {
                        return "igloo_tomcat";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else if (gov_flag.equalsIgnoreCase("msit_skt")){
                    // SKT기준 기반시설 일 경우
                    if (sw_nm.equalsIgnoreCase("jeus6")) {
                        return "skinfra_jeus6";
                    }
                } else {
                    // Common (SKT) 기준
                    if (sw_nm.equalsIgnoreCase("jboss")) {
                        return "skt_tomcat";
                    } else if (sw_nm.equalsIgnoreCase("jeus")) {
                        return "skt_jeus";
                    } else if (sw_nm.equalsIgnoreCase("jeus7")) {
                        return "skt_jeus7";
                    } else if (sw_nm.equalsIgnoreCase("jrun")) {
                        return "skt_jrun";
                    } else if (sw_nm.equalsIgnoreCase("mosquitto")) {
                        return "skt_mosquitto";
                    } else if (sw_nm.equalsIgnoreCase("openstack")) {
                        return "skt_openstack";
                    } else if (sw_nm.equalsIgnoreCase("resin")) {
                        return "skt_resin";
                    } else if (sw_nm.equalsIgnoreCase("tomcat")) {
                        return "skt_tomcat";
                    } else if (sw_nm.equalsIgnoreCase("weblogic8")) {
                        return "skt_weblogic8";
                    } else if (sw_nm.equalsIgnoreCase("weblogic9")) {
                        return "skt_weblogic9";
                    } else if (sw_nm.equalsIgnoreCase("weblogic11g")) {
                        return "skt_weblogic11g";
                    } else if (sw_nm.equalsIgnoreCase("websphere")) {
                        return "skt_websphere";
                    } else if (sw_nm.equalsIgnoreCase("websphere6/7")) {
                        return "skt_websphere6_7";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                }

            } else if (dgType.equalsIgnoreCase("WEB")) {
                if (gov_flag.equalsIgnoreCase("msit")) {
                    //기반시설 일 경우
                    if (sw_nm.equalsIgnoreCase("apache")) {
                        return "igloo_apache";
                    } else if (sw_nm.equalsIgnoreCase("webtob")) {
                        return "igloo_webtob";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else if (gov_flag.equalsIgnoreCase("msit_skt")){
                    // SKT기준 기반시설 일 경우
                    if (sw_nm.equalsIgnoreCase("apache")) {
                        return "skinfra_apache";
                    } else if (sw_nm.equalsIgnoreCase("webtob")) {
                        return "skinfra_webtob";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else {
                    // Common (SKT) 기준
                    if (sw_nm.equalsIgnoreCase("apache")) {
                        return "skt_apache";
                    } else if (sw_nm.equalsIgnoreCase("iplanet6")) {
                        return "skt_iplanet6";
                    } else if (sw_nm.equalsIgnoreCase("iplanet7")) {
                        return "skt_iplanet7";
                    } else if (sw_nm.equalsIgnoreCase("nginx")) {
                        return "skt_nginx";
                    } else if (sw_nm.equalsIgnoreCase("webtob")) {
                        return "skt_webtob";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                }
            } else if (dgType.equalsIgnoreCase("CLOUD")) {
                if (gov_flag.equalsIgnoreCase("msit")) {
                    //기반시설 일 경우
                    if (sw_nm.equalsIgnoreCase("kubernetes")) {
                        return "igloo_kubenetes";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else if (gov_flag.equalsIgnoreCase("common")) {
                    // Common (SKT) 기준
                    if (sw_nm.equalsIgnoreCase("docker")) {
                        return "skt_docker";
                    } else if (sw_nm.equalsIgnoreCase("kubernetes")) {
                        return "skt_kubernetes";
                    } else {
                        return sw_nm + " Not Supported.";
                    }
                } else {
                    // SKT기준 기반시설 일 경우
                    return sw_nm + " Not Supported.";
                }
            } else {
                return sw_nm + " Not Supported.";
            }
        }
        // Windows 도 아니고 UNIX 도 아니고..
        return "NoFile";
    }

    /**
     * 2.0 Agent properties 등록 코드
     */
    public void reloadMemoryDB(){
        String propFileName = AGENT_LIBS_DIR + "agent.context.properites";
        // 환경 설정 파일(Manager REST server init)
        String PropKey1 = "NOTI_IP";		// 알림 서버(Manager)
        String PropKey2 = "LISTENER_PORT";	// Manager 서버 포트
        String PropKey3 = "RELAY_NOTIPORT"; // 2.0 Relay 서버 포트
        String PropKey4 = "EXEC_WAIT_TIME";	// 2.0 타임아웃
        String PropKey5 = "USE_NAT";		// 2.0 Relay NAT 사용 여부

        File pFile = new File(propFileName);
        InputStream is = null;
        if (!pFile.exists()){
            logger.debug("Prop File Not exist. use default.");
            propFileName = "agent.context.properites";
            is = getClass().getClassLoader().getResourceAsStream(propFileName);
        }
        else{
            logger.debug("Prop File exist.!! ");
            try {
                is = new FileInputStream(pFile);
            } catch (FileNotFoundException e) {
                logger.error(e.	getMessage(), e.fillInStackTrace());
            }
        }

        try {
            if (pFile.exists()){
                Properties prop = new Properties();
                if (prop != null) {
                    prop.load(is);
                    INMEMORYDB.NOTI_IP = prop.getProperty(PropKey1);
                    INMEMORYDB.LISTENER_PORT = prop.getProperty(PropKey2);

                    if (prop.getProperty(PropKey3) != null)
                        INMEMORYDB.RELAY_NOTIPORT = prop.getProperty(PropKey3);

                    if (prop.getProperty(PropKey4) != null)
                        INMEMORYDB.EXEC_WAIT_TIME = prop.getProperty(PropKey4);

                    if (prop.getProperty(PropKey5) != null)
                        INMEMORYDB.USE_NAT = prop.getProperty(PropKey5);
                }

                logger.debug("NOTI_IP :" + NOTI_IP);
                logger.debug("LISTENER_PORT :" + LISTENER_PORT);
                logger.debug("RELAY_NOTIPORT :" + RELAY_NOTIPORT);
                logger.debug("EXEC_WAIT_TIME :" + EXEC_WAIT_TIME);
                logger.debug("USE_NAT :" + USE_NAT);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
        }finally{
            try {
                if(is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Auth 인증 완료후 AC000000000.dat 파일 삭제
     * HOYOUNG
     * 2020.12.22
     */
    public static int deleteACfileList() {
        int delCnt=0;
        File path = new File(ASSET_CODE_PATH);
        String fname = "";
        String ext = "";
        File[] fileList = path.listFiles();
        if(fileList.length > 0) {
            for(int i=0;i<fileList.length;i++) {
                fname = fileList[i].getName();
                if(fname.substring(0,2).startsWith("AC")) {
                    int pos = fname.lastIndexOf(".");
                    if(pos > 0 && fileList[i].isFile()) {
                        ext = fname.substring(fname.lastIndexOf(".") + 1);
                        if(ext.toUpperCase().equals("DAT")) {
                            fileList[i].delete();
                        }
                    } else {
                        fileList[i].delete();
                    }
                    delCnt++;
                }
            }
        }
        return delCnt;
    }

    /**
     * REST API - URI 생성코드
     * HOYOUNG
     * 2020.05.19
     */
    public void reloadURIDB(){
        String propFileName = AGENT_LIBS_DIR + "agent.context.properites";
        String PropKey1 = "REST_SERVER";
        String PropKey2 = "REST_PORT";
        String PropKey3 = "REST_PROTOCOL";
        String PropKey4 = "REST_CLIENT";
        String PropKey5 = "MANAGER_IP";
        String PropKey6 = "EXEC_WAIT_TIME";			// 진단 실행 타임아웃
        String PropKey7 = "CONNECTTIMEOUT";			// HTTP Session 타임아웃
        String PropKey8 = "READTIMEOUT";			// HTTP Session Read 타임아웃


        String PropKey0 = "COMPANKEY";

        File pFile = new File(propFileName);
        InputStream is = null;
        if (!pFile.exists()){
            propFileName = "agent.context.properites";
            logger.debug(propFileName + " Prop File Not exist. use default.");
            is = getClass().getClassLoader().getResourceAsStream(propFileName);
        }
        else{
            logger.debug("Prop File exist.!! ");
            try {
                is = new FileInputStream(pFile);
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }
        try {
            Properties prop = new Properties();
            if (prop != null) {
                prop.load(is);
                INMEMORYDB.REST_SERVER = prop.getProperty(PropKey1);

                INMEMORYDB.REST_PORT = prop.getProperty(PropKey2);
                INMEMORYDB.REST_PROTOCOL = prop.getProperty(PropKey3);
                INMEMORYDB.REST_CLIENT = prop.getProperty(PropKey4);
                INMEMORYDB.MANAGER_IP = prop.getProperty(PropKey5);
                INMEMORYDB.COMPANKEY = prop.getProperty(PropKey0);
                if (prop.getProperty(PropKey6) != null)
                    INMEMORYDB.EXEC_WAIT_TIME = prop.getProperty(PropKey6);
                if (prop.getProperty(PropKey7) != null)
                    INMEMORYDB.CONNECTTIMEOUT = prop.getProperty(PropKey7);
                if (prop.getProperty(PropKey8) != null)
                    INMEMORYDB.READTIMEOUT = prop.getProperty(PropKey8);

            }
            logger.debug("REST_SERVER :" + REST_SERVER);
            logger.debug("REST_PORT :" + REST_PORT);
            logger.debug("REST_PROTOCOL :" + REST_PROTOCOL);
            logger.debug("REST_CLIENT :" + REST_CLIENT);
            logger.debug("MANAGER_IP :" + MANAGER_IP);
            // 고객사 암호화키(2021.1.14 - Hoyoung)
            logger.debug("COMPANKEY :" + COMPANKEY);
            logger.debug("EXEC_WAIT_TIME :" + EXEC_WAIT_TIME);
            logger.debug("CONNECTTIMEOUT :" + CONNECTTIMEOUT);
            logger.debug("READTIMEOUT :" + READTIMEOUT);

            // 다중 REST Server 록록 서버 설정
            INMEMORYDB.REST_SERVERS = prop.getProperty(PropKey1);
            // 다중 REST Server 체크 해서 1개의 서버 설정
            INMEMORYDB.REST_SERVER = getRestServer(INMEMORYDB.REST_SERVER);
            AUTH_URI=REST_PROTOCOL+"://"+REST_SERVER+":"+REST_PORT+"/manager/v3/sga-api/agent/auth";
            JOB_URI=REST_PROTOCOL+"://"+REST_SERVER+":"+REST_PORT+"/manager/v3/sga-api/agent/jobs";
            NOTI_URI=REST_PROTOCOL+"://"+REST_SERVER+":"+REST_PORT+"/manager/v3/sga-api/asset/process";
            FILE_DN_URI=REST_PROTOCOL+"://"+REST_SERVER+":"+REST_PORT+"/manager/v3/sga-api/asset/file/dn";
            FILE_UP_URI=REST_PROTOCOL+"://"+REST_SERVER+":"+REST_PORT+"/manager/v3/sga-api/asset/file/up";
        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
        }finally{
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 다중 IP 추출후 대상 서버 선정
     */
    public static String getRestServer(String ipLists) {
        String result = "";
        String[] ips = ipLists.split(",");
        boolean ch = false;
        boolean httpch = false;

        if(ips.length > 0) {
            for(int i=0;i<ips.length;i++) {

                result = ips[i];
                // AGENT 시작 최초 인증 처리 ,SG 3.0 인증 처리 추가
                ch = availablePort(result,Integer.parseInt(INMEMORYDB.REST_PORT));
                if(ch) {

                    httpch = getUrlGet(result,Integer.parseInt(INMEMORYDB.REST_PORT));
                    if(httpch) {
                        logger.info("Multi REST_SERVER IP Address  :" + result);
                        break;
                    } else {
                        logger.debug("Multi REST_SERVER 3.0 IP Address Check Fail:" + result);
                    }
                }
            }
        } else {
            result = ipLists;
        }

        return result;
    }
    public static boolean availablePort(String host,int port) {
        boolean result = false;
        int timeout = 2000;
        SocketAddress socketAddress = new InetSocketAddress(host,port);
        Socket socket = new Socket();
        try {
            socket.setSoTimeout(timeout);
            socket.connect(socketAddress,timeout);
            result = true;
        } catch (SocketException e) {
            result = false;
        } catch (IOException e) {
            result = false;
        } finally {
            try {
                socket.close();;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * HTTP 서버 여부 확인 ( SG2.0 , SG3.0 check)
     * Hoyoung - 2021-02-08
     * @param host
     * @param port
     * @return
     */
    public static boolean getUrlGet(String host,int port) {
        URL url;
        String sample_url ="http://"+host+":"+port+"/manager/v3/sga-api/agent/auth";
        boolean result = false;
        try {
            System.setProperty("sun.net.client.defaultConnectTimeout","3000");
            System.setProperty("sun.net.client.defaultReadTimeout","5000");
            url = new URL(sample_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String contentType = conn.getContentType();
            if(contentType != null) {
                result = true;

            }

        }  catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * REST API - Login(Auth) 인증 키
     * HOYOUNG
     * 2020.05.19
     */
    public static void reloadAuthDB(){
        String propFileName = AGENT_LIBS_DIR + "agent.context.properites";
        String PropKey1 = "AGENTCD";
        String PropKey2 = "ASSETCD";

        File pFile = new File(propFileName);
        InputStream is = null;
        if (!pFile.exists()){
            propFileName = "agent.context.properites";
            logger.debug(propFileName +" Prop File Not exist. use default.");
            is = INMEMORYDB.class.getClassLoader().getResourceAsStream(propFileName);
        }
        else{
            logger.debug(propFileName + " Prop File exist.!! ");
            try {
                is = new FileInputStream(pFile);
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }
        try {
            Properties prop = new Properties();
            if (prop != null) {
                prop.load(is);
                // 테스트 코드
                if(INMEMORYDB.ASSETCD.equals("")) {
                    INMEMORYDB.ASSETCD =  prop.getProperty(PropKey2);
                    logger.debug("INMEMORYDB.ASSETCD is from context. " + INMEMORYDB.ASSETCD);
                } else {
                    logger.debug("INMEMORYDB.ASSETCD is from AC Files. " + INMEMORYDB.ASSETCD);
                    prop.setProperty(PropKey2, INMEMORYDB.ASSETCD);
                    FileOutputStream out = new FileOutputStream(propFileName);
                    prop.store(out,null);
                    out.close();
                }
                if (!INMEMORYDB.ASSETCD.isEmpty()) {
                    INMEMORYDB.ASSETCD_LISTS.add(INMEMORYDB.ASSETCD.trim());
                }
                if(INMEMORYDB.AGENTCD.equals("")) {
                    INMEMORYDB.AGENTCD =  prop.getProperty(PropKey1);
                } else {
                    prop.setProperty(PropKey1, INMEMORYDB.AGENTCD);
                    FileOutputStream out = new FileOutputStream(propFileName);
                    prop.store(out,null);
                    out.close();
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
        }finally{
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String[] readFile(File f) {
        String[] resultLines = null;
        String[] tempresultLines = new String[100];
        int lineCnt = 0;

        try {
            BufferedReader input = new BufferedReader(new FileReader(f));

            try{
                String line = null;
                while( (line = input.readLine()) != null)
                {
                    tempresultLines[lineCnt] = line;
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
            resultLines = new String[lineCnt];
            for(int k =0 ; k < lineCnt; k++){
                resultLines[k] = tempresultLines[k];
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return resultLines;
    }

    private String getLocalServerIp()
    {
        try
        {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(networkInterfaces.hasMoreElements()) {
                NetworkInterface if_ = (NetworkInterface) networkInterfaces.nextElement();
                Enumeration e2 = if_.getInetAddresses();
                while(e2.hasMoreElements()) {
                    InetAddress addr = (InetAddress)e2.nextElement();
                    if (!addr.isLoopbackAddress() && !addr.isLinkLocalAddress() && addr.isSiteLocalAddress()) {
                        System.out.println("Address = " +addr.getHostAddress());
                        System.out.println("HostName = " +addr.getHostName());
                        return addr.getHostAddress().toString();
                    }
                }
            }

        }
        catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 수동설치 실행시 AssetCd 설정 파일 등록
     */
    public static void agentManualSetAssetCd(String assetCd){
        String propFileName = AGENT_LIBS_DIR + "agent.context.properites";
        String PropKey1 = "ASSETCD";
        File pFile = new File(propFileName);
        InputStream is = null;
        if (!pFile.exists()){
            propFileName = "agent.context.properites";
            logger.debug(propFileName +" Prop File Not exist. use default.");
            is = INMEMORYDB.class.getClassLoader().getResourceAsStream(propFileName);
        }
        else{
            logger.debug(propFileName + " Prop File exist.!! ");
            try {
                is = new FileInputStream(pFile);
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e.fillInStackTrace());
            }
        }

        try {
            Properties prop = new Properties();
            if (prop != null) {
                prop.load(is);
                // 테스트 코드
                if(INMEMORYDB.ASSETCD.equals("")) {
                    INMEMORYDB.ASSETCD =  prop.getProperty(PropKey1);
                } else {
                    prop.setProperty(PropKey1, INMEMORYDB.ASSETCD);
                    FileOutputStream out = new FileOutputStream(propFileName);
                    prop.store(out,null);
                    out.close();
                }
            }
            logger.debug("ASSETCD :" + ASSETCD);
        } catch (Exception e) {
            logger.error(e.getMessage(), e.fillInStackTrace());
        }finally{
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 에이전트 로그 파일 삭제(10일)
    public void deleteLogFiles() {
        Calendar cal = Calendar.getInstance();
        Calendar fileCal = Calendar.getInstance();

        long oneDayMil = 24 * 60 * 60 * 1000;

        String logs = INMEMORYDB.AGENT_SYS_ROOT_DIR + "logs";

        if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
            logs = INMEMORYDB.AGENT_SYS_ROOT_DIR_WIN + "logs";
        }

        File path = new File(INMEMORYDB.replaceDir(logs));
        File[] list = path.listFiles();

        if (list != null && list.length > 0) {
            for (int i = 0; i < list.length; i++) {
                fileCal.setTime(new Date(list[i].lastModified()));

                long diffMil = cal.getTimeInMillis() - fileCal.getTimeInMillis();
                int diffDay = (int) (diffMil / oneDayMil);
                if (diffDay > 10 && list[i].exists()) {
                    list[i].delete();
                    logger.info("delete log file: " + list[i].getName());
                }
            }
        }
    }

    public static void main(String args[]){
        new INMEMORYDB().deleteOffLineFile("/usr/local/snet/");
    }
}
