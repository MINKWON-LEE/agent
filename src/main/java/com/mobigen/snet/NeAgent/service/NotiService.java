package com.mobigen.snet.NeAgent.service;

import com.mobigen.snet.NeAgent.crypto.AESCryptography;
import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.entity.RequestParams;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import com.mobigen.snet.NeAgent.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

public class NotiService {
    private static Logger logger = LoggerFactory.getLogger(NotiService.class);
    /**
     * Agent 상태 알림
     * hoyoung-2020.06.15
     * @param jobEntity
     * @return
     * @throws IOException
     */
    public void doProcessNoti(JobEntity3 jobEntity, HashMap params) throws Exception {
        HttpManager httpJobReq = new HttpManager();
        HttpsManager httpsJobReq = new HttpsManager();
        HashMap headers = new HashMap();
        try {
            // 수집 or 진단 실패일 때 현재까지 진행되어 있는 로그파일만 업로드.
            if (jobEntity.getNotiType().equals("AN052")) {
                String prev = jobEntity.getJobType();
                jobEntity.setJobType(INMEMORYDB.DGFIN);
                uploadLogFileOnly(jobEntity);
                jobEntity.setJobType(prev);
            }

            // 작업 진단 요청
            String methond = "GET";
            String resp = "";
            headers = new RequestParams().setHeader();
            params.put("auditFileCd", jobEntity.getAuditFileCd());
            params.put("notiType", jobEntity.getNotiType());
            params.put("jobType", jobEntity.getJobType3());
            if(INMEMORYDB.REST_PROTOCOL.equals("HTTP")) {
                resp = httpJobReq.httpUrlConnection(methond, INMEMORYDB.NOTI_URI, params, headers, true, false);
            } else {
                resp = httpsJobReq.httpsUrlConnection(methond, INMEMORYDB.NOTI_URI, params, headers, true, false);
            }

            logger.info( "doProcess Noti >> targetUrl= " + INMEMORYDB.NOTI_URI + "::" + params.toString() + ", " + resp.toString());
        } catch (Exception e) {
            logger.info("doProcess Noti Exception : " + e.getMessage());
            e.printStackTrace();
        } finally {

        }

    }

    /**
     *
     */
    public void uploadLogFileOnly(JobEntity3 jobEntity) {

        String fileNm = "";
        boolean logExist = false;
        try {
            fileNm = CommonUtils.getLastLogFileCheck(jobEntity);
            if (fileNm.indexOf(INMEMORYDB.LOG) > -1) {
                logExist = true;
            }

            if (logExist) {
                logger.debug("==== Diagnosis Log File Only - Exist ==== : OK ");
                CommonUtils.makeZipFile(jobEntity);
                logger.info("Diagnosis Log File Only - MakeZipFile :  " + jobEntity.getAuditFileCd());
                new AESCryptography().encryptionFile(jobEntity);
                logger.info("Diagnosis Log File Only - Cryptography :  " + jobEntity.getAuditFileCd());

                String prevJobType = jobEntity.getJobType3();
                jobEntity.setJobType3("AJ154");
                new  FileManager().setResultUp(jobEntity);
                logger.debug("====Diagnosis Upload Log File Only ==== : log file upload success..");
                INMEMORYDB.deleteFile(jobEntity, INMEMORYDB.SEND);
                logger.debug("====Diagnosis Upload Log File Only ==== : All File Deleted.. ");
                jobEntity.setJobType3(prevJobType);
            } else {
                logger.debug("==== makeZipFile Log result ==== : FALSE ");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {

        }
    }
}
