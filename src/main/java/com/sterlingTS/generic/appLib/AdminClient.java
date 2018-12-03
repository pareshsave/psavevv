package com.sterlingTS.generic.appLib;

import org.apache.log4j.Logger;

import com.sterlingTS.seleniumUI.seleniumCommands;
import com.sterlingTS.utils.commonUtils.BaseCommon;
//import com.sterlingTS.utils.commonUtils.database.DAO;
import com.sterlingTS.utils.commonVariables.Globals;
import com.sterlingTS.vv.VVDAO;


public class AdminClient extends BaseCommon{
	public static Logger log = Logger.getLogger(AdminClient.class);
	static seleniumCommands sc = new seleniumCommands(driver);
	
	
	
	
	public static String CloseSWestOrder() throws Exception{
		
		String retval 	= Globals.KEYWORD_FAIL;
		
		VVDAO absHire 	= new VVDAO();
		String [] swDB = Globals.getEnvPropertyValue("Swest_DBServer_Port").split(",");
		Globals.SWest_DBServerName = swDB[0];
		Globals.SWest_DBServerPort = swDB[1];
    	absHire.connectAbsHireDB(Globals.SWest_DBServerName, Globals.SWest_DBServerPort); // connect to absHire database
    	
    	//fetch expected results and order id 
    	String OrderID 					=	Globals.testSuiteXLS.getCellData_fromTestData("SWestOrderID").trim();
    	String expSearchReqFulfillment 	=	Globals.testSuiteXLS.getCellData_fromTestData("SearchReqFulfillment").trim();
    	int expSearchReqCount 			=	Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("ExpSearchReqCount").trim());
    	String expOrderStatus			= 	Globals.testSuiteXLS.getCellData_fromTestData("ExpectedOrderStatus").trim();
    	String expOrderScore 			=	Globals.testSuiteXLS.getCellData_fromTestData("ExpectedOrderScore").trim();
    	
    	expSearchReqFulfillment 		= 	expSearchReqFulfillment.toUpperCase();
    	
    	if(expSearchReqFulfillment == "" || expSearchReqFulfillment.isEmpty() || expSearchReqFulfillment ==null){
    		
    		sc.STAF_ReportEvent("Fail", "Order Fulfillment", "Expected status and score was not provided in test data",0);
    	
    	}else{
    		
    		String[] arrExpSearchReqStatusAndScore 	= expSearchReqFulfillment.split(";");
    		int arrLength 							= arrExpSearchReqStatusAndScore.length;
    		
    		if(expSearchReqFulfillment.contains("ALL")){ // case where all search request will be closed with the same search req status and score
        		
    			if(arrLength == 1){
    				
    				String expSearchReqStatus 	= arrExpSearchReqStatusAndScore[0].split(",")[1];
    				String expSearchReqScore 	= arrExpSearchReqStatusAndScore[0].split(",")[2];
    				
    				retval = absHire.closeOrderID(OrderID, expOrderStatus,expOrderScore,expSearchReqStatus, expSearchReqScore);	
        		
    			}else{
    				sc.STAF_ReportEvent("Fail", "Order Fulfillment", "Expected status and score was not provided right format in test data",0);
        			retval = Globals.KEYWORD_FAIL;
        		}
        		
        	}else{
        		//close individual search req with different status and score
        		retval = absHire.closeSearchRequests(OrderID, expOrderStatus, expOrderScore, arrExpSearchReqStatusAndScore,expSearchReqCount);
        		
        	}
    	}
    	absHire.cleanUpVVDAO();
    	return retval;
	}
}
