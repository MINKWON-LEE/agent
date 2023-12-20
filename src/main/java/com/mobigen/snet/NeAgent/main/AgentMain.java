package com.mobigen.snet.NeAgent.main;
import com.mobigen.snet.NeAgent.memory.INMEMORYDB;
import com.mobigen.snet.NeAgent.service.AgentServiceManager;
import com.mobigen.snet.NeAgent.service.OffLineDiagnosisManager;
import com.mobigen.snet.NeAgent.utils.Makedirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 스마트 가드 3.0 Agent
 * - Hoyoung 2020.06.01
 */
public class AgentMain {
    private static Logger logger = LoggerFactory.getLogger(AgentMain.class);
    public boolean skipVerChk = false;
    /**
     * 스마트 가드 3.0 실행
     * - Hoyoung 2020.06.01
     * @param mgcd : ManagerCD : T1sgmanager00000
     * @param dgtype
     * @param resultPath
     * @param gov_flag  : 진단 기준
     * @param sw_nm
     * @param sw_info
     * @param user_id
     * @param user_pw
     * @param etc_name
     * @param inst_id
     * @param inst_path
     * @param socketFilePath : 소켓파일(MySQL, MariaDB 진단)
     * @param criteria  : 진단 기준
     * @param param1
     * @param param2
     * @param param3
     * @throws Exception
     */
    public void doAgentService(String mgcd, String dgtype, String resultPath, String gov_flag,String sw_nm, String sw_info , String user_id,String user_pw,String etc_name,String inst_id,String inst_path,String socketFilePath, String criteria, String param1, String param2, String param3, String diagUse, String confNm ) throws Exception
    {
        boolean isOsWin = false;
        if(INMEMORYDB.osType.toUpperCase().indexOf(INMEMORYDB.WIN_ID)>-1){
            isOsWin = true;
        }
        INMEMORYDB memory = new INMEMORYDB();
        // 수동 진단 처리 (네트워크 연결 안됨)
        if (mgcd != "" && dgtype != "" && resultPath != "") {
            memory.off_init();
            logger.info("ENTERING OFFLINE AGENT PROCESS.!");
            new OffLineDiagnosisManager(mgcd, dgtype, resultPath,gov_flag,sw_nm,sw_info,user_id,user_pw,etc_name,inst_id,inst_path,socketFilePath, criteria, param1, param2, param3, diagUse, confNm).doDiagnosis();
            skipVerChk = true;
        } else {
            memory.init();
            new Makedirectory();
            logger.info("ENTERING NON-OFFLINE AGENT PROCESS.!");
            INMEMORYDB.deleteManualFile(INMEMORYDB.replaceDir(INMEMORYDB.AGENT_SYS_ROOT_DIR));
            AgentServiceManager sm = new AgentServiceManager();
            sm.setInmemorydb(memory);
            sm.initService();
            logger.info("AGENT STARTED. ~!!!");
        }
    }

    /**
     * Agent 3.0
     * Hoyoung - 2020.07.28
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        //== 파라미터 logging
        StringBuffer sb = new StringBuffer();
        sb.append("== main start\n");
        sb.append(" parameters : ");
        if(args!=null && args.length>0){
            for(int i=0; i<args.length-1; i++){
                sb.append(args[i] + " , ");
            }
            sb.append(args[args.length-1]);
        }
        logger.info(sb.toString());

        AgentMain agentMain = new AgentMain();

        // 배열로 처리 하면 안되나. ㅡㅡ;;;;;
        String mgcd = "";
        String dgtype = "";
        String resultPath = "";
        String gov_flag = "";
        String sw_nm = "";
        String sw_info = "";
        String user_id = "";
        String user_pw = "";
        String etc_name = "";
        String inst_id = "";
        String inst_path = "";
        String socketFilePath = ""; // MySQL, MariaDB Socket File 경로
        String criteria = "";		// 진단 기준 Common / MSIT / MSIT_SKT
        String param1 = "";			// 제품 마다 다름.
        String param2 = ""; 		// 제품 마다 다름.
        String param3 = ""; 		// 제품 마다 다름.
        String diagUse = "";
        String confNm = "";

        int argTotal = args.length;
        try{
            if(argTotal >=1 ){
                mgcd = args[0];
                if(mgcd != null && !"".equals(mgcd)&& mgcd.equals("noverchk")){
                    agentMain.skipVerChk = true;
                }
                if(mgcd != null && !"".equals(mgcd)&& mgcd.equals("debug")){
                    INMEMORYDB.isDebug = true;
                }
            }
            if(argTotal >=2 ){
                dgtype = args[1];
            }
            if(argTotal >=3 ){
                resultPath = args[2];
            }
            if(argTotal >=4 ){
                gov_flag = args[3];
            }
            if(argTotal >=5 ){
                sw_nm = args[4];
            }
            if(argTotal >=6 ){
                sw_info = args[5];
            }
            if(argTotal >=7 ){
                user_id = args[6];
                user_pw = args[7];
                etc_name = args[8];
                inst_id = args[9];
                inst_path = args[10];
            }
            if(argTotal >=12 ){
                socketFilePath = args[11];
            }
            if(argTotal >=16 ){
                criteria = args[12];
                param1 = args[13];
                param2 = args[14];
                param3 = args[15];
            }
            if (argTotal >= 18)  {
                diagUse = args[16];
                confNm = args[17];
            }

        }catch(Exception e){
            e.printStackTrace();
            // 에러 로그 처리 필요
            logger.debug(String.valueOf(e.getStackTrace()));
        }
        agentMain.doAgentService(mgcd,dgtype,resultPath,gov_flag,sw_nm,sw_info,user_id,user_pw,etc_name,inst_id,inst_path,socketFilePath,criteria,param1,param2,param3,diagUse,confNm);

    }
}