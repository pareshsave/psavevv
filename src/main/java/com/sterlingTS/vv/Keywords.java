package com.sterlingTS.vv;


import java.sql.ResultSet;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.sterlingTS.generic.appLib.AdminClient;
import com.sterlingTS.seleniumUI.seleniumCommands;
import com.sterlingTS.utils.commonUtils.APP_LOGGER;
import com.sterlingTS.utils.commonUtils.BaseCommon;
import com.sterlingTS.utils.commonUtils.CommonHelpMethods;
import com.sterlingTS.utils.commonUtils.Email;
import com.sterlingTS.utils.commonUtils.LocatorAccess;
import com.sterlingTS.utils.commonUtils.ProtectedEncryption;
//import com.sterlingTS.utils.commonUtils.database.DAO;
import com.sterlingTS.utils.commonVariables.Globals;
//import genResource.genericLib.GenUtils;

public class Keywords extends BaseCommon{
    public static Logger log = Logger.getLogger(AdminClient.class);
	
	public static seleniumCommands sc = new seleniumCommands(driver);

	/**************************************************************************************************
	 * Method to launch VV Admin Portal and login using sterling staff credentials
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String StaffLogin() throws Exception{
		return InhousePortal.staffLogin();
	}

	/**************************************************************************************************
	 * Method to search a client in VV Admin Portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/

	public String InhouseAccountSearch() throws Exception {
		APP_LOGGER.startFunction("InhouseAccountSearch");
		String retval=Globals.KEYWORD_FAIL;
		int rowID = Globals.INT_FAIL;

		try {
			String accountName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
			rowID = InhousePortal.searchAccount();
			if(rowID == Globals.INT_FAIL){
				log.error("Method-InhouseAccountSearch | Unable to search acount");
				sc.STAF_ReportEvent("Fail", "Account Search", "Account not found", 1);
				return retval;
			}

			sc.STAF_ReportEvent("Pass", "Account Search", "Account Found - "+accountName, 1);
			driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+rowID+"]")).click();
			retval=Globals.KEYWORD_PASS;
		} catch (Exception e) {
			sc.STAF_ReportEvent("Fail", "Account Search", "Exception occurred while searching account", 1);
			log.error("Inhouse Portal-Account Search | Unable to search acount|  Exception occurred - " + e.toString());
			throw e;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to create a new position in VV Admin Portal for a client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CreatePosition() throws Exception {
		return InhousePortal.CreatePosition();
	
	}
	
	/**************************************************************************************************
	 * Method to validate the  new position created in VV Admin Portal for a client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String verifyCreatedPosition() throws Exception {
		//return InhousePortal.verifyCreatedPosition();
		APP_LOGGER.startFunction("PositionSearch");
		String retval=Globals.KEYWORD_FAIL;
		int rowID = Globals.INT_FAIL;
	
		try {
			String positionName = Globals.testSuiteXLS.getCellData_fromTestData("PositionName");
			rowID = InhousePortal.verifyCreatedPosition();
			if(rowID == Globals.INT_FAIL){
				log.error("Method-PositionSearch | Unable to search Position");
				sc.STAF_ReportEvent("Fail", "Position Search", "Position not found", 1);
				return retval;
			}
	
			sc.STAF_ReportEvent("Pass", "Position Search", "Position Found - "+positionName, 1);
			driver.findElement(By.xpath("//table[@class='results']/tbody/tr["+rowID+"]")).click();
			retval=Globals.KEYWORD_PASS;
		} catch (Exception e) {
			sc.STAF_ReportEvent("Fail", "Position Search", "Exception occurred while searching Position", 1);
			log.error("Inhouse Portal-Position Search | Unable to search Position|  Exception occurred - " + e.toString());
			throw e;
		}
	
		return retval;
	
	}
	/**************************************************************************************************
	 * Method to validate edit position in VV Admin Portal for a client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String EditPosition() throws Exception {
		return InhousePortal.EditPosition();
	
	}
	/**************************************************************************************************
	 * Method to verify edited position in Volunteer Dashboard & BG Dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String verifyEditedPosition() throws Exception {
		APP_LOGGER.startFunction("PositionSearch");
		String retval=Globals.KEYWORD_FAIL;
		int rowID = Globals.INT_FAIL;
	
		try {
			String positionName = Globals.testSuiteXLS.getCellData_fromTestData("PositionName")+"-EditedPosition";
			rowID = ClientFacingApp.verifyEditedPositionBG();
			ClientFacingApp.navigateVolunteerDashboard();
			rowID = ClientFacingApp.verifyEditedPositionVolunteerDashBoard();
			ClientFacingApp.verifyEditedPositionOrdering();
//			if(rowID == Globals.INT_FAIL){
//				log.error("Method-PositionSearch | Unable to search Position");
//				sc.STAF_ReportEvent("Fail", "Position Search", "Position not found", 1);
//				return retval;
//			}
	
			sc.STAF_ReportEvent("Pass", "Position Search", "Position Found - "+positionName, 1);
			retval=Globals.KEYWORD_PASS;
		} catch (Exception e) {
			sc.STAF_ReportEvent("Fail", "Position Search", "Exception occurred while searching Position", 1);
			log.error("BG Dashboard-searchPosition | Unable to search Position|  Exception occurred - " + e.toString());
			throw e;
		}
	
		return retval;
	
	}

	/**************************************************************************************************
	 * Method to reset the  edited position name to original value in VV Admin Portal for a client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ResetEditPosition() throws Exception {
		return InhousePortal.ResetEditPosition();
	
	}

	/**************************************************************************************************
	 * Method to create a new volunteer account through Good Deed Ordering workflow in Volunteer Portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/

	public String CreateVolunteerAccount() throws Exception {
		return ClientFacingApp.volunteerAccountCreation();

	}

	/**************************************************************************************************
	 * Method for an organization user login by launching the corresponding url
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String OrgUserLogin() throws Exception {
		APP_LOGGER.startFunction("OrgUserLogin");
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		try {
			try{// the below code would throw an exception when the method is called for first cause no browser has been launchde yet
				if(driver != null && !driver.toString().contains("null")){
					driver.switchTo().defaultContent();
					tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", 5);

					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						LogOff();

					}
				}

			}catch(Exception e){
				log.debug("OrgUserLogin - No Browser launched yet");
			}

			String urlLaunched= sc.launchURL(Globals.BrowserType,Globals.getEnvPropertyValue("AppURL"));

			if (urlLaunched.equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.verifyPageTitle("Log in").equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			//try one more time by checking whether session is already open.if so log out and continue
				tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", 3);

				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					tempRetval = Globals.KEYWORD_FAIL;
					tempRetval = LogOff();
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						
							sc.STAF_ReportEvent("Fail", "OrganizationUserLogin", "Login Page has NOT been loaded", 1);
							log.error("Organizational Unable to Log In As URL not Launched ");
							throw new Exception("Organizational user Unable to Log In As Lo page has not Launched");
					}
						
				}
			}

				sc.STAF_ReportEvent("Pass", "OrganizationUserLogin", "Login Page has been loaded", 1);

				//field level validation for login page
				// verify and set account details
				String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
               
				if(fieldLevelValidation.equalsIgnoreCase("Yes")){
					ClientFacingApp.verifyOrgUserLoginPage();
				}


				String orgName=Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
				String orgUserName=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
				String orgUserPwd=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
				
				sc.addParamVal_InEmail("Organization", orgName);
				sc.addParamVal_InEmail("OrgUserName", orgUserName);
				
				tempRetval=sc.setValueJsChange("login_orgUsername_txt", orgUserName);
				tempRetval=sc.setValueJsChange("login_orgPassword_txt",orgUserPwd);

				log.debug(tempRetval + " Setting value for username and password for Org login");
				//tempRetval=sc.highlight("login_orgLogIn_btn");
				sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
				
				//if org username password is expired then modify change password date and relogin again
				if (!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
					tempRetval = sc.waitforElementToDisplay("passwordReset_changePassword_btn", timeOutInSeconds);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", 5);

						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							LogOff();

						}
						tempRetval=verifyOrgUserPassExp();
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
							
							sc.STAF_ReportEvent("Fail", "OrganizationUserLogin", "Unable to update password expiration date", 1);
							log.error("Organizational Unable to Log In As Unable to update password expiration date");
							throw new Exception("Organizational user Unable to Log In As Unable to update password expiration date");
						}else{
								if (sc.verifyPageTitle("Log in").equalsIgnoreCase(Globals.KEYWORD_FAIL)){
									//try one more time by checking whether session is already open.if so log out and continue
									tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", 3);
	
									if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
										tempRetval = Globals.KEYWORD_FAIL;
										tempRetval = LogOff();
										if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
										
											sc.STAF_ReportEvent("Fail", "OrganizationUserLogin", "Login Page has NOT been loaded", 1);
											log.error("Organizational Unable to Log In As URL not Launched ");
											throw new Exception("Organizational user Unable to Log In As Lo page has not Launched");
										}
										
									}
								}
						}
						
						tempRetval=sc.setValueJsChange("login_orgUsername_txt", orgUserName);
						tempRetval=sc.setValueJsChange("login_orgPassword_txt",orgUserPwd);
	
						log.debug(tempRetval + " Setting value for username and password for Org login");
						//tempRetval=sc.highlight("login_orgLogIn_btn");
						sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
											
			     	}
				}
				//if user is logging in for the first time, consent page is loaded
				tempRetval = Globals.KEYWORD_FAIL;
				tempRetval = sc.waitforElementToDisplay("consent_termsAndConditions_chk",10);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.checkCheckBox("consent_termsAndConditions_chk");
					sc.clickWhenElementIsClickable("consent_submit_btn", timeOutInSeconds);
				}
				
				//if survey page is visible 
				tempRetval = sc.waitforElementToDisplay("Survey_skip_btn",5);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.clickWhenElementIsClickable("Survey_skip_btn", timeOutInSeconds);
				}
				
				tempRetval = sc.waitforElementToDisplay("clientBGDashboard_resourceHubPopup_window",5);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.clickElementUsingJavaScript("clientBGDashboard_resourceHubPopupClose_btn");
				}

				if (sc.waitforElementToDisplay("clientBGDashboard_resourceHub_link",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){
					log.info("Organization User Logged In | Org Username - "+ orgUserName + " | Org Password - "+orgUserPwd);
					sc.STAF_ReportEvent("Pass", "VVOU19 - Verify the Log in button when the username and password fields are valid.", "User has logged in - " + orgUserName , 1);
					retval=Globals.KEYWORD_PASS;	

				}else{
					sc.STAF_ReportEvent("Fail", "VVOU19 - Verify the Log in button when the username and password fields are valid.", "User unable to log in - " + orgUserName , 1);
					log.error("Organizational user unable to Log In ");
				}	

		}catch(Exception e){
			log.error("Exception occurred in Organization user Login | "+e.toString());
			throw e;
		}

		return retval;
	}
	
	/**************************************************************************************************
	 * Method for an organization user login by launching the report url
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String ReportURLOrgUserLogin() throws Exception {
		APP_LOGGER.startFunction("ReportURL - OrgUserLogin");
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		try {
			try{// the below code would throw an exception when the method is called for first cause no browser has been launchde yet
				if(driver != null && !driver.toString().contains("null")){
					driver.switchTo().defaultContent();
					tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", 5);

					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						LogOff();

					}
				}

			}catch(Exception e){
				log.debug("OrgUserLogin - No Browser launched yet");
			}
            String url=Globals.testSuiteXLS.getCellData_fromTestData("InvitationURL");
			String urlLaunched= sc.launchURL(Globals.BrowserType,url);

			if (urlLaunched.equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.verifyPageTitle("Adjudicate").equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			//try one more time by checking whether session is already open.if so log out and continue
				tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", 3);

				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					tempRetval = Globals.KEYWORD_FAIL;
					tempRetval = LogOff();
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						
							sc.STAF_ReportEvent("Fail", "OrganizationUserLogin", "Login Page has NOT been loaded", 1);
							log.error("Organizational Unable to Log In As URL not Launched ");
							throw new Exception("Organizational user Unable to Log In As Lo page has not Launched");
					}
						
				}
			}

				String orgName=Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
				String orgUserName=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
				String orgUserPwd=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
				
				sc.addParamVal_InEmail("Organization", orgName);
				sc.addParamVal_InEmail("OrgUserName", orgUserName);
				
				tempRetval=sc.setValueJsChange("login_orgUsername_txt", orgUserName);
				tempRetval=sc.setValueJsChange("login_orgPassword_txt",orgUserPwd);

				log.debug(tempRetval + " Setting value for username and password for Org login");
				//tempRetval=sc.highlight("login_orgLogIn_btn");
				sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);

				//if user is logging then Bg report page should be open
				tempRetval = sc.waitforElementToDisplay("Survey_skip_btn",20);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.clickWhenElementIsClickable("Survey_skip_btn", timeOutInSeconds);
				}
				
				if (sc.waitforElementToDisplay("bgReport_review_link",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){
					log.info("Organization User Logged In | Org Username - "+ orgUserName + " | Org Password - "+orgUserPwd);
					sc.STAF_ReportEvent("Pass", "ReportURL - OrgUserLogin", "User has logged in - " + orgUserName , 1);
					retval=Globals.KEYWORD_PASS;	

				}else{
					sc.STAF_ReportEvent("Fail", "ReportURL - OrgUserLogin", "User unable to log in - " + orgUserName , 1);
					log.error("Organizational user unable to Log In ");
				}

			
			

		}catch(Exception e){
			log.error("Exception occurred in Organization user Login | "+e.toString());
			throw e;
		}

		return retval;
	}


	/**************************************************************************************************
	 * Method to create a order using Client Ordering workflow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/

	public String CreateClientOrder() throws Exception {
		APP_LOGGER.startFunction("CreateClientOrder");

		String retval = Globals.KEYWORD_FAIL;
		HashMap<String,String> volDashboard_beforeOrderCreation = null;
		
		String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");

		if(fieldLevelValidation.equalsIgnoreCase("Yes")){
			volDashboard_beforeOrderCreation = ClientFacingApp.fetchVolunteerDashboardCount();
		}
				
		retval= ClientFacingApp.ClientOrdering();
		
		
		if(fieldLevelValidation.equalsIgnoreCase("Yes")){
			ClientFacingApp.navigateVolunteerDashboard();
			HashMap<String,String> volDashboard_AfterOrderCreation = ClientFacingApp.fetchVolunteerDashboardCount();
			ClientFacingApp.verifyVolunteerDashboard(volDashboard_beforeOrderCreation,volDashboard_AfterOrderCreation,"BackgroundCheckPending",1);
			retval = ClientFacingApp.verifyVolunteerDashboard(volDashboard_beforeOrderCreation,volDashboard_AfterOrderCreation,"ViewAllVolunteers",1);
		}
		
		return retval;
	}

	/**************************************************************************************************
	 * Method to complete Step 1 of Client Ordering workflow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClientOrderStep1() throws Exception{
		return ClientFacingApp.clientOrderingStep1();
	}

	/**************************************************************************************************
	 * Method to complete Step 2 for  Client Ordering workflow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClientOrderStep2() throws Exception{
		return ClientFacingApp.clientOrderingStep2();
	}

	/**************************************************************************************************
	 * Method to click on Place an Order present on the Volunteer Dashboard page.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClickOnCreateClientOrder() throws Exception{
		return ClientFacingApp.clickOnCreateClientOrder();
	}


	/**************************************************************************************************
	 * Method to complete Step 1 in Client Ordering workflow.Created for functional testing phase
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClientOrderingStep1() throws Exception{
		return ClientFacingApp.clientOrderingStep1();
	}


	/**************************************************************************************************
	 * Method for an organization user to log out from the application
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String LogOff() throws Exception {
		APP_LOGGER.startFunction("LogOff");
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 60;
		String retval=Globals.KEYWORD_FAIL;

		try{
			driver.switchTo().defaultContent();// this is done as in some cases we are switching to frames for report validations.

			tempRetval = sc.waitforElementToDisplay("clientBGDashboard_logout_link", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				//				driver.findElement(LocatorAccess.getLocator("clientBGDashboard_logout_link")).click();
				sc.clickWhenElementIsClickable("clientBGDashboard_logout_link",(int) timeOutInSeconds  );

				tempRetval = Globals.KEYWORD_FAIL;
				tempRetval = sc.waitforElementToDisplay("login_orgUsername_txt", timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

					sc.STAF_ReportEvent("Pass", "Org UserLogOff", "Org user log off successful", 1);
					log.info("Method - LogOff | Successfully loggged off from VV Aplication");
					retval=Globals.KEYWORD_PASS;

				}else{
					sc.STAF_ReportEvent("Fail", "Org UserLogOff", "Unable to log off from VV Aplication", 1);

				}
			}
		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-LogOff | Exception - "+ e.toString());
			throw e;
		}
		//sc.closeAllBrowsers();
		return retval;

	}

	/**************************************************************************************************
	 * Method for to navigate to volunteer dashboard for a logged in  Organization user
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String navigateVolunteerDashboard() throws Exception {
		APP_LOGGER.startFunction("navigateVolunteerDashboard");
		String retval = Globals.KEYWORD_FAIL;
		try {

			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation.equalsIgnoreCase("Yes")){
				ClientFacingApp.verifyOrgUserHomepage();
			}

			retval= ClientFacingApp.navigateVolunteerDashboard();



		} catch (Exception e) {

			sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Unable to navigate to volunteer dashboard.", 1);
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to send an invite to an applicant
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	* @throws Exception
	 ***************************************************************************************************/
	public String SendInvite() throws Exception{
		APP_LOGGER.startFunction("SendInvite");
		String retval = Globals.KEYWORD_FAIL;
		try {

			HashMap<String,String> volDashboard_beforeOrderCreation = null;
			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");

			if(fieldLevelValidation.equalsIgnoreCase("Yes")){
				volDashboard_beforeOrderCreation = ClientFacingApp.fetchVolunteerDashboardCount();
			}
			

			retval= ClientFacingApp.sendInvitationToApplicant();

			ClientFacingApp.navigateVolunteerDashboard();
			
			if(fieldLevelValidation.equalsIgnoreCase("Yes")){
				HashMap<String,String> volDashboard_AfterOrderCreation = ClientFacingApp.fetchVolunteerDashboardCount();
				ClientFacingApp.verifyVolunteerDashboard(volDashboard_beforeOrderCreation,volDashboard_AfterOrderCreation,"OpenInvitation",1);
				ClientFacingApp.verifyVolunteerDashboard(volDashboard_beforeOrderCreation,volDashboard_AfterOrderCreation,"ViewAllVolunteers",1);
			}
			

		} catch (Exception e) {
			sc.STAF_ReportEvent("Fail", "Send Invite", "Exception occurred while sending mail invitation", 0);
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method fetch invitation email received using vbs and then stores the captured url in test data
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String FetchInvitationURLFromEmail() throws Exception {
		APP_LOGGER.startFunction("FetchInvitationURLFromEmail");
		String retval = Globals.KEYWORD_FAIL;
		HashMap<String, String> emailDetails=  new HashMap<String, String>();
		String emailFound=null;
		String emailBodyText =null;
		String invitationURL = null;
		String getAttachment = null;
		String getAttachmentFilename = null;
		String subjectBodyText= null;

		try{
		    
		    
			String subjectEmailPattern = "Invitation from "+Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
			String bodyEmailPattern = Globals.testSuiteXLS.getCellData_fromTestData("CustomEmailMessage");
			String urlPattern="";
            if (Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD"))
            {
                urlPattern = "https://app";
            }
            else{
                urlPattern="https://qapp";
            }
            int startIndex;
            int endindex ;
            String osName=System.getProperty("os.name");
                        
			if(Globals.EXECUTION_MACHINE.equalsIgnoreCase("jenkins")) {
			    emailBodyText = FetchInviatationURLFromDB();
			    if(emailBodyText.equalsIgnoreCase(Globals.KEYWORD_FAIL)) {
			        sc.STAF_ReportEvent("Fail", "Fetch Invite", "Applicant invitation mail NOT fetched by Database.", 0);
	                throw new Exception("Applicant Invitation mail NOT received from Database");
			    }
			    emailBodyText = emailBodyText.replace("\"", "").trim();
			    startIndex = emailBodyText.indexOf(urlPattern);
                endindex = emailBodyText.indexOf("><span style=");
                
			}else {
			    emailDetails = Email.fetchEmailFromOutlook(subjectEmailPattern,bodyEmailPattern,60);   
			    emailFound = emailDetails.get("EmailFound");
			    if (emailFound.equalsIgnoreCase("True")){
	                emailBodyText = emailDetails.get("EmailBody");
	                emailBodyText = emailBodyText.replace("\"", "").trim();
	                startIndex = emailBodyText.indexOf(urlPattern);
	                if(osName.equalsIgnoreCase("Windows 10")){
	                	
	                	endindex = emailBodyText.indexOf("If you have");
	                	                	
	                }else{
	                	endindex = emailBodyText.indexOf("Order My Background Check");	
	                }
	                
	                getAttachment = emailDetails.get("EmailAttachmentCount");
	                subjectBodyText = emailDetails.get("EmailSubject");
	                getAttachmentFilename = emailDetails.get("EmailAttachmentName");
	                String emailAtttachFlg=Globals.testSuiteXLS.getCellData_fromTestData("EmailAttachementFlag");
	                
	                if(emailAtttachFlg.contains("Yes")&& (Integer.parseInt(getAttachment)>0)){
	                sc.STAF_ReportEvent("Pass", "Fetch Invite with attachement", "Applicant invitation mail received in outlook client with attachement file name:-"+getAttachmentFilename, 0);
	                }
	                else if(Integer.parseInt(getAttachment)==0){
	                sc.STAF_ReportEvent("Pass", "Fetch Invite without attachement", "Applicant invitation mail received in outlook client without attachement", 0);
	                }
	                else{
	                	sc.STAF_ReportEvent("Fail", "Fetch Invite with/without attachement", "Applicant invitation mail received in outlook with missmatched in attachement Section", 0);
	                }
	                
			    }else{
	                sc.STAF_ReportEvent("Fail", "Fetch Invite", "Applicant invitation mail NOT received in outlook client", 0);
	                throw new Exception("Applicant Invitation mail NOT received in Outlook client");
	            }
			    
			}	
				invitationURL = emailBodyText.substring(startIndex, endindex).trim();
				invitationURL = invitationURL.replace(">", "").trim();		
						
				Globals.testSuiteXLS.setCellData_inTestData("InvitationURL", invitationURL);
				log.debug(" Method-FetchInvitationURLFromEmail | URL - " + invitationURL);
				sc.STAF_ReportEvent("Pass", "Send Invite", "Applicant invitation mail received  - "+ invitationURL, 0);
				retval=Globals.KEYWORD_PASS;
			


		}catch (Exception e) {
			log.error("Method-CreateInvitationOrder | Unable to create Invitation Order | Exception - "+ e.toString());
			sc.STAF_ReportEvent("Fail", "Send Invite", "Applicant invitation mail NOT received", 0);
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method for to create an invitation order starting from volunteer account creation till Order submission.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CreateInvitationOrder() throws Exception {
		APP_LOGGER.startFunction("CreateInvitationOrder");
		String retval = Globals.KEYWORD_FAIL;
		try {

			String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
			driver.navigate().to(url);
			//.launchURL(Globals.BrowserType, url);
			retval= ClientFacingApp.volunteerAccountCreation();
			ClientFacingApp.checkForIDAndProceed(); //Changed by Andy for  code re-usability
			retval= ClientFacingApp.invitationOrderStep1();
			retval= ClientFacingApp.invitationOrderStep2();
			retval= ClientFacingApp.invitationOrderStep3();
			ClientFacingApp.invitationStep3Abuse();
			retval= ClientFacingApp.invitationStep3COAbuse();
			retval= ClientFacingApp.invitationQuestionnaire();
			retval= ClientFacingApp.invitationOrderStep4();
		} catch (Exception e) {
			log.error("Method-CreateInvitationOrder | Unable to create Invitation Order | Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method for to launch the Invitation url that has been received in the invite email
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String LaunchInvitationURL() throws Exception{
		APP_LOGGER.startFunction("CreateInvitationOrder");
		String retval = Globals.KEYWORD_FAIL;
		String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
		//		driver.navigate().to(url);
		String urlLaunched = sc.launchURL(Globals.BrowserType, url);

		if (urlLaunched.equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.verifyPageTitle("Get Started with Verified Volunteers").equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", "Invitation URL", "Url launched successfully-"+url, 1);
			retval	=	Globals.KEYWORD_PASS;						
		}
		else{
			sc.STAF_ReportEvent("Fail", "Invitation URL", "Url launch Failed-"+url, 1);
			throw new Exception("Unable to launch Invitation URL");
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to create order using Invitation Ordering workflow starting from new volunteer account creation till order submission.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String FunctionalInvitationOrderStep1() throws Exception {
		return  ClientFacingApp.functionalInvitationOrderStep1();
	}

	/**************************************************************************************************
	 * Method to create order using Good Deed Ordering workflow starting from new volunteer account creation till order submission.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CreateGoodDeedOrder()throws Exception {
		APP_LOGGER.startFunction("CreateGoodDeedOrder");
		String retval = Globals.KEYWORD_FAIL;
		ClientFacingApp.updateVolunteerNameInTestData();
		ClientFacingApp.enterCodeAndProceedToStep1();
		ClientFacingApp.checkForIDAndProceed();   
		ClientFacingApp.invitationOrderStep1();
		ClientFacingApp.invitationOrderStep2();
		ClientFacingApp.invitationOrderStep3();
		ClientFacingApp.invitationStep3Abuse();
		ClientFacingApp.invitationStep3COAbuse();
		ClientFacingApp.invitationQuestionnaire();
		ClientFacingApp.invitationOrderStep4();
		retval = Globals.KEYWORD_PASS;

		return retval;
	}
	/**************************************************************************************************
	 * Method to Verify Good deed page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String validateGoodDeedCodePage()throws Exception {
		return  ClientFacingApp.validateGoodDeedCodePage();
	
	}
	/**************************************************************************************************
	 * Method to Click on Get Verified Tab for a volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String NavigateGetVerified() throws Exception{
		return ClientFacingApp.clickOnGetVerified();
	}

	/**************************************************************************************************
	 * Method to enter good deed code and proceed to Step1 for a volunteer during ordering
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String EnterCodeAndProceedToStep1() throws Exception {
		return ClientFacingApp.enterCodeAndProceedToStep1();
	}

	/**************************************************************************************************
	 * Method to create a new account in vv admin portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String NewAccountCreation() throws Exception{
		APP_LOGGER.startFunction("NewAccountCreation");
		String retval = Globals.KEYWORD_FAIL;
		int rowID = Globals.INT_FAIL;

		try{
			//check whether account name created already exists or not
			String accountName;
			String dynamicAccountFlag = Globals.testSuiteXLS.getCellData_fromTestData("Account_runtimeUpdateFlag");

			if(dynamicAccountFlag.equalsIgnoreCase("Yes")){
				int length = 10;
				accountName= "AutomationAcc" + sc.runtimeGeneratedStringValue(length);
				Globals.testSuiteXLS.setCellData_inTestData("AccountName", accountName);
				log.debug(" Method-verifyCompanyInformation | Account Name Runtime generated and store.value- "+accountName);
			}
			else{
				accountName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
			}


			rowID = InhousePortal.searchAccount();
			if(rowID != Globals.INT_FAIL){
				sc.STAF_ReportEvent("Fail", "Account Creation","Unable to create new account as account already exsts",1);
				return retval;
			}

			return InhousePortal.inhouseAccountCreation();
		}catch (Exception e) {
			log.error("Method-NewAccountCreation | Unable to create new account | Exception - "+ e.toString());
			throw e;
		}


	}

	/**************************************************************************************************
	 * Method to create a new user for an existing account in VV Admin Portal.Either Super user or Standard user can be created. 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CreateNewUser()throws Exception {
		APP_LOGGER.startFunction("CreateNewUser");

		try {
			String fieldLevelValidation ;
			fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");

			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}

			return InhousePortal.newUserCreation(fieldLevelValidation,"normal");

		}catch (Exception e) {
			log.error("Method-CreateNewUser | Unable to create new user | Exception - "+ e.toString());
			throw e;
		}

	}
	
	/**************************************************************************************************
	 * Method to create a new user for an existing account in VV Admin Portal.Either Super user or Standard user can be created. 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CreateNewCustomUser()throws Exception {
		APP_LOGGER.startFunction("CreateNewUser");

		try {
			String fieldLevelValidation ;
			fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");

			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}

			return InhousePortal.newUserCreation(fieldLevelValidation,"custom");

		}catch (Exception e) {
			log.error("Method-CreateNewUser | Unable to create new user | Exception - "+ e.toString());
			throw e;
		}

	}

	/**************************************************************************************************
	 * Method to create Custom Price for VV products in Admin Portal.Any existing custom price is overwritten with the new one.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CreateCustomPricing() throws Exception {
		APP_LOGGER.startFunction("CreateCustomPricing");

		try {
			String fieldLevelValidation ;
			fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");

			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}

			return InhousePortal.newPricingSetup(fieldLevelValidation);

		}catch (Exception e) {
			log.error("Method-CreateCustomPricing | Unable to create new pricing | Exception - "+ e.toString());
			throw e;
		}

	}

	/**************************************************************************************************
	 * Method to authorize all products present in Account's Authorization tab in VV Admin Portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String SetupAuthorization()throws Exception {
		APP_LOGGER.startFunction("SetupAuthorization");

		try {

			return InhousePortal.newAuthorizationSetup();

		}catch (Exception e) {
			log.error("Method-SetupAuthorization | Unable to setup authorization | Exception - "+ e.toString());
			throw e;
		}

	}

	/**************************************************************************************************
	 * Method to verify Position Listing tab in VV Admin Portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyPositionListings() throws Exception {
		APP_LOGGER.startFunction("VerifyPositionListings");

		try {

			return InhousePortal.verifyPositionListingsTab();

		}catch (Exception e) {
			log.error("Method-VerifyPositionListings | Unable to verify Position Listings Tab | Exception - "+ e.toString());
			throw e;
		}

	}

	/**************************************************************************************************
	 * Method to configure API Orderinig as well create a new Integration user in Vv Admin Portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ConfigureAPIOrdering()throws Exception {
		APP_LOGGER.startFunction("ConfigureAPIOrdering");

		try {

			return InhousePortal.newAPIConfiguration();

		}catch (Exception e) {
			log.error("Method-ConfigureAPIOrdering | Unable to setup API configurations | Exception - "+ e.toString());
			throw e;
		}

	}

	/**************************************************************************************************
	 * Method to create order using Good Deed URL Ordering workflow.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CreateGoodDeedURLOrder()throws Exception {
		APP_LOGGER.startFunction("CreateGoodDeedURLOrder");
		String retval = Globals.KEYWORD_FAIL;

		//ClientFacingApp.clickGetVerified();	
		ClientFacingApp.checkForIDAndProceed();
		ClientFacingApp.invitationOrderStep1();
		ClientFacingApp.invitationOrderStep2();
		ClientFacingApp.invitationOrderStep3();
		ClientFacingApp.invitationStep3Abuse();
		ClientFacingApp.invitationOrderStep4();

		retval=Globals.KEYWORD_PASS;

		return retval;
	}

	/**************************************************************************************************
	 * Method to Click On Get Verified button on et Verified page for a volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClickGetVerified() throws Exception{
		return ClientFacingApp.clickGetVerified();
	}

	/**************************************************************************************************
	 * Method to launch browser with the Good Deed URL mentioned in Test Data
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/

	public String LaunchGoodDeedURL()throws Exception {
		APP_LOGGER.startFunction("LaunchGoodDeedURL");
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 3;

		try {
			try{// the below code would throw an exception when the method is called for first cause no browser has been launchde yet
				tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", timeOutinSeconds);

				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					//						driver.findElement(LocatorAccess.getLocator("clientBGDashboard_logout_link")).click();
					sc.clickWhenElementIsClickable("clientBGDashboard_logout_link",timeOutinSeconds );
				}
			}catch(Exception e){
				log.debug("LaunchGoodDeedURL - No Browser launched yet");
			}

			//GoodDeedCode column name depends upon the Env that its being executed
			String colName = Globals.Env_To_Execute_ON+"_GoodDeedCodeOrURL";
			String goodDeedURL = Globals.testSuiteXLS.getCellData_fromTestData(colName);

			//				String goodDeedURL	=	Globals.testSuiteXLS.getCellData_fromTestData("GoodDeedCodeOrURL");
			String urlLaunched= sc.launchURL(Globals.BrowserType,goodDeedURL);

			if (urlLaunched.equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.verifyPageTitle("Get Started with Verified Volunteers").equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "Good Deed URL", "Url launched successfully-"+goodDeedURL, 1);
				retval	=	Globals.KEYWORD_PASS;						
			}
			else{
				sc.STAF_ReportEvent("Fail", "Good Deed URL", "Url launch Failed-"+goodDeedURL, 1);
				throw new Exception("Unable to launch Good Deed URL");
			}

		}catch(Exception e){
			log.error("Exception occurred in LaunchGoodDeedURL-VolunteerPortal | "+e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to launch browser with Volunteer Portal URL
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/

	public String LaunchVolunteerPortal() throws Exception {
		APP_LOGGER.startFunction("LaunchVolunteerPortal");
		String retval	=	Globals.KEYWORD_FAIL;
		String tempRetval	=	Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 3;

		try {
			try{// the below code would throw an exception when the method is called for first cause no browser has been launchde yet
				tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", timeOutinSeconds);

				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					//					driver.findElement(LocatorAccess.getLocator("clientBGDashboard_logout_link")).click();
					sc.clickWhenElementIsClickable("clientBGDashboard_logout_link",timeOutinSeconds );
				}
			}catch(Exception e){
				log.debug("LaunchVolunteerPortal - No Browser launched yet");
			}
			String urlLaunched	= sc.launchURL(Globals.BrowserType,Globals.getEnvPropertyValue("AppURL"));
			  try{// the below code would throw an exception when the Login session already exists
	                tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", timeOutinSeconds);
	                if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
	                    //                    driver.findElement(LocatorAccess.getLocator("clientBGDashboard_logout_link")).click();
	                    //sc.clickWhenElementIsClickable("clientBGDashboard_logout_link",timeOutinSeconds );
	                    //JavascriptExecutor js = (JavascriptExecutor)driver;
	                    //js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';arguments[0].click();",driver.findElement(LocatorAccess.getLocator("clientBGDashboard_logout_link")));
	                    sc.clickElementUsingJavaScript("clientBGDashboard_logout_link");
	                }
	            }catch(Exception e){
	                log.debug("Cannot logout of Volunteer Loggedin Portal");
	            }
			if (urlLaunched.equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.verifyPageTitle("Log in").equalsIgnoreCase(Globals.KEYWORD_PASS)){
				//				driver.findElement(LocatorAccess.getLocator("login_createAccount_link")).click();
				sc.clickWhenElementIsClickable("login_createAccount_link",timeOutinSeconds );
				retval	=	Globals.KEYWORD_PASS;			

			}

		}catch(Exception e){
			log.error("Exception occurred in LaunchVolunteerPortal .  | "+e.toString());
			throw e;
		}
		return retval;
	}


	public String PostOrderRequestXML() throws Exception {
		return ClientFacingApp.postOrderRequestXML();
	}
	
	
	public String NegativeValidationSeamless() throws Exception {
		return ClientFacingApp.negativeValidationSeamless();
	}
	
	public String negativeValidationSeamlessConsent() throws Exception {
		return ClientFacingApp.negativeValidationSeamlessConsent();
	}
	
	public String PostOrderRequestXMLSeamless() throws Exception {
        return ClientFacingApp.postOrderRequestXMLSeamless();
    }
	
	public String postOrderRequestXMLSeamlessConsent() throws Exception {
        return ClientFacingApp.postOrderRequestXMLSeamlessConsent();
    }

	/**************************************************************************************************
	 * Method to launch browser with Volunteer Portal URL
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author gaurimodani
	 * @throws Exception
	 ***************************************************************************************************/

	public String LaunchVVAppPortal() throws Exception {
		APP_LOGGER.startFunction("LaunchOrgVolunteerPortal");
		String retval	=	Globals.KEYWORD_FAIL;
		String tempRetval	=	Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 3;

		try {
			try{// the below code would throw an exception when the method is called for first cause no browser has been launchde yet
				tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", timeOutinSeconds);

				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					//					driver.findElement(LocatorAccess.getLocator("clientBGDashboard_logout_link")).click();
					sc.clickWhenElementIsClickable("clientBGDashboard_logout_link",timeOutinSeconds );
				}
			}catch(Exception e){
				log.debug("LaunchVolunteerPortal - No Browser launched yet");
			}
			String urlLaunched	= sc.launchURL(Globals.BrowserType,Globals.getEnvPropertyValue("AppURL"));
			  try{// the below code would throw an exception when the Login session already exists
	                tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", timeOutinSeconds);
	                if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
	                    sc.clickElementUsingJavaScript("clientBGDashboard_logout_link");
	                }
	            }catch(Exception e){
	                log.debug("Cannot logout of Volunteer Loggedin Portal");
	            }
			if (urlLaunched.equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.verifyPageTitle("Log in").equalsIgnoreCase(Globals.KEYWORD_PASS)){
				retval	=	Globals.KEYWORD_PASS;			
			}

		}catch(Exception e){
			log.error("Exception occurred in LaunchVolunteerPortal .  | "+e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to create an API ordering.End to End flow till order submission
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CreateAPIOrder() throws Exception {
		APP_LOGGER.startFunction("CreateAPIOrder");
		String retval = Globals.KEYWORD_FAIL;
		try {
            String apiShare=sc.testSuiteXLS.getCellData_fromTestData("SharingType") ;
			
			if(apiShare.equalsIgnoreCase("API Share")){
				retval= ClientFacingApp.apiExistingAccountLogin();
				retval= ClientFacingApp.verifyAPIOrderStep1();
				retval= ClientFacingApp.verifyAPIOrderStep2();
				retval= ClientFacingApp.invitationOrderStep3();
				retval= ClientFacingApp.invitationOrderStep4();
			}else{
			   String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
			   sc.launchURL(Globals.BrowserType, url);
               retval= ClientFacingApp.apiAccountCreation();
			   //retval = ClientFacingApp.clickOnGetVerified();
			   ClientFacingApp.checkForIDAndProceed();
		       retval= ClientFacingApp.verifyAPIOrderStep1();
			   retval= ClientFacingApp.verifyAPIOrderStep2();
		       retval= ClientFacingApp.invitationOrderStep3();
			   ClientFacingApp.invitationStep3Abuse();
		       retval= ClientFacingApp.invitationOrderStep4();
			}
		} catch (Exception e) {
			sc.STAF_ReportEvent("Fail", "API Ordering", "Exception occurred during API Order creation-Exception - "+ e.toString(),1);
			log.error("Method-CreateAPIOrder | Unable to create API Order | Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify existing Volunteer login for API 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String ApiExistingAccountLogin() throws Exception{
		return ClientFacingApp.apiExistingAccountLogin();
	}
	
	/**************************************************************************************************
	 * Method to verify Type zero share page for volunteer and shre the Bg
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String ShareTypeZero() throws Exception{
		return ClientFacingApp.shareTypeZero();
	}
	
	/**************************************************************************************************
	 * Method to verify API step 1 . Used for functional  testing
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyAPIOrderStep1Functional() throws Exception{
		return ClientFacingApp.verifyAPIOrderStep1Functional();
	}


	/**************************************************************************************************
	 * Method to create anew volunteer account for an api invited volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ApiVolunteerAccountCreation() throws Exception{
		String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
		sc.launchURL(Globals.BrowserType, url);
		return ClientFacingApp.apiAccountCreation();
	}

	/**************************************************************************************************
	 * Method to create an order by using upload ordering workflow.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CreateUploadOrder() throws Exception{
		return ClientFacingApp.CreateUploadOrder();
	}	

	/**************************************************************************************************
	 * Method to validate the Volunteers tab in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ValidateVolunteersTab() throws Exception {
		return InhousePortal.validateVolunteersTab();
	}


	/**************************************************************************************************
	 * Method to validate the Staffing tab in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ValidateStaffTab() throws Exception {
		return InhousePortal.validateStaffTab();
	}

	/**************************************************************************************************
	 * Method to validate the Available States tab in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ValidateAvailableStates() throws Exception{
		return InhousePortal.validateAvailableStates();
	}

	/**************************************************************************************************
	 * Method to validate the dashboard tab in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ValidateDashboardTab() throws Exception {
		return InhousePortal.validateDashboardTab();
	}

	/**************************************************************************************************
	 * Method to navigate to Email Setting Tab for a client in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String NavigateToEmailSettingsTab() throws Exception{
		return InhousePortal.navigateToEmailSettingsTab();	
	}

	/**************************************************************************************************
	 * Method to generate the good deed url for a postion (if it doesnt exists) for a client in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String GenerateGoodDeedURL() throws Exception{
		return InhousePortal.generateGoodDeedURL();

	}

	/**************************************************************************************************
	 * Method to remove the good deed url for a postion (if exists) for a client in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String RemoveGoodDeedURL() throws Exception{
		return InhousePortal.removeGoodDeedURL();		
	}

	/**************************************************************************************************
	 * Method to fetch good deed code for a position from the inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String FetchGoodDeedCode() throws Exception{
		return InhousePortal.fetchGoodDeedCode();
	}

	/**************************************************************************************************
	 * Method to verify GUI for volunteer dashboard 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyVolunteerDashboard() throws Exception{
		sc.isDisplayed_withCustomReporting("volDashboard_export_btn", "VVOU148- Verify the Presence of the Export Button on the Volunteer dashboard page.");
		sc.isDisplayed_withCustomReporting("volDashboard_removeRows_btn","VVOU150 - Verify the Presence of the Remove Rows Button on the Volunteer dashboard page.");
		sc.isDisplayed_withCustomReporting("volDashboard_showColumns_btn","Verify the presence of SHow/Hide Columns button on Volunteer Dashboard");
		sc.isDisplayed_withCustomReporting("volDashboard_customSort_btn","Verify the presence of Custom Sort button on Volunteer Dashboard");
		sc.isDisplayed_withCustomReporting("volDashboard_showFilter_btn","Verify the presence of SHow Filter button on Volunteer Dashboard");
		return Globals.KEYWORD_PASS;
	}

	/**************************************************************************************************
	 * Method to log out a staff user 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String StaffUserLogout() throws Exception {
		String retval = Globals.KEYWORD_FAIL;
		retval= InhousePortal.staffUserLogout();
		//sc.closeAllBrowsers();
		return retval;
	}

	/**************************************************************************************************
	 * Method to log out a volunter 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VolunteerLogOut() throws Exception {
		String retval = Globals.KEYWORD_FAIL;
		retval =  ClientFacingApp.volunteerLogOut();
		//sc.closeAllBrowsers();
		return retval;
	}

	/**************************************************************************************************
	 * Method to navigate to Staff tab in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String NavigateToStaffTab() throws Exception {
		return InhousePortal.navigateToStaffTab();
	}

	/**************************************************************************************************
	 * Method to add a new staff user to gain access to VV inhouse application
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String AddNewStaffUser() throws Exception {
		return InhousePortal.addNewStaffUser();
	}

	/**************************************************************************************************
	 * Method to configure custom fields for a client in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ConfigureCustomFields() throws Exception {
		return InhousePortal.ConfigureCustomFields();
	}
	
	/**************************************************************************************************
     * Method for EditClientFeatures Bg Dashboard - Client Views Check Box
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author psave
     * @throws Exception
     ***************************************************************************************************/
	public String ClientViewssettings() throws Exception{
		return InhousePortal.ClientViewssettings();

	}
	/**************************************************************************************************
     * Method for EditClientFeatures Bg Dashboard - AES Encryption Key Check Box
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author psave
     * @throws Exception
     ***************************************************************************************************/
	public String AESEncryptionsettings() throws Exception{
		return InhousePortal.AESEncryptionsettings();

	}
	/**************************************************************************************************
	 * Method to configure Client Views fields for a client in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String ConfigureClientViews() throws Exception {
		return InhousePortal.ConfigureClientViews();
	}
	
	/**************************************************************************************************
	 * Method to setup Client Views fields for a client users in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClientViewUsers() throws Exception {
		return InhousePortal.ClientViewUsers();
	}
	/**************************************************************************************************
	 * Method to verify Client view Functionality on VV application
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClientViewAssignRemoveDashboardVerify() throws Exception {
		return ClientFacingApp.clientViewAssignRemoveDashboardVerify();
	}

	/**************************************************************************************************
	 * Method to verify Questionnaire Filter on VV application
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String QuestionnaireFilter() throws Exception {
		return ClientFacingApp.questionnaireFilter();
	}
	
	/**************************************************************************************************
	 * Method to verify Client view Functionality for Users which having no views assigned on VV application
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClientViewsNoViewUsersVerify() throws Exception {
		return ClientFacingApp.clientViewsNoViewUsersVerify();
	}
	/**************************************************************************************************
	 * Method to verify Client view Functionality for Users which having Customs views assigned on VV application
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClientViewsCustomViewUsersVerify() throws Exception {
		return ClientFacingApp.clientViewsCustomViewUsersVerify();
	}
	/**************************************************************************************************
	 * Method to verify Client view rename Functionality on VV application
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClientViewsRenameVerifyDashboard() throws Exception {
		return ClientFacingApp.clientViewsRenameVerifyDashboard();
	}
	
	/**************************************************************************************************
	 * Method to navigate to Other configuration tab in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String NavigateToOtherConfigTab() throws Exception{
		return InhousePortal.navigateToOtherConfigTab();	
	}

	/**************************************************************************************************
	 * Method to navigate to Users tab in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String NavigateToUsersTab() throws Exception{
		return InhousePortal.navigateToUsersTab();	
	}

	/**************************************************************************************************
	 * Method to verify Other configutaion tab for a client in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyOtherConfiguartionTab() throws Exception{
		return InhousePortal.verifyOtherConfiguartionTab();	
	}

	/**************************************************************************************************
	 * Method to verify Systems Maintenance page in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifySystemMaintenance() throws Exception{
		return InhousePortal.verifySystemMaintenanceTab();
	}

	/**************************************************************************************************
	 * Method to search by user in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifySearchByUser() throws Exception{
		return InhousePortal.verifySearchByUser();
	}

	/**************************************************************************************************
	 * Method to Search by Client in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifySearchByClient() throws Exception{
		return InhousePortal.verifySearchByClient();
	}

	/**************************************************************************************************
	 * Method to verify volunteer agreement in Volunteer homepage oonce logged in
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyVolunteerAgreement() throws Exception{
		return ClientFacingApp.verifyVolunteerAgreement();
	}

	/**************************************************************************************************
	 * Method to edit volunteer photograph post logged in
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String EditVolunteerPhoto() throws Exception{
		return ClientFacingApp.editVolunteerPhoto();
	}

	/**************************************************************************************************
	 * Method to edit volunteer profile once volunteer has logged in
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String EditVolunteerProfile() throws Exception{
		return ClientFacingApp.editVolunteerProfile();
	}

	/**************************************************************************************************
	 * Method to verify Product Audit in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyProductAudit() throws Exception{
		return InhousePortal.verifyProductAudit();
	}

	/**************************************************************************************************
	 * Method to verify Volunteers Tab in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyVolunteerTab() throws Exception{
		return InhousePortal.verifyVolunteerTab();
	}

	/**************************************************************************************************
	 * Method to reset a volunteer/organization user password
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ResetVolunteerPassword() throws Exception{
		return ClientFacingApp.resetVolunteerPassword();
	}

	/**************************************************************************************************
	 * Method to verify search volunteer page along with its GUI validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifySearchVolunteer() throws Exception{
		return ClientFacingApp.verifySearchVolunteer();
	}

	
	public String VerifySearchVolunteerandClick() throws Exception{
		return ClientFacingApp.verifySearchVolunteerandclick();
	}
	/**************************************************************************************************
	 * Method to edit an volunteer and perform GUI vaidations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String EditVolunteer() throws Exception{
		return ClientFacingApp.editVolunteer();
	}
	
	/**************************************************************************************************
	 * Method to MASS update volunteers and perform GUI vaidations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String MassUpdateVolunteers() throws Exception{
		return ClientFacingApp.massUpdateVolunteers();
	}

	/**************************************************************************************************
	 * Method to fulfill an order in SWest through backend services.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String FulfillSWestOrder() throws Exception{
		Globals.SWest_DBServerName ="qa3.absodb.st.com";//irrespective of VV env, it always points to QA3 Admin Client
		Globals.SWest_DBServerPort ="9933";
		return AdminClient.CloseSWestOrder();
	}

	/**************************************************************************************************
	 * Method to Poll an Order post order fulfillment
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String PollOrder() throws Exception{
		return ClientFacingApp.PollSingleOrder();
	}
	/**************************************************************************************************
	 * Method to Get API Notification as per test data setting SendNotificationsviaApiLogTable and APIRetrievalNotifyResponseNewEndpoint
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String GetAPINotificationMsg() throws Exception{
		String ntMsgAPilog=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
        String ntMsgnwapiendpt=Globals.testSuiteXLS.getCellData_fromTestData("APIRetrievalNotifyResponseNewEndpoint");
        String npnOrderID=Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
        String rrResult=Globals.testSuiteXLS.getCellData_fromTestData("ExpectedOrderScore");
        if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") ){
        	//sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
        	return Globals.KEYWORD_PASS;
        }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
        	return ClientFacingApp.Queryforapilog(npnOrderID,"Complete",rrResult,"NoNo");            	
        }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
        	return ClientFacingApp.Queryforapilog(npnOrderID,"Complete",rrResult,"YesNo");
        }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
        	ClientFacingApp.Queryforapilog(npnOrderID,"Complete",rrResult,"NoYes");
        	return ClientFacingApp.getOrderRequestXML("Complete");
        }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
        	ClientFacingApp.Queryforapilog(npnOrderID,"Complete",rrResult,"YesYes");
        	return ClientFacingApp.getOrderRequestXML("Complete");
        }else{
        	sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
        	return Globals.KEYWORD_FAIL;
        }
		
	}
	
	/**************************************************************************************************
	 * Method to Get reportURL from api log table for API orders
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String RetriveReportURLLink() throws Exception{
		
        String npnOrderID=Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
        
        return ClientFacingApp.Queryforapilog(npnOrderID,"Complete","reportURL","");
        
			
	}

	/**************************************************************************************************
	 * Method to verify API Notification should not send for batch orders
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	
	public String GetBatchAPINotificationMsg() throws Exception{
		
        String npnOrderID=Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
        return ClientFacingApp.Queryforapilog(npnOrderID,"Batch","Blank","NoNo");
       
	}

	/**************************************************************************************************
	 * Method for to navigate to BG Dashboard dashboard for a logged in  Organization user
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String NavigateToBGDashboard() throws Exception {
		return ClientFacingApp.navigateBGDashboard();
	}
	/**************************************************************************************************
	 * Method for to navigate to BG Dashboard HoldTab for a logged in  Organization user
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String NavigateToBGDashboardHoldTab() throws Exception {
		return ClientFacingApp.navigateBGDashboardHoldTab();
	}
	/**************************************************************************************************
	 * Method to search a volunteer in BG dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String SearchVolunteerInBGDashboard() throws Exception {
		return ClientFacingApp.searchVolInBGDashboard();
	}
	/**************************************************************************************************
	 * Method to search a volunteer in Hold tab BG dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String searchVolInHoldTabBGDashboard() throws Exception {
		return ClientFacingApp.searchVolInHoldTabBGDashboard();
	}
	/**************************************************************************************************
	 * Method to verify BG report in Review Background Check page and marks the eleigibility of the volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyBGReport() throws Exception {
		return ClientFacingApp.verifyBGDashboardReport();
	}
	/**************************************************************************************************
	 * Method to verify Static BG report in Review Background Check page in production
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyBGReportPVT() throws Exception {
		return ClientFacingApp.verifyBGDashboardReportPVT();
	}
	/**************************************************************************************************
	 * Method to verify the BG report for API order using report URL link and mark eligibilty of volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyReportURLBGReport() throws Exception {
		return ClientFacingApp.verifyReportURLBGReport();
	}
	/**************************************************************************************************
	 * Method to naviggate to Volunteer's My Profile page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String GotoVolunteerMyProfile() throws Exception {
		return ClientFacingApp.gotoVolunteerMyProfile();
	}

	/**************************************************************************************************
	 * Method to Get verified on volunteers homepage before an order has been placed
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyGetVerified_BeforeOrder() throws Exception {
		String retval = Globals.KEYWORD_FAIL;

		try{
			LaunchVolunteerPortal();
			CreateVolunteerAccount();

			retval = ClientFacingApp.gotoVolunteerMyProfile();

			if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				String expectedText = "Track Order Status: "+"Get Verified";
				
				String objectNameInOR = "volunteerHomepage_orderstatus_span";
				retval =  ClientFacingApp.verifyTrackOrderStatus(objectNameInOR,expectedText);
			}
			if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.clickWhenElementIsClickable("volunteerHomepage_getVerifiedMenu_link",10 );
			}

		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "Verified Link", "Unable to verify Verified Link due to exception.Exception-"+e.toString(), 1);
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify BG report which can be viewed in volunteer's profile
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VolunteerProfileReport() throws Exception{
		return ClientFacingApp.verifyVolunteerProfileReport();
	}

	/**************************************************************************************************
	 * Method to fulfill an order, then it polls the order.Post which both the reports which a org user can view is  validated
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String FulfillAndVerifyBGReport() throws Exception{
		String retval = Globals.KEYWORD_FAIL;
		try{
			FulfillSWestOrder();
			PollOrder();
			OrgUserLogin();
			SearchVolunteerInBGDashboard();
			VerifyBGReport();
			navigateVolunteerDashboard();
			VolunteerProfileReport();
			retval = LogOff();

		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "FulfillAndVerifyBGReport", "Unable to fulfill and verify bg report exception.Exception-"+e.toString(), 1);
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify Get Verified text on volunteers homepage once the order has been placed
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyGetVerified_AfterOrder() throws Exception {
		String retval = Globals.KEYWORD_FAIL;
		retval = ClientFacingApp.gotoVolunteerMyProfile();

		if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			String expectedText = "Track Order Status: "+"Order processing";
			String objectNameInOR = "volunteerHomepage_orderstatus_span";
			retval =  ClientFacingApp.verifyVerifiedLinkText(objectNameInOR,expectedText);
			retval =VolunteerLogOut();
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify Get Verified text on volunteers homepage once the order has been fulfilled
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyGetVerified_AfterFulfillment() throws Exception {
		String retval = Globals.KEYWORD_FAIL;
		retval = VolunteerLogin();
		retval = ClientFacingApp.gotoVolunteerMyProfile();

		if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			String orderedDate=Globals.testSuiteXLS.getCellData_fromTestData("OrderedDate");
			String expectedText = "Verified: "+orderedDate; 
			String objectNameInOR = "volunteerHomepage_verified_link";
			retval =  ClientFacingApp.verifyVerifiedLinkText(objectNameInOR,expectedText);
			retval = VerifyVolunteerReport();
			String questFlag=Globals.testSuiteXLS.getCellData_fromTestData("Questionnaire");
			if(questFlag.equalsIgnoreCase("yes")){
				retval = ClientFacingApp.verifyQuestionnaireReport();
			}
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to login as volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VolunteerLogin() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		try {
			try{// the below code would throw an exception when the method is called for first cause no browser has been launchde yet
				if(driver != null && !driver.toString().contains("null")){
					tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", 2);

					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						sc.clickWhenElementIsClickable("clientBGDashboard_logout_link", timeOutInSeconds);

					}
				}

			}catch(Exception e){
				log.debug("VolunteerLogin - No Browser launched yet");
			}

			String urlLaunched= sc.launchURL(Globals.BrowserType,Globals.getEnvPropertyValue("AppURL"));

			if (urlLaunched.equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.verifyPageTitle("Log in").equalsIgnoreCase(Globals.KEYWORD_PASS)){

				sc.STAF_ReportEvent("Pass", "VolunteerLogin", "Login Page has been loaded", 1);


				String volName=Globals.testSuiteXLS.getCellData_fromTestData("VolunteerUsername");
				String orgUserPwd=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");

				sc.addParamVal_InEmail("VolunteerUsername", volName);
				
				tempRetval=sc.setValueJsChange("login_volunteerUsername_txt", volName);
				tempRetval=sc.setValueJsChange("login_volunteerPwd_txt",orgUserPwd);

				log.debug(tempRetval + " Setting value for username and password for Org login");

				sc.clickWhenElementIsClickable("login_volunteerLogin_btn", timeOutInSeconds);


				if (sc.waitforElementToDisplay("volunteerHomepage_logout_link",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){

					sc.STAF_ReportEvent("Pass", "Volunteer Login", "User has logged in - " + volName , 1);
					retval=Globals.KEYWORD_PASS;	

				}else{
					sc.STAF_ReportEvent("Fail", "Volunteer Login", "User unable to log in - " + volName , 1);

				}

			}else{
				sc.STAF_ReportEvent("Fail", "Volunteer Login", "Login Page has NOT been loaded", 1);

			}

		}catch(Exception e){
			log.error("Exception occurred in Organization user Login | "+e.toString());
			throw e;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify BG report when a volunteers tries to view one;s report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyVolunteerReport() throws Exception{
		return ClientFacingApp.verifyVolunteerReport();

	}

	/**************************************************************************************************
	 * Method to verify BG report which a volunnteer can view when he clicks on any of the badges
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String verifyBadgeReport() throws Exception{
		return ClientFacingApp.verifyVolunteerBadgeReport();
	}

	/**************************************************************************************************
	 * Method to bG report that is visible through the Order Details page in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyOrderDetailsReport() throws Exception{
		return InhousePortal.verifyOrderDetailsReport();
	}

	/**************************************************************************************************
	 * Method to Client Ordering for MVR service when its not authorized i.e. its not enabled in the Authorization tab for the client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String MVRAuthorizeClientOrder() throws Exception{
		return ClientFacingApp.mvrAuthorizeClientOrder();
	}


	/**************************************************************************************************
	 * Method to navigate the user to Client Report Tab in Org User homepage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String NavigateToClientReports() throws Exception {
		return ClientFacingApp.navigateToClientReports();
	}

	/**************************************************************************************************
	 * Method to verify Client Report - Invitation Status
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String InvitationStatusReport() throws Exception{
		return ClientFacingApp.invitationStatusReport();
	}

	/**************************************************************************************************
	 * Method to verify Client Report - Access Fees
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String AccessFeesReport() throws Exception{
		return ClientFacingApp.accessFeesReport();
	}
	/**************************************************************************************************
	 * Method to verify Client Report - Client Users
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClientUsersReport() throws Exception{
		return ClientFacingApp.clientUsersReport();
	} 
	/**************************************************************************************************
	 * Method to verify Client Report - Client Dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ClientDashboardReport() throws Exception{
		return ClientFacingApp.clientDashboardReport();
	}

	/**************************************************************************************************
	 * Method to verify Client Report -Order Turnaround
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String OrderTurnaroundReport() throws Exception{
		return ClientFacingApp.orderTurnaroundReport();
	}

	/**************************************************************************************************
	 * Method to verify Client Report - Order Trasanction
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String OrderTransactionReport() throws Exception{
		return ClientFacingApp.orderTransactionReport();
	}

	/**************************************************************************************************
	 * Method to verify Client Report - Volunteer Contribution
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VolunteerContributionReport() throws Exception{
		return ClientFacingApp.volunteerContributionReport();
	}

	/**************************************************************************************************
	 * Method to verify Client Report - Manage My Volunteers - Exportable Data
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author lvkumar
	 * @throws Exception
	 ***************************************************************************************************/
	public String ManageMyVolunteersExportableDataReport() throws Exception{
		return ClientFacingApp.ManageMyVolunteersExportableDataReport();
	}
	
	/**************************************************************************************************
	 * Method to create a new batch order csv file, uploades into the applications, validates the necessary steps.
	 * Post uploading, it process the batch order by calling the service from backend. This is done as in QA env batch files get processed every hour
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CreateBatchOrder() throws Exception{
		return ClientFacingApp.CreateBatchOrder();
	}

	/**************************************************************************************************
	 * Method to process the batch order by calling the service from backend. This is done as in QA env batch files get processed every hour
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ProcessBatchOrder() throws Exception {
		return ClientFacingApp.processBatchOrder();
	}

	/**************************************************************************************************
	 * Method to verify the batch order which ahs been uploaded.
	 * This validates all the error description messages as well exports two type of report from Review Batch Order page 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyBatchOrder() throws Exception {
		return ClientFacingApp.verifyBatchOrder();
	}
	
	/**************************************************************************************************
	 * Method to verify the Export funtionality
	 * This validates all the error description messages as well exports two type of report from Review Batch Order page 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyExport() throws Exception {
		return ClientFacingApp.verifyExport();
	}
	
	/**************************************************************************************************
	 * Method to update the Notes and verify the updated notes in all client setup pages
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyUpdatedNotes() throws Exception {
		return InhousePortal.VerifyUpdatedNotes();
		
	}
	/**************************************************************************************************
	 * Method to create a new integration user
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String newIntegrationUser() throws Exception {
       return  InhousePortal.newIntegrationUser();
	}
	/**************************************************************************************************
	 * Method to verify login for integration user
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String IntegrationUserLogin() throws Exception {
		
		APP_LOGGER.startFunction("IntegrationUserLogin");
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;
		String stepName="";

		try {
			
			String urlLaunched= sc.launchURL(Globals.BrowserType,Globals.getEnvPropertyValue("AppURL"));
			if (urlLaunched.equalsIgnoreCase(Globals.KEYWORD_PASS)&&sc.verifyPageTitle("Log in").equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "OrganizationUserLogin", "Login Page has been loaded", 1);
			}
			String orgName=Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
			String intUserName=Globals.testSuiteXLS.getCellData_fromTestData("APIUserName");
			String orgUserPwd=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
			
			sc.addParamVal_InEmail("Organization", orgName);
			sc.addParamVal_InEmail("OrgUserName", intUserName);
			
			tempRetval=sc.setValueJsChange("login_orgUsername_txt", intUserName);
			tempRetval=sc.setValueJsChange("login_orgPassword_txt",orgUserPwd);
			sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
			
			String expectdText = "Please provide a valid username and password";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
			stepName = "Verify Login for Integration User";

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
				retval=Globals.KEYWORD_PASS;
			}else{	
				sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Expected - "+expectdText, 1);
			}
		}
		catch(Exception e){
			log.error("Exception occurred in Integration user Login | "+e.toString());
			throw e;
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to lock a client user account
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String lockClientUser() throws Exception {
		return  ClientFacingApp.lockClientUser();
	}
	/**************************************************************************************************
	 * Method to unlock a client user account
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String unlockClientUser() throws Exception {
       return  InhousePortal.unlockClientUser();
	}
	/**************************************************************************************************
	 * Method to poll all orders which have been procesed in Swest through the UI
	 * This validates all the error description messages as well exports two type of report from Review Batch Order page 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String PollAllOrders() throws Exception {
		return InhousePortal.pollAllOrders();
		
	}
	/**************************************************************************************************
	 * Method fetch claim account url that is received by the volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String FetchClaimAccountURLFromEmail() throws Exception {
		APP_LOGGER.startFunction("FetchClaimAccountURLFromEmail");
		String retval = Globals.KEYWORD_FAIL;
		HashMap<String, String> emailDetails=  new HashMap<String, String>();
		String emailFound=null;
		String emailBodyText =null;
		String invitationURL = null;

		try{

			String subjectEmailPattern = "Your volunteer background check with "+Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
			String bodyEmailPattern = Globals.testSuiteXLS.getCellData_fromTestData("CustomEmailMessage");
			String urlPattern="";
            if (Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD"))
            {
                urlPattern = "https://app";
            }
            else{
                urlPattern="https://qapp";
            }
            int startIndex;
            int endindex ;
						
			if(Globals.EXECUTION_MACHINE.equalsIgnoreCase("jenkins")) {
				
			    emailBodyText = ClientFacingApp.FetchInviatationURLFromDB(bodyEmailPattern,subjectEmailPattern);
			    if(emailBodyText.equalsIgnoreCase(Globals.KEYWORD_FAIL)) {
			        sc.STAF_ReportEvent("Fail", "Fetch Invite", "Applicant invitation mail NOT fetched by Database.", 0);
	                throw new Exception("Applicant Invitation mail NOT received from Database");
			    }
			    emailBodyText = emailBodyText.replace("\"", "").trim();
			    startIndex = emailBodyText.indexOf(urlPattern);
                endindex = emailBodyText.indexOf("<br /> <br /> If you have any questions,");
                
               
			}else {
			    emailDetails = Email.fetchEmailFromOutlook(subjectEmailPattern,bodyEmailPattern,60);   
			    emailFound = emailDetails.get("EmailFound");
			    if (emailFound.equalsIgnoreCase("True")){
	                emailBodyText = emailDetails.get("EmailBody");
	                emailBodyText = emailBodyText.replace("\"", "").trim();
	                startIndex = emailBodyText.indexOf(urlPattern);
	                endindex = emailBodyText.indexOf("If you have any questions,");
	                
			    }else{
			    	sc.STAF_ReportEvent("Fail", "Claim Account Email", "Volunteer did not receive claim account email.Voluunter FirstName-"+bodyEmailPattern, 0);
					throw new Exception("Volunteer did not receive claim account email.Voluunter FirstName-"+bodyEmailPattern);

	            }
			    
			}
			invitationURL = emailBodyText.substring(startIndex, endindex).trim();

			Globals.testSuiteXLS.setCellData_inTestData("InvitationURL", invitationURL);
			log.debug(" Method-FetchClaimAccountURLFromEmail | URL - " + invitationURL);
			sc.STAF_ReportEvent("Pass", "Claim Account Email", "Volunteer received claim account email.URL  - "+ invitationURL, 0);
			retval=Globals.KEYWORD_PASS;
			
		}catch (Exception e) {
			log.error("Method-FetchClaimAccountURLFromEmail | Volunteer did not receive claim account email | Exception - "+ e.toString());
			sc.STAF_ReportEvent("Fail", "Claim Account Email", "Volunteer did not receive claim account email.", 0);
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to check whether any claim account email has been received or not.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CheckForNoEmailReceived() throws Exception {
		APP_LOGGER.startFunction("CheckForNoEmailReceived");
		String retval = Globals.KEYWORD_FAIL;
		HashMap<String, String> emailDetails=  new HashMap<String, String>();
		String emailFound=null;
		
		try{

			String subjectEmailPattern = "Your volunteer background check with "+Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
			String bodyEmailPattern = Globals.testSuiteXLS.getCellData_fromTestData("CustomEmailMessage");

			emailDetails = Email.fetchEmailFromOutlook(subjectEmailPattern,bodyEmailPattern,15);
			emailFound = emailDetails.get("EmailFound");

			if (emailFound.equalsIgnoreCase("True")){
				
				sc.STAF_ReportEvent("Fail", "Claim Account Email", "Volunteer received claim account email.FirstName-"+bodyEmailPattern, 0);
				
			}else{
				sc.STAF_ReportEvent("Pass", "Claim Account Email", "Volunteer did not receive claim account email.Voluunter FirstName-"+bodyEmailPattern, 0);
				retval=Globals.KEYWORD_PASS;
			}


		}catch (Exception e) {
			log.error("Method-CheckForNoEmailReceived | Unable to to verify no email received for claim account | Exception - "+ e.toString());
			sc.STAF_ReportEvent("Fail", "Claim Account Email", "Unable to to verify no email received for claim account", 0);
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to sharing settings for a client in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String ConfigureSharingSetting() throws Exception{
		return InhousePortal.configureSharingSetting();

	}
	
	/**************************************************************************************************
	 * Method to share the order to a client post volunteer login L3 sharing to l1
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String LowerOrderSharing() throws Exception{
		ClientFacingApp.orderSharing();
		String expClientName = Globals.testSuiteXLS.getCellData_fromTestData("ClientToBeShared");
		long timeOutinSeconds = 20;
		if(sc.waitforElementToDisplay("sharing_consent_chk", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			
			sc.STAF_ReportEvent("Fail", "Order Sharing", "Consent Page has not loaded", 1);
			throw new Exception("Order Sharing Consent Page has not loaded");
			
		}
		
		sc.checkCheckBox("sharing_consent_chk");
		sc.clickWhenElementIsClickable("sharing_ok_btn", (int) timeOutinSeconds);
		sc.STAF_ReportEvent("Done", "Order Sharing", "Order has been shared with client-"+expClientName, 0);
		
		sc.clickWhenElementIsClickable("sharing_close_link", (int) timeOutinSeconds);
		if(sc.waitforElementToDisplay("volunteerHomepage_findOrgToShare_link", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			
			sc.STAF_ReportEvent("Fail", "Order Sharing", "Volunteer homepage Page has not loaded", 1);
			throw new Exception("Order Sharing-Volunteer homepage Page has not loaded");
			
		}
		return Globals.KEYWORD_PASS;
		
	}
	
	/**************************************************************************************************
	 * Method to share the order to a client post volunteer login L1 sharing to L3
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String HigherOrderSharing() throws Exception{
		String retval = Globals.KEYWORD_FAIL;
		ClientFacingApp.orderSharing();
				
		long timeOutinSeconds = 20;
		if(sc.waitforElementToDisplay("sharing_startOrder_btn", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			
			sc.STAF_ReportEvent("Fail", "Order Sharing", "Higher level sharing popup NOT displayed", 1);
			throw new Exception("Order Sharing Higher level sharing popup NOT displayed");
			
		}
				
		sc.clickWhenElementIsClickable("sharing_startOrder_btn", (int) timeOutinSeconds);
		
		if(sc.waitforElementToDisplay("invitationStep1_fName_txt", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			
			sc.STAF_ReportEvent("Fail", "Step 1", "Ordering Step 1 page has not been displayed", 1);
			throw new Exception("Ordering Step 1 page has not been displayed");
			
		}
		
		ClientFacingApp.invitationOrderStep1();
		ClientFacingApp.invitationOrderStep2();
		ClientFacingApp.invitationOrderStep3();
		ClientFacingApp.invitationStep3Abuse();
		retval= ClientFacingApp.invitationOrderStep4();
		return retval;
	}
	/**************************************************************************************************
	 * Method to create outparameter for a test case- workaround for out put parameters
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String GenerateOutputParam() throws Exception{
		Globals.OutputParam.putAll(Globals.TestDataCollection);
		return Globals.KEYWORD_PASS;

	}
	
	/**************************************************************************************************  
	 * Method to navigate to email settings tab Edit Button
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author mshaik
     * @throws Exception
     ***************************************************************************************************/
	public String emailSettingsTabEditButton() throws Exception{
		return InhousePortal.emailSettingsTabEditButton();

	}
	/**************************************************************************************************
     * Method for Edit Custom Messaging in email settings page
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author mshaik
     * @throws Exception
     ***************************************************************************************************/
	public String CustomMessaging() throws Exception{
		return InhousePortal.CustomMessaging();

	}
	 /**************************************************************************************************
 	 * Method to configure Product Specific Settings for Abuse pensylvania
 	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
 	 * @author mshaik
 	 * @throws Exception
 	 ***************************************************************************************************/
	public String PAAbusesettings() throws Exception{
		return InhousePortal.PAAbusesettings();

	}
	
	/**************************************************************************************************
	 * Method to replace the test data with previous output param
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String UseOutputParam() throws Exception{
		
		Globals.OutputParam.put("TestCaseDescr", Globals.testSuiteXLS.getCellData_fromTestData("TestCaseDescr"));
		Globals.OutputParam.put("AccountName", Globals.testSuiteXLS.getCellData_fromTestData("AccountName"));
		Globals.OutputParam.put("SharingLevel", Globals.testSuiteXLS.getCellData_fromTestData("SharingLevel"));
		Globals.OutputParam.put("OrgUserName", Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName"));
		Globals.OutputParam.put("PositionName", Globals.testSuiteXLS.getCellData_fromTestData("PositionName"));
		Globals.OutputParam.put("PricingName", Globals.testSuiteXLS.getCellData_fromTestData("PricingName"));
		Globals.OutputParam.put("PositionProducts", Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts"));
		Globals.OutputParam.put("Exp_ReportQuickViewText", Globals.testSuiteXLS.getCellData_fromTestData("Exp_ReportQuickViewText"));
		Globals.OutputParam.put("Exp_ReportProductOverview", Globals.testSuiteXLS.getCellData_fromTestData("Exp_ReportProductOverview"));
		Globals.OutputParam.put("Exp_ReportProductHeaders", Globals.testSuiteXLS.getCellData_fromTestData("Exp_ReportProductHeaders"));
		Globals.OutputParam.put("ClientToBeShared", Globals.testSuiteXLS.getCellData_fromTestData("ClientToBeShared"));
		Globals.OutputParam.put("ExpectedJurisFees", Globals.testSuiteXLS.getCellData_fromTestData("ExpectedJurisFees"));
		
		Globals.TestDataCollection.putAll(Globals.OutputParam) ;
		
		for (String name: Globals.TestDataCollection.keySet()){

            String key =name.toString();
            String value = Globals.TestDataCollection.get(name).toString();  
            Globals.testSuiteXLS.setCellData_inTestData(key, value);

		} 
		return Globals.KEYWORD_PASS;

	}
	public void Debug() throws Exception {
		//		sc.STAF_ReportEvent("Warning", "stepName", "stepDescription", 0);
		//		sc.STAF_ReportEvent("Pass", "stepName", "stepDescription", 0);
		//		sc.STAF_ReportEvent("Fail", "stepName", "stepDescription", 0);
		//		Globals.testSuiteXLS.setCellData_inTestData("VolunteerUsername", "vol035819584");
		//		throw new Exception();
		//		return Globals.KEYWORD_PASS;
		//		ClientFacingApp.fetchVolunteerDashboardCount();


	}

	/**************************************************************************************************
     * Method for EditClientFeatures RatingRestrictions Check Box
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author lvkumar
     * @throws Exception
     ***************************************************************************************************/
	public String RatingRestrictionssettings() throws Exception{
		return InhousePortal.RatingRestrictionssettings();

	}
	
	/**************************************************************************************************
     * Method for EditClientFeatures Bg Dashboard - Batch Printing Check Box
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author lvkumar
     * @throws Exception
     ***************************************************************************************************/
	public String BatchPrintingsettings() throws Exception{
		return InhousePortal.BatchPrintingsettings();

	}
	
	/**************************************************************************************************
     * Method for EditClientFeatures OrderHoldQueue Check Box
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author lvkumar
     * @throws Exception
     ***************************************************************************************************/
	public String OrderHoldQueuesettings() throws Exception{
		return InhousePortal.OrderHoldQueuesettings();

	}
	
	
	
	public String UpdateInvitationStatusToCancelled() throws Exception {
		String retval 	= Globals.KEYWORD_FAIL;
		VVDAO vvDB 		= null;
		try{
			vvDB 	= new VVDAO();
			String dbURL = Globals.getEnvPropertyValue("dbURL");
 			String dbUserName = Globals.getEnvPropertyValue("dbUserName");
 			String dbPassword = ProtectedEncryption.decrypt(Globals.getEnvPropertyValue("dbPassword"), CommonHelpMethods.createKey());
	
 			// 		String dbURL = "jdbc:sqlserver://"+Globals.VV_DBServerName + ";portNumber="+Globals.VV_DBServerPort+";databaseName="+Globals.VV_DBName+";integratedSecurity=true;"; // to use windows authentication include the following code- integratedSecurity=true;";
 			//			log.debug(".........Connecting to VV - DatabaseServer-"+Globals.VV_DBServerName +":"+ "-" +Globals.VV_DBName  );
 			//			this.conn=DriverManager.getConnection(dbURL, Globals.VV_USERNAME, Globals.VV_PASSWORD);

 			log.info("DB URL is :"+"\t"+dbURL);
	
 			vvDB      = new VVDAO(dbURL,dbUserName,dbPassword); 
			
			String statusNotIn = "Canceled";
			String emailAddr =  Globals.fromEmailID;
			
			String selectSQL = "Select Status from Invitation where Status != ? and EmailAddress= ?";
			vvDB.ps = vvDB.conn.prepareStatement(selectSQL,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			vvDB.ps.setString(1, statusNotIn);
			vvDB.ps.setString(2, emailAddr);
			vvDB.rs = vvDB.ps.executeQuery();
			
			int initRowCount = 0;
			
			initRowCount = vvDB.getRows(vvDB.rs);
			
			
			String updateSQLQuery = "update Invitation set Status = ? where Status != ? and EmailAddress= ?";
			vvDB.ps = vvDB.conn.prepareStatement(updateSQLQuery);
			
			vvDB.ps.setString(1, statusNotIn);
			vvDB.ps.setString(2, statusNotIn);
			vvDB.ps.setString(3,emailAddr);
			
			
			int statusUpdateRowCount = 0;
			statusUpdateRowCount = vvDB.ps.executeUpdate();
			
			if(initRowCount ==  statusUpdateRowCount){
				sc.STAF_ReportEvent("Pass", "DB-Update", "Email Invitation status updated to Cancelled", 0);
				retval = Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "DB-Update", "Unable to update all Email Invitation status to Cancelled.ExpCount= "+initRowCount + " Actual- "+statusUpdateRowCount, 0);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			sc.STAF_ReportEvent("Fail", "DB-Update", "Unable to update all Email Invitation status to Cancelled.", 0);
		}finally{
			vvDB.cleanUpVVDAO();
		}
		
				
		
		return retval;
		
		
	}
	 /**************************************************************************************************
		 * Method to configure Rating Restrictions adding ,updating and canceling functionality.
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author mshaik
		 * @throws Exception
		 ***************************************************************************************************/
	public String RatingRestrictions() throws Exception{
		return InhousePortal.RatingRestrictions();

	}
	/**************************************************************************************************
	 * ethod to configure Rating Restrictions adding ,Deleting and canceling functionality.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author mshaik
	 * @throws Exception
	 ***************************************************************************************************/
	public String RatingRestrictions1() throws Exception{
		return InhousePortal.RatingRestrictions1();
	
	}
	 /**************************************************************************************************
		 * Method to verify  gui validations for rating restrictions in Volunteer Dashboard.
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author vgokulanathan
		 * @throws Exception
		 ***************************************************************************************************/
	public String verifyRatingRestriction() throws Exception{
		return ClientFacingApp.verifyRatingRestriction();

	}
	/**************************************************************************************************
	* Method to Volunteer Forgot password for volunteer which having security questions set up 
	* @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	* @author mshaik
	* @throws Exception
	***************************************************************************************************/
	public String VolunteerForgotPassword() throws Exception{
		return ClientFacingApp.VolunteerForgotPassword();
	
	}
	 /**************************************************************************************************
		 * Method to verify  gui validations for payment column in Volunteer Dashboard.
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author vgokulanathan
		 * @throws Exception
		 ***************************************************************************************************/
	public String verifyPaymentVolDashboard() throws Exception{
		return ClientFacingApp.verifyPaymentVolDashboard();

	}
	/**************************************************************************************************
	 * Method to verify bg Report for Order Hold before submit
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String orderHoldBGReportBeforeSubmit() throws Exception {
		return ClientFacingApp.orderHoldBGReportBeforeSubmit();
	}
	/**************************************************************************************************
	 * Method fetch SWEST id from Order hold notification mail recieved when an order on hold is submitted
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String FetchSwestIdFromPrderHoldEmail() throws Exception {
		return ClientFacingApp.FetchSwestIdFromPrderHoldEmail();
	}
	/**************************************************************************************************
	 * Method to verify cancelled order on hold on Volunteer Dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String verifyOrderHoldCancelled() throws Exception{
		return ClientFacingApp.verifyOrderHoldCancelled();
	}
	/**************************************************************************************************
	 * Method to verify Backgroundcheck Hold status on Volunteer Dashboard when an order is placed on hold
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String verifyOrderHoldStatusBeforeReview() throws Exception{
		return ClientFacingApp.verifyOrderHoldStatusBeforeReview();
	}
	/**************************************************************************************************
	 * Method fetch SWEST id from npnorder table for client/batch orders in order hold flow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String FetchSwestIdClientOrder() throws Exception{
		return ClientFacingApp.FetchSwestIdClientOrder();
	}
	/**************************************************************************************************
	 * Method for to create an gooddeedcode with pa abuse and then removing it and validate
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String RemovePAAbuse() throws Exception {
		   APP_LOGGER.startFunction("RemovePAAbuse");
		    String retval = Globals.KEYWORD_FAIL;
			
			ClientFacingApp.updateVolunteerNameInTestData();
			ClientFacingApp.enterCodeAndProceedToStep1();
			ClientFacingApp.checkForIDAndProceed();   
			ClientFacingApp.invitationOrderStep1();
			ClientFacingApp.invitationOrderStep2();
			ClientFacingApp.invitationOrderStep3();
			ClientFacingApp.invitationStep3Abuse();
			ClientFacingApp.invitationOrderStep4();
			retval = Globals.KEYWORD_PASS;
			return retval;
	}
	/**************************************************************************************************
	 * Method to send an renewal invitation to the volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	* @throws Exception
	 ***************************************************************************************************/
	public String sendRenewalInvitation() throws Exception{
		return ClientFacingApp.sendRenewalInvitation();
    }
	/**************************************************************************************************
	 * Method fetch renewal invitation email received using vbs and then stores the captured url in test data
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String FetchRenewalInvitationURLFromEmail() throws Exception {
		APP_LOGGER.startFunction("FetchRenewalInvitationURLFromEmail");
		String retval = Globals.KEYWORD_FAIL;
		HashMap<String, String> emailDetails=  new HashMap<String, String>();
		String emailFound=null;
		String emailBodyText =null;
		String renewalInvitationURL = null;

		try{

			String subjectEmailPattern = "Renewal Invitation from "+Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
			String bodyEmailPattern = Globals.testSuiteXLS.getCellData_fromTestData("CustomEmailMessage");
				
			emailDetails = Email.fetchEmailFromOutlook(subjectEmailPattern,bodyEmailPattern,60);

			emailFound = emailDetails.get("EmailFound");

			if (emailFound.equalsIgnoreCase("True")){
				emailBodyText = emailDetails.get("EmailBody");
				emailBodyText = emailBodyText.replace("\"", "").trim();
				
				String urlPattern="";
				if (Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD"))
				{
					urlPattern = "https://app";
				}
				else{
					urlPattern="https://qapp";
				}
				int startIndex = emailBodyText.indexOf(urlPattern);
				int endindex = emailBodyText.indexOf("If you have any questions,");
				renewalInvitationURL = emailBodyText.substring(startIndex, endindex).trim();

				Globals.testSuiteXLS.setCellData_inTestData("InvitationURL", renewalInvitationURL);
				log.debug(" Method-FetchRenewalInvitationURLFromEmail | URL - " + renewalInvitationURL);
				sc.STAF_ReportEvent("Pass", "Send Renewal Invite", "Applicant Renewal invitation mail received  - "+ renewalInvitationURL, 0);
				retval=Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "Fetch Renewal Invite", "Applicant Renewal invitation mail NOT received  - ", 0);
				throw new Exception("Applicant Renewal Invitation mail NOT received");

			}


		}catch (Exception e) {
			log.error("Method-CreateInvitationOrder | Unable to create Renewal Invitation Order | Exception - "+ e.toString());
			sc.STAF_ReportEvent("Fail", "Send Renewal Invite", "Applicant Renewal invitation mail NOT received", 0);
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method for to create an renewal invitation order by logging to volunteer account  till Order submission.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String RemoveRatingRestriction() throws Exception {
		APP_LOGGER.startFunction("RemoveRatingRestriction");
		String retval = Globals.KEYWORD_FAIL;
		String tempretval = Globals.KEYWORD_FAIL;
		try {

			VolunteerLogin();
			long timeOutinSeconds = 60;
            tempretval=sc.waitforElementToDisplay("volunteerHomepage_interestskill_btn", timeOutinSeconds);
            if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
            	sc.clickWhenElementIsClickable("volunteerHomepage_interestskill_btn", (int) timeOutinSeconds);
    		}
            tempretval=sc.waitforElementToDisplay("volunteerHomepage_getVerified_link", timeOutinSeconds);
            if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
            	sc.clickWhenElementIsClickable("volunteerHomepage_getVerified_link", (int) timeOutinSeconds);
    		}
            tempretval=sc.waitforElementToDisplay("volunteerHomepage_startnewbg_btn", timeOutinSeconds);
            if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
            	sc.clickWhenElementIsClickable("volunteerHomepage_startnewbg_btn", (int) timeOutinSeconds);
    		}
            ClientFacingApp.enterCodeAndProceedToStep1();
    		ClientFacingApp.checkForIDAndProceed();   
    		ClientFacingApp.invitationOrderStep1();
    		ClientFacingApp.invitationOrderStep2();
    		ClientFacingApp.invitationOrderStep3();
    		ClientFacingApp.invitationStep3Abuse();
    		ClientFacingApp.invitationOrderStep4();
    		retval = Globals.KEYWORD_PASS;
		} catch (Exception e) {
			log.error("Method-RemoveRatingRestriction | Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}
	 /**************************************************************************************************
		 * Method to verify  gui validations for removed rating restrictions in Volunteer Dashboard.
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author mshaik
		 * @throws Exception
		 ***************************************************************************************************/
	public String verifyRemovedRatingRestriction() throws Exception{
		return ClientFacingApp.verifyRemovedRatingRestriction();

	}
	/**************************************************************************************************
	* Method to Turn on visibility of Custom fields in Report
	* @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	* @author Navisha
	* @throws Exception
	***************************************************************************************************/
	public String ShowClientConfigOnReport() throws Exception{
		return InhousePortal.ShowClientConfigOnReport();
	}
	
	public String FetchInviatationURLFromDB() throws Exception {
        String retval   = Globals.KEYWORD_FAIL;
        VVDAO vvDB        = null;
        try{
            vvDB    = new VVDAO();
            
            String dbURL = Globals.getEnvPropertyValue("dbURL");
    		 String dbUserName = Globals.getEnvPropertyValue("dbUserName");
    		 String dbPassword = ProtectedEncryption.decrypt(Globals.getEnvPropertyValue("dbPassword"), CommonHelpMethods.createKey());
    		
//    		String dbURL = "jdbc:sqlserver://"+Globals.VV_DBServerName + ";portNumber="+Globals.VV_DBServerPort+";databaseName="+Globals.VV_DBName+";integratedSecurity=true;"; // to use windows authentication include the following code- integratedSecurity=true;";
//			Globals.log.debug(".........Connecting to VV - DatabaseServer-"+Globals.VV_DBServerName +":"+ "-" +Globals.VV_DBName  );
//			this.conn=DriverManager.getConnection(dbURL, Globals.VV_USERNAME, Globals.VV_PASSWORD);

    		 log.info("DB URL is :"+"\t"+dbURL);
    		
    		 vvDB      = new VVDAO(dbURL,dbUserName,dbPassword); 
             //vvDB.connectVV_DB(); // connect to absHire database
            log.info("Connected to VV Databse successfully");
            
            String bodyEmailPattern = Globals.testSuiteXLS.getCellData_fromTestData("CustomEmailMessage");
            
            String format ="YYYY-MM-dd";
            Format formatter = new SimpleDateFormat(format);
            Date result = new java.util.Date();

            String orderedDate = formatter.format(result); 
           
                            
            String selectSQL = "Select EmailBody from emailhistory where SentDate >= '"+orderedDate+"' AND EmailBody like '%"+bodyEmailPattern+"%'";
            
            log.info("Sql query for invitation"+selectSQL );
            
            vvDB.ps = vvDB.conn.prepareStatement(selectSQL,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Thread.sleep(5000); 
            vvDB.rs = vvDB.ps.executeQuery();
            
            int initRowCount = 0;
            
            initRowCount = vvDB.getRows(vvDB.rs);
            
            if(initRowCount != 1) {
            	int cnt=1;
            	while(initRowCount == 1){
            		vvDB.rs = vvDB.ps.executeQuery();
            		Thread.sleep(2000);
            		initRowCount = vvDB.getRows(vvDB.rs);
            		log.info("Query executed times : "+ cnt );
            	            	
            		if(cnt==5){
            			break;
            		}
            		cnt=cnt+1;
            	}
            	if(initRowCount != 1){
                log.error("Unable to fetch invitation email from database as sql returned "+initRowCount + " rows");
                retval = Globals.KEYWORD_FAIL;
            	}
            }else {
                while(vvDB.rs.next()){
                    retval = vvDB.rs.getString("EmailBody");    
                }
                
            }
            
                   
        }catch(Exception e){
            e.printStackTrace();
            sc.STAF_ReportEvent("Fail", "DB-FetchEmail", "Unable to Unable to fetch invitation email from DB", 0);
            throw e;
        }finally{
            vvDB.cleanUpVVDAO();
        }
        
                
        
        return retval;
        
        
    }
	/**************************************************************************************************
	 * Method to verify  gui validations for Custom Sort in Volunteer Dashboard.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String VerifyCustomSort() throws Exception{
		return ClientFacingApp.verifyCustomSort();
    
	}
	
	/**************************************************************************************************
	 * Method for to navigate to Account Settings page for a logged in  Organization user
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String navigateAccountSetting() throws Exception {
		APP_LOGGER.startFunction("AccountSettingPage");
		String retval = Globals.KEYWORD_FAIL;
		try {

			retval=AccountSetting.navigateAccountSetting();
			
		} catch (Exception e) {
			log.error("Method-AccountSetting Page | Navigate to Account Setting page| Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method for to navigate to Account Settings page for a logged in  Organization user
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String navigateOrganizationUsers() throws Exception {
		APP_LOGGER.startFunction("AccountSettingPage");
		String retval = Globals.KEYWORD_FAIL;
		try {

			retval=AccountSetting.navigateOrganizationUsers();
			
		} catch (Exception e) {
			log.error("Method-AccountSetting Page | Navigate to Account Setting page| Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to fulfill an order in SWest through backend services.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String verifyvalues() throws Exception{
		return AccountSetting.verifyvalues();    
	}
	
	/**************************************************************************************************
	 * Method to fulfill an order in SWest through backend services.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String nagativeValidationForNewUser() throws Exception{
		return AccountSetting.nagativeValidationForNewUser();    
	}
	
	/**************************************************************************************************
	 * Method to fulfill an order in SWest through backend services.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public String verifyAccountSettingExport() throws Exception{
		return AccountSetting.verifyAccountSettingExport();    
	}
	/**************************************************************************************************
	 * Method for launching the onlinesignup url
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String OnlineSignupLaunch() throws Exception {
		APP_LOGGER.startFunction("OnlineSignupLaunch");
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		try {
			
			String urlLaunched= sc.launchURL(Globals.BrowserType,Globals.getEnvPropertyValue("OnlineSignupAppURL"));
			
			if (urlLaunched.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			//try one more time by checking whether session is already open.if so log out and continue
				tempRetval=sc.waitforElementToDisplay("Onlinesignup_welcomepage_btn", 3);
				sc.STAF_ReportEvent("Pass", "OnlineSignupLaunch", "Online Signup Page has been loaded", 1);
				retval=Globals.KEYWORD_PASS;
			}	

		}catch(Exception e){
			log.error("Exception occurred in Organization user Login | "+e.toString());
			throw e;
		}

		return retval;
	}
	
	/**************************************************************************************************
	 * Method for to validating online signup pages
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String OnlineSignupPageValidation() throws Exception {
		APP_LOGGER.startFunction("OnlineSignupPageValidation");
		String retval = Globals.KEYWORD_FAIL;
		try {
            
			retval= OnlineSignupApp.onlineSignupQuotePage();
			retval= OnlineSignupApp.onlineSignupWelcomePage();
			retval= OnlineSignupApp.onlineSignupAgreementPage();
			retval= OnlineSignupApp.onlineSignupOrgInfoPage();
			retval= OnlineSignupApp.onlineSignupBillInfoPage();
			retval= OnlineSignupApp.onlineSignupVendorInfoPage();
			retval= OnlineSignupApp.onlineSignupPermissiblePurposePage();
			retval= OnlineSignupApp.onlineSignupOrgUsersPage();
			retval= OnlineSignupApp.onlineSignupSelectPackagePage();
			retval= OnlineSignupApp.onlineSignupSelectPaymentPage();
			retval= OnlineSignupApp.onlineSignupOtherConfigPage();
			retval= OnlineSignupApp.onlineSignupVerifyPageOne();
			
		} catch (Exception e) {
			log.error("Method-OnlineSignupPageValidation | Validation of Online Signup pages | Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to check if Docusign email is getting triggered to the client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String CheckDocusignEmail() throws Exception {
		APP_LOGGER.startFunction("CheckDocusignEmail");
		String retval = Globals.KEYWORD_FAIL;
		HashMap<String, String> emailDetails=  new HashMap<String, String>();
		String emailFound=null;
		String emailBodyText =null;
		String invitationURL = null;

		try{
		    
		    
			String subjectEmailPattern = "Action Required: Please Review & Sign your Verified Volunteers Contract";
			String bodyEmailPattern = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
			String urlPattern="";
         
            int startIndex;
            int endindex ;

			    emailDetails = Email.fetchEmailFromOutlook(subjectEmailPattern,bodyEmailPattern,60);   
			    emailFound = emailDetails.get("EmailFound");
			    if (emailFound.equalsIgnoreCase("True")){
//	                emailBodyText = emailDetails.get("EmailBody");
//	                emailBodyText = emailBodyText.replace("\"", "").trim();
//	                startIndex = emailBodyText.indexOf(urlPattern);
//	                endindex = emailBodyText.indexOf("If you have any questions,");
			    	 sc.STAF_ReportEvent("Pass", "Client Docusign Email", "Client Docusign Email received in outlook client", 0);
			    	 retval=Globals.KEYWORD_PASS;
			    }else{
	                sc.STAF_ReportEvent("Fail", "Client Docusign Email", "Client Docusign Email NOT received in outlook client", 0);
	                throw new Exception("Client Docusign Email NOT received in Outlook client");

	            }
		}catch (Exception e) {
			log.error("Method-CheckDocusignEmail | Unable to CheckDocusignEmail | Exception - "+ e.toString());
			sc.STAF_ReportEvent("Fail", "Client Docusign Email", "Client Docusign EmailNOT received", 0);
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to check if Docusign email is getting triggered to the client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public String verifyOrgUserPassExp() throws Exception {
		APP_LOGGER.startFunction("verifyOrgUserPassExp");
		String retval = Globals.KEYWORD_FAIL;
		HashMap<String, String> emailDetails=  new HashMap<String, String>();
		String emailFound=null;
		String emailBodyText =null;
		String invitationURL = null;
		VVDAO vvDB             = null;
		try{
			vvDB      = new VVDAO();
			String dbURL = Globals.getEnvPropertyValue("dbURL");
 			String dbUserName = Globals.getEnvPropertyValue("dbUserName");
 			String dbPassword = ProtectedEncryption.decrypt(Globals.getEnvPropertyValue("dbPassword"), CommonHelpMethods.createKey());
	
 			// 		String dbURL = "jdbc:sqlserver://"+Globals.VV_DBServerName + ";portNumber="+Globals.VV_DBServerPort+";databaseName="+Globals.VV_DBName+";integratedSecurity=true;"; // to use windows authentication include the following code- integratedSecurity=true;";
 			//			log.debug(".........Connecting to VV - DatabaseServer-"+Globals.VV_DBServerName +":"+ "-" +Globals.VV_DBName  );
 			//			this.conn=DriverManager.getConnection(dbURL, Globals.VV_USERNAME, Globals.VV_PASSWORD);

 			log.info("DB URL is :"+"\t"+dbURL);
	
 			vvDB      = new VVDAO(dbURL,dbUserName,dbPassword); 

			//vvDB.connectVV_DB(); // connect to absHire database
        
			String selectSQL = "select UserId from Users where username=?";
            String uName=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
			vvDB.ps = vvDB.conn.prepareStatement(selectSQL,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			vvDB.ps.setString(1, uName);
			Thread.sleep(5000);
			vvDB.rs = vvDB.ps.executeQuery();
            int initRowCount = 0;
    
            initRowCount = vvDB.getRows(vvDB.rs);
        
            if(initRowCount==0 || initRowCount==-1 ){
            	sc.STAF_ReportEvent("Fail", "Password Change date", "Unable to change LastPasswordChangedDate for password expiry ", 0); 
                return Globals.KEYWORD_FAIL;
     	    }else{
     	    	while(vvDB.rs.next())
     	    	{
               	String uId=vvDB.rs.getString(1);
               	Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.DATE, 90);
                String formatDate=new SimpleDateFormat("M/d/yyyy").format(c.getTime());
             	//String modifiedDate="2016-01-27";
             	String updateSQLQuery = "update Memberships set LastPasswordChangedDate = ? where UserId =?";
                
                vvDB.ps = vvDB.conn.prepareStatement(updateSQLQuery);
                vvDB.ps.setString(1, formatDate);
    			vvDB.ps.setString(2, uId);
    			
                Thread.sleep(5000);
                int statusUpdateRowCount = 0;
    			statusUpdateRowCount = vvDB.ps.executeUpdate();
    			if(initRowCount ==  statusUpdateRowCount){
    				//sc.STAF_ReportEvent("Pass", "DB-Update", "LastPasswordChangedDate updated to "+formatDate, 0);
    				retval = Globals.KEYWORD_PASS;
    				break;
    			}else{
    				sc.STAF_ReportEvent("Fail", "DB-Update", "Unable to update LastPasswordChangedDate to less than 365 days.ExpCount= "+initRowCount + " Actual- "+statusUpdateRowCount, 0);
    			    return Globals.KEYWORD_FAIL;
    			
    			}
     	    	}
                
         } 
		    
			
		}catch(Exception e){
            e.printStackTrace();
            sc.STAF_ReportEvent("Fail", "MembershipsTable-DB", "Unable to change LastPasswordChangedDate for password expiry org user login", 0);
		   }finally{
                 vvDB.cleanUpVVDAO();
        }
		
		
		
		
		
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify client hierarch dropdown value in bg dashboard.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String dropdownValidation() throws Exception{
		return ClientHierarchy.dropdownValidation();    
	}
	/**************************************************************************************************
	 * Method to verify client hierarch dropdown value in bg dashboard.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String dropdownValidation1() throws Exception{
		return ClientHierarchy.dropdownValidation1();    
	}
	/**************************************************************************************************
	 * Method to verify client hierarch dropdown value in bg dashboard.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public String validationOrderingDropdown() throws Exception{
		return ClientHierarchy.validationOrderingDropdown();    
	}
	
	
	public String SeamlessPayementConsent() throws Exception{
		ClientFacingApp.fetchURLAndLaunch();
		
		return ClientFacingApp.seamlessPayementConsent();    
	}
	
	  
 
    /**************************************************************************************************
     * Method for Warning message 
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author gaurimodani
     * @throws Exception
     ***************************************************************************************************/
    public String volunteerWarningmessage() throws Exception {
        return  ClientFacingApp.volunteerWarningmessage();
    }
    /**************************************************************************************************
     * Method to create draft order 
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author gaurimodani
     * @throws Exception
     ***************************************************************************************************/
    
    public String LaunchDocuSign()throws Exception{
        APP_LOGGER.startFunction("CreateDraftInvitationOrder");
        String retval = Globals.KEYWORD_FAIL;
        int timeOutinSeconds=20;
        try {
               // String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
                driver.navigate().to("https://demo.docusign.net/Member/EmailStart.aspx?a=8a107408-b192-40b2-857f-6d28dc85b9a9&acct=674f1995-fc03-48c3-bea5-f4e28ed4a461&er=66ac1ca1-91ce-4ab0-936c-267ae8daa7d9&espei=57eca2eb-dcc0-46b3-9b4f-ba1dd9db150f");
               // retval= ClientFacingApp.volunteerAccountCreation();
                sc.waitForPageLoad();
                sc.waitTillElementDisplayed(driver.findElement(By.xpath("//*[@id='action-bar-btn-continue']")), timeOutinSeconds);
                sc.clickElementUsingJavaScript(driver.findElement(By.xpath("//*[@id='action-bar-btn-continue']")));
                sc.waitTillElementDisplayed(driver.findElement(By.xpath("//div[starts-with(@data-label,'legal name_Text_')]")), timeOutinSeconds);
                String abc=sc.getWebElementText(driver.findElement(By.xpath("//div[starts-with(@data-label,'legal name_Text_')]")));
                sc.clickElementUsingJavaScript(driver.findElement(By.xpath("//*[@id='navigate-btn']")));
                
                sc.clickElementUsingJavaScript(driver.findElement(By.xpath("//div[starts-with(@data-label,'501c3 Attachment_SignerAttachment_')]//button")));
                String updatedFilePath = "C:\\Users\\psave\\Pictures\\uploadImage.jpg";
                driver.findElement(By.xpath("//input[@data-qa='file-upload']")).sendKeys(updatedFilePath);
                String uploadTxt=sc.getWebElementText(driver.findElement(By.xpath("//*[@id='doc-list']/li/div/span[1]")));
                sc.clickElementUsingJavaScript(driver.findElement(By.xpath("//button[@data-qa='dialog-submit']")));
                
                sc.clickElementUsingJavaScript(driver.findElement(By.xpath("//div[starts-with(@data-label,'Screenshot Website Attachment_SignerAttachment_')]//button")));
                driver.findElement(By.xpath("//input[@data-qa='file-upload']")).sendKeys(updatedFilePath);
                uploadTxt=sc.getWebElementText(driver.findElement(By.xpath("//*[@id='doc-list']/li/div/span[1]")));
                sc.clickElementUsingJavaScript(driver.findElement(By.xpath("//button[@data-qa='dialog-submit']")));
                
                sc.clickElementUsingJavaScript(driver.findElement(By.xpath("//*[@id='navigate-btn']")));
                
                
                List<WebElement> signList=driver.findElements(By.xpath("//button[starts-with(@data-qa,'signature-tab-required')]"));
                int flg=1;
                for(WebElement wb:signList){
                	 wb.click();
                	 if(flg==1){
                		// driver.findElement(By.xpath("//button[@data-qa='adopt-submit']")).click();
                	 }
                	 flg++;
                }
                
                //authorized by , title, phone email fill up 
                
                sc.scrollIntoView(driver.findElement(By.xpath("//div[starts-with(@data-label,'Authorized By pg 6_Text')]/input")));
                sc.setValue(driver.findElement(By.xpath("//div[starts-with(@data-label,'Authorized By pg 6_Text')]/input")), "tester");
                sc.setValue(driver.findElement(By.xpath("//div[starts-with(@data-label,'titlepg6_Text')]/input")), "tester");
                sc.setValue(driver.findElement(By.xpath("//div[starts-with(@data-label,'phone pg 6_Text')]/input")), "7895674356");
                sc.setValue(driver.findElement(By.xpath("//div[starts-with(@data-label,'email pg6_Text')]/input")), "pdfg@df.com");
               
                sc.scrollIntoView(driver.findElement(By.xpath("//div[starts-with(@data-label,'titlepg7_Text')]/input")));
                sc.setValue(driver.findElement(By.xpath("//div[starts-with(@data-label,'titlepg7_Text')]/input")), "tester title");
                
                sc.scrollIntoView(driver.findElement(By.xpath("//div[starts-with(@data-label,'print name serv agree arch_Text')]/input")));
                sc.setValue(driver.findElement(By.xpath("//div[starts-with(@data-label,'print name serv agree arch_Text')]/input")), "tester print name");
                sc.setValue(driver.findElement(By.xpath("//div[starts-with(@data-label,'title serv agree arch_Text')]/input")), "tester titile");
                
                sc.scrollIntoView(driver.findElement(By.xpath("//div[starts-with(@data-label,'print name pg 21 2_Text')]/input")));
                sc.setValue(driver.findElement(By.xpath("//div[starts-with(@data-label,'print name pg 21 2_Text')]/input")), "tester print name");
                sc.setValue(driver.findElement(By.xpath("//div[starts-with(@data-label,'title serv pg 21 2_Text')]/input")), "tester titile");
              
                //button[@id='action-bar-btn-finish']
                sc.clickElementUsingJavaScript(driver.findElement(By.xpath("//button[@id='action-bar-btn-finish']")));
                sc.clickElementUsingJavaScript(driver.findElement(By.xpath("//*[@id='save-a-copy-done']//button[@data-qa='dialog-submit']")));
                
                //online sign up congratulation page need
                
                retval = Globals.KEYWORD_PASS;    
        } 
        catch (Exception e) 
        {
            log.error("Method-CreateInvitationOrder | Unable to create Invitation Order | Exception - "+ e.toString());
            retval = Globals.KEYWORD_FAIL;
            throw e;
        }
        sc.waitTillElementDisplayed("login_volunteerUsername_txt", timeOutinSeconds);
        return retval;
    }
	
	/**************************************************************************************************
	 * Method to create draft order 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author gaurimodani
	 * @throws Exception
	 ***************************************************************************************************/
	
	public String CreateDraftInvitationOrder()throws Exception{
		APP_LOGGER.startFunction("CreateDraftInvitationOrder");
		String retval = Globals.KEYWORD_FAIL;
		int timeOutinSeconds=20;
		try {
				String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
				driver.navigate().to(url);
				retval= ClientFacingApp.volunteerAccountCreation();
				sc.waitforElementToDisplay("invitationStep1_fName_txt", timeOutinSeconds);
				sc.waitTillElementDisplayed("volunteerHomepage_logout_link", timeOutinSeconds);
				sc.clickWhenElementIsClickable("volunteerHomepage_logout_link", timeOutinSeconds);
				Alert alert = driver.switchTo().alert(); 
				alert.accept(); 
				retval = Globals.KEYWORD_PASS;	
		} 
		catch (Exception e) 
		{
			log.error("Method-CreateInvitationOrder | Unable to create Invitation Order | Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		sc.waitTillElementDisplayed("login_volunteerUsername_txt", timeOutinSeconds);
		return retval;
	}
	
	/**************************************************************************************************
	 * Method for volunteer re account creation 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author gaurimodani
	 * @throws Exception
	 ***************************************************************************************************/
	public String volunteerReaccountCreation() throws Exception {
		APP_LOGGER.startFunction("Create volunteer re account");
		String retval = Globals.KEYWORD_FAIL;
		retval=ClientFacingApp.volunteerReaccountCreation();
		retval=ClientFacingApp.volunteerWarningmessage();
		return  retval;
	}

	/**************************************************************************************************
	 * Method for Invalid Volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author gaurimodani
	 * @throws Exception
	 ***************************************************************************************************/
	public String Invalidvolunteer() throws Exception {
		return  ClientFacingApp.Invalidvolunteer();
	}
	

	/**************************************************************************************************
	 * Method for Cancel Volunteer Relaunch
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author gaurimodani
	 * @throws Exception
	 ***************************************************************************************************/
	public String CancelVolunteerRelaunch() throws Exception {
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds=30;
		String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
		String urlLaunched = sc.launchURL(Globals.BrowserType, url);
		retval=sc.waitforElementToDisplay("volunteer_InvalidPage_txt", timeOutinSeconds);
		if((retval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
			sc.STAF_ReportEvent("Pass", "Invalid Volunteer page ", "Invalid Volunteer page is displayed ", 1);
			}
			else{
				sc.STAF_ReportEvent("Fail", "Invalid Volunteer page ", "Invalid Volunteer page is not displayed ", 1);
			}
		return  retval;
	}
	
	
	/**************************************************************************************************
	 * Method to create an Draft API ordering.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author gaurimodani
	 * @throws Exception
	 ***************************************************************************************************/
	public String CreateDraftAPIOrder() throws Exception {
		APP_LOGGER.startFunction("Create Draft APIOrder");
		String retval = Globals.KEYWORD_FAIL;
		int timeOutinSeconds=20;
		try {
            String apiShare=sc.testSuiteXLS.getCellData_fromTestData("SharingType") ;
			
			if(apiShare.equalsIgnoreCase("API Share")){
				retval= ClientFacingApp.apiExistingAccountLogin();
				retval= ClientFacingApp.verifyAPIOrderStep1();
			}else{
			   String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
			   sc.launchURL(Globals.BrowserType, url);
               retval= ClientFacingApp.apiAccountCreation();
			   //retval = ClientFacingApp.clickOnGetVerified();
			   ClientFacingApp.checkForIDAndProceed();
		       retval= ClientFacingApp.verifyAPIOrderStep1();
		       sc.waitTillElementDisplayed("volunteerHomepage_logout_link", timeOutinSeconds);
		       sc.clickWhenElementIsClickable("volunteerHomepage_logout_link", timeOutinSeconds);
		       Alert alert = driver.switchTo().alert(); 
		       alert.accept(); 
		       retval = Globals.KEYWORD_PASS;	
			}
		} catch (Exception e) {
			sc.STAF_ReportEvent("Fail", "API Ordering", "Exception occurred during API Order creation-Exception - "+ e.toString(),1);
			log.error("Method-CreateAPIOrder | Unable to create API Order | Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to login with different candidate url 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author gaurimodani
	 * @throws Exception
	 ***************************************************************************************************/
	public String ApiOtherVolunteerLogin() throws Exception {
		APP_LOGGER.startFunction("Login with different volunteer");
		String retval = Globals.KEYWORD_FAIL;
		String username="vol085444298";
		int timeOutinSeconds = 20;
		try
		{
		Thread.sleep(4000);
		String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
		sc.launchURL(Globals.BrowserType, url);
		sc.waitforElementToDisplay("login_volunteerUsername_txt", timeOutinSeconds);
		retval=sc.setValueJsChange("login_volunteerUsername_txt",username);
		String password =	sc.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
		retval		=	sc.setValueJsChange("login_volunteerPwd_txt",password );
		sc.clickWhenElementIsClickable("apiVolunteers_LogIn_btn",timeOutinSeconds);
		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-Re login API | Exception - "+ e.toString());
			throw e;
		}
		retval = sc.waitforElementToDisplay("volunteerHomepage_myProfile_link",timeOutinSeconds);
		if (retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
		sc.STAF_ReportEvent("Pass", "API different account login ", "Different account profile page", 1);
		}
		else{
			sc.STAF_ReportEvent("Fail", "API different account login ", "Different account profile page is not displayed", 1);
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to verify invitation has been canceled 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author gauri.modani
	 * @throws Exception
	 ***************************************************************************************************/
	public String CancelInvitationOrder() throws Exception{
		return ClientFacingApp.cancelInvitationOrder();    
	}
	
	/**************************************************************************************************
	 * Method to verify invitation has been canceled 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author gauri.modani
	 * @throws Exception
	 ***************************************************************************************************/
	public String CancelInviRemoveRow() throws Exception{
		return ClientFacingApp.cancelInviRemoveRow();    
	}
	
}
