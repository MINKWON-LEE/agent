package com.mobigen.snet.NeAgent.utils;

import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import com.sk.snet.manipulates.PatternMaker;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by osujin12 on 2016. 4. 22..
 */
public class ZipUtil {
    private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public static void makeZip(ArrayList files , JobEntity3 jobEntity) throws Exception {

        String zipFileName =  INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobEntity.getJobType()) + jobEntity.getAuditFileCd() + INMEMORYDB.ZIP;
        //logger.info("-------------> "+INMEMORYDB.SEND + " ---> " + jobEntity.getJobType() + " --> " + jobEntity.getAuditFileCd() + INMEMORYDB.ZIP);
        try {
            //This is name and path of zip file to be created
            ZipFile zipFile = new ZipFile(zipFileName);

            //Add files to be archived into zip file


            //Initiate Zip Parameters which define various properties
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression

            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            //Set the encryption flag to true
            parameters.setEncryptFiles(true);

            //Set the encryption method to AES Zip Encryption
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

            //Set password
            parameters.setPassword(PatternMaker.ENCRPTION_PMANNER);

            //Now add files to the zip file
            zipFile.addFiles(files, parameters);
        }
        catch (ZipException e)
        {
            e.printStackTrace();
        }

    }

    public static void makeZip2(ArrayList files, String path, String fileNm, String jobType) throws Exception {

        String zipFileName = path + fileNm + INMEMORYDB.ZIP;

        try {
            //This is name and path of zip file to be created
            ZipFile zipFile = new ZipFile(zipFileName);
            //Add files to be archived into zip file
            //Initiate Zip Parameters which define various properties
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // set compression method to deflate compression
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            //Set the encryption flag to true
            parameters.setEncryptFiles(true);
            //Set the encryption method to AES Zip Encryption
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            //Set password
            parameters.setPassword(PatternMaker.ENCRPTION_PMANNER);
            //Now add files to the zip file
            zipFile.addFiles(files, parameters);
        }
        catch (ZipException e)
        {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
    }

}
