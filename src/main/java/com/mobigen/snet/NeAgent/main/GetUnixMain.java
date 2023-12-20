package com.mobigen.snet.NeAgent.main;

import com.mobigen.snet.NeAgent.crypto.AESCryptography;
import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import com.mobigen.snet.NeAgent.service.FileManager;
import com.mobigen.snet.NeAgent.utils.CommonUtils;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.net.InetAddress;

import java.security.MessageDigest;

/**
 * Created by osujin12 on 2016. 4. 18..
 * Modify by hoyoung on 2020.06.02
 * - 통신방신 변경 (HTTPS or HTTP)
 */
public class GetUnixMain {
    private static Logger logger = LoggerFactory.getLogger(GetUnixMain.class);

    public static void main(String args[]) throws Exception {
        INMEMORYDB memory = new INMEMORYDB();
        memory.reloadMemoryDB();
        // 1회 진단 처리 초기화
        if (args.length > 2) {
            memory.off_init();
        } else {
            memory.init();
        }

        boolean fileMake = false;

        File dir =new File(INMEMORYDB.DIAGPATH);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        String assetCode = "";
        assetCode = getAssetCode();
        // 2020.12.08 - manager Cd 처리
        String managerCd = "";
        if(args.length > 0){
            if(args[0].equals("")) {
                managerCd = "T1TiDC00000";
            } else {
                if(args[0].startsWith("T1")){
                    managerCd = args[0];
                } else {
                    managerCd = "T1TiDC00000";
                }
            }
        }  else {
            managerCd = "T1TiDC00000";
        }

        String exec = "LANG=C;export LANG;"+ "chmod 755 ";
        String getFullpath = null;

        if (args.length > 2){
            exec += INMEMORYDB.OFFLINE_DIAGPATH_GUNIX + INMEMORYDB.UNIX_GET_SH + "; ";
            exec += INMEMORYDB.OFFLINE_DIAGPATH_GUNIX + INMEMORYDB.UNIX_GET_SH + " " +managerCd;

            getFullpath =  INMEMORYDB.OFFLINE_AGENT_LIBS_DIR_GUNIX + CommonUtils.toMux(INMEMORYDB.UNIX_GET_SH);
            fileMake = new AESCryptography().decryptionShFile(getFullpath,INMEMORYDB.OFFLINE_DIAGPATH_GUNIX+INMEMORYDB.UNIX_GET_SH);
        } else {
            exec += INMEMORYDB.DIAGPATH + INMEMORYDB.UNIX_GET_SH + "; ";
            exec += INMEMORYDB.DIAGPATH + INMEMORYDB.UNIX_GET_SH + " " +managerCd + " " + assetCode;

            getFullpath =  INMEMORYDB.AGENT_LIBS_DIR + CommonUtils.toMux(INMEMORYDB.UNIX_GET_SH);
            fileMake = new AESCryptography().decryptionShFile(getFullpath,INMEMORYDB.DIAGPATH+INMEMORYDB.UNIX_GET_SH);
        }

        long startTime = System.currentTimeMillis();
        CommandLine cmdLine = CommandLine.parse("/bin/sh");
        cmdLine.addArgument("-c");
        cmdLine.addArgument(exec, false);
        // 2021.07.05(Hoyoung) - 구동(스트림처리 문제 제거) 방식 변경
        System.out.print("DefaultExecutor Process start.");
        DefaultExecutor executor = new DefaultExecutor();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000 * Integer.parseInt(INMEMORYDB.EXEC_WAIT_TIME));
        executor.setWatchdog(watchdog);

        CollectingLogOutputStream los = new CollectingLogOutputStream();
        executor.setStreamHandler(new PumpStreamHandler(los));

