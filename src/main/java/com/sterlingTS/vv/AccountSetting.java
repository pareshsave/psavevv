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
import com.gargoylesoftware.htmlunit.javascript.host.Set;
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


public class AccountSetting extends BaseCommon{
	
	
	public static Logger log = Logger.getLogger(AdminClient.class);
	
	public static seleniumCommands sc = new seleniumCommands(driver);
	
	
	/**************************************************************************************************
     * Method to find out notification message getting correctly for api log table for different order status
     * @return Globals.KEYWORD_PASS if successful, Globals.INT_FAIL otherwise
     * @author psave
     * @LastModifiedBy
     ***************************************************************************************************/
    
    public static ArrayList retriveFeildsDB(String query, String[] colhead, String usernmAgs, String multiRowRetrive) throws Exception {
    VVDAO vvDB             = null;
    ArrayList<HashMap<String, String>> rowList = new ArrayList<HashMap<String, String>>();
    ArrayList<String> rowList1 = new ArrayList<String>();
    try{
   	 	String dbURL = Globals.getEnvPropertyValue("dbURL");
   	 	String dbUserName = Globals.getEnvPropertyValue("dbUserName");
   	 	String dbPassword = ProtectedEncryption.decrypt(Globals.getEnvPropertyValue("dbPassword"), CommonHelpMethods.createKey());
   	 	
   	 	log.info("DB URL is :"+"\t"+dbURL);
		
   	 	vvDB      = new VVDAO(dbURL,dbUserName,dbPassword); 
                    
        //String selectSQL = "SELECT cu.FirstName, cu.LastName, cu.Title, cu.PhoneNumber, cu.EmailAddress, cu.AddressLine1, cu.AddressLine2, cu.City, cu.State, cu.Zipcode, cu.IsEnabled, cu.UserId, u.Username FROM Client c JOIN ClientUser cu ON c.ClientId = cu.ClientId JOIN Users u ON cu.UserId = u.UserId WHERE c.AccountName ='"+accName+"'";
                    
        vvDB.ps = vvDB.conn.prepareStatement(query,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        int rerurn=0;
        vvDB.rs = vvDB.ps.executeQuery();
        int initRowCount = 0;
        initRowCount = vvDB.getRows(vvDB.rs);
        if(initRowCount==0 || initRowCount==-1 )  {
              sc.STAF_ReportEvent("Fail", "ClientUser Table", "Unable to Retrive the data", 0); 
         }else{
               
               while(vvDB.rs.next())
               {
            	   HashMap<String, String> columnValue = new HashMap<>();
            	   for(int i=0;i<colhead.length;i++){
            		   if(multiRowRetrive.equalsIgnoreCase("Yes"))  {
            			   rowList1.add(vvDB.rs.getString(colhead[i]));
            			   rowList1.add(vvDB.rs.getString(colhead[++i]).replaceAll("<br/><br/>", "\n\n").replaceAll("<a.*?</a>", "current fees").replaceAll("\\s+ ", " "));
            		   }else{
            			   columnValue.put(colhead[i], vvDB.rs.getString(colhead[i]));  
            		   }
                	}
            	   
                	if(!(usernmAgs.equalsIgnoreCase("")) && columnValue.get("Username").equalsIgnoreCase(usernmAgs) && multiRowRetrive.equalsIgnoreCase("No"))  {
                		rowList.add(columnValue);
                	}                	
                }    
                        
        }    
                
      }catch(Exception e){
                    e.printStackTrace();
                    sc.STAF_ReportEvent("Fail", "APILogTable-DB", "Unable to Retrieve data from Client User Table", 0);
                    throw e;
      }finally{
                    vvDB.cleanUpVVDAO();
      }
    if(multiRowRetrive.equalsIgnoreCase("Yes"))  {
    		return rowList1;
	   }else{
		   return rowList;
	   }
    
    }
    
    /**************************************************************************************************
	 * Method to navigate to Account Setting Page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String navigateAccountSetting() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			sc.waitforElementToDisplay("clientBGDashboard_accountsettings_link", timeOutinSeconds);
			//			System.out.println(tempRetval);
			
			tempRetval=clickButtonUntilValidation("clientBGDashboard_accountsettings_link","AccountSetting_panelHeading_lbl");
			//sc.clickWhenElementIsClickable("clientBGDashboard_accountsettings_link", (int) timeOutinSeconds);
			
			if(!sc.getWebText("AccountSetting_panelHeading_lbl").equals("My User Profile")){
					sc.STAF_ReportEvent("Fail", "Account Settings", "Unable to navigated to Account Settings page", 1);
					log.error("Unable to  navigated to Account Setting page");
					throw new Exception("Organizational user Unable to navigated to Account Settings page");
			}else{
					retval=Globals.KEYWORD_PASS;
					log.debug("Successfully navigated to Account Setting page");
					sc.STAF_ReportEvent("Pass", "Account Settings", "Successfully navigated to Account Setting page", 1);
			}
			
		}catch(Exception e){
			log.error("Unable to  navigated to Account Settings page | Exception - "+e.toString());
			sc.STAF_ReportEvent("Fail", "Account Settings", "Unable to navigated to Account Settings page", 1);
			throw e;
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to navigate to Account Setting-Organization Users Page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String navigateOrganizationUsers() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{

			sc.waitforElementToDisplay("AccountSetting_OrgUser_link", timeOutinSeconds);
			//			System.out.println(tempRetval);
			sc.clickWhenElementIsClickable("AccountSetting_OrgUser_link", (int) timeOutinSeconds);
			
			if(!sc.getWebText("AccountSetting_panelHeading_lbl").equals("Organization Users")){
					sc.STAF_ReportEvent("Fail", "Account Settings-Organization Users", "Unable to  navigated to Account Settings-Organization Users page", 1);
					log.error("Unable to  navigated to Account Setting-Organization Users page");
					throw new Exception("Organizational user Unable to navigated to Account Settings page");
			}else{
					retval=Globals.KEYWORD_PASS;
					log.debug("Successfully navigated to Account Setting-Organization Users page");
			}
			
		}catch(Exception e){
			log.error("Unable to  navigated to Account Settings-Organization Users page | Exception - "+e.toString());
			sc.STAF_ReportEvent("Fail", "Account Settings", "Unable to navigated to Account Settings page", 1);
			throw e;
		}
		return retval;
	}
	
	
	/**************************************************************************************************
	 * Method to navigate to Account Setting Page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyvalues() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		String tempRetval1=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{
			
			tempRetval=editUserPhoto();		
											
			String[] colhead={"FirstName", "LastName", "Title", "PhoneNumber","EmailAddress","AddressLine1","AddressLine2","City","State","Zipcode","IsEnabled","Username","RoleGroupId"};
			HashMap<String, String> fieldvalue = new HashMap<>();
			
         	for(int i=0;i<colhead.length;i++){
         		   String tag="input";
         		   if(colhead[i].equalsIgnoreCase("State")||colhead[i].equalsIgnoreCase("IsEnabled")){
         			   tag="select";
         			  if(colhead[i].equalsIgnoreCase("IsEnabled")){
         				 colhead[i]="status";
         			  }
         		   }else if(colhead[i].equalsIgnoreCase("UserId")){
         			   continue;
         		   }else if (colhead[i].equalsIgnoreCase("RoleGroupId")){
         			   fieldvalue.put(colhead[i], "1");  
         			   continue;
         		   }	   
         		   String abc=driver.findElement(By.xpath("//"+tag+"[@formcontrolname='"+colhead[i].toLowerCase()+"']")).getAttribute("value");
         		  if(colhead[i].equalsIgnoreCase("status")){
      				 colhead[i]="IsEnabled";
      				 if(abc.equalsIgnoreCase("true")){
      					 abc="1";
      				 }else{
      					 abc="0";
      				 }
      				
      			  }
         		   fieldvalue.put(colhead[i], abc);  
         		
             	}
         	
       		//String[] colhead={"FirstName", "LastName", "Title", "PhoneNumber","EmailAddress","AddressLine1","AddressLine2","City","State","Zipcode","IsEnabled","UserId","Username"};
         	String accName=sc.testSuiteXLS.getCellData_fromTestData("AccountName");
    		String selectSQL = "SELECT cu.FirstName, cu.LastName, cu.Title, cu.PhoneNumber, cu.EmailAddress, cu.AddressLine1, cu.AddressLine2, cu.City, cu.State, cu.Zipcode, cu.IsEnabled, cu.UserId,cu.RoleGroupId, u.Username FROM Client c JOIN ClientUser cu ON c.ClientId = cu.ClientId JOIN Users u ON cu.UserId = u.UserId WHERE c.AccountName ='"+accName+"'";
    		String usernameArg=fieldvalue.get("Username");
    		ArrayList<HashMap<String, String>> rowList1=retriveFeildsDB(selectSQL,colhead,usernameArg,"No");	
         	HashMap<String, String> fieldvalue1 = (HashMap<String, String>) rowList1.get(0);
         	boolean textMatched=fieldvalue1.equals(fieldvalue);
         	if(!textMatched==true){
         		sc.STAF_ReportEvent("Fail", "Account Setting-My Profile Page", "Fields Value are not matching as Expected", 1);
    			return retval;         		
         	}else{
         		sc.STAF_ReportEvent("Pass", "Account Setting-My Profile Page", "Fields Value are matching as per expected database table - clientuser values", 1);
         	}
         	String ActUserInfo=sc.getWebText("AccountSetting_UserInfo_txt");	 
     		String expectedText=fieldvalue1.get("FirstName")+" "+fieldvalue1.get("LastName")+"\n"+fieldvalue1.get("City")+", "+fieldvalue1.get("State")+"\n"+fieldvalue1.get("PhoneNumber")+"\n"+fieldvalue1.get("EmailAddress");
     		textMatched=expectedText.equals(ActUserInfo);
     		if(!textMatched==true){
         		sc.STAF_ReportEvent("Fail", "Account Setting-My Profile Page", "User info text missmatched. Not matching as expected. Expected text:"+expectedText, 1);
    			return retval;         		
         	}
         	
     		HashMap<String, String> inputouthash = new HashMap<>();
     		
     		inputouthash.put("input", "Blank;dghbhsghjkyht ghdyhjkl fgvshjkldg;abc123@#$");
     		inputouthash.put("output", "First Name is required;First Name must be no more than 30 characters;First Name must not contain numbers or symbols");
     		inputouthash.put("index","2");
     		inputouthash.put("inputtag", "input");
     		verifyFieldMyUserProfileTab(inputouthash);
     		
     		inputouthash.put("input", "Blank;dghbhsghjkyht ghdyhjkl fgvshjkldg;abc123@#$");
     		inputouthash.put("output", "Last Name is required;Last Name must be no more than 30 characters;Last Name must not contain numbers or symbols");
     		inputouthash.put("index","3");
     		inputouthash.put("inputtag", "input");
     		verifyFieldMyUserProfileTab(inputouthash);
     		
     		inputouthash.put("input", "acfghy#$%^ acfghy#$%^acfghy#$%^acfghy#$%^acfghy#$%^");
     		inputouthash.put("output", "Title must be no more than 50 characters");
     		inputouthash.put("index","4");
     		inputouthash.put("inputtag", "input");
     		verifyFieldMyUserProfileTab(inputouthash);
     		
     		inputouthash.put("input", "Blank;458985956;0145898569;1458985695;dfgh$%^&df");
     		inputouthash.put("output", "Phone number is required Phone number is in an invalid format;Phone number is in an invalid format;Phone number is in an invalid format;Phone number is in an invalid format;Phone number is in an invalid format");
     		inputouthash.put("index","5");
     		inputouthash.put("inputtag", "input");
     		verifyFieldMyUserProfileTab(inputouthash);
     		
     		inputouthash.put("input", "Blank;test.com");
     		inputouthash.put("output", "Email is required Email is in an invalid format;Email is in an invalid format");
     		inputouthash.put("index","6");
     		inputouthash.put("inputtag", "input");
     		verifyFieldMyUserProfileTab(inputouthash);
     		
     		inputouthash.put("input", "Blank");
     		inputouthash.put("output", "Address Line 1 is required");
     		inputouthash.put("index","7");
     		inputouthash.put("inputtag", "input");
     		verifyFieldMyUserProfileTab(inputouthash);
     		
     		inputouthash.put("input", "Blank");
     		inputouthash.put("output", "City is required");
     		inputouthash.put("index","9");
     		inputouthash.put("inputtag", "input");
     		verifyFieldMyUserProfileTab(inputouthash);
     		
     		inputouthash.put("input", "Blank;5678;675645");
     		inputouthash.put("output", "Zip Code is required Zip Code is in an invalid format;Zip Code is in an invalid format;Zip Code is in an invalid format");
     		inputouthash.put("index","11");
     		inputouthash.put("inputtag", "input");
     		verifyFieldMyUserProfileTab(inputouthash);
     		
     		inputouthash.put("input", "00456");
     		inputouthash.put("output", "Zipcode is invalid for the chosen state");
     		inputouthash.put("index","11");
     		inputouthash.put("inputtag", "input");
     		verifyFieldMyUserProfileTab(inputouthash);
     		
     		
     		
     		//verify status and credential fields 
     		Select viewDropDwn = new Select(driver.findElement(By.xpath("//select[@formcontrolname='status']")));
			String selectedVal=viewDropDwn.getFirstSelectedOption().getText();
			if(!selectedVal.equalsIgnoreCase("Active")){
				sc.STAF_ReportEvent("Fail", "Account Setting-My Profile Page", "Status Field Value is as Active. Actual :"+selectedVal, 1);
			}			
			String emptyValue[]={"currentpassword","password","reenterpassword","status","username"};
			for(String emptyValuetext:emptyValue){
				
				String actText="";
				String tag="select";
				if(!emptyValuetext.equalsIgnoreCase("status")){
					actText=driver.findElement(By.xpath("//input[@formcontrolname='"+emptyValuetext+"']")).getAttribute("value");
					tag="input";
				}
				String enbled=sc.isEnabled(driver.findElement(By.xpath("//"+tag+"[@formcontrolname='"+emptyValuetext+"']")));
				if(!actText.equalsIgnoreCase("") && !(emptyValuetext.equalsIgnoreCase("status")) && !(emptyValuetext.equalsIgnoreCase("username"))){
					sc.STAF_ReportEvent("Fail", "Account Setting-My Profile Page", emptyValuetext+" Field Value is not blank", 1);
				}else if((enbled.equalsIgnoreCase(Globals.KEYWORD_PASS)) && ((emptyValuetext.equalsIgnoreCase("status")) || (emptyValuetext.equalsIgnoreCase("username")) ||(emptyValuetext.equalsIgnoreCase("reenterpassword")))){
					sc.STAF_ReportEvent("Fail", "Account Setting-My Profile Page", emptyValuetext+" Field is not in disabled mode", 1);
				}
				
			}
			sc.clickElementUsingJavaScript("AccountSetting_uname&req_link");
			tempRetval=sc.waitTillElementDisplayed(driver.findElement(By.xpath("//*[starts-with(@id,'ngb-popover')]")), timeOutinSeconds);
     		
     		String actTxt=sc.getWebElementText(driver.findElement(By.xpath("//*[starts-with(@id,'ngb-popover')]/div")));
     		if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) && !actTxt.contains("Username:\nUse 8-50 letters or numbers\nSpaces and special characters are not allowed (except for @ or .)")){
     			sc.scrollIntoView(driver.findElement(By.xpath("//*[starts-with(@id,'ngb-popover')]")));
     			sc.STAF_ReportEvent("Fail", "Account Setting-My Profile Page", "Username and password requirement window text is not as per expected", 1);
     		}
     		
     		//password field validation
     		WebElement currentpasswe=driver.findElement(By.xpath("//input[@formcontrolname='currentpassword']"));
     		WebElement passwe=driver.findElement(By.xpath("//input[@formcontrolname='password']"));
     		WebElement reenterpasswe=driver.findElement(By.xpath("//input[@formcontrolname='reenterpassword']"));
     		sc.setValue(currentpasswe,"123az");
     		tempRetval=clickButtonUntilValidation("AccountSetting_Save_btn","AccountSetting_Confirm_popup");
     		sc.clickElementUsingJavaScript("AccountSetting_Confirm_Save_btn");
			sc.waitForPageLoad();
			int index=5;
			int index1=6;
			tempRetval=sc.waitTillElementDisplayed(driver.findElement(By.xpath("//div[@class='toast-error toast ng-trigger ng-trigger-flyInOut']")), 10);
			WebElement webelmter=driver.findElement(By.xpath("//div[2]/div["+index+"]/div/div[@class='alert alert-danger']"));
			WebElement webelmter2=driver.findElement(By.xpath("//div[2]/div["+index1+"]/div/div[@class='alert alert-danger']"));
			String errText=sc.getWebElementText(webelmter);
			String errText1=sc.getWebElementText(webelmter2);
			 if(!(errText.contentEquals("This field is required")) || !(errText1.contentEquals("This field is required")) || !(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
				 sc.STAF_ReportEvent("Fail", "error message", "Error message is not getting displayed for password field ", 1); 
			 }else{
				 sc.STAF_ReportEvent("Pass", "Account Setting-My Profile-Negative Validation", "Getting Error message Successfully", 1);
			 }					
			 sc.clickElementUsingJavaScript("AccountSetting_Cancel_btn");
			 sc.waitForPageLoad();
			 
			 
			 
			 String[] inputValue={"123az","abcsfgt","abcsfgt"};
			 WebElement[] webElemTxtField={currentpasswe,passwe,reenterpasswe};
			// webelmter=driver.findElement(By.xpath("//div[2]/div[5]/div/div[@class='alert alert-danger']"));
			 //WebElement toasterrmsg=driver.findElement(By.xpath("//div[@class='toast-error toast ng-trigger ng-trigger-flyInOut']"));
			// String expErrorText="Password must be at least 8 characters";
			 String errtctwebElmet[]={"//div[2]/div[5]/div/div[@class='alert alert-danger']","Password must be at least 8 characters","//div[@class='toast-error toast ng-trigger ng-trigger-flyInOut']"};
			 
			 negativeValPassField(inputValue,webElemTxtField,errtctwebElmet);
			
			 String[] inputValue1={"aaaa1111","aaaa1111","aaaa1111"};
			 //webelmter=driver.findElement(By.xpath("//div[2]/div[4]/div/div[@class='alert alert-danger']"));
			 //toasterrmsg=driver.findElement(By.xpath("//*[@id='toast-container']/div[2]/div']"));
			 //expErrorText="Current password did not match";
			 String errtctwebElmet1[]={"//div[2]/div[4]/div/div[@class='alert alert-danger']","Current password did not match","//*[@id='toast-container']/div[2]/div"};
			 negativeValPassField(inputValue1,webElemTxtField,errtctwebElmet1);
			 
			
			 
			 String[] inputValue2={"123qa123!","abc123xy","abc123yz"};
			// webelmter=driver.findElement(By.xpath("//div[2]/div[6]/div/div[@class='alert alert-danger']"));
			// toasterrmsg=driver.findElement(By.xpath("//div[@class='toast-error toast ng-trigger ng-trigger-flyInOut']"));
			 //expErrorText="Current password did not match";
			 String errtctwebElmet2[]={"//div[2]/div[6]/div/div[@class='alert alert-danger']","Doesn't match Password","//div[@class='toast-error toast ng-trigger ng-trigger-flyInOut']"};
			 negativeValPassField(inputValue2,webElemTxtField,errtctwebElmet2);
			
			
			 // change all values and change password , value verify with database 
			 HashMap<String, String> updatedValue = new HashMap<>();
			 String emailID = driver.findElement(By.xpath("//input[@formcontrolname='emailaddress']")).getAttribute("value");
			 String usrnm = driver.findElement(By.xpath("//input[@formcontrolname='username']")).getAttribute("value");
			 String values[]={"modified userFName", "modified userLName","modified title","9569569850",emailID,"modified test address Line","modified test address Line","modified cityname","MD","21913","1",usrnm,"1"};
			 int cnt=0;
			 for (String items : colhead) {
				
	         	 if(items.equalsIgnoreCase("State")){
	         		sc.selectValue_byValue("AccountSetting_state_dropdown", values[cnt]);  
	         		  
	         	 }else if(items.equalsIgnoreCase("IsEnabled") || items.equalsIgnoreCase("UserId") || items.equalsIgnoreCase("Username") || items.equalsIgnoreCase("Emailaddress") || items.equalsIgnoreCase("RoleGroupId")){
	         		updatedValue.put(items, values[cnt]); 
	         		cnt++;
	         		continue;
	         	 }else{
	         	       	WebElement webelmt = driver.findElement(By.xpath("//input[@formcontrolname='"+items.toLowerCase()+"']"));
	         	       	sc.setValue(webelmt, values[cnt]);	         	       		
	         	 }
	         	updatedValue.put(items, values[cnt]);  
	         	cnt++;	         	 
			}
			
	     	sc.setValue(currentpasswe,"123qa123!");
	     	sc.setValue(passwe,"123QA123!");
	     	sc.setValue(reenterpasswe,"123QA123!");
	     		     		
			 tempRetval=clickButtonUntilValidation("AccountSetting_Save_btn","AccountSetting_Confirm_popup");
	     	 sc.clickElementUsingJavaScript("AccountSetting_Confirm_Save_btn");
			 sc.waitForPageLoad();
		 	 tempRetval=sc.waitTillElementDisplayed(driver.findElement(By.xpath("//*[@id='toast-container']/div/div[2]")), 10);
			
				
			 if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
					 sc.STAF_ReportEvent("Fail", "Success Message", "Success message is not getting displayed for updating Account Setting-My Profile", 1); 
					 return retval;  
			 }else{
				 WebElement webelmtergettxt=driver.findElement(By.xpath("//*[@id='toast-container']/div/div[2]"));
				 String successText=sc.getWebElementText(webelmtergettxt).trim();
				 tempRetval=sc.isEnabled("AccountSetting_Save_btn");
				 Calendar c = Calendar.getInstance();
				 c.setTime(new Date());
				 c.add(Calendar.DATE, 90);
				 String formatDate=new SimpleDateFormat("M/d/yyyy").format(c.getTime());
				 tempRetval1=sc.waitTillElementDisplayed(driver.findElement(By.xpath("//span[contains(text(),'"+formatDate+"')]")), timeOutinSeconds);
				 
				 if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)) || !(successText.contentEquals("Your changes were successfully saved.")) || !(tempRetval1.equalsIgnoreCase(Globals.KEYWORD_PASS)) ){ 
					 sc.STAF_ReportEvent("Fail", "Success Message", "Save button is not disabled after updating Account Setting-My Profile", 1); 
					 return retval;
				 }
				 sc.STAF_ReportEvent("Pass", "Account Setting-My Profile-Update My profile", "Getting Success message Successfully for Update My profile", 1);
				 Globals.testSuiteXLS.setCellData_inTestData("OrgUserPwd", "123QA123!");
			 }		 
			
			selectSQL = "SELECT cu.FirstName, cu.LastName, cu.Title, cu.PhoneNumber, cu.EmailAddress, cu.AddressLine1, cu.AddressLine2, cu.City, cu.State, cu.Zipcode, cu.IsEnabled, cu.UserId, cu.RoleGroupId, u.Username FROM Client c JOIN ClientUser cu ON c.ClientId = cu.ClientId JOIN Users u ON cu.UserId = u.UserId WHERE c.AccountName ='"+accName+"'";
	    	String usernameArg1=updatedValue.get("Username");
	    	ArrayList<HashMap<String, String>> rowList2=retriveFeildsDB(selectSQL,colhead,usernameArg1,"No");	
	        HashMap<String, String> fieldvalue2 = (HashMap<String, String>) rowList2.get(0);
	       
	        if(!(fieldvalue2.equals(updatedValue))){
	         		sc.STAF_ReportEvent("Fail", "Account Setting-My Profile-Update My profile", "Fields Value are not matching as Expected", 0);
	    			return retval;         		
	        }else{
	        	sc.STAF_ReportEvent("Pass", "Account Setting-My Profile-Update My profile", "Updated values are matched with database table values", 0);    			
	        }
	        String orgUsername=sc.testSuiteXLS.getCellData_fromTestData("OrgUserName"); 
	        tempRetval=ClientFacingApp.orgUserLogOff();
			ClientFacingApp.orgUserLogin(orgUsername);
			Globals.testSuiteXLS.setCellData_inTestData("OrgUserPwd", "123qa123!");
			ClientFacingApp.orgUserLogOff();
	        	        
	       retval=Globals.KEYWORD_PASS; 
		}catch(Exception e){
			log.error("Unable to validate Account Setting-My Profile page | Exception - "+e.toString());		  
			sc.STAF_ReportEvent("Fail", "Account Setting-My Profile", "Unable to validate Account Setting-My Profile page",0);
			throw e;
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to negative validation in Account Setting Page
	 * @author Psave
	 * @throws Exception
	 ***************************************************************************************************/
	
	 public static void negativeValPassField(String [] inputValues,WebElement [] webElemTextField,String [] webEErrTxtWebe) throws Exception{
		 String tempRetval=Globals.KEYWORD_FAIL;
		 try{
			 sc.setValue(webElemTextField[0],inputValues[0]);
			 sc.setValue(webElemTextField[1],inputValues[1]);
			 sc.setValue(webElemTextField[2],inputValues[2]);
			 tempRetval=clickButtonUntilValidation("AccountSetting_Save_btn","AccountSetting_Confirm_popup");
			 sc.clickElementUsingJavaScript("AccountSetting_Confirm_Save_btn");
			 sc.waitForPageLoad();
			 
			 tempRetval=sc.waitTillElementDisplayed(driver.findElement(By.xpath(""+webEErrTxtWebe[2]+"")), 10);
			 String expErrText=sc.getWebElementText(driver.findElement(By.xpath(""+webEErrTxtWebe[0]+"")));
			 if(!(expErrText.contains(webEErrTxtWebe[1])) || !(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
				 sc.STAF_ReportEvent("Fail", "Error message", "Error message is not getting displayed for Password field ", 1); 
			 }else{
				 sc.STAF_ReportEvent("Pass", "Account Setting-My Profile-Negative Validation", "Getting Error message Successfully", 1);
			 }
			 sc.clickElementUsingJavaScript("AccountSetting_Cancel_btn");
			 sc.waitForPageLoad();
		   }catch(Exception e){
			   log.error("Unable to validate Account Setting-My Profile page-password fields validation | Exception - "+e.toString());		  
			   sc.STAF_ReportEvent("Fail", "Account Setting-My Profile", "Unable to validate Account Setting-My Profile page-password fields validation",1);
			   throw e;
		   }
	 }
	
	
	
	/**************************************************************************************************
	 * Method to negative validation in Account Setting Page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author Psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyFieldMyUserProfileTab(HashMap<String, String> inputouthash) throws Exception {
		APP_LOGGER.startFunction("Negative Validation My Profile");
		String tempRetval = Globals.KEYWORD_FAIL;
		String retval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		String inputvalues[] = inputouthash.get("input").split(";");
		String outValue[]=inputouthash.get("output").split(";");
		
		String putInput="";
		int i=0;
		if(inputouthash.containsKey("page")){
			if(inputouthash.get("page").equalsIgnoreCase("new user")){
				String inputfields[] = inputouthash.get("inputfields").split(";");
				for (String items : inputfields) {
					WebElement webelmt = driver.findElement(By.xpath("//input[@formcontrolname='"+items+"']"));
					if (inputvalues[i].equalsIgnoreCase("blank")) {
						putInput = "     ";
					}else if(outValue[0].equalsIgnoreCase("Zipcode is invalid for the chosen state")){
						WebElement webelmt1 = driver.findElement(By.xpath("//input[@formcontrolname='firstname']"));
						sc.setValue(webelmt1, "     ");
						putInput = inputvalues[i];
					}else {
						putInput = inputvalues[i];
					}
					sc.setValue(webelmt, putInput);
					sc.waitForPageLoad();
					i++;			
				}
			    sc.waitforElementToDisplay("AccountSetting_Save_btn", timeOutInSeconds); 
			    sc.waitForPageLoad();		
			    Thread.sleep(2000);
				tempRetval = clickButtonUntilValidation("AccountSetting_Save_btn", "AccountSetting_Confirm_popup");
				sc.clickElementUsingJavaScript("AccountSetting_Confirm_Save_btn");
				sc.waitForPageLoad();
				tempRetval = sc.waitTillElementDisplayed(driver.findElement(By.xpath("//div[@class='toast-error toast ng-trigger ng-trigger-flyInOut']")),timeOutInSeconds);
				i=0;
			
				List<WebElement> listofalertmsgWE = driver.findElements(By.xpath("//div[1]//div[@class='alert alert-danger']"));
				int msgSize=listofalertmsgWE.size();
				if(!(msgSize==outValue.length)){
					sc.STAF_ReportEvent("Fail", "Account Setting-New User-Negative Validation","All Error message is not getting Successfully", 1);
					return retval;
				}
			
				for(WebElement lst:listofalertmsgWE){
					String errText = sc.getWebElementText(lst);
					if (!(errText.contentEquals(outValue[i])) && !(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))) {
							sc.STAF_ReportEvent("Fail", "Account Setting-New User-Negative Validation","Error message is not getting Successfully", 1);
							return retval;
					}
					i++;
				}
				sc.STAF_ReportEvent("Pass", "Account Setting-New User-Negative Validation","All expected Error message is getting Successfully", 1);
				tempRetval = clickButtonUntilValidation("AccountSetting_Cancel_btn", "AccountSetting_Confirm_popup");					
				sc.clickElementUsingJavaScript("AccountSetting_Confirm_Cancel_btn");
				tempRetval = sc.waitTillElementDisplayed("AccountSetting_OrgUser_table",timeOutInSeconds);
				if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
						sc.STAF_ReportEvent("Fail", "Negative Validation - Organization New User", "Unable to  navigated to New Organization User page after click on cancel changes button", 1);
						log.error("Unable to  navigated to New Organization User page");
						return retval;
				}
				tempRetval = clickButtonUntilValidation("AccountSetting_NewUser_btn", "AccountSetting_UseCompanyContact_btn");
				sc.waitForPageLoad();
				Thread.sleep(3000);
				if(!sc.getWebText("AccountSetting_panelHeading_lbl").contains("Organization User Details")){
						sc.STAF_ReportEvent("Fail", "Negative Validation - Organization New User", "Unable to  navigated to New Organization User page", 1);
						log.error("Unable to  navigated to New Organization User page");
						return retval;
				}
				return Globals.KEYWORD_PASS;
			}else{
				return Globals.KEYWORD_FAIL;
			}
		}else{
			int index=Integer.parseInt(inputouthash.get("index"));
			for (String items : inputvalues) {
				tempRetval = sc.isEnabled("AccountSetting_Save_btn");
				if (tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) {
					sc.STAF_ReportEvent("Fail", "Account Setting-My Profile-Negative Validation","Save Button is not in disabled mode when page is load", 1);
				}
				WebElement webelmt = driver.findElement(By.xpath("//div[1]/div[" + index + "]/div/input"));
				if (items.equalsIgnoreCase("blank")) {
					putInput = "     ";
				}else if(outValue[0].equalsIgnoreCase("Zipcode is invalid for the chosen state")){
					WebElement webelmt1 = driver.findElement(By.xpath("//input[@formcontrolname='firstname']"));
					sc.setValue(webelmt1, "     ");
					sc.waitForPageLoad();
					putInput = inputvalues[i];
				}else {
					putInput = items;
				}
				sc.setValue(webelmt, putInput);
				sc.waitForPageLoad();
				//WebElement webelmt1 = driver.findElement(By.xpath("//input[@formcontrolname='currentpassword']"));
				webelmt.sendKeys(Keys.TAB);
				webelmt.sendKeys(Keys.ENTER);
				sc.waitforElementToDisplay("AccountSetting_Save_btn", timeOutInSeconds); 
				sc.waitForPageLoad();		
				Thread.sleep(3000); 
				tempRetval = clickButtonUntilValidation("AccountSetting_Save_btn", "AccountSetting_Confirm_popup");
                
				/*
				 * tempRetval=sc.waitTillElementDisplayed(
				 * "AccountSetting_Save_btn", timeOutInSeconds);
				 * sc.clickElementUsingJavaScript("AccountSetting_Save_btn");
				 * int cnt=0; while (cnt<5) { sc.waitForPageLoad();
				 * tempRetval=sc.waitTillElementDisplayed(
				 * "AccountSetting_Confirm_popup", timeOutInSeconds);
				 * if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){ break;
				 * } tempRetval=sc.waitTillElementDisplayed(
				 * "AccountSetting_Save_btn", timeOutInSeconds);
				 * if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				 * sc.clickElementUsingJavaScript("AccountSetting_Save_btn"); }
				 * cnt++; }
				 */
				tempRetval = sc.waitTillElementDisplayed("AccountSetting_Confirm_popup", timeOutInSeconds);
				// driver.findElement(By.xpath("//*[@id='cdk-overlay-0']//*[@class='mat-dialog-container
				// ng-tns-c7-0 ng-trigger
				// ng-trigger-slideDialog']//button[2]")).sendKeys(Keys.ENTER);
				sc.clickElementUsingJavaScript("AccountSetting_Confirm_Save_btn");
				sc.waitForPageLoad();
				tempRetval = sc.waitTillElementDisplayed(driver.findElement(By.xpath("//div[@class='toast-error toast ng-trigger ng-trigger-flyInOut']")),timeOutInSeconds);

				WebElement webelmter = driver.findElement(By.xpath("//div[1]/div[" + index + "]/div/div[@class='alert alert-danger']"));
				String errText = sc.getWebElementText(webelmter);
				if (!(errText.contentEquals(outValue[i])) && !(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))) {
					sc.STAF_ReportEvent("Fail", "Account Setting-My Profile-Negative Validation",
							"Error message is not getting Successfully", 1);
				} else {
					sc.STAF_ReportEvent("Pass", "Account Setting-My Profile-Negative Validation",
							"Getting Error message Successfully", 1);
				}
				sc.clickElementUsingJavaScript("AccountSetting_Cancel_btn");
				sc.waitForPageLoad();
				i++;
			}
			retval=Globals.KEYWORD_PASS;
			return retval;
		}
		
	 
	}
	
	
	/**************************************************************************************************
	 * Method to edit Users photo in Account Setting Page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author Psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String editUserPhoto() throws Exception {
		APP_LOGGER.startFunction("editUserPhoto");
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds =20;

		String stepName = "AccountSetting-ClientUser-EditPhoto";

		tempRetval = sc.waitforElementToDisplay("AccountSetting_editphoto_link", timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Fail", stepName, "Edit Photo Link is not displayed", 1);
			return tempRetval;
		}
		
		// upload the photo
		String updatedFilePath = Globals.TestDir +"\\src\\test\\Resources\\refFile\\VV_UploadPhoto.jpg";
		driver.findElement((By.cssSelector("input[type='file']"))).sendKeys(updatedFilePath);
        Thread.sleep(3000); 
		//verify that spinner.gif image should not displayed after providing correct image file type
        
		tempRetval = sc.waitTillElementDisplayed("AccountSetting_editphoto_spinner_img",timeOutInSeconds);
		if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
			sc.STAF_ReportEvent("Pass", "Verify the Edit photo functionality on the My User profile tab.", "Photo has been uploaded", 1);
		}else{
			sc.STAF_ReportEvent("Fail", "Verify the Edit photo functionality on the My User profile tab.", "Unable to upload photo", 1);
			return tempRetval;
		}
		return Globals.KEYWORD_PASS;
	}
	/**************************************************************************************************
	 * Method to edit Users photo in Account Setting Page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author Psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String clickButtonUntilValidation(String elementtobeclick, String expectedconditionelement) throws Exception {
		String tempRetval = Globals.KEYWORD_FAIL;
		int timeOutInSeconds=30;
		try{
		tempRetval=sc.waitTillElementDisplayed(elementtobeclick, timeOutInSeconds);
		 sc.clickElementUsingJavaScript(elementtobeclick);
		 int cnt=0;
		 while (cnt<5) {
			 sc.waitForPageLoad();	
			 tempRetval=sc.waitTillElementDisplayed(expectedconditionelement, timeOutInSeconds);
			 if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				 break;
			 }
			 tempRetval=sc.waitTillElementDisplayed(elementtobeclick, timeOutInSeconds);
			 if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				 sc.clickElementUsingJavaScript(elementtobeclick);
			 }
			 cnt++;
		 }
		}catch(Exception e){
			log.error("Unable to Click button "+elementtobeclick+" | Exception - "+e.toString());		  
			sc.STAF_ReportEvent("Fail", "Account Setting-My Profile", "Unable to Click button "+elementtobeclick,1);
			throw e;
		}
		return tempRetval;
	}
	/**************************************************************************************************
	 * Method to navigate to Account Setting Page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String nagativeValidationForNewUser() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;
		int rowId   = 	Globals.INT_FAIL;

		try{
			//click on new user link
			tempRetval=sc.waitTillElementDisplayed("AccountSetting_NewUser_btn", timeOutinSeconds);
			 if(!tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				 sc.STAF_ReportEvent("Fail", "Negative Validation - Organization New User", "New User button is not displayed", 1);
				 return tempRetval;
			 }
			 
			 sc.clickElementUsingJavaScript("AccountSetting_NewUser_btn");
			 
			 if(!sc.getWebText("AccountSetting_panelHeading_lbl").contains("Organization User Details")){
					sc.STAF_ReportEvent("Fail", "Negative Validation - Organization New User", "Unable to  navigated to New Organization User page", 1);
					log.error("Unable to  navigated to New Organization User page");
					return retval;
			 }
			 
			 // New User error message validation
			 
			 HashMap<String, String> inputouthash = new HashMap<>();
	     		     		
	     	inputouthash.put("inputfields", "firstname;lastname;title");
	     	inputouthash.put("input", "Blank;Blank;qazwsxedcr          1234567890         qazwsxedcr!@#");
	     	inputouthash.put("output", "First Name is required;Last Name is required;Title must be no more than 50 characters;Phone number is required Phone number is in an invalid format;Email is required Email is in an invalid format;Address Line 1 is required;City is required;State is required;Zip Code is required Zip Code is in an invalid format;Permission is required");
	     	inputouthash.put("page", "new user");
	     	tempRetval=verifyFieldMyUserProfileTab(inputouthash);
			 
	     	inputouthash.put("inputfields", "firstname;lastname;phonenumber;emailaddress;addressline1;addressline2;city;zipcode");
     		inputouthash.put("input", "qazwsxedcr          1234567890         qazwsxedcr!@#;qazwsxedcr          1234567890         qazwsxedcr!@#;458985956;test.com;qazwsxedcr          1234567890         qazwsxedcr!@#;qazwsxedcr          1234567890         qazwsxedcr!@#;qazwsxedcr          1234567890         qazwsxedcr!@#;5678");
     		inputouthash.put("output", "First Name must not contain numbers or symbols First Name must be no more than 30 characters;Last Name must not contain numbers or symbols Last Name must be no more than 30 characters;Phone number is in an invalid format;Email is in an invalid format;Address Line 1 must be no more than 50 characters;Address Line 2 must be no more than 50 characters;City must be no more than 30 characters;State is required;Zip Code is in an invalid format;Permission is required");
     		inputouthash.put("page", "new user");
     		tempRetval=verifyFieldMyUserProfileTab(inputouthash);	
			
     		//verify use company contact button , permission and status section validation
     		sc.clickElementUsingJavaScript("AccountSetting_UseCompanyContact_btn");
     		sc.waitForPageLoad();
     		Thread.sleep(2000);
     		String inputfields[]={"firstname","lastname","title","phonenumber","emailaddress","addressline1","addressline2","city", "state","zipcode", "rolegroupid","status","username","resetpassword"};
     		String outputValue[]={"","","","9812345678","","test address Line","test address Line","Putnam","Fl","32007", "","true","","on"};
     		int i=0;
     		for (String item : inputfields){
				String tagType="input";
				if(item.equalsIgnoreCase("state")||item.equalsIgnoreCase("rolegroupid") || item.equalsIgnoreCase("status") ){
					tagType="select";
				}
				if(item.equalsIgnoreCase("status") || item.equalsIgnoreCase("username") || item.equalsIgnoreCase("resetpassword")){
					tempRetval=sc.isEnabled(driver.findElement(By.xpath("//"+tagType+"[@formcontrolname='"+item+"']")));
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						sc.STAF_ReportEvent("Fail", "Account Setting-Organization New User", item+" field is not disbaled mode for creating new user", 1);
					}
					if(item.equalsIgnoreCase("resetpassword")){
						tempRetval=sc.isSelected(driver.findElement(By.xpath("//"+tagType+"[@formcontrolname='"+item+"']")));
						if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
							sc.STAF_ReportEvent("Fail", "Account Setting-Organization New User", item+" field is not in selected mode for creating new user", 1);
						}
						String spanText=driver.findElement(By.xpath("//div[@class='col-sm-9']/div/span")).getText();
						if(!spanText.contains("PLEASE NOTE: The user will receive an email with a temporary password and login instructions within 24 hours.")){
							sc.STAF_ReportEvent("Fail", "Account Setting-Organization New User", item+" field is not in selected mode for creating new user", 1);
						}
					}
				}
				String actualText=driver.findElement(By.xpath("//"+tagType+"[@formcontrolname='"+item+"']")).getAttribute("value");
				if(!(actualText.equalsIgnoreCase(outputValue[i]))){
					sc.STAF_ReportEvent("Fail", "Account Setting-Organization New User", item+" field text is not matching with expected.", 1);
				}
				
				i++;
			}
     		// username & password requirement pop up window validation
     		sc.clickElementUsingJavaScript("AccountSetting_uname&req_link");
			tempRetval=sc.waitTillElementDisplayed(driver.findElement(By.xpath("//*[starts-with(@id,'ngb-popover')]")), timeOutinSeconds);
     		
     		String actTxt=sc.getWebElementText(driver.findElement(By.xpath("//*[starts-with(@id,'ngb-popover')]/div")));
     		if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) && !actTxt.contains("Username:\nUse 8-50 letters or numbers\nSpaces and special characters are not allowed (except for @ or .)")){
     			sc.scrollIntoView(driver.findElement(By.xpath("//*[starts-with(@id,'ngb-popover')]")));
     			sc.STAF_ReportEvent("Fail", "Account Setting-Organization New User", "Username and password requirement window text is not as per expected", 1);
     		}
     		
     		// permission validation 
     		Select viewDropDwn = new Select(driver.findElement(By.xpath("//select[@formcontrolname='rolegroupid']")));
			String verifyItems[]={"Administrator","Standard User","Restricted User"};
		    String disbaledDDItems[]={"Select a permission","Customized User"};
		    String permissionUserText[]={"This access should be granted to program owners that control authorized users to the system.","Most users should have this access with full ordering and viewing abilities.","This access should be granted to users that can initiate orders but should not see full results for privacy reasons"};
			sc.verifyDropdownItems("AccountSetting_permission_dropdown", verifyItems,"Verify option of Permission Dropdown On Account Setting-Organization New User");			
			List<WebElement> ddElements = driver.findElements(By.xpath("//select[@formcontrolname='rolegroupid']//option[@disabled='']"));
			i=0;
		
			for (WebElement ddElement : ddElements) {
					String ddItemText= ddElement.getText().trim();
					if(!disbaledDDItems[i].equalsIgnoreCase(ddItemText)){
						seleniumCommands.STAF_ReportEvent("Fail", "Account Setting-Organization New User", "Permission Dropdown items"+disbaledDDItems[i]+" is not in disabled mode",0);
					}
					i=i+1;
			}
			i=0;
			for (String ddElement : verifyItems) {
				sc.selectValue_byVisibleText("AccountSetting_permission_dropdown", ddElement);
				String webTxt=sc.getWebElementText(driver.findElement(By.xpath("//div[2]/div[3]/div/div")));
				if(!permissionUserText[i].equalsIgnoreCase(webTxt)){
					seleniumCommands.STAF_ReportEvent("Fail", "Account Setting-Organization New User", "Permission Dropdown items"+disbaledDDItems[i]+" is not in disabled mode",0);
				}
				i=i+1;
			}
			
			tempRetval = clickButtonUntilValidation("AccountSetting_Cancel_btn", "AccountSetting_Confirm_popup");					
			sc.clickElementUsingJavaScript("AccountSetting_Confirm_Cancel_btn");
			tempRetval = sc.waitTillElementDisplayed("AccountSetting_OrgUser_table",10);
			if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
					sc.STAF_ReportEvent("Fail", "Negative Validation - Organization New User", "Unable to  navigated to New Organization User page after click on cancel changes button", 1);
					log.error("Unable to  navigated to New Organization User page");
					return retval;
			 }
			
			//verify org user table - custom user should be displayed
			rowId=verifyUserOnUserTable("Customized User", 3, "Customized User");
			if(rowId == Globals.INT_FAIL){
			    sc.STAF_ReportEvent("Fail", "Account Setting-Restricted Users Search", "Unable to search Customized User on Org Users table", 1);
			    return Globals.KEYWORD_FAIL;
		    }
					
						
			//create new Restricted User , fname and lname unique 
			String[] colhead={"FirstName", "LastName", "Title", "PhoneNumber","EmailAddress","AddressLine1","AddressLine2","City","State","Zipcode","IsEnabled","Username","RoleGroupId"};
			
			String fname = RandomStringUtils.randomAlphabetic(6) ;
			String lname = RandomStringUtils.randomAlphabetic(10) ;
			String fnamelnameUsername =fname.charAt(0)+lname;
			String values1[]={fname, lname,"rstitle","9569564560",Globals.fromEmailID,"rs test address Line 1","rs test address Line 2","rs cityname","MD","21913","1",fnamelnameUsername,"5"};
			tempRetval = createNewUser("Restricted User",colhead,values1);
			String typeSearch=lname+", "+fname;
			rowId=verifyUserOnUserTable(typeSearch, 1, "Restricted User");
			if(rowId == Globals.INT_FAIL){
			    sc.STAF_ReportEvent("Fail", "Account Setting-Restricted Users Search", "Unable to search username - "+typeSearch+" on Org Users table", 1);
			    return Globals.KEYWORD_FAIL;
		    }
			//verify welcome email after creating user, login user change password
			tempRetval=verifyWelcomeEmails(fnamelnameUsername,fname,lname,"False");
			//reset password flow
			tempRetval=resetPasswordAC(fnamelnameUsername,rowId,"Restricted User",fname);
			
			//create standarad user, email address unique
			String emailusername = "User" + sc.runtimeGeneratedStringValue(6) + "@sduser.com";
			
			String values[]={"sduserFName", "sduserLName","sdtitle","9569569850",emailusername,"sd test address Line 1","sd test address Line 2","sd cityname","MD","21913","1",emailusername,"2"};
			tempRetval = createNewUser("Standard User",colhead,values);
			rowId=verifyUserOnUserTable(emailusername, 7, "Standard User");
			if(rowId == Globals.INT_FAIL){
				    sc.STAF_ReportEvent("Fail", "Account Setting-Standard User Search", "Unable to search username - "+emailusername+" on Org Users table", 1);
				    return Globals.KEYWORD_FAIL;
			
			}
			// tempRetval=orgUserLogin(emailusername,"Temp123!"); //not verifying using default password as this functionality is out of scope
			
			
			
			// verify customized user displayed on table
			
			retval=Globals.KEYWORD_PASS;
		}catch(Exception e){
			log.error("Unable to perform validation on Account Setting- New User creation page | Exception - "+e.toString());
			retval = Globals.KEYWORD_FAIL;
			throw e;
		
		}
		return retval;
	}

	
	/**************************************************************************************************
	 * Method to create new user using account setting page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String createNewUser(String userType, String colhead[],String values[]) throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;

		try{
			//click on new user link
			tempRetval=sc.waitTillElementDisplayed("AccountSetting_NewUser_btn", timeOutinSeconds);
			 if(!tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
				 sc.STAF_ReportEvent("Fail", "Organization New User Creation", "New User button is not displayed", 1);
				 return tempRetval;
			 }
			 
			 sc.clickElementUsingJavaScript("AccountSetting_NewUser_btn");
			 
			 if(!sc.getWebText("AccountSetting_panelHeading_lbl").contains("Organization User Details")){
					sc.STAF_ReportEvent("Fail", "Account Setting - Organization New User", "Unable to  navigated to New Organization User page", 1);
					throw new Exception("Organization New User creation "+userType+ " - Unable to  navigated to New Organization User page");
			 }
			 
			 HashMap<String, String> updatedValue = new HashMap<>();
			
			 int cnt=0;
			 for (String items : colhead) {
				
	         	 if(items.equalsIgnoreCase("State")){
	         		sc.selectValue_byValue("AccountSetting_state_dropdown", values[cnt]);  
	         		  
	         	 }else if(items.equalsIgnoreCase("IsEnabled") || items.equalsIgnoreCase("UserId") || items.equalsIgnoreCase("Username") || items.equalsIgnoreCase("Username")||items.equalsIgnoreCase("RoleGroupId") ){
	         		updatedValue.put(items, values[cnt]); 
	         		cnt++;
	         		continue;
	         	 }else if(items.equalsIgnoreCase("Username")){
	         		  	 String unmTxt=driver.findElement(By.xpath("//input[@formcontrolname='"+items.toLowerCase()+"']")).getAttribute("value").trim();       		 
	         			 updatedValue.put(items, unmTxt); 
	         			 cnt++;
	         			 continue;
	         		  
		         }else{
	         	       	WebElement webelmt = driver.findElement(By.xpath("//input[@formcontrolname='"+items.toLowerCase()+"']"));
	         	       	sc.setValue(webelmt, values[cnt]);	         	       		
	         	 }
	         	updatedValue.put(items, values[cnt]);  
	         	cnt++;	         	 
			}
			sc.selectValue_byVisibleText("AccountSetting_permission_dropdown", userType);  // set user
			
			//verify account info text displayed correctly 
			String ActUserInfo=sc.getWebText("AccountSetting_UserInfo_txt");	 
     		String expectedText=updatedValue.get("FirstName")+" "+updatedValue.get("LastName")+"\n"+updatedValue.get("City")+" , "+updatedValue.get("State")+"\n"+updatedValue.get("PhoneNumber")+"\n"+updatedValue.get("EmailAddress");
     		boolean textMatched=expectedText.equals(ActUserInfo);
     		if(!textMatched==true){
         		sc.STAF_ReportEvent("Fail", "Account Setting-Organization New User-"+userType, "User info text missmatched. Not matching as expected. Expected text:"+expectedText, 1);
    			return retval;         		
         	}
				
			 tempRetval=clickButtonUntilValidation("AccountSetting_Save_btn","AccountSetting_Confirm_popup");
	     	 sc.clickElementUsingJavaScript("AccountSetting_Confirm_Save_btn");
			 sc.waitForPageLoad();
			 tempRetval = sc.waitTillElementDisplayed("AccountSetting_OrgUser_table",10);
			if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
						sc.STAF_ReportEvent("Fail", "Account Setting - Organization New User"+userType, "Unable to create new user on account setting page", 1);
						log.error("Unable to create new user on a/c setting page");
						throw new Exception("Organization New User creation "+userType+ " - Unable to create new user on account setting page");
						
			}		else{
				sc.STAF_ReportEvent("Pass", "Account Setting - Organization New User"+userType, "New "+userType+" user is created with username as - "+updatedValue.get("Username"), 1);
			}
			
			String accName=sc.testSuiteXLS.getCellData_fromTestData("AccountName"); 
			String selectSQL = "SELECT cu.FirstName, cu.LastName, cu.Title, cu.PhoneNumber, cu.EmailAddress, cu.AddressLine1, cu.AddressLine2, cu.City, cu.State, cu.Zipcode, cu.IsEnabled, cu.UserId, cu.RoleGroupId, u.Username FROM Client c JOIN ClientUser cu ON c.ClientId = cu.ClientId JOIN Users u ON cu.UserId = u.UserId WHERE c.AccountName ='"+accName+"'";
	    	String usernameArg1=updatedValue.get("Username");
	    	ArrayList<HashMap<String, String>> rowList2=retriveFeildsDB(selectSQL,colhead,usernameArg1,"No");	
	        HashMap<String, String> fieldvalue2 = (HashMap<String, String>) rowList2.get(0);
	       
	        if(!(fieldvalue2.equals(updatedValue))){
	         		sc.STAF_ReportEvent("Fail", "Account Setting-New User Creation", "Fields Value are not matching as Expected", 0);
	    			return retval;         		
	        }else{
	        	sc.STAF_ReportEvent("Pass", "Account Setting-New User Creation-"+userType, "Updated values are matched with database table values for username - "+usernameArg1, 0); 
	        	retval=Globals.KEYWORD_PASS;
	        }
			
			
			
     		
		}catch(Exception e){
			log.error("Unable to create new user on Account Settings page | Exception - "+e.toString());
			sc.STAF_ReportEvent("Fail", "Account Setting-New User Creation", "Unable to create new user on Account Settings page", 1);
			throw e;	
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to create new user using account setting page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static int verifyUserOnUserTable(String username, int uniquColNoSearch, String userType) throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		int retval=Globals.INT_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;
		int i=0,cnt=0;
		boolean accountFound=false;
		int rowCount = Globals.INT_FAIL;
		String accountNameInUI;

		try{
			//click on new user link
			sc.waitForPageLoad();
			tempRetval = sc.waitTillElementDisplayed("AccountSetting_OrgUser_table",10);
			
			if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
					sc.STAF_ReportEvent("Fail", "Negative Validation - Organization New User", "Unable to  navigated to New Organization User page after click on cancel changes button", 1);
					log.error("Unable to  navigated to New Organization User page");
					return retval;
			}
		
			
			if (sc.waitforElementToDisplay("AccountSetting_OrgUser_table",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){ 
				WebElement accountsTable = sc.createWebElement("AccountSetting_OrgUser_table");
				Thread.sleep(5000);
				accountNameInUI = sc.getCellData_tbl(accountsTable, 1, 1);
				while(accountNameInUI.contains("No records found")){
					Thread.sleep(10000);
					accountNameInUI = sc.getCellData_tbl(accountsTable, 1, 1).trim();
					if(cnt==5){
					   break;	
					}
					log.info("Method searchAccount | Account result grid displayed with " + rowCount + " account(s)" );
					cnt++;
					
				}
											
				rowCount=sc.getRowCount_tbl(accountsTable);
				if(rowCount != Globals.INT_FAIL){
					log.info("Method searchAccount | Account result grid displayed with " + rowCount + " account(s)" );
					
					if (rowCount == 1) {
						accountNameInUI = sc.getCellData_tbl(accountsTable, 1, 1);
						if (accountNameInUI.contains("No records found")) {
							accountFound = false;
						} else {
							accountNameInUI = sc.getCellData_tbl(accountsTable, 1, uniquColNoSearch).trim();
							if (accountNameInUI.equalsIgnoreCase(username)) {
								accountFound = true;
								i = 1;
							}
						}
					}else {
						for (i = 1; i <= rowCount; i++) {
							accountNameInUI = sc.getCellData_tbl(accountsTable, i, uniquColNoSearch).trim();
							if (accountNameInUI.equalsIgnoreCase(username)) {
								accountFound = true;
								break;
							}

						}
					}
					
					if(accountFound == true){
						String status=sc.getCellData_tbl(accountsTable, i, 5).trim();
						String permission=sc.getCellData_tbl(accountsTable, i, 3).trim();
						if(!(status.equalsIgnoreCase("Active") && permission.equalsIgnoreCase(userType))){
							sc.STAF_ReportEvent("Fail", "Account Setting - Organization New User - "+userType, "Username- "+username+" is not displayed on org user table for account setting page", 1);	
						}else{
							sc.STAF_ReportEvent("Pass", "Account Setting - Organization New User - "+userType, "Username- "+username+" is getting displayed on org user table for account setting page", 1);	
						}
						
						return i; // row number of the user found
					}

	
				}else{
					 log.info("Method searchAccount | Account Grid is not displayed| Account Name:- "+ username);
	
				}
							
			}
		}catch(Exception e){
			log.error("Account Setting - Organization New User - "+userType+" verify on user table | Exception - "+e.toString());
			sc.STAF_ReportEvent("Fail", "Account Setting - Organization New User - "+userType, "User is not displayed on org user table for account setting page", 1);
			throw e;	
				
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method to reset password flow using account setting page
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String resetPasswordAC(String username, int rwId, String userType, String fname) throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;
		int i=0;
		String emailFound=null;
		String emailBodyText =null;
		String newPassword = null;
		VVDAO vvDB             = null;
		String newConfirmPwd=null;
		HashMap<String, String> emailDetails=  new HashMap<String, String>();

		try{
			//click on new user link
			tempRetval = sc.waitTillElementDisplayed("AccountSetting_OrgUser_table",10);
			if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
					sc.STAF_ReportEvent("Fail", "Account Setting-"+userType+" User-Reset Password Flow", "Unable to  navigated to New Organization User page after click on cancel changes button", 1);
					log.error("Unable to  navigated to New Organization User page");
					return retval;
			}
			
			if (sc.waitforElementToDisplay("AccountSetting_OrgUser_table",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){ 
				//WebElement accountsTable = sc.createWebElement("AccountSetting_OrgUser_table");
				driver.findElement(By.xpath("//table[@class='undefined']/tbody/tr["+rwId+"]")).click();
				tempRetval = sc.checkCheckBox("AccountSetting_resetpass_chks");
				if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
					sc.STAF_ReportEvent("Fail", "Account Setting-"+userType+" User-Reset Password Flow", "Unable to marked Reset Password checkbox as checked ", 1);
					log.error("Unable to marked Reset Password checkbox as checked");
					return retval;
			     } 
				tempRetval = sc.waitTillElementDisplayed(driver.findElement(By.xpath("//span[contains(text(),'Temporary password will be sent when changes are saved')]")),10);
				if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
					sc.STAF_ReportEvent("Fail", "Account Setting-"+userType+" User-Reset Password Flow", "After click on Reset Password cheeckbox , unable to get label text", 1);
					log.error("After click on Reset Password cheeckbox , unable to get label text");
					return retval;
				}	
				
			    // verify password expiration date should be displayed as 90 days
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.DATE, 90);
				String formatDate=new SimpleDateFormat("M/d/yyyy").format(c.getTime());
				tempRetval=sc.waitTillElementDisplayed(driver.findElement(By.xpath("//span[contains(text(),'"+formatDate+"')]")), timeOutinSeconds);
				String tempRetval1=sc.waitTillElementDisplayed(driver.findElement(By.xpath("//span[contains(text(),'No')]")), timeOutinSeconds); 
				 if( !((tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)) && (tempRetval1.equalsIgnoreCase(Globals.KEYWORD_PASS))) ){ 
					 sc.STAF_ReportEvent("Fail", "Account Setting-"+userType+" User-Reset Password Flow", "Password expiration date and account locked infor displayed missmatched expected - "+formatDate+" & No", 1); 
					 return retval;
				 }
				
				tempRetval=clickButtonUntilValidation("AccountSetting_Save_btn","AccountSetting_Confirm_popup");
		     	sc.clickElementUsingJavaScript("AccountSetting_Confirm_Save_btn");
				sc.waitForPageLoad();
				tempRetval = sc.waitTillElementDisplayed("AccountSetting_OrgUser_table",10);
				if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
							sc.STAF_ReportEvent("Fail", "Account Setting-"+userType+" User-Reset Password Flow", "After saving the info unable to get Org User Table", 1);
							log.error("Unable to create new user on a/c setting page");
							return retval;
				}	
				
								
				String subjectEmailPattern = "Your Password Reset Information is Here!";
				emailDetails = Email.fetchEmailFromOutlook(subjectEmailPattern,fname,60);
				emailFound = emailDetails.get("EmailFound");

				if (emailFound.equalsIgnoreCase("True")){
					emailBodyText = emailDetails.get("EmailBody");
					emailBodyText = emailBodyText.replace("\"", "").trim();
					int startIndex = emailBodyText.indexOf("Temporary Password: ");
					int endindex = emailBodyText.indexOf("Reset Password Link:");
					newPassword = emailBodyText.substring(startIndex, endindex).replace("Temporary Password: ","").trim();

				    //			Globals.testSuiteXLS.setCellData_inTestData("OrgUserPwd", newPassword);

				    sc.STAF_ReportEvent("Pass", "Account Setting-"+userType+" User-Reset Password Flow", "UserName - "+username+ " have received Password viva email successfully temp pass - "+ newPassword, 0);
				    retval=Globals.KEYWORD_PASS;
				}else{
					sc.STAF_ReportEvent("Fail", "Account Setting-"+userType+" User-Reset Password Flow", "Reset client user password email not received ", 0);
					retval = Globals.KEYWORD_FAIL;
					return retval;
				}
				
				tempRetval=ClientFacingApp.orgUserLogOff();
				orgUserLogin(username,newPassword);
				ClientFacingApp.orgUserLogOff();
				username=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
				ClientFacingApp.orgUserLogin(username);
				navigateAccountSetting();
				navigateOrganizationUsers();
				
			}
		}catch(Exception e){
			log.error("Unable to  navigated to Account Settings page | Exception - "+e.toString());
			sc.STAF_ReportEvent("Fail", "Account Setting - Organization New User - "+userType, "Password Reset validation is not working as expected", 1);
			throw e;	
		}
		return retval;
	}
	
	/**************************************************************************************************
	 * Method for an organization user login by launching the corresponding url
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String orgUserLogin(String orgUserName, String passwd ) throws Exception {
		APP_LOGGER.startFunction("OrgUserLogin");
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =20;

		try {
			try{// the below code would throw an exception when the method is called for first cause no browser has been launchde yet
				if(driver != null && !driver.toString().contains("null")){
					driver.switchTo().defaultContent();
					tempRetval=sc.waitforElementToDisplay("clientBGDashboard_logout_link", 5);

					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
						ClientFacingApp.orgUserLogOff();

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
					tempRetval = ClientFacingApp.orgUserLogOff();
					if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
						
							sc.STAF_ReportEvent("Fail", "OrganizationUserLogin", "Login Page has NOT been loaded", 1);
							log.error("Organizational Unable to Log In As URL not Launched ");
							throw new Exception("Organizational user Unable to Log In As Lo page has not Launched");
					}
						
				}
			}

						
			tempRetval=sc.setValueJsChange("login_orgUsername_txt", orgUserName);
			tempRetval=sc.setValueJsChange("login_orgPassword_txt",passwd);

			
			sc.clickWhenElementIsClickable("login_orgLogIn_btn", timeOutInSeconds);
                
			tempRetval = sc.waitforElementToDisplay("passwordReset_changePassword_lbl", timeOutInSeconds);
		    if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Account Setting-Standard User-Temp Password Flow", "Unable to login using reset password for user -"+orgUserName, 1);
				throw new Exception("Account Setting- Change Password Flow - Unable to login using reset password");
			}else{
						sc.STAF_ReportEvent("Pass", "Account Setting-Standard User-Temp Password Flow", "Login successfull using Temp password for Standard User "+orgUserName, 1);
			}
		     	
		    Globals.driver.findElement(By.xpath("//input[@id='OldPassword']")).sendKeys(passwd);;
				//sc.setValueJsChange("passwordReset_oldPassword_txt", newPassword)
			sc.setValueJsChange("passwordReset_newPassword_txt", "123qa123!");
			sc.setValueJsChange("passwordReset_confirmPassword_txt", "123qa123!");
			Thread.sleep(5000);
			//sc.clickWhenElementIsClickable("passwordReset_changePassword_btn",timeOutInSeconds);
			tempRetval=clickButtonUntilValidation("passwordReset_changePassword_btn","passwordReset_successMsg_span");
			sc.waitForPageLoad();	
			retval = sc.waitforElementToDisplay("passwordReset_successMsg_span", timeOutInSeconds);
			if(retval.equalsIgnoreCase(Globals.KEYWORD_FAIL)){
				sc.STAF_ReportEvent("Fail", "Account Setting-Standard User-Temp Password Flow", "Unable to change password.", 1);
				throw new Exception( "Account Setting-Standard User-Temp Password Flow Unable to change password");
			}else{
				 sc.STAF_ReportEvent("Pass", "Account Setting-Standard User-Temp Password Flow", "Temp Password changed successfully", 1);
			}
			
		}catch(Exception e){
			log.error("Exception occurred in Organization user Login | "+e.toString());
			throw e;
		}

		return retval;
	}
	
	/**************************************************************************************************
	 * Method to verify account setting export functionality
	 * @return String - Globals.KEYWORD_PASS if successful, Globals.KEYWORD_FAIL otherwise
	 * @author psave
	 * @throws Exception
	 ***************************************************************************************************/
	public static String verifyAccountSettingExport() throws Exception {
		Globals.Component = Thread.currentThread().getStackTrace()[1].getMethodName();
		APP_LOGGER.startFunction(Globals.Component);
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		int timeOutInSeconds =10;

		try{
			//click on new user link
			sc.waitForPageLoad();
			tempRetval = sc.waitTillElementDisplayed("AccountSetting_OrgUser_table",10);
			
			if(!(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS))){
					sc.STAF_ReportEvent("Fail", "Self Serve page-Organization New User", "Unable to  navigated to New Organization User page after click on cancel changes button", 1);
					log.error("Unable to  navigated to New Organization User page");
					return retval;
			}
			
			if (sc.waitforElementToDisplay("AccountSetting_OrgUser_table",60).equalsIgnoreCase(Globals.KEYWORD_PASS)){ 
				tempRetval = sc.waitTillElementDisplayed("AccountSetting_Export_link",10);
				sc.clickWhenElementIsClickable("AccountSetting_Export_link", 60);
				//tempRetval=clickButtonUntilValidation("AccountSetting_Export_link","clientUsers_reportheader_txt");
				tempRetval=sc.waitforElementToDisplay("clientUsers_reportheader_txt", 60);
				if(tempRetval.equalsIgnoreCase(Globals.KEYWORD_PASS)){
					sc.STAF_ReportEvent("Pass", "Self Serve page-Export Functionality", "Report Filters page has loaded", 1);
					ClientFacingApp.clientReport_verifyAccount();
					retval = Globals.KEYWORD_PASS;
				}
				else{
					sc.STAF_ReportEvent("Fail", "Self Serve page-Export Functionality", "Report Filters pages is Not loaded", 1);
					log.error("Unable to  navigated to Reports page");
				}		
						
							
			}
		}catch(Exception e){
			
			sc.STAF_ReportEvent("Fail", "Account Setting - Organization New User - ", "User is not displayed on org user table for account setting page", 1);
			throw e;	
				
		}
		return retval;
	}
	
	public static String verifyWelcomeEmails(String username, String fname, String lname, String inHouse) throws Exception {
		String retval=Globals.KEYWORD_FAIL;
		String tempRetval=Globals.KEYWORD_FAIL;
		long timeOutinSeconds = 60;
		int i=0;
		String emailFound=null;
		String emailBodyText =null;
		String newPassword = null;
		String newConfirmPwd=null;
		String urlPattern=null;
		HashMap<String, String> emailDetails=  new HashMap<String, String>();
		try{
			if (Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA2"))
            {
                urlPattern = "https://qapp2.verifiedvolunteers.com";
            }
            else if (Globals.Env_To_Execute_ON.equalsIgnoreCase("ENV_QA3")){
                urlPattern="https://qapp3.verifiedvolunteers.com";
            }
			
			
			String bodyPattern = "Here is your user ID for the Verified Volunteers application <"+urlPattern+"> : "+username;
			String subjectEmailPattern="Welcome to Verified Volunteers - Let's get you started";
			emailDetails = Email.fetchEmailFromOutlook(subjectEmailPattern,bodyPattern,60);
			emailFound = emailDetails.get("EmailFound");
		
			if (emailFound.equalsIgnoreCase("True")){
				sc.STAF_ReportEvent("Pass", "Account Setting-Welcome Email Verify", "Getting 1st Welcome email successfully ", 0);
			}else{
				sc.STAF_ReportEvent("Fail", "Account Setting-Welcome Email Verify", "Not Getting 1st Welcome email", 0);
			    return retval;
			}
	
	        
			HashMap<String, String> emailDetails1=  new HashMap<String, String>();
			String bodyPattern1 = "Here is your temporary password for the Verified Volunteers application <"+urlPattern+"> : ";
			String subjectEmailPattern2="Welcome to Verified Volunteers - "+fname.trim()+" "+lname.trim();
			emailDetails1 = Email.fetchEmailFromOutlook(subjectEmailPattern2,bodyPattern1,60);
			emailFound= emailDetails1.get("EmailFound");
	
			if (emailFound.equalsIgnoreCase("True")){
				emailBodyText = emailDetails1.get("EmailBody");
				emailBodyText = emailBodyText.replace("\"", "").trim();
				int startIndex = emailBodyText.indexOf("your temporary password for the Verified Volunteers application <"+urlPattern+"> :");
				int endindex = emailBodyText.indexOf("Again, if you have any");
				newPassword = emailBodyText.substring(startIndex, endindex).replace("your temporary password for the Verified Volunteers application <"+urlPattern+"> :","").trim();
				sc.STAF_ReportEvent("Pass", "Account Setting-Welcome Email Verify", "UserName - "+username+ " have received Password viva Welcome email successfully temp pass - "+ newPassword, 0);
				retval=Globals.KEYWORD_PASS;
			}else{
					sc.STAF_ReportEvent("Fail", "Account Setting-Welcome Email Verify", "user Welcome password email not received ", 0);
					retval = Globals.KEYWORD_FAIL;
					return retval;
			}
			if(inHouse.equalsIgnoreCase("true")){
				tempRetval=InhousePortal.staffUserLogout();
				orgUserLogin(username,newPassword);
				ClientFacingApp.orgUserLogOff()		;
				Globals.testSuiteXLS.setCellData_inTestData("OrgUserPwd", "123qa123!");
				
			}else{
				tempRetval=ClientFacingApp.orgUserLogOff();
				orgUserLogin(username,newPassword);
				ClientFacingApp.orgUserLogOff();
				username=Globals.testSuiteXLS.getCellData_fromTestData("OrgUserName");
				ClientFacingApp.orgUserLogin(username);
				navigateAccountSetting();
				navigateOrganizationUsers();
			}
			retval=Globals.KEYWORD_PASS;
	
	
	}catch(Exception e){
		
		sc.STAF_ReportEvent("Fail", "Account Setting - Organization New User - ", "User is not displayed on org user table for account setting page", 1);
		throw e;	
			
	}
	return "True";
	
	}

}