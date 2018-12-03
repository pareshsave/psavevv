package com.sterlingTS.vv;

import java.sql.ResultSet;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.sterlingTS.generic.appLib.AdminClient;
import com.sterlingTS.seleniumUI.seleniumCommands;
import com.sterlingTS.utils.commonUtils.APP_LOGGER;
import com.sterlingTS.utils.commonUtils.BaseCommon;
import com.sterlingTS.utils.commonUtils.CommonHelpMethods;
import com.sterlingTS.utils.commonUtils.LocatorAccess;
import com.sterlingTS.utils.commonUtils.ProtectedEncryption;
import com.sterlingTS.utils.commonUtils.XMLUtils;
import com.sterlingTS.utils.commonVariables.Enum.VV_Products;
import com.sterlingTS.utils.commonVariables.Globals;

public class InhousePortal extends BaseCommon{
	
    public static Logger log = Logger.getLogger(AdminClient.class);
	
	public static seleniumCommands sc = new seleniumCommands(driver);
	
	/**************************************************************************************************
	 * Method to create a new account in Inhouseportal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String inhouseAccountCreation() throws Exception{
		APP_LOGGER.startFunction("inhouseAccountCreation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		try {

			// verify and set account details
			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");

			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}

			tempRetval = verifyCompanyInformation(fieldLevelValidation);

			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				return retval;
			}
			
			tempRetval = createNotes();
			tempRetval = verifyNotes();
			//verify 
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = verifyStaffing(fieldLevelValidation);
			tempRetval = verifyNotes();

			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				return retval;
			}
		  
			String customPricingReq=Globals.testSuiteXLS.getCellData_fromTestData("CreateCustomPrice");
			if(customPricingReq.equalsIgnoreCase("Yes")){
				tempRetval = Globals.KEYWORD_FAIL;
				tempRetval = newPricingSetup(fieldLevelValidation);
				tempRetval = verifyNotes();

				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					return retval;
				}
			}
           
          
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = newBillingSetup(fieldLevelValidation);
			tempRetval = verifyNotes();

			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				return retval;
			}
            
			
			//need to make the account as active
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = activateAccount();

			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				return retval;
			}
			
			
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = newUserCreation(fieldLevelValidation,"normal");
			tempRetval = newUserCreation(fieldLevelValidation,"custom");
			tempRetval = verifyNotes();

			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				return retval;
			}
           
		
			String authorizationReqd=Globals.testSuiteXLS.getCellData_fromTestData("AuthorizationReqd");
			if(authorizationReqd.equalsIgnoreCase("Yes")){
				tempRetval = Globals.KEYWORD_FAIL;
				tempRetval = newAuthorizationSetup();
				tempRetval = verifyNotes();
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					return retval;
				}
			}
		
			//verfication of position listings
			tempRetval = verifyPositionListingsTab();
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				return retval;
			}
			tempRetval = verifyNotes();
			
            
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = otherConfigurations();
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				return retval;
			}
			
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = Questionnaire();
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				return retval;
			}
			//verfication of api configurations
			String apiConfigReqd=Globals.testSuiteXLS.getCellData_fromTestData("ApiConfigReqd");
			if(apiConfigReqd.equalsIgnoreCase("Yes")){
				tempRetval = Globals.KEYWORD_FAIL;
				tempRetval = newAPIConfiguration();
				tempRetval = verifyNotes();
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					return retval;
				}
			}

			//verfication of associated account
			String parentAccnt;
			parentAccnt=Globals.testSuiteXLS.getCellData_fromTestData("ParentAccount");
			if(!parentAccnt.isEmpty() || !(parentAccnt.equalsIgnoreCase(""))){
			  	tempRetval = Globals.KEYWORD_FAIL;
				tempRetval = newAssociatedAccount();
				tempRetval = verifyNotes();
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					return retval;
				}
			}

			//verfication of Available states
			String stateConfigReqd;
			stateConfigReqd=Globals.testSuiteXLS.getCellData_fromTestData("AvailableStatesConfigReqd");
			if(stateConfigReqd.equalsIgnoreCase("Yes") ){
				tempRetval = Globals.KEYWORD_FAIL;
				tempRetval = validateAvailableStates();
				tempRetval = verifyNotes();
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					return retval;
				}
			}
			tempRetval = navigateToEmailSettingsTab();
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				tempRetval = validateMinSharingProductLevel();
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					return retval;
				}
			}
			
					
			
			/*
			//verify welcome email for normal user and login into vv application change password 
			String fname=Globals.testSuiteXLS.getCellData_fromTestData("User_Fname");
			String lname=Globals.testSuiteXLS.getCellData_fromTestData("User_Lname");
			String username=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
			tempRetval=AccountSetting.verifyWelcomeEmails(username, fname, lname,"true");
			staffLogin();
			*/
			retval=Globals.KEYWORD_PASS;
		}catch (Exception e) {
			log.error("Method-inhouseAccountCreation | Unable to create New Account in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify and set data in company information tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyCompanyInformation(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("verifyCompanyInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {


			//need to check whether account already exists
			String accountName;

			accountName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName");

			sc.setValueJsChange("searchAccount_accountSearch_txt", accountName);

			sc.clickWhenElementIsClickable("seachAccount_searchBtn_btn", 10);


			String accountFound = Globals.KEYWORD_FAIL;
			int i=0;
			int rowCount =0;

			if (sc.waitforElementToDisplay("searchAccount_accountGrid_tbl",timeOutInSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){ 

				WebElement accountsTable = sc.createWebElement("searchAccount_accountGrid_tbl");
				rowCount=sc.getRowCount_tbl(accountsTable);

				if(rowCount != Globals.INT_FAIL && rowCount > 1){
					int accountCount = rowCount -1;
					log.info("Method InhouseAccountSearch | Account result grid displayed with " + accountCount + " account(s)" );

					String accountNameInUI;


					for(i= 1;i<rowCount;i++){
						//TODO - need to fetch the column position dynamically.
						accountNameInUI = sc.getCellData_tbl(accountsTable, i, 2);
						if(accountNameInUI.equalsIgnoreCase(accountName)){
							accountFound = Globals.KEYWORD_PASS;
							break;
						}

					}
				}

				if(accountFound.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Fail", "To create a new account for VV", "Account already exists.Account Name - "+accountName, 1);
					return Globals.KEYWORD_FAIL; 
				}else{
					sc.STAF_ReportEvent("Pass", "To create a new account for VV", "Account doesnt exists.Account Name - "+accountName, 1);
					retval = Globals.KEYWORD_PASS;
				}


				sc.clickWhenElementIsClickable("manageAccount_newAccount_link", timeOutInSeconds);
				sc.clickWhenElementIsClickable("manageAccount_singleAccount_link", timeOutInSeconds); // QAA-399 VV4.4 Maintenance changes
				tempRetval =  sc.waitforElementToDisplay("newAccCompanyInfo_accountName_txt", timeOutInSeconds);

				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					log.debug("Method-verifyCompanyInformation - Page has not been loaded for new account creation");
					return retval;
				}

				if(fieldValidationReq.equalsIgnoreCase("Yes")){

					tempRetval = verifyFieldsInCompanyInformation();
				}

				tempRetval = setCompanyInformation();

				if(fieldValidationReq.equalsIgnoreCase("Yes")){

					tempRetval = verifyFieldsAfterAccountCreation();
				}

				retval = Globals.KEYWORD_PASS;
			}
		}catch (Exception e) {
			log.error("Method-verifyCompanyInformation | Unable to create New Account in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify and set data in company information tab with GUI validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInCompanyInformation() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInCompanyInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("newAccCompanyInfo_accountName_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInCompanyInformation - Page has not been loaded for account creation");
				return retval;
			}


			sc.clickWhenElementIsClickable("newAccount_create_btn", timeOutInSeconds);

			expectdText = "Please review the information that you entered: IndustryValue, AccountUseTypeId, Market, AccountName, BadgeName, AddressLine1, City, State, ZipCode, CustomerType, VulnerableSector, NumberOfVolunteerString, NumberofScreenedString, AnnualContractString";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate the Mandatory Fields on Account Creation page", "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate the Mandatory Fields on Account Creation page", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
			}


			retval = Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInCompanyInformation | Unable to create New Account in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to set data in company information tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setCompanyInformation() throws Exception{

		APP_LOGGER.startFunction("setCompanyInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("newAccCompanyInfo_accountName_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInCompanyInformation - Page has not been loaded for account creation");
				return retval;
			}

			String accountName;
			accountName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName");

			String addressLine ="test address Line";
			String city="Putnam";
			//Anand 2/15-removing address line & city columns from test data

			//company Information
			tempRetval = sc.setValueJsChange("newAccCompanyInfo_accountName_txt", accountName);
			String badgeNM="Automation Acc-"+ accountName.substring(accountName.indexOf("Acc")+3);
			tempRetval = sc.setValueJsChange("newAccCompanyInfo_badgeName_txt", badgeNM); // set account name as badge name
			tempRetval = sc.setValueJsChange("newAccCompanyInfo_addressLine1_txt", addressLine);
			tempRetval = sc.setValueJsChange("newAccCompanyInfo_addressLine2_txt",addressLine);
			tempRetval = sc.setValueJsChange("newAccCompanyInfo_city_txt", city);
			tempRetval = sc.selectValue_byVisibleText("newAccCompanyInfo_state_dd", Globals.testSuiteXLS.getCellData_fromTestData("Account_State"));
			tempRetval = sc.setValueJsChange("newAccCompanyInfo_zipCode_txt", "67004");

			tempRetval = sc.setValueJsChange("newAccCompanyInfo_website_txt", "www.sterlingbackcheck.com");
			tempRetval = sc.setValueJsChange("newAccCompanyInfo_mainPhone_txt", "9812345678");
			tempRetval = sc.setValueJsChange("newAccCompanyInfo_mainEmail_txt", Globals.fromEmailID);
			tempRetval = sc.setValueJsChange("newAccCompanyInfo_salesForceLink_txt", "https://na30.salesforce.com/00O36000006xNbt");
			tempRetval = sc.selectValue_byVisibleText("newAccCompanyInfo_otherPlatform_dd", "Sterling West");
			tempRetval = sc.setValueJsChange("newAccCompanyInfo_annualForecastRev_txt", "1234567");

			//status information
            Select select = new Select(sc.createWebElement("newAccStatusInfo_status_dd"));
			String AccountInfoStatus =  select.getFirstSelectedOption().getText();
            if(AccountInfoStatus.equalsIgnoreCase("Setup")){
            	sc.STAF_ReportEvent("Pass", "To validate AccStatusInfo field on main page for new account on VV", "AccStatusInfo field value is visible by default " +AccountInfoStatus,1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate AccStatusInfo field on main page for new account on VV", "AccStatusInfo field value is not visible by default" +AccountInfoStatus,1);
            }
			//tempRetval = sc.selectValue_byVisibleText("newAccStatusInfo_status_dd", "Setup");

			//account type selection
			String accountType;
			accountType = Globals.testSuiteXLS.getCellData_fromTestData("AccountType");
			if(accountType == null || accountType.isEmpty() || accountType.equalsIgnoreCase("")){
				accountType = "Standard";
			}

			tempRetval = sc.selectValue_byVisibleText("newAccStatusInfo_custType_dd", accountType);

			// charge pilot check box would only be applicable when Customer type is Pilot
			if(accountType.equalsIgnoreCase("Pilot")){

				String chargePilot;
				chargePilot = Globals.testSuiteXLS.getCellData_fromTestData("ChargePilot");
				if(chargePilot.equalsIgnoreCase("Yes")){
					tempRetval = sc.checkCheckBox("newAccStatusInfo_chargePilot_chk");
				}

			}



			//account pilot and charges
			String isAccountPilot;
			isAccountPilot = Globals.testSuiteXLS.getCellData_fromTestData("IsAccountPrivate");
			if(isAccountPilot == null || isAccountPilot.isEmpty() || isAccountPilot.equalsIgnoreCase("")){
				isAccountPilot = "No";
			}

			if(isAccountPilot.equalsIgnoreCase("Yes")){
				tempRetval = sc.checkCheckBox("newAccStatusInfo_privateAccount_chk");
			}


			tempRetval = sc.selectValue_byVisibleText("newAccStatusInfo_market_dd", "Nonprofit");
			tempRetval = sc.selectValue_byVisibleText("newAccStatusInfo_industry_dd", "Enviroment");
			String accountUse = Globals.testSuiteXLS.getCellData_fromTestData("AccountUse");
			if(accountUse.equals("Volunteerism")){
				accountUse ="Volunteerism/Non-employee position";
			}
			else if(accountUse.equals("Employment")){
				accountUse ="Employment";
			}
			else{
				sc.STAF_ReportEvent("Fail", "Account Setup", "Could not select the account use value , please check the value in testdata for AccountUse column",0);
			}
			tempRetval = sc.selectValue_byVisibleText("newAccStatusInfo_accountUse_dd", accountUse);
			tempRetval = sc.selectValue_byVisibleText("newAccStatusInfo_vulnerableSector_dd", "No");
			tempRetval = sc.setValueJsChange("newAccStatusInfo_noOfVolunteers_txt", "12345");
			//tempRetval = sc.setValueJsChange("newAccStatusInfo_screened_txt", "1234");
			tempRetval = sc.setValueJsChange("newAccStatusInfo_annualContractValue_txt", "1000000");
			String screenedtxt = Globals.driver.findElement(LocatorAccess.getLocator("newAccStatusInfo_screened_txt")).getAttribute("value");
            String noOfVolunteers = "12345";
            if(screenedtxt.equals(noOfVolunteers)){
            	sc.STAF_ReportEvent("Pass", "To validate screened_txt field on main page for new account on VV", "noOfVolunteers_txt field value is copied to screened_txt value by default",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate screened_txt field on main page for new account on VV", "noOfVolunteers_txt field value is copied to screened_txt value by default",1);
            }
            tempRetval = sc.setValueJsChange("newAccStatusInfo_forecastNotes_txt", "Test Notes to test Forecast Fields");
            
			sc.clickWhenElementIsClickable("newAccount_create_btn", timeOutInSeconds);
			
			String expectdText = "Please review the information that you entered: ZipCode";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
            
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate the ZipCode Field on Account Creation page", "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate the ZipCode Field on Account Creation page", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
			}
			WebElement zipcode=Globals.driver.findElement(LocatorAccess.getLocator("newAccCompanyInfo_zipCode_txt"));
			zipcode.clear();
			tempRetval = sc.setValueJsChange("newAccCompanyInfo_zipCode_txt", Globals.testSuiteXLS.getCellData_fromTestData("Account_ZipCode"));
			
			sc.clickWhenElementIsClickable("newAccount_create_btn", timeOutInSeconds);
			
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("manageAccount_users_link", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To create a new account for VV", "New Account has been successfully created.AccountName - "+accountName, 1);

			}else{
				sc.STAF_ReportEvent("Fail", "To create a new account for VV", "Unable to create new Account.AccountName - "+accountName, 1);
			}

			String custID;
			custID =  Globals.driver.findElement(LocatorAccess.getLocator("newAccount_custID_txt")).getText();
			if(custID == null || custID.isEmpty()){
				sc.STAF_ReportEvent("Fail", "To create a new account for VV", "CustID not generated", 1);
				return retval;
			}else{
				sc.STAF_ReportEvent("Pass", "To create a new account for VV", "CustID has been successfully created.CustID - "+custID, 1);
				retval = Globals.KEYWORD_PASS;
			}


		}
		catch (Exception e) {
			log.error("Method-verifyCompanyInformation | Unable to create New Account in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify fields after acoount creation in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsAfterAccountCreation(){
		APP_LOGGER.startFunction("setCompanyInformation");
		String retval = Globals.KEYWORD_FAIL;

		try {

			sc.isDisplayed("manageAccount_staffing_link", true);
			sc.isDisplayed("manageAccount_users_link", true);
			sc.isDisplayed("manageAccount_pricing_link", true);
			sc.isDisplayed("manageAccount_authorization_link", true);
			sc.isDisplayed("manageAccount_biling_link", true);
			sc.isDisplayed("manageAccount_otherConfigurations_link", true);
			sc.isDisplayed("manageAccount_positions_link", true);
			sc.isDisplayed("manageAccount_positionListings_link", true);
			sc.isDisplayed("manageAccount_questionnaire_link", true);
			sc.isDisplayed("manageAccount_apiConfiguration_link", true);
			sc.isDisplayed("manageAccount_associatedAccounts_link", true);
			sc.isDisplayed("manageAccount_availableStates_link", true);
			sc.isDisplayed("manageAccount_emailSettings_link", true);

			retval=Globals.KEYWORD_PASS;


		}catch (Exception e) {
			log.error("Method-verifyFieldsAfterAccountCreation | UI field Validation - Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify and set data in Staffing tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyStaffing(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("verifyStaffing");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {

			sc.clickWhenElementIsClickable("manageAccount_staffing_link", timeOutInSeconds);
			tempRetval =  sc.waitforElementToDisplay("staffing_primaryAM_dd", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyStaffing - Page has not been loaded for new account creation");
				return retval;
			}

			if(fieldValidationReq.equalsIgnoreCase("Yes")){

				tempRetval = verifyFieldsInStaffing();
			}

			tempRetval = setStaffing();
			retval = tempRetval;
		}
		catch (Exception e) {
			log.error("Method-verifyStaffing | Unable to associate Staffing and Commisions in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify fields in Staffing Tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInStaffing() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInStaffing");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		//String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("staffing_primaryAM_dd", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInStaffing - Page has not been loaded for staffing associations");
				return retval;
			}
            /*Code added and changed by Veena for Onboarding story changes*/
            Select select = new Select(sc.createWebElement("staffing_primaryCSE_dd"));
			String primaryCSE =  select.getFirstSelectedOption().getText();
			String username=Globals.getEnvPropertyValue("Staff_Username");
            if(primaryCSE.equals(username)){
            	sc.STAF_ReportEvent("Pass", "To validate primaryCSE field on Staffing and Commissions for new account on VV", "Staff User name is visible by default in the primaryCSE field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate primaryCSE field on Staffing and Commissions for new account on VV", "Staff User name is not visible by default in the primaryCSE field ",1);
            }
            String phoneui = Globals.driver.findElement(LocatorAccess.getLocator("staffing_phone_txt")).getAttribute("value");
            String phone = "855-326-1820";
            if(phoneui.equals(phone)){
            	sc.STAF_ReportEvent("Pass", "To validate phone field on Staffing and Commissions for new account on VV", "Appsettings phone value is visible by default in the phone field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate phone field on Staffing and Commissions for new account on VV", "Appsettings phone value is not visible by default in the phone field ",1);
            }
            sc.waitforElementToDisplay("staffing_extension_txt", timeOutInSeconds);
            String extnui = Globals.driver.findElement(LocatorAccess.getLocator("staffing_extension_txt")).getAttribute("value");
            String extn = "3";
            if(extnui.equals(extn)){
            	sc.STAF_ReportEvent("Pass", "To validate phoneextn field on Staffing and Commissions for new account on VV", "Appsettings phoneextn value is visible by default in the phoneextn field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate phoneextn field on Staffing and Commissions for new account on VV", "Appsettings phoneextn value is not visible by default in the phoneextn field ",1);
            }
            String emailui = Globals.driver.findElement(LocatorAccess.getLocator("staffing_email_txt")).getAttribute("value");
            String email = "TheAdvocates@verifiedvolunteers.com";
            if(emailui.equals(email)){
            	sc.STAF_ReportEvent("Pass", "To validate email field on Staffing and Commissions for new account on VV", "Appsettings email value is visible by default in the email field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate email field on Staffing and Commissions for new account on VV", "Appsettings email value is not visible by default in the email field ",1);
            }
			sc.clickWhenElementIsClickable("staffing_create_btn", timeOutInSeconds);
            /*
			expectdText = "Please review the information that you entered: PrimaryCSE";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate mandatory fields on Staffing and Commissions for new account on VV", "Warning message displayed as expected - "+expectdText,1);

			}else{	
				sc.STAF_ReportEvent("Fail", "To validate mandatory fields on Staffing and Commissions for new account on VV", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
			}*/

			retval = Globals.KEYWORD_PASS;

		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInCompanyInformation | Unable to create New Account in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to set data in Staffings Tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setStaffing() throws Exception{

		APP_LOGGER.startFunction("setStaffing");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("staffing_primaryAM_dd", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-setStaffing - Page has not been loaded for Staffing and Commissions");
				return retval;
			}


			//staff linking
			tempRetval = sc.selectValue_byVisibleText("staffing_primaryAM_dd","teststaff");
			tempRetval = sc.selectValue_byVisibleText("staffing_secondaryAM_dd","teststaff");
			tempRetval = sc.selectValue_byVisibleText("staffing_primaryCSE_dd","teststaff");
			tempRetval = sc.selectValue_byVisibleText("staffing_secondaryCSE_dd","teststaff");

			//client support information
			tempRetval = sc.setValueJsChange("staffing_phone_txt", "9812345678");
			tempRetval = sc.setValueJsChange("staffing_extension_txt", "8819");
			tempRetval = sc.setValueJsChange("staffing_email_txt", Globals.fromEmailID);
			tempRetval = sc.selectValue_byVisibleText("staffing_startTime_dd","08:00 AM");
			tempRetval = sc.selectValue_byVisibleText("staffing_endTime_dd","09:00 PM");
			tempRetval = sc.selectValue_byVisibleText("staffing_timeZone_dd","EST");

			//internal commission
			tempRetval = sc.selectValue_byVisibleText("staffing_salesRep_dd","teststaff");
			tempRetval = sc.setValueJsChange("staffing_salesPercent_txt", "100");


			//external commission
			//		tempRetval = sc.selectValue_byVisibleText("staffing_partner_dd","1013 - Dipak-QA");
			//		tempRetval = sc.selectValue_byVisibleText("staffing_reseller_dd","1013 - Dipak-QA");
			//tempRetval = sc.selectValue_byIndex("staffing_partner_dd",1);
			//tempRetval = sc.setValueJsChange("staffing_newpartner_txt", "Test");
		//	sc.clickWhenElementIsClickable("staffing_newpartner_btn", timeOutInSeconds);
			
			tempRetval = sc.selectValue_byIndex("staffing_reseller_dd",1);
			//tempRetval = sc.checkCheckBox("staffing_partnerCommisiionApproved_chk");
			tempRetval = sc.checkCheckBox("staffing_resellerCommisiionApproved_chk");
			tempRetval = sc.selectValue_byIndex("staffing_salesQuota_dd", 1);

			sc.clickWhenElementIsClickable("staffing_create_btn", timeOutInSeconds);

			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("staffing_edit_btn", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To setup Staffing and Commissions for new account on VV", "Staffing created successfully.",1);
				retval = Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "To setup Staffing and Commissions for new account on VV", "Unable to create new staffing ", 1);
			}
		}
		catch (Exception e) {
			log.error("Method-verifyCompanyInformation | Unable to create New Account in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to create a new user for a client 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String newUserCreation(String fieldValidationReq,String userType) throws Exception{

		APP_LOGGER.startFunction("verifyStaffing");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {

			navigateToUsersTab();

			//Need to check whether user already exists
			WebElement userGrid;
			int rowCount;
			String expectdedUserName="";

			//User Credentials
			String dynamicUsernameFlag = Globals.testSuiteXLS.getCellData_fromTestData("user_runtimeUpdateFlag");

			if(dynamicUsernameFlag.equalsIgnoreCase("Yes") && (userType.equalsIgnoreCase("normal") || userType.equalsIgnoreCase("apiUser")) ){
				int length = 10;

				expectdedUserName= "User" + sc.runtimeGeneratedStringValue(length);
				if(userType.equalsIgnoreCase("normal")){
					Globals.testSuiteXLS.setCellData_inTestData("OrgUserName", expectdedUserName);	
				}else{
					Globals.testSuiteXLS.setCellData_inTestData("APIUserName", expectdedUserName);
				}
				
				log.debug(" Method-createNewUser | User Name Runtime generated and store.value- "+expectdedUserName);
			}else if(userType.equalsIgnoreCase("custom")){
				expectdedUserName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName") + "_CustomUser";
			}else{
				expectdedUserName = Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
			}

			if(dynamicUsernameFlag.equalsIgnoreCase("Yes") && userType.equalsIgnoreCase("apiUser")){
				int length = 10;

				expectdedUserName= "User" + sc.runtimeGeneratedStringValue(length);
				Globals.testSuiteXLS.setCellData_inTestData("APIUserName", expectdedUserName);
			}
			
			
			WebElement searchUser = sc.createWebElement("users_searchUser_txt");
			tempRetval =  sc.setValueJsChange("users_searchUser_txt", expectdedUserName);
			searchUser.sendKeys(Keys.ENTER);
			Thread.sleep(1000);

			userGrid = Globals.driver.findElement(LocatorAccess.getLocator("users_userGrid_tbl"));
			rowCount=sc.getRowCount_tbl(userGrid);
			String userFound = Globals.KEYWORD_FAIL;

			if(rowCount != Globals.INT_FAIL && rowCount > 1 ){
				int userCount = rowCount - 1;
				log.info("Method - newUserCreation | User result grid displayed with " + userCount + " users(s)" );

				String userNameInUI;

				int i;
				for(i= 1;i<rowCount;i++){
					//TODO - need to fetch the column position dynamically.
					userNameInUI = sc.getCellData_tbl(userGrid, i, 5);
					if(userNameInUI.equalsIgnoreCase(expectdedUserName)){
						userFound = Globals.KEYWORD_PASS;
						break;
					}
				}
			}

			if(userFound.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Fail", "To setup new User on VV", "User already exists.User Name - "+expectdedUserName, 1);
				return Globals.KEYWORD_FAIL;
			}else{
				sc.STAF_ReportEvent("Pass", "To setup new User on VV", "User doesnt exists in this Account.User Name - "+expectdedUserName, 1);

			}



			sc.clickWhenElementIsClickable("users_newUsers_link", timeOutInSeconds);
			tempRetval =  sc.waitforElementToDisplay("newUser_title_txt", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-newUserCreation - Page has not been loaded for new user creation");
				return retval;
			}

			if(fieldValidationReq.equalsIgnoreCase("Yes")){

				tempRetval = verifyFieldsInUsersTab();
			}

			tempRetval = createNewUser(userType);
			retval = tempRetval;
		}
		catch (Exception e) {
			log.error("Method-newUserCreation | Unable to associate Staffing and Commisions in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify fields in Users tab 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInUsersTab() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInUsersTab");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("newUser_title_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInUsersTab - Page has not been loaded for new user creation ");
				return retval;
			}
			 verifyUserNameFieldUsersTab("       g","negative");//validation for spaces
			 verifyUserNameFieldUsersTab("Df1.5@3","negative");//validation for value less than 8
			 verifyUserNameFieldUsersTab(RandomStringUtils.randomAlphabetic(51),"negative");//validation for value greater than 51
			 verifyUserNameFieldUsersTab("J!@#$%^&*()D&f-1.5@3","negative");//validation for value having special characters
			 verifyUserNameFieldUsersTab("Df1.5@3J","positive");//validation for correct value
			 
			 WebElement username=Globals.driver.findElement(LocatorAccess.getLocator("newUser_username_txt"));
			 username.clear();
			 
			sc.clickWhenElementIsClickable("newUser_create_btn", timeOutInSeconds);

			expectdText = "Please review the information that you entered: UserName, EmailAddress, Title, FirstName, LastName";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate mandatory fields on Users tab for new account on VV", "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate mandatory fields on Users tab for new account on VV", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
			}


			retval=Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInUsersTab | Unable to create New Account in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to create a new user for client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String createNewUser(String userPermissionArg) throws Exception{

		APP_LOGGER.startFunction("createNewUser");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("newUser_title_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-setStaffing - Page has not been loaded for User Creation");
				return retval;
			}
			//check whether the user name is unique or not.Proceed if its unique else fail.
			//User Credentials
			String userName=null;
			if(userPermissionArg.equalsIgnoreCase("normal")){
				userName = Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
			}else if (userPermissionArg.equalsIgnoreCase("apiUser")){
				userName = Globals.testSuiteXLS.getCellData_fromTestData("APIUserName");
			}
			
			if(userPermissionArg.equalsIgnoreCase("Custom")){
				userName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName") + "_CustomUser";
				tempRetval = sc.setValueJsChange("newUser_username_txt", userName);					
			}else{
				tempRetval = sc.setValueJsChange("newUser_username_txt", userName);
			}
			//first time click doesnt actually click
			sc.clickWhenElementIsClickable("newUser_checkUsername_btn", timeOutInSeconds);

			tempRetval = sc.waitforElementToDisplay("newUser_checkUsername_btn", 3);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.clickWhenElementIsClickable("newUser_checkUsername_btn", timeOutInSeconds);
			}

			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("newUser_checkUsernameSuccess_btn", 20);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "User Creation", "UserName provided is unique- "+userName, 1);
			}else{
				sc.STAF_ReportEvent("Fail", "User Creation", "UserName provided is NOT unique- "+userName, 1);
				return Globals.KEYWORD_FAIL;
			}


			//Individual Information
			tempRetval = sc.setValueJsChange("newUser_title_txt", "title");
			tempRetval = sc.selectValue_byVisibleText("newUser_salutation_dd","Mr.");
			tempRetval = sc.setValueJsChange("newUser_fname_txt", Globals.testSuiteXLS.getCellData_fromTestData("User_Fname"));
			tempRetval = sc.setValueJsChange("newUser_lName_txt", Globals.testSuiteXLS.getCellData_fromTestData("User_Lname"));
			//tempRetval = sc.selectValue_byVisibleText("newUser_status_dd","Active");
			tempRetval = sc.setValueJsChange("newUser_phoneNumber_txt", "9812345678");
			tempRetval = sc.setValueJsChange("newUser_extention_txt", "8819");
			//			replaced email address from test data
			//			tempRetval = sc.setValueJsChange("newUser_emailAddress_txt", Globals.testSuiteXLS.getCellData_fromTestData("User_EmailID"));
			if(userPermissionArg.equalsIgnoreCase("Custom")){
				tempRetval =  sc.setValueJsChange("newUser_emailAddress_txt", userName+"@abc.com" );	
				
			}else{
			tempRetval = sc.setValueJsChange("newUser_emailAddress_txt", Globals.fromEmailID);
			}
			String addressLine ="test address Line";
			String city="Putnam";
			String zipcode1="32007";
			//Anand 2/15-removing address line & city columns from test data
            
            String addressLineui = Globals.driver.findElement(LocatorAccess.getLocator("newUser_addressLine1_txt")).getAttribute("value");
            if(addressLineui.equals(addressLine)){
            	sc.STAF_ReportEvent("Pass", "To validate addressLine field on newUser page for new account on VV", "addressLine value from client main page is visible by default in the field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate addressLine field on newUser page  for new account on VV", "addressLine value from client main page is not visible by default in the field  ",1);
            }
			//tempRetval = sc.setValueJsChange("newUser_addressLine1_txt", addressLine);
            String addressLine2ui = Globals.driver.findElement(LocatorAccess.getLocator("newUser_addressLine2_txt")).getAttribute("value");
            if(addressLine2ui.equals(addressLine)){
            	sc.STAF_ReportEvent("Pass", "To validate addressLine2 field on newUser page for new account on VV", "addressLine2 value from client main page is visible by default in the field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate addressLine2 field on newUser page  for new account on VV", "addressLine2 value from client main page is not visible by default in the field  ",1);
            }
			//tempRetval = sc.setValueJsChange("newUser_addressLine2_txt", addressLine);
            String zipcodeui = Globals.driver.findElement(LocatorAccess.getLocator("newUser_zipcode_txt")).getAttribute("value");
            if(zipcodeui.equals(zipcode1)){
            	sc.STAF_ReportEvent("Pass", "To validate zipcode field on newUser page for new account on VV", "zipcode value from client main page is visible by default in the field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate zipcode field on newUser page  for new account on VV", "zipcode value from client main page is not visible by default in the field  ",1);
            }
			tempRetval = sc.setValueJsChange("newUser_zipcode_txt", "67004");
			String cityui = Globals.driver.findElement(LocatorAccess.getLocator("newUser_city_txt")).getAttribute("value");
            if(cityui.equals(city)){
            	sc.STAF_ReportEvent("Pass", "To validate city field on newUser page for new account on VV", "city value from client main page is visible by default in the field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate city field on newUser page  for new account on VV", "city value from client main page is not visible by default in the field  ",1);
            }
			//tempRetval = sc.setValueJsChange("newUser_city_txt", city);
            Select select = new Select(sc.createWebElement("newUser_state_dd"));
			String Stateui =  select.getFirstSelectedOption().getText();
			String State = Globals.testSuiteXLS.getCellData_fromTestData("Account_State");
            if(Stateui.equalsIgnoreCase(State)){
            	sc.STAF_ReportEvent("Pass", "To validate State field on newUser page for new account on VV", "State field value is visible by default " +Stateui,1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate State field on newUser page for new account on VV", "State field value is not visible by default" +Stateui,1);
            }
			//tempRetval = sc.selectValue_byVisibleText("newUser_state_dd","Florida");
            
            //Validate Generate Random Password 
            sc.clickWhenElementIsClickable("newUser_generatePassword_btn", timeOutInSeconds);
            String randomPwd=Globals.driver.findElement(LocatorAccess.getLocator("newUser_password_txt")).getAttribute("value");
            String tempPwd= "Temp123!";
            if(randomPwd.equalsIgnoreCase(tempPwd))
            {
            	sc.STAF_ReportEvent("Fail", "To validate Random Password generation on Users tab for new account on VV", "Random password is generated - "+randomPwd, 1);
			}
            else
            {	
				sc.STAF_ReportEvent("Pass", "To validate Random Password generation on Users tab for new account on VV", "Random password is not generated - "+randomPwd, 1);
				if(!userPermissionArg.equalsIgnoreCase("Normal")){
					Globals.driver.findElement(LocatorAccess.getLocator("newUser_password_txt")).clear();	
				}
			}
            if(userPermissionArg.equalsIgnoreCase("Normal")){
            	Globals.testSuiteXLS.setCellData_inTestData("OrgUserPwd", randomPwd);
            }
           

			String userPwd = Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
			if(!userPermissionArg.equalsIgnoreCase("Normal")){
				tempRetval = sc.setValueJsChange("newUser_password_txt",userPwd );
				tempRetval = sc.setValueJsChange("newUser_confirmPassword_txt", userPwd);
			}
			sc.waitforElementToDisplay("newUser_changePwdAtLogin_chk", timeOutInSeconds); 
			tempRetval=sc.isSelected("newUser_changePwdAtLogin_chk");
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				sc.STAF_ReportEvent("Pass","To validate Change password checkbox on newUser page for new account on VV","Change password checkbox by default as Checked", 1);
				//sc.uncheckCheckBox("newUser_changePwdAtLogin_chk");

			} else {
				sc.STAF_ReportEvent("Fail","To validate Change password checkbox on newUser page for new account on VV","Change password checkbox is not as marked as Checked", 1);

			}
			//User Permission
			String userPermission;
			userPermission = Globals.testSuiteXLS.getCellData_fromTestData("User_Permission");
			if(userPermissionArg.equalsIgnoreCase("Custom")){
				sc.selectValue_byVisibleText("newUser_userPermissiongroup_dd", "Customized User ");
				sc.checkCheckBox("newUser_viewBGResults_chk");				
				
			}else{
				if(userPermission.equalsIgnoreCase("Super User")){

					Globals.driver.findElements(LocatorAccess.getLocator("newUser_userPermission_rdb")).get(0).click();

				}
				else if (userPermission.equalsIgnoreCase("Integration User")){
					Globals.driver.findElements(LocatorAccess.getLocator("newUser_userPermission_rdb")).get(1).click();
				}
			}
			//TODO need to add different roles for a standard user when test scenarios for the same is added.

			sc.clickWhenElementIsClickable("newUser_create_btn", timeOutInSeconds);
            
            String expectdText = "Please review the information that you entered: ZipCode";
            sc.waitforElementToDisplay("newAccount_validationMsg_span", timeOutInSeconds);
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate ZipCode field on Users tab for new account on VV", "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate ZipCode field on Users tab for new account on VV", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
			}
			WebElement zipcode=Globals.driver.findElement(LocatorAccess.getLocator("newUser_zipcode_txt"));
			zipcode.clear();
			tempRetval = sc.setValueJsChange("newUser_zipcode_txt", "32007");
			
			// client views section - validate feature is turn off message
			String clientViewLable=Globals.driver.findElement(By.xpath("//div[@class='areaPadTop05 areaPadLeft10 areaPadBottom05']/span")).getText().trim();
			if(clientViewLable.equalsIgnoreCase("Feature is turned off.")||clientViewLable.equalsIgnoreCase("Views are not available in a hierarchy.")){
				sc.STAF_ReportEvent("Pass", "To validate Client Views on Users tab for new account on VV", "Getting expected message when client views feature is turn off",1);
			}else{
				sc.STAF_ReportEvent("Fail", "To validate Client Views on Users tab for new account on VV", "Not Getting expected message when client views feature is turn off",1);
			}
			sc.waitforElementToDisplay("newUser_create_btn", timeOutInSeconds);
			sc.clickWhenElementIsClickable("newUser_create_btn", timeOutInSeconds);
			
			tempRetval = sc.waitforElementToDisplay("users_userGrid_tbl", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To setup new User on VV", "New User successfully created successfully -"+userName,1);
				retval=Globals.KEYWORD_PASS;
				
			}else{
				sc.STAF_ReportEvent("Fail", "To setup new User on VV", "Unable to create new user - "+userName, 1);
			}
			
			/*
			tempRetval = sc.waitforElementToDisplay("newAccount_validationMsg_span", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Fail", "To setup new User on VV", "Unable to create new user - "+userName, 1);
			}else{
				sc.STAF_ReportEvent("Pass", "To setup new User on VV", "New User successfully created successfully -"+userName,1);
				retval=Globals.KEYWORD_PASS;

			}
			*/
		}
		catch (Exception e) {
			log.error("Method-createNewUser | Unable to create New User in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to setup pricing-wrapper method
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String newPricingSetup(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("newPricingSetup");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {
			tempRetval =  sc.waitforElementToDisplay("manageAccount_pricing_link", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-newPricingSetup - Page has not been loaded for pricing setup");
				return retval;
			}

			sc.clickWhenElementIsClickable("manageAccount_pricing_link", timeOutInSeconds);
			tempRetval =  sc.waitforElementToDisplay("pricing_addPricing_link", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-newPricingSetup - Page has not been loaded for pricing setup");
				return retval;
			}


			if(fieldValidationReq.equalsIgnoreCase("Yes")){

				tempRetval = verifyFieldsInPricing();
			}

			tempRetval = setupPricing();
			retval = tempRetval;
		}
		catch (Exception e) {
			log.error("Method-newPricingSetup | Unable to setup Pricing in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify feilds in Pricing tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInPricing() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInPricing");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			sc.clickWhenElementIsClickable("pricing_addPricing_link", timeOutInSeconds);
			tempRetval =  sc.waitforElementToDisplay("newPricing_product_dd", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyStaffing - Page has not been loaded for new pricing setup");
				return retval;
			}

			sc.clickWhenElementIsClickable("newPricing_save_btn", timeOutInSeconds);

			expectdText = "Please review the information that you entered: npnProductId";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate mandatory fields on Pricing tab for new account on VV", "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate mandatory fields on Pricing tab for new account on VV", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
			}

			sc.clickWhenElementIsClickable("newPricing_cancel_btn", timeOutInSeconds);



			//to verify deletion of price
			setupPriceForProduct(VV_Products.L1);

			int rowCountAfterDeletion;
			//TODO need to replace below by getChild items
			String priceGridLocator = "//div[@id='productPricePanel']/*/*/table";
			WebElement priceGrid = Globals.driver.findElement(By.xpath(priceGridLocator));
			WebElement delete =null;
			int rowCountBeforeDeletion=0;
			rowCountBeforeDeletion = sc.getRowCount_tbl(priceGrid);

			//check whether product has bee added. check for String "No matching records."
			String cellData = sc.getCellData_tbl(priceGrid, 1, 1);
			if (rowCountBeforeDeletion == 1 && cellData.contains("No matching records")){
				sc.STAF_ReportEvent("Fail","To Delete Pricing for new account on VV" , "Unable to add Level1 and No Matching Records has been found", 1);
				return Globals.KEYWORD_FAIL;
			}

			String productName;
			if (rowCountBeforeDeletion >= 1){
				for (int i=1;i<=rowCountBeforeDeletion;i++){
					productName = sc.getCellData_tbl(priceGrid, i, 2);
					productName = productName.toLowerCase().trim();

					if(productName.contains("level 1")){
						String deleteImgLocator = priceGridLocator+"/tbody/tr["+i+"]/td[8]/a/img";
						delete = Globals.driver.findElement(By.xpath(deleteImgLocator));
						delete.click();

						sc.waitforElementToDisplay("pricing_deletionConfirm_btn", 20);
						sc.clickWhenElementIsClickable("pricing_deletionConfirm_btn", 5);
						break;
					}
				}

				sc.clickWhenElementIsClickable("manageAccount_pricing_link", timeOutInSeconds);
				sc.waitforElementToDisplay("pricing_addPricing_link", timeOutInSeconds);

				priceGrid = Globals.driver.findElement(By.xpath(priceGridLocator));
				rowCountAfterDeletion = sc.getRowCount_tbl(priceGrid);

				cellData = sc.getCellData_tbl(priceGrid, 1, 1);
				if (rowCountAfterDeletion == 1 && cellData.contains("No matching records")){
					rowCountAfterDeletion = 0;
				}



				if(rowCountBeforeDeletion-rowCountAfterDeletion ==1){
					sc.STAF_ReportEvent("Pass", "To Delete Pricing for new account on VV", "Level1 product price successfully deleted ",1);
				}else{	
					sc.STAF_ReportEvent("Fail", "To Delete Pricing for new account on VV", "Unable to delete Level1 product price",1);
				}

			}else{
				sc.STAF_ReportEvent("Fail", "To Delete Pricing for new account on VV", "Unable to delete Level1 product price as price was not added",1);
			}



			retval=Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInPricing | Unable to create New Account in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to set up new pricing for products
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setupPricing() throws Exception{
		APP_LOGGER.startFunction("setupPricing");
		String retval = Globals.KEYWORD_FAIL;


		try {

			setupPriceForProduct(VV_Products.L1);
			setupPriceForProduct(VV_Products.L2);
			setupPriceForProduct(VV_Products.L3);
			setupPriceForProduct(VV_Products.ABUSE);
			setupPriceForProduct(VV_Products.CREDIT);
			setupPriceForProduct(VV_Products.MVR);
			setupPriceForProduct(VV_Products.REF);
			setupPriceForProduct(VV_Products.SSNPROF);
			setupPriceForProduct(VV_Products.OIG);
			setupPriceForProduct(VV_Products.MAR);
			setupPriceForProduct(VV_Products.SELFADVERSE);
			setupPriceForProduct(VV_Products.ID);	
			setupPriceForProduct(VV_Products.LS);
			setupPriceForProduct(VV_Products.FEDCIVIL);
			setupPriceForProduct(VV_Products.FEDCRIM);
			setupPriceForProduct(VV_Products.COUNTYCIVIL);
			retval =Globals.KEYWORD_PASS;

		}catch (Exception e) {
			log.error("Method-setupPricing | Unable to setup Pricing in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to setup price for individual products
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setupPriceForProduct(VV_Products product) throws Exception{
		APP_LOGGER.startFunction("setupPriceForProduct");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try{



			String testDataColumnName =null;
			String productNameUI=null;

			switch (product) {

			case L1:
				testDataColumnName ="CustomPrice_L1";
				//				productNameUI = "Level 1: Basic Criminal History Record Locator Search";
				break;
			case L2:
				testDataColumnName ="CustomPrice_L2";
				//				productNameUI = "Level 2: Advanced Criminal History Record Locator Search";
				break;	
			case L3:
				testDataColumnName ="CustomPrice_L3";
				//				productNameUI = "Level 3: Complete Criminal History Record Locator Search";
				break;
			case CREDIT:
				testDataColumnName ="CustomPrice_Credit";
				//				productNameUI = "Consumer Credit Check";
				break;
			case MVR:
				testDataColumnName ="CustomPrice_MVR";
				//				productNameUI = "Motor Vehicle Record Check";
				break;

			case REF:
				testDataColumnName ="CustomPrice_Reference";
				//				productNameUI = "Reference Interview";
				break;

			case OIG:
				testDataColumnName ="CustomPrice_OIG";
				//				productNameUI = "OIG-GSA Excluded Parties";
				break;

			case SSNPROF:
				testDataColumnName ="CustomPrice_SSNProfile";
				//				productNameUI = "SSN Profile";
				break;

			case MAR:
				testDataColumnName ="CustomPrice_ManagedAdverse";
				//				productNameUI = "Managed Adverse Action";
				break;

			case SELFADVERSE:
				testDataColumnName ="CustomPrice_SelfServeAdverse";
				//				productNameUI = "Self-Serve Adverse Action Kit";
				break;

			case ABUSE:
				testDataColumnName ="CustomPrice_Abuse";
				//				productNameUI = "Neglect/Abuse Registry";
				break;
			case ID:
				testDataColumnName ="CustomPrice_ID";
				break;
			case LS:
				testDataColumnName ="CustomPrice_LS";
				break;
			case FEDCRIM:
				testDataColumnName ="CustomPrice_FedCrim";
				break;
			case FEDCIVIL:
				testDataColumnName ="CustomPrice_FedCivil";
				break;
			case COUNTYCIVIL:
				testDataColumnName ="CustomPrice_CountyCivil";
				break;
			default:
				break;
			}

			productNameUI = product.ProductName;
			String price=null;
			price = Globals.testSuiteXLS.getCellData_fromTestData(testDataColumnName);

			if(price == null || price.isEmpty()){
				log.debug("Method-setupPriceForProduct - No Price to be setup for Product - "+product);
				return Globals.KEYWORD_FAIL;
			}

			sc.clickWhenElementIsClickable("manageAccount_pricing_link", timeOutInSeconds);
			tempRetval =  sc.waitforElementToDisplay("pricing_addPricing_link", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyStaffing - Page has not been loaded for pricing setup");
				return retval;
			}

			sc.clickWhenElementIsClickable("pricing_addPricing_link", timeOutInSeconds);
			tempRetval =  sc.waitforElementToDisplay("newPricing_product_dd", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-setupPricing - Page has not been loaded for new pricing setup");
				return retval;
			}


			sc.selectValue_byVisibleText("newPricing_product_dd", productNameUI);
			sc.setValueJsChange("newPricing_amount_txt", price);

			sc.clickWhenElementIsClickable("newPricing_save_btn", timeOutInSeconds);

			tempRetval =  sc.waitforElementToDisplay("pricing_addPricing_link", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To setup Pricing for new account on VV", "Pricing setup for - " + product + " Value - "+price, 1);
				retval = Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "To setup Pricing for new account on VV", "Unable to set up price for - " + product + " Value - "+price, 1);

			}

		}catch (Exception e) {
			log.error("Method-setupPricing | Unable to setup Pricing in Inhouse Portal for Prodcut -"+product + " | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify authorization tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String newAuthorizationSetup() throws Exception{
		APP_LOGGER.startFunction("newAuthorizationSetup");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try{


			tempRetval =  sc.waitforElementToDisplay("manageAccount_authorization_link", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-newAuthorizationSetup - Page has not been loaded for Authorization setup");
				return retval;
			}

			sc.clickWhenElementIsClickable("manageAccount_authorization_link", timeOutInSeconds);
			tempRetval =  sc.waitforElementToDisplay("authorization_productsGrid_tbl", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-newAuthorizationSetup - Page has not been loaded for new Authorization setup");
				return retval;
			}

			int rowCount = 0;
			String productGridLocator = "//div[@id='mainTable']/table[@class='results selectClick']";
			WebElement productGrid =Globals.driver.findElement(By.xpath(productGridLocator));
			rowCount=sc.getRowCount_tbl(productGrid);

			int i;
			String productName;
			String enabled;
			String jurisdiction;

			for(i=1;i<=rowCount;i++){
				productGrid = Globals.driver.findElement(By.xpath(productGridLocator));
				productName=sc.getCellData_tbl(productGrid, i, 1);
				jurisdiction=sc.getCellData_tbl(productGrid, i, 2);
				enabled=sc.getCellData_tbl(productGrid, i, 3);

				if(jurisdiction == null || jurisdiction.isEmpty()){
					jurisdiction = "";
				}

				if(enabled.equalsIgnoreCase("false")){
					Globals.driver.findElement(By.xpath(productGridLocator+"/tbody/tr["+i+"]")).click();
					tempRetval =  sc.waitforElementToDisplay("authorization_activationKey_txt", timeOutInSeconds);

					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						log.debug("Method-newAuthorizationSetup - Page has not been loaded for new Authorization setup");
						sc.STAF_ReportEvent("Fail", "Product Authorization", "Product - "+productName + " "+jurisdiction + "cannot be activated as page didnt load", 1);
						sc.clickWhenElementIsClickable("authorization_cancel_btn", timeOutInSeconds);
						continue;
					}

					sc.setValueJsChange("authorization_activationKey_txt", "VV1234");
					sc.setValueJsChange("authorization_activationNotes_txt", "Product activated for Automation Execution");
					sc.clickWhenElementIsClickable("authorization_save_btn", timeOutInSeconds);

					tempRetval =  sc.waitforElementToDisplay("authorization_productsGrid_tbl", timeOutInSeconds);

					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						productGrid = Globals.driver.findElement(By.xpath(productGridLocator));
						enabled=sc.getCellData_tbl(productGrid, i, 3);
						if(enabled.equalsIgnoreCase("true")){
							sc.STAF_ReportEvent("Pass", "Product Authorization", "Product - "+productName + " "+jurisdiction + "is enabled", 1);

						}else{
							sc.STAF_ReportEvent("Fail", "Product Authorization", "Product - "+productName + " "+jurisdiction + "is NOT enabled", 1);
						}


					}


				}else{
					sc.STAF_ReportEvent("Pass", "Product Authorization", "Product - "+productName + " "+jurisdiction + "is already enabled", 1);

				}
			}

			retval = Globals.KEYWORD_PASS;

		}catch (Exception e) {
			log.error("Method-newAuthorizationSetup | Unable to setup Authorization in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	//----Billing setup
	/**************************************************************************************************
	 * Method to verify Billing Setup tab 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String newBillingSetup(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("newBillingSetup");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {


			tempRetval =  sc.waitforElementToDisplay("manageAccount_biling_link", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-newBillingSetup - Page has not been loaded for Billing Setup");
				return retval;
			}
			sc.clickWhenElementIsClickable("manageAccount_biling_link", timeOutInSeconds);

			tempRetval =  sc.waitforElementToDisplay("billing_attn_txt", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-newBillingSetup - Page has not been loaded for Billing Setup");
				return retval;
			}

			if(fieldValidationReq.equalsIgnoreCase("Yes")){

				tempRetval = verifyFieldsInBillingTab();
			}

			tempRetval = createNewBilling();
			retval = tempRetval;
		}
		catch (Exception e) {
			log.error("Method-newBillingSetup | Unable to setup Billing in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify fields in Billing Tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInBillingTab() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInBillingTab");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("billing_attn_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInBillingTab - Page has not been loaded for billing setup ");
				return retval;
			}


			sc.clickWhenElementIsClickable("billing_save_btn", timeOutInSeconds);

			expectdText = "Please review the information that you entered: BillingAttn, SolomonBillingId";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate mandatory fields on Billing tab for new account on VV", "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate mandatory fields on Billing tab for new account on VV", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
			}


			retval=Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInBillingTab | Unable to validated fields in Billing Tab in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to create a new billing for client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String createNewBilling() throws Exception{

		APP_LOGGER.startFunction("createNewBilling");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("billing_attn_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-createNewBilling - Page has not been loaded for Billing Setup");
				return retval;
			}
			String addressLine ="test address Line";
			String city="Putnam";
			String zipcode1 ="32007";
			//Anand 2/15-removing address line & city columns from test data


			//Billing Information
			tempRetval = sc.setValueJsChange("billing_attn_txt", "12345");
			String addressLineui = Globals.driver.findElement(LocatorAccess.getLocator("billing_address1_txt")).getAttribute("value");
            if(addressLineui.equals(addressLine)){
            	sc.STAF_ReportEvent("Pass", "To validate addressLine field on Billing page for new account on VV", "addressLine value from client main page is visible by default in the field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate addressLine field on Billing page  for new account on VV", "addressLine value from client main page is not visible by default in the field  ",1);
            }
			//tempRetval = sc.setValueJsChange("billing_address1_txt", addressLine);
			String addressLine2ui = Globals.driver.findElement(LocatorAccess.getLocator("billing_address2_txt")).getAttribute("value");
            if(addressLine2ui.equals(addressLine)){
            	sc.STAF_ReportEvent("Pass", "To validate addressLine2 field on Billing page for new account on VV", "addressLine2 value from client main page is visible by default in the field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate addressLine2 field on Billing page  for new account on VV", "addressLine2 value from client main page is not visible by default in the field  ",1);
            }
			//tempRetval = sc.setValueJsChange("billing_address2_txt", addressLine);
            String cityui = Globals.driver.findElement(LocatorAccess.getLocator("billing_city_txt")).getAttribute("value");
            if(cityui.equals(city)){
            	sc.STAF_ReportEvent("Pass", "To validate city field on Billing page for new account on VV", "city value from client main page is visible by default in the field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate city field on Billing page  for new account on VV", "city value from client main page is not visible by default in the field  ",1);
            }
			//tempRetval = sc.setValueJsChange("billing_city_txt", city);
            Select select = new Select(sc.createWebElement("billing_state_dd"));
			String BillingStateui =  select.getFirstSelectedOption().getText();
			String BillingState = Globals.testSuiteXLS.getCellData_fromTestData("Account_State");
            if(BillingStateui.equalsIgnoreCase(BillingState)){
            	sc.STAF_ReportEvent("Pass", "To validate BillingState field on billing page for new account on VV", "BillingState field value is visible by default " +BillingStateui,1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate BillingState field on billing page for new account on VV", "BillingState field value is not visible by default" +BillingStateui,1);
            }
			//tempRetval = sc.selectValue_byVisibleText("billing_state_dd",Globals.testSuiteXLS.getCellData_fromTestData("Account_State"));
            String emailui = Globals.driver.findElement(LocatorAccess.getLocator("billing_emailAddress_txt")).getAttribute("value");
            if(emailui.equals(Globals.fromEmailID)){
            	sc.STAF_ReportEvent("Pass", "To validate email field on Billing page for new account on VV", "email value from client main page is visible by default in the field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate email field on Billing page  for new account on VV", "email value from client main page is not visible by default in the field  ",1);
            }
			//tempRetval = sc.setValueJsChange("billing_emailAddress_txt", Globals.fromEmailID);
            String zipcodeui = Globals.driver.findElement(LocatorAccess.getLocator("billing_zipcode_txt")).getAttribute("value");
            if(zipcodeui.equals(zipcode1)){
            	sc.STAF_ReportEvent("Pass", "To validate zipcode field on Billing page for new account on VV", "zipcode value from client main page is visible by default in the field ",1);	
            }
            else{
            	sc.STAF_ReportEvent("Fail", "To validate zipcode field on Billing page  for new account on VV", "zipcode value from client main page is not visible by default in the field  ",1);
            }
			tempRetval = sc.setValueJsChange("billing_zipcode_txt", "67004");


			//Solomon and Invoice Info

			tempRetval = sc.setValueJsChange("billing_solomonID_txt", "VV1234");

			//tax information
			//TODO-need to handle this section whenever required

			sc.clickWhenElementIsClickable("billing_save_btn", timeOutInSeconds);
            
            String expectdText = "Please review the information that you entered: ZipCode";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
            
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate the ZipCode Field on Billing page", "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate the ZipCode Field on Billing page", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
			}
			WebElement zipcode=Globals.driver.findElement(LocatorAccess.getLocator("billing_zipcode_txt"));
			zipcode.clear();
			tempRetval = sc.setValueJsChange("billing_zipcode_txt", Globals.testSuiteXLS.getCellData_fromTestData("Account_ZipCode"));
			
			sc.clickWhenElementIsClickable("billing_save_btn", timeOutInSeconds);
			
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("associatedAccounts_edit_btn", 20);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To setup Billing for new account on VV", "Billing setup configured successfully ",1);
				retval=Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "To setup Billing for new account on VV", "Unable to setup Billing", 1);
			}
		}
		catch (Exception e) {
			log.error("Method-createNewBilling | Unable to setup for Billing in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to activate a new account
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String activateAccount() throws Exception{

		APP_LOGGER.startFunction("activateAccount");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {

			tempRetval = sc.waitforElementToDisplay("manageAccount_main_link", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-activateAccount - Page has not been loaded for Accounts Main tab");
				return retval;
			}

			sc.clickWhenElementIsClickable("manageAccount_main_link", timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("associatedAccounts_edit_btn", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-activateAccount - Page has not been loaded for Edit - Accounts Main tab");
				return retval;
			}

			sc.clickWhenElementIsClickable("associatedAccounts_edit_btn", timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("newAccStatusInfo_status_dd", timeOutInSeconds);

			tempRetval = sc.selectValue_byVisibleText("newAccStatusInfo_status_dd", "Active");
			sc.clickWhenElementIsClickable("newAccount_create_btn", timeOutInSeconds);

			//tempRetval = sc.waitforElementToDisplay("searchAccount_accountSearch_txt", timeOutInSeconds);
			//VV4.4 Change -Start - cOMMENTED THE BAOVE LINE AND ADDED THE BELOW
			tempRetval = sc.waitforElementToDisplay("associatedAccounts_edit_btn", timeOutInSeconds);
			sc.clickWhenElementIsClickable("homepage_clients_link", timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("searchAccount_accountSearch_txt", timeOutInSeconds);

			//VV4.4 Change -eND
			//verify whether account status is active

			int rowID=Globals.INT_FAIL;
			rowID = searchAccount();
			if(rowID == Globals.INT_FAIL){
				return Globals.KEYWORD_FAIL;
			}
			String accountStatus;
			WebElement  accountsTable =  Globals.driver.findElement(By.xpath("//table[@class='results selectClick']"));
			accountStatus = sc.getCellData_tbl(accountsTable, rowID, 6);


			if(accountStatus.equalsIgnoreCase("Active")){
				sc.STAF_ReportEvent("Pass", "Account Search", "Account has been found and status is Active", 1);
				Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+rowID+"]")).click();
				retval=Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "Account Search", "Account has been NOT been Activated ", 1);
				return Globals.KEYWORD_FAIL;
			}


		}catch (Exception e) {
			log.error("Method-activateAccount | Unable to make the Account as active | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify position listing tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyPositionListingsTab() throws Exception{
		APP_LOGGER.startFunction("verifyPositionListingsTab");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try {

			tempRetval = sc.waitforElementToDisplay("manageAccount_positions_link", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-activateAccount - Page has not been loaded for Accounts Main tab");
				return retval;
			}

			sc.clickWhenElementIsClickable("manageAccount_positions_link", timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("position_searchPosition_txt", timeOutInSeconds);
			tempRetval = verifyNotes();
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-activateAccount - Page has not been loaded for Position tab");
				return retval;
			}

			String positionGridLocator = "//div[@id='mainTable']//table";
			WebElement positionGrid = Globals.driver.findElement(By.xpath(positionGridLocator));
			int totalPositions=0;

			totalPositions=sc.getRowCount_tbl(positionGrid);

			if (totalPositions == Globals.INT_FAIL ||  totalPositions <3 ){
				sc.STAF_ReportEvent("Fail", "Positions Tab", "No of positions - "+totalPositions, 1);
				return Globals.KEYWORD_FAIL;
			}
			
			int columnCount  =  positionGrid.findElements(By.xpath("./thead/tr/th")).size();
			String colHeaderName;

            String[] expectedHeaders1 = {"Position Id","Position Name","Products","Min Renewal Months","Regulated Juris","HOLD Queue","Use Seamless Payment","Renewal Policy"};
			for (int i = 1; i <= columnCount ; i++){

				colHeaderName = positionGrid.findElement(By.xpath("./thead/tr/th["+i+"]")).getText();
				if(colHeaderName.equalsIgnoreCase(expectedHeaders1[i-1].trim())){
					sc.STAF_ReportEvent("Pass", " Positions Tab", "Position Tab Header matched with expected-Val-"+colHeaderName, 0);

				}else{
					sc.STAF_ReportEvent("Fail", "Positions Tab", "Position Tab Header MisMatch with expected-Exp-"+expectedHeaders1[i-1] + " Actual="+colHeaderName, 1);
				}
			}
						
			// verify badges should be displayed on position tab
			tempRetval=sc.waitTillElementDisplayed("position_clientBadge_label", timeOutInSeconds);
			List<WebElement> badgeList=driver.findElements(By.xpath("//div[@class='col-sm-12, areaMarginBottom10']/img"));
			int arrInt[]=new int[4];
			int badgesListSize=badgeList.size();
			int i=0;
			if(badgesListSize==4 && tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				for(WebElement wb: badgeList){
					String extractBadgeId=wb.getAttribute("src");
					int number=Integer.parseInt(extractBadgeId.substring(extractBadgeId.indexOf("badgeId=")).replace("badgeId=", "").trim());
					arrInt[i]=number;
					i++;
				}
			
				String badgeid=Globals.driver.findElement(By.xpath("//div[@class='adminTitle']/div[1]")).getText();
				int badgId=Integer.parseInt(badgeid.substring(badgeid.indexOf("(ID:")+4).replace(")\nNotes", "").trim());
				VVDAO vvDB             = null;
				String dbURL = Globals.getEnvPropertyValue("dbURL");
	   	 		String dbUserName = Globals.getEnvPropertyValue("dbUserName");
	   	 		String dbPassword = ProtectedEncryption.decrypt(Globals.getEnvPropertyValue("dbPassword"), CommonHelpMethods.createKey());
	   	 	
	   	 		log.info("DB URL is :"+"\t"+dbURL);
			
	   	 		vvDB      = new VVDAO(dbURL,dbUserName,dbPassword); 
	                    
	   	 		String selectSQL = "select BadgeId,BadgeName from Badge where ClientId="+badgId;
	                    
	   	 		Object[][] retrRow=vvDB.executeSelectQuery(selectSQL);
	   	 		int badgIdInt[]=new int[4];
	   	 		String accountName=Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
	   	 		String badgeName="Automation Acc-"+ accountName.substring(accountName.indexOf("Acc")+3);
	   	 		badgeName=badgeName.substring(0, 20);
	   	 		i=0;
	   	 		for(int row=0; row< retrRow.length;row++){
	        	
	        		badgIdInt[i]=(int) retrRow[row][0];
	        		String badgeNm=(String) retrRow[row][1];
	        		if(!badgeNm.contains(badgeName)){
	        			sc.STAF_ReportEvent("Fail", "Positions Tab", "Badge name is not as per expected. Expected : "+badgeNm, 1);	
	        		}
	        		i++;
	   	 		}
	        
	   	 		if(!(Arrays.equals(arrInt, badgIdInt))){
	   	 			sc.STAF_ReportEvent("Fail", "Positions Tab", "Badge ID is not as per database badges ID", 1);
	   	 		}else{
	   	 			sc.STAF_ReportEvent("Pass", "Positions Tab", "Badges are getting disaplyed successfully", 1);
	   	 		}
	        
			}else{
				sc.STAF_ReportEvent("Fail", "Positions Tab", "Badges are not getting disaplyed successfully", 1);
			}
	        
			//proceed to position listings tab
			sc.clickWhenElementIsClickable("manageAccount_positionListings_link", timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("positionListings_posListingGrid_hide_tbl", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-activateAccount - Page has not been loaded for Position Listings");
				//return retval;
			}

			WebElement positionListingGrid = sc.createWebElement("positionListings_posListingGrid_hide_tbl");
			int posListingsCount =0;
			posListingsCount = sc.getRowCount_tbl(positionListingGrid);

			posListingsCount=posListingsCount-1;//this removed the header

			//Verification of all position 
			String accType=Globals.testSuiteXLS.getCellData_fromTestData("AccountUse");
			int totalExpectedListings = totalPositions * 5;
			if(accType.equalsIgnoreCase("Employment")){
				totalExpectedListings = totalPositions;
			}
			
			int hiddenListingBtnTxt = Integer.parseInt(Globals.driver.findElement(By.xpath("//*[@id='mainTable']/div[2]/div/div/button[2]/span")).getText().trim());
			if((totalExpectedListings == posListingsCount)&&(totalExpectedListings==hiddenListingBtnTxt) ){
				sc.STAF_ReportEvent("Pass", "To Verify Position Name Column on Position Listings for new account on VV", "Position Listing Count match as Expected . Count -"+totalExpectedListings, 1);
			}else{
				sc.STAF_ReportEvent("Fail", "To Verify Position Name Column on Position Listings for new account on VV", "Mismatch in Position Listing Count.Expected Count -"+totalExpectedListings + " Actual Count = "+posListingsCount, 1);
			}
			
			//verify the all position are in hidden listing with correct no. of position
			String[] expectedHeaders = {"Name","Prod Abbrev","Promo Code","Client % Product Fee","Client % Source Fee","Reg Juris","Hide Price","Hide Donations","Hide On Invite","Hide On Client Order"};
			
			retval=verifyCountsPositionListingsTab(accType,"hiddenListings",expectedHeaders,"select");
			
			if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-activateAccount - Counting is missmatched for Position tab - hidden listing");
				return retval;
			}else{
				sc.STAF_ReportEvent("Pass", "To Verify hidden listing positions counting and checkboxes on Position Listings for new account on VV", "hidden listing positions count and checkboxes on Position Listing page as per Expected", 1);
			}

			//unchecked checkbox Hide On Invite and Hide On Client Order on hidden listing tab and click on update button			
			i=0;
           	for(i=1;i<=posListingsCount;i++){
					retval=sc.uncheckCheckBox(Globals.driver.findElement(By.xpath("//div[@class='hiddenListings']/div/table[@class='table table-striped table-hover']/tbody[2]/tr["+i+"]/td[9]/input")));
					if((posListingsCount==15) && (i==1 || i==6 || i==11) ){
					  tempRetval=sc.uncheckCheckBox(Globals.driver.findElement(By.xpath("//div[@class='hiddenListings']/div/table[@class='table table-striped table-hover']/tbody[2]/tr["+i+"]/td[10]/input")));	
					}else if((posListingsCount==3)){
					  tempRetval=sc.uncheckCheckBox(Globals.driver.findElement(By.xpath("//div[@class='hiddenListings']/div/table[@class='table table-striped table-hover']/tbody[2]/tr["+i+"]/td[10]/input")));		
					}
			}
			
           	sc.clickWhenElementIsClickable(Globals.driver.findElement(By.xpath("//div[@id='mainTable']/button[1][@class='btn submit areaMarginLeft05']")),timeOutInSeconds);
           	sc.waitForPageLoad();
           	Thread.sleep(5000);
           	int visibleListingBtnTxt=0;
           	visibleListingBtnTxt = Integer.parseInt(Globals.driver.findElement(By.xpath("//*[@id='mainTable']/div[2]/div/div/button[1]/span")).getText().trim());
       		hiddenListingBtnTxt = Integer.parseInt(Globals.driver.findElement(By.xpath("//*[@id='mainTable']/div[2]/div/div/button[2]/span")).getText().trim());
           	//match the position count using button text for hidden listing button and visible listing button then click on visible listing button
           	int flagCnt=0;
            
           	while(visibleListingBtnTxt!=posListingsCount && (hiddenListingBtnTxt!=0)){
           		Thread.sleep(3000);
           		visibleListingBtnTxt = Integer.parseInt(Globals.driver.findElement(By.xpath("//*[@id='mainTable']/div[2]/div/div/button[1]/span")).getText().trim());
           		hiddenListingBtnTxt = Integer.parseInt(Globals.driver.findElement(By.xpath("//*[@id='mainTable']/div[2]/div/div/button[2]/span")).getText().trim());
           	    if(flagCnt==5){
           	    	break;
           	    }
           	    flagCnt++;
           	}
            if(!(visibleListingBtnTxt==posListingsCount) && !(hiddenListingBtnTxt==0)){
            	sc.STAF_ReportEvent("Fail", "To Verify Visible Listings count and hidden listing count on Position Listings for new account on VV", "Missmatch Visible Listings count or hidden listing counton Position Listings. Expected Visible count -"+posListingsCount+" Actual Visible count : -"+hiddenListingBtnTxt+" Expected Hidden Count -0 Actual Hidden Count -"+hiddenListingBtnTxt, 1);
            }else{
            	sc.STAF_ReportEvent("Pass", "To Verify Visible Listings count and hidden listing count on Position Listings for new account on VV", "Visible Listings count and hidden listing count on Position Listings is matched as expceted.", 1);
            }
                    
            sc.clickWhenElementIsClickable(Globals.driver.findElement(By.xpath("//div[@id='mainTable']/div[2]/div/div/button[1]")),timeOutInSeconds);
           	
          //verify the all position are in Visible listing with correct no. of position
            String[] expectedHeadersVisible = {"Name","Prod Abbrev","Promo Code","Client % Product Fee","Client % Source Fee","Reg Juris","Hide Client Price","Hide Donations","Hide On Invite","Hide On Client Order"};
			
			retval=verifyCountsPositionListingsTab(accType,"activeListings",expectedHeadersVisible,"uncheck");
			
			if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-activateAccount - Counting is missmatched for Position tab - Visible listing");
				return retval;
			}else{
				sc.STAF_ReportEvent("Pass", "To Verify Visible listing positions counting and checkboxes on Position Listings for new account on VV", "Visible listing positions count and checkboxes on Position Listing page as per Expected", 1);
			}
			//click on update button
			sc.clickWhenElementIsClickable(Globals.driver.findElement(By.xpath("//div[@id='mainTable']/button[1][@class='btn submit areaMarginLeft05']")),timeOutInSeconds);
			retval = Globals.KEYWORD_PASS;

		}catch (Exception e) {
			log.error("Method-verifyPositionListingsTab | Unable to verify Position Listings Tab | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to verify counts on position listing tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyCountsPositionListingsTab(String accType,String classNm,String[] expectedHeaders, String chkbxChk) throws Exception{
		APP_LOGGER.startFunction("verifyPositionListingsTab");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try{
			String prodAbb=null;
			String prodType=null;
			int i=0;
			
			//verification of table headers
			List<WebElement> headerList = Globals.driver.findElements(By.xpath("//div[@class='"+classNm+"']/div/table[@class='table table-striped table-hover']/tbody[1]/tr/th"));
    		String headerText;
			for(WebElement element : headerList){
				headerText = element.getText();
				if(headerText.equalsIgnoreCase(expectedHeaders[i])){
					sc.STAF_ReportEvent("Pass", "To Verify Position Name Column on Position Listings for new account on VV", "Position Listing Header is present in UI. Value  -"+headerText, 1);
				}else{
					sc.STAF_ReportEvent("Fail", "To Verify Position Name Column on Position Listings for new account on VV", "Mismatch in Position Listing Header.Expected = "+expectedHeaders[i] + " Actual = "+headerText, 1);
				    return retval;
				}
				i++;
			}		
			
			if(accType.equalsIgnoreCase("Volunteerism")){
				for(i=1;i<=15;i++){
					
					retval=sc.isSelected(Globals.driver.findElement(By.xpath("//div[@class='"+classNm+"']/div/table[@class='table table-striped table-hover']/tbody[2]/tr["+i+"]/td[9]/input")));
					prodAbb=Globals.driver.findElement(By.xpath("//div[@class='"+classNm+"']/div/table[@class='table table-striped table-hover']/tbody[2]/tr["+i+"]/td[2]")).getText();
					if((i<=5) && !(prodAbb.equalsIgnoreCase("L1"))){
						sc.STAF_ReportEvent("Fail", "To Verify Prod Abbrev Column on Position Listings for new account on VV", "Mismatch in Position Listing Prod Abbrev Column.Expected Prod Abbrev Column value - L1 Actual Value = "+prodAbb+" for Row -"+i, 1);
						return Globals.KEYWORD_FAIL;
					}else if(((i>5) && (i<=10)) && !(prodAbb.equalsIgnoreCase("L2"))){
						sc.STAF_ReportEvent("Fail", "To Verify Prod Abbrev Column on Position Listings for new account on VV", "Mismatch in Position Listing Prod Abbrev Column.Expected Prod Abbrev Column value - L2 Actual Value = "+prodAbb+" for Row -"+i, 1);
						return Globals.KEYWORD_FAIL;
					}else if(((i>10) && (i<=15)) && !(prodAbb.equalsIgnoreCase("L3"))){
						sc.STAF_ReportEvent("Fail", "To Verify Prod Abbrev Column on Position Listings for new account on VV", "Mismatch in Position Listing Prod Abbrev Column.Expected Prod Abbrev Column value - L3 Actual Value = "+prodAbb+" for Row -"+i, 1);
						return Globals.KEYWORD_FAIL;
					}
			        
					if(chkbxChk.equalsIgnoreCase("uncheck")){
						if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.STAF_ReportEvent("Fail", "To Verify Hide On Invite Column checkbox on Position Listings for new account on VV", "Hide On Invite Column checkbox on Position Listings is selected. Row - "+i, 1);	
							return Globals.KEYWORD_FAIL;
						}
					}else{
						if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
							sc.STAF_ReportEvent("Fail", "To Verify Hide On Invite Column checkbox on Position Listings for new account on VV", "Hide On Invite Column checkbox on Position Listings is not selected. Row - "+i, 1);	
							return Globals.KEYWORD_FAIL;
						}
					}
			  	
					if(i==1 || i==6 || i==11){
						retval=sc.isSelected(Globals.driver.findElement(By.xpath("//div[@class='"+classNm+"']/div/table[@class='table table-striped table-hover']/tbody[2]/tr["+i+"]/td[10]/input")));	
						
						if(chkbxChk.equalsIgnoreCase("uncheck")){
							if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
								sc.STAF_ReportEvent("Fail", "To Verify ide On Client Order Column checkbox on Position Listings for new account on VV", "ide On Client Order Column checkbox on Position Listings is selected. Row - "+i, 1);	
								return Globals.KEYWORD_FAIL;
							}
						}else{
							if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
								sc.STAF_ReportEvent("Fail", "To Verify ide On Client Order Column checkbox on Position Listings for new account on VV", "ide On Client Order Column checkbox on Position Listings is not selected. Row - "+i, 1);	
								return Globals.KEYWORD_FAIL;
							}
						}
					}
			  	}
			}else if(accType.equalsIgnoreCase("Employment")) {
				for(i=1;i<=3;i++){
					retval=sc.isSelected(Globals.driver.findElement(By.xpath("//div[@class='"+classNm+"']/div/table[@class='table table-striped table-hover']/tbody[2]/tr["+i+"]/td[9]/input")));
					prodType=Globals.driver.findElement(By.xpath("//div[@class='"+classNm+"']/div/table[@class='table table-striped table-hover']/tbody[2]/tr["+i+"]/td[1]/input")).getAttribute("value");
					prodAbb=Globals.driver.findElement(By.xpath("//div[@class='"+classNm+"']/div/table[@class='table table-striped table-hover']/tbody[2]/tr["+i+"]/td[2]")).getText();
					if((i==1) && (!(prodAbb.equalsIgnoreCase("L1"))) && (!prodType.contains("Basic Criminal Locator Search - Client Pays All"))){
						sc.STAF_ReportEvent("Fail", "To Verify Prod Abbrev Column on Position Listings for new account on VV", "Mismatch in Position Listing Prod Abbrev Column.Expected Prod Abbrev Column value - L1 Actual Value = "+prodAbb+" for Row -"+i, 1);
						return Globals.KEYWORD_FAIL;
					}else if((i==2) && (!(prodAbb.equalsIgnoreCase("L2"))) && (!prodType.contains("Advanced Criminal Locator Search - Client Pays All"))){
						sc.STAF_ReportEvent("Fail", "To Verify Prod Abbrev Column on Position Listings for new account on VV", "Mismatch in Position Listing Prod Abbrev Column.Expected Prod Abbrev Column value - L2 Actual Value = "+prodAbb+" for Row -"+i, 1);
						return Globals.KEYWORD_FAIL;
					}else if(i==3 && !(prodAbb.equalsIgnoreCase("L3")) && (!prodType.contains("Complete Criminal Locator Search - Client Pays All"))){
						sc.STAF_ReportEvent("Fail", "To Verify Prod Abbrev Column on Position Listings for new account on VV", "Mismatch in Position Listing Prod Abbrev Column.Expected Prod Abbrev Column value - L3 Actual Value = "+prodAbb+" for Row -"+i, 1);
						return Globals.KEYWORD_FAIL;					
					}
			    
					if(chkbxChk.equalsIgnoreCase("uncheck")){
						if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.STAF_ReportEvent("Fail", "To Verify Hide On Invite Column checkbox on Position Listings for new account on VV", "Hide On Invite Column checkbox on Position Listings is selected. Row - "+i, 1);	
							return Globals.KEYWORD_FAIL;
						}
					}else{
						if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
							sc.STAF_ReportEvent("Fail", "To Verify Hide On Invite Column checkbox on Position Listings for new account on VV", "Hide On Invite Column checkbox on Position Listings is not selected. Row - "+i, 1);	
							return Globals.KEYWORD_FAIL;
						}
					}
			  	
					if(i==1 || i==2 || i==3){
						retval=sc.isSelected(Globals.driver.findElement(By.xpath("//div[@class='"+classNm+"']/div/table[@class='table table-striped table-hover']/tbody[2]/tr["+i+"]/td[10]/input")));	
						if(chkbxChk.equalsIgnoreCase("uncheck")){
							if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
								sc.STAF_ReportEvent("Fail", "To Verify ide On Client Order Column checkbox on Position Listings for new account on VV", "ide On Client Order Column checkbox on Position Listings is selected. Row - "+i, 1);	
								return Globals.KEYWORD_FAIL;
							}
						}else{
							if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
								sc.STAF_ReportEvent("Fail", "To Verify ide On Client Order Column checkbox on Position Listings for new account on VV", "ide On Client Order Column checkbox on Position Listings is not selected. Row - "+i, 1);	
								return Globals.KEYWORD_FAIL;
							}
						}
					}
				}
				
			}			
			
		}catch (Exception e) {
		  log.error("Method-verifyPositionListingsTab | Unable to verify Position Listings Tab | Exception - "+ e.toString());
		  throw e;
	    }
		
	    return Globals.KEYWORD_PASS;
	}
	
	/**************************************************************************************************
	 * Method to configure api level settings
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String newAPIConfiguration() throws Exception{
		APP_LOGGER.startFunction("newAPIConfiguration");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try {

			//			Steps for API Configuring
			//			1.Create new user for API Ordering
			//			2.Configure the same user in API COnfiguration tab
			//			3.Go to users tab and then select Integration user

			//new user creation
			tempRetval = newUserCreation("No","apiUser");

			//configure api ordering

			tempRetval = sc.waitforElementToDisplay("manageAccount_apiConfiguration_link", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-newAPIConfiguration - Page has not been loaded for Accounts Main tab");
				return retval;
			}

			sc.clickWhenElementIsClickable("manageAccount_apiConfiguration_link", timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("apiConfig_Create_btn", 10);


			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				//need to check whether its already configured or not.
				tempRetval= Globals.KEYWORD_FAIL;
				tempRetval = sc.waitforElementToDisplay("associatedAccounts_edit_btn", 2);
				sc.clickWhenElementIsClickable("associatedAccounts_edit_btn", 2);

			}
			//client logo url
			sc.setValueJsChange("apiConfig_logoPath_txt","https://www.verifiedvolunteers.com/~/media/Verified%20Volunteers/Images/Logos/GS_Profile.png");

			//api configuration
			String expectdedUserName= Globals.testSuiteXLS.getCellData_fromTestData("APIUserName");
			String sendNotificationFlag=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
			
			sc.checkCheckBox("apiConfig_apiEnabled_chk");
			if(sendNotificationFlag.equalsIgnoreCase("Yes")||sendNotificationFlag.equalsIgnoreCase("N/A")||sendNotificationFlag.isEmpty()||sendNotificationFlag.equalsIgnoreCase("")){
			    sc.checkCheckBox("apiConfig_sendnotification_chk");
				sc.setValueJsChange("apiConfig_callBackURL_txt", Globals.testSuiteXLS.getCellData_fromTestData("callBackURL"));
				sc.setValueJsChange("apiConfig_userName_txt", expectdedUserName);
				sc.setValueJsChange("apiConfig_password_txt", Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd"));
			}else{
				retval=sc.isEnabled("apiConfig_callBackURL_txt");
				if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					retval=sc.isEnabled("apiConfig_userName_txt");
					if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						retval=sc.isEnabled("apiConfig_password_txt");
						if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.STAF_ReportEvent("Fail", "Password field should be disabled", "API Configured Tab Validation", 1);						
						}					
					}else{
						sc.STAF_ReportEvent("Fail", "Username field should be disabled", "API Configured Tab Validation", 1);	
					}
					
				}else{
					sc.STAF_ReportEvent("Fail", "Callback Url field should be disabled", "API Configured Tab Validation", 1);	
				}
				
			}
			
			sc.checkCheckBox("apiConfig_sendInvitationURL_chk");

			sc.clickWhenElementIsClickable("apiConfig_Create_btn", timeOutInSeconds);

			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("associatedAccounts_edit_btn", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To Verify API Configuration for new account on VV", "API Configured successfully", 1);
				retval = Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "To Verify API Configuration for new account on VV	", "Unable to setup API configuration",1);
				return Globals.KEYWORD_FAIL;
			}

			//modify user right for Integration

			tempRetval = Globals.KEYWORD_FAIL;
			sc.clickWhenElementIsClickable("manageAccount_users_link", timeOutInSeconds);
			tempRetval =  sc.waitforElementToDisplay("users_searchUser_txt", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-newAPIConfiguration - Page has not been loaded for User list");
				return retval;
			}
			WebElement userGrid;
			int rowCount;
			int i=0;
			WebElement searchUser = sc.createWebElement("users_searchUser_txt");
			tempRetval =  sc.setValueJsChange("users_searchUser_txt", expectdedUserName);
			searchUser.sendKeys(Keys.ENTER);
			Thread.sleep(1000);

			userGrid = Globals.driver.findElement(LocatorAccess.getLocator("users_userGrid_tbl"));
			rowCount=sc.getRowCount_tbl(userGrid);
			String userFound = Globals.KEYWORD_FAIL;

			if(rowCount != Globals.INT_FAIL && rowCount >= 1 ){

				String userNameInUI;


				for(i= 1;i<=rowCount;i++){
					//TODO - need to fetch the column position dynamically.
					userNameInUI = sc.getCellData_tbl(userGrid, i, 5);
					if(userNameInUI.equalsIgnoreCase(expectdedUserName)){
						userFound = Globals.KEYWORD_PASS;
						break;
					}
				}
			}

			if(userFound.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To setup new API User", "User found.User Name - "+expectdedUserName, 1);

			}else{
				sc.STAF_ReportEvent("Fail", "To setup new API User", "User doesnt exists in this Account.User Name - "+expectdedUserName, 1);
				return Globals.KEYWORD_FAIL;
			}

			//table[@class='results selectClick']
			Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+i+"]/td[5]")).click();

			tempRetval =  sc.waitforElementToDisplay("associatedAccounts_edit_btn", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "To setup new API User", "Edit user page has not been loaded.User Name - "+expectdedUserName, 1);
				log.debug("Method-newAPIConfiguration - Page has not been loaded for User list");
				return Globals.KEYWORD_FAIL;
			}

			sc.clickWhenElementIsClickable("associatedAccounts_edit_btn", timeOutInSeconds);
			List<WebElement> userRights = Globals.driver.findElements(LocatorAccess.getLocator("newUser_userPermission_rdb"));
			if(userRights.size() == 3){
				userRights.get(1).click();
				sc.STAF_ReportEvent("Pass", "To setup new API User", "Integartion User permission selected.User Name - "+expectdedUserName, 1);
			}else{
				sc.STAF_ReportEvent("Fail", "To setup new API User", "Integartion User permission doesnt exists.User Name - "+expectdedUserName, 1);
				return Globals.KEYWORD_FAIL;
			}
		    String fName="Integration"+Globals.testSuiteXLS.getCellData_fromTestData("User_Fname");
			tempRetval = sc.setValueJsChange("newUser_fname_txt",fName );
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "To setup new API User", "Unable to set first name for Integration User", 1);
				return Globals.KEYWORD_FAIL;
			}
			sc.clickWhenElementIsClickable("newUser_create_btn", timeOutInSeconds);
			tempRetval =  sc.waitforElementToDisplay("associatedAccounts_edit_btn", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "To setup new API User", "Unable to set Integration User right to user.User Name - "+expectdedUserName, 1);
				return Globals.KEYWORD_FAIL;
			}else{
				sc.STAF_ReportEvent("Pass", "To setup new API User", "API user and configuration setup successfully.User Name - "+expectdedUserName, 1);
				retval=Globals.KEYWORD_PASS;
			}



		}catch (Exception e) {
			log.error("Method-newAPIConfiguration | Unable to setup API configurations | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to configure associated accounts for client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String newAssociatedAccount() throws Exception{
		APP_LOGGER.startFunction("newAssociatedAccount");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try {

			tempRetval = sc.waitforElementToDisplay("manageAccount_associatedAccounts_link", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-newAssociatedAccount - Page has not been loaded for Accounts Main tab");
				return retval;
			}

			sc.clickWhenElementIsClickable("manageAccount_associatedAccounts_link", timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("associatedAccounts_edit_btn", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-newAPIConfiguration - Page has not been loaded for Associated Accounts tab");
				return retval;
			}
			sc.clickWhenElementIsClickable("associatedAccounts_edit_btn", timeOutInSeconds);

			//parent account
			String parentAct=Globals.testSuiteXLS.getCellData_fromTestData("ParentAccount");
			if(parentAct.isEmpty() || parentAct == null){
				sc.STAF_ReportEvent("Fail", "To Verify Associated Accounts for new account on VV", "Test Data error.Parent account not mentioned",0);
				return Globals.KEYWORD_FAIL;
			}

			tempRetval =  sc.selectValue_byVisibleText("associatedAccounts_clientParent_dd",parentAct );
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "To Verify Associated Accounts for new account on VV", "Unable to select parent account from dropdown"+parentAct,1);
				return Globals.KEYWORD_FAIL;
			}

			sc.clickWhenElementIsClickable("associatedAccounts_Create_btn", timeOutInSeconds);

			tempRetval = sc.waitforElementToDisplay("associatedAccounts_edit_btn", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "To Verify Associated Accounts for new account on VV", "Unable to setup parent account"+parentAct,1);
				return Globals.KEYWORD_FAIL;
			}

			//verify whether parent account has been set
			sc.clickWhenElementIsClickable("homepage_clients_link", timeOutInSeconds);
			
			int rowId   = 	Globals.INT_FAIL;
			rowId	=	searchAccount();
			String actualParent;

			if	(rowId == Globals.INT_FAIL){
				sc.STAF_ReportEvent("Fail", "To Verify Associated Accounts for new account on VV", "Unable to search account", 1);
				return Globals.KEYWORD_FAIL;
			}else{
				WebElement accountGrid 	=	Globals.driver.findElement(By.xpath("//table[@class='results selectClick']"));	 
				actualParent = sc.getCellData_tbl(accountGrid, rowId, 3);
				if(actualParent.equalsIgnoreCase(parentAct)){
					sc.STAF_ReportEvent("Pass", "To Verify Associated Accounts for new account on VV", "Parent Account Set - "+parentAct, 1);
					Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+rowId+"]")).click();
					retval=Globals.KEYWORD_PASS;
				}else{
					sc.STAF_ReportEvent("Fail", "To Verify Associated Accounts for new account on VV", "Parent account mismatch.Expected -"+parentAct + " Actual - "+actualParent,1);
					return Globals.KEYWORD_FAIL;
				}
			}

		}catch (Exception e) {
			log.error("Method-newAssociatedAccount | Unable to setup associated account | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to search an account in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static int searchAccount() throws Exception{
		APP_LOGGER.startFunction("searchAccount");
		int retval=Globals.INT_FAIL;
		String accountName="";
		int rowCount = Globals.INT_FAIL;

		try {
			if (sc.waitforElementToDisplay("searchAccount_accountSearch_txt",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){


				sc.STAF_ReportEvent("Pass", "InhousePortalHomePage", "Account Search Page has been loaded" , 1);


				accountName=Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
				sc.setValueJsChange("searchAccount_accountSearch_txt", accountName);
				//				
				sc.clickWhenElementIsClickable("seachAccount_searchBtn_btn", 10);
				//				

				boolean accountFound = false;
				int i=0;
				if (sc.waitforElementToDisplay("searchAccount_accountGrid_tbl",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){ 
					WebElement accountsTable = sc.createWebElement("searchAccount_accountGrid_tbl");
					rowCount=sc.getRowCount_tbl(accountsTable);
					if(rowCount != Globals.INT_FAIL){
						log.info("Method searchAccount | Account result grid displayed with " + rowCount + " account(s)" );

						String accountNameInUI;

						if(rowCount==1){
							accountNameInUI = sc.getCellData_tbl(accountsTable, 1, 1);
							if(accountNameInUI.contains("No matching records")){
								accountFound= false;
							}else{
								accountNameInUI = sc.getCellData_tbl(accountsTable, 1, 2);
								if(accountNameInUI.equalsIgnoreCase(accountName)){
									accountFound = true;
									i=1;
									
								}
							}
						}else{
							for(i= 1;i<=rowCount;i++){
								//TODO - need to fetch the column position dynamically.
								accountNameInUI = sc.getCellData_tbl(accountsTable, i, 2);
								if(accountNameInUI.equalsIgnoreCase(accountName)){
									accountFound = true;
									break;
								}

							}
						}
						
						
					}

					if(accountFound == true){
						return i; // row number of the account found
					}



				}else{
					log.info("Method searchAccount | Account Grid is not displayed| Account Name:- "+ accountName);

				}
			}else{
				sc.STAF_ReportEvent("Fail", "InhousePortalHomePage", "Account Search has NOT been loaded" , 1);
				log.error("Unable to Search Account as Account Search field is not displayed");
			}		


		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Inhouse Portal-searchAccount | Account Name - "+ accountName + "|  Exception occurred - " + e.toString());
			throw e;
		}




		return retval;
	}

	/**************************************************************************************************
	 * Method to verify order request fields
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String validateOrderRequestFields(String ordertype){
        APP_LOGGER.startFunction("validateOrderRequestFields");
        String retval = Globals.KEYWORD_FAIL;

        try{
               // Validation of PackageID data value

               HashMap<String, String> nodeListAndVals = new HashMap<String, String>();
               String colName = Globals.Env_To_Execute_ON+"_GoodDeedCodeOrURL";
               String goodDeedCode = Globals.testSuiteXLS.getCellData_fromTestData(colName);

               nodeListAndVals.put("PackageID", "");
               modifyNodeAndValidateResponseXML("Blank Package ID ",nodeListAndVals,"Promo Code not found","Blank_PackageID");
               nodeListAndVals.remove("PackageID");

               //                   invalid promo code is the first priority of validation.
               //                   hence if we send invalid promo code with invalid first name, only in valid promo code message would be displayed
               nodeListAndVals.put("PackageID", goodDeedCode); 

               nodeListAndVals.put("ScreeningSubjectID", "");
               modifyNodeAndValidateResponseXML("Blank Candidate ID",nodeListAndVals,"Missing api candidateId", "Blank_CandidateID");
               nodeListAndVals.remove("ScreeningSubjectID");

               nodeListAndVals.put("oa:GivenName", "");
               modifyNodeAndValidateResponseXML("Blank First Name",nodeListAndVals,"Missing first name", "Blank_FirstName");
               nodeListAndVals.remove("oa:GivenName");

               nodeListAndVals.put("FamilyName", "");
               modifyNodeAndValidateResponseXML("Blank Last Name",nodeListAndVals,"Missing last name","Blank_FamilyName");
               nodeListAndVals.remove("FamilyName");

               nodeListAndVals.put("oa:URI", "");
               modifyNodeAndValidateResponseXML("Blank Email Address",nodeListAndVals,"Missing Email address", "Blank_EmailID");
               nodeListAndVals.remove("oa:URI");
               
               nodeListAndVals.put("oa:PostalCode", "");
               modifyNodeAndValidateResponseXML("Blank Zip Code",nodeListAndVals,"Missing zip code", "Blank_ZipCode");
               nodeListAndVals.remove("oa:PostalCode");
               
               nodeListAndVals.put("oa:PostalCode", "1000");
               modifyNodeAndValidateResponseXML("less than 5 digit Zip Code",nodeListAndVals,"Invalid zip code", "ZipCode_less than 5");
               nodeListAndVals.remove("oa:PostalCode");
               
           String errMsgCity="";
           String errMsgAdd="";
           if(ordertype.equalsIgnoreCase("API")){
                     errMsgCity="Validation failed for one or more entities. See 'EntityValidationErrors' property for more details.";
                     errMsgAdd="Validation failed for one or more entities. See 'EntityValidationErrors' property for more details.";
               }else{
                     errMsgCity="Missing required address city";
                     errMsgAdd="Missing required address line 1";
               }
               nodeListAndVals.put("oa:CityName", "");
               nodeListAndVals.put("oa:GivenName", sc.runtimeGeneratedStringValue(9));
               nodeListAndVals.put("ScreeningSubjectID", sc.runtimeGeneratedStringValue(10));
               modifyNodeAndValidateResponseXML("Blank City Name",nodeListAndVals,errMsgCity, "Request_Blank_CityName");
               nodeListAndVals.remove("oa:CityName");
               nodeListAndVals.remove("oa:GivenName");
               nodeListAndVals.remove("ScreeningSubjectID");

               nodeListAndVals.put("oa:LineOne", "");
               nodeListAndVals.put("oa:GivenName", sc.runtimeGeneratedStringValue(9));
               nodeListAndVals.put("ScreeningSubjectID", sc.runtimeGeneratedStringValue(10));
               modifyNodeAndValidateResponseXML("Blank Address Line One",nodeListAndVals,errMsgAdd, "Blank_AddrLine1");
               nodeListAndVals.remove("oa:LineOne");
               nodeListAndVals.remove("oa:GivenName");
               nodeListAndVals.remove("ScreeningSubjectID");
               
               nodeListAndVals.put("oa:CountrySubDivisionCode", "");
               modifyNodeAndValidateResponseXML("Blank State Code",nodeListAndVals,"State required, Invalid Statecode received", "Blank_State Code");
               nodeListAndVals.remove("oa:CountrySubDivisionCode");
               
               nodeListAndVals.put("oa:CountrySubDivisionCode", "abc");
               modifyNodeAndValidateResponseXML("Invalid State Code",nodeListAndVals,"Invalid Statecode received", "Invalid_State Code");
               nodeListAndVals.remove("oa:CountrySubDivisionCode");
                      
               nodeListAndVals.put("FormattedDateTime", "");
               modifyNodeAndValidateResponseXML("Blank DOB",nodeListAndVals,"Missing Date of Birth", "Blank_DOB");
               nodeListAndVals.remove("FormattedDateTime");

               String invalidDOb = sc.getPriorDate("YYYY-MM-dd", 17); //format 1979-07-17
               nodeListAndVals.put("FormattedDateTime", invalidDOb);
               modifyNodeAndValidateResponseXML("Invalid DOB",nodeListAndVals,"Invalid DOB - Less Than 18 years of Age", "Invalid_DOB");
               nodeListAndVals.remove("FormattedDateTime");
               
               if(!ordertype.equalsIgnoreCase("API")){
                  //Removed the Gender validation as Gendor fiedl is not longer a required field and if left blank takes Notknown as the value
//            	   nodeListAndVals.put("GenderCode", "");
//                   modifyNodeAndValidateResponseXML("Blank Gender",nodeListAndVals,"Missing required gender information", "Blank_Gender");
//                   nodeListAndVals.remove("GenderCode");           	   
            	   
            	   nodeListAndVals.put("oa:AreaDialing", "");
                   modifyNodeAndValidateResponseXML("Blank Area Dialing Number",nodeListAndVals,"Missing or invalid required phone number", "Blank_Telephone");
                   nodeListAndVals.remove("oa:AreaDialing");
                                      
                   nodeListAndVals.put("oa:DialNumber", "");
                   modifyNodeAndValidateResponseXML("Blank Dialing Number",nodeListAndVals,"Missing or invalid required phone number", "Blank_Telephone");
                   nodeListAndVals.remove("oa:DialNumber");
                   
                   nodeListAndVals.put("oa:AreaDialing", "23");
                   modifyNodeAndValidateResponseXML("Area Dialing Number less than 3 digit",nodeListAndVals,"Missing or invalid required phone number", "Blank_Telephone");
                   nodeListAndVals.remove("oa:AreaDialing");                   
                   
                   nodeListAndVals.put("oa:AreaDialing", "2356");
                   modifyNodeAndValidateResponseXML("Area Dialing Number greater than 3 digit",nodeListAndVals,"Missing or invalid required phone number", "Blank_Telephone");
                   nodeListAndVals.remove("oa:AreaDialing"); 
                   
                   nodeListAndVals.put("oa:DialNumber", "23-3456");
                   modifyNodeAndValidateResponseXML("less than 7 Dialing Number",nodeListAndVals,"Missing or invalid required phone number", "Blank_Telephone");
                   nodeListAndVals.remove("oa:DialNumber");
                   
                   nodeListAndVals.put("oa:DialNumber", "723-34567");
                   modifyNodeAndValidateResponseXML("more than 7 Dialing Number",nodeListAndVals,"Missing or invalid required phone number", "Blank_Telephone");
                   nodeListAndVals.remove("oa:DialNumber");
                   
                   
                   String promocodentsupp=""; //l1+mvr client pays all
                   String prcdssnmr9="";//ssn more than 9 digit
                   String prcdnotcltpy="";//// package id is not client pays all
                   
                   if(ordertype.equalsIgnoreCase("SM")){
                   
                	   if(Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA2")){
                		   promocodentsupp="0za2ydm";
                		   prcdssnmr9="qehpc4q";
                		   prcdnotcltpy="4734hts";
                       }else if(Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA3")){
                		   promocodentsupp="aubj255"; 
                		   prcdssnmr9="ub34cd1";
                		   prcdnotcltpy="gzbjqxw";
                	   }
                   }else if(ordertype.equalsIgnoreCase("SM_Cons_EnSSN")){
                	   if(Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA2")){
                		   promocodentsupp="looip15"; // l1 + all
                		   prcdssnmr9="4731k0z"; //l1 + ls
                		   prcdnotcltpy="51d581x"; // l1 not client pays all
                       }else if(Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA3")){
                		   promocodentsupp="f5gdsty"; 
                		   prcdssnmr9="f54z15t";
                		   prcdnotcltpy="wwf5xlu";
                	   }
                   }
                  //product not supported for seamlessorder
                   nodeListAndVals.put("PackageID", promocodentsupp);
                   modifyNodeAndValidateResponseXML("product not supported for seamlessorder",nodeListAndVals,"Order contains a product not allowed via the integration", "Product_notsupported");
                   nodeListAndVals.remove("PackageID");
                   nodeListAndVals.put("PackageID", goodDeedCode);
                           	   
            	   // package id is not client pays all
                    nodeListAndVals.put("PackageID", prcdnotcltpy);
                    modifyNodeAndValidateResponseXML("Promocode not client pays all",nodeListAndVals,"Invalid Promo Code for Client", "Promocode_notclientpaysall");
                    nodeListAndVals.remove("PackageID");
                    nodeListAndVals.put("PackageID", goodDeedCode);
                     
                     if(ordertype.equalsIgnoreCase("SM")){
                    	//ssn more than 9 digit
                         nodeListAndVals.put("PackageID", prcdssnmr9);
                         nodeListAndVals.put("PersonLegalID", "5674567890");
                         modifyNodeAndValidateResponseXML("SSN Required for product",nodeListAndVals,"Order contains a product requiring an SSN", "SSN_Required_Morethan9Digit");
                         nodeListAndVals.remove("PackageID");
                         nodeListAndVals.put("PersonLegalID", "");
                         nodeListAndVals.put("PackageID", goodDeedCode);
                         
                        //ssn less than 9 digit
                         nodeListAndVals.put("PackageID", prcdssnmr9);
                         nodeListAndVals.put("PersonLegalID", "56745689");
                         modifyNodeAndValidateResponseXML("SSN Required for product",nodeListAndVals,"Order contains a product requiring an SSN", "SSN_Required_lessthann9digit");
                         nodeListAndVals.remove("PackageID");
                         nodeListAndVals.put("PersonLegalID", "");
                         nodeListAndVals.put("PackageID", goodDeedCode);	 
                    	                     	 
                         //SSN Required for product
                         nodeListAndVals.put("PackageID", prcdssnmr9);
                         nodeListAndVals.put("PersonLegalID", "");
                         modifyNodeAndValidateResponseXML("SSN Required for product",nodeListAndVals,"Order contains a product requiring an SSN", "SSN_Required");
                         nodeListAndVals.remove("PackageID");
                         nodeListAndVals.put("PersonLegalID", "");
                         nodeListAndVals.put("PackageID", goodDeedCode);
                     }
               }

               retval =      Globals.KEYWORD_PASS;
        }catch (Exception e) {
               // TODO Auto-generated catch block
               log.error("Inhouse Portal-validateOrderRequestFields |   Exception occurred - " + e.toString());

        }

        return retval;
 }

	/**************************************************************************************************
	 * Method to modify and update values in node as well as validates the reponse xml
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String modifyNodeAndValidateResponseXML(String stepName,HashMap<String, String> nodeListAndVals,String ValidationMsg,String scenarioName){
		APP_LOGGER.startFunction("modifyNodeAndValidateResponseXML");
		String retval = Globals.KEYWORD_FAIL;


		try{

			XMLUtils vvReferenceInput 	= new XMLUtils(Globals.vvInputReferenceXML, Globals.getEnvPropertyValue("SoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);

			for (String node: nodeListAndVals.keySet()){

				String key =node.toString();
				String value = nodeListAndVals.get(node).toString();  
				vvReferenceInput.updatedXMLNodeValueByTagName(key,value);
			}
			String modifiedInputFileName =Globals.TestCaseID+"_Request_"+scenarioName + ".xml";
			String ackFileName=Globals.TestCaseID+"_Response_"+scenarioName + ".xml";
			vvReferenceInput.saveXMLDOM2File(modifiedInputFileName); //Globals.TestCaseID+"_Request_Blank_DOB.xml

			vvReferenceInput =null;

			String updatedXMLFilePath 	= Globals.currentPlatform_XMLResults + "\\"+ modifiedInputFileName;

			XMLUtils fieldUpdatedXML 	= new XMLUtils(updatedXMLFilePath, Globals.getEnvPropertyValue("SoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);
			fieldUpdatedXML.postXML();
			//			System.out.println(response);
			String responseFilePath 	= Globals.currentPlatform_XMLResults + "\\"+ ackFileName;
			fieldUpdatedXML.saveXMLString(responseFilePath);
			fieldUpdatedXML = null;

			XMLUtils responseXML 	= new XMLUtils(responseFilePath);
			
			String errorDescriptionTagName = "q1:Description";
            if(ValidationMsg.equalsIgnoreCase("Validation failed for one or more entities. See 'EntityValidationErrors' property for more details.")){
                    errorDescriptionTagName = "Description";       
            }

			String errorDescrVal = responseXML.getXMLNodeValByTagName(errorDescriptionTagName);

			Globals.Result_Link_FilePath = responseFilePath;
			if(errorDescrVal.equalsIgnoreCase(ValidationMsg)){
				sc.STAF_ReportEvent("Pass", "Verify Mandatory fields in API. - "+stepName, "Validation Message Matches.Description - "+ValidationMsg , 3);
			}else{
				sc.STAF_ReportEvent("Fail", "Verify Mandatory fields in API - "+stepName, "Validation Message Mismatch.Expected - "+ValidationMsg + " Actual - "+ errorDescrVal , 3);
			}

			retval=Globals.KEYWORD_PASS;

		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Inhouse Portal-modifyNodeAndValidateResponseXML |   Exception occurred - " + e.toString());

		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to modify and update values in node as well as validates the reponse xml
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String modifyNodeAndValidateChangeResponseXML(String stepName,HashMap<String, String> nodeListAndVals,String ValidationMsg,String scenarioName){
		APP_LOGGER.startFunction("modifyNodeAndValidateResponseXML");
		String retval = Globals.KEYWORD_FAIL;


		try{

			XMLUtils vvReferenceInput 	= new XMLUtils(Globals.currentAPIOrderRequestPath, Globals.getEnvPropertyValue("ChangeSoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);
			String fname =  Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName"); 
			String lname= Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName"); 
			String textchangeXML=null;
			String changeConsent=vvReferenceInput.getXMLNodeValByTagName("ConsentHTML").toString();
			String checkboxName=null;
			String actualText;
			
			if(stepName.contains("Blank Consent")){
				    
			    if(stepName.split("Consent ")[1].trim().equalsIgnoreCase("First name")){
			    	textchangeXML=changeConsent.replace(fname, "");	 
			    }else{
			    	textchangeXML=changeConsent.replace(lname, "");	
			    }
                nodeListAndVals.put("ConsentHTML", textchangeXML);				
			}if(stepName.contains("Invalid Consent")){
				
				    if(stepName.split("Consent ")[1].trim().equalsIgnoreCase("First name")){
				    	textchangeXML=changeConsent.replace(fname, "invalid");	 
				    }else{
				    	textchangeXML=changeConsent.replace(lname, "invalid");	
				    }
				  nodeListAndVals.put("ConsentHTML", textchangeXML);	
			}if(stepName.contains("Consent checkbox Unchecked")){
				checkboxName=stepName.split(" Consent ")[0].trim();
				actualText=checkboxName+"\" style=\"float: left; margin-top: 5px;\" checked=\"true\"";
				textchangeXML=changeConsent.replace(actualText, checkboxName+"\" style=\"float: left; margin-top: 5px;\" checked=\"\""); 
			    nodeListAndVals.put("ConsentHTML", textchangeXML);
			}
			
			
			for (String node: nodeListAndVals.keySet()){

				String key =node.toString();
				String value = nodeListAndVals.get(node).toString();  
				vvReferenceInput.updatedXMLNodeValueByTagName(key,value);
			}
			String modifiedInputFileName =Globals.TestCaseID+"_Request_"+scenarioName + ".xml";
			String ackFileName=Globals.TestCaseID+"_Response_"+scenarioName + ".xml";
			vvReferenceInput.saveXMLDOM2File(modifiedInputFileName); //Globals.TestCaseID+"_Request_Blank_DOB.xml

			vvReferenceInput =null;

			String updatedXMLFilePath 	= Globals.currentPlatform_XMLResults + "\\"+ modifiedInputFileName;

			XMLUtils fieldUpdatedXML 	= new XMLUtils(updatedXMLFilePath,  Globals.getEnvPropertyValue("ChangeSoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);
			fieldUpdatedXML.postXML();
			//			System.out.println(response);
			String responseFilePath 	= Globals.currentPlatform_XMLResults + "\\"+ ackFileName;
			fieldUpdatedXML.saveXMLString(responseFilePath);
			fieldUpdatedXML = null;

			XMLUtils responseXML 	= new XMLUtils(responseFilePath);
			
			String errorDescriptionTagName = "q1:Description";
            if(ValidationMsg.equalsIgnoreCase("Validation failed for one or more entities. See 'EntityValidationErrors' property for more details.")){
                    errorDescriptionTagName = "Description";       
            }

			String errorDescrVal = responseXML.getXMLNodeValByTagName(errorDescriptionTagName);

			Globals.Result_Link_FilePath = responseFilePath;
			if(errorDescrVal.equalsIgnoreCase(ValidationMsg)){
				sc.STAF_ReportEvent("Pass", "Verify Mandatory fields in API. - "+stepName, "Validation Message Matches.Description - "+ValidationMsg , 3);
			}else{
				sc.STAF_ReportEvent("Fail", "Verify Mandatory fields in API - "+stepName, "Validation Message Mismatch.Expected - "+ValidationMsg + " Actual - "+ errorDescrVal , 3);
			}

			retval=Globals.KEYWORD_PASS;

		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Inhouse Portal-modifyNodeAndValidateResponseXML |   Exception occurred - " + e.toString());

		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to valaidate the order acknowledgement that is received
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyOrderAcknowledgment(String xmlFilePath,String ApplicantID){
		APP_LOGGER.startFunction("verifyOrderAcknowledgment");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;

		try{

			XMLUtils oResponseXML = new XMLUtils(xmlFilePath);
			oResponseXML.verifyXMLNodeValueByTagName("Code", "New","Code");
			oResponseXML.verifyXMLNodeValueByTagName("q1:DocumentID", "numeric","OrderID");

			tempRetval 	=	oResponseXML.verifyXMLNodeValueByTagName("VolunteerUrl", "url","Volunterr URL");

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				Globals.testSuiteXLS.setCellData_inTestData("CustomEmailMessage", "");
				return Globals.KEYWORD_FAIL;
			}else{
				Globals.testSuiteXLS.setCellData_inTestData("CustomEmailMessage", tempRetval);
			}


			oResponseXML.verifyXMLNodeValueByTagName("BODID", ApplicantID,"Applicant ID");
			retval= Globals.KEYWORD_PASS;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Inhouse Portal-verifyOrderAcknowledgment |   Exception occurred - " + e.toString());

		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to fetch the expected total proce for an order
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static double fetchExpectedTotalPrice() throws Exception{
		APP_LOGGER.startFunction("fetchExpectedTotalPrice");

		String products = Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts");
		String[] productsBeingOrdered = products.split("-");
		double totalAmt = 0;
		double price=0.00;

		if (productsBeingOrdered.length <=0 ){
			sc.STAF_ReportEvent("Fail", "Expected Total Price", "Total Price could not be calculated as Products are not mentioned in Test Data", 0);
			throw new Exception();
		}

		for(String productName : productsBeingOrdered){
			productName = productName.toUpperCase().trim();
			if(productName.equalsIgnoreCase("ID")){
				price =0.00;
			}else if(productName.equalsIgnoreCase("ABUSE(PA)")){
				price = ClientFacingApp.fetchProductPrice(VV_Products.valueOf("ABUSE"));
			}else if(productName.equalsIgnoreCase("ABUSE(CO)")){
				price = ClientFacingApp.fetchProductPrice(VV_Products.valueOf("ABUSE"));
			}else{
				price = ClientFacingApp.fetchProductPrice(VV_Products.valueOf(productName));
			}
			totalAmt = totalAmt +price ;
			

		}

		return totalAmt;		

	}

	/**************************************************************************************************
	 * Method to verify available states tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String validateAvailableStates(){
		APP_LOGGER.startFunction("validateAvailableStates");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try {

			tempRetval = sc.waitforElementToDisplay("manageAccount_availableStates_link", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","Available States","Home page has not been loaded for Account",1);
				return retval;
			}

			sc.clickWhenElementIsClickable("manageAccount_availableStates_link", timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("associatedAccounts_edit_btn", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","Available States","Page has not been loaded for Available States tab",1);
				return retval;
			}

			sc.clickWhenElementIsClickable("associatedAccounts_edit_btn", timeOutInSeconds);

			List<WebElement> availableState =  Globals.driver.findElements(By.xpath("//span[@id='StateCode']"));
			if(availableState.size() != 57){
				sc.STAF_ReportEvent("Fail", "Available States", "No of available states is not equal to 57. Actual Value-"+availableState.size(),0);
			}
			else{
				sc.STAF_ReportEvent("Pass", "Available States", "No of available states is equal to 57",0);

				String[] expVal = {"AA-Armed Forces, Americas","AE-Armed Forces, Europe","AK-Alaska","AL-Alabama","AP-Armed Forces, Pacific",
						"AR-Arkansas","AZ-Arizona","CA-California","CO-Colorado","CT-Connecticut","DC-District of Columbia","DE-Delaware","FL-Florida",
						"GA-Georgia","GU-Guam","HI-Hawaii","IA-Iowa","ID-Idaho","IL-Illinois","IN-Indiana","KS-Kansas","KY-Kentucky","LA-Louisiana",
						"MA-Massachusetts","MD-Maryland","ME-Maine","MI-Michigan","MN-Minnesota","MO-Missouri","MS-Mississippi","MT-Montana","NC-North Carolina",
						"ND-North Dakota","NE-Nebraska","NH-New Hampshire","NJ-New Jersey","NM-New Mexico","NV-Nevada","NY-New York","OH-Ohio","OK-Oklahoma",
						"OR-Oregon","PA-Pennsylvania","PR-Puerto Rico","RI-Rhode Island","SC-South Carolina","SD-South Dakota","TN-Tennessee","TX-Texas","UT-Utah",
						"VA-Virginia","VI-Virgin Islands","VT-Vermont","WA-Washington","WI-Wisconsin","WV-West Virginia","WY-Wyoming"
				};
				WebElement state=null;
				String stateName="";
				for(int i=0;i<57;i++){
					state = availableState.get(i);
					stateName =  state.getText();
					stateName= stateName.replace("−", "-");
					if(stateName.equalsIgnoreCase(expVal[i].trim())){
						sc.STAF_ReportEvent("Pass", "Available States", "State Name matches with Expected.Val = "+stateName,0);
					}else{
						sc.scrollIntoView(state);
						sc.STAF_ReportEvent("Fail", "Available States", "State Name MisMatches with Expected.Exp-"+expVal[i-1]+" Actual="+stateName,1);
					}

				}
				//validation to check if the state selected on client main page is already checked
				String stateChecked = Globals.testSuiteXLS.getCellData_fromTestData("Account_State");
				List<WebElement> stateChck =  Globals.driver.findElements(By.xpath("//input[@type='checkbox']"));
				for(int i=0;i<57;i++){
					state = availableState.get(i);
					stateName =  state.getText();
					if(stateName.contains(stateChecked)){
						if(stateChck.get(i).isSelected()){
						sc.STAF_ReportEvent("Pass", "Available States", "State Name is selected = "+stateName,0);
					}else{
						sc.scrollIntoView(state);
						sc.STAF_ReportEvent("Fail", "Available States", "State Name is not selected "+stateName,1);
					}
					}
				}
				//validation of all checkbox being selected
				sc.clickWhenElementIsClickable("availableStates_selectAll_btn", timeOutInSeconds);
				List<WebElement> stateChk =  Globals.driver.findElements(By.xpath("//input[@type='checkbox']"));
				for(int i=0;i<57;i++){
					state = availableState.get(i);
					stateName =  state.getText();
					stateName= stateName.replace("−", "-");
					if(stateChk.get(i).isSelected()){
						sc.STAF_ReportEvent("Pass", "Select All", "State Name is selected = "+stateName,0);
					}else{
						sc.scrollIntoView(state);
						sc.STAF_ReportEvent("Fail", "Select All", "State Name is not selected when Select All button is clicked-"+stateName,1);
					}

				}

				//validating None checkbox being selected
				sc.clickWhenElementIsClickable("availableStates_selectNone_btn", timeOutInSeconds);
				stateChk =  Globals.driver.findElements(By.xpath("//input[@type='checkbox']"));
				for(int i=0;i<57;i++){
					state = availableState.get(i);
					stateName =  state.getText();
					stateName= stateName.replace("−", "-");
					if(stateChk.get(i).isSelected() == false){
						sc.STAF_ReportEvent("Pass", "Select None", "State Name is DeSelected when None Button is clicked = "+stateName,0);
					}else{
						sc.scrollIntoView(state);
						sc.STAF_ReportEvent("Fail", "Select None", "State Name is not DeSelected when Select None button is clicked-"+stateName,1);
					}

				}

				//validation of only single state being selected
				sc.clickWhenElementIsClickable("availableStates_selectDefState_btn", timeOutInSeconds);
				String expState= Globals.driver.findElement(LocatorAccess.getLocator("availableStates_selectDefState_btn")).getText();
				expState = expState.replace("Select","").replace("Only", "").trim();
				stateChk =  Globals.driver.findElements(By.xpath("//input[@type='checkbox']"));
				int stateFound = 0;
				for(int i=0;i<57;i++){
					state = availableState.get(i);
					stateName =  state.getText();
					stateName= stateName.replace("−", "-");
					if(stateChk.get(i).isSelected() ){
						stateFound=stateFound+1;

						if(stateName.contains(expState)){
							sc.scrollIntoView(state);

							sc.STAF_ReportEvent("Pass", "Select State", "ONLY State Name is Selected when State Button is clicked = "+stateName,1);
						}
					}

				}	
				if(stateFound != 1){
					sc.STAF_ReportEvent("Fail", "Select State", "Multiple States has been selected.Total="+stateFound,0);
				}

				retval=Globals.KEYWORD_PASS;

			}


		}catch (Exception e) {
			log.error("Method-newAssociatedAccount | Unable to setup associated account | Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to configure custom fields for a client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String ConfigureCustomFields() throws Exception{
		APP_LOGGER.startFunction("ConfigureCustomFields");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		String stepName = "Other Configurations Tab";
		sc.clickWhenElementIsClickable("otherConfig_edit_btn",(int)  timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("otherConfig_titleForVolunteerOrdering_txt", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail",stepName,"Page hasnt loaded",1);
			throw new Exception(stepName+"Page hasnt loaded");
		}

		int noOfCustomFields = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfCustomFieldsReq"));

		String customFieldName="";
		String customFieldReq="";
		String customFieldDisplayed="";
		String customFieldLOV = "";

		for(int i=1;i<=noOfCustomFields;i++){
			customFieldName = Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i);
			customFieldReq = Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i+"_Required");
			customFieldDisplayed= Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i+"_Displayed");
			customFieldLOV= Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i+"_LOV");

			sc.setValueJsChange("otherConfig_clientConfigName"+i+"_txt", customFieldName);
			if(customFieldReq.equalsIgnoreCase("Yes")){
				sc.checkCheckBox("otherConfig_required"+i+"_chk");
			}else{
				sc.uncheckCheckBox("otherConfig_required"+i+"_chk");
			}

			if(customFieldDisplayed.equalsIgnoreCase("Yes")){
				sc.checkCheckBox("otherConfig_display"+i+"_chk");
			}else{
				sc.uncheckCheckBox("otherConfig_display"+i+"_chk");
			}



			if(!customFieldLOV.isEmpty()){

				String lovEditLocator = "//*[@id='adminRoot']/div[4]/section/form/fieldset/div[3]/div[2]/div[4]/div[2]/div["+i+"]/div[4]/a";
				Globals.driver.findElement(By.xpath(lovEditLocator)).click();

				tempRetval = sc.waitforElementToDisplay("otherConfig_LOVList_txt",timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail",stepName,"Unable to configure LOV for Custom fields",1);
					return retval;
				}

				String[] indvLOV = customFieldLOV.split(";");
				WebElement lovSetterArea = sc.createWebElement("otherConfig_LOVList_txt");
				lovSetterArea.clear();

				int noOfLoV = indvLOV.length;

				if(noOfLoV > 0){
					for( int j =0; j< noOfLoV ;j++){
						lovSetterArea.sendKeys(indvLOV[j]);
						if(j !=indvLOV.length-1 ){
							lovSetterArea.sendKeys(Keys.ENTER);
						}

					}
					sc.clickWhenElementIsClickable("otherConfig_OK_btn", timeOutInSeconds);

				}
			}

			tempRetval = sc.waitforElementToDisplay("otherConfig_titleForVolunteerOrdering_txt", 2);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Custom Field "+ i+" successfully configured", 0);
			}else{
				sc.STAF_ReportEvent("Fail", stepName, "Custom Field "+ i+" NOT Configured", 1);
			}


		}

		sc.setValueJsChange("otherConfig_titleForVolunteerOrdering_txt", Globals.VV_InvitationTitle);
		sc.setValueJsChange("otherConfig_volunteerInstruction_txt", Globals.VV_VolunterInstructions);

		sc.clickWhenElementIsClickable("otherConfig_volunteerInstruction_txt", timeOutInSeconds);

		sc.scrollIntoView("otherConfig_titleForVolunteerOrdering_txt");
		tempRetval = sc.waitforElementToDisplay("otherConfig_Save_btn", timeOutInSeconds);
		sc.clickWhenElementIsClickable("otherConfig_Save_btn", timeOutInSeconds);
		
		for(int i=1;i<=noOfCustomFields;i++){
		String temp=Globals.driver.findElement(LocatorAccess.getLocator("otherConfig_clientConfigName"+i+"_txt")).getAttribute("value");
			if(temp.equalsIgnoreCase("")||temp.isEmpty()){
				sc.STAF_ReportEvent("Fail", stepName, "Failed to retail values after saving", 1);
				retval = Globals.KEYWORD_FAIL;
			}
			else{
				sc.STAF_ReportEvent("Pass", stepName, "Custom Fields configured after saving", 1);
			}
		}
		String titleForVolunteerOrdering=Globals.driver.findElement(LocatorAccess.getLocator("otherConfig_titleForVolunteerOrdering_txt")).getAttribute("value");
		String volunteerInstruction=Globals.driver.findElement(LocatorAccess.getLocator("otherConfig_volunteerInstruction_txt")).getAttribute("value");
		if(titleForVolunteerOrdering.equalsIgnoreCase("") || volunteerInstruction.equalsIgnoreCase("") || titleForVolunteerOrdering.isEmpty() || volunteerInstruction.isEmpty() )
		{
			sc.STAF_ReportEvent("Fail", stepName, "Failed to retail values after saving", 1);
			retval = Globals.KEYWORD_FAIL;
			
		}
		else{
			sc.STAF_ReportEvent("Pass", stepName, "Custom Fields configured after saving", 1);
		}
		
		tempRetval = sc.waitforElementToDisplay("otherConfig_edit_btn", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", stepName, "Custom Fields configured ", 1);
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", stepName, "Unable to configure", 1);
		}


		return retval;
	}
	
	/**************************************************************************************************
 	 * Method to configure EditClientFeatures ClientViews Check Box
 	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
 	 * @author Psave
 	 * @throws Exception
 	 ***************************************************************************************************/
	
	public static String ClientViewssettings() throws Exception{
 		APP_LOGGER.startFunction("ClientViewssettings");
 		String retval = Globals.KEYWORD_FAIL;
 		String tempRetval = Globals.KEYWORD_FAIL;
 		int timeOutInSeconds = 20;
 		try {
 			String stepName = "EditClientFeatures ClientViews Check Box";
 			tempRetval = sc.waitforElementToDisplay("otherConfig_ClientFeatures_link", timeOutInSeconds);
 			
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
 				throw new Exception(stepName+"Page hasnt loaded");
 			}
 			sc.clickWhenElementIsClickable("otherConfig_ClientFeatures_link",(int)  timeOutInSeconds);

 			tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_ClientFeature_lable",  timeOutInSeconds);
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
 				throw new Exception(stepName+"Page hasnt loaded");
 			}
 			sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_btn",(int)  timeOutInSeconds);

 			tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_ClientViews_lable", timeOutInSeconds);
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 				sc.STAF_ReportEvent("Fail",stepName,"Client Views section has not been loaded",1);
 				throw new Exception(stepName+"Client Views section has not been loaded");
 			}
 			sc.checkCheckBox("otherConfig_EditClientFeatures_ClientViews_chks");
 			sc.scrollIntoView("otherConfig_EditClientFeatures_ClientViews_chks");
 			String clientViewscheckboxReq;
 			clientViewscheckboxReq = Globals.testSuiteXLS.getCellData_fromTestData("ClientViewscheckbox");
 			if (clientViewscheckboxReq.equalsIgnoreCase("Yes")){
 				tempRetval = sc.checkCheckBox("otherConfig_EditClientFeatures_ClientViews_chks");
 				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 					sc.STAF_ReportEvent("Pass", stepName, "User has been checked clientViews check box sucessfully", 1);
 				}else{
 					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been checked clientViews check box un-sucessfully", 1);
 					throw new Exception(stepName+"User has NOT been checked clientViews check box un-sucessful");
 				}
 				
 			}else{
 				tempRetval = sc.uncheckCheckBox("otherConfig_EditClientFeatures_ClientViews_chks");
 				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 					sc.STAF_ReportEvent("Pass", stepName, "User has been Unchecked clientViews un-check box sucessful", 1);
 				}else{
 					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been Unchecked clientViews un-check box un-sucessfully", 1);
 					throw new Exception(stepName+"User has NOT been Unchecked clientViews un-check box un-sucessfully");
 				}
 				
 			}
 			
 			sc.waitforElementToDisplay("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
 			sc.STAF_ReportEvent("Pass", stepName, "User has updated the check box of clientViews sucessfully", 1);
 			sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
 			retval = Globals.KEYWORD_PASS;
        }catch(Exception e){
        		sc.STAF_ReportEvent("Fail","Verifying the clientViews Check box", "Unable to Check clientViews check box sucessfully", 1);
               throw e;
        }
        return retval;
      }

	/**************************************************************************************************
 	 * Method to  Aes Encryption Check Box setting
 	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
 	 * @author Psave
 	 * @throws Exception
 	 ***************************************************************************************************/
	
	public static String AESEncryptionsettings() throws Exception{
 		APP_LOGGER.startFunction("AESEncryptionsettings");
 		String retval = Globals.KEYWORD_FAIL;
 		String tempRetval = Globals.KEYWORD_FAIL;
 		int timeOutInSeconds = 20;
 		try {
 			String stepName = "EditClientFeatures AES Encryption Check Box";
 			tempRetval = sc.waitforElementToDisplay("otherConfig_ClientFeatures_link", timeOutInSeconds);
 			
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
 				throw new Exception(stepName+"Page hasnt loaded");
 			}
 			sc.clickWhenElementIsClickable("otherConfig_ClientFeatures_link",(int)  timeOutInSeconds);

 			tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_ClientFeature_lable",  timeOutInSeconds);
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
 				throw new Exception(stepName+"Page hasnt loaded");
 			}
 			sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_btn",(int)  timeOutInSeconds);

 			sc.checkCheckBox("otherConfig_EditClientFeatures_AesEncryption_chks");
 			sc.scrollIntoView("otherConfig_EditClientFeatures_AesEncryption_chks");
 			String AESEncrcheckboxReq;
 			AESEncrcheckboxReq = Globals.testSuiteXLS.getCellData_fromTestData("Seamless");
 			if (AESEncrcheckboxReq.equalsIgnoreCase("SM_Cons_AESEnSSN")){
 				tempRetval = sc.checkCheckBox("otherConfig_EditClientFeatures_AesEncryption_chks");
 				//retval=sc.setValueJsChange("otherConfig_EditClientFeatures_AesEncryption_txt", "xO6gI5TI3Uosl/VA0f/NNiW+SgGjbjvD03ORTZ21Dtg=");
 				retval=sc.setValueJsChange("otherConfig_EditClientFeatures_AesEncryption_txt", "K3gN0LghjGUM4DMT/POlsCtwViMyCz2ZNsxFOc29vXQ=");
 				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS) && retval.equalsIgnoreCase(Globals.KEYWORD_PASS) ){
 					sc.STAF_ReportEvent("Pass", stepName, "User has set up AES encrytion key", 1);
 				}else{
 					sc.STAF_ReportEvent("Fail", stepName, "User has NOT set up AES encrytion key", 1);
 					throw new Exception(stepName+"User has NOT set up AES encrytion key");
 				}
 				
 			}else{
 				tempRetval = sc.uncheckCheckBox("otherConfig_EditClientFeatures_AesEncryption_chks");
 				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 					sc.STAF_ReportEvent("Pass", stepName, "User has been Unchecked AES Encrytion key sucessfully", 1);
 				}else{
 					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been Unchecked AES Encrytion key", 1);
 					throw new Exception(stepName+"User has NOT been Unchecked AES Encrytion key");
 				}
 				
 			}
 			
 			sc.waitforElementToDisplay("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
 			sc.STAF_ReportEvent("Pass", stepName, "User has updated the check box of AES Encrytion key", 1);
 			sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
 			retval = Globals.KEYWORD_PASS;
        }catch(Exception e){
        		sc.STAF_ReportEvent("Fail","Verifying the AES Encrytion key Check box", "Unable to Check AES Encrytion key check box sucessfully", 1);
               throw e;
        }
        return retval;
      }

	/**************************************************************************************************
	 * Method to configure client views for a client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author Psave
	 * @throws Exception
	 ***************************************************************************************************/
	 public static String ConfigureClientViews() throws Exception{
		APP_LOGGER.startFunction("ConfigureCustomViews");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String titleTestData = null;
		String viewDescTestData=null;
		String viewTestData=null;
		String InstructionTestData=null;
		String actualViewText=null;
		String actualViewDescText=null;
		int rowViewTbl=0;
		int i;
		
		try{ 
		String stepName = "Other Configurations - Client Views Tab";
		tempRetval = sc.waitforElementToDisplay("otherConfig_ClientViews_link", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail","Other Configurations","Page hasnt loaded",1);
			throw new Exception("Other Configurations"+"Page hasnt loaded");
		}
		
		sc.clickWhenElementIsClickable("otherConfig_ClientViews_link",(int)  timeOutInSeconds);

		tempRetval = sc.waitforElementToDisplay("otherConfig_ClientViews_AddView_btn",  timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
			throw new Exception(stepName+"Page hasnt loaded");
		}
		
		// verify by default setting like all fields disabled, empty fields, List View table etc 
		//String[] viewElements = {"otherConfig_ClientViews_VolOrderViewsDisplay_chks","otherConfig_ClientViews_VolOrderViewsTitle_txt","otherConfig_ClientViews_VolOrderViewsTitle_txt","otherConfig_ClientViews_AddView_btn"};
		String[] viewElements = {"otherConfig_ClientViews_AddView_btn"};
		boolean  enbledFlag = false,selectedFlag = false;
		String verifyTxt=null;
		for(i=0;i<=(viewElements.length-1);i++){
			enbledFlag=Globals.driver.findElement(LocatorAccess.getLocator(viewElements[i])).isEnabled();
			if(enbledFlag==true){
				break;
			}
//			if(i==0){
//				selectedFlag=Globals.driver.findElement(LocatorAccess.getLocator(viewElements[i])).isSelected();
//				if(selectedFlag==true){
//					break;
//				}
//			}
//			if(i==1 || i==2){
//				verifyTxt=Globals.driver.findElement(LocatorAccess.getLocator(viewElements[i])).getAttribute("value");
//				if(verifyTxt.equalsIgnoreCase("")==false){
//					sc.STAF_ReportEvent("Fail", stepName, " For new account, "+viewElements[i]+" field value is not blank", 1);
//					throw new Exception(stepName+" For new account, "+viewElements[i]+" field value is not blank");
//				}
//			}
		}
		
		WebElement listViewtbl=sc.createWebElement("otherConfig_ClientViews_ListViews_tbl");
		rowViewTbl=sc.getRowCount_tbl(listViewtbl); //verify no client view row available for new a/c 
		List<WebElement> headerList = listViewtbl.findElements(By.xpath("./thead/tr/th"));
        boolean viewTblHeader=false;
		String[] expectedHeaders = {"View Name","","Description","Action"};
		String headerText;
		i=0;
		for(WebElement element : headerList){  //verify the headers of list of client view table
			headerText = element.getText();
			if(headerText.equalsIgnoreCase(expectedHeaders[i])){
				viewTblHeader=true;
			}else{
				viewTblHeader=false;
				//break;
			}
			i++;
		}
		
		if((enbledFlag != false)){
			sc.STAF_ReportEvent("Fail", stepName, expectedHeaders[i]+" field in Client views page are not disabled when Client Views in View Mode",1);
			String abc="";
			throw new Exception(expectedHeaders[i]+" Client Views fields are not disabled when Vlient View is in View mode");
		}/*else if(selectedFlag != false){
		
			sc.STAF_ReportEvent("Fail", stepName, " Display Views in Volunteer Ordering checkbox is not unchecked for new account",1);
			throw new Exception("Display Views in Volunteer Ordering checkbox is not unchecked for new account");
		}*/else if((viewTblHeader != true) || (rowViewTbl != 0)){
			sc.STAF_ReportEvent("Fail", stepName," Client views list is not empty or list headres are change when Client Views in View Mode",1);
			throw new Exception("Client views list is not empty or list headres are change when Client Views in View Mode");
		}else{
			 retval = Globals.KEYWORD_PASS;
			 sc.STAF_ReportEvent("Pass", stepName, " All the fields are disabled and having Blank value when Client Views is in View Mode", 1);
		}
		
		//verify after click on edit button , update and cancel button display
		sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_btn",(int)  timeOutInSeconds);
		retval = sc.waitforElementToDisplay("otherConfig_ClientViews_update_btn",  timeOutInSeconds);
		tempRetval=sc.isDisplayed("otherConfig_ClientViews_cancel_btn");
		if((tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)) || (retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)) ){
			sc.STAF_ReportEvent("Fail", stepName, " After Click on Edit button , Cancel or update button is not getting displayed", 1);
			throw new Exception(stepName+" After Click on Edit button , Cancel or update button is not getting displayed");
		}
		
		//verify the error message on fields and clicking on update button when view is blank
		sc.clickWhenElementIsClickable("otherConfig_ClientViews_AddView_btn",(int)  timeOutInSeconds);
		String ActErrMsg = listViewtbl.findElement(By.xpath("//tr/td[1]/span[@class='validationMessage']")).getText().trim();
		if(ActErrMsg.equalsIgnoreCase("This field is required.")) {
			sc.STAF_ReportEvent("Pass", stepName, " Error message is getting displayed when view name is Blank", 1);
		}else{
			sc.STAF_ReportEvent("Fail", stepName, " Error message is not getting displayed when view name is Blank", 1);
		}
		sc.clickWhenElementIsClickable("otherConfig_ClientViews_update_btn",(int)  timeOutInSeconds);
		ActErrMsg = Globals.driver.findElement(By.xpath("//*[@id='validationDialog']/span[@class='message']")).getText().trim();
		if(ActErrMsg.equalsIgnoreCase("Please review the information that you entered: Name")) {
			sc.STAF_ReportEvent("Pass", stepName, " Warning message is getting displayed when view name is Blank", 1);
			retval=sc.waitforElementToDisplay("otherConfig_ClientViews_warningMsgCloseBtn",  timeOutInSeconds);
			if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, " Warning close button is not getting displayed", 1);
				return retval;
			}else{
				 retval = Globals.KEYWORD_PASS;
				 //Verify error message displayed on field as well as after click on update button when view contain ; and , character
				 String[] mulValue = {";",","};
				 for(i=0;i<=(mulValue.length-1);i++){
					 sc.clickWhenElementIsClickable("otherConfig_ClientViews_warningMsgCloseBtn",(int)  timeOutInSeconds);
					 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[1]/input[@id='Name']")).sendKeys(mulValue[i]);
					 ActErrMsg = listViewtbl.findElement(By.xpath("//tr/td[1]/span[@class='validationMessage']")).getText().trim();
						if(ActErrMsg.equalsIgnoreCase("Please check this value.")) {
							sc.STAF_ReportEvent("Pass", stepName, " Error message is getting displayed when view name has contain "+mulValue[i], 1);
						}else{
							sc.STAF_ReportEvent("Fail", stepName, " Error message is not getting displayed when view name has contain "+mulValue[i], 1);
						}
					 sc.clickWhenElementIsClickable("otherConfig_ClientViews_update_btn",(int)  timeOutInSeconds);
					 
					 ActErrMsg = Globals.driver.findElement(By.xpath("//*[@id='validationDialog']/span[@class='message']")).getText().trim();
					 if(ActErrMsg.equalsIgnoreCase("Please review the information that you entered: Name")) {
							sc.STAF_ReportEvent("Pass", stepName, " Warning message is getting displayed when view name has contain "+mulValue[i], 1);
							retval=sc.waitforElementToDisplay("otherConfig_ClientViews_warningMsgCloseBtn",  timeOutInSeconds);
							if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
								sc.STAF_ReportEvent("Fail", stepName, " Warning close button is not getting displayed view name has contain "+mulValue[i], 1);
								return retval;
							}else{
								sc.clickWhenElementIsClickable("otherConfig_ClientViews_warningMsgCloseBtn",(int)  timeOutInSeconds);
								Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[1]/input[@id='Name']")).clear();
							}
					 }else{
							retval=Globals.KEYWORD_FAIL;
						    sc.STAF_ReportEvent("Fail", stepName, " Warning message is not getting displayed when view name has contain "+mulValue[i], 1);
							return retval;   					
					 }	
					 
				 } 
							 
				 // validate the limitation of data fields, save the data and verified MER9301
				 //titleTestData = RandomStringUtils.randomAscii(50);
				 viewDescTestData=RandomStringUtils.randomAscii(200);
				 viewTestData="ABCD !@#$%^&* <>-.?/"+RandomStringUtils.randomAlphanumeric(30);
				 //InstructionTestData=RandomStringUtils.randomAscii(255);
				 //Globals.driver.findElement(LocatorAccess.getLocator("otherConfig_ClientViews_VolOrderViewsTitle_txt")).sendKeys(titleTestData+"A");
				 //Globals.driver.findElement(LocatorAccess.getLocator("otherConfig_ClientViews_VolOrderViewsInstruction_txt")).sendKeys(InstructionTestData+"A");
				 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[1]/input[@id='Name']")).sendKeys(viewTestData+"A");
				 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[3]/input[@id='Description']")).sendKeys(viewDescTestData+"A");
				 
				 sc.clickWhenElementIsClickable("otherConfig_ClientViews_update_btn",(int)  timeOutInSeconds);
				 Thread.sleep(3000);
				 //String[] viewPageTestData = {titleTestData,InstructionTestData,viewTestData,viewDescTestData};
				 String[] viewPageTestData = {viewTestData,viewDescTestData};
				 actualViewText=sc.getCellData_tbl(listViewtbl, 1, 1);
				 actualViewDescText=sc.getCellData_tbl(listViewtbl, 1, 3);
				 //String[] viewPageActualData = {"otherConfig_ClientViews_VolOrderViewsTitle_txt","otherConfig_ClientViews_VolOrderViewsInstruction_txt","otherConfig_ClientViews_VolOrderViewsDisplay_chks","otherConfig_ClientViews_AddView_btn"};
				 String[] viewPageActualData = {"otherConfig_ClientViews_AddView_btn"};
				 verifyTxt=null;
				 enbledFlag=false;
				 for(i=0;i<=(viewPageActualData.length-1);i++){
					 enbledFlag=Globals.driver.findElement(LocatorAccess.getLocator(viewPageActualData[i])).isEnabled();
					 if(enbledFlag==true){
						 break;
					 }
					 /*if(i==0 || i==1){
						 verifyTxt=Globals.driver.findElement(LocatorAccess.getLocator(viewPageActualData[i])).getAttribute("value"); 
						 if(verifyTxt.equals(viewPageTestData[i])){
							 sc.STAF_ReportEvent("Pass", stepName, "After saving Views, "+viewPageActualData[i]+" field actual text is matching with Expected text", 1); 
						 }else{
							 sc.STAF_ReportEvent("Fail", stepName, "After saving Views, "+viewPageActualData[i]+" field actual text is not matching with Expected text", 1);							 
						 }
					 }*/
				 }
				 
				 if(actualViewText.equals(viewTestData) && actualViewDescText.equals(viewDescTestData) && (enbledFlag == false)){
					 sc.STAF_ReportEvent("Pass", stepName, "After saving Views, View name field actual text is matching with Expected text", 1);
					 sc.STAF_ReportEvent("Pass", stepName, "After saving Views, View Description field actual text is matching with Expected text", 1);
					 sc.STAF_ReportEvent("Pass", stepName, "After saving Views, all views page fields are disabled ", 1);					 
			      }else{
			    	  retval = Globals.KEYWORD_FAIL;
			    	  if(actualViewText.equals(viewTestData)==false){
			    		  sc.STAF_ReportEvent("Fail", stepName, "After saving Views, View name field actual text is not matching with expected text", 1);
			    	  }else if(actualViewDescText.equals(viewDescTestData)==false){
			    		  sc.STAF_ReportEvent("Fail", stepName, "After saving Views, View description field actual text is not matching with expected text", 1);
			    	  }else{
					       sc.STAF_ReportEvent("Fail", stepName, "After saving Views, "+viewPageActualData[i]+" field is not disabled", 1);
			    	  }
					   return retval; 
			      }
				 
				 // check for duplicate - exact match
				 sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_btn",(int)  timeOutInSeconds);
				 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[1]/input[@id='Name']")).clear();
				 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[1]/input[@id='Name']")).sendKeys("TEST B");
				 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[3]/input[@id='Description']")).clear();
				 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[3]/input[@id='Description']")).sendKeys("Description - TEST B");
				 sc.clickWhenElementIsClickable("otherConfig_ClientViews_AddView_btn",(int)  timeOutInSeconds);
				 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[1]/input[@id='Name']")).sendKeys("TEST B");
				 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[3]/input[@id='Description']")).sendKeys("Description - TEST B");
				 ActErrMsg = listViewtbl.findElement(By.xpath("//tr/td[1]/span[@class='validationMessage']")).getText().trim();
				 String ActErrMsg1=listViewtbl.findElement(By.xpath("//tr[2]/td[1]/span[@class='validationMessage']")).getText().trim();
				 sc.clickWhenElementIsClickable("otherConfig_ClientViews_update_btn",(int)  timeOutInSeconds);
				 String ActErrMsg2 = Globals.driver.findElement(By.xpath("//*[@id='validationDialog']/span[@class='message']")).getText().trim();
				 if(ActErrMsg.equalsIgnoreCase("Name is a duplicate") && ActErrMsg1.equalsIgnoreCase("Name is a duplicate") && ActErrMsg2.equalsIgnoreCase("Please review the information that you entered: Name") ) {
						sc.STAF_ReportEvent("Pass", stepName, "Error message is getting displayed when view name has duplicate value", 1);
						sc.clickWhenElementIsClickable("otherConfig_ClientViews_warningMsgCloseBtn",(int)  timeOutInSeconds);
					}else{
						sc.STAF_ReportEvent("Fail", stepName, "Error message is not getting displayed when view name has duplicate value", 1);
				}
				 
				// check for duplicate - trim spaces and case insensitive 
				 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[1]/input[@id='Name']")).clear();
				 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[1]/input[@id='Name']")).sendKeys(" test B	");
				 sc.clickWhenElementIsClickable("otherConfig_ClientViews_update_btn",(int)  timeOutInSeconds);
				 ActErrMsg2 = Globals.driver.findElement(By.xpath("//*[@id='validationDialog']/span[@class='message']")).getText().trim();
				 if(ActErrMsg2.contains("Please review the information that you entered: Name") ) {
						sc.STAF_ReportEvent("Pass", stepName, "Error message is getting displayed when view name has duplicate value with leading-ending space and case-insensative text", 1);
						sc.clickWhenElementIsClickable("otherConfig_ClientViews_warningMsgCloseBtn",(int)  timeOutInSeconds);
					}else{
						sc.STAF_ReportEvent("Fail", stepName, "Error message is not getting displayed when view name has duplicate value with leading-ending space and case-insensative text", 1);
				} 
				 
				// check  list of Views gets sorted alphabetically
				 String[] expectedViewName = {"test !","test 1","TEST a","TEST B"};
				 for(i=0;i<=2;i++){
					 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[1]/input[@id='Name']")).clear();
					 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[1]/input[@id='Name']")).sendKeys(" "+expectedViewName[i]+" 		");
					 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[3]/input[@id='Description']")).clear();
					 Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[3]/input[@id='Description']")).sendKeys("Description - "+expectedViewName[i]);
					 sc.clickWhenElementIsClickable("otherConfig_ClientViews_AddView_btn",(int)  timeOutInSeconds); 
				 }
				 Globals.driver.findElement(By.xpath("//tr/td[4]/button/img[@src='/Images/delete_red_16.png']")).click();
				 //sc.checkCheckBox("otherConfig_ClientViews_VolOrderViewsDisplay_chks");				 
				 sc.clickWhenElementIsClickable("otherConfig_ClientViews_update_btn",(int)  timeOutInSeconds);
				 Thread.sleep(3000);
				 listViewtbl=sc.createWebElement("otherConfig_ClientViews_ListViews_tbl");
				 rowViewTbl = sc.getRowCount_tbl(listViewtbl);
				 String colVal = "";
				 boolean mismatchFound = false;
				
				 for(i=1;i<=rowViewTbl;i++){
					
					 colVal =sc.getCellData_tbl(listViewtbl, i, 1);
					
					if(!colVal.contains(expectedViewName[i-1])){
						mismatchFound = true;
						sc.scrollIntoView(listViewtbl);
						break;
					}
				 }
				if(mismatchFound){
					retval = Globals.KEYWORD_FAIL;
					sc.STAF_ReportEvent("Fail", stepName, "Mismatch found in Client View list name"+colVal,1);
				    return retval; 
				}else{
					sc.STAF_ReportEvent("Pass", stepName, "Client views list has been verified and gets sorted alphabetically", 1);
					retval = Globals.KEYWORD_PASS;
				}
				
				//cancel button functionality
				sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_btn",(int)  timeOutInSeconds);
				sc.clickWhenElementIsClickable("otherConfig_ClientViews_AddView_btn",(int)  timeOutInSeconds); 
				Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr[1]/td[1]/input[@id='Name']")).sendKeys("test cancel button");
				sc.clickWhenElementIsClickable("otherConfig_ClientViews_cancel_btn",(int)  timeOutInSeconds);
				List<WebElement> rows = Globals.driver.findElements(By.xpath("//table[@class='results selectClick']/tbody/tr"));
				rowViewTbl=rows.size(); 
      			retval = sc.isDisplayed("otherConfig_EditClientFeatures_edit_btn");
				//tempRetval=sc.isEnabled("otherConfig_ClientViews_VolOrderViewsDisplay_chks");
				//selectedFlag=Globals.driver.findElement(LocatorAccess.getLocator("otherConfig_ClientViews_VolOrderViewsDisplay_chks")).isSelected();	
				//if (rowViewTbl==4 && (retval.equalsIgnoreCase(Globals.KEYWORD_PASS)) && selectedFlag==true && (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)) ){
				if (rowViewTbl==4 && (retval.equalsIgnoreCase(Globals.KEYWORD_PASS)) ){
					sc.STAF_ReportEvent("Pass", stepName, "Cancel button functionality working correctly", 1);
					retval = Globals.KEYWORD_PASS;
				}else{
					retval = Globals.KEYWORD_FAIL;
					sc.STAF_ReportEvent("Fail", stepName, "Cancel button functionality not working correctly",1);
				    return retval; 
				}
				
				//delete client views - verify delete functionality and then we are adding 2 client view for new account 
				sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_btn",(int)  timeOutInSeconds);
				for(i=1;i<=3;i++){
					 Globals.driver.findElement(By.xpath("//tr[1]/td[4]/button/img[@src='/Images/delete_red_16.png']")).click();
				}
				String accountName =  Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
				sc.clickWhenElementIsClickable("otherConfig_ClientViews_AddView_btn",(int)  timeOutInSeconds);
				for(i=1;i<=2;i++){
					Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+i+"]/td[1]/input[@id='Name']")).clear();
					Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+i+"]/td[1]/input[@id='Name']")).sendKeys(accountName+"_View_"+i);
					Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+i+"]/td[3]/input[@id='Description']")).clear();
					Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+i+"]/td[3]/input[@id='Description']")).sendKeys(accountName+"_View_"+i+"_Des");
				}
				
				/*Globals.driver.findElement(LocatorAccess.getLocator("otherConfig_ClientViews_VolOrderViewsTitle_txt")).clear();
				Globals.driver.findElement(LocatorAccess.getLocator("otherConfig_ClientViews_VolOrderViewsTitle_txt")).sendKeys(accountName+" -  Title ");
				Globals.driver.findElement(LocatorAccess.getLocator("otherConfig_ClientViews_VolOrderViewsInstruction_txt")).clear();
				Globals.driver.findElement(LocatorAccess.getLocator("otherConfig_ClientViews_VolOrderViewsInstruction_txt")).sendKeys(accountName+" -  Instructions for Volunteer Ordering ");
				*/
				sc.clickWhenElementIsClickable("otherConfig_ClientViews_update_btn",(int)  timeOutInSeconds);
								
				retval = sc.waitTillElementDisplayed("otherConfig_EditClientFeatures_edit_btn",60);
				rows = Globals.driver.findElements(By.xpath("//div[3]/div[2]/table[@class='results selectClick']/tbody/tr"));
				rowViewTbl=rows.size(); 
				if (rowViewTbl==2 && (retval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
					sc.STAF_ReportEvent("Pass", stepName, "Delete button functionality working correctly", 1);
					sc.STAF_ReportEvent("Pass", stepName, "2 client views has been added succussfully for account - "+accountName, 1);
					retval = Globals.KEYWORD_PASS;
				}else{
					retval = Globals.KEYWORD_FAIL;
					sc.STAF_ReportEvent("Fail", stepName, "Delete button functionality not working correctly",1);
				    return retval; 
				}
				return retval;
		   }
			
			
		}else{
			retval=Globals.KEYWORD_FAIL;
			sc.STAF_ReportEvent("Fail", stepName, "Warning message is not getting displayed when view name is Blank", 1);
		}
			
		}catch (Exception e){
			log.error("Unable to add and verify client view page for new account | Exception - " + e.toString());
			throw e;
		}
        return retval;
	}
	 
	/**************************************************************************************************
	 * Method to configure client views for a client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author Psave
	 * @throws Exception
	 ***************************************************************************************************/
	 public static String ClientViewUsers() throws Exception{
		
		 APP_LOGGER.startFunction("clientViewUsers");
     	 String retval = Globals.KEYWORD_FAIL;
		 int rowCount;
		 int i,adminRowcnt=0,nmUser=0;
		 				
		 try{
			 // verify for the account admin user and normal user should be present
			 navigateToUsersTab();
			 WebElement userGrid = Globals.driver.findElement(LocatorAccess.getLocator("users_userGrid_tbl"));
			 rowCount=sc.getRowCount_tbl(userGrid);
			 
      		 if(rowCount != Globals.INT_FAIL && rowCount > 1 ){
				int userCount = rowCount;
				log.info("Method - newUserCreation | User result grid displayed with " + userCount + " users(s)" );

				String userNameInUI,supUser;
			
				for(i= 1;i<=rowCount;i++){
			
					userNameInUI = sc.getCellData_tbl(userGrid, i, 4);
					if(userNameInUI.contains("Admin")){
						adminRowcnt=i;
					}
					supUser = sc.getCellData_tbl(userGrid, i, 1);
					
					if(supUser.equalsIgnoreCase("Yes") && !(userNameInUI.contains("Admin")) && !(userNameInUI.contains("Integration")) ){
						nmUser=i;
					}
				}
			 }

			if(adminRowcnt==0 || nmUser==0 ){
				sc.STAF_ReportEvent("Fail", "Client View-Users", "No users is found to verify the client view setting", 1);
				
				return Globals.KEYWORD_FAIL;
			}else{
				sc.STAF_ReportEvent("Pass", "Client View-Users", "Admin user and normal user are found to verify the client view setting", 1);
			}
			 
			// verify client view setting for Admin User - check only view mode
			Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+adminRowcnt+"]")).click();
			String viewModeText="All Views;";
			i=1;
			retval=viewModeVerifyText(i,viewModeText);
			WebElement screensht=Globals.driver.findElement(By.xpath("//*[@id='adminFooter']"));
			sc.scrollIntoView(screensht); 
			if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Client View-Users", "For Admin User, Client views section text is not matched. Expected Text :"+viewModeText, 1);
				throw new Exception("For Admin User, Client views section default text is not matched. Expected Text :"+viewModeText);
			}else{
				sc.STAF_ReportEvent("Pass", "Client View-Users", "For Admin User, Client views section text is matched. Expected Text :"+viewModeText, 1);
			}
			
						
			// verify client view setting - normal user - view mode and edit mode
			navigateToUsersTab();
			
			String accountName =  Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
			
			Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+nmUser+"]")).click();
			
     		int[] arrWebelementSize={1,4,1,3};
			String[] arreditModeText={"Candidates with no Views assigned;"+accountName+"_View_1;"+accountName+"_View_2","All Views;",accountName+"_View_1;"+accountName+"_View_2"};
			
			String[] arrviewModeText={"No Views are assigned.;",arreditModeText[0],arreditModeText[1],arreditModeText[2]};
			//String[] arrviewModeText={"Candidates with no Views assigned;------Views on This Account------;"+accountName+"_View_1;"+accountName+"_View_2","All Views;------Views on This Account------;","------Views on This Account------;"+accountName+"_View_1;"+accountName+"_View_2"};
			//String[] arrviewModeText={"No Views are assigned.",arreditModeText[0],arreditModeText[1],arreditModeText[2]};
			String[] arrViewModeText1={"No Views are assigned.;","Candidates with no Views assigned;------Views on This Account------;"+accountName+"_View_1;"+accountName+"_View_2","All Views;","------Views on This Account------;"+accountName+"_View_1;"+accountName+"_View_2"};
			for(int cnt=0;cnt<3;cnt++){
				retval=viewModeVerifyText(arrWebelementSize[cnt],arrViewModeText1[cnt]);
				if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					 sc.STAF_ReportEvent("Pass", "Client View-Users", "For normal client User, Client views section default text is matched with Expected Text :"+arrviewModeText[cnt], 1);
					 retval=editModeClientViewSet(arrviewModeText[cnt],arreditModeText[cnt]);
					 if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						 sc.STAF_ReportEvent("Fail", "Client View-Users", "For normal client User, Unable to set the client views :"+arreditModeText[cnt], 1);
						 throw new Exception("For normal client User, Unable to set the client views :"+arreditModeText[cnt]);
					 }else{
						 sc.STAF_ReportEvent("Pass", "Client View-Users", "For normal client User, Client views are able to set up for the user. Client Views list :"+arreditModeText[cnt], 1);
					 }
				}else{ 
				     sc.STAF_ReportEvent("Fail", "Client View-Users", "For normal client User, Client views section default text is not matched with Expected Text :"+arrviewModeText[cnt], 1);
				     throw new Exception("For Admin User, For normal client User, Client views section default text is not matched with Expected Text :"+arrviewModeText[cnt]);
				}
				sc.clickWhenElementIsClickable("newUser_create_btn", 20); 
			}
		
			
			retval=viewModeVerifyText(arrWebelementSize[3],arrViewModeText1[3]);
			if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Client View-Users", "For normal client User, Client views section default text is not matched. Expected Text :"+arrviewModeText[3], 1);
				throw new Exception("For Admin User, Client views section default text is not matched. Expected Text :"+arrviewModeText[3]);
			}else{ 
			    sc.STAF_ReportEvent("Pass", "Client View-Users", "For normal client User, Client views section default text is matched with Expected Text :"+arrviewModeText[3], 1); 
			}
			
		}catch (Exception e){
			log.error("Unable to set up client views to client users | Exception - " + e.toString());
			throw e;
		 }
         return retval;
	 
	 }
	 
	 /**************************************************************************************************
		 * Method to verify text in Edit mode for client views
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @throws Exception
	 ***************************************************************************************************/
	public static String editModeClientViewSet(String elementChecked, String elementNeedChecked) throws Exception{
		String retval=Globals.KEYWORD_FAIL;
		String actualTxt,idName;
		String[] arrElemtCheckd=elementChecked.split(";");
		String[] arrElemtNdCheckd=elementNeedChecked.split(";");
		List<WebElement> listViewElement=null;
		WebElement chkBoxExp=null;
		int i,j=0;
		boolean exptFlag=false;
		try{
            //verify the all views label under client views section
			sc.clickWhenElementIsClickable("associatedAccounts_edit_btn",20);
			WebElement screensht=Globals.driver.findElement(By.xpath("//*[@id='adminFooter']"));
			sc.scrollIntoView(screensht); 
			String accountName =  Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
			String[] expectedLabeltxt={"All Views","Candidates with no Views assigned","------Views on This Account------",accountName+"_View_1",accountName+"_View_2"};
			for(i=2 ; i<=6 ; i++){
				actualTxt=Globals.driver.findElement(By.xpath("//div[@class='areaPadTop05 areaPadLeft10 areaPadBottom05']//li["+i+"]")).getText().trim();
				if(actualTxt.equalsIgnoreCase(expectedLabeltxt[j])){
					exptFlag=true;
					j=j+1;
				}else{
					exptFlag=false;
					break;
				}
			}
			
			if(exptFlag==false){
			    sc.STAF_ReportEvent("Fail", "Client View-Users", "Edit Mode- views list labels are not match with expected", 1);
				throw new Exception("Client views User- Edit Mode- views list labels are not match with expected");
			}else{
				 sc.STAF_ReportEvent("Pass", "Client View-Users", "Edit Mode- views list labels are displayed as expected", 1);
			}
			
			//verify in edit mode which checkbox is selected and which are not
						
			listViewElement=Globals.driver.findElements(By.xpath("//div[@class='areaPadTop05 areaPadLeft10 areaPadBottom05']//input"));
			if(!(listViewElement.size()==4)){
				 sc.STAF_ReportEvent("Fail", "Client View-Users", "Edit Mode- client views checkboxes count should be 4 , actual we are getting as:"+listViewElement.size(), 1);
				 throw new Exception("Client views User- Edit Mode- client views checkboxes count mismatch");
			}else{
                 if(arrElemtCheckd[0].contains("No Views are assigned")){
                	 for(i=0;i<=3;i++){
 				     	idName=listViewElement.get(i).getAttribute("id").trim(); 
 				     	chkBoxExp=Globals.driver.findElement(By.xpath("//div[@class='areaPadTop05 areaPadLeft10 areaPadBottom05']//input[@id='"+idName+"']"));
 				     	retval=sc.isSelected(chkBoxExp);
 				     	if(!(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
							sc.STAF_ReportEvent("Fail", "Client View-Users", "Edit Mode- client views checkbox : "+idName+" is getting as selected when No Views are assigned. " , 1);
							throw new Exception("Client View-Users, Edit Mode- client views checkbox : "+idName+" should not be selected when No Views are assigned."); 
						}
 				     	retval=Globals.KEYWORD_PASS;
                	 }
                 }else if(arrElemtCheckd[0].equalsIgnoreCase("All Views")){
                	 chkBoxExp=Globals.driver.findElement(By.xpath("//div[@class='areaPadTop05 areaPadLeft10 areaPadBottom05']//input[@id='All Views']"));
                	 retval=sc.isSelected(chkBoxExp);
				     	if((retval.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
							sc.STAF_ReportEvent("Fail", "Client View-Users", "Edit Mode- client views checkbox : All Views- is unable to marked as checked" , 1);
							throw new Exception("Client View-Users, Edit Mode- client views checkbox : All Views- is unable to marked as checked"); 
						}else{
							 sc.STAF_ReportEvent("Pass", "Client View-Users", "Edit Mode- client views checkbox : All Views- is selected as per expected" , 1);
							 for(i=1;i<=3;i++){
								 idName=listViewElement.get(i).getAttribute("id").trim();
							     chkBoxExp=Globals.driver.findElement(By.xpath("//div[@class='areaPadTop05 areaPadLeft10 areaPadBottom05']//input[@id='"+idName+"' and  @disabled='']")); 
							     retval=sc.isDisplayed(chkBoxExp);
							     if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
								      sc.STAF_ReportEvent("Fail", "Client View-Users", "Edit Mode- client views checkbox : "+idName+" is not disabled mode when All Views is checked " , 1);
								      throw new Exception("Client View-Users, Edit Mode- client views checkbox : "+idName+" is not disabled mode when All Views is checked "); 
						     	 }else{
						     		   sc.STAF_ReportEvent("Pass", "Client View-Users", "Edit Mode- client views checkbox : "+idName+" disabled mode when All Views is checked" , 1);
						     	 }
							     
							 }
						}
				     	
                 }else{
                	  for(i=0;i<=3;i++){
				     	idName=listViewElement.get(i).getAttribute("id").trim();
				     	chkBoxExp=Globals.driver.findElement(By.xpath("//div[@class='areaPadTop05 areaPadLeft10 areaPadBottom05']//input[@id='"+idName+"']"));
				     	retval=sc.isSelected(chkBoxExp);
				     	if(ArrayUtils.contains(arrElemtCheckd, idName)){
							if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
							      sc.STAF_ReportEvent("Fail", "Client View-Users", "Edit Mode- client views checkbox : "+idName+" is unable to marked as checked " , 1);
							      throw new Exception("Client View-Users, Edit Mode- client views checkbox : "+idName+" is unable to marked as checked"); 
					     	 }else{
					     		   sc.STAF_ReportEvent("Pass", "Client View-Users", "Edit Mode- client views checkbox : "+idName+" is selected as per expected" , 1);
					     	 }
						}else{	
							  if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
								 sc.STAF_ReportEvent("Fail", "Client View-Users", "Edit Mode- client views checkbox : "+idName+" is marked as selected which is not as per expected" , 1);
								 throw new Exception("Client View-Users Edit Mode- client views checkbox : "+idName+" is marked as selected which is not as per expected");
					     	  }else{
					     		 sc.STAF_ReportEvent("Pass", "Client View-Users", "Edit Mode- client views checkbox : "+idName+" is not selected as per expected" , 1);
						      }	
						
						}
					
				      }
			      }
			}
			
			//code to select checkbox in edit mode as per expected result - arrElemtNdCheckd
			for(i=0;i<=3;i++){
				idName=listViewElement.get(i).getAttribute("id").trim(); 
				chkBoxExp=Globals.driver.findElement(By.xpath("//div[@class='areaPadTop05 areaPadLeft10 areaPadBottom05']//input[@id='"+idName+"']"));
				if(ArrayUtils.contains( arrElemtNdCheckd, idName)){
					retval=sc.checkCheckBox(chkBoxExp);
					if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail", "Client View-Users", "Edit Mode- client views checkbox : "+idName+" is not selected as per expected" , 1);
						throw new Exception("Client View-Users, Edit Mode- client views checkbox : "+idName+" is not selected as per expected"); 
			     	  }
				}else{
					retval=sc.uncheckCheckBox(chkBoxExp);
					if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail", "Client View-Users", "Edit Mode- client views checkbox : "+idName+" is unable to marked as unchecked " , 1);
           		        throw new Exception("Client View-Users, Edit Mode- client views checkbox : "+idName+" is unable to marked as unchecked");
			     	  } 	
				
				}
			}
			
	     }catch (Exception e){
			log.error("Unable to verify the text for client user in client view section | Exception - " + e.toString());
			throw e;
		 }	
		 return retval;
		
	}
	 
	 /**************************************************************************************************
		 * Method to verify text in view mode for client views
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @throws Exception
	 ***************************************************************************************************/
	public static String viewModeVerifyText(int elementSize, String expectedText) throws Exception{
		String retval=Globals.KEYWORD_FAIL;
		String[] arrViewModeText=expectedText.split(";");
		List<WebElement> listViewElement=null;
		List<WebElement> listViewElement1=null;
		int lSize = 0;
		try{
		  WebElement screensht=Globals.driver.findElement(By.xpath("//*[@id='adminFooter']"));
		  sc.scrollIntoView(screensht); 	
		  if(expectedText.contains("No Views are assigned.")){
			  listViewElement=Globals.driver.findElements(By.xpath("//div[@class='areaPadTop05 areaPadLeft10 areaPadBottom05']//li[1]"));

			  lSize=listViewElement.size();
			  
		  }else{
		      listViewElement=Globals.driver.findElements(By.xpath("//div[@class='areaPadTop05 areaPadLeft10 areaPadBottom05']//li/label"));
		      lSize=listViewElement.size();
		  }
		 
		  String actualText=null;
		  boolean viewFlag=false;
		  if(arrViewModeText.length==lSize){
			
			 for(int i =0; i<arrViewModeText.length ; i++){
				actualText=listViewElement.get(i).getText().trim();
				if(!(actualText.equalsIgnoreCase(arrViewModeText[i]))){
					viewFlag=true;
					break;
				}
			 }
			 if(viewFlag==false){
				retval=Globals.KEYWORD_PASS;
			  }else{
			 	return retval;
		       }
		   }else{
			  return retval;
		    }
		
		 }catch (Exception e){
			log.error("Unable to verify the text for client user in client view section | Exception - " + e.toString());
			throw e;
		  }	
		return retval;
		
	}
	 
	/**************************************************************************************************
	 * Method to navigate to other confiurations tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String navigateToOtherConfigTab() throws Exception{

		APP_LOGGER.startFunction("NavigateToOtherConfigTab");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 20;

		String stepName = "Other Configuration Tab";
		tempretval =  sc.waitforElementToDisplay("manageAccount_otherConfigurations_link", timeOutInSeconds );

		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Accounts home page has not been loaded", 1);
			throw new Exception("Accounts home page has not been loaded");
		}

		sc.clickWhenElementIsClickable("manageAccount_otherConfigurations_link", (int) timeOutInSeconds);
		tempretval =  sc.waitforElementToDisplay("otherConfig_edit_btn", timeOutInSeconds );
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			tempretval = sc.waitforElementToDisplay("otherConfig_create_btn", 2);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail",stepName,"Neither buttons-Edit/Create button is visible",1);
				throw new Exception(stepName+"-Neither buttons-Edit/Create button is visible");
			}
		}else{
			sc.STAF_ReportEvent("Pass",stepName,"Page has loaded",1);

			retval = Globals.KEYWORD_PASS;
		}

		return retval;

	}

	/**************************************************************************************************
	 * Method to navigate to Users tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String navigateToUsersTab() throws Exception{

		APP_LOGGER.startFunction("navigateToUsersTab");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		sc.clickWhenElementIsClickable("manageAccount_users_link", timeOutInSeconds);
		tempretval =  sc.waitforElementToDisplay("users_searchUser_txt", timeOutInSeconds);

		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "New User Creation","Page has not been loaded for User list",1);
			throw new Exception("newUserCreation - Page has not been loaded for User list");

		}else{
			sc.STAF_ReportEvent("Pass", "New User Creation","Page has been loaded for User list",1);
		}
		return retval;

	}

	/**************************************************************************************************
	 * Method to verify other configurations tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyOtherConfiguartionTab() throws Exception{
		APP_LOGGER.startFunction("verifyOtherConfiguartionTab");

		int timeOutInSeconds = 20;
		int noOfUsers =0;
		String tempRetval = Globals.KEYWORD_FAIL;
		String stepName = "Other Configuration Tab";
		int ddItemsCount = 0;

		navigateToUsersTab();

		WebElement userGrid = Globals.driver.findElement(LocatorAccess.getLocator("users_userGrid_tbl"));
		noOfUsers=sc.getRowCount_tbl(userGrid);

		navigateToOtherConfigTab();
		sc.clickWhenElementIsClickable("otherConfig_edit_btn",(int)  timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("otherConfig_titleForVolunteerOrdering_txt", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail",stepName,"Page hasnt loaded",1);
			throw new Exception(stepName+"Page hasnt loaded");
		}

		WebElement missingInfoDD= sc.createWebElement("otherConfig_contactMissingInfo_dd");

		ddItemsCount =  missingInfoDD.findElements(By.xpath("./option")).size();

		stepName = "1.23-To Verify Contact for Missing Info field on Other Configurations on VV";
		if(noOfUsers == ddItemsCount-1){
			sc.STAF_ReportEvent("Pass",stepName,"Missing Info dropdown has displayed all users",0);
		}else{
			sc.STAF_ReportEvent("Fail",stepName,"Missing Info dropdown has not displayed all users.Exp-"+noOfUsers+" Actual-"+ddItemsCount,1);
		}

		WebElement preferredContactDD= sc.createWebElement("otherConfig_preferredContactMethod_dd");
		List<WebElement> preferredContactMethod =  preferredContactDD.findElements(By.xpath("./option"));

		stepName ="1.24 - To Verify Preferred Contact Method field on Other Configurations on VV";
		if(preferredContactMethod.get(0).getText().equalsIgnoreCase("Email")){
			sc.STAF_ReportEvent("Pass",stepName,"Email Dropdown item is present",0);
		}else{
			sc.STAF_ReportEvent("Fail",stepName,"Email Dropdown item is NOT present",0);
		}

		if(preferredContactMethod.get(1).getText().equalsIgnoreCase("Phone")){
			sc.STAF_ReportEvent("Pass",stepName,"Phone Dropdown item is present",0);
		}else{
			sc.STAF_ReportEvent("Fail",stepName,"Phone Dropdown item is NOT present",0);
		}

		sc.setValueJsChange("otherConfig_hitDelivery_txt",Globals.fromEmailID);
		sc.setValueJsChange("otherConfig_nonHitDelivery_txt",Globals.fromEmailID);

		stepName = "1.25 - To Verify Other Configurations for new account on VV-Interest";
		if(Globals.driver.findElements(By.xpath("//button[@class='multiselect dropdown-toggle btn btn-default form-control']")).size() ==2){
			sc.STAF_ReportEvent("Pass",stepName,"Interest and skills section is displayed",0);
		}else{
			sc.STAF_ReportEvent("Fail",stepName,"Interest and skills section is NOT displayed",0);
		}


		stepName = "1.28 - To Verify Other Configurations for new account on VV -  Email Invitation Remoder";
		sc.isDisplayed_withCustomReporting("otherConfig_frequencyOfEmailReminder_dd", stepName);
		sc.isDisplayed_withCustomReporting("otherConfig_numberOfEmailReminder_dd", stepName);
		sc.isDisplayed_withCustomReporting("otherConfig_resendAttachment_chk", stepName);

		String[] expectedItems = {"None","1 day","2 days","3 days"};
		stepName = "1.29 - To Verify Frequency of Email Reminders for account on VV";
		sc.verifyDropdownItems("otherConfig_frequencyOfEmailReminder_dd", expectedItems, stepName);


		String[] expectedItems2 = {"None"};
		stepName ="1.30 - To Verify Number of Email Reminders for account on VV";
		sc.verifyDropdownItems("otherConfig_numberOfEmailReminder_dd", expectedItems2, stepName);

		sc.selectValue_byIndex("otherConfig_frequencyOfEmailReminder_dd", 2);

		String[] expectedItems3 = {"1","2","3"};
		stepName ="1.31 - To Verify Frequency of Email Reminders for account on VV";
		sc.verifyDropdownItems("otherConfig_numberOfEmailReminder_dd", expectedItems3, stepName);
		sc.selectValue_byIndex("otherConfig_numberOfEmailReminder_dd", 2);

		sc.isDisplayed_withCustomReporting("otherConfig_adverseAction_chk","1.69 - To Verify Other Configurations for new account on VV-AdverseAction");
		sc.isDisplayed_withCustomReporting("otherConfig_sharingCreditsDisabled_chk", "1.71 - To Verify Other Configurations for new account on VV-Sharing Credits");
		return Globals.KEYWORD_PASS;
	}

	/**************************************************************************************************
	 * Method to verify System Maintenance Tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifySystemMaintenanceTab() throws Exception {
		APP_LOGGER.startFunction("verifySystemMaintenanceTab");
		String retval=Globals.KEYWORD_FAIL;

		String stepName= "System Maintenance";

		navigateToSystemMaintenance();


		WebElement mainMenu = sc.createWebElement("systemMaintenance_menu_ul");
		List<WebElement> menuItems = mainMenu.findElements(By.xpath("./li"));

		String linktextUI ="";

		linktextUI= menuItems.get(0).getText();
		stepName = "1.204 - To Verify tabs on System Maintenance for In-House Portal";
		if(linktextUI.equalsIgnoreCase("Product Audit")){
			sc.STAF_ReportEvent("Pass", stepName, "Product Audit is the First Tab on System Maintenance for In-House Portal", 0);
		}else{
			sc.STAF_ReportEvent("Fail", stepName, "Product Audit is NOT the First Tab on System Maintenance for In-House Portal", 0);
		}

		linktextUI= menuItems.get(1).getText();
		stepName = "1.205 - To Verify tabs on System Maintenance for In-House Portal";
		if(linktextUI.equalsIgnoreCase("Maintenance Window")){
			sc.STAF_ReportEvent("Pass", stepName, "Product Audit is the First Tab on System Maintenance for In-House Portal", 0);
		}else{
			sc.STAF_ReportEvent("Fail", stepName, "Product Audit is NOT the First Tab on System Maintenance for In-House Portal",1);
		}

		linktextUI= menuItems.get(2).getText();
		stepName = "1.206 - To Verify tabs on System Maintenance for In-House Portal";
		if(linktextUI.equalsIgnoreCase("Dynamic Content")){
			sc.STAF_ReportEvent("Pass", stepName, "Dynamic Content is the Third Tab on System Maintenance for In-House Portal", 0);
		}else{
			sc.STAF_ReportEvent("Fail", stepName, "Dynamic Content is NOT Third Tab on System Maintenance for In-House Portal", 1);
		}

		linktextUI= menuItems.get(3).getText();
		stepName = "1.207 - To Verify tabs on System Maintenance for In-House Portal";
		if(linktextUI.equalsIgnoreCase("Email Templates")){
			sc.STAF_ReportEvent("Pass", stepName, "Email Templates is the Fourth Tab on System Maintenance for In-House Portal", 0);
		}else{
			sc.STAF_ReportEvent("Fail", stepName, "Email Templates is NOT the Fourth Tab on System Maintenance for In-House Portal", 1);
		}

		linktextUI= menuItems.get(4).getText();
		stepName = "1.208 - To Verify tabs on System Maintenance for In-House Portal";
		if(linktextUI.equalsIgnoreCase("List Of Values")){
			sc.STAF_ReportEvent("Pass", stepName, "List Of Values is the Fifth Tab on System Maintenance for In-House Portal", 1);
		}else{
			sc.STAF_ReportEvent("Fail", stepName, "List Of Values is NOT the Fifth Tab on System Maintenance for In-House Portal", 1);
		}
		retval = Globals.KEYWORD_PASS;
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to verify search by user functionality
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifySearchByUser() throws Exception{
		APP_LOGGER.startFunction("verifySearchByUser");
		String retval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		String stepName= "Search By User";

		String tempRetval = sc.waitforElementToDisplay("searchAccount_SearchByUser_link", timeOutInSeconds) ;
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, " HomePage has Not loaded", 1);
			throw new Exception(stepName +" HomePage has Not loaded");
		}

		sc.clickWhenElementIsClickable("searchAccount_SearchByUser_link",timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("searchByUser_searchCritera_dd", timeOutInSeconds) ;
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Search page hasnt loaded", 1);
			throw new Exception(stepName +"Search page hasnt loaded");
		}

		//		verify table headers
		WebElement userTbl = sc.createWebElement("promotion_promotionGrid_tbl");

		List<WebElement> headerList = userTbl.findElements(By.xpath("./thead/tr/th"));

		String[] expectedHeaders = {"Last Name","First Name","UserName","Client ID","Account Name","Parent Account","City","State","Status","EM"};
		String headerText;
		int i=0;

		for(WebElement element : headerList){
			headerText = element.getText();
			if(headerText.equalsIgnoreCase(expectedHeaders[i])){

				sc.STAF_ReportEvent("Pass", "To Verify Columns on Clients tab for Search by User Page", "Users table Header is present in UI. Value  -"+headerText, 1);
			}else{
				sc.STAF_ReportEvent("Fail", "To Verify Columns on Clients tab for Search by User Page", "Mismatch in Users table Header.Expected = "+expectedHeaders[i] + " Actual = "+headerText, 1);
			}
			i++;
		}

		String[] expectedItems = {"Last Name, First Name","Client ID","Account Name"};
		sc.verifyDropdownItems("searchByUser_searchCritera_dd", expectedItems, "Search By User Dropdown");

		sc.selectValue_byVisibleText("searchByUser_searchCritera_dd", "Account Name");
		String accountName =  Globals.testSuiteXLS.getCellData_fromTestData("AccountName");

		sc.setValueJsChange("searchByUser_searchValue_txt", accountName);
		sc.clickWhenElementIsClickable("searchByUser_search_btn", timeOutInSeconds);

		int resultRowCount = sc.getRowCount_tbl(userTbl);

		String usernameUI="";
		String expUsername = Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
		boolean userFound = false;
		String[] expUserVal = {"userLName","userFName",expUsername,"",accountName,"","Putnam","FL","Active"};
		String cellVal = "";
		int counter = 0;
		for(i=1;i<=resultRowCount;i++){
			usernameUI = sc.getCellData_tbl(userTbl, i, 3);
			if(usernameUI.equalsIgnoreCase(expUsername)){
				for(int j=1;j<=9;j++){
					if(j!=4 && j!= 6 ){
						cellVal = 	sc.getCellData_tbl(userTbl, i, j);
						if(cellVal.equalsIgnoreCase(expUserVal[counter])){

							sc.STAF_ReportEvent("Pass", "To Verify Columns on Clients tab for Search by User Page", "Cell value is as Expected. Value  -"+expUserVal[counter], 1);
						}else{
							sc.STAF_ReportEvent("Fail", "To Verify Columns on Clients tab for Search by User Page", "Mismatch in Users cell value.Expected = "+expUserVal[counter] + " Actual = "+cellVal, 1);
						}
					}
					counter++;

				}
				userFound = true;
				break;

			}

		}
		if(userFound){
			sc.STAF_ReportEvent("Pass", "Search By Account name", "user found.value-"+expUsername, 1);
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "Search By Account name", "user not found.Expected = "+expUsername , 1);
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify search by client functionality
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifySearchByClient() throws Exception {
		APP_LOGGER.startFunction("verifySearchByClient");
		String retval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		String stepName= "Search By Client";

		sc.clickWhenElementIsClickable("homepage_clients_link",timeOutInSeconds);

		String tempRetval = sc.waitforElementToDisplay("searchAccount_SearchByClient_link", timeOutInSeconds) ;
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, " HomePage has Not loaded", 1);
			throw new Exception(stepName +" HomePage has Not loaded");
		}

		sc.clickWhenElementIsClickable("searchAccount_SearchByClient_link",timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("searchAccount_accountCriteria_dd", timeOutInSeconds) ;
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Search Account page hasnt loaded", 1);
			throw new Exception(stepName +"Search Account page hasnt loaded");
		}

		//		verify table headers
		WebElement userTbl = sc.createWebElement("promotion_promotionGrid_tbl");

		List<WebElement> headerList = userTbl.findElements(By.xpath("./thead/tr/th"));

		String[] expectedHeaders = {"ClientId","AccountName","Parent Account","City","State","Status","EM"};
		String headerText;
		int i=0;

		for(WebElement element : headerList){
			headerText = element.getText();
			if(headerText.equalsIgnoreCase(expectedHeaders[i])){

				sc.STAF_ReportEvent("Pass", "To Verify Columns for Search by Client Page", "Users table Header is present in UI. Value  -"+headerText, 1);
			}else{
				sc.STAF_ReportEvent("Fail", "To Verify Columns for Search by Client Page", "Mismatch in Users table Header.Expected = "+expectedHeaders[i] + " Actual = "+headerText, 1);
			}
			i++;
		}

		String[] expectedItems = {"Account Name","City","State","Status"};
		sc.verifyDropdownItems("searchAccount_accountCriteria_dd", expectedItems, "Search By Client Dropdown");

		sc.selectValue_byVisibleText("searchAccount_accountCriteria_dd", "City");
		String expCity =   "Putnam";
		sc.setValueJsChange("searchAccount_accountSearch_txt",expCity);
		sc.clickWhenElementIsClickable("seachAccount_searchBtn_btn", timeOutInSeconds);

		int resultRowCount = sc.getRowCount_tbl(userTbl);

		String cityNameUI="";
		boolean mismatchFound = false;

		String cellVal = "";

		for(i=1;i<=resultRowCount;i++){
			cityNameUI = sc.getCellData_tbl(userTbl, i, 4);
			if(!cityNameUI.equalsIgnoreCase(expCity)){

				mismatchFound = true;
				break;

			}

		}
		if(mismatchFound){
			sc.STAF_ReportEvent("Fail", "Search By Account-City", "Account found whose city is not as expected..value-"+expCity, 1);

		}else{
			sc.STAF_ReportEvent("Pass", "Search By Account-City", "Search results are as expected.Expected = "+expCity , 1);
			retval = Globals.KEYWORD_PASS;
		}

		sc.selectValue_byVisibleText("searchAccount_accountCriteria_dd", "State");
		String expState =   "FL";
		sc.setValueJsChange("searchAccount_accountSearch_txt",expState);
		sc.clickWhenElementIsClickable("seachAccount_searchBtn_btn", timeOutInSeconds);

		resultRowCount = sc.getRowCount_tbl(userTbl);

		mismatchFound = false;

		cellVal = "";

		for(i=1;i<=resultRowCount;i++){
			cellVal = sc.getCellData_tbl(userTbl, i, 5);
			if(!cellVal.equalsIgnoreCase(expState)){

				mismatchFound = true;
				break;

			}

		}
		if(mismatchFound){
			sc.STAF_ReportEvent("Fail", "Search By Account-State", "Account found whose State is not as expected.Value-"+expState, 1);

		}else{
			sc.STAF_ReportEvent("Pass", "Search By Account-State", "Search results are as expected.Expected = "+expState , 1);
			retval = Globals.KEYWORD_PASS;
		}


		sc.selectValue_byVisibleText("searchAccount_accountCriteria_dd", "Status");
		String expStatus =   "Active";
		sc.setValueJsChange("searchAccount_accountSearch_txt",expStatus);
		sc.clickWhenElementIsClickable("seachAccount_searchBtn_btn", timeOutInSeconds);

		resultRowCount = sc.getRowCount_tbl(userTbl);

		mismatchFound = false;

		cellVal = "";

		for(i=1;i<=resultRowCount;i++){
			cellVal = sc.getCellData_tbl(userTbl, i, 6);
			if(!cellVal.equalsIgnoreCase(expStatus)){

				mismatchFound = true;
				break;

			}

		}
		if(mismatchFound){
			sc.STAF_ReportEvent("Fail", "Search By Account-Status", "Account found whose State is not as expected.Value-"+expStatus, 1);

		}else{
			sc.STAF_ReportEvent("Pass", "Search By Account-State", "Search results are as expected status.Expected = "+expStatus , 1);
			retval = Globals.KEYWORD_PASS;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify Product Audit  tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyProductAudit() throws Exception {
		APP_LOGGER.startFunction("verifyProductAudit");
		String tempRetval = Globals.KEYWORD_FAIL;
		String retval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		String stepName = "Product Audit Tab";

		navigateToSystemMaintenance();

		tempRetval = sc.waitforElementToDisplay("productAudit_productDetails_tbl", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Product Audit Table is not displayed", 1);
			throw new Exception(stepName+ " - Product Audit Table is not displayed");
		}

		WebElement productAuditGrid = sc.createWebElement("productAudit_productDetails_tbl");
		int posListingsCount =0;
		posListingsCount = sc.getRowCount_tbl(productAuditGrid);

		posListingsCount=posListingsCount-1;//this removed the header

		//verification of table headers
		List<WebElement> headerList = productAuditGrid.findElements(By.xpath("./thead/tr/th"));

		String[] expectedHeaders = {"Product Description","Total Orders","Matched Orders","Audit Every nth Order"};
		String headerText;
		int i=0;

		for(WebElement element : headerList){
			headerText = element.getText();
			if(headerText.equalsIgnoreCase(expectedHeaders[i])){

				sc.STAF_ReportEvent("Pass", "1.245 - To Verify Product Audit tab on System Maintenance for In-House Portal", "Header is present in UI is as expected. Value  -"+headerText, 1);
			}else{
				sc.STAF_ReportEvent("Fail", "1.245 - To Verify Product Audit tab on System Maintenance for In-House Portal", "Mismatch in Header.Expected = "+expectedHeaders[i] + " Actual = "+headerText, 1);
			}
			i++;
		}

		retval=Globals.KEYWORD_PASS;
		return retval;
	}

	/**************************************************************************************************
	 * Method to navigate to Systems maintenance tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String navigateToSystemMaintenance() throws Exception {
		String stepName= "System Maintenance";
		String tempRetval = Globals.KEYWORD_FAIL;
		String retval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 10;

		tempRetval = sc.waitforElementToDisplay("homepage_systemMaintenance_link", timeOutInSeconds) ;
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, " HomePage has not loaded", 1);
			throw new Exception(stepName +" Page has Not loaded");
		}

		sc.clickWhenElementIsClickable("homepage_systemMaintenance_link",timeOutInSeconds);

		tempRetval = sc.waitforElementToDisplay("systemMaintenance_menu_ul", timeOutInSeconds) ;
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, " System Maintenance Page  has not loaded", 1);
			throw new Exception("System Maintenance page has not loaded");
		}else{
			sc.STAF_ReportEvent("Pass", stepName, " System Maintenance Page  has loaded", 1);
			retval = Globals.KEYWORD_PASS;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify Volunteers  tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyVolunteerTab() throws Exception {
		APP_LOGGER.startFunction("verifyVolunteerTab");
		String tempRetval = Globals.KEYWORD_FAIL;
		String retval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		String stepName = "Product Audit Tab";

		navigateToSystemMaintenance();

		tempRetval = sc.waitforElementToDisplay("productAudit_productDetails_tbl", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Product Audit Table is not displayed", 1);
			throw new Exception(stepName+ " - Product Audit Table is not displayed");
		}

		WebElement productAuditGrid = sc.createWebElement("productAudit_productDetails_tbl");
		int posListingsCount =0;
		posListingsCount = sc.getRowCount_tbl(productAuditGrid);

		posListingsCount=posListingsCount-1;//this removed the header

		//verification of table headers
		List<WebElement> headerList = productAuditGrid.findElements(By.xpath("./thead/tr/th"));

		String[] expectedHeaders = {"Product Description","Total Orders","Matched Orders","Audit Every nth Order"};
		String headerText;
		int i=0;

		for(WebElement element : headerList){
			headerText = element.getText();
			if(headerText.equalsIgnoreCase(expectedHeaders[i])){

				sc.STAF_ReportEvent("Pass", "1.245 - To Verify Product Audit tab on System Maintenance for In-House Portal", "Header is present in UI is as expected. Value  -"+headerText, 1);
			}else{
				sc.STAF_ReportEvent("Fail", "1.245 - To Verify Product Audit tab on System Maintenance for In-House Portal", "Mismatch in Header.Expected = "+expectedHeaders[i] + " Actual = "+headerText, 1);
			}
			i++;
		}


		return retval;
	}

	/**************************************************************************************************
	 * Method to add a new staff user to the systems
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String addNewStaffUser() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 10;

		String stepName= "New Staff User";
		String tempRetval = sc.waitforElementToDisplay("staff_newStaff_btn", timeOutinSeconds) ;
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			throw new Exception(stepName +" Page has Not loaded");
		}

		String username = Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
		sc.setValueJsChange("staff_search_txt", username);
		sc.clickWhenElementIsClickable("staff_searchUI_span", (int) timeOutinSeconds);

		WebElement staffTbl = Globals.driver.findElement(LocatorAccess.getLocator("staff_details_tbl"));
		int rowCount  =  staffTbl.findElements(By.xpath("./tbody/tr")).size();
		boolean userFound = false;

		if(rowCount == 1){
			String uiMsg = staffTbl.findElement(By.xpath("./tbody/tr/td")).getText();
			if( uiMsg.contains("No matching records")){
				userFound = false;
			}else {
				uiMsg = staffTbl.findElement(By.xpath("./tbody/tr/td[1]")).getText();
				if (uiMsg.equalsIgnoreCase(username)) {
					userFound = true;
				}
			}

		}else{
			String cellVal ="";
			for (int i = 1; i <= rowCount ; i++){
				cellVal = staffTbl.findElement(By.xpath("./tbody/tr/td[1]")).getText();
				if(cellVal.equalsIgnoreCase(username)){
					userFound = true;
					break;
				}
			}

		}

		if(userFound){
			sc.STAF_ReportEvent("Fail", stepName, "User LAready exists.-"+username, 1);
		}else{
			sc.clickWhenElementIsClickable("staff_newStaff_btn", (int) timeOutinSeconds);
			if(sc.waitforElementToDisplay("newStaff_username_txt", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Page has NOT loaded", 1);
				throw new Exception(stepName +"Page has Not loaded");
			}

			sc.setValueJsChange("newStaff_username_txt",username);
			sc.setValueJsChange("newStaff_name_txt",username);
			sc.setValueJsChange("newStaff_extn_txt","1234");
			sc.selectValue_byVisibleText("newStaff_dept_dd", "IT");
			sc.clickWhenElementIsClickable("newStaff_All_link", (int) timeOutinSeconds);
			sc.clickWhenElementIsClickable("newStaff_create_btn", (int) timeOutinSeconds);

			if(sc.waitforElementToDisplay("newStaff_edit_btn", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Unable to add new staff user-"+username, 1);

			}else{
				sc.STAF_ReportEvent("Pass", stepName, "New staff user added successfully-"+username, 1);
				retval=Globals.KEYWORD_PASS;
			}

		}


		if(sc.waitforElementToDisplay("newStaff_username_txt", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){
			retval=Globals.KEYWORD_PASS;
			sc.STAF_ReportEvent("Pass", stepName, "Page has loaded", 1);
			retval = Globals.KEYWORD_PASS;
		}
		else{
			sc.STAF_ReportEvent("Fail", stepName, "Page has NOT loaded", 1);
			throw new Exception(stepName +"Page has NOT loaded");
		}


		return retval;

	}

	/**************************************************************************************************
	 * Method to Navigate to Staff Tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String navigateToStaffTab() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 10;

		String stepName= "Staff Tab";
		sc.waitforElementToDisplay("homepage_staff_link", timeOutinSeconds);
		sc.clickWhenElementIsClickable("homepage_staff_link", (int) timeOutinSeconds);

		if(sc.waitforElementToDisplay("staff_newStaff_btn", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){

			sc.STAF_ReportEvent("Pass", stepName, "Page has loaded", 1);
			retval = Globals.KEYWORD_PASS;
		}
		else{
			sc.STAF_ReportEvent("Fail", stepName, "Page has NOT loaded", 1);
			throw new Exception(stepName +"Page has NOT loaded");
		}

		return retval;

	}

	/**************************************************************************************************
	 * Method to log out a staff user
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String staffUserLogout() throws Exception {
		APP_LOGGER.startFunction("staffUserLogout");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		String stepName = "Staff User Logout";
		tempretval =  sc.waitforElementToDisplay("searchAccount_Logout_link", timeOutInSeconds );

		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Log Out link is not Displayed", 1);
			throw new Exception("Log Out link is not Displayed");
		}
		sc.clickWhenElementIsClickable("searchAccount_Logout_link", timeOutInSeconds);
		tempretval =  sc.waitforElementToDisplay("adminLogin_login_btn", timeOutInSeconds );

		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Unable to log out successfully", 1);

		}else{
			sc.STAF_ReportEvent("Pass", stepName, "Logged Out successfully", 1);
			retval = Globals.KEYWORD_PASS;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to fetch the good deed code for a position
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String fetchGoodDeedCode() throws Exception{
		APP_LOGGER.startFunction("FetchGoodDeedCode");
		String retval=Globals.KEYWORD_FAIL;
        int timeOutInSeconds=60;
        String tempRetval= Globals.KEYWORD_FAIL;
		//InhousePortal.verifyPositionListingsTab();
		tempRetval = sc.waitforElementToDisplay("manageAccount_positions_link", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			log.debug("Method-activateAccount - Page has not been loaded for Accounts Main tab");
			return retval;
		}

		sc.clickWhenElementIsClickable("manageAccount_positions_link", timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("position_searchPosition_txt", timeOutInSeconds);
		tempRetval = verifyNotes();
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			log.debug("Method-activateAccount - Page has not been loaded for Position tab");
			return retval;
		}		
		sc.clickWhenElementIsClickable("manageAccount_positionListings_link", timeOutInSeconds);
		
		
		String expPositionName="";

		expPositionName= Globals.testSuiteXLS.getCellData_fromTestData("PositionName") +" - "+ Globals.testSuiteXLS.getCellData_fromTestData("PricingName");

		WebElement positionListingGrid = sc.createWebElement("positionListings_posListingGrid_tbl");

		int posListingsCount =0;
		int i=0;

		posListingsCount = sc.getRowCount_tbl(positionListingGrid);
		posListingsCount=posListingsCount-1;//this removed the header
		WebElement posNameEle=null;
		String posName="";
		boolean posFound = false;
		String goodDeedCode ="";

		for(i=1;i<=posListingsCount;i++){
			posNameEle = positionListingGrid.findElement(By.xpath("./tbody[2]/tr["+i+"]/td[1]/input"));
			posName=posNameEle.getAttribute("value");
			if(expPositionName.equalsIgnoreCase(posName)){
				posFound = true;
				WebElement deedEle = positionListingGrid.findElement(By.xpath("./tbody[2]/tr["+i+"]/td[3]/div/span"));

				goodDeedCode = deedEle.getText();
				sc.scrollIntoView(deedEle);
				break;
			}
		}
		String colName = Globals.Env_To_Execute_ON+"_GoodDeedCodeOrURL";

		if(posFound){
			sc.STAF_ReportEvent("Pass", "Good Deed Code", expPositionName +":-"+goodDeedCode,1);
			retval = Globals.KEYWORD_PASS;
			Globals.testSuiteXLS.setCellData_inTestData(colName, goodDeedCode);

		}
		else{
			Globals.testSuiteXLS.setCellData_inTestData(colName, "");
			sc.scrollIntoView(positionListingGrid);
			sc.STAF_ReportEvent("Fail", "Good Deed Code", "Good Deed Code not found for -"+expPositionName,1);

		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to remove good deed url for a position
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String removeGoodDeedURL() throws Exception{
		APP_LOGGER.startFunction("RemoveGoodDeedURL");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 20;

		String stepName = "Email Settings Tab";
		tempretval =  sc.waitforElementToDisplay("clientBGDashboard_manageMyVolunteer_link", timeOutInSeconds );

		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Email Settings Tab has not been loaded", 1);
			throw new Exception("Email Settings Tab has not been loaded");
		}
		String expPositionName ="";
		String actPositionName="";
		WebElement urlGrid = null;
		int rowCount;

		boolean positionFound = false;

		urlGrid = sc.createWebElement("emailSettings_goodDeedURlGrid_tbl");
		expPositionName= Globals.testSuiteXLS.getCellData_fromTestData("PositionName") +" - "+ Globals.testSuiteXLS.getCellData_fromTestData("PricingName");
		rowCount =  sc.getRowCount_tbl(urlGrid);

		String deedURL="";
		WebElement rowElement = null;
		int i =1;
		for ( ; i <rowCount; i++) {
			actPositionName = urlGrid.findElement(By.xpath("./tbody/tr["+i+"]/td[2]")).getText();
			if (actPositionName.equalsIgnoreCase(expPositionName.trim())) {

				positionFound = true;
				deedURL = urlGrid.findElement(By.xpath("./tbody/tr["+i+"]/td[3]/input")).getAttribute("value");
				rowElement = urlGrid.findElement(By.xpath("./tbody/tr["+i+"]/td[3]/input"));

				break;

			}
		}


		if(positionFound){
			sc.scrollIntoView(rowElement);
			sc.STAF_ReportEvent("Pass", "Good Deed URL", expPositionName +": is present in the table",1);
			int currentRow=i;
			sc.clickWhenElementIsClickable("emailSettings_edit_btn", (int) timeOutInSeconds);
			tempretval = sc.waitforElementToDisplay("emailSettings_addURL_btn", timeOutInSeconds);

			WebElement removeBtn = urlGrid.findElement(By.xpath("./tbody/tr["+currentRow+"]/td[5]/button"));
			removeBtn.click();

			Thread.sleep(2000);

			int afterRowCount = sc.getRowCount_tbl(urlGrid);

			if(rowCount-afterRowCount == 1){
				sc.STAF_ReportEvent("Pass", "Good Deed URL", expPositionName +" Good Deed URL has been removed.Url-"+deedURL,1);
			}else{
				sc.STAF_ReportEvent("Fail", "Good Deed URL", expPositionName +" Good Deed URL has NOT been removed.BeforeRowCount="+rowCount + " AfterRowCount="+afterRowCount,1);
			}
			sc.clickWhenElementIsClickable("emailSettings_submit_btn", (int) timeOutInSeconds);

			tempretval = sc.waitforElementToDisplay("emailSettings_ok_btn", timeOutInSeconds);
			sc.clickWhenElementIsClickable("emailSettings_ok_btn", (int) timeOutInSeconds);
			retval=Globals.KEYWORD_PASS;
		}
		else{
			sc.scrollIntoView(urlGrid);
			sc.STAF_ReportEvent("Fail", "Good Deed URL", expPositionName +": is NOT present in the table",1);

			throw new Exception("Unable to Delete Good Deed url for - "+expPositionName);

		}

		//end simulation
		sc.clickWhenElementIsClickable("endEmulation_endEmulation_link", (int) timeOutInSeconds);


		return retval;

	}

	/**************************************************************************************************
	 * Method to genarte good deed url for a postion
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String generateGoodDeedURL() throws Exception{
		APP_LOGGER.startFunction("GenerateGoodDeedURL");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 20;

		String stepName = "Email Settings Tab";
		tempretval =  sc.waitforElementToDisplay("clientBGDashboard_manageMyVolunteer_link", timeOutInSeconds );

		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Email Settings Tab has not been loaded", 1);
			throw new Exception("Email Settings Tab has not been loaded");
		}
		String expPositionName ="";
		String actPositionName="";
		WebElement urlGrid = null;
		int rowCount;
		String goodDeedURL="";
		boolean positionFound = false;

		urlGrid = sc.createWebElement("emailSettings_goodDeedURlGrid_tbl");
		expPositionName= Globals.testSuiteXLS.getCellData_fromTestData("PositionName") +" - "+ Globals.testSuiteXLS.getCellData_fromTestData("PricingName");
		rowCount =  sc.getRowCount_tbl(urlGrid);

		sc.scrollIntoView(urlGrid);

		for (int i = 1; i <rowCount; i++) {
			actPositionName = urlGrid.findElement(By.xpath("./tbody/tr["+i+"]/td[2]")).getText();
			if (actPositionName.equalsIgnoreCase(expPositionName.trim())) {

				positionFound = true;
				goodDeedURL = urlGrid.findElement(By.xpath("./tbody/tr["+i+"]/td[3]/input")).getAttribute("value");
				break;

			}
		}

		//GoodDeedCode column name depends upon the Env that its being executed
		String colName = Globals.Env_To_Execute_ON+"_GoodDeedCodeOrURL";
		
		if(positionFound){
			
			sc.testSuiteXLS.setCellData_inTestData(colName, goodDeedURL);
			sc.STAF_ReportEvent("Pass", "Good Deed URL", expPositionName +":-"+goodDeedURL,1);
			
			retval = Globals.KEYWORD_PASS;
		}
		else{
			sc.clickWhenElementIsClickable("emailSettings_edit_btn", (int) timeOutInSeconds);
			tempretval = sc.waitforElementToDisplay("emailSettings_addURL_btn", timeOutInSeconds);
			sc.clickWhenElementIsClickable("emailSettings_addURL_btn", (int) timeOutInSeconds);

			int lastRow= rowCount +1;

			WebElement bewPositionDD = urlGrid.findElement(By.xpath("./tbody/tr["+lastRow+"]/td[2]/select"));
			sc.selectValue_byVisibleText(bewPositionDD, expPositionName);

			WebElement generateURLBtn = urlGrid.findElement(By.xpath("./tbody/tr["+lastRow+"]/td[4]/button"));
			generateURLBtn.click();

			Thread.sleep(1000);
			

			goodDeedURL = "";
			goodDeedURL = urlGrid.findElement(By.xpath("./tbody/tr["+lastRow+"]/td[3]/input")).getAttribute("value");

			if(goodDeedURL == "" || goodDeedURL.isEmpty()) {
				sc.STAF_ReportEvent("Fail", "Good Deed URL", "Unable to generate Good Deed url for - "+expPositionName ,1);
				sc.testSuiteXLS.setCellData_inTestData(colName, "");
				retval=Globals.KEYWORD_FAIL;


			}else{
				sc.STAF_ReportEvent("Pass", "Good Deed URL", expPositionName +":-"+goodDeedURL,1);
				sc.testSuiteXLS.setCellData_inTestData(colName, goodDeedURL);
				sc.clickWhenElementIsClickable("emailSettings_submit_btn", (int) timeOutInSeconds);
				tempretval = sc.waitforElementToDisplay("emailSettings_ok_btn", timeOutInSeconds);
				sc.clickWhenElementIsClickable("emailSettings_ok_btn", (int) timeOutInSeconds);


				retval = Globals.KEYWORD_PASS;
			}

		}

		//end simulatiion
		sc.clickWhenElementIsClickable("endEmulation_endEmulation_link", (int) timeOutInSeconds);


		return retval;

	}

	/**************************************************************************************************
	 * Method to navigate to email settings tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String navigateToEmailSettingsTab() throws Exception{
		APP_LOGGER.startFunction("NavigateToEmailSettingsTab");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 20;

		String stepName = " Email Settings Tab";
		tempretval =  sc.waitforElementToDisplay("manageAccount_emailSettings_link", timeOutInSeconds );

		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Accounts home page has not been loaded", 1);
			throw new Exception("Accounts home page has not been loaded");
		}

		sc.clickWhenElementIsClickable("manageAccount_emailSettings_link", (int) timeOutInSeconds);
		tempretval =  sc.waitforElementToDisplay("clientBGDashboard_manageMyVolunteer_link", timeOutInSeconds );
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail",stepName, "Email Settings page has not been loaded", 1);
			throw new Exception("Email Settings page has not been loaded");
		}

		sc.STAF_ReportEvent("Pass", stepName, "Email Settings page has been loaded", 1);
		retval = Globals.KEYWORD_PASS;

		return retval;

	}

	/**************************************************************************************************
	 * Method to verify Dashboard Tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String validateDashboardTab() throws Exception {
		APP_LOGGER.startFunction("ValidateDashboardTab");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 20;

		try {
			String stepName = "1.292- To verify Dashboard tab for Inhouse Portal";

			tempretval =  sc.waitforElementToDisplay("homepage_dashboard_link", timeOutInSeconds );
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Inhouse portal has not been loaded", 1);
				throw new Exception("Inhouse portal has not been loaded");
			}

			sc.clickWhenElementIsClickable("homepage_dashboard_link", (int) timeOutInSeconds);
			tempretval =  sc.waitforElementToDisplay("dashboard_orderError_span", timeOutInSeconds );
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail",stepName, "Dashboard Tab in Inhouse portal has not been loaded", 1);
				throw new Exception("Volunteers Tab in Inhouse portal has not been loaded");
			}

			sc.STAF_ReportEvent("Pass", stepName, "Dashboard Tab in Inhouse portal has been loaded-Order Error text is displayed", 1);

			//			TODO - Need to replace the below for fetching headers using a generic functions
			WebElement staffTbl = Globals.driver.findElement(LocatorAccess.getLocator("dashboard_orderErrors_tbl"));
			int columnCount  =  staffTbl.findElements(By.xpath("./tbody/tr/th")).size();
			String colHeaderName;

			String[] expectedHeaders = {"Name","Account","Phone","Email","Created"};


			for (int i = 1; i <= columnCount ; i++){

				colHeaderName = staffTbl.findElement(By.xpath("./tbody/tr/th["+i+"]")).getText();
				if(colHeaderName.equalsIgnoreCase(expectedHeaders[i-1].trim())){
					sc.STAF_ReportEvent("Pass", stepName, "Dashboard Tab Header matched with expected-Val-"+colHeaderName, 0);

				}else{
					sc.STAF_ReportEvent("Fail", stepName, "Dashboard Tab Header MisMatch with expected-Exp-"+expectedHeaders[i-1] + " Actual="+colHeaderName, 1);
				}
			}


			retval = Globals.KEYWORD_PASS;	

		} catch (Exception e) {
			sc.STAF_ReportEvent("Fail", "Dashboard Tabb", "Exception occurred while validating Dashboard Tab", 1);
			throw e;
		}

		return retval;
	}


	/**************************************************************************************************
	 * Method to validate UI for Staffs Tab in Inhouse Portal
	 * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
	 * @author aunnikrishnan
	 * @created
	 * @LastModifiedBy
	 ***************************************************************************************************/
	public static String validateStaffTab() throws Exception {
		APP_LOGGER.startFunction("ValidateStaffTab");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 20;

		try {

			tempretval =  sc.waitforElementToDisplay("homepage_staff_link", timeOutInSeconds );
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "1.186 - To Verify Staff tab for In-House Portal", "Inhouse portal has not been loaded", 1);
				throw new Exception("Inhouse portal has not been loaded");
			}

			sc.clickWhenElementIsClickable("homepage_staff_link", (int) timeOutInSeconds);
			tempretval =  sc.waitforElementToDisplay("staff_search_txt", timeOutInSeconds );
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "1.186 - To Verify Staff tab for In-House Portal", "Staff Tab in Inhouse portal has not been loaded", 1);
				throw new Exception("Volunteers Tab in Inhouse portal has not been loaded");
			}

			sc.STAF_ReportEvent("Pass", "1.186 - To Verify Staff tab for In-House Portal", "Staff Tab in Inhouse portal has been loaded", 1);

			//			TODO - Need to replace the below for fetching headers using a generic functions
			WebElement staffTbl = Globals.driver.findElement(LocatorAccess.getLocator("staff_details_tbl"));
			int columnCount  =  staffTbl.findElements(By.xpath("./thead/tr/th")).size();
			String colHeaderName;

			String[] expectedHeaders = {"UserName","Name","Phone Extension","Principal Department","Role","Status"};


			for (int i = 1; i <= columnCount ; i++){

				colHeaderName = staffTbl.findElement(By.xpath("./thead/tr/th["+i+"]")).getText();
				if(colHeaderName.equalsIgnoreCase(expectedHeaders[i-1].trim())){
					sc.STAF_ReportEvent("Pass", "1.186 - To Verify Staff tab for In-House Portal", "Staff Tab Header matched with expected-Val-"+colHeaderName, 0);

				}else{
					sc.STAF_ReportEvent("Fail", "1.186 - To Verify Staff tab for In-House Portal", "Staff Tab Header MisMatch with expected-Exp-"+expectedHeaders[i-1] + " Actual="+colHeaderName, 1);
				}
			}

			// search a staff user

			sc.setValueJsChange("staff_search_txt", "vvautomation");
			sc.clickWhenElementIsClickable("staff_searchUI_span", (int) timeOutInSeconds);

			int rowCount  =  staffTbl.findElements(By.xpath("./tbody/tr")).size();

			if(rowCount != 1){

				sc.STAF_ReportEvent("Fail", "1.186 - To Verify Staff tab for In-House Portal", "Unable to search staff user",1);

			}
			else{

				String cellVal="";
				String[] expectedVal = {"vvautomation","VV Automation","8819","IT"};
				for (int i = 1; i <= 4 ; i++){
					cellVal = staffTbl.findElement(By.xpath("./tbody/tr/td["+i+"]")).getText();
					if(cellVal.equalsIgnoreCase(expectedVal[i-1])){
						sc.STAF_ReportEvent("Pass", "1.186 - To Verify Staff tab for In-House Portal", "Staff Tab Column Val matched with expected-Val-"+cellVal, 0);

					}else{
						sc.STAF_ReportEvent("Fail", "1.186 - To Verify Staff tab for In-House Portal", "Staff Tab Column Val MisMatch with expected-Exp-"+expectedVal[i-1] + " Actual="+cellVal, 1);
					}
				}		
			}

            retval=Globals.KEYWORD_PASS;


		} catch (Exception e) {
			sc.STAF_ReportEvent("Fail", "Volunteer Tab", "Exception occurred while validation Volunteer Tab", 1);
			throw e;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to validate UI for Volunteers Tab in Inhouse Portal
	 * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
	 * @author aunnikrishnan
	 * @created
	 * @LastModifiedBy
	 ***************************************************************************************************/
	public static String validateVolunteersTab() throws Exception {
		APP_LOGGER.startFunction("validateVolunteersTab");
		String retval=Globals.KEYWORD_FAIL;
        int timeOutInSeconds = 20;

		try {

			navigateToVolunteersTab();
			//			TODO - Need to replace the below for fetching headers using a generic functions
			WebElement volTbl = Globals.driver.findElement(By.xpath("//table[@class='results selectClick']"));
			int columnCount  =  volTbl.findElements(By.xpath("./thead/tr/th")).size();
			String colHeaderName;

			String[] expectedHeaders = {"Last Name","First Name","Username","Email Address","City",
					"State","Joined","Last Login","Orders","Shutdown","Active","Owned","Status","EM"};


			for (int i = 1; i <= columnCount ; i++){

				colHeaderName = volTbl.findElement(By.xpath("./thead/tr/th["+i+"]")).getText();
				if(colHeaderName.equalsIgnoreCase(expectedHeaders[i-1].trim())){
					sc.STAF_ReportEvent("Pass", "1.157 - To Verify Volunteers tab for In-House Portal", "Volunteers Tab Header matched with expected-Val-"+colHeaderName, 0);

				}else{
					sc.STAF_ReportEvent("Fail", "1.157 - To Verify Volunteers tab for In-House Portal", "Volunteers Tab Header MisMatch with expected-Exp-"+expectedHeaders[i-1] + " Actual="+colHeaderName, 1);
				}
			}

			// search a volunteer by order id
            if(Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA2")) 
            {
			String[] expectedVal = {"APIOrder -'","QIhIKalGzpWCSYP -'","vol014144210","paresh.save@sterlingts.com","Test City","FL","02/16/2018","02/16/2018","Summary   -   Details"};
			retval =searchAndVerifyVolunteer("Order ID","82508",expectedVal);
			sc.clickWhenElementIsClickable("homepage_volunteers_link", timeOutInSeconds);
			sc.waitforElementToDisplay("volunteers_details_tbl", timeOutInSeconds );
			retval =searchAndVerifyVolunteer("Last Name, First Name","QIhIKalGzpWCSYP -'",expectedVal);

            }
            else
            {
            	String[] expectedVal = {"APIOrder","aHmphZfOAykmaAL","vol011756108","Anand.Unnikrishnan@sterlingts.com","Test City","FL","07/26/2016","07/26/2016","Summary   -   Details"};
    			retval =searchAndVerifyVolunteer("Order ID","15031",expectedVal);
    			sc.clickWhenElementIsClickable("homepage_volunteers_link", timeOutInSeconds);
    			sc.waitforElementToDisplay("volunteers_details_tbl", timeOutInSeconds );
    			retval =searchAndVerifyVolunteer("Last Name, First Name","aHmphZfOAykmaAL",expectedVal);

            }

		} catch (Exception e) {
			sc.STAF_ReportEvent("Fail", "Volunteer Tab", "Exception occurred while validation Volunteer Tab", 1);
			throw e;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to login a staff user
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String staffLogin() throws Exception{
		APP_LOGGER.startFunction("StaffLogin");
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		String username=null;
		String password=null;
		try {
			//QAA-798:As business had requested Inhouse portal to be launched on Chrome browser
			String urlLaunched= sc.launchURL("chrome",Globals.getEnvPropertyValue("InHouseURL"));
			
			
			
			
			
			if (urlLaunched.equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.waitforElementToDisplay("adminLogin_Username_txt",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){

				sc.STAF_ReportEvent("Pass", "InhousePortalLogin", " Login page has loaded" , 1);

				/*
				if(Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
					 username=Globals.STAFF_USERNAME;
					 password=Globals.STAFF_USER_PSSWORD;
								
				}else{
					username=Globals.getEnvPropertyValue("Staff_Username");
					password=Globals.getEnvPropertyValue("Staff_Password");
				}
				*/
				username=Globals.getEnvPropertyValue("Staff_Username");
				//password=Globals.getEnvPropertyValue("Staff_Password");
				
				password=ProtectedEncryption.decrypt(Globals.getEnvPropertyValue("Staff_Password"), CommonHelpMethods.createKey());
					
				tempRetval=sc.setValueJsChange("adminLogin_Username_txt", username);
				tempRetval=sc.setValueJsChange("adminLogin_Password_txt",password);

				log.debug(tempRetval + " Setting value for username and password for Staff login");

				//				Globals.driver.findElement(LocatorAccess.getLocator("adminLogin_login_btn")).click();
				int timeOutinSeconds =10;
				sc.clickWhenElementIsClickable("adminLogin_login_btn",timeOutinSeconds  );


				if (sc.waitforElementToDisplay("searchAccount_accountSearch_txt",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){
					log.info("Staff Logged In | Staff Username - "+ username + " | Staff Password - "+password);
					sc.STAF_ReportEvent("Pass", "InhousePortalLogin", "User has logged in - " + username , 1);

					retval=Globals.KEYWORD_PASS;					
				}else{
					sc.STAF_ReportEvent("Fail", "InhousePortalLogin", "User unable log in - " + username , 1);
					log.error("Staff User Unable to Log In | Staff Username - "+ username + " | Staff Password - "+password);
					throw new Exception("Staff User Unable to Log In | Staff Username - "+ username);
				}

			}else{
				sc.STAF_ReportEvent("Fail", "InhousePortalLogin", " Login page has NOT loaded" , 1);
				log.error("Staff Unable to Log In As Login Page has not Launched ");
				throw new Exception("Staff Unable to Log In As Login Page has not Launched ");
			}


			//check for No results found

		} catch (Exception e) {

			log.error("Exception occurred in Staff Login | "+e.toString());
			throw e;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to navigate to volunteers tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public  static String navigateToVolunteersTab() throws Exception {
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =30;

		tempretval =  sc.waitforElementToDisplay("homepage_volunteers_link", timeOutInSeconds );
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "1.157 - To Verify Volunteers tab for In-House Portal", "Inhouse portal has not been loaded", 1);
			throw new Exception("Inhouse portal has not been loaded");
		}

		sc.clickWhenElementIsClickable("homepage_volunteers_link", (int) timeOutInSeconds);
		tempretval =  sc.waitforElementToDisplay("volunteers_details_tbl", timeOutInSeconds );
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "1.157 - To Verify Volunteers tab for In-House Portal", "Volunteers Tab in Inhouse portal has not been loaded", 1);
			throw new Exception("Volunteers Tab in Inhouse portal has not been loaded");
		}

		sc.STAF_ReportEvent("Pass", "1.157 - To Verify Volunteers tab for In-House Portal", "Volunteers Tab in Inhouse portal has been loaded", 1);
		retval = Globals.KEYWORD_PASS;
		return retval;
	}

	/**************************************************************************************************
	 * Method to search a volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String searchAndVerifyVolunteer(String searchCriteria,String searchVal,String[] expectedVal) throws Exception{

		int timeOutInSeconds=10;
		String cellVal="";
		int counter =0;
		int rowCount =0;
		String retval = Globals.KEYWORD_FAIL;

		sc.selectValue_byVisibleText("volunteers_searchCriteria_dd", searchCriteria);
		sc.setValueJsChange("volunteers_searchValue_txt", searchVal);
		sc.clickWhenElementIsClickable("volunteers_search_btn",  timeOutInSeconds);

		WebElement volTbl = Globals.driver.findElement(By.xpath("//table[@class='results selectClick']"));

		cellVal = volTbl.findElement(By.xpath("./tbody/tr/td[1]")).getText().toLowerCase();

		while (cellVal.contains("no matching records") && counter <10) {
			Thread.sleep(1000);
			counter ++;
			cellVal = volTbl.findElement(By.xpath("./tbody/tr/td[1]")).getText().toLowerCase();
		}
		rowCount  =  volTbl.findElements(By.xpath("./tbody/tr")).size();

		if(rowCount != 1){

			sc.STAF_ReportEvent("Fail", "1.157 - To Verify Volunteers tab for In-House Portal", "Unable to search volunteer",1);

		}
		else{
			boolean volFound = false;
			int i=0;
			for ( i = 1; i <= expectedVal.length ; i++){
				cellVal = volTbl.findElement(By.xpath("./tbody/tr/td["+i+"]")).getText();
				if(cellVal.equalsIgnoreCase(expectedVal[i-1])){

					volFound = true;					
				}else{
					volFound = false;
					break;
				}
			}	

			if(volFound){
				sc.STAF_ReportEvent("Pass", "1.157 - To Verify Volunteers tab for In-House Portal", "Volunteers Details matched with UI", 1);
				retval=Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "1.157 - To Verify Volunteers tab for In-House Portal", "Volunteers Tab Column Val MisMatch with expected-Exp-"+expectedVal[i-1] + " Actual="+cellVal, 1);

			}
		}

		return retval;

	}

	/**************************************************************************************************
	 * Method to verify Order details report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyOrderDetailsReport() throws Exception{
		Globals.Component = "Order Details Page Report";

		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		try{
			staffLogin();	
			navigateToVolunteersTab();


			String lastName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
			String fname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
			String volUsername = Globals.testSuiteXLS.getCellData_fromTestData("VolunteerUsername");
			String emailID = Globals.fromEmailID;
			String addressCity = Globals.Volunteer_City;
			//String joinedDate = Globals.testSuiteXLS.getCellData_fromTestData("OrderedDate");
			String format ="MM/dd/YYYY";
	    	Format formatter = new SimpleDateFormat(format);
	    	Date result = new java.util.Date();
	    	
	    	String joinedDate; 
	    	joinedDate = formatter.format(result); 
			String addressState = Globals.testSuiteXLS.getCellData_fromTestData("AddressState");
			String[] expectedVal = {lastName,fname,volUsername,emailID,addressCity,"FL",joinedDate,joinedDate,"Summary   -   Details"};
			String vvOrderID = Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");

			tempRetval = searchAndVerifyVolunteer("Order ID", vvOrderID, expectedVal);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				WebElement volTbl = Globals.driver.findElement(By.xpath("//table[@class='results selectClick']"));
				volTbl.findElement(By.xpath("./tbody/tr/td[9]/a[2]")).click();
				verifyOrderDetailsPage();

				WebElement orderLink=Globals.driver.findElement(By.xpath("//*[@id='adminRoot']//table/tbody/tr[1]/td/a[1]"));

				String orderIDText = orderLink.findElement(By.xpath("./span")).getText();
				if(vvOrderID.equalsIgnoreCase(orderIDText)){
					sc.STAF_ReportEvent("Pass", "Order Details Page", "Order id is as expected-"+vvOrderID, 1);
					orderLink.click();

					ArrayList<String> tabs = new ArrayList<String>(Globals.driver.getWindowHandles());
					Globals.driver.switchTo().window(tabs.get(1));

					tempRetval = sc.waitforElementToDisplay("bgReport_volInfoSection_list", timeOutInSeconds);

					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail", "BG Report", "Volunteer BG Report page has not loaded", 1);
					}else{

						ClientFacingApp.bgReport_verifyReportHeader_OrgUser();

						ClientFacingApp.bgReport_verifyVolunteerHeaderInfo(true);
						ClientFacingApp.bgReport_verifyQuickViewText("volunteer");
						ClientFacingApp.bgReport_verifyBackgroundCheckReportSummary();
						tempRetval= ClientFacingApp.bgReport_verifyPoductSumary();	

					}
					Globals.driver.close();
					Globals.driver.switchTo().window(tabs.get(0));
					retval = Globals.KEYWORD_PASS;
				}else{
					sc.STAF_ReportEvent("Fail", "Order Details Page", "Order id is as NOT expected.Exp-"+vvOrderID+ " Actual-"+orderIDText, 1);
				}

			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "Order Details Page", "Unable to verify due to Exception-"+e.toString(),1);

			throw e;
		}

		//Globals.driver.quit();
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify order details page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyOrderDetailsPage() throws Exception{
		//TODO need to implement the validations
		return Globals.KEYWORD_PASS;
	}
	
	/**************************************************************************************************
	 * Method to poll all orders which has been processed in Swest using Setup--Polling in UI
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String pollAllOrders() throws Exception{
		APP_LOGGER.startFunction("pollAllOrders");
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =30;
		
		String stepName = "Polling Order";
		try{
			tempRetval = sc.waitforElementToDisplay("inhouseHomepage_Setup_link", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Inhouse portal home page has not loaded.Setup link is not present", 1);
				throw new Exception("Inhouse portal home page has not loaded.Setup link is not present");
			}
			
			sc.clickWhenElementIsClickable("inhouseHomepage_Setup_link", timeOutInSeconds);
			
			tempRetval= Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("kitchenSink_testSSN_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Kitchen Sink page has not loaded", 1);
				throw new Exception("Kitchen Sink page has not loaded");
			}
			
			sc.clickWhenElementIsClickable("kitchenSink_polling_link", timeOutInSeconds);
			
			tempRetval= Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("polling_poll_dd", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Polling page has not loaded", 1);
				throw new Exception("Polling page has not loaded");
			}
			
			sc.selectValue_byVisibleText("polling_poll_dd", "All Orders In Process");
			sc.clickWhenElementIsClickable("polling_run_btn", (int)Globals.VV_Report_MaxTimeout);
			
			//expected result text format-Process Order: NpnOrder:63317, Sterling Order:2198475, Status returned:Complete
			String npnOrderID = Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
			String swestOrderID = Globals.testSuiteXLS.getCellData_fromTestData("SWestOrderID");
			
			String expText = "Process Order: NpnOrder:"+npnOrderID+", Sterling Order:"+swestOrderID+", Status returned:Complete";
			String actualText = Globals.driver.findElement(LocatorAccess.getLocator("polling_results_txt")).getText();
			
			int counter = 0;
			
			while(counter <= (int)Globals.VV_Report_MaxTimeout && actualText.isEmpty() ){
				
				Thread.sleep(1000);
				actualText = Globals.driver.findElement(LocatorAccess.getLocator("polling_results_txt")).getText();
				counter++;
			}
			
			if(actualText.contains(expText)){
				sc.STAF_ReportEvent("Pass", stepName, "Polling was successful for Order id:"+npnOrderID+"-"+swestOrderID, 0);
				
				retval = Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", stepName, "Polling was NOT successful for Order id:"+npnOrderID+"-"+swestOrderID, 1);
				throw new Exception("Polling was NOT successful for Order id:"+npnOrderID+"-"+swestOrderID);
			}
			
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", stepName, "Unable to perfrom polling to Exception-"+e.toString(),1);

			throw e;
		}

		return retval;
	}
	
	
	/**************************************************************************************************
	 * Method to verify MinSharingProduct for a new account in EmailSettings tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String validateMinSharingProductLevel() throws Exception{
		APP_LOGGER.startFunction("validateMinSharingProductLevel");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 20;

		String stepName = "Sharing Settings";
		tempretval =  sc.waitforElementToDisplay("clientBGDashboard_manageMyVolunteer_link", timeOutInSeconds );

		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Email Settings Tab has not been loaded", 1);
			throw new Exception("Email Settings Tab has not been loaded");
		}
						
		String expSharingLevel =  Globals.testSuiteXLS.getCellData_fromTestData("SharingLevel")+": Advanced Criminal History Record Locator Search";
		
		Select select = new Select(sc.createWebElement("emailSetting_sharingLevel_dd"));
		String SharingLevel =  select.getFirstSelectedOption().getText();
        if(SharingLevel.equalsIgnoreCase(expSharingLevel)){
        	sc.STAF_ReportEvent("Pass", "To validate MinSharingProduct level for new account on VV", "MinSharing displayed for the account is" +SharingLevel ,1);	
        }
        else{
        	sc.STAF_ReportEvent("Fail", "To validate MinSharingProduct level main page for new account on VV", "MinSharing displayed for the account is"+expSharingLevel+"Actual =" +SharingLevel,1);
        }
		
		retval = Globals.KEYWORD_PASS;
		//end simulatiion
		sc.clickWhenElementIsClickable("endEmulation_endEmulation_link", (int) timeOutInSeconds);


		return retval;

	}
	
	/**************************************************************************************************
     * Method to navigate to email settings tab Edit Button
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author mshaik
     * @throws Exception
     ***************************************************************************************************/
     public static String emailSettingsTabEditButton() throws Exception {
            APP_LOGGER.startFunction("emailsettingstabeditbutton");
            String retval=Globals.KEYWORD_FAIL;
            String tempretval=Globals.KEYWORD_FAIL;
            long timeOutInSeconds = 20;
            String stepName= "Verify Edit Button on the Email Setings page";
            try {
                   tempretval=sc.waitforElementToDisplay("emailSettings_edit_btn", timeOutInSeconds);
                   if (tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                         sc.STAF_ReportEvent("Fail", stepName, "Email settings page edit button has not been loaded", 1);
                         throw new Exception("Email settings page edit button has not been loaded");
                   }
                   sc.clickWhenElementIsClickable_andSyncPage("emailSettings_edit_btn", 1);
                   tempretval=sc.waitforElementToDisplay("emailSettings_submit_btn", timeOutInSeconds);
                   if (tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                         sc.STAF_ReportEvent("Fail", stepName, "User has unable to click the Email settings page edit button", 1);
                         throw new Exception("User has unable to click the Email settings page edit button");
                   }
                   sc.STAF_ReportEvent("Pass", stepName, "User has clicked email settings edit button sucessfullyl", 1);
                   retval = Globals.KEYWORD_PASS;
            }catch(Exception e){
                   sc.STAF_ReportEvent("Fail", "Email Settings page", "unable to click edit button on the email settings page", 1);
                   throw e;
            }
            
            return retval;
     }




/**************************************************************************************************
     * Method for Edit Custom Messaging in email settings page
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author mshaik
     * @throws Exception
     ***************************************************************************************************/
     public static String CustomMessaging() throws Exception{
            
            APP_LOGGER.startFunction("CustomMessaging");
            String retval=Globals.KEYWORD_FAIL;
            String tempRetval=Globals.KEYWORD_FAIL;
            int timeOutInSeconds =20;
            WebElement CustomMessaging_tbl=sc.createWebElement("emailSettings_customMessaging_tbl");
            int RowCount=sc.getRowCount_tbl(CustomMessaging_tbl);
            String Cellvalue;
            int i=1;
            
            boolean customMessagingSettingFound = false;
            for (;i<=RowCount;i++){
                   Cellvalue = CustomMessaging_tbl.findElement(By.xpath("./tbody/tr["+i+"]/td[1]/label/span")).getText();
                                
                                if (Cellvalue.equalsIgnoreCase("Order Step 1 Pricing Breakdown Org Message")){
                                       customMessagingSettingFound = true;
                                       break;
                                }
            }
            
            if(customMessagingSettingFound){
                   WebElement Edit=CustomMessaging_tbl.findElement(By.xpath("./tbody/tr["+i+"]/td[2]/a"));
                   sc.clickWhenElementIsClickable(Edit, timeOutInSeconds);
                   
                   tempRetval=sc.waitforElementToDisplay("emailSettings_customMessaging_edit_btn", timeOutInSeconds);
                   if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
                         sc.STAF_ReportEvent("Pass", "Navigating Admin user to Edit Custom Messaging Page", "Navigation for Admin User to Edit Custom Messaging page was successfull", 1);
                         
                         WebElement iframeElement = Globals.driver.findElement(By.xpath("//iframe[@id='editor1_ifr']"));
                         Globals.driver.switchTo().frame(iframeElement);
                                                     
                         String dynamicCustomMsg = "Custom Messaging Validation- {org_name} - "+ RandomStringUtils.randomAlphanumeric(20);
                          Globals.driver.findElement(LocatorAccess.getLocator("emailSettings_customMessaging_edit_message_txt")).clear();
                          Globals.driver.findElement(LocatorAccess.getLocator("emailSettings_customMessaging_edit_message_txt")).sendKeys(dynamicCustomMsg);
                         
                         String editedCustomMsg =Globals.driver.findElement(LocatorAccess.getLocator("emailSettings_customMessaging_edit_message_txt")).getText();
                         
                         if(dynamicCustomMsg.equals(editedCustomMsg)){
                                Globals.testSuiteXLS.setCellData_inTestData("CustomizingMessageforeditorderstep1txt", editedCustomMsg);
                                retval = Globals.KEYWORD_PASS;
                         }else{
                                sc.STAF_ReportEvent("Fail", "Edit Custom Messaging Page", "Unable to set custom message", 1);
                                Globals.testSuiteXLS.setCellData_inTestData("CustomizingMessageforeditorderstep1txt", "");
                         }
                                                     
                         Globals.driver.switchTo().defaultContent();
                         
                          Globals.driver.findElement(LocatorAccess.getLocator("Emailsetting_CoustmEdit_saveall")).click();
                         
                   }else{
                         sc.STAF_ReportEvent("Fail", "Navigating Admin user to Edit Custom Messaging Page", "Unable to login  Edit Custom Messaging Page", 1);
                   }
                   
                   
            }else{
                   sc.STAF_ReportEvent("Fail", "Edit Custom Messaging Page", "Unable to Edit Custom Messaging Page as setting not found", 1);
                   retval = Globals.KEYWORD_FAIL;
            }

            return retval;
     }

 	 /**************************************************************************************************
 	 * Method to configure Product Specific Settings for Abuse pensylvania
 	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
 	 * @author mshaik
 	 * @throws Exception
 	 ***************************************************************************************************/
 	public static String PAAbusesettings() throws Exception{
 		APP_LOGGER.startFunction("productSpecificSettingsforAbusePensylvania");
 		String retval = Globals.KEYWORD_FAIL;
 		String tempRetval = Globals.KEYWORD_FAIL;
 		int timeOutInSeconds = 20;
 		try {
 			String stepName = "Other Configurations Tab for productSpecificSetting";
 			tempRetval = sc.waitforElementToDisplay("otherConfig_edit_btn", timeOutInSeconds);
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
 				throw new Exception(stepName+"Page hasnt loaded");
 			}
 			sc.clickWhenElementIsClickable("otherConfig_edit_btn",(int)  timeOutInSeconds);
 			tempRetval = sc.waitforElementToDisplay("manageAccount_otherConfigurations_productSpecificSettings", timeOutInSeconds);
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 				sc.STAF_ReportEvent("Fail",stepName,"product Specific Settings section has not been loaded",1);
 				throw new Exception(stepName+"product Specific Settings section has not been loaded");
 			}
// 			sc.checkCheckBox("manageAccount_otherConfigurations_productSpecificSetting_chks");
 			String AbusePensylvaniacheckboxReq;
 			AbusePensylvaniacheckboxReq = Globals.testSuiteXLS.getCellData_fromTestData("AbusePensylvaniacheckbox");
 			if (AbusePensylvaniacheckboxReq.equalsIgnoreCase("Yes")){
 				tempRetval = sc.checkCheckBox("manageAccount_otherConfigurations_productSpecificSetting_chks");
 				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 					sc.STAF_ReportEvent("Pass", stepName, "User has been checked Pennsylvania Neglect/Abuse can be reused within 5 years.check box sucessfully", 1);
 				}else{
 					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been checked Pennsylvania Neglect/Abuse can be reused within 5 years.check box un-sucessfully", 1);
 					throw new Exception(stepName+"User has NOT been checked Pennsylvania Neglect/Abuse can be reused within 5 years.check box un-sucessfully");
 				}
 				
 			}else{
 				tempRetval = sc.uncheckCheckBox("manageAccount_otherConfigurations_productSpecificSetting_chks");
 				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 					sc.STAF_ReportEvent("Pass", stepName, "User has been Unchecked Pennsylvania Neglect/Abuse can be reused within 5 years.un-check box sucessfully", 1);
 				}else{
 					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been Unchecked Pennsylvania Neglect/Abuse can be reused within 5 years.un-check box un-sucessfully", 1);
 					throw new Exception(stepName+"User has NOT been Unchecked Pennsylvania Neglect/Abuse can be reused within 5 years.un-check box un-sucessfully");
 				}
 				
 				
 			}
 			
 			sc.clickWhenElementIsClickable("otherConfig_Save_btn", timeOutInSeconds);
 			retval = Globals.KEYWORD_PASS;
        }catch(Exception e){
        		sc.STAF_ReportEvent("Fail","Verifying the Pennsylvania Neglect/Abuse can be reused within 5 years Check box", "Unable to Check Pennsylvania Neglect/Abuse can be reused within 5 years check box sucessfully", 1);
               throw e;
        }
        
        return retval;
 }

	 /**************************************************************************************************
	 * Method to configure Product Specific Settings for Max BG Age for Invited Share
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author lvkumar
	 * @throws Exception
	 ***************************************************************************************************/
	public static String MaxBGAgesettings() throws Exception{
		APP_LOGGER.startFunction("productSpecificSettingsforAbusePensylvania");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try {
			String stepName = "Other Configurations Tab for productSpecificSetting";
			tempRetval = sc.waitforElementToDisplay("otherConfig_edit_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
				throw new Exception(stepName+"Page hasnt loaded");
			}
			sc.clickWhenElementIsClickable("otherConfig_edit_btn",(int)  timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("manageAccount_otherConfigurations_productSpecificSettings", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail",stepName,"product Specific Settings section has not been loaded",1);
				throw new Exception(stepName+"product Specific Settings section has not been loaded");
			}
//			sc.checkCheckBox("manageAccount_otherConfigurations_productSpecificSetting_chks");
			String AbusePensylvaniacheckboxReq;
			AbusePensylvaniacheckboxReq = Globals.testSuiteXLS.getCellData_fromTestData("AbusePensylvaniacheckbox");
			if (AbusePensylvaniacheckboxReq.equalsIgnoreCase("Yes")){
				tempRetval = sc.checkCheckBox("manageAccount_otherConfigurations_productSpecificSetting_chks");
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Pass", stepName, "User has been checked Pennsylvania Neglect/Abuse can be reused within 5 years.check box sucessfully", 1);
				}else{
					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been checked Pennsylvania Neglect/Abuse can be reused within 5 years.check box un-sucessfully", 1);
					throw new Exception(stepName+"User has NOT been checked Pennsylvania Neglect/Abuse can be reused within 5 years.check box un-sucessfully");
				}
				
			}else{
				tempRetval = sc.uncheckCheckBox("manageAccount_otherConfigurations_productSpecificSetting_chks");
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Pass", stepName, "User has been Unchecked Pennsylvania Neglect/Abuse can be reused within 5 years.un-check box sucessfully", 1);
				}else{
					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been Unchecked Pennsylvania Neglect/Abuse can be reused within 5 years.un-check box un-sucessfully", 1);
					throw new Exception(stepName+"User has NOT been Unchecked Pennsylvania Neglect/Abuse can be reused within 5 years.un-check box un-sucessfully");
				}
			}
			
			sc.clickWhenElementIsClickable("otherConfig_Save_btn", timeOutInSeconds);
			retval = Globals.KEYWORD_PASS;
       }catch(Exception e){
       		sc.STAF_ReportEvent("Fail","Verifying the Pennsylvania Neglect/Abuse can be reused within 5 years Check box", "Unable to Check Pennsylvania Neglect/Abuse can be reused within 5 years check box sucessfully", 1);
              throw e;
       }
       
       return retval;
}
	
 	 /**************************************************************************************************
 	 * Method to configure EditClientFeatures RatingRestrictions Check Box
 	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
 	 * @author lvkumar
 	 * @throws Exception
 	 ***************************************************************************************************/
 	public static String RatingRestrictionssettings() throws Exception{
 		APP_LOGGER.startFunction("RatingRestrictionssettings");
 		String retval = Globals.KEYWORD_FAIL;
 		String tempRetval = Globals.KEYWORD_FAIL;
 		int timeOutInSeconds = 20;
 		try {
 			String stepName = "EditClientFeatures RatingRestrictions Check Box";
 			tempRetval = sc.waitforElementToDisplay("otherConfig_ClientFeatures_link", timeOutInSeconds);
 			
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
 				throw new Exception(stepName+"Page hasnt loaded");
 			}
 			sc.clickWhenElementIsClickable("otherConfig_ClientFeatures_link",(int)  timeOutInSeconds);

 			tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_ClientFeature_lable",  timeOutInSeconds);
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
 				throw new Exception(stepName+"Page hasnt loaded");
 			}
 			sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_btn",(int)  timeOutInSeconds);

 			tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_RatingRestrictions_lable", timeOutInSeconds);
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 				sc.STAF_ReportEvent("Fail",stepName,"Rating Restrictions section has not been loaded",1);
 				throw new Exception(stepName+"Rating Restrictions section has not been loaded");
 			}
 			sc.checkCheckBox("otherConfig_EditClientFeatures_RatingRestrictions_chks");
 			sc.scrollIntoView("otherConfig_EditClientFeatures_RatingRestrictions_chks");
 			String RatingRestrictionscheckboxReq;
 			RatingRestrictionscheckboxReq = Globals.testSuiteXLS.getCellData_fromTestData("RatingRestrictionscheckbox");
 			if (RatingRestrictionscheckboxReq.equalsIgnoreCase("Yes")){
 				tempRetval = sc.checkCheckBox("otherConfig_EditClientFeatures_RatingRestrictions_chks");
 				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 					sc.STAF_ReportEvent("Pass", stepName, "User has been checked RatingRestrictions check box sucessfully", 1);
 				}else{
 					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been checked RatingRestrictions check box un-sucessfully", 1);
 					throw new Exception(stepName+"User has NOT been checked RatingRestrictions check box un-sucessfully");
 				}
 				
 			}else{
 				tempRetval = sc.uncheckCheckBox("otherConfig_EditClientFeatures_RatingRestrictions_chks");
 				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 					sc.STAF_ReportEvent("Pass", stepName, "User has been Unchecked RatingRestrictions un-check box sucessfully", 1);
 				}else{
 					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been Unchecked RatingRestrictions un-check box un-sucessfully", 1);
 					throw new Exception(stepName+"User has NOT been Unchecked RatingRestrictions un-check box un-sucessfully");
 				}
 				
 			}
 			
 			sc.waitforElementToDisplay("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
 			sc.STAF_ReportEvent("Pass", stepName, "User has updated the check box of RatingRestrictions sucessfully", 1);
 			sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
 			retval = Globals.KEYWORD_PASS;
        }catch(Exception e){
        		sc.STAF_ReportEvent("Fail","Verifying the RatingRestrictions Check box", "Unable to Check RatingRestrictions check box sucessfully", 1);
               throw e;
        }
        return retval;
 }
	 /**************************************************************************************************
	 * Method to configure Rating Restrictions | Client Setup | Custom Settings for Restrictions 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author mshaik
	 * @throws Exception
	 ***************************************************************************************************/
	public static String RatingRestrictions() throws Exception{
		APP_LOGGER.startFunction("RatingRestrictions");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String cellvalue1;
		String cellvalue2;
		try {
			String stepName = "Other Configurations Tab for productSpecificSetting";
			tempRetval = sc.waitforElementToDisplay("otherConfig_edit_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
				throw new Exception(stepName+"Page hasnt loaded");
			}
			sc.clickWhenElementIsClickable("otherConfig_edit_btn",(int)  timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_edit_addRestrictions_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail",stepName,"Rating Restrictions buttons is not present",1);
				throw new Exception(stepName+"Rating Restrictions buttons is not present");
			}
			sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_addRestrictions_btn", timeOutInSeconds);
			WebElement RatingRestrictions_tbl=sc.createWebElement("otherConfig_EditClientFeatures_edit_RatingRestrictions_tbl");
			int Rowcount=sc.getRowCount_tbl(RatingRestrictions_tbl);
			cellvalue1=RandomStringUtils.randomAlphabetic(10);
			cellvalue2=RandomStringUtils.randomAlphabetic(10);
			WebElement tempEditBoxCol2=RatingRestrictions_tbl.findElement(By.xpath("//tbody/tr["+Rowcount+"]/td[2]/input[@name='Name']"));
			WebElement tempEditBoxCol3=RatingRestrictions_tbl.findElement(By.xpath("//tbody/tr["+Rowcount+"]/td[3]/input[@name='Description']"));
			sc.setValueJsChange(tempEditBoxCol2, cellvalue1);
			sc.setValueJsChange(tempEditBoxCol3, cellvalue2);
			//WebElement tempDeleteButtonCol4=RatingRestrictions_tbl.findElement(By.xpath("//tbody/tr["+Rowcount+"]/td[4]"));
			tempRetval=sc.waitforElementToDisplay("otherConfig_Save_btn", timeOutInSeconds);
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","Update Button","Update Button has not been loaded",1);
				throw new Exception("Update Button has not been loaded");
			}
			sc.clickWhenElementIsClickable("otherConfig_Save_btn", timeOutInSeconds);			
			retval = Globals.KEYWORD_PASS;
			WebElement RatingRestrictions2_tbl=sc.createWebElement("otherConfig_EditClientFeatures_edit_RatingRestrictions_tbl");
			int Rowcount2=sc.getRowCount_tbl(RatingRestrictions2_tbl);
			List<WebElement> deletebutton2=RatingRestrictions2_tbl.findElements(By.xpath("//tbody/tr["+Rowcount2+"]/td[4]"));
			if (deletebutton2.size() ==1){
				sc.STAF_ReportEvent("Pass", "Verifying Rating Rest..row", "Rating..Restri row has been added sucessfully", 1);
				}else{
				sc.STAF_ReportEvent("Fail", "Verifying Rating Rest..row", "Rating..Restri row has not been added sucessfully", 1);
			}
			retval=Globals.KEYWORD_PASS;
			tempRetval = sc.waitforElementToDisplay("otherConfig_edit_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
				throw new Exception(stepName+"Page hasnt loaded");
			}
			sc.clickWhenElementIsClickable("otherConfig_edit_btn",(int)  timeOutInSeconds);
			WebElement RatingRestrictions3_tbl=sc.createWebElement("otherConfig_EditClientFeatures_edit_RatingRestrictions_tbl");
			WebElement tempDeleteButtonCol44=RatingRestrictions3_tbl.findElement(By.xpath("//tbody/tr["+Rowcount+"]/td[4]"));
			int Rowcount3=sc.getRowCount_tbl(RatingRestrictions3_tbl);
			List<WebElement> deletebutton3=RatingRestrictions3_tbl.findElements(By.xpath("//tbody/tr["+Rowcount3+"]/td[4]/button[@class='new btn btn-link' and not(contains(@style,'none'))]"));
			if (deletebutton3.size() ==1){
				sc.STAF_ReportEvent("pass", "Verfiy existing Rating Res.. row", "Existing Rating..Restriction row is displaying sucessfullyl", 1);
				sc.clickWhenElementIsClickable(tempDeleteButtonCol44, timeOutInSeconds);
				sc.STAF_ReportEvent("pass", "Verfiy existing Rating Res.. row", "Existing Rating..Restriction row is deleted sucessfullyl", 1);
			}else{
				sc.STAF_ReportEvent("Fail", "Verfiy existing Rating Res.. row", "Existing Rating..Restriction row is not displaying sucessfullyl", 1);
			}
			retval=Globals.KEYWORD_PASS;
			
			tempRetval=sc.waitforElementToDisplay("otherConfig_RatingRestrictions_cancel_btn", timeOutInSeconds);
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","Cancel Button","Cancel Button has not been loaded",1);
				throw new Exception("Cancel Button has not been loaded");
			}
			sc.clickWhenElementIsClickable("otherConfig_RatingRestrictions_cancel_btn", timeOutInSeconds);
			sc.STAF_ReportEvent("pass", "Verfiy Cancel button", "User clicked cancel button sucessfully", 1);
			WebElement RatingRestrictions4_tbl=sc.createWebElement("otherConfig_EditClientFeatures_edit_RatingRestrictions_tbl");
			WebElement tempDeleteButtonCol444=RatingRestrictions4_tbl.findElement(By.xpath("//tbody/tr["+Rowcount+"]/td[4]"));
			int Rowcount4=sc.getRowCount_tbl(RatingRestrictions4_tbl);
			List<WebElement> deletebutton4=RatingRestrictions4_tbl.findElements(By.xpath("//tbody/tr["+Rowcount4+"]/td[4]/button[@class='new btn btn-link' and not(contains(@style,'none'))]"));
			if (deletebutton4.size() ==1){
				sc.STAF_ReportEvent("pass", "Verfiy existing Rating Res.. row", "Existing Rating..Restriction row is not deleted until user clicks on update button after deletting the row", 1);
				}else{
				sc.STAF_ReportEvent("Fail", "Verfiy existing Rating Res.. row", "Existing Rating..Restriction row is deleted even if the user has not updated before clicks on the cancel button", 1);
			}
			retval=Globals.KEYWORD_PASS;
			tempRetval = sc.waitforElementToDisplay("otherConfig_edit_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
				throw new Exception(stepName+"Page hasnt loaded");
			}
			sc.clickWhenElementIsClickable("otherConfig_edit_btn",(int)  timeOutInSeconds);
			sc.clickWhenElementIsClickable(tempDeleteButtonCol444, timeOutInSeconds);
			tempRetval=sc.waitforElementToDisplay("otherConfig_Save_btn", timeOutInSeconds);
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","Update Button","Update Button has not been loaded",1);
				throw new Exception("Update Button has not been loaded");
			}
			sc.clickWhenElementIsClickable("otherConfig_Save_btn", timeOutInSeconds);	
			retval = Globals.KEYWORD_PASS;
       }catch(Exception e){
       		sc.STAF_ReportEvent("Fail","Verifying the Rating Restrictions webtable columns", "Unable to add the new Rating Restrictions sections Row", 1);
              throw e;
       }
       return retval;			
}
	 /**************************************************************************************************
		 * Method to configure Rating Restrictions | Client Setup | Custom Settings for Restrictions 
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author mshaik
		 * @throws Exception
		 ***************************************************************************************************/
		public static String RatingRestrictions1() throws Exception{
			APP_LOGGER.startFunction("RatingRestrictions1");
			String retval = Globals.KEYWORD_FAIL;
			String tempRetval = Globals.KEYWORD_FAIL;
			int timeOutInSeconds = 20;
			String cellvalue1;
			String cellvalue2;
			try {
				String stepName = "Other Configurations Tab for productSpecificSetting";
				tempRetval = sc.waitforElementToDisplay("otherConfig_edit_btn", timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
					throw new Exception(stepName+"Page hasnt loaded");
				}
				sc.clickWhenElementIsClickable("otherConfig_edit_btn",(int)  timeOutInSeconds);
				tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_edit_addRestrictions_btn", timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail",stepName,"Rating Restrictions buttons is not present",1);
					throw new Exception(stepName+"Rating Restrictions buttons is not present");
				}
				sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_addRestrictions_btn", timeOutInSeconds);
				WebElement RatingRestrictions_tbl=sc.createWebElement("otherConfig_EditClientFeatures_edit_RatingRestrictions_tbl");
				int Rowcount=sc.getRowCount_tbl(RatingRestrictions_tbl);
				cellvalue1=RandomStringUtils.randomAlphabetic(10);
				cellvalue2=RandomStringUtils.randomAlphabetic(10);
				//cellvalue2=sc.getCellData_tbl(RatingRestrictions_tbl, Rowcount, 2);
				WebElement tempEditBoxCol2=RatingRestrictions_tbl.findElement(By.xpath("//tbody/tr["+Rowcount+"]/td[2]/input[@name='Name']"));
				WebElement tempEditBoxCol3=RatingRestrictions_tbl.findElement(By.xpath("//tbody/tr["+Rowcount+"]/td[3]/input[@name='Description']"));
				sc.setValueJsChange(tempEditBoxCol2, cellvalue1);
				sc.setValueJsChange(tempEditBoxCol3, cellvalue2);
				WebElement tempDeleteButtonCol4=RatingRestrictions_tbl.findElement(By.xpath("//tbody/tr["+Rowcount+"]/td[4]"));
				List<WebElement> deletebutton=RatingRestrictions_tbl.findElements(By.xpath("//tbody/tr["+Rowcount+"]/td[4]"));
				if (deletebutton.size() ==1){
					sc.STAF_ReportEvent("Pass", "Verifying Rating Rest..row", "Newly added Rating Rest..row is displaying", 1);
					sc.clickWhenElementIsClickable(tempDeleteButtonCol4, timeOutInSeconds);
					sc.STAF_ReportEvent("Pass", "Verifying Rating Rest..row", "Newly added Rating Rest..row is deleted sucessfully", 1);
				}else{
					sc.STAF_ReportEvent("Fail", "Verifying Rating Rest..row", "Newly added Rating Rast..row is not displaying", 1);
				}
				tempRetval=sc.waitforElementToDisplay("otherConfig_RatingRestrictions_cancel_btn", timeOutInSeconds);
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail","Cancel Button","Cancel Button has not been loaded",1);
					throw new Exception("Cancel Button has not been loaded");
				}
				sc.clickWhenElementIsClickable("otherConfig_RatingRestrictions_cancel_btn", timeOutInSeconds);
				WebElement RatingRestrictions2_tbl=sc.createWebElement("otherConfig_EditClientFeatures_edit_RatingRestrictions_tbl");
				int Rowcount2=sc.getRowCount_tbl(RatingRestrictions2_tbl);
				List<WebElement> deletebutton2=RatingRestrictions2_tbl.findElements(By.xpath("//tbody/tr["+Rowcount2+"]/td[4]/button[@class='new btn btn-link' and not(contains(@style,'none'))]"));
				if (deletebutton2.size() == Rowcount){
					sc.STAF_ReportEvent("Fail", "Verfiy newly added Rating Res.. when user clicks on cancel button","Newly added Rating Restriction is displaying as user not updated before click on the cancel button" , 1);
				}else{
					sc.STAF_ReportEvent("pass", "Verfiy newly added Rating Res.. when user clicks on cancel button","Newly added Rating Restriction is not displaying until user clicks on the update button", 1);
				}
				retval=Globals.KEYWORD_PASS;
	       }catch(Exception e){
	       		sc.STAF_ReportEvent("Fail","Verifying the Rating Restrictions webtable columns", "Unable to add the new columns for Rating Restrictions sections table", 1);
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
		public static String CreatePosition()throws Exception {
			APP_LOGGER.startFunction("CreatePosition");
			String retval=Globals.KEYWORD_FAIL;
			String tempRetval=Globals.KEYWORD_FAIL;
			String positionName="";
			String positionProducts="";
			String regJuris="";
			String OrderHoldQueue="";
			String setRegJurFlag="False";
			int timeOutinSeconds = 20;

			try{
				tempRetval=sc.waitforElementToDisplay("manageAccount_positions_link",timeOutinSeconds);

				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

					sc.clickWhenElementIsClickable("manageAccount_positions_link", timeOutinSeconds);

					tempRetval=Globals.KEYWORD_FAIL;
					tempRetval = sc.waitforElementToDisplay("position_searchPosition_txt",timeOutinSeconds);

					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						sc.STAF_ReportEvent("Pass", "Position Creation", " Position page has been loaded" , 1);

						//					Globals.driver.findElement(LocatorAccess.getLocator("positions_newPosition_btn")).click();
						sc.clickWhenElementIsClickable("positions_newPosition_btn",timeOutinSeconds  );

						//					positionName=Globals.testSuiteXLS.getCellData_fromTestData("PositionName");
						positionName = sc.updateAndFetchRuntimeValue("Position_runtimeUpdateFlag","PositionName",10);
						positionProducts=Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts");

						log.debug("Creating New Position |PositionName-  "+positionName + " | Products - "+positionProducts);
						sc.STAF_ReportEvent("Pass", "Position Creation", " New Position - "+positionName+" to be created for Product -"+positionProducts , 1);

						tempRetval = sc.setValueJsChange("newPosition_positionName_txt", positionName);

						String[] products = positionProducts.split("-"); //+ delimiter cannot be used as Exception would be thrown-java.util.regex.PatternSyntaxException: Dangling meta character '+' near index 0
						VV_Products expProduct = VV_Products.NA;
						
						for (String p : products){
							p=p.trim().toUpperCase();

							if(p.contains("ABUSE")){
								expProduct = VV_Products.valueOf("ABUSE");
							}else{
								expProduct = VV_Products.valueOf(p);	
							}


							log.debug("Creating New Position | Products - "+p + " is being added");

							if(expProduct == VV_Products.L1){
								tempRetval = sc.selectValue_byVisibleText("newPosition_level_dd", "Level 1 - Basic Criminal Locator Search");

							}else if(expProduct == VV_Products.L2){
								tempRetval = sc.selectValue_byVisibleText("newPosition_level_dd", "Level 2 - Advanced Criminal Locator Search");
							}else if(expProduct == VV_Products.L3){
								tempRetval = sc.selectValue_byVisibleText("newPosition_level_dd", "Level 3 - Complete Criminal Locator Search");
							}else if(expProduct == VV_Products.CREDIT){
								tempRetval = sc.checkCheckBox("newPosition_creditCheck_chk");
							}else if(expProduct == VV_Products.MVR){
								tempRetval = sc.checkCheckBox("newPosition_mvr_chk");
							}else if(expProduct.toString().contains("ABUSE")){
								//need to check for ABUSE(PA)
								tempRetval = sc.checkCheckBox("newPosition_abuse_chk");
								if(p.equalsIgnoreCase("ABUSE(PA)")){
									tempRetval=sc.selectValue_byVisibleText("newPosition_abuseJuris_dd","Pennsylvania");
								}else if(p.equalsIgnoreCase("ABUSE(CO)")){
									tempRetval=sc.selectValue_byVisibleText("newPosition_abuseJuris_dd","Colorado");
								}
							}else if(expProduct == VV_Products.OIG){
								tempRetval = sc.checkCheckBox("newPosition_oig_chk");
							}else if(expProduct == VV_Products.REF){
								tempRetval = sc.checkCheckBox("newPosition_referenceCheck_chk");
							}else if(expProduct == VV_Products.SSNPROF){
								tempRetval = sc.checkCheckBox("newPosition_ssnProfile_chk");
							}else if(expProduct == VV_Products.CBSV){
								tempRetval = sc.checkCheckBox("newPosition_cbsv_chk");

							}else if(expProduct == VV_Products.ID){
								tempRetval = sc.checkCheckBox("newPosition_id_chk");
							}else if(expProduct == VV_Products.LS){
								tempRetval = sc.checkCheckBox("newPosition_ls_chk");
							}else if(expProduct == VV_Products.COUNTYCIVIL){
								tempRetval = sc.checkCheckBox("newPosition_ls_countycivil");
							}else if(expProduct == VV_Products.FEDCIVIL){
								tempRetval = sc.checkCheckBox("newPosition_ls_fedcivil");
							}else if(expProduct == VV_Products.GLOBEX){
								tempRetval = sc.checkCheckBox("newPosition_globex_chk");
							}else if(expProduct == VV_Products.FEDCRIM){
								tempRetval = sc.checkCheckBox("newPosition_ls_fedcrim");
							}
							else{
								sc.STAF_ReportEvent("Fail", "Position Creation", "TestData-Error-Product is not supported.Product-"+p, 0);
								log.error("Product - "+ p + " Not suppported yet");
							}
							
							if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
								sc.STAF_ReportEvent("Pass", "Position Creation", p+" has been added to position" , 1);
							}else{
								sc.STAF_ReportEvent("Fail", "Position Creation", p+" could not be added to position" , 1);
							}

						}
						
	                    // Veena- Coded added for Order Hold queue flag
						String 	StepName = "Order Hold Queue";
						String expectdText;
						int timeOutInSecond = 20;
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							String fieldLevelValidationReuired =  Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
							if(fieldLevelValidationReuired.equalsIgnoreCase("Yes")){
								boolean HoldQueuechk = Globals.driver.findElement(LocatorAccess.getLocator("newPosition_holdqueue_chk")).isSelected();
								boolean  HoldQueueAmt = Globals.driver.findElement(LocatorAccess.getLocator("newPosition_holdqueue_txt")).isEnabled();
								String HoldQueueAmtTxt = Globals.driver.findElement(LocatorAccess.getLocator("newPosition_holdqueue_txt")).getAttribute("placeholder");
								if((HoldQueuechk == false) && (HoldQueueAmt == false) && HoldQueueAmtTxt.equalsIgnoreCase("$ Amount")){
									retval = Globals.KEYWORD_PASS;
									sc.STAF_ReportEvent("Pass", StepName, "Hold Amount Field is disabled with correct Place holder value when Hold Queue checkbox is unchecked", 1);
								}
								else{
									sc.STAF_ReportEvent("Fail", StepName, "Hold Amount Field is not disabled or incorrect  Place holder value is displayed or Hold Queue checkbox is checked",1);
									throw new Exception("Hold Amount Field is not disabled ");
								}
								
								tempRetval = sc.checkCheckBox("newPosition_holdqueue_chk"); 
								sc.clickWhenElementIsClickable("newPosition_create_btn", timeOutInSecond);

								expectdText = "Please review the information that you entered: minHoldAmount";
								tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
								
								if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
									 
									sc.STAF_ReportEvent("Pass", "To validate hold min amount is mandatory field ", "Warning message displayed as expected - "+expectdText,1);

								}else{	
									sc.STAF_ReportEvent("Fail", "To validate hold min amount is not mandatory field or incorrect error message is displayed", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
								}
								
								sc.checkCheckBox("newPosition_holdqueue_chk"); 
								sc.setValueJsChange("newPosition_holdqueue_txt", "-1");
								sc.clickWhenElementIsClickable("newPosition_create_btn", timeOutInSecond);

								expectdText = "Please review the information that you entered: minHoldAmount";
								tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
								if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
									 
									sc.STAF_ReportEvent("Pass", "To validate hold min amount is not accepting negative value new position on VV", "Warning message displayed as expected - "+expectdText,1);

								}else{	
									sc.STAF_ReportEvent("Fail", "To validate hold min amount is accepting negative value new position on VV", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
								}
								
								sc.checkCheckBox("newPosition_holdqueue_chk"); 
								sc.setValueJsChange("newPosition_holdqueue_txt", "1000");
								tempRetval = sc.verifyProperty("newPosition_holdqueue_txt", "value", "1000");
								
								if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
									 
									sc.STAF_ReportEvent("Pass", "Validation for Hold Amount", "To validate hold min amount is not accepting value > 999 for new position on VV",1);

								}else{	
									sc.STAF_ReportEvent("Fail", "Validation for Hold Amount", "To validate hold min amount is accepting value > 999 for  new position on VV ", 1);
								}
								
                                sc.checkCheckBox("newPosition_holdqueue_chk"); 
								sc.setValueJsChange("newPosition_holdqueue_txt", "abc");

								sc.clickWhenElementIsClickable("newPosition_create_btn", timeOutInSecond);

								expectdText = "Please review the information that you entered: minHoldAmount";
								tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

								if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

									sc.STAF_ReportEvent("Pass", "To validate hold min amount is not accepting alphabets ", "Warning message displayed as expected - "+expectdText,1);

								}else{	
									sc.STAF_ReportEvent("Fail", "To validate hold min amount is accepting alphabets or incorrect error message is displayed", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
								}

								sc.checkCheckBox("newPosition_holdqueue_chk"); 
								sc.setValueJsChange("newPosition_holdqueue_txt", "@$1");
								sc.clickWhenElementIsClickable("newPosition_create_btn", timeOutInSecond);

								expectdText = "Please review the information that you entered: minHoldAmount";
								tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

								if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

									sc.STAF_ReportEvent("Pass", "To validate hold min amount is not accepting special characters ", "Warning message displayed as expected - "+expectdText,1);

								}else{	
									sc.STAF_ReportEvent("Fail", "To validate hold min amount is accepting special characters or incorrect error message is displayed", "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
								}
							
							}
							
							String orderHoldQAmt = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldqueueamount");
							if(orderHoldQAmt.isEmpty() || orderHoldQAmt.equalsIgnoreCase("") || orderHoldQAmt == null){
								 sc.uncheckCheckBox("newPosition_holdqueue_chk"); 
							}else{
								 sc.checkCheckBox("newPosition_holdqueue_chk"); 
								 tempRetval = sc.setValueJsChange("newPosition_holdqueue_txt", orderHoldQAmt);
								 if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
									 
										sc.STAF_ReportEvent("Pass", "Position with Hold order is created","To validate hold min amount is set for new position on VV",1);

									}else{	
										sc.STAF_ReportEvent("Fail", "Position with Hold order is not created","To validate hold min amount  value is not set for new position on VV", 1);
									}
								 
							}
							
																				
							
						}
						
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							regJuris=Globals.testSuiteXLS.getCellData_fromTestData("RegJuris");
							if(regJuris.equalsIgnoreCase("Yes")){
								  if((positionProducts.toUpperCase()).contains("L2") || (positionProducts.toUpperCase()).contains("L3") ){
									  tempRetval = sc.checkCheckBox("newPosition_regjuris_chk");
								  }else{
							    	   throw new Exception("Regulated Jurisdiction only applied when position having product L2 or L3"); 
							       }
							  }else{
								  tempRetval = sc.uncheckCheckBox("newPosition_regjuris_chk");
							   } 
							
							if (regJuris == "Yes" & tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
								sc.STAF_ReportEvent("Pass", "Position Creation with Regulated Jurisdiction","has been added to position" , 1);
							}else if(regJuris == "Yes" & tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)) {
								sc.STAF_ReportEvent("Pass", "osition Creation with Regulated Jurisdiction"," could not be added to position" , 1);
							}
						}

						//					Globals.driver.findElement(LocatorAccess.getLocator("newPosition_create_btn")).click();
						sc.clickWhenElementIsClickable("newPosition_create_btn",timeOutinSeconds  );
						long timeOutInSeconds=10;
						tempRetval = sc.waitforElementToDisplay("positionListings_posListingGrid_tbl", timeOutInSeconds);
						if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.STAF_ReportEvent("Pass", "Position Creation", "New Position created-"+positionName , 1);
						    tempRetval=sc.waitforElementToDisplay("manageAccount_positions_link",timeOutinSeconds);
					        sc.clickWhenElementIsClickable("manageAccount_positions_link", timeOutinSeconds);
							//postion tab click
							//search position
							//verify details
							retval = Globals.KEYWORD_PASS;
						}else{
							sc.STAF_ReportEvent("Fail", "Position Creation", "Unabel to create new position-"+positionName , 1);
						}
					}

				}

			}catch (Exception e){
				log.error("Unable to create new position -  " + positionName + " | Exception - " + e.toString());
				throw e;
			}
			return retval;
		}


