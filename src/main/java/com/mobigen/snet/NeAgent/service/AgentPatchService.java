package com.mobigen.snet.NeAgent.service;

import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.entity.RequestParams;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import com.mobigen.snet.NeAgent.utils.UnzipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AgentPatchService {
    private static Logger logger = LoggerFactory.getLogger(AgentPatchService.class);
    private static NotiService processNoti = new NotiService();
    private static HashMap params = new HashMap();

    /**
     * 3.0 Agent 업데이트
     * Hoyoung - 2020.06.09
     *
     * @param jobEntity
     * @throws Exception
     */
    public void handleAgentUpdate(JobEntity3 jobEntity) throws Exception {
        //
        Vmanager vmanager = new Vmanager();
        FileManager fileJobReq = new FileManager();
        String tempFileNm = "";
        //file 수신
        //String recvPath = vmanager.receiveMultiFiles(verInfoMap, SocketHolder.get().dis, SocketHolder.get().dos);
        String RestURI = INMEMORYDB.FILE_DN_URI;
        String reqParams = "?assetCd=" + jobEntity.getAssetCd() + "&agentCd=" + jobEntity.getAgentCd() + "&jobType=" + jobEntity.getJobType3();
        String targetUrl = RestURI + reqParams;
        logger.info("AGENT_PATCH_PATH : " +INMEMORYDB.AGENT_PATCH_PATH );
        // 업데이트 디렉토리 확인
        File Folder = new File(INMEMORYDB.AGENT_PATCH_PATH );
        if(!Folder.exists()) {
            try {
                Folder.mkdirs(); // 폴더 생성
                logger.debug("Folder create : " +INMEMORYDB.AGENT_PATCH_PATH );
            } catch (Exception e) {
                logger.debug("Folder error : GENT_PATCH_PATH : " +INMEMORYDB.AGENT_PATCH_PATH );
                e.printStackTrace();
            }
        }

        String recvPath = fileJobReq.downloadFile(targetUrl, INMEMORYDB.HTTP_AUTHVALUE, INMEMORYDB.AGENT_PATCH_PATH);
        tempFileNm = recvPath;

        try {
            String recvZipFile = INMEMORYDB.AGENT_PATCH_PATH + jobEntity.getFileNm();
            logger.info("handleAgentUpdate : GENT_PATCH_PATH : " +recvZipFile);
            new UnzipUtil(recvZipFile, INMEMORYDB.AGENT_PATCH_PATH).unzip();
            vmanager.executeVerUp(jobEntity);
        } catch (Throwable throwable) {
            params = new RequestParams().setParams("NOTI");
            jobEntity.setNotiType("AN059");
            jobEntity.setJobType(jobEntity.getJobType3());
            processNoti.doProcessNoti(jobEntity,params);
            throwable.printStackTrace();
        }


    }

}
