package com.mobigen.snet.NeAgent.service;

import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class FileManager  {
    //private static final int BUFFER_SIZE = 4096;
    private static Logger logger = LoggerFactory.getLogger(FileManager.class);
    private static final int maxBufferSize = 1024;


    // SSL 인증서 무시 처리
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



    public static String downloadFile(String fileURL, String header, String saveDir) throws IOException {
        // JDK 1.4 기준 타입아웃 설정
        System.setProperty("sun.net.client.defaultConnectTimeout",INMEMORYDB.CONNECTTIMEOUT);
        System.setProperty("sun.net.client.defaultReadTimeout",INMEMORYDB.READTIMEOUT);
        URL url = new URL(fileURL);
        String fileName = "";
        int responseCode = 0;
        String saveFilePath = "";
        HttpsURLConnection httpsConn = null;
        HttpURLConnection httpConn = null;

        if(INMEMORYDB.REST_PROTOCOL.equals("HTTP")) {
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestProperty("Authorization", header);
            responseCode = httpConn.getResponseCode();
        } else {
            httpsConn = (HttpsURLConnection) url.openConnection();
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, createTrustManagers(), new java.security.SecureRandom());
                httpsConn.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HostnameVerifier allHostsValid = (hostname, session) -> true;
                httpsConn.setDefaultHostnameVerifier(allHostsValid);
            } catch (Exception e) {
                e.printStackTrace();
            }

            httpsConn.setRequestProperty("Authorization", header);
            responseCode = httpsConn.getResponseCode();
        }
        logger.debug("fileURL : " + fileURL +" == :"+ responseCode + "," + header);
        // always check HTTP response code first
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String disposition = null;
        String contentType = null;
        int contentLength = 0;
        try {
            if (INMEMORYDB.REST_PROTOCOL.equals("HTTP")) {
                responseCode = HttpURLConnection.HTTP_OK;
                disposition = httpConn.getHeaderField("Content-Disposition");
                contentType = httpConn.getContentType();
                contentLength = httpConn.getContentLength();
            } else {
                responseCode = HttpsURLConnection.HTTP_OK;
                disposition = httpsConn.getHeaderField("Content-Disposition");
                contentType = httpsConn.getContentType();
                contentLength = httpsConn.getContentLength();
            }

            if (responseCode == HttpURLConnection.HTTP_OK ) {
                if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10, disposition.length() - 1);
                    }
                } else {
                    // extracts file name from URL
                    //fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,fileURL.length());
                    fileName="error.err";
                }

                if (INMEMORYDB.REST_PROTOCOL.equals("HTTP")) {
                    // opens input stream from the HTTP connection
                    inputStream = httpConn.getInputStream();
                } else {
                    // opens input stream from the HTTP connection
                    inputStream = httpsConn.getInputStream();
                }
                saveFilePath = saveDir + File.separator + fileName;

                // opens an output stream to save into file
                outputStream = new FileOutputStream(saveFilePath);

                int bytesRead = -1;
                byte[] buffer = new byte[maxBufferSize];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                logger.info("file to download : " +fileName );
                outputStream.close();
                inputStream.close();
            } else {
                fileName="error.err";
                logger.info("No file to download. Server replied HTTP code: " + responseCode);
            }

        } catch(Exception e) {
            fileName="error.err";
            logger.debug("No file to download. Exception: " + responseCode);
        } finally {
            if (INMEMORYDB.REST_PROTOCOL.equals("HTTP")) {
                // opens input stream from the HTTP connection
                httpConn.disconnect();
            } else {
                // opens input stream from the HTTP connection
                httpsConn.disconnect();
            }
            outputStream.close();
            inputStream.close();

        }
        return fileName;
    }
    /**
     * 장비정보 수집 및 진단결과 파일 업로드
     * @param jobEntity
     * @return
     * @throws IOException
     */
    public int setResultUp(JobEntity3 jobEntity)  throws IOException {
        if(INMEMORYDB.REST_PROTOCOL.equals("HTTPS")) {
            return setResultUpHttps(jobEntity);
        }
        String boundary;
        String tail;
        final String LINE_END = "\r\n";
        final String TWOHYPEN = "--";
        HttpURLConnection httpConn;
        String charset = "UTF-8";
        PrintWriter writer;
        OutputStream outputStream;
        final String TAG = "MultipartUtility";
        //int maxBufferSize = 1024;
        //ProgressListener progressListener;
        long startTime;
        int status = 0;

        String requestURL = INMEMORYDB.FILE_UP_URI;
        boundary = "---" + System.currentTimeMillis() + "===";
        tail = LINE_END + TWOHYPEN + boundary + TWOHYPEN + LINE_END;
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("Authorization", INMEMORYDB.HTTP_AUTHVALUE);

        String paramsPart = "";
        String fileHeader = "";
        String filePart = "";
        long fileLength = 0;
        startTime = System.currentTimeMillis();

        //ArrayList<String> paramHeaders = new ArrayList<>();
        ArrayList paramHeaders = new ArrayList();
        String param = TWOHYPEN + boundary + LINE_END
                + "Content-Disposition: form-data; name=\"assetCd\"" + LINE_END
                + "Content-Type: text/plain; charset=" + charset + LINE_END
                + LINE_END
                + jobEntity.getAssetCd() + LINE_END;
        paramsPart += param;
        paramHeaders.add(param);
        String param2 = TWOHYPEN + boundary + LINE_END
                + "Content-Disposition: form-data; name=\"agentCd\"" + LINE_END
                + "Content-Type: text/plain; charset=" + charset + LINE_END
                + LINE_END
                + jobEntity.getAgentCd() + LINE_END;
        paramsPart += param2;
        paramHeaders.add(param2);
        String param3 = TWOHYPEN + boundary + LINE_END
                + "Content-Disposition: form-data; name=\"jobType\"" + LINE_END
                + "Content-Type: text/plain; charset=" + charset + LINE_END
                + LINE_END
                + jobEntity.getJobType3() + LINE_END;
        paramsPart += param3;
        paramHeaders.add(param3);

        String param4 = TWOHYPEN + boundary + LINE_END
                + "Content-Disposition: form-data; name=\"files\"" + LINE_END
                + "Content-Type: text/plain; charset=" + charset + LINE_END
                + LINE_END
                + jobEntity.getAuditFileCd()+".des" + LINE_END;
        paramsPart += param4;
        paramHeaders.add(param4);

        ArrayList filesAL = new ArrayList();
        ArrayList fileHeaders = new ArrayList();
        String targetFileNm = "";
        String path = "";
        if(jobEntity.getJobType3().equals("AJ100") || jobEntity.getJobType3().equals("AJ101") || jobEntity.getJobType3().equals("AJ102") || jobEntity.getJobType3().equals("AJ154")) {
            targetFileNm = jobEntity.getAuditFileCd()+".des";
            path=INMEMORYDB.DIAG_RESULT_FILE_AGENT_DIR;
        } else if(jobEntity.getJobType3().equals("AJ200")) {
            targetFileNm = jobEntity.getAuditFileCd()+".des";
            path=INMEMORYDB.GETSCRIPT_RESULT_FILE_AGENT_DIR;
        } else if(jobEntity.getJobType3().equals("AJ201"))  {
            targetFileNm = jobEntity.getAuditFileCd()+".dat";
            path=INMEMORYDB.GETSCRIPT_RESULT_FILE_AGENT_DIR;
        } else if(jobEntity.getJobType3().equals("AJ300")) {
            targetFileNm = jobEntity.getAuditFileCd()+".des";
            path=INMEMORYDB.DIAG_RESULT_FILE_AGENT_DIR;
        }
        logger.debug(path+targetFileNm);
        File file = new File(path+targetFileNm);
        fileHeader = TWOHYPEN + boundary + LINE_END
                + "Content-Disposition: form-data; name=\"files\"; filename=\"" + targetFileNm + "\"" + LINE_END
                + "Content-Type: " + URLConnection.guessContentTypeFromName(file.getAbsolutePath()) + LINE_END
                + "Content-Transfer-Encoding: binary" + LINE_END
                + LINE_END;
        fileLength += file.length() + LINE_END.getBytes(charset).length;
        filePart += fileHeader;

        fileHeaders.add(fileHeader);
        filesAL.add(file);

        String partData = paramsPart + filePart;

        long requestLength = partData.getBytes(charset).length + fileLength + tail.getBytes(charset).length;
        httpConn.setRequestProperty("Content-length", "" + requestLength);
//        /httpConn.setFixedLengthStreamingMode((int) requestLength);
        httpConn.connect();

        outputStream = new BufferedOutputStream(httpConn.getOutputStream());
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

        for (int i =0; i < paramHeaders.size(); i++) {
            //writer.append((String) paramHeaders.get(i));
            writer.write((String) paramHeaders.get(i));
            writer.flush();
        }
        int totalRead = 0;
        int bytesRead;
        byte buf[] = new byte[maxBufferSize];
        BufferedInputStream bufferedInputStream = null;
        for (int i = 0; i < filesAL.size(); i++) {
            String fhd = (String) fileHeaders.get(i);
            //writer.append(fhd);
            writer.write(fhd);
            writer.flush();
            File setFile = (File) filesAL.get(i);
            bufferedInputStream = new BufferedInputStream(new FileInputStream(setFile));
            while ((bytesRead = bufferedInputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, bytesRead);
                writer.flush();
                totalRead += bytesRead;
            }
            outputStream.flush();
            bufferedInputStream.close();
        }
        //utputStream.write(LINE_END.getBytes());
        writer.write(tail);
        writer.flush();
        writer.close()
        ;
        StringBuffer sb = new StringBuffer();
        // checks server's status code first
        try {
            status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"), 8);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.flush();
            bufferedInputStream.close();
            writer.close();
            outputStream.close();
            httpConn.disconnect();
        }
        return status;
    }

    public int setResultUpHttps(JobEntity3 jobEntity)  throws IOException {
        String boundary;
        String tail;
        final String LINE_END = "\r\n";
        final String TWOHYPEN = "--";
        HttpsURLConnection httpsConn;
        String charset = "UTF-8";
        PrintWriter writer;
        OutputStream outputStream;
        final String TAG = "MultipartUtility";
        //int maxBufferSize = 1024;
        //ProgressListener progressListener;
        long startTime;
        int status = 0;

        String requestURL = INMEMORYDB.FILE_UP_URI;
        boundary = "---" + System.currentTimeMillis() + "===";
        tail = LINE_END + TWOHYPEN + boundary + TWOHYPEN + LINE_END;
        URL url = new URL(requestURL);
        httpsConn = (HttpsURLConnection) url.openConnection();
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, createTrustManagers(), new java.security.SecureRandom());
            httpsConn.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            httpsConn.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpsConn.setDoOutput(true);
        httpsConn.setDoInput(true);
        httpsConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        httpsConn.setRequestProperty("Authorization", INMEMORYDB.HTTP_AUTHVALUE);

        String paramsPart = "";
        String fileHeader = "";
        String filePart = "";
        long fileLength = 0;
        startTime = System.currentTimeMillis();

        //ArrayList<String> paramHeaders = new ArrayList<>();
        ArrayList paramHeaders = new ArrayList();
        String param = TWOHYPEN + boundary + LINE_END
                + "Content-Disposition: form-data; name=\"assetCd\"" + LINE_END
                + "Content-Type: text/plain; charset=" + charset + LINE_END
                + LINE_END
                + jobEntity.getAssetCd() + LINE_END;
        paramsPart += param;
        paramHeaders.add(param);
        String param2 = TWOHYPEN + boundary + LINE_END
                + "Content-Disposition: form-data; name=\"agentCd\"" + LINE_END
                + "Content-Type: text/plain; charset=" + charset + LINE_END
                + LINE_END
                + jobEntity.getAgentCd() + LINE_END;
        paramsPart += param2;
        paramHeaders.add(param2);
        String param3 = TWOHYPEN + boundary + LINE_END
                + "Content-Disposition: form-data; name=\"jobType\"" + LINE_END
                + "Content-Type: text/plain; charset=" + charset + LINE_END
                + LINE_END
                + jobEntity.getJobType3() + LINE_END;
        paramsPart += param3;
        paramHeaders.add(param3);

        String param4 = TWOHYPEN + boundary + LINE_END
                + "Content-Disposition: form-data; name=\"files\"" + LINE_END
                + "Content-Type: text/plain; charset=" + charset + LINE_END
                + LINE_END
                + jobEntity.getAuditFileCd()+".des" + LINE_END;
        paramsPart += param4;
        paramHeaders.add(param4);

        ArrayList filesAL = new ArrayList();
        ArrayList fileHeaders = new ArrayList();
        String targetFileNm = "";
        String path = "";
        if(jobEntity.getJobType3().equals("AJ100") || jobEntity.getJobType3().equals("AJ101") || jobEntity.getJobType3().equals("AJ102")) {
            targetFileNm = jobEntity.getAuditFileCd()+".des";
            path=INMEMORYDB.DIAG_RESULT_FILE_AGENT_DIR;
        } else if(jobEntity.getJobType3().equals("AJ200")) {
            targetFileNm = jobEntity.getAuditFileCd()+".des";
            path=INMEMORYDB.GETSCRIPT_RESULT_FILE_AGENT_DIR;
        } else if(jobEntity.getJobType3().equals("AJ201"))  {
            targetFileNm = jobEntity.getAuditFileCd()+".dat";
            path=INMEMORYDB.GETSCRIPT_RESULT_FILE_AGENT_DIR;
        } else if(jobEntity.getJobType3().equals("AJ300")) {
            targetFileNm = jobEntity.getAuditFileCd()+".des";
            path=INMEMORYDB.DIAG_RESULT_FILE_AGENT_DIR;
        }
        logger.debug(path+targetFileNm);
        File file = new File(path+targetFileNm);
        fileHeader = TWOHYPEN + boundary + LINE_END
                + "Content-Disposition: form-data; name=\"files\"; filename=\"" + targetFileNm + "\"" + LINE_END
                + "Content-Type: " + URLConnection.guessContentTypeFromName(file.getAbsolutePath()) + LINE_END
                + "Content-Transfer-Encoding: binary" + LINE_END
                + LINE_END;
        fileLength += file.length() + LINE_END.getBytes(charset).length;
        filePart += fileHeader;

        fileHeaders.add(fileHeader);
        filesAL.add(file);

        String partData = paramsPart + filePart;

        long requestLength = partData.getBytes(charset).length + fileLength + tail.getBytes(charset).length;
        httpsConn.setRequestProperty("Content-length", "" + requestLength);
