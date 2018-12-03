package com.sterlingTS.vv;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;


import com.sterlingTS.generic.appLib.AdminClient;
import com.sterlingTS.seleniumUI.seleniumCommands;
import com.sterlingTS.utils.commonUtils.BaseCommon;
import com.sterlingTS.utils.commonUtils.LocatorAccess;
import com.sterlingTS.utils.commonVariables.Globals;

public class ClientHierarchy extends BaseCommon{
	
	
	public static Logger log = Logger.getLogger(AdminClient.class);
	
	public static seleniumCommands sc = new seleniumCommands(driver);
	
	
	/**************************************************************************************************
     * Method to find out notification message getting correctly for api log table for different order status
     * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
     * @author psave
     * @LastModifiedBy
     ***************************************************************************************************/
    
	public static String dropdownValidation() throws Exception {
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=30;
		try{
		tempRetval= sc.waitforElementToDisplay("ClientHierarchy_Dashboard_dd", timeOutInSeconds);
		String expectedItemsAcName = Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpClients");
		String[] expectedItems = expectedItemsAcName.split(",");
		//sc.verifyDropdownItems("report_account_dd", expectedItems, "Client Hierarchy Dropdown");

	
		tempRetval=sc.verifyDropdownItems("ClientHierarchy_Dashboard_dd", expectedItems,"Verify option of Client Hierarchy Dropdown On BG DashBoard");
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Client Hierarchy-BG Dashboard", "Client Hierarchy Dropdown options is not matched as per expected", 1);
			return retval;
		}else{
			sc.STAF_ReportEvent("Pass", "Client Hierarchy_BG Dashboard", "Client Hierarchy Dropdown options is matched as per expected", 1);		
		}
		
		String[] orderNames = {"top parent","child one","sub child one one","sub child one two","child two","sub child two one","sub child two two","child three"};
		String[] expectedIndex={"0","0,1,2,3,4,5,6,7","1","1,2,3","2","3","4","4,5,6","5","6","7"};
		String[] searchInput={"top parent,child one","top parent,child one,sub child one one,sub child one two,child two,sub child two one,sub child two two,child three","child one,sub child one one,sub child one two","child one,sub child one one,sub child one two,top parent,child two","sub child one one,top parent","sub child one two,top parent","child two,top parent,child one","child two,sub child two one,sub child two two,top parent,child one","sub child two one,sub child two two","sub child two two,sub child two one","child three,top parent"};
		String[] expOp={"top parent","top parent,child one,sub child one one,sub child one two,child two,sub child two one,sub child two two,child three","child one","child one,sub child one one,sub child one two","sub child one one","sub child one two","child two","child two,sub child two one,sub child two two","sub child two one","sub child two two","child three"};
		int cnt=0;
		for (String ddElement : expectedItems) {
			tempRetval=sc.selectValue_byIndex("ClientHierarchy_Dashboard_dd", cnt);
			
			String tempRetval1 = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

			while (tempRetval1.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(2000);
				tempRetval1 = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

			}
			//int expectedIndexInt[]=Arrays.stream(expectedIndex[cnt].split(",")).mapToInt(Integer::parseInt).toArray(); 
			String expOpPerDD[]=expOp[cnt].split(",");
			String expIpPerDD[]=searchInput[cnt].split(",");
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				return tempRetval;
			}else{
				 for(int i=0; i<expIpPerDD.length;i++){
					 String searchText=expIpPerDD[i];
					 sc.setValueJsChange("bgDashboard_search_txt", searchText);
					 sc.clickWhenElementIsClickable("bgDashboard_searchBtn_btn", timeOutInSeconds);
					 tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

						while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
							Thread.sleep(2000);
							tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

						}
						
						int flag=0;
						int rowcount =0;
						WebElement searchGrid = sc.createWebElement("bgDashboard_searchResults_tbl");
						while(rowcount > 3){
							sc.setValueJsChange("bgDashboard_search_txt", searchText);
							sc.clickWhenElementIsClickable("bgDashboard_searchBtn_btn", timeOutInSeconds);

							tempRetval = Globals.KEYWORD_FAIL;
							tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

							while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
								Thread.sleep(2000);
								tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

							}
							searchGrid = sc.createWebElement("bgDashboard_searchResults_tbl");
							rowcount =0;
							rowcount = sc.getRowCount_tbl(searchGrid);
							if(flag>3){
								break;
							}
							flag++;
						}			
					    searchGrid = sc.createWebElement("bgDashboard_searchResults_tbl");
						rowcount =0;
						rowcount = sc.getRowCount_tbl(searchGrid);

						if(rowcount > 3){
							sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter", "Multiple volunteers has been found during search.Unable to select the appropriate volunteer", 1);
						}else {
								String noMatchRecords = "";
								
								if(ArrayUtils.contains(expOpPerDD, searchText)){
									noMatchRecords=	searchGrid.findElement(By.xpath("./tbody/tr[1]/td[1]")).getText();
									String errorText="No matching records.";
									if(errorText.equalsIgnoreCase(noMatchRecords)){
										sc.STAF_ReportEvent("Fail", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Unable to Search volunteer by search criteria getting as No match found.search value - "+searchText, 1);
									}else{
										if(!(rowcount==1)){
											for(int col=1;col<=rowcount;col++){
												String volNameUI = searchGrid.findElement(By.xpath("./tbody/tr[1]/td[2]")).getText();
												if(!volNameUI.contains(searchText)){
													sc.STAF_ReportEvent("Fail", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Unable to Search volunteer by search criteria.search value - "+searchText+" Actual Value - "+volNameUI, 1);
													
												}else{
													sc.STAF_ReportEvent("Pass", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Search volunteer by search criteria Successfully.search value - "+searchText+" Actual Value - "+volNameUI, 1);
												}
											}
										}else{
											String volNameUI = searchGrid.findElement(By.xpath("./tbody/tr[1]/td[2]")).getText();
											if(!volNameUI.contains(searchText)){
												sc.STAF_ReportEvent("Fail", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Unable to Search volunteer by search criteria.search value - "+searchText+" Actual Value - "+volNameUI, 1);
												
											}else{
												sc.STAF_ReportEvent("Pass", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Search volunteer by search criteria Successfully.search value - "+searchText+" Actual Value - "+volNameUI, 1);
											}
										}
									}
								}else{
									noMatchRecords=	searchGrid.findElement(By.xpath("./tbody/tr[1]/td[1]")).getText();
									String errorText="No matching records.";
									if(!errorText.equalsIgnoreCase(noMatchRecords)){
										sc.STAF_ReportEvent("Fail", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Getting search criteria not as per expected - Expected should be No match found for search value - "+searchText, 1);
									}else{
										sc.STAF_ReportEvent("Pass", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Getting search criteria as per expected - Expected should be No match found for search value - "+searchText, 1);
									}
								}
									
									
										
					 } 
					 
				 }
				 
				
				
			}
			cnt=cnt+1;
			
		}
		
		}catch(Exception e){
				  
			sc.STAF_ReportEvent("Fail", "Client Hierarchy-BG Dashboard", "Client Hierarchy Dropdown options is matched as per expected",1);
			throw e;
		}
		retval = Globals.KEYWORD_PASS;
		return retval;
	}
    
	/**************************************************************************************************
     * Method to find out notification message getting correctly for api log table for different order status
     * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
     * @author psave
     * @LastModifiedBy
     ***************************************************************************************************/
    
	public static String dropdownValidation1() throws Exception {
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=30;
		int flag=0;
		try{
		tempRetval= sc.waitforElementToDisplay("ClientHierarchy_Dashboard_dd", timeOutInSeconds);
		String expectedItemsAcName = Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpClients");
		String[] expectedItems = expectedItemsAcName.split(",");
		//sc.verifyDropdownItems("report_account_dd", expectedItems, "Client Hierarchy Dropdown");
	
		Select hierarchyDropDwn = new Select(driver.findElement(By.xpath("//select[@id='childAccountSelect']")));
		String selectedVal=hierarchyDropDwn.getFirstSelectedOption().getText();
		if(!selectedVal.contains("VVReg1 Child 3")){
			sc.STAF_ReportEvent("Fail", "Volunteer Dashboard-Client hierarchy DD default value", "Default value for ClientHierarchy Dropdown should be VVReg1 Child 3. Actual :"+selectedVal, 1);
		}			
			
		tempRetval=sc.verifyDropdownItems("ClientHierarchy_Dashboard_dd", expectedItems,"Verify option of Client Hierarchy Dropdown On BG DashBoard");
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Client Hierarchy-BG Dashboard", "Client Hierarchy Dropdown options is not matched as per expected", 1);
			return retval;
		}else{
			sc.STAF_ReportEvent("Pass", "Client Hierarchy_BG Dashboard", "Client Hierarchy Dropdown options is matched as per expected", 1);		
		}
		
		String[] orderNames = {"top parent","child one","sub child one one","sub child one two","child two","sub child two one","sub child two two","child three"};
		String[] expectedIndex={"0","0,1,2,3,4,5,6,7","1","1,2,3","2","3","4","4,5,6","5","6","7"};
		String[] searchInput={"top parent,child one","top parent,child one,sub child one one,sub child one two,child two,sub child two one,sub child two two,child three","child one,sub child one one,sub child one two","child one,sub child one one,sub child one two,top parent,child two","sub child one one,top parent","sub child one two,top parent","child two,top parent,child one","child two,sub child two one,sub child two two,top parent,child one","sub child two one,sub child two two","sub child two two,sub child two one","child three,top parent"};
		String[] expOp={"top parent","top parent,child one,sub child one one,sub child one two,child two,sub child two one,sub child two two,child three","child one","child one,sub child one one,sub child one two","sub child one one","sub child one two","child two","child two,sub child two one,sub child two two","sub child two one","sub child two two","child three"};
		int cnt=0;
		for (String ddElement : expectedItems) {
			tempRetval=sc.selectValue_byIndex("ClientHierarchy_Dashboard_dd", cnt);
			while(tempRetval.contains(ddElement)) {
				
				if(flag>3){
						break;
										
				}
				
				tempRetval=sc.selectValue_byIndex("ClientHierarchy_Dashboard_dd", cnt);
				flag++;
			}
			String tempRetval1 = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

			while (tempRetval1.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(2000);
				tempRetval1 = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

			}
			//int expectedIndexInt[]=Arrays.stream(expectedIndex[cnt].split(",")).mapToInt(Integer::parseInt).toArray(); 
			String expOpPerDD[]=expOp[cnt].split(",");
			String expIpPerDD[]=searchInput[cnt].split(",");
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				return tempRetval;
			}else{
				 for(int i=0; i<expIpPerDD.length;i++){
					 String searchText=expIpPerDD[i];
					 sc.setValueJsChange("volDashboard_searhValue_txt", searchText);
					 WebElement ddMenu = sc.createWebElement("volDashboard_searchBy_dd");
					 sc.selectValue_byVisibleText(ddMenu, "First Name");
					 sc.clickWhenElementIsClickable("volDashboard_search_btn", timeOutInSeconds);
					 tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

						while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
							Thread.sleep(2000);
							tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

						}
						
						flag=0;
						int rowcount =0;
						WebElement searchGrid = sc.createWebElement("volDashboard_volgrid_tbl");
						while(rowcount > 3){
							sc.setValueJsChange("volDashboard_searhValue_txt", searchText);
							sc.clickWhenElementIsClickable("volDashboard_search_btn", timeOutInSeconds);

							tempRetval = Globals.KEYWORD_FAIL;
							tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

							while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
								Thread.sleep(2000);
								tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

							}
							searchGrid = sc.createWebElement("volDashboard_volgrid_tbl");
							rowcount =0;
							rowcount = sc.getRowCount_tbl(searchGrid);
							if(flag>3){
								break;
							}
							flag++;
						}			
					    searchGrid = sc.createWebElement("volDashboard_volgrid_tbl");
						rowcount =0;
						rowcount = sc.getRowCount_tbl(searchGrid);

						if(rowcount > 3){
							sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter", "Multiple volunteers has been found during search.Unable to select the appropriate volunteer", 1);
						}else {
								String noMatchRecords = "";
								
								if(ArrayUtils.contains(expOpPerDD, searchText)){
									noMatchRecords=	searchGrid.findElement(By.xpath("./tbody/tr[1]/td[1]")).getText();
									String errorText="No matching records.";
									if(errorText.equalsIgnoreCase(noMatchRecords)){
										sc.STAF_ReportEvent("Fail", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Unable to Search volunteer by search criteria getting as No match found.search value - "+searchText, 1);
									}else{
										if(!(rowcount==1)){
											for(int col=1;col<=rowcount;col++){
												String volNameUI = searchGrid.findElement(By.xpath("./tbody/tr[1]/td[2]")).getText();
												if(!volNameUI.contains(searchText)){
													sc.STAF_ReportEvent("Fail", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Unable to Search volunteer by search criteria.search value - "+searchText+" Actual Value - "+volNameUI, 1);
													
												}else{
													sc.STAF_ReportEvent("Pass", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Search volunteer by search criteria Successfully.search value - "+searchText+" Actual Value - "+volNameUI, 1);
												}
											}
										}else{
											String volNameUI = searchGrid.findElement(By.xpath("./tbody/tr[1]/td[2]")).getText();
											if(!volNameUI.contains(searchText)){
												sc.STAF_ReportEvent("Fail", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Unable to Search volunteer by search criteria.search value - "+searchText+" Actual Value - "+volNameUI, 1);
												
											}else{
												sc.STAF_ReportEvent("Pass", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Search volunteer by search criteria Successfully.search value - "+searchText+" Actual Value - "+volNameUI, 1);
											}
										}
									}
								}else{
									noMatchRecords=	searchGrid.findElement(By.xpath("./tbody/tr[1]/td[1]")).getText();
									String errorText="No matching records.";
									if(!errorText.equalsIgnoreCase(noMatchRecords)){
										sc.STAF_ReportEvent("Fail", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Getting search criteria not as per expected - Expected should be No match found for search value - "+searchText, 1);
									}else{
										sc.STAF_ReportEvent("Pass", "BG Dashboard-Client hierarchy DD search Criteria- "+ddElement, "Getting search criteria as per expected - Expected should be No match found for search value - "+searchText, 1);
									}
								}
									
									
										
					 } 
					 
				 }
				 
				
				
			}
			cnt=cnt+1;
			
		}
		
		}catch(Exception e){
				  
			sc.STAF_ReportEvent("Fail", "Client Hierarchy-BG Dashboard", "Client Hierarchy Dropdown options is matched as per expected",1);
			throw e;
		}
		retval = Globals.KEYWORD_PASS;
		return retval;
	}
	
	
	/**************************************************************************************************
     * Method for validation of drop down for ordering (invitation,client,batch) 
     * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
     * @author psave
     * @LastModifiedBy
     ***************************************************************************************************/
    
	public static String validationOrderingDropdown() throws Exception {
		
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=30;
		int hierarchyDDIndex,orderHierrarchyDDIndex;
		
		int flag=0;
		try{
			String invitationInputs[]={"Invitation","1,1","VVReg1 Child 1 (This Account)","vvreg1 child 1 position-L1 - Volunteer Pays All"};
			String invitationInputs1[]={"Invitation","3,2","VVReg1 Sub Child 1-1","VVReg1 Sub Child 1-1 - L1 - Volunteer Pays All"};
			String uploadOrderInputs[]={"Upload","1,1","VVReg1 Child 1 (This Account)","vvreg1 child 1 position-L1 - Volunteer Pays All"};
			String clientOrderInputs[]={"Client","1,1","VVReg1 Child 1 (This Account)","vvreg1 child 1 position-L1 - Volunteer Pays All"};			
			String batchOrderInputs[]={"Batch","1,0","VVReg1 Top Parent (This Account)","VVReg1 Top Parent-L1"};
			
			ArrayList<String[]> listOfLists = new ArrayList<String[]>();
			listOfLists.add(invitationInputs);
			listOfLists.add(invitationInputs1);
			listOfLists.add(clientOrderInputs);
			listOfLists.add(batchOrderInputs);
		
			for(String[] inputs:listOfLists){
				tempRetval=dropdownValidationOrdering(inputs);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Validation getting failed", 1);
					throw new Exception("Validation getting failed for Client Hierarchy-"+inputs[0]+" Order-Single A/C selection Dropdown validation");
				}
			}		
			
		}catch(Exception e){
			log.error("Unable to perform validation on Client Hierarchy Dropdown validation page | Exception - "+e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		retval = Globals.KEYWORD_PASS;
		return retval;
	}

     
	/**************************************************************************************************
     * Method for validation of drop down for each type of ordering
     * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
     * @author psave
     * @LastModifiedBy
     ***************************************************************************************************/
    
	public static String dropdownValidationOrdering(String[] inputs) throws Exception {
		
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=30;
		int hierarchyDDIndex,orderHierrarchyDDIndex;
		int flag=0;
		try{
			hierarchyDDIndex=Integer.parseInt(inputs[1].split(",")[0]);	
			orderHierrarchyDDIndex=Integer.parseInt(inputs[1].split(",")[1]);	
			tempRetval= sc.waitforElementToDisplay("ClientHierarchy_Dashboard_dd", timeOutInSeconds);
		
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Client Hierarchy Dropdown options is not displayed", 1);
				return retval;
			}
			tempRetval=sc.selectValue_byIndex("ClientHierarchy_Dashboard_dd", hierarchyDDIndex);//HierarchyDDIndex
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Client Hierarchy Dropdown -Unable to select dropdown value VVReg1 Top Parent (All Accounts) ", 1);
				return retval;
			}
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

			while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(2000);
				tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

			}
			
			if(inputs[0].equalsIgnoreCase("Invitation")){
				tempRetval=sc.waitforElementToDisplay("volDashboard_communications_btn", timeOutInSeconds);
				sc.clickWhenElementIsClickable("volDashboard_communications_btn",timeOutInSeconds);
				sc.clickWhenElementIsClickable("volDashboard_sendInvitation_btn",timeOutInSeconds);
			}else if(inputs[0].equalsIgnoreCase("Client")||inputs[0].equalsIgnoreCase("Batch")){
				tempRetval = sc.waitforElementToDisplay("volDashboard_orderBackgroundCheck_btn", timeOutInSeconds);
				sc.clickWhenElementIsClickable("volDashboard_orderBackgroundCheck_btn",timeOutInSeconds);
			}else if(inputs[0].equalsIgnoreCase("Upload")){
				tempRetval = sc.waitforElementToDisplay("volDashboard_upload_btn", timeOutInSeconds);
				sc.clickWhenElementIsClickable("volDashboard_upload_btn",timeOutInSeconds);
			}
		
			tempRetval=sc.waitforElementToDisplay("ClientHierarchy_DashboardOrder_dd", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "client selection Dropdown for "+inputs[0]+" Order is not getting displayed", 1);
				return retval;
			}
			String[] expectedItems = {"VVReg1 Top Parent (This Account)","» VVReg1 Child 1 (This Account)","» VVReg1 Sub Child 1-1","» VVReg1 Sub Child 1-2","» VVReg1 Child 2 (This Account)","» VVReg1 Sub Child 2-1","» VVReg1 Sub Child 2-2","» VVReg1 Child 3"};
			tempRetval=sc.verifyDropdownItems("ClientHierarchy_DashboardOrder_dd", expectedItems,"Client Hierarchy-"+inputs[0]+" Ordering DD");
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "client selection Dropdown options is not matched as per expected", 1);
				return retval;
			}
			tempRetval = sc.selectValue_byIndex("ClientHierarchy_DashboardOrder_dd", orderHierrarchyDDIndex );//orderHierrarchyDDIndex
			if(!tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Pass", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Client selection Dropdown for "+inputs[0]+" Order is getting displayed", 1);
			} 
			sc.clickWhenElementIsClickable("ClientHierarchy_DashboardOrder_Ok_btn",timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

			while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(2000);
				tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

			}
		
			if(inputs[0].equalsIgnoreCase("Invitation")){
		
				tempRetval=sc.waitforElementToDisplay("sendInvitation_postionListing_dd", timeOutInSeconds);
		
				tempRetval = sc.selectValue_byVisibleText("sendInvitation_postionListing_dd", inputs[3]);//input position
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Unable to select position "+inputs[3]+" from send Invitation dropdown", 1);
					return retval;
				}
				sc.clickWhenElementIsClickable("sendInvitation_close_btn",timeOutInSeconds);
				Thread.sleep(3000);;
			}else if(inputs[0].equalsIgnoreCase("Upload")){
				tempRetval=sc.waitforElementToDisplay("uploadOrdering_sendInvitation_rdb", timeOutInSeconds);
				sc.scrollIntoViewUsingJavaScript("uploadOrdering_sendInvitation_rdb");
				sc.clickElementUsingJavaScript("uploadOrdering_sendInvitation_rdb");
				
				tempRetval=sc.waitforElementToDisplay("uploadOrdering_position_dd", timeOutInSeconds);
			
				tempRetval = sc.selectValue_byVisibleText("uploadOrdering_position_dd", inputs[3]);//input position
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Unable to select position "+inputs[3]+" from Upload Invitation dropdown", 1);
					return retval;
				}
				sc.clickWhenElementIsClickable("uploadordering_popupclose_btn",timeOutInSeconds);
				Thread.sleep(3000);;
			}
		
			String selectedVal=new Select(driver.findElement(LocatorAccess.getLocator("ClientHierarchy_Dashboard_dd"))).getFirstSelectedOption().getText();
			if(selectedVal.contains(inputs[2])){    //input[1]
				sc.STAF_ReportEvent("Pass", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Client Hierarchy Dropdown value is match as per client value selected form "+inputs[0]+" Single A/C selection DD", 1);
			}else{
				sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Client Hierarchy Dropdown value is not match as per client value selected form "+inputs[0]+" Ordering DD Expxcted : "+inputs[2], 1);
				return retval;
			}
		
			if(inputs[0].equalsIgnoreCase("Batch")){
				tempRetval = sc.waitforElementToDisplay("volDashboard_orderBackgroundCheck_btn", timeOutInSeconds);
				sc.clickWhenElementIsClickable("volDashboard_orderBackgroundCheck_btn",timeOutInSeconds);
				sc.clickWhenElementIsClickable("volDashboard_placeABatchOrder_btn", timeOutInSeconds);
				tempRetval = sc.waitforElementToDisplay("clientStep1_choosePosition_dd", timeOutInSeconds);

				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Batch Ordering Step 1 has not loaded", 1);
					throw new Exception("Batch Ordering Step 1 has not loaded");
				}

				tempRetval=sc.selectValue_byVisibleText("clientStep1_choosePosition_dd", inputs[3]);//input position
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Unable to select position "+inputs[3]+" from Batch Ordering position dropdown", 1);
					return retval;
				}
				retval= ClientFacingApp.navigateVolunteerDashboard();
			}
			
			if(inputs[0].equalsIgnoreCase("Client") && inputs[2].equalsIgnoreCase("VVReg1 Child 1 (This Account)")){
				tempRetval = sc.waitforElementToDisplay("volDashboard_orderBackgroundCheck_btn", timeOutInSeconds);
				tempRetval=sc.isEnabled("volDashboard_placeABatchOrder_btn");
				retval=sc.isEnabled("volDashboard_reviewBatchOrder_btn");

				if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL) && retval.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
					sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Batch Order is not disabled for VVReg1 Child 1 (This Account) ", 1);
					
				}

				
			}
		
			
		}catch(Exception e){
				  
			sc.STAF_ReportEvent("Fail", "Client Hierarchy-"+inputs[0]+" Order-Single A/C selection DD", "Validation getting failed for Client Hierarchy-"+inputs[0]+" Order-Single ac selection Dropdown validation",1);
			throw e;
		}
		retval = Globals.KEYWORD_PASS;
		return retval;
	}
	
	/**************************************************************************************************
     * Method for select single client from client hierarchy dropdown for any ordering type 
     * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
     * @author psave
     * @LastModifiedBy
     ***************************************************************************************************/
    
	public static String sendInvitationClientHierarchy() throws Exception {
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;
		String tempRetval=Globals.KEYWORD_FAIL;
		try{
			tempRetval=sc.waitforElementToDisplay("volDashboard_communications_btn", timeOutinSeconds);
			sc.clickWhenElementIsClickable("volDashboard_communications_btn",timeOutinSeconds);
			sc.clickWhenElementIsClickable("volDashboard_sendInvitation_btn",timeOutinSeconds);
			tempRetval=sc.waitforElementToDisplay("ClientHierarchy_DashboardOrder_dd", timeOutinSeconds);
			

		}catch(Exception e){
				  
			sc.STAF_ReportEvent("Fail", "Client Hierarchy Order-Single A/C selection DD", "Validation getting failed for Client Hierarchy- Order-Single ac selection Dropdown validation",1);
			throw e;
		}
		retval = Globals.KEYWORD_PASS;
		return retval;
	}
	
	/**************************************************************************************************
     * Method for select single client from client hierarchy dropdown for any ordering type 
     * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
     * @author psave
     * @LastModifiedBy
     ***************************************************************************************************/
    
	public static String selectClientFromClientHierarchyOrderDD() throws Exception {
		
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=30;
		int hierarchyDDIndex,orderHierrarchyDDIndex;
		int flag=0;
		try{
			
			
		}catch(Exception e){
				  
			sc.STAF_ReportEvent("Fail", "Client Hierarchy Order-Single A/C selection DD", "Validation getting failed for Client Hierarchy- Order-Single ac selection Dropdown validation",1);
			throw e;
		}
		retval = Globals.KEYWORD_PASS;
		return retval;
	}


}