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
import com.gargoylesoftware.htmlunit.javascript.host.Element;
import com.sterlingTS.generic.appLib.AdminClient;
//import com.google.common.util.concurrent.Service.State;
import com.sterlingTS.seleniumUI.seleniumCommands;
import com.sterlingTS.utils.commonUtils.APP_LOGGER;
import com.sterlingTS.utils.commonUtils.BaseCommon;
import com.sterlingTS.utils.commonUtils.Email;
import com.sterlingTS.utils.commonUtils.LocatorAccess;
import com.sterlingTS.utils.commonUtils.XMLUtils;
//import com.sterlingTS.utils.commonUtils.database.DAO;
import com.sterlingTS.utils.commonVariables.Enum.State;
import com.sterlingTS.utils.commonVariables.Enum.VV_Products;
import com.sterlingTS.utils.commonVariables.Globals;


public class OnlineSignupApp extends BaseCommon{
	
	public static Logger log = Logger.getLogger(AdminClient.class);
	
	public static seleniumCommands sc = new seleniumCommands(driver);
	
	/**************************************************************************************************
	 * Method to validate Online Signup Welcome page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupWelcomePage() throws Exception {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{
			if(sc.isDisplayed("Onlinesignup_welcomepage_less_radiobutton").equalsIgnoreCase(Globals.KEYWORD_PASS)
					&& sc.isDisplayed("Onlinesignup_welcomepage_more_radiobutton").equalsIgnoreCase(Globals.KEYWORD_PASS))
			{
				sc.STAF_ReportEvent("Pass", "Welcome Page", "Less/More radio buttons are displayed.", 1);
				log.debug("More/Less radio buttons are displayed.");
				sc.clickElementUsingJavaScript("Onlinesignup_welcomepage_less_radiobutton");
				if(sc.getWebText("Onlinesignup_welcomepage_less1yr_txt").contains("Copy of utility or telephone bill in the business name"))
				{
					sc.STAF_ReportEvent("Pass", "Welcome Page",
							"Less radio button is selected and text is displayed accordingly.", 1);
				}
				else
					sc.STAF_ReportEvent("Fail", "Welcome Page", "User is not able to select Less radio button.", 1);
				sc.clickElementUsingJavaScript("Onlinesignup_welcomepage_more_radiobutton");
				sc.isEnabled("Onlinesignup_welcomepage_btn");
				sc.STAF_ReportEvent("Pass", "Welcome Page",
						"More radio button is selected and button is displayed.", 1);	
			}
			
			else
			{
				sc.STAF_ReportEvent("Fail", "Welcome Page", "Less/More radio buttons are not displayed.", 1);
			}

			sc.waitforElementToDisplay("Onlinesignup_welcomepage_btn", timeOutinSeconds);
			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			
			if(sc.waitforElementToDisplay("Onlinesignup_welcomepage_btn", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){
			
				if(fieldLevelValidation=="Yes"){
					tempRetval=onlinesigup_verify_FAQ(11);}
				
				sc.STAF_ReportEvent("Pass", "Welcome Page", "Online Signup Welcome Page has been loaded", 1);
				log.debug("Successfully navigated to Online Signup Welcome page");
			}
			else{
				sc.STAF_ReportEvent("Fail", "Welcome Page", "Unable to  navigate to Online Signup Welcome page", 1);
				log.error("Unable to  navigate to Online Signup Quote page");
			}
			
			//Need to add code to verify the text and FAQ on page
			
			sc.clickElementUsingJavaScript("Onlinesignup_welcomepage_btn");

			if(sc.waitforElementToDisplay("Onlinesignup_agreement_pricequote_chk", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "onlineSignupAgreementPage", "Online Signup Agreement Page has been loaded", 1);
				log.debug("Successfully navigated to Online Signup Agreement page");
			}
			else{
				sc.STAF_ReportEvent("Fail", "Online Signup", "Unable to  navigate to Online Signup Agreement page", 1);
				log.error("Unable to  navigate to Online Signup Agreement page");
			}

		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup Agreement page | Exception - "+e.toString());
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to validate Online Signup Quote page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupQuotePage() throws Exception {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			tempRetval=sc.waitforElementToDisplay("Onlinesignup_quotepage_fname_txt", timeOutinSeconds);
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "Quote page", "Successfully navigated to Quote page", 1);
				log.debug("Successfully navigated to Online Signup Quote page");
			}else{
				sc.STAF_ReportEvent("Fail", "Quote page", "Unable to navigate to Quote page ", 1);
			}
			
			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}
			
			if(fieldLevelValidation=="Yes"){
				tempRetval= onlinesigup_verify_FAQ(12);}
			
			tempRetval = verifyQuotePage(fieldLevelValidation);

			tempRetval = sc.waitforElementToDisplay("Onlinesignup_welcomepage_btn", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "Welcome page", "Successfully navigated to Welcome page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Welcome page", "Unable to navigate to Welcome page ", 1);
			}

		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup Quote page | Exception - "+e.toString());
		}
		
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify and set data in quote page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyQuotePage(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("verifyQuotePage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {
			// Code added to verify negative validations on the page
			
			if(fieldValidationReq.equalsIgnoreCase("Yes")){

				tempRetval = verifyFieldsInQuotePage();
			}
			
			//Need to add code to verify  FAQ on page
			tempRetval = setQuotePageInformation();
			retval = Globals.KEYWORD_PASS;
			
		}catch (Exception e) {
			log.error("Method-verifyQuotePage | Unable to fill quote page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify negative GUI validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInQuotePage() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInQuotePage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("Onlinesignup_quotepage_getquote_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInQuotePage - Page has not been loaded for quote page");
				return retval;
			}

			driver.findElement(LocatorAccess.getLocator("Onlinesignup_quotepage_getquote_btn")).sendKeys(Keys.ENTER);
			

//			expectdText = "Validation Errors Occurred"+"\n"+"Please correct any invalid fields.";
//			tempRetval = sc.verifyText("Onlinesignup_quotepage_error_txt", expectdText);
			
			String[] expectedValue= {"First Name is required.","Last Name is required.","Phone number is required. Phone number is in an invalid format.", "Email address is required. Email address is in an invalid format.","Organization Name is required.","Industry is required.","This field is required.","This field is required.","This field is required."};
			tempRetval=onlinesigup_verify_Values("Onlinesignup_quotepage_error_div",expectedValue);	
			String abc = "";
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate the Mandatory Fields on Quote page", "Warning message displayed as expected - "+expectedValue,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate the Mandatory Fields on Quote page", "Mismatch in warning message displayed.Excpected - "+expectedValue, 1);
			}


			retval = Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInQuotePage | Unable to perform negative validations in Quote page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to validate info text QuotePage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author gaurimodani
	 * @throws Exception
	 ***************************************************************************************************/
	public static void validateInfoIcons(String popovertext,String []expectedValue)throws Exception{
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		List<WebElement> infoIcons= new ArrayList<WebElement>();
		List<String>infoIconText=new ArrayList<String>();
		int timeOutinSeconds = 30;
		infoIcons  = Globals.driver.findElements(LocatorAccess.getLocator(popovertext));
	
		String actualValue[]=new String[infoIcons.size()];
		
		for(int i=0;i<infoIcons.size();i++)
		{
			sc.clickElementUsingJavaScript(infoIcons.get(i));
			sc.waitForPageLoad();
			sc.waitTillElementDisplayed("InfoIcon_header", timeOutinSeconds);
			String infoIconBodyActual =Globals.driver.findElement(LocatorAccess.getLocator("InfoIcon_header")).getText().trim();
			actualValue[i]=infoIconBodyActual;
			sc.waitTillElementDisplayed("InfoIcon_close", timeOutinSeconds);
			sc.clickElementUsingJavaScript("InfoIcon_close");
			sc.waitForPageLoad();
			sc.waitTillElementDisplayed("Onlinesignup_quotepage_continue_btn", timeOutinSeconds);
		}
		
		if(!(Arrays.equals(actualValue, expectedValue)))
		{
			sc.STAF_ReportEvent("Fail", "Verify text of info icon", "Info icon text does not matched as expected" , 1);
		}
		
		
		System.out.println("Actual :"+ Arrays.asList(actualValue)+ "Expected "+ Arrays.asList(expectedValue));
		retval = Globals.KEYWORD_PASS;
	}
	
