package com.sterlingTS.vv;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.sterlingTS.seleniumUI.seleniumCommands;
import com.sterlingTS.utils.commonUtils.BaseCommon;
import com.sterlingTS.utils.commonUtils.CommonHelpMethods;
import com.sterlingTS.utils.commonUtils.Email;
import com.sterlingTS.utils.commonUtils.ProtectedEncryption;
import com.sterlingTS.utils.commonVariables.Enum;
import com.sterlingTS.utils.commonVariables.Enum.CMAStatus;
import com.sterlingTS.utils.commonVariables.Enum.OrderScore;
import com.sterlingTS.utils.commonVariables.Enum.OrderStatus;
import com.sterlingTS.utils.commonVariables.Enum.ProductCode;
import com.sterlingTS.utils.commonVariables.Enum.SearchReqScore;
import com.sterlingTS.utils.commonVariables.Enum.SearchReqStatus;
import com.sterlingTS.utils.commonVariables.Globals;


public class VVDAO {
	
	public static Logger log = Logger.getLogger(VVDAO.class);
	
	public PreparedStatement ps = 	null;
	public Statement stmt 		=  	null;
	public ResultSet rs 		= 	null;
	public Connection conn;
	
	
/**************************************************************************************************
 * Constructor to connect to databse with sql server authentication.Email alert is triggered in case of failures
 * @param dbURL - url string of the database to which it would be connecting 
 * @param userName - SA username
 * @param pwd SA password
 * @author aunnikrishnan
 * @created 6/22/2015
 * @LastModifiedBy
 ***************************************************************************************************/

	public VVDAO(String dbURL, String userName, String pwd){
		try {
			log.debug("......Connecting to Database-"+dbURL);
			this.conn = DriverManager.getConnection(dbURL, userName, pwd);
			
		
		} catch (Exception e) {
			
			e.printStackTrace();
			log.info("Database Connection-Unable to establish connection.Exception: - "+ e.toString());
			if (Globals.SendEmails.equalsIgnoreCase("true")) {
				String toList = Globals.StatusEmailToList+","+Globals.fromEmailID;
				String ccList = Globals.StatusEmailCCList;
				String subject = "***Selenium Execution => " + dbURL +" DB Connectivity Issue *****";
				String msgBody = "Unable to connect to the database..Exception - " + e.toString() + "<br><br><br>";
				Email.send(toList, ccList, subject, "html", msgBody);
			}
			
			
			this.conn = null;
		}
	}
	
	
	public VVDAO() {
		this.conn = null;
		this.ps = null;
		this.stmt = null;
		this.rs=null;
		System.setProperty("java.library.path", Globals.sqlAuthDLLPath);
	}
	/**************************************************************************************************
	 * Method to connect to absHire databse with windows authentication.Connection string will be formed by this method itself
	 * @param DBname -  Name of the database to which it needs to connect
	 * @param portNumber - Port number of the database instance to which it needs to coonect
	 * @author aunnikrishnan
	 * @created 6/22/2015
	 * @LastModifiedBy
	 ***************************************************************************************************/

	public void connectAbsHireDB(String DBname, String portNumber){
		try {
			String dbURL = "jdbc:sqlserver://"+DBname + ";portNumber="+portNumber+";databaseName=absHire;"; // to use windows authentication include the following code- integratedSecurity=true;";
			log.debug(".........Connecting to Database-"+DBname);
			String userName=Globals.getEnvPropertyValue("ABSHIRE_USERNAME");
			String pwd=ProtectedEncryption.decrypt(Globals.getEnvPropertyValue("ABSHIRE_PASSWORD"), CommonHelpMethods.createKey());
			this.conn = DriverManager.getConnection(dbURL, userName, pwd);
			
		
		} catch (Exception e) {
			
			e.printStackTrace();
			log.info("absHire DB Connection-Unable to establish connection.Exception: - "+ e.toString());
			if (Globals.SendEmails.equalsIgnoreCase("true")) {
				String toList = Globals.StatusEmailToList+","+Globals.fromEmailID;
				String ccList = Globals.StatusEmailCCList;
				String subject = "***Selenium Execution => " + DBname +" DB Connectivity Issue *****";
				String msgBody = "Unable to connect to abshire database.Exception - " + e.toString();
				Email.send(toList, ccList, subject, "html", msgBody);
			}
			
			this.conn = null;
		}
	}
	
