package com.mobigen.snet.NeAgent.entity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

public class ResponseEntity {
    private static Logger logger = LoggerFactory.getLogger(ResponseEntity.class);
    /**
     * HTTP 응답 처리
     * Hoyoung - 2020.06.02
     * job 스케줄링 요청 (장비정보 수집,진단실행,
     */
    public JobEntity3 jobsRespDate(String resp) throws IOException {
        JSONParser parse = new JSONParser();
        JobEntity3 je3 = new JobEntity3();
        int ss = 0;
        try {
            JSONObject jobj = (JSONObject) parse.parse(resp);
            // response 가져오기
            Iterator iterator = jobj.keySet().iterator();
            while(iterator.hasNext()) {
                String key = (String) iterator.next();
                //logger.info("=== result 2===key :: " + key +","+jobj.get(key).toString());
                if(key.equals("result")) {
                    je3.setResult(String.valueOf(jobj.get(key)));   // 요청결과 코드 (200 : ok)
                } else if(key.equals("timestamp")) {
                    je3.setTimestamp((String) jobj.get(key));       // 요청결과 응답시간
                } else if(key.equals("delaytime")) {
                    je3.setDelaytime((String) jobj.get(key));       // 재요청 간견
                } else if(key.equals("assetCd")) {
                    je3.setAssetCd((String) jobj.get(key));         // 요청 장비코드
                } else if(key.equals("agentCd")) {
                    je3.setAgentCd((String) jobj.get(key));         // 요청 장비 에이전트 코드
                } else if(key.equals("jobType")) {
                    ss++;
                    je3.setJobType((String) jobj.get(key));         // 응답 처리 타입
                } else if(key.equals("managerCd")) {
                    je3.setManagerCd((String) jobj.get(key));       // 응답 메니저 서버 코드
                } else if(key.equals("message")) {
                    je3.setMessage((String) jobj.get(key));         // 요청결과 메시지
                } else if(key.equals("jobData")) {
                        String jobDataObj = jobj.get("jobData").toString();
                        JSONObject resultData = (JSONObject) parse.parse(jobDataObj);
                        Iterator iterator2 = resultData.keySet().iterator();
                        while(iterator2.hasNext()) {
                            String key2 = (String) iterator2.next();
                            if(key2.equals("fileNm")) je3.setFileNm((String) resultData.get(key2));                 // 파일명
                            if(key2.equals("delaytime")) je3.setDelaytime((String) resultData.get(key2));           // 다음 요청 대기시간
                            if(key2.equals("auditFileCd")) je3.setAuditFileCd((String) resultData.get(key2));       // 응답 파일코드
                            if(key2.equals("auditFileNm")) je3.setAuditFileNm((String) resultData.get(key2));       // 응답 파일명
                            if(key2.equals("agentCpuMax")) je3.setAgentCpuMax(Integer.parseInt(resultData.get(key2).toString()));       // Agent CPU 임계값
                            if(key2.equals("agentMemMax")) je3.setAgentMemMax(Integer.parseInt(resultData.get(key2).toString()));       // Agent MEM 임계값
                            if(key2.equals("useDiagSudo")) je3.setUseDiagSudo((String) resultData.get(key2));       // Agent SUDO 실행 플래그
                            if(key2.equals("swType")) je3.setSwType((String) resultData.get(key2));                 // 점검군
                            if(key2.equals("dgWaitTime")) je3.setDgWaitTime((String) resultData.get(key2));         // 취약점 점검 실행대기 시간 - 2020.12.02
                            if(key2.equals("version"))  je3.setNewVerStr((String) resultData.get(key2));            // Agent 패치 버전
                            if(key2.equals("diagInfoUse"))  je3.setDiagInfoUse((String) resultData.get(key2));            // Agent 패치 버전
                        }
                    }
            }
        } catch (ParseException e) {
            logger.debug("jobsRespDate Error :" + e.getLocalizedMessage() + ")");
            e.printStackTrace();
        } catch (Exception e) {
            logger.debug("jobsRespDate Exception Error :" + e + ")");
            //e.printStackTrace();
        } finally {

        }
        return je3;
    }
}
