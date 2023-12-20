package com.mobigen.snet.NeAgent.service;

import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.entity.RequestParams;
import com.mobigen.snet.NeAgent.entity.ResponseEntity;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import com.mobigen.snet.NeAgent.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.ConnectException;
import java.util.HashMap;

public class AgentAuthService {
    private static Logger logger = LoggerFactory.getLogger(AgentAuthService.class);

    /**
     * 3.0 인증 처리 기능
     * hoyoung - 2020.06.16
     *
     * @throws Exception
     */
    public boolean doAuth() throws Exception {
        logger.info("Agent Auth request");
        HashMap headers;
        HashMap params;
        HttpManager httpJobReq = new HttpManager();
        HttpsManager httpsJobReq = new HttpsManager();
        ResponseEntity responseData = new ResponseEntity();
        JobEntity3 jobEntity = null;
        boolean result = false;
        String resp = "";
        int delCnt = 0; // AC 삭제 파일수
        try {

            logger.info("=>>>>>>>>>>> " + INMEMORYDB.AUTH_URI);
            // 작업 진단 요청
            String methond = "GET";
            headers = new RequestParams().setHeader();
            params = new RequestParams().setParams("AUTH");
            logger.info("=>>>>>>>>>>> " + params.toString());
            // 요청 Protocol 확인
            if(INMEMORYDB.REST_PROTOCOL.equals("HTTP")) {
                resp = httpJobReq.httpUrlConnection(methond, INMEMORYDB.AUTH_URI, params, headers, true, false);
            } else {
                resp = httpsJobReq.httpsUrlConnection(methond, INMEMORYDB.AUTH_URI, params, headers, true, false);
            }
            logger.info("=>>>>>>>>>>> " +INMEMORYDB.REST_PROTOCOL);

            // https 요청

            jobEntity = responseData.jobsRespDate(resp);
            if(jobEntity.getResult().equals("200")) {
                result = true;
                // 인증 성공 AGENT CD 적용
                INMEMORYDB.AGENTCD = jobEntity.getAgentCd();
                INMEMORYDB.ASSETCD = jobEntity.getAssetCd();
                // 인증 코드 properties 등록
                INMEMORYDB.reloadAuthDB();
                // 인증 성공 후 AC 파일 삭제
                delCnt = INMEMORYDB.deleteACfileList();
                logger.info("Agent Auth success AC000000.DAT delete File Cnt : " +delCnt);
            } else {
                jobEntity.setResult("auth.fail");
            }
            logger.debug(DateUtil.getCurTimeInDate() + " =targetUrl= " + INMEMORYDB.AUTH_URI + "::" + params.toString() + "::" + resp.toString());
        } catch (ConnectException e) {
            logger.info("Agent Auth ConnectException : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Agent Auth Exception : " + e.getMessage());
        } finally {
            //logger.info("Agent Auth Status : " + jobEntity.getResult());
            // 백업 디렉토리 확인
            String backup = INMEMORYDB.jobTypeAbsolutePath("BACKUP", "");
            String backupdg = INMEMORYDB.jobTypeAbsolutePath("BACKUPDG", "");
            String backupget = INMEMORYDB.jobTypeAbsolutePath("BACKUPGET", "");

            File Folder = new File(backup);
            if(!Folder.exists()) {
                try {
                    Folder.mkdirs(); // 폴더 생성
                    logger.debug("Folder create : " +backup);
                } catch (Exception e) {
                    logger.debug("Folder error : backup : " +backup);
                    e.printStackTrace();
                }
            }
            File Folder2 = new File(backupdg);
            if(!Folder2.exists()) {
                try {
                    Folder2.mkdirs(); // 폴더 생성
                    logger.debug("Folder create : " +backupdg);
                } catch (Exception e) {
                    logger.debug("Folder error : backupdg : " +backupdg);
                    e.printStackTrace();
                }
            }
            File Folder3 = new File(backupget);
            if(!Folder3.exists()) {
                try {
                    Folder3.mkdirs(); // 폴더 생성
                    logger.debug("Folder create : " +backupget);
                } catch (Exception e) {
                    logger.debug("Folder error : backupget : " +backupget);
                    e.printStackTrace();
                }
            }

        }
        return result;
    }
}
