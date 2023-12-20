package com.mobigen.snet.NeAgent.service;

import com.mobigen.snet.NeAgent.concurrents.OneTimeThread;
import com.mobigen.snet.NeAgent.crypto.AESCryptography;
import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.entity.RequestParams;
import com.mobigen.snet.NeAgent.entity.ResponseEntity;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import com.mobigen.snet.NeAgent.utils.*;
import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipException;

/**
 * 스마트 가드 3.0 서비스 시작
 * - Hoyoung 2020.06.01
 */
public class AgentServiceManager {
    private static Logger logger = LoggerFactory.getLogger(AgentServiceManager.class);
    private static JobEntity3 jobEntity = new JobEntity3();
    private static FileManager fileJobReq = new FileManager();
    private static HttpManager httpJobReq = new HttpManager();
    private static HttpsManager httpsJobReq = new HttpsManager();
    private static HashMap headers = new HashMap();
    private static HashMap params = new HashMap();
    private static String tempFileNm = "";
    private static NotiService processNoti = new NotiService();
    private static AgentReStartService restart = new AgentReStartService();
    private static AgentStopService stop = new AgentStopService();
    private static AgentLogUpService logUp = new AgentLogUpService();
    private static AgentPatchService patch = new AgentPatchService();

    INMEMORYDB inmemorydb;
    //Agent 프로세스 모니터링 파일
    ProcessCheck processCheck = new ProcessCheck();
    // 취약점 진단 대기 시간 변경 ( default 20Min )
    static int waitTime = 60 * 1000 * Integer.parseInt(INMEMORYDB.EXEC_WAIT_TIME);
    static int retry_waitTime = 5;
    static boolean setFlag = false;
    // 주석 처리
    public void setInmemorydb(INMEMORYDB inmemorydb) {
        this.inmemorydb = inmemorydb;
    }
    // 서비스 쓰레드 처리
    public void initService() {
        AgentAuthService auth = new AgentAuthService();
        int retry_cnt = 0;
        boolean ch = false;
        try {
            // AGENT 시작 최초 인증 처리 ,SG 3.0 인증 처리 추가
            if(INMEMORYDB.ASSETCD_LISTS.size() >= 1) {
                logger.info("initService Agent AC multiFile status : " + INMEMORYDB.ASSETCD_LISTS.size()  + "," +retry_waitTime);
                for(int ac=0;ac<INMEMORYDB.ASSETCD_LISTS.size();ac++) {

                    INMEMORYDB.ASSETCD = INMEMORYDB.ASSETCD_LISTS.get(ac).toString().trim();
                    ch = auth.doAuth();
                    if(ch) {
                        logger.info("initService Agent AC File List Auth OK : " + INMEMORYDB.ASSETCD_LISTS.get(ac));
                        break;
                    } else {
                        logger.info("initService Agent AC File List Auth Fail : " + INMEMORYDB.ASSETCD_LISTS.get(ac));
                    }
                }
            } else {
                logger.info("initService ASSETCD_LISTS is Empty");
                ch = auth.doAuth();
                inmemorydb.initAuthFileCh();
            }
            //ch = auth.doAuth();
            if (ch) {

                OneTimeThread worker = new OneTimeThread() {
                    public void task() throws Exception {
                        initServiceWorker();
                    }
                };
                worker.start();
            } else {
                // 빠른 무한반복 방지
                retry_cnt = retry_cnt + 1;
                if (retry_waitTime > 1799) { //1799
                    retry_waitTime = 1800;   //1800
                } else {
                    retry_waitTime = retry_waitTime * 2;
                }
                logger.info("initService Agent Auth : " + retry_cnt + "," +retry_waitTime);
                CommonUtils.sleep(retry_waitTime);
                // agent.context.properties 설처 reload (Hoyoung - 2020.12.01)
                inmemorydb.reloadAuthDB();
                logger.info("**** initService init.. : " + INMEMORYDB.REST_SERVERS +" ****");
                inmemorydb.reloadURIDB();
                initService();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logger.info("**** initService finally.. ****");

        }
    }

    // 주석 처리
    public void initServiceWorker() {
        String resp = "";
        int setTime = 60;
        int mb = 1024 * 1024;
        logger.info("initServiceWorker start");
        while (true) {

            OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            String[] chkMem  = { "/bin/sh", "-c", "free | grep ^Mem | awk '{print $2\",\"$3}'" };

            try {
                // 작업 진단 요청
                String methond = "GET";
                params = new RequestParams().setParams("JOBS");
                int memTotal = 0;
                int memUse = 0;
                int memFree = 0;
                int Check = 1;

                if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
                    memTotal = Math.round(os.getTotalPhysicalMemorySize() / 1000);      //메모리 토탈
                    memFree = Math.round(os.getFreePhysicalMemorySize() / 1000);        //메모리 사용가능
                    memUse = memTotal - memFree;                                       //메모리 사용
                } else if (System.getProperty("os.name").toLowerCase().contains("sun")
                        || System.getProperty("os.name").toLowerCase().contains("solaris")
                        || System.getProperty("os.name").toLowerCase().contains("hp")
                        || System.getProperty("os.name").toLowerCase().contains("aix")) {

                    Check = 0;
                    logger.debug("do not check. : unix os - " + System.getProperty("os.name"));
                } else {

                    String results = CommonUtils.getRunProcess(chkMem);

                    if (results.contains(",")) {
                        String[] result = results.split(",");
                        memTotal = Integer.valueOf(result[0]);  //메모리 토탈
                        memUse = Integer.valueOf(result[1]);    //메모리 사용
                        memFree = memTotal - memUse;            //메모리 사용가능
                    }

                }

                String memUseRate = "";
                if ( Check == 1 ) {
                    memUseRate = String.format("%.2f", ((double) memUse / memTotal) * 100); //메모리 사용률
                } else {
                    memUseRate = "12341234";
                }

                String cpuUseRate = "";
                try {
//                    if (System.getProperty("os.name").toLowerCase().contains("hp")) {
//                        cpuUseRate = new CpuUtil().getCpuUsage();
//                    } else {

                    if (Check == 1) {
                        cpuUseRate = String.format("%.2f", os.getSystemCpuLoad() * 100.0);  //CPU 사용률
                    } else {
                        cpuUseRate = "12341234";
                    }
                } catch (Exception ex) {
                    cpuUseRate = "0";
                }

                params.put("cpuUseRate", cpuUseRate);
                params.put("memTotal", memTotal);
                params.put("memFree", memFree);
                params.put("memUse", memUse);
                params.put("memUseRate", memUseRate);

                if(INMEMORYDB.REST_PROTOCOL.equals("HTTP")) {
                    resp = httpJobReq.httpUrlConnection(methond, INMEMORYDB.JOB_URI, params, headers, true, false);
                } else {
                    resp = httpsJobReq.httpsUrlConnection(methond, INMEMORYDB.JOB_URI, params, headers, true, false);
                }

                logger.info(DateUtil.getCurTimeInDate() + " =targetUrl= " + INMEMORYDB.JOB_URI + "::" + params.toString());
                mainExecute(resp);

                if(setFlag) {
                    setTime = Integer.parseInt(jobEntity.getDelaytime2()) / 1000;
                } else {
                    setTime = Integer.parseInt(jobEntity.getDelaytime()) / 1000;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            // 빠른 무한반복 방지
            CommonUtils.sleep(setTime);
            setFlag = false;
        }

    }


    // 주석 처리
    private void mainExecute(String resp) throws Exception, IOException, InterruptedException {
        String[] header;
        String rcvHeader;
        ResponseEntity responseData = new ResponseEntity();
        try {
            jobEntity = responseData.jobsRespDate(resp);
            String jobType = jobEntity.getJobType();
            logger.debug("RECEIVED HEADER :: " + jobEntity.toString());
            if(jobEntity.getResult().equals("200")) {
                if (jobEntity.getMessage().equals("success")) {
                    logger.debug("request Job Type success : " + jobType);
                    parseJobType(jobEntity);
                } else {

                }
            } else {
                logger.debug("RECEIVED HEADER Faile  : " + jobEntity.getResult() + " delaytime 5min");
                jobEntity.setDelaytime("300000");
            }

        } catch (Throwable th) {
            logger.error(String.valueOf(th), th);
            deleteAllFile();
        }
    }

    /**
     * 에이전트 작업 타입 구분
     *
     * @param jobEntity
     * @return
     * @throws Throwable
     */
    private boolean parseJobType(JobEntity3 jobEntity) throws Throwable {
        String jobType = jobEntity.getJobType();
        jobEntity.setJobType3(jobType);
        if (jobType.equals("AJ100") || jobType.equals("AJ101") || jobType.equals("AJ102")) {// Agent 진단실행,신규진단실행
            jobType = "DGFILE";
            jobEntity.setJobType("DGFILE");
        } else if (jobType.equals("AJ200")) {               // Agent 장비 정보 수집
            jobType = "GSCRPTFILE";
            jobEntity.setJobType("GSCRPTFILE");
        } else if (jobType.equals("AJ602")) {               // Agent 중지
            jobType = "KILLAGENT";
            jobEntity.setJobType("KILLAGENT");
        } else if (jobType.equals("AJ601")) {               // Agent 재시작
            jobType = "RESTARTAGENT";
            jobEntity.setJobType("RESTARTAGENT");
        } else if (jobType.equals("AJ300")) {               // Agent Log 전달
            jobType = "AGENTLOG";
            jobEntity.setJobType("AGENTLOG");
        } else if (jobType.equals("AJ603")) {               // Agent 업데이트
            jobType = "AGENTUPDATEREQ";
            jobEntity.setJobType("AJ603");
        }

        // INMEMORYDB 재사용을 위해서 기존 코드 사용
        if (INMEMORYDB.AGENTUPDATEREQ.equals(jobType)) {
            patch.handleAgentUpdate(jobEntity);                 // Agent 업데이트
        } else if (INMEMORYDB.GSCRPTFILE.equals(jobType)) {
            handleGS(jobEntity);                                //Get Script 파일 수신(장비정보 수집)
        } else if (INMEMORYDB.DGFILE.equals(jobType)) {
            handleDG(jobEntity);                                // 진단실행
        } else if (INMEMORYDB.KILLAGENT.equals(jobType)) {
            stop.killAgent(jobEntity);                          //Agent 중지
        } else if (INMEMORYDB.RESTARTAGENT.equals(jobType)) {
            jobEntity.setNotiType("AN003");
            restart.runAgent(jobEntity);                        //Agent 재시작
        } else if (INMEMORYDB.AGENTLOG.equals(jobType)) {
            logUp.getLog(jobEntity);                            //Agent 로그 파일 업로드
        } else {
            logger.error("JOBTYPE NOT FOUND.!");
        }
        logger.info(jobType + " complete..!!");
        return true;
    }

    /**
     * 3.0 장비 정보 수집 처리
     * Hoyoung - 2020.05.31
     *
     * @param jobEntity
     * @throws Throwable
     */
    private void handleGS(JobEntity3 jobEntity) throws Throwable {
        //process moniter file append
        processCheck.append_Start(jobEntity);
        long startTime = System.currentTimeMillis();
        long duration = 0;
        //진단 관련 파일 삭제.
        logger.info("GS : " + jobEntity.getJobType()+","+INMEMORYDB.RECV);
        INMEMORYDB.deleteFile(jobEntity, INMEMORYDB.RECV);
        //OS 타입 확인
        String os = System.getProperty("os.name");
        //file 수신 (다운로드 파일명 확인)
        receiveFile(jobEntity);


        jobEntity.setFileNm(tempFileNm.substring(0, tempFileNm.length() - 4));
        jobEntity.setAuditFileCd(tempFileNm.substring(0, tempFileNm.length() - 4));
        //다운로드 파일명 체크
        if (jobEntity.getFileNm().equals("error")) {
            params = new RequestParams().setParams("NOTI");
            jobEntity.setNotiType("AN054");
            jobEntity.setJobType(jobEntity.getJobType3());
            processNoti.doProcessNoti(jobEntity,params);
            logger.info("handleGS File download error : " + tempFileNm);
        } else {
            //Get Script file은 jar 타입으로 확장자를 붙여준다.
            if (os.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
                jobEntity.setFileType(INMEMORYDB.JAR);
            } else {
                jobEntity.setFileType(INMEMORYDB.SH);
            }
            // 2.0 에서는 setManagerCd, setAssetCd 추가
            //sudo 권한 실행 여부
            String useDiagSudo = "N";
            if (jobEntity.getUseDiagSudo() != null) {
                useDiagSudo = jobEntity.getUseDiagSudo();
                logger.info("useDiagSudo : " + useDiagSudo);
            }
            //Receive Zip File
            jobEntity.setFileType(INMEMORYDB.ZIP);
            //File decryption
            new AESCryptography().decryptionFile(jobEntity);
            /**Unzip File**/
            CommonUtils.unZipFile(jobEntity);
            /***
             * 진단 파일 수행
             * Windows = jar
             * 그 외에 shell script
             */
            String recvPath = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.RECV, jobEntity.getJobType());
            String exex;
            //Get Script 수행 명령어, 추후 정식 GetScript 파일이 오면 수정 필요.
            if (os.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
                exex = INMEMORYDB.AGENT_JRE_DIR + "java -jar " + recvPath + jobEntity.getAuditFileCd() + ".jar" + " " + jobEntity.getManagerCd() + " " + jobEntity.getAssetCd();
                logger.info("Process exex " + exex );
                CommandLine cmdLine = CommandLine.parse("cmd");
                cmdLine.addArgument("/c");
                cmdLine.addArgument(exex, false);
                DefaultExecutor executor = new DefaultExecutor();
                ExecuteWatchdog watchdog = new ExecuteWatchdog(waitTime);
                executor.setWatchdog(watchdog);
                ByteArrayOutputStream outputStreamAgentStart = new ByteArrayOutputStream();
                executor.setStreamHandler(new PumpStreamHandler(outputStreamAgentStart));
                int exitCode = -1;
                try {
                    exitCode = executor.execute(cmdLine);
                    duration = System.currentTimeMillis() - startTime;
                    logger.info("Process completed in " + duration + " millis; below is its output");
                    logger.debug(new String(outputStreamAgentStart.toByteArray()));
                    logger.info("exit " + exitCode);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    if (outputStreamAgentStart != null) {
                        outputStreamAgentStart.close();
                    }
                }
                if (watchdog.killedProcess()) {
                    logger.error("Process timed out and was killed by watchdog.");
                }
            } else {
                exex = "LANG=C;export LANG;" + "chmod 755  " + recvPath + jobEntity.getAuditFileCd() + INMEMORYDB.SH + "; " + recvPath + jobEntity.getAuditFileCd() + INMEMORYDB.SH + " " + jobEntity.getManagerCd() + " " + jobEntity.getAssetCd();

                String cmdString = "";
                try {
                    cmdString = makeCommandLine(useDiagSudo);
                } catch (Exception e) {
                    //진단 수행 후 Manager에서 전송받은 Get Script jar파일 삭제
                    if (!INMEMORYDB.isDebug) INMEMORYDB.deleteFile(jobEntity, INMEMORYDB.RECV);
                    jobEntity.setJobType(jobEntity.getJobType3());
                    params = new RequestParams().setParams("NOTI");
                    jobEntity.setNotiType("AN060");
                    // 진단파일 실행 실패 알림
                    processNoti.doProcessNoti(jobEntity , params);
                    processCheck.append_End();
                    logger.error(e.getMessage());
                    return;
                }

                CommandLine cmdLine = CommandLine.parse(cmdString);
                cmdLine.addArgument("-c");
                cmdLine.addArgument(exex, false);

                DefaultExecutor executor = new DefaultExecutor();
                ExecuteWatchdog watchdog = new ExecuteWatchdog(waitTime);
                executor.setWatchdog(watchdog);
                ByteArrayOutputStream outputStreamAgentStart = new ByteArrayOutputStream();
                executor.setStreamHandler(new PumpStreamHandler(outputStreamAgentStart));

                int exitCode = -1;

                try {
                    exitCode = executor.execute(cmdLine);
                    duration = System.currentTimeMillis() - startTime;
                    logger.info("Process completed in : " + duration + " millis, below is its output");
                    logger.debug(new String(outputStreamAgentStart.toByteArray()));
                    logger.info("exit " + exitCode);

                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    if (outputStreamAgentStart != null) {
                        outputStreamAgentStart.close();
                    }
                }

                if (watchdog.killedProcess()) {
                    logger.error("Process timed out and was killed by watchdog.");
                }
            }


            //진단 수행 후 Manager에서 전송받은 Get Script jar파일 삭제
            if (!INMEMORYDB.isDebug) INMEMORYDB.deleteFile(jobEntity, INMEMORYDB.RECV);

            //Get Script Result file Decryption
            jobEntity.setJobType(INMEMORYDB.GSCRPTFIN);
            jobEntity.setFileType(INMEMORYDB.GSRESULTTYPE);

            //Get 스크립트 수행 후 만들어지는 결과 피일의 이름을 구한다.
            logger.debug("Find FIle type : " + jobEntity.getFileType());
            INMEMORYDB.moveFileName(jobEntity, INMEMORYDB.SEND, jobEntity.getFileType());
            Thread.sleep(500);

            String path = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType());
            if (!INMEMORYDB.CURRENTACCOUNT.equals(INMEMORYDB.ROOTACCOUNT)) {
                changedOwnerBySudo(path);
            }

            //Get 프로그램 결과파일 생성 후 cOTP 값을 추가해준다.
            //insertCOTP(jobEntity);
            String resultPath = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType());
            String filePath = resultPath + jobEntity.getAuditFileCd() + jobEntity.getFileType();

            File file = new File(filePath);
            removeFirstLine(file);

            /**진단결파 파일 생성 체크**/
            boolean fck = CommonUtils.getResultFileCheck(jobEntity);
            logger.info("Device Infomation Result File Check : " + fck);
            if(!fck) {
                /**진단결과 파일 생성 실패 및 진달 실행 처리 오류**/
                params = new RequestParams().setParams("NOTI");
                jobEntity.setNotiType("AN052");
                // 진단파일 실행 실패 알림
                processNoti.doProcessNoti(jobEntity , params);
                logger.info("Finish Get program error && Noti(AN052) Send");
            } else {
                /**Make Zip File**/
                CommonUtils.makeZipFile(jobEntity);
                // 결과 파일 업로드(2020.06.08) File encryptionFile
                new AESCryptography().encryptionFile(jobEntity);
                /**
                 * 진단결과 파일 Upload 처리
                 */
                int i = new FileManager().setResultUp(jobEntity);
                //Delete Get Script result file
                jobEntity.setJobType("GSCRPTFIN");
                INMEMORYDB.deleteFile(jobEntity, INMEMORYDB.SEND);
                logger.info("Finish Get program && Noti Send");
            }

            long duration2 = System.currentTimeMillis() - startTime;
            logger.debug("handleGS :  " + startTime + " :: " +duration2 );
            nextSetTime(Long.parseLong(jobEntity.getDelaytime()),duration2);
            //process moniter file append
            processCheck.append_End();
        }
    }

    /**
     * 3.0 자산 취약점 실행 처리
     * Hoyoung - 2020.05.31
     * @param jobEntity
     * @throws Exception
     */
    private void handleDG(JobEntity3 jobEntity) throws Exception, ZipException {
        //process moniter file append
        processCheck.append_Start(jobEntity);
        long startTime = System.currentTimeMillis();
        long duration = 0;
        String scriptDnFile = "";
        boolean diagFailedApi = false;

        //진단 수행 후 Manager에서 전송받은 진단 스크립트 파일 삭제
        if (!INMEMORYDB.isDebug) INMEMORYDB.deleteFile(jobEntity, INMEMORYDB.RECV);
        //file 수신
        scriptDnFile = receiveFile(jobEntity);

        /** 진단스크립트 파일 다운로드 처리 - 2021.01.25 , Hoyoung **/
        if(scriptDnFile.equals("")) {
            jobEntity.setFileNm("error");
            jobEntity.setAuditFileCd("error");
        } else {
            jobEntity.setFileNm(tempFileNm.substring(0, tempFileNm.length() - 4));
            jobEntity.setAuditFileCd(tempFileNm.substring(0, tempFileNm.length() - 4));
        }

        if (jobEntity.getFileNm().equals("error")) {
            params = new RequestParams().setParams("NOTI");
            jobEntity.setNotiType("AN054");
            // 진단파일 다운로드 실패 알림
            processNoti.doProcessNoti(jobEntity,params);
            logger.info("handleDG File download error : " + tempFileNm);
        } else {
            params = new RequestParams().setParams("NOTI");
            jobEntity.setNotiType("AN001");
            // 진단파일 다운로드 완료 알림
            processNoti.doProcessNoti(jobEntity ,params);
            String recvPath = "";
            recvPath = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.RECV, jobEntity.getJobType());
            // 공통으로 진단파일은 zip 으로 받는다.
            jobEntity.setFileType(INMEMORYDB.ZIP);
            //진단스크립트는 .sh 로 확장자를 붙인다. window 진단은 zip파일로 받는다.
            if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
                //File decryption
                new AESCryptography().decryptionFile(jobEntity);
                String recvZipFile = recvPath + jobEntity.getAuditFileCd() + INMEMORYDB.ZIP;
                /**Unzip File**/
                try {

                    /**Unzip File**/
                    CommonUtils.unZipFile(jobEntity);

                    boolean unck = true;
                    recvZipFile = recvPath + jobEntity.getAuditFileCd() + "_"+jobEntity.getSwType() + INMEMORYDB.ZIP;
                    unck = new UnzipUtil(recvZipFile, recvPath).unzip();
                    if(!unck) {
                        params = new RequestParams().setParams("NOTI");
                        jobEntity.setNotiType("AN051");
                        // 진단파일 실행 실패 알림
                        processNoti.doProcessNoti(jobEntity , params);
                        long duration2 = System.currentTimeMillis() - startTime;
                        nextSetTime(Long.parseLong(jobEntity.getDelaytime()),duration2);
                        //process moniter file append
                        processCheck.append_End();
                        return;
                    }

                } catch (Throwable throwable) {
                    params = new RequestParams().setParams("NOTI");
                    jobEntity.setNotiType("AN051");
                    // 진단파일 실행 실패 알림
                    processNoti.doProcessNoti(jobEntity , params);

                    long duration2 = System.currentTimeMillis() - startTime;
                    nextSetTime(Long.parseLong(jobEntity.getDelaytime()),duration2);
                    throwable.printStackTrace();
                    //process moniter file append
                    processCheck.append_End();
                }

                Thread.sleep(5);

            } else {
                //File decryption
                new AESCryptography().decryptionFile(jobEntity);
                String recvZipFile = recvPath + jobEntity.getAuditFileCd() + INMEMORYDB.ZIP;
                try {
                    //압축해제 후 파일 확장자를 .sh 로 변경
                    jobEntity.setFileType(INMEMORYDB.SH);
                    boolean unck =  new UnzipUtil(recvZipFile, recvPath).unzip();

                    logger.info("Script File Download Status Check : "+unck);
                    if(!unck) {
                        params = new RequestParams().setParams("NOTI");
                        jobEntity.setNotiType("AN051");
                        // 진단파일 실행 실패 알림
                        processNoti.doProcessNoti(jobEntity , params);
                        long duration2 = System.currentTimeMillis() - startTime;
                        nextSetTime(Long.parseLong(jobEntity.getDelaytime()),duration2);
                        //process moniter file append
                        processCheck.append_End();
                        return;
                    }
                } catch (Throwable throwable) {
                    params = new RequestParams().setParams("NOTI");
                    jobEntity.setNotiType("AN051");
                    // 진단파일 실행 실패 알림
                    processNoti.doProcessNoti(jobEntity,params);
                    throwable.printStackTrace();
                    long duration2 = System.currentTimeMillis() - startTime;
                    nextSetTime(Long.parseLong(jobEntity.getDelaytime()),duration2);
                    //process moniter file append
                    processCheck.append_End();
                }
            }
            //2019.03.27 이상준
            //CPU, Mem 을 가져오기 위한 로직 추가
            String agentCPUMax = Integer.toString(jobEntity.getAgentCpuMax());
            String agentMemoryMax = Integer.toString(jobEntity.getAgentMemMax());
            Thread resourceCheckThread = null;
            if (agentCPUMax != null && agentMemoryMax != null) {
                if (!agentCPUMax.equals("") && !agentMemoryMax.equals("")) {
                    resourceCheckThread = new Thread(new ResourceCheckManager(agentCPUMax, agentMemoryMax ,jobEntity));
                    resourceCheckThread.start();
                }
            }

            String useDiagSudo = "N";
            if (jobEntity.getUseDiagSudo() != null) {
                useDiagSudo = jobEntity.getUseDiagSudo();
                logger.debug("[handleDG] useDiagSUdo : " + useDiagSudo);
            }

            String exec = "";
            List cmd = new ArrayList();

            CommandLine cmdLine = null;

            String cdDir = "cd " + recvPath + " && ";
            boolean isDat2 = false;

            //Get Script 수행 명령어, 추후 정식 GetScript 파일이 오면 수정 필요.
            if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
                exec = recvPath + jobEntity.getAuditFileCd()+ "_"+jobEntity.getSwType()+ jobEntity.getFileType();

                cmdLine = CommandLine.parse("cmd");
                cmdLine.addArgument("/c");

                File list = new File(INMEMORYDB.DIAG_RECV_FILE_AGENT_ROOT_DIR);
                String execFilename="";
                for (int i = 0; i < list.listFiles().length; i++) {
                    if (list.listFiles()[i].getName().lastIndexOf(INMEMORYDB.BAT) > -1) {
                    //if (list.listFiles()[i].getName().lastIndexOf(".conf") > -1) {
                        execFilename = list.listFiles()[i].getName();
                        isDat2 = true;
                        break;
                    }
                }

                if (isDat2) {
                    exec = cdDir + execFilename + " " + jobEntity.getAuditFileCd() + ".conf";
                    //exec = cdDir + exec + " " + recvPath + jobEntity.getAuditFileCd() + INMEMORYDB.BAT;
                } else {
                    exec = cdDir + exec + " " + jobEntity.getAuditFileCd() + ".conf";
                }

            } else {

                try {
                    cmdLine = CommandLine.parse(makeCommandLine(useDiagSudo));
                    cmdLine.addArgument("-c");
                } catch (Exception e) {
                    //진단 수행 후 Manager에서 전송받은 Get Script jar파일 삭제
                    if(!INMEMORYDB.isDebug) INMEMORYDB.deleteFile(jobEntity,INMEMORYDB.RECV);
                    jobEntity.setJobType(jobEntity.getJobType3());
                    params = new RequestParams().setParams("NOTI");
                    jobEntity.setNotiType("AN060");
                    // 진단파일 실행 실패 알림
                    processNoti.doProcessNoti(jobEntity , params);
                    long duration2 = System.currentTimeMillis() - startTime;
                    nextSetTime(Long.parseLong(jobEntity.getDelaytime()),duration2);
                    //process moniter file append
                    processCheck.append_End();
                    logger.error(e.getMessage());
                    return;
                }
                File list = new File(INMEMORYDB.DIAG_RECV_FILE_AGENT_ROOT_DIR);

                for (int i = 0; i < list.listFiles().length; i++) {
                    if (list.listFiles()[i].getName().lastIndexOf(INMEMORYDB.PASSWORDTYPE) > -1) {
                        isDat2 = true;
                        break;
                    }
                }
                // 진단 실행 스크립트 로그 파일
                // String scriptLog = " >> /usr/local/snet/logs/NeAgent.log";
                String scriptLog = "";
                if (isDat2) {
                    exec = "LANG=C;export LANG;" + "cd " + INMEMORYDB.DIAG_RECV_FILE_AGENT_ROOT_DIR + ";" + "chmod 755 ./*; " + recvPath + jobEntity.getAuditFileCd() + INMEMORYDB.SH + " " + jobEntity.getAuditFileCd() + "_1" + INMEMORYDB.SH + " " + recvPath + jobEntity.getAuditFileCd() + INMEMORYDB.PASSWORDTYPE + scriptLog;
                } else {
                    exec = "LANG=C;export LANG;" + "cd " + INMEMORYDB.DIAG_RECV_FILE_AGENT_ROOT_DIR + ";" + "chmod 755 ./*; " + recvPath + jobEntity.getAuditFileCd() + INMEMORYDB.SH + " " + jobEntity.getAuditFileCd() + "_get" + INMEMORYDB.SH + " " + jobEntity.getAuditFileCd() + ".conf" + scriptLog;
                }
            }
            logger.debug("run command : " + exec);

            // 취약점 진단 대기 시간 변경 ( default 20Min ) , 전달 시간 사용 - hoyoung ( 2020.12.04)
            if(!jobEntity.getDgWaitTime().equals("")) {
                waitTime = 60 * 1000 * Integer.parseInt(jobEntity.getDgWaitTime());
                //waitTime = 10000 * Integer.parseInt(jobEntity.getDgWaitTime());

            }
            cmdLine.addArgument(exec, false);
            DefaultExecutor executor = new DefaultExecutor();
            logger.info("Manager settion waitTime : " + jobEntity.getDgWaitTime() +" min " +",(" +waitTime+" millisecnds)") ;
            ExecuteWatchdog watchdog = new ExecuteWatchdog(waitTime);
            executor.setWatchdog(watchdog);
            ByteArrayOutputStream outputStreamAgentStart = new ByteArrayOutputStream();
            PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(outputStreamAgentStart);

            //long wt = 10000;
            // 출력 값 대기 시간
            pumpStreamHandler.setStopTimeout(waitTime);

            executor.setStreamHandler(pumpStreamHandler);
            int exitCode = -1;
            //진단로그 파일 생성
            BufferedWriter diagLogFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(INMEMORYDB.DIAG_RESULT_FILE_AGENT_DIR + jobEntity.getAuditFileCd() + ".log"), "utf-8"));
            try {
                exitCode = executor.execute(cmdLine);
                duration = System.currentTimeMillis() - startTime;
                logger.debug("Process completed in " + duration + " millis; below is its output");
                logger.debug("exit " + exitCode);
                diagLogFile.write("Process completed in " + duration + " millis; below is its output ");
                diagLogFile.newLine();
                diagLogFile.write("Diagnosis Success !!! ");
                diagLogFile.newLine();
                params = new RequestParams().setParams("NOTI");
                jobEntity.setNotiType("AN002");
                // 진단파일 실행 알림
                processNoti.doProcessNoti(jobEntity,params);
                long duration2 = System.currentTimeMillis() - startTime;
                nextSetTime(Long.parseLong(jobEntity.getDelaytime()),duration2);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                diagLogFile.write("Diagnosis IOException Error !!!");
                diagLogFile.newLine();
                diagLogFile.write(e.getMessage());
                diagLogFile.newLine();

                diagFailedApi = true;

                long duration2 = System.currentTimeMillis() - startTime;
                nextSetTime(Long.parseLong(jobEntity.getDelaytime()),duration2);
                logger.debug("Process IOException in " + startTime + " millis; "+startTime+"  below is its output");
            } finally {
                if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
                    diagLogFile.write(new String(outputStreamAgentStart.toByteArray(), "euc-kr"));
                } else {
                    diagLogFile.write(new String(outputStreamAgentStart.toByteArray(), "utf-8"));
                }


                if (outputStreamAgentStart != null) {
                    outputStreamAgentStart.close();
                }
                if (diagLogFile != null) {
                    diagLogFile.close();
                }
            }

            Thread.sleep(200);
            if (diagFailedApi) {
                params = new RequestParams().setParams("NOTI");
                jobEntity.setNotiType("AN052");
                // 진단파일 실행 실패 알림
                processNoti.doProcessNoti(jobEntity,params);
            }


            if (watchdog.killedProcess()) {
                logger.debug("Process timed out and was killed by watchdog.(Default wait 20 min)" );
                Thread reStartThread = new Thread(new ReStartAgentManager());
                reStartThread.start();

            }
            //CPU, Mem 체크 Thread 종료
            if (resourceCheckThread != null) {
                resourceCheckThread.stop();
            }

            //진단 수행 후 Manager에서 전송받은 진단 스크립트 파일 삭제
            if (!INMEMORYDB.isDebug) INMEMORYDB.deleteFile(jobEntity, INMEMORYDB.RECV);


                /**
                 * 파일명 변경
                 */
                //진단 스크립트 수행 결과파일의 이름을 바꿔주기 위해 FileType을 .xml로 변경
                //Dignosis Script Result file Decryption
                jobEntity.setJobType(INMEMORYDB.DGFIN);
                jobEntity.setFileType(INMEMORYDB.DGRESULTTYPE);
                //jobEntity.setManagerCd(header[9]);
                jobEntity.setMessage("T1sgmanager00000");
                //jobEntity.setManagerCd(jobEntity.getManagerCd());
                Thread.sleep(200);
                INMEMORYDB.moveFileName(jobEntity, INMEMORYDB.SEND, jobEntity.getFileType());
                //INMEMORYDB.updateOTPMem(header[2],jobEntity);
                Thread.sleep(2000);

                String path = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType());
                if (!INMEMORYDB.CURRENTACCOUNT.equals(INMEMORYDB.ROOTACCOUNT)) {
                    changedOwnerBySudo(path);
                }
                /**진단결파 파일 생성 체크**/
                boolean fck = CommonUtils.getResultFileCheck(jobEntity);
                //boolean fck = true;
                logger.info("Diagnosis result FileCheck  : " + fck);

                if(!fck) {
                    // 스크립트 실행대기 시간 (waittime) 지나거나 실행 실패등의 사유로
                    // AN052를 이미 호출한 경우에 서버에 실패 코드(AN052) 호출 안함.
                    if (!diagFailedApi) {
                        /**진단결과 파일 생성 실패 및 진달 실행 처리 오류**/
                        params = new RequestParams().setParams("NOTI");
                        jobEntity.setNotiType("AN052");
                        // 진단파일 실행 실패 알림
                        processNoti.doProcessNoti(jobEntity, params);
                        logger.info("Finish Diagnosis program upload error && Noti(AN052) Send");
                    }
                } else {
                    /**Make Zip File**/
                    CommonUtils.makeZipFile(jobEntity);
                    logger.info("Diagnosis result File - makeZipFile :  " + jobEntity.getAuditFileCd());
                    // 결과 파일 업로드(2020.06.08) , File encryptionFile
                    new AESCryptography().encryptionFile(jobEntity);
                    logger.info("Diagnosis result File - Cryptography :  " + jobEntity.getAuditFileCd());
                    logger.info("Diagnosis program upload && Noti Send");
                    /**진단결과 파일 Upload 처리*/
                    int i = new FileManager().setResultUp(jobEntity);
                    logger.info("file upload result code : " + i);

                    jobEntity.setJobType("DGFIN");

                    INMEMORYDB.deleteFile(jobEntity, INMEMORYDB.SEND);
                    //Delete Get Script result file
                    logger.info("** outbound delete jobType ** : " + jobEntity.getJobType() + "-->DGFIN");
                    logger.info("Finish Diagnosis program outbound delete");
                }
                //process moniter file append
                long duration2 = System.currentTimeMillis() - startTime;
                logger.debug("handleDS :  " + startTime + " :: " +duration2 );
                nextSetTime(Long.parseLong(jobEntity.getDelaytime()),duration2);
                processCheck.append_End();


        }
    }
    /**
     * Manager로 부터 파일 수신
     * hoyoung-2020.06.15
     * @param jobEntity
     * @return
     * @throws IOException
     */
    public String receiveFile(JobEntity3 jobEntity) throws IOException {
        String recvPath = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.RECV, jobEntity.getJobType());
        logger.debug("file recv" + recvPath);
        String params = "?assetCd=" + jobEntity.getAssetCd() + "&agentCd=" + jobEntity.getAgentCd() + "&auditFileCd=" + jobEntity.getAuditFileCd() + "&jobType=" + jobEntity.getJobType3();
        String targetUrl = INMEMORYDB.FILE_DN_URI + params;
        String resp = "";
        try {
            logger.debug("file download start. :: " + targetUrl);
            resp = fileJobReq.downloadFile(targetUrl, INMEMORYDB.HTTP_AUTHVALUE, recvPath);
            tempFileNm = resp;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /**진단스크립트 파일 백업**/
            if(!resp.equals("error.err")) {
                CommonUtils.makeDiagBackupFiles(jobEntity, recvPath, resp);
                logger.debug("Script file recv success!");
            }
        }
        return resp;
    }

    // 명령 실행
    private String makeCommandLine(String useDiagSudo) throws Exception {
        // 테스트 코드
        if(false) {
            throw new Exception("authorizationInvalid" + "%" + INMEMORYDB.CURRENTACCOUNT + "%" + useDiagSudo);
        }
        String cmdLine = "";
        //에이전트의 실행계정이 일반계정인 경우
        if (!INMEMORYDB.CURRENTACCOUNT.equals(INMEMORYDB.ROOTACCOUNT)) {
            // sudo 권한으로 진단실행한다.
            if (useDiagSudo.equals("Y")) {
                cmdLine = "sudo /bin/sh";
            } else { // root 권한으로 진단실행한다.
                throw new Exception("authorizationInvalid" + "%" + INMEMORYDB.CURRENTACCOUNT + "%" + useDiagSudo);
            }
        } else { // 에이전트의 실행계정이 root인 경우
            if (useDiagSudo.equals("Y")) {
                throw new Exception("authorizationInvalid" + "%" + INMEMORYDB.CURRENTACCOUNT + "%" + useDiagSudo);
                // cmdLine = "sudo /bin/sh";
            } else { // root 권한으로 진단실행한다.
                cmdLine = "/bin/sh";
            }
        }
        return cmdLine;
    }

    private void nextSetTime(long waitTime,long durationTime) {
        long setTime;

        if(waitTime > durationTime) {
            setTime = waitTime - durationTime;
        } else {
            setTime = waitTime - (durationTime % waitTime);
        }
        setFlag = true;
        logger.debug("nextSetTime : " + waitTime +"," + durationTime +"," + setTime);
        jobEntity.setDelaytime2(Long.toString(setTime));
    }

    //
    private void changedOwnerBySudo(String path) {
        if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
            logger.info("[changedOwnerBySudo] diagnosis for Windows.");
            return;
        }

        String changedOwner = "sudo chown -R " + INMEMORYDB.CURRENTACCOUNT + ":" + INMEMORYDB.CURRENTACCOUNT + " " + path;
        logger.debug(changedOwner);

        try {
            String line;
            Process p = Runtime.getRuntime().exec(changedOwner);
            BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = stdErr.readLine()) != null) {
                logger.info("[changedOwnerBySudo] " + changedOwner + " - result : " + line);
            }
            p.waitFor();
            stdErr.close();
        } catch (IOException e) {
            logger.error("[changedOwnerBySudo] exception happened!" + e.getMessage());
        } catch (InterruptedException e) {
            logger.error("[changedOwnerBySudo] exception happened!" + e.getMessage());
        } finally {

        }

    }

    // 결과 파일 삭제 처리
    private void deleteAllFile() {
        // Delete result file.
        if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
            INMEMORYDB.deleteManualFile(INMEMORYDB.replaceDir(INMEMORYDB.AGENT_SYS_ROOT_DIR));
        }
    }

    // 장비정보 수집 1 라인 삭제 (자동)
    private void removeFirstLine(File fileName) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(fileName, "rw");
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

        } catch (Exception e) {
            logger.error("FAILED removeFirstLine Exception" + e.getMessage(), e);
        } finally {
            raf.close();
        }

    }
}
