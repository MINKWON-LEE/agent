package com.mobigen.snet.NeAgent.entity;

import com.mobigen.snet.NeAgent.memory.INMEMORYDB;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class RequestParams {

    public HashMap setHeader() {
        HashMap headers = new HashMap();
        // Request Header 인증 key 요청
        headers.put("Authorization", INMEMORYDB.HTTP_AUTHVALUE);
        return headers;
    }
    public HashMap setParams(String reqType) throws UnsupportedEncodingException {
        HashMap params = new HashMap();
        if(reqType.equals("AUTH")) {
            // 인증 요청
            params.put("ip", INMEMORYDB.AGENT_IPADDR);
            params.put("os", URLEncoder.encode(INMEMORYDB.osType,"utf-8"));
            params.put("hostName",URLEncoder.encode(INMEMORYDB.AGENT_HOSTNAME,"utf-8"));
            params.put("assetCd", INMEMORYDB.ASSETCD);
        } else if(reqType.equals("JOBS")) {
            // 작업 요청
            params.put("version", INMEMORYDB.AGENT_VER);
            params.put("assetCd", INMEMORYDB.ASSETCD);
            params.put("agentCd", INMEMORYDB.AGENTCD);
        } else if(reqType.equals("NOTI")) {
            // 알림 요청
            params.put("assetCd", INMEMORYDB.ASSETCD);
            params.put("agentCd", INMEMORYDB.AGENTCD);
        } else {

        }
        return params;
    }
}