//        /httpsConn.setFixedLengthStreamingMode((int) requestLength);
        httpsConn.connect();

        outputStream = new BufferedOutputStream(httpsConn.getOutputStream());
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

        for (int i =0; i < paramHeaders.size(); i++) {
            //writer.append((String) paramHeaders.get(i));
            writer.write((String) paramHeaders.get(i));
            writer.flush();
        }
        int totalRead = 0;
        int bytesRead;
        byte buf[] = new byte[maxBufferSize];
        BufferedInputStream bufferedInputStream = null;
        for (int i = 0; i < filesAL.size(); i++) {
            String fhd = (String) fileHeaders.get(i);
            //writer.append(fhd);
            writer.write(fhd);
            writer.flush();
            File setFile = (File) filesAL.get(i);
            bufferedInputStream = new BufferedInputStream(new FileInputStream(setFile));
            while ((bytesRead = bufferedInputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, bytesRead);
                writer.flush();
                totalRead += bytesRead;
            }
            outputStream.flush();
            bufferedInputStream.close();
        }
        //utputStream.write(LINE_END.getBytes());
        writer.write(tail);
        writer.flush();
        writer.close()
        ;
        StringBuffer sb = new StringBuffer();
        // checks server's status code first
        try {
            status = httpsConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(), "UTF-8"), 8);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();
                httpsConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.flush();
            bufferedInputStream.close();
            writer.close();
            outputStream.close();
            httpsConn.disconnect();
        }
        return status;
    }
}