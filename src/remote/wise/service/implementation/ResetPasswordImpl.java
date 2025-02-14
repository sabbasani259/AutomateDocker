package remote.wise.service.implementation;

////import org.apache.log4j.Logger;

import java.util.HashMap;
import org.apache.logging.log4j.Logger;
import remote.wise.businessobject.LoginRegistrationBO;
import remote.wise.exception.CustomFault;
import remote.wise.handler.ContactDetailsProducerThread;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ResetPasswordReqContract;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;


public class ResetPasswordImpl {
	String loginID;
	String newPassword;
	
	//static Logger businessError = Logger.getLogger("businessErrorLogger");
	//Defect Id 1337 - Logger changes
		//public static WiseLogger businessError = WiseLogger.getLogger("ResetPasswordImpl:","businessError");
	/**
	 * @return the loginID
	 */
	public String getLoginID() {
		return loginID;
	}
	/**
	 * @param loginID the loginID to set
	 */
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	/**
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}
	/**
	 * @param newPassword the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	/**
	 * method to reset the password
	 * @param req
	 * @return String
	 * @throws CustomFault
	 */
	public String resetPassword(ResetPasswordReqContract req) throws CustomFault {
		Logger bLogger = BusinessErrorLoggerClass.logger;
    	if(req.getLoginID() == null || req.getLoginID().equals("")){
    		bLogger.error("Login Id is not provided.");
    		throw new CustomFault("Provide login ID");    		
    	}
    	if(req.getNewPassword()== null  || req.getNewPassword().equals("") ){
    		bLogger.error("New password is not provided.");
    		throw new CustomFault("Provide new password");    		
    	}
    	
    	//************************* DF20180730 - Rajani Nagaraju - Security Audit IssueID: JCBX-061-1-14 - START
    	//---------------- 1. Password Policy - Passwords should be atleast 8 characters long. Should have atleast 1 uppercase letter,1 lowercase letter,1 digit and 1 special character
    	Logger iLogger= InfoLoggerClass.logger;
    	String password = req.getNewPassword();
    	String loginID = req.getLoginID();
    	
    	boolean validPassword = checkPasswordPolicy(password);
    	if(!validPassword)
    	{
    		bLogger.error("Passwords should be atleast 12 characters long. Should have any 3 of the below condition: 1 uppercase letter,1 lowercase letter,1 digit and 1 special character among ! @ # $ & * ");
    		throw new CustomFault("Passwords should be atleast 12 characters long. Should have any 3 of the below condition: 1 uppercase letter,1 lowercase letter,1 digit and 1 special character among ! @ # $ & * "); 
    	}
    	
    	//-------------- 2.Password should not contain string "JCB" nor loginId nor userName
    	if(password.toUpperCase().contains("JCB"))
    	{
    		bLogger.error("Password cannot have the String JCB as it is predictable and hence vulnerable to attacks");
    		throw new CustomFault("Password cannot have the String JCB as it is predictable and hence vulnerable to attacks");
    	}
    	
    	//DF20180906 - Rajani Nagaraju - Password Policy Revision
    	/*if(password.equalsIgnoreCase(loginID))
    	{
    		bLogger.error("Password cannot be same as LoginID as it is predictable and hence vulnerable to attacks");
    		throw new CustomFault("Password cannot be same as LoginID as it is predictable and hence vulnerable to attacks");
    	}
    	
    	String charInLoginID = loginID.replaceAll("[^a-zA-Z]+", "");
    	if(password.toUpperCase().contains(charInLoginID.toUpperCase()))
    	{
    		bLogger.error("Password cannot have LoginID String as it is predictable and hence vulnerable to attacks");
    		throw new CustomFault("Password cannot have LoginID String as it is predictable and hence vulnerable to attacks");
    	}
    	
    	HashMap<String,String> contactDetails  = new LoginRegistrationBO().getUserName(loginID);
    	for(Map.Entry<String,String> contact:  contactDetails.entrySet())
    	{
    		if(contact.getValue()!=null)
    		{
    			if(password.toUpperCase().contains(contact.getValue().toUpperCase()))
    			{
    				bLogger.error("Password cannot contain user name as it is predictable and hence vulnerable to attacks");
    	    		throw new CustomFault("Password cannot contain user name as it is predictable and hence vulnerable to attacks");
    			}
    		}
    	}*/
    	
    	//------------------ 3. New password should not be same as last 5 password set by the user
    	boolean isDuplicatePwd = new LoginRegistrationBO().isDuplicatePassword(loginID, password);
    	if(isDuplicatePwd)
    	{
    		bLogger.error("Password cannot be same as last 5 passwords");
    		throw new CustomFault("Password cannot be same as last 5 passwords");
    	}
    	
    	//*************************  DF20180730 - Rajani Nagaraju - Security Audit IssueID: JCBX-061-1-14 - END
    	
    	LoginRegistrationBO regBo=new LoginRegistrationBO();
    	String message=regBo.resetPassword(req.getLoginID(),req.getNewPassword());
    	
    	//*************************  DF20180730 - Rajani Nagaraju - Security Audit IssueID: JCBX-061-1-14 - START
    	if(message!=null && message.equalsIgnoreCase("Successfully reset password"))
    	{
    		//Update the same in password history table
    		String historyUpdateStatus =  new LoginRegistrationBO().updatePwdHistory(loginID, password);
    		iLogger.info("ResetPasswordImpl:Updated Password Hitory for loginID:"+loginID+"; Status:"+historyUpdateStatus);
    	
    		 //============= Send this data to kafka topic
        	HashMap<String, String> payloadMap = new HashMap<>();
        	payloadMap.put("Contact_ID", req.getLoginID());
        	payloadMap.put("Password", password);

        	new ContactDetailsProducerThread(payloadMap, req.getLoginID()+"_ResetPwd");
        	//==============
    	}
    	//*************************  DF20180730 - Rajani Nagaraju - Security Audit IssueID: JCBX-061-1-14 - END
    	
    	//DF20180731 - KO369761 - Deleting existing user token ids.
    	new CommonUtil().deleteUserTokenIds(loginID);
    	
    	return message;
    }

