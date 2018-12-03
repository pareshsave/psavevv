package com.sterlingTS;


import com.sterlingTS.utils.commonUtils.DriverScriptUtils;
import com.sterlingTS.utils.commonVariables.Globals;

public class DriverScript {
	
	   public static void main(String[] args) throws Exception {
	    	
         if(args.length == 6 ){
         		Globals.STAFF_USERNAME				=	args[0]; // first command line argument is the username
         		Globals.STAFF_USER_PSSWORD			=	args[1]; // second command line argument is the user password
			 	Globals.DISPLAY_CREDENTIAL_POPUP	= 	false;
			 	
			 	Globals.EXECUTION_MACHINE 			=   args[2];
			 	Globals.Env_Name 					= 	args[3];
			 	Globals.suiteName 					= 	args[4];
			 	Globals.testCaseID 					= 	args[5];
         }
         
 
         DriverScriptUtils.testSuiteExecution();   

}

}