	/**************************************************************************************************
	 * Method to set data in QuotePage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setQuotePageInformation() throws Exception{

		APP_LOGGER.startFunction("setQuotePageInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_quotepage_fname_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInQuotePage - Page has not been loaded for quote page");
				return retval;
			}
			//Code added to generate unique first name
			String fName="";
			int length = 10;
			String dynamicnameFlag = Globals.testSuiteXLS.getCellData_fromTestData("VolunteeFName_runtimeUpdateFlag");

			if(dynamicnameFlag.equalsIgnoreCase("Yes")){
				

				fName= "Quote" + RandomStringUtils.randomAlphabetic(length);
				Globals.testSuiteXLS.setCellData_inTestData("volunteerFName", fName);	
				log.debug("Runtime generated and store.value- "+fName);
			}
			else{
				fName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");// Quote page first name
			}
			
			String LName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");// Quote page last name
			String phone = Globals.Volunteer_Phone;
			String email = Globals.fromEmailID;
			String OrgName = "OrgName" + RandomStringUtils.randomAlphabetic(length);
			Globals.testSuiteXLS.setCellData_inTestData("ReferenceNames", OrgName);	
			String industry = Globals.testSuiteXLS.getCellData_fromTestData("ENV_QA2_GoodDeedCodeOrURL");// Quote page industry
			String hearaboutus = Globals.testSuiteXLS.getCellData_fromTestData("ENV_QA3_GoodDeedCodeOrURL");// Quote page hearaboutus
			String nofscanually = Globals.testSuiteXLS.getCellData_fromTestData("ENV_PROD_GoodDeedCodeOrURL");// Quote page Number of volunteers to be screened annually
			String salesRep = "Test"+ RandomStringUtils.randomAlphabetic(length);
			String isNotCompetitor = Globals.testSuiteXLS.getCellData_fromTestData("VolunteeLName_runtimeUpdateFlag");// Quote page I acknowledge I am not a competitor

			tempRetval = sc.setValueJsChange("Onlinesignup_quotepage_fname_txt", fName);
			tempRetval = sc.setValueJsChange("Onlinesignup_quotepage_lname_txt", LName);
			tempRetval = sc.setValueJsChange("Onlinesignup_quotepage_phone_txt",phone);
			tempRetval = sc.setValueJsChange("Onlinesignup_quotepage_email_txt", email);
			tempRetval = sc.setValueJsChange("Onlinesignup_quotepage_organizationName_txt", OrgName);
//			tempRetval = sc.selectValue_byVisibleText("Onlinesignup_quotepage_industry_dd", industry);
		    Select sel = new Select(driver.findElement(By.cssSelector("select[id='hearAboutUs']")));
		    sel.selectByVisibleText(hearaboutus);
		    sel = new Select(driver.findElement(By.cssSelector("select[id='industry']")));
			sel.selectByVisibleText(industry);
//			sc.clickWhenElementIsClickable("Onlinesignup_quotepage_hearAboutUs_dd", timeOutInSeconds);
//			tempRetval = sc.selectValue_byVisibleText("Onlinesignup_quotepage_hearAboutUs_dd", hearaboutus);
		    sel = new Select(driver.findElement(By.cssSelector("select[id='numScreenedAnnually']")));
		    sel.selectByVisibleText(nofscanually);
			//tempRetval = sc.selectValue_byVisibleText("Onlinesignup_quotepage_numScreenedAnnually_dd", nofscanually);
			tempRetval = sc.setValueJsChange("Onlinesignup_quotepage_salesRep_txt", salesRep);
			if(isNotCompetitor.equalsIgnoreCase("No")){
			//tempRetval = sc.checkCheckBox("Onlinesignup_quotepage_isNotCompetitor_chk");
			((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_quotepage_isNotCompetitor_chk")));
			}
			sc.clickWhenElementIsClickable("Onlinesignup_quotepage_getquote_btn", timeOutInSeconds);
			//driver.findElement(LocatorAccess.getLocator("Onlinesignup_quotepage_getquote_btn")).click();
			
			String []expectedValue ={"Number of volunteers to be screened annually", "Fast-Pass / Shared Services", "Setup Fee", "Rebates" };
			validateInfoIcons("Onlinesignup_infoicon",expectedValue);
			
			tempRetval=driver.findElement(LocatorAccess.getLocator("Onlinesignup_setupfee_txt")).getText();
			String setupfee_txt="One time setup fee may apply";
				if(tempRetval.equalsIgnoreCase(setupfee_txt))
					{
						sc.STAF_ReportEvent("Pass", "Get setup fee description" , "Setup fee description is correct", 1);
					}else
					{
						sc.STAF_ReportEvent("Fail", "Get setup fee description" , "Setup fee description is not correct", 1);
					}
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_quotepage_continue_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				driver.findElement(LocatorAccess.getLocator("Onlinesignup_quotepage_continue_btn")).sendKeys(Keys.ENTER);
				//sc.clickWhenElementIsClickable("Onlinesignup_quotepage_continue_btn", timeOutInSeconds);
				sc.STAF_ReportEvent("Pass", "Get quote page continue button", "Get Quote page continue button is visible ", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Get quote page continue button", "Get Quote page continue button is not visible ", 1);
			}
            
		}
		catch (Exception e) {
			log.error("Method-setQuotePageInformation | Unable to enter values in quote page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to validate Online Signup Agreement page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupAgreementPage() {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			tempRetval=sc.waitforElementToDisplay("Onlinesignup_agreement_pricequote_chk", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "Agreement page", "Successfully navigated to Agreement page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Agreement pag", "Unable to navigate to Agreement page ", 1);
			}
			
			
			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}
			
			if(fieldLevelValidation=="Yes"){
				tempRetval= onlinesigup_verify_FAQ(13);}
			
			tempRetval = verifyAgreementPage(fieldLevelValidation);


			tempRetval = sc.waitforElementToDisplay("Onlinesignup_orginfopage_legalname_txt", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "Orginfo page", "Successfully navigated to Orginfo page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Orginfo pag", "Unable to navigate to Orginfo page ", 1);
			}

		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup Agreement page | Exception - "+e.toString());
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify and set data in Agreement page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyAgreementPage(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("verifyAgreementPage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {
			// Code added to verify negative validations on the page
			
			if(fieldValidationReq.equalsIgnoreCase("Yes")){

				tempRetval = verifyFieldsInAgreementPage();
			}
			
			//Need to add code to verify FAQ on page
			tempRetval = setAgreementPageInformation();
			retval = Globals.KEYWORD_PASS;
			
		}catch (Exception e) {
			log.error("Method-verifyAgreementPage | Unable to fill Agreement page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify negative GUI validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInAgreementPage() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInAgreementPage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("Onlinesignup_agreement_pricequote_chk", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInAgreementPage - Page has not been loaded for Agreement page");
				return retval;
			}

			driver.findElement(LocatorAccess.getLocator("Onlinesignup_agreement_iagree_btn")).sendKeys(Keys.ENTER);
//			expectdText = "Validation Errors Occurred"+"\n"+"Please correct any invalid fields.";
//			tempRetval = sc.verifyText("Onlinesignup_quotepage_error_txt", expectdText);
			
			String[] expectedValue= {"You must provide your acknowledgement to continue.","You must provide your acknowledgement to continue."};
			tempRetval = onlinesigup_verify_Values("Onlinesignup_quotepage_error_div",expectedValue);	
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate the Mandatory Fields on Agreement page", "Warning message displayed as expected - "+expectedValue,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate the Mandatory Fields on Agreements page", "Mismatch in warning message displayed.Excpected - "+expectedValue, 1);
			}


			retval = Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInAgreementPage | Unable to perform negative validations in Agreement page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to set data in AgreementPage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setAgreementPageInformation() throws Exception{

		APP_LOGGER.startFunction("setAgreementPageInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_agreement_pricequote_chk", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-setAgreementPageInformation - Page has not been loaded for Agreement page");
				return retval;
			}

			//tempRetval = sc.checkCheckBox("Onlinesignup_agreement_pricequote_chk");
			((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_agreement_pricequote_chk")));
			//tempRetval = sc.checkCheckBox("Onlinesignup_agreement_AuthorizedUser_chk");
			((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_agreement_AuthorizedUser_chk")));
	
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_agreement_iagree_btn", timeOutInSeconds);
			
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				//sc.clickWhenElementIsClickable("Onlinesignup_agreement_iagree_btn", timeOutInSeconds);
				driver.findElement(LocatorAccess.getLocator("Onlinesignup_agreement_iagree_btn")).sendKeys(Keys.ENTER);
				sc.STAF_ReportEvent("Pass", "Get agreement page iagree button", "Get agreement page continue button is visible ", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Get agreement page iagree button", "Get agreement page continue button is not visible ", 1);
			}
            
			

		}
		catch (Exception e) {
			log.error("Method-setAgreementPageInformation | Unable to enter values in Agreement page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to validate Online Signup OrgInfo page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupOrgInfoPage() {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			tempRetval=sc.waitforElementToDisplay("Onlinesignup_orginfopage_legalname_txt", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "Orginfo page", "Successfully navigated to Orginfo page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Orginfo pag", "Unable to navigate to Orginfo page ", 1);
			}
			
			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}
			
			if(fieldLevelValidation=="Yes"){
				tempRetval= onlinesigup_verify_FAQ(14);
			}
			String []expectedValue ={"Organization’s Legal Name", "DBA", "FEIN","Do you work with the vulnerable sector", "Do you work with the vulnerable sector","Years in business"};
			validateInfoIcons("Onlinesignup_infoicon",expectedValue);
			tempRetval = verifyOrgInfoPage(fieldLevelValidation);


			tempRetval = sc.waitforElementToDisplay("Onlinesignup_billinfopage_fname_txt", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "BillingInfo page", "Successfully navigated to BillingInfo page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "BillingInfo pag", "Unable to navigate to BillingInfo page ", 1);
			}

		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup OrgInfo page | Exception - "+e.toString());
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify and set data in OrgInfo page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyOrgInfoPage(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("verifyOrgInfoPage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {
			// Code added to verify negative validations on the page
			
			if(fieldValidationReq.equalsIgnoreCase("Yes")){

				tempRetval = verifyFieldsInOrgInfoPage();
			}
			
			//Need to add code to verify  FAQ on page
			
			//Code to validate progress bar header value
			String[] expectedProgressBarHeaderValue= {"Enter Organization Information","Select Package","Verify Information", "Upload Documents & eSign"};
			onlinesigup_verify_Values("Onlinesignup_orginfopage_progressBarHeader",expectedProgressBarHeaderValue);	
			//Code to validate progress bar percentage value
			String[] expectedProgressBarValue= {"0%","2","3", "4"};
			onlinesigup_verify_Values("Onlinesignup_orginfopage_progressBarValue",expectedProgressBarValue);	
			
			tempRetval = setOrgInfoPageInformation();
			retval = Globals.KEYWORD_PASS;
			
		}catch (Exception e) {
			log.error("Method-verifyOrgInfoPage | Unable to fill OrgInfo page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify negative GUI validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInOrgInfoPage() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInOrgInfoPage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("Onlinesignup_orginfopage_legalname_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInOrgInfoPage - Page has not been loaded for orginfo page");
				return retval;
			}


			driver.findElement(LocatorAccess.getLocator("Onlinesignup_orginfopage_continue_btn")).sendKeys(Keys.ENTER);

//			expectdText = "Validation Errors Occurred"+"\n"+"Please correct any invalid fields.";
//			tempRetval = sc.verifyText("Onlinesignup_quotepage_error_txt", expectdText);
			
			String[] expectedValue= {"Legal Name is required.","Type of Ownership is required.","Physical Street Address is required.","City is required.","State is required.","Zip Code is required. Zip Code is in an invalid format.","Phone number is required. Phone number is in an invalid format.","Market Type is required.","Industry is required.","Vulnerable sector is required.","","","Years in business is required. Years in business must be a number.","Months in business is required. Months in business must be a number.","Number of volunteers is required. Number of volunteers must be a number no more than 20 digits.","Number of screened annually is required. Number of screened annually must be a number no more than 20 digits.","Frequency of rescreening is required."};
			tempRetval = onlinesigup_verify_Values("Onlinesignup_quotepage_error_div",expectedValue);	
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate the Mandatory Fields on orginfo page", "Warning message displayed as expected - "+expectedValue,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate the Mandatory Fields on orginfo page", "Mismatch in warning message displayed.Excpected - "+expectedValue, 1);
			}


			retval = Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInorginfoPage | Unable to perform negative validations in orginfo page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to set data in OrgInfoPage
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setOrgInfoPageInformation() throws Exception{

		APP_LOGGER.startFunction("setOrgInfoPageInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_orginfopage_legalname_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInOrgInfoPage - Page has not been loaded for OrgInfoPage");
				return retval;
			}

			String legalName="";
			int length = 10;
			String dynamiclnameFlag = Globals.testSuiteXLS.getCellData_fromTestData("VolunteerUsername_runtimeUpdateFlag");

			if(dynamiclnameFlag.equalsIgnoreCase("Yes")){
				// Org info page legal name
				legalName= Globals.testSuiteXLS.getCellData_fromTestData("volunteerFName");
				//legalName= "Legal" + RandomStringUtils.randomAlphabetic(length);
				Globals.testSuiteXLS.setCellData_inTestData("VolunteerUsername", legalName);	
				log.debug("Runtime generated and store.value- "+legalName);
			}
			String DBAName = "";
			String dynamicdnameFlag = Globals.testSuiteXLS.getCellData_fromTestData("Account_runtimeUpdateFlag");

			if(dynamicdnameFlag.equalsIgnoreCase("Yes")){
				// Org info page legal name
				DBAName= "DBA" + RandomStringUtils.randomAlphabetic(length);
				Globals.testSuiteXLS.setCellData_inTestData("AccountName", DBAName);	
				log.debug("Runtime generated and store.value- "+DBAName);
			}
			String phone = Globals.Volunteer_Phone;
			String ownershipType = Globals.testSuiteXLS.getCellData_fromTestData("CR_Account");
			String fEIN = "345678";
			String physicalStreetAddress = Globals.Volunteer_AddressLine;
			String mailingAddressLine1 = "Mailing address line";
			String city = Globals.Volunteer_City;
			String state = Globals.testSuiteXLS.getCellData_fromTestData("AddressState");
			String zipcode = Globals.testSuiteXLS.getCellData_fromTestData("AddressZipCode");
			String website = Globals.testSuiteXLS.getCellData_fromTestData("InvitationURL");
			String industry = Globals.testSuiteXLS.getCellData_fromTestData("CR_StartDate");
			String Market = Globals.testSuiteXLS.getCellData_fromTestData("CR_EndDate");
			String vulnerableSector = Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpGropyByLOV");
			String yearsInBusiness = Globals.testSuiteXLS.getCellData_fromTestData("CR_GroupBY");
			String monthsInBusiness = Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpClients");
			String numberOfVolunteers = Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpProducts");
			String frequencyOfRescreening = Globals.testSuiteXLS.getCellData_fromTestData("CR_ExpScreeningLevel");
			
			tempRetval=sc.setValueJsChange("Onlinesignup_orginfopage_legalname_txt","vvreg1");
			driver.findElement(By.xpath("//*[@id='orgDBAName']")).sendKeys(Keys.TAB);
			tempRetval=sc.waitforElementToDisplay("Onlinesignup_orginfopage_orglegalname_name_msg", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)&& sc.getWebText("Onlinesignup_orginfopage_orglegalname_name_msg")
					.equalsIgnoreCase("Name is already in use."))
				sc.STAF_ReportEvent("Pass", "Get orginfo page legal name warning message for name", "Org Legal Name text field name warning message is displayed correctly.", 1);
			else
				sc.STAF_ReportEvent("Fail", "Get orginfo page legal name warning message for name", "Org Legal Name text field name warning message is not displayed.", 1);
			
			tempRetval=sc.setValueJsChange("Onlinesignup_orginfopage_DBAName_txt","vvreg1");
			driver.findElement(By.xpath("//*[@id='ownershipType']")).sendKeys(Keys.TAB);
			tempRetval=sc.waitforElementToDisplay("Onlinesignup_orginfopage_orglegalname_name_msg", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)&& sc.getWebText("Onlinesignup_orginfopage_orglegalname_name_msg")
					.equalsIgnoreCase("Name is already in use."))
				sc.STAF_ReportEvent("Pass", "Get orginfo page DBA name warning message for name", "DBA Name text field name warning message is displayed correctly.", 1);
			else
				sc.STAF_ReportEvent("Fail", "Get orginfo page DBA name warning message for name", "DBA Name text field name warning message is not displayed.", 1);
			
			
			tempRetval= sc.setValueJsChange("Onlinesignup_orginfopage_legalname_txt", RandomStringUtils.randomAlphabetic(102));
			tempRetval = sc.waitforElementToDisplay("Onliensignup_orginfopage_orglegalname_len_msg", timeOutInSeconds);
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)&& sc.getWebText("Onliensignup_orginfopage_orglegalname_len_msg").trim()
						.equalsIgnoreCase("Legal Name must be no more than 100 characters."))
			sc.STAF_ReportEvent("Pass", "Get orginfo page OrglLegalName length warning message.", "OrglegalName text field lenght warning message is displayed correctly.", 1);
			else
			sc.STAF_ReportEvent("Fail", "Get orginfo page OrglLegalName length warning message.", "OrglegalName text field length warning message is not displayed.", 1);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_legalname_txt", legalName);
			tempRetval = sc.waitforElementToDisplay("Onliensignup_orginfopage_orglegalname_msg", timeOutInSeconds);
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)
					&& sc.getWebText("Onliensignup_orginfopage_orglegalname_msg")
							.equalsIgnoreCase("Please verify that you have entered the Organization's Legal Name.")){
				sc.STAF_ReportEvent("Pass", "Get orginfo page OrglLegalName Warning message.", "OrglegalName warning message text is displayed correctly.", 1);
			}
			else
			sc.STAF_ReportEvent("Fail", "Get orginfo page OrglLegalName contains first/last name warning message.", "OrglegalName warning message text is not displayed.", 1);
			
			tempRetval= sc.setValueJsChange("Onlinesignup_orginfopage_DBAName_txt", RandomStringUtils.randomAlphabetic(102));
			tempRetval = sc.waitforElementToDisplay("Onliensignup_orginfopage_DBAname_len_msg", timeOutInSeconds);
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)&& sc.getWebText("Onliensignup_orginfopage_DBAname_len_msg").trim()
						.equalsIgnoreCase("DBA must be no more than 100 characters."))
			sc.STAF_ReportEvent("Pass", "Get orginfo page DBAName length warning message.", "DBAName text field lenght warning message is displayed correctly.", 1);
			else
			sc.STAF_ReportEvent("Fail", "Get orginfo page DBAName length warning message.", "DBAName text field length warning message is not displayed.", 1);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_DBAName_txt", DBAName);
			
			tempRetval = sc.selectValue_byVisibleText("Onlinesignup_orginfopage_ownershipType_dd", ownershipType);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_fEIN_txt",fEIN);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_physicalStreetAddress_txt", physicalStreetAddress);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_mailingAddressLine1_txt", mailingAddressLine1);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_mailingAddressLine2_txt", mailingAddressLine1);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_city_txt", city);
			tempRetval = sc.selectValue_byVisibleText("Onlinesignup_orginfopage_state_dd", state);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_zipCode_txt", zipcode);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_phone_txt", phone);
			
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_website_txt", website);
			tempRetval = sc.waitforElementToDisplay("Onliensignup_orginfopage_website_msg", timeOutInSeconds);
			if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)
					&& sc.getWebText("Onliensignup_orginfopage_website_msg").trim()
							.equalsIgnoreCase("Invalid Website URL")){
				sc.STAF_ReportEvent("Pass", "Get orginfo page Organization Website Warning message.", "Organization website warning message text is displayed correctly.", 1);
				tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_website_txt", website+".com");
			}
			else
			sc.STAF_ReportEvent("Fail", "Get orginfo page Organization website Warning message.", "Organization website warning message text is not displayed.", 1);
			
			tempRetval = sc.selectValue_byVisibleText("Onlinesignup_orginfopage_market_dd", Market);
			//tempRetval = sc.selectValue_byVisibleText("Onlinesignup_orginfopage_industry_dd", industry);
			Select sel = new Select(driver.findElement(By.cssSelector("select[id='industry']")));
			sel.selectByVisibleText(industry);
			tempRetval = sc.selectValue_byVisibleText("Onlinesignup_orginfopage_vulnerableSector_dd", vulnerableSector);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_yearsInBusiness_txt", yearsInBusiness);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_monthsInBusiness_txt", monthsInBusiness);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_numberOfVolunteers_txt", numberOfVolunteers);
			tempRetval = sc.setValueJsChange("Onlinesignup_orginfopage_numberOfScreened_txt", numberOfVolunteers);
			tempRetval = sc.selectValue_byVisibleText("Onlinesignup_orginfopage_frequencyOfRescreening_dd", frequencyOfRescreening);
			
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_orginfopage_continue_btn", timeOutInSeconds);
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				driver.findElement(LocatorAccess.getLocator("Onlinesignup_orginfopage_continue_btn")).sendKeys(Keys.ENTER);
				sc.STAF_ReportEvent("Pass", "Get orginfo page continue button", "Get orginfo page continue button is visible ", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Get orginfo page continue button", "Get orginfo page continue button is not visible ", 1);
			}
            

		}
		catch (Exception e) {
			log.error("Method-setOrgInfoPageInformation | Unable to enter values in orginfo page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to validate Online Signup BillInfo page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupBillInfoPage() {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			tempRetval=sc.waitforElementToDisplay("Onlinesignup_billinfopage_fname_txt", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "BillInfo page", "Successfully navigated to BillInfo page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "BillInfo pag", "Unable to navigate to BillInfo page ", 1);
			}
			

			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}
			
			if(fieldLevelValidation=="Yes"){
			tempRetval= onlinesigup_verify_FAQ(15);
			}
			
			String []expectedValue ={"Bank Name", "Bank Contact Name"};
			validateInfoIcons("Onlinesignup_infoicon",expectedValue);
			
			tempRetval = verifyBillInfoPage(fieldLevelValidation);


			tempRetval = sc.waitforElementToDisplay("Onlinesignup_VendorInfopage_tradeName_txt", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "VendorInfo page", "Successfully navigated to VendorInfo page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "VendorInfo pag", "Unable to navigate to VendorInfo page ", 1);
			}

		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup BillInfo page | Exception - "+e.toString());
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify and set data in BillInfo page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyBillInfoPage(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("verifyBillInfoPage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {
			// Code needs to added to verify negative validations on the page
			
			if(fieldValidationReq.equalsIgnoreCase("Yes")){

				tempRetval = verifyFieldsInBillInfoPage();
			}
			
			//Need to add code to verify FAQ on page
			
			//Code to validate progress bar percentage value
			String[] expectedProgressBarValue= {"20%","2","3", "4"};
			onlinesigup_verify_Values("Onlinesignup_orginfopage_progressBarValue",expectedProgressBarValue);	
			
			tempRetval = setBillInfoPageInformation();
			retval = Globals.KEYWORD_PASS;
			
		}catch (Exception e) {
			log.error("Method-verifyBillInfoPage | Unable to fill BillInfo page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify negative GUI validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInBillInfoPage() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInOrgInfoPage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("Onlinesignup_billinfopage_fname_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInBillInfoPage - Page has not been loaded for billinfo page");
				return retval;
			}


			driver.findElement(LocatorAccess.getLocator("Onlinesignup_billinfopage_continue_btn")).sendKeys(Keys.ENTER);

//			expectdText = "Validation Errors Occurred"+"\n"+"Please correct any invalid fields.";
//			tempRetval = sc.verifyText("Onlinesignup_quotepage_error_txt", expectdText);
			
			String[] expectedValue= {"First Name is required","Last Name is required","Email is required Email does not appear to be a valid format","Phone is required Phone does not appear to be valid","Billing Address Line 1 is required","City is required","State is required","Zip Code is required Zip Code is in an invalid format","Bank Name is required","Address is required","City is required","State is required","Zip Code is required Zip Code is in an invalid format","Phone is required Phone does not appear to be valid","Contact Name is required"};
			tempRetval = onlinesigup_verify_Values("Onlinesignup_quotepage_error_div",expectedValue);	
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate the Mandatory Fields on billinfo page", "Warning message displayed as expected - "+expectedValue,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate the Mandatory Fields on billinfo page", "Mismatch in warning message displayed.Excpected - "+expectedValue, 1);
			}


			retval = Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInbillinfoPage | Unable to perform negative validations in billinfo page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to set data in BillInfo
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setBillInfoPageInformation() throws Exception{

		APP_LOGGER.startFunction("setBillInfoPageInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_billinfopage_fname_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInBillInfoPage - Page has not been loaded for BillInfoPage");
				return retval;
			}
            int length = 10;
			String fName =  "Bill" + RandomStringUtils.randomAlphabetic(length);
			String lName = Globals.testSuiteXLS.getCellData_fromTestData("volunteerLName");
			String phone = Globals.Volunteer_Phone;
			String email = Globals.fromEmailID;
			String mailingAddressLine1 = Globals.Volunteer_AddressLine;
			String city = Globals.Volunteer_City;
			String state = Globals.testSuiteXLS.getCellData_fromTestData("PrevAddrState");
			String zipcode = Globals.testSuiteXLS.getCellData_fromTestData("PrevAddZipCode");
			String ContactName = "ContactName" + RandomStringUtils.randomAlphabetic(length);
			String AccountOpenDate = Globals.testSuiteXLS.getCellData_fromTestData("CR_States");
			String UseOrgAddress=Globals.testSuiteXLS.getCellData_fromTestData("Step3_Verify Consent");
			String bankname="Bank"+ RandomStringUtils.randomAlphabetic(length);

			tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_fname_txt", fName);
			tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_lname_txt", lName);
			tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_email_txt",email);
			tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_Phone_txt", phone);
			if(UseOrgAddress.equalsIgnoreCase("No")){
				tempRetval = sc.checkCheckBox("Onlinesignup_billinfopage_UseOrgAddress_chk");
				tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_AddressLine1_txt", mailingAddressLine1);
				tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_AddressLine2_txt", mailingAddressLine1);
				tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_City_txt", city);
				tempRetval = sc.selectValue_byVisibleText("Onlinesignup_billinfopage_State_dd", state);
				tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_ZipCode_txt", zipcode);
				}
			tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_bankName_txt", bankname);
			tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_bankAddress_txt", mailingAddressLine1);
			tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_bankCity_txt", city);
			tempRetval = sc.selectValue_byVisibleText("Onlinesignup_billinfopage_bankState_dd", state);
			tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_bankZipCode_txt", zipcode);
			tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_bankPhone_txt", phone);
			tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_bankContactName_txt", ContactName);
			tempRetval = sc.setValueJsChange("Onlinesignup_billinfopage_bankAccountOpenDate_txt", AccountOpenDate);
			
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_billinfopage_continue_btn", timeOutInSeconds);
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				driver.findElement(LocatorAccess.getLocator("Onlinesignup_billinfopage_continue_btn")).sendKeys(Keys.ENTER);
				sc.STAF_ReportEvent("Pass", "Get billinfo page continue button", "Get billinfo page continue button is visible ", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Get billinfo page continue button", "Get billinfo page continue button is not visible ", 1);
			}

		}
		catch (Exception e) {
			log.error("Method-setBillInfoPageInformation | Unable to enter values in billinfo page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to validate Online Signup VendorInfo page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupVendorInfoPage() {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			tempRetval=sc.waitforElementToDisplay("Onlinesignup_VendorInfopage_tradeName_txt", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "VendorInfo page", "Successfully navigated to VendorInfo page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "VendorInfo page", "Unable to navigate to VendorInfo page ", 1);
			}
			
			
			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}
			
			if(fieldLevelValidation=="Yes"){
				tempRetval= onlinesigup_verify_FAQ(16);
			}
			String []expectedValue ={"Contact Name", "Doing Business Since" };
			validateInfoIcons("Onlinesignup_infoicon",expectedValue);
			tempRetval = verifyVendorInfoPage(fieldLevelValidation);

			tempRetval = sc.waitforElementToDisplay("Onlinesignup_PermissiblePurposepage_permissiblePurposeVolunteer_btn", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "PermissiblePurpose page", "Successfully navigated to PermissiblePurpose page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "PermissiblePurpose page", "Unable to navigate to PermissiblePurpose page ", 1);
			}

		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup VendorInfo page | Exception - "+e.toString());
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify and set data in VendorInfo page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyVendorInfoPage(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("verifyVendorInfoPage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {
			// Code needs to added to verify negative validations on the page
			
			if(fieldValidationReq.equalsIgnoreCase("Yes")){

				tempRetval = verifyFieldsInVendorPage();
			}
			
            //Need to add code to verify FAQ on page
			
			//Code to validate progress bar percentage value
			String[] expectedProgressBarValue= {"40%","2","3", "4"};
			onlinesigup_verify_Values("Onlinesignup_orginfopage_progressBarValue",expectedProgressBarValue);	
			
			tempRetval = setVendorInfoPageInformation();
			retval = Globals.KEYWORD_PASS;
			
		}catch (Exception e) {
			log.error("Method-verifyVendorInfoPage | Unable to fill VendorInfo page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify negative GUI validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInVendorPage() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInVendorPage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("Onlinesignup_VendorInfopage_tradeName_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInVendorPage - Page has not been loaded for vendorinfo page");
				return retval;
			}


			driver.findElement(LocatorAccess.getLocator("Onlinesignup_VendorInfopage_continue_btn")).sendKeys(Keys.ENTER);

//			expectdText = "Validation Errors Occurred"+"\n"+"Please correct any invalid fields.";
//			tempRetval = sc.verifyText("Onlinesignup_quotepage_error_txt", expectdText);
			
			String[] expectedValue= {"Name is required","Address is required","City is required","State is required","Zipcode is required Zipcode is in an invalid format","Phone is required Phone is in an invalid format","Contact Name is required","This field is required"};
			tempRetval = onlinesigup_verify_Values("Onlinesignup_quotepage_error_div",expectedValue);	
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate the Mandatory Fields on vendorinfo page", "Warning message displayed as expected - "+expectedValue,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate the Mandatory Fields on vendorinfo page", "Mismatch in warning message displayed.Excpected - "+expectedValue, 1);
			}


			retval = Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInVendorPage | Unable to perform negative validations in vendorinfo page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to set data in VendorInfo
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setVendorInfoPageInformation() throws Exception{

		APP_LOGGER.startFunction("setVendorInfoPageInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_VendorInfopage_tradeName_txt", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInVendorInfoPage - Page has not been loaded for VendorInfoPage");
				return retval;
			}

			int length = 10;
			String fName =  "Vendor" + RandomStringUtils.randomAlphabetic(length);
			String phone = Globals.Volunteer_Phone;
			String email = Globals.fromEmailID;
			String mailingAddressLine1 = Globals.Volunteer_AddressLine;
			String city = Globals.Volunteer_City;
			String state = Globals.testSuiteXLS.getCellData_fromTestData("Account_State");
			String zipcode = Globals.testSuiteXLS.getCellData_fromTestData("Account_ZipCode");
			String ContactName = "ContactName" + RandomStringUtils.randomAlphabetic(length);
			String AccountOpenDate = Globals.testSuiteXLS.getCellData_fromTestData("CR_States");
			String AccountId="654564565";

			tempRetval = sc.setValueJsChange("Onlinesignup_VendorInfopage_tradeName_txt", fName);
			tempRetval = sc.setValueJsChange("Onlinesignup_VendorInfopage_tradeAddress_txt", mailingAddressLine1);
			tempRetval = sc.setValueJsChange("Onlinesignup_VendorInfopage_tradeCity_txt",city);
		    tempRetval = sc.selectValue_byVisibleText("Onlinesignup_VendorInfopage_tradeState_dd", state);
			tempRetval = sc.setValueJsChange("Onlinesignup_VendorInfopage_tradeZipCode_txt", zipcode);
			tempRetval = sc.setValueJsChange("Onlinesignup_VendorInfopage_tradePhone_txt", phone);
			tempRetval = sc.setValueJsChange("Onlinesignup_VendorInfopage_tradeContactName_txt", ContactName);
			tempRetval = sc.setValueJsChange("Onlinesignup_VendorInfopage_tradeDoingBusinessSince_txt", AccountOpenDate);
			tempRetval = sc.setValueJsChange("Onlinesignup_VendorInfopage_tradeAccountId_txt", AccountId);
			
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_VendorInfopage_continue_btn", timeOutInSeconds);
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				driver.findElement(LocatorAccess.getLocator("Onlinesignup_VendorInfopage_continue_btn")).sendKeys(Keys.ENTER);
				sc.STAF_ReportEvent("Pass", "Get VendorInfo page continue button", "Get VendorInfo page continue button is visible ", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Get VendorInfo page continue button", "Get VendorInfo page continue button is not visible ", 1);
			}

		}
		catch (Exception e) {
			log.error("Method-setVendorInfoPageInformation | Unable to enter values in VendorInfo page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to validate Online Signup PermissiblePurpose page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupPermissiblePurposePage() {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			tempRetval=sc.waitforElementToDisplay("Onlinesignup_PermissiblePurposepage_permissiblePurposeVolunteer_btn", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "PermissiblePurpose page", "Successfully navigated to PermissiblePurpose page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "PermissiblePurpose page", "Unable to navigate to PermissiblePurpose page ", 1);
			}
			
			
			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}
			
			if(fieldLevelValidation=="Yes"){
				tempRetval= onlinesigup_verify_FAQ(17);
			}
			
			tempRetval = verifyPermissiblePurposePage(fieldLevelValidation);


			tempRetval = sc.waitforElementToDisplay("Onlinesignup_OrgUserspage_add_btn", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "OrgUsers page", "Successfully navigated to OrgUsers page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "OrgUsers page", "Unable to navigate to OrgUsers page ", 1);
			}

		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup PermissiblePurpose page | Exception - "+e.toString());
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify and set data in PermissiblePurpose page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyPermissiblePurposePage(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("verifyPermissiblePurposePage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {
			// Code needs to added to verify negative validations on the page
			
			if(fieldValidationReq.equalsIgnoreCase("Yes")){

				tempRetval = verifyFieldsInPermissiblePurposePage();
			}
			
            //Need to add code to verify FAQ on page
			
			//Code to validate progress bar percentage value
			String[] expectedProgressBarValue= {"60%","2","3", "4"};
			onlinesigup_verify_Values("Onlinesignup_orginfopage_progressBarValue",expectedProgressBarValue);	
			
			tempRetval = setPermissiblePurposePageInformation();
			retval = Globals.KEYWORD_PASS;
			
		}catch (Exception e) {
			log.error("Method-verifyPermissiblePurposePage | Unable to fill PermissiblePurpose page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify negative GUI validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInPermissiblePurposePage() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInPermissiblePurposePage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("Onlinesignup_PermissiblePurposepage_permissiblePurposeVolunteer_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInPermissiblePurposePage - Page has not been loaded for vendorinfo page");
				return retval;
			}


			driver.findElement(LocatorAccess.getLocator("Onlinesignup_PermissiblePurposepage_continue_btn")).sendKeys(Keys.ENTER);

//			expectdText = "Validation Errors Occurred"+"\n"+"Please correct any invalid fields.";
//			tempRetval = sc.verifyText("Onlinesignup_quotepage_error_txt", expectdText);
			
			String[] expectedValue= {"Please select at least one of the options to proceed"};
			tempRetval = onlinesigup_verify_Values("Onlinesignup_quotepage_error_div",expectedValue);	
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate the Mandatory Fields on PermissiblePurpose page", "Warning message displayed as expected - "+expectedValue,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate the Mandatory Fields on PermissiblePurpose page", "Mismatch in warning message displayed.Excpected - "+expectedValue, 1);
			}


			retval = Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInPermissiblePurposePage | Unable to perform negative validations in vendorinfo page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to set data in PermissiblePurpose Page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setPermissiblePurposePageInformation() throws Exception{

		APP_LOGGER.startFunction("setPermissiblePurposePageInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_PermissiblePurposepage_permissiblePurposeVolunteer_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInPermissiblePurposePage - Page has not been loaded for PermissiblePurposePage");
				return retval;
			}
            int length =10;
			String PermissiblePurpose = Globals.testSuiteXLS.getCellData_fromTestData("IsAccountPrivate");//if yes then check the first radio button else the second radio button
			String PermissiblePurposeNotes = "PermissiblePurposeNotes-" + sc.runtimeGeneratedStringValue(length);;
            
//			Actions action = new Actions(driver);
//			action.moveToElement(driver.findElement(LocatorAccess.getLocator("Onlinesignup_PermissiblePurposepage_permissiblePurposeOther_btn"))).click().perform();
			
			if (PermissiblePurpose.equalsIgnoreCase("Yes")){
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_PermissiblePurposepage_permissiblePurposeVolunteer_btn")));
			}
			else{
			((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_PermissiblePurposepage_permissiblePurposeOther_btn")));
			//driver.findElement(LocatorAccess.getLocator("Onlinesignup_PermissiblePurposepage_permissiblePurposeOther_btn")).sendKeys(Keys.ENTER);
			//sc.clickWhenElementIsClickable("Onlinesignup_PermissiblePurposepage_permissiblePurposeOther_btn", timeOutInSeconds);
			tempRetval = sc.setValueJsChange("Onlinesignup_PermissiblePurposepage_permissiblePurposeOtherDesc_txt", PermissiblePurposeNotes);
			}
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_PermissiblePurposepage_continue_btn", timeOutInSeconds);
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				driver.findElement(LocatorAccess.getLocator("Onlinesignup_PermissiblePurposepage_continue_btn")).sendKeys(Keys.ENTER);
				sc.STAF_ReportEvent("Pass", "Get PermissiblePurpose page continue button", "Get PermissiblePurpose page continue button is visible ", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Get PermissiblePurpose page continue button", "Get PermissiblePurpose page continue button is not visible ", 1);
			}
            
		}
		catch (Exception e) {
			log.error("Method-setPermissiblePurposePageInformation | Unable to enter values in PermissiblePurpose page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to validate Online Signup OrgUsers page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupOrgUsersPage() {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			tempRetval=sc.waitforElementToDisplay("Onlinesignup_OrgUserspage_add_btn", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "OrgUsers page", "Successfully navigated to OrgUsers page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "OrgUsers page", "Unable to navigate to OrgUsers page ", 1);
			}
			
			
			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}
			
			if(fieldLevelValidation=="Yes"){
				tempRetval= onlinesigup_verify_FAQ(18);
			}
			
			tempRetval = verifyOrgUsersPage(fieldLevelValidation);


			tempRetval = sc.waitforElementToDisplay("Onlinesignup_SelectPackage_Complete_chk", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "SelectPackage page", "Successfully navigated to SelectPackage page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "SelectPackage page", "Unable to navigate to SelectPackage page ", 1);
			}

		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup OrgUsers page | Exception - "+e.toString());
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify and set data in OrgUsers page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyOrgUsersPage(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("verifyOrgUsersPage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {
			// Code needs to added to verify negative validations on the page
			
//			if(fieldValidationReq.equalsIgnoreCase("Yes")){
//
//				tempRetval = verifyFieldsInQuotePage();
//			}
			
            //Need to add code to verify FAQ on page
			
			//Code to validate progress bar percentage value
			String[] expectedProgressBarValue= {"80%","2","3", "4"};
			onlinesigup_verify_Values("Onlinesignup_orginfopage_progressBarValue",expectedProgressBarValue);	
			
			tempRetval = setOrgUsersPageInformation();
			retval = Globals.KEYWORD_PASS;
			
		}catch (Exception e) {
			log.error("Method-verifyPermissiblePurposePage | Unable to fill PermissiblePurpose page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to set data in OrgUsers Page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setOrgUsersPageInformation() throws Exception{

		APP_LOGGER.startFunction("setOrgUsersPageInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_OrgUserspage_add_btn", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInOrgUsersPage - Page has not been loaded for OrgUsersPage");
				return retval;
			}

			String phone = Globals.Volunteer_Phone;
			String email = Globals.fromEmailID;
			String mailingAddressLine1 = Globals.Volunteer_AddressLine;
			String city = Globals.Volunteer_City;
			String UseOrgAddress =Globals.testSuiteXLS.getCellData_fromTestData("Step4_VerifyProductList");
			String title="The verified volunteers are known for Background check";

			//sc.clickWhenElementIsClickable("Onlinesignup_OrgUserspage_add_btn", timeOutInSeconds);
			
			//Need to add code to add mutiple users on this page
			
			if(UseOrgAddress.equalsIgnoreCase("Yes")){
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_OrgUserspage_UseOrgAddress_chk")));
				//tempRetval = sc.checkCheckBox("Onlinesignup_OrgUserspage_UseOrgAddress_chk");
			}
			//sc.waitforElementToDisplay("Onlinesignup_Userspage_titlewarning_txt", timeOutInSeconds);
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_OrgUserspage_continue_btn", timeOutInSeconds);
			driver.findElement(By.xpath("//*[@id='title']")).sendKeys("The verified volunteers are known for Background check");
			tempRetval=driver.findElement(By.xpath("//*[@id='title']")).getText();
			if(tempRetval.length()<50)
			{
				sc.STAF_ReportEvent("Pass", "Validate title length", "Title consist of 50 characters", 1);
			}
			else
			{
				sc.STAF_ReportEvent("Fail", "Validate title length", "Title does not consist of 50 characters", 1);
			}
			tempRetval=sc.setValueJsChange("Onlinesignup_Userspage_title_txt", tempRetval);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				driver.findElement(LocatorAccess.getLocator("Onlinesignup_OrgUserspage_continue_btn")).sendKeys(Keys.ENTER);
				sc.STAF_ReportEvent("Pass", "Get OrgUsers page continue button", "Get OrgUsers page continue button is visible ", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Get OrgUsers page continue button", "Get OrgUsers page continue button is not visible ", 1);
			}
            

		}
		catch (Exception e) {
			log.error("Method-setOrgUsersPageInformation | Unable to enter values in OrgUsers page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to validate Online Signup SelectPackage page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupSelectPackagePage() {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			tempRetval=sc.waitforElementToDisplay("Onlinesignup_SelectPackage_Complete_chk", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "SelectPackage page", "Successfully navigated to SelectPackage page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "SelectPackage page", "Unable to navigate to SelectPackage page ", 1);
			}
			

			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}
			
			if(fieldLevelValidation=="Yes"){
				tempRetval= onlinesigup_verify_FAQ(9);
			}
			
			String []expectedValue ={"Pricing", "Motor Vehicle Record Search", "Applicable Fees" };
			validateInfoIcons("Onlinesignup_popover",expectedValue);
					
			tempRetval = verifySelectPackagePage(fieldLevelValidation);
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_SelectPayment_clientPaysAll_chk", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "SelectPayment page", "Successfully navigated to SelectPayment page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "SelectPayment page", "Unable to navigate to SelectPayment page ", 1);
			}

		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup SelectPackage page | Exception - "+e.toString());
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify and set data in SelectPackage page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifySelectPackagePage(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("verifySelectPackagePage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {
			
			// Code needs to added to verify negative validations on the page
			
			if(fieldValidationReq.equalsIgnoreCase("Yes")){

				tempRetval = verifyFieldsInSelectPackagePage();
			}
			
			//Need to add code to verify the FAQ on page
			
			//Added code to verify progress bar percentage values
			String[] expectedProgressBarValue= {"✓","0%","3", "4"};
			onlinesigup_verify_Values("Onlinesignup_orginfopage_progressBarValue",expectedProgressBarValue);	
			
			tempRetval = setSelectPackagePageInformation();
			retval = Globals.KEYWORD_PASS;
			
		}catch (Exception e) {
			log.error("Method-verifySelectPackagePage | Unable to fill SelectPackage page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify negative GUI validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInSelectPackagePage() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInSelectPackagePage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("Onlinesignup_SelectPackage_Complete_chk", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInSelectPackagePage - Page has not been loaded for SelectPackage page");
				return retval;
			}


			driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPackage_continue_btn")).sendKeys(Keys.ENTER);

//			expectdText = "Validation Errors Occurred"+"\n"+"Please correct any invalid fields.";
//			tempRetval = sc.verifyText("Onlinesignup_quotepage_error_txt", expectdText);
			
			String[] expectedValue= {"Please select one or more volunteer background screening packages"};
			tempRetval = onlinesigup_verify_Values("Onlinesignup_quotepage_error_div",expectedValue);	
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate the Mandatory Fields on SelectPackage page", "Warning message displayed as expected - "+expectedValue,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate the Mandatory Fields on SelectPackage page", "Mismatch in warning message displayed.Excpected - "+expectedValue, 1);
			}


			retval = Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInSelectPackagePage | Unable to perform negative validations in SelectPackage page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to set data in SelectPackage Page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setSelectPackagePageInformation() throws Exception{

		APP_LOGGER.startFunction("setSelectPackagePageInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_SelectPackage_Complete_chk", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInSelectPackagePage - Page has not been loaded for SelectPackagePage");
				return retval;
			}

			String Package = Globals.testSuiteXLS.getCellData_fromTestData("PositionProducts");

			if(Package.equals("Complete")){
				//tempRetval = sc.checkCheckBox("Onlinesignup_SelectPackage_Complete_chk");
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPackage_Complete_chk")));
				}
			else if(Package.equals("Advance")){
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPackage_Advanced_chk")));
				//tempRetval = sc.checkCheckBox("Onlinesignup_SelectPackage_Advanced_chk");
			}
			else if(Package.equals("All Products")){
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPackage_Complete_chk")));
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPackage_Advanced_chk")));
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPackage_MVR_chk")));
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPackage_Alacarte_chk")));
				}
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_SelectPackage_continue_btn", timeOutInSeconds);
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPackage_continue_btn")).sendKeys(Keys.ENTER);
				sc.STAF_ReportEvent("Pass", "Get SelectPackage page continue button", "Get SelectPackage page continue button is visible ", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Get SelectPackage page continue button", "Get SelectPackage page continue button is not visible ", 1);
			}
            

		}
		catch (Exception e) {
			log.error("Method-setSelectPackagePageInformation | Unable to enter values in SelectPackage page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to validate Online Signup SelectPayment page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupSelectPaymentPage() {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			tempRetval=sc.waitforElementToDisplay("Onlinesignup_SelectPayment_clientPaysAll_chk", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "SelectPayment page", "Successfully navigated to SelectPackage page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "SelectPayment page", "Unable to navigate to SelectPackage page ", 1);
			}
			
			
			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(fieldLevelValidation == null || fieldLevelValidation.isEmpty()){
				fieldLevelValidation = "No";
			}
			
			if(fieldLevelValidation=="Yes"){
				tempRetval= onlinesigup_verify_FAQ(10);
			}
			
			tempRetval = verifySelectPaymentPage(fieldLevelValidation);

			tempRetval = sc.waitforElementToDisplay("Onlinesignup_OtherConfigpage_badgeName_txt", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "OtherConfig page", "Successfully navigated to OtherConfig page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "OtherConfig page", "Unable to navigate to OtherConfig page ", 1);
			}

		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup SelectPayment page | Exception - "+e.toString());
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to verify and set data in SelectPayment page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifySelectPaymentPage(String fieldValidationReq) throws Exception{

		APP_LOGGER.startFunction("verifySelectPaymentPage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=20;

		try {
			
			// Code needs to added to verify negative validations on the page
//			if(fieldValidationReq.equalsIgnoreCase("Yes")){
//
//				tempRetval = verifyFieldsInQuotePage();
//			}
            //Need to add code to verify the FAQ on page
			
			//Added code to verify progress bar percentage values
			String[] expectedProgressBarValue= {"✓","33%","3", "4"};
			onlinesigup_verify_Values("Onlinesignup_orginfopage_progressBarValue",expectedProgressBarValue);	
			
			tempRetval = setSelectPaymentPageInformation();
			retval = Globals.KEYWORD_PASS;
			
		}catch (Exception e) {
			log.error("Method-verifySelectPaymentPage | Unable to fill SelectPayment page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to verify negative GUI validations
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldsInSelectPaymentPage() throws Exception{

		APP_LOGGER.startFunction("verifyFieldsInSelectPaymentPage");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;
		String expectdText;
		try {

			tempRetval = sc.waitforElementToDisplay("Onlinesignup_SelectPayment_clientPaysAll_chk", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInSelectPaymentPage - Page has not been loaded for SelectPackage page");
				return retval;
			}


			driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPayment_continue_btn")).sendKeys(Keys.ENTER);

//			expectdText = "Validation Errors Occurred"+"\n"+"Please correct any invalid fields.";
//			tempRetval = sc.verifyText("Onlinesignup_quotepage_error_txt", expectdText);
			
			String[] expectedValue= {"Please select at least one of the payment options to proceed"};
			tempRetval = onlinesigup_verify_Values("Onlinesignup_quotepage_error_div",expectedValue);	
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "To validate the Mandatory Fields on SelectPayment page", "Warning message displayed as expected - "+expectedValue,1);
			}else{	
				sc.STAF_ReportEvent("Fail", "To validate the Mandatory Fields on SelectPayment page", "Mismatch in warning message displayed.Excpected - "+expectedValue, 1);
			}


			retval = Globals.KEYWORD_PASS;
		}
		catch (Exception e) {
			log.error("Method-verifyFieldsInSelectPaymentPage | Unable to perform negative validations in SelectPayment page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to set data in SelectPayment Page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String setSelectPaymentPageInformation() throws Exception{

		APP_LOGGER.startFunction("setSelectPackagePageInformation");
		String retval = Globals.KEYWORD_FAIL;
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds = 20;

		try {
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_SelectPayment_clientPaysAll_chk", timeOutInSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				log.debug("Method-verifyFieldsInSelectPaymentePage - Page has not been loaded for SelectPaymentPage");
				return retval;
			}
			
			String Payment = Globals.testSuiteXLS.getCellData_fromTestData("PricingName");

			if(Payment.equals("Client Pays All")){
				//tempRetval = sc.checkCheckBox("Onlinesignup_SelectPayment_clientPaysAll_chk");
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPayment_clientPaysAll_chk")));
				}
			else if(Payment.equals("Volunteer Pays All")){
				//tempRetval = sc.checkCheckBox("Onlinesignup_SelectPayment_volunteerPaysAll_chk");
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPayment_volunteerPaysAll_chk")));
				}
			else if(Payment.equals("Client Pays Half + Fees")){
				//tempRetval = sc.checkCheckBox("Onlinesignup_SelectPayment_clientPaysHalf_chk");
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPayment_clientPaysHalf_chk")));
				}
			else if(Payment.equals("All Payment")){
				tempRetval = sc.checkCheckBox("Onlinesignup_SelectPayment_clientPaysAll_chk");
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPayment_volunteerPaysAll_chk")));
				((JavascriptExecutor) driver).executeScript("arguments[0].click()", driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPayment_clientPaysHalf_chk")));
				//tempRetval = sc.checkCheckBox("Onlinesignup_SelectPayment_candidateContribution_chk");
				}
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_SelectPayment_continue_btn", timeOutInSeconds);
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				driver.findElement(LocatorAccess.getLocator("Onlinesignup_SelectPayment_continue_btn")).sendKeys(Keys.ENTER);
				sc.STAF_ReportEvent("Pass", "Get SelectPayment page continue button", "Get SelectPayment page continue button is visible ", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Get SelectPayment page continue button", "Get SelectPayment page continue button is not visible ", 1);
			}

		}
		catch (Exception e) {
			log.error("Method-setSelectPaymentPageInformation | Unable to enter values in SelectPayment page | Exception - "+ e.toString());
			throw e;
		}
		return retval;
	}
	/**************************************************************************************************
	 * Method to validate Online Signup OtherConfig page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupOtherConfigPage() {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 60;

		try{

			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(sc.waitforElementToDisplay("Onlinesignup_OtherConfigpage_badgeName_txt", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "onlineSignupOtherConfigPage", "Online Signup OtherConfig Page has been loaded", 1);
				log.debug("Successfully navigated to Online Signup OtherConfig page");
			}
			else{
				sc.STAF_ReportEvent("Fail", "Online Signup", "Unable to  navigate to Online Signup OtherConfig page", 1);
				log.error("Unable to  navigate to Online Signup OtherConfig page");
			}
			
				if(fieldLevelValidation=="Yes"){
					tempRetval= onlinesigup_verify_FAQ(20);}
				
			//Added code to verify progress bar percentage values
			String[] expectedProgressBarValue= {"✓","66%","3", "4"};
			onlinesigup_verify_Values("Onlinesignup_orginfopage_progressBarValue",expectedProgressBarValue);	
			
			//Need to add code to verify the badge name and select clear and consider list of users
			String []expectedValue ={"Clear Email Contacts", "Consider Email Contacts" };
			validateInfoIcons("Onlinesignup_infoicon",expectedValue);
			
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_OtherConfigpage_continue_btn", timeOutinSeconds);
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				driver.findElement(LocatorAccess.getLocator("Onlinesignup_OtherConfigpage_continue_btn")).sendKeys(Keys.ENTER);
				sc.STAF_ReportEvent("Pass", "Get OtherConfig page continue button", "Get OtherConfig page continue button is visible ", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Get OtherConfig page continue button", "Get OtherConfig page continue button is not visible ", 1);
			}
            
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_Verifypage_continue_btn", timeOutinSeconds);
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				retval=Globals.KEYWORD_PASS;
				sc.STAF_ReportEvent("Pass", "Verify page", "Successfully navigated to Verify page", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Verify page", "Unable to navigate to Verify page ", 1);
			}
		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup OtherConfig page | Exception - "+e.toString());
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to validate Online Signup Verify page 1
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlineSignupVerifyPageOne() {
		
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutinSeconds = 60;

		try{

			String fieldLevelValidation = Globals.testSuiteXLS.getCellData_fromTestData("FieldValidationRequiredFlag");
			if(sc.waitforElementToDisplay("Onlinesignup_Verifypage_continue_btn", timeOutinSeconds).equalsIgnoreCase(Globals.KEYWORD_PASS)){
				sc.STAF_ReportEvent("Pass", "onlineSignupVerifyPageOne", "Online Signup Verify Page One has been loaded", 1);
				log.debug("Successfully navigated to Online Signup Verify page one");
			}
			else{
				sc.STAF_ReportEvent("Fail", "Online Signup", "Unable to  navigate to Online Signup Verify Page One", 1);
				log.error("Unable to  navigate to Online Signup Verify Page One");
			}


			if(fieldLevelValidation=="Yes"){
			tempRetval= onlinesigup_verify_FAQ(21);
			}
			//Added code to verify progress bar percentage values
			String[] expectedProgressBarValue= {"✓","✓","0%", "4"};
			onlinesigup_verify_Values("Onlinesignup_orginfopage_progressBarValue",expectedProgressBarValue);	
			
			
			//Need to add code to verify the details on verify page
           
			tempRetval = sc.waitforElementToDisplay("Onlinesignup_Verifypage_continue_btn", timeOutinSeconds);
			
			if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				driver.findElement(LocatorAccess.getLocator("Onlinesignup_Verifypage_continue_btn")).sendKeys(Keys.ENTER);
				retval=Globals.KEYWORD_PASS;
				sc.STAF_ReportEvent("Pass", "Get Verify Page One continue button", "Get Verify Page One continue button is visible ", 1);

			}else{
				sc.STAF_ReportEvent("Fail", "Get Verify Page One continue button", "Get Verify Page One continue button is not visible ", 1);
			}
			
			//Need to add code to verify navigation to verify page two
            
		}catch(Exception e){
			log.error("Unable to  navigate to Online Signup Verify Page One | Exception - "+e.toString());
		}

		return retval;
	}
	/**************************************************************************************************
	 * Method to compare the actual text vs expected text
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vgokulanathan
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlinesigup_verify_Values(String ElementValue,String[] expectedValue){
		String actualReport ="";
		String retval = Globals.KEYWORD_FAIL;
		List<WebElement> progressbarValues= new ArrayList<WebElement>();
		progressbarValues  = Globals.driver.findElements(LocatorAccess.getLocator(ElementValue));
		
			for(int i=0; i<progressbarValues.size(); i++)
			{   
				String Actualtext = progressbarValues.get(i).getText();
				if(Actualtext.equals(expectedValue[i])){ 
					sc.STAF_ReportEvent("Pass", "expectedValue", "expectedValue matches the expected text."+"    Expected value is:"+expectedValue[i]+"    Actual value is:"+Actualtext, 1);
					retval = Globals.KEYWORD_PASS;
				}
				else{
					sc.STAF_ReportEvent("Fail", "expectedValue", "expectedValue does not match expected text."+"    Expected value is:"+expectedValue[i]+"    Actual value is:"+Actualtext, 1);
				}
			}

		return retval;
	}
	
	/**************************************************************************************************
	 * Method to verify FQA Question and response values, compares the FAQ UI values vs DB values
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author vibhorsharma
	 * @throws Exception
	 ***************************************************************************************************/
	public static String onlinesigup_verify_FAQ(int sectionId) throws Exception{
		String actualReport ="";
		String retval = Globals.KEYWORD_FAIL;
			String[] colhead={"Question", "Response"};
			String selectSQL="Select Question, Response from PanelGroup where SectionId="+sectionId;
			String usernameArg="";
			ArrayList<String> faqDb=AccountSetting.retriveFeildsDB(selectSQL,colhead,usernameArg,"Yes");
			
			List<WebElement> faqHeader= new ArrayList<WebElement>();
			List<String> faqHeaderBody = new ArrayList<String>();
			sc.waitForPageLoad();
			faqHeader  = Globals.driver.findElements(LocatorAccess.getLocator("Onlinesignup_FAQ_header"));
			
			for(WebElement header: faqHeader)
			{
				header.click();
				faqHeaderBody.add(header.getText().replaceAll("n't listed here.", "n't listed here. "));
				sc.waitForPageLoad();
				WebElement faqBody= Globals.driver.findElement(LocatorAccess.getLocator("Onlinesignup_FAQ_body"));
				faqHeaderBody.add(faqBody.getText());
			}

			if(faqDb.equals(faqHeaderBody))
				sc.STAF_ReportEvent("Pass", "OnlineSignup- FAQ", "FAQ values are correct", 1);
			
			else
				sc.STAF_ReportEvent("Fail", "OnlineSignup- FAQ", "FAQ values are not correct", 1);
				
		return retval;
	}
}