	//DF20180730 - Rajani Nagaraju - Security Audit IssueID: JCBX-061-1-14 - START
	public boolean checkPasswordPolicy(String password)
	{
		boolean validPassword=true;
			
		//1.Passwords should be at least twelve characters long and maximum 45 characters
	   	if(password.length()< 12 || password.length() > 45)
	   		validPassword=false;
	    	
	   	//2.Password should have at least one UpperCase letter, One LowerCase letter, One Digit and One Special Character
	   /*	String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*(_|[^\\w])).+$";
	   	if(!(password.matches(regex)) )
	   		validPassword=false;*/
	    	
	   	/*	 ^        			// the start of the string
				(?=.*[a-z])        // use positive look ahead to see if at least one lower case letter exists
				(?=.*[A-Z])        // use positive look ahead to see if at least one upper case letter exists
				(?=.*\d)           // use positive look ahead to see if at least one digit exists
				(?=.*[_\W])        // use positive look ahead to see if at least one underscore or non-word character exists
				.+                 // gobble up the entire string
				$                  // the end of the string
		*/
	    	
	   	//DF20180906 - Rajani Nagaraju - Password Policy Revision
	   	
	   	int invalidCounter=0;
	  //a.Password should have at least one UpperCase letter
    	String regex1= "^(?=.*[A-Z]).+$";
    	if(!(password.matches(regex1)) )
    		invalidCounter=invalidCounter+1;
    	
    	//b.Password should have at least one LowerCase letter
    	String regex2 = "^(?=.*[a-z]).+$";
    	if(!(password.matches(regex2)) )
    		invalidCounter=invalidCounter+1;
    	
    	//c.Password should have at least one digit
    	String regex3 = ".*\\d+.*";
    	if(!(password.matches(regex3)) )
    		invalidCounter=invalidCounter+1;
    	
    	//d.Password should have at least one of these special character - ( ! @ # $ & * )
    	if( ! (password.contains("@") || password.contains("!") || password.contains("#") || password.contains("$") || password.contains("&")  || password.contains("*")) )
    		invalidCounter=invalidCounter+1;
    	
    	if(invalidCounter>1)
    		validPassword=false;
	    	
		return validPassword;
	 }
	//DF20180730 - Rajani Nagaraju - Security Audit IssueID: JCBX-061-1-14 - END
}
