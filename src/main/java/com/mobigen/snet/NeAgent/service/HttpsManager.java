package com.mobigen.snet.NeAgent.service;

import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpsManager {

    private static Logger logger = LoggerFactory.getLogger(HttpsManager.class);
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";

    public static final int BUFFER_SIZE = 1024;



    public String httpsUrlConnection(String getpost, String targetUrl, Map params) throws Exception {
        String returnText = this.httpsUrlConnection(getpost, targetUrl, params, null, false,false);
        return returnText;
    }

    public String httpsUrlConnection(String getpost, String targetUrl, Map params, boolean isJson, boolean isFile) throws Exception {
        String returnText = this.httpsUrlConnection(getpost, targetUrl, params, null, isJson,isFile);
        return returnText;
    }

    private String sample(String getpost, String targetUrl, Map params, HashMap headers, boolean isJson, boolean isFile) throws Exception {
        String returnText = httpsUrlConnection(getpost, targetUrl, params, null, isJson, isFile);
        return returnText;
    }

    public static TrustManager[] createTrustManagers() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
            }

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};
        return trustAllCerts;
    }

    public String httpsUrlConnection(String getpost, String targetUrl, Map params, Map header, boolean isJson, boolean isFile) throws Exception {

        URL url = null;
        HttpsURLConnection conn = null;
        String USER_AGENT = "Mozilla/5.0";
        // JDK 1.4 기준 타입아웃 설정
        System.setProperty("sun.net.client.defaultConnectTimeout", INMEMORYDB.CONNECTTIMEOUT);
        System.setProperty("sun.net.client.defaultReadTimeout",INMEMORYDB.READTIMEOUT);
        String jsonData = "";
        BufferedReader br = null;
        StringBuffer sb = null;
        String returnText = "";
        JSONObject jobj = null;
        JSONParser parse = new JSONParser();


        String postParams = "";
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            // Request Method
            if(getpost.equalsIgnoreCase(POST) || getpost.equalsIgnoreCase(DELETE)){
                url = new URL(targetUrl);
            } else if(getpost.equalsIgnoreCase(GET)){
                url = new URL(targetUrl + ((params!=null)?"?"+makeParams(params):""));
            }
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, createTrustManagers(), new java.security.SecureRandom());
                conn.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HostnameVerifier allHostsValid = (hostname, session) -> true;
                conn.setDefaultHostnameVerifier(allHostsValid);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // HTTPS connnection
            conn = (HttpsURLConnection) url.openConnection();

            if(header != null){
                Iterator iter = header.entrySet().iterator();
                while(iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    conn.setRequestProperty(entry.getKey().toString(), entry.getValue().toString());
                    logger.debug("header key : " + entry.getKey() + " / value : " + entry.getValue());
                }
            }
            //add reuqest header
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            if(isJson){
                conn.setRequestProperty("Content-Type", "application/json");               ;
                conn.setRequestProperty("Authorization", INMEMORYDB.HTTP_AUTHVALUE);
            }
            conn.setRequestMethod(getpost);
            conn.setDoOutput(true);
            if(isFile) {
                String disposition = conn.getHeaderField("Content-Disposition");
                logger.debug("http header...." + conn.getHeaderField("Content-Disposition"));
                inputStream = conn.getInputStream();
                String saveFilePath = "";
                String fileName = "";

                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,disposition.length() - 1);
                    }
                } else {
                    // extracts file name from URL
                    //fileName = targetUrl.substring(targetUrl.lastIndexOf("/") + 1,targetUrl.length());
                    fileName = "error.err";
                }

                saveFilePath = INMEMORYDB.GETSCRIPT_RECV_FILE_AGENT_ROOT_DIR + "/"+fileName;
                logger.debug(saveFilePath);
                outputStream = new FileOutputStream(saveFilePath);
                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer,0,bytesRead);
                }
                logger.info("Written successfully");
                outputStream.close();
                inputStream.close();
                returnText = "{\"message\":\""+fileName+"\",\"result\":200}";
                logger.info("File downloaded");
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                sb = new StringBuffer();
                while((jsonData = br.readLine()) != null){
                    sb.append(jsonData);

                }
                returnText = sb.toString();
            }
            int status = conn.getResponseCode();
            jobj = (JSONObject) parse.parse(returnText);

        } catch (IOException e){
            logger.debug("exception in httpurlconnection ! ", e);
            returnText = "{\"message\":\"connection refused \",\"result\":500}";
            jobj = (JSONObject) parse.parse(returnText);
        } finally {

            try {
                if (br != null) br.close();
            } catch(Exception e){
                logger.warn("finally..br.close()", e);
            }
            br = null;
            try {
                if(conn!=null)
                    conn.disconnect();
            } catch(Exception e){
                logger.warn("finally..conn.disconnect()", e);
            }
            conn = null;
        }
        return jobj != null ? jobj.toString() : null;
    }

    /**
     * Manager 연동 요청 Message 처리
     * 2021.06.24 - Hoyoung
     * @param params
     * @return String
     */
    private String makeParams(Map params){
        String param = null;
        StringBuffer sb = new StringBuffer();

        if(params != null){
            Iterator item = params.entrySet().iterator();
            while(item.hasNext()) {
                Map.Entry entry = (Map.Entry) item.next();
                // logger.info(" key : " + key + " / value : " + params.get(key));
                sb.append(entry.getKey()).append("=").append((entry.getValue()==null?"":entry.getValue()).toString().trim()).append("&");
            }
        }
        param = sb.toString().substring(0, sb.toString().length()-1);
        return param;
    }


    /**
     * Manager 연동 응답 Message 처리
     * 2021.06.24 - Hoyoung
     * @param params
     * @return String
     * @throws UnsupportedEncodingException
     */
    private String makeJsonParams(Map params) throws UnsupportedEncodingException {
        String param = null;
        StringBuffer sb = new StringBuffer();
        int i = 0;
        if(params != null){
            Iterator item = params.entrySet().iterator();
            while(item.hasNext()) {
                Map.Entry entry = (Map.Entry) item.next();
                logger.debug(" key : " + entry.getKey() + " / value : " + entry.getValue());
                sb.append(entry.getKey()).append("=").append((entry.getValue()==null?"": URLEncoder.encode(entry.getValue().toString().trim(),"UTF-8"))).append("&");
            }
        }
        param = sb.toString().substring(0, sb.toString().length()-1);
        return param;
    }

}
