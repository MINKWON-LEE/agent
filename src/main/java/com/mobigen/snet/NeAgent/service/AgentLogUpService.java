package com.mobigen.snet.NeAgent.service;

import com.mobigen.snet.NeAgent.crypto.AESCryptography;
import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import com.mobigen.snet.NeAgent.utils.CommonUtils;
import com.mobigen.snet.NeAgent.utils.DateUtil;
import com.mobigen.snet.NeAgent.utils.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

public class AgentLogUpService {
    private static Logger logger = LoggerFactory.getLogger(AgentLogUpService.class);
    /**
     * Log 파일 다운로드는 하나의 세션에서 동기방식으로 다운받는다.
     * Hoyoung - 2020.06.18
     * @param jobEntity
     * @throws Exception
     */
    public void getLog(JobEntity3 jobEntity) throws Exception {

        //String assetCd = header[10];
        String assetCd = INMEMORYDB.ASSETCD;
        logger.debug("Send Log File [" + assetCd + "]");

        String logFullPath = "";
        if (INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID) > -1) {
            logFullPath = INMEMORYDB.AGENT_SYS_LOG_ROOT_DIR_WIN2 + "NeAgent.log";
        } else {
            logFullPath = INMEMORYDB.LOG_FILE_AGENT_ROOT_DIR + "NeAgent.log";
        }

        logger.debug("log File Path ==>  " + logFullPath);
        String copyPath = INMEMORYDB.DIAG_RESULT_FILE_AGENT_DIR + DateUtil.getCurrDateByHour() + "_" + assetCd + "_NeAgent.log";
        jobEntity.setJobType(INMEMORYDB.AGENTLOGFIN);
        jobEntity.setFileType(INMEMORYDB.ZIP);
        jobEntity.setAuditFileCd(jobEntity.getFileNm());
        CommonUtils.fileCopy(logFullPath, copyPath);

        ArrayList files = new ArrayList();
        files.add(new File(copyPath));

        /**Make Zip File**/
        new ZipUtil().makeZip(files, jobEntity);

        File[] file;


        file = new AESCryptography().encryptionFile(jobEntity);
        //sendFile(file,jobEntity);
        //파일 전달
        logger.debug("Send Log File");
        /**
         * Log 파일 Upload 처리
         */
        jobEntity.setJobType("AJ300");
        int i = new FileManager().setResultUp(jobEntity);
        logger.debug("file upload result code : " + i);
    }
}
