package com.mobigen.snet.NeAgent.utils;

import com.mobigen.snet.NeAgent.entity.JobEntity3;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;

import java.io.*;

/**
 * Created by osujin12 on 2016. 4. 12..
 */
public class ProcessCheck {

    private String filePath = INMEMORYDB.AGENT_BIN_DIR;


    public void append_Start(JobEntity3 jobEntity){

        File f = new File(filePath+INMEMORYDB.AGENT_CHK_FILE);
        FileWriter fw = null;
        try {
            fw = new FileWriter(f,true);
            fw.write("\n"+jobEntity.getAuditFileCd() + INMEMORYDB.MONITER_DEILIMTER + jobEntity.getJobType()+INMEMORYDB.MONITER_DEILIMTER +DateUtil.getCurrDateBySecond()+INMEMORYDB.MONITER_DEILIMTER);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fw != null) try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void append_End(){

        File f = new File(filePath+INMEMORYDB.AGENT_CHK_FILE);
        FileWriter fw = null;
        try {
            fw = new FileWriter(f,true);
            fw.write(DateUtil.getCurrDateBySecond());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fw != null) try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static void main(String[] args) throws IOException, InterruptedException {
        new INMEMORYDB().init();
        ProcessCheck p = new ProcessCheck();

    }

}