	/**************************************************************************************************
	 * Method to close the connection data member
	 * @author aunnikrishnan
	 * @created 6/22/2015
	 * @LastModifiedBy
	 ***************************************************************************************************/
	public void closeConnection(){
		try {
	        if (this.conn != null && !this.conn.isClosed()) {
	            this.conn.close();
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        log.info("Automation DB Connection-Unable to close connection.Exception: - "+ ex.toString());
	    }
	}


	/**************************************************************************************************
	 * Method to fetch release/platform/environment id from the automation database
	 * @author aunnikrishnan
	 * @created 6/22/2015
	 * @LastModifiedBy
	 ***************************************************************************************************/
	
public int fetchID(String parameterName,String parameterValue,String SelectQuery, String InsertQuery){
		
		int retval = -1;
		int paramID = 0;

		
        if(parameterName == null || parameterName.isEmpty() || 
        		parameterValue == null || parameterValue.isEmpty() ||
				SelectQuery == null || SelectQuery.isEmpty() ||
				InsertQuery == null ||  InsertQuery.isEmpty() ){
			return retval;
		}
		
		if (this.conn == null){
			return retval;
		}
		
		try {
			ps = this.conn.prepareStatement(SelectQuery);
			ps.setString(1, parameterValue);
			rs = ps.executeQuery();
			
			while(rs.next()){
            	paramID = rs.getInt(parameterName);
            }
			rs = null;
			
			if(paramID == 0 ){
            	            	
            	//need to insert the releaseName into DB 
            	ps = this.conn.prepareStatement(InsertQuery);
            	ps.setString(1, parameterValue);
            	
            	int rowCount = ps.executeUpdate();
            	if(rowCount ==1){
            		ps = this.conn.prepareStatement(SelectQuery);
        			ps.setString(1, parameterValue);
        			rs = ps.executeQuery();
        			
        			while(rs.next()){
                    	paramID = rs.getInt(parameterName);
                    }
            		
            		
            		retval = paramID;
            	}
            }else{
            	retval =  paramID;
            }
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		finally{
			cleanUpVVDAO();
		}
		return retval;
	}
	

	public int insertNewTestCaseDetails(){
		
		int retval = -1;
		if (this.conn == null){
			return retval;
		} 
		
		try {
			
			java.sql.Timestamp currTimestamp = seleniumCommands.getDBCurrentTimeStamp();
		    
			ps = this.conn.prepareStatement(Globals.InsertTCDetailsSQL);
			
			ps.setInt(1, Globals.ScenarioID); //scenarioID
			ps.setString(2, Globals.TestCaseID); //tcid
			ps.setString(3, Globals.tcDescription); //tc description
			ps.setString(4, Globals.testScenario); //test scenario
			ps.setInt(5, Globals.ManualTCCount); //manual tc count
			ps.setInt(6, Globals.TestCaseStatusID); // env id
			ps.setTimestamp(7,currTimestamp );
			
			int rowCount = 0;
			rowCount = ps.executeUpdate();
			if(rowCount ==1){
				
				ps.close();
				ps =null;
				ps = this.conn.prepareStatement(Globals.SelectExecutionIDFromTCSQL);
				
				ps.setTimestamp(1,currTimestamp );
				ps.setInt(2, Globals.ScenarioID); //releaseid
				ps.setString(3, Globals.TestCaseID); //tcid	
				ps.setInt(4, Globals.TestCaseStatusID); // env id
				
				rs = ps.executeQuery();
				
				int executionID =0;
				while (rs.next()) {
					executionID = rs.getInt("ExecutionID");
					
				}
				
				if(executionID == 0 ){
					retval = -1;
				}else{
					Globals.ExecutionID = executionID;
					retval = executionID;
				}
				
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			cleanUpVVDAO();
		}
		
		return retval;
	}
	
public int updateTestCaseDetails() {
		
		int retval = -1;
		if (this.conn == null){
			return retval;
		} 
		
		try {
						
			ps = this.conn.prepareStatement(Globals.UpdateTCStatusSQL);
								
			ps.setInt(1, Globals.TestCaseStatusID); // statusID
			ps.setDouble(2, Double.parseDouble(Globals.tcManualEffort)); //manual execution time
			ps.setDouble(3, Double.parseDouble(Globals.tcExecution_Time)); //automation execution time
			ps.setInt(4, Globals.ExecutionID); //executionID
			
			int rowCount = 0;
			rowCount = ps.executeUpdate();
			if(rowCount ==1){
				retval = rowCount;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			cleanUpVVDAO();
		}
		
		return retval;
	}
	
	public void cleanUpVVDAO(){
			
		try {
            if (ps != null && !ps.isClosed()) {
                ps.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
		
		ps = null;
		
		try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
		
		stmt = null;
		
		try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
		
		rs = null;
		
			
	}
	
public int insertTestStepDetails(int executionID,String flowID,String stepName,String stepDescr,int statusID,String srcFilePath){
		
		int retval = -1;
		if (this.conn == null){
			return retval;
		} 
		
		try {
			String destPath ="";
			String destFileName ="";
			
			if (srcFilePath.isEmpty() || srcFilePath == null){
				destPath = "NA";
				destFileName = "NA";
			}else{
				destPath = Globals.ServerScreenshotPath + Globals.CurrentPlatform +File.separator+ Globals.currentTimestampFolderName;
				File srcFile = new File(srcFilePath);
				File destDir = new File( destPath);
				
		    	try {
					FileUtils.copyFileToDirectory(srcFile, destDir);
					destFileName = destPath +File.separator+ srcFile.getName();
					
				} catch (IOException e1) {
					e1.printStackTrace();
					destFileName = "Exception Occurred";
				}
		    	
			}
			
			java.sql.Timestamp currTimestamp = seleniumCommands.getDBCurrentTimeStamp();
			
			ps = this.conn.prepareStatement(Globals.InsertTestStepDetailsSQL);
			
			if (stepDescr.length() >= 295 ){
				stepDescr = stepDescr.substring(1, 295);// in case of exception being generated, no of characters exceed the column limit hence exception is generated
			}			
			
			ps.setInt(1,executionID); //releaseid
			ps.setString(2, flowID); //flowID
			ps.setString(3, stepName); //stepName
			ps.setString(4, stepDescr); //stepName
			ps.setInt(5, statusID);  // statusID
			ps.setTimestamp(6, currTimestamp);
			ps.setString(7, destFileName); // outputFilePath 
			
			int rowCount = 0;
			rowCount = ps.executeUpdate();
			if(rowCount ==1){
				retval = 1;
			}else{
				retval=-1;
			}
				
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			cleanUpVVDAO();
		}
		
		return retval;
	}
	
public int insertNewTestSceanrioDetails(){
	
	int retval = -1;
	if (this.conn == null){
		return retval;
	} 
	
	try {
		
		int executionCycleNo =Integer.parseInt(Globals.ExecutionCycle.replace("Cycle",""));
		String machineName = Globals.MachineName;
		String userName = Globals.MachineUserName;
		java.sql.Timestamp currTimestamp = seleniumCommands.getDBCurrentTimeStamp();
	    String sAutomationTool = "Selenium";
	    
		ps = this.conn.prepareStatement(Globals.InsertSceanrioDetailSQL);
		
		ps.setInt(1, Globals.ReleaseID); //releaseid
		ps.setInt(2, Globals.PlatformID); //platformid
		ps.setInt(3, Globals.EnvID); // env id
		ps.setInt(4, Globals.TestSuiteID);  // test suite id
		ps.setString(5, Globals.currentScenarioColValue); //test scenario
		ps.setInt(6, executionCycleNo); // Execution cycle
		ps.setString(7,machineName); //machine name
		ps.setString(8, userName); //username
		ps.setString(9, sAutomationTool); //AutomationTool
		ps.setTimestamp(10,currTimestamp );
		
		int rowCount = 0;
		rowCount = ps.executeUpdate();
		if(rowCount ==1){
			
			ps.close();
			ps =null;
			ps = this.conn.prepareStatement(Globals.SelectSceanrioDetailsSQL);
			
			ps.setTimestamp(1,currTimestamp );
			ps.setInt(2, Globals.ReleaseID); //releaseid
			ps.setInt(3, Globals.PlatformID); //platformid
			ps.setInt(4, executionCycleNo); // Execution cycle
			ps.setString(5,machineName); //machine name
			ps.setInt(6, Globals.EnvID); // env id

			
			rs = ps.executeQuery();
			
			int ScenarioID =0;
			while (rs.next()) {
				ScenarioID = rs.getInt("ScenarioID");
				
			}
			
			if(ScenarioID == 0 ){
				retval = -1;
			}else{
				Globals.ScenarioID = ScenarioID;
				retval = ScenarioID;
			}
			
			
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	finally{
		cleanUpVVDAO();
	}
	
	return retval;
	
	/**************************************************************************************************
	 * Method to close the all the search request with same status and score. Post which the Order status and score is verified
	 * @param OrderID -  Order id whose search request needs to be fulfilled
	 * @param expOrderStatus - String value  - expected order status post fulfilling all search request
	 * @param expOrderScore - String value  - expected order score post fulfilling all search request
	 * @param expSearchReqStatus - String - status of the search request with which it needs to be updated
	 * @param expSearchReqScore -  String - score of the search request with which it needs to be updated
	 * @throws SQLException
	 * @return int record count
	 * @author aunnikrishnan
	 * @created 6/22/2015
	 * @LastModifiedBy
	 ***************************************************************************************************/
}
	public String closeOrderID(String OrderID, String expectedOrderStatus, String expectedOrderScore,String expSearchReqStatus,String expSearchReqScore) throws Exception{
		
		String retval = Globals.KEYWORD_FAIL;
		SearchReqScore  score = SearchReqScore.valueOf(expSearchReqScore.toUpperCase());
		SearchReqStatus status = SearchReqStatus.valueOf(expSearchReqStatus.toUpperCase());
		int iOrderID = Integer.parseInt(OrderID);
		int searchReqCount = 0;								
		
		searchReqCount = fetchSearchReqCount(OrderID);
	
		String updateQuery = "UPDATE [abshire].[dbo].[SearchReq] WITH (ROWLOCK) SET   [Score] = ?, [Status] = ? WHERE [OrderID] = ?";
		
		this.ps = this.conn.prepareStatement(updateQuery);
		ps.setInt(1, score.ScoreID);
		ps.setInt(2, status.StatusID);
		ps.setInt(3, iOrderID);
		
		int statusUpdateRowCount = 0;
		statusUpdateRowCount = ps.executeUpdate();
				
		ps.close();
		
		String insertQuery ="INSERT INTO [dbo].[DataEvents]([EventID], [EventType],[RouteType])   SELECT ReqID AS [EventID] , 1 AS EventType, 0 AS RouteType FROM [abshire].[dbo].[SearchReq] WHERE OrderID = ?";
		this.ps = this.conn.prepareStatement(insertQuery);
		ps.setInt(1, iOrderID);
		
		int eventRowCount = 0;
		eventRowCount = ps.executeUpdate();
				
		if (searchReqCount ==eventRowCount && searchReqCount== statusUpdateRowCount){
			seleniumCommands.STAF_ReportEvent("Pass", "Order Fulfillment", "Updated row count matches search req count.Count = "+ searchReqCount,0);
		}
		else{
			seleniumCommands.STAF_ReportEvent("Fail", "Order Fulfillment", "Updated row count DOESNT matches search req count",0);
		}
		Thread.sleep(5000); //  this timeout is specified for the event in DataEvents to get processed.
		retval =verifyOrderStatus(OrderID, expectedOrderStatus, expectedOrderScore);
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to close the all the search request individually with different status and score. Post which the Order status and score is verified
	 * @param OrderID -  Order id whose search request needs to be fulfilled
	 * @param expOrderStatus - String value  - expected order status post fulfilling all search request
	 * @param expOrderScore - String value  - expected order score post fulfilling all search request
	 * @param expSearchReqStatusAndScore - String[] containing product code and the status and score with which it needs to be udpated.eg:[0]= VCEMP,Complete,Clear [1]=CRFM,Complete,Consider
	 * @param expSearchReqCount - int - expected search request count that should be present in the order.
	 * @return int record count
	 * @author aunnikrishnan
	 * @throws Exception 
	 * @created 6/22/2015
	 * @LastModifiedBy
	 ***************************************************************************************************/
public String closeSearchRequests(String OrderID, String expOrderStatus, String expOrderScore ,String[] expSearchReqStatusAndScore, int expSearchReqCount) throws Exception{
	
	String retval = Globals.KEYWORD_FAIL;
	SearchReqScore  score;
	SearchReqStatus status;
	ProductCode productCode;
	int iOrderID;
	int searchReqCount			 = 0;
	int statusUpdateRowCount 	= 0;
	int eventRowCount		 	= 0;
	String[] indvItems;
	
	searchReqCount 				= fetchSearchReqCount(OrderID);
		
	if (searchReqCount != expSearchReqCount){
		seleniumCommands.STAF_ReportEvent("Fail", "Order Fulfillment", "Expected Search Req Count doesnt match with actual search req count.Expected-"+expSearchReqCount+" Actual-"+searchReqCount,0);
	}
	else{
		seleniumCommands.STAF_ReportEvent("Pass", "Order Fulfillment", "Expected Search Req Count matches with actual search req count.Count="+expSearchReqCount,0);
		String updateQuery = "UPDATE [abshire].[dbo].[SearchReq] WITH (ROWLOCK) SET [Score] = ?, [Status] = ? WHERE OrderID = ? AND ProductID = ? ";
		String insertQuery ="INSERT INTO [dbo].[DataEvents]([EventID], [EventType],[RouteType])  SELECT ReqID AS [EventID] , 1 AS EventType, 0 AS RouteType FROM [abshire].[dbo].[SearchReq] WHERE  OrderID = ? AND ProductID = ?";
		
		iOrderID = Integer.parseInt(OrderID);
		
		for (int i=0;i<expSearchReqStatusAndScore.length;i++){
			indvItems =  expSearchReqStatusAndScore[i].split(",");
			
			productCode = ProductCode.valueOf(indvItems[0].toUpperCase());
			status = SearchReqStatus.valueOf(indvItems[1].toUpperCase());
			score = SearchReqScore.valueOf(indvItems[2].toUpperCase());
			
			if (score == SearchReqScore.LEVEL1 || score == SearchReqScore.LEVEL2 || score == SearchReqScore.LEVEL3 || score == SearchReqScore.PASSED || score == SearchReqScore.REVIEW){
				handleCMASearchRequest(iOrderID,productCode,score);
			}
			this.ps = this.conn.prepareStatement(updateQuery);
			ps.setInt(1, score.ScoreID);
			ps.setInt(2, status.StatusID);
			ps.setInt(3, iOrderID);
			ps.setInt(4, productCode.productID);
			
			statusUpdateRowCount = 0;
			statusUpdateRowCount = ps.executeUpdate();
			
			if (statusUpdateRowCount >= 1) {
				ps.close();
				this.ps = this.conn.prepareStatement(insertQuery);
				ps.setInt(1, iOrderID);
				ps.setInt(2, productCode.productID);
				
				eventRowCount = 0;
				eventRowCount = ps.executeUpdate();
				if (eventRowCount >= 1){
					seleniumCommands.STAF_ReportEvent("Pass", "SearchReq Fulfillment", "Updated the status=" +status +" and score="+score+" for "+productCode,0);
				}else{
					seleniumCommands.STAF_ReportEvent("Fail", "SearchReq Fulfillment", "Unable to update the sstatus=" +status +" and score="+score+" for "+productCode,0);
				}
				
			}else{
				seleniumCommands.STAF_ReportEvent("Fail", "SearchReq Fulfillment", "Unable to update the status=" +status +" and score="+score+" for "+productCode,0);
			}
		}
		
		retval = verifyOrderStatus(OrderID, expOrderStatus, expOrderScore);
		
	}
	
	cleanUpVVDAO();
	return retval;
	
	
}
		
/**************************************************************************************************
 * Method to fetch the row count for a record set
 * @param res -  result set whose record count needs to obtained
 * @throws SQLException
 * @return int record count
 * @author aunnikrishnan
 * @created 6/22/2015
 * @LastModifiedBy
 ***************************************************************************************************/
	public int getRows(ResultSet res) throws SQLException{
	    int totalRows = 0;
	    try {
	    	if(res.isClosed()){
	    		return -1;
	    	}
	        res.last();
	        totalRows = res.getRow();
	        res.beforeFirst();
	    } 
	    catch(Exception ex)  {
	    	ex.printStackTrace();
	    	
	    	totalRows = -1;
	    }
	   
	    return totalRows ;    
	}
	
	/**************************************************************************************************
	 * Method to fetch the search request count for an order id from from absHire database
	 * @param OrderID -  SWest order id whose status and score needs to be verified - String value
	 * @throws SQLException
	 * @return int search request count
	 * @author aunnikrishnan
	 * @created 6/22/2015
	 * @LastModifiedBy
	 ***************************************************************************************************/
	public int fetchSearchReqCount(String OrderID) throws SQLException{
		int iOrderID = Integer.parseInt(OrderID);
		
		String searchReqQuery = "select * from [abshire].[dbo].[SearchReq] where [OrderID] = ?";
		this.ps = this.conn.prepareStatement(searchReqQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ps.setInt(1, iOrderID);
		rs = ps.executeQuery();

		int searchReqCount = 0;
		searchReqCount = getRows(rs);
		cleanUpVVDAO();
		return searchReqCount;
	}
	
	/**************************************************************************************************
	 * Method to fetch the order status from absHire database for an order id
	 * @param OrderID -  SWest order id whose status and score needs to be verified - String value
	 * @throws SQLException
	 * @return OrderStatus enum value
	 * @author aunnikrishnan
	 * @created 6/22/2015
	 * @LastModifiedBy
	 ***************************************************************************************************/
	
	public OrderStatus fetchOrderStatus(String OrderID) throws SQLException{
		
		int iOrderID 				= Integer.parseInt(OrderID);
		OrderStatus requiredStatus 	= OrderStatus.NA;
		int searchReqCount 			= 0;
		String searchReqQuery 		= "select Status from BgOrders where BgOrderID = ?";
		
		this.ps = this.conn.prepareStatement(searchReqQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ps.setInt(1, iOrderID);
		rs = ps.executeQuery();
		
		searchReqCount = getRows(rs);
		if(searchReqCount >= 1 ){
			
			rs.next();
			requiredStatus = Enum.fetchOrderStatusEnum(rs.getInt("Status"));
		
		}
				
		return requiredStatus;
	}
	
	/**************************************************************************************************
	 * Method to fetch the order score from absHire database for an order id
	 * @param OrderID -  SWest order id whose status and score needs to be verified - String value
	 * @throws SQLException
	 * @return OrderScore enum value
	 * @author aunnikrishnan
	 * @created 6/22/2015
	 * @LastModifiedBy
	 ***************************************************************************************************/
	public OrderScore fetchOrderScore(String OrderID) throws SQLException{
		
		int iOrderID 			 = Integer.parseInt(OrderID);
		OrderScore requiredScore = OrderScore.NA;
		String searchReqQuery 	 = "select Score from BgOrders where BgOrderID = ?";
		int searchReqCount 		 = 0;
		this.ps = this.conn.prepareStatement(searchReqQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ps.setInt(1, iOrderID);
		rs = ps.executeQuery();

		
		searchReqCount = getRows(rs);
		if(searchReqCount >= 1 ){
			
			rs.next();
			requiredScore = Enum.fetchOrderScoreEnum(rs.getInt("Score"));
		
		}
		
		cleanUpVVDAO();
		return requiredScore;
	}
	
	
	/**************************************************************************************************
	 * Method to connect to absHire Order id status with the User's expected status and score
	 * @param OrderID -  SWest order id whose status and score needs to be verified - String value
	 * @param ExpectedStatus - Expected Order status - String value
	 * @param ExpectedScore - Expected Order score - String value
	 * @throws SQLException
	 * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
	 * @author aunnikrishnan
	 * @created 6/22/2015
	 * @LastModifiedBy
	 ***************************************************************************************************/

	public String verifyOrderStatus(String OrderID,String ExpectedStatus,String ExpectedScore) throws SQLException{
		
		String retval 			= 	Globals.KEYWORD_FAIL;
		OrderScore expScore 	= 	OrderScore.valueOf(ExpectedScore.toUpperCase());
		OrderStatus expStatus 	= 	OrderStatus.valueOf(ExpectedStatus.toUpperCase());
		
		OrderStatus actStatus 	= fetchOrderStatus(OrderID);
		OrderScore actScore 	= fetchOrderScore(OrderID);
		log.debug(" Waiting for the SWest Order status and score to be updated .......................");
		//setting up a counter as sometimes it takes time for  the order level status and score to get updated
		int counter =1;
		do {
			if(expStatus == actStatus && expScore == actScore){
				break;
			}else{
				try {
					Thread.sleep(2000);
					actStatus = fetchOrderStatus(OrderID);
					actScore = fetchOrderScore(OrderID);
				} catch (Exception e) { // exception for thread.sleep
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			counter++;
		
		} while (counter < 30); // timeout required for the ReqPublisher router to update the order status and score
		
		if(expStatus == actStatus){
			seleniumCommands.STAF_ReportEvent("Pass", "Order Status-"+OrderID, "Status is as Expected - "+expStatus, 0);
		}else {
			seleniumCommands.STAF_ReportEvent("Fail", "Order Status-"+OrderID, "Status is NOT as Expected.Expected- "+expStatus+ " Actual-"+actStatus, 0);
		}
		
		if(expScore == actScore){
			seleniumCommands.STAF_ReportEvent("Pass", "Order Score-"+OrderID, "Score is as Expected - "+expScore, 0);
			retval = Globals.KEYWORD_PASS;
		}else {
			seleniumCommands.STAF_ReportEvent("Fail", "Order Score-"+OrderID, "Score is NOT as Expected.Expected- "+expScore+ " Actual-"+actScore, 0);
		}
		
		return retval;
	}
	
	
	
	/**************************************************************************************************
     * Method to fetch the Message body from absHire database of an email Id to which an invite is being sent
     * @param OrderID - EmailID_To - String value
     * @throws SQLException
     * @return Message Body - String 
      * @author rtahiliani
     * @created 18-July-2015
     * @LastModifiedBy
     ***************************************************************************************************/

     public String fetchMessageBody(String EmailID_To) throws SQLException{
     
          try {
	      
	           int totalRecords               = 0;
	           String searchReqQuery            = "select MessageBody from EmailActivity where [To] = ?";
	           
	           this.ps = this.conn.prepareStatement(searchReqQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	           ps.setString(1, EmailID_To);
	           rs = ps.executeQuery();
	           
	           totalRecords = getRows(rs);
		       if(!(totalRecords == 1) ){	              
		    	   seleniumCommands.STAF_ReportEvent("Fail", "fetchMessageBody", "Record Count is not equal to 1 in the EmailActivity database table for the emaild ID "+EmailID_To,0);
		    	   return Globals.KEYWORD_FAIL;
		       }
	                        
	           rs.first();
	           String MessageBody = rs.getString("MessageBody");	          
	           
	           closeConnection();
	           
	           return MessageBody;
           
          } catch(Exception e){
  			log.error("Exception occurred in fetchMessageBody | "+e.toString());
  			throw e;
  		   }
          
          
     }
     
     public String handleCMASearchRequest(int iOrderID,ProductCode productCode ,SearchReqScore score) throws Exception {
    	 
    	 String retval = Globals.KEYWORD_FAIL;
    	 String selectSearchReqQuery = "SELECT  ReqID FROM [abshire].[dbo].[SearchReq] WHERE OrderID = ? AND ProductID = ? ";
    	 String selectAllowancesQuery = "SELECT AdjudicationID,Allowances,AllowancesYellow from  Adjudications where AdjudicationID IN (select AdjudicationID from SearchAdjudications where ReqID = ? )";
    	 String updateHitCountQuery = "UPDATE SearchAdjudications set HitCount = ? ,Status = ? where ReqID =  ? and AdjudicationID=?";
    	 int totalRecords = 0;
    	 int searchReqID = 0;
    	 int allowance = 0;
    	 int yellowAllowance = 0;
    	 int adjudicationID = 0;
    	 int hitCountToBeSet = 0;
    	 CMAStatus cmaStatus = CMAStatus.NA;
    	 PreparedStatement hitPS =null;
    	 PreparedStatement updateHitCountPS = null;
    	 ResultSet hitRS =null;
    	 
    	 try{
    		 	hitPS = this.conn.prepareStatement(selectAllowancesQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        	 	updateHitCountPS = this.conn.prepareStatement(updateHitCountQuery);
        	 
        	 	this.ps = this.conn.prepareStatement(selectSearchReqQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

    			ps.setInt(1, iOrderID);
    			ps.setInt(2, productCode.productID);
    			rs = null; 
    			rs = ps.executeQuery();
    			
    			totalRecords = getRows(rs);
    			if(!(totalRecords >= 1) ){	              
    				seleniumCommands.STAF_ReportEvent("Fail", "CMA Hit Count Update", "Search reqs are not obtained for the Product -"+productCode.productName +" in order-"+iOrderID,0);
    	    	   throw new Exception( "Search reqs are not obtained for the Product -"+productCode.productName +" in order-"+iOrderID);
    	    	   
    			}
    			while(rs.next()){
    				searchReqID = rs.getInt("ReqID");
//    				cmaStatus = cmaStatus.valueOf(score.name());
    				hitPS.setInt(1, searchReqID);
    				hitRS = hitPS.executeQuery();
    				totalRecords= 0;
    				totalRecords = getRows(hitRS);
    				
    				if(!(totalRecords >= 1) ){	              
    					seleniumCommands.STAF_ReportEvent("Fail", "CMA Hit Count Update", "Unable to obtain Allowance limit of CMA for -"+productCode.productName +" in order-"+iOrderID,0);
    		    	   hitPS.close();
    		    	   updateHitCountPS.close();
    		    	   throw new Exception("Unable to obtain Allowance limit of CMA for -"+productCode.productName +" in order-"+iOrderID);
    				}
    				
    				while(hitRS.next()){
    					adjudicationID = hitRS.getInt("AdjudicationID");
    					allowance = hitRS.getInt("Allowances");
    					yellowAllowance = hitRS.getInt("AllowancesYellow");
    					
    					if(score == SearchReqScore.LEVEL1){
    						
    						hitCountToBeSet =allowance;
    						cmaStatus = CMAStatus.LEVEL1;
    					}else if(score == SearchReqScore.LEVEL2){
    						if(yellowAllowance ==0){
    							hitCountToBeSet = yellowAllowance;
    						}else{
    							hitCountToBeSet =yellowAllowance-1;
    						}
    						
    						cmaStatus = CMAStatus.LEVEL2;
    						
    					}else if(score == SearchReqScore.LEVEL3){
    						hitCountToBeSet = yellowAllowance + 1;
    						cmaStatus = CMAStatus.LEVEL3;
    						
    					}else if(score == SearchReqScore.PASSED){
    						hitCountToBeSet =allowance;
    						cmaStatus = CMAStatus.PASS;
    						
    					}else if(score == SearchReqScore.REVIEW){
    						hitCountToBeSet =allowance + 1;
    						cmaStatus = CMAStatus.REVIEW;
    					}
    					
    					totalRecords = 0 ;
    					updateHitCountPS.setInt(1, hitCountToBeSet);
    					updateHitCountPS.setInt(2, cmaStatus.statusID);
    					updateHitCountPS.setInt(3, searchReqID);
    					updateHitCountPS.setInt(4,adjudicationID);
    					totalRecords = updateHitCountPS.executeUpdate();
    					if(!(totalRecords >= 1) ){	              
    						seleniumCommands.STAF_ReportEvent("Fail", "CMA Hit Count Update", "Unable to update HitCount of CMA for -"+productCode.productName +" in order-"+iOrderID,0);
    				    	   break;
    				    	   
    					}
    					
    				
    				}
    				retval = Globals.KEYWORD_PASS;	
                }
    			  			
         

    	 }catch(Exception e){
    		 e.printStackTrace();
    		 throw new Exception("Unable to update CMA hit count.Exception - "+e.toString());
    		 
    	 }finally{
    		 try {
    	            if (rs != null && !rs.isClosed()) {
    	                rs.close();
    	            }
    	        } catch (SQLException ex) {
    	            ex.printStackTrace();
    	        }
    		 try {
 	            if (hitRS != null && !hitRS.isClosed()) {
 	            	hitRS.close();
 	            }
 	        } catch (SQLException ex) {
 	            ex.printStackTrace();
 	        }
    		 try {
  	            if (hitPS != null && !hitPS.isClosed()) {
  	            	hitPS.close();
  	            }
  	        } catch (SQLException ex) {
  	            ex.printStackTrace();
  	        }
    		 try {
   	            if (updateHitCountPS != null && !updateHitCountPS.isClosed()) {
   	            	updateHitCountPS.close();
   	            }
   	        } catch (SQLException ex) {
   	            ex.printStackTrace();
   	        }
    	 }
    	 return retval;
     } 
     
     public void connectVV_DB(){
 		try {
 			String dbURL = Globals.getEnvPropertyValue("dbURL");
 			log.debug(".........Connecting to VV - DatabaseServer-"+dbURL );
 			this.conn = DriverManager.getConnection(dbURL);
 			
 		
 		} catch (Exception e) {
 			
 			e.printStackTrace();
 			log.info("absHire DB Connection-Unable to establish connection.Exception: - "+ e.toString());
 			if (Globals.SendEmails.equalsIgnoreCase("true")) {
 				String toList = Globals.StatusEmailToList+","+Globals.fromEmailID;
 				String ccList = Globals.StatusEmailCCList;
 				String subject = "***Selenium Execution => " +  Globals.getEnvPropertyValue("dbURL") +" DB Connectivity Issue *****";
 				String msgBody = "Unable to connect to VV database.Exception - " + e.toString();
 				Email.send(toList, ccList, subject, "html", msgBody);
 			}
 			
 			this.conn = null;
 		}
 	}
     
     public Object[][] executeSelectQuery(String query) throws SQLException
 	{
 		ResultSetMetaData rsMetaData = null;
 		Object[][] finalResult = null;
 		try
 		{
 			stmt = this.conn.createStatement();
 			rs = stmt.executeQuery(query);
 			rsMetaData = rs.getMetaData();
 			int columnCount = rsMetaData.getColumnCount();
 			ArrayList<Object[]> data = new ArrayList<Object[]>();
 			Object[] header = new Object[columnCount];
 			for(int i=1;i<=columnCount;i++)
 			{
 				Object label =rsMetaData.getColumnLabel(i);
 				header[i-1]=label;
 			}
 			while(rs.next()){
 				Object[] str = new Object[columnCount];
 				for(int i=1;i<=columnCount;i++)
 				{
 					Object obj;
 					obj = rs.getObject(i);
 					str[i-1]=obj;
 				}
 				data.add(str);
 			}
 			int resultSetLength = data.size();
 			finalResult = new Object[resultSetLength][columnCount];
 			for(int i=0;i<resultSetLength;i++)
 			{
 				Object[] row = data.get(i);
 				finalResult[i] = row;  
 			}
 		}catch(Exception ie)
 		{
// 			log.error("Getting Exception While Execution of Query :"+"\t"+ie.toString());
 			ie.printStackTrace();
// 			if (Globals.SendEmails.equalsIgnoreCase("true")) {
// 				String toList = Globals.StatusEmailToList+","+Globals.fromEmailID;
// 				String ccList = Globals.StatusEmailCCList;
// 				String subject = "***Selenium Execution => DB Query Execution Issue *****";
// 				String msgBody = "Unable to connect to the database..Exception - " + ie.toString() + "<br><br><br>";
// 				Email.send(toList, ccList, subject, "html", msgBody);
// 			}
 		}
 		return finalResult;
 	}
	
}


