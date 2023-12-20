package com.mobigen.snet.NeAgent.entity;

import java.io.File;
import java.util.Arrays;

/**
 * Created by hoyoung on 2020. 6. 05
 */


public class JobEntity3 {


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDgWaitTime() {
        return dgWaitTime;
    }

    public void setDgWaitTime(String dgWaitTime) {
        this.dgWaitTime = dgWaitTime;
    }

    String dgWaitTime;      // 취약점 진댄 대기 시간 설정

    String result;          // 응답코드
    String message;         // 응답메시지
    String timestamp;       // Manager 응답 시간
    String delaytime;       // 다은 요청 대기 시간

    public String getDelaytime2() {
        return delaytime2;
    }

    public void setDelaytime2(String delaytime2) {
        this.delaytime2 = delaytime2;
    }

    String delaytime2;      // 진단수행후 대기 요청 시간
    String assetCd;         // 장비코드
    String agentCd;         // 에이전트 코드
    String managerCd;       // 메니저 코드
    String jobType;         // 작업코드
    String jobType3;        // 작업코드

    public String getNotiType() {
        return notiType;
    }

    public void setNotiType(String notiType) {
        this.notiType = notiType;
    }

    String notiType;        // 진행상태 코드

    String jobData;         // 상세 정보
    String auditFileCd;     // 파일코드
    String fileNm;          // 출력 파일명
    String auditFileNm;     // Manager 로부터 전달 받은 파일명
    String fileName;          //난수 값으로 생성된 파일 명.

    public String getSwType() {
        return swType;
    }

    public void setSwType(String swType) {
        this.swType = swType;
    }

    String swType;          // 점검군

    //for multiple file transfer
    File[] multiFiles;
    String[] multiFilesNames; //[fileName]
    String[] multiFilesSize; //[file byte size]
    String newVerStr;
    int fileTransferCount;

    String category;        //OS,DB,WAS 등 어떤 진단인지 파악.
    int agentCpuMax;        // Agent CPU 입계치값
    int agentMemMax;        // Agent MEM 임계치값
    String useDiagSudo;        // Agent Sudo 사용 여부

    String diagInfoUse;     // 수집/진단 분리 사용 여부


    public String toString() {
        return "JobEntity3{" +
                "result=" + result +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", delaytime='" + delaytime + '\'' +
                ", assetCd='" + assetCd + '\'' +
                ", agentCd='" + agentCd + '\'' +
                ", managerCd='" + managerCd + '\'' +
                ", jobType='" + jobType + '\'' +
                ", jobType3='" + jobType3 + '\'' +
                ", jobData='" + jobData + '\'' +
                ", auditFileCd='" + auditFileCd + '\'' +
                ", fileNm='" + fileNm + '\'' +
                ", auditFileNm='" + auditFileNm + '\'' +
                ", fileName='" + fileName + '\'' +
                ", multiFiles=" + (multiFiles == null ? null : Arrays.asList(multiFiles)) +
                ", multiFilesNames=" + (multiFilesNames == null ? null : Arrays.asList(multiFilesNames)) +
                ", multiFilesSize=" + (multiFilesSize == null ? null : Arrays.asList(multiFilesSize)) +
                ", newVerStr='" + newVerStr + '\'' +
                ", fileTransferCount=" + fileTransferCount +
                ", category='" + category + '\'' +
                ", agentCpuMax=" + agentCpuMax +
                ", agentMemMax=" + agentMemMax +
                ", useDiagSudo='" + useDiagSudo + '\'' +
                ", agentInfo=" + agentInfo +
                ", dgWaitTime=" + dgWaitTime +
                ", fileType='" + fileType + '\'' +
                ", diagInfoUse='" + diagInfoUse + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDelaytime() {
        return delaytime;
    }

    public void setDelaytime(String delaytime) {
        this.delaytime = delaytime;
    }

    public String getAssetCd() {
        return assetCd;
    }

    public void setAssetCd(String assetCd) {
        this.assetCd = assetCd;
    }

    public String getAgentCd() {
        return agentCd;
    }

    public void setAgentCd(String agentCd) {
        this.agentCd = agentCd;
    }

    public String getManagerCd() {
        return managerCd;
    }

    public void setManagerCd(String managerCd) {
        this.managerCd = managerCd;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getJobData() {
        return jobData;
    }

    public void setJobData(String jobData) {
        this.jobData = jobData;
    }

    public String getAuditFileCd() {
        return auditFileCd;
    }

    public void setAuditFileCd(String auditFileCd) {
        this.auditFileCd = auditFileCd;
    }

    public String getFileNm() {
        return fileNm;
    }

    public void setFileNm(String fileNm) {
        this.fileNm = fileNm;
    }

    public String getAuditFileNm() {
        return auditFileNm;
    }

    public void setAuditFileNm(String auditFileNm) {
        this.auditFileNm = auditFileNm;
    }

    public int getAgentCpuMax() {
        return agentCpuMax;
    }

    public void setAgentCpuMax(int agentCpuMax) {
        this.agentCpuMax = agentCpuMax;
    }

    public int getAgentMemMax() {
        return agentMemMax;
    }

    public void setAgentMemMax(int agentMemMax) {
        this.agentMemMax = agentMemMax;
    }


    public String getUseDiagSudo() {
        return useDiagSudo;
    }

    public void setUseDiagSudo(String useDiagSudo) {
        this.useDiagSudo = useDiagSudo;
    }




    public String getJobType3() {
        return jobType3;
    }

    public void setJobType3(String jobType3) {
        this.jobType3 = jobType3;
    }


    public File[] getMultiFiles() {
        return multiFiles;
    }

    public void setMultiFiles(File[] multiFiles) {
        this.multiFiles = multiFiles;
    }

    public String[] getMultiFilesNames() {
        return multiFilesNames;
    }

    public void setMultiFilesNames(String[] multiFilesNames) {
        this.multiFilesNames = multiFilesNames;
    }

    public String[] getMultiFilesSize() {
        return multiFilesSize;
    }

    public void setMultiFilesSize(String[] multiFilesSize) {
        this.multiFilesSize = multiFilesSize;
    }

    public String getNewVerStr() {
        return newVerStr;
    }

    public void setNewVerStr(String newVerStr) {
        this.newVerStr = newVerStr;
    }

    public void setDiagInfoUse(String diagInfoUse) {
        this.diagInfoUse = diagInfoUse;
    }

    public String getDiagInfoUse() { return diagInfoUse; }

    public AgentInfo getAgentInfo() {
        return agentInfo;
    }

    public void setAgentInfo(AgentInfo agentInfo) {
        this.agentInfo = agentInfo;
    }



    public int getFileTransferCount() {
        return fileTransferCount;
    }

    public void setFileTransferCount(int fileTransferCount) {
        this.fileTransferCount = fileTransferCount;
    }

    AgentInfo agentInfo;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    String fileType;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}
