package com.mobigen.snet.NeAgent.crypto;

import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import com.mobigen.snet.NeAgent.utils.CommonUtils;

import com.mobigen.snet.NeAgent.utils.PatternBuilder;
import com.sk.snet.manipulates.PatternMaker;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;


public class AESCryptography {
	Logger logger = LoggerFactory.getLogger(getClass());
	static PatternBuilder patterBuilder = new PatternBuilder();
	public static byte[] key = PatternMaker.ENCRPTION_PMANNER.getBytes();
	private String keyValue = "snet^igloo!sec";
	private Key keySpec;
	private String iv;
	/**
	 * ?
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String str) throws GeneralSecurityException, UnsupportedEncodingException {
		String enStr = "";
		try {
			Cipher cipher = Cipher.getInstance("AES");
			byte[] keyBytes = new byte[16];
			byte[] b=keyValue.getBytes("ASCII");
			int len =b.length;
			if(len>keyBytes.length) len=keyBytes.length;
			System.arraycopy(b,0,keyBytes,0,len);
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes,"AES");
			IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
			cipher.init(Cipher.ENCRYPT_MODE,keySpec);
			byte[] results = cipher.doFinal(str.getBytes("UTF-8"));
			//enStr = new String(results);
			enStr = new String(Hex.encodeHex(results));
//			BASE64Encoder encoder = new BASE64Encoder();
//			encoder.encode(results);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return enStr;
	}

	public File[] encryptionFile(JobEntity3 jobEntity) throws Exception{
		String jobType = jobEntity.getJobType();
		String path =  INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.SEND, jobType);
		//String fileName = jobEntity.getFileName();
		String fileName = jobEntity.getAuditFileCd();
		FileInputStream inFile = null;
		FileOutputStream outFile =null;

		File[] files = new File[3];

		try {
			// file to be encrypted
			inFile= new FileInputStream(path+ File.separator+ fileName+jobEntity.getFileType());

			// encrypted file
			files[0] = new File(path + fileName + ".des");
			outFile = new FileOutputStream(path + fileName + ".des");

			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			//file encryption
			byte[] input = new byte[64];
			int bytesRead;

			while ((bytesRead = inFile.read(input)) != -1) {
				byte[] output = cipher.update(input, 0, bytesRead);
				if (output != null)
					outFile.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				outFile.write(output);
			outFile.flush();

			//make dummy File
			byte[] salt = new byte[8];
			FileOutputStream saltOutFile = new FileOutputStream(path + fileName + ".salt");
			files[1] = new File(path + fileName + ".salt");
			saltOutFile.write(salt);
			saltOutFile.close();

			FileOutputStream ivOutFile = new FileOutputStream(path + fileName + ".iv");
			files[2] = new File(path + fileName + ".iv");
			byte[] iv =  new byte[16];
			ivOutFile.write(iv);
			ivOutFile.close();

		}catch (Exception e){
			logger.error(CommonUtils.printError(e));
		}finally {
			inFile.close();
			outFile.close();
			return files;
		}
	}


	/**
	 * 백업 폴더 암호화
	 */
	public File[] encryptionBackupFile(String path, JobEntity3 jobEntity) throws Exception{
		String jobType = jobEntity.getJobType3();
		String fileName = jobType;
		FileInputStream inFile = null;
		FileOutputStream outFile =null;

		File[] files = new File[3];

		try {
			// file to be encrypted
			inFile= new FileInputStream(path+ File.separator+ fileName+INMEMORYDB.ZIP);

			// encrypted file
			files[0] = new File(path + fileName + ".des");
			outFile = new FileOutputStream(path + fileName + ".des");

			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			//file encryption
			byte[] input = new byte[64];
			int bytesRead;

			while ((bytesRead = inFile.read(input)) != -1) {
				byte[] output = cipher.update(input, 0, bytesRead);
				if (output != null)
					outFile.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				outFile.write(output);
			outFile.flush();

			//make dummy File
			byte[] salt = new byte[8];
			FileOutputStream saltOutFile = new FileOutputStream(path + fileName + ".salt");
			files[1] = new File(path + fileName + ".salt");
			saltOutFile.write(salt);
			saltOutFile.close();

			FileOutputStream ivOutFile = new FileOutputStream(path + fileName + ".iv");
			files[2] = new File(path + fileName + ".iv");
			byte[] iv =  new byte[16];
			ivOutFile.write(iv);
			ivOutFile.close();

		}catch (Exception e){
			logger.error(CommonUtils.printError(e));
		}finally {
			inFile.close();
			outFile.close();
			return files;
		}
	}



	/**
	 * 1회용 진단 파일 암호화(장비정보 수집,진단결과)
	 * 2020.11.23 - hoyoung
	 * @param srcfile
	 * @param ext
	 * @return
	 * @throws Exception
	 */
	public File[] encryptionOneTimeFile(String path, String srcfile,String ext) throws Exception{
		//String fileName = jobEntity.getFileName();
		String fileName = srcfile;
		FileInputStream inFile = null;
		FileOutputStream outFile =null;

		File[] files = new File[3];

		try {
			// file to be encrypted
			inFile= new FileInputStream(path+srcfile+ext);

			// encrypted file
			files[0] = new File(path + fileName + ".des");
			outFile = new FileOutputStream(path + fileName + ".des");

			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			//file encryption
			byte[] input = new byte[64];
			int bytesRead;

			while ((bytesRead = inFile.read(input)) != -1) {
				byte[] output = cipher.update(input, 0, bytesRead);
				if (output != null)
					outFile.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				outFile.write(output);
			outFile.flush();

			//make dummy File
			byte[] salt = new byte[8];
			FileOutputStream saltOutFile = new FileOutputStream(path + fileName + ".salt");
			files[1] = new File(path + fileName + ".salt");
			saltOutFile.write(salt);
			saltOutFile.close();

			FileOutputStream ivOutFile = new FileOutputStream(path + fileName + ".iv");
			files[2] = new File(path + fileName + ".iv");
			byte[] iv =  new byte[16];
			ivOutFile.write(iv);
			ivOutFile.close();

		}catch (Exception e){
			logger.error(CommonUtils.printError(e));
		}finally {
			inFile.close();
			outFile.close();
			return files;
		}
	}


	public boolean decryptionFile(JobEntity3 jobEntity) throws Exception{

		String path = INMEMORYDB.jobTypeAbsolutePath(INMEMORYDB.RECV, jobEntity.getJobType());
		String fileName = jobEntity.getAuditFileCd();

		FileInputStream fis =null;
		FileOutputStream fos =null;
		try {
			fis = new FileInputStream(path+fileName+".des");
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			fos = new FileOutputStream(path+File.separator+fileName+jobEntity.getFileType());

			byte[] in = new byte[64];
			int read;
			while ((read = fis.read(in)) != -1) {
				byte[] output = cipher.update(in, 0, read);
				if (output != null)
					fos.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				fos.write(output);
			fos.flush();
		}catch (Exception e){
			logger.error(CommonUtils.printError(e));
			return false;
		}finally{
			fis.close();
			fos.close();
			return true;
		}
	}

	public boolean decryptionShFile(String srcDir , String dstDir) throws Exception{
		boolean isDec = false;
		FileInputStream fis		= null;
		FileOutputStream fos	= null;
		try {
			fis = new FileInputStream(srcDir);

			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);

			fos = new FileOutputStream(dstDir);
			byte[] in = new byte[64];
			int read;
			while ((read = fis.read(in)) != -1) {
				byte[] output = cipher.update(in, 0, read);
				if (output != null)
					fos.write(output);
			}

			byte[] output = cipher.doFinal();
			if (output != null)
				fos.write(output);
			fos.flush();

			isDec = true;
		}catch (Exception e){
			logger.error("get program File Not Found. " + srcDir);
		}finally{
			if(fis != null) fis.close();
			if(fos != null) fos.close();

			return isDec;
		}
	}

	public static void main(String[] args){

		System.out.println((new String(key)));
		String sourcePath = "C:\\snet\\txFiles\\backup\\AJ100.des";
		try {
			new AESCryptography().decryptionShFile(sourcePath, "c:\\temp\\sample.zip");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}