        int exitCode = -1;
        try {
            exitCode = executor.execute(cmdLine);
            long duration = System.currentTimeMillis() - startTime;
            logger.info("exit : "+exitCode+", Process completed in : " + duration + " millis, below is its output");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        if (watchdog.killedProcess()) {
            logger.error("Process timed out and was killed by watchdog.");
        }
        System.out.print("DefaultExecutor Process end.");
        long duration2 = System.currentTimeMillis() - startTime;
        logger.debug("GetUnixMain :  " + startTime + " :: " +duration2 );
        if(args.length > 1){
            System.out.println("Here is the standard error of the command (if any):\n");
            los.close();
        } else {
            String assetcd = "";
            try {
                String s = null;
                List lines = los.getLines();
                for (int i = 0; i < lines.size(); i++) {
                    s = lines.get(i).toString();
                    System.out.println(new Date().toString() +" : "+ s);
                    if (s.indexOf("ASSETCD") != -1) {
                        String temp[] = s.split("=");
                        assetcd = temp[1];
                    }
                }
                // AssetCD 코드 등록(agent.context.properites)
                logger.debug("before uploading dat file. - " + assetcd);
                memory.agentManualSetAssetCd(assetcd);
                JobEntity3 jobEntity = new JobEntity3();
                FileManager httpClient = new FileManager();
                jobEntity.setManagerCd(managerCd);
                jobEntity.setFileNm(assetcd);
                jobEntity.setJobType3("AJ201");
                jobEntity.setAuditFileCd(assetcd);
                jobEntity.setAssetCd(assetcd);
                String targetFileNm = jobEntity.getAuditFileCd()+".dat";
                String path=INMEMORYDB.GETSCRIPT_RESULT_FILE_AGENT_DIR;
                File file = new File(path+targetFileNm);
                removeFirstLine(file);

                httpClient.setResultUp(jobEntity);
                // 수동 처리 할때 AssetCD 를  agent.context.properites 에 등록
                INMEMORYDB.ASSETCD = assetcd;
                memory.reloadAuthDB();
                System.out.println("Executor Finish Get prog.");
            } catch (IOException e) {
                System.out.println("EXCEPTION : "+new Date().toString() +" :"+e.getMessage());
                los.close();
            } finally {
                if (los != null) {
                    los.close();
                }
            }
        }
    }
    // 장비정보 수집 1 라인 삭제 (자동)
    private static void removeFirstLine(File fileName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        //Initial write position
        long writePosition = raf.getFilePointer();
        // Shift the next lines upwards.
        long readPosition = -1;

        String s;
        while ((s = raf.readLine()) != null) {
            if (s.indexOf("GSBFAGENT") > -1) {
                readPosition = raf.getFilePointer();
                logger.debug(s);
                logger.debug("readPosition : " + readPosition);
                break;
            }
        }

        byte[] buff = new byte[1024];
        int n;
        while (-1 != (n = raf.read(buff))) {
            raf.seek(writePosition);
            raf.write(buff, 0, n);
            readPosition += n;
            writePosition += n;
            raf.seek(readPosition);
        }
        raf.setLength(writePosition);
        raf.close();
    }


    private static String[] listToArray(List list){
        String[] result = new String[list.size()];
        for(int i=0; i<list.size();i++){
            result[i] = list.get(i).toString();
        }
        return result;
    }

    private static String getAssetCode() {
        String ipAddr = "";
        String hostName = "";
        String hashData = "";
        String assetCode = "";

        try {
            InetAddress local = InetAddress.getLocalHost();
            ipAddr = local.getHostAddress();
            hostName = local.getHostName();

            hashData = extractHashSHA256(ipAddr + hostName + "s");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date time = new Date();
        assetCode = "AC" + sdf.format(time) + hashData;

        return assetCode;
    }

    public static String extractHashSHA256(String input) throws Exception {
        String SHA = "";
        MessageDigest hashSum = MessageDigest.getInstance("SHA-256");

        byte[] partialHash = null;

        hashSum.update(input.getBytes(), 0, input.getBytes().length);

        partialHash = hashSum.digest();

        StringBuffer sb = new StringBuffer();
        for(int i = 0 ; i < partialHash.length ; i++){
            sb.append(Integer.toString((partialHash[i]&0xff) + 0x100, 16).substring(1));
        }

        SHA = sb.toString().toUpperCase();

        SHA = SHA.substring(0, 4);

        return SHA;
    }

}
