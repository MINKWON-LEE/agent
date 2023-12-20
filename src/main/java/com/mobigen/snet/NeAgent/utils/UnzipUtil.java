package com.mobigen.snet.NeAgent.utils;

import com.sk.snet.manipulates.PatternMaker;
import net.lingala.zip4j.core.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by osujin12 on 2016. 3. 28..
 */
public class UnzipUtil {

    Logger logger = LoggerFactory.getLogger(getClass());

    private String  zipFile;
    private String targetDir;
    private String pENC_MAN = PatternMaker.ENCRPTION_PMANNER;

    public UnzipUtil(String zipFile,String targetDir){
        this.zipFile = zipFile;
        this.targetDir = targetDir;
    }

    public boolean unzip(){
        boolean result = false;
        try {
            ZipFile zipFile = new ZipFile(this.zipFile);
            zipFile.setPassword(pENC_MAN);
            //System.out.println("+====================> > "+ pENC_MAN);
            zipFile.extractAll(targetDir);
            result = true;
        }catch (Exception e){
           logger.error(e.getMessage());

            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) throws Throwable {
        //new UnzipUtil("/usr/local/snetManager/txFiles/outbound/diags/work/548973.zip","/Users/osujin12/Downloads").unzip();
        new UnzipUtil("c:\\temp\\sample.zip","c:\\temp\\ok").unzip();
    }

}