/**************************************************************************************************
 * Method to validate the  new position created in VV Admin Portal for a client
 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
 * @author vgokulanathan
 * @throws Exception
 ***************************************************************************************************/
public static int verifyCreatedPosition() throws Exception{
	APP_LOGGER.startFunction("searchPosition");
	int retval=Globals.INT_FAIL;
	String PositionName="";
	int rowCount = Globals.INT_FAIL;

	try {
		if (sc.waitforElementToDisplay("position_searchPosition_txt",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){


			sc.STAF_ReportEvent("Pass", "Position Page", "Position Page has been loaded" , 1);


			PositionName=Globals.testSuiteXLS.getCellData_fromTestData("PositionName");
			String positionNameInUI;
			String holdAmt;
			String regJuris;
			String orderHoldQAmt = "$"+Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldqueueamount");
			String reguJuris=Globals.testSuiteXLS.getCellData_fromTestData("RegJuris");
			sc.setValueJsChange("position_searchPosition_txt", PositionName);			
			sc.clickWhenElementIsClickable("position_searchPosition_btn", 10);
			boolean positionFound = false;
			int i=0;
			if (sc.waitforElementToDisplay("positions_positionGrid_tbl",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){ 
				WebElement positionsTable = sc.createWebElement("positions_positionGrid_tbl");
				rowCount=sc.getRowCount_tbl(positionsTable);
				if(rowCount != Globals.INT_FAIL){
					log.info("Method searchPosition | Position result grid displayed with " + rowCount + " position(s)" );
					if(rowCount==1){
						positionNameInUI = sc.getCellData_tbl(positionsTable, 1, 1);
						if(positionNameInUI.contains("No matching records")){
							positionFound= false;
						}else{
							positionNameInUI = sc.getCellData_tbl(positionsTable, 1, 2);
							holdAmt = sc.getCellData_tbl(positionsTable, 1, 8);
							regJuris = sc.getCellData_tbl(positionsTable, 1, 7);
							if((positionNameInUI.equalsIgnoreCase(PositionName))&& (holdAmt.equalsIgnoreCase(orderHoldQAmt))&& (regJuris.equalsIgnoreCase(reguJuris))  ){
								positionFound = true;
								i=1;
								sc.STAF_ReportEvent("Pass", "Positions Page", "Position Name/Hold value amount is matched and Regulated Juris is set for the created position" , 1);
							}
						}
					}else{
						for(i= 1;i<=rowCount;i++){
							//TODO - need to fetch the column position dynamically.
							positionNameInUI = sc.getCellData_tbl(positionsTable, i, 2);
							holdAmt = sc.getCellData_tbl(positionsTable, 1, 8);
							regJuris = sc.getCellData_tbl(positionsTable, 1, 7);
							if((positionNameInUI.equalsIgnoreCase(PositionName))&& (holdAmt.equalsIgnoreCase(orderHoldQAmt))&&(regJuris.equalsIgnoreCase(reguJuris)) ){
								positionFound = true;
								sc.STAF_ReportEvent("Pass", "Positions Page", "Position Name/Hold value amount is matched and Regulated Juris is set for the created position" , 1);
								break;
							}

						}
					}
					
					
				}

				if(positionFound == true){
					return i; // row number of the account found
				}



			}else{
				log.info("Method searchPosition | Position Grid is not displayed| Position Name:- "+ PositionName);

			}
		}else{
			sc.STAF_ReportEvent("Fail", "Positions Page", "Position Search has NOT been loaded" , 1);
			log.error("Unable to Search Position as Position Search field is not displayed");
		}		


	} catch (Exception e) {
		// TODO Auto-generated catch block
		log.error("Inhouse Portal-searchPosition | Position Name - "+ PositionName + "|  Exception occurred - " + e.toString());
		throw e;
	}


	return retval;
}

/**************************************************************************************************
 * Method to validate the edit position in VV Admin Portal for a client
 * 
 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL
 *         otherwise
 * @author vgokulanathan
 * @throws Exception
 ***************************************************************************************************/
public static String EditPosition() throws Exception {
	APP_LOGGER.startFunction("EditPosition");
	String retval = Globals.KEYWORD_FAIL;
	int rowID = Globals.INT_FAIL;
	String tempRetval = Globals.KEYWORD_FAIL;
	int timeOutinSeconds = 20;
	String expectdText;

	try {

		tempRetval = sc.waitforElementToDisplay("manageAccount_positions_link", timeOutinSeconds);
		sc.clickWhenElementIsClickable(	"manageAccount_positions_link", timeOutinSeconds);
		String PositionName = Globals.testSuiteXLS.getCellData_fromTestData("PositionName");
		sc.setValueJsChange("position_searchPosition_txt", PositionName);			
		sc.clickWhenElementIsClickable("position_searchPosition_btn", 10);
		sc.clickWhenElementIsClickable("positions_positionEdit_btn", 10);
		WebElement positionsTable = sc.createWebElement("positions_positionGrid_tbl");
		
		String orderHoldQAmt = "$"+ Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldqueueamount");
			
		String FieldValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			
		if(FieldValidation.equalsIgnoreCase("Yes")){
			String PositionName1 = Globals.testSuiteXLS.getCellData_fromTestData("PositionName")+ "-EditedPosition";
			positionsTable.findElements(By.xpath("//input[@data-bind='value: Name']")).clear();
			
			sc.setValueJsChange("positions_positionEdit_txt", PositionName1);
			Globals.testSuiteXLS.setCellData_inTestData("PositionName", PositionName1);
			
			positionsTable.findElements(By.xpath("//input[@data-bind='value: MinHoldAmount']")).clear();

			sc.setValueJsChange("positions_holdAmtEdit_txt", "@$1");
			sc.clickWhenElementIsClickable("positions_positionUpdate_btn", timeOutinSeconds);

			expectdText = "Please enter a number.";
			tempRetval = sc.verifyText(positionsTable.findElement(By.xpath("//span[contains(text(),'Please enter a number.')]")), expectdText);

			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {

				sc.STAF_ReportEvent("Pass","To validate hold min amount is not accepting special characters ","Warning message displayed as expected - "+ expectdText, 1);

			} else {
				sc.STAF_ReportEvent("Fail","To validate hold min amount is accepting special characters or incorrect error message is displayed","Mismatch in warning message displayed.Excpected - "+ expectdText, 1);
			}

			Thread.sleep(10000);
			positionsTable.findElements(By.xpath("//input[@data-bind='value: MinHoldAmount']")).clear();
			sc.setValueJsChange("positions_holdAmtEdit_txt", "-1");
			sc.clickWhenElementIsClickable("positions_positionUpdate_btn", timeOutinSeconds);

			expectdText = "Please check this value.";
			tempRetval = sc.verifyText(positionsTable.findElement(By.xpath("//span[contains(text(),'Please check this value.')]")), expectdText);
			
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				sc.STAF_ReportEvent("Pass","To validate hold min amount is not accepting negative value ","Warning message displayed as expected - "+ expectdText, 1);

			} else {
				sc.STAF_ReportEvent("Fail","To validate hold min amount is accepting negative value or incorrect error message is displayed","Mismatch in warning message displayed.Excpected - "+ expectdText, 1);
			}
			Thread.sleep(10000);
		}
		String OrderHoldqueueamount =  Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldqueueamount");
		positionsTable.findElements(By.xpath("//input[@data-bind='value: MinHoldAmount']")).clear();
		sc.setValueJsChange("positions_holdAmtEdit_txt", OrderHoldqueueamount);
		sc.clickWhenElementIsClickable("positions_positionUpdate_btn", 10);
		sc.STAF_ReportEvent("Pass","Order Hold queue amount  Edit","OrderHoldQAmt edited to new value",	1);
		if(FieldValidation.equalsIgnoreCase("Yes")){
			String Position = Globals.testSuiteXLS.getCellData_fromTestData("PositionName");
			String EditPositionName = sc.getCellData_tbl(positionsTable, 1, 2);
			String EditorderHoldQAmt = sc.getCellData_tbl(positionsTable, 1, 8);
			if (EditPositionName.equalsIgnoreCase(Position)&& EditorderHoldQAmt.equalsIgnoreCase(orderHoldQAmt)) {
				sc.STAF_ReportEvent("Pass","Position Edit","Position Name and orderHoldQAmt edited to new value",1);
			} else {
				sc.STAF_ReportEvent(
						"Fail","Position Edit","Position Name and orderHoldQAmt could not be edited to new value",1);
			}
			
		}
			retval = Globals.KEYWORD_PASS;

	} catch (Exception e) {
		sc.STAF_ReportEvent("Fail", "Position Search","Exception occurred while searching Position", 1);
		log.error("Inhouse Portal-Position Search | Unable to search Position|  Exception occurred - "+ e.toString());
		throw e;
	}

	return retval;
}

/**************************************************************************************************
 * Method to reset the edited position name to original value in VV Admin
 * Portal for a client
 * 
 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL
 *         otherwise
 * @author vgokulanathan
 * @throws Exception
 ***************************************************************************************************/
public static String ResetEditPosition() throws Exception {
	APP_LOGGER.startFunction("ResetEditPosition");
	String retval = Globals.KEYWORD_FAIL;
	String tempRetval = Globals.KEYWORD_FAIL;
	int timeOutinSeconds = 20;

	try {
		tempRetval = sc.waitforElementToDisplay(
				"manageAccount_positions_link", timeOutinSeconds);
		sc.clickWhenElementIsClickable(
				"manageAccount_positions_link", timeOutinSeconds);
		String PositionName = Globals.testSuiteXLS
				.getCellData_fromTestData("PositionName")
				;
		
		sc.setValueJsChange("position_searchPosition_txt", PositionName);
		sc.clickWhenElementIsClickable("position_searchPosition_btn",
				10);
		WebElement positionsTable = sc
				.createWebElement("positions_positionGrid_tbl");
		String PositionNameO = "EditPosition-L2-MVR";
		sc.clickWhenElementIsClickable("positions_positionEdit_btn",
				10);
		positionsTable.findElements(
				By.xpath("//input[@data-bind='value: Name']")).clear();
		sc.setValueJsChange("positions_positionEdit_txt", PositionNameO);
		Globals.testSuiteXLS.setCellData_inTestData("PositionName", PositionNameO);
		positionsTable.findElements(
				By.xpath("//input[@data-bind='value: MinHoldAmount']"))
				.clear();
		sc.setValueJsChange("positions_holdAmtEdit_txt", "20");
		sc.clickWhenElementIsClickable(
				"positions_positionUpdate_btn", 10);
		String EditPositionNameO = sc.getCellData_tbl(positionsTable,
				1, 2);
		String EditorderHoldQAmtO = sc.getCellData_tbl(
				positionsTable, 1, 8);
		if (EditPositionNameO.equalsIgnoreCase(PositionNameO)
				&& EditorderHoldQAmtO.equalsIgnoreCase("$20")) {
			sc.STAF_ReportEvent(
					"Pass",
					"Position Reset",
					"Position Name and orderHoldQAmt reset to original value",
					1);
		} else {
			sc.STAF_ReportEvent(
					"Fail",
					"Position Reset",
					"Position Name and orderHoldQAmt could not be reset to original value",
					1);
		}
		retval = Globals.KEYWORD_PASS;

	} catch (Exception e) {
		sc.STAF_ReportEvent("Fail", "Position Search",
				"Exception occurred while searching Position", 1);
		log
				.error("Inhouse Portal-Position Search | Unable to search Position|  Exception occurred - "
						+ e.toString());
		throw e;
	}

	return retval;
}
 /**************************************************************************************************
 * Method to configure EditClientFeatures OrderHoldQueue Check Box
 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
 * @author lvkumar
 * @throws Exception
 ***************************************************************************************************/
public static String OrderHoldQueuesettings() throws Exception{
	APP_LOGGER.startFunction("OrderHoldQueuesettings");
	String retval = Globals.KEYWORD_FAIL;
	String tempRetval = Globals.KEYWORD_FAIL;
	int timeOutInSeconds = 20;
	try {
		String stepName = "EditClientFeatures OrderHoldQueue Check Box";
		tempRetval = sc.waitforElementToDisplay("otherConfig_ClientFeatures_link", timeOutInSeconds);
		
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
			throw new Exception(stepName+"Page hasn't loaded");
		}
		sc.clickWhenElementIsClickable("otherConfig_ClientFeatures_link",(int)  timeOutInSeconds);

		tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_OrderHoldQueue_lable",  timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Page hasn't loaded", 1);
			throw new Exception(stepName+"Page hasn't loaded");
		}
		sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_btn",(int)  timeOutInSeconds);

		tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_OrderHoldQueue_lable", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail",stepName,"Order Hold Queue section has not been loaded",1);
			throw new Exception(stepName+"Order Hold Queue section has not been loaded");
		}
		sc.checkCheckBox("otherConfig_EditClientFeatures_OrderHoldQueue_chks");
		String OrderHoldQueuecheckboxReq;
		OrderHoldQueuecheckboxReq = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldQueuecheckbox");
		if (OrderHoldQueuecheckboxReq.equalsIgnoreCase("Yes")){
			tempRetval = sc.checkCheckBox("otherConfig_EditClientFeatures_OrderHoldQueue_chks");
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "User has been checked Order Hold Queue check box sucessfully", 1);
			}else{
				sc.STAF_ReportEvent("Fail", stepName, "User has NOT been checked Order Hold Queue check box un-sucessfully", 1);
				throw new Exception(stepName+"User has NOT been checked Order Hold Queue check box un-sucessful");
			}
			
		}else{
			tempRetval = sc.uncheckCheckBox("otherConfig_EditClientFeatures_OrderHoldQueue_chks");
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "User has been Unchecked Order Hold Queue un-check box sucessful", 1);
			}else{
				sc.STAF_ReportEvent("Fail", stepName, "User has NOT been Unchecked Order Hold Queue un-check box un-sucessful", 1);
				throw new Exception(stepName+"User has NOT been Unchecked Order Hold Queue un-check box un-sucessful");
			}
			
		}
		
		sc.waitforElementToDisplay("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
		sc.STAF_ReportEvent("Pass", stepName, "User has updated the check box of Order Hold Queue sucessful", 1);
		sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
		retval = Globals.KEYWORD_PASS;
   }catch(Exception e){
   		sc.STAF_ReportEvent("Fail","Verifying the Order Hold Queue Check box", "Unable to Check Order Hold Queue check box sucessful", 1);
          throw e;
   }
   return retval;
}
	/**************************************************************************************************
	 * Method to configure EditClientFeatures ShowClientConfigOnReport Check Box
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author Navisha
	 * @throws Exception
 	***************************************************************************************************/
	public static String ShowClientConfigOnReport() throws Exception{

		APP_LOGGER.startFunction("CustomFieldsReportVisibility");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		String tempRetval1 = Globals.KEYWORD_FAIL;
		String tempRetval2 = Globals.KEYWORD_FAIL;
		String tempRetval3 = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try {
		String stepName = "EditClientFeatures ShowClientConfigOnReport CheckBox";
		tempRetval = sc.waitforElementToDisplay("otherConfig_ClientFeatures_link", timeOutInSeconds);
		
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
			throw new Exception(stepName+"Page hasnt loaded");
			}
		sc.clickWhenElementIsClickable("otherConfig_ClientFeatures_link",(int)  timeOutInSeconds);

		tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_edit_btn",  timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
			throw new Exception(stepName+"Page hasnt loaded");
			}
		sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_btn",(int)  timeOutInSeconds);
	
	
		tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_ClientFeatures_lable", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail",stepName,"Client Features Page has not been loaded",1);
			throw new Exception(stepName+"Client Features Page has not been loaded");
			}
		
		String customBGReportSetting = Globals.testSuiteXLS.getCellData_fromTestData("ShowClientConfigOnReportcheckbox");
		String[] indvCustomFieldSetting =  customBGReportSetting.split(";");
			if (indvCustomFieldSetting[0].equalsIgnoreCase("Yes")){
			tempRetval1=sc.checkCheckBox("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport1_chks");
				if(tempRetval1.equalsIgnoreCase(Globals.KEYWORD_PASS) ){
				
				sc.STAF_ReportEvent("Pass", stepName, "User has been checked ShowClientConfigOnReport1 check box sucessful", 1);
				}else{
				sc.STAF_ReportEvent("Fail", stepName, "User has NOT been checked ShowClientConfigOnReport1 check box un-sucessful", 1);
				throw new Exception(stepName+"User has NOT been checked ShowClientConfigOnReport1 check box un-sucessful");
			}
			sc.scrollIntoView("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport3_chks");
		}
			else{
			tempRetval1=sc.uncheckCheckBox("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport1_chks");
				if(tempRetval1.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.scrollIntoView("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport3_chks");
				sc.STAF_ReportEvent("Pass", stepName, "User has been Unchecked ShowClientConfigOnReport1 check box sucessful", 1);
			}	else{
				sc.STAF_ReportEvent("Fail", stepName, "User has NOT been Unchecked ShowClientConfigOnReport1 check box un-sucessfully", 1);
				throw new Exception(stepName+"User has NOT been Unchecked ShowClientConfigOnReport1 check box un-sucessfully");
			}
			}
				if (indvCustomFieldSetting[1].equalsIgnoreCase("Yes")){
					tempRetval2=sc.checkCheckBox("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport2_chks");
						if(tempRetval2.equalsIgnoreCase(Globals.KEYWORD_PASS) ){
				
							sc.STAF_ReportEvent("Pass", stepName, "User has been checked ShowClientConfigOnReport2 check box sucessful", 1);
			}			else{
					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been checked ShowClientConfigOnReport2 check box un-sucessful", 1);
					throw new Exception(stepName+"User has NOT been checked ShowClientConfigOnReport2 check box un-sucessful");
			}
						sc.scrollIntoView("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport2_chks");
		}
				else{
					tempRetval2=sc.uncheckCheckBox("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport2_chks");
					if(tempRetval2.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						sc.scrollIntoView("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport2_chks");
						sc.STAF_ReportEvent("Pass", stepName, "User has been Unchecked ShowClientConfigOnReport2 check box sucessful", 1);
			}		else{
					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been Unchecked ShowClientConfigOnReport2 check box un-sucessfully", 1);
					throw new Exception(stepName+"User has NOT been Unchecked ShowClientConfigOnReport2 check box un-sucessfully");
			}
		}
		if (indvCustomFieldSetting[2].equalsIgnoreCase("Yes")){
			tempRetval3=sc.checkCheckBox("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport3_chks");
			if(tempRetval3.equalsIgnoreCase(Globals.KEYWORD_PASS) ){
				
				sc.STAF_ReportEvent("Pass", stepName, "User has been checked ShowClientConfigOnReport3 check box sucessful", 1);
			}else{
				sc.STAF_ReportEvent("Fail", stepName, "User has NOT been checked ShowClientConfigOnReport3 check box un-sucessful", 1);
				throw new Exception(stepName+"User has NOT been checked ShowClientConfigOnReport3 check box un-sucessful");
			}
			sc.scrollIntoView("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport3_chks");
		}
		else{
			tempRetval3=sc.uncheckCheckBox("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport3_chks");
			if(tempRetval3.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.scrollIntoView("otherConfig_EditClientFeatures_edit_ShowClientConfigOnReport3_chks");
				sc.STAF_ReportEvent("Pass", stepName, "User has been Unchecked ShowClientConfigOnReport3 check box sucessful", 1);
			}else{
				sc.STAF_ReportEvent("Fail", stepName, "User has NOT been Unchecked ShowClientConfigOnReport3 check box un-sucessfully", 1);
				throw new Exception(stepName+"User has NOT been Unchecked ShowClientConfigOnReport3 check box un-sucessfully");
			}
		}
		
	
		sc.waitforElementToDisplay("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
		sc.STAF_ReportEvent("Pass", stepName, "User has updated the check box of ShowClientConfigOnReport sucessful", 1);
		sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
		retval = Globals.KEYWORD_PASS;
	}catch(Exception e){
		sc.STAF_ReportEvent("Fail","Verifying the ShowClientConfigOnReport Check box", "Unable to Check ShowClientConfigOnReport check box sucessful", 1);
       throw e;
		}
		return retval;
	}
	
	 /**************************************************************************************************
	 * Method to configure EditClientFeatures Bg Dashboard - Batch Printing Check Box
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author lvkumar
	 * @throws Exception
	 ***************************************************************************************************/
	public static String BatchPrintingsettings() throws Exception{
		APP_LOGGER.startFunction("BatchPrintingsettings");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try {
			String stepName = "EditClientFeatures Bg Dashboard - Batch Printing Check Box";
			tempRetval = sc.waitforElementToDisplay("otherConfig_ClientFeatures_link", timeOutInSeconds);
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Page hasnt loaded", 1);
				throw new Exception(stepName+"Page hasn't loaded");
			}
			sc.clickWhenElementIsClickable("otherConfig_ClientFeatures_link",(int)  timeOutInSeconds);

			tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_edit_btn",  timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Page hasn't loaded", 1);
				throw new Exception(stepName+"Page hasn't loaded");
			}
			sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_btn",(int)  timeOutInSeconds);

			tempRetval = sc.waitforElementToDisplay("otherConfig_EditClientFeatures_BatchPrinting_lable", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail",stepName,"Batch Printing section has not been loaded",1);
				throw new Exception(stepName+"Batch Printing section has not been loaded");
			}
			sc.checkCheckBox("otherConfig_EditClientFeatures_BatchPrinting_chks");
			String BatchPrintingcheckboxReq;
			BatchPrintingcheckboxReq = Globals.testSuiteXLS.getCellData_fromTestData("BatchPrintingcheckbox");
			if (BatchPrintingcheckboxReq.equalsIgnoreCase("Yes")){
				tempRetval = sc.checkCheckBox("otherConfig_EditClientFeatures_BatchPrinting_chks");
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Pass", stepName, "User has been checked Batch Printing check box sucessful", 1);
				}else{
					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been checked Batch Printing check box un-sucessful", 1);
					throw new Exception(stepName+"User has NOT been checked Batch Printing check box un-sucessful");
				}
				
			}else{
				tempRetval = sc.uncheckCheckBox("otherConfig_EditClientFeatures_BatchPrinting_chks");
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Pass", stepName, "User has been Unchecked Batch Printing un-check box sucessful", 1);
				}else{
					sc.STAF_ReportEvent("Fail", stepName, "User has NOT been Unchecked Batch Printing un-check box un-sucessful", 1);
					throw new Exception(stepName+"User has NOT been Unchecked Batch Printing un-check box un-sucessful");
				}
				
			}
			
			sc.waitforElementToDisplay("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
			sc.STAF_ReportEvent("Pass", stepName, "User has updated the check box of Batch Printing sucessful", 1);
			sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_update_btn", timeOutInSeconds);
			retval = Globals.KEYWORD_PASS;
	   }catch(Exception e){
	   		sc.STAF_ReportEvent("Fail","Verifying the Batch Printing Check box", "Unable to Check Batch Printing check box sucessful", 1);
	          throw e;
	   }
	   return retval;
	}

	/**************************************************************************************************
	 * Method to validate UserName in Inhouseportal UserCreation
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author Navisha
	 * @throws Exception
 	***************************************************************************************************/


	public static String verifyUserNameFieldUsersTab(String value,String validation) throws Exception{

		APP_LOGGER.startFunction("verifyUserNameFieldUsersTab_Inhouseportal");
 		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 5;
		WebElement username=Globals.driver.findElement(LocatorAccess.getLocator("newUser_username_txt"));
		username.clear();
 		tempRetval = sc.setValueJsChange("newUser_username_txt", value);
		//first time click doesnt actually click
			sc.clickWhenElementIsClickable("newUser_checkUsername_btn", timeOutInSeconds);

			tempRetval = sc.waitforElementToDisplay("newUser_checkUsername_btn", 3);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.clickWhenElementIsClickable("newUser_checkUsername_btn", timeOutInSeconds);
			}

			tempRetval = Globals.KEYWORD_FAIL;
			sc.waitforElementToDisplay("newUser_usernameErrorValidation_txt", 20);
			WebElement usernameErrorValidation = sc.createWebElement("newUser_usernameErrorValidation_txt");
			int lengthDisplayed=username.getAttribute("value").length();
			int valueLength=value.length();
			
			if(validation.equalsIgnoreCase("negative")){
				if(lengthDisplayed==valueLength){
					if (usernameErrorValidation.getText().equalsIgnoreCase("Invalid username, must be between 8-50 chars")){
						sc.STAF_ReportEvent("Pass", "User Creation", "Negative validation for username is successful- "+value, 1);
						retval=Globals.KEYWORD_PASS;
					}
					else{
						sc.STAF_ReportEvent("Fail", "User Creation", "Failed during negative validation", 1);
						retval=Globals.KEYWORD_FAIL;
					}	
				}
				else{
					sc.STAF_ReportEvent("Pass", "User Creation", "Username Field does not accept characters above 50 characters "+value, 1);
				}
			}
			if(validation.equalsIgnoreCase("positive"))
			{
				tempRetval=sc.waitforElementToDisplay("newUser_checkUsernameSuccess_btn", 20);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "User Creation", "Positive validation for username is successful- "+value, 1);
				retval=Globals.KEYWORD_PASS;
			}	else{
				sc.STAF_ReportEvent("Fail", "User Creation", "Failed during negative validation", 1);
				retval=Globals.KEYWORD_FAIL;
			}
				
			}
			
		
		return retval;
 	}
	

	/**************************************************************************************************
	 * Method to configure the sharing level for volunteer using limit volunteer sharing
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String configureSharingSetting() throws Exception{
		APP_LOGGER.startFunction("configureSharingSetting");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 20;

		String stepName = "SHaring Settings";
		tempretval =  sc.waitforElementToDisplay("clientBGDashboard_manageMyVolunteer_link", timeOutInSeconds );

		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Email Settings Tab has not been loaded", 1);
			throw new Exception("Email Settings Tab has not been loaded");
		}
		
		sc.clickWhenElementIsClickable("emailSettings_edit_btn", (int) timeOutInSeconds);
		tempretval = sc.waitforElementToDisplay("emailSettings_addURL_btn", timeOutInSeconds);
				
		String expSharingLevel =  Globals.testSuiteXLS.getCellData_fromTestData("SharingLevel");
		String value ="0";
		if(expSharingLevel.contains("Level 1")){
			value="1";
		}else if(expSharingLevel.contains("Level 2")){
			value="2";
		}else if(expSharingLevel.contains("Level 3")){
			value="3";
		}
		
		sc.scrollIntoView("emailSetting_sharingLevel_dd");
		sc.selectValue_byValue("emailSetting_sharingLevel_dd", value);
		
		sc.clickWhenElementIsClickable("emailSettings_submit_btn", (int) timeOutInSeconds);
		tempretval = sc.waitforElementToDisplay("emailSettings_ok_btn", timeOutInSeconds);
		sc.clickWhenElementIsClickable("emailSettings_ok_btn", (int) timeOutInSeconds);
		
		tempretval = sc.waitforElementToDisplay("emailSettings_edit_btn", timeOutInSeconds);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Sharing Settings has not been saved", 1);
			throw new Exception("Email Settings has not been saved");
		}
		sc.STAF_ReportEvent("Pass", stepName, "Sharing Settings has been saved", 1);
		retval = Globals.KEYWORD_PASS;
		//end simulatiion
		sc.clickWhenElementIsClickable("endEmulation_endEmulation_link", (int) timeOutInSeconds);


		return retval;

	}
	/**************************************************************************************************
	 * Method to set data in Notes
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulantahan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String createNotes() throws Exception{

		APP_LOGGER.startFunction("createNotes");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;
		String Notes =  Globals.testSuiteXLS.getCellData_fromTestData("Notes");

		try {

			sc.clickWhenElementIsClickable("notes__notes_btn", timeOutInSeconds);
			tempRetval =  sc.waitforElementToDisplay("notes_Notestxt_txt", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-createNotes - Popup has not been loaded for Notes creation");
				return retval;
			}
			else{
				tempRetval = sc.setValueJsChange("notes_Notestxt_txt", Notes);
				sc.clickWhenElementIsClickable("notes_update_btn", timeOutInSeconds);
				retval = tempRetval;
			}
			
		}
		catch (Exception e) {
			log.error("Method-createNotes | Unable to create Notes in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify set data in Notes 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulantahan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyNotes() throws Exception{

		APP_LOGGER.startFunction("verifyNotes");
		String retval = Globals.KEYWORD_FAIL;

		try {
            
			WebElement element = Globals.driver.findElement(LocatorAccess.getLocator("notes__notes_btn"));
			String expNote =  Globals.testSuiteXLS.getCellData_fromTestData("Notes");
			String actNote = element.getAttribute("title");
			
			if(actNote.equalsIgnoreCase(expNote)){
				sc.STAF_ReportEvent("Pass", "Verify Notes", "Note text is as expected"+expNote+"Actual:"+actNote, 1);
				retval = Globals.KEYWORD_PASS;
			}
			else{
				sc.STAF_ReportEvent("Fail", "Verify Notes", "Note text is not as expected"+expNote+"Actual:"+actNote, 1);
				retval = Globals.KEYWORD_FAIL;
			}
			
		}
		catch (Exception e) {
			log.error("Method-createNotes | Unable to create Notes in Inhouse Portal | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to configure otherConfigurations for client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String otherConfigurations() throws Exception{
		APP_LOGGER.startFunction("otherConfigurations");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try {
            
			sc.clickWhenElementIsClickable("manageAccount_otherConfigurations_link", timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("otherConfig_hitDelivery_txt", timeOutInSeconds);
			verifyNotes();
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-otherConfigurations - Page has not been loaded for otherConfigurations tab");
				return retval;
			}

			sc.setValueJsChange("otherConfig_hitDelivery_txt", "test@test.com");

			sc.clickWhenElementIsClickable("otherConfig_Save_btn", timeOutInSeconds);
			retval = tempRetval;
		}catch (Exception e) {
			log.error("Method-otherConfigurations | Unable to setup otherConfigurations | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to configure Questionnaire for client
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String Questionnaire() throws Exception{
		APP_LOGGER.startFunction("Questionnaire");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		try {
            
			sc.clickWhenElementIsClickable("manageAccount_questionnaire_link", timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("Questionnaire_create_btn", timeOutInSeconds);
			verifyNotes();
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-Questionnaire - Page has not been loaded for Questionnaire tab");
				return retval;
			}
			//sc.clickWhenElementIsClickable("Questionnaire_create_btn", timeOutInSeconds);
			retval = tempRetval;
		}catch (Exception e) {
			log.error("Method-Questionnaire | Unable to setup Questionnaire | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to update the Notes and verify the updated notes in all client setup pages
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String VerifyUpdatedNotes() throws Exception {
		APP_LOGGER.startFunction("VerifyUpdatedNotes");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			
			int rowId   = 	Globals.INT_FAIL;
		    rowId	=	searchAccount();

		    if(rowId == Globals.INT_FAIL){
			    sc.STAF_ReportEvent("Fail", "Search Account", "Unable to search account", 1);
			    return Globals.KEYWORD_FAIL;
		    }
		    else{
		    	Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+rowId+"]")).click();
		    	tempretval =  sc.waitforElementToDisplay("newAccCompanyInfo_accountName_txt", timeOutInSeconds);
		    	if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
		    		sc.STAF_ReportEvent("Fail", "Main Account Page", "Main Account Page not loaded", 1);
				    return Globals.KEYWORD_FAIL;
		    	}
		        tempretval = createNotes();
		    	if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
		    		sc.STAF_ReportEvent("Fail", "Update Notes-Main page", "Unable to update Notes", 1);
				    return Globals.KEYWORD_FAIL;
		    	}
		    	sc.STAF_ReportEvent("Pass", "Update Notes-Main page", "Notes Updated Successfully", 1);
		    	sc.clickWhenElementIsClickable("manageAccount_staffing_link", timeOutInSeconds);
		    	tempretval =  sc.waitforElementToDisplay("staffing_primaryAM_dd", timeOutInSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Staffing", "Page has not been loaded", 1);
					return Globals.KEYWORD_FAIL;
				}
				tempretval = verifyNotes();
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes-Staffing Page", "Updated Note is not visible", 1);
					return Globals.KEYWORD_FAIL;
				}
				sc.STAF_ReportEvent("Pass", "VerifyUpdatedNotes-Staffing Page", "Updated Note is visible on the Page", 1);
				sc.clickWhenElementIsClickable("manageAccount_users_link", timeOutInSeconds);
				tempretval =  sc.waitforElementToDisplay("users_searchUser_txt", timeOutInSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "User", "Page has not been loaded", 1);
					return Globals.KEYWORD_FAIL;
				}
				tempretval = verifyNotes();
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes-User Page", "Updated Note is not visible", 1);
					return Globals.KEYWORD_FAIL;
				}
				sc.STAF_ReportEvent("Pass", "VerifyUpdatedNotes-User Page", "Updated Note is visible on the Page", 1);
				sc.clickWhenElementIsClickable("manageAccount_pricing_link", timeOutInSeconds);
				tempretval =  sc.waitforElementToDisplay("pricing_addPricing_link", timeOutInSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Pricing", "Page has not been loaded", 1);
					return Globals.KEYWORD_FAIL;
				}
				tempretval = verifyNotes();
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes-Pricing Page", "Updated Note is not visible", 1);
					return Globals.KEYWORD_FAIL;
				}
				sc.STAF_ReportEvent("Pass", "VerifyUpdatedNotes-Pricing Page", "Updated Note is visible on the Page", 1);
				sc.clickWhenElementIsClickable("manageAccount_biling_link", timeOutInSeconds);
				tempretval =  sc.waitforElementToDisplay("billing_attn_txt", timeOutInSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Billing", "Page has not been loaded", 1);
					return Globals.KEYWORD_FAIL;
				}
				tempretval = verifyNotes();
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes-Billing Page", "Updated Note is not visible", 1);
					return Globals.KEYWORD_FAIL;
				}
				sc.STAF_ReportEvent("Pass", "VerifyUpdatedNotes-Billing Page", "Updated Note is visible on the Page", 1);
				sc.clickWhenElementIsClickable("manageAccount_authorization_link", timeOutInSeconds);
				tempretval =  sc.waitforElementToDisplay("authorization_productsGrid_tbl", timeOutInSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Authorization", "Page has not been loaded", 1);
					return Globals.KEYWORD_FAIL;
				}
				tempretval = verifyNotes();
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes-Authorization Page", "Updated Note is not visible", 1);
					return Globals.KEYWORD_FAIL;
				}
				sc.STAF_ReportEvent("Pass", "VerifyUpdatedNotes-Authorization Page", "Updated Note is visible on the Page", 1);
				sc.clickWhenElementIsClickable("manageAccount_positions_link", timeOutInSeconds);
				tempretval = sc.waitforElementToDisplay("position_searchPosition_txt", timeOutInSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "PositionListing", "Page has not been loaded", 1);
					return Globals.KEYWORD_FAIL;
				}
				tempretval = verifyNotes();
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes-PositionListing Page", "Updated Note is not visible", 1);
					return Globals.KEYWORD_FAIL;
				}
				sc.STAF_ReportEvent("Pass", "VerifyUpdatedNotes-PositionListing Page", "Updated Note is visible on the Page", 1);
				sc.clickWhenElementIsClickable("manageAccount_otherConfigurations_link", timeOutInSeconds);
				tempretval = sc.waitforElementToDisplay("otherConfig_hitDelivery_txt", timeOutInSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "OtherConfigurations", "Page has not been loaded", 1);
					return Globals.KEYWORD_FAIL;
				}
				tempretval = verifyNotes();
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes-OtherConfigurations Page", "Updated Note is not visible", 1);
					return Globals.KEYWORD_FAIL;
				}
				sc.STAF_ReportEvent("Pass", "VerifyUpdatedNotes-OtherConfigurations Page", "Updated Note is visible on the Page", 1);
				sc.clickWhenElementIsClickable("manageAccount_questionnaire_link", timeOutInSeconds);
				tempretval = sc.waitforElementToDisplay("Questionnaire_create_btn", timeOutInSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Questionnaire", "Page has not been loaded", 1);
					return Globals.KEYWORD_FAIL;
				}
				tempretval = verifyNotes();
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes-Questionnaire page", "Updated Note is not visible", 1);
					return Globals.KEYWORD_FAIL;
				}
				sc.STAF_ReportEvent("Pass", "VerifyUpdatedNotes-Questionnaire Page", "Updated Note is visible on the Page", 1);
				sc.clickWhenElementIsClickable("manageAccount_apiConfiguration_link", timeOutInSeconds);
				tempretval = sc.waitforElementToDisplay("associatedAccounts_edit_btn", 10);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "ApiConfiguration", "Page has not been loaded", 1);
					return Globals.KEYWORD_FAIL;
				}
				tempretval = verifyNotes();
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes-ApiConfiguration Page", "Updated Note is not visible", 1);
					return Globals.KEYWORD_FAIL;
				}
				sc.STAF_ReportEvent("Pass", "VerifyUpdatedNotes-ApiConfiguration Page", "Updated Note is visible on the Page", 1);
				sc.clickWhenElementIsClickable("manageAccount_associatedAccounts_link", timeOutInSeconds);
				tempretval = sc.waitforElementToDisplay("associatedAccounts_edit_btn", timeOutInSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "AssociatedAccounts", "Page has not been loaded", 1);
					return Globals.KEYWORD_FAIL;
				}
				tempretval = verifyNotes();
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes-AssociatedAccounts Page", "Updated Note is not visible", 1);
					return Globals.KEYWORD_FAIL;
				}
				sc.STAF_ReportEvent("Pass", "VerifyUpdatedNotes-AssociatedAccounts Page", "Updated Note is visible on the Page", 1);
				sc.clickWhenElementIsClickable("manageAccount_availableStates_link", timeOutInSeconds);
				tempretval = sc.waitforElementToDisplay("associatedAccounts_edit_btn", timeOutInSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "AvailableStates", "Page has not been loaded", 1);
					return Globals.KEYWORD_FAIL;
				}
				tempretval = verifyNotes();
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes-AvailableStates Page", "Updated Note is not visible", 1);
					return Globals.KEYWORD_FAIL;
				}
				sc.STAF_ReportEvent("Pass", "VerifyUpdatedNotes-AvailableStates Page", "Updated Note is visible on the Page", 1);
				retval=Globals.KEYWORD_PASS;
		    }
			
		} catch (Exception e) {
			sc.STAF_ReportEvent("Fail", "VerifyUpdatedNotes", "Exception occurred while updating or verifying the updated Notes", 1);
			throw e;
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to update the Notes and verify the updated notes in all client setup pages
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String newIntegrationUser() throws Exception {
		APP_LOGGER.startFunction("newIntegrationUser");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {

			int rowId   = 	Globals.INT_FAIL;
		    rowId	=	searchAccount();

		    if(rowId == Globals.INT_FAIL){
			    sc.STAF_ReportEvent("Fail", "Search Account", "Unable to search account", 1);
			    return Globals.KEYWORD_FAIL;
		    }
		    else{
		    	Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+rowId+"]")).click();
		    	tempretval =  sc.waitforElementToDisplay("newAccCompanyInfo_accountName_txt", timeOutInSeconds);
		    	if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
		    		sc.STAF_ReportEvent("Fail", "Main Account Page", "Main Account Page not loaded", 1);
				    return Globals.KEYWORD_FAIL;
		    	}
		    	
		    	navigateToUsersTab();
		    	newUserCreation("No","apiUser");
		    	retval=Globals.KEYWORD_PASS;
		    }
		} catch (Exception e) {
			log.error("Method-IntegrationUserLogin | Unable to execute IntegrationUserLogin | Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to unlock a client user account
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String unlockClientUser() throws Exception {
		APP_LOGGER.startFunction("unlockClientUser");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {

			int rowId   = 	Globals.INT_FAIL;
		    rowId	=	searchAccount();

		    if(rowId == Globals.INT_FAIL){
			    sc.STAF_ReportEvent("Fail", "Search Account", "Unable to search account", 1);
			    return Globals.KEYWORD_FAIL;
		    }
		    else{
		    	Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+rowId+"]")).click();
		    	tempretval =  sc.waitforElementToDisplay("newAccCompanyInfo_accountName_txt", timeOutInSeconds);
		    	if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
		    		sc.STAF_ReportEvent("Fail", "Main Account Page", "Main Account Page not loaded", 1);
				    return Globals.KEYWORD_FAIL;
		    	}
		    	
		    	navigateToUsersTab();
		    	int userrowId   = 	Globals.INT_FAIL;
		    	userrowId	= searchUser();
		    	if(rowId == Globals.INT_FAIL){
				    sc.STAF_ReportEvent("Fail", "Search User", "Unable to search User", 1);
				    return Globals.KEYWORD_FAIL;
			    }
			    else{
			    	Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+rowId+"]")).click();
			    	tempretval =  sc.waitforElementToDisplay("newUser_title_txt", timeOutInSeconds);
			    	if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			    		sc.STAF_ReportEvent("Fail", "User Page", "User Page not loaded", 1);
					    return Globals.KEYWORD_FAIL;
			    	}
			    	else{
			    		sc.clickWhenElementIsClickable("users_edit_btn", timeOutInSeconds);
			    		String resetPwdFlag=sc.testSuiteXLS.getCellData_fromTestData("ExistingAPIUser_Reset_change_pwd");
			    		if (resetPwdFlag.equalsIgnoreCase("Org_Pass_Reset")){
			    			tempretval = sc.setValueJsChange("newUser_emailAddress_txt", Globals.fromEmailID);
			    		}else{
			    			tempretval = sc.uncheckCheckBox("users_locked_chk");
			    		}
			    	
			    		sc.clickWhenElementIsClickable("newUser_create_btn", timeOutInSeconds);
			    	}
		    	retval=Globals.KEYWORD_PASS;
		    }
		}} catch (Exception e) {
			log.error("Method-unlockClientUser | Unable to execute unlockClientUser | Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}

		return retval;
	}
	
	/**************************************************************************************************
	 * Method to search an user in Inhouse portal
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static int searchUser() throws Exception{
		APP_LOGGER.startFunction("searchUser");
		int retval=Globals.INT_FAIL;
		String userName="";
		int rowCount = Globals.INT_FAIL;

		try {
			if (sc.waitforElementToDisplay("users_searchUser_txt",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){


				sc.STAF_ReportEvent("Pass", "InhousePortalUserPage", "User Search Page has been loaded" , 1);


				userName=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
				WebElement searchUser = sc.createWebElement("users_searchUser_txt");
				sc.setValueJsChange("users_searchUser_txt", userName);			
				searchUser.sendKeys(Keys.ENTER);
				Thread.sleep(1000);			

				boolean userFound = false;
				int i=0;
				if (sc.waitforElementToDisplay("users_userGrid_tbl",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){ 
					WebElement usersTable = sc.createWebElement("users_userGrid_tbl");
					rowCount=sc.getRowCount_tbl(usersTable);
					System.out.println("++++++++++"+rowCount);
					if(rowCount != Globals.INT_FAIL){
						log.info("Method searchUser | User result grid displayed with " + rowCount + " account(s)" );

						String userNameInUI;

						if(rowCount==1){
							userNameInUI = sc.getCellData_tbl(usersTable, 1, 1);
							if(userNameInUI.contains("No matching records")){
								userFound= false;
							}else{
								userNameInUI = sc.getCellData_tbl(usersTable, 1, 2);
								if(userNameInUI.equalsIgnoreCase(userName)){
									userFound = true;
									i=1;
									
								}
							}
						}else{
							for(i= 1;i<=rowCount;i++){
								//TODO - need to fetch the column position dynamically.
								userNameInUI = sc.getCellData_tbl(usersTable, i, 2);
								if(userNameInUI.equalsIgnoreCase(userName)){
									userFound = true;
									break;
								}

							}
						}
						
						
					}

					if(userFound == true){
						return i; // row number of the user found
					}



				}else{
					log.info("Method searchUser | User Grid is not displayed| Account Name:- "+ userName);

				}
			}else{
				sc.STAF_ReportEvent("Fail", "InhousePortalUserPage", "User Search has NOT been loaded" , 1);
				log.error("Unable to Search User as User Search field is not displayed");
			}		


		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Inhouse Portal-searchUser | User Name - "+ userName + "|  Exception occurred - " + e.toString());
			throw e;
		}




		return retval;
	}
	/**************************************************************************************************
	 * Method to unlock a client user account
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String updateEmailForUser() throws Exception {
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {

			int rowId   = 	Globals.INT_FAIL;
		    rowId	=	searchAccount();

		    if(rowId == Globals.INT_FAIL){
			    sc.STAF_ReportEvent("Fail", "Search Account", "Unable to search account", 1);
			    return Globals.KEYWORD_FAIL;
		    }
		    else{
		    	Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+rowId+"]")).click();
		    	tempretval =  sc.waitforElementToDisplay("newAccCompanyInfo_accountName_txt", timeOutInSeconds);
		    	if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
		    		sc.STAF_ReportEvent("Fail", "Main Account Page", "Main Account Page not loaded", 1);
				    return Globals.KEYWORD_FAIL;
		    	}
		    	
		    	navigateToUsersTab();
		    	int userrowId   = 	Globals.INT_FAIL;
		    	userrowId	= searchUser();
		    	if(rowId == Globals.INT_FAIL){
				    sc.STAF_ReportEvent("Fail", "Search User", "Unable to search User", 1);
				    return Globals.KEYWORD_FAIL;
			    }
			    else{
			    	Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+rowId+"]")).click();
			    	tempretval =  sc.waitforElementToDisplay("newUser_title_txt", timeOutInSeconds);
			    	if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			    		sc.STAF_ReportEvent("Fail", "User Page", "User Page not loaded", 1);
					    return Globals.KEYWORD_FAIL;
			    	}
			    	else{
			    		sc.clickWhenElementIsClickable("users_edit_btn", timeOutInSeconds);
			    		tempretval = sc.setValueJsChange("newUser_emailAddress_txt", Globals.fromEmailID);
			    		sc.clickWhenElementIsClickable("newUser_create_btn", timeOutInSeconds);
			    	}
		    	retval=Globals.KEYWORD_PASS;
		    }
		}} catch (Exception e) {
			log.error("Method-unlockClientUser | Unable to execute unlockClientUser | Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}

		return retval;
	}

}
