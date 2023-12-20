package com.mobigen.snet.NeAgent.main;

import com.mobigen.snet.NeAgent.crypto.AESCryptography;
import com.mobigen.snet.NeAgent.utils.UnzipUtil;

import java.io.File;


public class ScFileMain {

    public static void main(String[] args) throws Exception {

        if(args[0].equals("dec")) {

            String zipFile = args[1];
            zipFile = zipFile.replaceAll(".des", "");
            zipFile += ".zip";

            System.out.println("Decrypt files ["+args[1] + "] to [" + zipFile + "]");

            new AESCryptography().decryptionShFile(args[1], zipFile);

            String path = new File(args[1]).getParent();
            System.out.println(path);

            Thread.sleep(10);

            new UnzipUtil(zipFile, path).unzip();
        } else if (args[0].equals("one")) {
            String zipFile = args[1];
            zipFile = zipFile.replaceAll(".des", "");
            zipFile += ".";
            zipFile += args[2];

            System.out.println("Decrypt files ["+args[1] + "] to [" + zipFile + "]");

            new AESCryptography().decryptionShFile(args[1], zipFile);
        }
    }
}
