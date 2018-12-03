package com.sterlingTS.vv;




import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.sterlingTS.generic.appLib.AdminClient;
//import com.google.common.util.concurrent.Service.State;
import com.sterlingTS.seleniumUI.seleniumCommands;
import com.sterlingTS.utils.commonUtils.APP_LOGGER;
import com.sterlingTS.utils.commonUtils.BaseCommon;
import com.sterlingTS.utils.commonUtils.CommonHelpMethods;
import com.sterlingTS.utils.commonUtils.Email;
import com.sterlingTS.utils.commonUtils.LocatorAccess;
import com.sterlingTS.utils.commonUtils.ProtectedEncryption;
import com.sterlingTS.utils.commonUtils.XMLUtils;
//import com.sterlingTS.utils.commonUtils.database.DAO;
import com.sterlingTS.utils.commonVariables.Enum.State;
import com.sterlingTS.utils.commonVariables.Enum.VV_Products;
import com.sterlingTS.utils.commonVariables.Globals;



public class ClientFacingApp extends BaseCommon{
	
	public static Logger log = Logger.getLogger(AdminClient.class);
	
	public static seleniumCommands sc = new seleniumCommands(driver);
	
	

	/**************************************************************************************************
	 * Method to navigate to volunteer dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String navigateVolunteerDashboard() {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			sc.waitforElementToDisplay("clientBGDashboard_manageMyVolunteer_link", timeOutinSeconds);
			//			System.out.println(tempRetval);
			//sc.clickWhenElementIsClickable("clientBGDashboard_manageMyVolunteer_link", (int) timeOutinSeconds);
			sc.clickElementUsingJavaScript("clientBGDashboard_manageMyVolunteer_link");  // IE 11 required java script click
			if(sc.waitforElementToDisplay("volDashboard_orderBackgroundCheck_btn", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){
				retval=Globals.KEYWORD_PASS;
				log.debug("Successfully navigated to Volunteer Dashboard page");
			}
			else{
				sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Unable to  navigated to Volunteer Dashboard page", 1);
				log.error("Unable to  navigated to Volunteer Dashboard page");
			}

		}catch(Exception e){
			log.error("Unable to  navigated to Volunteer Dashboard page | Exception - "+e.toString());
		}


		return retval;


	}

	/**************************************************************************************************
	 * Method to create client ordering
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String ClientOrdering() throws Exception{
		APP_LOGGER.startFunction("ClientOrdering");
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 60;
		Exception myException = new Exception("Error in Client Ordering");

		try{
			clickOnCreateClientOrder();

			tempRetval = sc.waitforElementToDisplay("clientStep1_choosePosition_dd", timeOutInSeconds);
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				clientOrderingStep1();
				clientOrderingStep2();

				//alias name STEP-3 would nly be visible of L3 is part of ordering
				if(isProductPartOfOrdering("L3").equalsIgnoreCase(Globals.KEYWORD_PASS)){
					clientOrderingStep3();
				}

				clientOrderingStep4();
				clientOrderingStep5();

			}else{
				throw myException;
			}
			retval=Globals.KEYWORD_PASS;
		}catch (Exception e){
			retval=Globals.KEYWORD_FAIL;
			sc.STAF_ReportEvent("Fail", "ClientOrdering", "Unable to create client ordering due to exception -"+e.toString(), 1);
			log.error("Method-ClientOrdering .Unable to create client order.| Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to enter data in Step1- CLient Ordering
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clientOrderingStep1() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);

		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		String positionName = "";

		try{

			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation.equalsIgnoreCase("Yes")){
				ClientFacingApp.verifyClientOrderingStep1();
			}

			positionName = Globals.testSuiteXLS.getCellData_fromTestData("PositionName");
			
			sc.addParamVal_InEmail("PositionName", positionName);
			
			tempRetval = sc.selectValue_byVisibleText("clientStep1_choosePosition_dd", positionName);

			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				sc.STAF_ReportEvent("Pass", "Step 1", "Position selected for Ordering - "+positionName , 1);

				//				tempRetval = sc.waitforElementToDisplay("clientOrdering_nextStep_btn", 10);
				sc.clickWhenElementIsClickable("clientStep1_nextStep_btn", 10);

				tempRetval = sc.waitforElementToDisplay("clientStep2_firstName_txt", 10);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Pass", "Step 2 ", "Page has loaded" , 1);
					retval = Globals.KEYWORD_PASS;
				}else{
					sc.STAF_ReportEvent("Fail", "Step 2 ", "Page has Not loaded" , 1);
					throw new Exception();
				}


			}else{
				sc.STAF_ReportEvent("Fail", "Step 1", "Unable to select Position for Ordering - "+positionName , 1);
				throw new Exception();
			}

		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-clientOrderingStep1 | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to enter data in Step2- Client Ordering
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clientOrderingStep2() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 10;
		String fname;
		String lname;
		String midName;
		String suffix;

		try{

			tempRetval = sc.waitforElementToDisplay("clientStep2_firstName_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
				if(fieldLevelValidation.equalsIgnoreCase("Yes")){
					ClientFacingApp.verifyClientOrderingStep2();
				}

				updateVolunteerNameInTestData();

				fname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
				lname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
				
				sc.addParamVal_InEmail("volunteerFName", fname);
				sc.addParamVal_InEmail("volunteerLName", lname);
				
				tempRetval = sc.setValueJsChange("clientStep2_firstName_txt",fname);
				midName =Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");

				if(midName != null ){
					tempRetval = sc.setValueJsChange("clientStep2_middleName_txt",midName);
				}else{
					tempRetval = sc.uncheckCheckBox("clientStep2_middleName_chk");
				}
				
				Globals.testSuiteXLS.setCellData_inTestData("CustomEmailMessage", fname);
				
				tempRetval = sc.setValueJsChange("clientStep2_lastName_txt",lname);

				suffix =Globals.testSuiteXLS.getCellData_fromTestData("VolunterrSuffix");
				if(suffix != null ){
					tempRetval = sc.setValueJsChange("clientStep2_suffix_txt",suffix);
				}

				int noOfyears=0;
				String dobCategory =Globals.testSuiteXLS.getCellData_fromTestData("VolDOBCategory");
				if(dobCategory.equalsIgnoreCase("GT than 18")){
					noOfyears = -22;
				}else if(dobCategory.equalsIgnoreCase("GT 14 Less than 18")){
					noOfyears = -16;
				}else if(dobCategory.equalsIgnoreCase("Less Than 14")){
					noOfyears = -13;
				}

				String dob = "";
                if(dobCategory.equalsIgnoreCase("As Test Data")){
                    dob=Globals.testSuiteXLS.getCellData_fromTestData("VolunteerDOB");
                }else{
                    dob = sc.getPriorDate("dd-MMM-yyyy", noOfyears);
                    Globals.testSuiteXLS.setCellData_inTestData("VolunteerDOB",dob);
                }
				tempRetval = sc.selectValue_byVisibleText("clientStep2_dobMonth_dd", dob.split("-")[1]);
				tempRetval = sc.setValueJsChange("clientStep2_dobDay_txt", dob.split("-")[0]);
				tempRetval = sc.setValueJsChange("clientStep2_dobYear_txt", dob.split("-")[2]);

				String ssn =Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN");
				if(ssn != null && !ssn.isEmpty() && !ssn.equals("") ){
					tempRetval = sc.setValueJsChange("clientStep2_SSN_txt",ssn);
				}else{
					tempRetval = sc.uncheckCheckBox("clientStep2_SSN_chk");
				}
				String phoneNo=Globals.Volunteer_Phone;
				tempRetval = sc.selectValue_byVisibleText("clientStep2_gender_dd", Globals.Volunteer_Gender);
				tempRetval = sc.setValueJsChange("clientStep2_phoneNo_txt",phoneNo );
				String spaces = "   ";
				//code updated to include support Claim Account functionality
				String setVolunteerEmail  =	Globals.testSuiteXLS.getCellData_fromTestData("SetVolunteerEmail");
				
				if(setVolunteerEmail.equalsIgnoreCase("Yes")){
					String emailID =spaces+Globals.fromEmailID+spaces;
					tempRetval = sc.setValueJsChange("clientStep2_emailAddress_txt",emailID);
					sc.STAF_ReportEvent("Pass", "Step 2", "Volunteer Email set-"+emailID, 1);
				}else{
					tempRetval = sc.uncheckCheckBox("clientStep2_emailAddress_chk");
					sc.STAF_ReportEvent("Pass", "Step 2", "Volunteer Email Unchecked", 1);
				}
				

				//check whether MVR is part of ordering
				tempRetval =  Globals.KEYWORD_FAIL;
				tempRetval = isProductPartOfOrdering("MVR");
				String mvrFirstName = spaces + Globals.Volunteer_DL_Name + spaces;
				String mvrMidName = spaces + Globals.Volunteer_DL_Name + spaces;
				String mvrLastName = spaces + Globals.Volunteer_DL_Name + spaces;
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Pass", "Step 2", "MVR is part of ordering" , 0);

					tempRetval = sc.selectValue_byVisibleText("clientStep2_mvrState_dd", Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_State"));
					Thread.sleep(1000);
					tempRetval = sc.setValueJsChange("clientStep2_mvrLicense_txt", Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_License"));
					tempRetval = sc.setValueJsChange("clientStep2_mvrFirstName_txt", mvrFirstName);
					tempRetval = sc.setValueJsChange("clientStep2_mvrMidName_txt", mvrMidName);
					tempRetval = sc.setValueJsChange("clientStep2_mvrLastName_txt", mvrLastName);
				}
				//Custom Field Handling
				setCustomFields_ClientOrdering();


				sc.STAF_ReportEvent("Pass", "Step 2", "Data entered successfully for step 2 " , 1);
				sc.clickWhenElementIsClickable("clientStep2_nextStep_btn", (int)timeOutInSeconds);

				tempRetval = Globals.KEYWORD_FAIL;
				tempRetval = sc.waitforElementToDisplay("duplicateOrder_selectNone_rdb", 6);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Pass", "Step 2", "Duplicate Order check.Previous Orders have been displayed " , 1);
					sc.clickWhenElementIsClickable("duplicateOrder_selectNone_rdb",(int)timeOutInSeconds);
					sc.clickWhenElementIsClickable("duplicateOrder_continueOrder_btn",(int)timeOutInSeconds);

					log.info(" Duplicate Order check has been performed and Previous orders displayed during client ordering");
				}

				tempRetval = sc.waitforElementToDisplay("clientStep2_firstName_txt", 2);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					retval  = Globals.KEYWORD_PASS;
				}else{
					throw new Exception();
				}



			}else{
				throw new Exception();
			}

		}catch(Exception e){

			log.error("Method-clientOrderingStep2 | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to enter data in Step3- Client Ordering
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clientOrderingStep3() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		String sNoOfAlias="";
		int iNoOfAlias=0;
		String aliasFName ;
		String aliasLName ;
		String aliasMidName;
		String fromMonth ;
		String fromYear ;
		String toMonth   ;
		String toYear ;
		String spaces = "   ";


		long timeOutInSeconds = 60;


		try{
			tempRetval = sc.waitforElementToDisplay("clientStep3_addAlias_btn", timeOutInSeconds);


			if( tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				sc.STAF_ReportEvent("Pass", "Step 3", "Alias Section is visible", 0);
				sNoOfAlias = sc.testSuiteXLS.getCellData_fromTestData("NoOfAlias");

				iNoOfAlias = Integer.parseInt(sNoOfAlias);
				if (iNoOfAlias <= 0 ){
					log.info(" NO Alias to be added during Client Ordering");

				}else {
					log.info(iNoOfAlias + "-  Alias to be added during Client Ordering");
					iNoOfAlias = iNoOfAlias +iNoOfAlias; // doubling the count as Step is increemented by 2
					int index ;
					for(int i=1;i<=iNoOfAlias;i=i+2){

						FluentWait<WebDriver> btnWait = new FluentWait<WebDriver>(driver);
						WebElement btnNext = driver.findElement(LocatorAccess.getLocator("clientStep3_addAlias_btn"));

						btnWait.withTimeout(20, TimeUnit.SECONDS);
						btnWait.pollingEvery(Globals.gPollingInterval, TimeUnit.SECONDS);
						btnWait.ignoring(NoSuchElementException.class);

						btnWait.until(ExpectedConditions.elementToBeClickable(btnNext));
						btnNext.click();

						index = i;
						String objDivLocator = "//div[" + index + "]/";
						String aliasFNameLocator = objDivLocator +"div[1]/input[@name='firstNameAlias']";
						String aliasMidNameLocator =objDivLocator +"div[2]/input[@name='middleNameAlias']";
						String aliasLNameLocator =objDivLocator +"div[3]/input[@name='lastNameAlias']";

						index = i+1;
						String objDivLocator1 = "//div[" + index + "]";
						String aliasFromMonthLocator = objDivLocator1 + "/div[1]/select[contains(@data-bind,'aliasMonthFrom')]"; 
						String aliasFromYearLocator = objDivLocator1 + "/div[2]/select[contains(@data-bind,'aliasYearFrom')]";
						String aliasToMonthLocator = objDivLocator1 + "/div[3]/select[contains(@data-bind,'aliasMonthTo')]";
						String aliasToYearLocator =  objDivLocator1 + "/div[4]/select[contains(@data-bind,'aliasYearTo')]";


						//The second  alias to be added should be Volunteer details as this scenario was requested to be added-2/4

						if(i != 2){
							aliasFName = spaces+Globals.Volunteer_AliasName+spaces;
							aliasLName = spaces+Globals.Volunteer_AliasName+spaces;
							aliasMidName = spaces+Globals.Volunteer_AliasName+spaces;

						}else{

							aliasFName = spaces+Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName")+spaces;
							aliasLName = spaces+Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName")+spaces;
							aliasMidName = spaces+Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName")+spaces;
						}

						fromMonth = "Jan";
						fromYear =  "2010";
						toMonth =  "Dec";
						toYear =  "2015";

						driver.findElement(By.xpath(aliasFNameLocator)).sendKeys(aliasFName);
						driver.findElement(By.xpath(aliasMidNameLocator)).sendKeys(aliasMidName);
						driver.findElement(By.xpath(aliasLNameLocator)).sendKeys(aliasLName);


						WebElement oFromMonth = driver.findElement(By.xpath(aliasFromMonthLocator));
						WebElement oFromYear = driver.findElement(By.xpath(aliasFromYearLocator));
						WebElement oToMonth = driver.findElement(By.xpath(aliasToMonthLocator));
						WebElement oToYear = driver.findElement(By.xpath(aliasToYearLocator));

						tempRetval = sc.selectValue_byVisibleText(oFromMonth,fromMonth);
						tempRetval = sc.selectValue_byVisibleText(oFromYear,fromYear);
						tempRetval = sc.selectValue_byVisibleText(oToMonth,toMonth);
						tempRetval = sc.selectValue_byVisibleText(oToYear,toYear);


					}

					sc.STAF_ReportEvent("Pass", "Step 3", "Total Alias Added - " + iNoOfAlias/2, 1);	
				}

			}else{
				sc.STAF_ReportEvent("Fail", "Step 3", "Alias Section is NOT visible", 1);
				throw new Exception();
			}


			sc.clickWhenElementIsClickable("clientStep3_nextStep_btn", 20);
			tempRetval = sc.waitforElementToDisplay("clientStep4_addressLine1_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				retval  = Globals.KEYWORD_PASS;
			}else{
				throw new Exception();
			}


		}catch(Exception e){

			log.error("Method-clientOrderingStep3 | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to enter data in Step4- Client Ordering
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clientOrderingStep4()throws Exception {
		//Address History 
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 60;
		String spaces = "   ";

		try{
			tempRetval = sc.waitforElementToDisplay("clientStep4_addressLine1_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				sc.STAF_ReportEvent("Pass", "Step 4", "Address section is visible", 1);

				String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
				if(fieldLevelValidation.equalsIgnoreCase("Yes")){
					ClientFacingApp.verifyClientOrderingStep4();
				}


				String addressLine =spaces+Globals.Volunteer_AddressLine+spaces;
				String city=spaces+Globals.Volunteer_City+spaces;
				//Anand 2/15-removing address line & city columns from test data
				tempRetval = sc.setValueJsChange("clientStep4_addressLine1_txt", addressLine);
				tempRetval = sc.setValueJsChange("clientStep4_addressLine2_txt", addressLine);
				tempRetval = sc.setValueJsChange("clientStep4_addressCity_txt", city);

				Thread.sleep(1000);
				String zipcode = Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode");
				tempRetval = sc.setValueJsChange("clientStep4_addressZipCode_txt", zipcode);

				tempRetval = sc.selectValue_byVisibleText("clientStep4_addressState_dd", Globals.testSuiteXLS.getCellData_fromTestData("AddressState"));

				//Anand 2/15 - Removing from month and year from test data excel.
				String month = sc.getPriorDate("MMM-yyyy", -6);
				tempRetval = sc.selectValue_byVisibleText("clientStep4_addressFromMonth_dd", month.split("-")[0]);
				tempRetval = sc.selectValue_byVisibleText("clientStep4_address_FromYear_dd",month.split("-")[1]);

				sc.STAF_ReportEvent("Pass", "Step 4", "Data enterd in address section", 1);

				sc.clickWhenElementIsClickable("clientStep4_nextStep_btn", (int)timeOutInSeconds);

				if(sc.waitforElementToDisplay("clientStep5_summaryConsent_chk",10).equalsIgnoreCase(Globals.KEYWORD_PASS)){
					retval  = Globals.KEYWORD_PASS;
				}else{
					sc.clickWhenElementIsClickable("clientStep4_nextStep_btn",(int)timeOutInSeconds);
					//					driver.findElement(LocatorAccess.getLocator("clientStep4_nextStep_btn")).click();
				}

			}else{
				throw new Exception();
			}

		}catch(Exception e){

			log.error("Method-clientOrderingStep4 | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify data in Step5- Client Ordering
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clientOrderingStep5() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 60;
		String npnOrderId="";
		String swestOrderId="";
		String syncLabel="";
		Pattern p = Pattern.compile("([0-9])");
		Matcher m = null;

		try{
			tempRetval = sc.waitforElementToDisplay("clientStep5_summaryConsent_chk",timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Step 4", "Consent page has not been loaded", 1);
				return Globals.KEYWORD_FAIL;
			}

			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation.equalsIgnoreCase("Yes")){
				ClientFacingApp.verifyClientOrderingStep5();
			}
			sc.clickWhenElementIsClickable("clientStep5_summaryConsent_chk",(int)timeOutInSeconds);
			sc.clickWhenElementIsClickable("clientStep5_standardConsent_chk",(int)timeOutInSeconds);
			//			driver.findElement(LocatorAccess.getLocator("clientStep5_summaryConsent_chk")).click();
			//			driver.findElement(LocatorAccess.getLocator("clientStep5_standardConsent_chk")).click();

			verifyPricingClientOrdering();
			
			String prodlistvalidation = Globals.testSuiteXLS.getCellData_fromTestData("Step4_VerifyProductList");
			if(prodlistvalidation.equalsIgnoreCase("Yes")){
				//verifyProdListClientOrdering();
			}
			
			sc.STAF_ReportEvent("Pass", "Step 5", "Consent page has been loaded", 1);

			tempRetval = sc.waitforElementToDisplay("clientStep5_submitOrder_btn",timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				sc.clickWhenElementIsClickable("clientStep5_submitOrder_btn", (int)timeOutInSeconds);
				
				String OrderHoldQueuesettings = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldQueuecheckbox");
				String pricingType = sc.testSuiteXLS.getCellData_fromTestData("PricingName");
				
				if(OrderHoldQueuesettings.equalsIgnoreCase("Yes")){
					double totalcost = fetchExpectedTotalPriceSTEP4();
                    String orderHoldQAmt = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldqueueamount");
                    double orderHoldAmt = Double.parseDouble(orderHoldQAmt);					
				/*If Order Hold is ON and meets the Below requirement then the order will placed as HOLD*//*@Lakshmi*/
				if(pricingType.equalsIgnoreCase("Client Pays All") && OrderHoldQueuesettings.equalsIgnoreCase("Yes") && (orderHoldAmt <= totalcost) )
				{
					By syncLocator = LocatorAccess.getLocator("clientStep6_syncLabel_lbl");
					syncLabel = driver.findElement(syncLocator).getText();
					
					int counter =0;
					boolean orderIdFound = false;
					while(counter <= (int)timeOutInSeconds &&  orderIdFound == false ){
						Thread.sleep(1000);
						syncLabel = driver.findElement(syncLocator).getText();
						counter++;

						m = p.matcher(syncLabel);
						if(m.find()){
							orderIdFound = true;
							String orderID = syncLabel.split(":")[1];
							npnOrderId = orderID.split("-")[0].trim();
							break;
						}
					}
						if(npnOrderId.isEmpty()){
							log.error(" Order ID is not generated OR unable to capture the order id");
							sc.STAF_ReportEvent("Fail", "Step 5", "OrderID not generated", 1);

						}
						else{
							String orderedDate = sc.getTodaysDate();
							Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
							Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderId);
							sc.addParamVal_InEmail("NPNOrderID", npnOrderId);
							sc.STAF_ReportEvent("Pass", "Step 5", "OrderID generated ="+npnOrderId, 1);

							log.info(" NPN Order ID Generated - "+ npnOrderId.trim());
							tempRetval = sc.waitforElementToDisplay("clientStep6_complete_btn",timeOutInSeconds);
							//					driver.findElement(LocatorAccess.getLocator("clientStep6_complete_btn")).click();
							sc.clickWhenElementIsClickable("clientStep6_complete_btn",(int)timeOutInSeconds);
							tempRetval = sc.waitforElementToDisplay("volDashboard_orderBackgroundCheck_btn",timeOutInSeconds);
							retval=Globals.KEYWORD_PASS;
						}
					}
				}
				
				else{
				By syncLocator = LocatorAccess.getLocator("clientStep6_syncLabel_lbl");
				syncLabel = driver.findElement(syncLocator).getText();

				int counter =0;
				boolean orderIdFound = false;
				while(counter <= (int)timeOutInSeconds &&  orderIdFound == false ){
					Thread.sleep(1000);
					syncLabel = driver.findElement(syncLocator).getText();
					counter++;

					m = p.matcher(syncLabel);
					if(m.find()){
						orderIdFound = true;
						String orderID = syncLabel.split(":")[1];
						npnOrderId = orderID.split("-")[0].trim();
						swestOrderId = orderID.split("-")[1].trim();
						break;
					}

				}

				/*tempRetval=Globals.KEYWORD_FAIL;
				tempRetval = sc.waitforElementToDisplay("clientStep6_npnOrderID_lbl",timeOutInSeconds);
				tempRetval = sc.waitforElementToDisplay("clientStep6_swestOrderID_lbl",timeOutInSeconds);


				npnOrderId=driver.findElement(LocatorAccess.getLocator("clientStep6_npnOrderID_lbl")).getText();
				swestOrderId=driver.findElement(LocatorAccess.getLocator("clientStep6_swestOrderID_lbl")).getText();*/

				if(npnOrderId.isEmpty() || swestOrderId.isEmpty()){
					log.error(" Order ID is not generated OR unable to capture the order id");
					sc.STAF_ReportEvent("Fail", "Step 5", "OrderID not generated", 1);

				}else{
					String orderedDate = sc.getTodaysDate();
					Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
					Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderId);
					Globals.testSuiteXLS.setCellData_inTestData("SWestOrderID", swestOrderId);
					sc.addParamVal_InEmail("SWestOrderID", swestOrderId);
					sc.addParamVal_InEmail("NPNOrderID", npnOrderId);
					sc.STAF_ReportEvent("Pass", "Step 5", "OrderID generated ="+npnOrderId +"-"+swestOrderId , 1);

					log.info(" NPN Order ID Generated - "+ npnOrderId.trim());
					log.info(" S.West Order ID Generated - "+ swestOrderId.trim());

					tempRetval = sc.waitforElementToDisplay("clientStep6_complete_btn",timeOutInSeconds);
					//					driver.findElement(LocatorAccess.getLocator("clientStep6_complete_btn")).click();
					sc.clickWhenElementIsClickable("clientStep6_complete_btn",(int)timeOutInSeconds);
					tempRetval = sc.waitforElementToDisplay("volDashboard_orderBackgroundCheck_btn",timeOutInSeconds);
					retval=Globals.KEYWORD_PASS;
				}

			}
		
		}}catch(Exception e){
			log.error("Method-clientOrderingStep5 | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	//	
	/**************************************************************************************************
	 * Method to check whether a product is part of ordering based on test data
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String isProductPartOfOrdering(String productCode){
		APP_LOGGER.startFunction("isProductPartOfOrdering");
		String retval=Globals.KEYWORD_FAIL;

		try{
			String products =  Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts");
			products.toLowerCase().trim();
			productCode.toLowerCase().trim();
			String[] arrProduct = products.split("-");
			boolean productFound = false;

			for (int i=0;i<arrProduct.length;i++){
				if(arrProduct[i].equalsIgnoreCase(productCode)){
					productFound = true;
					break;
				}
			}

			if(productFound){
				retval = Globals.KEYWORD_PASS;
				log.info("Product - "+ productCode + " is part of Ordering");
			}else{
				retval = Globals.KEYWORD_FAIL;
				log.debug("Product - "+ productCode + " is NOT part of Ordering");
			}
		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-isProductPartOfOrdering | Exception - "+ e.toString());
		}
		return retval;

	}

	/**************************************************************************************************
	 * Method to send an invite to the volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String sendInvitationToApplicant(){
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;
		String tempRetval=Globals.KEYWORD_FAIL;

		try{

			tempRetval=sc.waitforElementToDisplay("volDashboard_communications_btn", timeOutinSeconds);
			//			driver.findElement(LocatorAccess.getLocator("volDashboard_communications_btn")).click();
			//			driver.findElement(LocatorAccess.getLocator("volDashboard_sendInvitation_btn")).click();
			sc.clickWhenElementIsClickable("volDashboard_communications_btn",timeOutinSeconds);
			sc.clickWhenElementIsClickable("volDashboard_sendInvitation_btn",timeOutinSeconds);

			tempRetval=sc.waitforElementToDisplay("sendInvitation_postionListing_dd", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
				if(fieldLevelValidation.equalsIgnoreCase("Yes")){
					ClientFacingApp.verifySendInvitationPage();
				}

				String positionName= Globals.testSuiteXLS.getCellData_fromTestData("PositionName") +" - "+ Globals.testSuiteXLS.getCellData_fromTestData("PricingName");
				if(positionName.contains("Client Pays")){	
					String arr0 =positionName.split("Client Pays")[0];
					String arr1 =positionName.split("Client Pays")[1];
					String arrf1=arr0+"Client Pays";
					String arrf2=arr1.trim();
					Select sel = new Select(driver.findElement(LocatorAccess.getLocator("sendInvitation_postionListing_dd")));
					List<WebElement> list = sel.getOptions();
							for (WebElement option : list) {
								if (option.getText().contains(arrf1) && option.getText().contains(arrf2)) {
									sel.selectByVisibleText(option.getText());
									break;
				        }
					}
				}
					else{
						tempRetval = sc.selectValue_byVisibleText("sendInvitation_postionListing_dd", positionName );
						}

				sc.addParamVal_InEmail("Position", positionName);
				
				
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					log.error(" Unable to select Position from the Postion Dropdown | Postion - "+positionName);
					sc.STAF_ReportEvent("Fail", "Send Invite", "Unable to select Position - "+ positionName, 1);
					return tempRetval;
				}
				sc.STAF_ReportEvent("Pass", "Send Invite", "Position selected - "+ positionName, 1);

				updateVolunteerNameInTestData();

				String fname=null;
				String lname=null;
				String customEmailMessage =null;
				String spaces = "   ";
				fname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");

				lname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");


				customEmailMessage = sc.updateAndFetchRuntimeValue("CustomEmailMsg_runtimeUpdateFlag","CustomEmailMessage",45);

				tempRetval = sc.setValueJsChange("sendInvitation_fName_txt", fname);
				tempRetval = sc.setValueJsChange("sendInvitation_lName_txt", lname);
				String emailID=null;
				emailID = spaces + Globals.fromEmailID + spaces;
							
				tempRetval = sc.setValueJsChange("sendInvitation_emailAddress_txt",emailID );
				//TODO need to handle custom fields

				String ccList = Globals.testSuiteXLS.getCellData_fromTestData("CCEmailAddr");
				if(ccList != null && !ccList.isEmpty() && !ccList.equals("")){
					tempRetval = sc.selectValue_byVisibleText("sendInvitation_ccEmailAddress_dd",ccList  );
				}

				String bccList = Globals.testSuiteXLS.getCellData_fromTestData("CCEmailAddr");
				if(bccList != null && !bccList.isEmpty() && !bccList.equals("")){
					tempRetval = sc.selectValue_byVisibleText("sendInvitation_bccEmailAddress_dd", bccList);
				}


				WebElement emailBody = sc.createWebElement("sendInvitation_emailBody_txt");
				//emailBody.sendKeys(Keys.TAB);
				
				tempRetval = sc.setValueJsChange(emailBody, customEmailMessage);
				//driver.findElement(LocatorAccess.getLocator("sendInvitation_fName_txt")).click();//Focus is shifted to other element
				sc.clickElementUsingJavaScript("sendInvitation_fName_txt");
				tempRetval = sc.waitforElementToDisplay("sendInvitation_sendInvite_btn",timeOutinSeconds);
				//sc.clickWhenElementIsClickable("sendInvitation_sendInvite_btn",timeOutinSeconds);
				sc.clickElementUsingJavaScript("sendInvitation_sendInvite_btn");
				
				boolean sendInvitePageVisible = true;
				int counter = 1;
				while(sendInvitePageVisible && counter <= 90){
					tempRetval=Globals.KEYWORD_FAIL;
					tempRetval=sc.waitforElementToDisplay("sendInvitation_sendInvite_btn", 2);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						counter = counter +2;
					}else{
						sendInvitePageVisible = false;
					}

				}

				if(sendInvitePageVisible){
					sc.STAF_ReportEvent("Fail", "Send Invite", "Unable to send E-invite to  - "+ emailID, 1);
					retval=Globals.KEYWORD_FAIL;

				}else{
					sc.STAF_ReportEvent("Pass", "Send Invite", "E-Invite send to  - "+ emailID, 1);
					retval=Globals.KEYWORD_PASS;
				}


			}

		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-sendInvitationToApplicant | Exception - "+ e.toString());
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to complete Ordering Step-1
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String invitationOrderStep1() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 10;
		String tempRetval=Globals.KEYWORD_FAIL;
		String midName=null;
		String suffix=null;
		String fname = null;
		String lname = null;


		try{
			tempRetval=sc.waitforElementToDisplay("invitationStep1_fName_txt", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				//verify pricing in Step1
				verifyPricingSTEP1();
				
				//verify custom messaging Step1
                verifyCustomMessagingOrderSTEP1();
              
				fname = sc.testSuiteXLS.getCellData_fromTestData("volunteerFName").trim();
				lname = sc.testSuiteXLS.getCellData_fromTestData("volunteerLName").trim();


				tempRetval = sc.setValueJsChange("invitationStep1_fName_txt",fname);
				midName =Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");
				
				sc.addParamVal_InEmail("volunteerFName", fname);
				sc.addParamVal_InEmail("volunteerLName", lname);

				if(midName != null && !midName.isEmpty() ){

					tempRetval = sc.setValueJsChange("invitationStep1_midName_txt",midName);
				}else{
					tempRetval = sc.checkCheckBox("invitationStep1_midName_chk");
				}

				tempRetval = sc.setValueJsChange("invitationStep1_lName_txt",lname);

				suffix =Globals.testSuiteXLS.getCellData_fromTestData("VolunterrSuffix");
				if(suffix != null ){
					tempRetval = sc.setValueJsChange("invitationStep1_suffix_txt",suffix);
				}

				int noOfyears=0;
				String dobCategory =Globals.testSuiteXLS.getCellData_fromTestData("VolDOBCategory");
				if(dobCategory.equalsIgnoreCase("GT than 18")){
					noOfyears = -22;
				}else if(dobCategory.equalsIgnoreCase("GT 14 Less than 18")){
					noOfyears = -16;
				}else if(dobCategory.equalsIgnoreCase("Less Than 14")){
					noOfyears = -13;
				}
				
				String dob = sc.getPriorDate("dd-MMM-yyyy", noOfyears);
				Globals.testSuiteXLS.setCellData_inTestData("VolunteerDOB",dob);
				String monMonthName = dob.split("-")[1]; // 3 chara equivalent of Month like Jan
				String monthNameFull = sc.convertToFullMonthName(monMonthName);
				tempRetval = sc.selectValue_byVisibleText("invitationStep1_dobMonth_dd",monthNameFull );
				Thread.sleep(1000);

				//Anand-3/1- application has changed the format of Date dropdown from 01 to 1

				String dobDate = dob.split("-")[0];
				Pattern pattern = Pattern.compile("[0].");//. represents single character  
				Matcher m = pattern.matcher(dobDate);  
				boolean b = m.matches();

				if(b){
					dobDate =	dobDate.replace("0","");
				}


				tempRetval = sc.selectValue_byVisibleText("invitationStep1_dobDay_dd", dobDate);
				tempRetval = sc.selectValue_byVisibleText("invitationStep1_dobYear_dd",dob.split("-")[2]);

				String ssn =Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN");

				if(!ssn.isEmpty() && ssn != null){
					tempRetval = sc.setValueJsChange("invitationStep1_ssn1_txt",ssn.substring(0, 3)); // first 3 digits
					tempRetval = sc.setValueJsChange("invitationStep1_ssn2_txt",ssn.substring(3, 5)); //next 2 digits
					tempRetval = sc.setValueJsChange("invitationStep1_ssn3_txt",ssn.substring(5, 9)); //last 4 digits
					sc.STAF_ReportEvent("Pass", "Step 1", "SSN Entered for ordering", 1);
				}else{
					sc.STAF_ReportEvent("Pass", "Step 1", "SSN Not Entered for ordering", 1);
					tempRetval = sc.checkCheckBox("invitationStep1_noSSN_chk");
				}

				tempRetval = sc.selectValue_byVisibleText("invitationStep1_gender_dd", Globals.Volunteer_Gender);
				String phoneNo=Globals.Volunteer_Phone;

				tempRetval = sc.setValueJsChange("invitationStep1_phone_txt",phoneNo );

				String emailID =Globals.fromEmailID;
				String emailIDinUI = driver.findElement(LocatorAccess.getLocator("invitationStep1_emailAddress_txt")).getAttribute("value");
				if(emailID.equalsIgnoreCase(emailIDinUI)){
					log.info("Email ID displayed in Invitation Ordering-STEP 1 is correct");
				}else{
					sc.STAF_ReportEvent("Fail", "Step 1", "Email ID displayed in Invitation Ordering-STEP 1 is not correct", 1);
					log.error("Email ID displayed in Invitation Ordering-STEP 1 is not correct.| Expected -"+emailID + "| Actual:- "+emailIDinUI);
				}

				//Custom Fields Handling - for os-002 script script will ignore customfield verification method
				String customFieldchkValue=Globals.testSuiteXLS.getCellData_fromTestData("TestCaseDescr");
				if(!(customFieldchkValue.contains("L1 sharing Order with L3-Order Creation"))){
					verifyAndSetCustomFields();
				}

				//Alias Data Entry
				String sNoOfAlias = sc.testSuiteXLS.getCellData_fromTestData("NoOfAlias");
				int iNoOfAlias = Integer.parseInt(sNoOfAlias);

				populateAliasSectionData(iNoOfAlias);

				//alacarte section verification would only appear if they are part of ordering
				alacarteSectionVerification();

				sc.scrollIntoView("invitationStep1_saveAndContinue_btn");
				sc.STAF_ReportEvent("Pass", "Step 1", "Data entered in Step 1", 1);
				
				sc.clickWhenElementIsClickable("invitationStep1_saveAndContinue_btn", timeOutinSeconds);
				retval=Globals.KEYWORD_PASS;

			}else{
				sc.STAF_ReportEvent("Fail", "Step 1", "Step 1 page has not been loaded" , 1);
				throw new Exception("Unable to complete STEP 1");
			}


		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-invitationOrderStep1 | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to complete Ordering Step-2
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String invitationOrderStep2() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 10;
		String tempRetval=Globals.KEYWORD_FAIL;

		try{
			tempRetval=sc.waitforElementToDisplay("invitationStep2_addressLine1_txt", 60);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				//MVR section would only be displayed if MVR product is part of Ordering
				populateMVRData();
				populateInternationalAddData();
				String products =  Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts");
				
				String addressLine =Globals.Volunteer_AddressLine;
				String city=Globals.Volunteer_City;
				//Anand 2/15-removing address line & city columns from test data

				//current address section-data
				tempRetval = sc.setValueJsChange("invitationStep2_addressLine1_txt", addressLine);
				tempRetval = sc.setValueJsChange("invitationStep2_addressLine2_txt", addressLine);
				tempRetval = sc.setValueJsChange("invitationStep2_city_txt", city);
				tempRetval = sc.setValueJsChange("invitationStep2_zipCode_txt", Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode"));
				
				if ((products.equalsIgnoreCase("L1")|| products.equalsIgnoreCase("L1-Globex") || products.equalsIgnoreCase("Globex"))&&(Globals.testSuiteXLS.getCellData_fromTestData("CountryCode").equalsIgnoreCase("CAN"))  ){
					
					tempRetval = sc.setValueJsChange("invitationStep2_province_txt", Globals.testSuiteXLS.getCellData_fromTestData("AddressState"));
					tempRetval = sc.selectValue_byValue("invitationStep2_country_dd",Globals.testSuiteXLS.getCellData_fromTestData("CountryCode") );
				}else{
					tempRetval = sc.selectValue_byVisibleText("invitationStep2_state_dd",Globals.testSuiteXLS.getCellData_fromTestData("AddressState") );
				}


				//Anand 2/15 - Removing from month and year from test data excel.
				enterDate(-5,"invitationStep2_fromDate_txt"); // format mm/yyyy
				sc.clickWhenElementIsClickable("invitationStep2_addressLine1_txt", timeOutinSeconds);

				// Previous address section
				tempRetval =  Globals.KEYWORD_FAIL;
				tempRetval = isProductPartOfOrdering("L3");
				String addPrevAddress =   Globals.testSuiteXLS.getCellData_fromTestData("AddPrevAddr");

				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS) && addPrevAddress.equalsIgnoreCase("Yes")){
					// add the previous address
					String zipcode =Globals.testSuiteXLS.getCellData_fromTestData("PrevAddZipCode");
					String prevState = Globals.testSuiteXLS.getCellData_fromTestData("PrevAddrState");

					sc.clickWhenElementIsClickable("invitationStep2_addAddress_btn", timeOutinSeconds);
					tempRetval = sc.setValueJsChange("invitationStep2_PrevaddressLine1_txt", addressLine);
					tempRetval = sc.setValueJsChange("invitationStep2_PrevaddressLine2_txt", addressLine);
					tempRetval = sc.setValueJsChange("invitationStep2_Prevcity_txt", city);
					tempRetval = sc.setValueJsChange("invitationStep2_PrevzipCode_txt", zipcode);
					tempRetval = sc.selectValue_byVisibleText("invitationStep2_Prevstate_dd",prevState);

					enterDate(-8,"invitationStep2_PrevfromDate_txt");
					sc.clickWhenElementIsClickable("invitationStep2_PrevaddressLine1_txt", timeOutinSeconds);
					enterDate(-5,"invitationStep2_PrevtoDate_txt");
					sc.clickWhenElementIsClickable("invitationStep2_PrevaddressLine1_txt", timeOutinSeconds);

				}

				//Reference Section
				tempRetval =  Globals.KEYWORD_FAIL;
				tempRetval = isProductPartOfOrdering("REF");
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

					tempRetval = Globals.KEYWORD_FAIL;
					tempRetval = setData_InvitationOrder_Reference();
					if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						sc.STAF_ReportEvent("Pass", "Step 2", "Data entered for Reference section as its part of ordering" , 1);
					}else{
						sc.STAF_ReportEvent("Fail", "Step 2", "Unable to enter for Reference section as its part of ordering" , 1);
						throw new Exception("Unable to enter and validate Reference section");
					}


				}	

				sc.STAF_ReportEvent("Pass", "Step 2", "Data entered in Step 2", 1);
				sc.clickWhenElementIsClickable("invitationStep2_saveAndContinue_btn", timeOutinSeconds);

				retval = Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "Step 2", "Step 2 page has not been loaded" , 1);
				log.error("Method-invitationOrderStep2 | Page has not loaded");
				throw new Exception("Unable to complete STEP 2");
			}



		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-invitationOrderStep2 | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to complete Ordering Step-3
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String invitationOrderStep3() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;
		boolean spConsent=false;
		String tempRetval=Globals.KEYWORD_FAIL;
		String addressState = null;
		String consentChkPath=null;
		String vvscConsentTxt="";
		String accntNm =Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
		String accntUse =Globals.testSuiteXLS.getCellData_fromTestData("AccountUse");
		String verifyConsent=Globals.testSuiteXLS.getCellData_fromTestData("Step3_Verify Consent");
		String commonConsentTxt="You, as a consumer, have a number of rights when it comes to your personal information and your background check report. "+accntNm+" is required by law to provide you with information regarding those rights and to gain your consent for a background check before allowing you to continue with your order. Please review and sign, by checking the boxes, to indicate your consent to begin the background check process and to acknowledge your rights under the Federal and applicable State Fair Credit Reporting Act(s).";
		//String disclosureConsentVerbiageTxt="I have read the Disclosure Regarding the Volunteer/Non-Paid Position Background Report provided by Verified Volunteers and this Authorization to Obtain Volunteer/Non-Paid Position Background Report. By my signature below, I hereby consent to the preparation by Verified Volunteers, a consumer reporting agency located at 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3, www.verifiedvolunteers.com, of background reports regarding me and the release of such reports to any organization I authorize and its designated representatives, to assist the organization in making a volunteer/non-paid position decision involving me at any time after receipt of this authorization and throughout my volunteerism/non-paid position, to the extent permitted by law. To this end, I hereby authorize, without reservation, any state or federal law enforcement agency or court, educational institution, motor vehicle record agency, credit bureau or other information service bureau or data repository, to furnish any and all information regarding me to Verified Volunteers and/or the organization itself, and authorize Verified Volunteers to provide such information to the organization. I agree that a facsimile(\"fax\"), electronic or photographic copy of this Authorization shall be as valid as the original.";
		String disclosureConsentVerbiageTxt="I have read the Disclosure Regarding the Employment and/or Volunteerism/Non-Employee Position Background Report provided by Verified Volunteers and this Authorization to Obtain Employment and/or Volunteerism/Non-Employee Position Background Report. By my signature below, I hereby consent to the preparation by Verified Volunteers, a consumer reporting agency located at 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3, www.verifiedvolunteers.com, of background reports regarding me and the release of such reports to any organization I authorize and its designated representatives, to assist the organization in making an employment and/or volunteerism/non-employee position decision involving me at any time after receipt of this authorization and throughout my employment and/or volunteerism/non-employee position, to the extent permitted by law. To this end, I hereby authorize, without reservation, any state or federal law enforcement agency or court, educational institution, motor vehicle record agency, credit bureau or other information service bureau or data repository, to furnish any and all information regarding me to Verified Volunteers and/or the organization itself, and authorize Verified Volunteers to provide such information to the organization. I agree that a facsimile(\"fax\"), electronic or photographic copy of this Authorization shall be as valid as the original. ";
		String rightsSummaryConsentTxt="I acknowledge receipt of the preceding Consumer Financial Protection Bureau's \"A SUMMARY OF YOUR RIGHTS UNDER THE FAIR CREDIT REPORTING ACT.\" and \"Security Freeze Notice\"";
		String eSignatureConsentTxt="I understand that by typing my name where indicated below, I consent to the use of electronic records and signatures in the manner described above, and the electronic storage of such documents.";		
		String splStateWATxt="Washington State Employment and/or Volunteerism/Non-Employee Positions Only: I acknowledge that I am aware I have the right to request, from the consumer reporting agency, a written summary of my rights and remedies under the Washington Fair Credit Reporting Act.";
		String splStateNYTxt="NY Employment and/or Volunteerism/Non-Employee Positions Only: I acknowledge that I have received a copy of New York Correction Law Article 23-A. I have the right, upon written request, to be informed whether an investigative consumer REPORT was requested. If such a REPORT was requested, you will be provided with the name and address of the consumer reporting agency that prepared the report and you can contact that agency to inspect or receive a copy of the REPORT.";
		String splStateOthertxt="California, Massachusetts, Minnesota, New Jersey and Oklahoma Employment and/or Volunteerism/Non-Employee Positions Only: Check the box to the left if you would like a free copy of your background report from Verified Volunteers. Please note that you can access your completed report at any time through your Profile.";
		String esdConsentTxt="Consent to Use of Electronic Records and Signatures\nYou have the opportunity to complete and sign documents, as well as receive notices and other documents related to your application and background check, in electronic rather than paper form. To agree to these uses of electronic documents and signatures, and to sign this document with the same effect as physically signing your name, click the \"Sign\" button at the bottom of this page after reviewing the information below.\nIn order to sign, complete and receive documents electronically you will need the following:\na. A personal e-mail address;\nb. A computer or other device with standard e-mail software;\nc. Internet Explorer version 9 or newer, Firefox, Google Chrome, or Safari\nd. A connection to the Internet; and\ne. A printer if you want to print paper copies.\nAlternatively, you may elect to use and sign paper versions of documents related to your application, including the background check. To do so, please contact Verified Volunteers at 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3.\nBy typing your name below, you consent to sign, complete and receive documents relating to your application and background check during both this session and any future sessions relating to your application. Additionally, you consent to electronically receive: communications relating to your application and associated background check, including requests for additional information; notices of actions taken on your application required by law, including the Fair Credit Reporting Act; and notices of your rights under federal or state laws.\nYour consent applies to documents completed, signed or provided via this website, as well as to documents transmitted via email.\nYou have the right to withdraw your consent at any time by calling or writing to: Verified Volunteers, 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3 or TheAdvocates@VerifiedVolunteers.com. After withdrawing your consent, please also contact your organization to make arrangements to receive paper copies of documents and communications.\nIf your contact information changes, please call or write to: 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3 or TheAdvocates@VerifiedVolunteers.com.\nAfter consenting, you can obtain copies of documents and communications relating to your associated background check by: (1) logging into Verified Volunteers and accessing the Agreements section of 'My Profile'; or (3) calling 855-326-1820 Option 3 or emailing TheAdvocates@VerifiedVolunteers.com to request that paper copies be mailed to you at no charge.";
		if(accntUse.equals("Volunteerism")){
		    vvscConsentTxt="Disclosure Regarding Employment and/or Volunteerism/Non-Employee Position Background Report\nVerified Volunteers, 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3. www.verifiedvolunteers.com, may obtain a consumer report and/or an investigative consumer report (\"REPORT\") that contains background information about you in connection with your employment and/or volunteerism/non-employee Position. Verified Volunteers may obtain further reports throughout your employment and/or volunteerism/non-employee Position so as to update your report without providing further disclosure or obtaining additional consent.\nThe REPORT may contain information about your character, general reputation, personal characteristics and mode of living. The REPORT may include, but is not limited to, credit reports and credit history information; criminal and other public records and history; public court records; motor vehicle and driving records; and Social Security verification and address history, subject to any limitations imposed by applicable federal and state law. This information may be obtained from public record and private sources, including credit bureaus, government agencies and judicial records, and other sources.\nIf an investigative consumer REPORT is obtained, in addition to the description above, the nature and scope of any such REPORT will be for personal references.";
		}
		else{
			vvscConsentTxt="Disclosure Regarding Employment and/or Volunteerism/Non-Employee Position Background Report\nVVRegEmployment, test address Line, test address Line, test, FL 32007, may obtain a consumer report and/or an investigative consumer report (\"REPORT\") that contains background information about you in connection with your employment and/or volunteerism/non-employee position. VVRegEmployment may obtain further reports throughout your employment and/or volunteerism/non-employee position so as to update your report without providing further disclosure or obtaining additional consent.\nThe REPORT may contain information about your character, general reputation, personal characteristics and mode of living. The REPORT may include, but is not limited to, credit reports and credit history information; criminal and other public records and history; public court records; motor vehicle and driving records; and Social Security verification and address history, subject to any limitations imposed by applicable federal and state law. This information may be obtained from public record and private sources, including credit bureaus, government agencies and judicial records, and other sources.\nIf an investigative consumer REPORT is obtained, in addition to the description above, the nature and scope of any such REPORT will be for personal references.";	
		}
		String fcraConsentTxt="A Summary of Your Rights under the Fair Credit Reporting Act\nThe federal Fair Credit Reporting Act (FCRA) promotes the accuracy, fairness, and privacy of information in the files of consumer reporting agencies. There are many types of consumer reporting agencies, including credit bureaus and specialty agencies (such as agencies that sell information about check writing histories, medical records, and rental history records). Here is a summary of your major rights under the FCRA. For more information, including information about additional rights, go to www.consumerfinance.gov/learnmore or write to: Consumer Financial Protection Bureau, 1700 G Street N.W., Washington, D.C. 20552.\nYou must be told if information in your file has been used against you. Anyone who uses a credit report or another type of consumer report to deny your application for credit, insurance, or employment - or to take another adverse action against you - must tell you, and must give you the name, address, and phone number of the agency that provided the information.\nYou have the right to know what is in your file. You may request and obtain all the information about you in the files of a consumer reporting agency (your \"file disclosure\"). You will be required to provide proper identification, which may include your Social Security number. In many cases, the disclosure will be free. You are entitled to a free file disclosure if:\n- a person has taken adverse action against you because of information in your credit report;\n- you are the victim of identity theft and place a fraud alert in your file;\n- your file contains inaccurate information as a result of fraud;\n- you are on public assistance;\n- you are unemployed but expect to apply for employment within 60 days.\nIn addition, all consumers are entitled to one free disclosure every 12 months upon request from each nationwide credit bureau and from nationwide specialty consumer reporting agencies. See www.consumerfinance.gov/learnmore for additional information.\nYou have the right to ask for a credit score. Credit scores are numerical summaries of your creditworthiness based on information from credit bureaus. You may request a credit score from consumer reporting agencies that create scores or distribute scores used in residential real property loans, but you will have to pay for it. In some mortgage transactions, you will receive credit score information for free from the mortgage lender.\nYou have the right to dispute incomplete or inaccurate information. If you identify information in your file that is incomplete or inaccurate, and report it to the consumer reporting agency, the agency must investigate unless your dispute is frivolous. See www.consumerfinance.gov/learnmore for an explanation of dispute procedures.\nConsumer reporting agencies must correct or delete inaccurate, incomplete, or unverifiable information. Inaccurate, incomplete or unverifiable information must be removed or corrected, usually within 30 days. However, a consumer reporting agency may continue to report information it has verified as accurate.\nConsumer reporting agencies may not report outdated negative information. In most cases, a consumer reporting agency may not report negative information that is more than seven years old, or bankruptcies that are more than 10 years old.\nAccess to your file is limited. A consumer reporting agency may provide information about you only to people with a valid need -- usually to consider an application with a creditor, insurer, employer, landlord, or other business. The FCRA specifies those with a valid need for access.\nYou must give your consent for reports to be provided to employers. A consumer reporting agency may not give out information about you to your employer, or a potential employer, without your written consent given to the employer. Written consent generally is not required in the trucking industry. For more information, go to www.consumerfinance.gov/learnmore.\nYou may limit \"prescreened\" offers of credit and insurance you get based on information in your credit report. Unsolicited \"prescreened\" offers for credit and insurance must include a toll-free phone number you can call if you choose to remove your name and address from the lists these offers are based on. You may opt-out with the nationwide credit bureaus at 1 888 5OPTOUT (1 888 567 8688).\nYou may seek damages from violators. If a consumer reporting agency, or, in some cases, a user of consumer reports or a furnisher of information to a consumer reporting agency violates the FCRA, you may be able to sue in state or federal court.\nIdentity theft victims and active duty military personnel have additional rights. For more Information, visit www.consumerfinance.gov/learnmore.\nStates may enforce the FCRA, and many states have their own consumer reporting laws. In some cases, you may have more rights under state law. For more information, contact your state or local consumer protection agency or your state Attorney General. For more information about your federal rights, contact:\nFor questions or concerns regarding: Please contact:\n1. a. Banks, savings associations, and credit unions with total assets of over $10 billion and their affiliates. a. Bureau of Consumer Financial Protection\n1700 G Street NW\nWashington, DC 20552\nb. Such affiliates that are not banks, savings associations, or credit unions also should list in addition to the Bureau: b. Federal Trade Commission: Consumer Response Center - FCRA Washington, DC 20580\n(877) 382-4357\n2. To the extent not included in item 1 above:\na. National banks, federal savings associations, and federal branches and federal agencies of foreign banks a. Office of the Comptroller of the Currency\nCustomer Assistance Group\n1301 McKinney Street, Suite 3450\nHouston, TX 77010-9050\nb. State member banks, branches and agencies of foreign banks (other than federal branches, federal agencies, and insured state branches of foreign banks), commercial lending companies owned or controlled by foreign banks, and organizations operating under section 25 or 25A of the Federal Reserve Act. b. Federal Reserve Consumer Help Center\nPO Box 1200\nMinneapolis, MN 55480\nc. Nonmember Insured banks, Insured State Branches of Foreign Banks, and insured state savings associations c. FDIC Consumer Response Center\n1100 Walnut Street, Box #11\nKansas City, MO 64106\nd. Federal Credit Unions d. National Credit Union Administration\nOffice of Consumer Protection (OCP)\nDivision of Consumer Compliance and Outreach (DCCO)\n1775 Duke Street\nAlexandria, VA 22314\n3. Air carriers Asst. General Counsel for Aviation Enforcement & Proceedings\nAviation Consumer Protection Division\nDepartment of Transportation\n1200 New Jersey Avenue SE\nWashington, DC 20590\n4. Creditors Subject to Surface Transportation Board Office of Proceedings, Surface Transportation Board\nDepartment of Transportation\n395 E Street, SW\nWashington, DC 20423\n5. Creditors Subject to Packers and Stockyards Act Nearest Packers and Stockyards Administration area supervisor\n6. Small Business Investment Companies Associate Deputy Administrator for Capital Access\nUnited States Small Business Administration\n409 Third Street, SW, 8th Floor\nWashington, DC 20416\n7. Brokers and Dealers Securities and Exchange Commission\n100 F St NE\nWashington, DC 20549\n8. Federal Land Banks, Federal Land Bank Associations, Federal Intermediate Credit Banks, and Production Credit Associations Farm Credit Administration\n1501 Farm Credit Drive\nMcLean, VA 22102-5090\n9. Retailers, Finance Companies, and All Other Creditors Not Listed Above FTC Regional Office for region in which  the creditor operates or Federal Trade Commission: Consumer Response Center - FCRA\nWashington, DC 20580\n(877) 382-4357\nTop of Section | Top\nConsumers have the right to obtain a security freeze\nYou have a right to place a ''security freeze'' on your credit report, which will prohibit a consumer reporting agency from releasing information in your credit report without your express authorization. The security freeze is designed to prevent credit, loans, and services from being approved in your name without your consent. However, you should be aware that using a security freeze to take control over who gets access to the personal and financial information in your credit report may delay, interfere with, or prohibit the timely approval of any subsequent request or application you make regarding a new loan, credit, mortgage, or any other account involving the extension of credit. As an alternative to a security freeze, you have the right to place an initial or extended fraud alert on your credit file at no cost. An initial fraud alert is a 1-year alert that is placed on a consumer's credit file. Upon seeing a fraud alert display on a consumer's credit file, a business is required to take steps to verify the consumer's identity before extending new credit. If you are a victim of identity theft, you are entitled to an extended fraud alert, which is a fraud alert lasting 7 years. A security freeze does not apply to a person or entity, or its affiliates, or collection agencies acting on behalf of the person or entity, with which you have an existing account that requests information in your credit report for the purposes of reviewing or collecting the account. Reviewing the account includes activities related to account maintenance, monitoring, credit line increases, and account upgrades and enhancements.";
		String nya23ConsentTxt="New York Article 23-A Correction Law\n§750. Definitions. For the purposes of this article, the following terms shall have the following meanings:\n(1)\"Public agency\"; means the state or any local subdivision thereof, or any state or local department, agency, board or commission.\n(2)\"Private employer\"; means any person, company, corporation, labor organization or association which employs ten or more persons.\n(3)\"Direct relationship\"; means that the nature of criminal conduct for which the person was convicted has a direct bearing on his fitness or ability to perform one or more of the duties or responsibilities necessarily related to the license, opportunity, or job in question.\n(4)\"License\"; means any certificate, license, permit or grant of permission required by the laws of this state, its political subdivisions or instrumentalities as a condition for the lawful practice of any occupation, employment, trade, vocation, business, or profession. Provided, however, that \"license\"; shall not, for the purposes of this article, include any license or permit to own, possess, carry, or fire any explosive, pistol, handgun, rifle, shotgun, or other firearm.\n(5)\"Employment\"; means any occupation, vocation or employment, or any form of vocational or educational training. Provided, however, that \"employment\"; shall not, for the purposes of this article, include membership in any law enforcement agency.\n§751. Applicability. The provisions of this article shall apply to any application by any person for a license or employment at any public or private employer, who has previously been convicted of one or more criminal offenses in this state or in any other jurisdiction, and to any license or employment held by any person whose conviction of one or more criminal offenses in this state or in any other jurisdiction preceded such employment or granting of a license, except where a mandatory forfeiture, disability or bar to employment is imposed by law, and has not been removed by an executive pardon, certificate of relief from disabilities or certificate of good conduct. Nothing in this article shall be construed to affect any right an employer may have with respect to an intentional misrepresentation in connection with an application for employment made by a prospective employee or previously made by a current employee.\n§752. Unfair discrimination against persons previously convicted of one or more criminal offenses prohibited. No application for any license or employment, and no employment or license held by an individual, to which the provisions of this article are applicable, shall be denied or acted upon adversely by reason of the individual's having been previously convicted of one or more criminal offenses, or by reason of a finding of lack of \"good moral character\"; when such finding is based upon the fact that the individual has previously been convicted of one or more criminal offenses, unless:\n(1) There is a direct relationship between one or more of the previous criminal offenses and the specific license or employment sought or held by the individual; or\n(2) the issuance or continuation of the license or the granting or continuation of the employment would involve an unreasonable risk to property or to the safety or welfare of specific individuals or the general public.\n§753. Factors to be considered concerning a previous criminal conviction; presumption. 1. In making a determination pursuant to section seven hundred fifty-two of this chapter, the public agency or private employer shall consider the following factors:\n(a) The public policy of this state, as expressed in this act, to encourage the licensure and employment of persons previously convicted of one or more criminal offenses.\n(b) The specific duties and responsibilities necessarily related to the license or employment sought or held by the person.\n(c) The bearing, if any, the criminal offense or offenses for which the person was previously convicted will have on his fitness or ability to perform one or more such duties or responsibilities.\n(d) The time which has elapsed since the occurrence of the criminal offense or offenses.\n(e) The age of the person at the time of occurrence of the criminal offense or offenses.\n(f) The seriousness of the offense or offenses.\n(g) Any information produced by the person, or produced on his behalf, in regard to his rehabilitation and good conduct.\n(h) The legitimate interest of the public agency or private employer in protecting property, and the safety and welfare of specific individuals or the general public.\n2. In making a determination pursuant to section seven hundred fifty-two of this chapter, the public agency or private employer shall also give consideration to a certificate of relief from disabilities or a certificate of good conduct issued to the applicant, which certificate shall create a presumption of rehabilitation in regard to the offense or offenses specified therein.\n§754. Written statement upon denial of license or employment. At the request of any person previously convicted of one or more criminal offenses who has been denied a license or employment, a public agency or private employer shall provide, within thirty days of a request, a written statement setting forth the reasons for such denial.\n§755. Enforcement. 1. In relation to actions by public agencies, the provisions of this article shall be enforceable by a proceeding brought pursuant to article seventy-eight of the civil practice law and rules. 2. In relation to actions by private employers, the provisions of this article shall be enforceable by the division of human rights pursuant to the powers and procedures set forth in article fifteen of the executive law, and, concurrently, by the New York city commission on human rights.";
		String cairConsnentTxt="California Disclosure Regarding Employment and/or Volunteerism/Non-Employee Position Background Report\nVerified Volunteers, 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3, www.verifiedvolunteers.com, may obtain a consumer report and/or an investigative consumer report (\"REPORT\") that contains background information about you in connection with your employment and/or volunteerism/non-employee position. Verified Volunteers may obtain further reports throughout your employment and/or volunteerism/non-employee position so as to update your report without providing further disclosure or obtaining additional consent.\nThe REPORT may contain information about your character, general reputation, personal characteristics and mode of living. The REPORT may include, but is not limited to, credit reports and credit history information; criminal and other public records and history; public court records; motor vehicle and driving records; and Social Security verification and address history, subject to any limitations imposed by applicable federal and state law. This information may be obtained from public record and private sources, including credit bureaus, government agencies and judicial records, and other sources.\nIf an investigative consumer REPORT is obtained, in addition to the description above, the nature and scope of any such REPORT will be personal references.\nYou may inspect Verified Volunteers’ files concerning you during normal business hours and upon reasonable notice. You can inspect the files at Verified Volunteers’ offices if you furnish proper identification, and you can obtain a copy by paying duplication costs. One other person can accompany you if he or she furnishes reasonable identification. You can also obtain a copy of your files by sending Verified Volunteers at the address listed above a written request, including proper identification, by certified mail. Verified Volunteers will give you a summary of the information in the files by telephone if you submit a written request including proper identification. Verified Volunteers has trained personnel who can explain the information furnished to you, and can provide a written explanation of any coded information contained in your files. \"Proper identification\" includes documents such as a valid driver’s license, Social Security card, military identification card or credit card. If necessary, Verified Volunteers may request additional information about your employment and/or volunteerism/non-employee position and personal or family history to verify your identity.";
		String mafcraConsnentTxt="Summary of Your Rights Under the Massachusetts Consumer Credit Reporting Act\nYou have the right to obtain a free copy of your credit file from a consumer credit reporting agency. You may be charged a reasonable fee not exceeding eight dollars. There is no fee, however, if you have been turned down for credit, employment, insurance, or rental dwelling because of information in your credit report within the preceding sixty days. The consumer credit reporting agency must provide someone to help you interpret the information in your credit file. Each calendar year you are entitled to receive, upon request, one free consumer credit report.\nYou have a right to dispute inaccurate information by contacting the consumer credit reporting agency directly. However, neither you nor any credit repair company or credit service organization has the right to have accurate, current, and verifiable information removed from your credit report. In most cases, under state and federal law, the consumer credit reporting agency must remove accurate, negative information from your report only if it is over seven years old, and must remove bankruptcy information only if it is over ten years old.\nIf you have notified a consumer credit reporting agency in writing that you dispute the accuracy of information in your file, the consumer credit reporting agency must then, within thirty business days, reinvestigate and modify or remove inaccurate information. The consumer credit reporting agency may not charge a fee for this service. Any pertinent information and copies of all documents you have concerning a dispute should be given to the consumer credit reporting agency.\nIf reinvestigation does not resolve the dispute to your satisfaction, you may send a statement to the consumer credit reporting agency to keep in your file, explaining why you think the record is inaccurate. The consumer credit reporting agency must include your statement about the disputed information in a report it issues about you.\nYou have a right to receive a record of all inquiries relating to a credit transaction initiated in the six months preceding your request, or two years in the case of a credit report used for employment purposes. This record shall include the recipients of any consumer credit report.\nYou have the right to opt out of any pre-screening lists compiled by or with the assistance of a consumer credit reporting agency by calling the agency's toll-free telephone number or contacting the agency in writing. You may be entitled to collect compensation, in certain circumstances, if you are damaged by a person's negligent or intentional failure to comply with the provisions of the credit report act.";
		String njfcraConsnentTxt="Description of Your Rights under the New Jersey Fair Credit Reporting Act\nThe New Jersey Fair Credit Reporting Act is modeled after the federal Fair Credit Reporting Act and provides you with many of the same rights. You have received A Summary of Your Rights Under the Fair Credit Reporting Act.\nNew Jersey Consumers Have the Right to Obtain a Security Freeze\nYou may obtain a security freeze on your credit report to protect your privacy and ensure that credit is not granted in your name without your knowledge. You have a right to place a \"security freeze\" on your credit report pursuant to New Jersey law.\nThe security freeze will prohibit a consumer reporting agency from releasing any information in your credit report without your express authorization or approval.\nThe security freeze is designed to prevent credit, loans, and services from being approved in your name without your consent. When you place a security freeze on your credit report, within five business days you will be provided a personal identification number or password to use if you choose to remove the freeze on your credit report or to temporarily authorize the release of your credit report for a specific party, parties or period of time after the freeze is in place. To provide that authorization, you must contact the consumer reporting agency and provide all of the following:\n(i) The unique personal identification number or password provided by the consumer reporting agency;\n(ii) Proper identification to verify your identity; and\n(iii) The proper information regarding the third party or parties who are to receive the credit report or the period of time for which the report shall be available to users of the credit report.\nA consumer reporting agency that receives a request from a consumer to lift temporarily a freeze on a credit report shall comply with the request no later than three business days or less, as provided by regulation, after receiving the request.\nA security freeze does not apply to circumstances in which you have an existing account relationship and a copy of your report is requested by your existing creditor or its agents or affiliates for certain types of account review, collection, fraud control or similar activities.\nIf you are actively seeking credit, you should understand that the procedures involved in lifting a security freeze may slow your own applications for credit. You should plan ahead and lift a freeze, either completely if you are shopping around, or specifically for a certain creditor, a few days before actually applying for new credit.\nYou have a right to bring a civil action against someone who violates your rights under the credit reporting laws. The action can be brought against a consumer reporting agency or a user of your credit report.\n(2) If a consumer requests information about a security freeze, he shall be provided with the notice provided in paragraph (1) of this subsection and with any other information, as prescribed by the director by regulation, about how to place, temporarily lift and permanently lift a security freeze.";
		try{
			 sc.waitForPageLoad();
		     if(verifyConsent.equalsIgnoreCase("Yes")){
		    	 sc.waitForPageLoad();
		    	 tempRetval=sc.waitforElementToDisplay("invitationStep3_summaryConsent_chk", timeOutinSeconds);
		    	 verifyTextWithReport("invitationStep3_commonConsent_txt", commonConsentTxt,"Step 3 Common Consent" );
		    	 verifyTextWithReport("invitationStep3_esdConsent_txt", esdConsentTxt,"Step 3 ESD Consent");
				 verifyTextWithReport("invitationStep3_vvscConsent_txt", vvscConsentTxt,"Step 3 VVSC Consent");
				 verifyTextWithReport("invitationStep3_fcraConsent_txt", fcraConsentTxt,"Step 3 FCRA Consent");
				 verifyTextWithReport("invitationStep3_disclosureConsentVerbiage_txt", disclosureConsentVerbiageTxt,"Step 3 DisclosureConsentVerbiage" );
				 verifyTextWithReport("invitationStep3_rightsSummaryConsent_txt", rightsSummaryConsentTxt,"Step 3 RightsSummaryConsent" );
				 verifyTextWithReport("invitationStep3_eSignatureConsent_txt", eSignatureConsentTxt,"Step 3 eSignatureConsent" );
		     }
			
			tempRetval=sc.waitforElementToDisplay("invitationStep3_summaryConsent_chk", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.waitForPageLoad();
				//if addressState is one of the following then an additional checkbox should be displayed.
				// States - California, Massachusetts, Minnesota, New Jersey , New York , Washington and Oklahoma 
				addressState = Globals.testSuiteXLS.getCellData_fromTestData("AddressState");
				if(addressState.equalsIgnoreCase("California") || addressState.equalsIgnoreCase("Massachusetts") || addressState.equalsIgnoreCase("Minnesota") || addressState.equalsIgnoreCase("New Jersey") || addressState.equalsIgnoreCase("Oklahoma")|| addressState.equalsIgnoreCase("Washington")|| addressState.equalsIgnoreCase("New York") ){

					tempRetval=Globals.KEYWORD_FAIL;
					
					if(addressState.equalsIgnoreCase("New York")){
					    tempRetval=sc.waitforElementToDisplay("invitationStep3_NYConsent_chk", 2);	
					    verifyTextWithReport("invitationStep3_splConsentNY_txt", splStateNYTxt,"Step 3 "+addressState+" Consent" );	
					    verifyTextWithReport("invitationStep3_nya23Consent_txt", nya23ConsentTxt,"Step 3 "+addressState+" NY 23A Consent" );	
					    consentChkPath="invitationStep3_NYConsent_chk";
					}else if(addressState.equalsIgnoreCase("Washington")){ 
						tempRetval=sc.waitforElementToDisplay("invitationStep3_WAConsent_chk", 2);	
					    verifyTextWithReport("invitationStep3_splConsentWA_txt", splStateWATxt,"Step 3 "+addressState+" Consent" );
					    consentChkPath="invitationStep3_WAConsent_chk";
					}else{
						tempRetval=sc.waitforElementToDisplay("invitationStep3_californiaConsent_chk", 2);
						verifyTextWithReport("invitationStep3_splConsentOther_txt", splStateOthertxt,"Step 3 "+addressState+" Consent" ); 
						if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
						
						if(addressState.equalsIgnoreCase("California")){
						   verifyTextWithReport("invitationStep3_cairConsent_txt", cairConsnentTxt,"Step 3 "+addressState+" California Disclosure Regarding Consent" );
						}
						if(addressState.equalsIgnoreCase("Massachusetts")){
							   verifyTextWithReport("invitationStep3_mafcraConsent_txt", mafcraConsnentTxt,"Step 3 "+addressState+"  Massachusetts Consumer Credit Reporting Act Consent" );
						}
						if(addressState.equalsIgnoreCase("New Jersey")){
							   verifyTextWithReport("invitationStep3_njfcraConsent_txt", njfcraConsnentTxt,"Step 3 "+addressState+"  New Jersey Fair Credit Reporting Act Consent" );
						}	
						}
						consentChkPath="invitationStep3_californiaConsent_chk";
					}
					
												
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						sc.STAF_ReportEvent("Pass", "Step 3", "Consent check box for "+ addressState +"is present" , 1);
						log.info("State Consent checkbox is present for state -  "+addressState);
						
						spConsent=true;						

					}else{
						sc.STAF_ReportEvent("Fail", "Step 3", "Consent check box for "+ addressState +"is NOT present" , 1);
						log.error("State Consent checkbox is NOT present for state -  "+addressState);
						throw new Exception("State Consent checkbox is NOT present for state -  "+addressState);
					}

				}
								
				if(spConsent==true){
					tempRetval=sc.checkCheckBox(consentChkPath);
				}
				tempRetval = sc.checkCheckBox("invitationStep3_summaryConsent_chk");
				tempRetval = sc.checkCheckBox("invitationStep3_disclosureConsent_chk");
				tempRetval = sc.checkCheckBox("invitationStep3_eSignConsent_chk");

				tempRetval = sc.waitforElementToDisplay("invitationStep3_fName_txt", timeOutinSeconds);
				tempRetval = sc.setValueJsChange("invitationStep3_fName_txt", Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName"));
				tempRetval = sc.setValueJsChange("invitationStep3_lName_txt", Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName"));

				sc.STAF_ReportEvent("Pass", "Step 3", "Consent details entered by volunteer", 1);

				sc.clickWhenElementIsClickable("invitationStep3_saveAndContinue_btn", timeOutinSeconds);


				retval = Globals.KEYWORD_PASS;

			}else{
				sc.STAF_ReportEvent("Fail", "Step 3", "Step 3 page has not been loaded" , 1);
				log.error("Method-invitationOrderStep3 | Page has not loaded");
				throw new Exception("Unable to complete STEP 3");
			}

		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-invitationOrderStep3 | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to complete Ordering Step-4
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String invitationOrderStep4() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 10;
		String tempRetval=Globals.KEYWORD_FAIL;
		String npnOrderID =null;
		String swestOrderID=null;
		String orderedDate=null;
		String volDonationAmount="";
		boolean paAbuseFlag = false;
		String pricingType = sc.testSuiteXLS.getCellData_fromTestData("PricingName");
		String OrderHoldQueuesettings = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldQueuecheckbox");

		try{
			sc.waitForPageLoad();
			tempRetval=sc.waitforElementToDisplay("invitationStep4_infoCorrect_chk", 60);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				//				driver.findElement(LocatorAccess.getLocator("invitationStep4_infoCorrect_chk")).click();
				sc.clickWhenElementIsClickable("invitationStep4_infoCorrect_chk",timeOutinSeconds);

				//Product list verification
				String prodlistvalidation = Globals.testSuiteXLS.getCellData_fromTestData("Step4_VerifyProductList");
				if(prodlistvalidation.equalsIgnoreCase("Yes")){
					verifyProdListInvitationOrdering();
				}

				/*It will check if Order HoldQueue Settings is ON or OFF *//*@Lakshmi*/ 
				if(OrderHoldQueuesettings.equalsIgnoreCase("Yes")){
					sc.waitForPageLoad();
					double totalcost = fetchExpectedTotalPriceSTEP4();
                    String orderHoldQAmt = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldqueueamount");
                    double orderHoldAmt = Double.parseDouble(orderHoldQAmt);					
				/*If Order Hold is ON and meets the Below requirement then the order will placed as HOLD*//*@Lakshmi*/
				if(pricingType.equalsIgnoreCase("Client Pays All") && OrderHoldQueuesettings.equalsIgnoreCase("Yes") && (orderHoldAmt <= totalcost) )
				{
					//if Client Pays All no amount is to be paid by the volunteer then Submit Order button would be displayed.

					tempRetval =  sc.waitforElementToDisplay("invitationStep4_submitOrder_btn", 3);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						sc.clickWhenElementIsClickable("invitationStep4_submitOrder_btn", timeOutinSeconds);
					}
					sc.waitForPageLoad();
					Thread.sleep(1000);
					// capturing NPN on Success HoldPage
					tempRetval=sc.waitforElementToDisplay("invitationStep5_goToMyProfile_lnk", 60);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){						
						String textInUI = driver.findElement(LocatorAccess.getLocator("invitationStep5_orderIDSection_section")).getText();

						if(textInUI.contains("Order Number:")){
							String strOrderIDS=textInUI.split("Order Number:")[1].trim();
							strOrderIDS = strOrderIDS.replace("Go to My Profile", "");
							npnOrderID= strOrderIDS.split("\n")[0];
							if(npnOrderID != null){
								log.info("Method-invitationOrderStep4 | Hold Order ID generated | NPN Order ID- "+npnOrderID);
								orderedDate = sc.getTodaysDate();
								Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
								Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderID);
								sc.addParamVal_InEmail("NPNOrderID", npnOrderID);
								sc.STAF_ReportEvent("Pass", "Step 5", " Hold Order id has been generated  "+npnOrderID , 1);
								//							sc.clickWhenElementIsClickable("invitationStep5_goToMyProfile_lnk", timeOutinSeconds);
								retval = Globals.KEYWORD_PASS;
								// API Retrieval Notify Response check 
							    String ntMsgAPilog=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
					            String ntMsgnwapiendpt=Globals.testSuiteXLS.getCellData_fromTestData("APIRetrievalNotifyResponseNewEndpoint");
					            if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
					            	
						            if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") || ntMsgAPilog.equalsIgnoreCase("") || ntMsgnwapiendpt.equalsIgnoreCase("")||ntMsgAPilog.isEmpty() || ntMsgnwapiendpt.isEmpty()){
						            	//sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
						            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
						            	Queryforapilog(npnOrderID,"Hold","Blank","NoNo");            	
						            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
						            	Queryforapilog(npnOrderID,"Hold","Blank","YesNo");
						            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
						            	Queryforapilog(npnOrderID,"Hold","Blank","NoYes");
						            	getOrderRequestXML("Hold");
						            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
						            	Queryforapilog(npnOrderID,"Hold","Blank","YesYes");
						            	getOrderRequestXML("Hold");
						            }else{
						            	sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
						            }
					            }						
							}
						}
						
					}else{
						log.error("Method-invitationOrderStep4 | Successfull Completion Hold page is not displayed");
						sc.STAF_ReportEvent("Fail", "Step 5", "Successfull Completion Hold page is not displayed", 1);
						throw new Exception("Successfull Completion Hold page is not displayed");
					}
				}
			}
				/*Normal Ordering this part of the code will not go to Order Hold Status*//*@Lakshmi*/
				else{

					String volunteerDonationRequired =  Globals.testSuiteXLS.getCellData_fromTestData("VolunteerDonationRequired");
					//Donation section wont be present when Client Pays fees Only is selected and there is no amount in Source Fees.Need to check the pricing section first
					tempRetval = Globals.KEYWORD_FAIL;
					tempRetval = sc.waitforElementToDisplay("invitationStep4_donationSelection_dd", 2);

					if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						if(volunteerDonationRequired.equalsIgnoreCase("Yes")){
							sc.clickWhenElementIsClickable("invitationStep4_donation_chk", timeOutinSeconds);
							sc.waitForPageLoad();
							Thread.sleep(4000);
							volDonationAmount= sc.selectValue_byIndex("invitationStep4_donationSelection_dd", 2);
							if(volDonationAmount.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
								sc.STAF_ReportEvent("Fail", "Step 4", "Unable to select Volunteer Donation Amount", 1);
							}else{
								sc.STAF_ReportEvent("Pass", "Step 4", "Volunteer Donation Yes selected.Amount ="+volDonationAmount, 1);
							}
	
						}else{
							sc.clickWhenElementIsClickable("invitationStep4_NoDonation_chk", timeOutinSeconds);
							sc.waitForPageLoad();
							Thread.sleep(2000);
							sc.STAF_ReportEvent("Pass", "Step 4", "Volunteer Donation Not selected", 1);
	
						}
					}
					Thread.sleep(1000);

					//  Fast Pass wont section wont appear if L1/L2/L3 is not part of the order.

					String l1Present;
					String l2Present;
					String l3Present;

					l1Present = isProductPartOfOrdering("L1");
					l2Present = isProductPartOfOrdering("L2");
					l3Present = isProductPartOfOrdering("L3");
					if(l1Present.equalsIgnoreCase(Globals.KEYWORD_PASS)||l2Present.equalsIgnoreCase(Globals.KEYWORD_PASS) || l3Present.equalsIgnoreCase(Globals.KEYWORD_PASS)){
										
						// capturing and Verifying FastPass Heading
						
						String fastPassheading = "Sign up for the Fast-Pass";
						tempRetval=driver.findElement(LocatorAccess.getLocator("invitationStep4_fastPassYes_headingtext")).getText();
						if(tempRetval.equals(fastPassheading)){					
							sc.STAF_ReportEvent("Pass", "Step4", "FastPasss Title match", 1);
						}
						else{
							sc.STAF_ReportEvent("Fail", "Step 4", "FastPasss Title miss match", 1);
						}
						
						// selecting the FastPass based on requirement 
						
						String fastPassRequired = Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_FastPass");
						if(fastPassRequired.equalsIgnoreCase("Yes")){
							sc.clickWhenElementIsClickable("invitationStep4_fastPassYes_chk", timeOutinSeconds);
							sc.waitForPageLoad();
							Thread.sleep(3000);
							//						driver.findElement(LocatorAccess.getLocator("invitationStep4_fastPassYes_chk")).click();
							sc.STAF_ReportEvent("Pass", "Step 4", "Volunteer Fast Pass Required Selected", 1);
						}else{
							sc.waitForPageLoad();
							Thread.sleep(1000);
							sc.clickWhenElementIsClickable("invitationStep4_fastPassNo_chk", timeOutinSeconds);
							sc.waitForPageLoad();
							Thread.sleep(3000);
				
							//						driver.findElement(LocatorAccess.getLocator("invitationStep4_fastPassNo_chk")).click();
							sc.STAF_ReportEvent("Pass", "Step 4", "Volunteer Fast Pass Not Required Selected", 1);
						}
					}else{
						tempRetval = Globals.KEYWORD_FAIL;
						tempRetval = sc.waitforElementToDisplay("invitationStep4_fastPassNo_chk", 2);
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.STAF_ReportEvent("Fail", "Step 4", "Volunteer Fast Pass Section is present even though L1/L2/L3 is not part of Ordering", 1);
						}else{
							sc.STAF_ReportEvent("Pass", "Step 4", "Volunteer Fast Pass Section is not present as L1/L2/L3 is not part of Ordering", 1);
						}
					}
						
					//				pricing verification	
					    sc.waitForPageLoad();
					    Thread.sleep(8000);
					    paAbuseFlag=verifyPricingStep4();

						Thread.sleep(1000);
						String removePA = Globals.testSuiteXLS.getCellData_fromTestData("RemovePAFlag");
					    if(removePA.equals("Yes")){
					    	RemoveAbusePa();
					    	paAbuseFlag=false;
					    }
						//if no amount is to be paid by the volunteer then Submit Order button would be displayed else Continue Payement.
						tempRetval =  sc.waitforElementToDisplay("invitationStep4_submitOrder_btn", 3);
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.clickWhenElementIsClickable("invitationStep4_submitOrder_btn", timeOutinSeconds);
							sc.waitForPageLoad();
							//					driver.findElement(LocatorAccess.getLocator("invitationStep4_submitOrder_btn")).click();
						}else{
							sc.clickWhenElementIsClickable("invitationStep4_continuePayment_btn", timeOutinSeconds);
							sc.waitForPageLoad();
							//					driver.findElement(LocatorAccess.getLocator("invitationStep4_continuePayment_btn")).click();

							/*//By executing a java script - to find out number of iframes
							JavascriptExecutor exe = (JavascriptExecutor) driver;
							Integer numberOfFrames = Integer.parseInt(exe.executeScript("return window.length").toString());
							System.out.println("Number of iframes on the page are " + numberOfFrames);*/

							//By finding all the web elements using iframe tag
							List<WebElement> iframeElements = driver.findElements(By.tagName("iframe"));

							if (iframeElements.size() <=0 ){
								log.error("Method-invitationOrderStep4 | Fake Payment Frame is not displayed");
								sc.STAF_ReportEvent("Fail", "Step 4", "Fake Payment Frame is not displayed", 1);
								return Globals.KEYWORD_FAIL;
							}
							//System.out.println("The total number of iframes are " + iframeElement.size());
							WebElement iframeElement = driver.findElement(By.tagName("iframe"));
							driver.switchTo().frame(iframeElement); // need to switch control over to iframe


							
							int flag=1;
							tempRetval=sc.waitforElementToDisplay("invitationStep4_fakePayment_btn", timeOutinSeconds);
							while(flag<=3){								
								if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
									break;
								}else{
									flag++;
									tempRetval=sc.waitforElementToDisplay("invitationStep4_fakePayment_btn", timeOutinSeconds);
								}
							}
							tempRetval=sc.waitforElementToDisplay("invitationStep4_fakePayment_btn", timeOutinSeconds);
							if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
								sc.highlight("invitationStep4_fakePayment_btn");
								sc.clickWhenElementIsClickable("invitationStep4_fakePayment_btn", timeOutinSeconds);
								sc.waitForPageLoad();
								//						driver.findElement(LocatorAccess.getLocator("invitationStep4_fakePayment_btn")).click();
								sc.STAF_ReportEvent("Pass", "Step 4", "Fake Payment selected", 1);
							}else{
								tempRetval=sc.waitforElementToDisplay("invitationStep4_fakePayment_btn", timeOutinSeconds);
								log.error("Method-invitationOrderStep4 | Fake Payment Button is not displayed");
								sc.STAF_ReportEvent("Fail", "Step 4", "Fake Payment Button is not displayed", 1);
								return Globals.KEYWORD_FAIL;
							}
						}	
						
						if(paAbuseFlag==true){ //Psave-This section for PA abuse Thank you page validation
							sc.waitForPageLoad();
							sc.waitforElementToDisplay("invitationStep5_paabusethankyou_lnk", 300);
					  		String paAbuselink="https://www.compass.state.pa.us/cwis";
					  		tempRetval=sc.verifyText("invitationStep5_paabusethankyou_lnk", paAbuselink);
					  		String textInUIt = driver.findElement(LocatorAccess.getLocator("invitationStep5_paabusethankyou_span")).getText();
					  		
					  		if(textInUIt.contains("Your Authorization Code is")){
					  		//checking pa order placed notification email
					  			
					  			HashMap<String, String> emailDetails=  new HashMap<String, String>();
					  			String emailFound=null;
					  			String emailBodyText =null;
					  			String fetchOrderid = null;
					  			String subjectEmailPattern = "One more step to become a Verified Volunteer!";
								String bodyEmailPattern = paAbuselink;
								Thread.sleep(60000);	
								int startIndex;
					            int endindex ;
					            String npnordersrchpattern="Order #";
								if(Globals.EXECUTION_MACHINE.equalsIgnoreCase("jenkins")) {
									bodyEmailPattern =Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
						
								    emailBodyText = FetchInviatationURLFromDB(bodyEmailPattern,subjectEmailPattern);
								    if(emailBodyText.equalsIgnoreCase("") || emailBodyText == null) {
								        sc.STAF_ReportEvent("Fail", "Fetch Invite", "Applicant invitation mail NOT fetched by Database.", 0);
						                throw new Exception("Applicant Invitation mail NOT received from Database");
								    }
								   // emailFound="True";
								   emailBodyText = emailBodyText.replace("\"", "").trim();
								    startIndex = emailBodyText.indexOf(npnordersrchpattern);
								    
					                endindex = emailBodyText.indexOf("<br /><div style=clear:both></div>");
									fetchOrderid = emailBodyText.substring(startIndex+7, endindex).trim();
									npnOrderID= fetchOrderid.split(" - ")[0];
									String swestID = fetchOrderid.split(" - ")[1];
									swestOrderID = swestID.split("<")[0];
									orderedDate = sc.getTodaysDate();
									Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
									Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderID);
									Globals.testSuiteXLS.setCellData_inTestData("SWestOrderID", swestOrderID);
					                
								}else{
								
								
									emailDetails = Email.fetchEmailFromOutlook(subjectEmailPattern,bodyEmailPattern,60);
									emailFound = emailDetails.get("EmailFound");
									if (emailFound.equalsIgnoreCase("True")){
										emailBodyText = emailDetails.get("EmailBody");
									}else{
										
											log.error("Method-invitationOrderStep4 | PA abuse order notification not received");
											sc.STAF_ReportEvent("Fail", "Step 5", "PA abuse orer notification not received", 1);
											throw new Exception("PA abuse orer notification not received");
										
									}
									emailBodyText = emailBodyText.replace("\"", "").trim();
									
									npnordersrchpattern="Order #";
									
									startIndex = emailBodyText.indexOf(npnordersrchpattern);
									 endindex = emailBodyText.indexOf("Your Background Check Order #");
									fetchOrderid = emailBodyText.substring(startIndex+7, endindex).trim();
									npnOrderID= fetchOrderid.split(" - ")[0];
									swestOrderID = fetchOrderid.split(" - ")[1];
									
								}
								
									
									if(npnOrderID != null && swestOrderID != null && swestOrderID.length() > 4){
										log.info("Method-invitationOrderStep4 | Order ID generated | NPN Order ID- "+npnOrderID + "| SWest Order ID = "+swestOrderID);
										orderedDate = sc.getTodaysDate();
										Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
										Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderID);
										Globals.testSuiteXLS.setCellData_inTestData("SWestOrderID", swestOrderID);
										sc.addParamVal_InEmail("SWestOrderID", swestOrderID);
										sc.addParamVal_InEmail("NPNOrderID", npnOrderID);
										sc.STAF_ReportEvent("Pass", "Step 5", "PA abuse order notification mail successfully received with "+npnOrderID+"-"+swestOrderID , 1);
										//							sc.clickWhenElementIsClickable("invitationStep5_goToMyProfile_lnk", timeOutinSeconds);
										retval = Globals.KEYWORD_PASS;
										// API Retrieval Notify Response check 
									    String ntMsgAPilog=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
							            String ntMsgnwapiendpt=Globals.testSuiteXLS.getCellData_fromTestData("APIRetrievalNotifyResponseNewEndpoint");
							            if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
							            	
								            if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") || ntMsgAPilog.equalsIgnoreCase("") || ntMsgnwapiendpt.equalsIgnoreCase("")||ntMsgAPilog.isEmpty() || ntMsgnwapiendpt.isEmpty()){
								            	//sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
								            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
								            	Queryforapilog(npnOrderID,"InProgress","Blank","NoNo");            	
								            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
								            	Queryforapilog(npnOrderID,"InProgress","Blank","YesNo");
								            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
								            	Queryforapilog(npnOrderID,"InProgress","Blank","NoYes");
								            	getOrderRequestXML("InProgress");
								            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
								            	Queryforapilog(npnOrderID,"InProgress","Blank","YesYes");
								            	getOrderRequestXML("InProgress");
								            }else{
								            	sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
								            }
							            }
										
									}else{
									log.error("Method-invitationOrderStep4 | SWest Order ID NOT generated");

										sc.STAF_ReportEvent("Fail", "Step 5", "Order id has been NOT been generated", 1);
										Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", "");
										Globals.testSuiteXLS.setCellData_inTestData("SWestOrderID", "");
									}

									

								
					  			
					  			
					  			
					  		}else{
								log.error("Method-invitationOrderStep4 | PA abuse Thank you page not loaded");
								sc.STAF_ReportEvent("Fail", "Step 5", "PA abuse Thank you page not loaded", 1);
								throw new Exception("PA abuse order notification not received");
							}
					  		
						}else{

						  //capturing NPN and SWest Order ID
						sc.waitForPageLoad();	
						tempRetval=sc.waitforElementToDisplay("invitationStep5_goToMyProfile_lnk", 180);
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							String textInUI = driver.findElement(LocatorAccess.getLocator("invitationStep5_orderIDSection_section")).getText();
	
							if(textInUI.contains("Order Number:")){
								String strOrderIDS=textInUI.split("Order Number:")[1].trim();
								strOrderIDS = strOrderIDS.replace("Go to My Profile", "");
								npnOrderID= strOrderIDS.split(" - ")[0];
								swestOrderID = strOrderIDS.split(" - ")[1];
								if(npnOrderID != null && swestOrderID != null && swestOrderID.length() > 4){
									log.info("Method-invitationOrderStep4 | Order ID generated | NPN Order ID- "+npnOrderID + "| SWest Order ID = "+swestOrderID);
									orderedDate = sc.getTodaysDate();
									Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
									Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderID);
									Globals.testSuiteXLS.setCellData_inTestData("SWestOrderID", swestOrderID);
									sc.addParamVal_InEmail("SWestOrderID", swestOrderID);
									sc.addParamVal_InEmail("NPNOrderID", npnOrderID);
									sc.STAF_ReportEvent("Pass", "Step 5", "Order id has been generated  "+npnOrderID+"-"+swestOrderID , 1);
									//							sc.clickWhenElementIsClickable("invitationStep5_goToMyProfile_lnk", timeOutinSeconds);
									retval = Globals.KEYWORD_PASS;
									// API Retrieval Notify Response check 
								    String ntMsgAPilog=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
						            String ntMsgnwapiendpt=Globals.testSuiteXLS.getCellData_fromTestData("APIRetrievalNotifyResponseNewEndpoint");
						            if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
						            	
							            if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") || ntMsgAPilog.equalsIgnoreCase("") || ntMsgnwapiendpt.equalsIgnoreCase("")||ntMsgAPilog.isEmpty() || ntMsgnwapiendpt.isEmpty()){
							            	//sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
							            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
							            	Queryforapilog(npnOrderID,"InProgress","Blank","NoNo");            	
							            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
							            	Queryforapilog(npnOrderID,"InProgress","Blank","YesNo");
							            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
							            	Queryforapilog(npnOrderID,"InProgress","Blank","NoYes");
							            	getOrderRequestXML("InProgress");
							            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
							            	Queryforapilog(npnOrderID,"InProgress","Blank","YesYes");
							            	getOrderRequestXML("InProgress");
							            }else{
							            	sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
							            }
						            }					
							}else{
								log.error("Method-invitationOrderStep4 | SWest Order ID NOT generated");
	
									sc.STAF_ReportEvent("Fail", "Step 5", "Order id has been NOT been generated", 1);
									Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", "");
									Globals.testSuiteXLS.setCellData_inTestData("SWestOrderID", "");
								}
	
							}
						
							//System.out.println(textInUI);
	
						}else{
							log.error("Method-invitationOrderStep4 | Successfull Completion page is not displayed");
							sc.STAF_ReportEvent("Fail", "Step 5", "Successfull Completion page is not displayed", 1);
							throw new Exception("Successfull Completion page is not displayed");
						}
	
					}
				}
				
				
			}else{
				log.error("Method-invitationOrderStep4 | Page has not loaded");
				sc.STAF_ReportEvent("Fail", "Step 4", "Page has not loaded", 1);
				throw new Exception("Step 4 Page has not loaded");
			}

		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-invitationOrderStep4 | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	* Method to retrive sterling west id and npnorder id from PA abuse from database for jenkins
	* @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	* @author Navisha
	* @throws Exception
	***************************************************************************************************/
	
	public static String FetchInviatationURLFromDB(String bodyEmailPattern,String subjectEmailPattern) throws Exception {
		String retval   = Globals.KEYWORD_FAIL;
        VVDAO vvDB        = null;
        try{
            vvDB    = new VVDAO();
                      
             
             String dbURL = Globals.getEnvPropertyValue("dbURL");
     		 String dbUserName = Globals.getEnvPropertyValue("dbUserName");
     		 String dbPassword = ProtectedEncryption.decrypt(Globals.getEnvPropertyValue("dbPassword"), CommonHelpMethods.createKey());
     		
//     		String dbURL = "jdbc:sqlserver://"+Globals.VV_DBServerName + ";portNumber="+Globals.VV_DBServerPort+";databaseName="+Globals.VV_DBName+";integratedSecurity=true;"; // to use windows authentication include the following code- integratedSecurity=true;";
// 			Globals.log.debug(".........Connecting to VV - DatabaseServer-"+Globals.VV_DBServerName +":"+ "-" +Globals.VV_DBName  );
// 			this.conn=DriverManager.getConnection(dbURL, Globals.VV_USERNAME, Globals.VV_PASSWORD);

     		 log.info("DB URL is :"+"\t"+dbURL);
     		
     		 vvDB      = new VVDAO(dbURL,dbUserName,dbPassword); 
              //vvDB.connectVV_DB(); // connect to absHire database
             log.info("Connected to VV Databse successfully");

            String format ="YYYY-MM-dd";
            Format formatter = new SimpleDateFormat(format);
            Date result = new java.util.Date();

            String orderedDate = formatter.format(result); 


            String selectSQL = "Select EmailBody from emailhistory where SentDate >= '"+orderedDate+"' AND EmailBody like '%"+bodyEmailPattern+"%' AND Subject like '%"+subjectEmailPattern+"%'";
            vvDB.ps = vvDB.conn.prepareStatement(selectSQL,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            Boolean flg=false;
            int initRowCount = 0;
            for(int i=1;i<=10;i++)
            {
            vvDB.rs = vvDB.ps.executeQuery();
         
            Thread.sleep(10000);
        	
            initRowCount = vvDB.getRows(vvDB.rs);
            
            if(initRowCount != 1) {
            	flg=true;
            }else {
                while(vvDB.rs.next()){
                    retval = vvDB.rs.getString("EmailBody");  
                    flg=false;
                }
                break;
            }  
            }
            if(flg==true){
            	 log.error("Unable to fetch invitation email from database as sql returned "+initRowCount + " rows");
                 retval = Globals.KEYWORD_FAIL;
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
	 * Method to create a new volunteer account
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String volunteerAccountCreation() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;
		String tempRetval=Globals.KEYWORD_FAIL;
		String username = null;
		String volunteersecurityanswer1 = null;
		String volunteersecurityanswer2 = null;

		try{
			tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_username_txt",timeOutinSeconds);

			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				if(sc.testSuiteXLS.getCellData_fromTestData("VolunteerUsername_runtimeUpdateFlag").equalsIgnoreCase("Yes")){
					int length = 9;
					username = "vol" + sc.runtimeGeneratedStringValue(length);
					Globals.testSuiteXLS.setCellData_inTestData("VolunteerUsername", username);
					log.debug(" Method-volunteerAccountCreation | Volunteer Username Runtime generated and store.value- "+username);
				}else{
					username = sc.testSuiteXLS.getCellData_fromTestData("VolunteerUsername");
				}

				tempRetval=sc.setValueJsChange("volunteerAccCreation_username_txt",username );
				tempRetval=sc.setValueJsChange("volunteerAccCreation_email_txt", Globals.fromEmailID);
				sc.STAF_ReportEvent("Pass", "Volunteer Account Creation", "Volunteer Username used for account creation - "+username, 1);

				String password = sc.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
				tempRetval=sc.setValueJsChange("volunteerAccCreation_password_txt",password );
				tempRetval=sc.setValueJsChange("volunteerAccCreation_confirmPassword_txt", password);
				String sescFlag = sc.testSuiteXLS.getCellData_fromTestData("Securityquestion_Flag");
				
				if(sescFlag.equalsIgnoreCase("Yes")){
				
				
				tempRetval=sc.checkCheckBox("volunteerAccCreation_securityquestions_chk");
				tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_securityquestion1_list",timeOutinSeconds);
				if(tempRetval.equals(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Volunteer Security Question & Answer Creation", "Page has not loaded",1);
					throw new Exception("Volunteer Security Question & Answer Creation"+ "-Page has not loaded");
				}
				tempRetval=sc.selectValue_byVisibleText("volunteerAccCreation_securityquestion1_list",Globals.testSuiteXLS.getCellData_fromTestData("Securityquestion_1"));
				
				//tempRetval=sc.selectValue_byVisibleText("volunteerAccCreation_securityquestion1_list", sc.testSuiteXLS.getCellData_fromTestData("Securityquestion_1"));
				//tempRetval = sc.selectValue_byValue("volunteerAccCreation_securityquestion1_list", Securityquestion1);
				//tempRetval=sc.selectValue_byVisibleText("volunteerAccCreation_securityquestion1_list",sc.testSuiteXLS.getCellData_fromTestData("Securityquestion_1"))
				tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_securityanswer1_txt",timeOutinSeconds);
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					if(sc.testSuiteXLS.getCellData_fromTestData("SecurityAnswer1_runtimeUpdateFlag").equalsIgnoreCase("Yes")){
						int length = 9;
						volunteersecurityanswer1 = "vol" + RandomStringUtils.randomAlphabetic(length);
						Globals.testSuiteXLS.setCellData_inTestData("SecurityAnswer1", volunteersecurityanswer1);
						log.debug(" Method-volunteerSecurityQuestiong and answer creation | Volunteer Security anser Runtime generated and store.value- "+volunteersecurityanswer1);
					}else{
					volunteersecurityanswer1 = sc.testSuiteXLS.getCellData_fromTestData("SecurityAnswer1");
					}
					tempRetval=sc.setValueJsChange("volunteerAccCreation_securityanswer1_txt",volunteersecurityanswer1);	
					sc.STAF_ReportEvent("Pass", "Volunteer Security Question & Answer Creation", "Volunteer Security Answer 1 - "+volunteersecurityanswer1, 1);
				}
				tempRetval=sc.selectValue_byVisibleText("volunteerAccCreation_securityquestion2_list", Globals.testSuiteXLS.getCellData_fromTestData("Securityquestion_2"));
				tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_securityanswer2_txt",timeOutinSeconds);
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					if(sc.testSuiteXLS.getCellData_fromTestData("SecurityAnswer2_runtimeUpdateFlag").equalsIgnoreCase("Yes")){
						int length = 9;
						volunteersecurityanswer2 = "vol" + RandomStringUtils.randomAlphabetic(length);
						Globals.testSuiteXLS.setCellData_inTestData("SecurityAnswer2", volunteersecurityanswer2);
						log.debug(" Method-volunteerSecurity answer creation | Volunteer Security anser Runtime generated and store.value- "+volunteersecurityanswer2);
					}else{
					volunteersecurityanswer2 = sc.testSuiteXLS.getCellData_fromTestData("SecurityAnswer2");
					}
				}
				tempRetval=sc.setValueJsChange("volunteerAccCreation_securityanswer2_txt",volunteersecurityanswer2 );
				sc.STAF_ReportEvent("Pass", "Volunteer Security Question & Answer Creation", "Volunteer Security Answer 2 - "+volunteersecurityanswer2, 1);
			}	
				tempRetval=sc.checkCheckBox("volunteerAccCreation_consent_chk");
				sc.addParamVal_InEmail("VolunteerUsername", username);
				sc.addParamVal_InEmail("VolunteerEmail", Globals.fromEmailID);
								
				tempRetval = sc.waitforElementToDisplay("volunteerAccCreation_submit_btn",timeOutinSeconds);
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.scrollIntoView("volunteerAccCreation_submit_btn");
				//				driver.findElement(LocatorAccess.getLocator("volunteerAccCreation_submit_btn")).click();
				sc.clickWhenElementIsClickable("volunteerAccCreation_submit_btn",timeOutinSeconds);
				sc.waitForPageLoad();
				sc.STAF_ReportEvent("Pass", "Volunteer Account Creation", "Volunteer account submit button is visible", 1);
				}
				else{
					sc.STAF_ReportEvent("Fail", "Volunteer Account Creation", "Volunteer account submit button is not visible", 1);
				}
				tempRetval=Globals.KEYWORD_FAIL;
				tempRetval = sc.waitforElementToDisplay("duplicateVolunteerAccount_login_btn", 5);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					//					driver.findElement(LocatorAccess.getLocator("duplicateVolunteerAccount_login_btn")).click();
					sc.clickWhenElementIsClickable("duplicateVolunteerAccount_login_btn",timeOutinSeconds);
					sc.waitForPageLoad();
					//TODO need to verify email sent for account registration
				}


				retval=Globals.KEYWORD_PASS;

			}else{
				sc.STAF_ReportEvent("Fail", "Volunteer Account Creation", "Volunteer account creation page is not loaded", 1);
				log.error(" Method-volunteerAccountCreation | Volunteer account  page has not loaded ");
			}
		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-volunteerAccountCreation | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to reference data in Ordering Step 2
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setData_InvitationOrder_Reference(){
		APP_LOGGER.startFunction("setData_InvitationOrder_Reference");
		String retval = Globals.KEYWORD_FAIL;
		try{
			int noOfReference ; 
			int i;

			noOfReference = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("ReferenceToBeAddedCount"));
			if(noOfReference < 1){
				sc.STAF_ReportEvent("Fail", "Step 2", "Reference Count is Test Data is set to " + noOfReference, 0);
				return retval;
			}

			sc.STAF_ReportEvent("Pass", "Step 2", "References to be added - " + noOfReference, 0);
			String fname =  Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
			String midName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");
			String lname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
			String allRefNames="";

			for(i=1;i<=noOfReference;i++){
				//				driver.findElement(LocatorAccess.getLocator("invitationStep2_addReference_btn")).click();
				int timeOutinSeconds =10;
				sc.clickWhenElementIsClickable("invitationStep2_addReference_btn",timeOutinSeconds );


				String refFullName  =  RandomStringUtils.randomAlphabetic(10) + " -'" + RandomStringUtils.randomAlphabetic(10);
				if(allRefNames.isEmpty() || allRefNames.equals("")){
					allRefNames = refFullName;
				}else{
					allRefNames=allRefNames+";"+refFullName;
				}

				String phoneNumber = Globals.Volunteer_Phone;
				String emailID = Globals.Volunteer_ReferenceEmail;
				String relationShip = Globals.Volunteer_ReferenceRelationship;

				String fNameLocator = "//*[@id='step2Form']/div[1]/div[2]/div[5]/div/div["+ i +"]/div[1]/input[@type='text' and @name='ReferenceKnownAsFirstName']" ;
				String midNameLocator = "//*[@id='step2Form']/div[1]/div[2]/div[5]/div/div["+ i +"]/div[2]/input[@type='text' and @name='ReferenceKnownAsMiddleName']";
				String lNameLocator = "//*[@id='step2Form']/div[1]/div[2]/div[5]/div/div["+ i +"]/div[3]/input[@type='text' and @name='ReferenceKnownAsLastName']";
				String refFullNameLocator = "//*[@id='step2Form']/div[1]/div[2]/div[5]/div/div["+ i +"]/div[4]/input[@type='text' and @name='ReferenceName']";
				String phoneNoLocator = "//*[@id='step2Form']/div[1]/div[2]/div[5]/div/div["+ i +"]/div[5]/input[@type='text' and @name='ReferencePhone']";
				String emailIDLocator = "//*[@id='step2Form']/div[1]/div[2]/div[5]/div/div["+ i +"]/div[6]/input[@type='text' and @name='ReferenceEmail']";
				String relationShipLocator = "//*[@id='step2Form']/div[1]/div[2]/div[5]/div/div["+ i +"]/div[7]/input[@type='text' and @name='Relationship']";

				driver.findElement(By.xpath(fNameLocator)).sendKeys(fname);
				driver.findElement(By.xpath(midNameLocator)).sendKeys(midName);
				driver.findElement(By.xpath(lNameLocator)).sendKeys(lname);
				driver.findElement(By.xpath(refFullNameLocator)).sendKeys(refFullName);
				driver.findElement(By.xpath(phoneNoLocator)).sendKeys(phoneNumber);
				driver.findElement(By.xpath(emailIDLocator)).sendKeys(emailID);
				driver.findElement(By.xpath(relationShipLocator)).sendKeys(relationShip);

				sc.STAF_ReportEvent("Pass", "Step 2", "Reference Added.Full Name - " + refFullName, 0);
				log.info("Method-setData_InvitationOrder_Reference | Reference Name Added : - "+ refFullName );


			}
			Globals.testSuiteXLS.setCellData_inTestData("ReferenceNames", allRefNames);
			retval = Globals.KEYWORD_PASS;
		}catch(Exception e){

			log.error("Method-setData_InvitationOrder_Reference | Exception - "+ e.toString());
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify api landing page for volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyAPIAccountCreationPage() throws Exception {
		APP_LOGGER.startFunction("verifyAPIAccountCreationPage");
		int timeOutinSeconds = 10;
		String tempRetval=Globals.KEYWORD_FAIL;
		String expectdText;
		String stepName = "To verify the API Account Validation";

		sc.clickWhenElementIsClickable("apiVolunteerDetails_continue_btn", timeOutinSeconds);

		expectdText = "Please review the information that you entered: Email Address, Month, Day, Year, ZIP or Postal Code";
		tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
		}else{	
			sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
		}


		sc.setValueJsChange("apiVolunteerDetails_emailID_txt", Globals.fromEmailID);
		sc.clickWhenElementIsClickable("apiVolunteerDetails_continue_btn", timeOutinSeconds);

		expectdText = "Please review the information that you entered: Month, Day, Year, ZIP or Postal Code";
		tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
		}else{	
			sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
		}

		enterDOBInVolunteerPortal();
		sc.clickWhenElementIsClickable("apiVolunteerDetails_continue_btn", timeOutinSeconds);

		expectdText = "Please review the information that you entered: ZIP or Postal Code";
		tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
		}else{	
			sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
		}

		String zipcode = "99999";
		tempRetval				=	sc.setValueJsChange("apiVolunteerDetails_zipCode_txt",zipcode );
		sc.clickWhenElementIsClickable("apiVolunteerDetails_continue_btn", timeOutinSeconds);

		expectdText = "Please review the information that you entered. It does not match what we received.";
		tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
		}else{	
			sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Excpected - "+expectdText, 1);
		}

	}

	/**************************************************************************************************
	 * Method to create a new account for ap invited volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String apiAccountCreation() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;
		String tempRetval=Globals.KEYWORD_FAIL;
		String username = null;
		String zipCode=null;
		String dob=null;


		try{
			tempRetval=sc.waitforElementToDisplay("apiVolunteerDetails_emailID_txt",timeOutinSeconds);

			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				
				String apiShare=sc.testSuiteXLS.getCellData_fromTestData("API_Skip_Verify") ;
				 int count=0;
				 if(apiShare.equalsIgnoreCase("Yes"))
				{
					 tempRetval=sc.setValueJsChange("apiVolunteerDetails_emailID_txt", Globals.fromEmailID);

						dob = enterDOBInVolunteerPortal();
						zipCode	=	"10001";
						tempRetval = sc.setValueJsChange("apiVolunteerDetails_zipCode_txt",zipCode );
						sc.addParamVal_InEmail("VolunteerZipcode", zipCode);
						sc.addParamVal_InEmail("VolunteerDOB", dob);
					 WebElement element = sc.createWebElement("apiVolunteerDetails_continue_btn");
					 WebElement elementSkip = sc.createWebElement("apiVolunteerDetails_skip_btn");
					 WebElement msg= sc.createWebElement("apiVolunteersDetails_validation_msg");
					 while((element.getAttribute("value").contains("Continue")) && element.getAttribute("style").isEmpty())
					 {
						 sc.clickWhenElementIsClickable("apiVolunteerDetails_continue_btn", timeOutinSeconds);
						 }
					 String expectedtxt= elementSkip.getAttribute("value");
					 String expectedval= sc.getWebElementText(msg);
					 String actualval= "Skip Verify And Proceed to Registration";
					 if(expectedval.equalsIgnoreCase(actualval) && expectedtxt.equalsIgnoreCase(actualval)) 
					 {
						 sc.clickWhenElementIsClickable("apiVolunteerDetails_skip_btn", timeOutinSeconds);
						 retval = Globals.KEYWORD_PASS;
					 }
					 else
					 {
						 sc.STAF_ReportEvent("Fail", "API Verify ByPass after Failed attempt", "The skip and verify option not appearing.", 1);
							retval = Globals.KEYWORD_FAIL; 
					 } 
				 }
				 
				 else
				 {

				String fieldLevelValidation ;
				fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");

				if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
					fieldLevelValidation = "No";
				}

				if(fieldLevelValidation.equalsIgnoreCase("Yes")){
					verifyAPIAccountCreationPage();
				}

				//api first page data entry
				tempRetval=sc.setValueJsChange("apiVolunteerDetails_emailID_txt", Globals.fromEmailID);

				dob = enterDOBInVolunteerPortal();

				zipCode			=	sc.testSuiteXLS.getCellData_fromTestData("AddressZipCode");
				tempRetval		=	sc.setValueJsChange("apiVolunteerDetails_zipCode_txt",zipCode );
				
				sc.addParamVal_InEmail("VolunteerZipcode", zipCode);
				sc.addParamVal_InEmail("VolunteerDOB", dob);
				
				sc.clickWhenElementIsClickable("apiVolunteerDetails_continue_btn", timeOutinSeconds);
				 }

				//volunteer account creation
				tempRetval = Globals.KEYWORD_FAIL;
				tempRetval = sc.waitforElementToDisplay("volunteerAccCreation_username_txt", timeOutinSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Volunteer Account Creation", "Page has not loaded", 1);
					return Globals.KEYWORD_FAIL;
				}

				if(sc.testSuiteXLS.getCellData_fromTestData("VolunteerUsername_runtimeUpdateFlag").equalsIgnoreCase("Yes")){
					int length 	= 9;
					username 	= "vol" + sc.runtimeGeneratedStringValue(length);
					Globals.testSuiteXLS.setCellData_inTestData("VolunteerUsername", username);
					log.debug(" Method-volunteerAccountCreation | Volunteer Username Runtime generated and store.value- "+username);
				}else{
					username 	= sc.testSuiteXLS.getCellData_fromTestData("VolunteerUsername");

				}

				tempRetval		=	sc.setValueJsChange("volunteerAccCreation_username_txt",username );
				String password =	sc.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
				tempRetval		=	sc.setValueJsChange("volunteerAccCreation_password_txt",password );
				tempRetval		=	sc.setValueJsChange("volunteerAccCreation_confirmPassword_txt", password);
				tempRetval		=	sc.checkCheckBox("volunteerAccCreation_consent_chk");
				if (!tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					tempRetval		=	sc.checkCheckBox("volunteerAccCreation_consent_chk");
				}

				tempRetval 		=	sc.waitforElementToDisplay("apiVolunteerDetails_createAccount_btn",timeOutinSeconds);

				sc.STAF_ReportEvent("Pass", "Volunteer Account Creation", "Volunteer Username used for account creation - "+username, 1);

				sc.clickWhenElementIsClickable("apiVolunteerDetails_createAccount_btn", timeOutinSeconds);

				retval=Globals.KEYWORD_PASS;

			}else{
				sc.STAF_ReportEvent("Fail", "Volunteer Account Creation", "Volunteer account creation page is not loaded", 1);
				log.error(" Method-"+Globals.Component+"| Volunteer account  page has not loaded ");
			}
		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error(" Method-"+Globals.Component+" | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to login to exsting account for API invited volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	 public static String apiExistingAccountLogin() throws Exception{
		 Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
			APP_LOGGER.startFunction(Globals.Component);
			String retval=Globals.KEYWORD_FAIL;
			int timeOutinSeconds = 20;
			String username= null,fname,accName;

			try{
				Thread.sleep(6000);
				String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
				sc.launchURL(Globals.BrowserType, url);
				fname=sc.testSuiteXLS.getCellData_fromTestData("volunteerFName");
				accName=sc.testSuiteXLS.getCellData_fromTestData("AccountName");
				String resetPwdFlag=sc.testSuiteXLS.getCellData_fromTestData("ExistingAPIUser_Reset_change_pwd");
				String lbaelTxt= Globals.driver.findElement(By.xpath("//*[@id='LoginCandidate']/div[1]")).getText(); 
				String exptTxt="Welcome back "+fname+"!\n\nYou already have an active Verified Volunteers account. In order to continue with your background check order for "+accName+", please log in using the username and password you chose when you registered with Verified Volunteers.";
				
				if(!(lbaelTxt.equalsIgnoreCase(exptTxt))){
					retval=Globals.KEYWORD_FAIL;
					sc.STAF_ReportEvent("Fail", "API existing Account Login", "API existing Account Login page text is not displayed as per expected client", 1);
					return retval;   			
				}else{
					sc.STAF_ReportEvent("Pass", "API existing Account Login", "API existing Account Login page text is displayed as per expected client", 1);
				}
				
				if(resetPwdFlag.equalsIgnoreCase("Reset pwd")){
					sc.clickWhenElementIsClickable("apiexisting_login_forgotPassword_link", timeOutinSeconds);
					retval=ClientFacingApp.resetVolunteerPassword();
					if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail", "API existing Account Login", "Reset Password Flow not working properly", 1);
						return Globals.KEYWORD_FAIL;
					}else{
						Globals.driver.findElement(By.xpath("//input[@value='Start Your Background Check']")).click();
						 //sc.clickWhenElementIsClickable("login_StartyourBackgroundCheck_btn", timeOutinSeconds);
					}
					
				}else if(resetPwdFlag.equalsIgnoreCase("Change pwd")){
					retval=ClientFacingApp.resetVolunteerPassword();
					if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail", "API existing Account Login", "Reset Password Flow not working properly", 1);
						return Globals.KEYWORD_FAIL;
					}else{
						Globals.driver.findElement(By.xpath("//input[@value='Start Your Background Check']")).click();
						 //sc.clickWhenElementIsClickable("login_StartyourBackgroundCheck_btn", timeOutinSeconds);
					}
					
				}else{
					username=sc.testSuiteXLS.getCellData_fromTestData("VolunteerUsername");
					retval		=	sc.setValueJsChange("login_volunteerUsername_txt",username );
					String password =	sc.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
					retval		=	sc.setValueJsChange("login_volunteerPwd_txt",password );
					sc.clickWhenElementIsClickable("apiVolunteers_LogIn_btn",timeOutinSeconds);
			
					retval=Globals.KEYWORD_PASS;
				}
				retval = sc.waitforElementToDisplay("volunteerHomepage_myProfile_link",timeOutinSeconds);
				if (retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "API Reaccount Creation", "My Profile button is visible", 1);
				}
				else{
					sc.STAF_ReportEvent("Fail", "API Reaccount Creation", "My Profile button is not visible", 1);
				}

			}catch(Exception e){
				retval=Globals.KEYWORD_FAIL;
				log.error(" Method-"+Globals.Component+" | Exception - "+ e.toString());
				throw e;
			}
			return retval;
	}
	 
	 /**************************************************************************************************
		 * Method to validate share page for type zero shre for volunteer
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @throws Exception
		 ***************************************************************************************************/
		 public static String shareTypeZero() throws Exception{
			Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
			APP_LOGGER.startFunction(Globals.Component);
			String retval=Globals.KEYWORD_FAIL;
			String accName,npnOrderId="";
			Pattern p = Pattern.compile("([0-9])");
			Matcher m = null;

			try{
				accName=sc.testSuiteXLS.getCellData_fromTestData("AccountName");
				
				String lbaelTxt= driver.findElement(By.xpath("//*[@id='initial']/div[1]/div/h2")).getText(); 
			    String exptTxt="A Background Check has been found that matches the request from the Organization";
				if(!(lbaelTxt.equalsIgnoreCase(exptTxt))){
					retval=Globals.KEYWORD_FAIL;
				    sc.STAF_ReportEvent("Fail", "Sharing Type 0", "Share order page is not getting displayed to volunteer", 1);
					return retval;   			
				}else{
					sc.STAF_ReportEvent("Pass", "Sharing Type 0", "Share order page is getting displayed to volunteer", 1);
					
				}
				lbaelTxt= driver.findElement(By.xpath(".//*[@id='initial']/div[2]/div[1]/table/tbody/tr/td[2]/label")).getText(); 
			    exptTxt="By Checking this box, I consent to share my Verified Volunteers background check with "+accName+" for the purpose of Volunteerism/Non-employee position.";
			    if(!(lbaelTxt.trim().equalsIgnoreCase(exptTxt))){
					retval=Globals.KEYWORD_FAIL;
				    sc.STAF_ReportEvent("Fail", "Sharing Type 0", "Sharing consent checkbox label text is not matching as expected", 1);
					return retval;   			
				}
			    
			    retval		=	sc.isEnabled("Sharing_type0_sharingbg_btn");
			    if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Fail","Sharing Type 0", "Share Existing Background Check Button should be disabled when consent checkbox is uncheck ", 1);
					return retval;
			    }
			    
			    sc.checkCheckBox("Sharing_type0_consent_chk");
//				retval		=	sc.isEnabled("Sharing_type0_sharingbg_btn");
//				if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
//					sc.STAF_ReportEvent("Fail","Sharing Type 0-", "Share Existing Background Check Button should be Enabled when consent checkbox is checked ", 1);
//					return retval;
//			    }
				sc.isDisplayed_withCustomReporting("agreements_termsOfUse_link", "Terms Of Use");
				driver.findElement(By.xpath("//*[@id='initial']//button[@class='btn btn-primary']")).click();
				//sc.clickWhenElementIsClickable("Sharing_type0_sharingbg_btn",timeOutinSeconds);
			
				Thread.sleep(10000);
				//*[@id='result']/div/h3
				lbaelTxt= driver.findElement(By.xpath("//*[@id='result']/div/h3")).getText().trim(); 
						   
			    if(!(lbaelTxt.trim().contains("has been successfully shared with "+accName))){
					retval=Globals.KEYWORD_FAIL;
				    sc.STAF_ReportEvent("Fail", "Sharing Type 0", "Unable to get sharing successfull message to volunteer", 1);
					return retval;   			
				}else{
					sc.STAF_ReportEvent("Pass", "Sharing Type 0", "BG report is shared to account "+accName+" successfully", 1);
				}
                
			    m = p.matcher(lbaelTxt);
				if(m.find()){
					//lbaelTxt=lbaelTxt.replace(")", ",");
					String[] orderID = lbaelTxt.split(Pattern.quote("Order"));
					String[] arrnpnOrderId = orderID[1].split(Pattern.quote(")"));
					npnOrderId=arrnpnOrderId[0].trim();
					
				}
				
				if(npnOrderId.isEmpty()){
					log.error(" Order ID is not generated OR unable to capture the order id");
					sc.STAF_ReportEvent("Fail", "Sharing Type 0", "OrderID not generated", 1);

				}else{
					String orderedDate = sc.getTodaysDate();
					Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
					Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderId);
					String swestOrderId=sc.testSuiteXLS.getCellData_fromTestData("SWestOrderID");
					sc.addParamVal_InEmail("SWestOrderID", swestOrderId);
					sc.addParamVal_InEmail("NPNOrderID", npnOrderId);
					sc.STAF_ReportEvent("Pass", "Sharing Type 0", "OrderID generated ="+npnOrderId +"-"+swestOrderId , 1);

					log.info(" NPN Order ID Generated - "+ npnOrderId.trim());
					log.info(" S.West Order ID Generated - "+ swestOrderId.trim());
					
					String dob = sc.getPriorDate("dd-MMM-yyyy", -22);
					Globals.testSuiteXLS.setCellData_inTestData("VolunteerDOB",dob);
				}
				
				retval=Globals.KEYWORD_PASS;
			

			}catch(Exception e){
				retval=Globals.KEYWORD_FAIL;
				log.error(" Method-"+Globals.Component+" | Exception - "+ e.toString());
				retval=Globals.KEYWORD_FAIL;
				throw e;
			}
			return retval;
		}

	 
	
	/**************************************************************************************************
	 * Method to verify data in Ordering STep 1 for API Ordering
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyAPIOrderStep1() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;
		String tempRetval=Globals.KEYWORD_FAIL;

		String suffix=null;

		try{
			tempRetval=sc.waitforElementToDisplay("invitationStep1_fName_txt", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
                String apiShare=sc.testSuiteXLS.getCellData_fromTestData("SharingType"); 
                String apiSkip=sc.testSuiteXLS.getCellData_fromTestData("API_Skip_Verify");
                if(!(apiShare.equalsIgnoreCase("API Share"))){
				//verify custom messaging Step1
                verifyCustomMessagingOrderSTEP1();
                }
				XMLUtils inputXML = new XMLUtils(Globals.currentAPIOrderRequestPath);
				String expectedValue;

				expectedValue = inputXML.getXMLNodeValByXPath("//ScreeningSubjectName/ScreeningPersonName/PersonName[@nameTypeCode='CurrentName']/GivenName");
				tempRetval = verifyXMLValWithUIFieldVal(expectedValue,"invitationStep1_fName_txt","VolunteerFirstName");

				expectedValue = inputXML.getXMLNodeValByXPath("//ScreeningSubjectName/ScreeningPersonName/PersonName[@nameTypeCode='CurrentName']/FamilyName");
				tempRetval = verifyXMLValWithUIFieldVal(expectedValue,"invitationStep1_lName_txt","VolunteerLastName");

				expectedValue = inputXML.getXMLNodeValByXPath("//ScreeningSubjectName/ScreeningPersonName/PersonName[@nameTypeCode='CurrentName']/MiddleName");
				tempRetval = verifyXMLValWithUIFieldVal(expectedValue,"invitationStep1_midName_txt","VolunteerMidName");

				suffix =Globals.testSuiteXLS.getCellData_fromTestData("VolunterrSuffix");
				if(suffix != null ){
					tempRetval = sc.setValueJsChange("invitationStep1_suffix_txt",suffix);
				}
				
				if(apiSkip.equalsIgnoreCase("Yes"))
				{			
					Select select_Month = new Select(sc.createWebElement("invitationStep1_dobMonth_dd"));			
					String dobMonth =  select_Month.getFirstSelectedOption().getText();			
					Select select_dd = new Select(sc.createWebElement("invitationStep1_dobDay_dd"));			
					String dobDay =  select_dd.getFirstSelectedOption().getText();			
					Select select_yy = new Select(sc.createWebElement("invitationStep1_dobYear_dd"));			
					String dobYear =  select_yy.getFirstSelectedOption().getText();			
					if(dobMonth.contains("Month") && dobDay.contains("Day") && dobYear.contains("Year"))			
					{			
					select_Month.selectByValue("8");			
					select_dd.selectByValue("9");			
					select_yy.selectByValue("1995");			
					retval= Globals.KEYWORD_PASS;			
					}			
					else			
					{			
					sc.STAF_ReportEvent("Fail", "API Verify ByPass after Failed attempt", "Date of birth Field is not blank.", 1);			
					retval= Globals.KEYWORD_FAIL;			
					}			
					}			
					else			
					{
				String dob = inputXML.getXMLNodeValByTagName("FormattedDateTime"); //MM/dd/yyyy

				String monthNumber = dob.split("/")[0]; 

				Pattern pattern = Pattern.compile("[0].");//. represents single character  
				Matcher m=null;
				boolean b = false;
				m = pattern.matcher(monthNumber); 
				b = m.matches();
				if(b){
					monthNumber =	monthNumber.replace("0","");
				}

				String monthName = sc.convertIntToFullMonthName(monthNumber);

				tempRetval = verifyXMLValWithUIFieldVal(monthNumber,"invitationStep1_dobMonth_dd","DOB-Month");

				//Anand-3/1- application has changed the format of Date dropdown from 01 to 1

				String dobDate = dob.split("/")[1];

				m = pattern.matcher(dobDate);  
				b= false;
				b = m.matches();

				if(b){
					dobDate =	dobDate.replace("0","");
				}

				tempRetval = verifyXMLValWithUIFieldVal(dobDate,"invitationStep1_dobDay_dd","DOB-Day");
				tempRetval = verifyXMLValWithUIFieldVal(dob.split("/")[2],"invitationStep1_dobYear_dd","DOB-Year");

				String reportFormatDOB =dobDate+"-"+ monthName.substring(0, 3)+"-"+dob.split("/")[2];
				Globals.testSuiteXLS.setCellData_inTestData("VolunteerDOB",reportFormatDOB); //dd-MMM-yyyy
				}
				
				String ssn =inputXML.getXMLNodeValByTagName("PersonLegalID");

				if(ssn != null && !ssn.isEmpty() && (apiSkip.equalsIgnoreCase(""))){
					tempRetval = verifyXMLValWithUIFieldVal(ssn.substring(0, 3),"invitationStep1_ssn1_txt","SSN-1");
					tempRetval = verifyXMLValWithUIFieldVal(ssn.substring(3, 5),"invitationStep1_ssn2_txt","SSN-1");
					tempRetval = verifyXMLValWithUIFieldVal(ssn.substring(5, 9),"invitationStep1_ssn3_txt","SSN-1");
				}
				
				 else if((ssn != null && !ssn.isEmpty()) && (apiSkip.equalsIgnoreCase("Yes")))			
					{			
					  WebElement ssn1_txt = sc.createWebElement("invitationStep1_ssn1_txt");			
					  WebElement ssn2_txt = sc.createWebElement("invitationStep1_ssn2_txt");			
					  WebElement ssn3_txt = sc.createWebElement("invitationStep1_ssn3_txt");			
					  WebElement[] ssntxt= {ssn1_txt,ssn2_txt,ssn3_txt};			
					 for(WebElement elm: ssntxt)			
					 {			
					   if(elm.getAttribute("value").isEmpty())			
					  {			
					    retval= Globals.KEYWORD_PASS;			
					    }			
					  else			
					  {			
					   sc.STAF_ReportEvent("Fail", "API Verify ByPass after Failed attempt", "SSN Field is not blank.", 1);			
					   retval= Globals.KEYWORD_FAIL;			
					   }                			
					      }			
				       ssn1_txt.sendKeys(ssn.substring(0, 3));			
					   ssn2_txt.sendKeys(ssn.substring(3, 5));			
					   ssn3_txt.sendKeys(ssn.substring(5, 9));			
					   }
				
				else{
					sc.STAF_ReportEvent("Pass", "Step 1", "SSN Not Entered for ordering", 1);
					tempRetval = sc.checkCheckBox("invitationStep1_noSSN_chk");
				}

				String genderVal =inputXML.getXMLNodeValByTagName("GenderCode");
				if (genderVal.equalsIgnoreCase("Male")){
					expectedValue = "2";
				}else {
					expectedValue = "1";
				}
				tempRetval = verifyXMLValWithUIFieldVal(expectedValue,"invitationStep1_gender_dd","GenderCode");

				String phoneNo="9012121234";
				
				if((apiSkip.equalsIgnoreCase("Yes")))
				{
					WebElement phone_Field = sc.createWebElement("invitationStep1_phone_txt");
					if(phone_Field.getAttribute("value").isEmpty())
					{
						tempRetval = sc.setValueJsChange("invitationStep1_phone_txt",phoneNo);
						retval=  Globals.KEYWORD_PASS;
						}
					else
					{
						sc.STAF_ReportEvent("Fail", "API Verify ByPass after Failed attempt", "Phone Field is not blank.", 1);
						retval=  Globals.KEYWORD_FAIL;
						}
					}
				else
				{
					tempRetval = sc.setValueJsChange("invitationStep1_phone_txt",phoneNo );
					}

				String emailID =inputXML.getXMLNodeValByTagName("oa:URI");
				tempRetval = verifyXMLValWithUIFieldVal(emailID,"invitationStep1_emailAddress_txt","EmailAddress");

				//Custom Field Handling
				verifyAndSetCustomFields();

				//Alias Data Entry
				String sNoOfAlias = sc.testSuiteXLS.getCellData_fromTestData("NoOfAlias");
				int intNoOfAlias = Integer.parseInt(sNoOfAlias);

				//fetch no of alias provided in input xml
				verifyAliasSectionData_API(inputXML);

				tempRetval=sc.waitforElementToDisplay("invitationStep1_removeAlias_btn",3);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.clickWhenElementIsClickable("invitationStep1_removeAlias_btn", 3);
				}

				populateAliasSectionData(intNoOfAlias);

				//alacarte section verification would only appear if they are part of ordering
				alacarteSectionVerification();

				sc.scrollIntoView("invitationStep1_saveAndContinue_btn");
				sc.STAF_ReportEvent("Pass", "Step 1", "Data entered in Step 1", 1);
				sc.clickWhenElementIsClickable("invitationStep1_saveAndContinue_btn", timeOutinSeconds);
				retval=Globals.KEYWORD_PASS;
				}
			else
			{
				sc.STAF_ReportEvent("Fail", "Step 1", "Step 1 page has not been loaded" , 1);
				}


		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-invitationOrderStep1 | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify XML based data for a node alongwith its corresponding object in UI by creating a webelement
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @param ExpectedValue - String
	 * @param elementName - String - Object name used in OR
	 * @param FieldName - String - xml node name
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyXMLValWithUIFieldVal(String ExpectedValue, String elementName,String FieldName) throws Exception {
		String retval	=	Globals.KEYWORD_FAIL;
		WebElement element	= 	sc.createWebElement(elementName);
		retval = verifyXMLValWithUIFieldVal(ExpectedValue, element,FieldName);
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify XML based data for a node alongwith its corresponding object in UI
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @param ExpectedValue - String
	 * @param elementName - String - Object name used in OR
	 * @param FieldName - String - xml node name
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyXMLValWithUIFieldVal(String ExpectedValue, WebElement element,String FieldName) throws Exception {
		String actualValue;
		String retval	=	Globals.KEYWORD_FAIL;
		actualValue	= 	element.getAttribute("value");
		ExpectedValue = ExpectedValue.trim();
		if(ExpectedValue.equals(actualValue)){
			sc.STAF_ReportEvent("Pass", "UI Field Validation with XML value for Node-"+FieldName, "Value is as Expected.Exp Val-"+ExpectedValue, 1);
			retval =  Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "UI Field Validation with XML value for Node-"+FieldName, "Value is NOT as Expected.Exp Val-"+ExpectedValue+" Actual Val-"+actualValue, 1);
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to populate alias section data based on number of aliases
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @param int - no of aliases that needs to be added to the order 
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void populateAliasSectionData(int iNoOfAlias)throws Exception {
		//Alias names section would only appear if L3 is part of ordering

		String tempRetval =  Globals.KEYWORD_FAIL;
		int timeOutinSeconds= 20;

		tempRetval = isProductPartOfOrdering("L3");
		if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){


			String aliasFNameLocator;
			String aliasMidNameLocator;
			String aliasLNameLocator;
			String aliasFromDateLocator;
			String aliasToDateLocator;
			String aliasFName;
			String aliasLName;
			String aliasMidName ;
			String fromDate;
			String toDate;


			tempRetval = sc.waitforElementToDisplay("invitationStep1_addAlias_btn", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){



				sc.STAF_ReportEvent("Pass", "Step 1", "Alias Section is visible", 0);

				if (iNoOfAlias <= 0 ){
					sc.STAF_ReportEvent("Pass", "Step 1", "No Alias to be added", 0);
					log.info(" NO Alias to be added during Client Ordering");

				}else {

					sc.STAF_ReportEvent("Pass", "Step 1", "Number of Aliases to be added - "+iNoOfAlias, 0);
					log.info(iNoOfAlias + "-  Alias to be added during Ordering");
					int i=1;

					String sNoOfAlias = sc.testSuiteXLS.getCellData_fromTestData("NoOfAlias");
					int actNoOfAlias = Integer.parseInt(sNoOfAlias);

					if(iNoOfAlias > actNoOfAlias){
						i=iNoOfAlias-actNoOfAlias+1;
						//TODO Need to verify alias name fields for Api orders
					}

					for(;i<=iNoOfAlias;i=i+1){

						sc.clickWhenElementIsClickable("invitationStep1_addAlias_btn", 30);

						aliasFNameLocator = "//div[@data-bind='foreach: AliasList']/ul[" + i + "]/li[1]/input[@type='text' and @name='FirstNameAlias']";
						aliasMidNameLocator ="//div[@data-bind='foreach: AliasList']/ul[" + i + "]/li[2]/input[@type='text' and @name='MiddleNameAlias']";
						aliasLNameLocator ="//div[@data-bind='foreach: AliasList']/ul[" + i + "]/li[3]/input[@type='text' and @name='LastNameAlias']";


						aliasFromDateLocator = "//div[@data-bind='foreach: AliasList']/ul[" + i + "]/li[4]/input[@type='text' and @name='FromAlias']"; 

						aliasToDateLocator =  "//div[@data-bind='foreach: AliasList']/ul[" + i + "]/li[5]/input[@type='text' and @name='ToAlias']";


						//The second  alias to be added should be Volunteer details as this scenario was requested to be added-2/4
						if(i != 2){
							//							aliasFName = RandomStringUtils.randomAlphabetic(lenthOfDynamicString) + specialChara ;
							//							aliasLName = RandomStringUtils.randomAlphabetic(lenthOfDynamicString) + specialChara;
							//							aliasMidName = RandomStringUtils.randomAlphabetic(lenthOfDynamicString)+ specialChara;
							aliasFName = Globals.Volunteer_AliasName;
							aliasLName = Globals.Volunteer_AliasName;
							aliasMidName = Globals.Volunteer_AliasName;

						}else{

							aliasFName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
							aliasLName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
							aliasMidName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");
						}



						fromDate = sc.getPriorDate("MM/yyyy", -6);
						toDate =  sc.getPriorDate("MM/yyyy", -1);

						driver.findElement(By.xpath(aliasFNameLocator)).sendKeys(aliasFName);
						driver.findElement(By.xpath(aliasMidNameLocator)).sendKeys(aliasMidName);
						driver.findElement(By.xpath(aliasLNameLocator)).sendKeys(aliasLName);

						driver.findElement(By.xpath(aliasFromDateLocator)).sendKeys(fromDate);
						tempRetval = sc.waitforElementToDisplay("invitationStep1_datePickerDone_btn", 3);// this is done to remove the calendar from view else add alias button wont get clicked
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							try{
								sc.clickWhenElementIsClickable("invitationStep1_datePickerDone_btn", 2);	
							}catch(Exception e){
								log.error("Exception generated while  clicking on Date picker Done button.Exception-"+e.toString());
							}

						}

						driver.findElement(By.xpath(aliasToDateLocator)).sendKeys(toDate);
						driver.findElement(By.xpath(aliasToDateLocator)).sendKeys(Keys.TAB);//Shifting Focus from aliasToDate
						tempRetval = sc.waitforElementToDisplay("invitationStep1_datePickerDone_btn", 3);
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.clickWhenElementIsClickable("invitationStep1_datePickerDone_btn", 2);
						}


						sc.STAF_ReportEvent("Pass", "Step 1", "Alias Name Added - "+aliasFName + " "+aliasLName, 1);
						log.info("Alias Name Added - "+aliasFName + " "+aliasLName);
					}
					sc.STAF_ReportEvent("Pass", "Step 1", "Total Alias Added - " + iNoOfAlias, 1);
				}

			}else{
				sc.STAF_ReportEvent("Fail", "Step 1", "Alias Section is NOT visible", 1);

			}

		}

	}

	/**************************************************************************************************
	 * Method to verify Ordering Step 1 Alacarte section.
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void alacarteSectionVerification(){

		String tempRetval = Globals.KEYWORD_FAIL;
		String positionProducts=null;
		String[] products =null;

		positionProducts=Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts");
		products = positionProducts.split("-"); //+ delimiter cannot be used as Exception would be thrown-java.util.regex.PatternSyntaxException: Dangling meta character '+' near index 0
		int noOfProducts = products.length;

		//alacarte section should not appear when only L1/L2/L3 is selected
		if (noOfProducts == 1 && ( products[0].equalsIgnoreCase("L1") || products[0].equalsIgnoreCase("L2") || products[0].equalsIgnoreCase("L3"))){
			log.info(" Alacarte section should not be present");

			tempRetval=Globals.KEYWORD_FAIL;
			tempRetval = sc.isDisplayed("invitationStep1_mvr_chk");
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Fail", "Step 1", "Alacarte section is present when only "+products[0]+" is selcted for Ordering" , 1);
				log.error(" Alacarte section IS PRESENT.");
			}

		}else {
			VV_Products expProduct;
			noOfProducts = noOfProducts -1 ;// removing Level based product
			for (String p : products){
				p=p.trim().toUpperCase();

				if (p.contains("ABUSE")){
					expProduct =  VV_Products.valueOf("ABUSE");
				}else{
					expProduct =  VV_Products.valueOf(p);
				}

				//verify selection of checkbox also
				log.debug("Alacarte section validation-STEP1 | Products - "+p);
				if(expProduct == VV_Products.CREDIT){
					sc.isSelected_withCustomReporting("invitationStep1_credit_chk","Alacarte-Credit");

				}else if(expProduct == VV_Products.MVR){
					sc.isSelected_withCustomReporting("invitationStep1_mvr_chk","Alacarte-MVR");

				}else if(expProduct == VV_Products.ABUSE){
					//need to check for ABUSE(PA)
					sc.isSelected_withCustomReporting("invitationStep1_abuse_chk","Alacarte-ABUSE");

				}else if(expProduct == VV_Products.OIG){
					sc.isSelected_withCustomReporting("invitationStep1_oig_chk","Alacarte-OIG");
				}else if(expProduct == VV_Products.REF){
					sc.isSelected_withCustomReporting("invitationStep1_reference_chk","Alacarte-REFERENCE");
				}else if(expProduct == VV_Products.SSNPROF){
					sc.isSelected_withCustomReporting("invitationStep1_ssnProfile_chk","Alacarte-SSN PROFILE");
				}else if(expProduct == VV_Products.CBSV){
					sc.isSelected_withCustomReporting("invitationStep1_cbsv_chk","Alacarte-CBSV");

				}else if(expProduct == VV_Products.ID){
					//					sc.isSelected_withCustomReporting("invitationStep1_id_chk","Alacarte-ID");
					//					ID confirm product cannot be verified in alacarte as its only supported in mobile device and hence OOS for automation
				}else if(expProduct == VV_Products.COUNTYCIVIL){
					sc.isSelected_withCustomReporting("invitationStep1_countycivil_chk","Alacarte-County Civil");
				}else if(expProduct == VV_Products.FEDCRIM){
					sc.isSelected_withCustomReporting("invitationStep1_fedcrim_chk","Alacarte-Federal Crimianl");
				}else if(expProduct == VV_Products.FEDCIVIL){
					sc.isSelected_withCustomReporting("invitationStep1_fedcivil_chk","Alacarte-Federal Civil");
				}else if(expProduct == VV_Products.LS){
					sc.isSelected_withCustomReporting("invitationStep1_ls_chk","Alacarte-Locator Select");
				}else if(expProduct == VV_Products.GLOBEX){
					sc.isSelected_withCustomReporting("invitationStep1_globex_chk","Alacarte-Locator Select");
				}
			}
		}
	}

	/**************************************************************************************************
	 * Method to verify Ordering Step2 for API Ordering
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyAPIOrderStep2() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;
		String tempRetval=Globals.KEYWORD_FAIL;

		try{
			tempRetval=sc.waitforElementToDisplay("invitationStep2_addressLine1_txt", timeOutinSeconds);
			String apiSkip=sc.testSuiteXLS.getCellData_fromTestData("API_Skip_Verify");
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS) && apiSkip.equalsIgnoreCase("")){

				//MVR section would only be displayed if MVR product is part of Ordering
				populateMVRData();

				XMLUtils inputXML = new XMLUtils(Globals.currentAPIOrderRequestPath);
				//Current address
				String addressLine1 =inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='true']/LineOne");
				String addressLine2 =inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='true']/LineTwo");
				String city=inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='true']/CityName");
				String state=inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='true']/CountrySubDivisionCode");
				String zipcode=inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='true']/PostalCode");


				tempRetval = verifyXMLValWithUIFieldVal(addressLine1,"invitationStep2_addressLine1_txt","AddressLine1");
				tempRetval = verifyXMLValWithUIFieldVal(addressLine2,"invitationStep2_addressLine2_txt","AddressLine1");
				tempRetval = verifyXMLValWithUIFieldVal(city,"invitationStep2_city_txt","City");
				tempRetval = verifyXMLValWithUIFieldVal(zipcode,"invitationStep2_zipCode_txt","ZipCode");
				tempRetval = verifyXMLValWithUIFieldVal(state,"invitationStep2_state_dd","State");

				//Previous adddress validation
				String addPrevAddress = sc.testSuiteXLS.getCellData_fromTestData("AddPrevAddr");
				if(addPrevAddress.equalsIgnoreCase("yes")){
					String PrevaddressLine1 =inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='false']/LineOne");
					String PrevaddressLine2 =inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='false']/LineTwo");
					String Prevcity=inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='false']/CityName");
					String Prevstate=inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='false']/CountrySubDivisionCode");
					String Prevzipcode=inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='false']/PostalCode");

					tempRetval = verifyXMLValWithUIFieldVal(PrevaddressLine1,"invitationStep2_PrevaddressLine1_txt","Previous AddressLine1");
					tempRetval = verifyXMLValWithUIFieldVal(PrevaddressLine2,"invitationStep2_PrevaddressLine2_txt","Previous AddressLine1");
					tempRetval = verifyXMLValWithUIFieldVal(Prevcity,"invitationStep2_Prevcity_txt","Previous City");
					tempRetval = verifyXMLValWithUIFieldVal(Prevzipcode,"invitationStep2_PrevzipCode_txt","Previous ZipCode");
					tempRetval = verifyXMLValWithUIFieldVal(Prevstate,"invitationStep2_Prevstate_dd","Previous State");
				}else{
					tempRetval = sc.waitforElementToDisplay("invitationStep2_removePrevAddress_btn", 2);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						sc.clickWhenElementIsClickable("invitationStep2_removePrevAddress_btn", timeOutinSeconds);
					}
				}



				//Reference Section
				tempRetval =  Globals.KEYWORD_FAIL;
				tempRetval = isProductPartOfOrdering("REF");
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

					tempRetval = Globals.KEYWORD_FAIL;
					tempRetval = setData_InvitationOrder_Reference();
					if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						sc.STAF_ReportEvent("Pass", "Step 2", "Data entered for Reference section as its part of ordering" , 1);
					}else{
						sc.STAF_ReportEvent("Fail", "Step 2", "Unable to enter data for Reference section as its part of ordering" , 1);
					}


				}	

				sc.STAF_ReportEvent("Pass", "Step 2", "Data entered in Step 2", 1);

				sc.clickWhenElementIsClickable("apiVolunteers_step2Continue_btn", 20);

				retval = Globals.KEYWORD_PASS;
			}
			
			 //Api Skip Scenario		
			  else if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS) && apiSkip.equalsIgnoreCase("Yes"))			
			 {
			 //MVR section would only be displayed if MVR product is part of Ordering			
			 populateMVRData();	
			  XMLUtils inputXML = new XMLUtils(Globals.currentAPIOrderRequestPath);			
			//Current address
			 String addressLine1 =inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='true']/LineOne");			
			 String addressLine2 =inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='true']/LineTwo");			
			   String city=inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='true']/CityName");			
			   String state=inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='true']/CountrySubDivisionCode");			
			   String zipcode=inputXML.getXMLNodeValByXPath("//Address[@currentAddressIndicator='true']/PostalCode");			
			   WebElement invitationStep2_addressLine1_txt = sc.createWebElement("invitationStep2_addressLine1_txt");			
			   WebElement invitationStep2_addressLine2_txt = sc.createWebElement("invitationStep2_addressLine2_txt");			
			   WebElement invitationStep2_city_txt = sc.createWebElement("invitationStep2_city_txt");			
			   WebElement invitationStep2_zipCode_txt = sc.createWebElement("invitationStep2_zipCode_txt");			
			   WebElement[] fields= {invitationStep2_addressLine1_txt,invitationStep2_addressLine2_txt,invitationStep2_city_txt,invitationStep2_zipCode_txt};			
			  for(WebElement elm: fields)
			   {
			   if(elm.getAttribute("value").isEmpty())
			     {
			     retval= Globals.KEYWORD_PASS;
			      }
			    else			
			     {
			       sc.STAF_ReportEvent("Fail", "API Verify ByPass after Failed attempt", "Text field on Step2 are not blank.", 1);			
			    retval= Globals.KEYWORD_FAIL;	
			      }
			  }
			   invitationStep2_addressLine1_txt.sendKeys(addressLine1);	
			   invitationStep2_addressLine2_txt.sendKeys(addressLine2);			
			       invitationStep2_city_txt.sendKeys(city);
			   invitationStep2_zipCode_txt.sendKeys(zipcode);		
			       sc.selectValue_byVisibleText("invitationStep2_state_dd",Globals.testSuiteXLS.getCellData_fromTestData("AddressState") );			
			          enterDate(-5,"invitationStep2_fromDate_txt");   			
			  sc.clickWhenElementIsClickable("invitationStep2_addressLine1_txt", timeOutinSeconds);
			 //Reference Section
			   tempRetval =  Globals.KEYWORD_FAIL;
			   tempRetval = isProductPartOfOrdering("REF");
			      if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			   tempRetval = Globals.KEYWORD_FAIL;
			   tempRetval = setData_InvitationOrder_Reference();
			     if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			       sc.STAF_ReportEvent("Pass", "Step 2", "Data entered for Reference section as its part of ordering" , 1);			
			 }
			     else
			     {
				 sc.STAF_ReportEvent("Fail", "Step 2", "Unable to enter for Reference section as its part of ordering" , 1);			
			     throw new Exception("Unable to enter and validate Reference section");			
			       }
			    }     			
			     sc.STAF_ReportEvent("Pass", "Step 2", "Data entered in Step 2", 1);
		         sc.clickWhenElementIsClickable("apiVolunteers_step2Continue_btn", 20);
			      retval = Globals.KEYWORD_PASS;
	             }
			  else
			  {
				sc.STAF_ReportEvent("Fail", "Step 2", "Step 2 page has not been loaded" , 1);
				log.error("Method-invitationOrderStep2 | Page has not loaded");
			}

		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-invitationOrderStep2 | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to populate data for MVR in Ordering Step 2
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void populateMVRData() throws Exception {

		String tempRetval =  Globals.KEYWORD_FAIL;
		tempRetval = isProductPartOfOrdering("MVR");
		if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation.equalsIgnoreCase("Yes")){
				mvrAuthorization();
			}
			tempRetval = sc.selectValue_byVisibleText("invitationStep2_mvrState_dd",Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_State") );


			tempRetval = sc.setValueJsChange("invitationStep2_mvrFName_txt", Globals.Volunteer_DL_Name);
			tempRetval = sc.setValueJsChange("invitationStep2_mvrMidName_txt", Globals.Volunteer_DL_Name);
			tempRetval = sc.setValueJsChange("invitationStep2_mvrLName_txt", Globals.Volunteer_DL_Name);

			Thread.sleep(2000);
			String dlNumber = Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_License");
			//			driver.findElement(LocatorAccess.getLocator("invitationStep2_mvrDL_txt")).click();
			int timeOutinSeconds=10;
			sc.clickWhenElementIsClickable("invitationStep2_mvrDL_txt",timeOutinSeconds );
			tempRetval = sc.setValueJsChange("invitationStep2_mvrDL_txt", dlNumber);

			sc.STAF_ReportEvent("Pass", "Step 2", "Data entered for MVR section as MVR is part of ordering" , 1);
		}


	}
	
	/**************************************************************************************************
	 * Method to populate data for International address in Ordering Step 2
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static void populateInternationalAddData() throws Exception {

		String tempRetval =  Globals.KEYWORD_FAIL;
		String tempRetval1 = Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;
		String products =  Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts");
		String countrycode=Globals.testSuiteXLS.getCellData_fromTestData("CountryCode");
		try{
			if ((products.equalsIgnoreCase("L1")|| products.equalsIgnoreCase("L1-Globex") || products.equalsIgnoreCase("Globex")) && (countrycode.equalsIgnoreCase("CAN"))  ){

				String usaRadioLabel= driver.findElement(By.xpath("//*[@id='step2Form']/div[1]/div[2]/div[2]/label[1]")).getText();
				String InternationalRadioText=driver.findElement(By.xpath("//*[@id='step2Form']/div[1]/div[2]/div[2]/label[2]")).getText();
				if(!(usaRadioLabel.contains("USA") && InternationalRadioText.contains("International"))){
					sc.STAF_ReportEvent("Fail", "Step 2", "International address section is not displayed" , 1);
				}
				tempRetval=sc.isSelected("invitationStep2_USA_radio");
				tempRetval1=sc.isSelected("invitationStep2_International_radio");
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL) || tempRetval1.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Fail", "Step 2", "By default USA radio is not selected" , 1);
				}
	
				sc.checkCheckBox("invitationStep2_International_radio");
				sc.waitForPageLoad();
				sc.waitforElementToDisplay("invitationStep2_province_txt", 10);
				/*
				sc.clickWhenElementIsClickable("invitationStep2_saveAndContinue_btn", timeOutinSeconds);
				
				if(!sc.getWebText("questionnaire_page_warning_message").contains("Please verify the move-in date for your current address.")){
					sc.STAF_ReportEvent("Fail", "Step 2", "Validation fail no error message displayed when fields are blank" , 1);
				}
			 */
			
			}
		}catch(Exception e){
				log.error("Method-invitationOrderStep2 | Exception - "+ e.toString());
				throw e;
		}



	}

	/**************************************************************************************************
	 * Method to enter volunteer DOB
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String enterDOBInVolunteerPortal() throws Exception{
		
		String dob 				=	sc.getPriorDate("dd-MMM-yyyy", -22);
		String monMonthName 	=	dob.split("-")[1]; // 3 chara equivalent of Month like Jan
		String monthNameFull 	=	sc.convertToFullMonthName(monMonthName);

		sc.selectValue_byVisibleText("apiVolunteerDetails_dobMonth_dd",monthNameFull );
		Thread.sleep(1000);

		//need to convert date like 01 to 1 as dropdwon contains days like 1,2,3
		String dobDate = dob.split("-")[0];
		Pattern p = Pattern.compile("[0].");//. represents single character  
		Matcher m = p.matcher(dobDate);  
		boolean b = m.matches();

		if(b){
			dobDate =	dobDate.replace("0","");
		}

		sc.selectValue_byVisibleText("apiVolunteerDetails_dobDay_dd", dobDate);

		sc.selectValue_byVisibleText("apiVolunteerDetails_dobYear_dd",dob.split("-")[2]);
		return dob;

	}

	/**************************************************************************************************
	 * Method to verify pricing details in Ordering Step 1
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyPricingSTEP1() throws Exception{

		String actualCostSpanText="";
		String actualAmtClientpaysText="";
		String accountName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName").toLowerCase();
		String pricingType = sc.testSuiteXLS.getCellData_fromTestData("PricingName");


		if(pricingType.equalsIgnoreCase("Client Pays Fees Only") || pricingType.equalsIgnoreCase("Volunteer Pays All")){
			actualCostSpanText = driver.findElement(LocatorAccess.getLocator("invitationStep1_bgCheckTotal_td")).getText();
		}else{
			actualCostSpanText = driver.findElement(LocatorAccess.getLocator("invitationStep1_totalCost_td")).getText();
			actualAmtClientpaysText = driver.findElement(LocatorAccess.getLocator("invitationStep1_clientPays_td")).getText();
		}


		double expectedPrice = InhousePortal.fetchExpectedTotalPrice();
		double actualCost = Double.parseDouble(actualCostSpanText.split("\\$")[1]);
		double actualAmtClientpays;


		//validation of total price in Step 1 - Cost of background check
		if (expectedPrice == actualCost){
			sc.STAF_ReportEvent("Pass", "Step 1", "Cost of Background Check is as Expected.Val-"+expectedPrice, 1);
			
		}else{
			sc.STAF_ReportEvent("Fail", "Step 1", "Cost of Background Check is NOT as Expected.Exp-"+expectedPrice+ " Actual-"+actualCost, 1);
		}

		//Account Will Pay wont be present for client pays fess,volunteer pays all
		boolean clientPaysAmtMatch = false;

		if(pricingType.equalsIgnoreCase("Client Pays All") || pricingType.equalsIgnoreCase("Client Pays Half + Fees") || pricingType.equalsIgnoreCase("Client Pays  Half + No Fees") ){

			actualAmtClientpays= Double.parseDouble(actualAmtClientpaysText.split("\\$")[1]);

			if(pricingType.equalsIgnoreCase("Client Pays All")){
				if (expectedPrice == actualAmtClientpays){

					clientPaysAmtMatch = true;
				}
			}else{
				if ((expectedPrice/2) == actualAmtClientpays){

					clientPaysAmtMatch = true;
				}

			}

			if(clientPaysAmtMatch){
				sc.STAF_ReportEvent("Pass", "Step 1", "Account Pays amount is as expected.Val-"+actualAmtClientpays, 1);
			}else{
				sc.STAF_ReportEvent("Fail", "Step 1", "Account Pays amount is NOT as expected.Exp-"+expectedPrice/2+ " Actual-"+actualAmtClientpays, 1);
			}

			//validation of Account will pay only
			if(actualAmtClientpaysText.toLowerCase().contains(accountName)){
				sc.STAF_ReportEvent("Pass", "Step 1", "Account will pay contains account name.Val-"+accountName, 1);
			}else{
				String clientToBeShared=Globals.testSuiteXLS.getCellData_fromTestData("ClientToBeShared");
				if(clientToBeShared.isEmpty() || clientToBeShared.equals("") || clientToBeShared == null){
					sc.STAF_ReportEvent("Fail", "Step 1", "Account will pay DOES NOt contain expected account name.Exp-"+accountName, 1);	
				}
				
			}	
		}



		//validation of You pay which doesnt appear for Client Pays All pricing mode
		String volunteerPaysText;
		if(!pricingType.equalsIgnoreCase("Client Pays All") && !pricingType.equalsIgnoreCase("Client Pays Fees Only") && !pricingType.equalsIgnoreCase("Volunteer Pays All") ){
			volunteerPaysText = driver.findElement(LocatorAccess.getLocator("invitationStep1_volunteerPays_td")).getText();
			double actualVolunteerCost = Double.parseDouble(volunteerPaysText.split("\\$")[1]);
			double expectedVolCost=0;

			if(pricingType.equalsIgnoreCase("Volunteer Pays All") || pricingType.equalsIgnoreCase("Client Pays Fees Only")){
				expectedVolCost = expectedPrice;

			}else if(pricingType.equalsIgnoreCase("Client Pays Half + Fees")|| pricingType.equalsIgnoreCase("Client Pays  Half + No Fees")){
				expectedVolCost = expectedPrice /2;

			}

			if(expectedVolCost == actualVolunteerCost){
				sc.STAF_ReportEvent("Pass", "Step 1", "Volunteer will pay Amount is as Expected.Val-"+expectedVolCost, 1);
			}else{
				sc.STAF_ReportEvent("Fail", "Step 1", "Volunteer will pay Amount is NOT as Expected.Exp-"+expectedVolCost+ " Actual-"+actualVolunteerCost, 1);
			}
		}

	}

	/**************************************************************************************************
	 * Method to verify GUI validation for Org User login page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyOrgUserLoginPage(){
		int timeOutInSeconds = 5;
		//header section validation

		sc.clickLink_NavigateAndVerify("clientBGDashboard_abount_link", "about_repOrg_chk", "VVOU2 - Verify the About Button functionality.");
		sc.clickLink_NavigateAndVerify("clientBGDashboard_contact_link", "contact_bodySection_section", "VVOU3 - Verify the Contact Button functionality.");
		sc.clickLink_NavigateAndVerify("clientBGDashboard_blog_link", "blog_headerForm_form", "VVOU4 - Verify the Blog Button functionality.");
		sc.clickLink_NavigateAndVerify("clientBGDashboard_login_link", "login_orgUsername_txt", "VVOU1 - Verify the Login Button functionality.");

		sc.clickLink_NavigateAndVerify("login_twitter_img", "twitter_loginSearchPage_txt", "VVOU5 - Verify the twitter link on login page.");
		sc.clickLink_NavigateAndVerify("login_facebook_img", "fb_login_txt", "VVOU6 - Verify the Facebook link on login page.");
		//		sc.clickLink_NavigateAndVerify("login_linkedin_img", "linkedin_whatsLinkenIn_link", "VVOU7 - Verify the Linkedin link on login page.");
		//		Anand 3/21 -The below validation is commented as google plus cannot be accessed
		//		sc.clickLink_NavigateAndVerify("login_googleplus_img", "googlePlus_signIn_link", "VVOU8 - Verify the googleplus link on login page.");
		sc.clickLink_NavigateAndVerify("login_rssfeed_img", "blog_headerForm_form", "VVOU9 - Verify the RSS Feeds link on login page.");

		sc.clickLink_NavigateAndVerify("login_home_link", "login_orgUsername_txt", "VVOU10 - Verify the Home link on login page.");

		//TODO - need to remove reporting when reporting is implemented in sc.isDisplayed
		sc.isDisplayed_withCustomReporting("login_privacypolicy_link", "VVOU11 - Verify the Privacy policy  link on login page.");
		sc.isDisplayed_withCustomReporting("login_termsOfUse_link", "VVOU12 - Verify the Terms Of Use link on login page.");
		sc.isDisplayed_withCustomReporting("login_email_link", "VVOU13 - Verify the Email link on login page.");
		sc.isDisplayed_withCustomReporting("login_top_link", "VVOU14 - Verify the Top link on login page.");



		try {
			sc.setValueJsChange("login_orgUsername_txt", "");
			sc.setValueJsChange("login_orgPassword_txt","");
			sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);

			String tempRetval = Globals.KEYWORD_FAIL;
			String stepName="";

			String expectdText = "Please verify your user name.";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
			stepName = "VVOU15 - Verify the Log in button when the username and password fields are blank.";

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Expected - "+expectdText, 1);
			}

			sc.setValueJsChange("login_orgUsername_txt", "abcd");
			sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
			expectdText = "Please verify your password.";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
			stepName = "VVOU16 - Verify the Log in button when the Username field has the valid value and password field has the invalid.";

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Expected - "+expectdText, 1);
			}


			String orgUserName=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
			sc.setValueJsChange("login_orgUsername_txt", orgUserName);
			tempRetval=sc.setValueJsChange("login_orgPassword_txt","abc");
			sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
			Thread.sleep(3000);

			expectdText = "Please provide a valid username and password";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
			stepName = "VVOU17 - Verify the Log in button when the Username field has the valid value and password field has the invalid";

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Expected - "+expectdText, 1);
			}

			stepName = "VVOU20 - Verify the innertext label fields for Organization User Name";
			sc.verifyPlaceholderText("login_orgUsername_txt", stepName, "Username or Email");

			stepName = "VVOU21 - Verify the innertext label fields for Organization User Password.";
			sc.verifyPlaceholderText("login_orgPassword_txt", stepName, "Password");


		} catch (Exception e) {

			e.printStackTrace();
		}

		//		sc.clickLink_NavigateAndVerify("clientBGDashboard_abount_link", "about_repOrg_chk", "clientBGDashboard_abount_link");
	}

	/**************************************************************************************************
	 * Method to verify links present in Org user homepage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyOrgUserHomepage(){
		//sc.clickLink_NavigateAndVerify("clientBGDashboard_courtNews_link", "courtNew_jurisTable_tbl", "VVOU27 - Verify the COURT NEWS Button on Volunteer dashboard page.");
		//sc.clickLink_NavigateAndVerify("clientBGDashboard_abount_link", "about_repOrg_chk", "VVOU28 - Verify the ABOUT Button on Volunteer dashboard page.");
		//sc.clickLink_NavigateAndVerify("clientBGDashboard_contact_link", "contact_bodySection_section", "VVOU29 - Verify the CONTACT Button on Volunteer dashboard page.");
		sc.clickLink_NavigateAndVerify("clientBGDashboard_dashboard_link", "dashboard_search_txt", "VVOU34 - Verify the Dashboar Button on volunteer dashboard page.");
		//sc.clickLink_NavigateAndVerify("clientBGDashboard_blog_link", "blog_headerForm_form", "VVOU33 - Verify the Blog Button on volunteer dashboard page.");
		sc.clickLink_NavigateAndVerify("clientBGDashboard_resourceHub_link", "resourcehub_welcome_txt", "VVOU35- Verify the Resource Hub button on Volunteer dashboard menu bar");
		String otherplatform=Globals.testSuiteXLS.getCellData_fromTestData("Other Platform");
		if(otherplatform.equalsIgnoreCase("Sterling West")){
		sc.clickLink_NavigateAndVerify("clientBGDashboard_screeningDirect_link", "ScreeningDirect_img", "VVOU35 - Verify the Screening Direct image on volunteer dashboard page.");
		} else if(otherplatform.equalsIgnoreCase("Sterling One")){
		sc.clickLink_NavigateAndVerify("clientBGDashboard_screeningDirect_link", "SterlingOne_txt", "VVOU36 - Verify the SterlingOne text on volunteer dashboard page.");
		}
	}

	/**************************************************************************************************
	 * Method to verify Send Invitation page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifySendInvitationPage(){

		String stepName = "VVOU36 - Verify the presence of the innerlable fields for everfy field on the Send invitation popup.";
		sc.verifyPlaceholderText("sendInvitation_fName_txt", stepName, "First Name*");
		sc.verifyPlaceholderText("sendInvitation_lName_txt", stepName, "Last Name*");
		sc.verifyPlaceholderText("sendInvitation_emailAddress_txt", stepName, "Email Address*");
		sc.verifyPlaceholderText("sendInvitation_emailBody_txt", stepName, "Enter message to send to candidates");

	}

	/**************************************************************************************************
	 * Method to verify Client Ordering Step 1 page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyClientOrderingStep1(){

		try {
			sc.clickWhenElementIsClickable("clientStep1_nextStep_btn", 10);

			String expectdText = "Position is required";
			String tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
			String stepName = "VVOU78 - Verify the error messages on the Client ordering Step 1 section when the user has clicked on Next Step button without selecting any position";

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Expected - "+expectdText, 1);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**************************************************************************************************
	 * Method to verify Client Ordering Step 2 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyClientOrderingStep2(){



		try {
			String stepName = "VVOU80 - Verify the all the innerlabel fields on the step 2 section client ordering page.";
			sc.verifyPlaceholderText("clientStep2_firstName_txt", stepName, "First Name*");
			sc.verifyPlaceholderText("clientStep2_lastName_txt", stepName, "Last Name*");
			sc.verifyPlaceholderText("clientStep2_middleName_txt", stepName, "Middle Name*");
			sc.verifyPlaceholderText("clientStep2_suffix_txt", stepName, "Suffix");
			sc.verifyPlaceholderText("clientStep2_dobDay_txt", stepName, "DOB Day*");
			sc.verifyPlaceholderText("clientStep2_dobYear_txt", stepName, "DOB Year*");
			sc.verifyPlaceholderText("clientStep2_SSN_txt", stepName, "SSN* (format ###-##-####)");
			sc.verifyPlaceholderText("clientStep2_phoneNo_txt", stepName, "Phone Number* (format ###-###-####)");

			sc.clickWhenElementIsClickable("clientStep2_nextStep_btn", 10);

			String expectdText="";
			String tempRetval = isProductPartOfOrdering("MVR");
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				expectdText = "DOB Month is required, DOB Day is required (or invalid), DOB Year is required, First Name is required, Middle Name is required (or uncheck), Last Name is required, SSN is required (or uncheck), Gender is required, Phone is required, Email Address is required (or uncheck), Drivers License Number, Drivers License State, Last Name on License, First Name on License, Candidate's age must be 14 or older";
			}else{
				expectdText = "DOB month is invalid, DOB day is invalid, DOB year is invalid, First Name is required, Middle Name is required (or uncheck), Last Name is required, SSN is required (or uncheck), Gender is required, Phone is required, Email Address is required (or uncheck), Candidate's age must be 14 or older";
			}

			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
			stepName = "VVOU81 - Verify the error messages on the step 2 of client ordering page when the user has clicked on the  Next step button with out selecting any fields.";

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Expected - "+expectdText, 1);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**************************************************************************************************
	 * Method to verify Client Ordering Step 4 page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyClientOrderingStep4(){



		try {
			String stepName = "VVOU85 - Verify the all the innerlabel fields on the step 4 section client ordering page.";
			sc.verifyPlaceholderText("clientStep4_addressLine1_txt", stepName, "Address Line 1*");
			sc.verifyPlaceholderText("clientStep4_addressLine2_txt", stepName, "Address Line 2");
			sc.verifyPlaceholderText("clientStep4_addressCity_txt", stepName, "City*");
			sc.verifyPlaceholderText("clientStep4_addressZipCode_txt", stepName, "Zip Code*");


			sc.clickWhenElementIsClickable("clientStep4_nextStep_btn", 10);

			String expectdText = "Address Line 1 is required, City is required, State is required, Zip Code is required, Month candidate started living at this address is required is required, Year candidate started living at this address is required is required";
			String tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
			stepName = "VVOU86 - Verify the error messages on the step 4 of client ordering page when the user has clicked on the  Next step button with out selecting any fields.";

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Expected - "+expectdText, 1);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**************************************************************************************************
	 * Method to verify Client Ordering Step 5
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyClientOrderingStep5(){



		try {


			sc.clickWhenElementIsClickable("clientStep5_submitOrder_btn", 10);

			String expectdText = "Summary of Rights is required, Standard Consent is required";
			String tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
			String stepName = "VVOU88 - Verify the error messages on the step 5 of client ordering page when the user has clicked on the Submit Order button with out selecting any fields.";

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Expected - "+expectdText, 1);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	
	/**************************************************************************************************
	 * Method to verify pricing details on Ordering Step 4 page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifySourceFees(){
		//		int rowCount = 0;
		int rowCountGrid_2;
		String tempRetval=Globals.KEYWORD_FAIL;
		
		double totalCost= 0 ;
		double accountCost= 0 ,taxAmount=0;
		double fastpassCost = 0;
		double volunteerDonationAmt= 0 ;
		String cellValue,priceInUI;
		double subTotal=0,volunteerAmount=0;
		String l1Present,l2Present,l3Present;
		boolean paAbuseFlag=false;

		String productList[]=Globals.testSuiteXLS.getCellData_fromTestData("SourceFees").split(";");
		for(String product:productList){
		    
			String prodName=product.split("=")[0];
			String sourFeexpath="//tbody[@id='orderProducts']/tr/td[contains(text(),'"+prodName+"')]/../following-sibling::tr/td[text()='Source Fees']/following-sibling::td";
			//		rowCount = sc.getRowCount_tbl(elementTable);
			tempRetval=sc.waitTillElementDisplayed(driver.findElement(By.xpath(sourFeexpath)), 2);
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Source Fee Verify", "Source Fee is not getting displayed For - "+prodName,1);
			}else{
				String getSourceFeeText=sc.getWebElementText(driver.findElement(By.xpath(sourFeexpath))).split("\\$")[1].trim();
				if(!(getSourceFeeText.equalsIgnoreCase(product.split("=")[1].trim()))){
					sc.STAF_ReportEvent("Fail", "Source Fee Verify", "Source Fee of "+prodName+" is not matched with expected- "+product.split("=")[1].trim(),1);
				}
			}
		}
	}



	/**************************************************************************************************
	 * Method to verify pricing details on Ordering Step 4 page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static boolean verifyPricingStep4(){
		//		int rowCount = 0;
		int rowCountGrid_2;
		double totalCost= 0 ;
		double accountCost= 0 ,taxAmount=0;
		double fastpassCost = 0;
		double volunteerDonationAmt= 0 ;
		String cellValue,priceInUI;
		double subTotal=0,volunteerAmount=0;
		String l1Present,l2Present,l3Present;
		boolean paAbuseFlag=false;


		WebElement elementTable = sc.createWebElement("clientStep4_priceGrid_tbl");
		//		rowCount = sc.getRowCount_tbl(elementTable);


		List<WebElement> rows = elementTable.findElements(By.xpath("./tbody[2]/tr"));
		rowCountGrid_2 = rows.size();

		l1Present = isProductPartOfOrdering("L1");
		l2Present = isProductPartOfOrdering("L2");
		l3Present = isProductPartOfOrdering("L3");
        sc.waitForPageLoad();
       

		for(int i = 3;i<=rowCountGrid_2;i++){
			cellValue = elementTable.findElement(By.xpath("./tbody[2]/tr["+i+"]/td[1]")).getText();
			if(cellValue.equalsIgnoreCase("") || cellValue.isEmpty()){
				continue;
			}
			priceInUI = elementTable.findElement(By.xpath("./tbody[2]/tr["+i+"]/td[2]")).getText();

			if (cellValue.equalsIgnoreCase("Total cost of Background Check")){

				totalCost = Double.parseDouble(priceInUI.split("\\$")[1]);

			}else if(cellValue.contains("Amount Paid by")){

				accountCost = Double.parseDouble(priceInUI.split("\\$")[1].replace(")", ""));

			}else if(cellValue.equalsIgnoreCase("Donation")){

				volunteerDonationAmt = Double.parseDouble(priceInUI.split("\\$")[1]);

			}else if(cellValue.equalsIgnoreCase("Volunteer Fast-Pass")){

				fastpassCost = Double.parseDouble(priceInUI.split("\\$")[1]);

			}else if(cellValue.equalsIgnoreCase("Sub-Total")){

				subTotal = Double.parseDouble(priceInUI.split("\\$")[1]);

			}  //need to handle tax calculation
					else if(cellValue.equalsIgnoreCase("Tax")){
					
						taxAmount = Double.parseDouble(priceInUI.split("\\$")[1]);
					
			}else if(cellValue.equalsIgnoreCase("Amount paid by me")){

				volunteerAmount = Double.parseDouble(priceInUI.split("\\$")[1]);
			}
		}

		sc.scrollIntoView(elementTable);

		String pricingType = Globals.testSuiteXLS.getCellData_fromTestData("PricingName");
		double searchReqFees = sc.parseDouble(Globals.testSuiteXLS.getCellData_fromTestData("ExpectedJurisFees")) ;

		String creditPresent = "";
		creditPresent = isProductPartOfOrdering("CREDIT");
		double creditAmountToBeDeducted =0.00;
		if (creditPresent.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
			creditAmountToBeDeducted = fetchProductPrice(VV_Products.CREDIT);
		}


		//			Fast Pass price validation
		String fastPassSelected = Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_FastPass");
		double expectedFassPassAmt = 0.00 ;

		if (fastPassSelected.equalsIgnoreCase("Yes") && (l1Present.equalsIgnoreCase(Globals.KEYWORD_PASS)||l2Present.equalsIgnoreCase(Globals.KEYWORD_PASS) || l3Present.equalsIgnoreCase(Globals.KEYWORD_PASS))){
			expectedFassPassAmt = 4.00;
			sc.verifyExpectedValue(expectedFassPassAmt,fastpassCost,"Verify FAST PASS PRICE ");

		}

		//			Volunteer Donation Amount price validation
		String volDonationSelected = Globals.testSuiteXLS.getCellData_fromTestData("VolunteerDonationRequired");
		double expectedDonationAmount = 0.00;

		if(!pricingType.equalsIgnoreCase("Client Pays Fees Only") && !pricingType.equalsIgnoreCase("Volunteer Pays All") ){
			if (volDonationSelected.equalsIgnoreCase("Yes")) {

				Select select = new Select(sc.createWebElement("invitationStep4_donationSelection_dd"));
				String expectedDonationAmountTxt =  select.getFirstSelectedOption().getText();
				expectedDonationAmount =  Double.parseDouble(expectedDonationAmountTxt);

			}

			sc.verifyExpectedValue(expectedDonationAmount,volunteerDonationAmt,"Verify Volunteer Donation Amt");
		}


		//		Total Cost of Background price validation
		double expectedTotalPrice =0.00;
		try {
			expectedTotalPrice = fetchExpectedTotalPriceSTEP4();
			sc.verifyExpectedValue(expectedTotalPrice,totalCost,"Verify Total Cost of background check");

		} catch (Exception e) {

			e.printStackTrace();
		}

		//		Amount Paid by Account validation
		
		String absprod=Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts");
		String addState=Globals.testSuiteXLS.getCellData_fromTestData("AddressState");
		int aliasQuantity=0;
		double abuseAccessCharges = 0;
		
		if ((absprod.contains("ABUSE")) && (addState.equalsIgnoreCase("Pennsylvania")) || (absprod.contains("ABUSE(PA)")) ){
							
							String response =   Globals.testSuiteXLS.getCellData_fromTestData("AbusePAResponse");
							if (response.equalsIgnoreCase("No")){
								aliasQuantity = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
								aliasQuantity = aliasQuantity+1;
								paAbuseFlag=true;
							}else{
								String AbusePACheckbox = Globals.testSuiteXLS.getCellData_fromTestData("AbusePensylvaniacheckbox");
								if(AbusePACheckbox.equalsIgnoreCase("Yes")){
									aliasQuantity = 0;
									abuseAccessCharges=0.00;
								}else{
									aliasQuantity = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
									aliasQuantity = aliasQuantity+1;
									abuseAccessCharges = 13.00*aliasQuantity; //access charges will only be included when response is Yes and setting is No
									paAbuseFlag=true; 
								}
							}

							searchReqFees=searchReqFees+abuseAccessCharges;
		} 
		
		
		double expectedClientAmount = 0.00;

		if (pricingType.equalsIgnoreCase("Client Pays Half + Fees") ) {
			expectedClientAmount = ((expectedTotalPrice - creditAmountToBeDeducted - searchReqFees)/2) + searchReqFees +creditAmountToBeDeducted;
		}else if (pricingType.equalsIgnoreCase("Client Pays  Half + No Fees") ) {
			expectedClientAmount = (((expectedTotalPrice - creditAmountToBeDeducted) - searchReqFees)/2) + creditAmountToBeDeducted;
		}else if (pricingType.equalsIgnoreCase("Client Pays All") ) {
			expectedClientAmount = expectedTotalPrice ;

		}else if (pricingType.equalsIgnoreCase("Client Pays Fees Only") ) {
			expectedClientAmount = creditAmountToBeDeducted + searchReqFees ;
		}else if (pricingType.equalsIgnoreCase("Volunteer Pays All") ) {
			expectedClientAmount = creditAmountToBeDeducted;
		}
		
		verifyExpectedValuePricing(expectedClientAmount,accountCost,"Verify Account Pays Amount");

		//		Sub Total Amount Validation
		double expectedSubTotal=0.00;
		if(!pricingType.equalsIgnoreCase("Client Pays Fees Only") && !pricingType.equalsIgnoreCase("Volunteer Pays All") ){
			expectedSubTotal = expectedTotalPrice - expectedClientAmount + expectedFassPassAmt + expectedDonationAmount ;
			verifyExpectedValuePricing(expectedSubTotal,subTotal,"Verify Sub-Total Amount");
		}

		//		Amount Paid by me Validation- tax has not been included in the validation
		double expectedAmtPaidByMe=0.00;
		expectedAmtPaidByMe = expectedTotalPrice - expectedClientAmount + expectedFassPassAmt + expectedDonationAmount+taxAmount	 ;
		double TotalPrice = 0.00;
		TotalPrice = expectedAmtPaidByMe-expectedFassPassAmt;
		String Payment = Double.toString(TotalPrice);
		Globals.testSuiteXLS.setCellData_inTestData("Payment", Payment);  
		verifyExpectedValuePricing(expectedAmtPaidByMe,volunteerAmount,"Verify Amount paid by me");
        return paAbuseFlag; 
	}


	/**************************************************************************************************
	 * Method to fetch the expected price based on the products selected for Ordering in Step 4
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static double fetchExpectedTotalPriceSTEP4() throws Exception{
		APP_LOGGER.startFunction("fetchExpectedTotalPriceSTEP4");

		String products = Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts");
		String[] productsBeingOrdered = products.split("-");
		double totalAmt = 0;
		double price = 0.00;
		double totalProductPrice =0;
		int aliasQuantity=0;
		int prevAddressQuantity =0;
		int quantity = 1;
		int multiDistrict =0;
		int multiCountyCount =0;
		double abuseAccessCharges = 0;
		String applicantState = Globals.testSuiteXLS.getCellData_fromTestData("AddressState");
		if (productsBeingOrdered.length <=0 ){
			sc.STAF_ReportEvent("Fail", "Expected Total Price", "Total Price could not be calculated as Products are not mentioned in Test Data", 0);
			throw new Exception();
		}

		String addPrevAddress =   Globals.testSuiteXLS.getCellData_fromTestData("AddPrevAddr");


		for(String productName : productsBeingOrdered){
			quantity = 1;
			aliasQuantity=0;
			prevAddressQuantity =0;
			multiDistrict =0; 
			multiCountyCount=0;

			productName = productName.toUpperCase().trim();

			if(productName.equalsIgnoreCase("ref")){
				quantity = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("ReferenceToBeAddedCount"));
			}else if(productName.equalsIgnoreCase("oig")){
				aliasQuantity = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
				quantity = aliasQuantity+1;
			}else if(productName.equalsIgnoreCase("ID")){
				quantity = 0;
			}else if(productName.equalsIgnoreCase("COUNTYCIVIL")){
				aliasQuantity = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
				multiCountyCount = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfMultiCounties"));

				if(addPrevAddress.equalsIgnoreCase("Yes")){
					prevAddressQuantity =1; // considering one quantity would be added for previous address
				}

				quantity = (multiCountyCount * (aliasQuantity + 1)) + (prevAddressQuantity * aliasQuantity +1);

			}else if(productName.equalsIgnoreCase("FEDCRIM") || productName.equalsIgnoreCase("FEDCIVIL")){
				aliasQuantity = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));

				multiDistrict = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfMultiDistricts"));

				if(addPrevAddress.equalsIgnoreCase("Yes")){
					prevAddressQuantity =1; // considering one quantity would be added for previous address
				}

				quantity = (multiDistrict * (aliasQuantity + 1)) + (prevAddressQuantity * aliasQuantity +1);

			}else if(productName.contains("ABUSE")){
				String tempRetval =  Globals.KEYWORD_FAIL;
				boolean isAbuseOrdered = false;

				tempRetval = isProductPartOfOrdering("ABUSE(PA)");
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					isAbuseOrdered = true;
				}else {
					tempRetval = Globals.KEYWORD_FAIL;
					tempRetval = isProductPartOfOrdering("ABUSE");
					if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						if(applicantState.equalsIgnoreCase("Pennsylvania")){
							isAbuseOrdered = true;
						}
						else if(applicantState.equalsIgnoreCase("Colorado")){
							isAbuseOrdered = false;
						}
					}
				}

				
				if(isAbuseOrdered){
					
					String response =   Globals.testSuiteXLS.getCellData_fromTestData("AbusePAResponse");
					if (response.equalsIgnoreCase("No")){
						aliasQuantity = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
						aliasQuantity = aliasQuantity+1;
					}else{
						String AbusePACheckbox = Globals.testSuiteXLS.getCellData_fromTestData("AbusePensylvaniacheckbox");
						if(AbusePACheckbox.equalsIgnoreCase("Yes")){
							aliasQuantity = 0;
							abuseAccessCharges=0.00;
						}else{
							aliasQuantity = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
							aliasQuantity = aliasQuantity+1;
							abuseAccessCharges = 13.00*aliasQuantity; //access charges will only be included when response is Yes and setting is No
						}
					}
					
				
					productName = "ABUSE";
					quantity = aliasQuantity;

				}else if(isAbuseOrdered == false && applicantState.equalsIgnoreCase("Pennsylvania") ){
					aliasQuantity = 0;
					quantity = aliasQuantity;
				}
				else if(isAbuseOrdered == false && applicantState.equalsIgnoreCase("Colorado") ){
					aliasQuantity = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
					aliasQuantity = aliasQuantity+1;
					quantity = aliasQuantity;
					productName = "ABUSE";
				}
				quantity = aliasQuantity;
			}
			price = fetchProductPrice(VV_Products.valueOf(productName));
			totalProductPrice =0;
			totalProductPrice = price * quantity ;

			totalAmt = totalAmt +totalProductPrice ;



		}

		double searchReqFees =sc.parseDouble(Globals.testSuiteXLS.getCellData_fromTestData("ExpectedJurisFees")) ;

		totalAmt = totalAmt + searchReqFees + abuseAccessCharges;

		return totalAmt;		

	}

	/**************************************************************************************************
	 * Method to fetch the product price either default price or custom price based on test data
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static double fetchProductPrice(VV_Products product){
		String testDataColumnName="";
		double defaultPrice =0.00;
		double retval=0.00;
		double price =0.00;


		switch (product) {

		case L1:
			testDataColumnName ="CustomPrice_L1";
			//			defaultPrice = 13;
			break;
		case L2:
			testDataColumnName ="CustomPrice_L2";
			//			defaultPrice = 25;
			break;	
		case L3:
			testDataColumnName ="CustomPrice_L3";
			//			defaultPrice = 45;
			break;
		case CREDIT:
			testDataColumnName ="CustomPrice_Credit";
			//			defaultPrice = 16;
			break;
		case MVR:
			testDataColumnName ="CustomPrice_MVR";
			//			defaultPrice = 13.5;
			break;

		case REF:
			testDataColumnName ="CustomPrice_Reference";
			//			defaultPrice = 18;
			break;

		case OIG:
			testDataColumnName ="CustomPrice_OIG";
			//			defaultPrice = 11;

			break;

		case SSNPROF:
			testDataColumnName ="CustomPrice_SSNProfile";
			//			defaultPrice = 3.5;
			break;

		case MAR:
			testDataColumnName ="CustomPrice_ManagedAdverse";
			//			defaultPrice = 4;
			break;

		case SELFADVERSE:
			testDataColumnName ="CustomPrice_SelfServeAdverse";
			//			defaultPrice = 0;
			break;

		case ABUSE:
			testDataColumnName ="CustomPrice_Abuse";
			//			defaultPrice = 12;
			break;
		case LS:
			testDataColumnName ="CustomPrice_LS";
			//			defaultPrice = 12;
			break;
		case ID:
			testDataColumnName ="CustomPrice_ID";
			//			defaultPrice = 12;
			break;
		case FEDCRIM:
			testDataColumnName ="CustomPrice_FedCrim";
			//			defaultPrice = 12;
			break;
		case FEDCIVIL:
			testDataColumnName ="CustomPrice_FedCivil";
			//			defaultPrice = 12;
			break;
		case COUNTYCIVIL:
			testDataColumnName ="CustomPrice_CountyCivil";
			//			defaultPrice = 12;
			break;
			
		case GLOBEX:
			testDataColumnName ="CustomPrice_Globex";
			//			defaultPrice = 18;
			break;

		default:
			sc.STAF_ReportEvent("Fail", "Expected Total Price", "TestDataError-Product format not supported -"+product, 0);
			break;
		}

		defaultPrice = product.DefPrice;
		
		
		String testDataVal =Globals.testSuiteXLS.getCellData_fromTestData(testDataColumnName) ;

		if(testDataVal.equalsIgnoreCase("NA")||testDataVal.equalsIgnoreCase("")||testDataVal.isEmpty()){

			retval  = defaultPrice  ;
		}else{
			price = sc.parseDouble(testDataVal);
			retval = price;
		}

		return retval;			

	}

	/**************************************************************************************************
	 * Method to verify pricing in Client Ordering workflow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyPricingClientOrdering() throws Exception{
		//		int rowCount = 0;
		int rowCountGrid_2;
		double totalCost= 0 ;
		String cellValue,priceInUI;
		double subTotal=0;

		WebElement elementTable = sc.createWebElement("clientStep4_priceGrid_tbl");
		//		rowCount = sc.getRowCount_tbl(elementTable);

		//		Need to sync as sometimes this grid takes a lot of time to load
		int timeOutInSeconds = 60;
		String syncLabel = null;
		String syncLocator = "//tbody[@id='orderProducts']/following-sibling::tbody/tr[3]/td[2]";
		Pattern p = Pattern.compile("([0-9])");
		Matcher m = null;
		int counter =0;
		boolean priceFound = false;
		double expectedTotalPrice =0.00;
        expectedTotalPrice = fetchExpectedTotalPriceSTEP4();
		while(counter <= timeOutInSeconds  ){
			if(priceFound == false && expectedTotalPrice!=0.00){
			Thread.sleep(1000);
			syncLabel = driver.findElement(By.xpath(syncLocator)).getText();
			counter++;

			m = p.matcher(syncLabel);
				if(m.find()){
					priceFound = true;
					break;
				}
			}
			counter++;
		}
		if (priceFound == false && expectedTotalPrice!=0.00){
			sc.STAF_ReportEvent("Fail", "Pricing Validations","Price table has not loaded properly",1);
			throw new Exception("Pricing table didnt load");
		}

		List<WebElement> rows = elementTable.findElements(By.xpath("./tbody[2]/tr"));
		rowCountGrid_2 = rows.size();

		for(int i = 3;i<=rowCountGrid_2;i++){
			cellValue = elementTable.findElement(By.xpath("./tbody[2]/tr["+i+"]/td[1]")).getText();
			if(cellValue.equalsIgnoreCase("") || cellValue.isEmpty()){
				continue;
			}
			//cellValue = elementTable.findElement(By.xpath("./tbody[2]/tr["+i+"]/td[2]")).getText();
			priceInUI = elementTable.findElement(By.xpath("./tbody[2]/tr["+i+"]/td[2]")).getText();

			if (cellValue.equalsIgnoreCase("Total")){

				totalCost = Double.parseDouble(priceInUI.split("\\$")[1]);

			}else if(cellValue.equalsIgnoreCase("Sub-Total")){

				subTotal = Double.parseDouble(priceInUI.split("\\$")[1]);

			} // need to handle tax calculation
			//			else if(cellValue.equalsIgnoreCase("Tax")){
			//			
			//				taxAmount = Double.parseDouble(priceInUI.split("\\$")[1]);
			//				
			//			}

		}

		sc.scrollIntoView(elementTable);

		//		Total Cost of Background price validation
		
		try {
			expectedTotalPrice = fetchExpectedTotalPriceSTEP4();
			sc.verifyExpectedValue(expectedTotalPrice,totalCost,"Verify Total Cost of background check");

		} catch (Exception e) {

			e.printStackTrace();
		}

		//		Sub Total Amount Validation
		double expectedSubTotal=0.00;
		expectedSubTotal = expectedTotalPrice;
		sc.verifyExpectedValue(expectedSubTotal,subTotal,"Verify Sub-Total Amount");


	}
	
	/**************************************************************************************************
	 * Method to verify Product list in Client Ordering workflow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyProdListClientOrdering() throws Exception{
		String cellValue;
		String expProdList="";
		WebElement elementTable = sc.createWebElement("clientStep4_ProdList_tbl");
		//		rowCount = sc.getRowCount_tbl(elementTable);

		//		Need to sync as sometimes this grid takes a lot of time to load
		int timeOutInSeconds = 60;
		String syncLabel = null;
		String syncLocator = "//table[@id='draftOrder']/tbody[@id='orderProducts']/tr[1]/td[1]";
		//elementCount = elementTable.findElements(By.xpath(syncLocator)).size();
		Pattern p = Pattern.compile("(Level|Motor|OIG|SSN)");
		Matcher m = null;
		int counter =0;
		boolean prodListFound = false;
		while(counter <= timeOutInSeconds &&  prodListFound == false ){
			Thread.sleep(1000);
			syncLabel = driver.findElement(By.xpath(syncLocator)).getText();
			counter++;

			m = p.matcher(syncLabel);
			if(m.find()){
				prodListFound = true;
				break;
			}

		}
		if (prodListFound == false){
			sc.STAF_ReportEvent("Fail", "Product List Validations","Product List table has not loaded properly",1);
			throw new Exception("Product List table didnt load");
		}

		List<WebElement> rows = elementTable.findElements(By.xpath("//table[@id='draftOrder']/tbody[@id='orderProducts']/tr/td[1]"));
		String[] arrProdList = new String[rows.size()];
		String[] arrProductList=null;
		int expProductCount = 0;
		int arrProdListindex =0;
		
		for(int i = 1;i<=rows.size();i++){
			cellValue = elementTable.findElement(By.xpath("./tr["+i+"]/td[1]")).getText();
			arrProdList[arrProdListindex] = cellValue;
			arrProdListindex=arrProdListindex+1;
		}

		//sc.scrollIntoView(elementTable);

		expProdList = Globals.testSuiteXLS.getCellData_fromTestData("Exp_Step4ProdList").trim();
		arrProductList = expProdList.split("\\\n");
		expProductCount = arrProductList.length;
		

		String[] diff = sc.differences(arrProdList, arrProductList);
		System.out.println(Arrays.toString(diff));

		if(diff.length == 0){
			sc.STAF_ReportEvent("Pass", "Order Step 4 Product List", "Products in order step 4 product list are as expected.Exp="+expProductCount , 0);
			
		}else{
			for(int i=0;i<diff.length;i++){
				sc.STAF_ReportEvent("Fail", "Order Step 4 Product List", "Product present/missing which is not expected-"+diff[i] , 0);
			}
		}


		
		
	}
	
	/**************************************************************************************************
	 * Method to verify Product list in Invitation Ordering workflow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyProdListInvitationOrdering() throws Exception{
		String cellValue;
		String expProdList="";
		WebElement elementTable = sc.createWebElement("clientStep4_ProdList_tbl");
		//		rowCount = sc.getRowCount_tbl(elementTable);

		//		Need to sync as sometimes this grid takes a lot of time to load
		int timeOutInSeconds = 60;
		String syncLabel = null;
		String syncLocator = "//table[@id='draftOrder']/tbody[@id='orderProducts']/tr[1]/td[1]";
		//elementCount = elementTable.findElements(By.xpath(syncLocator)).size();
		Pattern p = Pattern.compile("(Level|Motor|OIG|SSN)");
		Matcher m = null;
		int counter =0;
		boolean prodListFound = false;
		while(counter <= timeOutInSeconds &&  prodListFound == false ){
			Thread.sleep(1000);
			syncLabel = driver.findElement(By.xpath(syncLocator)).getText();
			counter++;

			m = p.matcher(syncLabel);
			if(m.find()){
				prodListFound = true;
				break;
			}

		}
		if (prodListFound == false){
			sc.STAF_ReportEvent("Fail", "Product List Validations","Product List table has not loaded properly",1);
			throw new Exception("Product List table didnt load");
		}

		List<WebElement> rows = elementTable.findElements(By.xpath("//table[@id='draftOrder']/tbody[@id='orderProducts']/tr/td[1]"));
		String[] arrProdList = new String[rows.size()];
		String[] arrProductList=null;
		int expProductCount = 0;
		int arrProdListindex =0;
		
		for(int i = 1;i<=rows.size();i++){
			cellValue = elementTable.findElement(By.xpath("./tr["+i+"]/td[1]")).getText();
			arrProdList[arrProdListindex] = cellValue;
			arrProdListindex=arrProdListindex+1;
		}

		//sc.scrollIntoView(elementTable);

		expProdList = Globals.testSuiteXLS.getCellData_fromTestData("Exp_Step4ProdList").trim();
		arrProductList = expProdList.split("\\\n");
		expProductCount = arrProductList.length;
		

		String[] diff = sc.differences(arrProdList, arrProductList);
		System.out.println(Arrays.toString(diff));

		if(diff.length == 0){
			sc.STAF_ReportEvent("Pass", "Order Step 4 Product List", "Products in order step 4 product list are as expected.Exp="+expProductCount , 0);
			
		}else{
			for(int i=0;i<diff.length;i++){
				sc.STAF_ReportEvent("Fail", "Order Step 4 Product List", "Product present/missing which is not expected-"+diff[i] , 0);
			}
		}


		
		
	}

	/**************************************************************************************************
	 * Method to update the upload ordering csv file by generating dynamic values
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String updateUploadOrderingCSVFile() throws Exception{
		//		String refUploadFile = "C:\\STAF_Selenium\\SeleniumFramework\\src\\dataEngine\\VV_Upload.csv";
		String refUploadFile = Globals.TestDir +"\\src\\test\\Resources\\refFile\\VV_Upload.csv";
		String resultCSVFile = Globals.currentPlatform_Path +"\\Excel_Results\\"+ Globals.TestCaseID + "_ModifiedUpload.csv";

		CsvReader volData = new CsvReader(refUploadFile);
		CsvWriter newVolData = new CsvWriter(resultCSVFile);

		//		volData.readHeaders();

		int lineCount = 2;
		String[] modifiedValues = new String[14]; 
		String[] existsingValues = new String[14];
		String volSinceDate = sc.getPriorDate("MM/dd/yyyy", -5);
		String dynamicString="";

		modifiedValues[2] = Globals.fromEmailID;
		//modifiedValues[3] = volSinceDate;
		modifiedValues[4] = Globals.Volunteer_Phone;
		modifiedValues[5] = Globals.Volunteer_AddressLine;
		modifiedValues[6] = Globals.Volunteer_AddressLine;
		modifiedValues[7] = Globals.Volunteer_City;
		modifiedValues[8] =  sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("AddressState"));
		modifiedValues[9] =  Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode");
		modifiedValues[10] = Globals.testSuiteXLS.getCellData_fromTestData("CustomField1");
		modifiedValues[11] = Globals.testSuiteXLS.getCellData_fromTestData("CustomField2");
		modifiedValues[12] = Globals.testSuiteXLS.getCellData_fromTestData("CustomField3");
		//modifiedValues[13] = volSinceDate;

		while(volData.readRecord()){
			if(lineCount>=2 && lineCount <=9){
				dynamicString = RandomStringUtils.randomAlphabetic(15);
				if(lineCount ==2){
					modifiedValues[0] =dynamicString +"'fname";
					Globals.testSuiteXLS.setCellData_inTestData("volunteerFName", modifiedValues[0]);

					modifiedValues[1] = dynamicString +"'lname";
					Globals.testSuiteXLS.setCellData_inTestData("volunteerLName", modifiedValues[1]);
				}else{
					modifiedValues[0] = dynamicString;
					modifiedValues[1] = dynamicString; 
				}


				newVolData.writeRecord(modifiedValues);
				lineCount =lineCount +1;
			}else{
				existsingValues = volData.getValues();
				newVolData.writeRecord(existsingValues);
			}
		}

		newVolData.close();

		volData.close();


		return resultCSVFile;

	}

	/**************************************************************************************************
	 * Method to fetch the volunteer dashboard count of volunteers in different status
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static HashMap<String,String> fetchVolunteerDashboardCount() throws Exception{
		APP_LOGGER.startFunction("fetchVolunteerDashboardCount");
		String tempRetval = Globals.KEYWORD_FAIL;
		HashMap<String,String> volDashboard =  new HashMap<String,String>();
		long timeOutinSeconds = 60;

		try {

			tempRetval = sc.waitforElementToDisplay("volDashboard_openInvitation_link", timeOutinSeconds);
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Open invitations Count is not visible", 1);
				throw new Exception("Volunteer Dashboard - Open invitations Count is not visible");
			}

			volDashboard.put("OpenInvitation",driver.findElement(LocatorAccess.getLocator("volDashboard_openInvitation_link")).getText().trim().split(" ")[0]);
			volDashboard.put("BackgroundCheckPending",driver.findElement(LocatorAccess.getLocator("volDashboard_backgroundCheckPending_link")).getText().trim().split(" ")[0]);
			volDashboard.put("Inactive",driver.findElement(LocatorAccess.getLocator("volDashboard_inactive_link")).getText().trim().split(" ")[0]);
			volDashboard.put("Eligible",driver.findElement(LocatorAccess.getLocator("volDashboard_eligible_link")).getText().trim().split(" ")[0]);
			volDashboard.put("Ineligible",driver.findElement(LocatorAccess.getLocator("volDashboard_ineligible_link")).getText().trim().split(" ")[0]);
			volDashboard.put("NoOrderPlaced",driver.findElement(LocatorAccess.getLocator("volDashboard_none_link")).getText().trim().split(" ")[0]);
			volDashboard.put("PendingReview",driver.findElement(LocatorAccess.getLocator("volDashboard_pendingReview_link")).getText().trim().split(" ")[0]);
			volDashboard.put("ViewAllVolunteers",driver.findElement(LocatorAccess.getLocator("volDashboard_viewAllVolunteers_link")).getText().trim().split(" ")[0]);
		
		}catch (Exception e) {
			log.error("Method-fetchVolunteerDashboardCount | Unable to fetch volunteer dashboard count | Exception - "+ e.toString());
			throw e;
		}
		return volDashboard;
	}

	/**************************************************************************************************
	 * Method to verify volunteer dashboard count update post order/invite
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyVolunteerDashboard(HashMap<String,String> before, HashMap<String,String> after, String keyName, int differenceCount) {

		APP_LOGGER.startFunction("verifyVolunteerDashboard");
		String retval= Globals.KEYWORD_FAIL;
		int beforeCount = Integer.parseInt(before.get(keyName));
		int afterCount = Integer.parseInt(after.get(keyName));

		if(afterCount - beforeCount == differenceCount){
			sc.STAF_ReportEvent("Pass", "Volunteer Dashboard", keyName +" count has increased by " + differenceCount + " volunteers", 1);
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Mismatch in " + keyName +" count.Expected Diff=" + differenceCount + " BeforeCount = "+beforeCount +" After Count ="  +afterCount, 1);
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify and set data in Custom fields
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyAndSetCustomFields() throws Exception{

		int noOfCustomFields = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfCustomFieldsReq"));

		String customFieldName="";
		String customFieldReq="";
		String customFieldDisplayed="";
		String customFieldLOV = "";
		String tempretval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =5;
		String stepName = "STEP 1";
		String[] indvLOV;
		List<WebElement> ddItems = null;
		int ddSize=0;
		String ddItemUI="";
		String expFieldName ="";
		String customFieldAnswers="";
		String [] customFieldsplit;
		customFieldAnswers=Globals.testSuiteXLS.getCellData_fromTestData("CustomFieldAnswers");
		customFieldsplit=customFieldAnswers.split(";");

		for(int i=1;i<=noOfCustomFields;i++){


			customFieldName = Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i);
			customFieldLOV= Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i+"_LOV");
			customFieldReq = Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i+"_Required");
			customFieldDisplayed= Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i+"_Displayed");

			if(customFieldDisplayed.equalsIgnoreCase("Yes")){
				tempretval = sc.waitforElementToDisplay("invitationStep1_customField"+i+"_dd", timeOutInSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Pass", stepName, "Custom Field-"+i+" is visible ", 1);

					if(customFieldReq.equalsIgnoreCase("Yes")){
						expFieldName = customFieldName + "*";
					}else{
						expFieldName = customFieldName;
					}

					if(!customFieldLOV.isEmpty()){
						ddItems = driver.findElements(By.xpath("//select[@name='Extra"+i+"']/option"));
						ddSize = ddItems.size();

						indvLOV = customFieldLOV.split(";");

						if(ddSize == indvLOV.length +1 ){
							sc.STAF_ReportEvent("Pass", stepName, "Dropdown items count for Custom Field-"+i+"is as Expected.Count-"+indvLOV.length, 0);

							ddItemUI = ddItems.get(0).getText();
							if(ddItemUI.equalsIgnoreCase(expFieldName) ){
								sc.STAF_ReportEvent("Pass", stepName, "Name for Custom Field-"+i+" is as Expected.Name-"+customFieldName, 0);
							}else{
								sc.STAF_ReportEvent("Fail", stepName, "Mismatch in Name for Custom Field-"+i+"Exp-"+customFieldName + " actual-"+ddItemUI, 0);
							}

							for( int j =1; j<= indvLOV.length ;j++){
								ddItemUI = ddItems.get(j).getText();
								if(ddItemUI.equalsIgnoreCase(indvLOV[j-1]) ){
									sc.STAF_ReportEvent("Pass", stepName, "Dropdown item for Custom Field-"+i+"is as Expected.Exp-"+ddItemUI, 0);
								}else{
									sc.STAF_ReportEvent("Fail", stepName, "Mismatch in Dropdown item for Custom Field-"+i+"Exp-"+indvLOV[j-1] + " actual-"+ddItemUI, 0);
								}
							}
							//sc.selectValue_byIndex("invitationStep1_customField"+i+"_dd", indvLOV.length-1);
							sc.selectValue_byVisibleText("invitationStep1_customField"+i+"_dd", customFieldsplit[i-1]);
						}else{
							sc.STAF_ReportEvent("Fail", stepName, "Mismatch in Dropdwon items count for Custom Field-"+i, 0);
						
						}


					}else{
						sc.setValueJsChange("invitationStep1_customField"+i+"_txt", "custom");	
					}

				}else{
					sc.STAF_ReportEvent("Fail", stepName, "Custom Field-"+i+" is NOT visible when Displayed = Yes ", 1);
				}

			}else{
				tempretval = sc.waitforElementToDisplay("invitationStep1_customField"+i+"_dd", 2);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Fail", stepName, "Custom Field-"+i+" is visible as Displayed is set to False ", 1);
				}else{
					sc.STAF_ReportEvent("Pass", stepName, "Custom Field-"+i+" is not visible as Displayed is set to False", 1);
				}
			}
		}
	}

	/**************************************************************************************************
	 * Method to verify and set data for custom fields in Client Ordering workflow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void setCustomFields_ClientOrdering() throws Exception{
		int noOfCustomFields = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfCustomFieldsReq"));
		String customFieldName="";
		String tempretval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 5;
		String fieldNameLocator = "";

		String stepName="Step 2";
		
		for(int i=1;i<=noOfCustomFields;i++){
			customFieldName = Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i);
			fieldNameLocator = "clientStep2_customField"+i+"_txt";
			String[] customFieldsplit = Globals.testSuiteXLS.getCellData_fromTestData("CustomFieldAnswers").split(";");
			
			tempretval = sc.waitforElementToDisplay(fieldNameLocator, timeOutInSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.scrollIntoView(fieldNameLocator);
				sc.STAF_ReportEvent("Pass", stepName, "Custom Field-"+i+" is visible ", 1);
				sc.verifyPlaceholderText(fieldNameLocator, "Custom Field Name", customFieldName);
				sc.setValueJsChange(fieldNameLocator, customFieldsplit[i-1]);
			}else{
				sc.STAF_ReportEvent("Fail", stepName, "Custom Field-"+i+" is NOT visible ", 1);
			}
		}
	}       

	/**************************************************************************************************
	 * Method to verify volunteer agrremnt section in Volunteer homepage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyVolunteerAgreement() throws Exception {
		APP_LOGGER.startFunction("verifyVolunteerAgreement");
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		String stepName = "VolunteerLogin-Agreements";
		//		sc.clickWhenElementIsClickable("volHomePage_getVerified_link",timeOutInSeconds );
		sc.clickWhenElementIsClickable("volunteerHomepage_myProfile_link",timeOutInSeconds );

		tempRetval = sc.waitforElementToDisplay("volunteerHomepage_agreements_link", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Agreements Link is not displayed", 1);
			throw new Exception(stepName+ " - Agreements Link is not displayed");
		}


		sc.clickWhenElementIsClickable("volunteerHomepage_agreements_link", timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("agreements_clientTermOfService_link", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Terms And Condition page has not loaded", 1);
			throw new Exception(stepName+ " - Terms And Condition page has not loaded");
		}

		/* These two links are removed as a part of MER-9475*//*Lakshmi*/
		//sc.isDisplayed_withCustomReporting("agreements_clientTermOfService_link", "Client Terms of service");
		//sc.isDisplayed_withCustomReporting("agreements_vvTermOfService_link", "VV  Terms of service");
		sc.isDisplayed_withCustomReporting("agreements_privacyPolicy_link", "Privacy policy");
		sc.isDisplayed_withCustomReporting("agreements_termsOfUse_link", "Terms Of Use");
		return Globals.KEYWORD_PASS;
	}

	/**************************************************************************************************
	 * Method to edit volunteer photo in Volunteer Homepage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String editVolunteerPhoto() throws Exception {
		APP_LOGGER.startFunction("editVolunteerPhoto");
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		String stepName = "VolunteerLogin-EditPhoto";

		tempRetval = sc.waitforElementToDisplay("volunteerHomepage_editPhoto_link", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Edit Photo Link is not displayed", 1);
			throw new Exception(stepName+ " - Edit Photo Link is not displayed");
		}


		sc.clickWhenElementIsClickable("volunteerHomepage_editPhoto_link", timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("editPhoto_browsePhoto_file", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Edit Photo page has not loaded", 1);
			throw new Exception(stepName+ " - Edit Photo page has not loaded");
		}

		sc.clickWhenElementIsClickable("editPhoto_close_btn", timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("volunteerHomepage_logo_img", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", "VVOL11 - Verify the behavior of the close button on the Edit Photo tab.", "Edit Photo tab has been closed", 1);
		}else{
			sc.STAF_ReportEvent("Fail", "VVOL11 - Verify the behavior of the close button on the Edit Photo tab.", "Edit Photo tab has NOT been closed", 1);
		}

		sc.clickWhenElementIsClickable("volunteerHomepage_editPhoto_link", timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("editPhoto_browsePhoto_file", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Edit Photo page has not loaded", 1);
			throw new Exception(stepName+ " - Edit Photo page has not loaded");
		}

		/*Anand-5-5-The below code is comment as defect MER-7164 wont be fixed.Hence test case is updated accordingly
		 * tempRetval = sc.isEnabled("editPhoto_uploadPhoto_btn");
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Pass", "VVOL10 - Verify the presence of the buttons when the photo is not uploaded.", "Upload Photo button is disabled", 1);
		}else{
			sc.STAF_ReportEvent("Fail", "VVOL10 - Verify the presence of the buttons when the photo is not uploaded.", "Upload Photo button is enabled", 1);
		}*/

		String updatedFilePath = Globals.TestDir +"\\src\\test\\Resources\\refFile\\VV_UploadPhoto.jpg";
		driver.findElement(LocatorAccess.getLocator("editPhoto_browsePhoto_file")).sendKeys(updatedFilePath);

		sc.clickWhenElementIsClickable("editPhoto_uploadPhoto_btn", timeOutInSeconds);

		tempRetval = sc.waitforElementToDisplay("volunteerHomepage_logo_img", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", "VVOL9 - Verify the Edit photo functionality on the Edit phot tab.", "Photo has been uploaded", 1);
		}else{
			sc.STAF_ReportEvent("Fail", "VVOL9 - Verify the Edit photo functionality on the Edit phot tab.", "Unable to upload photo", 1);
		}


		return Globals.KEYWORD_PASS;
	}

	/**************************************************************************************************
	 * Method to edit voluunteer profile data in Volunteer Homepage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String editVolunteerProfile() throws Exception {
		APP_LOGGER.startFunction("editVolunteerProfile");
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		String stepName = "VolunteerLogin-EditProfile";

		sc.clickWhenElementIsClickable("volunteerHomepage_myProfile_link",timeOutInSeconds );

		tempRetval = sc.waitforElementToDisplay("volunteerHomepage_editProfile_link", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Edit Profile Link is not displayed", 1);
			throw new Exception(stepName+ " - Edit Profile Link is not displayed");
		}


		sc.clickWhenElementIsClickable("volunteerHomepage_editProfile_link", timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("info_fname_txt", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "VVOL13 - Verify the presence of the fields values on the Edit Profile tab info section.", "Edit Profile page has not loaded", 1);
			throw new Exception(stepName+ " - Edit Profile page has not loaded");
		}else{
			sc.STAF_ReportEvent("Pass", "VVOL13 - Verify the presence of the fields values on the Edit Profile tab info section.", "Edit Profile page has loaded", 1);
		}


		sc.isDisplayed_withCustomReporting("editProfile_info_btn", "VVOL12 - Verify the presence of the sub sections on the Edit Profile tab.");
		sc.isDisplayed_withCustomReporting("editProfile_login_btn", "VVOL12 - Verify the presence of the sub sections on the Edit Profile tab.");

		sc.clickWhenElementIsClickable("info_close_btn", timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("volunteerHomepage_logo_img", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", "VVOL15 - Verify the behavior of the close button on the Edit Profile tab.", "Edit Profile tab has been closed", 1);
		}else{
			sc.STAF_ReportEvent("Fail", "VVOL15 - Verify the behavior of the close button on the Edit Profile tab.", "Edit Profile tab has NOT been closed", 1);
		}

		sc.clickWhenElementIsClickable("volunteerHomepage_editProfile_link", timeOutInSeconds);

		sc.setValueJsChange("info_fname_txt","testFName");
		sc.setValueJsChange("info_lname_txt", "testLName");

		tempRetval = sc.isEnabled("info_save_btn");
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", "VVOL14 - Verify the behavior of the save button on the Edit Profile tab Info section. ", "Save button is enabled when data is entered in fields", 1);
		}else{
			sc.STAF_ReportEvent("Fail", "VVOL14 - Verify the behavior of the save button on the Edit Profile tab Info section. ", "Save button is disabled after data is entered in fields", 1);
		}

		sc.clickWhenElementIsClickable("info_save_btn", timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("volunteerHomepage_logo_img", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", "VVOL15 - Verify the behavior of the save button on the Edit Profile tab.", "Profile data has been saved", 1);
		}else{
			sc.STAF_ReportEvent("Fail", "VVOL15 - Verify the behavior of the save button on the Edit Profile tab.", "Unable to save profile data", 1);
		}


		sc.clickLink_NavigateAndVerify("volunteerHomepage_about_link", "about_repOrg_chk", "VVOL34 - Verify the functionality of the About button.");
		sc.clickLink_NavigateAndVerify("volunteerHomepage_contact_link", "contact_bodySection_section", "VVOL35 - Verify the functionality of the Contact button.");
		sc.clickLink_NavigateAndVerify("volunteerHomepage_blog_link", "blog_headerForm_form", "VVOL37 - Verify the functionality of the Blog button.");

		sc.clickLink_NavigateAndVerify("volunteerHomepage_logout_link", "login_orgUsername_txt", "VVOL38 - Verify the functionality of the Logout button.");

		sc.clickLink_NavigateAndVerify("login_twitter_img", "twitter_loginSearchPage_txt", "VVOL39 - Verify the functionality of the twitter button.");
		sc.clickLink_NavigateAndVerify("login_facebook_img", "fb_login_txt", "VVOL40 - Verify the functionality of the Facebook button.");
		//		Anand-The below line is commented due to fact that the login screen for LinkenIn loads differently for different browsers
		//		sc.clickLink_NavigateAndVerify("login_linkedin_img", "linkedin_whatsLinkenIn_link", "VVOL41 - Verify the functionality of the Linked In button.");
		//		Anand 3/21 -The below validation is commented as google plus cannot be accessed
		//		sc.clickLink_NavigateAndVerify("login_googleplus_img", "googlePlus_signIn_link", "VVOL42 - Verify the functionality of the google plus button.");
		sc.clickLink_NavigateAndVerify("login_rssfeed_img", "blog_headerForm_form", "VVOL43 - Verify the functionality of the blog button.");

		sc.clickLink_NavigateAndVerify("login_home_link", "login_orgUsername_txt", "VVOL45 - Verify the functionality of the Home button.");

		//TODO - need to remove reporting when reporting is implemented in sc.isDisplayed
		sc.isDisplayed_withCustomReporting("login_privacypolicy_link", "VVOL46 - Verify the functionality of the Privacy Policay link.");
		sc.isDisplayed_withCustomReporting("login_termsOfUse_link", "VVOL47 - Verify the functionality of the Terms of Use link.");
		sc.isDisplayed_withCustomReporting("login_email_link", "VVOL49 - Verify the functionality of the Email link.");
		sc.isDisplayed_withCustomReporting("login_top_link", "VVOL50 - Verify the functionality of the Top link.");

		return Globals.KEYWORD_PASS;
	}

	/**************************************************************************************************
	 * Method to reset the volunteer password
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String resetVolunteerPassword() throws Exception{
		APP_LOGGER.startFunction("resetVolunteerPassword");
		String tempRetval = Globals.KEYWORD_FAIL;
		String retval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		String stepName = "Reset Volunteer Password";
		HashMap<String, String> emailDetails=  new HashMap<String, String>();
		String emailFound=null;
		String emailBodyText =null;
		String newPassword = null;
		 VVDAO vvDB             = null;
		 String newConfirmPwd=null;
		 String uName="";
		try{ 
		String resetPwdFlag=sc.testSuiteXLS.getCellData_fromTestData("ExistingAPIUser_Reset_change_pwd");
		if (resetPwdFlag.equalsIgnoreCase("Org_Pass_Reset")){
			uName = Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
		}else{
			uName = Globals.testSuiteXLS.getCellData_fromTestData("VolunteerUsername");
		}
		
		
		if(!(resetPwdFlag.equalsIgnoreCase("Reset pwd")) && !(resetPwdFlag.equalsIgnoreCase("Change pwd"))  ){
			sc.launchURL(Globals.BrowserType,Globals.getEnvPropertyValue("AppURL"));

			tempRetval = sc.waitforElementToDisplay("login_volunteerUsername_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Volunteer Login page is not loaded", 1);
				throw new Exception(stepName+ " - Volunteer Login page is not loaded");
			}

			sc.clickWhenElementIsClickable("login_forgotVolunteerPwd_link",timeOutInSeconds);
		}
		 
		if(!resetPwdFlag.equalsIgnoreCase("Change pwd")){
			tempRetval = sc.waitforElementToDisplay("passwordReset_username_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Password reset page is not loaded", 1);
				throw new Exception(stepName+ " - Password reset page is not loaded");
			}
			String bodyEmailPattern;
			if (resetPwdFlag.equalsIgnoreCase("Org_Pass_Reset")){
				bodyEmailPattern = Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName").trim();
			    
			}else{
				uName = Globals.testSuiteXLS.getCellData_fromTestData("VolunteerUsername");
				bodyEmailPattern = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName").trim();
			    if(bodyEmailPattern.equalsIgnoreCase("")||bodyEmailPattern==null || bodyEmailPattern.isEmpty() ){
			    	bodyEmailPattern= Globals.testSuiteXLS.getCellData_fromTestData("VolunteerUsername").trim();
			    
			    }

			}
//			String bodyEmailPattern = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName").trim();
//		    if(bodyEmailPattern.equalsIgnoreCase("")||bodyEmailPattern==null || bodyEmailPattern.isEmpty() ){
//		    	bodyEmailPattern= Globals.testSuiteXLS.getCellData_fromTestData("VolunteerUsername").trim();
//		    }

			sc.setValueJsChange("passwordReset_username_txt", uName);

			sc.clickWhenElementIsClickable("passwordReset_submit_btn",timeOutInSeconds);

			tempRetval = sc.waitforElementToDisplay("passwordReset_validationMsg_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Email sent for password reset request. Mesage not displayed", 1);
				throw new Exception(stepName+ " - Email sent for password reset request. Mesage not displayed");
			}else{
				sc.STAF_ReportEvent("Pass", stepName, "Email sent for password reset request. Mesage displayed", 1);
				retval = Globals.KEYWORD_PASS;
			}


			String subjectEmailPattern = "Your Password Reset Information is Here!";


			emailDetails = Email.fetchEmailFromOutlook(subjectEmailPattern,bodyEmailPattern,120);
			emailFound = emailDetails.get("EmailFound");

			if (emailFound.equalsIgnoreCase("True")){
				emailBodyText = emailDetails.get("EmailBody");
				emailBodyText = emailBodyText.replace("\"", "").trim();
				int startIndex = emailBodyText.indexOf("Temporary Password: ");
				int endindex = emailBodyText.indexOf("Reset Password Link:");
				newPassword = emailBodyText.substring(startIndex, endindex).replace("Temporary Password: ","").trim();

			    //			Globals.testSuiteXLS.setCellData_inTestData("OrgUserPwd", newPassword);

			    sc.STAF_ReportEvent("Pass", "Reset Password Email", "New volunteer Password received in email  - "+ newPassword, 0);
			    retval=Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "Reset Password Email", "Reset Volunteer password email not received ", 0);
				retval = Globals.KEYWORD_FAIL;
				return retval;
			}

			if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.clickWhenElementIsClickable("clientBGDashboard_login_link",timeOutInSeconds);	
				tempRetval = sc.waitforElementToDisplay("login_volunteerUsername_txt", timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", stepName, "Volunteer Login page is not loaded", 1);
					throw new Exception(stepName+ " - Volunteer Login page is not loaded");
				}

				sc.setValueJsChange("login_volunteerUsername_txt", uName);
				sc.setValueJsChange("login_volunteerPwd_txt", newPassword);
				sc.clickWhenElementIsClickable("login_volunteerLogin_btn",timeOutInSeconds);
			}
			newConfirmPwd="123qa123!";
			
		}else{
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
                 	String modifiedDate="2016-01-27";
                 	String updateSQLQuery = "update Memberships set LastPasswordChangedDate = ? where UserId =?";
                    
                    vvDB.ps = vvDB.conn.prepareStatement(updateSQLQuery);
                    vvDB.ps.setString(1, modifiedDate);
        			vvDB.ps.setString(2, uId);
        			
                    Thread.sleep(5000);
                    int statusUpdateRowCount = 0;
        			statusUpdateRowCount = vvDB.ps.executeUpdate();
        			if(initRowCount ==  statusUpdateRowCount){
        				sc.STAF_ReportEvent("Pass", "DB-Update", "LastPasswordChangedDate updated to "+modifiedDate, 0);
        				retval = Globals.KEYWORD_PASS;
        				break;
        			}else{
        				sc.STAF_ReportEvent("Fail", "DB-Update", "Unable to update LastPasswordChangedDate to greater than 365 days.ExpCount= "+initRowCount + " Actual- "+statusUpdateRowCount, 0);
        			    return Globals.KEYWORD_FAIL;
        			
        			}
         	    	}
	                
             }            

		   }catch(Exception e){
            e.printStackTrace();
            sc.STAF_ReportEvent("Fail", "MembershipsTable-DB", "Unable to change LastPasswordChangedDate for password expiry", 0);
		   }finally{
                 vvDB.cleanUpVVDAO();
           }
		
	   
		retval		=	sc.setValueJsChange("login_volunteerUsername_txt",uName );
		String password =	sc.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
		retval		=	sc.setValueJsChange("login_volunteerPwd_txt",password );
		sc.clickWhenElementIsClickable("apiVolunteers_LogIn_btn",timeOutInSeconds);	
		newPassword="123qa123!";
		newConfirmPwd="123Qa123!";	
			
		
		}
		
		tempRetval = sc.waitforElementToDisplay("passwordReset_changePassword_lbl", timeOutInSeconds);
     	if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Unable to login using reset password", 1);
				throw new Exception(stepName+ " - Unable to login using reset password");
		}else{
				sc.STAF_ReportEvent("Pass", stepName, "Login successfull using reset password", 1);
		}
		Globals.driver.findElement(By.xpath("//input[@id='OldPassword']")).sendKeys(newPassword);;
		//sc.setValueJsChange("passwordReset_oldPassword_txt", newPassword)
		sc.setValueJsChange("passwordReset_newPassword_txt", newConfirmPwd);
		sc.setValueJsChange("passwordReset_confirmPassword_txt", newConfirmPwd);
		Thread.sleep(3000);
		//sc.clickWhenElementIsClickable("passwordReset_changePassword_btn",timeOutInSeconds);
		AccountSetting.clickButtonUntilValidation("passwordReset_changePassword_btn", "passwordReset_successMsg_span");
		retval = sc.waitforElementToDisplay("passwordReset_successMsg_span", timeOutInSeconds);
			if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "Unable to change password.New Pwd-"+newConfirmPwd, 1);
				throw new Exception(stepName+ " - Unable to change password");
			}else{
				sc.STAF_ReportEvent("Pass", stepName, "Password change successful.New Pwd-"+newConfirmPwd, 1);
				//sc.testSuiteXLS.setCellData_inTestData("OrgUserPwd", newConfirmPwd);
			}
		}	catch(Exception e){
            e.printStackTrace();
            sc.STAF_ReportEvent("Fail", "resetVolunteerPassword", "reset Volunteer Password page getting exception", 0);
         }	
			return retval;
	}

	/**************************************************************************************************
	 * Method to Ordering Step1 - used for functional testing only
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String functionalInvitationOrderStep1() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 10;
		String tempRetval=Globals.KEYWORD_FAIL;
		String midName=null;
		String suffix=null;
		String fname = null;
		String lname = null;



		tempRetval=sc.waitforElementToDisplay("invitationStep1_fName_txt", timeOutinSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

			//verify pricing in Step1
			//				Below code is commented to support functional testing- Need to uncomment
			//				verifyPricingSTEP1();

			fname = sc.testSuiteXLS.getCellData_fromTestData("volunteerFName");
			lname = sc.testSuiteXLS.getCellData_fromTestData("volunteerLName");


			tempRetval = sc.setValueJsChange("invitationStep1_fName_txt",fname);
			midName =Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");
			if(midName != null ){
				tempRetval = sc.setValueJsChange("invitationStep1_midName_txt",midName);
			}else{
				tempRetval = sc.checkCheckBox("invitationStep1_midName_chk");
			}

			tempRetval = sc.setValueJsChange("invitationStep1_lName_txt",lname);

			suffix =Globals.testSuiteXLS.getCellData_fromTestData("VolunterrSuffix");
			if(suffix != null ){
				tempRetval = sc.setValueJsChange("invitationStep1_suffix_txt",suffix);
			}

			String dob = sc.getPriorDate("dd-MMM-yyyy", -22);
			String monMonthName = dob.split("-")[1]; // 3 chara equivalent of Month like Jan
			String monthNameFull = sc.convertToFullMonthName(monMonthName);
			tempRetval = sc.selectValue_byVisibleText("invitationStep1_dobMonth_dd",monthNameFull );
			Thread.sleep(1000);

			//Anand-3/1- application has changed the format of Date dropdown from 01 to 1

			String dobDate = dob.split("-")[0];
			Pattern pattern = Pattern.compile("[0].");//. represents single character  
			Matcher m = pattern.matcher(dobDate);  
			boolean b = m.matches();

			if(b){
				dobDate =	dobDate.replace("0","");
			}


			tempRetval = sc.selectValue_byVisibleText("invitationStep1_dobDay_dd", dobDate);
			tempRetval = sc.selectValue_byVisibleText("invitationStep1_dobYear_dd",dob.split("-")[2]);

			//				String ssn =Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN");
			String isSSNPROF = isProductPartOfOrdering("SSNPROF");
			String isCBSV = isProductPartOfOrdering("CBSV");
			String isLocator = isProductPartOfOrdering("LS");
			String isL3 = isProductPartOfOrdering("L3");

			sc.scrollIntoView("invitationStep1_gender_dd");
			tempRetval = sc.waitTillElementDisplayed("invitationStep1_noSSN_chk", 2);

			if(isSSNPROF.equalsIgnoreCase(Globals.KEYWORD_PASS) || isCBSV.equalsIgnoreCase(Globals.KEYWORD_PASS) || isLocator.equalsIgnoreCase(Globals.KEYWORD_PASS) || isL3.equalsIgnoreCase(Globals.KEYWORD_PASS) ){

				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Pass", "MER-6214-SSN required", "SSN Not required checkbox is not visible", 1);
				}else{

					sc.STAF_ReportEvent("Fail", "MER-6214-SSN required", "SSN Not required is visible", 1);
				}

			}else{
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

					sc.STAF_ReportEvent("Pass", "SSN Not required", "SSN Not required checkbox is visible", 1);
				}else{
					sc.STAF_ReportEvent("Fail", "SSN not required", "SSN Not required checkbox is NOT visible", 1);
				}
			}

			tempRetval = sc.selectValue_byVisibleText("invitationStep1_gender_dd", "Male");
			String phoneNo="9012121234";
			tempRetval = sc.setValueJsChange("invitationStep1_phone_txt",phoneNo );

			String emailID =Globals.fromEmailID;
			String emailIDinUI = driver.findElement(LocatorAccess.getLocator("invitationStep1_emailAddress_txt")).getAttribute("value");
			if(emailID.equalsIgnoreCase(emailIDinUI)){
				log.info("Email ID displayed in Invitation Ordering-STEP 1 is correct");
			}else{
				sc.STAF_ReportEvent("Fail", "Step 1", "Email ID displayed in Invitation Ordering-STEP 1 is not correct", 1);
				log.error("Email ID displayed in Invitation Ordering-STEP 1 is not correct.| Expected -"+emailID + "| Actual:- "+emailIDinUI);
			}


			driver.findElement(By.xpath("//*[@id='step1Form']/div[1]/div[2]/div[13]/div[2]/div[2]/input")).click();


			tempRetval = sc.waitTillElementDisplayed("invitationStep1_errorMessage_span", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Step 1", "SSN Validation Message is not displayed", 1);
			}else{
				String errorMsgUI = driver.findElement(LocatorAccess.getLocator("invitationStep1_errorMessage_span")).getText();
				String expMessage = "Please verify your ssn.";
				if(expMessage.equalsIgnoreCase(errorMsgUI)){
					sc.scrollIntoView("invitationStep1_errorMessage_span");
					sc.STAF_ReportEvent("Pass", "Step 1", "SSN Validation Message is displayed", 1);
				}else{
					sc.STAF_ReportEvent("Fail", "Step 1", "SSN Validation Message is not displayed", 1);
				}
			}

			sc.clickWhenElementIsClickable("volunteerHomepage_logout_link", timeOutinSeconds);
			Alert alert = driver.switchTo().alert(); 
			alert.accept(); 
			retval = Globals.KEYWORD_PASS;				




		}else{
			sc.STAF_ReportEvent("Fail", "Step 1", "Step 1 page has not been loaded" , 1);
			throw new Exception("Unable to complete STEP 1");
		}

		return retval;
	}
	

	/**************************************************************************************************
	 * Method to click on get verified link during api ordering 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clickOnGetVerified() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_PASS;
		String tempRetval = Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 10;
		/*
		tempRetval=sc.waitforElementToDisplay("volHomePage_getVerified_link",timeOutinSeconds);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", "Get Verified Page", "Page has loaded", 1);
			sc.clickWhenElementIsClickable("volHomePage_getVerified_link",(int)timeOutinSeconds );
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "Get Verified Page", "Page has NOT loaded", 1);
			throw new Exception("Volunteer Get Verified Page is not loaded");
		}
		*/
		return retval;

	}
	
	/**************************************************************************************************
	 * Method to enter good deed code and proceed to Ordering step1 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String enterCodeAndProceedToStep1() throws Exception{
		APP_LOGGER.startFunction("EnterCodeAndProceedToStep1");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;

		tempRetval=sc.waitforElementToDisplay("goodDeedPage_goodDeed_txt",timeOutinSeconds);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

			tempRetval = Globals.KEYWORD_FAIL;
			//GoodDeedCode column name depends upon the Env that its being executed
			String colName = Globals.Env_To_Execute_ON+"_GoodDeedCodeOrURL";
			String goodDeedCode = Globals.testSuiteXLS.getCellData_fromTestData(colName);
			tempRetval = sc.setValueJsChange("goodDeedPage_goodDeed_txt", goodDeedCode);

			sc.clickWhenElementIsClickable("goodDeedPage_continue_btn",timeOutinSeconds );

		}else{
			sc.STAF_ReportEvent("Fail", "GoodDeedCode", "Good Deed Page page is not loaded", 1);
			log.error("Method-CreateGoodDeedOrder | Good Deed Page page is not loaded " );
			throw new Exception("Good Deed Page page is not loaded");
		}


		return retval;		

	}
	
	/**************************************************************************************************
	 * Method to Verify Good deed page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String validateGoodDeedCodePage() throws Exception{
		APP_LOGGER.startFunction("validateGoodDeedCodePage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		String 	StepName = "Good deed page";
	
		int timeOutinSeconds = 20;

		tempRetval=sc.waitforElementToDisplay("goodDeedPage_goodDeed_txt",timeOutinSeconds);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

			tempRetval = Globals.KEYWORD_FAIL;
			//GoodDeedCode column name depends upon the Env that its being executed
			
			String colName = Globals.Env_To_Execute_ON+"_GoodDeedCodeOrURL";
			String goodDeedCode = Globals.testSuiteXLS.getCellData_fromTestData(colName);
			tempRetval = sc.setValueJsChange("goodDeedPage_goodDeed_txt", goodDeedCode);
			
            //Test to check if the place holder values on the right side fields on the GoodDeedCode Page are still visible even when disabled on entering a good deed code
			String FullName = driver.findElement(LocatorAccess.getLocator("goodDeedPage_fullName_txt")).getAttribute("placeholder");
			String EmailAddress = driver.findElement(LocatorAccess.getLocator("goodDeedPage_email_txt")).getAttribute("placeholder");
			String PhoneNo = driver.findElement(LocatorAccess.getLocator("goodDeedPage_phone_txt")).getAttribute("placeholder");
			String Organization = driver.findElement(LocatorAccess.getLocator("goodDeedPage_org_txt")).getAttribute("placeholder");
			
			if (FullName.equals("Full Name*") && EmailAddress.equals("E-Mail Address*") && PhoneNo.equals("Phone Number*") && Organization.equals("Organization*")  )
			{
				retval = Globals.KEYWORD_PASS;
				sc.STAF_ReportEvent("Pass", StepName, "Place holder values are correctly displayed", 1);
			}
			else{
				sc.STAF_ReportEvent("Fail", StepName, "Place holder values are not correctly displayed",1);
				throw new Exception("Incorrect place holder values in good deed code page");
			}
			//Test to check if the right side fields on the GoodDeedCode Page are disabled on entering a good deed code
			boolean  nameValue = driver.findElement(LocatorAccess.getLocator("goodDeedPage_fullName_txt")).isEnabled();
			boolean  emailValue = driver.findElement(LocatorAccess.getLocator("goodDeedPage_email_txt")).isEnabled();
			boolean  phoneValue = driver.findElement(LocatorAccess.getLocator("goodDeedPage_phone_txt")).isEnabled();
			boolean  orgValue = driver.findElement(LocatorAccess.getLocator("goodDeedPage_org_txt")).isEnabled();
			boolean  submitRequestValue = driver.findElement(LocatorAccess.getLocator("goodDeedPage_request_btn")).isEnabled();
			
			if ((nameValue == false) && (emailValue == false) && (phoneValue == false) && (orgValue == false) && (submitRequestValue == false) ) 
			{
				retval = Globals.KEYWORD_PASS;
				sc.STAF_ReportEvent("Pass", StepName, "Don't have a Good Deed Code Fields are  disabled", 1);
			}
			
			else{
				sc.STAF_ReportEvent("Fail", StepName, "Don't have a Good Deed Code Fields are not disabled",1);
				throw new Exception("Don't have a Good Deed Code Fields are not disabled");
			}
			
			driver.findElement(LocatorAccess.getLocator("goodDeedPage_goodDeed_txt")).clear();
			
			tempRetval =  Globals.KEYWORD_FAIL;
			
			String 	nameValue1 = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
			String 	emailValue1 = Globals.fromEmailID;
			String 	phoneValue1 = Globals.Volunteer_Phone;
			String 	orgValue1 = Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
			
			tempRetval = sc.setValueJsChange("goodDeedPage_fullName_txt", nameValue1);
			tempRetval = sc.setValueJsChange("goodDeedPage_email_txt", emailValue1);
			tempRetval = sc.setValueJsChange("goodDeedPage_phone_txt", phoneValue1);
			tempRetval = sc.setValueJsChange("goodDeedPage_org_txt", orgValue1);
			
			
			//Test to check if the place holder value of the gooddeedcode field on the GoodDeedCode Page is still visible even when disabled
			String GoodDeedCode = driver.findElement(LocatorAccess.getLocator("goodDeedPage_goodDeed_txt")).getAttribute("placeholder");
			if (GoodDeedCode.equals("Input GOOD DEED code here:") )
			{
				retval = Globals.KEYWORD_PASS;
				sc.STAF_ReportEvent("Pass", StepName, "Place holder values are correctly displayed", 1);
			}
			else{
				sc.STAF_ReportEvent("Fail", StepName, "Place holder values are not correctly displayed",1);
				throw new Exception("Incorrect place holder values in good deed code page");
			}
			//Test to check if the GoodDeedCode field on the GoodDeedCode Page is disabled on entering values on the right side of the page
			boolean  goodDeedCode1 = driver.findElement(LocatorAccess.getLocator("goodDeedPage_goodDeed_txt")).isEnabled();
			boolean  submitRequest = driver.findElement(LocatorAccess.getLocator("goodDeedPage_request_btn")).isEnabled();
			if ((goodDeedCode1 == false) && (submitRequest == true)) 
			{
				retval = Globals.KEYWORD_PASS;
				sc.STAF_ReportEvent("Pass", StepName, "Good Deed Code Field is disabled & Submit request button is enabled", 1);
			}
			else{
				sc.STAF_ReportEvent("Fail", StepName, " Good Deed Code Field is not disabled",1);
				throw new Exception("Good Deed Code Field is not disabled");
			}
			
			driver.findElement(LocatorAccess.getLocator("goodDeedPage_fullName_txt")).clear();
			driver.findElement(LocatorAccess.getLocator("goodDeedPage_email_txt")).clear();
			driver.findElement(LocatorAccess.getLocator("goodDeedPage_phone_txt")).clear();
			driver.findElement(LocatorAccess.getLocator("goodDeedPage_org_txt")).clear();
			
			//Test to check if the valid good deed code is entered continue button is enabled
			tempRetval =  Globals.KEYWORD_FAIL;
			String goodDeedCode2 = Globals.testSuiteXLS.getCellData_fromTestData(colName);
			tempRetval = sc.setValueJsChange("goodDeedPage_goodDeed_txt", goodDeedCode2);
			
			boolean  goodDeedCode3 = driver.findElement(LocatorAccess.getLocator("goodDeedPage_continue_btn")).isEnabled();
			
			if (goodDeedCode3 == true){
				retval = Globals.KEYWORD_PASS;
				sc.STAF_ReportEvent("Pass", StepName, "Good Deed Code Field is enabled", 1);
			}
			else{
				sc.STAF_ReportEvent("Fail", StepName, "Good Deed Code Field is not enabled",1);
				throw new Exception("Good Deed Code Field is not enabled");
			}
			sc.clickWhenElementIsClickable("goodDeedPage_continue_btn",timeOutinSeconds );

			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = isProductPartOfOrdering(VV_Products.ID.toString());
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){ 
				
				 ClientFacingApp.checkForIDAndProceed();//Changed by Andy for code re-useability in case ID confirm product is on the order
				 
			}else{
				tempRetval=sc.waitforElementToDisplay("invitationStep1_fName_txt",timeOutinSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

					sc.STAF_ReportEvent("Pass", "GoodDeedCode", "Code used successfully-"+goodDeedCode, 1);
					retval = Globals.KEYWORD_PASS;
				}else{
					sc.STAF_ReportEvent("Fail", "GoodDeedCode", "Unable to create order using  good deed code -"+goodDeedCode, 1);
					log.error("Method-CreateGoodDeedOrder | Ordering STEP 1 is not displayed");
					throw new Exception("Ordering STEP 1 is not displayed");
				}

			}

			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval =  sc.waitforElementToDisplay("volunteerHomepage_logout_link", 20 );

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Volunteer Logout", "Log Out link is not Displayed", 1);
				throw new Exception("Log Out link is not Displayed");
			}
			
			sc.clickWhenElementIsClickable("volunteerHomepage_logout_link", 20);
			
			Alert alert = driver.switchTo().alert(); 
			alert.accept();
			
			tempRetval =  sc.waitforElementToDisplay("login_orgUsername_txt", 20 );

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Volunteer Logout", "Unable to log out successfully", 1);

			}
			else{
				sc.STAF_ReportEvent("Pass", "Volunteer Logout", "Logged Out successfully", 1);
				retval = Globals.KEYWORD_PASS;
			}
			
		}
		
		else{
			sc.STAF_ReportEvent("Fail", "GoodDeedCode", "Good Deed Page page is not loaded", 1);
			log.error("Method-CreateGoodDeedOrder | Good Deed Page page is not loaded " );
			throw new Exception("Good Deed Page page is not loaded");
		}


		return retval;		

	}


	/**************************************************************************************************
	 * Method to click on Get Verified tab on volunteer homepage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clickGetVerified() throws Exception{
		APP_LOGGER.startFunction("clickGetVerified");
		String retval = Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;

		if (sc.waitforElementToDisplay("volHomePage_getVerified_link",timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){

			//			driver.findElement(LocatorAccess.getLocator("volHomePage_getVerified_link")).click();
			
			sc.clickWhenElementIsClickable("volHomePage_getVerified_link",timeOutinSeconds );

			updateVolunteerNameInTestData();

			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "Get Verified ", " Page has not loaded properly",1);
			throw new Exception("Unable to create Good Deed URL Order");
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify API ordeirng Step1-Used in functiona testing phase only
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyAPIOrderStep1Functional() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;
		String tempRetval=Globals.KEYWORD_FAIL;

		String suffix=null;

		tempRetval=sc.waitforElementToDisplay("invitationStep1_fName_txt", timeOutinSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

			XMLUtils inputXML = new XMLUtils(Globals.currentAPIOrderRequestPath);
			String expectedValue;

			expectedValue = inputXML.getXMLNodeValByTagName("oa:GivenName");
			tempRetval = verifyXMLValWithUIFieldVal(expectedValue,"invitationStep1_fName_txt","VolunteerFirstName");

			expectedValue = inputXML.getXMLNodeValByTagName("FamilyName");
			tempRetval = verifyXMLValWithUIFieldVal(expectedValue,"invitationStep1_lName_txt","VolunteerLastName");

			expectedValue = inputXML.getXMLNodeValByTagName("MiddleName");
			tempRetval = verifyXMLValWithUIFieldVal(expectedValue,"invitationStep1_midName_txt","VolunteerMidName");

			suffix =Globals.testSuiteXLS.getCellData_fromTestData("VolunterrSuffix");
			if(suffix != null ){
				tempRetval = sc.setValueJsChange("invitationStep1_suffix_txt",suffix);
			}
			String dob = inputXML.getXMLNodeValByTagName("FormattedDateTime"); //MM/dd/yyyy
			String monthNumber = dob.split("/")[0]; 

			Pattern pattern = Pattern.compile("[0].");//. represents single character  
			Matcher m=null;
			boolean b = false;
			m = pattern.matcher(monthNumber); 
			b = m.matches();
			if(b){
				monthNumber =	monthNumber.replace("0","");
			}


			//				String monthName = sc.convertIntToFullMonthName(monMonthName);
			tempRetval = verifyXMLValWithUIFieldVal(monthNumber,"invitationStep1_dobMonth_dd","DOB-Month");



			//Anand-3/1- application has changed the format of Date dropdown from 01 to 1

			String dobDate = dob.split("/")[1];

			m = pattern.matcher(dobDate);  
			b= false;
			b = m.matches();

			if(b){
				dobDate =	dobDate.replace("0","");
			}
			tempRetval = verifyXMLValWithUIFieldVal(dobDate,"invitationStep1_dobDay_dd","DOB-Day");
			tempRetval = verifyXMLValWithUIFieldVal(dob.split("/")[2],"invitationStep1_dobYear_dd","DOB-Year");


			String ssn =inputXML.getXMLNodeValByTagName("PersonLegalID");
			sc.scrollIntoView("invitationStep1_gender_dd");

			String isSSNPROF = isProductPartOfOrdering("SSNPROF");
			String isCBSV = isProductPartOfOrdering("CBSV");
			String isLocator = isProductPartOfOrdering("LS");
			String isL3 = isProductPartOfOrdering("L3");
			tempRetval = sc.waitTillElementDisplayed("invitationStep1_noSSN_chk", 2);

			if(isSSNPROF.equalsIgnoreCase(Globals.KEYWORD_PASS) || isCBSV.equalsIgnoreCase(Globals.KEYWORD_PASS) || isLocator.equalsIgnoreCase(Globals.KEYWORD_PASS) || isL3.equalsIgnoreCase(Globals.KEYWORD_PASS) ){

				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Pass", "MER-6214-SSN required", "SSN Not required checkbox is not visible", 1);
				}else{

					sc.STAF_ReportEvent("Fail", "MER-6214-SSN required", "SSN Not required is visible", 1);
				}

				tempRetval = verifyXMLValWithUIFieldVal(ssn.substring(0, 3),"invitationStep1_ssn1_txt","SSN-1");
				tempRetval = verifyXMLValWithUIFieldVal(ssn.substring(3, 5),"invitationStep1_ssn2_txt","SSN-1");
				tempRetval = verifyXMLValWithUIFieldVal(ssn.substring(5, 9),"invitationStep1_ssn3_txt","SSN-1");

			}else{
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

					sc.STAF_ReportEvent("Pass", "SSN Not required", "SSN Not required checkbox is visible", 1);
					if(ssn == null ){
						sc.STAF_ReportEvent("Pass", "Step 1", "SSN Not Entered for ordering", 1);
						tempRetval = sc.checkCheckBox("invitationStep1_noSSN_chk");
					}
				}else{
					sc.STAF_ReportEvent("Fail", "SSN not required", "SSN Not required checkbox is NOT visible", 1);
				}
			}


			driver.findElement(LocatorAccess.getLocator("invitationStep1_ssn1_txt")).clear();
			driver.findElement(LocatorAccess.getLocator("invitationStep1_ssn2_txt")).clear();
			driver.findElement(LocatorAccess.getLocator("invitationStep1_ssn3_txt")).clear();

			String genderVal =inputXML.getXMLNodeValByTagName("GenderCode");
			if (genderVal.equalsIgnoreCase("Male")){
				expectedValue = "2";
			}else {
				expectedValue = "1";
			}
			tempRetval = verifyXMLValWithUIFieldVal(expectedValue,"invitationStep1_gender_dd","GenderCode");

			String phoneNo="9012121234";
			tempRetval = sc.setValueJsChange("invitationStep1_phone_txt",phoneNo );


			String emailID =inputXML.getXMLNodeValByTagName("oa:URI");
			tempRetval = verifyXMLValWithUIFieldVal(emailID,"invitationStep1_emailAddress_txt","EmailAddress");


			//Custom Field Handling
			verifyAndSetCustomFields();


			//Alias Data Entry
			String sNoOfAlias = sc.testSuiteXLS.getCellData_fromTestData("NoOfAlias");
			int iNoOfAlias = Integer.parseInt(sNoOfAlias);

			tempRetval=sc.waitforElementToDisplay("invitationStep1_removeAlias_btn",3);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.clickWhenElementIsClickable("invitationStep1_removeAlias_btn", 3);
			}

			populateAliasSectionData(iNoOfAlias);

			//alacarte section verification would only appear if they are part of ordering
			alacarteSectionVerification();

			driver.findElement(By.xpath("//*[@id='step1Form']/div[1]/div[2]/div[13]/div[2]/div[2]/input")).click();

			tempRetval = sc.waitTillElementDisplayed("invitationStep1_errorMessage_span", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Step 1", "SSN Validation Message is not displayed", 1);
			}else{
				String errorMsgUI = driver.findElement(LocatorAccess.getLocator("invitationStep1_errorMessage_span")).getText();
				String expMessage = "Please verify your ssn.";
				if(expMessage.equalsIgnoreCase(errorMsgUI)){
					sc.scrollIntoView("invitationStep1_errorMessage_span");
					sc.STAF_ReportEvent("Pass", "Step 1", "SSN Validation Message is displayed", 1);
				}else{
					sc.STAF_ReportEvent("Fail", "Step 1", "SSN Validation Message is not displayed", 1);
				}
			}

			sc.clickWhenElementIsClickable("volunteerHomepage_logout_link", timeOutinSeconds);
			Alert alert = driver.switchTo().alert(); 
			alert.accept(); 
			retval = Globals.KEYWORD_PASS;



		}else{
			sc.STAF_ReportEvent("Fail", "Step 1", "Step 1 page has not been loaded" , 1);
		}


		return retval;
	}

	/**************************************************************************************************
	 * Method to click on Create client order in Volunteer dashboard page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clickOnCreateClientOrder() throws Exception{
		APP_LOGGER.startFunction("clickOnCreateClientOrder");
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 60;

		tempRetval = sc.waitforElementToDisplay("volDashboard_orderBackgroundCheck_btn", timeOutInSeconds);
		sc.STAF_ReportEvent("Pass", "Volunteer Dashboard", "Volunteer Page has been loaded" , 1);

		sc.clickWhenElementIsClickable("volDashboard_orderBackgroundCheck_btn",timeOutInSeconds);
		sc.clickWhenElementIsClickable("volDashboard_placeAnOrder_btn",timeOutInSeconds);
		//clear the order ids present in the test data
		Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", ""); 
		Globals.testSuiteXLS.setCellData_inTestData("SWestOrderID", "");


		tempRetval = sc.waitforElementToDisplay("clientStep1_choosePosition_dd", timeOutInSeconds);
		if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", "Step 1 ", "Position selection page has been loaded" , 1);
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "Step 1 ", "Position selection page has NOT been loaded" , 1);
			throw new Exception("Step 1 Position selection page has NOT been loaded" );
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify Step 2 for client ordering - used in functional testing
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clientOrderingStep2Functional() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 10;
		String fname;
		String lname;
		String midName;
		String suffix;

		tempRetval = sc.waitforElementToDisplay("clientStep2_firstName_txt", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

			fname = sc.updateAndFetchRuntimeValue("VolunteeFName_runtimeUpdateFlag","volunteerFName",15);
			lname = sc.updateAndFetchRuntimeValue("VolunteeLName_runtimeUpdateFlag","volunteerLName",15);

			fname=Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
			tempRetval = sc.setValueJsChange("clientStep2_firstName_txt",fname);
			midName =Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");

			if(midName != null ){
				tempRetval = sc.setValueJsChange("clientStep2_middleName_txt",midName);
			}else{
				tempRetval = sc.uncheckCheckBox("clientStep2_middleName_chk");
			}

			tempRetval = sc.setValueJsChange("clientStep2_lastName_txt",lname);

			suffix =Globals.testSuiteXLS.getCellData_fromTestData("VolunterrSuffix");
			if(suffix != null ){
				tempRetval = sc.setValueJsChange("clientStep2_suffix_txt",suffix);
			}


			String dob = sc.getPriorDate("dd-MMM-yyyy", -22);
			tempRetval = sc.selectValue_byVisibleText("clientStep2_dobMonth_dd", dob.split("-")[1]);
			tempRetval = sc.setValueJsChange("clientStep2_dobDay_txt", dob.split("-")[0]);
			tempRetval = sc.setValueJsChange("clientStep2_dobYear_txt", dob.split("-")[2]);

			//			String ssn =Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN");

			sc.scrollIntoView("clientStep2_gender_dd");
			String isSSNPROF = isProductPartOfOrdering("SSNPROF");
			String isCBSV = isProductPartOfOrdering("CBSV");
			String isLocator = isProductPartOfOrdering("LS");
			String isL3 = isProductPartOfOrdering("L3");

			tempRetval = sc.isSelected("clientStep2_SSN_chk");
			String tempRetval2 = sc.isEnabled("clientStep2_SSN_chk");

			if(isSSNPROF.equalsIgnoreCase(Globals.KEYWORD_PASS) || isCBSV.equalsIgnoreCase(Globals.KEYWORD_PASS) || isLocator.equalsIgnoreCase(Globals.KEYWORD_PASS) || isL3.equalsIgnoreCase(Globals.KEYWORD_PASS) ){

				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Pass", "MER-6214-SSN required", "SSN Not required checkbox is cHECKED ", 1);
				}else{

					sc.STAF_ReportEvent("Fail", "MER-6214-SSN required", "SSN Not required is not Checked", 1);
				}

				if(tempRetval2.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Pass", "MER-6214-SSN required", "SSN Not required checkbox is Disabled ", 1);
				}else{

					sc.STAF_ReportEvent("Fail", "MER-6214-SSN required", "SSN Not required is not Disabled", 1);
				}

			}else{
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){

					sc.STAF_ReportEvent("Pass", "SSN Not required", "SSN Not required checkbox is not checked", 1);

				}else{
					sc.STAF_ReportEvent("Fail", "SSN not required", "SSN Not required checkbox is checked", 1);
				}

				if(tempRetval2.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Pass", "MER-6214-SSN required", "SSN Not required checkbox is Enabled ", 1);
				}else{

					sc.STAF_ReportEvent("Fail", "MER-6214-SSN required", "SSN Not required is not Enabled", 1);
				}
			}


			String phoneNo="9012121234";
			tempRetval = sc.selectValue_byVisibleText("clientStep2_gender_dd", "Male");
			tempRetval = sc.setValueJsChange("clientStep2_phoneNo_txt",phoneNo );

			String emailID =Globals.fromEmailID;
			if(emailID != null ){
				tempRetval = sc.setValueJsChange("clientStep2_emailAddress_txt",emailID);
			}else{
				tempRetval = sc.uncheckCheckBox("clientStep2_emailAddress_chk");
			}

			//check whether MVR is part of ordering
			tempRetval =  Globals.KEYWORD_FAIL;
			tempRetval = isProductPartOfOrdering("MVR");
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "Step 2", "MVR is part of ordering" , 0);

				tempRetval = sc.selectValue_byVisibleText("clientStep2_mvrState_dd", Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_State"));
				Thread.sleep(1000);
				tempRetval = sc.setValueJsChange("clientStep2_mvrLicense_txt", Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_License"));
				tempRetval = sc.setValueJsChange("clientStep2_mvrFirstName_txt", Globals.Volunteer_DL_Name);
				tempRetval = sc.setValueJsChange("clientStep2_mvrMidName_txt", Globals.Volunteer_DL_Name);
				tempRetval = sc.setValueJsChange("clientStep2_mvrLastName_txt", Globals.Volunteer_DL_Name);
			}
			//Custom Field Handling
			setCustomFields_ClientOrdering();

			sc.clickWhenElementIsClickable("clientStep2_nextStep_btn", (int)timeOutInSeconds);

			String expectdText = "SSN is required (or uncheck)";
			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
			String stepName = "MER-6214-SSN REQUIRED";

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
				retval= Globals.KEYWORD_PASS;
			}else{	
				sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Expected - "+expectdText, 1);
			}
		}else{
			sc.STAF_ReportEvent("Fail", "MER-6214-SSN REQUIRED", "Page not loaded",1);

		}
		sc.clickWhenElementIsClickable("clientBGDashboard_logout_link", (int)timeOutInSeconds);
		Alert alert = driver.switchTo().alert(); 
		alert.accept(); 
		retval = Globals.KEYWORD_PASS;
		return retval;
	}

	/**************************************************************************************************
	 * Method to log out a logged in volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String volunteerLogOut() throws Exception {
		APP_LOGGER.startFunction("VolunteerLogOut");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		String stepName = "Volunteer Logout";
		tempretval =  sc.waitforElementToDisplay("volunteerHomepage_logout_link", timeOutInSeconds );

		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Log Out link is not Displayed", 1);
			throw new Exception("Log Out link is not Displayed");
		}
		sc.clickWhenElementIsClickable("volunteerHomepage_logout_link", timeOutInSeconds);
		tempretval =  sc.waitforElementToDisplay("login_orgUsername_txt", timeOutInSeconds );

		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Unable to log out successfully", 1);

		}else{
			sc.STAF_ReportEvent("Pass", stepName, "Logged Out successfully", 1);
			retval = Globals.KEYWORD_PASS;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to create a order using upload ordering workflow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String CreateUploadOrder() throws Exception{
		APP_LOGGER.startFunction("CreateUploadOrder");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		long  timeOutInSeconds =  30;

		HashMap<String,String> volDashboard_beforeOrderCreation = ClientFacingApp.fetchVolunteerDashboardCount();

		String updatedFilePath = ClientFacingApp.updateUploadOrderingCSVFile();

		tempRetval = sc.waitforElementToDisplay("volDashboard_upload_btn", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "VVOU139 - Verify the presence of the Upload Button on the Volunteer dashboard page.", "Upload Button is not visible" ,1);
			throw new Exception("Upload button is not visible");
		}



		sc.STAF_ReportEvent("Pass", "VVOU139 - Verify the presence of the Upload Button on the Volunteer dashboard page.", "Upload Button is present" ,1);
		sc.clickWhenElementIsClickable("volDashboard_upload_btn", (int)timeOutInSeconds);


		tempRetval = sc.waitforElementToDisplay("uploadOrdering_browse_span", timeOutInSeconds);
		sc.clickWhenElementIsClickable("uploadOrdering_close_btn", (int)timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("volDashboard_upload_btn", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "VVOU147 - Verify the Close button functionality on the upload volunteers pop up.", "Upload Button is not visible after Close had ben clicked" ,1);
			throw new Exception("Upload button is not visible");
		}

		sc.STAF_ReportEvent("Pass", "VVOU147 - Verify the Close button functionality on the upload volunteers pop up.", "Upload Button is visible after Close had ben clicked" ,1);
		sc.clickWhenElementIsClickable("volDashboard_upload_btn", (int)timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("uploadOrdering_browse_span", timeOutInSeconds);


		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "VVOU140 - Verify the presence of the Browse button on the upload volunteers pop up.", "Browse Button is not visible" ,1);
			throw new Exception("Browse button is not visible");
		}

		sc.STAF_ReportEvent("Pass", "VVOU140 - Verify the presence of the Browse button on the upload volunteers pop up.", "Browse Button is visible" ,1);
		if(sc.BrowserType.equalsIgnoreCase("mozilla")||sc.BrowserType.equalsIgnoreCase("firefox")){
			WebElement objToBeClicked = sc.createWebElement("volDashboard_clickHere_link");
			String fileName = "Client Instructions - Volunteer Upload Instructions.pdf";
			String stepName = "VVerify Click here link url-Upload File Instruction";
			String absPath2BeSaved = Globals.currentPlatform_Path+"\\Snapshots\\"+Globals.TestCaseID+"_"+fileName;
			sc.clickAndDownloadFile(objToBeClicked, absPath2BeSaved, fileName, stepName);
       }else{
			
			String winHandleBefore = driver.getWindowHandle();
			sc.clickWhenElementIsClickable("volDashboard_clickHere_link", (int)timeOutInSeconds);
			for(String winHandle : driver.getWindowHandles()){
				driver.switchTo().window(winHandle);
			}
			String url = driver.getCurrentUrl();
			String []env=Globals.Env_To_Execute_ON.split("_"); 
			String linkEnv=env[1];
			String amzezonUrl=null;
			if(linkEnv.startsWith("QA")){
				amzezonUrl="dev";
			}else if(linkEnv.equalsIgnoreCase("PROD")){
				amzezonUrl="prod";
			}
			String instructionurl = "https://s3.amazonaws.com/sterling-"+amzezonUrl+"-vv/pdf/Client%20Instructions%20-%20Volunteer%20Upload%20Instructions.pdf";
			String instructionUrlMoz="https://s3.amazonaws.com/sterling-"+amzezonUrl+"-vv/pdf/Client Instructions - Volunteer Upload Instructions.pdf";
			
			if(instructionurl.contains(url) || (instructionUrlMoz.contains(url))){
				sc.STAF_ReportEvent("Pass", "Verify Click here link url",  "Upload instruction PDF url is correct",3);
			}
			else{
				sc.STAF_ReportEvent("Fail", "Verify Click here link url",  "Upload instruction PDF url is not correct",0);
			}
//					String absFilePath = Globals.currentRunPath+"\\Client Instructions - Volunteer Upload Instructions.pdf";
//					File reportFile =  new File(absFilePath);
//					int counter = 0;
//					while (counter<=60) {
//						if(reportFile.exists()){
//							break;
//						}else{
//							Thread.sleep(1000);
//							counter++;
//						}
//					}
//					if(reportFile.exists() && reportFile.length() > 0) {
//						String destPath = Globals.currentPlatform_Path+"\\Snapshots\\Client Instructions - Volunteer Upload Instructions.pdf";
//						File destFile = new File(destPath);
//						FileUtils.moveFile(reportFile, destFile);
//						Globals.Result_Link_FilePath = destPath;
//						sc.STAF_ReportEvent("Pass", "VVOU142-Verify presence of Click here link",  "Upload instruction PDF export successfull",3);
//
//					}else{
//						sc.STAF_ReportEvent("Fail", "VVOU142-Verify presence of Click here link", "Upload instructions PDFReport export unsuccessfull",0);
//					}
					driver.close();
					driver.switchTo().window(winHandleBefore);
			}
			/* Internet Expllorer 11 else{
			sc.clickElementUsingJavaScript("volDashboard_clickHere_link");
				//JavascriptExecutor js = (JavascriptExecutor)driver;
				//js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';","volDashboard_clickHere_link");
				//sc.clickWhenElementIsClickable("volDashboard_clickHere_link",(int) timeOutInSeconds);
					Thread.sleep(3000);
					String filePath = Globals.currentRunPath;
//					String fileName = "\\Client Instructions - Volunteer Upload Instructions.pdf";
//					String tempval=sc.fileDownloadUsingAutoIt(filePath, fileName);
//					if(tempval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
//						
//						sc.STAF_ReportEvent("Pass", "VVOU142-Verify presence of Click here link",  "Upload instruction PDF export successfull",3);
//						
//					}
//					
//					else {
//						
//						sc.STAF_ReportEvent("Fail", "VVOU142-Verify presence of Click here link", "Upload instructions PDF export unsuccessful",0);
//					}

			}*/

		driver.findElement(LocatorAccess.getLocator("uploadOrdering_browse_file")).sendKeys(updatedFilePath);

		String textInUI =driver.findElement(LocatorAccess.getLocator("uploadOrdering_browse_file")).getAttribute("value");
		Thread.sleep(1000);

		if(textInUI.contains(Globals.TestCaseID +"_ModifiedUpload.csv")){
			Globals.Result_Link_FilePath = updatedFilePath;
			sc.STAF_ReportEvent("Pass", "Upload ordering", "File Successfully uploaded" ,3);
			Globals.Result_Link_FilePath ="";
		}else{
			sc.STAF_ReportEvent("Fail", "Upload ordering", "File uploaded failed-Mismatch between uploaded file and file name being displayed" ,1);
			throw new Exception("unbale to upload csv file");
		}

//		driver.findElement(LocatorAccess.getLocator("uploadOrdering_sendInvitation_rdb")).click();
		sc.scrollIntoViewUsingJavaScript("uploadOrdering_sendInvitation_rdb");
		sc.clickElementUsingJavaScript("uploadOrdering_sendInvitation_rdb");
		
		String positionName= Globals.testSuiteXLS.getCellData_fromTestData("PositionName") +" - "+ Globals.testSuiteXLS.getCellData_fromTestData("PricingName");
		tempRetval = sc.selectValue_byVisibleText("uploadOrdering_position_dd", positionName );

		String customEmailMessage = sc.updateAndFetchRuntimeValue("CustomEmailMsg_runtimeUpdateFlag","CustomEmailMessage",45);
		tempRetval = sc.setValueJsChange("uploadOrdering_emailMessage_txt", customEmailMessage);

//		sc.clickWhenElementIsClickable("uploadOrdering_sendInvitation_btn", (int)timeOutInSeconds);
		sc.scrollIntoViewUsingJavaScript("uploadOrdering_sendInvitation_btn");
		sc.clickElementUsingJavaScript("uploadOrdering_sendInvitation_btn");
		
		sc.waitforElementToDisplay("uploadOrdering_noOfVolunteersUploaded_txt", timeOutInSeconds);
		String volunteerUploadCount = driver.findElement(LocatorAccess.getLocator("uploadOrdering_noOfVolunteersUploaded_txt")).getText();
		int volCount = Integer.parseInt(volunteerUploadCount.replace("Candidate(s) uploaded successfully.", "").trim());

		WebElement errorMessages = sc.createWebElement("uploadOrdering_errorMessages_txt"); 
		List<WebElement> errorList = errorMessages.findElements(By.xpath("./li"));

		FileReader fr = new FileReader(Globals.TestDir +"\\src\\test\\Resources\\refFile\\UploadOrderingError.txt");
		BufferedReader br = new BufferedReader(fr);
		String actualErrorMessage;
		String expectedErrorMessage;


		for(int i=0;i<errorList.size();i++){
			actualErrorMessage = errorList.get(i).getText();
			expectedErrorMessage = br.readLine().trim();
			if(actualErrorMessage.equalsIgnoreCase(expectedErrorMessage)){
				sc.STAF_ReportEvent("Pass", "Upload Ordering", "Error Message is as Expected-"+actualErrorMessage, 1);
			}else{
				sc.scrollIntoView(errorList.get(i));
				sc.STAF_ReportEvent("Fail", "Upload Ordering", "Error Message is NOT as Expected @ line-"+i+" --Expected-"+expectedErrorMessage+"Actual-"+actualErrorMessage, 1);

			}

		}
		br.close();

		int expectedVolCount = 8;
		if(volCount == expectedVolCount){
			sc.STAF_ReportEvent("Pass", "Upload Ordering", "Successfully uploaded Volunteers.Count = "+expectedVolCount, 0);
			retval=Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "Upload Ordering", "Uploaded volunteer count mismatch.Expected= "+expectedVolCount+ "Actual="+volCount, 0);
		}
		sc.clickWhenElementIsClickable("uploadordering_close_btn", (int)timeOutInSeconds);

		ClientFacingApp.navigateVolunteerDashboard();
		HashMap<String,String> volDashboard_AfterOrderCreation = ClientFacingApp.fetchVolunteerDashboardCount();
		ClientFacingApp.verifyVolunteerDashboard(volDashboard_beforeOrderCreation,volDashboard_AfterOrderCreation,"OpenInvitation",1);
		ClientFacingApp.verifyVolunteerDashboard(volDashboard_beforeOrderCreation,volDashboard_AfterOrderCreation,"ViewAllVolunteers",expectedVolCount);
		ClientFacingApp.verifyVolunteerDashboard(volDashboard_beforeOrderCreation,volDashboard_AfterOrderCreation,"NoOrderPlaced",expectedVolCount-1);
		return retval;
	}


	/**************************************************************************************************
	 * Method to post an order request xml for VV Api ordering
	 * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
	 * @author aunnikrishnan
	 * @created
	 * @LastModifiedBy
	 ***************************************************************************************************/

	public static String postOrderRequestXML() throws Exception {
		APP_LOGGER.startFunction("PostOrderRequest");
		String retval	=	Globals.KEYWORD_FAIL;
		String tempRetval	=	Globals.KEYWORD_FAIL;


		try {

			String currentPlatform_Path 		=	 Globals.currentRunPath+"\\"+Globals.CurrentPlatform;

			Globals.vvInputReferenceXML			=	Globals.TestDir + "\\src\\test\\Resources\\refFile\\API_Request.xml";	 
			Globals.currentPlatform_XMLResults	= 	currentPlatform_Path+"\\"+"XML_Results";
			sc.mkDirs(Globals.currentPlatform_XMLResults);


			Globals.VV_API_UserName		=	Globals.testSuiteXLS.getCellData_fromTestData("APIUserName");
			Globals.VV_API_UserPwd		=   Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");

			String fieldLevelValidationReq = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");

			if (fieldLevelValidationReq.equalsIgnoreCase("Yes")){

                tempRetval                 =        InhousePortal.validateOrderRequestFields("API");
                log.debug("Field Level Validation during Order request is performed.Result-"+tempRetval);
          }

			//update volunteer details based on test data
			HashMap<String, String> NodeListVals = new HashMap<String, String>();

			//GoodDeedCode column name depends upon the Env that its being executed
			String colName = Globals.Env_To_Execute_ON+"_GoodDeedCodeOrURL";
			String goodDeedCode = Globals.testSuiteXLS.getCellData_fromTestData(colName);
			NodeListVals.put("PackageID", goodDeedCode);

			String applicantID = sc.runtimeGeneratedStringValue(10);
			NodeListVals.put("ScreeningSubjectID", applicantID);

			updateVolunteerNameInTestData();

			String fname=null;
			String lname=null;

			fname = sc.testSuiteXLS.getCellData_fromTestData("volunteerFName");
			lname = sc.testSuiteXLS.getCellData_fromTestData("volunteerLName");

			NodeListVals.put("oa:GivenName", fname);

			NodeListVals.put("FamilyName", lname);

			NodeListVals.put("oa:URI", Globals.fromEmailID);

			String midName ="";
			midName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");
			if(midName.isEmpty() || midName == null){
				midName ="";
			}
			NodeListVals.put("MiddleName", midName);

			String addressLine =Globals.Volunteer_AddressLine;
			String city=Globals.Volunteer_City;

			NodeListVals.put("oa:PostalCode", Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode"));
			NodeListVals.put("oa:CityName", city);
			NodeListVals.put("oa:LineOne", addressLine);
			NodeListVals.put("oa:LineTwo", addressLine);
			
			int noOfyears=0;
			String dobCategory =Globals.testSuiteXLS.getCellData_fromTestData("VolDOBCategory");
			if(dobCategory.equalsIgnoreCase("GT than 18")){
				noOfyears = -22;
			}else if(dobCategory.equalsIgnoreCase("GT 14 Less than 18")){
				noOfyears = -16;
			}else if(dobCategory.equalsIgnoreCase("Less Than 14")){
				noOfyears = -13;
			}
			String dob = sc.getPriorDate("MM/dd/yyyy", noOfyears);
			NodeListVals.put("FormattedDateTime", dob);

			NodeListVals.put("PersonLegalID", Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN"));

			String stateCode = sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("AddressState"));

			NodeListVals.put("oa:CountrySubDivisionCode", stateCode);
			NodeListVals.put("CountryCode", Globals.testSuiteXLS.getCellData_fromTestData("CountryCode"));
			NodeListVals.put("GenderCode", "Male");

			XMLUtils  orderRequestXML 		= 	new XMLUtils(Globals.vvInputReferenceXML, Globals.getEnvPropertyValue("SoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);

			for (String node: NodeListVals.keySet()){

				String key =node.toString();
				String value = NodeListVals.get(node).toString();  
				orderRequestXML.updatedXMLNodeValueByTagName(key,value);


			}

			String PrevzipcodeLocator="//Address[@currentAddressIndicator='false']/PostalCode";
			String PrevstateLocator="//Address[@currentAddressIndicator='false']/CountrySubDivisionCode";
			String PrecountrycdLocator="//Address[@currentAddressIndicator='false']/CountryCode";

			String prevAddressStateCode = sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("PrevAddrState"));
			String prevAddressCountryCode = sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("CountryCode"));
			orderRequestXML.updatedXMLNodeValueByXPATH(PrevstateLocator, prevAddressStateCode);
			//orderRequestXML.updatedXMLNodeValueByXPATH(PrecountrycdLocator, prevAddressCountryCode);
			orderRequestXML.updatedXMLNodeValueByXPATH(PrevzipcodeLocator, Globals.testSuiteXLS.getCellData_fromTestData("PrevAddZipCode"));

			String updatedXMlFileName = Globals.TestCaseID +"_Request.xml";
			orderRequestXML.saveXMLDOM2File(updatedXMlFileName);
			Globals.currentAPIOrderRequestPath = Globals.currentPlatform_XMLResults + "\\"+ updatedXMlFileName;
			String updatedXMLFilePath 	= Globals.currentAPIOrderRequestPath;

			XMLUtils updatedXMLFile 	= new XMLUtils(updatedXMLFilePath, Globals.getEnvPropertyValue("SoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);
			updatedXMLFile.postXML();	

			Globals.Result_Link_FilePath = updatedXMLFilePath;
			sc.STAF_ReportEvent("Pass", "Post Order Request", "Request XML used,stored at XML_results folder.FileName-"+updatedXMlFileName, 3);

			String responseFilePath =	 Globals.currentPlatform_XMLResults + "\\" + Globals.TestCaseID +"_Response.xml";

			tempRetval				=	updatedXMLFile.saveXMLString(responseFilePath);

			Globals.Result_Link_FilePath = responseFilePath;
			sc.STAF_ReportEvent("Pass", "Order Request Ack", "Response XML stored at XML_results folder.FileName--"+Globals.TestCaseID +"_Response.xml", 3);
			tempRetval				=	InhousePortal.verifyOrderAcknowledgment(responseFilePath,applicantID);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Order Request Ack", "Successsful Response was not received", 3);
				return Globals.KEYWORD_FAIL;
			}

			Globals.Result_Link_FilePath = "";
			retval					= 	Globals.KEYWORD_PASS;
			//Notification message validation
			XMLUtils orResponseXML = new XMLUtils(responseFilePath);
            String npnOrderid=orResponseXML.getXMLNodeValByTagName("q1:DocumentID");
            String ntMsgAPilog=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
            String ntMsgnwapiendpt=Globals.testSuiteXLS.getCellData_fromTestData("APIRetrievalNotifyResponseNewEndpoint");
            if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
            	
	            if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") ){
	            	//sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
	            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
	            	Queryforapilog(npnOrderid,"New","Blank","NoNo");            	
	            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
	            	Queryforapilog(npnOrderid,"New","Blank","YesNo");
	            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
	            	Queryforapilog(npnOrderid,"New","Blank","NoYes");
	            	getOrderRequestXML("New");
	            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
	            	Queryforapilog(npnOrderid,"New","Blank","YesYes");
	            	getOrderRequestXML("New");
	            }
            }
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "Post XML", "Exception occurred during XML Post-Exception - "+ e.toString(),0);
			log.error("Exception occurred in PostOrderRequestXML .  | "+e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
     * Method to post an order request xml of negative scenario for VV Seamless ordering
     * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
     * @author psave
     * @created
     * @LastModifiedBy
     ***************************************************************************************************/

     public static String negativeValidationSeamless() throws Exception {
            APP_LOGGER.startFunction("PostOrderRequest");
            String retval =      Globals.KEYWORD_FAIL;
            String tempRetval    =      Globals.KEYWORD_FAIL;
 
            try {

                   String currentPlatform_Path              =      Globals.currentRunPath+"\\"+Globals.CurrentPlatform;

                          
                   String fldSm                             =      Globals.testSuiteXLS.getCellData_fromTestData("Seamless");
                                     
                   if(fldSm.equalsIgnoreCase("SM")){
                	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless"+"\\"+"Negative Validation"; 
                	   Globals.vvInputReferenceXML              =      Globals.TestDir + "\\src\\test\\Resources\\refFile\\Seamless_API_Request.xml";
                   }else if(fldSm.equalsIgnoreCase("SM_Cons_EnSSN")) {
                	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless_Consent_EncrySSN"+"\\"+"Negative Validation"; 
                	   Globals.vvInputReferenceXML              =      Globals.TestDir + "\\src\\test\\Resources\\refFile\\Seamless_Consent_API_Request.xml";
                   }else if(fldSm.equalsIgnoreCase("N/A") || fldSm.equalsIgnoreCase("") || fldSm.isEmpty()){
                	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Negative Validation";               	   
                   }
                   
                   sc.mkDirs(Globals.currentPlatform_XMLResults);


                   Globals.VV_API_UserName           =       Globals.testSuiteXLS.getCellData_fromTestData("APIUserName");
                   Globals.VV_API_UserPwd            =   Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");

                   retval                 =        InhousePortal.validateOrderRequestFields(fldSm);
                   log.debug("Field Level Validation during Order request is performed.Result-"+tempRetval);

                                     
                  }catch(Exception e){
                    sc.STAF_ReportEvent("Fail", "Negative validation Seamless", "Exception occurred during XML Post-Exception - "+ e.toString(),0);
                    log.error("Exception occurred in PostOrderRequestXMLSeamless .  | "+e.toString());
                    throw e;
                  }
                    return retval;
     }
     
     /**************************************************************************************************
      * Method to post an order request xml of negative scenario for VV Seamless ordering
      * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
      * @author psave
      * @created
      * @LastModifiedBy
      ***************************************************************************************************/

      public static String negativeValidationSeamlessConsent() throws Exception {
             APP_LOGGER.startFunction("Negative Validation SM Consent");
             String retval =      Globals.KEYWORD_FAIL;
             String tempRetval    =      Globals.KEYWORD_FAIL;
  
             try {

                    String currentPlatform_Path              =      Globals.currentRunPath+"\\"+Globals.CurrentPlatform;

                           
                    String fldSm                             =      Globals.testSuiteXLS.getCellData_fromTestData("Seamless");
                                      
                    if(fldSm.equalsIgnoreCase("SM")){
                 	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless"+"\\"+"Negative Validation"; 
                 	   Globals.vvInputReferenceXML              =      Globals.TestDir + "\\src\\test\\Resources\\refFile\\Seamless_API_Request.xml";
                    }else if(fldSm.equalsIgnoreCase("SM_Cons_EnSSN")) {
                 	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless_Consent_EncrySSN"+"\\"+"Negative Validation"; 
                 	   Globals.vvInputReferenceXML              =      Globals.TestDir + "\\src\\test\\Resources\\refFile\\Seamless_Consent_API_Request.xml";
                    }else if(fldSm.equalsIgnoreCase("N/A") || fldSm.equalsIgnoreCase("") || fldSm.isEmpty()){
                 	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Negative Validation";               	   
                    }
                    
                    sc.mkDirs(Globals.currentPlatform_XMLResults);


                    Globals.VV_API_UserName           =       Globals.testSuiteXLS.getCellData_fromTestData("APIUserName");
                    Globals.VV_API_UserPwd            =   Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
                  
                    //update volunteer details based on test data
                    HashMap<String, String> NodeListVals = new HashMap<String, String>();

                    //GoodDeedCode column name depends upon the Env that its being executed
                    String colName = Globals.Env_To_Execute_ON+"_GoodDeedCodeOrURL";
                    String goodDeedCode = Globals.testSuiteXLS.getCellData_fromTestData(colName);
                    NodeListVals.put("PackageID", goodDeedCode);

                    String applicantID = sc.runtimeGeneratedStringValue(10);
                    NodeListVals.put("ScreeningSubjectID", applicantID);
                    Globals.testSuiteXLS.setCellData_inTestData("CustomFieldAnswers", applicantID);
                    

                    updateVolunteerNameInTestData();

                    String fname=null;
                    String lname=null;

                    fname = sc.testSuiteXLS.getCellData_fromTestData("volunteerFName");
                    lname = sc.testSuiteXLS.getCellData_fromTestData("volunteerLName");

                    NodeListVals.put("oa:GivenName", fname);

                    NodeListVals.put("FamilyName", lname);

                    NodeListVals.put("PersonLegalID", "47858985");
                    
                    NodeListVals.put("oa:URI", Globals.fromEmailID);

                    String midName ="";
                    midName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");
                    if(midName.isEmpty() || midName == null){
                          midName ="";
                    }
                    NodeListVals.put("MiddleName", midName);

                    String addressLine =Globals.Volunteer_AddressLine;
                    String city=Globals.Volunteer_City;

                    NodeListVals.put("oa:PostalCode", Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode"));
                    NodeListVals.put("oa:CityName", city);
                    NodeListVals.put("oa:LineOne", addressLine);
                    NodeListVals.put("oa:LineTwo", addressLine);

                    int noOfyears=0;
                    String dobCategory =Globals.testSuiteXLS.getCellData_fromTestData("VolDOBCategory");
                    if(dobCategory.equalsIgnoreCase("GT than 18")){
                          noOfyears = -22;
                    }else if(dobCategory.equalsIgnoreCase("GT 14 Less than 18")){
                          noOfyears = -16;
                    }else if(dobCategory.equalsIgnoreCase("Less Than 14")){
                          noOfyears = -13;
                    }
                    String dobExcel = sc.getPriorDate("dd-MMM-yyyy", noOfyears);
    				Globals.testSuiteXLS.setCellData_inTestData("VolunteerDOB",dobExcel);
                    String dob = sc.getPriorDate("MM/dd/yyyy", noOfyears);
                    NodeListVals.put("FormattedDateTime", dob);
                     
                    String stateCode = sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("AddressState"));

                    NodeListVals.put("oa:CountrySubDivisionCode", stateCode);
                    NodeListVals.put("GenderCode", "Male");
                    String aysoFlagValue=Globals.testSuiteXLS.getCellData_fromTestData("CustomField1_LOV");
                    NodeListVals.put("Custom1", aysoFlagValue);
                    NodeListVals.put("Custom2", applicantID);
                                        
                    XMLUtils  orderRequestXML         =      new XMLUtils(Globals.vvInputReferenceXML, Globals.getEnvPropertyValue("SoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);

                    for (String node: NodeListVals.keySet()){

                          String key =node.toString();
                          String value = NodeListVals.get(node).toString();  
                          orderRequestXML.updatedXMLNodeValueByTagName(key,value);


                    }

                    String updatedXMlFileName = Globals.TestCaseID +"_Request.xml";
                    orderRequestXML.saveXMLDOM2File(updatedXMlFileName);
                    Globals.currentAPIOrderRequestPath = Globals.currentPlatform_XMLResults + "\\"+ updatedXMlFileName;
                    String updatedXMLFilePath = Globals.currentAPIOrderRequestPath;

                    XMLUtils updatedXMLFile    = new XMLUtils(updatedXMLFilePath, Globals.getEnvPropertyValue("SoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);
                    updatedXMLFile.postXML();  

                    Globals.Result_Link_FilePath = updatedXMLFilePath;
                    sc.STAF_ReportEvent("Pass", "Post Order Request", "Request XML used,stored at XML_results folder.FileName-"+updatedXMlFileName, 3);

                    String responseFilePath =  Globals.currentPlatform_XMLResults + "\\" + Globals.TestCaseID +"_Response.xml";

                    tempRetval                        =       updatedXMLFile.saveXMLString(responseFilePath);

                    Globals.Result_Link_FilePath = responseFilePath;
                    sc.STAF_ReportEvent("Pass", "Order Request Ack", "Response XML stored at XML_results folder.FileName--"+Globals.TestCaseID +"_Response.xml", 3);
                    tempRetval                        =       InhousePortal.verifyOrderAcknowledgment(responseFilePath,applicantID);

                    if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                          sc.STAF_ReportEvent("Fail", "Order Request Ack", "Successsful Response was not received", 3);
                          return Globals.KEYWORD_FAIL;
                    }
                    
                    Globals.Result_Link_FilePath = "";
                    retval                            =      Globals.KEYWORD_PASS;
                    
                    
                    //Store npnorder id in test data
                    XMLUtils orResponseXML = new XMLUtils(responseFilePath);
                    String npnOrderid=orResponseXML.getXMLNodeValByTagName("q1:DocumentID");
                    String expectedMsg=null;
                    Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderid);
                    
                     //Creating Change order request file 
                    changeOrderrequestXML(responseFilePath,applicantID);
                    
                    HashMap<String, String> nodeListAndVals = new HashMap<String, String>();
                                        
                    nodeListAndVals.put("DocumentID", "");
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Blank Order ID",nodeListAndVals,"OrderID invalid","Blank_OrderId");
                    nodeListAndVals.remove("DocumentID");
                    
                    nodeListAndVals.put("DocumentID", "testinvalid");
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Invalid Order ID",nodeListAndVals,"Input string was not in a correct format.","Invalid_OrderId");
                    nodeListAndVals.remove("DocumentID");
                    
                    nodeListAndVals.put("SSN", "");
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Blank SSN",nodeListAndVals,"Missing Encrypted SSN","Blank_Encrypted_SSN");
                    nodeListAndVals.remove("SSN");
                    
                    nodeListAndVals.put("SSN", "FgFOcF1HIgcYxYwNWojA9kMXG8fzfPUYhyxmtcbyaugfdVACXCdwf+z");
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Invalid SSN Decryption Key",nodeListAndVals,"Failure decrypting SSN","Invalid_Descrypted_SSN");
                    nodeListAndVals.remove("SSN");
                    
                    nodeListAndVals.put("DLStateCode", "");
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Blank DL State Code",nodeListAndVals,"Invalid DL StateCode Received, DL Number format (1234567) does not match acceptable State format ()","Blank_DL_STATE_CODE");
                    nodeListAndVals.remove("DLStateCode");
                    
                    nodeListAndVals.put("DLStateCode", "ZQ");
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Invalid DL State Code",nodeListAndVals,"DL Number format (1234567) does not match acceptable State format (ZQ)","Invalid_DL_STATE_CODE");
                    nodeListAndVals.remove("DLStateCode");
                    
                    nodeListAndVals.put("DLStateCode", "GA");
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("No Authorization DL State Code",nodeListAndVals,"Missing MVR State Authorization","No_Auth_DL_STATE_CODE");
                    nodeListAndVals.remove("DLStateCode");
                        
                    nodeListAndVals.put("DLNumber", "");
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Blank DLNumber",nodeListAndVals,"DL Number format () does not match acceptable State format (AL)","Blank_DLNumber");
                    nodeListAndVals.remove("DLNumber");
                    
                    nodeListAndVals.put("DLNumber", "123456");
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Invalid DLNumber",nodeListAndVals,"DL Number format (123456) does not match acceptable State format (AL)","Invalid_DLNumber");
                    nodeListAndVals.remove("DLNumber");
                    
                    nodeListAndVals.put("DLFirstName", "");
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Blank DLFirstName",nodeListAndVals,"Missing DL FirstName","Blank_DLFirstName");
                    nodeListAndVals.remove("DLFirstName");
                    
                    nodeListAndVals.put("DLLastName", "");
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Blank DLLastName",nodeListAndVals,"Missing DL LastName","Blank_DLLastName");
                    nodeListAndVals.remove("DLLastName");
                    
                    nodeListAndVals.put("ConsentHTML", "");
                    expectedMsg="ChangeOrder Consent HTML - First Name is required. Order "+npnOrderid;
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Blank Consent First Name",nodeListAndVals,expectedMsg,"Blank_Consent_FirstName");
                    nodeListAndVals.remove("ConsentHTML");
                    
                    nodeListAndVals.put("ConsentHTML", "");
                    expectedMsg="ChangeOrder Consent HTML First Name (invalid) does not match First Name on Order ("+npnOrderid+").";
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Invalid Consent First Name",nodeListAndVals,expectedMsg,"Invalid_Consent_FirstName");
                    nodeListAndVals.remove("ConsentHTML");
                    
                    nodeListAndVals.put("ConsentHTML", "");
                    expectedMsg="ChangeOrder Consent HTML - Last Name is required. Order "+npnOrderid;
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Blank Consent Last Name",nodeListAndVals,expectedMsg,"Blank_Consent_LastName");
                    nodeListAndVals.remove("ConsentHTML");
                    
                    nodeListAndVals.put("ConsentHTML", "");
                    expectedMsg="ChangeOrder Consent HTML Last Name (invalid) does not match Last Name on Order ("+npnOrderid+").";
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("Invalid Consent Last Name",nodeListAndVals,expectedMsg,"Invalid_Consent_LastName");
                    nodeListAndVals.remove("ConsentHTML");
                    
                    nodeListAndVals.put("ConsentHTML", "");
                    expectedMsg="Checkbox 'rightsSummaryConsent' is not checked. Order "+npnOrderid;
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("rightsSummaryConsent Consent checkbox Unchecked",nodeListAndVals,expectedMsg,"Unchecked_Checkbox_rightsSummaryConsent");
                    nodeListAndVals.remove("ConsentHTML");
                    
                    nodeListAndVals.put("ConsentHTML", "");
                    expectedMsg="Checkbox 'disclosureConsent' is not checked. Order "+npnOrderid;
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("disclosureConsent Consent checkbox Unchecked",nodeListAndVals,expectedMsg,"Unchecked_Checkbox__disclosureConsent");
                    nodeListAndVals.remove("ConsentHTML");
                    
                    nodeListAndVals.put("ConsentHTML", "");
                    expectedMsg="Checkbox 'eSignatureConsent' is not checked. Order "+npnOrderid;
                    InhousePortal.modifyNodeAndValidateChangeResponseXML("eSignatureConsent Consent checkbox Unchecked",nodeListAndVals,expectedMsg,"Unchecked_Checkbox__eSignatureConsent");
                    nodeListAndVals.remove("ConsentHTML");
                    
                    //retval                 =        InhousePortal.validateOrderRequestFields(fldSm);
                    log.debug("Field Level Validation during Order request is performed.Result-"+tempRetval);
                                      
                   }catch(Exception e){
                     sc.STAF_ReportEvent("Fail", "Negative validation Seamless", "Exception occurred during XML Post-Exception - "+ e.toString(),0);
                     log.error("Exception occurred in PostOrderRequestXMLSeamless .  | "+e.toString());
                     throw e;
                   }
                     return retval;
      }
     
     

	/**************************************************************************************************
     * Method to post an order request xml for VV Seamless ordering
     * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
     * @author psave
     * @created
     * @LastModifiedBy
     ***************************************************************************************************/

     public static String postOrderRequestXMLSeamless() throws Exception {
            APP_LOGGER.startFunction("PostOrderRequest");
            String retval =      Globals.KEYWORD_FAIL;
            String tempRetval    =      Globals.KEYWORD_FAIL;


            try {

                   String currentPlatform_Path              =      Globals.currentRunPath+"\\"+Globals.CurrentPlatform;

                   Globals.vvInputReferenceXML              =      Globals.TestDir + "\\src\\test\\Resources\\refFile\\Seamless_API_Request.xml";       
                   String fldSm                             =      Globals.testSuiteXLS.getCellData_fromTestData("Seamless");
                                     
                   if(fldSm.equalsIgnoreCase("SM")){
                	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless"; 
                   }else if(fldSm.equalsIgnoreCase("SM_Cons_EnSSN")) {
                	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless_Consent_EncrySSN"; 
                   }else if(fldSm.equalsIgnoreCase("N/A") || fldSm.equalsIgnoreCase("") || fldSm.isEmpty()){
                	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results";               	   
                   }
                   
                   sc.mkDirs(Globals.currentPlatform_XMLResults);


                   Globals.VV_API_UserName           =       Globals.testSuiteXLS.getCellData_fromTestData("APIUserName");
                   Globals.VV_API_UserPwd            =   Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");

                   //update volunteer details based on test data
                   HashMap<String, String> NodeListVals = new HashMap<String, String>();

                   //GoodDeedCode column name depends upon the Env that its being executed
                   String colName = Globals.Env_To_Execute_ON+"_GoodDeedCodeOrURL";
                   String goodDeedCode = Globals.testSuiteXLS.getCellData_fromTestData(colName);
                   NodeListVals.put("PackageID", goodDeedCode);

                   String applicantID = sc.runtimeGeneratedStringValue(10);
                   NodeListVals.put("ScreeningSubjectID", applicantID);

                   updateVolunteerNameInTestData();

                   String fname=null;
                   String lname=null;

                   fname = sc.testSuiteXLS.getCellData_fromTestData("volunteerFName");
                   lname = sc.testSuiteXLS.getCellData_fromTestData("volunteerLName");

                   NodeListVals.put("oa:GivenName", fname);

                   NodeListVals.put("FamilyName", lname);

                   NodeListVals.put("oa:URI", Globals.fromEmailID);

                   String midName ="";
                   midName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");
                   if(midName.isEmpty() || midName == null){
                         midName ="";
                   }
                   NodeListVals.put("MiddleName", midName);

                   String addressLine =Globals.Volunteer_AddressLine;
                   String city=Globals.Volunteer_City;

                   NodeListVals.put("oa:PostalCode", Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode"));
                   NodeListVals.put("oa:CityName", city);
                   NodeListVals.put("oa:LineOne", addressLine);
                   NodeListVals.put("oa:LineTwo", addressLine);

                   int noOfyears=0;
                   String dobCategory =Globals.testSuiteXLS.getCellData_fromTestData("VolDOBCategory");
                   if(dobCategory.equalsIgnoreCase("GT than 18")){
                         noOfyears = -22;
                   }else if(dobCategory.equalsIgnoreCase("GT 14 Less than 18")){
                         noOfyears = -16;
                   }else if(dobCategory.equalsIgnoreCase("Less Than 14")){
                         noOfyears = -13;
                   }
                   String dobExcel = sc.getPriorDate("dd-MMM-yyyy", noOfyears);
   				   Globals.testSuiteXLS.setCellData_inTestData("VolunteerDOB",dobExcel);
                   String dob = sc.getPriorDate("MM/dd/yyyy", noOfyears);
                   NodeListVals.put("FormattedDateTime", dob);
                    
                   NodeListVals.put("PersonLegalID", Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN"));

                   String stateCode = sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("AddressState"));

                   NodeListVals.put("oa:CountrySubDivisionCode", stateCode);
                  NodeListVals.put("CountryCode", Globals.testSuiteXLS.getCellData_fromTestData("CountryCode"));
                   NodeListVals.put("GenderCode", Globals.testSuiteXLS.getCellData_fromTestData("Gender"));

                   XMLUtils  orderRequestXML         =      new XMLUtils(Globals.vvInputReferenceXML, Globals.getEnvPropertyValue("SoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);

                   for (String node: NodeListVals.keySet()){

                         String key =node.toString();
                         String value = NodeListVals.get(node).toString();  
                         orderRequestXML.updatedXMLNodeValueByTagName(key,value);


                   }

                   String PrevzipcodeLocator="//Address[@currentAddressIndicator='false']/PostalCode";
                   String PrevstateLocator="//Address[@currentAddressIndicator='false']/CountrySubDivisionCode";
                   String PrecountrycdLocator="//Address[@currentAddressIndicator='false']/CountryCode";
                   
                   String prevAddressStateCode = sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("PrevAddrState"));
                   String prevAddressCountryCode = sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("CountryCode"));
                   orderRequestXML.updatedXMLNodeValueByXPATH(PrevstateLocator, prevAddressStateCode);
                  // orderRequestXML.updatedXMLNodeValueByXPATH(PrecountrycdLocator, prevAddressCountryCode);
                   orderRequestXML.updatedXMLNodeValueByXPATH(PrevzipcodeLocator, Globals.testSuiteXLS.getCellData_fromTestData("PrevAddZipCode"));

                   String updatedXMlFileName = Globals.TestCaseID +"_Request.xml";
                   orderRequestXML.saveXMLDOM2File(updatedXMlFileName);
                   Globals.currentAPIOrderRequestPath = Globals.currentPlatform_XMLResults + "\\"+ updatedXMlFileName;
                   String updatedXMLFilePath = Globals.currentAPIOrderRequestPath;

                   XMLUtils updatedXMLFile    = new XMLUtils(updatedXMLFilePath, Globals.getEnvPropertyValue("SoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);
                   updatedXMLFile.postXML();  

                   Globals.Result_Link_FilePath = updatedXMLFilePath;
                   sc.STAF_ReportEvent("Pass", "Post Order Request", "Request XML used,stored at XML_results folder.FileName-"+updatedXMlFileName, 3);

                   String responseFilePath =  Globals.currentPlatform_XMLResults + "\\" + Globals.TestCaseID +"_Response.xml";

                   tempRetval                        =       updatedXMLFile.saveXMLString(responseFilePath);

                   Globals.Result_Link_FilePath = responseFilePath;
                   sc.STAF_ReportEvent("Pass", "Order Request Ack", "Response XML stored at XML_results folder.FileName--"+Globals.TestCaseID +"_Response.xml", 3);
                   tempRetval                        =       InhousePortal.verifyOrderAcknowledgment(responseFilePath,applicantID);

                   if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                         sc.STAF_ReportEvent("Fail", "Order Request Ack", "Successsful Response was not received", 3);
                         return Globals.KEYWORD_FAIL;
                   }
                   
                   Globals.Result_Link_FilePath = "";
                   retval                            =      Globals.KEYWORD_PASS;
                   
                   
                   //Notification message validation
                   XMLUtils orResponseXML = new XMLUtils(responseFilePath);
                   String npnOrderid=orResponseXML.getXMLNodeValByTagName("q1:DocumentID");
                   Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderid);
                   
                   String ntMsgAPilog=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
                   String ntMsgnwapiendpt=Globals.testSuiteXLS.getCellData_fromTestData("APIRetrievalNotifyResponseNewEndpoint");
          
                   if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") ){
                     //sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
                   }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
                      Queryforapilog(npnOrderid,"InProgress","Blank","NoNo");              
                   }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
                      Queryforapilog(npnOrderid,"InProgress","Blank","YesNo");
                   }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
                      Queryforapilog(npnOrderid,"InProgress","Blank","NoYes");
                      getOrderRequestXML("InProgress");
                   }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
                      Queryforapilog(npnOrderid,"InProgress","Blank","YesYes");
                      getOrderRequestXML("InProgress");
                   }
                   
                 //Verify npnorder id generated with SWEST id and then update in excel
                   
                   tempRetval=FetchSwestIdClientOrder();
                   if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                	   sc.STAF_ReportEvent("Fail", "FetchSwestIdClientOrder", "Unable to retrieve SWESt order id", 0);
                	   throw new Exception("Failed to fetch Swest Id from npnorder table");
                   }else{
                	    String swestOrderID=Globals.testSuiteXLS.getCellData_fromTestData("SWestOrderID");
                	    log.info("Method-Seamless | Order ID generated | NPN Order ID- "+npnOrderid + "| SWest Order ID = "+swestOrderID);
						String orderedDate = sc.getTodaysDate();
						Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
						sc.addParamVal_InEmail("SWestOrderID", swestOrderID);
						sc.addParamVal_InEmail("NPNOrderID", npnOrderid);
						sc.STAF_ReportEvent("Pass", "Seamless", "Seamless order successfully placed with "+npnOrderid+"-"+swestOrderID , 1);
                   }
                   

                  }catch(Exception e){
                    sc.STAF_ReportEvent("Fail", "Post XML", "Exception occurred during XML Post-Exception - "+ e.toString(),0);
                    log.error("Exception occurred in PostOrderRequestXMLSeamless .  | "+e.toString());
                    throw e;
                  }
                    return retval;
     }

     /**************************************************************************************************
      * Method to post an order request xml for VV Seamless with consent ordering
      * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
      * @author psave
      * @created
      * @LastModifiedBy
      ***************************************************************************************************/

      public static String postOrderRequestXMLSeamlessConsent() throws Exception {
             APP_LOGGER.startFunction("PostOrderRequestSeamlessWithConsent");
             String retval =      Globals.KEYWORD_FAIL;
             String tempRetval    =      Globals.KEYWORD_FAIL;
             String encryptSSN="FgFOcF1HIgcYxYwNWojA9kMXG8fzfPUYhyxmtcbyaugfdVACXCdwf+z6SO9I/em9TAEP9vQKqJOu5uhMXklONup/ID/S2HeOfGuVZsLl31ouxsEyeT7U3/kbsFjJMFFq";
             //description ssn - actual ssn : 222334444		
             try {

                    String currentPlatform_Path              =      Globals.currentRunPath+"\\"+Globals.CurrentPlatform;

                    Globals.vvInputReferenceXML              =      Globals.TestDir + "\\src\\test\\Resources\\refFile\\Seamless_Consent_API_Request.xml";       
                    String fldSm                             =      Globals.testSuiteXLS.getCellData_fromTestData("Seamless");
                                      
                    if(fldSm.equalsIgnoreCase("SM")){
                 	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless"; 
                    }else if(fldSm.equalsIgnoreCase("SM_Cons_EnSSN")) {
                 	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless_Consent_EncrySSN"; 
                    }else if(fldSm.equalsIgnoreCase("SM_Cons_EnSSN_SkipSSN")) {
                  	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"SM_Cons_EnSSN_SkipSSN";
                    }else if(fldSm.equalsIgnoreCase("SM_Cons_AESEnSSN")) {
                  	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless_Consent_AESEncrySSN"; 
                    }else if(fldSm.equalsIgnoreCase("SM_Enh_Payment")) {
                  	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless_Enh_Payment";               	   
                    }else if(fldSm.equalsIgnoreCase("N/A") || fldSm.equalsIgnoreCase("") || fldSm.isEmpty()){
                 	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results";               	   
                    }
                    
                    sc.mkDirs(Globals.currentPlatform_XMLResults);


                    Globals.VV_API_UserName           =       Globals.testSuiteXLS.getCellData_fromTestData("APIUserName");
                    Globals.VV_API_UserPwd            =   Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");

                    //update volunteer details based on test data
                    HashMap<String, String> NodeListVals = new HashMap<String, String>();

                    //GoodDeedCode column name depends upon the Env that its being executed
                    String colName = Globals.Env_To_Execute_ON+"_GoodDeedCodeOrURL";
                    String goodDeedCode = Globals.testSuiteXLS.getCellData_fromTestData(colName);
                    NodeListVals.put("PackageID", goodDeedCode);

                    String applicantID = sc.runtimeGeneratedStringValue(10);
                    NodeListVals.put("ScreeningSubjectID", applicantID);
                    Globals.testSuiteXLS.setCellData_inTestData("CustomFieldAnswers", applicantID);
                    

                    updateVolunteerNameInTestData();

                    String fname=null;
                    String lname=null;

                    fname = sc.testSuiteXLS.getCellData_fromTestData("volunteerFName");
                    lname = sc.testSuiteXLS.getCellData_fromTestData("volunteerLName");

                    NodeListVals.put("oa:GivenName", fname);

                    NodeListVals.put("FamilyName", lname);

                    NodeListVals.put("oa:URI", Globals.fromEmailID);

                    String midName ="";
                    midName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");
                    if(midName.isEmpty() || midName == null){
                          midName ="";
                    }
                    NodeListVals.put("MiddleName", midName);

                    String addressLine =Globals.Volunteer_AddressLine;
                    String city=Globals.Volunteer_City;

                    NodeListVals.put("oa:PostalCode", Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode"));
                    NodeListVals.put("oa:CityName", city);
                    NodeListVals.put("oa:LineOne", addressLine);
                    NodeListVals.put("oa:LineTwo", addressLine);

                    int noOfyears=0;
                    String dobCategory =Globals.testSuiteXLS.getCellData_fromTestData("VolDOBCategory");
                    if(dobCategory.equalsIgnoreCase("GT than 18")){
                          noOfyears = -22;
                    }else if(dobCategory.equalsIgnoreCase("GT 14 Less than 18")){
                          noOfyears = -16;
                    }else if(dobCategory.equalsIgnoreCase("Less Than 14")){
                          noOfyears = -13;
                    }
                    String dobExcel = sc.getPriorDate("dd-MMM-yyyy", noOfyears);
    				Globals.testSuiteXLS.setCellData_inTestData("VolunteerDOB",dobExcel);
                    String dob = sc.getPriorDate("MM/dd/yyyy", noOfyears);
                    NodeListVals.put("FormattedDateTime", dob);
                     
                    if((!fldSm.equalsIgnoreCase("SM_Cons_EnSSN")) || (!fldSm.equalsIgnoreCase("SM_Cons_AESEnSSN"))  ){
                    	NodeListVals.put("PersonLegalID", Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN"));
                    }
                    
                    String stateCode = sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("AddressState"));

                    NodeListVals.put("oa:CountrySubDivisionCode", stateCode);
                    NodeListVals.put("CountryCode", Globals.testSuiteXLS.getCellData_fromTestData("CountryCode"));
                    NodeListVals.put("GenderCode", Globals.testSuiteXLS.getCellData_fromTestData("Gender"));
                    String aysoFlagValue=Globals.testSuiteXLS.getCellData_fromTestData("CustomField1_LOV");
                    NodeListVals.put("Custom1", aysoFlagValue);
                    NodeListVals.put("Custom2", applicantID);
                                        
                    XMLUtils  orderRequestXML         =      new XMLUtils(Globals.vvInputReferenceXML, Globals.getEnvPropertyValue("SoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);

                    for (String node: NodeListVals.keySet()){

                          String key =node.toString();
                          String value = NodeListVals.get(node).toString();  
                          orderRequestXML.updatedXMLNodeValueByTagName(key,value);


                    }

                    String updatedXMlFileName = Globals.TestCaseID +"_Request.xml";
                    orderRequestXML.saveXMLDOM2File(updatedXMlFileName);
                    Globals.currentAPIOrderRequestPath = Globals.currentPlatform_XMLResults + "\\"+ updatedXMlFileName;
                    String updatedXMLFilePath = Globals.currentAPIOrderRequestPath;

                    XMLUtils updatedXMLFile    = new XMLUtils(updatedXMLFilePath, Globals.getEnvPropertyValue("SoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);
                    updatedXMLFile.postXML();  

                    Globals.Result_Link_FilePath = updatedXMLFilePath;
                    sc.STAF_ReportEvent("Pass", "Post Order Request", "Request XML used,stored at XML_results folder.FileName-"+updatedXMlFileName, 3);

                    String responseFilePath =  Globals.currentPlatform_XMLResults + "\\" + Globals.TestCaseID +"_Response.xml";

                    tempRetval                        =       updatedXMLFile.saveXMLString(responseFilePath);

                    Globals.Result_Link_FilePath = responseFilePath;
                    sc.STAF_ReportEvent("Pass", "Order Request Ack", "Response XML stored at XML_results folder.FileName--"+Globals.TestCaseID +"_Response.xml", 3);
                    tempRetval                        =       InhousePortal.verifyOrderAcknowledgment(responseFilePath,applicantID);

                    if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                          sc.STAF_ReportEvent("Fail", "Order Request Ack", "Successsful Response was not received", 3);
                          return Globals.KEYWORD_FAIL;
                    }
                    if(fldSm.equalsIgnoreCase("SM_Enh_Payment")){
                    	XMLUtils oResponseXML = new XMLUtils(responseFilePath);
                    	String resnNpnorder=oResponseXML.getXMLNodeValByTagName("q1:DocumentID");
                        String ConsentPaymentURI=oResponseXML.getXMLNodeValByTagName("ConsentPaymentURI");
                        Boolean flag=Pattern.compile( "[0-9]" ).matcher(resnNpnorder).find(); 
                        
                        if(flag==false && ConsentPaymentURI.equalsIgnoreCase("")){
                        	sc.STAF_ReportEvent("Fail", "Order Request Ack", "Successsful Response was not received", 3);
                            return Globals.KEYWORD_FAIL;
                        }
                        Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", resnNpnorder);
                        Globals.testSuiteXLS.setCellData_inTestData("InvitationURL", ConsentPaymentURI);
                        return Globals.KEYWORD_PASS;
                    }else{            
                    
                    
                    Globals.Result_Link_FilePath = "";
                    retval                            =      Globals.KEYWORD_PASS;
                                       
                    //Store npnorder id in test data
                    XMLUtils orResponseXML = new XMLUtils(responseFilePath);
                    String npnOrderid=orResponseXML.getXMLNodeValByTagName("q1:DocumentID");
                    Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderid);
                           
                     //Creating Change order request file 
                    changeOrderrequestXML(responseFilePath,applicantID);
                                   
                    String updatedChangeXMLFilePath = Globals.currentAPIOrderRequestPath;
                    
                    //Post Change order request file
                    XMLUtils updatedChangeXMLFile    = new XMLUtils(updatedChangeXMLFilePath, Globals.getEnvPropertyValue("ChangeSoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);
                    updatedChangeXMLFile.postXML(); 
                    
                    Globals.Result_Link_FilePath = updatedChangeXMLFilePath;
                    sc.STAF_ReportEvent("Pass", "Post Order Request", "Request XML used,stored at XML_results folder.File Path-"+updatedChangeXMLFilePath, 3);

                    responseFilePath =  Globals.currentPlatform_XMLResults + "\\" + Globals.TestCaseID +"_Change_Response.xml";

                    tempRetval                        =       updatedChangeXMLFile.saveXMLString(responseFilePath);

                    Globals.Result_Link_FilePath = responseFilePath;
                    sc.STAF_ReportEvent("Pass", "Change Order Request Ack", "Change Order Response XML stored at XML_results folder.FileName--"+Globals.TestCaseID +"_Change_Response.xml", 3);
                   
                    //After creating change order response file - verify the npnorder, candidate id and code value match with expcted 
                    XMLUtils orChangeResponseXML = new XMLUtils(responseFilePath);
                    String changeResNpnOrderid=orChangeResponseXML.getXMLNodeValByTagName("q1:DocumentID");
                    String changeResApplicantid=orChangeResponseXML.getXMLNodeValByTagName("BODID");
                    String changeCodeValue=orChangeResponseXML.getXMLNodeValByTagName("Code");
                    
                    if((changeResNpnOrderid.equalsIgnoreCase(npnOrderid)) && (changeResApplicantid.equalsIgnoreCase(applicantID))  && (changeCodeValue.equalsIgnoreCase("Change"))){
                    		sc.STAF_ReportEvent("Pass", "Change Order Request Ack", "OrderID , Candidate id and Code is matched as per Expected with change order response xml. File Path : "+responseFilePath, 3); 
                    		Globals.Result_Link_FilePath = "";
                    		retval                            =      Globals.KEYWORD_PASS;
                        
                   }else{
                    		sc.STAF_ReportEvent("Fail", "Change Order Request Ack", "Successsful Response was not received. Response XML file path : "+responseFilePath, 3);
                    		return Globals.KEYWORD_FAIL;
                    }
                                      
                    // Notification message validation for APIlog table and Order Response
                    String ntMsgAPilog=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
                    String ntMsgnwapiendpt=Globals.testSuiteXLS.getCellData_fromTestData("APIRetrievalNotifyResponseNewEndpoint");
           
                    String OrderHoldQueuesettings = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldQueuecheckbox");
                    if(OrderHoldQueuesettings.equalsIgnoreCase("Yes")){
                    	if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") || ntMsgAPilog.equalsIgnoreCase("") || ntMsgnwapiendpt.equalsIgnoreCase("")||ntMsgAPilog.isEmpty() || ntMsgnwapiendpt.isEmpty()){
			            	//sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
			            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
			            	Queryforapilog(npnOrderid,"Hold","Blank","NoNo");            	
			            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
			            	Queryforapilog(npnOrderid,"Hold","Blank","YesNo");
			            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
			            	Queryforapilog(npnOrderid,"Hold","Blank","NoYes");
			            	getOrderRequestXML("Hold");
			            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
			            	Queryforapilog(npnOrderid,"Hold","Blank","YesYes");
			            	getOrderRequestXML("Hold");
			            }else{
			            	sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
			            }
                    	String orderedDatewe = sc.getTodaysDate();
 						Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDatewe);
                    	retval                            =      Globals.KEYWORD_PASS;               
                    
                    }else{                  
                    	if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") ){
                    		//sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
                    	}else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
                    		Queryforapilog(npnOrderid,"InProgress","Blank","NoNo");              
                    	}else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
                    		Queryforapilog(npnOrderid,"InProgress","Blank","YesNo");
                    	}else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
                    		Queryforapilog(npnOrderid,"InProgress","Blank","NoYes");
                    		getOrderRequestXML("InProgress");
                    	}else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
                    		Queryforapilog(npnOrderid,"InProgress","Blank","YesYes");
                    		getOrderRequestXML("InProgress");
                    	}
                    	
                    	   //Verify npnorder id generated with SWEST id and then update in excel
                        
                        retval=FetchSwestIdClientOrder();
                        if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                     	   sc.STAF_ReportEvent("Fail", "FetchSwestIdClientOrder", "Unable to retrieve SWESt order id", 0);
                     	   throw new Exception("Failed to fetch Swest Id from npnorder table");
                        }else{
                     	    String swestOrderID=Globals.testSuiteXLS.getCellData_fromTestData("SWestOrderID");
                     	    log.info("Method-Seamless | Order ID generated | NPN Order ID- "+npnOrderid + "| SWest Order ID = "+swestOrderID);
     						String orderedDate = sc.getTodaysDate();
     						Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
     						sc.addParamVal_InEmail("SWestOrderID", swestOrderID);
     						sc.addParamVal_InEmail("NPNOrderID", npnOrderid);
     						sc.STAF_ReportEvent("Pass", "Seamless", "Seamless order successfully placed with "+npnOrderid+"-"+swestOrderID , 1);
     						
                        }
                                     	
                    }
               
                    } 		
                   }catch(Exception e){
                     sc.STAF_ReportEvent("Fail", "Post XML", "Exception occurred during XML Post-Exception - "+ e.toString(),0);
                     log.error("Exception occurred in PostOrderRequestXMLSeamless .  | "+e.toString());
                     throw e;
                   }
                     return retval;
      }
  
      /**************************************************************************************************
       * Method to post an change order request xml for VV Seamless with consent ordering
       * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
       * @author psave
       * @created
       * @LastModifiedBy
       ***************************************************************************************************/

       public static void changeOrderrequestXML(String responseFilePath,String applicantID) throws Exception {
              APP_LOGGER.startFunction("PostOrderRequestSeamlessWithConsent");
              String encryptSSN="FgFOcF1HIgcYxYwNWojA9kMXG8fzfPUYhyxmtcbyaugfdVACXCdwf+z6SO9I/em9TAEP9vQKqJOu5uhMXklONup/ID/S2HeOfGuVZsLl31ouxsEyeT7U3/kbsFjJMFFq";
              //description ssn - actual ssn : 222334444		
              try {

                     String fldSm   =      Globals.testSuiteXLS.getCellData_fromTestData("Seamless");
                     String product   =      Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts");
                     String npnOrderid	=  Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
                     String dlname =  Globals.Volunteer_DL_Name; 
                     String fname =  Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName"); 
                     String lname =  Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName"); 
                     String mname=   Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName"); 
                     String dlstate=   Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_State").toUpperCase(); 
                     String dlStateCode = State.valueOf(dlstate).abbreviation;
                     String dlnumber=  Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_License"); 
                     Globals.VV_API_UserName           =       Globals.testSuiteXLS.getCellData_fromTestData("APIUserName");
                     Globals.VV_API_UserPwd            =   Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
                     
                     XMLUtils orResponseXML = new XMLUtils(responseFilePath);
                                         
                     //Creating Change order request file                
                     String actConsentHTML=orResponseXML.getXMLNodeValByTagName("ConsentHTML");
                     String accName=Globals.testSuiteXLS.getCellData_fromTestData("AccountName").trim();
                     String expctedConsentHTML=accName+" is required by law to provide you with information regarding those        rights and to gain your consent for a background check before allowing you to continue with your order.";
                     String expctedConsentHTMLEMP="VVRegEmployment, test address Line, test address Line, test, FL 32007, may obtain a consumer report and/or an investigative consumer report (\"REPORT\") that contains background information about you in connection with your employment and/or volunteerism/non-employee position.        VVRegEmployment may obtain further reports throughout your employment and/or volunteerism/non-employee position so as to update your report without providing further disclosure or obtaining additional consent.";
                     String empConsent=Globals.testSuiteXLS.getCellData_fromTestData("AccountUse");
                     
                     if(empConsent.equalsIgnoreCase("Employment")){
                    	 if(!actConsentHTML.contains(expctedConsentHTMLEMP)){
                          	sc.STAF_ReportEvent("Fail", "FetchConsentHtml", "Unable to fetch Consent HTML from post order response xml. File path : "+responseFilePath, 0);
                        	    throw new Exception("Failed to fetch Consent HTML from post order response xml");	
                          }
                     }
                     
                     if(!actConsentHTML.contains(expctedConsentHTML)){
                     	sc.STAF_ReportEvent("Fail", "FetchConsentHtml", "Unable to fetch Consent HTML from post order response xml. File path : "+responseFilePath, 0);
                   	    throw new Exception("Failed to fetch Consent HTML from post order response xml");	
                     }
                     
                     Globals.vvInputReferenceXML              =      Globals.TestDir + "\\src\\test\\Resources\\refFile\\Seamless_Consent_API_ChangeOrder_Request.xml"; 
                                        
                     XMLUtils  orderChangeRequestXML         =      new XMLUtils(Globals.vvInputReferenceXML, Globals.getEnvPropertyValue("ChangeSoapServer"), Globals.VV_API_UserName,Globals.VV_API_UserPwd);
                     
                     //Taking ref file for change order request file - Value Update in consentHTML , npnorder and applicant id tags
                     String changeConsent=orderChangeRequestXML.getXMLNodeValByTagName("ConsentHTML").toString();
                     String textchangeXML=changeConsent.replace("testabc", fname);
                     textchangeXML=textchangeXML.replace("testxyz", lname).toString();
                     
                     orderChangeRequestXML.updatedXMLNodeValueByTagName("DocumentID",npnOrderid);
                     orderChangeRequestXML.updatedXMLNodeValueByTagName("ScreeningSubjectID",applicantID);
                     orderChangeRequestXML.updatedXMLNodeValueByTagName("ConsentHTML",textchangeXML);
                     if(fldSm.equalsIgnoreCase("SM_Cons_EnSSN")){
                     	orderChangeRequestXML.updatedXMLNodeValueByTagName("SSN",encryptSSN);
                     }else if (fldSm.equalsIgnoreCase("SM_Cons_AESEnSSN")){
                    	// encryptSSN="CaMNRHzgEbYbcUdFhL8Y8A==";
                    	 encryptSSN="x3zpfZuj+GYIUZ0caZ3Ekg==";
                    	 orderChangeRequestXML.updatedXMLNodeValueByTagName("SSN",encryptSSN);
                     }
                     
                     if(product.contains("MVR")){
                    	 orderChangeRequestXML.updatedXMLNodeValueByTagName("DLStateCode",dlStateCode); 
                    	 orderChangeRequestXML.updatedXMLNodeValueByTagName("DLNumber",dlnumber);
                    	 orderChangeRequestXML.updatedXMLNodeValueByTagName("DLFirstName", dlname);
                    	 orderChangeRequestXML.updatedXMLNodeValueByTagName("DLLastName", dlname);
                    	 orderChangeRequestXML.updatedXMLNodeValueByTagName("DLMiddleName", dlname);
                     }
                     
                             
                     String updatedXMlChangeFileName = Globals.TestCaseID +"_Change_Request.xml";
                     orderChangeRequestXML.saveXMLDOM2File(updatedXMlChangeFileName);
                     Globals.currentAPIOrderRequestPath = Globals.currentPlatform_XMLResults + "\\"+ updatedXMlChangeFileName;
                     

                    }catch(Exception e){
                      sc.STAF_ReportEvent("Fail", "Change Order Post XML", "Exception occurred during XML Post-Exception - "+ e.toString(),0);
                      log.error("Exception occurred in PostOrderRequestXMLSeamlessConsent .  | "+e.toString());
                      throw e;
                    }
                   
       }
   

	/**************************************************************************************************
      * Method to find out notification message getting correctly for api log table for different order status
      * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
      * @author psave
      * @LastModifiedBy
      ***************************************************************************************************/
     
     public static String Queryforapilog(String npnorderid,String orderStatus, String reprtResult, String colFlag) throws Exception {
     String retval        = Globals.KEYWORD_FAIL;
     VVDAO vvDB             = null;
     try{
    	 			String dbURL = Globals.getEnvPropertyValue("dbURL");
    	 			String dbUserName = Globals.getEnvPropertyValue("dbUserName");
    	 			String dbPassword = ProtectedEncryption.decrypt(Globals.getEnvPropertyValue("dbPassword"), CommonHelpMethods.createKey());
 		
    	 			// 		String dbURL = "jdbc:sqlserver://"+Globals.VV_DBServerName + ";portNumber="+Globals.VV_DBServerPort+";databaseName="+Globals.VV_DBName+";integratedSecurity=true;"; // to use windows authentication include the following code- integratedSecurity=true;";
    	 			//			log.debug(".........Connecting to VV - DatabaseServer-"+Globals.VV_DBServerName +":"+ "-" +Globals.VV_DBName  );
    	 			//			this.conn=DriverManager.getConnection(dbURL, Globals.VV_USERNAME, Globals.VV_PASSWORD);

    	 			log.info("DB URL is :"+"\t"+dbURL);
 		
    	 			vvDB      = new VVDAO(dbURL,dbUserName,dbPassword); 
                     
                     //String selectSQL = "with xmlnamespaces ('http://www.hr-xml.org/3' as xa,'http://www.openapplications.org/oagis/9' as xb,'http://www.w3.org/2001/XMLSchema-instance' as xc,'http://www.hr-xml.org/3 ../Developer/BODs/ProcessScreeningPackageOrder.xsd' as xd,'http://www.verifiedvolunteers.com/' as xe) SELECT      [ApiLogId],[Xml].value('(.//xa:DocumentID)[1]', 'nvarchar(max)') As OrderId,[Xml].value('(.//xb:ChangeStatus/xb:Code)[1]', 'nvarchar(max)') As OrderStatus,[Xml].value('(.//xa:UserArea/ReportResult)[1]', 'nvarchar(max)') As ReportResult,[Xml].value('(.//xe:VolunteerUrl)[1]', 'nvarchar(max)') As VolunteerUrl,[Xml].value('(.//xa:PersonName/xb:GivenName)[1]','nvarchar(max)') As FirstName,[Xml].value('(.//xa:FamilyName)[1]','nvarchar(max)') As LastName,[Xml].value('(.//xb:Description)[1]','nvarchar(max)') As Error,[Result],[Xml]FROM  ApiLog where Date>'2016-12-07'and [Xml].value('(.//xa:DocumentID)[1]', 'nvarchar(max)') =?" ;
                     
                     String selectSQL = "with xmlnamespaces ( 'http://www.hr-xml.org/3' as xa, 'http://www.openapplications.org/oagis/9' as xb, 'http://www.w3.org/2001/XMLSchema-instance' as xc, 'http://www.hr-xml.org/3 ../Developer/BODs/ProcessScreeningPackageOrder.xsd' as xd, 'https://www.verifiedvolunteers.com/' as xe) SELECT [ApiLogId],[RequestId],[Date],[UserName],[Request],[StatusId],[TypeId],[Xml].value('(.//xa:PackageID)[1]', 'nvarchar(max)') as PackageId,[Xml].value('(.//xa:DocumentID)[1]', 'nvarchar(max)') As OrderId,[Xml].value('(.//xb:BODID)[1]', 'nvarchar(max)') As ApiCandidateIdOUT,[Xml].value('(.//xb:ChangeStatus/xb:Code)[1]', 'nvarchar(max)') As OrderStatus,[Xml].value('(.//xa:UserArea/ReportResult)[1]', 'nvarchar(max)') As ReportResult,[Xml].value('(.//xa:UserArea/EffectiveDate)[1]', 'nvarchar(max)') as EffectiveDate,[Xml].value('(.//xe:ReportURL)[1]', 'nvarchar(max)') as ReportURL,[Xml].value('(.//xe:Restriction)[1]', 'nvarchar(max)') as Restriction,[Xml].value('(.//xa:ScreeningSubjectID)[1]', 'nvarchar(max)') As ApiCandidateIN,[Xml].value('(.//xe:VolunteerUrl)[1]', 'nvarchar(max)') As VolunteerUrl,[Xml].value('(.//xa:Communication/xb:URI)[1]', 'nvarchar(max)') As Email,[Xml].value('(.//xa:PersonName/xb:GivenName)[1]','nvarchar(max)') As FirstName,[Xml].value('(.//xa:FamilyName)[1]','nvarchar(max)') As LastName,[Xml].value('(.//xa:Custom1)[1]','nvarchar(max)') As Custom1,[Xml].value('(.//xa:Custom2)[1]','nvarchar(max)') As Custom2,[Xml].value('(.//xa:Custom3)[1]','nvarchar(max)') As Custom3,[Xml].value('(.//xa:ClientView)[1]','nvarchar(max)') As ClientView,[Xml].value('(.//xa:FreeFormBirthDate)[1]','nvarchar(max)') As DOB,[Xml].value('(.//xa:Address/xb:LineOne)[1]','nvarchar(max)') As LineOne,[Xml].value('(.//xa:Address/xb:CityName)[1]','nvarchar(max)') As City,[Xml].value('(.//xa:Address/xb:CountrySubDivisionCode)[1]','nvarchar(max)') As State,[Xml].value('(.//xa:Address/xb:CountryCode)[1]','nvarchar(max)') As Country,[Xml].value('(.//xa:Communicaton/xb:AreaDialing)[1]','nvarchar(max)') As AreaCode,[Xml].value('(.//xa:Communication/xb:DialNumber)[1]','nvarchar(max)') As DialNumber,[Xml].value('(.//xa:Address/xb:PostalCode)[1]','nvarchar(max)') As ZIP,[Xml].value('(.//xa:GenderCode)[1]','nvarchar(max)') As Gender,[Xml].value('(.//xb:Description)[1]','nvarchar(max)') As Error,[Result],[Xml] FROM  ApiLog (nolock) where Date>'2016-12-07' and [Xml].value('(.//xa:DocumentID)[1]', 'nvarchar(max)') =? Order by date";
                     
                     vvDB.ps = vvDB.conn.prepareStatement(selectSQL,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                     
                     vvDB.ps.setString(1, npnorderid);
                     //Thread.sleep(3000);
                     int rerurn=0;
                 
                     vvDB.rs = vvDB.ps.executeQuery();
                    
                    
                     int initRowCount = 0;
                     //int initColCount = 0;
                     
                     initRowCount = vvDB.getRows(vvDB.rs);
                     //initColCount =vvDB.rs.getMetaData().getColumnCount();
                     String odId=null;
                     String actOrderStatus=null;
                     String actReportResult=null;
                     String restrictionValue=null;
                     String reportURLValue=null;
                     int cntNew=0;
                     int cntChange=0;
                     int cntInprogress=0;
                     int cntComplete=0;
                     int cntAdju=0;
                     int whileRunFlag=0;
                     int cntHold=0;
                                       
                   if(initRowCount==0 || initRowCount==-1 )  {
                	   if(orderStatus.equalsIgnoreCase("Batch")){
                		   sc.STAF_ReportEvent("Pass", "APILog Table", "Notification messages are not sending for batch orders", 0);
                		   retval        = Globals.KEYWORD_PASS;
                	   }else{
                		   sc.STAF_ReportEvent("Fail", "APILog Table", "Unable to Retrive the data", 0); 
                           return Globals.KEYWORD_FAIL;
                	   }
                	   
                    }else{
                    	//vvDB.rs = vvDB.ps.executeQuery();
                    	 //Thread.sleep(5000);
                    	 int reRun=0;
                    	 boolean flg=false;
                    	 while(reRun<=3){
                    		 vvDB.rs = vvDB.ps.executeQuery();
                    		 
                    		 while(vvDB.rs.next())
                             {
                    			 if(orderStatus.equalsIgnoreCase("Batch") || colFlag.equalsIgnoreCase("NoYes") || colFlag.equalsIgnoreCase("NoNo") ){
                    				 flg=true;
                    				 break;
                           	     }
                    			 actOrderStatus=(vvDB.rs.getString("OrderStatus"));
                    			 if(actOrderStatus.equalsIgnoreCase(orderStatus)){
                    				 flg=true;
                    				 break; 
                    			 }
                    			                			 
                           	   
                             }
                    		 if(flg==true){
                    			 break;
                    		 }
                    		 reRun++;
                    	 }
                    	 
                    	 if(!flg==false){
                    		 vvDB.rs = vvDB.ps.executeQuery();
                    		 Thread.sleep(5000);
                    	 }
                    	 
                   
                         while(vvDB.rs.next())
                          {
                        	 if(orderStatus.equalsIgnoreCase("Batch")){
                      		   break;
                      	      }                       	 
                         
                            odId=(vvDB.rs.getString("OrderId"));
                            actOrderStatus=(vvDB.rs.getString("OrderStatus"));
                            actReportResult=(vvDB.rs.getString("ReportResult"));
                            restrictionValue=(vvDB.rs.getString("Restriction"));
                            reportURLValue=(vvDB.rs.getString("ReportURL"));
                            
                            if(odId.equalsIgnoreCase(npnorderid)){
                            	if((actOrderStatus.equalsIgnoreCase("New"))&&(actReportResult == null || actReportResult.isEmpty())){   //New Status
                            		cntNew=cntNew+1;
                            	}else if((actOrderStatus.equalsIgnoreCase("Hold"))){   //Inprogress Status
                            		cntHold=cntHold+1;
                            	}else if((actOrderStatus.equalsIgnoreCase("Change"))&&(actReportResult == null || actReportResult.isEmpty())){   //Inprogress Status
                            		cntChange=cntChange+1;
                            	}else if((actOrderStatus.equalsIgnoreCase("InProgress"))&&(actReportResult == null || actReportResult.isEmpty())){   //Inprogress Status
                            		cntInprogress=cntInprogress+1;
                            	}else if((actOrderStatus.equalsIgnoreCase("Complete"))&&((actReportResult.equalsIgnoreCase("Clear"))||(actReportResult.equalsIgnoreCase("Consider")))){   //Complete- Clear/Cosider Status
                            		cntComplete=cntComplete+1;
                            	}else if((actOrderStatus.equalsIgnoreCase("Adjudicated"))&&((actReportResult.equalsIgnoreCase("Eligible"))||(actReportResult.equalsIgnoreCase("InEligible")))){  //Adjudicated-Eligible/InEligible Status
                            		cntAdju=cntAdju+1;
                            	}else{
                            		sc.STAF_ReportEvent("Fail", "APILog Table", "Status is not matching, Actual Status getting as "+actOrderStatus, 0);
                                	whileRunFlag=1;
                            		break;
                            	}                          	
                            	
                            }else{
                            	sc.STAF_ReportEvent("Fail", "APILog Table", "Npnorderid is not matching", 0);
                            	whileRunFlag=1;
                            	break;
                            }
                           }//while closed
                           
                           if(whileRunFlag!=1){
                         
                           if(orderStatus.equalsIgnoreCase("New")){
                        	   if(cntNew>=1){
                        	      sc.STAF_ReportEvent("Pass", "APILog Table", "API Log Table is retrived NEW status notification message Successfully", 0);
                        	      retval=Globals.KEYWORD_PASS; 
                        	   }else{
                        		     sc.STAF_ReportEvent("Fail", "APILog Table", "Failed to retrived NEW status notification message", 0);
                        		     retval=Globals.KEYWORD_FAIL;
                        	   }
                           }else if(orderStatus.equalsIgnoreCase("Hold")){
                      	     if(((colFlag.equalsIgnoreCase("NoNo"))||(colFlag.equalsIgnoreCase("NoYes")))&&(cntNew>=1)){
                    	    	 sc.STAF_ReportEvent("Pass", "APILog Table", "Hold notification messages not getting as expected when SendNotification flag Turn Off", 0);
                    	    	 retval=Globals.KEYWORD_PASS; 
                    	     }else if(((colFlag.equalsIgnoreCase("YesNo"))||(colFlag.equalsIgnoreCase("YesYes")))&&(cntNew>=1)&&(cntHold>=1)){
                            	 sc.STAF_ReportEvent("Pass", "APILog Table", "Hold notification message getting Successfully", 0);
                            	 retval=Globals.KEYWORD_PASS; 
                    	     }else{
                            	 sc.STAF_ReportEvent("Fail", "APILog Table", "Failed to retrived notification message, Please check API configuration tab with SendNotificationsviaApiLogTable and APIRetrievalNotifyResponseNewEndpoint Column in test data", 0);
                            	 retval=Globals.KEYWORD_FAIL;
                    	     }
                           }else if(orderStatus.equalsIgnoreCase("InProgress")){
                        	     if(((colFlag.equalsIgnoreCase("NoNo"))||(colFlag.equalsIgnoreCase("NoYes")))&&(cntNew>=1)){
                        	    	 sc.STAF_ReportEvent("Pass", "APILog Table", "InProgress notification messages not getting as expected when SendNotification flag Turn Off", 0);
                        	    	 retval=Globals.KEYWORD_PASS; 
                        	     }else if(((colFlag.equalsIgnoreCase("YesNo"))||(colFlag.equalsIgnoreCase("YesYes")))&&(cntNew>=1)&&(cntInprogress>=1)){
                                	 sc.STAF_ReportEvent("Pass", "APILog Table", "InProgress notification message getting Successfully", 0);
                                	 retval=Globals.KEYWORD_PASS; 
                        	     }else{
                                	 sc.STAF_ReportEvent("Fail", "APILog Table", "Failed to retrived notification message, Please check API configuration tab with SendNotificationsviaApiLogTable and APIRetrievalNotifyResponseNewEndpoint Column in test data", 0);
                                	 retval=Globals.KEYWORD_FAIL;
                        	     }
                           }else if(orderStatus.equalsIgnoreCase("Complete")){
                        	     if(reprtResult.equalsIgnoreCase("reportURL")){
                        	    	 if(reportURLValue.equalsIgnoreCase(null)||reportURLValue.isEmpty()||reportURLValue.equalsIgnoreCase("") ){
                        	    		 retval=Globals.KEYWORD_FAIL;
                        	    		 sc.STAF_ReportEvent("Fail", "APILog Table", "Failed to retrive reportURl link for Adjucated result using report URL", 0);
                        	 			 throw new Exception("Failed to retrive reportURl link for Adjucated result using report URL");
                        	         }else{
                        	        	 Globals.testSuiteXLS.setCellData_inTestData("InvitationURL",reportURLValue);
                        	        	 log.debug(" Method-getReportUrl from API log table | URL - " + reportURLValue);
                        				 sc.STAF_ReportEvent("Pass", "APILog Table", "Retrive RportURL from API Log Table ReportURL - "+ reportURLValue, 0);
                        				 retval=Globals.KEYWORD_PASS;
                        				 return retval;
                        	         }
                        	    	 
                        	     }else{
                        	    	 
                        	       if((colFlag.equalsIgnoreCase("NoNo"))&&(cntNew>=1)){
                        	        	 sc.STAF_ReportEvent("Pass", "APILog Table", "Complete order status notification messages not getting as expected when SendNotification flag Turn Off", 0);
                        	        	 retval=Globals.KEYWORD_PASS; 
                          	       }else if(((colFlag.equalsIgnoreCase("YesNo"))||(colFlag.equalsIgnoreCase("YesYes")))&&(cntNew>=1)&&(cntInprogress>=1)&&(cntComplete>=1)){
                          	    	   retval=Globals.KEYWORD_PASS; 
                          	    	   sc.STAF_ReportEvent("Pass", "APILog Table", "Complete order status notification message getting Successfully", 0);
                          	       }else if((colFlag.equalsIgnoreCase("NoYes"))&&(cntNew>=1)&&(cntInprogress==1)){
                          	    	   retval=Globals.KEYWORD_PASS; 
                          	    	   sc.STAF_ReportEvent("Pass", "APILog Table", " Complete notification message getting Successfully only for new endpoint", 0);
                          	       }else{
                          	    	   sc.STAF_ReportEvent("Fail", "APILog Table", "Failed to retrived notification message, Please check API configuration tab with SendNotificationsviaApiLogTable and APIRetrievalNotifyResponseNewEndpoint Column in test data", 0);
                          	    	   retval=Globals.KEYWORD_FAIL;
                          	       }
                        	    	 
                        	     }
                      	      
                        	   
                           }else if(orderStatus.equalsIgnoreCase("Adjudicated")){
                        	     boolean restFlag=false;
                        	     if((colFlag.equalsIgnoreCase("NoNo"))&&(cntNew>=1)){
                        	    	 retval=Globals.KEYWORD_PASS; 
                        	    	 sc.STAF_ReportEvent("Pass", "APILog Table", "Adjudicated order status notification message not getting as expected when SendNotification flag Turn Off", 0);
                                 }else if(((colFlag.equalsIgnoreCase("YesNo"))||(colFlag.equalsIgnoreCase("YesYes")))&&(cntNew>=1)&&(cntInprogress>=1)&&(cntComplete>=1)&&(cntAdju>=1)){
                                	 retval=Globals.KEYWORD_PASS; 
                                	 sc.STAF_ReportEvent("Pass", "APILog Table", "Adjudicated order status notification message getting Successfully", 0);
                                 }else if((colFlag.equalsIgnoreCase("NoYes"))&&(cntNew>=1)&&(cntInprogress==1)&&(cntComplete==1)){
                                	 retval=Globals.KEYWORD_PASS; 
                                	 sc.STAF_ReportEvent("Pass", "APILog Table", "Adjudicated notification message getting Successfully only for new endpoint", 0);
                                 }else{
                                	 sc.STAF_ReportEvent("Fail", "APILog Table", "Failed to retrived notification message, Please check API configuration tab with SendNotificationsviaApiLogTable and APIRetrievalNotifyResponseNewEndpoint Column in test data", 0);
                              	   retval=Globals.KEYWORD_FAIL;
                                 }
                        	     String restVal=sc.testSuiteXLS.getCellData_fromTestData("RestrictionValue");
                        	     String elgWithRestFlag=sc.testSuiteXLS.getCellData_fromTestData("VolunteerEligible");
                        	     if(elgWithRestFlag.equalsIgnoreCase("Eligible with Restrictions") && ( (colFlag.equalsIgnoreCase("YesNo"))||(colFlag.equalsIgnoreCase("YesYes")))){
                        	    	 if(restVal.equalsIgnoreCase(restrictionValue)){
                        	    		 sc.STAF_ReportEvent("Pass", "APILog Table", "Adjudicated Eligible with Restriction - getting restriction value as Expected: "+restVal, 0);
                        	    	 }else{
                        	    		 sc.STAF_ReportEvent("Fail", "APILog Table", "Adjudicated Eligible with Restriction - getting restriction value not as per Expected. Actual Value "+restrictionValue, 0);
                        	    	 }
                        	     }
                        	     
                        	     
                        	     
                           }else if(orderStatus.equalsIgnoreCase("Batch")){
                        	   sc.STAF_ReportEvent("Fail", "APILog Table", "Notification messages are sending for batch orders", 0);
                        	   
                           }else{
                        	   sc.STAF_ReportEvent("Fail", "APILog Table", "Unable to retrieve , Order status "+orderStatus+" is not matching with expected ", 0);
                        	   retval=Globals.KEYWORD_FAIL;
                           }
        
                         }//closed for if loop where while condition is not break                               
                     }    
                         
                   
                 
     }catch(Exception e){
                     e.printStackTrace();
                     sc.STAF_ReportEvent("Fail", "APILogTable-DB", "Unable to Retrieve data from APILog Table", 0);
     }finally{
                     vvDB.cleanUpVVDAO();
     }
     
                                     
     
     return retval;
     
     
}
	/**************************************************************************************************
	 * Method to get an order request xml for VV Api ordering
	 * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
	 * @author psave
	 * @created
	 * @LastModifiedBy
	 ***************************************************************************************************/
	
	public static String getOrderRequestXML(String ExpectedValue) throws Exception {
		APP_LOGGER.startFunction("getOrderRequest");
		String retval	=	Globals.KEYWORD_FAIL;
		String tempRetval	=	Globals.KEYWORD_FAIL;
		String ordstatus="";
		String ApiSendSearchResultTypes="";
		String searchesFlag="";
		String responseFilePath=null;
		try {

			String currentPlatform_Path 		=	 Globals.currentRunPath+"\\"+Globals.CurrentPlatform;
			String npnorderid="";
			  String fldSm                      =      Globals.testSuiteXLS.getCellData_fromTestData("Seamless");
              
              if(fldSm.equalsIgnoreCase("SM")){
           	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless"; 
              }else if(fldSm.equalsIgnoreCase("SM_Cons_EnSSN")) {
           	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless_Consent_EncrySSN"; 
              }else if(fldSm.equalsIgnoreCase("SM_Cons_EnSSN_SkipSSN")) {
           	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"SM_Cons_EnSSN_SkipSSN";
              }else if(fldSm.equalsIgnoreCase("SM_Cons_AESEnSSN")) {
           	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results"+"\\"+"Seamless_Consent_AESEncrySSN"; 
              }else if(fldSm.equalsIgnoreCase("N/A") || fldSm.equalsIgnoreCase("") || fldSm.isEmpty()){
           	   Globals.currentPlatform_XMLResults =      currentPlatform_Path+"\\"+"XML_Results";               	   
              }
            sc.mkDirs(Globals.currentPlatform_XMLResults);
            if(fldSm.equalsIgnoreCase("SM")||fldSm.equalsIgnoreCase("N/A")){
            	responseFilePath =	 Globals.currentPlatform_XMLResults + "\\" + Globals.TestCaseID +"_Response.xml";
            }else{
            	responseFilePath =	 Globals.currentPlatform_XMLResults + "\\" + Globals.TestCaseID +"_Change_Response.xml";
            }


			Globals.VV_API_UserName		=	Globals.testSuiteXLS.getCellData_fromTestData("APIUserName");
			Globals.VV_API_UserPwd		=   Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
			
			// when status is new then responxml get npnorder id for other status take from sheet
			
			XMLUtils oResponseXML = new XMLUtils(responseFilePath);
            npnorderid	=	oResponseXML.getXMLNodeValByTagName("q1:DocumentID");
            
            if(npnorderid.equalsIgnoreCase("")){
            	sc.STAF_ReportEvent("Fail", "API Order Status Retrieval", "Npnorder is blank", 3);
				return Globals.KEYWORD_FAIL;
            }
			
            String getUrl= Globals.getEnvPropertyValue("APIOrderresponse")+npnorderid;
            log.debug(" Method-API Order Status Retrieval | URL - " + getUrl);
			XMLUtils updatedXMLFile 	= new XMLUtils(getUrl, Globals.VV_API_UserName,Globals.VV_API_UserPwd);
			updatedXMLFile.getXML();	
			String orderResFilePath =	 Globals.currentPlatform_XMLResults + "\\" + Globals.TestCaseID +"_API Order Status Retrieval_"+ExpectedValue+".xml";
			tempRetval				=	updatedXMLFile.saveXMLString(orderResFilePath);
			sc.STAF_ReportEvent("Pass", "API Order Status Retrieval", "API Order Status Retrieval XML stored at XML_results folder.FileName--"+Globals.TestCaseID +"_API Order Status Retrieval_"+ExpectedValue+".xml", 3);

			XMLUtils orResponseXML = new XMLUtils(orderResFilePath);
			
			
			if(ExpectedValue.equalsIgnoreCase("New")){	
		        orResponseXML.verifyXMLNodeValueByTagName("q1:DocumentID",npnorderid,"API Order Status Retrieval - OrderID");
		        orResponseXML.verifyXMLNodeValueByTagName("Code", "New","API Order Status Retrieval - Status");
			}else if(ExpectedValue.equalsIgnoreCase("Hold")){
				orResponseXML.verifyXMLNodeValueByTagName("q1:DocumentID",npnorderid,"API Order Status Retrieval - OrderID");
				orResponseXML.verifyXMLNodeValueByTagName("Code", "Hold","API Order Status Retrieval - Status");
			}else if(ExpectedValue.equalsIgnoreCase("InProgress")){
				orResponseXML.verifyXMLNodeValueByTagName("q1:DocumentID",npnorderid,"API Order Status Retrieval - OrderID");
				orResponseXML.verifyXMLNodeValueByTagName("Code", "InProgress","API Order Status Retrieval - Status");
			}else if(ExpectedValue.equalsIgnoreCase("Complete")){
				orResponseXML.verifyXMLNodeValueByTagName("q1:DocumentID",npnorderid,"API Order Status Retrieval - OrderID");
				orResponseXML.verifyXMLNodeValueByTagName("Code", "Complete","API Order Status Retrieval - Status");
				ordstatus=Globals.testSuiteXLS.getCellData_fromTestData("ExpectedOrderScore");
				ApiSendSearchResultTypes=Globals.testSuiteXLS.getCellData_fromTestData("ApiSendSearchResultTypes");
				searchesFlag=Globals.testSuiteXLS.getCellData_fromTestData("Clear_Cons_Unper_OrderSearchTable_Flag");
				String hasConsider=(searchesFlag.split("-")[1].equalsIgnoreCase("1"))?"True":"False";
				String hasUnperf=(searchesFlag.split("-")[2].equalsIgnoreCase("1"))?"True":"False";
				if(ordstatus.equalsIgnoreCase("Clear")){
					orResponseXML.verifyXMLNodeValueByTagName("ReportResult", "Clear","API Order Status Retrieval - ReportResult");
				}
				if(ordstatus.equalsIgnoreCase("Consider")){
					orResponseXML.verifyXMLNodeValueByTagName("ReportResult", "Consider","API Order Status Retrieval - ReportResult");
				}
				if(ApiSendSearchResultTypes.equalsIgnoreCase("Yes")){
					orResponseXML.verifyXMLNodeValueByTagName("HasConsider", hasConsider,"ApiSendSearchResultTypes Retrieval - HasConsider");	
					orResponseXML.verifyXMLNodeValueByTagName("HasUnperformable", hasUnperf,"ApiSendSearchResultTypes Retrieval - HasUnperformable");
					
				}if(ApiSendSearchResultTypes.equalsIgnoreCase("No")){
					int i=orResponseXML.getNumberOfNodesPresentByTagName("HasConsider");
					int j=orResponseXML.getNumberOfNodesPresentByTagName("HasUnperformable");
					if(i!=0 || j!=0){
					    sc.STAF_ReportEvent("Fail", "Order Response XML", "HasConsider or HasUnperformable is getting displayed even ApiSendSearchResultTypes flag is false",0);	
					}
				}
			}else if(ExpectedValue.equalsIgnoreCase("Adjudicated")){
				orResponseXML.verifyXMLNodeValueByTagName("q1:DocumentID",npnorderid,"API Order Status Retrieval - OrderID");
				orResponseXML.verifyXMLNodeValueByTagName("Code", "Adjudicated","API Order Status Retrieval - Status");
				ordstatus=Globals.testSuiteXLS.getCellData_fromTestData("VolunteerEligible");
				if(ordstatus.equalsIgnoreCase("Eligible") || ordstatus.equalsIgnoreCase("Eligible with Restrictions") ){
					orResponseXML.verifyXMLNodeValueByTagName("ReportResult", "Eligible","API Order Status Retrieval ");
				}
				if(ordstatus.equalsIgnoreCase("InEligible")){
					orResponseXML.verifyXMLNodeValueByTagName("ReportResult", "InEligible","API Order Status Retrieval ");
				}
				 String restVal=sc.testSuiteXLS.getCellData_fromTestData("RestrictionValue");
        	   
        	     if(ordstatus.equalsIgnoreCase("Eligible with Restrictions")){
        	    	 orResponseXML.verifyXMLNodeValueByTagName("Restriction", restVal,"API Order Status Retrieval - Eligible with restriction");
        	    	
        	     }
				
				
				
			}
		
			
			//----------------------------------------------------------------------------------------------------------
			
			retval					= 	Globals.KEYWORD_PASS;

		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "Order Response XML", "Exception occurred during XML GET-Exception - "+ e.toString(),0);
			log.error("Exception occurred in getOrderRequestXML .  | "+e.toString());
			throw e;
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify search volunteer and its gui validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifySearchVolunteer() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 10;
		int counter = 0;
		String[] expDDitems={"Last Name","First Name","Email","Level/Screen","Position","Origin","Order Date","Latest Update","VV Order","Adjud Date","Volunteer Since","Status","Phone","Address City","Address State","Address Zip Code"}; 


		tempretval = sc.waitforElementToDisplay("volDashboard_searchBy_dd", timeOutinSeconds);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Search Volunteer", "Volunteer Dashboard page has not loaded", 1);
			throw new Exception("Search Volunteer-Volunteer Dashboard page has not loaded");
		}


		WebElement ddMenu = sc.createWebElement("volDashboard_searchBy_dd");
		List<WebElement> ddItems = ddMenu.findElements(By.xpath("./option"));
		int size = expDDitems.length;
		for (WebElement ddItem : ddItems){
			if(expDDitems[counter].equalsIgnoreCase(ddItem.getText())){
				sc.STAF_ReportEvent("Pass", "VVOU163 - Verify the presence of the all the fields in the Search dropdown list on the volunteer dashboard page.", "Search By parameter is as expected.Value-"+expDDitems[counter], 0);
			}else{
				sc.STAF_ReportEvent("Pass", "VVOU163 - Verify the presence of the all the fields in the Search dropdown list on the volunteer dashboard page.", "Search By parameter is not present in dropdown.Value-"+expDDitems[counter], 0);
			}
			counter ++;
			if(counter >= size){
				break;
			}
		}
		WebElement searchVal = sc.createWebElement("volDashboard_searhValue_txt");
		sc.verifyPlaceholderText(searchVal, "VVOU164 - Verify the presence of the inner text label for the Search Criteria text box.", "Search");

		sc.clickWhenElementIsClickable("volDashboard_search_btn", (int) timeOutinSeconds);

		WebElement volGrid =  sc.createWebElement("volDashboard_volgrid_tbl");
		int rowCount = sc.getRowCount_tbl(volGrid);
		if(rowCount > 2){
			sc.STAF_ReportEvent("Pass", "VVOU165 - Verify the Search Button functionality when the search text box is null.", "null search has displayed all rows", 1);
		}else{
			sc.STAF_ReportEvent("Fail", "VVOU165 - Verify the Search Button functionality when the search text box is null.", "null search has NOT displayed all rows", 1);
		}

		sc.selectValue_byVisibleText(ddMenu, "First Name");
		sc.setValueJsChange(searchVal, "@@@@@######");
		sc.clickWhenElementIsClickable("volDashboard_search_btn", (int) timeOutinSeconds);
		Thread.sleep(5000);
		rowCount = sc.getRowCount_tbl(volGrid);
		if(rowCount ==1 && volGrid.findElement(By.xpath("./tbody/tr/td")).getText().equalsIgnoreCase("No matching records.")){
			sc.STAF_ReportEvent("Pass", "VVOU166 - Verify the Search Button functionality when the user has entered the invalid value in Search text box.", "No Search result displayed", 1);
		}else{
			sc.STAF_ReportEvent("Fail", "VVOU166 - Verify the Search Button functionality when the user has entered the invalid value in Search text box", "Search results have displayed", 1);
		}

		//unhide all columns
		//			sc.clickWhenElementIsClickable("volDashboard_showColumns_btn", (int) timeOutinSeconds);
        showColumn();

		String reportingStepName;
		
		reportingStepName="VVOU173 - Verify the Search Criteria for volunteers Invitation Date on the volunteer dashboard page.";
		retval = searchVolunteer("Invitation Date","2016","Invitation Date","2016", reportingStepName);
		
		String invDateToSearch="3/30/2016";
		if(Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA3")){
			invDateToSearch="4/12/2016";
		}
		
		reportingStepName="VVOU173 - Verify the Search Criteria for volunteers Invitation Date on the volunteer dashboard page.";
		retval = searchVolunteer("Invitation Date",invDateToSearch,"Invitation Date",invDateToSearch, reportingStepName);
		
		invDateToSearch="9/14/2017-9/20/2018";
		if(Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA3")){
			invDateToSearch="8/23/2017-8/28/2018";
		}
        
		reportingStepName="VVOU173 - Verify the Search Criteria for volunteers Invitation Date on the volunteer dashboard page.";
		retval = searchVolunteer("Invitation Date",invDateToSearch,"Invitation Date",invDateToSearch, reportingStepName);
		
		reportingStepName="VVOU167 - Verify the Search Criteria for volunteers Last Name on the volunteer dashboard page.";
		retval = searchVolunteer("Last Name","test","Name","test", reportingStepName);

		reportingStepName="VVOU168 - Verify the Search Criteria for volunteers First Name on the volunteer dashboard page.";
		retval = searchVolunteer("First Name","test","Name", "test",reportingStepName);

		reportingStepName="VVOU169 - Verify the Search Criteria for volunteers Email on the volunteer dashboard page.";
		retval = searchVolunteer("Email","sterling","Email","sterling", reportingStepName);

		reportingStepName="VVOU170 - Verify the Search Criteria for volunteers Level Screen on the volunteer dashboard page.";
		retval = searchVolunteer("Level/Screen","L1","Level/ Screen","L1", reportingStepName);

		reportingStepName="VVOU171 - Verify the Search Criteria for volunteers Position on the volunteer dashboard page.";
		retval = searchVolunteer("Position","Basic Criminal Locator Search","Position(s)","Basic Criminal Locator Search", reportingStepName);

		reportingStepName="VVOU172 - Verify the Search Criteria for volunteers Origin on the volunteer dashboard page.";
		retval = searchVolunteer("Origin","Integration","Origin", "Integration",reportingStepName);

		reportingStepName="VVOU173 - Verify the Search Criteria for volunteers Order Date on the volunteer dashboard page.";
		retval = searchVolunteer("Order Date","2018","Order Date","2018", reportingStepName);

		reportingStepName="VVOU175 - Verify the Search Criteria for volunteers VV Order on the volunteer dashboard page.";
		String searchString = Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
		retval = searchVolunteer("VV Order",searchString,"VV Order",searchString, reportingStepName);

		reportingStepName="VVOU178 - Verify the Search Criteria for volunteers Status on the volunteer dashboard page.";
		retval = searchVolunteer("Status","Background Check Pending","Status","Background Check Pending", reportingStepName);
		return retval;


	}
	/**************************************************************************************************
	 * Method to verify search volunteer and its gui validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifySearchVolunteerViews(String viewInvalidSearchName,String viewSearchName, String clientViewName) throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 10;
		int counter = 0;
		String[] expDDitems={"Last Name","First Name","Email","Level/Screen","Position","Origin","Order Date","Latest Update","VV Order","Adjud Date","Volunteer Since","Status","Views","Phone","Address City","Address State","Address Zip Code"}; 


		tempretval = sc.waitforElementToDisplay("volDashboard_searchBy_dd", timeOutinSeconds);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Search Volunteer", "Volunteer Dashboard page has not loaded", 1);
			throw new Exception("Search Volunteer-Volunteer Dashboard page has not loaded");
		}

		WebElement ddMenu = sc.createWebElement("volDashboard_searchBy_dd");
		WebElement searchVal = sc.createWebElement("volDashboard_searhValue_txt");
	
		sc.verifyPlaceholderText(searchVal, "Verify the presence of the inner text label for the Search Criteria text box for Views option", "Search");
		sc.selectValue_byVisibleText(ddMenu, "Views");
		sc.setValueJsChange(searchVal, viewInvalidSearchName); 
		
		sc.clickWhenElementIsClickable("volDashboard_search_btn", (int) timeOutinSeconds);
				
		tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

		while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
			Thread.sleep(2000);
			tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

		}

		WebElement volGrid =  sc.createWebElement("volDashboard_volgrid_tbl");
		int rowCount = sc.getRowCount_tbl(volGrid);
		
		if(rowCount ==1 && volGrid.findElement(By.xpath("./tbody/tr/td")).getText().equalsIgnoreCase("No matching records.")){
			sc.STAF_ReportEvent("Pass", "Verify the Search Button functionality for Client Views - "+clientViewName+" when the search text box having invalid value.", "search has displayed all rows", 1);
			retval=Globals.KEYWORD_PASS;
			searchVal.clear();
		}else{
			sc.STAF_ReportEvent("Fail", "Verify the Search Button functionality for Client Views - "+clientViewName+" when the search text box having invalid value.", "search has NOT displayed all rows", 1);
		}
		
	  
		if(!(clientViewName.equalsIgnoreCase("Candidates with no Views assigned"))){
			String reportingStepName;
			reportingStepName="Verify the Search Criteria for volunteers Views on the volunteer dashboard page.";
			retval = searchVolunteer("Views",viewSearchName,"Views",viewSearchName, reportingStepName);
			
		}
		return retval;


	}
	/**************************************************************************************************
	 * Method to verify search volunteer and select volunteer to open Volunteer profile page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifySearchVolunteerandclick() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 10;
		
		tempretval = sc.waitforElementToDisplay("volDashboard_searchBy_dd", timeOutinSeconds);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Search Volunteer", "Volunteer Dashboard page has not loaded", 1);
			throw new Exception("Search Volunteer-Volunteer Dashboard page has not loaded");
		}


		WebElement ddMenu = sc.createWebElement("volDashboard_searchBy_dd");
		
		WebElement searchVal = sc.createWebElement("volDashboard_searhValue_txt");
		
		sc.clickWhenElementIsClickable("volDashboard_search_btn", (int) timeOutinSeconds);

				
		String volFname = "";
		String volLname = "";
		volFname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
		volLname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
		sc.selectValue_byVisibleText(ddMenu, "First Name");
		sc.setValueJsChange(searchVal, volFname);
	
		sc.clickWhenElementIsClickable("volDashboard_search_btn", (int) timeOutinSeconds);
		Thread.sleep(5000);
		
		WebElement volGrid =  sc.createWebElement("volDashboard_volgrid_tbl");
		int rowCount = sc.getRowCount_tbl(volGrid);
			
		if(rowCount != 1){
				int flag=0;
				
				while(rowCount != 1){
					sc.setValueJsChange(searchVal, volFname);
					sc.clickWhenElementIsClickable("volDashboard_search_btn", (int) timeOutinSeconds);

					tempretval = Globals.KEYWORD_FAIL;
					tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

					while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
						Thread.sleep(2000);
						tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

					}
					volGrid = sc.createWebElement("volDashboard_volgrid_tbl");
					rowCount =0;
					rowCount = sc.getRowCount_tbl(volGrid);
					if(flag>3){
						break;
					}
					flag++;
				}			
						
		}
		
		if(rowCount != 1){			
			sc.STAF_ReportEvent("Fail", "Volunteer Dashboard Dashboard", "Multiple volunteers has been found during search.Unable to select the appropriate volunteer", 1);
		}else {
			   String noMatchRecords = "";
			   noMatchRecords=	volGrid.findElement(By.xpath("./tbody/tr[1]/td[1]")).getText();
			   String errorText="No matching records.";
			   if(errorText.equalsIgnoreCase(noMatchRecords)){
				     sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Unable to Search volunteer by FirstName.No match found.FirstName-"+volFname, 1);
			   }else{
				    //verify volunteer name and order date,position and results
					String volNameUI = volGrid.findElement(By.xpath("./tbody/tr[1]/td[2]")).getText();
					
					String expVolName = volLname +", "+volFname;
					if(expVolName.equalsIgnoreCase(volNameUI)){
										sc.STAF_ReportEvent("Pass", "Volunteer Search - Volunteer Dashboard", "Volunteer searched successfully.Name-"+expVolName, 1);
										volGrid.findElement(By.xpath("./tbody/tr[1]/td[2]/a")).click();
										tempretval = sc.waitforElementToDisplay("volDashboard_volunteerprile_txt", timeOutinSeconds);
										if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
											volunteerProf_clickLink_NavigateAndVerify("volDashboard_volunteerprile_Summary_link", "bgReport_volInfoSection_list", "Volunteer Profile - Verify the Summary BG report page");
											volunteerProf_clickLink_NavigateAndVerify("volDashboard_volunteerprile_Detail_link", "bgReport_volInfoSection_list", "Volunteer Profile - Verify the Detail BG report page");			
											volunteerProf_clickLink_NavigateAndVerify("volDashboard_volunteerprile_Consent_link", "volDashboard_volunteerprile_Consent_txt", "Volunteer Profile - Verify the Consent page");
											String questFlag=Globals.testSuiteXLS.getCellData_fromTestData("Questionnaire");
											if(questFlag.equalsIgnoreCase("yes")){
												sc.clickWhenElementIsClickable("volDashboard_volunteerprile_Questionnaire_link", (int)timeOutinSeconds);
												switchTabAndVerifyQuestionnaireTab();
											}
											sc.clickWhenElementIsClickable("volunteerProfile_close_btn",(int)timeOutinSeconds);
										}else{
											sc.STAF_ReportEvent("Fail", "Volunteer Profile", "Volunteer Dashboard - Volunteer Profile page has not loaded", 1);
											throw new Exception("Volunteer Dashboard - Volunteer Profile page has not loaded");
										}
					}else{
			        	  sc.STAF_ReportEvent("Pass", "Volunteer Search - Volunteer Dashboard", "Volunteer searched successfully.Name-"+expVolName, 1);
			          }
					
		            }
			   retval=Globals.KEYWORD_PASS;
		}	

		return retval;
	}
	/**************************************************************************************************
	 * Method to Click on link present on volunteer profile page which open on new tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String volunteerProf_clickLink_NavigateAndVerify(String objName2beCLicked,String objName2BeVerified,String reporterStepName) throws Exception {
		
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		String tempRetval1=Globals.KEYWORD_FAIL;
		
		long timeOutinSeconds=60;
		ArrayList<String> tabs =null;
		try{
			sc.clickWhenElementIsClickable(objName2beCLicked, 5);
			
			tabs = new ArrayList<String> (driver.getWindowHandles());
			
			if (tabs.size() > 1){
				driver.switchTo().window(tabs.get(1));
			}
			String questFlag=Globals.testSuiteXLS.getCellData_fromTestData("Questionnaire");
			String param="org user";
			if(questFlag.equalsIgnoreCase("yes")){
				param="badge";
			}
						
			if(sc.isDisplayed(objName2BeVerified).equalsIgnoreCase(Globals.KEYWORD_PASS)){
				
				if (objName2beCLicked.equalsIgnoreCase("volDashboard_volunteerprile_Summary_link")){
					retval=bgReport_verifyReportHeader_OrgUser();
					bgReport_verifyLinkReadReport_BGReport("bgReport_VolReportRead_link");
					retval=bgReport_verifyClientInfo(false);
					retval=bgReport_verifyVolunteerHeaderInfo(true);
					retval=bgReport_verifyVolunteerInfo();
					retval=bgReport_verifyQuickViewText(param);
					retval=bgReport_verifyBackgroundCheckReportSummary();
					tempRetval = sc.waitforElementToDisplay("bgReport_productSummary_text", 2);
					tempRetval1 = sc.waitforElementToDisplay("bgReport_generalDisclaimer_text", 2);
					if((tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)) && (tempRetval1.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
						retval = Globals.KEYWORD_PASS;
						sc.STAF_ReportEvent("Pass", "Summary BG Report", "Volunteer Summary BG Report page displayed correctly", 1);
					}else{
						sc.STAF_ReportEvent("Fail", "Summary BG Report", "Volunteer Summary BG Report page displayed as detail report page", 1);
					}
				}else if(objName2beCLicked.equalsIgnoreCase("volDashboard_volunteerprile_Detail_link")) {
					retval=bgReport_verifyReportHeader_OrgUser();
					bgReport_verifyLinkReadReport_BGReport("bgReport_VolReportRead_link");
					retval=bgReport_verifyClientInfo(false);
					retval=bgReport_verifyVolunteerHeaderInfo(false);
					retval=bgReport_verifyVolunteerInfo();
					retval=bgReport_verifyQuickViewText(param);
					retval=bgReport_verifyBackgroundCheckReportSummary();
					retval=bgReport_verifyPoductSumary();	
					
				}else if (objName2beCLicked.equalsIgnoreCase("volDashboard_volunteerprile_Consent_link")) {
					sc.STAF_ReportEvent("Pass", reporterStepName, "The Consent page is displayed on new tab", 1);
				}
				else{
					sc.STAF_ReportEvent("Fail", reporterStepName, " page is not displayed on new tab", 1);
				}
				
			}else{
				sc.STAF_ReportEvent("Fail", reporterStepName, " page is not displayed on new tab", 1);
			}
		
			
		}catch(Exception e){
			driver.switchTo().defaultContent();
		}
		
				
		if (tabs.size() > 1){
			for(int i = 1;i<tabs.size();i++){
				driver.switchTo().window(tabs.get(i));
				driver.close();
			}
		}
		
		driver.switchTo().window(tabs.get(0));
		
		return retval;
		
	}

	
	
	/**************************************************************************************************
	 * Method to search a volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String searchVolunteer(String searchCriteria,String searchVal,String colName2beVerified,String colVal2beVerified,String reportingStepName) throws Exception {
		String retval =  Globals.KEYWORD_FAIL;
		String tempretval =  Globals.KEYWORD_FAIL;
		WebElement volGrid =  sc.createWebElement("volDashboard_volgrid_tbl");
		WebElement searchValTxt = sc.createWebElement("volDashboard_searhValue_txt");

		WebElement ddMenu = sc.createWebElement("volDashboard_searchBy_dd");
		int timeOutinSeconds = 10;
		tempretval=verifyOrgUserClientHierarchyDD();
		
		//get col index
		List<WebElement> tblHeaders = volGrid.findElements(By.xpath("./thead//tr[@class='trTop']/th"));
		int colIndex = 0;
		int i =0;
		boolean colFound = false;
		colName2beVerified = colName2beVerified.trim();
		for(i=0;i<tblHeaders.size();i++){
			if(colName2beVerified.equalsIgnoreCase(tblHeaders.get(i).getText())){
				colIndex = i;
				colFound = true;
				break;
			}
		}
		if(!colFound){
			sc.STAF_ReportEvent("Fail", reportingStepName, "Unable to search as column not found.Column-"+colName2beVerified, 0);
		}else{
			sc.selectValue_byVisibleText(ddMenu, searchCriteria);
			sc.setValueJsChange(searchValTxt, searchVal);
			sc.clickWhenElementIsClickable("volDashboard_search_btn", timeOutinSeconds);
			Thread.sleep(2000);
			tempretval = Globals.KEYWORD_FAIL;
			tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

			while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(4000);
				tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

			}

			int rowCount = 0;
			rowCount = sc.getRowCount_tbl(volGrid);
			String colVal = "";
			WebElement searchCol = null;
			boolean mismatchFound = false;
			colIndex = colIndex +1;
			for(i=1;i<=rowCount;i++){
				searchCol = volGrid.findElement(By.xpath("./tbody/tr["+i+"]/td["+colIndex+"]"));
				colVal = searchCol.getText().trim();
				if(!searchVal.contains("-")){
					if(!colVal.toLowerCase().contains(colVal2beVerified.toLowerCase())){
						mismatchFound = true;
						sc.scrollIntoView(searchCol);
						break;
					}
				}else{
					 String arr[]=colVal2beVerified.split("-");
					 Date expDate1=new SimpleDateFormat("MM/dd/yyyy").parse(arr[0].trim());
					 Date expDate2=new SimpleDateFormat("MM/dd/yyyy").parse(arr[1].trim());
					 Date actDate=new SimpleDateFormat("MM/dd/yyyy").parse(colVal);  
					 if(!((actDate.after(expDate1) && (actDate.before(expDate2))) || (actDate.equals(expDate1) ||actDate.equals(expDate2)))){
							mismatchFound = true;
							sc.scrollIntoView(searchCol);
							break;
					}
				}
			}
			if(mismatchFound){
				sc.STAF_ReportEvent("Fail", reportingStepName, "Mismatch found in search result. SearchCriteria-"+searchCriteria+" SearchVal ="+searchVal, 1);
			}else{
				sc.STAF_ReportEvent("Pass", reportingStepName, "Search result as expected. SearchCriteria-"+searchCriteria+" SearchVal ="+searchVal, 1);
				retval = Globals.KEYWORD_PASS;
                searchValTxt.clear();     
			}
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to edit a volunteer in volunteer dashboard page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String editVolunteer() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		String tempretval1=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 10;

		
		tempretval = sc.waitforElementToDisplay("volDashboard_editVolunteer_btn", timeOutinSeconds);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "VVOU91 - Verify the presence of the Edit Candidate Menu button on the Volunteer dashboard page.", "Edit Candidate Menu button is not displayed", 1);
			throw new Exception("Search Volunteer-Volunteer Dashboard page has not loaded");
		}

		sc.STAF_ReportEvent("Pass", "Verify the presence of the Edit Candidate Menu button on the Volunteer dashboard page.", "Edit Candidate Menu button is displayed on volunteer dashboard page", 1);
		sc.clickWhenElementIsClickable("volDashboard_editVolunteer_btn", (int) timeOutinSeconds);
		tempretval = sc.isEnabled("volDashboard_editCandidate_btn");
		tempretval1=sc.isEnabled("volDashboard_MassUpdates_btn");
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS) && tempretval1.equalsIgnoreCase(Globals.KEYWORD_FAIL) ){
			sc.STAF_ReportEvent("Pass", "Verify the Edit Candidate button should be enabled and Mass Update Button by default disabled on the Volunteer dashboard page.", "Edit Candidate button is enabled and Mass Update Button are disabled", 1);
		}else{
			sc.STAF_ReportEvent("Fail", "Verify the Edit Candidate button and Mass Update Button by default disabled on the Volunteer dashboard page.", "Edit Candidate button and Mass Update Button are not disabled", 1);

		}
		WebElement volTbl = sc.createWebElement("volDashboard_volgrid_tbl");
		volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input")).click();
		//					By.xpath("//*[@id='dashboard']/tbody/tr[1]/td[1]/input")).click();
		sc.clickWhenElementIsClickable("volDashboard_editVolunteer_btn", (int) timeOutinSeconds);
		
		tempretval = sc.isEnabled("volDashboard_editCandidate_btn");
		tempretval1=sc.isEnabled("volDashboard_MassUpdates_btn");
		//&& volDashboard_MassUpdates_btn");
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS) && tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", "Verify the presence of the Edit Candidate button and Mass Update button", "Edit Volunteer and Mass Update button is Enabled ", 1);
		}else{
			sc.STAF_ReportEvent("Fail", "Verify the presence of the Edit Candidate button and Mass Update button.", "Edit Volunteer and Mass Update button is not Enabled ", 1);
		}
		
		String reportingStepName;

		reportingStepName="VVOU97 - Verify the presence of the editable fields on the volunteer dashboard page when the record is in Background check pending status.";
		retval = checkForEditableFields_VolDashboard("Status","Background Check Pending","Status", reportingStepName);


		reportingStepName="VVOU94 - Verify the presence of the editable fields on the volunteer dashboard page when the record is in open invitation status.";
		retval = checkForEditableFields_VolDashboard("Status","Open Invitation","Status", reportingStepName);

		reportingStepName="VVOU98 - Verify the presence of the editable fields on the volunteer dashboard page when the record is in Pending review status.";
		retval = checkForEditableFields_VolDashboard("Status","Pending Review","Status", reportingStepName);

		reportingStepName="VVOU99 - Verify the presence of the editable fields on the volunteer dashboard page when the record is in Eligible status.";
		retval = checkForEditableFields_VolDashboard("Status","Eligible","Status", reportingStepName);

		reportingStepName="VVOU100 - Verify the presence of the editable fields on the volunteer dashboard page when the record is in InEligible status.";
		retval = checkForEditableFields_VolDashboard("Status","Ineligible","Status", reportingStepName);


		return retval;


	}

	/**************************************************************************************************
	 * Method to check for editable fields for a volunteer in volunteer dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String checkForEditableFields_VolDashboard(String searchCriteria,String searchVal,String colName,String reportingStepName) throws Exception{
		String retval = searchVolunteer(searchCriteria,searchVal,colName, searchVal,reportingStepName);
		int timeOutInSeconds = 10;
		String volSinceVal = "01/01/2015";
		

		if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			WebElement volTbl = sc.createWebElement("volDashboard_volgrid_tbl");
			
			
			int counter = 0;
			while (counter<=60) {
				
				if(sc.isSelected(volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input"))).equalsIgnoreCase(Globals.KEYWORD_PASS)){
					break;
				}else{
					
					sc.scrollIntoViewUsingJavaScript(volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input")));
					sc.clickElementUsingJavaScript(volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input")));
					log.info("Element -"+volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input")).toString() + "| Checkbox Javascript click iteration "+counter);
					Thread.sleep(1000);
					counter++;

				}
			}
			    sc.clickWhenElementIsClickable("volDashboard_editVolunteer_btn", 10); 
				if(sc.isEnabled("volDashboard_editCandidate_btn").equalsIgnoreCase(Globals.KEYWORD_PASS)){
				
					sc.scrollIntoViewUsingJavaScript("volDashboard_editCandidate_btn");
					sc.clickWhenElementIsClickable("volDashboard_editCandidate_btn", timeOutInSeconds);
					log.info("Element -"+driver.findElement(LocatorAccess.getLocator("volDashboard_editVolunteer_btn")).toString() + "|Edit button Javascript click iteration"+counter);
				}
			
			
			//JavascriptExecutor js = (JavascriptExecutor)driver;
			//js.executeScript("arguments[0].style.display = 'block';arguments[0].style.height='auto';arguments[0].style.visibility = 'visible';",volTbl.findElement(By.xpath(".//tr[1]/td[12]/div/input")));
			
			//WebElement volSince = volTbl.findElement(By.xpath(".//tr[1]/td[12]/div/input"));
			//			sc.setValueJsChange(volSince, volSinceVal);
			//Identify the element-pass value-sendkeys-//table[@class='dashboard table js_canBeFixed original']
			
			WebElement Customfield1 = volTbl.findElement(By.xpath(".//tbody/tr[1]/td[20]/input"));
			sc.waitTillElementDisplayed(Customfield1, 10);
			String inputstr= RandomStringUtils.randomAlphabetic(5);
			

			sc.scrollIntoViewUsingJavaScript(Customfield1);
			Customfield1.clear();
			sc.setValueJsChange(Customfield1, inputstr);
			
			
			String uiTexta = volTbl.findElement(By.xpath(".//tbody/tr[1]/td[20]/input")).getAttribute("value");
			WebElement Save=volTbl.findElement(By.xpath("//tr[1]/td[24]/a[1]"));
			sc.waitTillElementDisplayed(Save, 10);
			jsExecutor.executeScript("window.scrollTo(0,"+Save.getLocation().y+")");
			sc.clickElementUsingJavaScript(Save);
			
			if(inputstr.equalsIgnoreCase(uiTexta)){
				sc.STAF_ReportEvent("Pass", "VVOU95 - Verify the behaviour of custom field", "Data saved", 1);
				retval = Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "VVOU95 - Verify the behaviour of custom field", "Data not saved", 1);
				retval = Globals.KEYWORD_FAIL;
			}
			
			//WebElement volSince = volTbl.findElement(By.xpath(".//tr[1]/td[12]/div/input"));
			
			//JavascriptExecutor js = (JavascriptExecutor)driver;
			//js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';",volSince);
			//js.executeScript("arguments[0].value='01/01/2015';", volSince);
			//Thread.sleep(3000);
			//volSince.sendKeys(volSinceVal);
			//js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';arguments[0].click();",volTbl.findElement(By.xpath("//tr[1]/td[25]/a[1]")));
			

		/*	String uiText = volTbl.findElement(By.xpath("./tbody/tr[1]/td[12]")).getText();
			if(volSinceVal.equalsIgnoreCase(uiText)){
				sc.STAF_ReportEvent("Pass", "VVOU95 - Verify the behavior of the save button on the volunteer dashboard page Action Column.", "Data saved", 1);
				retval = Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "VVOU95 - Verify the behavior of the save button on the volunteer dashboard page Action Column.", "Data not saved", 1);
				retval = Globals.KEYWORD_FAIL;
			}
*/
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to edit volunteers in volunteer dashboard page using mass update
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author Psave
	 * @throws Exception
	 ***************************************************************************************************/
	 public static String massUpdateVolunteers() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		String tempretval1=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 10;
		String npnOrderIds[]=new String[2];
		String dynamicData="";
		int counter=0;

        try{
        	      
        	changeStatusVerify("volDashboard_ineligible_link","InEligible","Eligible","volDashboard_eligible_link");
        	changeStatusVerify("volDashboard_eligible_link","Eligible","Inactive","volDashboard_inactive_link");
        	changeStatusVerify("volDashboard_inactive_link","Inactive","Eligible","volDashboard_eligible_link");
        	changeStatusVerify("volDashboard_pendingReview_link","Pending Review","None","");
        	//String arr[]={"volDashboard_openInvitation_link;FAIL;Invitation Sent;Invitations must be canceled before changing the status."};
        	//String arr[]={"volDashboard_pendingReview_link;FAIL;Pending Review;Candidate status cannot be changed."};
        	
        	String arr[]={"volDashboard_openInvitation_link;FAIL;Invitation Sent;Invitations must be canceled before changing the status.","volDashboard_pendingReview_link;FAIL;Pending Review;Candidate status cannot be changed.","volDashboard_viewAllVolunteers_link;FAIL;All Status;All selected candidates must be in the same status to edit this value."};
            for(String inputs:arr){
                verifyMassUpdateCancelButton(inputs);
            }
        	     	
        	retval=Globals.KEYWORD_PASS;
        }catch(Exception e){
            sc.STAF_ReportEvent("Fail", "MassUpdate Volunteers", "Exception occurred during Mass Update volunteers-Exception - "+ e.toString(),0);
            log.error("xception occurred during Mass Update volunteers .  | "+e.toString());
            throw e;
          }
            return retval;
        	
        }
	 /**************************************************************************************************
		 * Method to verify mass update window cancel button functionality
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author Psave
		 * @throws Exception
		 ***************************************************************************************************/
		 public static String verifyMassUpdateCancelButton(String inputs) throws Exception {
			Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
			APP_LOGGER.startFunction(Globals.Component);
			String retval=Globals.KEYWORD_FAIL;
			String tempretval=Globals.KEYWORD_FAIL;
			String tempretval1=Globals.KEYWORD_FAIL;
			int timeOutinSeconds = 10;
			String npnOrderIds[]=new String[2];
			String dynamicData="";
			int counter=0;
			boolean flg=false;
			String input[]=inputs.split(";");

	        try{
	        	sc.clickWhenElementIsClickable(input[0], timeOutinSeconds);
	        	searchWaitPopup();
				
	        	//sc.clickWhenElementIsClickable("volDashboard_minimumRowSet_link", timeOutinSeconds);
	        	WebElement volTbl = sc.createWebElement("volDashboard_volgrid_tbl");
	        	if(input[2].equalsIgnoreCase("All Status")){
	        		tempretval=sc.checkCheckBox("volDashboard_Selectall_chk");
		 			int runFlag=0;
		 			while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
						Thread.sleep(2000);
						tempretval = sc.checkCheckBox("volDashboard_Selectall_chk");
						if(runFlag==4){
							break;
						}
						runFlag=runFlag+1; 
					
		 			}
		 			searchWaitPopup();
		 			sc.waitForPageLoad();
		 			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
		 				sc.STAF_ReportEvent("Pass", "Verify Mass update Window-Cancel Functionality-"+input[2], "volunteers selected successfully for "+input[2], 1);
		 			}else{
		 				sc.STAF_ReportEvent("Fail", "Verify Mass update Window-Cancel Functionality-"+input[2], "volunteers not selected successfully for "+input[2], 1);
		 				return retval;
		 			}
	        	}else{
	        		while (counter<=60) {
					
	        			if(sc.isSelected(volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input"))).equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.isSelected(volTbl.findElement(By.xpath("./tbody/tr[2]/td[1]/input"))).equalsIgnoreCase(Globals.KEYWORD_PASS)){
	        				flg=true;
	        				break;
	        			}else{
						
	        				sc.scrollIntoViewUsingJavaScript(volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input")));
	        				searchWaitPopup();
	        				sc.clickElementUsingJavaScript(volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input")));
	        				Thread.sleep(2000);
	        				sc.clickElementUsingJavaScript(volTbl.findElement(By.xpath("./tbody/tr[2]/td[1]/input")));
	        				log.info("Element -"+volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input")).toString() + "| Checkbox Javascript click iteration "+counter);
	        				Thread.sleep(1000);
	        				counter++;

						}
	        		}
	        		if(flg==true){
		        		npnOrderIds[0]=sc.getWebElementText(volTbl.findElement(By.xpath("./tbody/tr[1]/td[contains(@data-bind,'NpnOrderId')]"))).trim();
			        	npnOrderIds[1]=sc.getWebElementText(volTbl.findElement(By.xpath("./tbody/tr[2]/td[contains(@data-bind,'NpnOrderId')]"))).trim();
		        		sc.STAF_ReportEvent("Pass", "Verify Mass update Window-Cancel Functionality-"+input[2], "2 volunteers selected successfully VVOrder : "+npnOrderIds[0]+", "+npnOrderIds[1], 1);	
		        	}else{
		        		sc.STAF_ReportEvent("Fail", "Verify Mass update Window-Cancel Functionality-"+input[2], "Unable to select 2 volunteers for mass update" , 1);
		        		return retval;
		        	}
	        	}
	        	
	        	
	        	sc.clickWhenElementIsClickable("volDashboard_editVolunteer_btn", 10); 
	        	tempretval=sc.isEnabled("volDashboard_editCandidate_btn");
	        	if (tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
	        		sc.STAF_ReportEvent("Fail", "Verify Mass update Window-EditCandidate Button", "editCandidate button is not in disabled mode " , 1);
	        	}
	        	
	        	AccountSetting.clickButtonUntilValidation("volDashboard_MassUpdates_btn", "massUpdates_Save_btn");
	        	tempretval=sc.isEnabled("massUpdates_Status_dd");
	        	String statusNm=sc.getWebText("massUpdates_Status_dd");
	        	String allSts=(input[2].equalsIgnoreCase("All Status"))? "":input[2];
	        	
	        	String statusDDlbl=driver.findElement(By.xpath("//*[@id='Status']/../span")).getText();
	        	if(!tempretval.contains(input[1])){
	        		String enblDis=input[1];
	        		if(enblDis.equalsIgnoreCase("fail")){
	        			sc.STAF_ReportEvent("Fail", "Verify Mass update Window-Cancel Functionality-"+input[2], "Status Dropdown is displayed as Enabled for Status"+input[2] , 1);
	        		}else if(enblDis.equalsIgnoreCase("pass")){
	        			sc.STAF_ReportEvent("Fail", "Verify Mass update Window-Cancel Functionality-"+input[2], "Status Dropdown is displayed as Disabled for Status"+input[2] , 1);
	        		}
	        	}else if(!statusNm.trim().equalsIgnoreCase(allSts)){
	        		sc.STAF_ReportEvent("Fail", "Verify Mass update Window-Cancel Functionality-"+input[2], "Status dropdown name is not matched with expected. expected: "+input[2] , 1);
	        	}else if(!statusDDlbl.equalsIgnoreCase(input[3])){
	        		sc.STAF_ReportEvent("Fail", "Verify Mass update Window-Cancel Functionality-"+input[2], "Status dropdown label text is not matched with expected. expected: "+input[3] , 1);
	        	}
	        	
	        	driver.findElement(By.xpath("//input[@id='candidateSinceDate']")).sendKeys(Keys.ENTER);
	            driver.findElement(By.xpath("//input[@id='candidateSinceDate']")).sendKeys(Keys.TAB);
	            String enterData="zxcvbnmASDFGHJKL1234567890)(*&^%$#@!qazwsxedcrtyhBj";
	            
	            sc.setValue("massUpdates_Custom1_txt", enterData);
	            sc.setValue("massUpdates_Custom2_txt", enterData);
	            sc.setValue("massUpdates_Custom3_txt", enterData);
	         	    
	            if(!(sc.verifyProperty("massUpdates_Custom1_txt", "value", enterData.substring(0, 50)).equalsIgnoreCase("pass"))||!(sc.verifyProperty("massUpdates_Custom2_txt", "value", enterData.substring(0, 50)).equalsIgnoreCase("pass"))||!(sc.verifyProperty("massUpdates_Custom3_txt", "value", enterData.substring(0, 50)).equalsIgnoreCase("pass"))){
	            	sc.STAF_ReportEvent("Fail", "Verify Mass update Window-Cancel Functionality-"+input[2], "Verify Mass Update window functionality for status "+input[2] , 1);
	            }else{
	            	sc.STAF_ReportEvent("Pass", "Verify Mass update Window-Cancel Functionality-"+input[2], "Verify Mass Update window functionality for status "+input[2] , 1);
	            }
	            
	            sc.clickElementUsingJavaScript("massUpdates_Cancel_btn");
	            searchWaitPopup();
	            sc.waitForPageLoad();
	            tempretval=sc.isDisplayed("massUpdates_Cancel_btn");
	            if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
	            	sc.STAF_ReportEvent("Fail", "Verify Mass update Window-Cancel Functionality-"+input[2], "Unable to click on Cancel button", 1);	
	            	throw new Exception("Unable to click on Cancel button on mass update window");
	            }
	            
	            sc.clickWhenElementIsClickable("volDashboard_editVolunteer_btn", 10); 
	        	tempretval=sc.isEnabled("volDashboard_editCandidate_btn");
	          	AccountSetting.clickButtonUntilValidation("volDashboard_MassUpdates_btn", "massUpdates_Save_btn");	
	          	String cust1=sc.getWebText("massUpdates_Custom1_txt");
				String cust2=sc.getWebText("massUpdates_Custom2_txt");
				String cust3=sc.getWebText("massUpdates_Custom3_txt");
				String candSince=driver.findElement(By.xpath("//input[@id='candidateSinceDate']")).getText();
				if(!(sc.verifyProperty("massUpdates_Custom1_txt", "value", "").equalsIgnoreCase("pass"))||!(sc.verifyProperty("massUpdates_Custom2_txt", "value", "").equalsIgnoreCase("pass"))||!(sc.verifyProperty("massUpdates_Custom3_txt", "value", "").equalsIgnoreCase("pass"))||!(candSince.equalsIgnoreCase(""))){
		            	sc.STAF_ReportEvent("Fail", "Verify Mass update Window-Cancel Functionality-"+input[2], "After click on cancel getting all fields are not blank" , 1);
		        }else{
		        	sc.STAF_ReportEvent("Pass", "Verify Mass update Window-Cancel Functionality-"+input[2], "After click on cancel getting all fields are blank as per expected" , 1);
		        }
				sc.clickElementUsingJavaScript("massUpdates_Cancel_btn");
	            searchWaitPopup();
	            sc.waitForPageLoad();
	            tempretval=sc.isDisplayed("massUpdates_Cancel_btn");
	            if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
	            	sc.STAF_ReportEvent("Fail", "Verify Mass update Window-Cancel Functionality-"+input[2], "Unable to click on Cancel button", 1);	
	            	throw new Exception("Unable to click on Cancel button on mass update window");
	            } 
	            
	            if(input[2].equalsIgnoreCase("Invitation Sent")||input[2].equalsIgnoreCase("All Status") ){
	            	for(int i=1;i<=2;i++){
	            	volTbl = sc.createWebElement("volDashboard_volgrid_tbl");
	            	String orderid=	volTbl.findElement(By.xpath("./tbody/tr["+i+"]/td[contains(@data-bind,'NpnOrderId')]")).getText();
	            	cust1=volTbl.findElement(By.xpath("./tbody/tr["+i+"]/td[contains(@data-bind,'Extra1')]")).getText();
	            	cust2=volTbl.findElement(By.xpath("./tbody/tr["+i+"]/td[contains(@data-bind,'Extra2')]")).getText();
	            	cust3=volTbl.findElement(By.xpath("./tbody/tr["+i+"]/td[contains(@data-bind,'Extra3')]")).getText();
	            	
	            	if(!cust1.equalsIgnoreCase(enterData.substring(0, 50)) || !cust2.equalsIgnoreCase(enterData.substring(0, 50)) || !cust3.equalsIgnoreCase(enterData.substring(0, 50)) ){
	            		flg=true;
	            	}else{
	            		flg=false;
	            	}
	            	}
	            	
	            }else{
	            	WebElement ddMenu = sc.createWebElement("volDashboard_searchBy_dd");
	           	
	            	for(int i=0;i<=1;i++){
	            		sc.selectValue_byVisibleText(ddMenu, "VV Order");
	            		sc.setValue("volDashboard_searhValue_txt", npnOrderIds[i]);
	            		sc.clickWhenElementIsClickable("volDashboard_search_btn", 10);
	            		searchWaitPopup();
	            		volTbl = sc.createWebElement("volDashboard_volgrid_tbl");
	            		String orderid=	volTbl.findElement(By.xpath("./tbody/tr[1]/td[contains(@data-bind,'NpnOrderId')]")).getText();
	            		cust1=volTbl.findElement(By.xpath("./tbody/tr[1]/td[contains(@data-bind,'Extra1')]")).getText();
	            		cust2=volTbl.findElement(By.xpath("./tbody/tr[1]/td[contains(@data-bind,'Extra2')]")).getText();
	            		cust3=volTbl.findElement(By.xpath("./tbody/tr[1]/td[contains(@data-bind,'Extra3')]")).getText();
	            		
	            		if(!cust1.equalsIgnoreCase(enterData.substring(0, 50)) || !cust2.equalsIgnoreCase(enterData.substring(0, 50)) || !cust3.equalsIgnoreCase(enterData.substring(0, 50))){
	            			flg=true;
						
	            		}else{
	            			flg=false;
	            		}
	            	}
	            }  
	            
	            if(flg==true){
            		sc.STAF_ReportEvent("Pass", "Verify Mass update Window-Cancel Functionality-"+input[2], "After Click on Cancel button Records are not getting saved", 1);
            	}else{
            		sc.STAF_ReportEvent("Fail", "Verify Mass update Window-Cancel Functionality-"+input[2], "After Click on Cancel button Records are getting saved. CustomFields value updated.", 1);
            	}
				retval=Globals.KEYWORD_PASS;
	        }catch(Exception e){
	            sc.STAF_ReportEvent("Fail", "MassUpdate Volunteers", "Exception occurred during Mass Update volunteers-Exception - "+ e.toString(),0);
	            log.error("xception occurred during Mass Update volunteers .  | "+e.toString());
	            throw e;
	          }
	            return retval;
	        	
	    }
	 /**************************************************************************************************
		 * Method to edit volunteers in volunteer dashboard page using mass update
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author Psave
		 * @throws Exception
		 ***************************************************************************************************/
		 public static String changeStatusVerify(String statusLink,String orgStatus,String changeStatus,String changeStatusLinkPath) throws Exception {
			Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
			APP_LOGGER.startFunction(Globals.Component);
			String retval=Globals.KEYWORD_FAIL;
			String tempretval=Globals.KEYWORD_FAIL;
			String tempretval1=Globals.KEYWORD_FAIL;
			int timeOutinSeconds = 10;
			String npnOrderIds[]=new String[2];
			String dynamicData="";
			int counter=0;
			boolean flg=false;
			WebElement changeStatusLink=null;
			int chngStsCnt=0;

	        try{
	        	
	        	WebElement orgStatusLink=sc.createWebElement(statusLink);
	        	
	        	int stsCnt=Integer.parseInt(orgStatusLink.findElement(By.xpath("./span[1]")).getText());
	        	if(!changeStatus.equalsIgnoreCase("None")){
	        		changeStatusLink=sc.createWebElement(changeStatusLinkPath);
	        		chngStsCnt=Integer.parseInt(changeStatusLink.findElement(By.xpath("./span[1]")).getText());
	        	}
	        	sc.clickWhenElementIsClickable(statusLink, timeOutinSeconds);
	        	searchWaitPopup();
				
	        	//sc.clickWhenElementIsClickable("volDashboard_minimumRowSet_link", timeOutinSeconds);
	        	WebElement volTbl = sc.createWebElement("volDashboard_volgrid_tbl");
	        	showColumn();
	        	//click on vvorder for sorting in descending 
        		sc.clickElementUsingJavaScript(volTbl.findElement(By.xpath("./thead/tr[1]/th[10]/a")));
        		searchWaitPopup();
        		sc.clickElementUsingJavaScript(volTbl.findElement(By.xpath("./thead/tr[1]/th[10]/a")));
        		searchWaitPopup();
	        	while (counter<=60) {
	        		        		
					if(sc.isSelected(volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input"))).equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.isSelected(volTbl.findElement(By.xpath("./tbody/tr[2]/td[1]/input"))).equalsIgnoreCase(Globals.KEYWORD_PASS)){
						flg=true;
						break;
					}else{
						
						sc.scrollIntoViewUsingJavaScript(volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input")));
						searchWaitPopup();
						sc.clickElementUsingJavaScript(volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input")));
						Thread.sleep(2000);
						sc.clickElementUsingJavaScript(volTbl.findElement(By.xpath("./tbody/tr[2]/td[1]/input")));
						log.info("Element -"+volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]/input")).toString() + "| Checkbox Javascript click iteration "+counter);
						Thread.sleep(1000);
						counter++;

					}
				 }
	        	if(flg==true){
	        		npnOrderIds[0]=sc.getWebElementText(volTbl.findElement(By.xpath("./tbody/tr[1]/td[contains(@data-bind,'NpnOrderId')]"))).trim();
		        	npnOrderIds[1]=sc.getWebElementText(volTbl.findElement(By.xpath("./tbody/tr[2]/td[contains(@data-bind,'NpnOrderId')]"))).trim();
	        		sc.STAF_ReportEvent("Pass", "Mass update- Status Change from "+orgStatus+" to "+changeStatus, "2 volunteers selected successfully VVOrder : "+npnOrderIds[0]+", "+npnOrderIds[1], 1);	
	        	}else{
	        		sc.STAF_ReportEvent("Fail", "Mass update- Status Change from "+orgStatus+" to "+changeStatus, "Unable to select 2 volunteers for mass update" , 1);
	        	}
	        	
	        	sc.clickWhenElementIsClickable("volDashboard_editVolunteer_btn", 10); 
	        	AccountSetting.clickButtonUntilValidation("volDashboard_MassUpdates_btn", "massUpdates_Save_btn");
	        	if(!changeStatus.equalsIgnoreCase("None")||!changeStatus.equalsIgnoreCase("")){
	        		sc.selectValue_byVisibleText("massUpdates_Status_dd", changeStatus);
	        	}
	        	driver.findElement(By.xpath("//input[@id='candidateSinceDate']")).sendKeys(Keys.ENTER);
	            driver.findElement(By.xpath("//input[@id='candidateSinceDate']")).sendKeys(Keys.TAB);
	            dynamicData=RandomStringUtils.randomAlphabetic(5);
	            sc.setValue("massUpdates_Custom1_txt", dynamicData);
	            sc.setValue("massUpdates_Custom2_txt", dynamicData);
	            sc.setValue("massUpdates_Custom3_txt", dynamicData);
	            sc.STAF_ReportEvent("Pass", "Mass update- Status Change from "+orgStatus+" to "+changeStatus, "Changes the fields successfully for VVOrder : "+npnOrderIds[0]+", "+npnOrderIds[1], 1);
	            AccountSetting.clickButtonUntilValidation("massUpdates_Save_btn", "volDashboard_editVolunteer_btn"); 
	           
	            WebElement ddMenu = sc.createWebElement("volDashboard_searchBy_dd");
	            if(!changeStatus.equalsIgnoreCase("None")){
	            
	            	for(int i=0;i<=1;i++){
	            		sc.selectValue_byVisibleText(ddMenu, "VV Order");
	            		sc.setValue("volDashboard_searhValue_txt", npnOrderIds[i]);
	            		sc.clickWhenElementIsClickable("volDashboard_search_btn", 10);
	            		searchWaitPopup();
	            		volTbl = sc.createWebElement("volDashboard_volgrid_tbl");
	            		String noMatchRecords=	volTbl.findElement(By.xpath("./tbody/tr[1]/td[1]")).getText();
	            		String errorText="No matching records.";
	            		if(!errorText.equalsIgnoreCase(noMatchRecords)){
	            			sc.STAF_ReportEvent("Fail", "Mass update- Status Change from "+orgStatus+" to "+changeStatus, "Unable to change the status for VVOrder : "+npnOrderIds[i] , 1);	
						
	            		}else{
	            			sc.STAF_ReportEvent("Pass", "Mass update- Status Change from "+orgStatus+" to "+changeStatus, "VVOrder : "+npnOrderIds[i]+" is not displayed on "+orgStatus+" status search orders" , 1);	
	            		}
	            	}
	            
	            	orgStatusLink=sc.createWebElement(statusLink);
	            	changeStatusLink=sc.createWebElement(changeStatusLinkPath);
	            	if((Integer.parseInt(orgStatusLink.findElement(By.xpath("./span[1]")).getText().trim())==stsCnt-2) && (Integer.parseInt(changeStatusLink.findElement(By.xpath("./span[1]")).getText().trim())==chngStsCnt+2)){
	            		sc.STAF_ReportEvent("Pass", "Mass update- Status Change from "+orgStatus+" to "+changeStatus, "Status Count is updated successfully, "+orgStatus+": from "+stsCnt+" to "+(stsCnt-2)+" and "+changeStatus+": from "+chngStsCnt+" to "+(chngStsCnt+2) , 1);
	            	}else{
	            		sc.STAF_ReportEvent("Fail", "Mass update- Status Change from "+orgStatus+" to "+changeStatus, "Status Count is not updated successfully, expected "+orgStatus+": "+(stsCnt-2)+" and Expected "+changeStatus+": "+(chngStsCnt+2) , 1);
	            	}
	                       
	            	sc.clickWhenElementIsClickable(changeStatusLink, timeOutinSeconds);
	            }
	       
	            for(int i=0;i<=1;i++){
	            	sc.selectValue_byVisibleText(ddMenu, "VV Order");
	            	sc.setValue("volDashboard_searhValue_txt", npnOrderIds[i]);
	            	sc.clickWhenElementIsClickable("volDashboard_search_btn", 10);
	            	searchWaitPopup();
	            	volTbl = sc.createWebElement("volDashboard_volgrid_tbl");
	            	String orderid=	volTbl.findElement(By.xpath("./tbody/tr[1]/td[contains(@data-bind,'NpnOrderId')]")).getText();
					String cust1=volTbl.findElement(By.xpath("./tbody/tr[1]/td[contains(@data-bind,'Extra1')]")).getText();
					String cust2=volTbl.findElement(By.xpath("./tbody/tr[1]/td[contains(@data-bind,'Extra2')]")).getText();
					String cust3=volTbl.findElement(By.xpath("./tbody/tr[1]/td[contains(@data-bind,'Extra3')]")).getText();
					String st=volTbl.findElement(By.xpath("./tbody/tr[1]/td[14]/span")).getText();
					String candSincDt=volTbl.findElement(By.xpath("./tbody/tr[1]/td[contains(@data-bind,'volunteerSince')]")).getText();
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
					String expCandSinceDt=sdf.toString();
					 if(changeStatus.equalsIgnoreCase("None")){
						 if(!npnOrderIds[i].equalsIgnoreCase(orderid) && !cust1.equalsIgnoreCase(dynamicData) && !cust2.equalsIgnoreCase(dynamicData) && !cust3.equalsIgnoreCase(dynamicData) && !expCandSinceDt.equalsIgnoreCase(candSincDt) && !st.equalsIgnoreCase(orgStatus)){
								sc.STAF_ReportEvent("Fail", "Mass update- Status Change from "+orgStatus+" to "+changeStatus, "Order status Changed from "+orgStatus+" to "+changeStatus, 1);	
						 }else{
								sc.STAF_ReportEvent("Pass", "Mass update- Status Change from "+orgStatus+" to "+changeStatus, "Order status from "+orgStatus+" to "+changeStatus+" and all fileds for mass update are updated Successfully for VVOrder : "+npnOrderIds[i], 1);
						 }
					 }else{
					
					
						 if(!npnOrderIds[i].equalsIgnoreCase(orderid) && !cust1.equalsIgnoreCase(dynamicData) && !cust2.equalsIgnoreCase(dynamicData) && !cust3.equalsIgnoreCase(dynamicData) && !expCandSinceDt.equalsIgnoreCase(candSincDt) && !st.equalsIgnoreCase(changeStatus)){
							 sc.STAF_ReportEvent("Fail", "Mass update- Status Change from "+orgStatus+" to "+changeStatus, "Order status Changed from "+orgStatus+" to "+changeStatus, 1);	
						 }else{
							 sc.STAF_ReportEvent("Pass", "Mass update- Status Change from "+orgStatus+" to "+changeStatus, "Order status from "+orgStatus+" to "+changeStatus+" and all fileds for mass update are updated Successfully for VVOrder : "+npnOrderIds[i], 1);
						 }
					 }
	            }
	            
	           
	            retval=Globals.KEYWORD_PASS;
	        	
	        	
	        }catch(Exception e){
	            sc.STAF_ReportEvent("Fail", "MassUpdate Volunteers", "Exception occurred during Mass Update volunteers-Exception - "+ e.toString(),0);
	            log.error("xception occurred during Mass Update volunteers .  | "+e.toString());
	            throw e;
	          }
	            return retval;
	        	
	 }
	/**************************************************************************************************
	 * Method to poll a single fufilled order
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String PollSingleOrder() throws Exception{
		String retval = Globals.KEYWORD_FAIL;
		String npnOrderID = "";
		String queryString ="";

		try{
			/* Anand
		     *  fix for
		     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
		     *       sun.security.validator.ValidatorException:
		     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
		     *               unable to find valid certification path to requested target
		     */
		    TrustManager[] trustAllCerts = new TrustManager[] {
		       new X509TrustManager() {
		          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		            return null;
		          }

		          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

		          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

		       }
		    };

		    SSLContext ssc = SSLContext.getInstance("SSL");
		    ssc.init(null, trustAllCerts, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(ssc.getSocketFactory());

		    // Create all-trusting host name verifier
		    HostnameVerifier allHostsValid = new HostnameVerifier() {
		        public boolean verify(String hostname, SSLSession session) {
		          return true;
		        }
		    };
		    // Install the all-trusting host verifier
		    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		    /*
		     * end of the fix
		     */
		

		    String expectedMessage = "Status returned:Complete";
			npnOrderID = Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
			//queryString = "npnOrderId=" +  	URLEncoder.encode(npnOrderID, "UTF-8");
            
			String urlConn=Globals.getEnvPropertyValue("PollURL")+npnOrderID;
			
			//URL url = new URL(Globals.getEnvPropertyValue("PollURL"));
			URL url = new URL(urlConn);
			URLConnection urlConnection = url.openConnection();
			
			urlConnection.setDoOutput(true);
			
			//DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
			// Write query string to request body

			//out.write(queryString);
			//out.flush();
			// Read the response .Success message example 'Order 60654 results updated.'
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String line = null;
			String completeResponse = "";
			while ((line = in.readLine()) != null)
			{
				completeResponse = completeResponse + line;

			}
			
			if (!completeResponse.contains(expectedMessage)){
				URL url1 = new URL(urlConn);
				URLConnection urlConnection1 = url.openConnection();
				
				urlConnection1.setDoOutput(true);
				//DataOutputStream out1 = new DataOutputStream(urlConnection1.getOutputStream());
				//out1.flush();
				// Read the response .Success message example 'Order 60654 results updated.'
				BufferedReader in1 = new BufferedReader(new InputStreamReader(urlConnection1.getInputStream()));
				line = null;
			    completeResponse = "";
				while ((line = in1.readLine()) != null)
				{
					completeResponse = completeResponse + line;

				}
				//out1.close();
				in1.close();
			}
			//out.close();
			in.close();

			
		
			if(completeResponse.contains(expectedMessage)){
				sc.STAF_ReportEvent("Pass", "Order Polling-"+npnOrderID, "Order Polling successfully completed" , 0);
				retval= Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "Order Polling-"+npnOrderID, "Order Polling Not successful.Response-"+completeResponse , 0);
				String abc="";
			}
			if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
				fetchOrderSharingTable();
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
			
		}
		
		return retval;
	}

	/**************************************************************************************************
	 * Method to verfiy ABUSE-PA consent page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void invitationStep3Abuse() throws Exception{

		//if ABUSE is in package, then ABUSE will only get added if PA is select as the address state
		// if ABUSE-PA is configured in package then it will get added no matter what the address state is
		String tempRetval =  Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 30;
		boolean isAbuseOrdered = false;
        Thread.sleep(5000);
		tempRetval = isProductPartOfOrdering("ABUSE(PA)");
		if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			isAbuseOrdered = true;
		}else {
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = isProductPartOfOrdering("ABUSE");
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				String applicantState = Globals.testSuiteXLS.getCellData_fromTestData("AddressState");
				if(applicantState.equalsIgnoreCase("Pennsylvania")){
					isAbuseOrdered = true;
				}
			}
		}

		if(isAbuseOrdered){
			sc.waitForPageLoad();
			invitationStep3Abusepa(); //new page will always be displayed when ABUSE PA is present in the order
			
			String response =   Globals.testSuiteXLS.getCellData_fromTestData("AbusePAResponse");
			String AbusePACheckbox = Globals.testSuiteXLS.getCellData_fromTestData("AbusePensylvaniacheckbox");
			
//			popup-ABUSE PA - New page navigation logic
//			
//			response = Yes
//				Setting = Yes
//					Step 4/Questtion
//				Setting  = No
//			 		Abuse consent
//			
//			response =no
//				Abuse Consent
			
			if (response.equalsIgnoreCase("Yes" )  &&( AbusePACheckbox.equalsIgnoreCase("Yes"))){
				
				sc.waitForPageLoad();
				tempRetval = Globals.KEYWORD_FAIL;		
				tempRetval = sc.waitforElementToDisplay("invitationStep4_infoCorrect_chk", timeOutInSeconds);	// when Questionairre is not configured for the client
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "ABUSE-Pennsylvania", "Step 4 page has not loaded", 1);
					throw new Exception("ABUSE-Pennsylvania" +"Step 4 page has not loaded");
				}
			}else{
				tempRetval = Globals.KEYWORD_FAIL;		
				tempRetval = sc.waitforElementToDisplay("invitationStep3abuse_consentSignature_canvas", timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "ABUSE-Pennsylvania", "Consent Page has not loaded", 1);

				}else{
					String actualAbuseConsentText= driver.findElement(By.xpath("//div[@id='consentBindingAnchor']/div[1]/div[@class='content']")).getText().trim();
					String fname=Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");;
					String lname=Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
					String clientName=Globals.testSuiteXLS.getCellData_fromTestData("AccountName");;
					Date date = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a"); // 06/30/2016 5:18 AM
					String currentTimestamp = sdf.format(date);
					
					String strDateFormat = "";
					//Thread.sleep(5000);
					if(Globals.BrowserType.toLowerCase().contains("ie")){
						strDateFormat = "DATE: ___"+currentTimestamp+"___ ";
					}else{
						strDateFormat = "DATE: ___"+currentTimestamp+"___\n";
					}
					String expAbuseConsentText = "I, ___"+fname+" "+lname+"___, hereby authorize the PA Department of Human Services, ChildLine to release my Pennsylvania Child Abuse History Clearance information directly to ___Verified Volunteers___. I understand that this information is confidential in nature pursuant to §6339 (relating to information in confidential reports) of the Child Protective Services Law (CPSL) (23 Pa.C.S Chapter 63) and will not otherwise be released by ___Verified Volunteers___ without my expressed authorization or pursuant to authorization by Title 55 of the Pennsylvania Code. I also understand that the aforementioned information will not be released directly to me ___"+fname+" "+lname+"___ as stated on the Pennsylvania Child Abuse History Clearance application. I understand that I will not receive a copy of my Pennsylvania Child Abuse History Clearance directly from ChildLine ; however, I may request a copy of my Pennsylvania Child Abuse History Clearance from ___Verified Volunteers___ upon written request. I have read this Consent/Release of Information Authorization form and fully understand and agree to its content. I further understand and agree to all information and ramifications of the Pennsylvania Child Abuse History Clearance application as it otherwise relates to this consent. Further I understand that if I am listed in the statewide central registry for child abuse that my consent allows the result stating such information to be shared with the agency/organization noted on next page.\n"+
							"Please send my clearance result(s) to:\n"+
							"Agency Name: Verified Volunteers\n"+
							"Agency Street Address: 6111 Oak Tree Blvd. Ste. 400\n"+
							"Agency City, State, Zip Code: Independence, OH 44131\n"+
							"I, ___"+fname+" "+lname+"___ hereby authorize Verified Volunteers, A Sterling Backcheck Company, to release my Pennsylvania Child Abuse History Clearance to ___"+clientName+"___.\n"+
							strDateFormat+
							"Applicant's Signature:\n"+
							"As the agency/organization representative, I understand that, except for the subject of a report, persons who receive this information are subject to the confidentiality provisions of the CPSL and 55 Pa. Code, Chapter 3490 and are required to ensure the confidentiality and security of the information and are liable for civil and criminal penalties for releasing information to persons who are not permitted access to this information. I agree to receive and maintain this information in accordance with these requirements.\n"+
							strDateFormat+
							"Agency Representative's Signature:";

					if(expAbuseConsentText.equals(actualAbuseConsentText)){
						sc.STAF_ReportEvent("Pass", "Abuse-PA-Consent", "Consent text matched as expected.Verified volunteer name,date and client name", 1);
					}else{
						sc.STAF_ReportEvent("Fail", "Abuse-PA-Consent", "Consent text Mismatch as Verification of entire text content failed.", 1);
					}


					//Need to verify other consent page validations
					WebElement element = driver.findElement(LocatorAccess.getLocator("invitationStep3abuse_consentSignature_canvas"));

					Actions builder = new Actions(driver);

					for(int i=150;i<400;i++){
						builder.moveToElement(element,i,40).click().build().perform();

					}
					sc.STAF_ReportEvent("Done", "ABUSE-Pennsylvania", "E-Signature provided", 1);
					sc.clickWhenElementIsClickable("invitationStep3abuse_continue_btn", (int) timeOutInSeconds);

				}

			}
			

			
			
		}    
	}

	/**************************************************************************************************
	 * Method to verfiy Questionnaire page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String invitationQuestionnaire() throws Exception{

		//check Questionnaire flag using test data if flag yes then validate Questionnaire page
		String tempRetval =  Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 30;
		
        Thread.sleep(5000);
		String questionnaireFlag = Globals.testSuiteXLS.getCellData_fromTestData("Questionnaire");
		try{
			if (questionnaireFlag.equalsIgnoreCase("Yes")){
					tempRetval=sc.waitforElementToDisplay("questionnaire_heading_lbl", timeOutInSeconds);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail", "Questionnaire Page", "Questionnaire Page has not loaded", 1);
						throw new Exception("Questionnaire Page" +"Questionnaire Page has not loaded");
					}
					String lblTxt=sc.getWebText("questionnaire_heading_lbl").trim();
					String lblTxt1=sc.getWebText("questionnaire_paragraph_lbl").trim();
					if(!((lblTxt.equalsIgnoreCase("NCAA Questions"))&&(lblTxt1.contains("Please respond to the following questionnaire.")))){
						sc.STAF_ReportEvent("Fail", "Questionnaire Page", "Questionnaire Page label text and paragraph text has not matched as per expected", 1);
				
					}
					
					List<WebElement> clientList = driver.findElements(By.xpath("//div[@id='questionnaire']/div[1]/ol/li"));
					int Qsize=clientList.size();
					if(Qsize == 6){
						sc.STAF_ReportEvent("Pass", "Questionnaire Page", "Questions count getting as expected : "+Qsize, 1);
					
					}else{
						sc.STAF_ReportEvent("Fail", "Questionnaire Page", "Not getting displayed 6 questions. Actual qustions count : "+Qsize , 1);
					}
					
					sc.clickWhenElementIsClickable("questionnaire_page_continue_button", 60);
					String warnText1=sc.getWebText("questionnaire_page_warning_message").trim();
					if(!warnText1.contains("Please answer all required questions.")){
						sc.STAF_ReportEvent("Fail", "Questionnaire Page", "Questionnaire Page warning alert text has not matched as per expected", 1);
					}
					
					String warnMess="This is a required question. Please answer it to continue.";
					for(int i=2;i<=12;)
					{
						String WarnText=driver.findElement(By.xpath(".//*[@id='questionnaire']/div[1]/ol/span["+i+"]")).getText();
					    
					    if(!warnMess.equals(WarnText)){
							sc.STAF_ReportEvent("Fail", "Questionnaire Page", "Questionnaire Page warning message text not getting as expected. Expected msg :"+warnMess, 1);
						}
					    i=i+2;
					}
					
					
				String arr[]={"Have you ever been previously placed on probation, dismissed, expelled, suspended or refused participation in a youth program?","Are you a sports agent?","All deadlines associated with NCAA certification and/or approval are STRICTLY ENFORCED. Each applicant is required to complete the \"NCAA Eligibility\" course on NFHSlearn.com. If you have already completed the course, you DO NOT have to take it again. If you have not completed the course, you are required to do so within 10 days from the date you submit your background check application. Failure to comply will result in the WITHDRAWAL of your background check approval and will impact your eligibility to participate in NCAA-certified events. Reinstatement of your background check approval would then require you to re-apply with Verified Volunteers (with additional fees). Please acknowledge that you have or you will comply with this requirement.","Although you are not part of the NCAA membership, by participating in the NCAA certification and approval process and benefiting from those opportunities, you are voluntarily submitting yourself and your team/organizations to NCAA legislation, guidelines and requirements. Please acknowledge that you understand that you are required to comply with all NCAA-requests and requirements.","Individuals who seek and obtain NCAA certification or approval are required to cooperate with the NCAA related to possible NCAA rules and violations, even if the violations are unrelated to basketball certification or scouting service rules and guidelines. Further, these individuals are required to provide complete and accurate information to the NCAA. The provision of false and/or misleading information in obtaining certification or approval may result in the inability to obtain an NCAA Participant Approval required to operate or coach in an NCAA-certified event for a period of UP TO FIVE YEARS. Please acknowledge your understanding.","I acknowledge the information I am submitting is correct to my knowledge."};
				Boolean qaFlag=false;
				int ansRadioButtonidYes=0;
				int ansRadioButtonidNo=0;
				if(Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA2")){
					ansRadioButtonidYes=351;
					ansRadioButtonidNo=352;
				}else if(Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA3")){
					ansRadioButtonidYes=105;
					ansRadioButtonidNo=106;
				}
		
				for(int i=0;i<Qsize;i++)
				{
					int sz=i+1;

					String Qnnaire=driver.findElement(By.xpath(".//*[@id='questionnaire']/div[1]/ol/li["+sz+"]/span[1]")).getText();
					String warngMsgClassnm=driver.findElement(By.xpath(".//*[@id='questionnaire']/div[1]/ol/li["+sz+"]/span[2]")).getAttribute("class");
				    String Ytext=driver.findElement(By.xpath("//*[@id='questionnaire']//label[@for='"+ansRadioButtonidYes+"']")).getText();
				    
				    String Ntext=driver.findElement(By.xpath("//*[@id='questionnaire']//label[@for='"+ansRadioButtonidNo+"']")).getText();
				   
			
				    
				    if(!(arr[i].equals(Qnnaire) && Ytext.equalsIgnoreCase("yes") && Ntext.equalsIgnoreCase("no"))){
						sc.STAF_ReportEvent("Fail", "Questionnaire Page", sz+" Question text not getting expected. Actual Qust Txt+ "+Qnnaire+ " Yes & No text not getting as expected :", 1);
						sc.STAF_ReportEvent("Fail", "Questionnaire Page", sz+" Question optins text as Yes & No not getting as per expected. Actual options ghetting as : "+Ytext+" & "+Ntext, 1);
						qaFlag=true;
						break;
				    }
				    if(!(warngMsgClassnm.equalsIgnoreCase("image-block warn"))){
				    	sc.STAF_ReportEvent("Fail", "Questionnaire Page", " For Question - "+sz+" , Warning Symbol image not getting displayed.", 1);
				    	qaFlag=true;
						break;
				    }
				    
				    String questionnaireFlag1 = Globals.testSuiteXLS.getCellData_fromTestData("QuestionnaireQA");
				   
				    String arrSplt[]=questionnaireFlag1.split(";");
				    
					
					if(!(arrSplt.length==6)){
						sc.STAF_ReportEvent("Fail", "Questionnaire Page", "Questions ans counts from test data is getting as expected : "+arrSplt.length, 0);
						qaFlag=true;
						break;
					}
					
					if(arrSplt[i].equalsIgnoreCase("yes")){
						WebElement rdby=driver.findElement(By.xpath(".//input[@id='"+ansRadioButtonidYes+"']"));
						sc.clickWhenElementIsClickable(rdby, 60);	
						
					}else if(arrSplt[i].equalsIgnoreCase("no")){
						WebElement rdbn=driver.findElement(By.xpath(".//input[@id='"+ansRadioButtonidNo+"']"));
						sc.clickWhenElementIsClickable(rdbn, 60);
						
					}
					//verify after clicking answer correct symbol image should be displayed 
					warngMsgClassnm=driver.findElement(By.xpath(".//*[@id='questionnaire']/div[1]/ol/li["+sz+"]/span[2]")).getAttribute("class");
					if(!(warngMsgClassnm.equalsIgnoreCase("image-block submit"))){
						sc.STAF_ReportEvent("Fail", "Questionnaire Page", "After Clicking-"+sz+"question answer as "+arrSplt[i]+" -Correct icon is not getting displayed after question", 1);
						qaFlag=true;
						break;
					}
					
					ansRadioButtonidYes=ansRadioButtonidYes+2;
					
					ansRadioButtonidNo=ansRadioButtonidNo+2;
				}
			
				if(qaFlag==true){
					sc.STAF_ReportEvent("Fail", "Questionnaire Page", "All questions are not answerd successfully", 1);
					throw new Exception("Questionnaire Page" +"All questions are not answerd successfully");
				}else{
					sc.STAF_ReportEvent("Pass", "Questionnaire Page", "All questions are answered successfully", 1);
				}
				sc.clickWhenElementIsClickable("questionnaire_page_continue_button", 60);
				sc.waitForPageLoad();
				tempRetval=sc.waitforElementToDisplay("invitationStep4_infoCorrect_chk", 60);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Questionnaire Page", "All questions are not answerd successfully", 1);
					throw new Exception("Questionnaire Page" +"All questions are not answerd successfully");
				}
		       
		    	}
			}catch (Exception e){
				e.printStackTrace();
			}
		return tempRetval;
		}
	/**************************************************************************************************
	 * Method to check for ID confirm page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void  checkForIDAndProceed() throws Exception{
		// in case ID confirm product is part of the order, we have to click on btn 'I dont have a mobile device'
		String tempRetval =  Globals.KEYWORD_FAIL;
		tempRetval = isProductPartOfOrdering(VV_Products.ID.toString());
		long timeOutinSeconds = 30;

		if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval=sc.waitforElementToDisplay("goodDeedPage_noMobileDevice_btn",timeOutinSeconds);
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.clickWhenElementIsClickable("goodDeedPage_noMobileDevice_btn",(int) timeOutinSeconds );
				//Changed by Lakshmi for user story QAA-847,848,849
				tempRetval=sc.waitforElementToDisplay("idSurvay_noMobileDevice_ansonequestion_page",timeOutinSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("PASS", "Process on your desktop computer", "ID confirm process on your desktop computer page loaded",1);
					String response =   Globals.testSuiteXLS.getCellData_fromTestData("IDOneSurvayResponse");
					if(response.equalsIgnoreCase("I do not have a mobile device that can access the internet")){
						driver.findElement(LocatorAccess.getLocator("idSurvay_noMobileDevice_ansonequestion_firstradiobtn")).click();
					}else if(response.equalsIgnoreCase("I do not have my State Issued ID available right now")){
						driver.findElement(LocatorAccess.getLocator("idSurvay_noMobileDevice_ansonequestion_secondradiobtn")).click();
					}else if(response.equalsIgnoreCase("I do not have time to switch to my mobile device right now")){
						driver.findElement(LocatorAccess.getLocator("idSurvay_noMobileDevice_ansonequestion_thirdradiobtn")).click();
					}else if(response.equalsIgnoreCase("I do not want to provide information from my State Issued ID")){
						driver.findElement(LocatorAccess.getLocator("idSurvay_noMobileDevice_ansonequestion_fourthradiobtn")).click();
					}else{
						sc.STAF_ReportEvent("Fail", "Process on your desktop computer", "ID confirm process one Survay Response is not selected",1);
					}
					
					sc.clickWhenElementIsClickable("idSurvay_noMobileDevice_ansonequestion_submitbtn",(int) timeOutinSeconds );
					tempRetval=sc.waitforElementToDisplay("invitationStep1_fName_txt", timeOutinSeconds);

					if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)) {
						sc.STAF_ReportEvent("Fail", "Process on your desktop computer", "ID confirm process not completed. Unable to get order step 1",1);			

					}
					
				}else{
					sc.STAF_ReportEvent("Fail", "Process on your desktop computer", "ID confirm process on your desktop computer page not loaded",1);
				}
			}else{
				sc.STAF_ReportEvent("Fail", "Before you proceed", "ID confirm is part of the order but continue button doesnt exists",1);
			}
		}
	}

	/**************************************************************************************************
	 * Method to enter date in VV applications
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void enterDate(int yeardFromCurrentYear, String elementName) {
		String tempRetval=Globals.KEYWORD_FAIL;
		String month = sc.getPriorDate("MMM-yyyy", yeardFromCurrentYear);

		String sFromMonth = month.split("-")[0];
		sFromMonth = sc.convertToFullMonthName(sFromMonth);
		sFromMonth = sc.convertFullMonthNameToNumber(sFromMonth);

		String sFromYear = month.split("-")[1];;
		String sFromDate = null;

		sFromDate = sFromMonth + "/"+ sFromYear;
		tempRetval = sc.setValueJsChange(elementName, sFromDate);
		tempRetval = sc.waitforElementToDisplay("invitationStep1_datePickerDone_btn", 3);// this is done to remove the calendar from view else add alias button wont get clicked
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			try{
				sc.clickWhenElementIsClickable("invitationStep1_datePickerDone_btn", 5);
			}catch(Exception e){
				log.error("Method - enterDate | Exception thrown while clicking on Done Datepicker btn."+e.toString());
			}
		}
	}

	/**************************************************************************************************
	 * Method to verify alias section data for API ordeing workflow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyAliasSectionData_API(XMLUtils inputXML) throws Exception{

		int iNoOfAlias 		= 0;
		iNoOfAlias 			= 	inputXML.getNumberOfNodesPresentByXPath("//ScreeningSubjectName/ScreeningPersonName/PersonName[@nameTypeCode='Birth Name']/GivenName");
		String tempRetval 	=  Globals.KEYWORD_FAIL;
		int timeOutinSeconds= 20;

		tempRetval = isProductPartOfOrdering("L3");
		if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS) && iNoOfAlias > 0){


			String aliasFNameLocator;
			String aliasMidNameLocator;
			String aliasLNameLocator;
			//			String aliasFromDateLocator;
			//			String aliasToDateLocator;
			//			String fromDate;
			//			String toDate;
			String expectedValue="";
			WebElement fname=null;
			WebElement lname = null;
			WebElement midName = null;

			tempRetval = sc.waitforElementToDisplay("invitationStep1_addAlias_btn", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

				int i=1;

				for(;i<=iNoOfAlias;i=i+1){

					aliasFNameLocator = "//div[@data-bind='foreach: AliasList']/ul[" + i + "]/li[1]/input[@type='text' and @name='FirstNameAlias']";
					fname = driver.findElement(By.xpath(aliasFNameLocator));

					aliasMidNameLocator ="//div[@data-bind='foreach: AliasList']/ul[" + i + "]/li[2]/input[@type='text' and @name='MiddleNameAlias']";
					midName = driver.findElement(By.xpath(aliasMidNameLocator));

					aliasLNameLocator ="//div[@data-bind='foreach: AliasList']/ul[" + i + "]/li[3]/input[@type='text' and @name='LastNameAlias']";
					lname = driver.findElement(By.xpath(aliasLNameLocator));

					//						aliasFromDateLocator = "//div[@data-bind='foreach: AliasList']/ul[" + i + "]/li[4]/input[@type='text' and @name='FromAlias']"; 
					//
					//						aliasToDateLocator =  "//div[@data-bind='foreach: AliasList']/ul[" + i + "]/li[5]/input[@type='text' and @name='ToAlias']";

					expectedValue="";
					expectedValue = inputXML.getXMLNodeValByXPath("//ScreeningSubjectName/ScreeningPersonName/PersonName[@nameTypeCode='Birth Name']/GivenName");
					tempRetval = verifyXMLValWithUIFieldVal(expectedValue,fname,"AliasFirstName");

					expectedValue="";
					expectedValue = inputXML.getXMLNodeValByXPath("//ScreeningSubjectName/ScreeningPersonName/PersonName[@nameTypeCode='Birth Name']/FamilyName");
					tempRetval = verifyXMLValWithUIFieldVal(expectedValue,lname,"AliasLastName");

					expectedValue="";
					expectedValue = inputXML.getXMLNodeValByXPath("//ScreeningSubjectName/ScreeningPersonName/PersonName[@nameTypeCode='Birth Name']/MiddleName");
					tempRetval = verifyXMLValWithUIFieldVal(expectedValue,midName,"AliasMidName");

					//						fromDate = sc.getPriorDate("MM/yyyy", -6);
					//						toDate =  sc.getPriorDate("MM/yyyy", -1);
					//
					//						driver.findElement(By.xpath(aliasFNameLocator)).sendKeys(aliasFName);
					//						driver.findElement(By.xpath(aliasMidNameLocator)).sendKeys(aliasMidName);
					//						driver.findElement(By.xpath(aliasLNameLocator)).sendKeys(aliasLName);


				}

			}else{
				sc.STAF_ReportEvent("Fail", "Step 1", "Alias Section is NOT visible", 1);

			}

		}

	}

	/**************************************************************************************************
	 * Method to update volunteer first,last and mid name in test data
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void  updateVolunteerNameInTestData(){


		String specialChara = " -'";
		String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
		String fname=null;
		String lname=null;
		String midName = null;
		String spaces = "   ";

		fname = sc.updateAndFetchRuntimeValue("VolunteeFName_runtimeUpdateFlag","volunteerFName",15);
		lname = sc.updateAndFetchRuntimeValue("VolunteeLName_runtimeUpdateFlag","volunteerLName",15);
		midName =Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");

		if(fieldLevelValidation.equalsIgnoreCase("Yes")){

			if(!fname.contains(specialChara)){
				fname = spaces + fname + specialChara + spaces;
			}
			Globals.testSuiteXLS.setCellData_inTestData("volunteerFName", fname);
			
			if(!lname.contains(specialChara)){
				lname = spaces + lname + specialChara + spaces;
			}
			Globals.testSuiteXLS.setCellData_inTestData("volunteerLName", lname);
			
			if(!midName.contains(specialChara)){
				midName = spaces + midName + specialChara + spaces;
			}
						
			Globals.testSuiteXLS.setCellData_inTestData("volunteerMidName", midName);
		}

	}

	/**************************************************************************************************
	 * Method to navigate user to BG dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String navigateBGDashboard() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		sc.waitforElementToDisplay("clientBGDashboard_reviewBackgroundCheck_link", timeOutinSeconds);
		sc.clickWhenElementIsClickable("clientBGDashboard_reviewBackgroundCheck_link", (int) timeOutinSeconds);
		retval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

		while (retval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
			Thread.sleep(2000);
			retval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

		}

		if(sc.waitforElementToDisplay("bgDashboard_search_txt", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){
			retval=Globals.KEYWORD_PASS;
			sc.STAF_ReportEvent("Pass", "BG Dashboard", "User navigated to BG Dashboard Page", 1);
		}
		else{
			sc.STAF_ReportEvent("Fail", "BG Dashboard", "Unable to  navigated to BG Dashboard page", 1);
			log.error("Unable to  navigated to BG Dashboard page");
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to navigate user to BG dashboard Hold Tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String navigateBGDashboardHoldTab() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		sc.waitforElementToDisplay("clientBGDashboard_reviewBackgroundCheck_link", timeOutinSeconds);
		sc.clickWhenElementIsClickable("clientBGDashboard_holdtab_link", (int) timeOutinSeconds);

		if(sc.waitforElementToDisplay("bgDashboard_search_txt", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){
			retval=Globals.KEYWORD_PASS;
			sc.STAF_ReportEvent("Pass", "BG Dashboard", "User navigated to BG Dashboard Hold Tab", 1);
		}
		else{
			sc.STAF_ReportEvent("Fail", "BG Dashboard", "Unable to  navigated to BG Dashboard Hold Tab", 1);
			log.error("Unable to  navigated to BG Dashboard Hold Tab");
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to search volunteer in BG dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String searchVolInBGDashboard() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;
		
		verifyOrgUserClientHierarchyDD();
		
		tempretval = sc.waitforElementToDisplay("bgDashboard_search_txt", timeOutinSeconds);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "BG Dashboard", "Volunteer Search text field is not displayed", 1);
		}else{
								
			String volFname = "";
			String volLname ="";
			volFname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
			volLname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
			sc.setValueJsChange("bgDashboard_search_txt", volFname);
			sc.clickWhenElementIsClickable("bgDashboard_searchBtn_btn", (int) timeOutinSeconds);

			tempretval = Globals.KEYWORD_FAIL;
			tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

			while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(2000);
				tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

			}

			//get row count of  the search results table
			WebElement searchGrid = sc.createWebElement("bgDashboard_searchResults_tbl");
			int rowcount =0;
			rowcount = sc.getRowCount_tbl(searchGrid);
            // if search result is not getting at first attempt then try for 3 times 
			if(rowcount != 1){
				int flag=0;
				
				while(rowcount != 1){
					sc.setValueJsChange("bgDashboard_search_txt", volFname);
					sc.clickWhenElementIsClickable("bgDashboard_searchBtn_btn", (int) timeOutinSeconds);

					tempretval = Globals.KEYWORD_FAIL;
					tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

					while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
						Thread.sleep(2000);
						tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

					}
					searchGrid = sc.createWebElement("bgDashboard_searchResults_tbl");
					rowcount =0;
					rowcount = sc.getRowCount_tbl(searchGrid);
					if(flag>3){
						break;
					}
					flag++;
				}			
						
			}
			
			if(rowcount!=1){
				sc.STAF_ReportEvent("Fail", "BG Dashboard", "Multiple volunteers has been found during search.Unable to select the appropriate volunteer", 1);
			}else {
				String noMatchRecords = "";
				noMatchRecords=	searchGrid.findElement(By.xpath("./tbody/tr[1]/td[1]")).getText();
				String errorText="No matching records.";
				if(errorText.equalsIgnoreCase(noMatchRecords)){
					sc.STAF_ReportEvent("Fail", "BG Dashboard", "Unable to Search volunteer by FirstName.No match found.FirstName-"+volFname, 1);
				}else{
					
					//Select which report to be view summary report or detail report
					String viewReportvalue=Globals.testSuiteXLS.getCellData_fromTestData("BgDashboard_ReportView");
					WebElement viewReportdd = sc.createWebElement("bgDashboard_viewReport_dd");
					String[] arrviewReportdd={"Detail Report","Summary Report"};
					tempretval = Globals.KEYWORD_FAIL;
					tempretval = sc.waitforElementToDisplay("bgDashboard_viewReport_dd", 2);
					
					if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail", "BG Dashboard", "View report Dropdown not displayed", 1);
					}else{
							sc.verifyDropdownItems(viewReportdd, arrviewReportdd, "Verify View Report DropDown List Items");
							tempretval = Globals.KEYWORD_FAIL;
							tempretval=sc.selectValue_byVisibleText(viewReportdd, viewReportvalue);
							if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
								sc.STAF_ReportEvent("Fail", "BG Dashboard", "Unable to select option "+viewReportvalue+" From View Report Dropdown", 1);
							}else{
							//verify volunteer name and order date,position and results
							sc.STAF_ReportEvent("Pass", "BG Dashboard", "Dropdown option "+viewReportvalue+" has been selected successfully From View Report Dropdown", 1);
							String volNameUI = searchGrid.findElement(By.xpath("./tbody/tr[1]/td[2]")).getText();
							String orderDateUI ="";
							String positionUI="";
							String resultsUI="";
						    String resultType="";
							String expOrderedDate="";
							String expPosition="";
							String expResults="";
							String questionnaire="";
							String questionnaireUI="";
							String imgAttribute="";
							Boolean qFlag=false;

							String expVolName = volLname +", "+volFname;
							if(expVolName.equalsIgnoreCase(volNameUI)){
								orderDateUI = searchGrid.findElement(By.xpath("./tbody/tr[1]/td[8]")).getText();
								positionUI =  searchGrid.findElement(By.xpath("./tbody/tr[1]/td[4]")).getText();
								resultsUI =  searchGrid.findElement(By.xpath("./tbody/tr[1]/td[10]")).getText();

								questionnaire=Globals.testSuiteXLS.getCellData_fromTestData("Questionnaire");
								if(questionnaire.equalsIgnoreCase("yes")){
									String questans=Globals.testSuiteXLS.getCellData_fromTestData("QuestionnaireQA");
									if(questans.equalsIgnoreCase("no;no;yes;yes;yes;yes")){
										tempretval=sc.isDisplayed(searchGrid.findElement(By.xpath("./tbody/tr[1]/td[12]//img[1]")));
										if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
											sc.STAF_ReportEvent("Fail", "BG Dashboard", "Volunteers data mismatch.Kindly verify Questionnaire Column. Expected is Green Flag Icon.", 1);
										}else{
											imgAttribute=searchGrid.findElement(By.xpath("./tbody/tr[1]/td[12]//img[1]")).getAttribute("alt");
											if(!(imgAttribute.contains("All responses were preferred"))){
												sc.STAF_ReportEvent("Fail", "BG Dashboard", "Volunteers data mismatch.Kindly verify Questionnaire Column. Expected is Green Flag Icon.", 1);
											}else{
												sc.STAF_ReportEvent("Pass", "Volunteer Search - BG Dashboard", "Questionnaire Column is displayed as per expected - Green Flag Icon", 1);
											}
										}
									}else{
										tempretval=sc.isDisplayed(searchGrid.findElement(By.xpath("./tbody/tr[1]/td[12]//img[2]")));
										if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
											sc.STAF_ReportEvent("Fail", "BG Dashboard", "Volunteers data mismatch.Kindly verify Questionnaire Column. Expected is Yellow Flag Icon.", 1);
										}else{
											imgAttribute=searchGrid.findElement(By.xpath("./tbody/tr[1]/td[12]//img[2]")).getAttribute("alt");
											if(!(imgAttribute.contains("One or more responses were not preferred"))){
												sc.STAF_ReportEvent("Fail", "BG Dashboard", "Volunteers data mismatch.Kindly verify Questionnaire Column. Expected is Yellow Flag Icon.", 1);
											}else{
												sc.STAF_ReportEvent("Pass", "Volunteer Search - BG Dashboard", "Questionnaire Column is displayed as per expected - Yellow Flag Icon", 1);
											}
										}
									}
								}					
								//verify id confirm column text
								tempretval=sc.isDisplayed(searchGrid.findElement(By.xpath("//th[6]")));
								if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
									String idColumnTxt=searchGrid.findElement(By.xpath("./tbody/tr[1]/td[6]")).getText();
									if(!idColumnTxt.equalsIgnoreCase("N/A")){
										sc.STAF_ReportEvent("Fail", "BG Dashboard", "Volunteers data mismatch.Kindly verify ID Confirm column", 1);	
									}else{
										sc.mouseMoveToElement(driver.findElement(By.xpath("//tr[1]/td[6]")));
										WebElement idConfirmtxtWE=driver.findElement(By.xpath("//*[contains(@id,'popover')]/div[2]/ul"));
										tempretval=sc.waitTillElementDisplayed(idConfirmtxtWE, timeOutinSeconds);
										if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
											String webText=sc.getWebElementText(idConfirmtxtWE);
											String expectedTxt="ID Confirm\nA blue camera icon in this column indicates that an ID has been validated for this volunteer. The volunteer’s background check was run using the exact information contained on the ID.\n\nAn orange camera icon in this column indicates that an ID has been validated for this volunteer. The information used to run the volunteer’s complete background check was modified by the volunteer.\n\nIf this column is blank, ID Confirm was not run on the volunteer or the results were inconclusive.";
											if(webText.contentEquals(expectedTxt)){
												sc.STAF_ReportEvent("Pass", "BG Dashboard- ID Confirm Column", "ID Confirm column text displayed as per expected", 1);
											}
										}
									}
								}
								//verify Result Type column text
								tempretval=sc.isDisplayed(driver.findElement(By.xpath("//*[@id='resultTypePopover']")));
								if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
									sc.clickWhenElementIsClickable(driver.findElement(By.xpath("//*[@id='resultTypePopover']")), 10);
									tempretval=sc.waitTillElementDisplayed(driver.findElement(By.xpath("//div[contains(@id,'popover')]/div[2]")),10);
									if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
										
										String text="A green checkmark indicates that all searches on the order are clear\nA yellow exclamation mark indicates that there is at least one search with a consider result\na blue 'U' indicates that there is at least one search with an unperformable result and requires your attention";
									    if(!(driver.findElement(By.xpath("//div[contains(@id,'popover')]/div[2]")).getText().equalsIgnoreCase(text))){
									    	sc.STAF_ReportEvent("Fail", "BG Dashboard- Result Type Column", "Result Type column text not displayed as per expected", 1);
									    }
									}
								}
								
								expResults = Globals.testSuiteXLS.getCellData_fromTestData("ExpectedOrderScore");
								expOrderedDate = Globals.testSuiteXLS.getCellData_fromTestData("OrderedDate");
								expPosition = Globals.testSuiteXLS.getCellData_fromTestData("PositionName");
								String expSearchReqFulfillment 	=	Globals.testSuiteXLS.getCellData_fromTestData("SearchReqFulfillment").trim();
								expSearchReqFulfillment 		= 	expSearchReqFulfillment.toUpperCase();
								String orderStatusFlag=Globals.testSuiteXLS.getCellData_fromTestData("Clear_Cons_Unper_OrderSearchTable_Flag");

								if(expOrderedDate.equalsIgnoreCase(orderDateUI) && expPosition.equalsIgnoreCase(positionUI) && expResults.equalsIgnoreCase(resultsUI)){
									sc.STAF_ReportEvent("Pass", "Volunteer Search - BG Dashboard", "Volunteer searched successfully.Name-"+expVolName, 1);
									//verify result type column as per search result
									if(expResults.equalsIgnoreCase("CLEAR")){
								
										tempretval=sc.isDisplayed("bgDashboard_ResultType_Clear_img");
										if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.isDisplayed("bgDashboard_ResultType_Consider_img").equalsIgnoreCase(Globals.KEYWORD_FAIL) && sc.isDisplayed("bgDashboard_ResultType_Unperformable_img").equalsIgnoreCase(Globals.KEYWORD_FAIL)){
											sc.STAF_ReportEvent("Pass", "Volunteer Search - BG Dashboard", "Result Type image is getting displayed as per expected", 1);
										}else{
											sc.STAF_ReportEvent("Fail", "Volunteer Search - BG Dashboard", "Result Type image is not getting displayed as per expected", 1);
										}
												
									}else{										
										String[] arrExpSearchReqStatusAndScore 	= expSearchReqFulfillment.split(";");
							    		int arrLength 							= arrExpSearchReqStatusAndScore.length;
							    		if(expSearchReqFulfillment.contains("ALL")){
							    			String expSearchReqScore 	= arrExpSearchReqStatusAndScore[0].split(",")[2];
							    			if(expSearchReqScore.equalsIgnoreCase("Consider")){
							    				if(sc.isDisplayed("bgDashboard_ResultType_Consider_img").equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.isDisplayed("bgDashboard_ResultType_Unperformable_img").equalsIgnoreCase(Globals.KEYWORD_FAIL) && sc.isDisplayed("bgDashboard_ResultType_Clear_img").equalsIgnoreCase(Globals.KEYWORD_FAIL)){
							    					sc.STAF_ReportEvent("Pass", "Volunteer Search - BG Dashboard", "Result Type- Consider image is getting displayed as per expected", 1);
							    				}else{
							    					sc.STAF_ReportEvent("Fail", "Volunteer Search - BG Dashboard", "Result Type- Consider image is not getting displayed as per expected", 1);
							    				}
							    			}else if(expSearchReqScore.equalsIgnoreCase("Unperformable")){
							    				if(sc.isDisplayed("bgDashboard_ResultType_Unperformable_img").equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.isDisplayed("bgDashboard_ResultType_Consider_img").equalsIgnoreCase(Globals.KEYWORD_FAIL) && sc.isDisplayed("bgDashboard_ResultType_Clear_img").equalsIgnoreCase(Globals.KEYWORD_FAIL)){
							    					sc.STAF_ReportEvent("Pass", "Volunteer Search - BG Dashboard", "Result Type- Unperformable image is getting displayed as per expected", 1);
							    				}else{
							    					sc.STAF_ReportEvent("Fail", "Volunteer Search - BG Dashboard", "Result Type- Unperformable image is not getting displayed as per expected", 1);
							    				}
							    			}
							    			
							    		}else{
							    			if ( ArrayUtils.contains( arrExpSearchReqStatusAndScore, "Consider" ) || ArrayUtils.contains( arrExpSearchReqStatusAndScore, "consider" )){
							    				if(sc.isDisplayed("bgDashboard_ResultType_Consider_img").equalsIgnoreCase(Globals.KEYWORD_PASS)){
													sc.STAF_ReportEvent("Pass", "Volunteer Search - BG Dashboard", "Result Type Consider image is getting displayed as per expected", 1);
												}else{
													sc.STAF_ReportEvent("Fail", "Volunteer Search - BG Dashboard", "Result Type Consider image is not getting displayed as per expected", 1);
												}
							    			}else if (ArrayUtils.contains( arrExpSearchReqStatusAndScore, "Unperformable" ) || ArrayUtils.contains( arrExpSearchReqStatusAndScore, "unperformable" )){
							    				if(sc.isDisplayed("bgDashboard_ResultType_Unperformable_img").equalsIgnoreCase(Globals.KEYWORD_PASS)){
													sc.STAF_ReportEvent("Pass", "Volunteer Search - BG Dashboard", "Result Type image is getting displayed as per expected", 1);
												}else{
													sc.STAF_ReportEvent("Fail", "Volunteer Search - BG Dashboard", "Result Type image is not getting displayed as per expected", 1);
												}
							    			}
							    			
							    		}
									}
																
									searchGrid.findElement(By.xpath("./tbody/tr[1]/td[2]/a")).click(); // click on volunteer name link to open bg report
							        String testCaseDescr=Globals.testSuiteXLS.getCellData_fromTestData("TestCaseDescr");
									String shrType=Globals.testSuiteXLS.getCellData_fromTestData("SharingType");
									if((shrType.equalsIgnoreCase("N/A")) && !(testCaseDescr.contains("L1 sharing Order with L3"))){
										tempretval = sc.waitforElementToDisplay("bgDashboard_sharingConsent_chk", 5);
									}else if(testCaseDescr.contains("L1 sharing Order with L3")){
										tempretval = sc.waitforElementToDisplay("bgDashboard_sharingConsent_chk", 5);
										if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
											sc.checkCheckBox("bgDashboard_sharingConsent_chk");
											sc.clickWhenElementIsClickable("bgDashboard_sharingConsentViewReport_btn", (int) timeOutinSeconds);
									
										}
									}else{
										tempretval = sc.waitforElementToDisplay("bgDashboard_sharingConsent_chk", 5);
										if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
											sc.checkCheckBox("bgDashboard_sharingConsent_chk");
											sc.clickWhenElementIsClickable("bgDashboard_sharingConsentViewReport_btn", (int) timeOutinSeconds);
									
										}
									}									
							        
									tempretval = Globals.KEYWORD_FAIL;
									tempretval = sc.waitforElementToDisplay("bgReport_volInfoSection_list", timeOutinSeconds);
									if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
										retval = Globals.KEYWORD_PASS;
									}else{
											sc.STAF_ReportEvent("Fail", "BG Report","Unable to load BG Report for Volunteer",1);
										}
							}else{
									sc.STAF_ReportEvent("Fail", "BG Dashboard", "Volunteers data mismatch.Kindly verify ResultStatus,OrderedDate and PositionName", 1);	
							} 
					}else{
						sc.STAF_ReportEvent("Fail", "BG Dashboard", "Volunteers name mismatch.Kindly verify volunteer first and last name", 1);
					}
				 }			
				}
			}	
			}		
			}
		return retval;

	}

	/**************************************************************************************************
	 * Method to verify report header for BG Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_verifyReportHeader_OrgUser() throws Exception{

		APP_LOGGER.startFunction("bgReport_verifyReportHeader_OrgUser");
		String retval=Globals.KEYWORD_FAIL;

		WebElement header = sc.createWebElement("bgReport_backgroundCheckReport_header");
		String expText = "Confidential Background Check Report";
		if(header.getText().equalsIgnoreCase(expText)){
			sc.scrollIntoView(header);
			sc.STAF_ReportEvent("Pass", "BG Report", "Report Header is as Expected.Val-"+expText, 1);
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "BG Report", "Report Header is not as expected.", 1);
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to fetch volunteer full name for BG Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_fetchVolunteerFullName() throws Exception {
		String volFullName ="";
		String volFnameName = sc.capitalizeFirstLetterInWord(Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName")) ;
		String volLnameName = sc.capitalizeFirstLetterInWord(Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName")) ;
		String volMidName = sc.capitalizeFirstLetterInWord(Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName")) ;

		if(volMidName.isEmpty() || volMidName == null){
			volFullName = volFnameName + " " + volLnameName;
		}else{
			volFullName = volFnameName + " " +volMidName +" " + volLnameName;
		}
		return volFullName;
	}
	
	/**************************************************************************************************
	 * Method to verify volunteer header section in BG Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_verifyVolunteerHeaderInfo(boolean volunteerEligibilityPerformed) throws Exception{

		APP_LOGGER.startFunction("bgReport_verifyVolunteerHeaderInfo");
		String retval=Globals.KEYWORD_FAIL;

		WebElement volunteerInfoHeader = driver.findElement(LocatorAccess.getLocator("bgReport_volName_list"));
		String volHeaderText =volunteerInfoHeader.getText();
		String npnOrderID = Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
		String swestOrderID = Globals.testSuiteXLS.getCellData_fromTestData("SWestOrderID");
		String eligibility = Globals.testSuiteXLS.getCellData_fromTestData("VolunteerEligible");
		swestOrderID = swestOrderID.replace("\\\n", "").trim();
		String expOrderDate = "";
		expOrderDate = fetchExpectedOrderedDate_reportFormat();

		String volFullName =bgReport_fetchVolunteerFullName();
		String expVolHeader="";


		expVolHeader = "Candidate: "+volFullName  ;
		expVolHeader = expVolHeader + "\nOrder ID: "+npnOrderID+"−"+swestOrderID ;
		expVolHeader = expVolHeader + "\nOrder Date: "+expOrderDate;

		if(volunteerEligibilityPerformed){
			expVolHeader = expVolHeader + "\nEligibility Date: "+expOrderDate;
		}
		sc.scrollIntoView(volunteerInfoHeader);
		if(expVolHeader.contains(volHeaderText)){
			sc.STAF_ReportEvent("Pass", "BG Report-Header", "Volunteer text is as Expected.", 1);
			retval = Globals.KEYWORD_PASS;
		}else{
			if(eligibility.equalsIgnoreCase("Eligible")||eligibility.equalsIgnoreCase("Eligible with Restrictions")){
				expVolHeader = expVolHeader + "\nEligibility Date: "+expOrderDate;
			}
			sc.scrollIntoView(volunteerInfoHeader);
			if(!expVolHeader.contains(volHeaderText)){
				sc.STAF_ReportEvent("Fail", "BG Report-Header", "Volunteer for text is NOT as Expected.", 1);
			}else{
				sc.STAF_ReportEvent("Pass", "BG Report-Header", "Volunteer text is as Expected.", 1);
				retval = Globals.KEYWORD_PASS;
			}
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify client info header section in BG Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_verifyClientInfo(boolean verifyCustomFields) throws Exception{

		APP_LOGGER.startFunction("bgReport_verifyClientInfo");
		String retval=Globals.KEYWORD_FAIL;
		String seamless = Globals.testSuiteXLS.getCellData_fromTestData("Seamless");
		String accountUse = Globals.testSuiteXLS.getCellData_fromTestData("AccountUse");
		//verify custom fields on report
		if((accountUse.equalsIgnoreCase("Employment")) && !(accountUse.equalsIgnoreCase("N/A"))){
			verifyCustomFields=false;
		}
		
		if((verifyCustomFields)){
		bgReport_verifyReportHeaderCustomFields_OrgUser("bgReport_customField1_text",1);
		bgReport_verifyReportHeaderCustomFields_OrgUser("bgReport_customField2_text",2);
		bgReport_verifyReportHeaderCustomFields_OrgUser("bgReport_customField3_text",3);
		}
		String expectedAccountName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
		String Accounttype = Globals.testSuiteXLS.getCellData_fromTestData("AccountUse");
		//verify Prepared for
		WebElement preparedTextEle =  driver.findElement(LocatorAccess.getLocator("bgReport_preparedFor_list"));
		String actualPreparedText =preparedTextEle.getText();
		String expPreparedText = "Prepared for:\n"+expectedAccountName +"\nAccount ID:";
		sc.scrollIntoView(preparedTextEle);
		if(actualPreparedText.contains(expPreparedText)){
			sc.STAF_ReportEvent("Pass", "BG Report-Header", "Prepared for text is as Expected.", 1);
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "BG Report-Header", "Prepared for text is NOT as Expected.", 1);
		}
		String expAcctType ="";
		if(Accounttype.equals("Volunteerism")){
			expAcctType="Background Check Use: Volunteerism/Non-employee position";
		}
		else{
			expAcctType="Background Check Use: Employment";
		}
		if(actualPreparedText.contains(expAcctType)){
			sc.STAF_ReportEvent("Pass", "BG Report-Header", "Background Check Use text is as Expected.", 1);
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "BG Report-Header", "Background Check Use text is NOT as Expected.", 1);
		}
		preparedTextEle =null;
		
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify volunteer info section in BG Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_verifyVolunteerInfo() throws Exception{

		APP_LOGGER.startFunction("bgReport_verifyVolunteerInfo");
		String retval=Globals.KEYWORD_FAIL;

		//verify Volunteer Information section
		WebElement volSectionEle = driver.findElement(LocatorAccess.getLocator("bgReport_volInfoSection_list"));
		String volSectionText = volSectionEle.getText();
		String volZipcode = Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode").trim();
		String volState = Globals.testSuiteXLS.getCellData_fromTestData("AddressState").toUpperCase();
		String volStateCode =  State.valueOf(volState).getAbbreviation();

		String volSSN =Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN");
		String volEmail=Globals.testSuiteXLS.getCellData_fromTestData("SetVolunteerEmail");
		String expectedSSNText="";
		String volPhoneNumber = Globals.Volunteer_Phone;
		String expPhoneText ="";
		String expEmailText = "";
		String expectedVolText ="";
		//expected phone number format - (901) 212-1234
		if(!volPhoneNumber.isEmpty() && volPhoneNumber != null){
			expPhoneText = volPhoneNumber.substring(0, 3);
			expPhoneText = "(" + expPhoneText +") ";
			expPhoneText = expPhoneText+volPhoneNumber.substring(3, 6) + "-" + volPhoneNumber.substring(6, 10);

		}


		if(!volSSN.isEmpty() && volSSN != null){
			expectedSSNText = "SSN: xxx-xx-" + volSSN.substring(5, 9)+"\n"; // last 3 digits
		}else{
			expectedSSNText = "SSN: Not Provided\n";
		}
		
		 if(!(volEmail.equalsIgnoreCase("No"))){
	            if(!volEmail.isEmpty() || volEmail != null){
	                expEmailText = Globals.fromEmailID; 
	            }    
	     }    
		
		String volFullName =bgReport_fetchVolunteerFullName();

		String abResponse =Globals.testSuiteXLS.getCellData_fromTestData("AbusePAResponse");
		String absprod=Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts");
		String addState=Globals.testSuiteXLS.getCellData_fromTestData("AddressState");
		
		if ((absprod.contains("ABUSE")) && (addState.equalsIgnoreCase("Pennsylvania")) || (absprod.contains("ABUSE(PA)")) ) {
			expectedVolText = volFullName+"\n"+Globals.Volunteer_AddressLine+", "+Globals.Volunteer_AddressLine+", "
					+Globals.Volunteer_City+", "+volStateCode+" "+volZipcode+"\n"+expectedSSNText + expEmailText +"\n" + expPhoneText +"\nHave you previously been screened for the Pennsylvania Neglect/Abuse Registry or Pennsylvania Child Abuse within the last 5 years?    Response: " + abResponse;
		
		}else{
			 expectedVolText = volFullName+"\n"+Globals.Volunteer_AddressLine+", "+Globals.Volunteer_AddressLine+", "
					+Globals.Volunteer_City+", "+volStateCode+" "+volZipcode+"\n"+expectedSSNText + expEmailText +"\n" + expPhoneText;
			
		}
		
		
		//String expectedVolText = volFullName+"\n"+Globals.Volunteer_AddressLine+", "+Globals.Volunteer_AddressLine+", "
			//	+Globals.Volunteer_City+", "+volStateCode+" "+volZipcode+"\n"+expectedSSNText + Globals.fromEmailID +"\n" + expPhoneText;
		sc.scrollIntoView(volSectionEle);
		if(expectedVolText.contains(volSectionText)){
			sc.STAF_ReportEvent("Pass", "BG Report-Header", "Volunteer Information section text is as Expected.", 1);
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "BG Report-Header", "Volunteer Information section for text is NOT as Expected.", 1);
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to verify screening summary section in BG Reports for AYSO
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_verifyScreeningSummary() throws Exception{

		APP_LOGGER.startFunction("bgReport_verifyScreeningSummaryAYSO");
		String retval=Globals.KEYWORD_FAIL;
		String expScrenSummText="";
		int timeOutinSeconds=60;
		
		sc.clickWhenElementIsClickable("bgReport_screeningsummary_btn", timeOutinSeconds);
		
		retval=sc.waitTillElementDisplayed("bgReport_screeningsummary_text", timeOutinSeconds);
		//verify Volunteer Information section
		if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			WebElement volSectionEle = driver.findElement(LocatorAccess.getLocator("bgReport_screeningsummary_text"));
			String actScnSummText = volSectionEle.getText();
			
			expScrenSummText= Globals.testSuiteXLS.getCellData_fromTestData("Exp_ScreeningSummarySection").trim();
			
			String fname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName").trim();
			String lname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName").trim();
			
			
			String date =  driver.findElement(By.xpath("//*[@id='0']/div[2]/div/header/ul[2]/li[3]")).getText().trim();
			String actualDate=date.split(":")[1].toString().trim();
			String aysoFlagans=Globals.testSuiteXLS.getCellData_fromTestData("CustomField1_LOV").trim();
			if(aysoFlagans.equalsIgnoreCase("n") || aysoFlagans.equalsIgnoreCase("no") ){
				aysoFlagans="No";
			}else if(aysoFlagans.equalsIgnoreCase("y") || aysoFlagans.equalsIgnoreCase("yes") ){
				aysoFlagans="Yes";
			}
			expScrenSummText=expScrenSummText.replace("fname", fname);
			expScrenSummText=expScrenSummText.replace("lname", lname);
			expScrenSummText=expScrenSummText.replace("Date", actualDate);
			expScrenSummText=expScrenSummText.replace("Flag", aysoFlagans);
			
			if(expScrenSummText.contains(actScnSummText)){
				sc.STAF_ReportEvent("Pass", "BG Report-Screening Summary Section", "Screening Summary Section text is as Expected.", 1);
				retval = Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "BG Report-Screening Summary Section", "Screening Summary Section text is NOT as Expected.", 1);
				
			}
			
		}else{
				sc.STAF_ReportEvent("Fail", "BG Report","Unable to get Screening Summary section",1);
		}
		
		
		return retval;
	}

	
	/**************************************************************************************************
	 * Method to verify screening summary section in BG Reports for AYSO
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_verifyScreeningSummaryQuestionnaire() throws Exception{

		APP_LOGGER.startFunction("bgReport_verifyScreeningSummaryQuestionnaire");
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		String expScrenSummText="";
		String lblnm="";
		int timeOutinSeconds=60;
		
		sc.clickWhenElementIsClickable("bgReport_screeningsummary_btn", timeOutinSeconds);
		
		retval=sc.waitTillElementDisplayed("bgReport_screeningsummary_text", timeOutinSeconds);
		//verify Volunteer Information section
		if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			WebElement volSectionEle = driver.findElement(LocatorAccess.getLocator("bgReport_screeningsummary_text"));
			String actScnSummText = volSectionEle.getText();
			
			expScrenSummText= Globals.testSuiteXLS.getCellData_fromTestData("Exp_ScreeningSummarySection").trim();
			
			String fname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName").trim();
			String lname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName").trim();
			
			
			String date =  driver.findElement(By.xpath("//*[@id='0']/div[2]/div/header/ul[2]/li[3]")).getText().trim();
			String actualDate=date.split(":")[1].toString().trim();
			
			expScrenSummText=expScrenSummText.replace("fname", fname);
			expScrenSummText=expScrenSummText.replace("lname", lname);
			expScrenSummText=expScrenSummText.replace("Date", actualDate);
			//expScrenSummText=expScrenSummText.replace("Flag", aysoFlagans);
			
			if(expScrenSummText.contains(actScnSummText)){
				sc.STAF_ReportEvent("Pass", "BG Report-Screening Summary Section", "Screening Summary Section text is as Expected.", 1);
				
			}else{
				sc.STAF_ReportEvent("Fail", "BG Report-Screening Summary Section", "Screening Summary Section text is NOT as Expected.", 1);				
			}
			
			tempretval=sc.waitTillElementDisplayed("bgReport_screeningsummary_HideQuestion_link", timeOutinSeconds);
			
			sc.clickWhenElementIsClickable("bgReport_screeningsummary_HideQuestion_link", timeOutinSeconds);
			
			tempretval=sc.isDisplayed("bgReport_screeningsummary_Questionnaire_table");
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Pass", "BG Report-Screening Summary Section", "After click on hide questionnaire link, Questionnaire section is getting as hidden ", 1);	
			}else{
				sc.STAF_ReportEvent("Pass", "BG Report-Screening Summary Section", "After click on hide questionnaire link, Questionnaire section is not hidden format ", 1);
			}
			sc.clickWhenElementIsClickable("bgReport_screeningsummary_ViewQuestion_link", timeOutinSeconds);
			
			switchTabAndVerifyQuestionnaireTab();
			
			sc.clickWhenElementIsClickable("bgReport_screeningsummary_ShowQuestion_link", timeOutinSeconds);
			retval=sc.waitTillElementDisplayed("bgReport_screeningsummary_HideQuestion_link", timeOutinSeconds);
						
			if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "BG Report-Screening Summary Section", "After click on Show questionnaire link, Questionnaire section is displayed correctly", 1);	
			}else{
				sc.STAF_ReportEvent("Fail", "BG Report-Screening Summary Section", "After click on Show questionnaire link, Questionnaire section is not displayed", 1);
			}
			sc.clickWhenElementIsClickable("bgReport_screeningsummary_btn", timeOutinSeconds);
			tempretval=sc.isDisplayed("bgReport_screeningsummary_ShowQuestion_link");
			if(!tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "BG Report-Screening Summary Section", "After click on - Screening Summary link, Screening Summary section is not getting as hidden", 1);	
			}
			
		}else{
				sc.STAF_ReportEvent("Fail", "BG Report","Unable to get Screening Summary section",1);
				
		}
		
		return retval;
	}

	/**************************************************************************************************
	 * Method to fecth expected Quick View Text  in BG Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_fetchQuickViewText() throws Exception {

		String actualQuickViewText = "";

		WebElement quickView = sc.createWebElement("bgReport_quickView_list");
		sc.scrollIntoView(quickView);
		actualQuickViewText = quickView.getText();

		//		TODO- Uncomment the below code to push the headers into excel
		//		Globals.testSuiteXLS.setCellData_inTestData("Exp_ReportQuickViewText", actualQuickViewText);
		return actualQuickViewText;
	}

	/**************************************************************************************************
	 * Method to verify Quick View Text section in BG Reports based on type of view
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @param String - typeOfView can be volunteer/badge value only
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_verifyQuickViewText(String typeOfView) throws Exception {


		APP_LOGGER.startFunction("bgReport_verifyQuickViewText");
		String retval=Globals.KEYWORD_FAIL;
		String actualQuickViewText = "";
		String expQuickViewText ="";

		actualQuickViewText = bgReport_fetchQuickViewText();

		expQuickViewText= Globals.testSuiteXLS.getCellData_fromTestData("Exp_ReportQuickViewText").trim();
		//need to check whether volunteer has been marked as eligibile or not
		String accName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName").trim();
		String oldChar="";

		if(typeOfView.equalsIgnoreCase("volunteer") || typeOfView.equalsIgnoreCase("badge")){

			oldChar = "Your background check is pending review by "+accName+".\n";
			expQuickViewText = expQuickViewText.replace(oldChar, "");

		}

		if (typeOfView.equalsIgnoreCase("badge")){
			String inelig= Globals.testSuiteXLS.getCellData_fromTestData("VolunteerEligible").trim();
			if(inelig.equalsIgnoreCase("Ineligible")){
				oldChar ="";
				oldChar ="Your background check has been viewed by "+accName+".  Your history contains some information they’d like to review.\n";
				expQuickViewText = oldChar + expQuickViewText;
			}else{
				oldChar ="";
				oldChar =accName+" has reviewed your background check.\n";
				expQuickViewText = oldChar + expQuickViewText;
			}
			
		}
	
		if(expQuickViewText.equals(actualQuickViewText) ){
			sc.STAF_ReportEvent("Pass", "Volunteer Quick View", "Text is as Expected", 1);
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "Volunteer Quick View", "Text is not as Expected.", 1);
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to fetch expected background report summary text section in BG Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_fetchBackgroundCheckReportSummary() throws Exception {
		//Background Check Report Summary-Start
		WebElement productOverview = sc.createWebElement("bgReport_productStatus_list");
		sc.scrollIntoView(productOverview);
		String actualProductOverview = "";
		actualProductOverview = productOverview.getText();

		//TODO- Uncomment the below code to push the headers into excel
		//		Globals.testSuiteXLS.setCellData_inTestData("Exp_ReportProductOverview", actualProductOverview);

		return actualProductOverview;
	}

	/**************************************************************************************************
	 * Method to verify Background Check Summary Text section in BG Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_verifyBackgroundCheckReportSummary() throws Exception {

		APP_LOGGER.startFunction("bgReport_verifyBackgroundCheckReportSummary");
		String retval=Globals.KEYWORD_FAIL;

		String actualProductOverview = "";
		String expProductOverview="";

		actualProductOverview = bgReport_fetchBackgroundCheckReportSummary();
		expProductOverview= Globals.testSuiteXLS.getCellData_fromTestData("Exp_ReportProductOverview").trim();

		if(expProductOverview.equals(actualProductOverview) ){
			sc.STAF_ReportEvent("Pass", "Background Check Report Summary", "Text is as Expected", 1); 
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "Background Check Report Summary", "Text is not as Expected.", 1);
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify Product Summary section in BG reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_verifyPoductSumary() throws Exception{


		APP_LOGGER.startFunction("bgReport_verifyPoductSumary");
		String retval=Globals.KEYWORD_FAIL;

		WebElement productSummary = sc.createWebElement("bgReport_productSummary_text");
		WebElement productOverview = sc.createWebElement("bgReport_productStatus_list");			
		List<WebElement> productsSummaryList = productSummary.findElements(By.xpath("./div[@class='report-summary']"));
		List<WebElement> bgReportSummary = productOverview.findElements(By.xpath("./li[@class='status']"));
		String productHeaders =null;
		String expProductHeader="";
		String[] arrProductHeader=null;
		String[] arrBGReportSummary = new String[bgReportSummary.size()];
		WebElement productElement = null;



		int expProductCount = 0;
		int actualProductCount =0;


		for (int j=0;j<bgReportSummary.size();j++){
			productHeaders = bgReportSummary.get(j).findElement(By.xpath("./span")).getText();
			arrBGReportSummary[j] = productHeaders;
		}


		actualProductCount = productsSummaryList.size();
		String[] arrActualProductHeader = new String[actualProductCount] ;



		for (int j=0;j<actualProductCount;j++){
			productElement = productsSummaryList.get(j);
			productHeaders = productElement.findElement(By.xpath("./h4/span")).getText();
			arrActualProductHeader[j] = productHeaders;
		}

		//		//TODO- Uncomment the below code to push the headers into excel
		//		String dummyHeader="";
		//		for(int i=0;i<arrActualProductHeader.length;i++){
		//			if(dummyHeader.isEmpty() || dummyHeader == null){
		//				dummyHeader = arrActualProductHeader[i];
		//			}else{
		//				dummyHeader =dummyHeader +"\n"+ arrActualProductHeader[i];
		//			}
		//		}
		//		Globals.testSuiteXLS.setCellData_inTestData("Exp_ReportProductHeaders", dummyHeader);

		expProductHeader = Globals.testSuiteXLS.getCellData_fromTestData("Exp_ReportProductHeaders");
		arrProductHeader = expProductHeader.split("\\\n");
		expProductCount = arrProductHeader.length;


		String[] diff = sc.differences(arrProductHeader, arrActualProductHeader);
		System.out.println(Arrays.toString(diff));

		if(diff.length == 0){
			sc.STAF_ReportEvent("Pass", "Background Check Report", "Products in report are as expected.Exp="+expProductCount , 0);
			retval= Globals.KEYWORD_PASS;
		}else{
			for(int i=0;i<diff.length;i++){
				sc.STAF_ReportEvent("Fail", "Background Check Report", "Product present/missing which is not expected-"+diff[i] , 0);
			}
		}


		for(int i=0;i<arrActualProductHeader.length;i++){

			productElement = productsSummaryList.get(i);
			verifyProductDetails_BGReport(arrBGReportSummary[i],productElement);

		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify BG report when accessed from BG Dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyBGDashboardReport() throws Exception {
		Globals.Component = "BG Dashboard Report";
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		String tempretval1=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{	
			
			tempretval = sc.waitforElementToDisplay("bgReport_volInfoSection_list", timeOutinSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "BG Report", "Volunteer BG Report page has not loaded", 1);
			}else{
				String viewReportvalue=Globals.testSuiteXLS.getCellData_fromTestData("BgDashboard_ReportView");
				String aysoFlag=Globals.testSuiteXLS.getCellData_fromTestData("CustomField2");
				String questFlag=Globals.testSuiteXLS.getCellData_fromTestData("Questionnaire");
				if (viewReportvalue.equalsIgnoreCase("Detail Report")){

					bgReport_verifyReportHeader_OrgUser();
					//AYSO screening summary validation
					bgReport_verifyLinkReadReport_BGReport("bgReport_OrgReportRead_link");
					if(aysoFlag.equalsIgnoreCase("AYSO ID")){
						bgReport_verifyScreeningSummary();	
					}else if(questFlag.equalsIgnoreCase("yes")){
						bgReport_verifyScreeningSummaryQuestionnaire();	
					}
					bgReport_verifyClientInfo(true);
					bgReport_verifyVolunteerHeaderInfo(false);
					bgReport_verifyVolunteerInfo();
					bgReport_verifyQuickViewText("orguser");
					bgReport_verifyBackgroundCheckReportSummary();
					bgReport_verifyPoductSumary();	
					retval = VolunteerEligibility();
				}else if(viewReportvalue.equalsIgnoreCase("Summary Report")){
					bgReport_verifyReportHeader_OrgUser();
					bgReport_verifyLinkReadReport_BGReport("bgReport_OrgReportRead_link");
					if(aysoFlag.equalsIgnoreCase("AYSO ID")){
						bgReport_verifyScreeningSummary();	
					}else if(questFlag.equalsIgnoreCase("yes")){
						bgReport_verifyScreeningSummaryQuestionnaire();	
					}
					bgReport_verifyClientInfo(true);
					bgReport_verifyVolunteerHeaderInfo(false);
					bgReport_verifyVolunteerInfo();
					bgReport_verifyQuickViewText("orguser");
					bgReport_verifyBackgroundCheckReportSummary();
					tempretval = sc.waitforElementToDisplay("bgReport_productSummary_text", 3);
					tempretval1 = sc.waitforElementToDisplay("bgReport_generalDisclaimer_text", 3);
					if((tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)) && (tempretval1.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
						sc.STAF_ReportEvent("Pass", "Summary BG Report", "Volunteer Summary BG Report page displayed correctly", 1);
					}else{
						sc.STAF_ReportEvent("Fail", "Summary BG Report", "Volunteer Summary BG Report page displayed as detail report page", 1);
					}
					retval = VolunteerEligibility();
				}

			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "BG Report", "Unable to verify BG Report due to exception.Exception-"+e.toString(), 1);
			throw new Exception("Unable to verify BG Report due to exception.Exception-"+e.toString());
		}


		return retval;
	}

	/**************************************************************************************************
	 * Method to verify BG report when accessed from BG Dashboard for PVT
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyBGDashboardReportPVT() throws Exception {
		Globals.Component = "BG Dashboard Report FOR PVT";
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		String tempretval1=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{	
			
			tempretval = sc.waitforElementToDisplay("bgReport_volInfoSection_list", timeOutinSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "BG Report", "Volunteer BG Report page has not loaded", 1);
			}else{
				String viewReportvalue=Globals.testSuiteXLS.getCellData_fromTestData("BgDashboard_ReportView");
				String aysoFlag=Globals.testSuiteXLS.getCellData_fromTestData("CustomField2");
				String questFlag=Globals.testSuiteXLS.getCellData_fromTestData("Questionnaire");
				if (viewReportvalue.equalsIgnoreCase("Detail Report")){

					bgReport_verifyReportHeader_OrgUser();
					//AYSO screening summary validation
					bgReport_verifyLinkReadReport_BGReport("bgReport_OrgReportRead_link");
					if(aysoFlag.equalsIgnoreCase("AYSO ID")){
						bgReport_verifyScreeningSummary();	
					}else if(questFlag.equalsIgnoreCase("yes")){
						bgReport_verifyScreeningSummaryQuestionnaire();	
					}
					bgReport_verifyClientInfo(true);
					bgReport_verifyVolunteerHeaderInfo(false);
					if(!Globals.EXECUTION_MACHINE.equalsIgnoreCase("jenkins")) {
					bgReport_verifyVolunteerInfo();
					}
					bgReport_verifyQuickViewText("orguser");
					bgReport_verifyBackgroundCheckReportSummary();
					retval=bgReport_verifyPoductSumary();	
					//retval = VolunteerEligibility();
				}else if(viewReportvalue.equalsIgnoreCase("Summary Report")){
					bgReport_verifyReportHeader_OrgUser();
					bgReport_verifyLinkReadReport_BGReport("bgReport_OrgReportRead_link");
					if(aysoFlag.equalsIgnoreCase("AYSO ID")){
						bgReport_verifyScreeningSummary();	
					}else if(questFlag.equalsIgnoreCase("yes")){
						bgReport_verifyScreeningSummaryQuestionnaire();	
					}
					bgReport_verifyClientInfo(true);
					bgReport_verifyVolunteerHeaderInfo(false);
					bgReport_verifyVolunteerInfo();
					bgReport_verifyQuickViewText("orguser");
					bgReport_verifyBackgroundCheckReportSummary();
					tempretval = sc.waitforElementToDisplay("bgReport_productSummary_text", 3);
					tempretval1 = sc.waitforElementToDisplay("bgReport_generalDisclaimer_text", 3);
					if((tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)) && (tempretval1.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
						sc.STAF_ReportEvent("Pass", "Summary BG Report", "Volunteer Summary BG Report page displayed correctly", 1);
						retval=Globals.KEYWORD_PASS;
					}else{
						sc.STAF_ReportEvent("Fail", "Summary BG Report", "Volunteer Summary BG Report page displayed as detail report page", 1);
					}
					//retval = VolunteerEligibility();
				}
				
				sc.clickWhenElementIsClickable("bgReport_close_btn", 10);
				retval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

				while (retval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
					Thread.sleep(2000);
					retval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

				}
				if(sc.waitforElementToDisplay("bgDashboard_search_txt", 10).equalsIgnoreCase(Globals.KEYWORD_PASS)){
					retval=Globals.KEYWORD_PASS;
					sc.STAF_ReportEvent("Pass", "BG Dashboard", "User navigated to BG Dashboard Page", 1);
				}else{
					sc.STAF_ReportEvent("Fail", "BG Dashboard", "unable to navigated to BG Dashboard Page", 1);
					retval=Globals.KEYWORD_FAIL;
				}

			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "BG Report", "Unable to verify BG Report due to exception.Exception-"+e.toString(), 1);
			throw new Exception("Unable to verify BG Report due to exception.Exception-"+e.toString());
		}


		return retval;
	}

	
	
	
	/**************************************************************************************************
	 * Method to verify BG report for API order using Report URL
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyReportURLBGReport() throws Exception {
		Globals.Component = "API Order- Report URL BG Report";
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		
		long timeOutinSeconds = 60;

		try{	
			
			tempretval = sc.waitforElementToDisplay("bgReport_reporturl_searchWait_popup", 2);

			while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(2000);
				tempretval = sc.waitforElementToDisplay("bgReport_reporturl_searchWait_popup", 2);

			}

			tempretval = sc.waitforElementToDisplay("bgReport_volInfoSection_list", timeOutinSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "API Order- Report URL BG Report", "Volunteer BG Report page has not loaded", 1);
			}else{
					bgReport_verifyReportHeader_OrgUser();
					bgReport_verifyClientInfo(true);
					bgReport_verifyVolunteerHeaderInfo(false);
					bgReport_verifyVolunteerInfo();
					bgReport_verifyQuickViewText("orguser");
					bgReport_verifyBackgroundCheckReportSummary();
					bgReport_verifyPoductSumary();	
					retval = reportURLVolunteerEligibility();
				
			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "BG Report", "Unable to verify BG Report due to exception.Exception-"+e.toString(), 1);
			throw new Exception("Unable to verify BG Report due to exception.Exception-"+e.toString());
		}


		return retval;
	}

	
	/**************************************************************************************************
	 * Method to verify Individual product detail section in BG Report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyProductDetails_BGReport(String productHeader, WebElement productElement){

		//verify status details of the product
		String actualStatusMsg="";
		String expSearchReqStatus ="";
		String expStatusMsg="";
		String orderedDate="";
		orderedDate = fetchExpectedOrderedDate_reportFormat();	
		WebElement statusElement = productElement.findElement(By.xpath("./div[@class='reportStatus']"));
		actualStatusMsg = statusElement.getText();

		//expected format Status: CLEAR Requested: July 18, 2016 Completed: July 18, 2016
		String expSearchReqFulfillment 	=	Globals.testSuiteXLS.getCellData_fromTestData("SearchReqFulfillment").trim();
		if(expSearchReqFulfillment == "" || expSearchReqFulfillment.isEmpty() || expSearchReqFulfillment ==null){

			sc.STAF_ReportEvent("Fail", "Order Fulfillment", "Expected status and score was not provided in test data",0);

		}else{	
			String[] arrExpSearchReqStatusAndScore 	= expSearchReqFulfillment.split(";");
			int arrLength 							= arrExpSearchReqStatusAndScore.length;

			if(expSearchReqFulfillment.toUpperCase().contains("ALL")){ // case where all search request will be closed with the same search req status and score

				if(arrLength == 1){

					expSearchReqStatus 	= arrExpSearchReqStatusAndScore[0].split(",")[2].toUpperCase();
				}

				expStatusMsg = "Status: "+expSearchReqStatus+" Requested: "+orderedDate+" Completed: "+orderedDate;
				sc.scrollIntoView(statusElement);
				if(expStatusMsg.equalsIgnoreCase(actualStatusMsg)){
					sc.STAF_ReportEvent("Pass", "BG Report-Product Status", "Status Text is as Expected for-"+productHeader, 1); 
				}else{
					sc.STAF_ReportEvent("Fail","BG Report-Product Status", "Status Text is NOT as Expected for-"+productHeader, 1);
				}
			}
			
			boolean isAlias = false;
			boolean isStatusClear = false;
			if(expSearchReqStatus.equalsIgnoreCase("Clear")){
				isStatusClear = true;
			}

			if(productHeader.contains("OFAC")){
				if(productHeader.contains("(Alias:")){
					isAlias = true;
				}
				bgReport_StandardProduct(productElement,"OFAC",isAlias,isStatusClear);	

			}else if(productHeader.contains("DOJ Sex Offender")){
				if(productHeader.contains("(Alias:")){
					isAlias = true;
				}
				bgReport_SexOffender(productElement,isAlias,isStatusClear);

			}else if(productHeader.contains("Federal Criminal Search")){
				if(productHeader.contains("(Alias:")){
					isAlias = true;
				}
				bgReport_StandardProduct(productElement,"FederalCriminal",isAlias,isStatusClear);	


			}else if(productHeader.contains("Federal Civil Search")){
				if(productHeader.contains("(Alias:")){
					isAlias = true;
				}
				bgReport_StandardProduct(productElement,"FederalCivil",isAlias,isStatusClear);


			}else if(productHeader.contains("County Civil Search")){
				if(productHeader.contains("(Alias:")){
					isAlias = true;
				}
				bgReport_StandardProduct(productElement,"CountyCivil",isAlias,isStatusClear);

			}else if(productHeader.contains("Motor Vehicle Record")){

				bgReport_MVR(productElement);

			}else if(productHeader.contains("Consumer Credit Check")){

				bgReport_Credit(productElement);

			}else if(productHeader.contains("Reference Interview")){

				bgReport_Reference(productElement);
			}else if(productHeader.contains("GLOBEX")){

				bgReport_Globex(productElement);
			}else if(productHeader.contains("OIG-GSA Excluded Parties")){
				if(productHeader.contains("(Alias:")){
					isAlias = true;
				}
				bgReport_OIG(productElement,isAlias,isStatusClear);

			}else if(productHeader.contains("Neglect/Abuse Registry")){

				String abuseState="";
				if (productHeader.contains("(Alias:")) {
					isAlias=true;
				}

				if (productHeader.contains("Pennsylvania")) {
					abuseState="PA";
				}else if (productHeader.contains("Colorado")) {
					abuseState="CO";
				}

				bgReport_Abuse(productElement,isAlias,abuseState,isStatusClear);

			}else if(productHeader.contains("SSN Profile")){

				bgReport_SSNProfile(productElement);

			}else if(productHeader.contains("County Criminal Search")){
				if(productHeader.contains("(Alias:")){
					isAlias = true;
				}
				bgReport_StandardProduct(productElement,"CountyCriminal",isAlias,isStatusClear);


			}else if(productHeader.contains("State Criminal Search")){
				if(productHeader.contains("(Alias:")){
					isAlias = true;
				}
				bgReport_StandardProduct(productElement,"StateCriminal",isAlias,isStatusClear);


			}else if(productHeader.contains("Locator Select")){
				if(productHeader.contains("(Alias:")){
					isAlias = true;
				}
				bgReport_LocatorSelect(productElement,isAlias);

			}else{
				sc.STAF_ReportEvent("Fail","BG Report", "Unable to verify product as Its not handled in Automation", 1);
			}
		}


	}

	/**************************************************************************************************
	 * Method to verify Sex Offender product details in BG Report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void bgReport_SexOffender(WebElement productElement,boolean isAliasName,boolean isStatusClear){
		String product = "SexOffender";
		String expReportText = "";
		String staticText ="\nComments:\nLimitations on the DOJ Sex Offender\n"+ "Nevada: This Search does not provide results from the state of Nevada, as by both statue and regulations, information from the state of Nevada Sex Offender Registry website cannot be used for employment purposes and cannot be distributed commercially.\nOregon: This Search has special conditions from the state of Oregon. Information is only provided for sex offenders who have been designated as predatory, as provided in ORS 181.585, who have been determined to present the highest risk of reoffending and to require the widest range of notification or are found to be a sexually violent dangerous offender under ORS 144.635.";
		String statusText="";
		String ssnText="\n" + bgReport_fetchVolunteerSSNText() +"\n"+bgReport_fetchVolunteerDOBText();
		int count =0;
		String tempretval="";

		if (isStatusClear){
			statusText = "\nVerified Data:\nNo data found";	
		}else{
			statusText = "\nVerified Data:";
		}

		if(isAliasName){
			count = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
		}

		if(count==0){
			expReportText = bgReport_fetchVolunteerNameText() +ssnText + statusText+staticText;

			bgReport_verifyProductReport_WithReporting(productElement,product,expReportText);
		}else{
			boolean reportTextMatched = false;
			for(int i=1;i<=count;i++){
				tempretval = Globals.KEYWORD_FAIL;
				expReportText="";
				expReportText = bgReport_fetchVolunteerAliasText(i);
				expReportText = expReportText + ssnText + statusText+staticText;
				tempretval = bgReport_verifyProduct_BGReport(productElement, product, expReportText);

				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					reportTextMatched = true;
					break;
				}
			}

			if(reportTextMatched){
				sc.STAF_ReportEvent("Pass", "BG Report-"+product, "Report Text is as Expected", 1); 
			}else{
				sc.STAF_ReportEvent("Fail","BG Report-"+product, "Report Text is NOT as Expected", 1);
			}
		}


	}

	/**************************************************************************************************
	 * Method to verify Standard product details in BG Report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void bgReport_StandardProduct(WebElement productElement,String product,boolean isAliasName,boolean isStatusClear){
		String expReportText = "";
		int count =0;
		String statusText="";
		String ssnText = "\n" + bgReport_fetchVolunteerSSNText() +"\n"+bgReport_fetchVolunteerDOBText();
		String tempretval;

		if (isStatusClear){
			statusText = "\nVerified Data:\nNo data found";
		}else{
			statusText = "\nVerified Data:";
		}

		if(isAliasName){
			count = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
		}

		if(count==0){
			expReportText = bgReport_fetchVolunteerNameText() +ssnText + statusText;

			bgReport_verifyProductReport_WithReporting(productElement,product,expReportText);
		}else{
			boolean reportTextMatched = false;
			for(int i=1;i<=count;i++){
				tempretval = Globals.KEYWORD_FAIL;
				expReportText="";
				expReportText = bgReport_fetchVolunteerAliasText(i);
				expReportText = expReportText + ssnText + statusText;
				tempretval = bgReport_verifyProduct_BGReport(productElement, product, expReportText);

				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					reportTextMatched = true;
					break;
				}
			}

			if(reportTextMatched){
				sc.STAF_ReportEvent("Pass", "BG Report-"+product, "Report Text is as Expected", 1); 
			}else{
				sc.STAF_ReportEvent("Fail","BG Report-"+product, "Report Text is NOT as Expected", 1);
			}
		}


	}


	/**************************************************************************************************
	 * Method to verify MVR product details in BG Report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void bgReport_MVR(WebElement productElement){
		String product="MVR";
		String expReportText = "";
		expReportText = bgReport_fetchVolunteerNameText();
		expReportText = expReportText +"\n" +bgReport_fetchVolunteerDOBText() + "\n";
		//expReportText = expReportText +"\nVerified Data:\n"+bgReport_fetchMVRVerifiedData();
		expReportText = expReportText +bgReport_fetchMVRVerifiedData();
		expReportText=expReportText +"\nVerified Data:" +"\n" + "No Motor Vehicle was found for this individual";

		bgReport_verifyProductReport_WithReporting(productElement,product,expReportText);	


	}

	/**************************************************************************************************
	 * Method to verify Credit product details in BG Report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void bgReport_Credit(WebElement productElement){
		String product = "Credit";
		String expReportText = "";
		expReportText = bgReport_fetchVolunteerNameText();
		String expReportText1 = expReportText +"\n" + bgReport_fetchVolunteerSSNText() ;
		expReportText=expReportText1+"\nStreet Address: "+Globals.Volunteer_AddressLine+"\nCity: "+sc.capitalizeFirstLetterInWord(Globals.Volunteer_City)+"\n";

		String state = Globals.testSuiteXLS.getCellData_fromTestData("AddressState").toUpperCase();
		String zipCode = Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode");
		String dlStateCode = State.valueOf(state).abbreviation;
		expReportText=expReportText+"State: "+dlStateCode+"\nZip Code: "+zipCode;
		expReportText=expReportText +"\nVerified Data:";
	
		String retval =  bgReport_verifyProduct_BGReport(productElement, product, expReportText);
		if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			expReportText1=expReportText1+"\nFCRA regulations do not permit us to display your credit report on this report. However, you may still obtain your credit report by contacting The Advocates Customer Care team at (855) 326-1860 option 1 or email TheAdvocates@VerifiedVolunteers.com";
			retval =  bgReport_verifyProduct_BGReport(productElement, product, expReportText1);
			if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","BG Report-"+product, "Report Text is NOT as Expected", 1);
			}else{
				sc.STAF_ReportEvent("Pass", "BG Report-"+product, "Report Text is as Expected", 1); 
				retval = Globals.KEYWORD_PASS;
			}
		}else{
			sc.STAF_ReportEvent("Pass", "BG Report-"+product, "Report Text is as Expected", 1); 
			retval = Globals.KEYWORD_PASS;
		}

		
		
	}
	
	/**************************************************************************************************
	 * Method to verify Globex product details in BG Report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static void bgReport_Globex(WebElement productElement){
		
		String product = "Globex";
		String expReportText = "";
		//String gender = Globals.Volunteer_Gender.toLowerCase();
		expReportText = bgReport_fetchVolunteerNameText();
		expReportText = expReportText +"\n"+bgReport_fetchVolunteerDOBText();
		expReportText = expReportText +"\nVerified Data:\nNo data found";
		

		bgReport_verifyProductReport_WithReporting(productElement,product,expReportText);	


	}

	/**************************************************************************************************
	 * Method to verify Reference product details in BG Report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void bgReport_Reference(WebElement productElement){
		String product = "Reference";
		String expReportText = "";
		String tempretval="";

		String volPhoneNumber = Globals.Volunteer_Phone;
		int noOfAlias = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
		String expPhoneText="";

		if(!volPhoneNumber.isEmpty() && volPhoneNumber != null){
			expPhoneText = volPhoneNumber.substring(0, 3);
			expPhoneText = "(" + expPhoneText +") ";
			expPhoneText = expPhoneText+volPhoneNumber.substring(3, 6) + "-" + volPhoneNumber.substring(6, 10);

		}
		String referenceName = Globals.testSuiteXLS.getCellData_fromTestData("ReferenceNames");
		String[] refNames = referenceName.split(";");
		boolean refReportFound = false;
		int count = 0;

		for(int i =0;i<refNames.length;i++){
			tempretval = Globals.KEYWORD_FAIL;
			expReportText="";
			expReportText = bgReport_fetchVolunteerNameText();
			expReportText = expReportText +"\nReference Name: "+refNames[i];
			expReportText = expReportText+"\nRelationship: " +Globals.Volunteer_ReferenceRelationship+"\n" + "Phone: "+expPhoneText;

			if(noOfAlias >0){
				count = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
				if(count==1){
					expReportText = expReportText +"\nNotes: ALIAS NAME(S): ["+Globals.Volunteer_AliasName+", "+Globals.Volunteer_AliasName+" ]";
				}else{
					String volLname=Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
					String volFname=Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
					expReportText = expReportText +"\nNotes: ALIAS NAME(S): ["+Globals.Volunteer_AliasName+", "+Globals.Volunteer_AliasName+" ], ["+volLname+", "+volFname+" ]";	
				}

			}

			expReportText = expReportText +"\nVerified Data:";
			tempretval = bgReport_verifyProduct_BGReport(productElement, product, expReportText);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				refReportFound = true;
				break;
			}
		}

		if(refReportFound){
			sc.STAF_ReportEvent("Pass", "BG Report-"+product, "Report Text is as Expected", 1); 

		}else{
			sc.STAF_ReportEvent("Fail","BG Report-"+product, "Report Text is NOT as Expected", 1);
		}

	}

	/**************************************************************************************************
	 * Method to verify OIG product details in BG Report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void bgReport_OIG(WebElement productElement,boolean isAliasName,boolean isStatusClear){
		String product = "OIG";
		String expReportText = "";
		expReportText = bgReport_fetchVolunteerNameText();
		String ssnText = "\n" + bgReport_fetchVolunteerSSNText() +"\n"+bgReport_fetchVolunteerDOBText()+"\nExcluded Type: OIG-GSA-Combined GSA and OIG\n";
		String statusText="";	

		if (isStatusClear){
			statusText = "Verified Data:\nNo data found";	
		}else{
			statusText = "Verified Data:";
		}

		int count =0;
		String tempretval="";
		if(isAliasName){
			count = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
		}

		if(count==0){
			expReportText = bgReport_fetchVolunteerNameText() +ssnText + statusText;

			bgReport_verifyProductReport_WithReporting(productElement,product,expReportText);
		}else{
			boolean reportTextMatched = false;
			for(int i=1;i<=count;i++){
				tempretval = Globals.KEYWORD_FAIL;
				expReportText="";
				expReportText = bgReport_fetchVolunteerAliasText(i);
				expReportText = expReportText + ssnText + statusText;
				tempretval = bgReport_verifyProduct_BGReport(productElement, product, expReportText);

				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					reportTextMatched = true;
					break;
				}
			}

			if(reportTextMatched){
				sc.STAF_ReportEvent("Pass", "BG Report-"+product, "Report Text is as Expected", 1); 
			}else{
				sc.STAF_ReportEvent("Fail","BG Report-"+product, "Report Text is NOT as Expected", 1);
			}
		}
	}

	/**************************************************************************************************
	 * Method to verify SSN Profile product details in BG Report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void bgReport_SSNProfile(WebElement productElement){
		String product = "SSNProfile";
		String expReportText = "";
		String gender = Globals.Volunteer_Gender.toLowerCase();
		expReportText = bgReport_fetchVolunteerNameText();
		expReportText = expReportText +"\n" + bgReport_fetchVolunteerSSNText() +"\n"+bgReport_fetchVolunteerDOBText()+"\nGender: "+gender;
		//expReportText = expReportText +"\nVerified Data:";
		expReportText = expReportText +"\nVerified Data:\nComments: No Consumer file was found matching the input information";
		bgReport_verifyProductReport_WithReporting(productElement,product,expReportText);	

	}

	/**************************************************************************************************
	 * Method to verify Abuse product details in BG Report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void bgReport_Abuse(WebElement productElement,boolean isAliasName,String abuseState,boolean isStatusClear){
		String product="Abuse";
		String expReportText = "";
		String expAbuseStateText="";
		String statusText ="";
		String ssnText = "\n" + bgReport_fetchVolunteerSSNText() +"\n"+bgReport_fetchVolunteerDOBText();


//		if (isStatusClear){
//			statusText = "Verified Data:\nNo data found";	
//		}else{
//			statusText = "Verified Data:\nNo Neglect/Abuse Registry data was found for this individual";
//		}
        //Defect#10008 - psave for abuse product Verified data contain change for Clear and consider status   
		statusText = "Verified Data:\nNo Neglect/Abuse Registry data was found for this individual";
		
		if(abuseState.equalsIgnoreCase("PA")){
			expAbuseStateText = "PA-Dept. Health-Child";
		}else if(abuseState.equalsIgnoreCase("CO")){
			expAbuseStateText = "CO-Dept. Health-Child";
		}

		expAbuseStateText = "\nState: "+expAbuseStateText;

		int count =0;
		String tempretval="";
		if(isAliasName){
			count = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
		}

		if(count==0){
			expReportText = bgReport_fetchVolunteerNameText() + ssnText + expAbuseStateText + "\n"+ statusText;

			bgReport_verifyProductReport_WithReporting(productElement,product,expReportText);
		}else{
			boolean reportTextMatched = false;
			for(int i=1;i<=count;i++){
				tempretval = Globals.KEYWORD_FAIL;
				expReportText="";
				expReportText = bgReport_fetchVolunteerAliasText(i);
				expReportText = expReportText + ssnText + expAbuseStateText + "\n"+ statusText;
				tempretval = bgReport_verifyProduct_BGReport(productElement, product, expReportText);

				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					reportTextMatched = true;
					break;
				}
			}

			if(reportTextMatched){
				sc.STAF_ReportEvent("Pass", "BG Report-"+product, "Report Text is as Expected", 1); 
			}else{
				sc.STAF_ReportEvent("Fail","BG Report-"+product, "Report Text is NOT as Expected", 1);
			}
		}
	}

	/**************************************************************************************************
	 * Method to verify Locator Select product details in BG Report
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void bgReport_LocatorSelect(WebElement productElement,boolean isAliasName){
		String product="LocatorSelect";
		String expReportText = "";
		String staticText ="If the Search revealed any potential reportable information that was not up to date, primary source searches were initiated to ensure only up to date information is reported. Any information from such primary source searches is located elsewhere in this report.";
		expReportText = bgReport_fetchVolunteerNameText();
		expReportText = expReportText +"\n" + bgReport_fetchVolunteerSSNText() +"\n"+bgReport_fetchVolunteerDOBText();
		expReportText = expReportText +"\nVerified Data:\n"+staticText;



		bgReport_verifyProductReport_WithReporting(productElement,product,expReportText);	

	}

	/**************************************************************************************************
	 * Method to fetch expected Order creation date for BG reports validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String fetchExpectedOrderedDate_reportFormat(){
		String expOrderDate="";
		expOrderDate = Globals.testSuiteXLS.getCellData_fromTestData("OrderedDate");
		String monthName = sc.convertIntToFullMonthName(expOrderDate.split("/")[0]);
		String expDate = expOrderDate.split("/")[1];
		if(expDate.length() ==1){
			expDate = "0"+expDate;	
		}
		expOrderDate = monthName +" "+expDate+", "+expOrderDate.split("/")[2];
		return expOrderDate;
	}

	/**************************************************************************************************
	 * Method to fetch volunteer name based on bg report formats
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_fetchVolunteerNameText(){

		String volLnameName = "";
		String volFnameName ="";
		String volMidName="";
		String expVolText="";

		volFnameName = sc.capitalizeFirstLetterInWord(Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName")) ;
		volLnameName = sc.capitalizeFirstLetterInWord(Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName")) ;
		volMidName = sc.capitalizeFirstLetterInWord(Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName")) ;


		if(volMidName.isEmpty() || volMidName == null){
			volMidName ="";
		}



		expVolText="Provided Data:\nLast Name: "+volLnameName+"\nFirst Name: "+volFnameName+"\nMiddle Name: "+volMidName;
		return expVolText;

	}

	/**************************************************************************************************
	 * Method to fetch SSN text for volunteer based on BG report format
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_fetchVolunteerSSNText(){
		String expectedSSNText="";

		String volSSN =Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN");
		if(!volSSN.isEmpty() && volSSN != null){
			expectedSSNText = "SSN: xxx-xx-" + volSSN.substring(5, 9); // last 3 digits
		}else{
			expectedSSNText = "SSN: Not Provided";
		}
		return expectedSSNText;
	}

	/**************************************************************************************************
	 * Method to fetch DOB text for volunteer based on BG report format
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_fetchVolunteerDOBText(){
		String expVolDOBText="";

		String volDOB = Globals.testSuiteXLS.getCellData_fromTestData("VolunteerDOB"); // format is dd-MMM-yyyy
		/* expected DOB format in Report is 7/18/xxxx */
		String monMonthName = volDOB.split("-")[1]; // 3 chara equivalent of Month like Jan
		String monthNameFull = sc.convertToFullMonthName(monMonthName);
		String monthNumber  = sc.convertFullMonthNameToNumber(monthNameFull) ;

		String dobDate = volDOB.split("-")[0];
		Pattern pattern = Pattern.compile("[0].");//. represents single character  
		Matcher m = pattern.matcher(dobDate);  
		boolean b =  false;
		b = m.matches();

		if(b){
			dobDate =	dobDate.replace("0","");
		}

		b = false;
		m = pattern.matcher(monthNumber);
		if(b){
			monthNumber =	monthNumber.replace("0","");
		}
		expVolDOBText = "DOB: "+monthNumber+"/"+dobDate+"/xxxx";

		return expVolDOBText;

	}

	/**************************************************************************************************
	 * Method to verify product details on bg report with custom reporting
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_verifyProductReport_WithReporting(WebElement productElement,String product,String expReportText ){
		String retval = Globals.KEYWORD_FAIL;

		retval =  bgReport_verifyProduct_BGReport(productElement, product, expReportText);

		if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			sc.STAF_ReportEvent("Pass", "BG Report-"+product, "Report Text is as Expected", 1); 
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail","BG Report-"+product, "Report Text is NOT as Expected", 1);
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to fetch MVR text for volunteer based on BG report format
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_fetchMVRVerifiedData(){

		String retvalText ="";

		String dl = Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_License");
		String dlState = Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_State").toUpperCase();
		String dlName = sc.capitalizeFirstLetterInWord(Globals.Volunteer_DL_Name);
		String dlStateCode = State.valueOf(dlState).abbreviation;
		//retvalText = "Gender: "+Globals.Volunteer_Gender.toLowerCase()+"\n";
		retvalText = retvalText + "License Number: "+dl+"\nState: "+dlStateCode+"\nLast Name on License: "+dlName+"\nFirst Name on License: "+dlName+"\nMiddle Name on License: "+dlName;
		return retvalText;

	}

	/**************************************************************************************************
	 * Method to fetch Volunteer alias text for volunteer based on BG report format
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_fetchVolunteerAliasText(int index){
		String volLnameName = "";
		String volFnameName ="";
		String volMidName="";
		String expVolText="";

		volMidName = "Not Provided";//Globals.Volunteer_AliasName;
		if(index ==1){
			volFnameName = Globals.Volunteer_AliasName;
			volLnameName = Globals.Volunteer_AliasName;

		}else{
			volFnameName = sc.capitalizeFirstLetterInWord(Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName")) ;
			volLnameName = sc.capitalizeFirstLetterInWord(Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName")) ;

		}


		expVolText="Provided Data:\nLast Name: "+volLnameName+"\nFirst Name: "+volFnameName+"\nMiddle Name: "+volMidName;
		return expVolText;
	}


	/**************************************************************************************************
	 * Method to verify product details on bgreport, compares the actual text vs expected text
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String bgReport_verifyProduct_BGReport(WebElement productElement,String product,String expReportText ){
		String actualReport ="";
		String retval = Globals.KEYWORD_FAIL;
		WebElement reportElement = productElement.findElement(By.xpath("./div[@class='reportData']"));
		actualReport = reportElement.getText();
		sc.scrollIntoView(reportElement);
		if(expReportText.equalsIgnoreCase(actualReport)){
			retval = Globals.KEYWORD_PASS;
		}else{
			retval = Globals.KEYWORD_FAIL; 
		}

		return retval;
	}
	
	/**************************************************************************************************
	 * Method to verify How Do I Read Report? link on bgreport, compares the actual text vs expected text
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @return 
	 * @throws Exception
	 ***************************************************************************************************/
	public static void bgReport_verifyLinkReadReport_BGReport(String readReportlink) throws Exception{
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=10;
		tempRetval = sc.waitforElementToDisplay(readReportlink,(long) timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail","Read Report URL", "Read Report URL is not visible", 1);
		}else{
			
			
			if(sc.BrowserType.equalsIgnoreCase("mozilla")||sc.BrowserType.equalsIgnoreCase("firefox")){
				WebElement objToBeClicked = sc.createWebElement(readReportlink);
				String fileName = "BackgroundCheckReportGuide_Organization.pdf";
				String stepName = "Verify How Do I read Report link url";
				String absPath2BeSaved = Globals.currentPlatform_Path+"\\Snapshots\\"+Globals.TestCaseID+"_"+fileName;
				sc.clickAndDownloadFile(objToBeClicked, absPath2BeSaved, fileName, stepName);
	       }else{
	    	   sc.clickWhenElementIsClickable(readReportlink, timeOutInSeconds);	
			
			String winHandleBefore = driver.getWindowHandle();
			sc.clickWhenElementIsClickable("volDashboard_clickHere_link", (int)timeOutInSeconds);
			for(String winHandle : driver.getWindowHandles()){
				driver.switchTo().window(winHandle);
			}
			String url = driver.getCurrentUrl();
			String []env=Globals.Env_To_Execute_ON.split("_"); 
			String linkEnv=env[1];
		
			String amzezonUrl=null;
			if(linkEnv.startsWith("QA")){
				amzezonUrl="dev";
			}else if(linkEnv.equalsIgnoreCase("PROD")){
				amzezonUrl="prod";
			}
			String instructionurl ="https://s3.amazonaws.com/sterling-"+amzezonUrl+"-vv/pdf/BackgroundCheckReportGuide_Organization.pdf";
			String instructionUrlMoz="https://s3.amazonaws.com/sterling-"+amzezonUrl+"-vv/pdf/BackgroundCheckReportGuide_Organization.pdf";
		    if(readReportlink.equalsIgnoreCase("bgReport_VolReportRead1_link")){
		    	instructionurl="https://s3.amazonaws.com/sterling-"+amzezonUrl+"-vv/pdf/BackgroundCheckReportGuide_Volunteer.pdf";
		    	instructionUrlMoz="https://s3.amazonaws.com/sterling-"+amzezonUrl+"-vv/pdf/BackgroundCheckReportGuide_Volunteer.pdf";
		    }
			
			if(instructionurl.contains(url) || (instructionUrlMoz.contains(url))){
				sc.STAF_ReportEvent("Pass", "Verify How Do I read Report link url",  "How Do I Read This Report? url is correct",1);
			}
			else{
				sc.STAF_ReportEvent("Fail", "Verify How Do I read Report link url",  "How Do I Read This Report? url is not correct",1);
			}
			driver.close();
			driver.switchTo().window(winHandleBefore);
	       }	
		}
	}


	/**************************************************************************************************
	 * Method to mark the volunteer as either eligible or ineligible
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String VolunteerEligibility() throws Exception {
		String expVolunteerEligibility ="";
		int timeOutInSeconds = 10;
		String tempRetval= Globals.KEYWORD_FAIL;
		String expOrderScore ="";

		try{
			tempRetval = sc.waitforElementToDisplay("bgReport_review_link",(long) timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Review button is not visible", 1);
				return Globals.KEYWORD_FAIL;
			}
			sc.scrollIntoView("bgReport_review_link");
			expOrderScore = Globals.testSuiteXLS.getCellData_fromTestData("ExpectedOrderScore");
			expVolunteerEligibility= Globals.testSuiteXLS.getCellData_fromTestData("VolunteerEligible");

			if(expVolunteerEligibility.isEmpty() || expVolunteerEligibility.equals("") || expVolunteerEligibility == null){
				sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Test Data error.VolunteerEligibile column is blank", 0);
				return Globals.KEYWORD_FAIL;
			}
			String objLocator = "";
			String uiText="";
			String RatingRestrictionscheckboxReq = Globals.testSuiteXLS.getCellData_fromTestData("RatingRestrictionscheckbox");
			if (RatingRestrictionscheckboxReq.equalsIgnoreCase("Yes")){
				if(expVolunteerEligibility.equalsIgnoreCase("Eligible with Restrictions")){
					objLocator="bgReport_EligibleWithRestrictions_btn";
					uiText="The selected candidate has been moved to Eligible.";
				}
			}
			else{
				if(expVolunteerEligibility.equalsIgnoreCase("Eligible")){
					objLocator="bgReport_eligible_btn";
					uiText="The selected candidate has been moved to Eligible.";
				}else {
					objLocator = "bgReport_Ineligible_btn";
					uiText = "The selected candidate has been moved to Ineligible.";
				}
			}

			sc.clickWhenElementIsClickable("bgReport_review_link", timeOutInSeconds);
			sc.clickWhenElementIsClickable(objLocator, timeOutInSeconds);
			
			if (RatingRestrictionscheckboxReq.equalsIgnoreCase("Yes")){
				if(expOrderScore.equalsIgnoreCase("Clear") && expVolunteerEligibility.equalsIgnoreCase("Eligible with Restrictions")){
				tempRetval = sc.waitforElementToDisplay("RatingRestriction_Required_lbl",(long) timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "RatingRestrictions pop up is not displayed", 1);
					return Globals.KEYWORD_FAIL;
				}else{
					restrictionPopup();
					
				}
				
			}
				if(expOrderScore.equalsIgnoreCase("Consider") && expVolunteerEligibility.equalsIgnoreCase("Eligible with Restrictions")){
					tempRetval = sc.waitforElementToDisplay("RatingRestriction_Required_lbl",(long) timeOutInSeconds);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "RatingRestrictions pop up is not displayed", 1);
						return Globals.KEYWORD_FAIL;
					}else{
						restrictionPopup();
					    String advActionEnabled = Globals.testSuiteXLS.getCellData_fromTestData("AdverseActionEnabled");
						if(advActionEnabled.equalsIgnoreCase("Yes")){
							tempRetval = sc.waitforElementToDisplay("adverseAction_Required_lbl",(long) timeOutInSeconds);
							if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
								sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Adverse Action Required pop up is not displayed", 1);
								return Globals.KEYWORD_FAIL;
							}else{
								sc.clickWhenElementIsClickable("adverseAction_sendLetter_btn", timeOutInSeconds);
								sc.setValueJsChange("adverseAction_adverseReason_txt", "Automation testing purpose");
								sc.clickWhenElementIsClickable("adverseAction_startAction_btn", timeOutInSeconds);
							}
						}
						
					}
					
				}
			}
			
			if(expOrderScore.equalsIgnoreCase("Clear") && expVolunteerEligibility.equalsIgnoreCase("Ineligible")){
				//wait for adverse action pop up
				tempRetval = sc.waitforElementToDisplay("adverseAction_NotRequired_lbl",(long) timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Adverse Action Not Required pop up is not displayed", 1);
					return Globals.KEYWORD_FAIL;
				}else{
					sc.clickWhenElementIsClickable("adverseAction_okay_btn", timeOutInSeconds);
				}


			}else if(expOrderScore.equalsIgnoreCase("Consider") && expVolunteerEligibility.equalsIgnoreCase("Ineligible")){
				String advActionEnabled = Globals.testSuiteXLS.getCellData_fromTestData("AdverseActionEnabled");
				if(advActionEnabled.equalsIgnoreCase("Yes")){
					tempRetval = sc.waitforElementToDisplay("adverseAction_Required_lbl",(long) timeOutInSeconds);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Adverse Action Required pop up is not displayed", 1);
						return Globals.KEYWORD_FAIL;
					}else{
						sc.clickWhenElementIsClickable("adverseAction_sendLetter_btn", timeOutInSeconds);
						sc.setValueJsChange("adverseAction_adverseReason_txt", "Automation testing purpose");
						sc.clickWhenElementIsClickable("adverseAction_startAction_btn", timeOutInSeconds);
					}
				}else{
					//TODO-need to update this code when adverse action is automated
				}
			}

			//check for successfull message
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 10);

			while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(2000);
				tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

			}

			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("bgDashboard_warningMsg_span",(long) 20);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Successfuly message is not displayed on BG Dashboard", 1);
				return Globals.KEYWORD_FAIL;
			}else{
				String actualUItext ="";
				actualUItext = driver.findElement(LocatorAccess.getLocator("bgDashboard_warningMsg_span")).getText();
				if(uiText.equalsIgnoreCase(actualUItext)){
					sc.STAF_ReportEvent("Pass","Volunteer Eligibility", "Volunteer marked as "+expVolunteerEligibility,1);
					
					//API Retrieval | Notify Response
					if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
					String ntMsgAPilog=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
			        String ntMsgnwapiendpt=Globals.testSuiteXLS.getCellData_fromTestData("APIRetrievalNotifyResponseNewEndpoint");
			        String npnOrderID=Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
			        String rrResult=Globals.testSuiteXLS.getCellData_fromTestData("VolunteerEligible");
			        if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") || ntMsgAPilog.equalsIgnoreCase("") || ntMsgnwapiendpt.equalsIgnoreCase("")||ntMsgAPilog.isEmpty() || ntMsgnwapiendpt.isEmpty()){
			        	//sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
			        	
			        }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
			        	Queryforapilog(npnOrderID,"Adjudicated",rrResult,"NoNo");            	
			        }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
			        	Queryforapilog(npnOrderID,"Adjudicated",rrResult,"YesNo");
			        }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
			        	Queryforapilog(npnOrderID,"Adjudicated",rrResult,"NoYes");
			        	getOrderRequestXML("Adjudicated");
			        }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
			        	Queryforapilog(npnOrderID,"Adjudicated",rrResult,"YesYes");
			        	getOrderRequestXML("Adjudicated");
			        }else{
			        	sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
			        }
				}  
			        
					return Globals.KEYWORD_PASS;
				}else{
					sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Unable to mark Volunteer as "+expVolunteerEligibility,1);
					return Globals.KEYWORD_FAIL;
				}

			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Unable to mark Volunteer as "+expVolunteerEligibility +" due to Exception-"+e.toString(),1);	
			e.printStackTrace();
		}

		return Globals.KEYWORD_FAIL;// only failed cases would reach this point
	}

	/**************************************************************************************************
	 * Method to mark the volunteer as either eligible or ineligible using reportURL
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String reportURLVolunteerEligibility() throws Exception {
		String expVolunteerEligibility ="";
		int timeOutInSeconds = 60;
		String tempRetval= Globals.KEYWORD_FAIL;
		String expOrderScore ="";

		try{
			String printBtnLabel = driver.findElement(By.xpath("//button[@title='Print Background Report']")).getText();
				
			if(!printBtnLabel.equalsIgnoreCase("Print")){
				sc.STAF_ReportEvent("Fail","API Order- Report URL BG Report", "Print button is not visible", 1);
			return Globals.KEYWORD_FAIL;
			}
			
			tempRetval = sc.waitforElementToDisplay("bgReport_review_link",(long) timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Review button is not visible", 1);
				return Globals.KEYWORD_FAIL;
			}
			sc.scrollIntoView("bgReport_review_link");
			
			expOrderScore = Globals.testSuiteXLS.getCellData_fromTestData("ExpectedOrderScore");
			expVolunteerEligibility= Globals.testSuiteXLS.getCellData_fromTestData("VolunteerEligible");

			if(expVolunteerEligibility.isEmpty() || expVolunteerEligibility.equals("") || expVolunteerEligibility == null){
				sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Test Data error.VolunteerEligibile column is blank", 0);
				return Globals.KEYWORD_FAIL;
			}
			String objLocator = "";
			String uiText="";
			String RatingRestrictionscheckboxReq = Globals.testSuiteXLS.getCellData_fromTestData("RatingRestrictionscheckbox");
			if (RatingRestrictionscheckboxReq.equalsIgnoreCase("Yes")){
				if(expVolunteerEligibility.equalsIgnoreCase("Eligible with Restrictions")){
					objLocator="bgReport_EligibleWithRestrictions_btn";
					uiText="The selected candidate has been moved to Eligible.";
				}
			}
			else{
				if(expVolunteerEligibility.equalsIgnoreCase("Eligible")){
					objLocator="bgReport_eligible_btn";
					uiText="The selected candidate has been moved to Eligible.";
				}else {
					objLocator = "bgReport_reporturl_ineligible_btn";
					uiText = "The selected candidate has been moved to Ineligible.";
				}
			}

			sc.clickWhenElementIsClickable("bgReport_review_link", timeOutInSeconds);
			sc.clickWhenElementIsClickable(objLocator, timeOutInSeconds);
			
			if (RatingRestrictionscheckboxReq.equalsIgnoreCase("Yes")){
				if(expOrderScore.equalsIgnoreCase("Clear") && expVolunteerEligibility.equalsIgnoreCase("Eligible with Restrictions")){
				tempRetval = sc.waitforElementToDisplay("RatingRestriction_Required_lbl",(long) timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "RatingRestrictions pop up is not displayed", 1);
					return Globals.KEYWORD_FAIL;
				}else{
					restrictionPopup();
					
				}
				
			}
				if(expOrderScore.equalsIgnoreCase("Consider") && expVolunteerEligibility.equalsIgnoreCase("Eligible with Restrictions")){
					tempRetval = sc.waitforElementToDisplay("RatingRestriction_Required_lbl",(long) timeOutInSeconds);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "RatingRestrictions pop up is not displayed", 1);
						return Globals.KEYWORD_FAIL;
					}else{
						restrictionPopup();
					    String advActionEnabled = Globals.testSuiteXLS.getCellData_fromTestData("AdverseActionEnabled");
						if(advActionEnabled.equalsIgnoreCase("Yes")){
							tempRetval = sc.waitforElementToDisplay("adverseAction_Required_lbl",(long) timeOutInSeconds);
							if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
								sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Adverse Action Required pop up is not displayed", 1);
								return Globals.KEYWORD_FAIL;
							}else{
								sc.clickWhenElementIsClickable("adverseAction_sendLetter_btn", timeOutInSeconds);
								sc.setValueJsChange("adverseAction_adverseReason_txt", "Automation testing purpose");
								sc.clickWhenElementIsClickable("adverseAction_startAction_btn", timeOutInSeconds);
							}
						}
						
					}
					
				}
			}
			
			
			//check for successfull message
			
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("bgReport_reporturl_success_msg",(long) 20);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Successfuly message is not displayed on BG Dashboard", 1);
				return Globals.KEYWORD_FAIL;
			}else{
					sc.STAF_ReportEvent("Pass","Volunteer Eligibility", "Volunteer marked as "+expVolunteerEligibility,1);
					
					//log off org user
					tempRetval = sc.waitforElementToDisplay("bgReport_reporturl_logout_btn", timeOutInSeconds);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

						//				driver.findElement(LocatorAccess.getLocator("clientBGDashboard_logout_link")).click();
						sc.clickWhenElementIsClickable("bgReport_reporturl_logout_btn",(int) timeOutInSeconds  );

						tempRetval = Globals.KEYWORD_FAIL;
						tempRetval = sc.waitforElementToDisplay("login_orgUsername_txt", timeOutInSeconds);
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

							sc.STAF_ReportEvent("Pass", "Report Url - Org UserLogOff", "Org user log off successful", 1);
							log.info("Method - LogOff | Successfully loggged off from VV Aplication");
							

						}else{
							sc.STAF_ReportEvent("Fail", "Report Url - Org UserLogOff", "Unable to log off from VV Aplication", 1);

						}	
					}
					//API Retrieval | Notify Response
					if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
					String ntMsgAPilog=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
			        String ntMsgnwapiendpt=Globals.testSuiteXLS.getCellData_fromTestData("APIRetrievalNotifyResponseNewEndpoint");
			        String npnOrderID=Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
			        String rrResult=Globals.testSuiteXLS.getCellData_fromTestData("VolunteerEligible");
			        if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") || ntMsgAPilog.equalsIgnoreCase("") || ntMsgnwapiendpt.equalsIgnoreCase("")||ntMsgAPilog.isEmpty() || ntMsgnwapiendpt.isEmpty()){
			        	//sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
			        	
			        }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
			        	Queryforapilog(npnOrderID,"Adjudicated",rrResult,"NoNo");            	
			        }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
			        	Queryforapilog(npnOrderID,"Adjudicated",rrResult,"YesNo");
			        }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
			        	Queryforapilog(npnOrderID,"Adjudicated",rrResult,"NoYes");
			        	getOrderRequestXML("Adjudicated");
			        }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
			        	Queryforapilog(npnOrderID,"Adjudicated",rrResult,"YesYes");
			        	getOrderRequestXML("Adjudicated");
			        }else{
			        	sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
			        }
				}  
			        
					return Globals.KEYWORD_PASS;
			}
		 		
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail","Volunteer Eligibility", "Unable to mark Volunteer as "+expVolunteerEligibility +" due to Exception-"+e.toString(),1);	
			e.printStackTrace();
		}

		return Globals.KEYWORD_FAIL;// only failed cases would reach this point
	}
	
	
	
	
	
	/**************************************************************************************************
	 * Method to select current day from date picker
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/

	private static String getCurrentDay() {
		 Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
	     //Get Current Day as a number
	     int todayInt = calendar.get(Calendar.DAY_OF_MONTH);
	     System.out.println("Today Int: " + todayInt +"\n");

	     //Integer to String Conversion
	     String todayStr = Integer.toString(todayInt);
	     return todayStr;
		
	}
	
	/**************************************************************************************************
	 * Method to select restriction values from restriction popup
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/

	public static String restrictionPopup() throws Exception {
		int timeOutInSeconds = 10;
		String tempRetval= Globals.KEYWORD_FAIL;
		String restriction =Globals.testSuiteXLS.getCellData_fromTestData("RestrictionValue");
		//Added as the restriction dropdown xpath value is different when viewing report from report url and bg dashboard
		String Note=Globals.testSuiteXLS.getCellData_fromTestData("Notes");
		if((Note.equalsIgnoreCase("Test"))){
			tempRetval = sc.selectValue_byVisibleText("RatingRestriction_Requiredreporturl_dd", restriction);
		}
		else{
			tempRetval = sc.selectValue_byVisibleText("RatingRestriction_Required_dd", restriction);
		}
	    if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail","Rating Restrictions", "Couldnot select rating restriction value", 1);
			return Globals.KEYWORD_FAIL;
		}
	    else{
	    	sc.STAF_ReportEvent("Pass","Rating Restrictions", "Rating restriction value selected", 1);
	    }
//	    String today=getCurrentDay(); 
//	    driver.findElement(By.xpath("//*[@id='ratingRestrictionDate']")).click();
//	    Thread.sleep(5000);
//	    WebElement dateWidgetForm= driver.findElement(By.xpath("//div[@class='datepicker datepicker-dropdown dropdown-menu datepicker-orient-left datepicker-orient-bottom']"));
//	    List<WebElement> columns = dateWidgetForm.findElements(By.tagName("td"));
//
//	        for (WebElement cell: columns) {
//	          String z=cell.getAttribute("class").toString();
//	          if(z.equalsIgnoreCase("day")){
//	          if (cell.getText().equals(today)) {
//	            cell.click();
//	            sc.STAF_ReportEvent("Pass","Rating Restrictions", "Selected End date for RatingRestriction", 1);
//	            break;
//	          }
//	        }
//	        }
	        String RestrictionNote = "RestrictionNote";
	        sc.setValueJsChange("RatingRestriction_Note_txt", RestrictionNote);  
		    sc.clickWhenElementIsClickable("RatingRestriction_save_btn", timeOutInSeconds);
		    return tempRetval;
		
	}
	/**************************************************************************************************
	 * Method to select show/hide column value from Volunteer Dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/

	public static void showColumn() {
		WebElement showColumn = driver.findElement(By.xpath("//div[@class='row']/menu/ul/li[5]"));
		showColumn.findElement(By.xpath("./button")).sendKeys(Keys.ENTER);
		//			Thread.sleep(1000);
		List<WebElement> columns =showColumn.findElements(By.xpath("//div/label[not(contains(@style,'none'))]/input[@type='checkbox']"));
		for(int i=1 ;i<columns.size();i++){		
			sc.checkCheckBox(columns.get(i));

		}
		sc.STAF_ReportEvent("Pass", "Show/Hide Columns", "All columns have been selected for being displayed",1);
		showColumn.findElement(By.xpath("./button")).sendKeys(Keys.ENTER);;
	
	}
	/**************************************************************************************************
	 * Method to navigate to volunteer profile
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String gotoVolunteerMyProfile() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 10;

		tempRetval = sc.waitforElementToDisplay("volunteerHomepage_myProfile_link",timeOutinSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "My Profile", "My Profile link is not displayed", 1);
			return retval;
		}

		sc.clickWhenElementIsClickable("volunteerHomepage_myProfile_link", (int)timeOutinSeconds);

		tempRetval = Globals.KEYWORD_FAIL;
		tempRetval = sc.waitforElementToDisplay("volunteerHomepage_editProfile_link",timeOutinSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "My Profile", "My Profile page has not been displayed", 1);

		}else{
			sc.STAF_ReportEvent("Pass", "My Profile", "My Profile page has been displayed", 1);
			retval = Globals.KEYWORD_PASS;
		}

		return retval;

	}

	/**************************************************************************************************
	 * Method to verify Get Verified link in volunteer homepage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyVerifiedLinkText(String objectNameInOR,String expectedText) throws Exception{
		String retval = Globals.KEYWORD_FAIL;
		String actualText="";
		String expectedText1[]=expectedText.split(" ");

		actualText = driver.findElement(LocatorAccess.getLocator(objectNameInOR)).getText().trim();
		if(actualText.contains(expectedText1[0]) && actualText.contains(expectedText1[1])){
			sc.STAF_ReportEvent("Pass", "Verified Link", "Text is a as expected-"+expectedText, 1);

			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "Verified Link", "Text is a Not as expected.Exp-"+expectedText+ " Actual-"+actualText, 1);

		}

		return retval;
	}
	
	/**************************************************************************************************
	 * Method to verify Get Verified link in volunteer homepage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyTrackOrderStatus(String objectNameInOR,String expectedText) throws Exception{
		String retval = Globals.KEYWORD_FAIL;
		String actualText="";

		actualText = driver.findElement(LocatorAccess.getLocator(objectNameInOR)).getText();
		if(expectedText.equals(actualText)){
			sc.STAF_ReportEvent("Pass", "Verified Link", "Text is a as expected-"+expectedText, 1);

			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "Verified Link", "Text is a Not as expected.Exp-"+expectedText+ " Actual-"+actualText, 1);

		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify BG report based on Volunteeer view
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyVolunteerReport() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		try{
			sc.clickWhenElementIsClickable("volunteerHomepage_verified_link", timeOutInSeconds);
			String questFlag=Globals.testSuiteXLS.getCellData_fromTestData("Questionnaire");
			if(questFlag.equalsIgnoreCase("yes")){
				
				tempRetval=sc.waitTillElementDisplayed(driver.findElement(By.xpath("//div[@id='VerifiedDateReports']//a[contains(@href,'BGCandidate')]")),timeOutInSeconds);				
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "BG Report", "Volunteer BG Report Link is not displayed", 1);
					return retval;
				}
				WebElement wbl=driver.findElement(By.xpath("//div[@id='VerifiedDateReports']//a[contains(@href,'BGCandidate')]"));
				sc.clickWhenElementIsClickable(wbl, timeOutInSeconds);
			}
			Globals.Component = "Verified Link Report";
			tempRetval = sc.waitforElementToDisplay("bgReport_volInfoSection_list", (long)timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "BG Report", "Volunteer BG Report page has not loaded", 1);
			}else{

				bgReport_verifyReportHeader_OrgUser();
				bgReport_verifyLinkReadReport_BGReport("bgReport_VolReportRead1_link");
				//verify custom fields on report
				/*	bgReport_verifyReportHeaderCustomFields_OrgUser("bgReport_customField1_text",1);
					bgReport_verifyReportHeaderCustomFields_OrgUser("bgReport_customField2_text",2);
					bgReport_verifyReportHeaderCustomFields_OrgUser("bgReport_customField3_text",3);
	*/
				bgReport_verifyVolunteerHeaderInfo(false);
				bgReport_verifyQuickViewText("volunteer");
				bgReport_verifyBackgroundCheckReportSummary();
				retval= bgReport_verifyPoductSumary();	
				sc.clickWhenElementIsClickable(driver.findElement(By.xpath("//*[@id='sectionContent']//a[1]/button")), timeOutInSeconds);
			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "BG Report", "Unable to verify BG Report due to exception.Exception-"+e.toString(), 1);
			throw new Exception("Unable to verify BG Report due to exception.Exception-"+e.toString());
		}


		return retval;


	}
	
	/**************************************************************************************************
	 * Method to verify Questionnaire Report based on Volunteeer view
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyQuestionnaireReport() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction("Questionnaire Report");
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		try{
			tempRetval = ClientFacingApp.gotoVolunteerMyProfile();
			sc.clickWhenElementIsClickable("volunteerHomepage_verified_link", timeOutInSeconds);
			String questFlag=Globals.testSuiteXLS.getCellData_fromTestData("Questionnaire");
			if(questFlag.equalsIgnoreCase("yes")){
				
				tempRetval=sc.waitTillElementDisplayed(driver.findElement(By.xpath("//div[@id='VerifiedDateReports']//a[contains(@href,'QuestionnaireReport')]")),timeOutInSeconds);				
				WebElement wbl=driver.findElement(By.xpath("//div[@id='VerifiedDateReports']//a[contains(@href,'QuestionnaireReport')]"));
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Volunteer Profile-BG Report", "Volunteer BG Report Link is not displayed", 1);
					return retval;
				}
				sc.clickWhenElementIsClickable(wbl, timeOutInSeconds);
				tempRetval = sc.waitTillElementDisplayed(driver.findElement(By.xpath("//*[@id='sectionContent']/div[2]")), timeOutInSeconds);

				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Volunteer profile -QuestionnaireReport", "Volunteer Questionnaire page report has not loaded", 1);
					return retval;
				}else{
				
		             String actualtext=driver.findElement(By.xpath("//*[@id='sectionContent']/div[2]")).getText().trim(); 
		             String expectedText=Globals.testSuiteXLS.getCellData_fromTestData("QuestionnaireReportPageSection").trim();
		             String fname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName").trim();
		 			 String lname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName").trim();
		 			 String mname= Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName").trim();
		 			 String npnorder=Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID").trim();
		 			String swestorder=Globals.testSuiteXLS.getCellData_fromTestData("SWestOrderID").trim();
		 			
		 			String date =  driver.findElement(By.xpath("//div[2]/div[3]/p[3]")).getText().trim();
		 						
		 			expectedText=expectedText.replace("fname", fname);
		 			expectedText=expectedText.replace("lname", lname);
		 			expectedText=expectedText.replace("mname", mname);
		 			expectedText=expectedText.replace("Date", date);
		 			expectedText=expectedText.replace("npnorder", npnorder);
		 			expectedText=expectedText.replace("swestorder", swestorder);
		 					
		 			if(expectedText.contains(actualtext)){
		 				sc.STAF_ReportEvent("Pass", "Volunteer profile -QuestionnaireReport", "Questionnaire Report Section text is displayed as Expected.", 1);
		 			}else{
		 				sc.STAF_ReportEvent("Fail", "Volunteer profile -QuestionnaireReport", "Questionnaire ReportSection text is NOT as per Expected.", 1);	
		 				return retval;
		 			}		
		 			            
				}
				sc.clickWhenElementIsClickable(driver.findElement(By.xpath("//*[@id='sectionContent']/div[1]/a/button")), timeOutInSeconds);
				retval=Globals.KEYWORD_PASS;
				
			}
			
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "BG Report", "Unable to verify BG Report due to exception.Exception-"+e.toString(), 1);
			throw new Exception("Unable to verify BG Report due to exception.Exception-"+e.toString());
		}


		return retval;


	}

	/**************************************************************************************************
	 * Method to verify BG report based on Volunteeer Badge view
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyVolunteerBadgeReport() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		try{
			gotoVolunteerMyProfile();

			WebElement badgesList = driver.findElement(By.xpath("//span[@class='badgeItems']"));
			List<WebElement> badges = badgesList.findElements(By.xpath("./a"));

			if(badges.size() == 0){
				sc.scrollIntoView(badgesList);
				sc.STAF_ReportEvent("Fail", "BG Report", "NO badges are visible", 1);
				return retval;
			}

			badges.get(0).click();
			Globals.Component = " Volunteer My Profile Badge Report";
			tempRetval = sc.waitforElementToDisplay("bgReport_volInfoSection_list", (long)timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "BG Report", "Volunteer BG Report page has not loaded", 1);
			}else{
				bgReport_verifyLinkReadReport_BGReport("bgReport_VolReportRead1_link");
				bgReport_verifyReportHeader_OrgUser();
				bgReport_verifyClientInfo(true);
				bgReport_verifyVolunteerHeaderInfo(true);
				bgReport_verifyQuickViewText("badge");
				bgReport_verifyBackgroundCheckReportSummary();
				retval= bgReport_verifyPoductSumary();	

			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "BG Report", "Unable to verify BG Report due to exception.Exception-"+e.toString(), 1);
			throw new Exception("Unable to verify BG Reportthrough Badge due to exception.Exception-"+e.toString());
		}


		return retval;

	}

	/**************************************************************************************************
	 * Method to verify BG report based on Volunteeer Profile view
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyVolunteerProfileReport() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		String searchVal =  Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
		String colName2beVerified = "Status";
		String expEligibility = Globals.testSuiteXLS.getCellData_fromTestData("VolunteerEligible");
		String reportingStepName ="Volunteer Search-Manage My Volunteers";
		tempRetval = searchVolunteer("VV Order",searchVal,colName2beVerified,expEligibility,reportingStepName);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			WebElement volGrid =  sc.createWebElement("volDashboard_volgrid_tbl");
			volGrid.findElement(By.xpath("./tbody/tr/td[2]/a")).click();

			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("volunteerProfile_activityHistory_heading", (long)timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Volunteer Profile", "Page has not loaded", 1);
				return retval;
			}

			String eligibilityText = driver.findElement(LocatorAccess.getLocator("volunteerProfile_eligibility_div")).getText();


			if(expEligibility.equalsIgnoreCase(eligibilityText)){
				sc.STAF_ReportEvent("Pass", "Volunteer Profile Status", "Status is as expected.Status-"+expEligibility, 1);
			}else{
				sc.STAF_ReportEvent("Fail", "Volunteer Profile Status", "Status is NOT as expected.Exp-"+expEligibility + " Actual-"+eligibilityText, 1);
			}

			sc.clickWhenElementIsClickable("volunteerProfile_viewReport_link", timeOutInSeconds);
			Globals.Component = "VolunteerProfile-ViewReport";

			switchTabAndVerifyReport();

			tempRetval = sc.waitforElementToDisplay("volunteerProfile_activityHistory_heading", (long)timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Volunteer Profile", "Couldnt navigate back from Reports", 1);
				return retval;
			}

			WebElement badgeSection = driver.findElement(LocatorAccess.getLocator("volunteerProfile_badges_list"));
			List<WebElement> badges = badgeSection.findElements(By.xpath("./li/button"));

			if(badges.size() == 0 ){
				sc.STAF_ReportEvent("Fail", "Volunteer Profile", "Couldnt navigate to Reports as NO badge exist in Volunteer Profile", 1);
				return retval;
			}

			badges.get(0).click();
			Thread.sleep(2000);
			Globals.Component = "VolunteerProfile-BadgesReport";
			switchTabAndVerifyReport();

			sc.clickWhenElementIsClickable("volunteerProfile_close_btn", timeOutInSeconds);
			retval = Globals.KEYWORD_PASS;
		}

		return retval;

	}

	/**************************************************************************************************
	 * Method to switch to differnt tab , verify report and navigate to default tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void switchTabAndVerifyReport() throws Exception {
		String tempRetval= Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 10;
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(1));

		tempRetval = sc.waitforElementToDisplay("bgReport_volInfoSection_list", timeOutInSeconds);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "BG Report", "Volunteer BG Report page has not loaded", 1);
		}else{

			bgReport_verifyReportHeader_OrgUser();
			bgReport_verifyLinkReadReport_BGReport("bgReport_VolReportRead_link");
			bgReport_verifyClientInfo(true);
			bgReport_verifyVolunteerHeaderInfo(true);
			bgReport_verifyQuickViewText("badge");
			bgReport_verifyBackgroundCheckReportSummary();
			tempRetval= bgReport_verifyPoductSumary();	

		}
		driver.close();
		driver.switchTo().window(tabs.get(0));


	}
	
	/**************************************************************************************************
	 * Method to switch to differnt tab , verify report and navigate to default tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void switchTabAndVerifyQuestionnaireTab() throws Exception {
		String tempRetval= Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 10;
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(1));
        
		tempRetval = sc.waitTillElementDisplayed(driver.findElement(By.xpath("//div[@class='questionnaire col-sm-6 col-sm-push-3']")), timeOutInSeconds);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "QuestionnaireReport", "Volunteer Questionnaire page report has not loaded", 1);
		}else{
		
             String actualtext=driver.findElement(By.xpath("//div[@class='questionnaire col-sm-6 col-sm-push-3']")).getText().trim(); 
             String expectedText=Globals.testSuiteXLS.getCellData_fromTestData("QuestionnaireReportPageSection").trim();
             String fname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName").trim();
 			 String lname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName").trim();
 			 String mname= Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName").trim();
 			 String npnorder=Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID").trim();
 			String swestorder=Globals.testSuiteXLS.getCellData_fromTestData("SWestOrderID").trim();
 			
 			String date =  driver.findElement(By.xpath("//div[2]/div[3]/p[3]")).getText().trim();
 						
 			expectedText=expectedText.replace("fname", fname);
 			expectedText=expectedText.replace("lname", lname);
 			expectedText=expectedText.replace("mname", mname);
 			expectedText=expectedText.replace("Date", date);
 			expectedText=expectedText.replace("npnorder", npnorder);
 			expectedText=expectedText.replace("swestorder", swestorder);
 					
 			if(expectedText.contains(actualtext)){
 				sc.STAF_ReportEvent("Pass", "QuestionnaireReport", "Questionnaire Report Section text is displayed as Expected.", 1);
 			}else{
 				sc.STAF_ReportEvent("Fail", "QuestionnaireReport", "Questionnaire ReportSection text is NOT as per Expected.", 1);				
 			}		
 			            
		}
		driver.close();
		driver.switchTo().window(tabs.get(0));


	}


	/**************************************************************************************************
	 * Method to verify mvr authorization functionality
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void mvrAuthorization() throws Exception{


		String accName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
		String stateCode="";
		String expectedErrorMessage ="";


		stateCode = "CA";
		expectedErrorMessage = "This order may not be placed. Please notify "+accName+" that they are restricted from ordering the product Motor Vehicle Record Check - CA - California.";
		validateMVRAuthorizationError(stateCode,expectedErrorMessage);

		stateCode = "CO";
		expectedErrorMessage = "This order may not be placed. Please notify "+accName+" that they are restricted from ordering the product Motor Vehicle Record Check - CO - Colorado.";
		validateMVRAuthorizationError(stateCode,expectedErrorMessage);

		stateCode = "NH";
		expectedErrorMessage ="The order can not be completed for Drivers License state of New Hampshire. Please contact "+accName+" to have a new Background Check request sent to you that does not require an MVR.";
		validateMVRAuthorizationError(stateCode,expectedErrorMessage);

		stateCode = "NM";
		expectedErrorMessage = "This order may not be placed. Please notify "+accName+" that they are restricted from ordering the product Motor Vehicle Record Check - NM - New Mexico.";
		validateMVRAuthorizationError(stateCode,expectedErrorMessage);

		stateCode = "UT";
		expectedErrorMessage = "This order may not be placed. Please notify "+accName+" that they are restricted from ordering the product Motor Vehicle Record Check - UT - Utah.";
		validateMVRAuthorizationError(stateCode,expectedErrorMessage);

		stateCode = "WA";
		expectedErrorMessage = "The order can not be completed for Drivers License state of Washington. Please contact VVReg1 to have a new Background Check request sent to you that does not require an MVR.";
		validateMVRAuthorizationError(stateCode,expectedErrorMessage);


	}

	/**************************************************************************************************
	 * Method to verify mvr authorization error messages
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void validateMVRAuthorizationError(String stateCode,String expectedErrorMessage){
		String tempRetval = Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 20;
		WebElement element = sc.createWebElement("invitationStep2_mvrState_dd");
		String errorText ="";

		tempRetval = sc.waitforElementToDisplay("invitationStep2_mvrState_dd",10);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "MVR Authorization-"+stateCode, "State dropdown is not visible", 1);
		}else{
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.selectValue_byValue(element,stateCode);

			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("invitationStep2_mvrAuthorizationModify_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "MVR Authorization-"+stateCode, "Error message not displayed when un-authorized state is selected for ordering", 1);
			}else{
				errorText = driver.findElement(By.xpath("//*[@id='cancel-order-dialog']/div/div/div[2]/span")).getText();
				if(errorText.contains(expectedErrorMessage)){
					sc.STAF_ReportEvent("Pass", "MVR Authorization-"+stateCode, "Error message is as expected", 1);
				}else{
					sc.STAF_ReportEvent("Fail", "MVR Authorization-"+stateCode, "Error message is Not as expected", 1);
				}

				sc.clickWhenElementIsClickable("invitationStep2_mvrAuthorizationModify_btn", (int)timeOutInSeconds);
			}
		}
	}

	/**************************************************************************************************
	 * Method to verify mvr authorization in Client Ordering wrkflow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String mvrAuthorizeClientOrder() throws Exception{
		String tempRetval = Globals.KEYWORD_FAIL;
		String retval = Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 20;

		tempRetval = sc.waitforElementToDisplay("clientStep2_mvrAuthorizeError_header",timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "MVR Authorization", "Order exception page has not been loaded", 1);
		}else{
			String accName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName");

			String expErrorMsg = "This order may not be placed. Please notify "+accName+" that they are restricted from ordering the product Motor Vehicle Record Check - CO - Colorado";
			String actualMsg = driver.findElement(LocatorAccess.getLocator("clientStep2_mvrAuthorizationError_txt")).getText();

			if(actualMsg.contains(expErrorMsg)){
				sc.STAF_ReportEvent("Pass", "MVR Authorization", "Error message is as expected", 1);
				retval = Globals.KEYWORD_PASS;
			}else{
				sc.STAF_ReportEvent("Fail", "MVR Authorization", "Error message is Not as expected", 1);
			}
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to navigate to Client Reports tab
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String navigateToClientReports() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		String stepName = "Client Reports";
		sc.waitforElementToDisplay("clientBGDashboard_report_link", timeOutinSeconds);
		sc.clickWhenElementIsClickable("clientBGDashboard_report_link", (int) timeOutinSeconds);

		if(sc.waitforElementToDisplay("report_viewMetrics_link", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){
			retval=Globals.KEYWORD_PASS;
			sc.STAF_ReportEvent("Pass", stepName, "View Metrics link is available", 0);
		}
		else{
			sc.STAF_ReportEvent("Fail", stepName, "View Metrics link NOT is avilable", 1);
			log.error("Unable to  navigated to Reports page");
		}
		return retval;
	}

	/**************************************************************************************************
	 * Method to verify Client Report -InvitationStatus
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String invitationStatusReport() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		//table[@cols='20']
		long timeOutinSeconds = 60;

		String stepName = "InvitationStatus";
		try{
			sc.waitforElementToDisplay("report_invitationStatus_link", timeOutinSeconds);
			sc.clickWhenElementIsClickable("report_invitationStatus_link", (int) timeOutinSeconds);

			tempretval=sc.waitforElementToDisplay("invitationStatus_reportheader_txt", timeOutinSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Report Filters page has loaded", 0);
				clientReport_GoBackAndVerify("report_invitationStatus_link");
				clientReport_runReportAndVerify("report_invitationStatus_tbl",stepName);
				clientReport_GoBackToFilterPage("invitationStatus_reportheader_txt");
				clientReport_verifyAccount();
				clientReport_dateRange();
				clientReport_verifyDetailSummary();
				clientReport_verifyGroupBy();
				clientReport_runReportAndExport("report_invitationStatus_tbl",stepName);
				retval = Globals.KEYWORD_PASS;

			}
			else{
				sc.STAF_ReportEvent("Fail", stepName, "Report Filters pages is Not loaded", 1);
				log.error("Unable to  navigated to Reports page");
			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", stepName, "Unable to verify report due to exception.Exception-"+e.toString(), 1);
			throw e;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify Client Report -AccessFees
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String accessFeesReport() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		String stepName = "AccessFees";
		try{
			sc.waitforElementToDisplay("report_accesFees_link", timeOutinSeconds);
			sc.clickWhenElementIsClickable("report_accesFees_link", (int) timeOutinSeconds);

			tempretval=sc.waitforElementToDisplay("accessFees_reportheader_txt", timeOutinSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Report Filters page has loaded", 0);
				clientReport_GoBackAndVerify("report_accesFees_link");
				clientReport_runReportAndVerify("report_accessFees_tbl",stepName);
				clientReport_GoBackToFilterPage("accessFees_reportheader_txt");

				clientReport_verifyAccount();
				clientReport_dateRange();
				clientReport_verifyDetailSummary();
				clientReport_verifyGroupBy();
				clientReport_verifyScreeningLevel();
				clientReport_verifyProduct();
				clientReport_verifyStates();
				clientReport_runReportAndExport("report_accessFees_tbl",stepName);

				retval = Globals.KEYWORD_PASS;
			}
			else{
				sc.STAF_ReportEvent("Fail", stepName, "Report Filters pages is Not loaded", 1);
				log.error("Unable to  navigated to Reports page");
			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", stepName, "Unable to verify report due to exception.Exception-"+e.toString(), 1);
			throw e;
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify Client Report -ClientUsers
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clientUsersReport() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		String stepName = "ClientUser";
		try{
			sc.waitforElementToDisplay("report_clientUsers_link", timeOutinSeconds);
			sc.clickWhenElementIsClickable("report_clientUsers_link", (int) timeOutinSeconds);

			tempretval=sc.waitforElementToDisplay("clientUsers_reportheader_txt", timeOutinSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Report Filters page has loaded", 0);
				clientReport_GoBackAndVerify("report_clientUsers_link");
				clientReport_runReportAndVerify("report_clientUsers_tbl",stepName);
				clientReport_GoBackToFilterPage("clientUsers_reportheader_txt");
				clientReport_verifyAccount();
				clientReport_runReportAndExport("report_clientUsers_tbl",stepName);

				//also needs to verify other tables

				retval = Globals.KEYWORD_PASS;
			}
			else{
				sc.STAF_ReportEvent("Fail", stepName, "Report Filters pages is Not loaded", 1);
				log.error("Unable to  navigated to Reports page");
			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", stepName, "Unable to verify report due to exception.Exception-"+e.toString(), 1);
			throw e;
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify Client Report -ClientDashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clientDashboardReport() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		String stepName = "ClientDashboard";
		try{
			sc.waitforElementToDisplay("report_clientDashboard_link", timeOutinSeconds);
			sc.clickWhenElementIsClickable("report_clientDashboard_link", (int) timeOutinSeconds);

			tempretval=sc.waitforElementToDisplay("clientDashboard_reportheader_txt", timeOutinSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Report Filters page has loaded", 0);
				clientReport_GoBackAndVerify("report_clientDashboard_link");
				clientReport_runReportAndVerify("report_clientDashVolume_tbl",stepName);
				clientReport_GoBackToFilterPage("clientDashboard_reportheader_txt");
				clientReport_verifyAccount();
				clientReport_dateRange();
				clientReport_runReportAndExport("report_clientDashVolume_tbl",stepName);

				//also needs to verify other tables

				retval = Globals.KEYWORD_PASS;
			}
			else{
				sc.STAF_ReportEvent("Fail", stepName, "Report Filters pages is Not loaded", 1);
				log.error("Unable to  navigated to Reports page");
			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", stepName, "Unable to verify report due to exception.Exception-"+e.toString(), 1);
			throw e;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify Client Report -OrderTurnaroundTime
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String orderTurnaroundReport() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		String stepName = "OrderTurnaroundTime";
		try{
			sc.waitforElementToDisplay("report_orderTurnaroundTime_link", timeOutinSeconds);
			sc.clickWhenElementIsClickable("report_orderTurnaroundTime_link", (int) timeOutinSeconds);

			tempretval=sc.waitforElementToDisplay("orderTurnaround_reportheader_txt", timeOutinSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Report Filters page has loaded", 0);
				clientReport_GoBackAndVerify("report_orderTurnaroundTime_link");
				clientReport_runReportAndVerify("report_orderTurnaround_tbl",stepName);
				clientReport_GoBackToFilterPage("orderTurnaround_reportheader_txt");
				clientReport_verifyAccount();
				clientReport_dateRange();
				clientReport_runReportAndExport("report_orderTurnaround_tbl",stepName);

				//also needs to verify other tables

				retval = Globals.KEYWORD_PASS;
			}
			else{
				sc.STAF_ReportEvent("Fail", stepName, "Report Filters pages is Not loaded", 1);
				log.error("Unable to  navigated to Reports page");
			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", stepName, "Unable to verify report due to exception.Exception-"+e.toString(), 1);
			throw e;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify Client Report - OrderTransactionsReport
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String orderTransactionReport() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		String stepName = "OrderTransactionsReport";
		try{
			sc.waitforElementToDisplay("report_OrderTransactionReport_link", timeOutinSeconds);
			sc.clickWhenElementIsClickable("report_OrderTransactionReport_link", (int) timeOutinSeconds);

			tempretval=sc.waitforElementToDisplay("orderTransaction_reportHeader_txt", timeOutinSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Report Filters page has loaded", 0);
				clientReport_GoBackAndVerify("report_OrderTransactionReport_link");
				clientReport_runReportAndVerify("report_orderTransaction_tbl",stepName);
				clientReport_GoBackToFilterPage("orderTransaction_reportHeader_txt");
				clientReport_verifyAccount();
				clientReport_dateRange();
				clientReport_verifyOrderStatus();
				clientReport_verifyScreeningLevel();
				clientReport_verifyProduct();
				clientReport_verifyGroupBy();
				clientReport_runReportAndExport("report_orderTransaction_tbl",stepName);


				retval = Globals.KEYWORD_PASS;

			}
			else{
				sc.STAF_ReportEvent("Fail", stepName, "Report Filters pages is Not loaded", 1);
				log.error("Unable to  navigated to Reports page");
			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", stepName, "Unable to verify report due to exception.Exception-"+e.toString(), 1);
			throw e;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to verify Client Report - VolunteerContribution
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String volunteerContributionReport() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		String stepName = "VolunteerContribution";
		try{
			sc.waitforElementToDisplay("report_volunteerContribution_link", timeOutinSeconds);
			sc.clickWhenElementIsClickable("report_volunteerContribution_link", (int) timeOutinSeconds);

			tempretval=sc.waitforElementToDisplay("volunteerContribution_reportHeader_txt", timeOutinSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Report Filters page has loaded", 0);
				clientReport_GoBackAndVerify("report_volunteerContribution_link");
				clientReport_runReportAndVerify("report_volunteerContribution_tbl",stepName);
				clientReport_GoBackToFilterPage("volunteerContribution_reportHeader_txt");
				clientReport_verifyAccount();
				clientReport_dateRange();
				clientReport_verifyDetailSummary();
				clientReport_verifyGroupBy();
				clientReport_runReportAndExport("report_volunteerContribution_tbl",stepName);


				retval = Globals.KEYWORD_PASS;

			}
			else{
				sc.STAF_ReportEvent("Fail", stepName, "Report Filters pages is Not loaded", 1);
				log.error("Unable to  navigated to Reports page");
			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", stepName, "Unable to verify report due to exception.Exception-"+e.toString(), 1);
			throw e;
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify Client Report - Manage My Volunteers - Exportable Data
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author lvkumar
	 * @throws Exception
	 ***************************************************************************************************/
	public static String ManageMyVolunteersExportableDataReport() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		String stepName = "ManageMyVolunteersExportableData";
		try{
			sc.waitforElementToDisplay("report_ManageMyVolunteersExportableData_link", timeOutinSeconds);
			sc.clickWhenElementIsClickable("report_ManageMyVolunteersExportableData_link", (int) timeOutinSeconds);

			tempretval=sc.waitforElementToDisplay("ManageMyVolunteersExportableData_reportHeader_txt", timeOutinSeconds);
			if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", stepName, "Report Filters page has loaded", 0);
				clientReport_GoBackAndVerify("report_ManageMyVolunteersExportableData_link");
				clientReport_runReportAndVerify("report_ManageMyVolunteersExportableData_tbl",stepName);
				clientReport_GoBackToFilterPage("ManageMyVolunteersExportableData_reportHeader_txt");
				clientReport_verifyAccount();
				clientReport_dateRange();
				clientReport_verifydateType();
				clientReport_verifyStatus();
				clientReport_runReportAndExport("report_ManageMyVolunteersExportableData_tbl",stepName);


				retval = Globals.KEYWORD_PASS;

			}
			else{
				sc.STAF_ReportEvent("Fail", stepName, "Report Filters pages is Not loaded", 1);
				log.error("Unable to  navigated to Reports page");
			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", stepName, "Unable to verify report due to exception.Exception-"+e.toString(), 1);
			throw e;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to select State dropdwon value in Client Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_verifyStates() throws Exception{

		String value2beSelected =  Globals.testSuiteXLS.getCellData_fromTestData("CR_States");

		sc.selectValue_byVisibleText("report_screeningLevel_dd", value2beSelected);


	}

	/**************************************************************************************************
	 * Method to select Order Status dropdwon value in Client Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_verifyOrderStatus() throws Exception{
		String[] expectedItems = {"All","Processing","Complete"};
		sc.verifyDropdownItems("report_orderStatus_dd", expectedItems, "Order Status LOV");
		String value2beSelected =  Globals.testSuiteXLS.getCellData_fromTestData("CR_OrderStatus");

		sc.selectValue_byVisibleText("report_orderStatus_dd", value2beSelected);


	}

	/**************************************************************************************************
	 * Method to select Screening Level dropdwon value in Client Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_verifyScreeningLevel() throws Exception{

		String expScrenniningLevels= Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpScreeningLevel");
		String[] expectedItems = expScrenniningLevels.split(",");
		sc.verifyDropdownItems("report_screeningLevel_dd", expectedItems, "Screening Level LOV");
		String value2beSelected =  Globals.testSuiteXLS.getCellData_fromTestData("CR_ScreeningLevel");

		sc.selectValue_byVisibleText("report_screeningLevel_dd", value2beSelected);


	}

	/**************************************************************************************************
	 * Method to verify Date Type LOV in Client Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author lvkumar
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_verifydateType() throws Exception{

		String expDateTypes= Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpDateType");
		String[] expectedItems = expDateTypes.split(",");

		sc.verifyDropdownItems("report_dateType_dd", expectedItems, "Date Type LOV");
		String value2beSelected =  Globals.testSuiteXLS.getCellData_fromTestData("CR_DateType");

		sc.selectValue_byVisibleText("report_dateType_dd", value2beSelected);
	}

	/**************************************************************************************************
	 * Method to verify Status LOV in Client Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author lvkumar
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_verifyStatus() throws Exception{

		String expStatus= Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpStatus");
		String[] expectedItems = expStatus.split(",");

		sc.verifyDropdownItems("report_Status_dd", expectedItems, "Status LOV");
		String value2beSelected =  Globals.testSuiteXLS.getCellData_fromTestData("CR_Status");

		sc.selectValue_byVisibleText("report_Status_dd", value2beSelected);
	}
	
	/**************************************************************************************************
	 * Method to go back to report filtering page 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_GoBackToFilterPage(String reportHeaderElementName) throws Exception{
		int timeOutinSeconds = 30;
		String tempretval= Globals.KEYWORD_FAIL;
		driver.switchTo().defaultContent();

		sc.clickWhenElementIsClickable("report_back_link", timeOutinSeconds);

		tempretval = sc.waitforElementToDisplay(reportHeaderElementName, timeOutinSeconds);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

			sc.STAF_ReportEvent("Pass", "Reports Filter Page", "Back button clicked and navigated to Report Filter page", 1);

		}
		else{
			sc.STAF_ReportEvent("Fail", "Reports Filter Page", "Back button clicked but not navigated to Report Filter page", 1);
			log.error("Unable to  navigated to Reports page");
		}
	}

	/**************************************************************************************************
	 * Method to go back to Client Reports homepage from an individual report filtering page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_GoBackAndVerify(String reportLink) throws Exception{
		int timeOutinSeconds = 10;
		String tempretval= Globals.KEYWORD_FAIL;
		sc.clickWhenElementIsClickable("report_back_link", timeOutinSeconds);

		tempretval = sc.waitforElementToDisplay("report_viewMetrics_link", timeOutinSeconds);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){

			sc.STAF_ReportEvent("Pass", "Reports HomePage", "Back button clicked and navigated to Client Reports homepage", 1);
			sc.waitforElementToDisplay(reportLink, timeOutinSeconds);
			sc.clickWhenElementIsClickable(reportLink, (int) timeOutinSeconds);
		}
		else{
			sc.STAF_ReportEvent("Fail", "Reports HomePage", "Back button clicked but not navigated to Client Reports homepage", 1);
			log.error("Unable to  navigated to Reports page");
		}
	}

	/**************************************************************************************************
	 * Method to run an report amd export the same in different formats
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_runReportAndExport(String elementName,String stepName) throws Exception{
		clientReport_runReportAndVerify(elementName, stepName);
		clientReport_exportReport(stepName,"csv");
		clientReport_exportReport(stepName,"pdf");
		clientReport_exportReport(stepName,"excel");
		clientReport_exportReport(stepName,"tiff");
		clientReport_exportReport(stepName,"word");
		//		clientReport_exportReport(stepName,"xml");
		//		clientReport_exportReport(stepName,"mhtml");
	}

	/**************************************************************************************************
	 * Method to verify whether a report has been generated or not
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_runReportAndVerify(String elementName,String stepName){
		int timeOutinSeconds = 10;
		String tempretval= Globals.KEYWORD_FAIL;
		sc.clickWhenElementIsClickable("report_runReport_link", timeOutinSeconds);

		List<WebElement> iframeElements = driver.findElements(By.tagName("iframe"));

		if (iframeElements.size() <=0 ){

			sc.STAF_ReportEvent("Fail", stepName, "Report page frames has not loaded",1);

		}

		//System.out.println("The total number of iframes are " + iframeElement.size());
		WebElement iframeElement = driver.findElement(By.tagName("iframe"));
		driver.switchTo().frame(iframeElement); // need to switch control over to iframe

		tempretval = sc.waitforElementToDisplay(elementName, Globals.VV_Report_MaxTimeout);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Report page has not loaded",1);
		}else{
			sc.STAF_ReportEvent("Pass", stepName, "Report page has loaded",1);

		}

	}

	/**************************************************************************************************
	 * Method to verify Account LOV in Report filtering page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_verifyAccount() throws Exception{
		//String[] expectedItems = {"This Account"};
		String expectedItemsAcName = Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpClients");
		String[] expectedItems = expectedItemsAcName.split(",");
		sc.verifyDropdownItems("report_account_dd", expectedItems, "Account LOV");
		
		String value2beSelected =  Globals.testSuiteXLS.getCellData_fromTestData("CR_Account");

		sc.selectValue_byVisibleText("report_account_dd", value2beSelected);


	}

	/**************************************************************************************************
	 * Method to verify Product LOV in Client Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_verifyProduct() throws Exception{

		String expProducts= Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpProducts");
		String[] expectedItems = expProducts.split(",");

		sc.verifyDropdownItems("report_products_dd", expectedItems, "Products LOV");
		String value2beSelected =  Globals.testSuiteXLS.getCellData_fromTestData("CR_Products");

		sc.selectValue_byVisibleText("report_products_dd", value2beSelected);


	}

	/**************************************************************************************************
	 * Method to verify Date Range LOV in Client Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_dateRange() throws Exception{
		//		String[] expectedItems = {"Trailing Twelve Months","Year to Date","Quarter to Date","Month to Date","Q1","Q2","Q3","120 days","90 days","60 days","30 days","Last Week","This Week","Yesterday","Provide Date Range"};
		int timeOutinSeconds = 10;
		String value2beSelected =  Globals.testSuiteXLS.getCellData_fromTestData("CR_DateRange");
		WebElement dateRangePicker=null;

		sc.clickWhenElementIsClickable("report_dateSelector_dd", timeOutinSeconds);
		dateRangePicker = driver.findElement(By.xpath("//div[@class='ranges']/ul/li[text()='"+value2beSelected+"']"));
		dateRangePicker.click();

		if(value2beSelected.contains("Provide Date Range")){

			String fromDate = Globals.testSuiteXLS.getCellData_fromTestData("CR_StartDate");
			String toDate = Globals.testSuiteXLS.getCellData_fromTestData("CR_EndDate");
			sc.setValueJsChange("report_fromDate_txt", fromDate);

			sc.setValueJsChange("report_endDate_txt", toDate);
			sc.clickWhenElementIsClickable("report_applyDate_btn", timeOutinSeconds);
		}

	}

	/**************************************************************************************************
	 * Method to verify Detail/Summary LOV in Client Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_verifyDetailSummary() throws Exception{
		String[] expectedItems = {"Detail","Summary"};
		sc.verifyDropdownItems("report_detailSummary_dd", expectedItems, "Detail/Summary LOV");

		String value2beSelected =  Globals.testSuiteXLS.getCellData_fromTestData("CR_Detail");
		sc.selectValue_byVisibleText("report_detailSummary_dd", value2beSelected);
	}	

	/**************************************************************************************************
	 * Method to verify GroupBy LOV in Client Reports
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_verifyGroupBy() throws Exception{
		String lovItems = Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpGropyByLOV");
		String[] expectedItems = lovItems.split(",");
		sc.verifyDropdownItems("report_groupBy_dd", expectedItems, "GroupBy LOV");

		String value2beSelected =  Globals.testSuiteXLS.getCellData_fromTestData("CR_GroupBY");
		sc.selectValue_byVisibleText("report_groupBy_dd", value2beSelected);
	}

	
	/**************************************************************************************************
	 * Method to verify exported report format
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void clientReport_exportReport(String reportName,String reportType) throws Exception{
		int timeOutInSeconds=60;
		String reportObj="";
		String fileExtn="";
		switch (reportType) {
		case "csv":
			reportObj="report_exportCSV_link";
			fileExtn="csv";
			break;
		case "pdf":
			reportObj="report_exportPDF_link";
			fileExtn="pdf";
			break;
		case "xml":
			reportObj="report_exportXML_link";
			fileExtn="xml";
			break;
		case "mhtml":
			reportObj="report_exportMHTML_link";
			fileExtn="mhtml";
			break;
		case "excel":
			reportObj="report_exportExcel_link";
			fileExtn="xlsx";
			break;
		case "word":
			reportObj="report_exportWORD_link";
			fileExtn="docx";
			break;
		case "tiff":
			reportObj="report_exportTIFF_link";
			fileExtn="TIF";
			break;
		default:
			sc.STAF_ReportEvent("Fail", reportName, "Export format not supported.Format-"+reportType,0);
			break;
		}

		sc.clickWhenElementIsClickable("report_export_link", timeOutInSeconds);
		sc.clickWhenElementIsClickable(reportObj, timeOutInSeconds);
		String destinatioPath =Globals.currentPlatform_Path+"\\"+"ClientReports";
		String destFileName ="";

		File reportFile =  new File(Globals.currentRunPath+"\\"+reportName+"."+fileExtn);
		int counter = 0;
		while (counter<=60) {
			if(reportFile.exists()){
				break;
			}else{
				Thread.sleep(1000);
				counter++;
			}
		}

		if(reportFile.exists()){
			double bytes = reportFile.length();
			if(bytes == 0){
				sc.STAF_ReportEvent("Fail", reportName, "Exported report is 0 bytes.Format-"+reportType,0);
			}else{
				sc.mkDirs(destinatioPath);
				destFileName = destinatioPath+"\\"+Globals.TestCaseID +"_"+ reportFile.getName();
				File destFile = new File(destFileName);


				FileUtils.moveFile(reportFile, destFile);

				if(destFile.exists() && destFile.length() > 0) {
					Globals.Result_Link_FilePath = destFileName;
					sc.STAF_ReportEvent("Pass", reportName, reportType + "-Report export successfull",3);
				}else{
					sc.STAF_ReportEvent("Fail", reportName, reportType + "-Report export unsuccessfull",0);
				}
			}

		}else{
			sc.STAF_ReportEvent("Fail", reportName, "Unable to export report in format-"+reportType,0);
		}
	}

	/**************************************************************************************************
	 * Method to process a btach order through backend services
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String processBatchOrder() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;


		try{
			/* Psave Code use from single poll order
		     *  fix for
		     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
		     *       sun.security.validator.ValidatorException:
		     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
		     *               unable to find valid certification path to requested target
		     */
		    TrustManager[] trustAllCerts = new TrustManager[] {
		       new X509TrustManager() {
		          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		            return null;
		          }

		          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

		          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

		       }
		    };

		    SSLContext sc = SSLContext.getInstance("SSL");
		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		    // Create all-trusting host name verifier
		    HostnameVerifier allHostsValid = new HostnameVerifier() {
		        public boolean verify(String hostname, SSLSession session) {
		          return true;
		        }
		    };
		    // Install the all-trusting host verifier
		    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		    /*
		     * end of the fix
		     */
			
			String url=Globals.getEnvPropertyValue("BatchProcessing_URL");
			
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			String userpass = "CloudServices" + ":" + "820b5fe3f5a25d12";
			String basicAuth = "Basic "+javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", basicAuth);

			String data="";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			// Write query string to request body


			out.write(data);
			out.flush();
			out.close();

			// Read the response .Success message example 'Order 60654 results updated.'
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			String completeResponse = "";
			while ((line = in.readLine()) != null)
			{
				completeResponse = completeResponse + line;

			}
			int responseCode =0;
			responseCode = conn.getResponseCode();

			if(responseCode == 200){
				retval = Globals.KEYWORD_PASS;
			}else{
				retval = Globals.KEYWORD_FAIL;
			}
			in.close();


		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "Process Batch Order","Unable to process batch order using API due to exception-",1);
			throw e;
		}

		return retval;
	}

	/**************************************************************************************************
	 * Method to create and upload a btach order
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String CreateBatchOrder() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		long  timeOutInSeconds =  30;
		String updatedFilePath=null;
		String aysoSetting = Globals.testSuiteXLS.getCellData_fromTestData("CustomField2");
		
		if(aysoSetting.equalsIgnoreCase("AYSO ID")){
			updatedFilePath = ClientFacingApp.updateBatchOrderingCSVFileAYSO();
		}else{
			updatedFilePath = ClientFacingApp.updateBatchOrderingCSVFile();
		}
		
		tempRetval = sc.waitforElementToDisplay("volDashboard_orderBackgroundCheck_btn", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Order Background check button is not displayed" ,1);
			throw new Exception("Order Background check button is not displayed");
		}

		sc.clickWhenElementIsClickable("volDashboard_orderBackgroundCheck_btn",(int)timeOutInSeconds);
		sc.clickWhenElementIsClickable("volDashboard_placeABatchOrder_btn", (int)timeOutInSeconds);


		tempRetval = sc.waitforElementToDisplay("clientStep1_choosePosition_dd", timeOutInSeconds);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Step 1", "Batch Ordering Step 1 has not loaded" ,1);
			throw new Exception("Batch Ordering Step 1 has not loaded");
		}

		String posName = Globals.testSuiteXLS.getCellData_fromTestData("PositionName");
		sc.selectValue_byVisibleText("clientStep1_choosePosition_dd", posName);
		if(sc.BrowserType.equalsIgnoreCase("mozilla")||sc.BrowserType.equalsIgnoreCase("firefox")){
			WebElement objToBeClicked = sc.createWebElement("volDashboard_clickHere_link");
			String fileName = "Client Instructions - Volunteer Batch Order Instructions.pdf";
			String stepName = "Step1-Batch Ordering Instruction";
			String absPath2BeSaved = Globals.currentPlatform_Path+"\\Snapshots\\"+Globals.TestCaseID+"_"+fileName;
			sc.clickAndDownloadFile(objToBeClicked, absPath2BeSaved, fileName, stepName);
       }else{
    	   String winHandleBefore = driver.getWindowHandle();
    	   sc.clickWhenElementIsClickable("volDashboard_clickHere_link", (int)timeOutInSeconds);
    	   for(String winHandle : driver.getWindowHandles()){
    		   driver.switchTo().window(winHandle);
    	   }
    	   String url = driver.getCurrentUrl();
    	   String []env=Globals.Env_To_Execute_ON.split("_"); 
    	   String linkEnv=env[1];
		
    	   String amzezonUrl=null;
    	   if(linkEnv.startsWith("QA")){
    		   amzezonUrl="dev";
    	   }else if(linkEnv.equalsIgnoreCase("PROD")){
    		   amzezonUrl="prod";
    	   }
    	   String instructionurl = "https://s3.amazonaws.com/sterling-"+amzezonUrl+"-vv/pdf/Client%20Instructions%20-%20Volunteer%20Batch%20Order%20Instructions.pdf";
    	   String instructionUrlMoz="https://s3.amazonaws.com/sterling-"+amzezonUrl+"-vv/pdf/Client Instructions - Volunteer Batch Order Instructions.pdf";
		
    	   if(instructionurl.contains(url) || (instructionUrlMoz.contains(url))){
    		   sc.STAF_ReportEvent("Pass", "Verify Click here link url",  "Batch upload instruction PDF url is correct",3);
    	   }
    	   else{
    		   sc.STAF_ReportEvent("Fail", "Verify Click here link url",  "Batch upload instruction PDF url is not correct",0);
    	   }
    	   driver.close();
    	   driver.switchTo().window(winHandleBefore);
       }	
//		if(sc.BrowserType.equalsIgnoreCase("chrome") ||sc.BrowserType.equalsIgnoreCase("mozilla")||sc.BrowserType.equalsIgnoreCase("firefox")){
////			WebElement objToBeClicked = sc.createWebElement("volDashboard_clickHere_link");
////			String fileName = "Client Instructions - Volunteer Batch Order Instructions.pdf";
////			String stepName = "Step1-Batch Ordering Instruction";
////			String absPath2BeSaved = Globals.currentPlatform_Path+"\\Snapshots\\"+Globals.TestCaseID+"_"+fileName;
////			sc.clickAndDownloadFile(objToBeClicked, absPath2BeSaved, fileName, stepName);
//	}else{
//			sc.clickElementUsingJavaScript("volDashboard_clickHere_link");
//		//JavascriptExecutor js = (JavascriptExecutor)driver;
//		//js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';","volDashboard_clickHere_link");
//		//sc.clickWhenElementIsClickable("volDashboard_clickHere_link",(int) timeOutInSeconds);
//			Thread.sleep(3000);
//			String filePath = Globals.currentPlatform_Path+"\\Snapshots\\"+Globals.TestCaseID+"_";
//			String fileName = "Client Instructions - Volunteer Batch Order Instructions.pdf";
//			String tempval=sc.fileDownloadUsingAutoIt(filePath, fileName);
//			if(tempval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
//				
//				sc.STAF_ReportEvent("Pass", "VVOU142-Verify presence of Click here link",  "Upload instruction PDF export successfull",3);
//				
//			}
//			
//			else {
//				
//				sc.STAF_ReportEvent("Fail", "VVOU142-Verify presence of Click here link", "Upload instructions PDF export unsuccessful",0);
//			}
//			}

		sc.clickWhenElementIsClickable("batchOrdering_nextStep1_btn", (int)timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("batchOrdering_nextStep2_btn", timeOutInSeconds);


		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Step 2", "Batch Ordering Step 2 has not loaded" ,1);
			throw new Exception("Batch Ordering Step 2 has not loaded");
		}

//		if(sc.BrowserType.equalsIgnoreCase("chrome") ||sc.BrowserType.equalsIgnoreCase("mozilla")||sc.BrowserType.equalsIgnoreCase("firefox")){
//			WebElement objToBeClicked = sc.createWebElement("volDashboard_clickHere_link");
//			String fileName = "Client Instructions - Volunteer Batch Order Instructions.pdf";
//			String stepName = "Step2-Batch Ordering Instruction";
//			String absPath2BeSaved = Globals.currentPlatform_Path+"\\Snapshots\\"+Globals.TestCaseID+"_2_"+fileName;
//		sc.clickAndDownloadFile(objToBeClicked, absPath2BeSaved, fileName, stepName);
//		}else{
//			sc.clickElementUsingJavaScript("volDashboard_clickHere_link");
//			//JavascriptExecutor js = (JavascriptExecutor)driver;
//			//js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';","volDashboard_clickHere_link");
//			//sc.clickWhenElementIsClickable("volDashboard_clickHere_link",(int) timeOutInSeconds);
//				Thread.sleep(3000);
//				String filePath = Globals.currentPlatform_Path+"\\Snapshots\\"+Globals.TestCaseID+"_2_";
//				String fileName = "Client Instructions - Volunteer Batch Order Instructions.pdf";
//				String tempval=sc.fileDownloadUsingAutoIt(filePath, fileName);
//				if(tempval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
//					
//					sc.STAF_ReportEvent("Pass", "VVOU142-Verify presence of Click here link",  "Upload instruction PDF export successfull",3);
//					
//				}
//				
//				else {
//					
//					sc.STAF_ReportEvent("Fail", "VVOU142-Verify presence of Click here link", "Upload instructions PDF export unsuccessful",0);
//				}
//		}

		if(posName.equalsIgnoreCase(driver.findElement(By.xpath("//div[@id='step1Reset']/div[2]/div")).getText())){
			sc.STAF_ReportEvent("Pass", "Step 1",  "Selected position after Step 1 is displayed in UI ",1);
		}else{
			sc.STAF_ReportEvent("Fail", "Step 1",  "Selected position after Step 1 is NOT displayed in UI.Expected-"+posName,1);
		}
		String nonCSVFilePath = Globals.TestDir +"\\src\\appResource\\"+Globals.CurrentPlatform.toLowerCase()+"\\dataEngine\\VV_ReferenceFiles\\"+Globals.CurrentPlatform+"_UploadPhoto.jpg";
		
		if(Globals.BrowserType.equalsIgnoreCase("mozilla") || Globals.BrowserType.equalsIgnoreCase("firefox"))
		{
			
			JavascriptExecutor js = (JavascriptExecutor)driver;
			js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';",driver.findElement(LocatorAccess.getLocator("batchOrdering_browse_btn")));
			driver.findElement(LocatorAccess.getLocator("batchOrdering_browse_btn")).sendKeys(nonCSVFilePath);
			//driver.findElement(LocatorAccess.getLocator("batchOrdering_browse_btnb")).click();
		    //driver.switchTo().activeElement().sendKeys("abc");
		    //driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		   // String textI =driver.findElement(LocatorAccess.getLocator("batchOrdering_browse_btnb")).getAttribute("value");
			
		}
		else{
		driver.findElement(LocatorAccess.getLocator("batchOrdering_browse_btn")).sendKeys(nonCSVFilePath);
		}
		
		tempRetval = sc.waitforElementToDisplay("batchOrdering_nextStep2_btn", 3);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Step 2", "Error message is not displayed when non csv file is uploaded" ,1);

		}else{
			sc.STAF_ReportEvent("Pass", "Step 2", "Error message is displayed when non csv file is uploaded" ,1);
		}
	String textInUI =" ";
		if(Globals.BrowserType.equalsIgnoreCase("mozilla") || Globals.BrowserType.equalsIgnoreCase("firefox"))
		{
			JavascriptExecutor js = (JavascriptExecutor)driver;
			js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';",driver.findElement(LocatorAccess.getLocator("batchOrdering_browse_btn")));
			driver.findElement(LocatorAccess.getLocator("batchOrdering_browse_btn")).sendKeys(updatedFilePath);
		//driver.findElement(LocatorAccess.getLocator("batchOrdering_browse_btnb")).sendKeys(updatedFilePath);
		//textInUI =driver.findElement(By.xpath("//input[@id='btnUpload']/following::div/span[@class='btn btn-primary btn-file' and contains(text(),'Browse')]/following::span")).getText();
			textInUI =driver.findElement(LocatorAccess.getLocator("batchOrdering_browse_btn")).getAttribute("value");
		}
		else{
		driver.findElement(LocatorAccess.getLocator("batchOrdering_browse_btn")).sendKeys(updatedFilePath);

		textInUI =driver.findElement(LocatorAccess.getLocator("batchOrdering_browse_btn")).getAttribute("value");
		}	
		String expFileName = Globals.testSuiteXLS.getCellData_fromTestData("ReferenceNames");
		if(textInUI.contains(expFileName)){
			Globals.Result_Link_FilePath = updatedFilePath;
			sc.STAF_ReportEvent("Pass", "Step 2", "File Successfully uploaded" ,3);
			Globals.Result_Link_FilePath ="";
		}else{
			sc.STAF_ReportEvent("Fail", "Step 2", "batch Ordering File uploaded failed-Mismatch between uploaded file and file name being displayed" ,1);
			throw new Exception("unable to upload csv file");
		}

		sc.clickWhenElementIsClickable("batchOrdering_nextStep2_btn", (int)timeOutInSeconds);
		tempRetval = sc.waitforElementToDisplay("batchOrdering_summaryConsent_chk", timeOutInSeconds);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Step 3", "Batch Ordering Step 3 has not loaded" ,1);
			throw new Exception("Batch Ordering Step 3 has not loaded");
		}

		sc.checkCheckBox("batchOrdering_summaryConsent_chk");
		sc.checkCheckBox("batchOrdering_standardConsent_chk");
		sc.clickWhenElementIsClickable("batchOrdering_nextStep3_btn", (int)timeOutInSeconds);

		tempRetval = sc.waitforElementToDisplay("batchOrdering_placeOrder_btn", timeOutInSeconds);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Step 4", "Batch Ordering Step 4 has not loaded" ,1);
			throw new Exception("Batch Ordering Step 4 has not loaded");
		}

		sc.clickWhenElementIsClickable("batchOrdering_placeOrder_btn", (int)timeOutInSeconds);

		tempRetval = sc.waitforElementToDisplay("batchOrdering_done_link", timeOutInSeconds);

		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Step 5", "Batch Ordering Step 5 has not loaded" ,1);
			throw new Exception("Batch Ordering Step 5 has not loaded");
		}

		String actualText = driver.findElement(By.xpath("//form[@id='batchOrderForm']/div/div[5]/div[2]/div[@class='row']")).getText();
		if(actualText.contains("Batch successfully submitted")){
			sc.STAF_ReportEvent("Pass", "Step 5", "Batch successfully submitted." ,1);
			retval = Globals.KEYWORD_PASS;
		}else{
			sc.STAF_ReportEvent("Fail", "Step 5", "Unable to submit the bathc order" ,1);
			throw new Exception("Unable to submit the bathc order");
		}

		sc.clickWhenElementIsClickable("batchOrdering_done_link", (int)timeOutInSeconds);

		processBatchOrder();


		return retval;
	}

	/**************************************************************************************************
	 * Method to update data in batch order
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String updateBatchOrderingCSVFile() throws Exception{
        
		String OrderHoldQueuesettings = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldQueuecheckbox");
		String aysoSetting = Globals.testSuiteXLS.getCellData_fromTestData("CustomField2");
		String refUploadFile="";
		if(OrderHoldQueuesettings.equalsIgnoreCase("Yes")){
			
		 refUploadFile = Globals.TestDir +"\\src\\test\\Resources\\refFile\\BatchOrderingOrderHold_"+Globals.Env_To_Execute_ON.toUpperCase()+".csv";
		}else{
		 refUploadFile = Globals.TestDir +"\\src\\test\\Resources\\refFile\\BatchOrdering_"+Globals.Env_To_Execute_ON.toUpperCase()+".csv";
		}
		String uniqueString=RandomStringUtils.randomAlphanumeric(8);
		String filename = Globals.TestCaseID + "_BO_"+uniqueString+".csv";
		String resultCSVFile = Globals.currentPlatform_Path +"\\Excel_Results\\"+filename ;

		Globals.testSuiteXLS.setCellData_inTestData("ReferenceNames",filename);

		String orderedDate = sc.getTodaysDate();
		Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);


		CsvReader volData = new CsvReader(refUploadFile);
		File file = new File(resultCSVFile);
		if(!file.exists()){
			file.createNewFile();
		}

		CsvWriter newVolData = new CsvWriter(resultCSVFile);

		//		volData.readHeaders();
		String noOfOrders = "";
		noOfOrders = Globals.testSuiteXLS.getCellData_fromTestData("NoOfBatchOrders");

		int noOfBatchOrders = 0;
		if(noOfOrders.isEmpty() || noOfOrders.equals("") || noOfOrders == null){
			noOfBatchOrders=0;
		}else{
			noOfBatchOrders = Integer.parseInt(noOfOrders);
		}

		int lineCount = 1;
		String[] modifiedValues = new String[27]; 
		String[] existsingValues = new String[27];
		String dynamicString="";
        int aysoIndex=0;   
		while(volData.readRecord()){
			if(lineCount == 2){

				modifiedValues[0] = "";
				modifiedValues[1] = "";
				modifiedValues[2] = "";
				modifiedValues[3] = "";
				modifiedValues[9] = Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN");
				
				int noOfyears=0;
				String dobCategory =Globals.testSuiteXLS.getCellData_fromTestData("VolDOBCategory");
				if(dobCategory.equalsIgnoreCase("GT than 18")){
					noOfyears = -22;
				}else if(dobCategory.equalsIgnoreCase("GT 14 Less than 18")){
					noOfyears = -16;
				}else if(dobCategory.equalsIgnoreCase("Less Than 14")){
					noOfyears = -13;
				}
				
				modifiedValues[10] = sc.getPriorDate("M/dd/yyyy", noOfyears);
				modifiedValues[11] = Globals.Volunteer_Gender;
				modifiedValues[12]= Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_License");
				modifiedValues[13]=  sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("AddressState"));
				modifiedValues[14]="";

				String setVolunteerEmail  =	Globals.testSuiteXLS.getCellData_fromTestData("SetVolunteerEmail");
				String emailID="";
				if(setVolunteerEmail.equalsIgnoreCase("Yes")){
					emailID =Globals.fromEmailID;
				}else{
					emailID="";
					}

				modifiedValues[15] = emailID;
				modifiedValues[16] = Globals.Volunteer_Phone;
				modifiedValues[17] ="USA";
				modifiedValues[18] = Globals.Volunteer_AddressLine;
				modifiedValues[19] = Globals.Volunteer_AddressLine;
				modifiedValues[20] = Globals.Volunteer_City;
				modifiedValues[21] =  sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("AddressState"));
				modifiedValues[22] =  Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode");
				modifiedValues[23] = Globals.testSuiteXLS.getCellData_fromTestData("CustomField1");
				modifiedValues[24] = Globals.testSuiteXLS.getCellData_fromTestData("CustomField2");
				modifiedValues[25] = Globals.testSuiteXLS.getCellData_fromTestData("CustomField3");
				modifiedValues[26] ="";
				for(int i=1;i<=noOfBatchOrders;i++){

					modifiedValues[4] = RandomStringUtils.randomNumeric(6);

					dynamicString = RandomStringUtils.randomAlphabetic(15);
					modifiedValues[5] = dynamicString; // applicant name
					modifiedValues[6] = dynamicString;
					modifiedValues[7] = dynamicString;
					modifiedValues[8] = dynamicString;
                    
																
					newVolData.writeRecord(modifiedValues);
					
					if(i==1){
						//this will write the details of 1 volunteer in excel
						Globals.testSuiteXLS.setCellData_inTestData("volunteerFName", dynamicString);
						Globals.testSuiteXLS.setCellData_inTestData("volunteerLName",dynamicString);
						Globals.testSuiteXLS.setCellData_inTestData("volunteerMidName", dynamicString);
						Globals.testSuiteXLS.setCellData_inTestData("CustomEmailMessage", dynamicString);
					}

				}
				lineCount++;
			}
			else{
				existsingValues = volData.getValues();
				newVolData.writeRecord(existsingValues);
				lineCount ++;
			}
		}

		newVolData.close();

		volData.close();


		return resultCSVFile;

	}

	
	/**************************************************************************************************
	 * Method to update data in batch order AYSO
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String updateBatchOrderingCSVFileAYSO() throws Exception{
        
		
		String aysoSetting = Globals.testSuiteXLS.getCellData_fromTestData("CustomField2");
		String refUploadFile="";
		
		if(aysoSetting.equalsIgnoreCase("AYSO ID")){
			refUploadFile = Globals.TestDir +"\\src\\test\\Resources\\refFile\\BatchOrderingAYSO_"+Globals.Env_To_Execute_ON.toUpperCase()+".csv";		
		}
		else{
		 refUploadFile = Globals.TestDir +"\\src\\test\\Resources\\refFile\\BatchOrdering_"+Globals.Env_To_Execute_ON.toUpperCase()+".csv";
		}
		String uniqueString=RandomStringUtils.randomAlphanumeric(8);
		String filename = Globals.TestCaseID + "_BO_"+uniqueString+".csv";
		String resultCSVFile = Globals.currentPlatform_Path +"\\Excel_Results\\"+filename ;

		Globals.testSuiteXLS.setCellData_inTestData("ReferenceNames",filename);

		String orderedDate = sc.getTodaysDate();
		Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);


		CsvReader volData = new CsvReader(refUploadFile);
		File file = new File(resultCSVFile);
		if(!file.exists()){
			file.createNewFile();
		}

		CsvWriter newVolData = new CsvWriter(resultCSVFile);

		//		volData.readHeaders();
		String noOfOrders = "";
		noOfOrders = Globals.testSuiteXLS.getCellData_fromTestData("NoOfBatchOrders");

		int noOfBatchOrders = 0;
		if(noOfOrders.isEmpty() || noOfOrders.equals("") || noOfOrders == null){
			noOfBatchOrders=0;
		}else{
			noOfBatchOrders = Integer.parseInt(noOfOrders);
		}

		int lineCount = 1;
		String[] modifiedValues = new String[27]; 
		String[] existsingValues = new String[27];
		String dynamicString="";
        String[] aysoValue={"yes","No"};   
        int aysoIndex=0;   
		while(volData.readRecord()){
			if(lineCount == 2){

				modifiedValues[0] = "";
				modifiedValues[1] = "";
				modifiedValues[2] = "";
				modifiedValues[3] = "";
				modifiedValues[9] = Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN");
				
				int noOfyears=0;
				String dobCategory =Globals.testSuiteXLS.getCellData_fromTestData("VolDOBCategory");
				if(dobCategory.equalsIgnoreCase("GT than 18")){
					noOfyears = -22;
				}else if(dobCategory.equalsIgnoreCase("GT 14 Less than 18")){
					noOfyears = -16;
				}else if(dobCategory.equalsIgnoreCase("Less Than 14")){
					noOfyears = -13;
				}
				
				modifiedValues[10] = sc.getPriorDate("M/dd/yyyy", noOfyears);
				modifiedValues[11] = Globals.Volunteer_Gender;
				modifiedValues[12]= Globals.testSuiteXLS.getCellData_fromTestData("Volunteer_DL_License");
				modifiedValues[13]=  sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("AddressState"));
				modifiedValues[14]="";

				String setVolunteerEmail  =	Globals.testSuiteXLS.getCellData_fromTestData("SetVolunteerEmail");
				String emailID="";
				if(setVolunteerEmail.equalsIgnoreCase("Yes")){
					emailID =Globals.fromEmailID;
				}else{
					emailID="";
					}

				modifiedValues[15] = emailID;
				modifiedValues[16] = Globals.Volunteer_Phone;
				modifiedValues[17] ="USA";
				modifiedValues[18] = Globals.Volunteer_AddressLine;
				modifiedValues[19] = Globals.Volunteer_AddressLine;
				modifiedValues[20] = Globals.Volunteer_City;
				modifiedValues[21] =  sc.getStateCode(Globals.testSuiteXLS.getCellData_fromTestData("AddressState"));
				modifiedValues[22] =  Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode");
				modifiedValues[23] = Globals.testSuiteXLS.getCellData_fromTestData("CustomField1");
				modifiedValues[24] = Globals.testSuiteXLS.getCellData_fromTestData("CustomField2");
				modifiedValues[25] = Globals.testSuiteXLS.getCellData_fromTestData("CustomField3");
				modifiedValues[26] ="";
				for(int i=1;i<=noOfBatchOrders;i++){

					modifiedValues[4] = RandomStringUtils.randomNumeric(6);

					dynamicString = RandomStringUtils.randomAlphabetic(15);
					modifiedValues[5] = dynamicString; // applicant name
					modifiedValues[6] = dynamicString;
					modifiedValues[7] = dynamicString;
					modifiedValues[8] = dynamicString;
                    
					//AYSO batch order custom field, custom 1 and custom 2
					if(aysoSetting.equalsIgnoreCase("AYSO ID")){
						modifiedValues[23]=aysoValue[aysoIndex];
						modifiedValues[24] = modifiedValues[4]; 
						aysoIndex++;
						
					}
								
					newVolData.writeRecord(modifiedValues);
					
					if(i==1){
						//this will write the details of 1 volunteer in excel
						Globals.testSuiteXLS.setCellData_inTestData("volunteerFName", dynamicString);
						Globals.testSuiteXLS.setCellData_inTestData("volunteerLName",dynamicString);
						Globals.testSuiteXLS.setCellData_inTestData("volunteerMidName", dynamicString);
						Globals.testSuiteXLS.setCellData_inTestData("CustomEmailMessage", dynamicString);
						Globals.testSuiteXLS.setCellData_inTestData("CustomFieldAnswers", modifiedValues[4]);
						
					}

				}
				lineCount++;
				
			}
			else{
				existsingValues = volData.getValues();
				newVolData.writeRecord(existsingValues);
				lineCount ++;
			}
		}

		newVolData.close();

		volData.close();


		return resultCSVFile;

	}
	
	/**************************************************************************************************
	 * Method to verify batch ordering datat  that has been uploaded and processed
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyBatchOrder() throws Exception{
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		long  timeOutInSeconds =  30;
		try{

			ClientFacingApp.navigateVolunteerDashboard();
			Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
			sc.clickWhenElementIsClickable("volDashboard_orderBackgroundCheck_btn",(int)timeOutInSeconds);
			sc.clickWhenElementIsClickable("volDashboard_reviewBatchOrder_btn", (int)timeOutInSeconds);


			tempRetval = sc.waitforElementToDisplay("reviewBatch_uploadGrid_tbl", timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Review Batch Order", "Page has not loaded" ,1);
				throw new Exception("Review Batch Order Page has not loaded");
			}
			WebElement resultTbl= sc.createWebElement("reviewBatch_uploadGrid_tbl");

			int rowCount = -1;
			int colCount = -1;
			rowCount = sc.getRowCount_tbl(resultTbl);
			colCount = sc.getColumnCount_tbl(resultTbl);

			String expFileName =  Globals.testSuiteXLS.getCellData_fromTestData("ReferenceNames");
			String actualFileName ="";
			String orderedDate = Globals.testSuiteXLS.getCellData_fromTestData("OrderedDate");
			String username = Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
			String cellVal ="";
			boolean batchFileFound = false;
			String batchSize ="";
			String OrderFailed =Globals.testSuiteXLS.getCellData_fromTestData("OrderedFailed");
			batchSize = String.valueOf(Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfBatchOrders")) + Integer.parseInt(OrderFailed));
			String[] expVals = {expFileName,batchSize,OrderFailed,orderedDate,orderedDate,"Submitted",username};
			int i=0;
			for(i=1;i<=rowCount;i++){
				actualFileName = resultTbl.findElement(By.xpath("./tbody/tr["+i+"]/td[1]")).getText();
				if(expFileName.equalsIgnoreCase(actualFileName)){
					//counter to wait as sometimes it takes time to process batch order - max timeoout 60 seconds
					int counter =0;
					while(counter<=60){
						for(int j=2;j<=colCount;j++){
							cellVal = resultTbl.findElement(By.xpath("./tbody/tr["+i+"]/td["+j+"]")).getText();
							if(expVals[j-1].equalsIgnoreCase(cellVal)){
								batchFileFound = true;
								continue;
							}else{
								batchFileFound=false;
								break;
							}
						}

						if(!batchFileFound){
							processBatchOrder();
							driver.navigate().refresh();
							resultTbl= sc.createWebElement("reviewBatch_uploadGrid_tbl");
							Thread.sleep(1000);
							counter++;
						}else{
							break;
						}
					}

				}

				if(batchFileFound){
					break;
				}

			}

			if(batchFileFound){
				sc.STAF_ReportEvent("Pass", "Review Batch Order", "Uploaded batch order file processed as expected.FileName-"+expFileName ,1);
			}else{
				sc.STAF_ReportEvent("Fail", "Review Batch Order", "Unable to find uploaded batch file that has been Submitted.FileName-"+ expFileName,1);
				throw new Exception("Unable to find uploaded batch file that has been Submitted");
			}

			//verify downloaded reportd from UI
			if(sc.BrowserType.equalsIgnoreCase("chrome") ||sc.BrowserType.equalsIgnoreCase("mozilla")||sc.BrowserType.equalsIgnoreCase("firefox")){
			WebElement objToBeClicked = resultTbl.findElement(By.xpath("./tbody/tr["+i+"]/td[2]/a/span"));
			String fileName = "ExportAll_"+expFileName;
			String stepName = "Review Batch Order-Export All";
			String absPath2BeSaved = Globals.currentPlatform_Path+"\\Snapshots\\"+fileName;
			sc.clickAndDownloadFile(objToBeClicked, absPath2BeSaved, fileName, stepName);
			verifyBatchOrder_ExportedFile( Globals.currentPlatform_Path +"\\Excel_Results\\"+expFileName,absPath2BeSaved,stepName);
			}
			else{
			WebElement objToBeClicked = resultTbl.findElement(By.xpath("./tbody/tr["+i+"]/td[2]/a/span"));
			sc.clickElementUsingJavaScript(objToBeClicked);
			//JavascriptExecutor js = (JavascriptExecutor)driver;
			//js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';","volDashboard_clickHere_link");
			//sc.clickWhenElementIsClickable("volDashboard_clickHere_link",(int) timeOutInSeconds);
				Thread.sleep(3000);
				String filePath = Globals.currentPlatform_Path+"\\Snapshots\\";
//				String fileName = "ExportAll_"+expFileName;
//				String tempval=sc.fileDownloadUsingAutoIt(filePath, fileName);
//				if(tempval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
//					
//					sc.STAF_ReportEvent("Pass", "VV-Verify export during batch order",  "Export successful",3);
//					
//				}
//				
//				else {
//					
//					sc.STAF_ReportEvent("Fail", "VV-Verify export during batch order", "Export unsuccessful",0);
//				}
			}
			String OrderHoldQueuesettings = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldQueuecheckbox");
			if(OrderHoldQueuesettings.equalsIgnoreCase("")||OrderHoldQueuesettings.equals("No")||OrderHoldQueuesettings.equals(null)){
			if(sc.BrowserType.equalsIgnoreCase("chrome") ||sc.BrowserType.equalsIgnoreCase("mozilla")||sc.BrowserType.equalsIgnoreCase("firefox")){
			WebElement objToBeClicked = resultTbl.findElement(By.xpath("./tbody/tr["+i+"]/td[3]/a/span"));
			String fileName = "ExportFailures_"+expFileName;
			String stepName = "Review Batch Order-Export Failed Orders";
			String absPath2BeSaved = Globals.currentPlatform_Path+"\\Snapshots\\"+fileName;
			sc.clickAndDownloadFile(objToBeClicked, absPath2BeSaved, fileName, stepName);
			verifyBatchOrder_ExportedFile(Globals.currentPlatform_Path +"\\Excel_Results\\"+expFileName,absPath2BeSaved,stepName);
			}
			else{
			WebElement objToBeClicked = resultTbl.findElement(By.xpath("./tbody/tr["+i+"]/td[3]/a/span"));
			sc.clickElementUsingJavaScript(objToBeClicked);
			//JavascriptExecutor js = (JavascriptExecutor)driver;
			//js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';","volDashboard_clickHere_link");
			//sc.clickWhenElementIsClickable("volDashboard_clickHere_link",(int) timeOutInSeconds);
				Thread.sleep(3000);
				String filePath = Globals.currentPlatform_Path+"\\Snapshots\\";
//				String fileName = "ExportFailures_"+expFileName;
//				String tempval=sc.fileDownloadUsingAutoIt(filePath, fileName);
//				if(tempval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
//					
//					sc.STAF_ReportEvent("Pass", "VV-Verify export during batch order",  "Export successful",3);
//					
//				}
//				
//				else {
//					
//					sc.STAF_ReportEvent("Fail", "VV-Verify export during batch order", "Export unsuccessful",0);
//				}
			}}
			retval = Globals.KEYWORD_PASS;
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "Verify Batch Order", "Unable to verify batch order due to exception - "+e.toString(),1);
			throw e;
		}
		return retval;
	}

	
	/**************************************************************************************************
	 * Method to verify Export functionality that has been download and processed
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyExport() throws Exception{
		APP_LOGGER.startFunction("Export Functionality");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		long  timeOutInSeconds =  30;
		InputStream inStream = null;
		OutputStream outStream = null;
		try{

			//ClientFacingApp.navigateVolunteerDashboard();
			tempRetval=sc.waitforElementToDisplay("volDashboard_export_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Volunteer Dashboard - Export Button", "Export Button is not getting displayed" ,1);
				throw new Exception("Export Button is not getting displayed");
			}
			//sc.clickWhenElementIsClickable("volDashboard_export_btn",(int)timeOutInSeconds);
            String filename="ExportRefFile_ENV_QA2.csv";
			String refUploadFile="";
			refUploadFile = Globals.TestDir +"\\src\\test\\Resources\\refFile\\ExportRefFile_"+Globals.Env_To_Execute_ON.toUpperCase()+".csv";
			String resultCSVFile = Globals.currentPlatform_Path +"\\Excel_Results\\"+filename ;
			int totalVolunteer=Integer.parseInt((driver.findElement(By.xpath("//div[@class='sumTable-graph-label areaPadTop20']/span")).getText().trim()));
			//Globals.testSuiteXLS.setCellData_inTestData("ReferenceNames",filename);

			CsvReader volData = new CsvReader(refUploadFile);
			File source = new File(refUploadFile);
			
			File dest = new File(resultCSVFile);
			

		    inStream = new FileInputStream(source);
		    outStream = new FileOutputStream(dest);

		    byte[] buffer = new byte[1024];

		    int length;
		        //copy the file content in bytes 
		    while ((length = inStream.read(buffer)) > 0) {
		           outStream.write(buffer, 0, length);
		    }

		   inStream.close();
		   outStream.close();

					
			//verify downloaded reportd from UI
			if(sc.BrowserType.equalsIgnoreCase("chrome") ||sc.BrowserType.equalsIgnoreCase("mozilla")||sc.BrowserType.equalsIgnoreCase("firefox")){
			WebElement objToBeClicked = sc.createWebElement("volDashboard_export_btn");
			String fileName = "Volunteers.csv";
			String expFileName = "Volunteers.csv";
			String stepName = "Export Functionality-File Data Verify";
			String absPath2BeSaved = Globals.currentPlatform_Path+"\\Snapshots\\"+fileName;
			sc.clickAndDownloadFile(objToBeClicked, absPath2BeSaved, fileName, stepName);
			verifyExport_ExportedFile(resultCSVFile,absPath2BeSaved,stepName,totalVolunteer);
			}
			else{
			WebElement objToBeClicked = sc.createWebElement("volDashboard_export_btn");
			sc.clickElementUsingJavaScript(objToBeClicked);
			//JavascriptExecutor js = (JavascriptExecutor)driver;
			//js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';","volDashboard_clickHere_link");
			//sc.clickWhenElementIsClickable("volDashboard_clickHere_link",(int) timeOutInSeconds);
				Thread.sleep(3000);
				String filePath = Globals.currentPlatform_Path+"\\Snapshots\\";
//				String fileName = "ExportAll_"+expFileName;
//				String tempval=sc.fileDownloadUsingAutoIt(filePath, fileName);
//				if(tempval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
//					
//					sc.STAF_ReportEvent("Pass", "VV-Verify export during batch order",  "Export successful",3);
//					
//				}
//				
//				else {
//					
//					sc.STAF_ReportEvent("Fail", "VV-Verify export during batch order", "Export unsuccessful",0);
//				}
			}
			
		
			retval = Globals.KEYWORD_PASS;
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "Verify Batch Order", "Unable to verify batch order due to exception - "+e.toString(),1);
			throw e;
		}
		return retval;
	}	

	/**************************************************************************************************
	 * Method to verify batch ordering 2 exported files containing error description and order ids
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyBatchOrder_ExportedFile(String srcFile,String destFile,String exportedFileType) throws Exception {

		String noOfOrders = "";
		noOfOrders = Globals.testSuiteXLS.getCellData_fromTestData("NoOfBatchOrders");

		int noOfBatchOrders = 0;
		if(noOfOrders.isEmpty() || noOfOrders.equals("") || noOfOrders == null){
			noOfBatchOrders=0;
		}else{
			noOfBatchOrders = Integer.parseInt(noOfOrders);
		}

		CsvReader refFile = new CsvReader(srcFile);
		CsvReader opFile = new CsvReader(destFile);


		int lineCount = 1;
		String[] refValues = new String[27]; 
		String[] opValues = new String[10];

		opFile.readRecord();
		refFile.readRecord();

		//failed order exported files wont conatin successful rows
		if(exportedFileType.contains("Failed Orders")){
			for (int i = 1; i <= noOfBatchOrders; i++) {
				refFile.readRecord();

			}

		}
		boolean errorDescriptionMismatchFound = false;

		while(opFile.readRecord() && refFile.readRecord() ){

			opValues = opFile.getValues();
			refValues = refFile.getValues();

			for(int i=0;i<opValues.length;i++){
				opValues[i]=opValues[i].replace(new Character((char)0).toString(), "").replace(new Character((char)34).toString(), "").trim();
			}


			if(lineCount<=noOfBatchOrders && exportedFileType.contains("Export All")){
				if(opValues[0].isEmpty() || opValues[0].equals("") || opValues[0] ==null){
					sc.STAF_ReportEvent("Fail", exportedFileType, "Order ID not generated for line-"+lineCount+"for ClientRefID-"+opValues[2] , 0);
				}else{
					sc.STAF_ReportEvent("Pass", exportedFileType, "Order ID generated for line-"+lineCount+"for ClientRefID-"+opValues[2] + " Order ID="+opValues[0] , 0);
					
					if(lineCount==1){
						//this will write the order id of 1 volunteer in excel
						String npnOrderID="";
						String OrderHoldQueuesettings = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldQueuecheckbox");
						if(OrderHoldQueuesettings.equalsIgnoreCase("Yes")){
							String npnid = opValues[0].split("-")[0];
							npnOrderID= npnid.split("-")[0];
							String orderedDate = sc.getTodaysDate();
							Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
							Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderID);
						}
						else{
						npnOrderID= opValues[0].split("-")[0];
						String swestOrderID = opValues[0].split("-")[1];
						String orderedDate = sc.getTodaysDate();
						Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
						Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderID);
						Globals.testSuiteXLS.setCellData_inTestData("SWestOrderID", swestOrderID);
						}
						if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
							Queryforapilog(npnOrderID,"Batch","Blank","NoNo");
						}
					}
				}
				
			
			}else{

				String expError = refValues[26].trim();
				String actualError = opValues[8];
				if(!expError.equalsIgnoreCase(actualError)){

					errorDescriptionMismatchFound = true;
					sc.STAF_ReportEvent("Fail", exportedFileType, "Error Description Mismatch at line-"+lineCount+" for ClientRefID-"+opValues[2] + " Exp-"+refValues[26]+" Actual-"+opValues[8], 0);
				}
			}

			lineCount++;



		}

		if(!errorDescriptionMismatchFound){
			sc.STAF_ReportEvent("Pass", exportedFileType, "All generated error description is as expected",0);
		}

		opFile.close();

		refFile.close();

	}

	/**************************************************************************************************
	 * Method to verify actual export downloaded file compare with expected export report file
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyExport_ExportedFile(String srcFile,String destFile,String exportedFileType, int totalNoVol) throws Exception {

		CsvReader refFile = new CsvReader(srcFile);
		CsvReader opFile = new CsvReader(destFile);
		
		//String[] refValues = new String[34]; 
		//String[] refValue1=  new String[34]; 
		//String[] opValues = new String[34];

		//opFile.readRecord();
		//refFile.readRecord();

		int lineCount = 1;
		boolean errorDescriptionMismatchFound = false;

		while(opFile.readRecord() && refFile.readRecord() ){

			String[] opValues = opFile.getValues();
			String[] refValues = refFile.getValues();
			String[] refValue1=new String[opValues.length]; 
			for(int i=0;i<opValues.length;i++){
				opValues[i]=opValues[i].replace(new Character((char)0).toString(), "").replace(new Character((char)34).toString(), "").trim();
				//refValues[i]=refValues[i].replace(new Character((char)0).toString(), "").replace(new Character((char)34).toString(), "").trim();
			}
            int cnt=0; 
			for(int i=0;i<refValues.length;i++){
				refValues[i]=refValues[i].replace(new Character((char)0).toString(), "").replace(new Character((char)34).toString(), "").trim();
				String arr[]=refValues[i].split(",");
				if(arr.length>1){
					
					for(int j=0;j<arr.length;j++){
						refValue1[cnt]=arr[j].trim();
						cnt++;
												
					}
				}else{
					refValue1[cnt]=arr[0].trim();
					cnt++;
				}
				//refValues[i]=refValues[i].replace(new Character((char)0).toString(), "").replace(new Character((char)34).toString(), "").trim();
			}

			int size=14;
			if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA2")){
				size=3;		
			}
			for(int i=0;i<size;i++){
				String exptValue = refValue1[i].trim();
				String actualValue = opValues[i].trim();
				if(!actualValue.equalsIgnoreCase(exptValue)){
						errorDescriptionMismatchFound = true;
						sc.STAF_ReportEvent("Fail", exportedFileType, "Data Mismatch at row-"+lineCount+" Expected Value : "+exptValue+" Actual Value -"+actualValue, 0);
						throw new Exception("Export File data not matching as per expected");
				}
							
			}
			
			lineCount++;



		}
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(destFile));
	     String input;
	     int count = 0;
	     while((input = bufferedReader.readLine()) != null)
	     {
	    	 count++;
	     }
	     bufferedReader.close();

		if(!errorDescriptionMismatchFound){
			
			int noOfVolunteerRecord=count-2;
			if(noOfVolunteerRecord==totalNoVol){
				sc.STAF_ReportEvent("Pass", exportedFileType, "All actual export file data Matched as per expected",0);
				sc.STAF_ReportEvent("Pass", exportedFileType, "Actual volunteer data Matched :"+noOfVolunteerRecord+" Expected Volunteer count data matched : "+totalNoVol,0);
			}else{
				sc.STAF_ReportEvent("Fail", exportedFileType, "Actual volunteer data Missmatched :"+noOfVolunteerRecord+" Expected Volunteer count data Missmatched : "+totalNoVol,0);
			}
			
		}

		opFile.close();

		refFile.close();

	}
	
	/**************************************************************************************************
	 * Method to share a volunteer order
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String orderSharing() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{
			
			gotoVolunteerMyProfile();
			
			sc.waitforElementToDisplay("volunteerHomepage_findOrgToShare_link", timeOutinSeconds);
			
			sc.clickWhenElementIsClickable("volunteerHomepage_findOrgToShare_link", (int) timeOutinSeconds);

			if(sc.waitforElementToDisplay("sharing_state_dd", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_FAIL)){
		
				sc.STAF_ReportEvent("Fail", "Order Sharing", "Page has not loaded", 1);
				throw new Exception("Order Sharing Page has not loaded");
				
			}

			sc.clickWhenElementIsClickable("sharing_close_link", (int) timeOutinSeconds);
			if(sc.waitforElementToDisplay("volunteerHomepage_findOrgToShare_link", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				
				sc.STAF_ReportEvent("Fail", "Order Sharing", "Volunteer homepage Page has not loaded", 1);
				throw new Exception("Order Sharing-Volunteer homepage Page has not loaded");
				
			}
			
			
			sc.clickWhenElementIsClickable("volunteerHomepage_findOrgToShare_link", (int) timeOutinSeconds);

			if(sc.waitforElementToDisplay("sharing_state_dd", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_FAIL)){
		
				sc.STAF_ReportEvent("Fail", "Order Sharing", "Page has not loaded", 1);
				throw new Exception("Order Sharing Page has not loaded");
				
			}		
			
			sc.clickWhenElementIsClickable("sharing_clickHere_link", (int) timeOutinSeconds);
			if(sc.waitforElementToDisplay("externalShare_fname_txt", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				
				sc.STAF_ReportEvent("Fail", "Order Sharing", "Non Client Sharing Page has not loaded", 1);
				throw new Exception("Non Client Sharing Page has not loaded");
				
			}	
			sc.setValueJsChange("externalShare_fname_txt", "test");
			sc.setValueJsChange("externalShare_lname_txt", "test");
			sc.setValueJsChange("externalShare_orgName_txt", "testAutomation");
			sc.setValueJsChange("externalShare_email_txt", Globals.fromEmailID);
			String customEmailMessage = sc.updateAndFetchRuntimeValue("CustomEmailMsg_runtimeUpdateFlag","CustomEmailMessage",45);
			
			sc.setValueJsChange("externalShare_candidateComments_txt", customEmailMessage);
			sc.checkCheckBox("externalShare_sendEmail_chk");
			sc.checkCheckBox("externalShare_consent_chk");
			sc.clickWhenElementIsClickable("externalShare_share_btn", (int) timeOutinSeconds);
			String []env=Globals.Env_To_Execute_ON.split("_"); 
			String linkEnv=env[1];
			
			String subjectEmailPattern = linkEnv+" Important: New Volunteer Request";
			String bodyEmailPattern = Globals.testSuiteXLS.getCellData_fromTestData("CustomEmailMessage");
			String emailFound=null;
			
			if(Globals.EXECUTION_MACHINE.equalsIgnoreCase("jenkins")) {
			    String subjectEmailPatterndb="Important: New Volunteer Request";
			    String  emailBodyText = ClientFacingApp.FetchInviatationURLFromDB(bodyEmailPattern,subjectEmailPatterndb);
			    if(emailBodyText.equalsIgnoreCase(Globals.KEYWORD_FAIL)) {
			        sc.STAF_ReportEvent("Fail", "Non Client Share", "Email NOT fetched by Database of volunteer for non client share", 0);
	                
			    }else{
			    	sc.STAF_ReportEvent("Pass", "Non Client Share", "Email fetched by Database of volunteer for non client share",0);
			    }
			               
               
			}else {
			
			
			HashMap<String, String> emailDetails=  new HashMap<String, String>();
			emailDetails = Email.fetchEmailFromOutlook(subjectEmailPattern,bodyEmailPattern,15);
			emailFound = emailDetails.get("EmailFound");

			if (emailFound.equalsIgnoreCase("True")){
				
				sc.STAF_ReportEvent("Pass", "Non Client Share", "Email received from volunteer for non client share",0);
				
			}else{
				sc.STAF_ReportEvent("Fail", "Non Client Share", "Email NOT received from volunteer for non client share", 0);
				
			}
			}

			if(sc.waitforElementToDisplay("sharing_state_dd", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_FAIL)){
		
				sc.STAF_ReportEvent("Fail", "Order Sharing", "Page has not loaded", 1);
				throw new Exception("Order Sharing Page has not loaded after sharing with non client");
				
			}	
			
			sc.selectValue_byValue("sharing_miles_dd", "100");
			sc.setValueJsChange("sharing_zipCode_txt", "32007");
			sc.clickWhenElementIsClickable("sharing_search_btn", (int) timeOutinSeconds);
			
			int flagSet=0;
			
			
			tempRetval = sc.waitforElementToDisplay("sharing_noresult_lable", 5);

			while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(5000);
				tempRetval = sc.waitforElementToDisplay("sharing_noresult_lable", 5);
				if(flagSet==10){
					break;
				}
				flagSet++;
			}
			
			List<WebElement> clientList = driver.findElements(By.xpath("//div[@class='col-md-4 col-sm-6 col-xs-8']"));
			if(clientList.size() == 0 ){
				sc.STAF_ReportEvent("Fail", "Order Sharing", "Search results not generated when searched with zip code criteria", 1);
			}else{
				sc.STAF_ReportEvent("pass", "Order Sharing", "Search results are generated when searched with zip code criteria", 1);
			}
			clientList = null;
			
			//searching based on client name
			sc.selectValue_byVisibleText("sharing_state_dd", "Florida");
			sc.clickWhenElementIsClickable("sharing_advancedSearch_btn", (int) timeOutinSeconds);
			String expClientName = Globals.testSuiteXLS.getCellData_fromTestData("ClientToBeShared");
			sc.setValueJsChange("sharing_clientName_txt", expClientName);
			sc.clickWhenElementIsClickable("sharing_search_btn", (int) timeOutinSeconds);
			
			flagSet=0;
			
			
			tempRetval = sc.waitforElementToDisplay("sharing_noresult_lable", 5);

			while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(5000);
				tempRetval = sc.waitforElementToDisplay("sharing_noresult_lable", 5);
				if(flagSet==10){
					break;
				}
				flagSet++;
			}
			
			clientList = driver.findElements(By.xpath("//div[@class='col-md-4 col-sm-6 col-xs-8']"));
			int size = clientList.size();
			if(size == 0 ){
				sc.STAF_ReportEvent("Fail", "Order Sharing", "Search results not generated when searched with client name criteria", 1);
				throw new Exception("Search results not generated when searched with client name criteria");
			}else{
				sc.STAF_ReportEvent("Pass", "Order Sharing", "Search results are generated when searched with client name criteria", 1);
				
				String resultClientName = "";
				boolean clientFound =false;
				int i=0;
				
				for (i=0;i<size;i++){
					resultClientName = clientList.get(i).findElement(By.xpath(".//h4")).getText();
					
					if(expClientName.equalsIgnoreCase(resultClientName)){
						clientFound = true;
						break;
					}
				}
				
				if(!clientFound){
					sc.STAF_ReportEvent("Fail", "Order Sharing", "Searched client not found-"+expClientName, 1);
					throw new Exception("Searched client not found-"+expClientName);
					
				}
				
				clientList.get(i).findElement(By.xpath(".//button")).click();
				
				retval= Globals.KEYWORD_PASS;
			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail", "Order Sharing", "Unable to shre order due to exception.Exception-"+e.toString(), 1);
			throw e;
		}


		return retval;


	}
	
	/**
	 * Method which verifies the Expected Text with the text retrieved through getText method for the WebElement Name Passed with report.
	 * @param elementName 		- WebElement Name in the OR Properties file for which Text Needs to be verified using gettext.
	 * @param ExpectedText 		- Expected Text for WebElement retrieved using gettext.
	 * @param StepName		 	- Step name description which will pass in report event
	 * @author 					- Paresh Save
	 * @created 				- Sept 2016
	 * @LastModifiedBy 			- Paresh Save
	 * @LastModifiedDate 		- Sept 2016
	 */
		
	public static void verifyTextWithReport(String elementName, String ExpectedText, String StepNameDesc){
		APP_LOGGER.startFunction("verifyText - String");
		String retval 		= Globals.KEYWORD_FAIL;
		WebElement element	= null;
		
		try {
			element = sc.createWebElement(elementName);
			retval 	= sc.verifyText(element, ExpectedText);

			if(retval==Globals.KEYWORD_FAIL){
				
				sc.STAF_ReportEvent("Fail", StepNameDesc, "Text is not as per expected", 1);
				
			}
			else{
				sc.scrollIntoView(element);
				sc.STAF_ReportEvent("Pass", StepNameDesc, "Text is as per expected", 1);
			}
								
		}
		catch (Exception e){
			log.error("Element -"+ elementName + " is either Not Present in OR/Doesnt exists in UI | Exception - " + e.toString());
			retval 		= Globals.KEYWORD_FAIL;
		}
	}
	
    /**************************************************************************************************
    * Method to Verifying Custom Messaging on Order Step1 page
    * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
    * @author mshaik
    * @throws Exception
    ***************************************************************************************************/
    public static String verifyCustomMessagingOrderSTEP1() throws Exception{
                    
                     String retval = Globals.KEYWORD_FAIL;
                    String custommessage = ""; 
                     long  timeOutInSeconds =  30;
                    String tempretval=Globals.KEYWORD_FAIL;
                    custommessage = Globals.testSuiteXLS.getCellData_fromTestData("CustomizingMessageforeditorderstep1txt");
                    if(custommessage == null || custommessage.isEmpty() || custommessage.equals("")){
                                    return Globals.KEYWORD_FAIL;
                    }
                    try{
                                    String stepName = "Verify the Step1 headline text";
                                    tempretval=sc.waitforElementToDisplay("orderStep1_headline_txt", timeOutInSeconds);
                                    if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                                    sc.STAF_ReportEvent("Fail", stepName, "Step1 headline text is not loaded", 1);
                                    throw new Exception("Step1 headline text is not loaded");
                                    }
                    String actualHeadingTextStep1= driver.findElement(LocatorAccess.getLocator("orderStep1_headline_txt")).getText();
                    String accountName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName").toLowerCase();
                    String expectedHeadingTextStep1=custommessage.replace("{org_name}",accountName);
                    if(expectedHeadingTextStep1.equalsIgnoreCase(actualHeadingTextStep1)){
                                    sc.STAF_ReportEvent("Pass", "Header text order step1", "Header text order step1 is displayingas expected"+expectedHeadingTextStep1, 1);
                                    retval = Globals.KEYWORD_PASS;
                    }else{
                                    sc.STAF_ReportEvent("Fail", "Header text order step1", "Header text order step1 is not displayingas expected",1);
                    }

                                    } catch (Exception e) {
                                                    sc.STAF_ReportEvent("Fail", "Header text order step1", "Header text order step1 is not displaying as expected", 1);
                                                    throw e;
                                    }
                    
                                     return retval;
                    
     
     }

	
	/**************************************************************************************************
     * Method to navigate Abuse PA popup
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author mshaik
     * @throws Exception
     ***************************************************************************************************/
     
     
     public static void invitationStep3Abusepa() throws Exception{

                     //if ABUSE is in package, then ABUSE will only get added if PA is select as the address state
                     // if ABUSE-PA is configured in package then it will get added no matter what the address state is
                     APP_LOGGER.startFunction("invitationStep3Abusepa");
                     //String retval = Globals.KEYWORD_FAIL;
                     String tempRetval=Globals.KEYWORD_FAIL;
                     int timeOutInSeconds =20;
                     sc.waitForPageLoad();
                     tempRetval=sc.waitforElementToDisplay("invitationStep3abuse_pennsylvania_continue_btn", 60);
                     String ActualAbusestep3text =driver.findElement(LocatorAccess.getLocator("invitationStep3abuse_pennsylvania_continue_btn")).getText();
                     String ExpectedAbusestep3text="Please answer the following question";
                     if(ExpectedAbusestep3text.equalsIgnoreCase(ActualAbusestep3text)){                                                         
                     tempRetval=sc.waitforElementToDisplay("invitationStep3abuse_pennsylvania_continue_btn", timeOutInSeconds);
                     if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                    	 sc.STAF_ReportEvent("Fail", "Verifying the presence of the Abuse PA popup with Yes radio button", "Abuse Pa popup is displaying with the Yes and No radio buttons ", 1);
                    	 throw new Exception("Abuse Pa popup is not displaying with the yes radio button");
                     }
                     String response =   Globals.testSuiteXLS.getCellData_fromTestData("AbusePAResponse");
                     if(response.equalsIgnoreCase("yes")){
                    	 tempRetval = sc.waitforElementToDisplay("invitationStep3abuse_pennsylvania_continue_yesradio_btn", timeOutInSeconds);
                    	 driver.findElement(LocatorAccess.getLocator("invitationStep3abuse_pennsylvania_continue_yesradio_btn")).click();
                     }else{
                    	 //todo
                    	 tempRetval=sc.waitforElementToDisplay("invitationStep3abuse_pennsylvania_continue_Noradio_btn", timeOutInSeconds);
                    	 if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                    		 sc.STAF_ReportEvent("Fail", "Verifying the Yes and No  button on the Abuse pa popup", "Abuse Pa popup is NOT displaying with the Yes and No radio buttons", 1);
                             throw new Exception("Abuse Pa popup is displaying with the Yes and No radio buttons");
                    	 }
                    	 sc.clickWhenElementIsClickable("invitationStep3abuse_pennsylvania_continue_Noradio_btn", timeOutInSeconds);
      
                    	 }
                     sc.STAF_ReportEvent("Pass", "Verifying the Yes and No  button on the Abuse pa popup", "Abuse Pa popup Text is displayed as expectd and Response= "+ response, 1);
                     //driver.findElement(LocatorAccess.getLocator("invitationStep3abuse_pennsylvania_continue_yesradio_btn")).click();
                     tempRetval =sc.waitforElementToDisplay("invitationStep3abusepa_continue_btn", timeOutInSeconds);
                     if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                                     sc.STAF_ReportEvent("Fail", "Verifying the Continue button on the Abuse pa popup", "Continue Button is not displaying on the Abuse PA popup", 1);
                                     throw new Exception("Continue Button is not displaying on the Abuse PA popup");
                     }
                    // tempRetval =sc.waitforElementToDisplay("invitationStep3abusepa_continue_btn", timeOutInSeconds);
                    // if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                      //               sc.STAF_ReportEvent("Fail", "Verifying the Continue button on the Abuse pa popup", "Continue Button is not displaying on the Abuse PA popup", 1);
                        //             throw new Exception("Continue Button is not displaying on the Abuse PA popup");
                    // }
                     sc.clickWhenElementIsClickable("invitationStep3abusepa_continue_btn", timeOutInSeconds);
                     sc.waitForPageLoad();
                     }
   
 
     
                     
     }
     
     /**************************************************************************************************
      * Method to Volunteer Forgot password
      * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
      * @author mshaik
      * @throws Exception
      ***************************************************************************************************/
     public static String VolunteerForgotPassword() throws Exception {
 		APP_LOGGER.startFunction("VolunteerForgotPassword");
 		String retval=Globals.KEYWORD_FAIL;
 		String tempRetval=Globals.KEYWORD_FAIL;
 		int timeOutInSeconds = 20;
 		String username = null;
 		sc.launchURL(Globals.BrowserType,Globals.getEnvPropertyValue("AppURL"));
 		String volunteersecurityanswer1;
 		String volunteersecurityanswer2;
 		String stepName = "volunteer Forgot Password with Security Questions";
 		
 		try {
 			tempRetval = sc.waitforElementToDisplay("login_volunteerUsername_txt", timeOutInSeconds);
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 				sc.STAF_ReportEvent("Fail", stepName, "Volunteer Login page is not loaded", 1);
 				throw new Exception(stepName+ " - Volunteer Login page is not loaded");
 			}

 		tempRetval =  sc.waitforElementToDisplay("login_forgotVolunteerPwd_link", timeOutInSeconds );
 		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Volunteer Forgot Password link has not been loaded", 1);
			throw new Exception(stepName+ " - Volunteer Forgot Password link has not been loaded");
		}
			sc.clickWhenElementIsClickable("login_forgotClientPwd_link", timeOutInSeconds); //we are clicking on client forgot password link insted of vol forgot pass
			sc.STAF_ReportEvent("Pass", stepName, "Volunteer has been clicked Forgot password Link", 1);
 			tempRetval=sc.waitforElementToDisplay("voluneerAccCreation_Forgotpassword_Edit_txt", timeOutInSeconds);
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
			if(sc.testSuiteXLS.getCellData_fromTestData("VolunteerUsername_runtimeUpdateFlag").equalsIgnoreCase("No")){
				int length = 9;
				username = "vol" + sc.runtimeGeneratedStringValue(length);
				Globals.testSuiteXLS.setCellData_inTestData("VolunteerUsername", username);
				log.debug(" Method-volunteerAccountCreation | Volunteer Username Runtime generated and store.value- "+username);
			}else
				tempRetval=sc.waitforElementToDisplay("voluneerAccCreation_Forgotpassword_Edit_txt", timeOutInSeconds);
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Verify Voluntee User Name text field", "Forgot password page Volunteer User Name text box  has not been loaded", 1);
					throw new Exception(" - Forgot password page Volunteer User Name text box  has not been loaded");
				}
				//sc.clickWhenElementIsClickable(Globals.testSuiteXLS.getCellData_fromTestData("VolunteerUsername"), timeOutInSeconds);
				username = sc.testSuiteXLS.getCellData_fromTestData("VolunteerUsername");
				sc.setValueJsChange("voluneerAccCreation_Forgotpassword_Edit_txt", username);
				sc.STAF_ReportEvent("Pass", "Verify Voluntee User Name text field", "Volunteer has entered User Name sucessful for Resetting the password", 1);
				
				tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_Username_Submit_btn", timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", stepName, "On Forgot password page Submit Button has not been loaded", 1);
				throw new Exception(stepName+ " - On Forgot password page Submit Button has not been loaded");
				}
				sc.clickWhenElementIsClickable("volunteerAccCreation_Username_Submit_btn", timeOutInSeconds);
				sc.STAF_ReportEvent("Pass", stepName, "On Forgot password page Volunteer has been clicked submit button sucessfull", 1);
				volunteersecurityanswer1 = sc.testSuiteXLS.getCellData_fromTestData("SecurityAnswer1").toUpperCase();
				
				tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_Forgotpassword_Response1_txt", timeOutInSeconds);
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", stepName, "Forgot password page volunteer Response1 field is not loaded", 1);
					throw new Exception(stepName+ " - Forgot password page volunteer Response1 field is not loaded");	
				}
				tempRetval=sc.setValueJsChange("volunteerAccCreation_Forgotpassword_Response1_txt",volunteersecurityanswer1);	
				sc.STAF_ReportEvent("Pass", stepName, "Forgot password page volunteer has been entered Response1 field sucessfull", 1);
				volunteersecurityanswer2 = sc.testSuiteXLS.getCellData_fromTestData("SecurityAnswer2").toLowerCase();
				
				tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_Forgotpassword_Response2_txt", timeOutInSeconds);
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", stepName, "Forgot password page volunteer Response2 field is not loaded", 1);
					throw new Exception(stepName+ " - Forgot password page volunteer Response2 field is not loaded");
				}
				tempRetval=sc.setValueJsChange("volunteerAccCreation_Forgotpassword_Response2_txt",volunteersecurityanswer2);	
				sc.STAF_ReportEvent("Pass", stepName, "Forgot password page volunteer has been entered Response2 field sucessfull", 1);
				tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_Resetpassword_button", timeOutInSeconds);
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", stepName, "Forgot password page volunteer Reset password button is not loaded", 1);
					throw new Exception(stepName+ " - Forgot password page volunteer Reset password button is not loaded");
					
				}
				
				sc.clickWhenElementIsClickable("volunteerAccCreation_Resetpassword_button", timeOutInSeconds);
				sc.STAF_ReportEvent("Pass", stepName, "Forgot password page volunteer has been Clicked Reset Password button sucessfull", 1);
				//Below is the code for Resetting the password 
				
				String oldpassword = sc.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
				sc.setValueJsChange("passwordReset_oldPassword_txt", oldpassword);
				String newConfirmPwd="123qa123!";
				sc.setValueJsChange("passwordReset_newPassword_txt", newConfirmPwd);
				sc.setValueJsChange("passwordReset_confirmPassword_txt", newConfirmPwd);
				sc.clickWhenElementIsClickable("passwordReset_changePassword_btn",timeOutInSeconds);
				tempRetval = sc.waitforElementToDisplay("passwordReset_successMsg_span", timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", stepName, "Unable to change password.New Pwd-"+newConfirmPwd, 1);
					throw new Exception(stepName+ " - Unable to change password");
				}else{
					sc.STAF_ReportEvent("Pass", stepName, "Password change successful.New Pwd-"+newConfirmPwd, 1);
				}
				
					}
				} catch (Exception e){
					sc.STAF_ReportEvent("Fail",stepName, "Volunteer has unable to reset his password with security questions and answers", 1);
		              throw e;
			}
 		  return retval;
     }

     /**************************************************************************************************
 	 * Method to verify edited position in BG Dasboard
 	 * 
 	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL
 	 *         otherwise
 	 * @author vgokulanathan
 	 * @throws Exception
 	 ***************************************************************************************************/
 	public static int verifyEditedPositionBG() throws Exception {
 		APP_LOGGER.startFunction("verifyEditedPosition");
 		int retval = Globals.INT_FAIL;
 		long timeOutinSeconds = 20;
 		String positionName = "";
 		String volFName;

 		try {
 			if (sc.waitforElementToDisplay("bgDashboard_search_txt", 60)
 					.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
 				sc.STAF_ReportEvent("Pass", "Search Volunteer",
 						"BG Dashboard page has loaded", 1);

 				// WebElement ddMenu =
 				// sc.createWebElement("volDashboard_searchBy_dd");

 				WebElement searchVal = sc
 						.createWebElement("bgDashboard_search_txt");

 				positionName = Globals.testSuiteXLS
 						.getCellData_fromTestData("PositionName")
 						;
 				volFName = Globals.testSuiteXLS
 						.getCellData_fromTestData("volunteerFName");

 				// sc.selectValue_byVisibleText(ddMenu, "Position");
 				sc.setValueJsChange(searchVal, volFName);

 				sc.clickWhenElementIsClickable(
 						"bgDashboard_searchBtn_btn", (int) timeOutinSeconds);
 				Thread.sleep(5000);

 				String positionNameInUI;

 				boolean positionFound = false;
 				int i = 0;
 				if (sc.waitforElementToDisplay(
 						"bgDashboard_searchResults_tbl", 60).equalsIgnoreCase(
 						Globals.KEYWORD_PASS)) {
 					WebElement volGrid = sc
 							.createWebElement("bgDashboard_searchResults_tbl");
 					int rowCount = sc.getRowCount_tbl(volGrid);
 					if (rowCount != Globals.INT_FAIL) {
 						log
 								.info("Method searchPosition | BG Dashboard grid displayed with "
 										+ rowCount + " position(s)");
 						if (rowCount == 1) {
 							positionNameInUI = sc.getCellData_tbl(
 									volGrid, 1, 1);
 							if (positionNameInUI
 									.contains("No matching records")) {
 								positionFound = false;
 							} else {
 								positionNameInUI = sc.getCellData_tbl(
 										volGrid, 1, 4);

 								if (positionNameInUI
 										.equalsIgnoreCase(positionName)) {
 									positionFound = true;
 									i = 1;
 									sc.STAF_ReportEvent(
 											"Pass",
 											"BG Dashboard Page",
 											"Edited position name is displayed in BG Dashboard Page",
 											1);
 								}
 							}
 						} else {
 							for (i = 1; i <= rowCount; i++) {
 								// TODO - need to fetch the column position
 								// dynamically.
 								positionNameInUI = sc.getCellData_tbl(
 										volGrid, i, 4);
 								if (positionNameInUI
 										.equalsIgnoreCase(positionName)) {
 									positionFound = true;
 									sc.STAF_ReportEvent(
 											"Pass",
 											"BG Dashboard Page",
 											"Edited position name is displayed in BG Dashboard Page",
 											1);
 									break;
 								}

 							}
 						}

 					}

 					if (positionFound == true) {
 						return i; // row number of the account found
 					}

 				} else {
 					log
 							.info("Method searchPosition | Position in BG Dashboard is not displayed| Position Name:- "
 									+ positionName);

 				}
 			} else {
 				sc.STAF_ReportEvent("Fail", "BG Dashboard Page",
 						"BG Dashboard Search has NOT been loaded", 1);
 				log
 						.error("Unable to Search Position as BG Dashboard Search field is not displayed");
 			}

 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			log.error("BG Dashboard-searchPosition | Position Name - "
 					+ positionName + "|  Exception occurred - " + e.toString());
 			throw e;
 		}

 		return retval;
 	}

 	/**************************************************************************************************
 	 * Method to verify edited position in Volunteer DashBoard Page and
 	 * Volunteer Profile Page
 	 * 
 	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL
 	 *         otherwise
 	 * @author vgokulanathan
 	 * @throws Exception
 	 ***************************************************************************************************/
 	public static int verifyEditedPositionVolunteerDashBoard() throws Exception {
 		APP_LOGGER.startFunction("verifyEditedPosition");
 		int retval = Globals.INT_FAIL;
 		String tempretval = Globals.KEYWORD_FAIL;
 		int timeOutinSeconds = 20;
 		String positionName = "";

 		try {
 			if (sc
 					.waitforElementToDisplay("volDashboard_searchBy_dd", 60)
 					.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
 				sc.STAF_ReportEvent("Pass", "Search Volunteer",
 						"Volunteer Dashboard page has loaded", 1);

 				WebElement ddMenu = sc
 						.createWebElement("volDashboard_searchBy_dd");

 				WebElement searchVal = sc
 						.createWebElement("volDashboard_searhValue_txt");

 				positionName = Globals.testSuiteXLS
 						.getCellData_fromTestData("PositionName")
 						;

 				sc.selectValue_byVisibleText(ddMenu, "Position");
 				sc.setValueJsChange(searchVal, positionName);

 				sc.clickWhenElementIsClickable("volDashboard_search_btn",
 						timeOutinSeconds);
 				Thread.sleep(5000);

 				String positionNameInUI;

 				boolean positionFound = false;
 				int i = 0;
 				if (sc.waitforElementToDisplay(
 						"volDashboard_volgrid_tbl", 60).equalsIgnoreCase(
 						Globals.KEYWORD_PASS)) {
 					WebElement volGrid = sc
 							.createWebElement("volDashboard_volgrid_tbl");
 					int rowCount = sc.getRowCount_tbl(volGrid);
 					if (rowCount != Globals.INT_FAIL) {
 						log
 								.info("Method searchPosition | Volunteer Dashboard grid displayed with "
 										+ rowCount + " position(s)");
 						if (rowCount == 1) {
 							positionNameInUI = sc.getCellData_tbl(
 									volGrid, 1, 1);
 							if (positionNameInUI
 									.contains("No matching records")) {
 								positionFound = false;
 							} else {
 								positionNameInUI = sc.getCellData_tbl(
 										volGrid, 1, 4);

 								if (positionNameInUI
 										.equalsIgnoreCase(positionName)) {
 									positionFound = true;
 									i = 1;
 									sc.STAF_ReportEvent(
 											"Pass",
 											"Volunteer Dashboard Page",
 											"Edited position name is displayed in Volunteer Dashboard Page",
 											1);
 								}

 								volGrid.findElement(
 										By.xpath("./tbody/tr[1]/td[2]/a"))
 										.click();
 								tempretval = sc.waitforElementToDisplay(
 										"volDashboard_volunteerprile_txt",
 										timeOutinSeconds);
 								if (tempretval
 										.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
 									WebElement position = sc
 											.createWebElement("volunteerProfile_position_div");
 									String expText = Globals.testSuiteXLS
 											.getCellData_fromTestData("PositionName")
 											;
 									if (position.getText().equalsIgnoreCase(
 											expText)) {
 										sc.STAF_ReportEvent("Pass",
 												"Volunteer Profile Page",
 												"Edited position name is getting displayed-"
 														+ expText, 1);
 									} else {
 										sc.STAF_ReportEvent(
 												"Fail",
 												"Volunteer Profile Page",
 												"Edited position name is not getting displayed-",
 												1);
 									}
 									sc.clickWhenElementIsClickable(
 											"volunteerProfile_close_btn",
 											timeOutinSeconds);
 								} else {
 									sc.STAF_ReportEvent(
 											"Fail",
 											"Volunteer Profile",
 											"Volunteer Dashboard - Volunteer Profile page has not loaded",
 											1);
 									throw new Exception(
 											"Volunteer Dashboard - Volunteer Profile page has not loaded");
 								}
 							}
 						} else {
 							for (i = 1; i <= rowCount; i++) {
 								// TODO - need to fetch the column position
 								// dynamically.
 								positionNameInUI = sc.getCellData_tbl(
 										volGrid, i, 4);
 								if (positionNameInUI
 										.equalsIgnoreCase(positionName)) {
 									positionFound = true;
 									sc.STAF_ReportEvent(
 											"Pass",
 											"Volunteer Dashboard Page",
 											"Edited position name is displayed in Volunteer Dashboard Page",
 											1);
 								}
 								volGrid.findElement(
 										By.xpath("./tbody/tr[1]/td[2]/a"))
 										.click();
 								tempretval = sc.waitforElementToDisplay(
 										"volDashboard_volunteerprile_txt",
 										timeOutinSeconds);
 								if (tempretval
 										.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
 									WebElement position = sc
 											.createWebElement("volunteerProfile_position_div");
 									String expText = Globals.testSuiteXLS
 											.getCellData_fromTestData("PositionName")
 											;
 									if (position.getText().equalsIgnoreCase(
 											expText)) {
 										sc.STAF_ReportEvent("Pass",
 												"Volunteer Profile Page",
 												"Edited position name is getting displayed-"
 														+ expText, 1);
 									} else {
 										sc.STAF_ReportEvent(
 												"Fail",
 												"Volunteer Profile Page",
 												"Edited position name is not getting displayed-",
 												1);
 									}
 									sc.clickWhenElementIsClickable(
 											"volunteerProfile_close_btn",
 											timeOutinSeconds);
 								} else {
 									sc.STAF_ReportEvent(
 											"Fail",
 											"Volunteer Profile",
 											"Volunteer Dashboard - Volunteer Profile page has not loaded",
 											1);
 									throw new Exception(
 											"Volunteer Dashboard - Volunteer Profile page has not loaded");
 								}
 							}
 						}

 					}

 					if (positionFound == true) {
 						return i; // row number of the account found
 					}

 				} else {
 					log
 							.info("Method searchPosition | Position in Volunteer Dashboard is not displayed| Position Name:- "
 									+ positionName);

 				}
 			} else {
 				sc.STAF_ReportEvent("Fail", "Volunteer Dashboard Page",
 						"Volunteer Dashboard Search has NOT been loaded", 1);
 				log
 						.error("Unable to Search Position as Volunteer Dashboard Search field is not displayed");
 			}

 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			log
 					.error("Volunteer Dashboard-searchPosition | Position Name - "
 							+ positionName
 							+ "|  Exception occurred - "
 							+ e.toString());
 			throw e;
 		}

 		return retval;
 	}

 	/**********************************************************************************************************************************************
 	 * Method to verify edited position is reflecting in Send Invitations,
 	 * Renewal Invitation, Client Ordering and Batch Ordering Position dropdown
 	 * 
 	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL
 	 *         otherwise
 	 * @author aunnikrishnan
 	 * @throws Exception
 	 **********************************************************************************************************************************************/
 	public static String verifyEditedPositionOrdering() {
 		Globals.Component = Thread.currentThread().getStackTrace()[1]
 				.getMethodName();
 		APP_LOGGER.startFunction(Globals.Component);
 		String retval = Globals.KEYWORD_FAIL;
 		int timeOutinSeconds = 40;
 		String tempRetval = Globals.KEYWORD_FAIL;
 		String positionName = Globals.testSuiteXLS
 				.getCellData_fromTestData("PositionName")
 				+ " - "
 				+ Globals.testSuiteXLS.getCellData_fromTestData("PricingName");
 		try {

 			tempRetval = sc.waitforElementToDisplay(
 					"volDashboard_communications_btn", timeOutinSeconds);
 			sc.clickWhenElementIsClickable(
 					"volDashboard_communications_btn", timeOutinSeconds);
 			sc.clickWhenElementIsClickable(
 					"volDashboard_sendInvitation_btn", timeOutinSeconds);

 			tempRetval = sc.waitforElementToDisplay(
 					"sendInvitation_postionListing_dd", timeOutinSeconds);
 			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
 				if (positionName.contains("Client Pays")) {
 					String arr0 = positionName.split("Client Pays")[0];
 					String arr1 = positionName.split("Client Pays")[1];
 					String arrf1 = arr0 + "Client Pays";
 					String arrf2 = arr1.trim();
 					Select sel = new Select(driver.findElement(LocatorAccess
 							.getLocator("sendInvitation_postionListing_dd")));
 					List<WebElement> list = sel.getOptions();
 					boolean editedPositionFound = false;
 					for (WebElement option : list) {
 						if (option.getText().contains(arrf1)
 								&& option.getText().contains(arrf2)) {
 							sel.selectByVisibleText(option.getText());
 							if (option.getText().equalsIgnoreCase(positionName)) {
 								editedPositionFound = true;
 								break;
 							}
 						}
 					}
 					if (editedPositionFound == true) {
 						sc.STAF_ReportEvent("Pass",
 								"Send Invitations Page",
 								"Edited position name is getting displayed", 1);
 					} else {
 						sc.STAF_ReportEvent(
 								"Fail",
 								"Send Invitations Page",
 								"Edited position name is not getting displayed",
 								1);
 					}
 				} else {
 					tempRetval = sc.selectValue_byVisibleText(
 							"sendInvitation_postionListing_dd", positionName);
 				}
 			} else {
 				sc.STAF_ReportEvent("Fail", "Send Invitations Page",
 						"Edited position name is not getting displayed", 1);
 			}
 			sc.clickWhenElementIsClickable("sendInvitation_close_btn",
 					timeOutinSeconds);
 			WebElement volGrid = sc
 					.createWebElement("volDashboard_volgrid_tbl");
 			// sc.checkCheckBox(volGrid.findElement(By.xpath("./tbody/tr[1]/td[1]/a")));
 			sc.checkCheckBox(volGrid.findElement((By
 					.xpath("./tbody/tr[1]/td[1]/input"))));
 			sc.clickWhenElementIsClickable(
 					"volDashboard_communications_btn", timeOutinSeconds);
 			sc.clickWhenElementIsClickable(
 					"volDashboard_sendRenewalInvitation_btn", timeOutinSeconds);
 			tempRetval = sc.waitforElementToDisplay(
 					"renewalInvitation_postionListing_dd", timeOutinSeconds);
 			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
 				if (positionName.contains("Client Pays")) {
 					String arr0 = positionName.split("Client Pays")[0];
 					String arr1 = positionName.split("Client Pays")[1];
 					String arrf1 = arr0 + "Client Pays";
 					String arrf2 = arr1.trim();
 					Select sel = new Select(driver.findElement(LocatorAccess
 							.getLocator("renewalInvitation_postionListing_dd")));
 					List<WebElement> list = sel.getOptions();
 					boolean editedPositionFound = false;
 					for (WebElement option : list) {
 						if (option.getText().contains(arrf1)
 								&& option.getText().contains(arrf2)) {
 							sel.selectByVisibleText(option.getText());
 							if (option.getText().equalsIgnoreCase(positionName)) {
 								editedPositionFound = true;
 								break;
 							}
 						}
 					}
 					if (editedPositionFound == true) {
 						sc.STAF_ReportEvent("Pass",
 								"Renewal Invitations Page",
 								"Edited position name is getting displayed", 1);
 					} else {
 						sc.STAF_ReportEvent(
 								"Fail",
 								"Renewal Invitations Page",
 								"Edited position name is not getting displayed",
 								1);
 					}
 				} else {
 					tempRetval = sc
 							.selectValue_byVisibleText(
 									"renewalInvitation_postionListing_dd",
 									positionName);
 				}
 			} else {
 				sc.STAF_ReportEvent("Fail", "Renewal Invitations Page",
 						"Edited position name is not getting displayed", 1);
 			}
 			sc.clickWhenElementIsClickable("renewalInvitation_close_btn",
 					timeOutinSeconds);
 			sc.uncheckCheckBox(volGrid.findElement((By
 					.xpath("./tbody/tr[1]/td[1]/input"))));
 			clickOnCreateClientOrder();
 			tempRetval = sc.waitforElementToDisplay(
 					"clientStep1_choosePosition_dd", timeOutinSeconds);
 			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
 				Select sel = new Select(driver.findElement(LocatorAccess
 						.getLocator("clientStep1_choosePosition_dd")));
 				List<WebElement> list = sel.getOptions();

 				boolean editedPositionFound = false;
 				for (WebElement option : list) {
 					if (option.getText().contains("EditedPosition")) {
 						editedPositionFound = true;
 						break;
 					}
 				}
 				if (editedPositionFound == true) {
 					sc.STAF_ReportEvent("Pass", "Client order Page",
 							"Edited position name is getting displayed", 1);
 				} else {
 					sc.STAF_ReportEvent("Fail", "Client order Page",
 							"Edited position name is not getting displayed", 1);
 				}
 			} else {
 				sc.STAF_ReportEvent("Fail", "Client order Page",
 						"Edited position name is not getting displayed", 1);
 			}
 			sc.clickWhenElementIsClickable(
 					"clientBGDashboard_manageMyVolunteer_link",timeOutinSeconds);
 			Alert alert = driver.switchTo().alert();
 			alert.accept();
 			sc.waitforElementToDisplay(
 					"volDashboard_orderBackgroundCheck_btn", timeOutinSeconds);
 			sc.clickWhenElementIsClickable(
 					"volDashboard_orderBackgroundCheck_btn", timeOutinSeconds);
 			sc.clickWhenElementIsClickable(
 					"volDashboard_placeABatchOrder_btn", timeOutinSeconds);
 			tempRetval = sc.waitforElementToDisplay(
 					"clientStep1_choosePosition_dd", timeOutinSeconds);
 			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
 				Select sel = new Select(driver.findElement(LocatorAccess
 						.getLocator("clientStep1_choosePosition_dd")));
 				List<WebElement> list = sel.getOptions();
 				boolean editedPositionFound = false;
 				for (WebElement option : list) {
 					if (option.getText().contains("EditedPosition")) {
 						editedPositionFound = true;
 						break;
 					}
 				}
 				if (editedPositionFound == true) {
 					sc.STAF_ReportEvent("Pass", "Batch order Page",
 							"Edited position name is getting displayed", 1);
 				} else {
 					sc.STAF_ReportEvent("Fail", "Batch order Page",
 							"Edited position name is not getting displayed", 1);
 				}
 			} else {
 				sc.STAF_ReportEvent("Fail", "Batch order Page",
 						"Edited position name is not getting displayed", 1);
 			}

 		} catch (Exception e) {
 			retval = Globals.KEYWORD_FAIL;
 			log.error("Method-sendInvitationToApplicant | Exception - "
 					+ e.toString());
 		}
 		return retval;
 	}
 	/**************************************************************************************************
     * Method to verify report header custom fields for BG Reports
     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
     * @author Navisha
     * @throws Exception
     ***************************************************************************************************/
    public static String bgReport_verifyReportHeaderCustomFields_OrgUser(String elementName,int i) throws Exception{

    	APP_LOGGER.startFunction("bgReport_verifyReportHeaderCustomFields_OrgUser");
        String retval=Globals.KEYWORD_FAIL;

        String customBGReportSetting = Globals.testSuiteXLS.getCellData_fromTestData("ShowClientConfigOnReportcheckbox");
        String sharingType = Globals.testSuiteXLS.getCellData_fromTestData("SharingType");
        String[] indvCustomFieldSetting =  customBGReportSetting.split(";");
       
       WebElement element=null;
       String customFieldchkValue=Globals.testSuiteXLS.getCellData_fromTestData("TestCaseDescr");
		if(!(customFieldchkValue.contains("L1 sharing Order with L3-Order Creation"))){
			String noOfCustomFieldsReq= Globals.testSuiteXLS.getCellData_fromTestData("NoOfCustomFieldsReq");
			int noOfCustomFieldsReqvalue= Integer.parseInt(noOfCustomFieldsReq);
			if(indvCustomFieldSetting.length==3){
				if(indvCustomFieldSetting[i-1].equalsIgnoreCase("Yes") && noOfCustomFieldsReqvalue!=0){
            
					element = sc.createWebElement(elementName);
					
					String CustomField_Displayed = Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i+"_Displayed");
       
					String[] customFieldsplit = Globals.testSuiteXLS.getCellData_fromTestData("CustomFieldAnswers").split(";");
					String expText="";
	            if(CustomField_Displayed.equalsIgnoreCase("No")||sharingType.equalsIgnoreCase(("HighToLowsharing"))){
	            	expText=Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i)+": ";
	            }    
	            else{
	            	expText=Globals.testSuiteXLS.getCellData_fromTestData("CustomField"+i)+": "+customFieldsplit[i-1];
	            }
        
        
	            if(element.getText().trim().equalsIgnoreCase(expText.trim())){
	            	sc.scrollIntoView(element);
	            	sc.STAF_ReportEvent("Pass", "BG Report", "Custom Field value"+ i +" is as Expected.Val-"+expText, 1);
	            	retval = Globals.KEYWORD_PASS;
	            }    
	            else{
	            sc.STAF_ReportEvent("Fail", "BG Report", "Custom Field value"+ i +" is not as Expected.Val-"+expText, 1);
	            }
			}
    	   }else{
           sc.STAF_ReportEvent("Pass", "BG Report", "Custom Field value"+ i +" display settings on ShowClientConfigOnReportcheckbox is off", 1);
           retval = Globals.KEYWORD_PASS;
    	   }
		}
       return retval;
    }
    
    /**************************************************************************************************
	 * Method to verify  gui validations for rating restrictions in Volunteer Dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyRatingRestriction() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval = Globals.KEYWORD_FAIL;
 		int timeOutinSeconds = 20;
 		try{
 			showColumn();
 			String reportingStepName1="Verify the Search Criteria for volunteer Restriction on the volunteer dashboard page.";
 			retval = searchVolunteer("Restrictions","Test","Restrictions","Test", reportingStepName1);
 			
 			String order = Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
 			String reportingStepName="Verify the  Restriction value appearing on the volunteer dashboard page.";
 			retval = searchVolunteer("VV Order",order,"Restrictions","Test", reportingStepName);
 			if (retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 		    String color = driver.findElement(By.xpath("//*[@id='dashboard']/tbody/tr/td[13]/span")).getAttribute("style");//color attribute
 			if (color.contains("rgb(210, 210, 0)")){
 				sc.STAF_ReportEvent("Pass", "Rating Restrictions", "Correct colour code for rating restrictions is displayed on bg dashboard", 1);
 			}
 			else{
 				sc.STAF_ReportEvent("Fail", "Rating Restrictions", "Wrong colour code for rating restrictions is displayed on bg dashboard", 1);
 			}
 			
 			String statusDashboard = driver.findElement(By.xpath("//*[@id='dashboard']/tbody/tr/td[14]/span")).getText();
 			if (statusDashboard.equalsIgnoreCase("Eligible")){
 				sc.STAF_ReportEvent("Pass", "Rating Restrictions", "with status Eligible is displayed on volunteer dashboard", 1);
 			}
 			else{
 				sc.STAF_ReportEvent("Fail", "Rating Restrictions", "Wrong status for rating restrictions is displayed on volunteer dashboard", 1);
 			}
 			
 			WebElement volGrid = sc.createWebElement("volDashboard_volgrid_tbl");
 			volGrid.findElement(By.xpath("./tbody/tr[1]/td[2]/a")).click();
 			tempretval = sc.waitforElementToDisplay("volDashboard_volunteerprile_txt",timeOutinSeconds);
 				if (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
 					String status = driver.findElement(By.xpath("//*[@id='profileCommunications']/table/tbody/tr[2]/td[1]")).getText();
 					String expText = "Eligible with Restriction";
 					if (status.equalsIgnoreCase(expText)) {
 						sc.STAF_ReportEvent("Pass","Volunteer Profile Page","Correct status is getting displayed in Activity log-"+ expText, 1);
 					} else {
 						sc.STAF_ReportEvent("Fail","Volunteer Profile Page","Correct status name is not getting displayed in Activity log",1);
 					}
 					
 					WebElement header = sc.createWebElement("RatingRestrictions_restrictions_header");
 					String expheader = "Restrictions";
 					if(header.getText().equalsIgnoreCase(expheader)){
 						sc.STAF_ReportEvent("Pass", "Rating Restrictions", "Restrictions Header is as Expected.Val-"+expheader, 1);
 					}else{
 						sc.STAF_ReportEvent("Fail", "Rating Restrictions", "Restrictions Header is not as expected.", 1);
 					}
 					
 					WebElement restrictionsGrid = sc.createWebElement("RatingRestriction_restrictionGrid_tbl");
 					
 					//verification of table headers
 					List<WebElement> headerList = restrictionsGrid.findElements(By.xpath("./thead/tr/th"));

 					String[] expectedHeaders = {"Restrict","Review","Removed","Note"};
 					String headerText;
 					int i=0;

 					for(WebElement element : headerList){
 						headerText = element.getText();
 						if(headerText.equalsIgnoreCase(expectedHeaders[i])){

 							sc.STAF_ReportEvent("Pass", "To Verify Restrict  Column on Volunteer Profile", "Restriction Header is present in UI. Value  -"+headerText, 1);
 						}else{
 							sc.STAF_ReportEvent("Fail", "To Verify Restrict  Column on Volunteer Profile", "Mismatch in Restriction Header.Expected = "+expectedHeaders[i] + " Actual = "+headerText, 1);
 						}
 						i++;
 					}
 					
 					List<WebElement> valueList = restrictionsGrid.findElements(By.xpath("./tbody/tr/td"));
 					String Restrict= Globals.testSuiteXLS.getCellData_fromTestData("RestrictionValue");
 					String Note= "RestrictionNote";
 					String Enddate ="Invalid Date";
 					String[] expectedvalues = {Restrict,Enddate,"Active",Note};
 					int j=0;
 					String valueText;
 					for(WebElement element : valueList){
 						valueText = element.getText();
 						if(valueText.equalsIgnoreCase(expectedvalues[j])){

 							sc.STAF_ReportEvent("Pass", "To Verify Restrict  Column on Volunteer Profile", "Restriction value is present in UI. Value  -"+valueText, 1);
 						}else{
 							sc.STAF_ReportEvent("Fail", "To Verify Restrict  Column on Volunteer Profile", "Mismatch in Restriction value.Expected = "+expectedHeaders[j] + " Actual = "+valueText, 1);
 						}
 						j++;
 					}
 					String advActionEnabled= Globals.testSuiteXLS.getCellData_fromTestData("AdverseActionEnabled");
 					if(advActionEnabled.equalsIgnoreCase("No")){
 						tempretval = sc.waitforElementToDisplay("RatingRestrictions_remove_btn",timeOutinSeconds);
 						if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 							sc.STAF_ReportEvent("Fail","Rating Restrictions", "Remove Active Restriction link is not displayed", 1);
 							return Globals.KEYWORD_FAIL;
 						}else{
 							sc.clickWhenElementIsClickable("RatingRestrictions_remove_btn", timeOutinSeconds);
 							WebElement restrictionsGrid1 = sc.createWebElement("RatingRestriction_restrictionGrid_tbl");
 							List<WebElement> valueList1 = restrictionsGrid1.findElements(By.xpath("./tbody/tr/td"));
 							Date date = new Date();
 						    String DATE_FORMAT = "MM/dd/yy";
 						    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
 						    String removedDate = sdf.format(date);
 							String[] expectedvalue = {Restrict,Enddate,removedDate,Note};
 							int k=0;
 							
 							for(WebElement element : valueList1){
 								valueText = element.getText();
 								if(valueText.equalsIgnoreCase(expectedvalue[k])){

 									sc.STAF_ReportEvent("Pass", "To Verify Restrict  Column on Volunteer Profile", "Restriction value is present in UI. Value  -"+valueText, 1);
 								}else{
 									sc.STAF_ReportEvent("Fail", "To Verify Restrict  Column on Volunteer Profile", "Mismatch in Restriction value.Expected = "+expectedvalue[k] + " Actual = "+valueText, 1);
 								}
 								k++;
 							}
 							
 						}
 					}
 					
 					
 				}
 				sc.clickWhenElementIsClickable("volunteerProfile_close_btn",timeOutinSeconds);
				String reportingStepName2="Verify the  Removed Restriction value is not appearing on the volunteer dashboard page.";
				retval = searchVolunteer("Restrict",order,"Restrictions","", reportingStepName2);
 					
 			}
 		}catch(Exception e){
 			e.printStackTrace();
 			throw e ;
 		}
		
			
		
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify bg Report for Order Hold before submit
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String orderHoldBGReportBeforeSubmit() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutInSeconds = 60;

		try{
			tempRetval = sc.waitforElementToDisplay("bgReport_orderhold_list",timeOutInSeconds);

			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Order Hold BG", "BG Report for Order Hold has not been loaded", 1);
				return Globals.KEYWORD_FAIL;
			}

			String expectdText = "This order has not been submitted yet. Please review the background check order and then select submit or cancel from the review menu above to proceed.";
			String TextUI = driver.findElement(LocatorAccess.getLocator("bgReport_orderhold_list")).getText().trim();
	        
			if(TextUI.equalsIgnoreCase(expectdText)){
				sc.STAF_ReportEvent("Pass", "Order Hold BG", "Header Text displayed as expected - "+expectdText,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "Order Hold BG", "Mismatch in Header Text displayed.Expected - "+expectdText, 1);
			}
			
			String volFname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
			String volLname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
			String volMidname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");
			String OrderID =Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
			String Position = Globals.testSuiteXLS.getCellData_fromTestData("PositionName");
//			Calendar c=Calendar.getInstance();
//			int mYear=c.get(Calendar.YEAR);
//	        int mDay=c.get(Calendar.DAY_OF_MONTH);
//	        String Day= Integer.toString(mDay);
//	        String Year = Integer.toString(mYear);
//	        String Month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
//	        String Date =Month+" "+Day+", "+Year;
			SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, YYYY");
            Date today = new Date();
            String Date = formatter.format(today);
			String expectdText1 = "Candidate: "+volFname+" "+volMidname+" "+volLname+"\nOrder ID: "+OrderID+"\nDate Created: "+Date+"\nPosition: "+Position;
			String expectdText2 = "Candidate: "+volFname+" "+volMidname+" "+volLname+"\nOrder ID: "+OrderID+"Date Created: "+Date+"\nPosition: "+Position;
			String TextUI2 = driver.findElement(LocatorAccess.getLocator("bgReport_orderholdvolunteerinfo_list")).getText();
			String[] split = TextUI2.split("Your Background Check Order");
			String TextUI1 = split[0].trim();
			if(TextUI1.equalsIgnoreCase(expectdText1) || TextUI1.equalsIgnoreCase(expectdText2)){
				sc.STAF_ReportEvent("Pass", "Order Hold BG", "VolunteerInfo Text displayed as expected - "+expectdText1,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "Order Hold BG", "Mismatch in Volunteer Info Text displayed.Expected - "+expectdText1, 1);
			}
			verifyPricingBGOrderHold();
			//verifyProdListClientOrdering();
			retval = orderHoldReview();

		}catch(Exception e){
			log.error("Method-orderHoldBGReportBeforeSubmit | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	 }
	/**************************************************************************************************
	 * Method to verify pricing in BG Order Hold
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void verifyPricingBGOrderHold() throws Exception{
		//		int rowCount = 0;
		int rowCountGrid_2;
		float totalCost1= 0 ;
		double totalCost= 0 ;
		double Cost= 0 ;
		String cellValue,priceInUI,amount;
		double subTotal=0;

		WebElement elementTable = sc.createWebElement("clientStep4_priceGrid_tbl");
		//		rowCount = sc.getRowCount_tbl(elementTable);

		//		Need to sync as sometimes this grid takes a lot of time to load
		int timeOutInSeconds = 60;
		String syncLabel = null;
		String syncLocator = "//table[@id='draftOrder']/tbody[2]/tr[3]/td[2]";
		Pattern p = Pattern.compile("([0-9])");
		Matcher m = null;
		int counter =0;
		boolean priceFound = false;
		double expectedTotalPrice =0.00;
        expectedTotalPrice = fetchExpectedTotalPriceSTEP4();
		while(counter <= timeOutInSeconds  ){
			if(priceFound == false && expectedTotalPrice!=0.00){
			Thread.sleep(1000);
			syncLabel = driver.findElement(By.xpath(syncLocator)).getText();
			counter++;

			m = p.matcher(syncLabel);
				if(m.find()){
					priceFound = true;
					break;
				}
			}
			counter++;
		}
		if (priceFound == false && expectedTotalPrice!=0.00){
			sc.STAF_ReportEvent("Fail", "Pricing Validations","Price table has not loaded properly",1);
			throw new Exception("Pricing table didnt load");
		}

		List<WebElement> rows = elementTable.findElements(By.xpath("./tbody[2]/tr"));
		rowCountGrid_2 = rows.size();

		for(int i = 3;i<=rowCountGrid_2;i++){
			cellValue = elementTable.findElement(By.xpath("./tbody[2]/tr["+i+"]/td[1]")).getText();
			if(cellValue.equalsIgnoreCase("") || cellValue.isEmpty()){
				continue;
			}
			priceInUI = elementTable.findElement(By.xpath("./tbody[2]/tr["+i+"]/td[2]")).getText();
			amount = elementTable.findElement(By.xpath("./tbody[2]/tr["+i+"]/td[2]")).getText();

			if (cellValue.contains("Amount Paid by")){
                String price = (priceInUI.split("\\$")[1]);
				totalCost = Double.parseDouble(price.replace(")",""));
				
			}
			if (amount.equalsIgnoreCase("Amount paid by me")){
				Cost = Double.parseDouble(priceInUI.split("\\$")[1]);
				String Holdamt = new Double(Cost).toString();
				if(amount.equalsIgnoreCase("Amount paid by me")&& Holdamt.equals("0.0")){
					sc.STAF_ReportEvent("Pass", "Order Hold BG", " Amount paid by me displayed as expected - "+amount+Holdamt,1);
				}else{	
					sc.STAF_ReportEvent("Fail", "Order Hold BG", "Mismatch in Amount paid by me displayed.Expected - "+amount+Holdamt, 1);
				}
			}
			
//			}else if(cellValue.equalsIgnoreCase("Sub-Total")){
//
//				subTotal = Double.parseDouble(priceInUI.split("\\$")[1]);
//
//			} // need to handle tax calculation
			//			else if(cellValue.equalsIgnoreCase("Tax")){
			//			
			//				taxAmount = Double.parseDouble(priceInUI.split("\\$")[1]);
			//				
			//			}

		}

		sc.scrollIntoView(elementTable);

		//		Total Cost of Background price validation
		
		try {
			expectedTotalPrice = fetchExpectedTotalPriceSTEP4();
			sc.verifyExpectedValue(expectedTotalPrice,totalCost,"Verify Total Cost of background check");

		} catch (Exception e) {

			e.printStackTrace();
		}
	 }
	/**************************************************************************************************
	 * Method to search volunteer in BG dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author aunnikrishnan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String searchVolInHoldTabBGDashboard() throws Exception {
        Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
        APP_LOGGER.startFunction(Globals.Component);
        String retval=Globals.KEYWORD_FAIL;
        String tempretval=Globals.KEYWORD_FAIL;
        long timeOutinSeconds = 60;
  
        verifyOrgUserClientHierarchyDD();
        tempretval = sc.waitforElementToDisplay("bgDashboard_search_txt", timeOutinSeconds);
        if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
               sc.STAF_ReportEvent("Fail", "BG Dashboard", "Volunteer Search text field is not displayed", 1);
        }else{
               String volFname = "";
               String volLname ="";
               volFname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
               volLname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
               sc.clickElementUsingJavaScript("bgDashboard_search_txt");
               sc.setValueJsChange("bgDashboard_search_txt", volFname);
//             sc.clickElementUsingJavaScript("bgDashboard_search_txt");
//             int i =0;
//             do{
//                   sc.setValueJsChange("bgDashboard_search_txt", volFname);
//                   retval= sc.verifyText("bgDashboard_search_txt", volFname, true);
//                   if(retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
//                          break;
//                   }
//                   i++;
//             }while(i<=60);
               retval = sc.getWebText("bgDashboard_search_txt");
               if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
                     sc.setValueJsChange("bgDashboard_search_txt", volFname);
               }
               sc.clickWhenElementIsClickable("bgDashboard_searchBtn_btn", (int) timeOutinSeconds);

               tempretval = Globals.KEYWORD_FAIL;
               tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

               while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
                     Thread.sleep(2000);
                     tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

               }

               //get row count of  the search results table
               WebElement searchGrid = sc.createWebElement("bgDashboard_searchResults_tbl");
               int rowcount =0;
               rowcount = sc.getRowCount_tbl(searchGrid);

               if(rowcount != 1){
                     int flag=0;
                     while(rowcount != 1){
                            sc.setValueJsChange("bgDashboard_search_txt", volFname);
                            sc.clickWhenElementIsClickable("bgDashboard_searchBtn_btn", (int) timeOutinSeconds);

                            tempretval = Globals.KEYWORD_FAIL;
                            tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

                            while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
                                   Thread.sleep(2000);
                                   tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

                            }
                            searchGrid = sc.createWebElement("bgDashboard_searchResults_tbl");
                            rowcount =0;
                            rowcount = sc.getRowCount_tbl(searchGrid);
                            if(flag>3){
                                   break;
                            }
                            flag++;
                     }
               }
               
               if(rowcount!=1){
                     sc.STAF_ReportEvent("Fail", "BG Dashboard", "Multiple volunteers has been found during search.Unable to select the appropriate volunteer", 1);
               }else {
                     String noMatchRecords = "";
                     noMatchRecords=       searchGrid.findElement(By.xpath("./tbody/tr[1]/td[1]")).getText();
                     String errorText="No matching records.";
                     if(errorText.equalsIgnoreCase(noMatchRecords)){
                            sc.STAF_ReportEvent("Fail", "BG Dashboard", "Unable to Search volunteer by FirstName.No match found.FirstName-"+volFname, 1);
                     }else{
                            String OrderHoldQueuesettings = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldQueuecheckbox");
                         if(OrderHoldQueuesettings.equalsIgnoreCase("Yes")){
                            String volNameUI = searchGrid.findElement(By.xpath("./tbody/tr[1]/td[2]")).getText();
                            String orderDateUI ="";
                            String positionUI="";
                                   String resultsUI="";
                                   String expOrderedDate="";
                                   String expPosition="";
                                   String expResults="Hold";
                                   String expVolName = volLname +", "+volFname;
                                   if(expVolName.equalsIgnoreCase(volNameUI)){
                                          orderDateUI = searchGrid.findElement(By.xpath("./tbody/tr[1]/td[8]")).getText();
                                          positionUI =  searchGrid.findElement(By.xpath("./tbody/tr[1]/td[4]")).getText();
                                          resultsUI =  searchGrid.findElement(By.xpath("./tbody/tr[1]/td[10]")).getText();

                                          expOrderedDate = Globals.testSuiteXLS.getCellData_fromTestData("OrderedDate");
                                          expPosition = Globals.testSuiteXLS.getCellData_fromTestData("PositionName");
                                          if(expOrderedDate.equalsIgnoreCase(orderDateUI) && expPosition.equalsIgnoreCase(positionUI) && expResults.equalsIgnoreCase(resultsUI)){

                                                 sc.STAF_ReportEvent("Pass", "Volunteer Search - BG Dashboard Hold Tab", "Volunteer searched successfully.Name-"+expVolName, 1);
                                                 searchGrid.findElement(By.xpath("./tbody/tr[1]/td[2]/a")).click();
                                                 tempretval = Globals.KEYWORD_FAIL;
                                                 tempretval = sc.waitforElementToDisplay("bgReport_orderhold_list", timeOutinSeconds);
                                                 if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
                                                        retval = Globals.KEYWORD_PASS;
                                                 }else{
                                                               sc.STAF_ReportEvent("Fail", "BG Report","Unable to load BG Report for Volunteer on Hold",1);
                                                        }
                                           }else{
                                                         sc.STAF_ReportEvent("Fail", "BG Dashboard Hold Tab", "Volunteers data mismatch.Kindly verify ResultStatus,OrderedDate and PositionName", 1);     
                                               } 
                         }else{
                                        sc.STAF_ReportEvent("Fail", "BG Dashboard Hold Tab", "Volunteers name mismatch.Kindly verify volunteer first and last name", 1);
                         }
                         }
               }             
               }
        }
        return retval;

 }

	/**************************************************************************************************
	 * Method to mark the order on hold as either cancel or submit
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String orderHoldReview() throws Exception {
		int timeOutInSeconds = 10;
		String tempRetval= Globals.KEYWORD_FAIL;
		String expOrderScore ="";

		try{
			tempRetval = sc.waitforElementToDisplay("bgReport_review_link",(long) timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","Order Hold Review", "Review button is not visible", 1);
				return Globals.KEYWORD_FAIL;
			}

			sc.scrollIntoView("bgReport_review_link");
			expOrderScore = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldReview");
			
			if(expOrderScore.isEmpty() || expOrderScore.equals("") || expOrderScore == null){
				sc.STAF_ReportEvent("Fail","Order Hold Review", "Test Data error.Order Hold Review column is blank", 0);
				return Globals.KEYWORD_FAIL;
			}
			String objLocator = "";
			String uiText="";
				if(expOrderScore.equalsIgnoreCase("Submit")){
					objLocator="bgReport_orderholdsubmit_btn";
					uiText="Successfully submitted order.";
				}else {
					objLocator = "bgReport_orderholdcancel_btn";
					uiText = "Successfully canceled order.";
				}
			
			sc.clickWhenElementIsClickable("bgReport_review_link", timeOutInSeconds);
			sc.clickWhenElementIsClickable(objLocator, timeOutInSeconds);
			
			//check for successfull message
			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 10);

			while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(2000);
				tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

			}

			tempRetval = Globals.KEYWORD_FAIL;
			tempRetval = sc.waitforElementToDisplay("bgDashboard_warningMsg_span",(long) 20);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail","Order Hold Review", "Successfuly message is not displayed on BG Dashboard", 1);
				return Globals.KEYWORD_FAIL;
			}else{
				String actualUItext ="";
				actualUItext = driver.findElement(LocatorAccess.getLocator("bgDashboard_warningMsg_span")).getText();
				if(uiText.equalsIgnoreCase(actualUItext)){
					sc.STAF_ReportEvent("Pass","Order Hold Review", "Order on Hold reviewed as "+expOrderScore,1);
					return Globals.KEYWORD_PASS;
				}else{
					sc.STAF_ReportEvent("Fail","Order Hold Review", "Unable to review Order on Hold as "+expOrderScore,1);
					return Globals.KEYWORD_FAIL;
				}

			}
		}catch(Exception e){
			sc.STAF_ReportEvent("Fail","Order Hold Review", "Unable to review Order on Hold as "+expOrderScore +" due to Exception-"+e.toString(),1);	
			e.printStackTrace();
		}

		return Globals.KEYWORD_FAIL;// only failed cases would reach this point
	}

	/**************************************************************************************************
	 * Method to verify cancelled order on hold on Volunteer Dashboard
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyOrderHoldCancelled() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 10;

		tempretval = sc.waitforElementToDisplay("volDashboard_searchBy_dd", timeOutinSeconds);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Search Volunteer", "Volunteer Dashboard page has not loaded", 1);
			throw new Exception("Search Volunteer-Volunteer Dashboard page has not loaded");
		}
        showColumn();

		String reportingStepName;
		
		String volFname = "";
		String volLname ="";
		volFname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
		volLname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
        String Position = Globals.testSuiteXLS.getCellData_fromTestData("PositionName");
	
		reportingStepName="VVOU168 - Verify the column First Name on the volunteer dashboard page for order hold cancelled.";
		retval = searchVolunteer("First Name",volFname,"Name", volFname,reportingStepName);

		reportingStepName="VVOU178 - Verify the column Status on the volunteer dashboard page for order hold cancelled.";
		retval = searchVolunteer("First Name",volFname,"Status","No Order Placed", reportingStepName);
		return retval;


	}
	/**************************************************************************************************
	 * Method fetch SWEST id from Order hold notification mail recieved when an order on hold is submitted
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String FetchSwestIdFromPrderHoldEmail() throws Exception {
		APP_LOGGER.startFunction("FetchSwestIdFromPrderHoldEmail");
		String retval = Globals.KEYWORD_FAIL;
		HashMap<String, String> emailDetails=  new HashMap<String, String>();
		String emailFound=null;
		String emailBodyText =null;
		String npnOrderID =null;
		String swestOrderID=null;
		String orderedDate=null;
		int startIndex,endindex;  
		String npnordersrchpattern="Order #";
		try{
  			String fetchOrderid = null;
  			String subjectEmailPattern = "You did it! You’re on your way to becoming a Verified Volunteer!";
			String bodyEmailPattern = "Order Placed Notification";
			Thread.sleep(100000);	
			if(Globals.EXECUTION_MACHINE.equalsIgnoreCase("jenkins")) {
				bodyEmailPattern =Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
				emailBodyText = FetchInviatationURLFromDB(bodyEmailPattern,subjectEmailPattern);
			    if(emailBodyText.equalsIgnoreCase("") || emailBodyText == null) {
			        sc.STAF_ReportEvent("Fail", "Fetch Order Hold Notification mail", "Order Hold Notification mail mail NOT fetched by Database.", 0);
	                throw new Exception("Applicant Order Hold Notification mail NOT received from Database");
			    }
			   // emailFound="True";
			    //emailBodyText = emailBodyText.replace("\"", "").trim();
			    startIndex = emailBodyText.indexOf(npnordersrchpattern);
			    
                endindex = emailBodyText.indexOf("<br /><br />Staff 10 : Order");
				fetchOrderid = emailBodyText.substring(startIndex+7, endindex).trim();
				npnOrderID= fetchOrderid.split(" - ")[0];
				String swestID = fetchOrderid.split(" - ")[1];
				swestOrderID = swestID.split("<")[0];
				orderedDate = sc.getTodaysDate();
			
                
			}else{			
				emailDetails = Email.fetchEmailFromOutlook(subjectEmailPattern,bodyEmailPattern,60);

				emailFound = emailDetails.get("EmailFound");
			
				if (emailFound.equalsIgnoreCase("True")){
					emailBodyText = emailDetails.get("EmailBody");
					emailBodyText = emailBodyText.replace("\"", "").trim();
					startIndex = emailBodyText.indexOf(npnordersrchpattern);
					endindex = emailBodyText.indexOf("Staff");
					fetchOrderid = emailBodyText.substring(startIndex+7, endindex).trim();
					npnOrderID= fetchOrderid.split(" - ")[0];
					swestOrderID = fetchOrderid.split(" - ")[1];
				
				}else{
					log.error("Method-FetchSwestIdFromPrderHoldEmail | Order Hold Notification mail not received");
					sc.STAF_ReportEvent("Fail", "Order Hold Notification mail", "Order Hold Notification mail not received", 1);
					throw new Exception("Order Hold Notification mail not received");
				}
			}	
			if(npnOrderID != null && swestOrderID != null && swestOrderID.length() > 4){
					log.info("Method-FetchSwestIdFromPrderHoldEmail | Order ID generated | NPN Order ID- "+npnOrderID + "| SWest Order ID = "+swestOrderID);
					orderedDate = sc.getTodaysDate();
					Globals.testSuiteXLS.setCellData_inTestData("OrderedDate", orderedDate);
					Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", npnOrderID);
					Globals.testSuiteXLS.setCellData_inTestData("SWestOrderID", swestOrderID);
					sc.addParamVal_InEmail("SWestOrderID", swestOrderID);
					sc.addParamVal_InEmail("NPNOrderID", npnOrderID);
					sc.STAF_ReportEvent("Pass", "Order Hold Notification mail", "Order Hold notification mail successfully received with "+npnOrderID+"-"+swestOrderID , 1);
					
					// API Retrieval Notify Response check 
				    String ntMsgAPilog=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
		            String ntMsgnwapiendpt=Globals.testSuiteXLS.getCellData_fromTestData("APIRetrievalNotifyResponseNewEndpoint");
		            if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
		            	
			            if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") || ntMsgAPilog.equalsIgnoreCase("") || ntMsgnwapiendpt.equalsIgnoreCase("")||ntMsgAPilog.isEmpty() || ntMsgnwapiendpt.isEmpty()){
			            	//sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
			            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
			            	Queryforapilog(npnOrderID,"InProgress","Blank","NoNo");            	
			            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
			            	Queryforapilog(npnOrderID,"InProgress","Blank","YesNo");
			            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
			            	Queryforapilog(npnOrderID,"InProgress","Blank","NoYes");
			            	getOrderRequestXML("InProgress");
			            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
			            	Queryforapilog(npnOrderID,"InProgress","Blank","YesYes");
			            	getOrderRequestXML("InProgress");
			            }else{
			            	sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
			            }
			            
		            }
					retval = Globals.KEYWORD_PASS;
					
				}else{
				log.error("Method-FetchSwestIdFromPrderHoldEmail | SWest Order ID NOT generated");

					sc.STAF_ReportEvent("Fail", "Order Hold Notification mail", "Order id has been NOT been generated", 1);
					Globals.testSuiteXLS.setCellData_inTestData("NPNOrderID", "");
					Globals.testSuiteXLS.setCellData_inTestData("SWestOrderID", "");
				}

		}catch (Exception e) {
			log.error("Method-orderHoldBGReportBeforeSubmit | Unable to create Invitation Order | Exception - "+ e.toString());
			sc.STAF_ReportEvent("Fail", "Order Hold Notification mail", "Order Hold Notification mail NOT received", 0);
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method fetch SWEST id from npnorder table for client/batch orders in order hold flow
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public  static String FetchSwestIdClientOrder() throws Exception {
		APP_LOGGER.startFunction("FetchSwestIdClientOrder");
		String retval = Globals.KEYWORD_FAIL;
		String npnOrderID =Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
		int swestOrderID=0;
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


	           //vvDB.connectVV_DB(); 
	           int iOrderID 				= Integer.parseInt(npnOrderID);
	    		int rowCount = 0;
	    		String searchReqQuery 		= "select SterlingId from npnorder where npnorderid = ?";
	    		
	    		vvDB.ps = vvDB.conn.prepareStatement(searchReqQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    		vvDB.ps.setInt(1, iOrderID);
	    		vvDB.rs = vvDB.ps.executeQuery();
	    		
	    		rowCount = vvDB.getRows(vvDB.rs);
	    		if(rowCount >= 1 ){
	    			
	    			vvDB.rs.next();
	    			swestOrderID = vvDB.rs.getInt("SterlingId");
	    		
	    		}
	    		
	    		String SWestOrderID = Integer.toString(swestOrderID);
	    		Globals.testSuiteXLS.setCellData_inTestData("SWestOrderID", SWestOrderID);
	    		if(SWestOrderID!=null && !SWestOrderID.isEmpty()){
	    			retval = Globals.KEYWORD_PASS;
	    		}
	    		
	    		 String npnOrderid=	Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");	
	    		 String ntMsgAPilog=Globals.testSuiteXLS.getCellData_fromTestData("SendNotificationsviaApiLogTable");
                 String ntMsgnwapiendpt=Globals.testSuiteXLS.getCellData_fromTestData("APIRetrievalNotifyResponseNewEndpoint");
        
                 String OrderHoldQueuesettings = Globals.testSuiteXLS.getCellData_fromTestData("OrderHoldQueuecheckbox");
                 if(OrderHoldQueuesettings.equalsIgnoreCase("Yes")){
                	 if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
 		            	
 			            if(ntMsgAPilog.equalsIgnoreCase("N/A") || ntMsgnwapiendpt.equalsIgnoreCase("N/A") || ntMsgAPilog.equalsIgnoreCase("") || ntMsgnwapiendpt.equalsIgnoreCase("")||ntMsgAPilog.isEmpty() || ntMsgnwapiendpt.isEmpty()){
 			            	//sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
 			            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
 			            	Queryforapilog(npnOrderID,"InProgress","Blank","NoNo");            	
 			            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("No")){
 			            	Queryforapilog(npnOrderID,"InProgress","Blank","YesNo");
 			            }else if(ntMsgAPilog.equalsIgnoreCase("No") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
 			            	Queryforapilog(npnOrderID,"InProgress","Blank","NoYes");
 			            	getOrderRequestXML("InProgress");
 			            }else if(ntMsgAPilog.equalsIgnoreCase("Yes") && ntMsgnwapiendpt.equalsIgnoreCase("Yes")){
 			            	Queryforapilog(npnOrderID,"InProgress","Blank","YesYes");
 			            	getOrderRequestXML("InProgress");
 			            }else{
 			            	sc.STAF_ReportEvent("Fail", "APi Notification messages", "Could not verified API Notification message please Check the value in testdata for SendNotificationsviaApiLogTable or APIRetrievalNotifyResponseNewEndpoint column",0);
 			            }
 			            
 		            }
                 	
                 	                 
                 }
	    		
	    		
	    		
	    		
	    		
                
		}catch (Exception e) {
			log.error("Method-FetchSwestIdClientOrder | Unable to retrieve SWESt order id | Exception - "+ e.toString());
			sc.STAF_ReportEvent("Fail", "FetchSwestIdClientOrder", "Unable to retrieve SWESt order id", 0);
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
		return retval;
	}

	/*****************************************************************************************************************
	 * Method to verify Backgroundcheck Hold status on Volunteer Dashboard before review of an order is placed on hold
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 *****************************************************************************************************************/
	public static String verifyOrderHoldStatusBeforeReview() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 10;

		tempretval = sc.waitforElementToDisplay("volDashboard_searchBy_dd", timeOutinSeconds);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Search Volunteer", "Volunteer Dashboard page has not loaded", 1);
			throw new Exception("Search Volunteer-Volunteer Dashboard page has not loaded");
		}
        showColumn();

		String reportingStepName;
		
		String volFname = "";
		
		volFname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");

		reportingStepName="VVOU178 - Verify the column Status on the volunteer dashboard page for order hold.";
		retval = searchVolunteer("First Name",volFname,"Status","Background Check Hold", reportingStepName);
		return retval;


	}
	/*****************************************************************************************************************
	 * Method to verify Backgroundcheck Hold status on Volunteer Dashboard after review of an order is placed on hold
	 * as submit
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 *****************************************************************************************************************/
	public static String verifyOrderHoldStatusAfterReview() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempretval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 10;

		tempretval = sc.waitforElementToDisplay("volDashboard_searchBy_dd", timeOutinSeconds);
		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "Search Volunteer", "Volunteer Dashboard page has not loaded", 1);
			throw new Exception("Search Volunteer-Volunteer Dashboard page has not loaded");
		}
        showColumn();

		String reportingStepName;
		
		String volFname = "";
		
		volFname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");

		reportingStepName="VVOU178 - Verify the column Status on the volunteer dashboard page for order hold after review as submit.";
		retval = searchVolunteer("First Name",volFname,"Status","Background Check Pending", reportingStepName);
		return retval;


	}
	/**************************************************************************************************
	 * Method for to create an gooddeedcode with pa abuse and then removing it and validate
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static void RemoveAbusePa() throws Exception {
		APP_LOGGER.startFunction("RemovePAAbuse");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds=20;
		try {
			
			Globals.testSuiteXLS.setCellData_inTestData("AddressState", "Florida");
			Globals.testSuiteXLS.setCellData_inTestData("AddressZipCode", "32007");
	        sc.clickWhenElementIsClickable("invitationStep4_back_btn",timeOutinSeconds);
	    	retval= ClientFacingApp.invitationOrderStep1();
	    	Globals.testSuiteXLS.setCellData_inTestData("PositionProducts", "L1");
			retval= ClientFacingApp.invitationOrderStep2();
			retval= ClientFacingApp.invitationOrderStep3();
			tempRetval=sc.waitforElementToDisplay("invitationStep4_infoCorrect_chk", 60);
			sc.clickWhenElementIsClickable("invitationStep4_infoCorrect_chk",timeOutinSeconds);
			sc.clickWhenElementIsClickable("invitationStep4_fastPassNo_chk", timeOutinSeconds);
			verifyPricingStep4();
			Globals.testSuiteXLS.setCellData_inTestData("AddressState", "Pennsylvania");
			Globals.testSuiteXLS.setCellData_inTestData("AddressZipCode", "15001");
	    	Globals.testSuiteXLS.setCellData_inTestData("PositionProducts", "L1-ABUSE");
	    	
			
		} catch (Exception e) {
			log.error("Method-CreateInvitationOrder | Unable to create Invitation Order | Exception - "+ e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		}
	}
	/**************************************************************************************************
	 * Method to send an renewal invitation to the volunteer
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String sendRenewalInvitation(){
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 20;
		String tempRetval=Globals.KEYWORD_FAIL;

		try{
            
			String Fname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
			String reportingStepName="Verify the Search Criteria for volunteers First Name on the volunteer dashboard page.";
			retval = searchVolunteer("First Name",Fname,"Name", Fname,reportingStepName);
			
			WebElement volGrid = sc.createWebElement("volDashboard_volgrid_tbl");
			sc.checkCheckBox(volGrid.findElement((By.xpath("./tbody/tr[1]/td[1]/input"))));
 			tempRetval=sc.waitforElementToDisplay("volDashboard_communications_btn", timeOutinSeconds);
 				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
 					sc.clickWhenElementIsClickable("volDashboard_communications_btn", timeOutinSeconds);
 		 			sc.clickWhenElementIsClickable("volDashboard_sendRenewalInvitation_btn", timeOutinSeconds);
 		 			tempRetval = sc.waitforElementToDisplay("renewalInvitation_postionListing_dd", timeOutinSeconds);	
 		 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 						String positionName= Globals.testSuiteXLS.getCellData_fromTestData("PositionName") +" - "+ Globals.testSuiteXLS.getCellData_fromTestData("PricingName");
 						if(positionName.contains("Client Pays")){	
 							String arr0 =positionName.split("Client Pays")[0];
 							String arr1 =positionName.split("Client Pays")[1];
 							String arrf1=arr0+"Client Pays";
 							String arrf2=arr1.trim();
 							Select sel = new Select(driver.findElement(LocatorAccess.getLocator("renewalInvitation_postionListing_dd")));
 							List<WebElement> list = sel.getOptions();
 									for (WebElement option : list) {
 										if (option.getText().contains(arrf1) && option.getText().contains(arrf2)) {
 											sel.selectByVisibleText(option.getText());
 											break;
 						        }
 							}
 						}
 							else{
 								tempRetval = sc.selectValue_byVisibleText("renewalInvitation_postionListing_dd", positionName );
 								}

 						sc.addParamVal_InEmail("Position", positionName);
 						
 						
 						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
 							log.error(" Unable to select Position from the Postion Dropdown | Postion - "+positionName);
 							sc.STAF_ReportEvent("Fail", "Send Renewal Invitation", "Unable to select Position - "+ positionName, 1);
 							return tempRetval;
 						}
 						sc.STAF_ReportEvent("Pass", "Send Renewal Invitation", "Position selected - "+ positionName, 1);

 						String customEmailMessage =null;

 						customEmailMessage = sc.updateAndFetchRuntimeValue("CustomEmailMsg_runtimeUpdateFlag","CustomEmailMessage",45);

 						String ccList = Globals.testSuiteXLS.getCellData_fromTestData("CCEmailAddr");
 						if(ccList != null && !ccList.isEmpty() && !ccList.equals("")){
 							tempRetval = sc.selectValue_byVisibleText("renewalInvitation_ccEmailAddress_dd",ccList  );
 						}

 						String bccList = Globals.testSuiteXLS.getCellData_fromTestData("CCEmailAddr");
 						if(bccList != null && !bccList.isEmpty() && !bccList.equals("")){
 							tempRetval = sc.selectValue_byVisibleText("renewalInvitation_bccEmailAddress_dd", bccList);
 						}


 						tempRetval = sc.setValueJsChange("renewalInvitation_emailBody_txt", customEmailMessage);

 						tempRetval = sc.waitforElementToDisplay("renewalInvitation_sendInvite_btn",timeOutinSeconds);
 	
 						sc.clickWhenElementIsClickable("renewalInvitation_sendInvite_btn",timeOutinSeconds);

 						boolean sendRenewalInvitePageVisible = true;
 						int counter = 1;
 						while(sendRenewalInvitePageVisible && counter <= 90){
 							tempRetval=Globals.KEYWORD_FAIL;
 							tempRetval=sc.waitforElementToDisplay("renewalInvitation_sendInvite_btn", 2);
 							if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 								counter = counter +2;
 							}else{
 								sendRenewalInvitePageVisible = false;
 							}

 						}

 						if(sendRenewalInvitePageVisible){
 							sc.STAF_ReportEvent("Fail", "Send Renewal Invitation", "Unable to send E-invite to  - ", 1);
 							retval=Globals.KEYWORD_FAIL;

 						}else{
 							sc.STAF_ReportEvent("Pass", "Send Renewal Invitation", "E-Invite send ", 1);
 							retval=Globals.KEYWORD_PASS;
 						}


 					}
 				}


		}catch(Exception e){
			retval=Globals.KEYWORD_FAIL;
			log.error("Method-SendRenewalInvitationToApplicant | Exception - "+ e.toString());
		}
		return retval;
	}
	 /**************************************************************************************************
		 * Method to verify  gui validations for removed rating restrictions in Volunteer Dashboard
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author vgokulanathan
		 * @throws Exception
		 ***************************************************************************************************/
		public static String verifyRemovedRatingRestriction() throws Exception {
			Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
			APP_LOGGER.startFunction(Globals.Component);
			String retval=Globals.KEYWORD_FAIL;
			String tempretval = Globals.KEYWORD_FAIL;
	 		int timeOutinSeconds = 20;
	 		int noOfRows;
	 		int noOfCols;
	 		try{
	 			showColumn();
	 			
	 			String order = Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
	 			String reportingStepName="Verify the  Restriction value appearing on the volunteer dashboard page.";
	 			retval = searchVolunteer("VV Order",order,"Restrictions","SecondTest", reportingStepName);
	 			if (retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
	 			WebElement volGrid = sc.createWebElement("volDashboard_volgrid_tbl");
	 			volGrid.findElement(By.xpath("./tbody/tr[1]/td[2]/a")).click();
	 			String valueText;
	 			tempretval = sc.waitforElementToDisplay("volDashboard_volunteerprile_txt",timeOutinSeconds);
	 				if (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
	 					WebElement restrictionsGrid = sc.createWebElement("RatingRestriction_restrictionGrid_tbl");
	 					String Restrict= Globals.testSuiteXLS.getCellData_fromTestData("RestrictionValue");
	 					String Note= "RestrictionNote";
	 					String Enddate ="Invalid Date";
	 					String[] expectedvalues = {Restrict,Enddate,"Active",Note};
	 					Date date = new Date();
						String DATE_FORMAT = "MM/dd/yy";
						SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
						String removedDate = sdf.format(date);
						String[] expectedvalue = {"Test",Enddate,removedDate,Note};
						
	 					for(noOfRows=1;noOfRows<=2;noOfRows++){
	 						int j=0;
	 					   for(noOfCols=1;noOfCols<=4;noOfCols++){
	 						  List<WebElement> valueList = restrictionsGrid.findElements(By.xpath("./tbody/tr["+noOfRows+"]/td["+noOfCols+"]"));
	 						  for(WebElement element : valueList){
			 						valueText = element.getText();
			 						if(valueText.equalsIgnoreCase(expectedvalues[j])){
			 							sc.STAF_ReportEvent("Pass", "To Verify Restrict  Column on Volunteer Profile", "Restriction value is present in UI. Value  -"+valueText, 1);
			 						}else if(valueText.equalsIgnoreCase(expectedvalue[j])){
			 							sc.STAF_ReportEvent("Pass", "To Verify Restrict  Column on Volunteer Profile", "Restriction value is present in UI. Value  -"+valueText, 1);
			 						}
			 						else{
			 							sc.STAF_ReportEvent("Fail", "To Verify Restrict  Column on Volunteer Profile", "Mismatch in Restriction value.Expected = "+expectedvalues[j]+expectedvalue[j] + " Actual = "+valueText, 1);
			 						}
			 						j++;
			 					}
	 	 						
	 	 					  }
	 					}
	 					sc.clickWhenElementIsClickable("volunteerProfile_close_btn",timeOutinSeconds);
	 					retval = Globals.KEYWORD_PASS;
	 				}
	 				}}catch(Exception e){
			 			e.printStackTrace();
			 			throw e ;
	 		}
			return retval;
		}
		/**************************************************************************************************
		 * Method to verfiy ABUSE-CO consent page
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author vgokulanathan
		 * @throws Exception
		 ***************************************************************************************************/
		public static String invitationStep3COAbuse() throws Exception{
			//if ABUSE is in package, then ABUSE will only get added if PA is select as the address state
			// if ABUSE-PA is configured in package then it will get added no matter what the address state is
			String retval = Globals.KEYWORD_FAIL;
			String tempRetval =  Globals.KEYWORD_FAIL;
			long timeOutInSeconds = 30;
			boolean isAbuseOrdered = false;
	        Thread.sleep(5000);
			tempRetval = isProductPartOfOrdering("ABUSE(CO)");
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				isAbuseOrdered = true;
			}else {
				tempRetval = Globals.KEYWORD_FAIL;
				tempRetval = isProductPartOfOrdering("ABUSE");
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					String applicantState = Globals.testSuiteXLS.getCellData_fromTestData("AddressState");
					if(applicantState.equalsIgnoreCase("Colorado")){
						isAbuseOrdered = true;
					}
				}
			}

			if(isAbuseOrdered){
				 //new page will always be displayed when ABUSE PA is present in the order
				sc.waitForPageLoad();
				retval = invitationStep3Abuseco(); //page will only be displayed when ABUSE CO is present in the order
				tempRetval = sc.waitforElementToDisplay("invitationStep3abuseco_signatr1_btn", timeOutInSeconds);
				if (retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail", "ABUSE-Colorado", "Abuse page has not loaded", 1);
						throw new Exception("ABUSE-Colorado" +"Abuse page has not loaded");
					}
				else{
					    // Verifying the current date stamp on the CO abuse consent pdf
					    tempRetval = sc.waitforElementToDisplay("invitationStep3abuseco_prnt_btn", timeOutInSeconds);
					    Thread.sleep(5000);
					    Date daten = new Date();
						SimpleDateFormat sdfs = new SimpleDateFormat("MM/dd/yyyy h:mm a"); // 06/30/2016 5:18 AM
						String currentDatestamp = sdfs.format(daten);
						String currentDate [] = currentDatestamp.split(":");
						String currentTimestamp = currentDate[0];
						String actdatesign=driver.findElement(By.xpath("//*[@id='co_abuse_signatureDate1']")).getText();
						String actdate1 [] = actdatesign.split(":");
						String actdatesign1=actdate1[0];
						String actdatesgn=driver.findElement(By.xpath("//*[@id='co_abuse_signatureDate2']")).getText();
						String actdate2 [] = actdatesgn.split(":");
						String actdatesign2=actdate2[0];
						if(actdatesign1.equalsIgnoreCase(currentTimestamp) && actdatesign2.equalsIgnoreCase(currentTimestamp))
						{
							sc.STAF_ReportEvent("Pass", "Abuse-CO-Consent", "Consent text matched - Verified currentTimestamp is as expected." +currentTimestamp, 1);
						}
						else{
							sc.STAF_ReportEvent("Fail", "Abuse-CO-Consent", "Consent text not matched - Verified currentTimestamp is not as expected."+currentTimestamp+" "+actdatesign1+" "+actdatesign2, 1);
						}
						
					    // Verifying the volunteer details on the CO abuse consent pdf
						String actualfullname= driver.findElement(By.xpath("//*[@id='co_abuse_applicantName']")).getText();
						String fname=Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
						String lname=Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
						String mname=Globals.testSuiteXLS.getCellData_fromTestData("volunteerMidName");
						String expfullname = fname+" "+mname+" "+lname;
						if(actualfullname.equalsIgnoreCase(expfullname))
						{
							sc.STAF_ReportEvent("Pass", "Abuse-CO-Consent", "Consent text matched - Verified volunteer name as expected.", 1);
						}
						else{
							sc.STAF_ReportEvent("Fail", "Abuse-CO-Consent", "Consent text not matched - Verified volunteer name is not as expected.", 1);
						}
						
						String additionaldata=Globals.testSuiteXLS.getCellData_fromTestData("Notes"); 
						int aliasQuantity = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
						if(additionaldata.equalsIgnoreCase("No")){					
						if(aliasQuantity==0){
							String actualaliasname= driver.findElement(By.xpath("//*[@id='co_abuse_applicantAliasNames']")).getText();
							String expaliasname="No alias names used.";
							if(actualaliasname.equalsIgnoreCase(expaliasname))
							{
								sc.STAF_ReportEvent("Pass", "Abuse-CO-Consent", "Consent text matched - Verified alias name as expected.", 1);
							}
							else{
								sc.STAF_ReportEvent("Fail", "Abuse-CO-Consent", "Consent text not matched - Verified alias name is not as expected.", 1);
							}
						}}
						else{
							
								String actualaliasname= driver.findElement(By.xpath("//*[@id='co_abuse_applicantAliasNames']")).getText();
								String expaliasname=Globals.Volunteer_AliasName+" "+Globals.Volunteer_AliasName+" "+Globals.Volunteer_AliasName;
								if(actualaliasname.equalsIgnoreCase(expaliasname))
								{
									sc.STAF_ReportEvent("Pass", "Abuse-CO-Consent", "Consent text matched - Verified alias name as expected.", 1);
								}
								else{
									sc.STAF_ReportEvent("Fail", "Abuse-CO-Consent", "Consent text not matched - Verified alias name is not as expected.", 1);
								}
							
						}
						String actualdob= driver.findElement(By.xpath("//*[@id='co_abuse_applicantDOB']")).getText();
						//Date convertedDate  = null;
						String voldate =Globals.testSuiteXLS.getCellData_fromTestData("VolunteerDOB");
						DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy"); // 06/30/2016
						Date date = sdf.parse(voldate);
						SimpleDateFormat sdff = new SimpleDateFormat("M/d/yyyy");
						String formattedDob = sdff.format(date);
						String expgender="M";
						String actgender=driver.findElement(By.xpath("//*[@id='co_abuse_applicantSex']")).getText();
						String actrace=driver.findElement(By.xpath("//*[@id='co_abuse_applicantRace']")).getText();
						String exprace="I Prefer Not To Answer";
						String expssn =Globals.testSuiteXLS.getCellData_fromTestData("VolunteerSSN");
						String actssna=driver.findElement(By.xpath("//*[@id='co_abuse_applicantSSN']")).getText();
						String actssn= actssna.replaceAll("-", "");
						String actresult = actualdob+actgender+actrace+actssn;
						String expresult = formattedDob+expgender+exprace+expssn;
						if(actualdob.equals(formattedDob) && actgender.equalsIgnoreCase(expgender) && actrace.equalsIgnoreCase(exprace) && actssn.equalsIgnoreCase(expssn))
						{
							sc.STAF_ReportEvent("Pass", "Abuse-CO-Consent", "Consent text matched - Verified dob,gender,race,ssn as expected." + actresult + expresult , 1);
						}
						else{
							sc.STAF_ReportEvent("Fail", "Abuse-CO-Consent", "Consent text not matched - Verified dob,gender,race,ssn is not as expected." + actresult + expresult, 1);
						}
						String addressLine =Globals.Volunteer_AddressLine;
						String city=Globals.Volunteer_City;
						String state ="CO";
						String prevstate = "FL";
						String zipcode =Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode");
						String actcurrentaddress=driver.findElement(By.xpath("//*[@id='co_abuse_applicantAddressCurrent']")).getText();
						String expcurrentaddress =addressLine+" "+addressLine+","+" "+city+","+" "+state+" "+zipcode;
						String Prezip=Globals.testSuiteXLS.getCellData_fromTestData("PrevAddZipCode");
						String actpreviousaddress=driver.findElement(By.xpath("//*[@id='co_abuse_applicantAddressPrevious']")).getText();
						String exppreviousaddress =addressLine+" "+addressLine+","+" "+city+","+" "+prevstate+" "+Prezip;
						String exphoneNo=Globals.Volunteer_Phone;
						String actphoneNo=driver.findElement(By.xpath("//*[@id='co_abuse_applicantPhone']")).getText();
						if(actcurrentaddress.equalsIgnoreCase(expcurrentaddress) && actpreviousaddress.equalsIgnoreCase(exppreviousaddress) && actphoneNo.equalsIgnoreCase(exphoneNo))
						{
							sc.STAF_ReportEvent("Pass", "Abuse-CO-Consent", "Consent text matched - Verified current address,previous address,phoneNo as expected.", 1);
						}
						else{
							sc.STAF_ReportEvent("Fail", "Abuse-CO-Consent", "Consent text not matched - Verified current address,previous address,phoneNo is not as expected.", 1);
						}
						if(additionaldata.equalsIgnoreCase("Yes")){
						//Verifying parent and children details on CO abuse consent
						String actualparentname= driver.findElement(By.xpath("//*[@id='co_abuse_parent1Name']")).getText();
						String expparentname = "Spousefstnme"+" "+"Spousemdlnme"+" "+"Spouselstnme";
						if(actualparentname.equalsIgnoreCase(expparentname))
						{
							sc.STAF_ReportEvent("Pass", "Abuse-CO-Consent", "Consent text matched - Verified parent name as expected.", 1);
						}
						else{
							sc.STAF_ReportEvent("Fail", "Abuse-CO-Consent", "Consent text not matched - Verified parent name is not as expected.", 1);
						}
						String actualpardob= driver.findElement(By.xpath("//*[@id='co_abuse_parent1DOB']")).getText();
						String parenrDob = "5/1/1996";
						String exppargender="F";
						String actpargender=driver.findElement(By.xpath("//*[@id='co_abuse_parent1Sex']")).getText();
						String actparrace=driver.findElement(By.xpath("//*[@id='co_abuse_parent1Race']")).getText();
						String actparssn=driver.findElement(By.xpath("//*[@id='co_abuse_parent1SSN']")).getText();
						String expparrace="I Prefer Not To Answer";
						String expparssn ="Not Provided";
						String actparracessn = actparrace+actparssn;
						String expparracessn =expparrace+expparssn;
						String actparresult = actualpardob+actpargender+actparrace+actparssn;
						String expparresult = parenrDob+exppargender+expparracessn;
						if(actualpardob.equals(parenrDob) && actpargender.equalsIgnoreCase(exppargender) && actparracessn.equalsIgnoreCase(expparracessn))
						{
							sc.STAF_ReportEvent("Pass", "Abuse-CO-Consent", "Parent Consent text matched - Verified dob,gender,race,ssn as expected." + actparresult + expparresult , 1);
						}
						else{
							sc.STAF_ReportEvent("Fail", "Abuse-CO-Consent", "Parent Consent text not matched - Verified dob,gender,race,ssn is not as expected." + actparresult + expparresult, 1);
						}
						String actualchildinfo= driver.findElement(By.xpath("//*[@id='co_abuse_child1Name']")).getText();
						String expchildinfo ="Childfstnme"+" "+"Childmdlnme"+" "+"Childlstnme"+" "+"DOB: 4/20/2002 Sex: Female";
						if(actualchildinfo.equals(expchildinfo))
						{
							sc.STAF_ReportEvent("Pass", "Abuse-CO-Consent", "Child Consent text matched - Verified name,dob,gender as expected." + actualchildinfo + expchildinfo , 1);
						}
						else{
							sc.STAF_ReportEvent("Fail", "Abuse-CO-Consent", "Child Consent text not matched - Verified name,dob,gender is not as expected." + actualchildinfo + expchildinfo, 1);
						}
						}
					    sc.clickWhenElementIsClickable("invitationStep3abuseco_signatr1_btn", (int) timeOutInSeconds);
					    tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_consentSignature_canvas", timeOutInSeconds);
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
							sc.STAF_ReportEvent("Fail", "CO Abuse page", "CO Abuse Signature popup is not displayed ", 1);
						}
						else{
						WebElement element = driver.findElement(LocatorAccess.getLocator("invitationStep3abuseco_consentSignature_canvas"));

						Actions builder = new Actions(driver);

						for(int i=150;i<400;i++){
							builder.moveToElement(element,i,40).click().build().perform();

						}
						sc.clickWhenElementIsClickable("invitationStep3abuseco_signatrclse_btn", (int) timeOutInSeconds);
						}
						
						sc.clickWhenElementIsClickable("invitationStep3abuseco_signatr2_btn", (int) timeOutInSeconds);
						tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_consentSignature_canvas", timeOutInSeconds);
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
							sc.STAF_ReportEvent("Fail", "CO Abuse page", "CO Abuse Signature popup is not displayed ", 1);
						}
						else{
						WebElement element = driver.findElement(LocatorAccess.getLocator("invitationStep3abuseco_consentSignature_canvas"));

						Actions builder = new Actions(driver);

						for(int i=150;i<400;i++){
							builder.moveToElement(element,i,40).click().build().perform();

						}
						sc.clickWhenElementIsClickable("invitationStep3abuseco_signatrclse_btn", (int) timeOutInSeconds);
						}
						
						sc.clickWhenElementIsClickable("invitationStep3abuse_continue_btn", (int) timeOutInSeconds);
						retval = Globals.KEYWORD_PASS;
					}
			}
			return retval;
		}
		
		/**************************************************************************************************
	     * Method to navigate Abuse CO page
	     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	     * @author vgokulanathan
	     * @throws Exception
	     ***************************************************************************************************/
	     public static String invitationStep3Abuseco() throws Exception{
			//if ABUSE is in package, then ABUSE will only get added if CO is select as the address state
			// if ABUSE-CO is configured in package then it will get added no matter what the address state is
			APP_LOGGER.startFunction("invitationStep3Abuseco");
			String retval = Globals.KEYWORD_FAIL;
			String tempRetval=Globals.KEYWORD_FAIL;
			int timeOutInSeconds =20;
			sc.waitForPageLoad();                                                                    
			tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_race_dd", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", "CO Abuse page", "CO Abuse page is not displayed ", 1);
			throw new Exception("CO Abuse page is not displayed");
			}
			int aliasQuantity = Integer.parseInt(Globals.testSuiteXLS.getCellData_fromTestData("NoOfAlias"));
			String Preadd=Globals.testSuiteXLS.getCellData_fromTestData("PrevAddrState");
			String Prezip=Globals.testSuiteXLS.getCellData_fromTestData("PrevAddZipCode");
			String addressLine =Globals.Volunteer_AddressLine;
			String city=Globals.Volunteer_City;
			sc.selectValue_byVisibleText("invitationStep3abuseco_race_dd", "I Prefer Not To Answer");	
			String addPrevAddress =   Globals.testSuiteXLS.getCellData_fromTestData("AddPrevAddr");
			
			//Scenario where no aliases, spouse or children are added
			if (addPrevAddress.equalsIgnoreCase("No")){
			sc.setValueJsChange("invitationStep3abuseco_preadd1_text", addressLine);
			sc.setValueJsChange("invitationStep3abuseco_preadd2_text", addressLine);
			sc.setValueJsChange("invitationStep3abuseco_precity_text", city);
			sc.selectValue_byVisibleText("invitationStep3abuseco_prestate_dd", Preadd);
			sc.setValueJsChange("invitationStep3abuseco_prezip_text", Prezip);
			}
			else {
//				String Preadd1txt = driver.findElement(LocatorAccess.getLocator("invitationStep3abuseco_preadd1_text")).getAttribute("value");
//				String Preadd2txt = driver.findElement(LocatorAccess.getLocator("invitationStep3abuseco_preadd2_text")).getAttribute("value");
//				String citytxt = driver.findElement(LocatorAccess.getLocator("invitationStep3abuseco_preadd2_text")).getAttribute("value");
//				Select select = new Select(sc.createWebElement("invitationStep3abuseco_prestate_dd"));
//				String statedd =  select.getFirstSelectedOption().getText();
//				 if(Preadd1txt.equals(Preadd) && Preadd2txt.equals(Preadd) && citytxt.equals(city) && statedd.equals(Prezip)){
//		            	sc.STAF_ReportEvent("Pass", "CO Abuse page previous address Section", "CO Abuse page previous address Section is displayed with expected values ",1);	
//		            }
//				 else{
//		            	sc.STAF_ReportEvent("Fail", "CO Abuse page previous address Section", "CO Abuse page previous address Section is not displayed with expected values ",1);
//		            }
			}
			
			//Scenario where aliases added from CO abuse page, spouse or children are added
			String additionaldata=Globals.testSuiteXLS.getCellData_fromTestData("Notes"); 
			if(additionaldata.equalsIgnoreCase("No")){
			if(aliasQuantity==0){
			tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_alias_chk", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL))
			{
				sc.STAF_ReportEvent("Fail", "CO Abuse page Alias Section", "CO Abuse page Alias Section is not displayed ", 1);
			}
			sc.clickWhenElementIsClickable("invitationStep3abuseco_alias_chk",timeOutInSeconds);
			}
			tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_spouse_chk", timeOutInSeconds);
			sc.clickWhenElementIsClickable("invitationStep3abuseco_spouse_chk",timeOutInSeconds);
			tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_childrn_chk", timeOutInSeconds);
			sc.clickWhenElementIsClickable("invitationStep3abuseco_childrn_chk",timeOutInSeconds);
			tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_review_btn", timeOutInSeconds);
			sc.clickWhenElementIsClickable("invitationStep3abuseco_review_btn",timeOutInSeconds);
			retval = Globals.KEYWORD_PASS;
			}
			else{
				tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_aliasadd_btn", timeOutInSeconds);
				    if(aliasQuantity==0){
				    	//Alias Section
				    	tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_aliasadd_btn", timeOutInSeconds);
				    	sc.clickWhenElementIsClickable("invitationStep3abuseco_aliasadd_btn",timeOutInSeconds);
				    	sc.setValueJsChange("abuseco_aliasfrstnme_txt", Globals.Volunteer_AliasName);
				    	sc.setValueJsChange("abuseco_aliasmdlnme_txt", Globals.Volunteer_AliasName);
				    	sc.setValueJsChange("abuseco_aliaslstnme_txt", Globals.Volunteer_AliasName);
				    	
					}
				    else{
				    	
				    }
				  //Parent Section
			    	tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_spouseadd_btn", timeOutInSeconds);
			    	sc.clickWhenElementIsClickable("invitationStep3abuseco_spouseadd_btn",timeOutInSeconds);
			    	sc.selectValue_byVisibleText("abusecospouse_relation_dd", "Spouse");	
			    	sc.setValueJsChange("abusecospouse_parentfrstnme_txt", "Spousefstnme");
			    	sc.setValueJsChange("abusecospouse_parentmdlnme_txt", "Spousemdlnme");
			    	sc.setValueJsChange("abusecospouse_parentlstnme_txt", "Spouselstnme");
			    	sc.selectValue_byVisibleText("abusecospouse_birthmnth_dd", "May");
			    	sc.selectValue_byVisibleText("abusecospouse_birthday_dd", "1");	
			    	sc.selectValue_byVisibleText("abusecospouse_birthyear_dd", "1996"); 
			    	sc.selectValue_byVisibleText("abusecospouse_gender_dd", "Female"); 
			    	sc.selectValue_byVisibleText("abusecospouse_race_dd", "I Prefer Not To Answer");	
			    	tempRetval=sc.waitforElementToDisplay("abusecospouse_nossn_chk", timeOutInSeconds);
					sc.clickWhenElementIsClickable("abusecospouse_nossn_chk",timeOutInSeconds);
					//Children Section
					tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_childrnadd_btn", timeOutInSeconds);
			    	sc.clickWhenElementIsClickable("invitationStep3abuseco_childrnadd_btn",timeOutInSeconds);
			    	sc.setValueJsChange("abusecochldrn_chldrnfrstnme_txt", "Childfstnme");
			    	sc.setValueJsChange("abusecochldrn_chldrnmdlnme_txt", "Childmdlnme");
			    	sc.setValueJsChange("abusecochldrn_chldrnlstnme_txt", "Childlstnme");
			    	sc.selectValue_byVisibleText("abusecochldrn_birthmnth_dd", "April");
			    	sc.selectValue_byVisibleText("abusecochldrn_birthday_dd", "20");	
			    	sc.selectValue_byVisibleText("abusecochldrn_birthyear_dd", "2002"); 
			    	sc.selectValue_byVisibleText("abusecochldrn_gender_dd", "Female"); 
			    	//Review button click
			    	tempRetval=sc.waitforElementToDisplay("invitationStep3abuseco_review_btn", timeOutInSeconds);
					sc.clickWhenElementIsClickable("invitationStep3abuseco_review_btn",timeOutInSeconds);
					
				retval = Globals.KEYWORD_PASS;
			}
			
	        return retval;
	     
	                     
	     }
	 	/**************************************************************************************************
	 	 * Method to lock a client user account
	 	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 	 * @author vgokulanathan
	 	 * @throws Exception
	 	 ***************************************************************************************************/	     
        public static String lockClientUser() throws Exception {
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
 			String intUserName=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
 			String orgUserPwd=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
 			
 			sc.addParamVal_InEmail("Organization", orgName);
 			sc.addParamVal_InEmail("OrgUserName", intUserName);
 			
 			tempRetval=sc.setValueJsChange("login_orgUsername_txt", intUserName);
 			tempRetval=sc.setValueJsChange("login_orgPassword_txt","abcdefg");
 			sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
 			
 			String expectdText = "Please provide a valid username and password";
 			tempRetval = sc.verifyText("newAccount_validationMsg_span", expectdText);
 			stepName = "Verify Account Lock for client user";

 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 				sc.STAF_ReportEvent("Pass", stepName, "Warning message displayed as expected - "+expectdText,1);
 				retval=Globals.KEYWORD_PASS;
 			}else{	
 				sc.STAF_ReportEvent("Fail", stepName, "Mismatch in warning message displayed.Expected - "+expectdText, 1);
 			}
 			sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
 			sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
 			sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
 			sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
 			sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
 			tempRetval=sc.waitforElementToDisplay("voluneerAccCreation_Forgotpassword_Edit_txt", timeOutInSeconds);
 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
 				sc.STAF_ReportEvent("Pass", stepName, "Forgot password page is displayed as expected - "+expectdText,1);
 				retval=Globals.KEYWORD_PASS;
 			}else{
 				sc.STAF_ReportEvent("Fail", stepName, "Forgot password page is not displayed as expected - "+expectdText,1);
 				retval=Globals.KEYWORD_FAIL;
 			}
 		}
 		catch(Exception e){
 			log.error("Exception occurred in Integration user Login | "+e.toString());
 			throw e;
 		}

 		return retval;
 	}
     
	   /**************************************************************************************************
		 * Method to verify  gui validations for payment in Volunteer Dashboard
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author vgokulanathan
		 * @throws Exception
		 ***************************************************************************************************/
		public static String verifyPaymentVolDashboard() throws Exception {
			Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
			APP_LOGGER.startFunction(Globals.Component);
			String retval=Globals.KEYWORD_FAIL;
			String tempretval = Globals.KEYWORD_FAIL;
	 		int timeOutinSeconds = 20;
	 		try{
	 			navigateVolunteerDashboard();
	 			Thread.sleep(2000);
	 			showColumn();
	 			String Payment="";
	 			String CandidateName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
	 			String pricingType = Globals.testSuiteXLS.getCellData_fromTestData("PricingName");
	 			String reportingStepName="Verify the  Payment value appearing on the volunteer dashboard page.";
	 			
	 			
	 				if(pricingType.equalsIgnoreCase("Client Pays All")){
	 					Payment="";
	 					retval = searchVolunteer("First Name",CandidateName,"Payment",Payment, reportingStepName);
	 					if (retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
	 						sc.STAF_ReportEvent("Pass", "Payment", "Correct payment value is displayed on volunteer dashboard", 1);
	 					}
	 					else{
	 		 				sc.STAF_ReportEvent("Fail", "Payment", "Wrong payment value is displayed on volunteer dashboard", 1);
	 		 			}
	 				}
	 				else{
	 					Payment = "$"+Globals.testSuiteXLS.getCellData_fromTestData("Payment");
	 					retval = searchVolunteer("First Name",CandidateName,"Payment",Payment, reportingStepName);
	 					if (retval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
	 						sc.STAF_ReportEvent("Pass", "Payment", "Correct payment value is displayed on volunteer dashboard", 1);
	 					}
	 					else{
	 		 				sc.STAF_ReportEvent("Fail", "Payment", "Wrong payment value is displayed on volunteer dashboard", 1);
	 		 			}
	 					}
	 			
	 		}catch(Exception e){
	 			e.printStackTrace();
	 			throw e ;
	 		}
			
			return retval;
		}
	
	/**************************************************************************************************
	 * Method to verify view functionality for single client. 
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/	     
	 public static String clientViewAssignRemoveDashboardVerify() throws Exception {
    	 APP_LOGGER.startFunction("Client View Verification");
	 	 String retval=Globals.KEYWORD_FAIL;
	 	 String tempRetval=Globals.KEYWORD_FAIL;
	 	 int timeOutinSeconds =10;
	 	 int totalVolunteer=0;
	 	 
	 	 try{
	 		tempRetval = sc.waitforElementToDisplay("bgDashboard_clientView_dd", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "BG Dashboard", "Client View Dropdown is not getting displayed", 1);
				return retval;
			}else{
				String accName=sc.testSuiteXLS.getCellData_fromTestData("AccountName").toLowerCase();
				String[] expectedItem={"All My Views","Candidates with no Views assigned",accName+"_view_1",accName+"_view_2",accName+"_view_3"};
				//List<WebElement> dropdownItem=driver.findElements(LocatorAccess.getLocator("bgDashboard_clientView_dd"));
				Select viewDropDwn = new Select(driver.findElement(By.id("viewSelect")));
				String selectedVal=viewDropDwn.getFirstSelectedOption().getText();
				tempRetval=sc.verifyDropdownItems("bgDashboard_clientView_dd", expectedItem,"Verify option of Client Views Dropdown On BG DashBoard");
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL) && !(selectedVal.equalsIgnoreCase("All My Views"))){
					sc.STAF_ReportEvent("Fail", "BG Dashboard", "Client View Dropdown options is not matched as per expected", 1);
					return retval;
				}
				navigateVolunteerDashboard();
				tempRetval=sc.verifyDropdownItems("bgDashboard_clientView_dd", expectedItem,"Verify option of Client Views Dropdown On Volunteer DashBoard");
				viewDropDwn = new Select(driver.findElement(By.id("viewSelect")));
				selectedVal=viewDropDwn.getFirstSelectedOption().getText();
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)  && !(selectedVal.equalsIgnoreCase("All My Views"))){
					sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Client View Dropdown options is not matched as per expected", 1);
					return retval;
				}
				
				totalVolunteer=Integer.parseInt(driver.findElement(By.xpath("//div[@class='sumTable-graph-label areaPadTop20']/span")).getText());
				int viewVolunteerno=0;
				//verify all 3 views should have no volunteer assigned
				int runflag=0;
				for(int i=3;i<=5;i++){
					viewDropDwn.selectByIndex(i);
					
					while(runflag<=3){				
					viewVolunteerno=Integer.parseInt(driver.findElement(By.xpath("//div[@class='sumTable-graph-label areaPadTop20']/span")).getText());
					Thread.sleep(3000);
					runflag=runflag+1;
					if(viewVolunteerno==0){
						break;
					}
					}
					
					if(!(viewVolunteerno==0)){
						sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Selected View already having some volunteer assigned. Volunteer No: "+viewVolunteerno, 1);
						return retval;
					}
				}
				
				tempRetval=sc.isEnabled("volDashboard_editView_btn");
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Edit View Button is enabled even no volunteer selected on dashboard", 1);
					return retval;
				}
				tempRetval=sc.selectValue_byVisibleText("bgDashboard_clientView_dd", "All My Views");
				runflag=0;
				//totalVolunteer=Integer.parseInt(driver.findElement(By.xpath("//div[@class='sumTable-graph-label areaPadTop20']/span")).getText());
				while(totalVolunteer!=(Integer.parseInt(driver.findElement(By.xpath("//div[@class='sumTable-graph-label areaPadTop20']/span")).getText()))){
					Thread.sleep(2000);	
					if(runflag==3){
						break;
					}
					runflag=runflag+1;
				}
				
				String assignExpectedViewItem[]={accName+"_view_1",accName+"_view_2",accName+"_view_3"};
				String assignElement[]={accName+"_view_1",accName+"_view_2",accName+"_view_3"};
				int noVolunteerView=totalVolunteer-50;
				int[] volDashViewCnt={totalVolunteer,noVolunteerView,50,50,50};
				String colVal2beVerified[]={"","",accName+"_view_1, "+accName+"_view_2, "+accName+"_view_3",accName+"_view_1, "+accName+"_view_2, "+accName+"_view_3",accName+"_view_1, "+accName+"_view_2, "+accName+"_view_3"};
				

				retval=assignViewVerifyAllDashboard("assign",assignElement,totalVolunteer,volDashViewCnt,colVal2beVerified,expectedItem,assignExpectedViewItem); 
				if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Assign Views and Verify both dashboard", "Fail to assign view and verify volunteer dashboard and BG Dashboard", 1);
					return retval;
				}
										
				navigateVolunteerDashboard();
				
				//search functionality
								
				viewDropDwn = new Select(driver.findElement(By.id("viewSelect")));
				String[] expectedSearchCriteria={accName+"_view_1",accName+"_view_1",accName+"_view_2",accName+"_view_3","_view_1"};
				for(int i=0;i<=4;i++){
					tempRetval=sc.selectValue_byVisibleText("bgDashboard_clientView_dd", expectedItem[i]);
					tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

					while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
						Thread.sleep(2000);


						tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

					}
					tempRetval=verifySearchVolunteerViews("ABCD",expectedSearchCriteria[i],expectedItem[i]);
				    
				}
				
				tempRetval=sc.selectValue_byVisibleText("bgDashboard_clientView_dd", "All My Views");
								
				//remove functionality
				tempRetval=sc.selectValue_byVisibleText("bgDashboard_clientView_dd", "clientviewauto_view_1");
				//String removeExpectedViewItem[]={accName+"_view_1",accName+"_view_2",accName+"_view_3"};
				String removeElement[]={accName+"_view_2",accName+"_view_3"};
				int noVolunteerView1=totalVolunteer-50;
				int[] volDashViewCnt1={totalVolunteer,noVolunteerView1,50,0,0};
				String colVal2beVerified1[]={"","",accName+"_view_1","",""};
				String assignExpectedViewItem2[]={accName+"_view_1",accName+"_view_2",accName+"_view_3"};
				retval=assignViewVerifyAllDashboard("remove",removeElement,totalVolunteer,volDashViewCnt1,colVal2beVerified1,expectedItem,assignExpectedViewItem2); 
				if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Remove Views and Verify both dashboard", "Fail to remove view and verify volunteer dashboard and BG Dashboard", 1);
					return retval;
				}
				
				//Rename view on inhouse portal and verify both dashboard. 
				String[] rnmView={accName+"_view_1_rename"};
				String[] viewName={accName+"_view_1"};
				retval=clientViewsRenameVerify(viewName,1,rnmView);
				if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "Rename view functionality", "Fail to rename View and verify volunteer dashboard and BG Dashboard", 1);
					return retval;
				}
			}
			
			
	 	    }catch(Exception e){
	 			log.error("Exception occurred in Client View Verification | "+e.toString());
	 			throw e;
	 		}

	 		return retval;

	 }

	 /**************************************************************************************************
		 * Method to check rename client view functionality
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @throws Exception
	 ***************************************************************************************************/	     
	  public static String clientViewsRenameVerify(String[] viewName, int viewSize, String[] rnmViewNm) throws Exception {
		  	APP_LOGGER.startFunction("Client View Rename Verification");
		 	String retval=Globals.KEYWORD_FAIL;
		 	String tempRetval=Globals.KEYWORD_FAIL;
		 	int timeOutInSeconds =10;
		 	int totalVolunteer=0;
		 	int rowID = Globals.INT_FAIL;
		 	 
		 	 try{
		 		tempRetval = InhousePortal.staffLogin();
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "BG Dashboard", "Client View Dropdown is not getting displayed", 1);
					return retval;
				}
				
				String accountName = Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
				rowID = InhousePortal.searchAccount();
				if(rowID == Globals.INT_FAIL){
					log.error("Method-InhouseAccountSearch | Unable to search acount");
					sc.STAF_ReportEvent("Fail", "Account Search", "Account not found", 1);
					return retval;
				}

				driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+rowID+"]")).click();
				InhousePortal.navigateToOtherConfigTab();	
				String stepName = "Other Configurations - Client Views Tab";
				tempRetval = sc.waitforElementToDisplay("otherConfig_ClientViews_link", timeOutInSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail","Other Configurations","Page hasnt loaded",1);
					throw new Exception("Other Configurations"+"Page hasnt loaded");
				}
				
				sc.clickWhenElementIsClickable("otherConfig_ClientViews_link",(int)  timeOutInSeconds);
				sc.clickWhenElementIsClickable("otherConfig_EditClientFeatures_edit_btn",(int)  timeOutInSeconds);
				WebElement listViewtbl=sc.createWebElement("otherConfig_ClientViews_ListViews_tbl");
				int rowViewTbl = sc.getRowCount_tbl(listViewtbl);
				 String colVal = "";
				 WebElement searchCol = null;
				 int viewFound = 0;
				 int j =0;
				 for(int i=1;i<=rowViewTbl;i++){
					searchCol = listViewtbl.findElement(By.xpath("./tbody/tr["+i+"]/td[1]/input"));
					colVal = searchCol.getAttribute("value");
					if(ArrayUtils.contains(viewName,colVal)){
						viewFound = viewFound+1;
						Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+i+"]/td[1]/input[@id='Name']")).clear();
						Globals.driver.findElement(By.xpath("//table[@class='results selectClick']/tbody/tr["+i+"]/td[1]/input[@id='Name']")).sendKeys(rnmViewNm[j]);
						j++;
					}
				 }
                 
				 if(viewFound==viewSize){
					 sc.STAF_ReportEvent("Pass","Client View - Rename","Client View Rename successfully completed",1); 
				 }else{
					 sc.STAF_ReportEvent("Fail","Client View - Rename","Client View Rename Not successfully completed",1); 
					 return retval;
				 }
				 
				 sc.clickWhenElementIsClickable("otherConfig_ClientViews_update_btn",timeOutInSeconds);
				 retval= InhousePortal.staffUserLogout();
				
		 	   }catch(Exception e){
		 			log.error("Exception occurred in Client View Rename Verification | "+e.toString());
		 			throw e;
		 	   }

		 		return retval;
	 }
		 
		 
     /**************************************************************************************************
		 * Method to check rename client view functionality
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @throws Exception
	 ***************************************************************************************************/	     
	 public static String clientViewsRenameVerifyDashboard() throws Exception {
		APP_LOGGER.startFunction("Client View Rename Verification");
	 	String retval=Globals.KEYWORD_FAIL;
	 	String tempRetval=Globals.KEYWORD_FAIL;
	 	int timeOutInSeconds =10;
	 	int totalVolunteer=0;
	 	int rowID = Globals.INT_FAIL;
			 	 
	 	 try{
	 		tempRetval = sc.waitforElementToDisplay("bgDashboard_clientView_dd", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "BG Dashboard", "Client View Dropdown is not getting displayed", 1);
				return retval;
			}else{
				String accName=sc.testSuiteXLS.getCellData_fromTestData("AccountName").toLowerCase();
				String[] expectedItem={"All My Views","Candidates with no Views assigned",accName+"_view_1_rename",accName+"_view_2",accName+"_view_3"};
				//List<WebElement> dropdownItem=driver.findElements(LocatorAccess.getLocator("bgDashboard_clientView_dd"));
				Select viewDropDwn = new Select(driver.findElement(By.id("viewSelect")));
				String selectedVal=viewDropDwn.getFirstSelectedOption().getText();
				tempRetval=sc.verifyDropdownItems("bgDashboard_clientView_dd", expectedItem,"Verify option of Client Views Dropdown On BG DashBoard");
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL) && !(selectedVal.equalsIgnoreCase("All My Views"))){
					sc.STAF_ReportEvent("Fail", "BG Dashboard", "Client View Dropdown options is not matched as per expected", 1);
					return retval;
				}
				navigateVolunteerDashboard();
				tempRetval=sc.verifyDropdownItems("bgDashboard_clientView_dd", expectedItem,"Verify option of Client Views Dropdown On Volunteer DashBoard");
				viewDropDwn = new Select(driver.findElement(By.id("viewSelect")));
				selectedVal=viewDropDwn.getFirstSelectedOption().getText();
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)  && !(selectedVal.equalsIgnoreCase("All My Views"))){
					sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Client View Dropdown options is not matched as per expected", 1);
					return retval;
				}
		
				totalVolunteer=Integer.parseInt(driver.findElement(By.xpath("//div[@class='sumTable-graph-label areaPadTop20']/span")).getText());
						
				tempRetval=sc.selectValue_byVisibleText("bgDashboard_clientView_dd", "All My Views");
				int runflag=0;
				//totalVolunteer=Integer.parseInt(driver.findElement(By.xpath("//div[@class='sumTable-graph-label areaPadTop20']/span")).getText());
				while(totalVolunteer!=(Integer.parseInt(driver.findElement(By.xpath("//div[@class='sumTable-graph-label areaPadTop20']/span")).getText()))){
					Thread.sleep(2000);	
					if(runflag==3){
						break;
					}
					runflag=runflag+1;
				}
									
				String assignElement1[]={accName+"_view_2",accName+"_view_3"};
				String removeExpectedViewItem1[]={accName+"_view_1_rename"};
				int noVolunteerView1=totalVolunteer-50;
				int[] volDashViewCnt1={totalVolunteer,noVolunteerView1,50,0,0};
				String colVal2beVerified1[]={"","",accName+"_view_1_rename","",""};
				String expectedItemValue[]={"All My Views","Candidates with no Views assigned",accName+"_view_1_rename",accName+"_view_2",accName+"_view_3"};
				retval=assignViewVerifyAllDashboard("rename",assignElement1,totalVolunteer,volDashViewCnt1,colVal2beVerified1,expectedItemValue,removeExpectedViewItem1); 
					   
				navigateVolunteerDashboard();
						
						/* this code is to remove 50 volunteer for view 1 
						tempRetval=sc.selectValue_byVisibleText("bgDashboard_clientView_dd", "clientviewauto_view_1_rename");
						String removeExpectedViewItem[]={accName+"_view_1_rename"};
						String assignElement[]={accName+"_view_1_rename"};
						int noVolunteerView=totalVolunteer;
						int[] volDashViewCnt={totalVolunteer,noVolunteerView,0,0,0};
						String colVal2beVerified[]={"","","","",""};
						
						retval=assignViewVerifyAllDashboard("remove",assignElement,totalVolunteer,volDashViewCnt,colVal2beVerified,expectedItem,removeExpectedViewItem); 
						*/
				String[] rnmView={accName+"_view_1"};
				String[] viewName={accName+"_view_1_rename"};
				retval=clientViewsRenameVerify(viewName,1,rnmView);
			}	
					
			}catch(Exception e){
				log.error("Exception occurred in Client View Rename Verification | "+e.toString());
				throw e;
			}
 	 		return retval;

	 }	 
		 
		 
	 
	 /**************************************************************************************************
		 * Method to check user having no view assigned and verify client view functionality
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @throws Exception
	 ***************************************************************************************************/	     
		 public static String clientViewsNoViewUsersVerify() throws Exception {
		 	String retval=Globals.KEYWORD_FAIL;
		 	String tempRetval=Globals.KEYWORD_FAIL;
		 	int timeOutInSeconds =10;
		 	int totalVolunteer=0;
		 	int rowID = Globals.INT_FAIL;
			 	 
		 	 try{
		 		String[] expectedItem1={"All My Views"};
				//List<WebElement> dropdownItem=driver.findElements(LocatorAccess.getLocator("bgDashboard_clientView_dd"));
		 		Select viewDropDwn = new Select(driver.findElement(By.id("viewSelect")));
				String selectedVal=viewDropDwn.getFirstSelectedOption().getText();
				tempRetval=sc.verifyDropdownItems("bgDashboard_clientView_dd", expectedItem1,"Verify option of Client Views Dropdown On BG DashBoard");
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL) && !(selectedVal.equalsIgnoreCase("All My Views"))){
					sc.STAF_ReportEvent("Fail", "BG Dashboard", "Client View Dropdown options is not matched as per expected", 1);
					return retval;
				}
						
				navigateVolunteerDashboard();
				
				tempRetval=sc.verifyDropdownItems("bgDashboard_clientView_dd", expectedItem1,"Verify option of Client Views Dropdown On Volunteer DashBoard");
				viewDropDwn = new Select(driver.findElement(By.id("viewSelect")));
				selectedVal=viewDropDwn.getFirstSelectedOption().getText();
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)  && !(selectedVal.equalsIgnoreCase("All My Views"))){
					sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Client View Dropdown options is not matched as per expected", 1);
					return retval;
				}
				
				String assignElement3[]={"All My Views"};
				String removeExpectedViewItem3[]={"All My Views"};
				//int noVolunteerView=totalVolunteer-50;
				int[] volDashViewCnt3={0};
				String colVal2beVerified3[]={""};
				String expectedItemValue3[]={"All My Views"};
				retval=assignViewVerifyAllDashboard("rename",assignElement3,0,volDashViewCnt3,colVal2beVerified3,expectedItemValue3,removeExpectedViewItem3);
								 								
			  }catch(Exception e){
					log.error("Exception occurred in Client View Rename Verification | "+e.toString());
					throw e;
			  }
			 		return retval;
		}	 
			 /**************************************************************************************************
					 * Method to check user having custom views(view1 , view2) assigned and verify client view functionality
					 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
					 * @author psave
					 * @throws Exception
			 ***************************************************************************************************/	     
					 public static String clientViewsCustomViewUsersVerify() throws Exception {
						
					 	String retval=Globals.KEYWORD_FAIL;
					 	String tempRetval=Globals.KEYWORD_FAIL;
					 	int timeOutInSeconds =10;
					 	int totalVolunteer=0;
					 	int rowID = Globals.INT_FAIL;
					 	 
					 	 try{
					 		
					 		String accName=sc.testSuiteXLS.getCellData_fromTestData("AccountName").toLowerCase();
							String[] expectedItem3={"All My Views",accName+"_view_1",accName+"_view_2"};
							//List<WebElement> dropdownItem=driver.findElements(LocatorAccess.getLocator("bgDashboard_clientView_dd"));
							Select viewDropDwn = new Select(driver.findElement(By.id("viewSelect")));
							String selectedVal=viewDropDwn.getFirstSelectedOption().getText();
							tempRetval=sc.verifyDropdownItems("bgDashboard_clientView_dd", expectedItem3,"Verify option of Client Views Dropdown On BG DashBoard");
							if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL) && !(selectedVal.equalsIgnoreCase("All My Views"))){
								sc.STAF_ReportEvent("Fail", "BG Dashboard", "Client View Dropdown options is not matched as per expected", 1);
								return retval;
							}
							
							navigateVolunteerDashboard();
							
							tempRetval=sc.verifyDropdownItems("bgDashboard_clientView_dd", expectedItem3,"Verify option of Client Views Dropdown On Volunteer DashBoard");
							viewDropDwn = new Select(driver.findElement(By.id("viewSelect")));
							selectedVal=viewDropDwn.getFirstSelectedOption().getText();
							if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)  && !(selectedVal.equalsIgnoreCase("All My Views"))){
								sc.STAF_ReportEvent("Fail", "Volunteer Dashboard", "Client View Dropdown options is not matched as per expected", 1);
								return retval;
							}
							
							String assignElement4[]={"All My Views",accName+"_view_1",accName+"_view_2"};
							String removeExpectedViewItem4[]={"All My Views",accName+"_view_1",accName+"_view_2"};
							//int noVolunteerView=totalVolunteer-50;
							int[] volDashViewCnt4={50,50,0};
							String colVal2beVerified4[]={"",accName+"_view_1",""};
							String expectedItemValue4[]={"All My Views",accName+"_view_1",accName+"_view_2"};
							retval=assignViewVerifyAllDashboard("rename",assignElement4,50,volDashViewCnt4,colVal2beVerified4,expectedItemValue4,removeExpectedViewItem4); 
							
							navigateVolunteerDashboard();
							tempRetval=sc.selectValue_byVisibleText("bgDashboard_clientView_dd", accName+"_view_1");
							tempRetval=sc.uncheckCheckBox("volDashboard_Selectall_chk");
				 			tempRetval=sc.checkCheckBox("volDashboard_Selectall_chk");
				 			int runFlag=0;
				 			while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
								Thread.sleep(2000);
								tempRetval = sc.checkCheckBox("volDashboard_Selectall_chk");
								if(runFlag==2){
									break;
								}
								runFlag=runFlag+1; 
							
				 			}
				 			Thread.sleep(3000);
				 			sc.clickWhenElementIsClickable("volDashboard_editView_btn", timeOutInSeconds);
				 			
				 			tempRetval=sc.waitforElementToDisplay("volDashboard_assignView_lbl", 120);
				 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				 				sc.STAF_ReportEvent("Fail", "Volunteer Dashboard-Assign View", "Assign View section not getting displayed on volunteer dashboard", 1);
				 				return retval;
				 			}
				 			
				 			WebElement assListBx=sc.createWebElement("volDashboard_removeView_list");
				 			//Select listItem=new Select(assListBx);
				 			for(int i=0;i<1;i++)
				 			{
				 				tempRetval=sc.selectValue_byIndex(assListBx, i);
				    		    	      		        
				 			}
				 			sc.STAF_ReportEvent("Pass", "Remove Views", "Remove View section is getting displayed on volunteer dashboard", 1);    
				 			sc.clickWhenElementIsClickable("volDashboard_assignView_Save_btn", timeOutInSeconds);
				 			
							
							
							
					 	   }catch(Exception e){
					 			log.error("Exception occurred in Client View Rename Verification | "+e.toString());
					 			throw e;
					 	   }

					 		return retval;

					 
					 
					 }	 		 
		 
	 /**************************************************************************************************
		 * Method to assign view and verify views count on volunteer dashboard and BG dashboard
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @throws Exception
	 ***************************************************************************************************/	     
		 public static String assignViewVerifyAllDashboard(String cvOpration,String[] assignElement,int totalVolunteer,int[] volDashViewCnt, String[] colVal2beVerified,String expectedItemValue[], String assignExpectedViewItem[]) throws Exception {
			 APP_LOGGER.startFunction("Client View column verification");
		 	 String retval=Globals.KEYWORD_FAIL;
		 	String tempRetval=Globals.KEYWORD_FAIL;
		 	int timeOutinSeconds =60;
		 		 	 
		 	 try{
		 		String accName=sc.testSuiteXLS.getCellData_fromTestData("AccountName").toLowerCase();
	 			
	 			 	
		 		if(cvOpration.equalsIgnoreCase("assign")){ 
		 			tempRetval=sc.uncheckCheckBox("volDashboard_Selectall_chk");
		 			tempRetval=sc.checkCheckBox("volDashboard_Selectall_chk");
		 			int runFlag=0;
		 			while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
						Thread.sleep(2000);
						tempRetval = sc.checkCheckBox("volDashboard_Selectall_chk");
						if(runFlag==5){
							break;
						}
						runFlag=runFlag+1; 
					
		 			}
		 			Thread.sleep(3000);
		 			sc.clickWhenElementIsClickable("volDashboard_editView_btn", timeOutinSeconds);
		 			tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 5);

					while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
						Thread.sleep(2000);
						tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

					}
		 			tempRetval=sc.waitforElementToDisplay("volDashboard_assignView_lbl", 120);
		 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
		 				sc.STAF_ReportEvent("Fail", "Volunteer Dashboard-Assign View", "Assign View section not getting displayed on volunteer dashboard", 1);
		 				return retval;
		 			}
		 			
		 			tempRetval=sc.verifyDropdownItems("volDashboard_assignView_list", assignExpectedViewItem,"Verify list items of Client Views-Assign View list On Volunteer DashBoard");
		 			WebElement assListBx=sc.createWebElement("volDashboard_assignView_list");
		 			//Select listItem=new Select(assListBx);
		 			for(int i=0;i<assignExpectedViewItem.length;i++)
		 			{
		 				if ( ArrayUtils.contains( assignElement, assignExpectedViewItem[i] ) ) {
		 					tempRetval=sc.selectValue_byIndex(assListBx, i);
		 				}
			    		    	      		        
		 			}
		 			sc.STAF_ReportEvent("Pass", "Assign View", "Assign View section is getting displayed on volunteer dashboard", 1); 
		 			sc.clickWhenElementIsClickable("volDashboard_assignView_Save_btn", timeOutinSeconds);
		 			Thread.sleep(5000);
		 		}else if((cvOpration.equalsIgnoreCase("remove"))){
		 			tempRetval=sc.uncheckCheckBox("volDashboard_Selectall_chk");
		 			tempRetval=sc.checkCheckBox("volDashboard_Selectall_chk");
		 			int runFlag=0;
		 			while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
						Thread.sleep(2000);
						tempRetval = sc.checkCheckBox("volDashboard_Selectall_chk");
						if(runFlag==5){
							break;
						}
						runFlag=runFlag+1; 
					
		 			}
		 			Thread.sleep(3000);
		 			
		 			AccountSetting.clickButtonUntilValidation("volDashboard_editView_btn", "volDashboard_assignView_lbl");
		 			//sc.clickWhenElementIsClickable("volDashboard_editView_btn", timeOutinSeconds);
		 			tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 5);

					while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
						Thread.sleep(2000);
						tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

					}
		 			tempRetval=sc.waitforElementToDisplay("volDashboard_assignView_lbl", 120);
		 			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
		 				sc.STAF_ReportEvent("Fail", "Volunteer Dashboard-Assign View", "Assign View section not getting displayed on volunteer dashboard", 1);
		 				return retval;
		 			}
		 			
		 			tempRetval=sc.verifyDropdownItems("volDashboard_removeView_list", assignExpectedViewItem,"Verify list items of Client Views-Remove View list On Volunteer DashBoard");
		 			WebElement assListBx=sc.createWebElement("volDashboard_removeView_list");
		 			//Select listItem=new Select(assListBx);
		 			for(int i=0;i<assignExpectedViewItem.length;i++)
		 			{
		 				if ( ArrayUtils.contains( assignElement, assignExpectedViewItem[i] ) ) {
		 					tempRetval=sc.selectValue_byIndex(assListBx, i);
		 				}
			    		    	      		        
		 			}
		 			sc.STAF_ReportEvent("Pass", "Remove Views", "Remove View section is getting displayed on volunteer dashboard", 1);    
		 			sc.clickWhenElementIsClickable("volDashboard_assignView_Save_btn", timeOutinSeconds);
		 			Thread.sleep(5000);
		 						
		 		}
		 		
				//verify the count match on volunteer dashboard. Assume that 50 volunteer assigned using select all
				tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 10);

				while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
					Thread.sleep(2000);
					tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 20);

				}
				
				int noVolunteerView=totalVolunteer-50;

				//int[] volDashViewCnt={totalVolunteer,noVolunteerView,50,50,50};
				
				//String colVal2beVerified[]={"","",accName+"_view_1, "+accName+"_view_2, "+accName+"_view_3",accName+"_view_1, "+accName+"_view_2, "+accName+"_view_3",accName+"_view_1, "+accName+"_view_2, "+accName+"_view_3"};

                
				Select viewDropDwn = new Select(driver.findElement(By.id("viewSelect")));
				
				HashMap<String,Integer> volDashboardCnt =  new HashMap<String,Integer>();
				int viewVolunteerno=0;
				for(int i=0;i<expectedItemValue.length;i++){
					tempRetval=sc.selectValue_byVisibleText("bgDashboard_clientView_dd", expectedItemValue[i]);
					int runflag=0;					
					while(runflag<=3){				
						viewVolunteerno=Integer.parseInt(driver.findElement(By.xpath("//div[@class='sumTable-graph-label areaPadTop20']/span")).getText());
						Thread.sleep(3000);
						runflag=runflag+1;
						if(viewVolunteerno==volDashViewCnt[i]){
							break;

						}
					}
					
					if(!(viewVolunteerno==volDashViewCnt[i])){
						sc.STAF_ReportEvent("Fail", "Volunteer Dashboard-View", "View : "+expectedItemValue[i]+" having missmatch in volunteer count. Expected :"+volDashViewCnt[i]+" Actual :"+viewVolunteerno, 1);
						return retval;
					}else{
						sc.STAF_ReportEvent("Pass", "Volunteer Dashboard View - "+expectedItemValue[i], "View : "+expectedItemValue[i]+" -having correct number of volunteer count. Expected :"+volDashViewCnt[i]+" Actual :"+viewVolunteerno, 1);
						//get row count of  the search results table
						WebElement searchGrid = sc.createWebElement("bgDashboard_searchResults_tbl");
						int rowcount =0;
						//click on 1000 row select on vol dashboard
						sc.clickWhenElementIsClickable_andSyncPage("volDashboard_maximumRowSet_link", timeOutinSeconds);
						tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 10);

						while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
							Thread.sleep(2000);
							tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 30);

						}
						rowcount = sc.getRowCount_tbl(searchGrid);
						
						
						
						if(!(expectedItemValue[i].equalsIgnoreCase("All My Views"))){	
							
							if(volDashViewCnt[i]==0){
								if(rowcount ==1 && searchGrid.findElement(By.xpath("./tbody/tr/td")).getText().equalsIgnoreCase("No matching records.")){
									sc.STAF_ReportEvent("Pass", "Volunteer Dashboard View Name - "+expectedItemValue[i], "View volunteer count as No Matching Records match on volunteer dashboard table. ", 1);
									
								}else{
									sc.STAF_ReportEvent("Fail", "Volunteer Dashboard View Name - "+expectedItemValue[i], "View volunteer count as No Matching Records is not getting on volunteer dashboard table. ", 1);
								    return Globals.KEYWORD_FAIL;
								}
							}else{							
								tempRetval=	verifyTableViewColumn("bgDashboard_searchResults_tbl",rowcount,colVal2beVerified[i],expectedItemValue[i]);						
								if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
									return tempRetval;
								}
							}
						}
						volDashboardCnt.put("BackgroundCheckPending_"+expectedItemValue[i],Integer.parseInt(driver.findElement(LocatorAccess.getLocator("volDashboard_backgroundCheckPending_link")).getText().trim().split(" ")[0]));
						volDashboardCnt.put("PendingReview_"+expectedItemValue[i],Integer.parseInt(driver.findElement(LocatorAccess.getLocator("volDashboard_pendingReview_link")).getText().trim().split(" ")[0]));
						volDashboardCnt.put("BackgroundCheckHold_"+expectedItemValue[i],Integer.parseInt(driver.findElement(LocatorAccess.getLocator("volDashboard_backgroundCheckHold_link")).getText().trim().split(" ")[0]));
						volDashboardCnt.put("ViewAllVolunteers_"+expectedItemValue[i],Integer.parseInt(driver.findElement(LocatorAccess.getLocator("volDashboard_viewAllVolunteers_link")).getText().trim().split(" ")[0]));
					}
				}			
				
				navigateBGDashboard();
								
			    //int actualBgCheckPending=0,actualPendingReview=0,actualAllVolCnt=0,actualBgHoldCnt=0;
			    int expectedBgCheckPending=0,expectedPendingReview=0,expectedAllVolCnt=0,expectedBgHoldCnt=0;
				// need to add code for checking vol dashboard view should be match with bg dashboard which is selected
				for(int i=0;i<expectedItemValue.length;i++){
					
					tempRetval=sc.selectValue_byVisibleText("bgDashboard_clientView_dd", expectedItemValue[i]);
					tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);

					while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
						Thread.sleep(2000);
						tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

					}
					tempRetval = sc.waitforElementToDisplay("volDashboard_maximumRowSet_link", 6);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						sc.clickWhenElementIsClickable_andSyncPage("volDashboard_maximumRowSet_link", 6);
						Thread.sleep(2000);
					}
					
					tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);
				
					while (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
						Thread.sleep(2000);
						tempRetval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

					}
					
					int actualBgCheckPending=0,actualPendingReview=0,actualAllVolCnt=0,actualBgHoldCnt=0;
					//total volunteer count
					for(int j=1;j<=2;j++){
					  
					  actualBgCheckPending = actualBgCheckPending + (Integer.parseInt(driver.findElement(By.xpath("//div[@id='sumTable']//div["+j+"]/ul/li[1]/a/span[1]")).getText()));
					  actualPendingReview =  actualPendingReview + (Integer.parseInt(driver.findElement(By.xpath("//div[@id='sumTable']//div["+j+"]/ul/li[2]/a/span[1]")).getText()));  	
					  actualPendingReview =  actualPendingReview + (Integer.parseInt(driver.findElement(By.xpath("//div[@id='sumTable']//div["+j+"]/ul/li[3]/a/span[1]")).getText()));
					  actualAllVolCnt = actualAllVolCnt + (Integer.parseInt(driver.findElement(By.xpath("//div[@id='sumTable']//div["+j+"]/h4/span")).getText()));
					}
					actualBgHoldCnt = (Integer.parseInt(driver.findElement(By.xpath("//li[@id='liHold']/a/span")).getText()));

					actualAllVolCnt=actualAllVolCnt+actualBgHoldCnt;
					 for(Map.Entry m:volDashboardCnt.entrySet()){    
					        String key=(String) m.getKey();  
					        int b=(int) m.getValue();  
					        if(key.equalsIgnoreCase("BackgroundCheckPending_"+expectedItemValue[i])){
					        	expectedBgCheckPending=b;
					        }
					        if(key.equalsIgnoreCase("PendingReview_"+expectedItemValue[i])){
					        	expectedPendingReview=b;
					        }
					        if(key.equalsIgnoreCase("BackgroundCheckHold_"+expectedItemValue[i])){
					        	expectedBgHoldCnt=b;
					        }
					        if(key.equalsIgnoreCase("ViewAllVolunteers_"+expectedItemValue[i])){
					        	expectedAllVolCnt=b;
					        }
					        
					    }    
					 int expectedAllVolCnt1=expectedBgCheckPending+expectedPendingReview+expectedBgHoldCnt;
					//compare actual vs expected
					if(!(actualBgCheckPending==expectedBgCheckPending) || !(actualPendingReview==expectedPendingReview) || !(actualAllVolCnt==expectedAllVolCnt1)){
						sc.STAF_ReportEvent("Fail", "BG Dashboard-Volunteer Count-"+expectedItemValue[i], "Volunteer count missmatched between BG dashboard and Volunteer Dashboard", 1);
						return retval;
					}else{
						WebElement searchGrid = sc.createWebElement("bgDashboard_searchResults_tbl");
						int rowcount =0;
						//click on 1000 row select on vol dashboard
						sc.clickWhenElementIsClickable_andSyncPage("volDashboard_maximumRowSet_link", timeOutinSeconds);
						sc.scrollIntoView("bgDashboard_clientView_dd");
						rowcount = sc.getRowCount_tbl(searchGrid) + expectedBgHoldCnt ;
						
						if(expectedAllVolCnt1==0){
							if(rowcount ==1 && searchGrid.findElement(By.xpath("./tbody/tr/td")).getText().equalsIgnoreCase("No matching records.")){
								sc.STAF_ReportEvent("Pass", "BG Dashboard-Volunteer Count-"+expectedItemValue[i], "Volunteer count matched between BG dashboard and Volunteer Dashboard. View Name-"+expectedItemValue[i]+" , Expected BG check Pending Cnt-"+expectedBgCheckPending+" ,Expected Pending Review Cnt-"+expectedPendingReview+" ,Expected All Volunteer Cnt-"+expectedAllVolCnt1, 1);
								retval=Globals.KEYWORD_PASS;
							}else{
								sc.STAF_ReportEvent("Fail", "BG Dashboard-Volunteer Count-"+expectedItemValue[i], "Volunteer count missmatched between BG dashboard table row and status lable count View Name-"+expectedItemValue[i]+" ,Expected volunteer Cnt-"+expectedAllVolCnt1+" ,Actual BG dashboard Row Cnt : "+rowcount, 1);
							}
						}else{	
												
							if(rowcount==expectedAllVolCnt1){
								sc.STAF_ReportEvent("Pass", "BG Dashboard-Volunteer Count-"+expectedItemValue[i], "Volunteer count matched between BG dashboard and Volunteer Dashboard. View Name-"+expectedItemValue[i]+"- Expected BG check Pending Cnt-"+expectedBgCheckPending+" ,Expected Pending Review Cnt-"+expectedPendingReview+" ,Expected All Volunteer Cnt-"+expectedAllVolCnt1, 1);
								retval=Globals.KEYWORD_PASS;
							}else{
								sc.STAF_ReportEvent("Fail", "BG Dashboard-Volunteer Count-"+expectedItemValue[i], "Volunteer count missmatched between BG dashboard table row and status lable count View Name-"+expectedItemValue[i]+"- Expected volunteer Cnt-"+expectedAllVolCnt1+" ,Actual BG dashboard Row Cnt : "+rowcount, 1);	
							}
						
						}
					}
					
				}	
 				
		 	    }catch(Exception e){
		 			log.error("Exception occurred in Client View Verification | "+e.toString());
		 			throw e;
		 		}
		 		return retval;
		 }
	 
	 /**************************************************************************************************

		 * Method to verify table with view column
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise

		 * @author psave
		 * @throws Exception
	 ***************************************************************************************************/	     
		 public static String verifyTableViewColumn(String tblName,int rowCnt, String colVal2beVerified , String viewName) throws Exception {

			 APP_LOGGER.startFunction("Client View column verification");

		 	 String retval=Globals.KEYWORD_FAIL;
		 	int timeOutinSeconds =10;
		 		 	 
		 	 try{
		 		 	WebElement volGrid=sc.createWebElement(tblName);
		 			//get col index
		 			List<WebElement> tblHeaders = volGrid.findElements(By.xpath("./thead//tr[@class='trTop']/th"));
		 			int colIndex = 0;
		 			int j =0;
		 			boolean colFound = false;
		 			String colName2beVerified = "Views";
		 			for(j=0;j<tblHeaders.size();j++){
		 				if(colName2beVerified.equalsIgnoreCase(tblHeaders.get(j).getText())){
		 					colIndex = j;
		 					colFound = true;
		 					break;
		 				}
		 			}
		 			if(!colFound){
		 				sc.STAF_ReportEvent("Fail", "Volunteer Dashboard Table- View Column", "Unable to search as column not found.Column-"+colName2beVerified, 0);
		 			}else{	 		 
		 							
		 				int rowCount = 0;
		 				rowCount = sc.getRowCount_tbl(volGrid);
		 				String colVal = "";
		 				WebElement searchCol = null;
		 				boolean mismatchFound = false;
		 				colIndex = colIndex +1;
		 				int i;
		 				if(!(rowCount==rowCnt)){
		 					sc.STAF_ReportEvent("Fail", "Volunteer Dashboard - View - "+viewName, "Volunteer count miss match as per volunteer dashboard table.", 1);

		 				    return Globals.KEYWORD_FAIL;
		 				}else{
		 					sc.STAF_ReportEvent("Pass", "Volunteer Dashboard View Name - "+viewName, viewName+" - View volunteers count match on volunteer dashboard table", 1);
 						}
		 				for(i=1;i<=rowCount;i++){
		 					searchCol = volGrid.findElement(By.xpath("./tbody/tr["+i+"]/td["+colIndex+"]"));
		 					colVal = searchCol.getText().trim();
		 					if(!colVal.toLowerCase().contains(colVal2beVerified.toLowerCase().trim())){
		 						mismatchFound = true;
		 						sc.scrollIntoView(searchCol);
		 						break;
		 					}
		 				}
		 				if(mismatchFound){
		 					sc.STAF_ReportEvent("Fail", "Volunteer Dashboard Table- View Column for View Name - "+viewName, "Mismatch found in search result. Column number-"+i+" ExpectedVal ="+colVal2beVerified, 1);
		 					return Globals.KEYWORD_FAIL;
		 				}else{
		 					sc.scrollIntoView("bgDashboard_clientView_dd");
		 					sc.STAF_ReportEvent("Pass", "Volunteer Dashboard Table- View Column for View Name - "+viewName, "All rows having expected view column value : "+colVal2beVerified, 1);
		 					retval = Globals.KEYWORD_PASS;
		 				}
		 		  }
									
		 	    }catch(Exception e){
		 			log.error("Exception occurred in Client View Verification | "+e.toString());
		 			throw e;
		 		}

		 		return retval;
	 
		 }
		 
		 /**************************************************************************************************
			 * Method for an organization user to log out from the application
			 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
			 * @author psave
			 * @throws Exception
			 ***************************************************************************************************/
			public static String orgUserLogOff() throws Exception {
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
			 * Method for an organization user login by launching the corresponding url
			 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
			 * @author psave
			 * @throws Exception
			 ***************************************************************************************************/
			public static String orgUserLogin(String orgUserName) throws Exception {
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
								orgUserLogOff();

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
							tempRetval = orgUserLogOff();
							if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
								
									sc.STAF_ReportEvent("Fail", "OrganizationUserLogin", "Login Page has NOT been loaded", 1);
									log.error("Organizational Unable to Log In As URL not Launched ");
									throw new Exception("Organizational user Unable to Log In As Lo page has not Launched");
							}
								
						}
					}

						//sc.STAF_ReportEvent("Pass", "OrganizationUserLogin", "Login Page has been loaded", 1);

						//field level validation for login page
						// verify and set account details
						String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
		               
						if(fieldLevelValidation.equalsIgnoreCase("Yes")){
							ClientFacingApp.verifyOrgUserLoginPage();
						}


						String orgName=Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
						//String orgUserName=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");						
						String orgUserPwd=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
						
						sc.addParamVal_InEmail("Organization", orgName);
						sc.addParamVal_InEmail("OrgUserName", orgUserName);
						
						tempRetval=sc.setValueJsChange("login_orgUsername_txt", orgUserName);
						tempRetval=sc.setValueJsChange("login_orgPassword_txt",orgUserPwd);

						log.debug(tempRetval + " Setting value for username and password for Org login");
						//tempRetval=sc.highlight("login_orgLogIn_btn");
						sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);

						//if user is logging in for the first time, consent page is loaded
						tempRetval = Globals.KEYWORD_FAIL;
						tempRetval = sc.waitforElementToDisplay("consent_termsAndConditions_chk",10);
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.checkCheckBox("consent_termsAndConditions_chk");
							sc.clickWhenElementIsClickable("consent_submit_btn", timeOutInSeconds);
						}
						
						tempRetval = sc.waitforElementToDisplay("Survey_skip_btn",10);
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.clickWhenElementIsClickable("Survey_skip_btn", timeOutInSeconds);
						}
						tempRetval = sc.waitforElementToDisplay("clientBGDashboard_resourceHubPopup_window",5);
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.clickElementUsingJavaScript("clientBGDashboard_resourceHubPopupClose_btn");
						}

						if (sc.waitforElementToDisplay("clientBGDashboard_resourceHub_link",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){
							log.info("Organization User Logged In | Org Username - "+ orgUserName + " | Org Password - "+orgUserPwd);
							sc.STAF_ReportEvent("Pass", "Organization User Login", "User has logged in - " + orgUserName , 1);
							retval=Globals.KEYWORD_PASS;	

						}else{
							sc.STAF_ReportEvent("Fail", "Organization User Login", "User unable to log in - " + orgUserName , 1);
							log.error("Organizational user unable to Log In ");
						}
				}catch(Exception e){
					log.error("Exception occurred in Organization user Login | "+e.toString());
					throw e;
				}

				return retval;
			}
			
		/**************************************************************************************************
			 * Method for verify custom sort functionality
			 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
			 * @author psave
			 * @throws Exception
		 ***************************************************************************************************/
		public static String verifyCustomSort() throws Exception {
			APP_LOGGER.startFunction("CustomSort");
			String tempRetval=Globals.KEYWORD_FAIL;
			int timeOutInSeconds = 20;
			String retval=Globals.KEYWORD_FAIL;
			String tempretval = Globals.KEYWORD_FAIL;
			int i=0;
			int tbSize=0;
			try{
				tempRetval=sc.waitTillElementDisplayed("volDashboard_customsort_btn",timeOutInSeconds);
				String btnTxt=sc.getWebText("volDashboard_customsort_btn").trim();
				//verify custom sort button text displayed as OFF , if not then turn off the custom sort functionality deleting all Column
				if(btnTxt.equalsIgnoreCase("Custom Sort OFF")){
					sc.STAF_ReportEvent("Pass", "Verify Custom Sort Button", "Custom Sort Button is getting Displayed in OFF status" , 1);
					sc.clickWhenElementIsClickable("volDashboard_customsort_btn",timeOutInSeconds);
					int flagSet=0;
					tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);
					while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
						Thread.sleep(2000);
						tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);
					}

					
					btnTxt=sc.getWebText("volDashboard_customsort_lbl").trim();
					if(!btnTxt.equalsIgnoreCase("Custom Sort")){
						sc.STAF_ReportEvent("Fail", "Verify Custom Sort Pop-up window", "Custom Sort window is not getting Displayed" , 1);
						return retval;
				     }
					tbSize=sc.getRowCount_tbl("volDashboard_customsort_tbl");
					for(i=1;i<=tbSize;i++){
						sc.clickWhenElementIsClickable("volDashboard_customsort_DeleteLastLine_btn",timeOutInSeconds);
					}
				}else{
					tempRetval=turnOffCustomSort();
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.clickWhenElementIsClickable("volDashboard_customsort_btn",timeOutInSeconds);
							btnTxt=sc.getWebText("volDashboard_customsort_lbl").trim();
							if(!btnTxt.equalsIgnoreCase("Custom Sort")){
								sc.STAF_ReportEvent("Fail", "Verify Custom Sort Pop-up window", "Custom Sort window is not getting Displayed" , 1);
								return retval;
							}
					 }else{
						   sc.STAF_ReportEvent("Fail", "Verify Custom Button", "Custom Sort button text is not displayed in OFF status" , 1);
						   return retval; 
					 }
				    
				}
				
				
				
				//verify custom sort pop-up window with all buttons present or not
				String elementArr[]={"volDashboard_customsort_addNewLine_btn","volDashboard_customsort_DeleteLastLine_btn","volDashboard_customsort_Cancel_btn","volDashboard_customsort_TurnOff_btn","volDashboard_customsort_Sort_btn"};
				String elementTxtArr[]={"Add New Line","Delete Last Line","Cancel","Turn Off Custom Sort","Sort"};
				boolean flg=false;
				for(i=0;i<elementArr.length;i++){
					btnTxt=sc.getWebText(elementArr[i]).trim();
					tempRetval=sc.isEnabled(elementArr[i]);
					if(!btnTxt.equalsIgnoreCase(elementTxtArr[i])){
						sc.STAF_ReportEvent("Fail", "Verify Custom Sort Pop-up window", btnTxt+" Button is not getting displayed on Custom Sort window" , 1);
						break;
					}else if((btnTxt.equalsIgnoreCase("Delete Last Line"))&&(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
						sc.STAF_ReportEvent("Fail", "Verify Custom Sort Pop-up window", btnTxt+" Button is Enabled on Custom Sort window" , 1);
						break;
					}else if(!(btnTxt.equalsIgnoreCase("Delete Last Line"))&&(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
						sc.STAF_ReportEvent("Fail", "Verify Custom Sort Pop-up window", btnTxt+" Button is Disabled mode on Custom Sort window" , 1);
						break;
					}else{
						flg=true;
					}
				 }
				 if(flg==true){
					sc.STAF_ReportEvent("Pass", "Verify Custom Sort Pop-up window", "UI validation for custom sort window is getting successfully" , 1);	
				 }else{
					sc.STAF_ReportEvent("Fail", "Verify Custom Sort Pop-up window", "UI validation for custom sort window is not displayed successfully" , 1);
					return retval; 
				 }
				
				 // verify custom sort popup window - table headers
				 List<WebElement> headerList = Globals.driver.findElements(By.xpath("//div[@id='multiSortModal']//table/thead/tr/th"));
			     String headerText;
			     String expectedHeaders[]={"Do this","Column","Order"};
			     i=0;
				 for(WebElement element : headerList){
					headerText = element.getText();
					if(headerText.equalsIgnoreCase(expectedHeaders[i])){
						sc.STAF_ReportEvent("Pass", "Verify Custom Sort Pop-up window - Table Headers", "Custom Sort Table Header is present in UI. Value  -"+headerText, 1);
					}else{
						sc.STAF_ReportEvent("Fail", "Verify Custom Sort Pop-up window - Table Headers", "Mismatch in Custom Sort Table Header.Expected = "+expectedHeaders[i] + " Actual = "+headerText, 1);
						return retval; 
					}
					i++;
				 }		
				
				 //verify add line limit should be 11 and verify all row dropdown, after adding 11 deleting all 11 row
				 tbSize=0;
				 String expectedItem[]={"Name","Account Name","Level/ Screen","Origin","Invitation Date","Order Date","Last Update","VV Order","Adjud Date","Volunteer Since","Restrictions","Status","Email","Phone","Address","Date Of Birth","Extra 1","Extra 2","Extra 3","Positions"};
				 String expectedItem1[]={"Ascending","Descending"};
				 flg=true;
				 for(i=1;i<=11;i++){
				 	sc.clickWhenElementIsClickable("volDashboard_customsort_addNewLine_btn",timeOutInSeconds);
					tbSize=sc.getRowCount_tbl("volDashboard_customsort_tbl");
					if(!(tbSize==i)){
						sc.STAF_ReportEvent("Fail", "Verify Custom Sort window - Add New Line", "By clicking on Add new line - New Row is not getting added on custom sort table. Row No:"+i, 1);
						flg=false;
						break;
					}
					
					 WebElement viewDropDwn = driver.findElement(By.xpath("//div[@id='multiSortModal']//table/tbody/tr["+i+"]/td[3]/select"));
					 Select viewDropDwnTxt = new Select(driver.findElement(By.xpath("//div[@id='multiSortModal']//table/tbody/tr["+i+"]/td[3]/select")));
					 String selectedVal=viewDropDwnTxt.getFirstSelectedOption().getText();
					 tempRetval=sc.verifyDropdownItems(viewDropDwn, expectedItem,"Verify option of Custom Sort column Dropdown On Custom Sort Window");
					 if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL) && !(selectedVal.equalsIgnoreCase("Name"))){
					    sc.STAF_ReportEvent("Fail", "Verify Custom Sort window - Add New Line", "Custom Sort column Dropdown options is not matched as per expected. Row: "+i, 1);
						flg=false;
						break;
					 }
					 Select viewDropDwnTxt1 = new Select(driver.findElement(By.xpath("//div[@id='multiSortModal']//table/tbody/tr["+i+"]/td[4]/select")));
					 WebElement viewDropDwn1 = driver.findElement(By.xpath("//div[@id='multiSortModal']//table/tbody/tr["+i+"]/td[4]/select"));
					 String selectedVal1=viewDropDwnTxt1.getFirstSelectedOption().getText();
					 tempRetval=sc.verifyDropdownItems(viewDropDwn1, expectedItem1,"Verify option of Custom Sort Order Dropdown On Custom Sort Window");
					 if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL) && !(selectedVal1.equalsIgnoreCase("Ascending"))){
						sc.STAF_ReportEvent("Fail", "Verify Custom Sort window - Add New Line", "Custom Sort Order Dropdown options is not matched as per expected. Row"+i, 1);
						flg=false;
						break;
					 }
				 }
					
				 tempRetval=sc.isEnabled("volDashboard_customsort_addNewLine_btn");
				 if((flg==true) && (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
				     sc.STAF_ReportEvent("Pass", "Verify Custom Sort window - Add New Line", "Added 11 row succussfully and verify Dropdown items for Column and order successfully" , 1);	
				 }else{
				      sc.STAF_ReportEvent("Fail", "Verify Custom Sort window - Add New Line", "Add new Line Button is not disabled after adding all 11 row" , 1);
				      return retval;
				 }
				 for(i=10;i>=0;i--){
				 	sc.clickWhenElementIsClickable("volDashboard_customsort_DeleteLastLine_btn",timeOutInSeconds);
					tbSize=sc.getRowCount_tbl("volDashboard_customsort_tbl");
					if(!(tbSize==i)){
						sc.STAF_ReportEvent("Fail", "Verify Custom Sort window - Delete Last Line", "By clicking on Delete Last Line - Row is not getting Deleted on custom sort table. Row No:"+i, 1);
						flg=false;
						break;
				 	}
				 }
				 tempRetval=sc.isEnabled("volDashboard_customsort_DeleteLastLine_btn");
				 if((flg==true) && (tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
					sc.STAF_ReportEvent("Pass", "Verify Custom Sort window - Delete Last Line", "Deleted 11 row succussfully" , 1);	
				 }else{
				   	 sc.STAF_ReportEvent("Fail", "Verify Custom Sort window - Delete Last Line", "Add new Line Button is not disabled after Deleting all 11 row" , 1);
				   	 return retval;
				 }
				
				 //Set up custom sort for 1 column -Invitation date as descending, verify name column on Volunteer dash-board sorted as descending order 
				 int noOfSort=1;
				 String colName[]={"InvitationDate"};
				 int sortOrder[]={1}; // 0 as ascending and 1 is for descending
				 String[] colToVerified={"Invitation Date"};
				 tempRetval=setupSortConditionVerify(noOfSort,colName,sortOrder,colToVerified);
				 
				 if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					 sc.STAF_ReportEvent("Fail", "Custom Sort 1 column - Sorting Invitation Date - Descending Order", "Sorting invitation date by descending is not completed successfully" , 1);
				   	 return retval; 
				 }else{
					 sc.STAF_ReportEvent("Pass", "Custom Sort 1 column - Sorting Invitation Date - Descending Order", "Sorting invitation date by descending order successfully" , 1);
					 tempRetval=turnOffCustomSort();
					 if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
								sc.waitTillElementDisplayed("volDashboard_customsort_btn", 60);
								sc.scrollIntoView("volDashboard_customsort_btn");
								sc.clickWhenElementIsClickable("volDashboard_customsort_btn",timeOutInSeconds);
								sc.waitTillElementDisplayed("volDashboard_customsort_lbl", 60);
								btnTxt=sc.getWebText("volDashboard_customsort_lbl").trim();
								if(!btnTxt.equalsIgnoreCase("Custom Sort")){
									sc.STAF_ReportEvent("Fail", "Verify Custom Sort Pop-up window", "Custom Sort window is not getting Displayed" , 1);
									return retval;
								}
						 }else{
							   sc.STAF_ReportEvent("Fail", "Verify Custom Button", "Custom Sort button text is not displayed in OFF status" , 1);
							   return retval; 
						 }
					 }else{
						 if(!tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							 sc.STAF_ReportEvent("Fail", "Verify Custom Button", "Custom Sort button text is not displayed in OFF status" , 1);
							 return retval;  
						 }
						 return tempRetval; 
					 }
				 }
			
				 
				
				 noOfSort=2;
				 String colName1[]={"LastScreen","Status"};
				 int sortOrder1[]={0,0}; // 0 as ascending and 1 is for descending
				 String[] colToVerified1={"Order Date","Status"};
				 tempRetval=setupSortConditionVerify(noOfSort,colName1,sortOrder1,colToVerified1);
				 if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					 sc.STAF_ReportEvent("Fail", "Custom Sort 2 column- Sorting Order date and status - Ascending Order", "Sorting Order Date and status by ascending is not completed successfully" , 1);
				   	 return retval; 
				 }else{
					 sc.STAF_ReportEvent("Pass", "Custom Sort 2 column- Sorting Order date and status - Ascending Order", "Sorting Order Date and status by ascending is completed successfully" , 1);
				 }
				 
				 
				/* 
				int noOfSort1[]={2};
				 String colName1[]={"Status","Extra2"};
				 int sortOrder1[]={0,0};
				 retval=setupSortConditionVerify(noOfSort,colName,sortOrder);
				 
					*/
				
				 
				retval=Globals.KEYWORD_PASS; 
				}catch(Exception e){
					log.error("Exception occurred in Custom Sort Verification | "+e.toString());
		 			throw e;
				}
				
				return retval;


			}
			
		/**************************************************************************************************
			 * Method for set up sort criteria and click on sort and verify sort orders
			 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
			 * @author psave
			 * @throws Exception
		 ***************************************************************************************************/
		public static String setupSortConditionVerify(int noSort, String[] nameCol, int[] nameOrder, String[] colName2beVerified) throws Exception {
			APP_LOGGER.startFunction("CustomSort");
			String tempRetval=Globals.KEYWORD_FAIL;
			int timeOutInSeconds = 20;
			String retval=Globals.KEYWORD_FAIL;
			int i=0;
			try{
				int tbSize=0;
				boolean flg=false;
				// click on add new line as per required and set column name and sort type and verify	
				for(i=0;i<noSort;i++){
				 	sc.clickWhenElementIsClickable("volDashboard_customsort_addNewLine_btn",timeOutInSeconds);
					tbSize=sc.getRowCount_tbl("volDashboard_customsort_tbl");
					if(!(tbSize==i+1)){
						sc.STAF_ReportEvent("Fail", "Verify Custom Sort window - Add New Line", "By clicking on Add new line - New Row is not getting added on custom sort table. Row No:"+i, 1);
						return retval;
					}
										
					int indx=i+1;
					WebElement viewDropDwn = driver.findElement(By.xpath("//div[@id='multiSortModal']//table/tbody/tr["+indx+"]/td[3]/select"));
					
					tempRetval=sc.selectValue_byValue(viewDropDwn, nameCol[i]);
							
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					   sc.STAF_ReportEvent("Fail", "Verify Custom Sort window - Add New Line", "Custom Sort column Dropdown options is not matched as per expected. Row: "+i+" Expected column set : "+nameCol[i], 1);
					   return retval;
					}
								
					WebElement viewDropDwn1 = driver.findElement(By.xpath("//div[@id='multiSortModal']//table/tbody/tr["+indx+"]/td[4]/select"));
					tempRetval=sc.selectValue_byIndex(viewDropDwn1, nameOrder[i]);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						sc.STAF_ReportEvent("Fail", "Verify Custom Sort window - Add New Line", "Custom Sort Order Dropdown options is not matched as per expected. Row"+i+" Expected column set : "+nameOrder[i], 1);
						return retval;								
					 }
				}
					
				sc.clickWhenElementIsClickable("volDashboard_customsort_Sort_btn", timeOutInSeconds);
				tempRetval=sc.waitTillElementDisplayed("volDashboard_maximumRowSet_link", timeOutInSeconds);
				sc.clickWhenElementIsClickable_andSyncPage("volDashboard_maximumRowSet_link", timeOutInSeconds);
				sc.waitTillElementDisplayed("volDashboard_customsort_btn", timeOutInSeconds);
				String btnTxt=sc.getWebText("volDashboard_customsort_btn").trim();
				if(btnTxt.equalsIgnoreCase("Custom Sort ON")){
					sc.STAF_ReportEvent("Pass", "Verify Custom Sort Button-After Sort", "Custom Sort Button is getting Displayed in ON status" , 1);
					
				 }else{
					  sc.STAF_ReportEvent("Fail", "Verify Custom Button-After Sort", "Custom Sort button text is not displayed in ON status" , 1);
					  return retval; 
				 }
				
				
				
				WebElement volGrid=sc.createWebElement("bgDashboard_searchResults_tbl");
			 	//get col index
			 	List<WebElement> tblHeaders = volGrid.findElements(By.xpath("./thead//tr[@class='trTop']/th"));
			 	int colIndex1 = 0;
			 	int colIndex2 = 0;
			 	int j =0;
			 	boolean colFound1 = false;
			 	boolean colFound2 = false;
			 	//String colName2beVerified2nd = "CustomField2";
			 	//String colName2beVerified1st = nameCol[0];
			 	
				Boolean colFound[]=new Boolean[colName2beVerified.length];
				int colIndex[]=new int[colName2beVerified.length];
			 	for(i=0;i<colName2beVerified.length;i++){
			 		for(j=0;j<tblHeaders.size();j++){
			 				if(colName2beVerified[i].equalsIgnoreCase(tblHeaders.get(j).getText())){
			 					colIndex[i] = j+1;
			 					colFound[i] = true;
			 					break;
			 				}else{
			 					colFound[i] = false;
			 				}
			 		}
			 	}		
			 	/*for(j=0;j<tblHeaders.size();j++){
			 				if(colName2beVerified2nd.equalsIgnoreCase(tblHeaders.get(j).getText())){
			 					colIndex2 = j;
			 					colFound2 = true;
			 					break;
			 				}			 			
			 			}
			 	*/	
			 	for(i=0;i<colFound.length;i++){
			 		if((!colFound[i])){
			 			sc.STAF_ReportEvent("Fail", "Volunteer Dashboard Table- Search Column", "Unable to search as column not found.Column-"+colName2beVerified[i], 0);
			 			return retval;	
			 		}
			 	}	 		 
			 							
			 	int rowCount = 0;
			 	rowCount = sc.getRowCount_tbl(volGrid);
			 	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			 	Date colVal;
			 	WebElement searchCol = null;
			 	boolean mismatchFound = false;
			 			 			   
			 	
			 	int noOfData=0;
			 	
			 	for(i=1;i<=rowCount;i++){
			 			searchCol = volGrid.findElement(By.xpath("./tbody/tr["+i+"]/td["+colIndex[0]+"]"));
			 			String emptyCheck=searchCol.getText().trim();
			 			if(!(emptyCheck.equals(""))){
			 				noOfData=noOfData+1;
			 			}
				 	
			 	}	
			 	
			 	Date[] singlyLinkedList=new Date[noOfData];
			 	String[] secondLinkedList=new String[noOfData];
			 				 	
			 	int x=0,y=0;			
			 	for(j=0;j<colIndex.length;j++){
			 	
			 		for(i=1;i<=rowCount;i++){
			 			
			 			searchCol = volGrid.findElement(By.xpath("./tbody/tr["+i+"]/td["+colIndex[0]+"]"));
			 			String emptyCheck=searchCol.getText().trim();
			 			if(!(emptyCheck.equals(""))){
			 				colVal = simpleDateFormat.parse(emptyCheck);
			 				
			 				if(j==0){
			 					 singlyLinkedList[x]=colVal; 
			 					 x++;
			 				}else{
			 					searchCol = volGrid.findElement(By.xpath("./tbody/tr["+i+"]/td["+colIndex[1]+"]"));
					 			emptyCheck=searchCol.getText().trim();
					 			secondLinkedList[y]=emptyCheck;
					 			y++;
			 				}
			 				
			 			}
					 	
			 	    }	
			 	}	
			 					
			 	int n = singlyLinkedList.length;
			 	boolean ass=false;
			 	
			 	if(noSort==1){ // this is executed when sort for only 1 column
			 		for (int t = 0; t < n - 1; ++t){
				       	if(nameOrder[0]==0){  //ascending order verify
				       		if (singlyLinkedList[t].compareTo(singlyLinkedList[t+1]) > 0){
		 		        		ass=true;
		 		        		break;
				       		}
				       	}else if(nameOrder[0]==1){   // descending order verify
				       		if (singlyLinkedList[t].compareTo(singlyLinkedList[t+1]) < 0){
		 		        		ass=true;
		 		        		break;
				       		}
				       	}
				 	}      	
			 	}else if(noSort==2){ //this will executed when sort for 2 column
			 		for (int t = 0; t < n - 1; ++t){
				       	if(nameOrder[0]==0){  //ascending order for 1st column
				       		if (singlyLinkedList[t].compareTo(singlyLinkedList[t+1]) == 0){ //when 1st column having same value then compare second column
				       			if(nameOrder[1]==0){//ascending order for 2nd column
				       				if (secondLinkedList[t].compareTo(secondLinkedList[t+1]) > 0){
				       					ass=true;
				       					break;
				       				}
				       			}else if(nameOrder[1]==1){//descending order for 2nd column
				       				if (secondLinkedList[t].compareTo(secondLinkedList[t+1]) < 0){
				       					ass=true;
				       					break;
				       				}
				       			}
				       		}else if (singlyLinkedList[t].compareTo(singlyLinkedList[t+1]) > 0){ // this will executed when 1st column have unique value and verify order should be in ascending order
				 		        		ass=true;
				 		        		break;
				       		}
				       		
				       	}else if(nameOrder[0]==1){   // descending order for 1st column verify
				       		if (singlyLinkedList[t].compareTo(singlyLinkedList[t+1]) == 0){
				       			if(nameOrder[1]==0){//ascending order for 2nd column
				       				if (secondLinkedList[t].compareTo(secondLinkedList[t+1]) > 0){
				       					ass=true;
				       					break;
				       				}
				       			}else if(nameOrder[1]==1){//descending order for 2nd column
				       				if (secondLinkedList[t].compareTo(secondLinkedList[t+1]) < 0){
				       					ass=true;
				       					break;
				       				}
				       			}
				       		}else if (singlyLinkedList[t].compareTo(singlyLinkedList[t+1]) < 0){// this will executed when 1st column have unique value and verify order should be in descending order
				 		        		ass=true;
				 		        		break;
				       		}
				       	}
				 	}      	
			 	
			 	}else{
			 		sc.STAF_ReportEvent("Fail", "Volunteer Dashboard Table- Verify Column Sort", "Automation is not supported more than 2 column sort. Providing sort length : "+noSort, 0);
		 			return retval;	
			 	}
			 	
			 		 		       
			 	if(ass==false){
			 					sc.STAF_ReportEvent("Pass", "Volunteer Dashboard Table- Sorting Validation", "Custom sort is working correctly, validation for volunteer dashboard column is Pass", 1);
			 	}else{
			 					sc.STAF_ReportEvent("Fail", "Volunteer Dashboard Table- Search Validation", "Custom sort is not working correctly, validation for volunteer dashboard is Fail", 1);
			 					return retval; 
			 	}
				
			 	/*
			 	sc.clickWhenElementIsClickable("volDashboard_customsort_btn",timeOutInSeconds);
				tbSize=sc.getRowCount_tbl("volDashboard_customsort_tbl");
				for(i=1;i<=tbSize;i++){
					sc.clickWhenElementIsClickable("volDashboard_customsort_DeleteLastLine_btn",timeOutInSeconds);
				}
				tbSize=sc.getRowCount_tbl("volDashboard_customsort_tbl");
				if(tbSize==0){
					sc.clickWhenElementIsClickable("volDashboard_customsort_TurnOff_btn",timeOutInSeconds);	
					String btnTxt=sc.getWebText("volDashboard_customsort_btn").trim();
					if(btnTxt.equalsIgnoreCase("Custom Sort OFF")){
						sc.STAF_ReportEvent("Pass", "Verify Custom Sort Button", "Custom Sort Button is getting Displayed in OFF status" , 1);
						
					 }else{
						  sc.STAF_ReportEvent("Fail", "Verify Custom Button", "Custom Sort button text is not displayed in OFF status" , 1);
						  return retval; 
					 }
			    }else{
			    	sc.STAF_ReportEvent("Fail", "Verify Custom Button", "Unable to delete all row to turn off custom sort" , 1);
					return retval; 
			    }
			 	*/
			 		 	
				retval=Globals.KEYWORD_PASS;
			 			
					
				}catch(Exception e){
					log.error("Exception occurred in Custom Sort Verification | "+e.toString());
					
		 			throw e;
				}
				//sc.closeAllBrowsers();
				return retval;


			}	
		
	/**************************************************************************************************
		 * Method for Turn Off Custom Sort
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @throws Exception
	 ***************************************************************************************************/
	 public static String turnOffCustomSort() throws Exception {
		APP_LOGGER.startFunction("CustomSort");
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String retval=Globals.KEYWORD_FAIL;
		int i=0;
		try{
			sc.waitTillElementDisplayed("volDashboard_customsort_btn", 60);
			sc.clickWhenElementIsClickable("volDashboard_customsort_btn",timeOutInSeconds);
			sc.waitTillElementDisplayed("volDashboard_customsort_lbl", 60);
			int tbSize=sc.getRowCount_tbl("volDashboard_customsort_tbl");
			for(i=1;i<=tbSize;i++){
				sc.clickWhenElementIsClickable("volDashboard_customsort_DeleteLastLine_btn",timeOutInSeconds);
			}
			tbSize=sc.getRowCount_tbl("volDashboard_customsort_tbl");
			if(tbSize==0){
				sc.clickWhenElementIsClickable("volDashboard_customsort_TurnOff_btn",timeOutInSeconds);	
				String btnTxt=sc.getWebText("volDashboard_customsort_btn").trim();
				if(btnTxt.equalsIgnoreCase("Custom Sort OFF")){
					sc.STAF_ReportEvent("Pass", "Verify Custom Sort Button", "Custom Sort Button is getting Displayed in OFF status" , 1);
				}	
		    }else{
		    	sc.STAF_ReportEvent("Fail", "Verify Custom Button", "Unable to delete all row to turn off custom sort" , 1);
				return retval; 
		    }
		}catch(Exception e){
			log.error("Exception occurred in Custom Sort Verification | "+e.toString());
			throw e;
		}
		retval=Globals.KEYWORD_PASS;
		return retval;
	 }
	 
	 /**************************************************************************************************
		 * Method to verify questionnaire Filter On Bg dashboard. 
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @throws Exception
	 ***************************************************************************************************/	     
		 public static String questionnaireFilter() throws Exception {
	    	 APP_LOGGER.startFunction("BG dashboard-Questionnaire Filter");
		 	 String retval=Globals.KEYWORD_FAIL;
		 	 String tempRetval=Globals.KEYWORD_FAIL;
		 	 int timeOutinSeconds =10;
		 	 int totalVolunteer=0;
		 	 
		 	 try{
		 		tempRetval = sc.waitforElementToDisplay("bgDashboard_questionnaireFilter_dd", timeOutinSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
					sc.STAF_ReportEvent("Fail", "BG Dashboard", "Questionnaire Filter Dropdown is not getting displayed", 1);
					return retval;
				}else{
					
					String[] expectedItem={"All","Favorable Answers","Unfavorable Answers","No Questionnaire"};
					//List<WebElement> dropdownItem=driver.findElements(LocatorAccess.getLocator("bgDashboard_clientView_dd"));
					Select viewDropDwn = new Select(driver.findElement(By.id("questionnaireFilter")));
					String selectedVal=viewDropDwn.getFirstSelectedOption().getText();
					tempRetval=sc.verifyDropdownItems("bgDashboard_questionnaireFilter_dd", expectedItem,"Verify option of Questionnaire Filter Dropdown On BG DashBoard");
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL) && !(selectedVal.equalsIgnoreCase("All"))){
						sc.STAF_ReportEvent("Fail", "BG Dashboard", "Questionnaire Filter Dropdown options is not matched as per expected", 1);
						return retval;
					}
					String inputName[]={"noquestionanire","questionnairepreferd","questionanireconsider"};
					String exppectedAnsAll[]={"yes","yes","yes"};
					String questColVal[]={"Blank","Clear","Consider"};
					tempRetval=questionnaireFilterSearch("All",inputName,exppectedAnsAll,questColVal);
					
					String exppectedAnsFavAns[]={"no","yes","no"};
					String questColValFavAns[]={"Blank","Clear","Blank"};
					tempRetval=questionnaireFilterSearch("Favorable Answers",inputName,exppectedAnsFavAns,questColValFavAns);
					
					String exppectedAnsNonFavAns[]={"no","no","yes"};
					String questColValNonFavAns[]={"Blank","Blank","Consider"};
					tempRetval=questionnaireFilterSearch("Unfavorable Answers",inputName,exppectedAnsNonFavAns,questColValNonFavAns);
					
					String exppectedAnsNoQust[]={"yes","no","no"};
					String questColValNoQust[]={"Blank","Blank","Blank"};
					tempRetval=questionnaireFilterSearch("No Questionnaire",inputName,exppectedAnsNoQust,questColValNoQust);
					
					
					
					
				}
                    
		 	    }catch(Exception e){
		 			log.error("Exception occurred in Client View Verification | "+e.toString());
		 			throw e;
		 		}

		 		return retval;

		 }
	 /**************************************************************************************************
			 * Method to verify questionnaire Filter On Bg dashboard. 
			 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
			 * @author psave
			 * @throws Exception
	 ***************************************************************************************************/	     
	 public static String questionnaireFilterSearch(String drpDownOption,String[] inputSearchNames, String[] expectedAns, String[] questColVal) throws Exception {
	   	
	 	 String retval=Globals.KEYWORD_FAIL;
	 	 String tempretval=Globals.KEYWORD_FAIL;
	 	 String tempretval1=Globals.KEYWORD_FAIL;
	 	 int timeOutinSeconds =10;
	 	 int i=0;
		 	 	 
	 	 try{
	 		WebElement searchGrid = sc.createWebElement("bgDashboard_searchResults_tbl");
	 		tempretval = sc.selectValue_byVisibleText("bgDashboard_questionnaireFilter_dd", drpDownOption);
	 		tempretval1 = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);
			Thread.sleep(2000);		
			while (tempretval1.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
				Thread.sleep(2000);
				tempretval1 = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

			}
	 		
	 		if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
	 			 sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter", "Unable to select dropdown option-"+drpDownOption+" From Questionnaire Filter dropdown", 1);
	 			 return retval;
	 		 }
	 		//after selecting questionnaire filter dropdown, verify questionnaire column for all row as per dropdown
	 		int rwcnt=sc.getRowCount_tbl("bgDashboard_searchResults_tbl");
	 		if(!drpDownOption.equalsIgnoreCase("All")){
	 			for(i=1;i<=rwcnt;i++){
	 					String clearDis=sc.isDisplayed(searchGrid.findElement(By.xpath("./tbody/tr["+i+"]/td[12]//img[1]")));
	 					String considerDis=sc.isDisplayed(searchGrid.findElement(By.xpath("./tbody/tr["+i+"]/td[12]//img[2]")));
	 					if(drpDownOption.equalsIgnoreCase("No Questionnaire")){
	 						if(!(clearDis.equalsIgnoreCase(Globals.KEYWORD_FAIL) && considerDis.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
								sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Questionnaire column value as per dropdown option-"+drpDownOption+" for bgdashboard Row - "+i+" is missmatched", 1);
							}
	 					}else if(drpDownOption.equalsIgnoreCase("Favorable Answers")){
	 						if(!(clearDis.equalsIgnoreCase(Globals.KEYWORD_PASS) && considerDis.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
								sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Questionnaire column value as per dropdown option-"+drpDownOption+" for bgdashboard Row - "+i+" is missmatched", 1);
							}
	 					}else if(drpDownOption.equalsIgnoreCase("Unfavorable Answers")){
	 						if(!(clearDis.equalsIgnoreCase(Globals.KEYWORD_FAIL) && considerDis.equalsIgnoreCase(Globals.KEYWORD_PASS))){
								sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Questionnaire column value as per dropdown option-"+drpDownOption+" for bgdashboard Row - "+i+" is missmatched", 1);
							}
	 					}
	 			
	 					
	 			}
	 		}
	 			 			 		
	 		for(i=0;i<inputSearchNames.length;i++){
	 			tempretval=sc.waitforElementToDisplay("bgDashboard_search_txt", timeOutinSeconds);
	 			sc.setValueJsChange("bgDashboard_search_txt", inputSearchNames[i]);
	 			String expitm=sc.getWebText("bgDashboard_search_txt");
	 			
	 			while(expitm.equalsIgnoreCase(inputSearchNames[i])){
	 				Thread.sleep(2000);
	 				sc.setValueJsChange("bgDashboard_search_txt", inputSearchNames[i]);
	 				expitm=sc.getWebText("bgDashboard_search_txt");
	 			}
	 				 			
				sc.clickWhenElementIsClickable("bgDashboard_searchBtn_btn", (int) timeOutinSeconds);

				tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);
				Thread.sleep(2000);		
				while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
					Thread.sleep(2000);
					tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);

				}

				//get row count of  the search results table
				
				int rowcount =0;
				rowcount = sc.getRowCount_tbl(searchGrid);

				if(rowcount != 1){
					sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter", "Multiple volunteers has been found during search.Unable to select the appropriate volunteer", 1);
				}else {
						String noMatchRecords = "";
					
						if(expectedAns[i].equalsIgnoreCase("no")){
							noMatchRecords=	searchGrid.findElement(By.xpath("./tbody/tr[1]/td[1]")).getText();
							String errorText="No matching records.";
							if(errorText.equalsIgnoreCase(noMatchRecords)){
								sc.STAF_ReportEvent("Pass", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Getting search criteria as expected - No match found for Vol name-"+inputSearchNames[i], 1);
							}else{
									int flagwait=0;
									boolean nomatch=false;
					 				while(flagwait>2){
					 					if(!(errorText.equalsIgnoreCase(noMatchRecords))){
					 						sc.setValueJsChange("bgDashboard_search_txt", inputSearchNames[i]);
					 						sc.clickWhenElementIsClickable("bgDashboard_searchBtn_btn", (int) timeOutinSeconds);
					 						Thread.sleep(2000);
					 						noMatchRecords=	searchGrid.findElement(By.xpath("./tbody/tr[1]/td[1]")).getText();
					 						flagwait=flagwait+1;
					 					}else{
					 						sc.STAF_ReportEvent("Pass", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Getting search criteria as expected - No match found for Vol name-"+inputSearchNames[i], 1);
					 						nomatch=true;
					 						break;
					 					}
					 				}	 			
					 				if(nomatch==false){		
					 					sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Not Getting as No match found for Vol name-"+inputSearchNames[i], 1);
										return retval;
					 				}
							}		
						}else{
							String volNameUI = searchGrid.findElement(By.xpath("./tbody/tr[1]/td[2]")).getText();
							if(volNameUI.contains(inputSearchNames[i])){
							
								if(questColVal[i].equalsIgnoreCase("Blank")){
									String flag1=sc.isDisplayed(searchGrid.findElement(By.xpath("./tbody/tr[1]/td[12]//img[1]")));
									String flag2=sc.isDisplayed(searchGrid.findElement(By.xpath("./tbody/tr[1]/td[12]//img[2]")));
									if(!(flag1.equalsIgnoreCase(Globals.KEYWORD_FAIL) && flag2.equalsIgnoreCase(Globals.KEYWORD_FAIL))){
										sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Questionnaire column for search name - "+inputSearchNames[i]+" is missmatched", 1);
										return retval;
									}else{
										sc.STAF_ReportEvent("Pass", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Getting search criteria and questionnaire column search as expected. For search name : - "+inputSearchNames[i], 1);	
									
									}
								}else if(questColVal[i].equalsIgnoreCase("Clear")){
									tempretval=sc.isDisplayed(searchGrid.findElement(By.xpath("./tbody/tr[1]/td[12]//img[1]")));
								
									if(!(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
										sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Questionnaire column for search name - "+inputSearchNames[i]+" is missmatched", 1);
										return retval;
									}else{
										sc.STAF_ReportEvent("Pass", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Getting search criteria and questionnaire column search as Clear icon. For search name : - "+inputSearchNames[i], 1);	
									}
								}else if((questColVal[i].equalsIgnoreCase("Consider"))){
									tempretval=sc.isDisplayed(searchGrid.findElement(By.xpath("./tbody/tr[1]/td[12]//img[2]")));
								
									if(!(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
										sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Questionnaire column for search name - "+inputSearchNames[i]+" is missmatched", 1);
										return retval;
									}else{
										sc.STAF_ReportEvent("Pass", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Getting search criteria and questionnaire column search as Consider Icon. For search name : - "+inputSearchNames[i], 1);	
									}							
								
								}
						}else{
							sc.STAF_ReportEvent("Fail", "BG Dashboard-Questionnaire Filter-"+drpDownOption, "Volunteers name mismatch.Kindly verify volunteer first and last name", 1);
							return retval;
						}
					}
				}
				retval=Globals.KEYWORD_PASS;
	 		 }
	 	 	}catch(Exception e){
				log.error("Exception occurred in Client View Verification | "+e.toString());
		 		throw e;
	 		}
			 return retval;

	  }
	 
	 /**************************************************************************************************
		 * Method to check whether client hierarchy dropdown present, if yes then select valid org user
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @throws Exception
		 ***************************************************************************************************/
		public static String verifyOrgUserClientHierarchyDD() throws Exception {
			String retval=Globals.KEYWORD_FAIL;
			String tempretval=Globals.KEYWORD_FAIL;
			
			long timeOutinSeconds = 5;

			try{				
				tempretval = sc.waitforElementToDisplay("ClientHierarchy_Dashboard_dd", timeOutinSeconds);
				if(tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					String accName=Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
					String drpDownText=accName.trim()+" (This Account)";
					WebElement ClientHierarchyDD=sc.createWebElement("ClientHierarchy_Dashboard_dd");
					Select select = new Select(ClientHierarchyDD);
					WebElement selectedOption = select.getFirstSelectedOption();
					String actualValue = selectedOption.getAttribute("value");
					if(!actualValue.contains(drpDownText)){
										
						List<WebElement> ddElements = ClientHierarchyDD.findElements(By.xpath("./option[not(@disabled='')]"));
						int ddItemsCount = 0;
						int i = 0;
						String ddItemText = "";
						boolean matchInDD = false;
						ddItemsCount = ddElements.size();
					
						for (WebElement ddElement : ddElements) {
							ddItemText= ddElement.getText().trim();
							if(ddItemText.contains(drpDownText)){
								matchInDD = true;
								break;			
							}
						}
						if(matchInDD==true){
							tempretval=sc.selectValue_byVisibleText(ClientHierarchyDD, ddItemText);
							if(tempretval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
								return tempretval;
							}
						}else{
							return retval;
						}
					}
					
				}	
					retval=Globals.KEYWORD_PASS;
			}catch(Exception e){
				sc.STAF_ReportEvent("Fail", "Volunteer/BG dashboard", "Unable to set client Hierarchy dropdown item as expected value.Exception-"+e.toString(), 1);
				throw new Exception("Unable to set client Hierarchy dropdown item as expected value-"+e.toString());
			}
			return retval;
		}
		
		/**************************************************************************************************
		 * Method fetch hasClear, hasConsider, hasUnperformable value from OrderSharing table
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @throws Exception
		 ***************************************************************************************************/
		public  static String fetchOrderSharingTable() throws Exception {
			APP_LOGGER.startFunction("FetchOrderSharing table");
			String retval = Globals.KEYWORD_FAIL;
			String npnOrderID =Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
			String hasClear,hasConsider,hasUnperformable;
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


		           //vvDB.connectVV_DB(); 
		           int iOrderID 				= Integer.parseInt(npnOrderID);
		           String accName=Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
		           if(Globals.testSuiteXLS.getCellData_fromTestData("SharingType").equalsIgnoreCase("HighToLowsharing")){
		        	   accName="VVReg1"; // this is for RJD-003 and OS-003
		           }
		           
		    		int rowCount = 0;
		    		String searchReqQuery 		= "select HasClearSearch, HasConsiderSearch, HasUnperformableSearch from OrderSharing where npnorderid=? and ClientId=(select ClientId from Client where AccountName='"+accName+"')";
		    		
		    		vvDB.ps = vvDB.conn.prepareStatement(searchReqQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    		vvDB.ps.setInt(1, iOrderID);
		    		vvDB.rs = vvDB.ps.executeQuery();
		    		
		    		rowCount = vvDB.getRows(vvDB.rs);
		    		if(rowCount >= 1 ){
		    			
		    			vvDB.rs.next();
		    			hasClear = vvDB.rs.getString("HasClearSearch").trim();
		    			hasConsider = vvDB.rs.getString("HasConsiderSearch").trim();
		    			hasUnperformable = vvDB.rs.getString("HasUnperformableSearch").trim();
		    			String ExpctedFlagStatus=Globals.testSuiteXLS.getCellData_fromTestData("Clear_Cons_Unper_OrderSearchTable_Flag").trim(); 
		    			String ActualFlagStatus=hasClear+"-"+hasConsider+"-"+hasUnperformable;
		    			
		    			if(ExpctedFlagStatus.equalsIgnoreCase(ActualFlagStatus)){
		    				sc.STAF_ReportEvent("Pass", "Fetch OrderSharing Table", "OrderSharing Table (HasClear-HasConsider-HasUnperformable) Flag matched Expected :"+ExpctedFlagStatus+" Actual :"+ActualFlagStatus, 0);
		    				retval = Globals.KEYWORD_PASS;
		    			}else{
		    				sc.STAF_ReportEvent("Fail", "Fetch OrderSharing Table", "OrderSharing Table (HasClear-HasConsider-HasUnperformable) Flag MissMatched Expected :"+ExpctedFlagStatus+" Actual :"+ActualFlagStatus, 0);
		    			}
		    			
		    		}else{
		    			sc.STAF_ReportEvent("Fail", "Fetch OrderSharing Table", "Unable to Fetch Order Sharing Table for npnorder"+iOrderID, 0);
		    		}
		    		                            
			}catch (Exception e) {
				log.error("Method-FetchSwestIdClientOrder | Unable to retrieve OrderSharing table value | Exception - "+ e.toString());
				sc.STAF_ReportEvent("Fail", "FetchOrderSharingTable", "Unable to retrieve OrderSharing table value", 0);
				retval = Globals.KEYWORD_FAIL;
				throw e;
			}
			return retval;
		}

		/**************************************************************************************************
		 * Method to wait for searchWaitPopup displayed
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @return 
		 * @throws Exception
		 ***************************************************************************************************/
		public  static void searchWaitPopup() throws Exception {
		String tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 6);
           int i=0;
			while (tempretval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				Thread.sleep(4000);
				tempretval = sc.waitforElementToDisplay("bgDashboard_searchWait_popup", 2);
				if(i>20){
					break;
				}
                i++;
			}
		}
		
		/**************************************************************************************************
		 * Method to complete Ordering Step-3
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author aunnikrishnan
		 * @throws Exception
		 ***************************************************************************************************/
		public static String seamlessPayementConsent() throws Exception{
			Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
			APP_LOGGER.startFunction(Globals.Component);
			String retval=Globals.KEYWORD_FAIL;
			int timeOutinSeconds = 20;
			String tempRetval=Globals.KEYWORD_FAIL;
			String addressState = null;
			Boolean spConsent=false;
			String consentChkPath=null;
			String vvscConsentTxt="";
			String accntNm =Globals.testSuiteXLS.getCellData_fromTestData("AccountName");
			String accntUse =Globals.testSuiteXLS.getCellData_fromTestData("AccountUse");
			String verifyConsent=Globals.testSuiteXLS.getCellData_fromTestData("Step3_Verify Consent");
			String commonConsentTxt="You, as a consumer, have a number of rights when it comes to your personal information and your background check report. "+accntNm+" is required by law to provide you with information regarding those rights and to gain your consent for a background check before allowing you to continue with your order. Please review and sign, by checking the boxes, to indicate your consent to begin the background check process and to acknowledge your rights under the Federal and applicable State Fair Credit Reporting Act(s).";
			//String disclosureConsentVerbiageTxt="I have read the Disclosure Regarding the Volunteer/Non-Paid Position Background Report provided by Verified Volunteers and this Authorization to Obtain Volunteer/Non-Paid Position Background Report. By my signature below, I hereby consent to the preparation by Verified Volunteers, a consumer reporting agency located at 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3, www.verifiedvolunteers.com, of background reports regarding me and the release of such reports to any organization I authorize and its designated representatives, to assist the organization in making a volunteer/non-paid position decision involving me at any time after receipt of this authorization and throughout my volunteerism/non-paid position, to the extent permitted by law. To this end, I hereby authorize, without reservation, any state or federal law enforcement agency or court, educational institution, motor vehicle record agency, credit bureau or other information service bureau or data repository, to furnish any and all information regarding me to Verified Volunteers and/or the organization itself, and authorize Verified Volunteers to provide such information to the organization. I agree that a facsimile(\"fax\"), electronic or photographic copy of this Authorization shall be as valid as the original.";
			String disclosureConsentVerbiageTxt="I have read the Disclosure Regarding the Employment and/or Volunteerism/Non-Employee Position Background Report provided by Verified Volunteers and this Authorization to Obtain Employment and/or Volunteerism/Non-Employee Position Background Report. By my signature below, I hereby consent to the preparation by Verified Volunteers, a consumer reporting agency located at 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3, www.verifiedvolunteers.com, of background reports regarding me and the release of such reports to any organization I authorize and its designated representatives, to assist the organization in making an employment and/or volunteerism/non-employee position decision involving me at any time after receipt of this authorization and throughout my employment and/or volunteerism/non-employee position, to the extent permitted by law. To this end, I hereby authorize, without reservation, any state or federal law enforcement agency or court, educational institution, motor vehicle record agency, credit bureau or other information service bureau or data repository, to furnish any and all information regarding me to Verified Volunteers and/or the organization itself, and authorize Verified Volunteers to provide such information to the organization. I agree that a facsimile(\"fax\"), electronic or photographic copy of this Authorization shall be as valid as the original. ";
			//String rightsSummaryConsentTxt="I acknowledge receipt of the preceding Consumer Financial Protection Bureau's \"A SUMMARY OF YOUR RIGHTS UNDER THE FAIR CREDIT REPORTING ACT.\"";
			String rightsSummaryConsentTxt="I acknowledge receipt of the preceding Consumer Financial Protection Bureau's \"A SUMMARY OF YOUR RIGHTS UNDER THE FAIR CREDIT REPORTING ACT.\" and \"Security Freeze Notice\"";
			String eSignatureConsentTxt="I understand that by typing my name where indicated below, I consent to the use of electronic records and signatures in the manner described above, and the electronic storage of such documents.";		
			String splStateWATxt="Washington State Employment and/or Volunteerism/Non-Employee Positions Only: I acknowledge that I am aware I have the right to request, from the consumer reporting agency, a written summary of my rights and remedies under the Washington Fair Credit Reporting Act.";
			String splStateNYTxt="NY Employment and/or Volunteerism/Non-Employee Positions Only: I acknowledge that I have received a copy of New York Correction Law Article 23-A. I have the right, upon written request, to be informed whether an investigative consumer REPORT was requested. If such a REPORT was requested, you will be provided with the name and address of the consumer reporting agency that prepared the report and you can contact that agency to inspect or receive a copy of the REPORT.";
			String splStateOthertxt="California, Massachusetts, Minnesota, New Jersey and Oklahoma Employment and/or Volunteerism/Non-Employee Positions Only: Check the box to the left if you would like a free copy of your background report from Verified Volunteers. Please note that you can access your completed report at any time through your Profile.";
			String esdConsentTxt="Consent to Use of Electronic Records and Signatures\nYou have the opportunity to complete and sign documents, as well as receive notices and other documents related to your application and background check, in electronic rather than paper form. To agree to these uses of electronic documents and signatures, and to sign this document with the same effect as physically signing your name, click the \"Sign\" button at the bottom of this page after reviewing the information below.\nIn order to sign, complete and receive documents electronically you will need the following:\na. A personal e-mail address;\nb. A computer or other device with standard e-mail software;\nc. Internet Explorer version 9 or newer, Firefox, Google Chrome, or Safari\nd. A connection to the Internet; and\ne. A printer if you want to print paper copies.\nAlternatively, you may elect to use and sign paper versions of documents related to your application, including the background check. To do so, please contact Verified Volunteers at 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3.\nBy typing your name below, you consent to sign, complete and receive documents relating to your application and background check during both this session and any future sessions relating to your application. Additionally, you consent to electronically receive: communications relating to your application and associated background check, including requests for additional information; notices of actions taken on your application required by law, including the Fair Credit Reporting Act; and notices of your rights under federal or state laws.\nYour consent applies to documents completed, signed or provided via this website, as well as to documents transmitted via email.\nYou have the right to withdraw your consent at any time by calling or writing to: Verified Volunteers, 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3 or TheAdvocates@VerifiedVolunteers.com. After withdrawing your consent, please also contact your organization to make arrangements to receive paper copies of documents and communications.\nIf your contact information changes, please call or write to: 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3 or TheAdvocates@VerifiedVolunteers.com.\nAfter consenting, you can obtain copies of documents and communications relating to your associated background check by: (1) logging into Verified Volunteers and accessing the Agreements section of 'My Profile'; or (3) calling 855-326-1820 Option 3 or emailing TheAdvocates@VerifiedVolunteers.com to request that paper copies be mailed to you at no charge.";
			if(accntUse.equals("Volunteerism")){
			    vvscConsentTxt="Disclosure Regarding Employment and/or Volunteerism/Non-Employee Position Background Report\nVerified Volunteers, 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3. www.verifiedvolunteers.com, may obtain a consumer report and/or an investigative consumer report (\"REPORT\") that contains background information about you in connection with your employment and/or volunteerism/non-employee Position. Verified Volunteers may obtain further reports throughout your employment and/or volunteerism/non-employee Position so as to update your report without providing further disclosure or obtaining additional consent.\nThe REPORT may contain information about your character, general reputation, personal characteristics and mode of living. The REPORT may include, but is not limited to, credit reports and credit history information; criminal and other public records and history; public court records; motor vehicle and driving records; and Social Security verification and address history, subject to any limitations imposed by applicable federal and state law. This information may be obtained from public record and private sources, including credit bureaus, government agencies and judicial records, and other sources.\nIf an investigative consumer REPORT is obtained, in addition to the description above, the nature and scope of any such REPORT will be for personal references.";
			}
			else{
				vvscConsentTxt="Disclosure Regarding Employment and/or Volunteerism/Non-Employee Position Background Report\nVVRegEmployment, test address Line, test address Line, test, FL 32007, may obtain a consumer report and/or an investigative consumer report (\"REPORT\") that contains background information about you in connection with your employment and/or volunteerism/non-employee position. VVRegEmployment may obtain further reports throughout your employment and/or volunteerism/non-employee position so as to update your report without providing further disclosure or obtaining additional consent.\nThe REPORT may contain information about your character, general reputation, personal characteristics and mode of living. The REPORT may include, but is not limited to, credit reports and credit history information; criminal and other public records and history; public court records; motor vehicle and driving records; and Social Security verification and address history, subject to any limitations imposed by applicable federal and state law. This information may be obtained from public record and private sources, including credit bureaus, government agencies and judicial records, and other sources.\nIf an investigative consumer REPORT is obtained, in addition to the description above, the nature and scope of any such REPORT will be for personal references.";	
			}
			String fcraConsentTxt="A Summary of Your Rights under the Fair Credit Reporting Act\nThe federal Fair Credit Reporting Act (FCRA) promotes the accuracy, fairness, and privacy of information in the files of consumer reporting agencies. There are many types of consumer reporting agencies, including credit bureaus and specialty agencies (such as agencies that sell information about check writing histories, medical records, and rental history records). Here is a summary of your major rights under the FCRA. For more information, including information about additional rights, go to www.consumerfinance.gov/learnmore or write to: Consumer Financial Protection Bureau, 1700 G Street N.W., Washington, D.C. 20552.\nYou must be told if information in your file has been used against you. Anyone who uses a credit report or another type of consumer report to deny your application for credit, insurance, or employment - or to take another adverse action against you - must tell you, and must give you the name, address, and phone number of the agency that provided the information.\nYou have the right to know what is in your file. You may request and obtain all the information about you in the files of a consumer reporting agency (your \"file disclosure\"). You will be required to provide proper identification, which may include your Social Security number. In many cases, the disclosure will be free. You are entitled to a free file disclosure if:\n- a person has taken adverse action against you because of information in your credit report;\n- you are the victim of identity theft and place a fraud alert in your file;\n- your file contains inaccurate information as a result of fraud;\n- you are on public assistance;\n- you are unemployed but expect to apply for employment within 60 days.\nIn addition, all consumers are entitled to one free disclosure every 12 months upon request from each nationwide credit bureau and from nationwide specialty consumer reporting agencies. See www.consumerfinance.gov/learnmore for additional information.\nYou have the right to ask for a credit score. Credit scores are numerical summaries of your creditworthiness based on information from credit bureaus. You may request a credit score from consumer reporting agencies that create scores or distribute scores used in residential real property loans, but you will have to pay for it. In some mortgage transactions, you will receive credit score information for free from the mortgage lender.\nYou have the right to dispute incomplete or inaccurate information. If you identify information in your file that is incomplete or inaccurate, and report it to the consumer reporting agency, the agency must investigate unless your dispute is frivolous. See www.consumerfinance.gov/learnmore for an explanation of dispute procedures.\nConsumer reporting agencies must correct or delete inaccurate, incomplete, or unverifiable information. Inaccurate, incomplete or unverifiable information must be removed or corrected, usually within 30 days. However, a consumer reporting agency may continue to report information it has verified as accurate.\nConsumer reporting agencies may not report outdated negative information. In most cases, a consumer reporting agency may not report negative information that is more than seven years old, or bankruptcies that are more than 10 years old.\nAccess to your file is limited. A consumer reporting agency may provide information about you only to people with a valid need -- usually to consider an application with a creditor, insurer, employer, landlord, or other business. The FCRA specifies those with a valid need for access.\nYou must give your consent for reports to be provided to employers. A consumer reporting agency may not give out information about you to your employer, or a potential employer, without your written consent given to the employer. Written consent generally is not required in the trucking industry. For more information, go to www.consumerfinance.gov/learnmore.\nYou may limit \"prescreened\" offers of credit and insurance you get based on information in your credit report. Unsolicited \"prescreened\" offers for credit and insurance must include a toll-free phone number you can call if you choose to remove your name and address from the lists these offers are based on. You may opt-out with the nationwide credit bureaus at 1 888 5OPTOUT (1 888 567 8688).\nYou may seek damages from violators. If a consumer reporting agency, or, in some cases, a user of consumer reports or a furnisher of information to a consumer reporting agency violates the FCRA, you may be able to sue in state or federal court.\nIdentity theft victims and active duty military personnel have additional rights. For more Information, visit www.consumerfinance.gov/learnmore.\nStates may enforce the FCRA, and many states have their own consumer reporting laws. In some cases, you may have more rights under state law. For more information, contact your state or local consumer protection agency or your state Attorney General. For more information about your federal rights, contact:\nFor questions or concerns regarding: Please contact:\n1. a. Banks, savings associations, and credit unions with total assets of over $10 billion and their affiliates. a. Bureau of Consumer Financial Protection\n1700 G Street NW\nWashington, DC 20552\nb. Such affiliates that are not banks, savings associations, or credit unions also should list in addition to the Bureau: b. Federal Trade Commission: Consumer Response Center - FCRA Washington, DC 20580\n(877) 382-4357\n2. To the extent not included in item 1 above:\na. National banks, federal savings associations, and federal branches and federal agencies of foreign banks a. Office of the Comptroller of the Currency\nCustomer Assistance Group\n1301 McKinney Street, Suite 3450\nHouston, TX 77010-9050\nb. State member banks, branches and agencies of foreign banks (other than federal branches, federal agencies, and insured state branches of foreign banks), commercial lending companies owned or controlled by foreign banks, and organizations operating under section 25 or 25A of the Federal Reserve Act. b. Federal Reserve Consumer Help Center\nPO Box 1200\nMinneapolis, MN 55480\nc. Nonmember Insured banks, Insured State Branches of Foreign Banks, and insured state savings associations c. FDIC Consumer Response Center\n1100 Walnut Street, Box #11\nKansas City, MO 64106\nd. Federal Credit Unions d. National Credit Union Administration\nOffice of Consumer Protection (OCP)\nDivision of Consumer Compliance and Outreach (DCCO)\n1775 Duke Street\nAlexandria, VA 22314\n3. Air carriers Asst. General Counsel for Aviation Enforcement & Proceedings\nAviation Consumer Protection Division\nDepartment of Transportation\n1200 New Jersey Avenue SE\nWashington, DC 20590\n4. Creditors Subject to Surface Transportation Board Office of Proceedings, Surface Transportation Board\nDepartment of Transportation\n395 E Street, SW\nWashington, DC 20423\n5. Creditors Subject to Packers and Stockyards Act Nearest Packers and Stockyards Administration area supervisor\n6. Small Business Investment Companies Associate Deputy Administrator for Capital Access\nUnited States Small Business Administration\n409 Third Street, SW, 8th Floor\nWashington, DC 20416\n7. Brokers and Dealers Securities and Exchange Commission\n100 F St NE\nWashington, DC 20549\n8. Federal Land Banks, Federal Land Bank Associations, Federal Intermediate Credit Banks, and Production Credit Associations Farm Credit Administration\n1501 Farm Credit Drive\nMcLean, VA 22102-5090\n9. Retailers, Finance Companies, and All Other Creditors Not Listed Above FTC Regional Office for region in which  the creditor operates or Federal Trade Commission: Consumer Response Center - FCRA\nWashington, DC 20580\n(877) 382-4357\nConsumers have the right to obtain a security freeze\nYou have a right to place a ''security freeze'' on your credit report, which will prohibit a consumer reporting agency from releasing information in your credit report without your express authorization. The security freeze is designed to prevent credit, loans, and services from being approved in your name without your consent. However, you should be aware that using a security freeze to take control over who gets access to the personal and financial information in your credit report may delay, interfere with, or prohibit the timely approval of any subsequent request or application you make regarding a new loan, credit, mortgage, or any other account involving the extension of credit. As an alternative to a security freeze, you have the right to place an initial or extended fraud alert on your credit file at no cost. An initial fraud alert is a 1-year alert that is placed on a consumer's credit file. Upon seeing a fraud alert display on a consumer's credit file, a business is required to take steps to verify the consumer's identity before extending new credit. If you are a victim of identity theft, you are entitled to an extended fraud alert, which is a fraud alert lasting 7 years. A security freeze does not apply to a person or entity, or its affiliates, or collection agencies acting on behalf of the person or entity, with which you have an existing account that requests information in your credit report for the purposes of reviewing or collecting the account. Reviewing the account includes activities related to account maintenance, monitoring, credit line increases, and account upgrades and enhancements.";
			String nya23ConsentTxt="New York Article 23-A Correction Law\n§750. Definitions. For the purposes of this article, the following terms shall have the following meanings:\n(1)\"Public agency\"; means the state or any local subdivision thereof, or any state or local department, agency, board or commission.\n(2)\"Private employer\"; means any person, company, corporation, labor organization or association which employs ten or more persons.\n(3)\"Direct relationship\"; means that the nature of criminal conduct for which the person was convicted has a direct bearing on his fitness or ability to perform one or more of the duties or responsibilities necessarily related to the license, opportunity, or job in question.\n(4)\"License\"; means any certificate, license, permit or grant of permission required by the laws of this state, its political subdivisions or instrumentalities as a condition for the lawful practice of any occupation, employment, trade, vocation, business, or profession. Provided, however, that \"license\"; shall not, for the purposes of this article, include any license or permit to own, possess, carry, or fire any explosive, pistol, handgun, rifle, shotgun, or other firearm.\n(5)\"Employment\"; means any occupation, vocation or employment, or any form of vocational or educational training. Provided, however, that \"employment\"; shall not, for the purposes of this article, include membership in any law enforcement agency.\n§751. Applicability. The provisions of this article shall apply to any application by any person for a license or employment at any public or private employer, who has previously been convicted of one or more criminal offenses in this state or in any other jurisdiction, and to any license or employment held by any person whose conviction of one or more criminal offenses in this state or in any other jurisdiction preceded such employment or granting of a license, except where a mandatory forfeiture, disability or bar to employment is imposed by law, and has not been removed by an executive pardon, certificate of relief from disabilities or certificate of good conduct. Nothing in this article shall be construed to affect any right an employer may have with respect to an intentional misrepresentation in connection with an application for employment made by a prospective employee or previously made by a current employee.\n§752. Unfair discrimination against persons previously convicted of one or more criminal offenses prohibited. No application for any license or employment, and no employment or license held by an individual, to which the provisions of this article are applicable, shall be denied or acted upon adversely by reason of the individual's having been previously convicted of one or more criminal offenses, or by reason of a finding of lack of \"good moral character\"; when such finding is based upon the fact that the individual has previously been convicted of one or more criminal offenses, unless:\n(1) There is a direct relationship between one or more of the previous criminal offenses and the specific license or employment sought or held by the individual; or\n(2) the issuance or continuation of the license or the granting or continuation of the employment would involve an unreasonable risk to property or to the safety or welfare of specific individuals or the general public.\n§753. Factors to be considered concerning a previous criminal conviction; presumption. 1. In making a determination pursuant to section seven hundred fifty-two of this chapter, the public agency or private employer shall consider the following factors:\n(a) The public policy of this state, as expressed in this act, to encourage the licensure and employment of persons previously convicted of one or more criminal offenses.\n(b) The specific duties and responsibilities necessarily related to the license or employment sought or held by the person.\n(c) The bearing, if any, the criminal offense or offenses for which the person was previously convicted will have on his fitness or ability to perform one or more such duties or responsibilities.\n(d) The time which has elapsed since the occurrence of the criminal offense or offenses.\n(e) The age of the person at the time of occurrence of the criminal offense or offenses.\n(f) The seriousness of the offense or offenses.\n(g) Any information produced by the person, or produced on his behalf, in regard to his rehabilitation and good conduct.\n(h) The legitimate interest of the public agency or private employer in protecting property, and the safety and welfare of specific individuals or the general public.\n2. In making a determination pursuant to section seven hundred fifty-two of this chapter, the public agency or private employer shall also give consideration to a certificate of relief from disabilities or a certificate of good conduct issued to the applicant, which certificate shall create a presumption of rehabilitation in regard to the offense or offenses specified therein.\n§754. Written statement upon denial of license or employment. At the request of any person previously convicted of one or more criminal offenses who has been denied a license or employment, a public agency or private employer shall provide, within thirty days of a request, a written statement setting forth the reasons for such denial.\n§755. Enforcement. 1. In relation to actions by public agencies, the provisions of this article shall be enforceable by a proceeding brought pursuant to article seventy-eight of the civil practice law and rules. 2. In relation to actions by private employers, the provisions of this article shall be enforceable by the division of human rights pursuant to the powers and procedures set forth in article fifteen of the executive law, and, concurrently, by the New York city commission on human rights.";
			String cairConsnentTxt="California Disclosure Regarding Employment and/or Volunteerism/Non-Employee Position Background Report\nVerified Volunteers, 113 South College Avenue, Fort Collins, CO, 80524, 855-326-1820 Option 3, www.verifiedvolunteers.com, may obtain a consumer report and/or an investigative consumer report (\"REPORT\") that contains background information about you in connection with your employment and/or volunteerism/non-employee position. Verified Volunteers may obtain further reports throughout your employment and/or volunteerism/non-employee position so as to update your report without providing further disclosure or obtaining additional consent.\nThe REPORT may contain information about your character, general reputation, personal characteristics and mode of living. The REPORT may include, but is not limited to, credit reports and credit history information; criminal and other public records and history; public court records; motor vehicle and driving records; and Social Security verification and address history, subject to any limitations imposed by applicable federal and state law. This information may be obtained from public record and private sources, including credit bureaus, government agencies and judicial records, and other sources.\nIf an investigative consumer REPORT is obtained, in addition to the description above, the nature and scope of any such REPORT will be personal references.\nYou may inspect Verified Volunteers’ files concerning you during normal business hours and upon reasonable notice. You can inspect the files at Verified Volunteers’ offices if you furnish proper identification, and you can obtain a copy by paying duplication costs. One other person can accompany you if he or she furnishes reasonable identification. You can also obtain a copy of your files by sending Verified Volunteers at the address listed above a written request, including proper identification, by certified mail. Verified Volunteers will give you a summary of the information in the files by telephone if you submit a written request including proper identification. Verified Volunteers has trained personnel who can explain the information furnished to you, and can provide a written explanation of any coded information contained in your files. \"Proper identification\" includes documents such as a valid driver’s license, Social Security card, military identification card or credit card. If necessary, Verified Volunteers may request additional information about your employment and/or volunteerism/non-employee position and personal or family history to verify your identity.";
			String mafcraConsnentTxt="Summary of Your Rights Under the Massachusetts Consumer Credit Reporting Act\nYou have the right to obtain a free copy of your credit file from a consumer credit reporting agency. You may be charged a reasonable fee not exceeding eight dollars. There is no fee, however, if you have been turned down for credit, employment, insurance, or rental dwelling because of information in your credit report within the preceding sixty days. The consumer credit reporting agency must provide someone to help you interpret the information in your credit file. Each calendar year you are entitled to receive, upon request, one free consumer credit report.\nYou have a right to dispute inaccurate information by contacting the consumer credit reporting agency directly. However, neither you nor any credit repair company or credit service organization has the right to have accurate, current, and verifiable information removed from your credit report. In most cases, under state and federal law, the consumer credit reporting agency must remove accurate, negative information from your report only if it is over seven years old, and must remove bankruptcy information only if it is over ten years old.\nIf you have notified a consumer credit reporting agency in writing that you dispute the accuracy of information in your file, the consumer credit reporting agency must then, within thirty business days, reinvestigate and modify or remove inaccurate information. The consumer credit reporting agency may not charge a fee for this service. Any pertinent information and copies of all documents you have concerning a dispute should be given to the consumer credit reporting agency.\nIf reinvestigation does not resolve the dispute to your satisfaction, you may send a statement to the consumer credit reporting agency to keep in your file, explaining why you think the record is inaccurate. The consumer credit reporting agency must include your statement about the disputed information in a report it issues about you.\nYou have a right to receive a record of all inquiries relating to a credit transaction initiated in the six months preceding your request, or two years in the case of a credit report used for employment purposes. This record shall include the recipients of any consumer credit report.\nYou have the right to opt out of any pre-screening lists compiled by or with the assistance of a consumer credit reporting agency by calling the agency's toll-free telephone number or contacting the agency in writing. You may be entitled to collect compensation, in certain circumstances, if you are damaged by a person's negligent or intentional failure to comply with the provisions of the credit report act.";
			String njfcraConsnentTxt="Description of Your Rights under the New Jersey Fair Credit Reporting Act\nThe New Jersey Fair Credit Reporting Act is modeled after the federal Fair Credit Reporting Act and provides you with many of the same rights. You have received A Summary of Your Rights Under the Fair Credit Reporting Act.\nNew Jersey Consumers Have the Right to Obtain a Security Freeze\nYou may obtain a security freeze on your credit report to protect your privacy and ensure that credit is not granted in your name without your knowledge. You have a right to place a \"security freeze\" on your credit report pursuant to New Jersey law.\nThe security freeze will prohibit a consumer reporting agency from releasing any information in your credit report without your express authorization or approval.\nThe security freeze is designed to prevent credit, loans, and services from being approved in your name without your consent. When you place a security freeze on your credit report, within five business days you will be provided a personal identification number or password to use if you choose to remove the freeze on your credit report or to temporarily authorize the release of your credit report for a specific party, parties or period of time after the freeze is in place. To provide that authorization, you must contact the consumer reporting agency and provide all of the following:\n(i) The unique personal identification number or password provided by the consumer reporting agency;\n(ii) Proper identification to verify your identity; and\n(iii) The proper information regarding the third party or parties who are to receive the credit report or the period of time for which the report shall be available to users of the credit report.\nA consumer reporting agency that receives a request from a consumer to lift temporarily a freeze on a credit report shall comply with the request no later than three business days or less, as provided by regulation, after receiving the request.\nA security freeze does not apply to circumstances in which you have an existing account relationship and a copy of your report is requested by your existing creditor or its agents or affiliates for certain types of account review, collection, fraud control or similar activities.\nIf you are actively seeking credit, you should understand that the procedures involved in lifting a security freeze may slow your own applications for credit. You should plan ahead and lift a freeze, either completely if you are shopping around, or specifically for a certain creditor, a few days before actually applying for new credit.\nYou have a right to bring a civil action against someone who violates your rights under the credit reporting laws. The action can be brought against a consumer reporting agency or a user of your credit report.\n(2) If a consumer requests information about a security freeze, he shall be provided with the notice provided in paragraph (1) of this subsection and with any other information, as prescribed by the director by regulation, about how to place, temporarily lift and permanently lift a security freeze.";
			try{
				 sc.waitForPageLoad();
			     if(verifyConsent.equalsIgnoreCase("Yes")){
			    	 sc.waitForPageLoad();
			    	 tempRetval=sc.waitforElementToDisplay("invitationStep3_summaryConsent_chk", timeOutinSeconds);
			    	 verifyTextWithReport("invitationStep3_commonConsent_txt", commonConsentTxt,"Seamless Payment Enh- Consent Page Common Consent" );
			    	 verifyTextWithReport("invitationStep3_sm_esdConsent_txt", esdConsentTxt,"Seamless Payment Enh- Consent Page ESD Consent");
					 verifyTextWithReport("invitationStep3_sm_vvscConsent_txt", vvscConsentTxt,"Seamless Payment Enh- Consent Page VVSC Consent");
					 verifyTextWithReport("invitationStep3_sm_fcraConsent_txt", fcraConsentTxt,"v FCRA Consent");
					 verifyTextWithReport("invitationStep3_sm_disclosureConsentVerbiage_txt", disclosureConsentVerbiageTxt,"Seamless Payment Enh- Consent Page DisclosureConsentVerbiage" );
					 verifyTextWithReport("invitationStep3_sm_rightsSummaryConsent_txt", rightsSummaryConsentTxt,"Seamless Payment Enh- Consent Page RightsSummaryConsent" );
					 verifyTextWithReport("invitationStep3_eSignatureConsent_txt", eSignatureConsentTxt,"Seamless Payment Enh- Consent Page eSignatureConsent" );
			     }
				
				tempRetval=sc.waitforElementToDisplay("invitationStep3_summaryConsent_chk", timeOutinSeconds);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.waitForPageLoad();
					//if addressState is one of the following then an additional checkbox should be displayed.
					// States - California, Massachusetts, Minnesota, New Jersey , New York , Washington and Oklahoma 
					addressState = Globals.testSuiteXLS.getCellData_fromTestData("AddressState");
					if(addressState.equalsIgnoreCase("California") || addressState.equalsIgnoreCase("Massachusetts") || addressState.equalsIgnoreCase("Minnesota") || addressState.equalsIgnoreCase("New Jersey") || addressState.equalsIgnoreCase("Oklahoma")|| addressState.equalsIgnoreCase("Washington")|| addressState.equalsIgnoreCase("New_York") ){

						tempRetval=Globals.KEYWORD_FAIL;
						
						if(addressState.equalsIgnoreCase("New_York")){
						    tempRetval=sc.waitforElementToDisplay("invitationStep3_NYConsent_chk", 2);	
						    verifyTextWithReport("invitationStep3_sm_splConsentNY_txt", splStateNYTxt,"Seamless Payment Enh- Consent Page "+addressState+" Consent" );	
						    verifyTextWithReport("invitationStep3_nya23Consent_txt", nya23ConsentTxt,"Seamless Payment Enh- Consent Page "+addressState+" NY 23A Consent" );	
						    consentChkPath="invitationStep3_NYConsent_chk";
						}else if(addressState.equalsIgnoreCase("Washington")){ 
							tempRetval=sc.waitforElementToDisplay("invitationStep3_WAConsent_chk", 2);	
						    verifyTextWithReport("invitationStep3_splConsentWA_txt", splStateWATxt,"Seamless Payment Enh- Consent Page "+addressState+" Consent" );
						    consentChkPath="invitationStep3_WAConsent_chk";
						}else{
							tempRetval=sc.waitforElementToDisplay("invitationStep3_californiaConsent_chk", 2);
							verifyTextWithReport("invitationStep3_sm_splConsentOther_txt", splStateOthertxt,"Seamless Payment Enh- Consent Page "+addressState+" Consent" ); 
							if(!Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_PROD")){
							
							if(addressState.equalsIgnoreCase("California")){
							   verifyTextWithReport("invitationStep3_sm_cairConsent_txt", cairConsnentTxt,"Seamless Payment Enh- Consent Page "+addressState+" California Disclosure Regarding Consent" );
							}
							if(addressState.equalsIgnoreCase("Massachusetts")){
								   verifyTextWithReport("invitationStep3_mafcraConsent_txt", mafcraConsnentTxt,"Seamless Payment Enh- Consent Page "+addressState+"  Massachusetts Consumer Credit Reporting Act Consent" );
							}
							if(addressState.equalsIgnoreCase("New Jersey")){
								   verifyTextWithReport("invitationStep3_njfcraConsent_txt", njfcraConsnentTxt,"Seamless Payment Enh- Consent Page "+addressState+"  New Jersey Fair Credit Reporting Act Consent" );
							}	
							}
							consentChkPath="invitationStep3_californiaConsent_chk";
						}
						
													
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
							sc.STAF_ReportEvent("Pass", "Seamless Payment Enh- Consent Page", "Consent check box for "+ addressState +"is present" , 1);
							log.info("State Consent checkbox is present for state -  "+addressState);
							spConsent=true;
							

						}else{
							sc.STAF_ReportEvent("Fail", "Seamless Payment Enh- Consent Page", "Consent check box for "+ addressState +"is NOT present" , 1);
							log.error("State Consent checkbox is NOT present for state -  "+addressState);
							throw new Exception("State Consent checkbox is NOT present for state -  "+addressState);
						}

					}
					// validation if fields are blank
					sc.clickWhenElementIsClickable("invitationStep3_saveAndContinue_btn", timeOutinSeconds);
					sc.waitTillElementDisplayed(driver.findElement(By.xpath("//div[contains(text(),'Please acknowledge your consent')]")),10);
					List<WebElement> errMsg=driver.findElements(By.xpath("//div[contains(text(),'Please acknowledge your consent')]"));
					if(spConsent==true){
						if(errMsg.size()!=4){
							sc.STAF_ReportEvent("Fail", "Seamless Payment Enh- Consent Page", "Error message is not getting displayed" , 1);
						}
					}else{
						if(errMsg.size()!=3){
							sc.STAF_ReportEvent("Fail", "Seamless Payment Enh- Consent Page", "Error message is not getting displayed" , 1);
						}
					}
					List<WebElement> errMsg1=driver.findElements(By.xpath("//div[contains(text(),'This field is required')]"));
					if(errMsg1.size()!=2){
						sc.STAF_ReportEvent("Fail", "Seamless Payment Enh- Consent Page", "Error message is not getting displayed for first name and lat name" , 1);
					}else{
						sc.scrollIntoView("invitationStep3_saveAndContinue_btn");
						sc.STAF_ReportEvent("Pass", "Seamless Payment Enh- Consent Page", "Getting error message Correctly when required fields are blank" , 1);
					}
					if(spConsent==true){
						tempRetval =sc.checkCheckBox(consentChkPath);
					}
					tempRetval = sc.checkCheckBox("invitationStep3_summaryConsent_chk");
					tempRetval = sc.checkCheckBox("invitationStep3_disclosureConsent_chk");
					tempRetval = sc.checkCheckBox("invitationStep3_eSignConsent_chk");

					tempRetval = sc.waitforElementToDisplay("invitationStep3_fName_txt", timeOutinSeconds);
					tempRetval = sc.setValueJsChange("invitationStep3_fName_txt", Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName"));
					tempRetval = sc.setValueJsChange("invitationStep3_lName_txt", Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName"));

					sc.STAF_ReportEvent("Pass", "Seamless Payment Enh- Consent Page", "Consent details entered by volunteer", 1);

					sc.clickWhenElementIsClickable("invitationStep3_saveAndContinue_btn", timeOutinSeconds);
				}else{
					sc.STAF_ReportEvent("Fail", "Seamless Payment Enh- Consent Page", "Seamless Payment Enh- Consent Page has not been loaded" , 1);
					log.error("Method-Seamless Payment Enh | Consent Page has not loaded");
					throw new Exception("Seamless Payment Enh | Consent Page has not loaded");
				}
				
				//orderstep4
					sc.waitTillElementDisplayed(driver.findElement(By.xpath("//*[contains(text(),'Your Background Check Order #')]")), 10);
					String npnorder=Globals.testSuiteXLS.getCellData_fromTestData("NPNOrderID");
					String actualNpnorderid=driver.findElement(By.xpath("//*[contains(text(),'Your Background Check Order #')]")).getText().split("#")[1].trim();
					if(!npnorder.equalsIgnoreCase(actualNpnorderid)){
						sc.STAF_ReportEvent("Fail", "Seamless Payment Enh-Pricing Detail Page", "Npnorder is missmatched with APi response npnorder", 1);
						log.error("Method-Seamless Payment Enh-Pricing Detail Page has not loaded");
						throw new Exception("Unable to complete Seamless Payment Enh-Pricing Detail Page");
					}
					
					String volunteerDonationRequired =  Globals.testSuiteXLS.getCellData_fromTestData("VolunteerDonationRequired");
					//Donation section wont be present when Client Pays fees Only is selected and there is no amount in Source Fees.Need to check the pricing section first
					tempRetval = Globals.KEYWORD_FAIL;
					tempRetval = sc.waitforElementToDisplay("invitationStep4_donationSelection_dd", 2);
					String volDonationAmount;
					if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						if(volunteerDonationRequired.equalsIgnoreCase("Yes")){
							
								sc.clickWhenElementIsClickable("smConsentPaymentStep4_donation_chk", timeOutinSeconds);
								sc.waitForPageLoad();
								Thread.sleep(4000);
								volDonationAmount= sc.selectValue_byIndex("invitationStep4_donationSelection_dd", 2);
								if(volDonationAmount.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
									sc.STAF_ReportEvent("Fail", "Seamless Payment Enh-Pricing Detail Page", "Unable to select Volunteer Donation Amount", 1);
								}else{
									sc.STAF_ReportEvent("Pass", "Seamless Payment Enh-Pricing Detail Page", "Volunteer Donation Yes selected.Amount ="+volDonationAmount, 1);
								}
							
						}else{
								sc.clickWhenElementIsClickable("smConsentPaymentStep4_NoDonation_chk", timeOutinSeconds);
								sc.waitForPageLoad();
								Thread.sleep(2000);
								sc.STAF_ReportEvent("Pass", "Seamless Payment Enh-Pricing Detail Page", "Volunteer Donation Not selected", 1);
							
						}
					}else{
						if(volunteerDonationRequired.equalsIgnoreCase("Yes")){
							sc.STAF_ReportEvent("Fail", "Seamless Payment Enh-Pricing Detail Page", "Volunteer donation section should not displayed", 1);
						}else{
							sc.STAF_ReportEvent("Pass", "Seamless Payment Enh-Pricing Detail Page", "Volunteer Donation section is not getting displayed as per expected", 1);
						}
					}
					Thread.sleep(1000);
					
					//verifyProdListInvitationOrdering();
					boolean flag=verifyPricingStep4();
					
					tempRetval=sc.waitforElementToDisplay("invitationStep4_fastPassYes_headingtext", 2);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){					
						sc.STAF_ReportEvent("FAIL", "Seamless Payment Enh-Pricing Detail Page", "FastPasss is getting displayed", 1);
					}
					
					tempRetval =  sc.waitforElementToDisplay("smConsentPaymentStep4_Submit_btn", 3);
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						sc.clickWhenElementIsClickable("smConsentPaymentStep4_Submit_btn", timeOutinSeconds);
						sc.waitForPageLoad();
						//					driver.findElement(LocatorAccess.getLocator("invitationStep4_submitOrder_btn")).click();
					}else{
					
					}
					
					retval = Globals.KEYWORD_PASS;

				

			}catch(Exception e){
				retval=Globals.KEYWORD_FAIL;
				log.error("Method-invitationOrderStep3 | Exception - "+ e.toString());
				throw e;
			}
			return retval;
		}
		
		/**************************************************************************************************
		 * Method fetch hasClear, hasConsider, hasUnperformable value from OrderSharing table
		 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
		 * @author psave
		 * @return 
		 * @throws Exception
		 ***************************************************************************************************/
		public  static void fetchURLAndLaunch() throws Exception {
			try{				
				String lauchUrl=Globals.testSuiteXLS.getCellData_fromTestData("InvitationURL");
					
				 sc.launchURL(Globals.BrowserType, lauchUrl);
					
				
			
			}catch(Exception e){
				sc.STAF_ReportEvent("Fail", "Volunteer/BG dashboard", "Unable to set client Hierarchy dropdown item as expected value.Exception-"+e.toString(), 1);
				throw new Exception("Unable to set client Hierarchy dropdown item as expected value-"+e.toString());
			}
			
			
		}
		
		 /**************************************************************************************************
	     * Method to reference volunteer reaccount creation through Invitation URL 
	     * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	     * @author gaurimodani
	     * @throws Exception
	     ***************************************************************************************************/
	        public static String volunteerReaccountCreation()
	        {
	            String retval = Globals.KEYWORD_FAIL;
	            int timeOutinSeconds=30;
	            String tempRetval=Globals.KEYWORD_FAIL;
	            String username = null;
	            String confirmPassword=null;
	            String password;
	            try
	            {
	            String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
	            String urlLaunched = sc.launchURL(Globals.BrowserType, url);
	 
	            if (urlLaunched.equalsIgnoreCase(Globals.KEYWORD_PASS) && sc.verifyPageTitle("Get Started with Verified Volunteers").equalsIgnoreCase(Globals.KEYWORD_PASS)){
	                sc.STAF_ReportEvent("Pass", "Invitation URL", "Url relaunched successfully-"+url, 1);
	                retval    =    Globals.KEYWORD_PASS;                        
	            }
	            
	            tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_username_txt",timeOutinSeconds);
	            username = sc.testSuiteXLS.getCellData_fromTestData("VolunteerUsername");
	            tempRetval=sc.setValueJsChange("volunteerAccCreation_username_txt",username );
	            tempRetval=sc.setValueJsChange("volunteerAccCreation_email_txt", Globals.fromEmailID);
	            password = sc.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
	            tempRetval=sc.setValueJsChange("volunteerAccCreation_password_txt",password );
	            tempRetval=sc.setValueJsChange("volunteerAccCreation_confirmPassword_txt", password);
	            tempRetval=sc.checkCheckBox("volunteerAccCreation_consent_chk");
	            tempRetval = sc.waitforElementToDisplay("volunteerAccCreation_submit_btn",timeOutinSeconds);
	            if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
	            sc.scrollIntoView("volunteerAccCreation_submit_btn");
	            sc.clickWhenElementIsClickable("volunteerAccCreation_submit_btn",timeOutinSeconds);
	            sc.STAF_ReportEvent("Pass", "Volunteer Account Creation", "Volunteer account submit button is visible", 1);
	            }
	            else{
	                sc.STAF_ReportEvent("Fail", "Volunteer Reaccount Creation", "Volunteer account submit button is not visible", 1);
	            }
	            }
	            catch(Exception e){
	                retval=Globals.KEYWORD_FAIL;
	                log.error("Method-volunteerReaccountCreation | Exception - "+ e.toString());
	                throw e;
	            }
	            sc.waitTillElementDisplayed("login_volunteerUsername_txt", timeOutinSeconds);
	            password=sc.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
	            sc.setValueJsChange("login_volunteerPwd_txt", password);
	            sc.clickWhenElementIsClickable("login_volunteerLogin_btn", timeOutinSeconds);
	            tempRetval=sc.waitforElementToDisplay("invitationStep1_fName_txt", timeOutinSeconds);
	            if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
	                sc.STAF_ReportEvent("Pass", "Order 1 page", "Volunteer order 1 page is displayed", 1);
	            }
	            else{
	                sc.STAF_ReportEvent("Fail", "Order 1 page", "Volunteer order 1 page is not displayed", 1);
	            }
	            
	            return retval;
	        }
	    
	        /**************************************************************************************************
	         * Method to reference volunteer warning message if it tries to create account with different username and password 
	         * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	         * @author gaurimodani
	         * @throws Exception
	         ***************************************************************************************************/
	        public static String volunteerWarningmessage()
	        {
	            String retval = Globals.KEYWORD_FAIL;
	            int timeOutinSeconds=30;
	            String tempRetval=Globals.KEYWORD_FAIL;
	            String username = "sampleinvithree";
	            String confirmPassword=null;
	            String password;
	            try
	            {
	            String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
	            driver.navigate().to(url);
	            tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_username_txt",timeOutinSeconds);
	            tempRetval=sc.setValueJsChange("volunteerAccCreation_username_txt",username);
	            tempRetval=sc.setValueJsChange("volunteerAccCreation_email_txt", Globals.fromEmailID);
	            password = sc.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
	            tempRetval=sc.setValueJsChange("volunteerAccCreation_password_txt",password );
	            tempRetval=sc.setValueJsChange("volunteerAccCreation_confirmPassword_txt", password);
	            tempRetval=sc.checkCheckBox("volunteerAccCreation_consent_chk");
	            if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
	                sc.scrollIntoView("volunteerAccCreation_submit_btn");
	                sc.clickWhenElementIsClickable("volunteerAccCreation_submit_btn",timeOutinSeconds);
	                tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_Warning_txt", timeOutinSeconds);
	            sc.STAF_ReportEvent("Pass", "Warning message is displayed", "Warning message is displayed", 1);
	            }
	            else{
	                sc.STAF_ReportEvent("Fail", "Warning message is displayed", "Warning message is not displayed", 1);
	            }
	            }
	            catch(Exception e){
	                retval=Globals.KEYWORD_FAIL;
	                log.error("Method-volunteerWarning Message | Exception - "+ e.toString());
	                throw e;
	            }
	            return retval;
	        }
	        
	        /**************************************************************************************************
	         * Method to reference data in Invalid volunteer page
	         * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	         * @author gaurimodani
	         * @throws Exception
	         ***************************************************************************************************/
	        public static String Invalidvolunteer()
	        {
	            String retval = Globals.KEYWORD_FAIL;
	            int timeOutinSeconds=30;
	            String tempRetval=Globals.KEYWORD_FAIL;
	            String username = "sampleinvithree";
	            String confirmPassword=null;
	            String password;
	            try
	            {
	            String url=sc.testSuiteXLS.getCellData_fromTestData("InvitationURL") ;
	            driver.navigate().to(url);
	            tempRetval=sc.waitforElementToDisplay("volunteerAccCreation_username_txt",timeOutinSeconds);
	            driver.findElement(LocatorAccess.getLocator("volunteerAccCreation_ClickLogin_btn")).click();
	            sc.waitTillElementDisplayed("login_volunteerUsername_txt", timeOutinSeconds);
	            driver.findElement(LocatorAccess.getLocator("login_volunteerUsername_txt")).clear();
	            tempRetval=sc.setValueJsChange("login_volunteerUsername_txt",username);
	            password=sc.testSuiteXLS.getCellData_fromTestData("OrgUserPwd");
	            sc.setValueJsChange("login_volunteerPwd_txt", password);
	            sc.clickWhenElementIsClickable("login_volunteerLogin_btn", timeOutinSeconds);
	            if((tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
	                tempRetval=sc.waitforElementToDisplay("volunteer_InvalidPage_txt", timeOutinSeconds);
	                sc.STAF_ReportEvent("Pass", "Invalid Volunteer page ", "Invalid Volunteer page is displayed ", 1);
	                }
	                else{
	                    sc.STAF_ReportEvent("Fail", "Invalid Volunteer page ", "Invalid Volunteer page is not displayed ", 1);
	                }
	                }
	                catch(Exception e){
	                    retval=Globals.KEYWORD_FAIL;
	                    log.error("Method-volunteerWarning Message | Exception - "+ e.toString());
	                    throw e;
	                }
	                return retval;
	            }
	        /**************************************************************************************************
	         * Method to reference data in Invalid volunteer page
	         * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	         * @author psave
	         * @throws Exception
	         ***************************************************************************************************/
	        
	        public static void verifyExpectedValuePricing( double ExpectedValue, double ActualValue , String stepName)
			   {
				
			      if(ExpectedValue == ActualValue){
			    	
			    	  seleniumCommands.STAF_ReportEvent("Pass", stepName, "Actual Value is as Expected. Val = "+ExpectedValue, 1); 
			      }else{
			    	  double diff=ActualValue-ExpectedValue;
			    	  if((int)diff<0.1){
			    		  seleniumCommands.STAF_ReportEvent("Pass", stepName, "Actual Value is as Expected. Val = "+ActualValue, 1);
			    	  }else{
			    		  seleniumCommands.STAF_ReportEvent("Fail", stepName, "Actual Value is as Not as Expected.Exp = "+ExpectedValue+ " Actual ="+ActualValue, 1); 
			    	  }
			    	  
			      }
			    }
	        /**************************************************************************************************
			 * Method to Cancel the invitation 
			 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
			 * @author gauri.modani
			 * @return 
			 * @throws Exception
			 ***************************************************************************************************/
			public static String cancelInvitationOrder(){
				Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
				APP_LOGGER.startFunction(Globals.Component);
				String retval=Globals.KEYWORD_FAIL;
				int timeOutinSeconds = 20;
				String tempRetval=Globals.KEYWORD_FAIL;

				try{
		            
					String Fname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
					String reportingStepName="Verify the Search Criteria for volunteers First Name on the volunteer dashboard page.";
					retval = searchVolunteer("First Name",Fname,"Name", Fname,reportingStepName);
					WebElement volGrid = sc.createWebElement("volDashboard_volgrid_tbl");
					sc.checkCheckBox(volGrid.findElement((By.xpath("./tbody/tr[1]/td[1]/input"))));
		 			sc.waitforElementToDisplay("volDashboard_communications_btn", timeOutinSeconds);
		 			sc.clickWhenElementIsClickable("volDashboard_communications_btn", timeOutinSeconds);
		 		 	sc.clickWhenElementIsClickable("volDashboard_cancelInvitation_btn", timeOutinSeconds);
		 		 	sc.waitTillElementDisplayed("cancelInvitation_btn", timeOutinSeconds);
		 		 	sc.clickWhenElementIsClickable("cancelInvitation_btn", timeOutinSeconds);
		 		 	sc.waitforElementToDisplay("cancelInvitation_popup", timeOutinSeconds);
		 		 	sc.clickElementUsingJavaScript("cancelInvitationpopup_btn");
		 		 	sc.waitForPageLoad();
		 		 	tempRetval=sc.waitforElementToDisplay("volDashboard_communications_btn", timeOutinSeconds);
		 		 	if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
		 		 		sc.STAF_ReportEvent("Pass", "Cancelled Invitation", "Invitation has been cancelled", 1);
		 				}
		 		 	else {
		 		 		sc.STAF_ReportEvent("Fail", "Cancelled Invitation", "Invitation has not been cancelled", 1);
		 		 	}
				}catch(Exception e){
					
				}
				return retval;
				
			}
			
			/**************************************************************************************************
			 * Method to Cancel the invitation 
			 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
			 * @author gauri.modani
			 * @return 
			 * @throws Exception
			 ***************************************************************************************************/
			public static String cancelInviRemoveRow(){
				Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
				APP_LOGGER.startFunction(Globals.Component);
				String retval=Globals.KEYWORD_FAIL;
				int timeOutinSeconds = 20;
				String tempRetval=Globals.KEYWORD_FAIL;

				try{
		            
					String Fname = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
					String reportingStepName="Verify the Search Criteria for volunteers First Name on the volunteer dashboard page.";
					retval = searchVolunteer("First Name",Fname,"Name", Fname,reportingStepName);
					WebElement volGrid = sc.createWebElement("volDashboard_volgrid_tbl");
					sc.checkCheckBox(volGrid.findElement((By.xpath("./tbody/tr[1]/td[1]/input"))));
		 		 	sc.clickWhenElementIsClickable("volDashboard_removeRows_btn", timeOutinSeconds);
		 		 	sc.waitForPageLoad();
					sc.checkCheckBox(volGrid.findElement((By.xpath("./tbody/tr[1]/td[1]/input"))));
					sc.waitTillElementDisplayed("confirmremove_txt", timeOutinSeconds);
					sc.clickElementUsingJavaScript("confirmremove_btn");
					sc.waitForPageLoad();
		 		 	tempRetval=sc.waitforElementToDisplay("removerow_txt", timeOutinSeconds);
		 		 	if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
		 		 		sc.STAF_ReportEvent("Pass", "Cancelled Invitation Record Removal", "Cancelled Invitation has been removed ", 1);
		 				}
		 		 	else {
		 		 		sc.STAF_ReportEvent("Fail", "Cancelled Invitation Record Removal", "Cancelled Invitation has not been removed ", 1);
		 		 	}
				}catch(Exception e){
					
				}
				return retval;
				
			}
}