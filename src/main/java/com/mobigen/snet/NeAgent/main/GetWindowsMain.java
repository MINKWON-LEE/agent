package com.mobigen.snet.NeAgent.main;

import com.mobigen.snet.NeAgent.crypto.AESCryptography;
import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import com.mobigen.snet.NeAgent.service.FileManager;
import com.mobigen.snet.NeAgent.utils.CommonUtils;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by osujin12 on 2016. 4. 18..
 * Modify by hoyoung on 2020.06.02
 * - 통신방신 변경 (HTTPS or HTTP)
 */
public class GetWindowsMain {
    private static Logger logger = LoggerFactory.getLogger(GetWindowsMain.class);

    public static class ExecResult extends LogOutputStream {
        private  int exitCode;
        public int getExitCode() {
            return exitCode;
        }
        public void setExitCode(int exitCode) {
            this.exitCode = exitCode;
        }
        private final List lines = new LinkedList();

        protected  void processLine(String line, int level) {
            lines.add(line);
        }

        public List getLines() {
            return lines;
        }
    }

    public static ExecResult execCmd(String cmd, int exitValue) throws  Exception {
        CommandLine cmdLine = CommandLine.parse(cmd);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(exitValue);
        ExecResult result = new ExecResult();
        executor.setStreamHandler(new PumpStreamHandler(result));
        result.setExitCode(executor.execute(cmdLine));
        return result;
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

    public static void main(String args[]) throws Exception {

        INMEMORYDB memory = new INMEMORYDB();
        memory.reloadMemoryDB();
        // 1회 진단 처리 초기화
        if (args.length > 1) {
            memory.off_init();
        } else {
            memory.init();
        }

        //String managerCd = args[0];
        //String managerCd = "T100000000";
        // 2020.12.08 - manager Cd 처리
        String managerCd = "";
        System.out.println("input size : " + args.length);
        if(args.length > 0){
            if(args[0].equals("")) {
                //managerCd= "T100000000";
                //managerCd = "T1diagtester00000";
                managerCd = "T1TiDC00000";

            } else {
                if(args[0].startsWith("T1")){
                    managerCd = args[0];
                } else {
                    //managerCd = "T1diagtester00000";
                    managerCd = "T1TiDC00000";
                }
            }
        } else {
            managerCd = "T1TiDC00000";
        }


        String libPath = INMEMORYDB.AGENT_LIBS_DIR + INMEMORYDB.WIN_GET_JAR + " " + managerCd;
        String javaPath = INMEMORYDB.AGENT_JRE_DIR + "java ";

        String getFullpath = INMEMORYDB.AGENT_LIBS_DIR + CommonUtils.toMux(INMEMORYDB.WIN_GET_JAR);
        new AESCryptography().decryptionShFile(getFullpath, INMEMORYDB.AGENT_LIBS_DIR + INMEMORYDB.WIN_GET_JAR);

        String exec = javaPath + "-jar " + libPath;
        System.out.println();
        logger.debug("exec cmd : " + exec);

        ExecResult result = null;
        try {
            result = execCmd(exec, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.debug("exitValue : " + result.exitCode);

        /*
         * 2020.05.29 - Hoyoung
         * 통신 방식 변경 HTTP 처리
         */
        List lines = result.getLines();

        if(args.length > 1){
            // read the output from the command
            for(int i = 0; i < lines.size(); i++) {
                System.out.println(lines.get(i));
            }
        }else {
            String s = null;
            String assetcd = "";
            try {
                for(int i = 0; i < lines.size(); i++) {
                    s = lines.get(i).toString();
                    logger.debug("line : " + s);
                    if (s.indexOf("ASSETCD") != -1) {
                        String temp[] = s.split("=");
                        assetcd = temp[1];
                    }
                }
                // AssetCD 코드 등록(agent.context.properites)
                memory.agentManualSetAssetCd(assetcd);

                JobEntity3 jobEntity = new JobEntity3();
                FileManager httpClient = new FileManager();
                jobEntity.setManagerCd(managerCd);
                jobEntity.setAuditFileCd(assetcd);
                jobEntity.setJobType3("AJ201");
                jobEntity.setAssetCd(assetcd);
                String targetFileNm = jobEntity.getAuditFileCd()+".dat";
                String path=INMEMORYDB.GETSCRIPT_RESULT_FILE_AGENT_DIR;
                File file = new File(path+targetFileNm);
                removeFirstLine(file);
                httpClient.setResultUp(jobEntity);
                logger.debug("Finish Get prog.");
                // 수동 처리 할때 AssetCD 를  agent.context.properites 에 등록
                INMEMORYDB.ASSETCD = assetcd;
                memory.reloadAuthDB();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
              //  if(socketClient != null) socketClient.closeSocket();
            }
        }

    }

}
