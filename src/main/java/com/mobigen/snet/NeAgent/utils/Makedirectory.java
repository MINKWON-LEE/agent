package com.mobigen.snet.NeAgent.utils;

import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by osujin12 on 2016. 2. 18..
 */
public class Makedirectory {
    Logger logger = LoggerFactory.getLogger(getClass());


    public Makedirectory(){
        ArrayList path = new ArrayList();
        //String osType = System.getProperty("os.name");

        path.add(INMEMORYDB.DIAG_RESULT_FILE_AGENT_DIR);
        path.add(INMEMORYDB.GETSCRIPT_RESULT_FILE_AGENT_DIR);
        path.add(INMEMORYDB.OTP_SEND_FILE_AGENT_ROOT_DIR);
        path.add(INMEMORYDB.DIAG_RECV_FILE_AGENT_ROOT_DIR);
        path.add(INMEMORYDB.GETSCRIPT_RECV_FILE_AGENT_ROOT_DIR);
        path.add(INMEMORYDB.OTP_RECV_FILE_AGENT_ROOT_DIR);

        File[] f = new File[path.size()];

        try {
            for (int i = 0; i<path.size(); i++){
                f[i] =new File((String) path.get(i));
                if(!f[i].isDirectory()){
                    if(f[i].mkdirs())
                    	logger.debug("directory created. Success :: "+ path.get(i));
                    else
                    	logger.debug("directory created. Failed :: "+ path.get(i));
                }
            }
        }catch (Exception e){
            logger.error(CommonUtils.printError(e));
        }
    }


}
