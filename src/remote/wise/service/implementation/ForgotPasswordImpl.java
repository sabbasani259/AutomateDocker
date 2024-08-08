/*
 * JCB6622 : 20240805 : Dhiraj Kumar : Email Flooding security fix
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.Statement;
import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.LoginRegistrationBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.ForgotPasswordReqContract;
import remote.wise.service.datacontract.ForgotPasswordRespContract;
//import remote.wise.util.WiseLogger;
import remote.wise.util.CommonUtil;
import remote.wise.util.ConnectMySQL;

public class ForgotPasswordImpl {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("ForgotPasswordImpl:","businessError");
	//static Logger businessError = Logger.getLogger("businessErrorLogger");
	
	String password;
	String primaryEmailID;
	String message;

	 /**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the primaryEmailID
	 */
	public String getPrimaryEmailID() {
		return primaryEmailID;
	}

	/**
	 * @param primaryEmailID the primaryEmailID to set
	 */
	public void setPrimaryEmailID(String primaryEmailID) {
		this.primaryEmailID = primaryEmailID;
	}

	 /**
     * method to get forgotten password for the user 
     */
    
    public ForgotPasswordRespContract getForgottenPassword(ForgotPasswordReqContract request) throws CustomFault{
    	Logger bLogger = BusinessErrorLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
		
    	if(request.getLoginId() == null || request.getLoginId().equals("") ){
    	    bLogger.error("The login id is not provided !");
    	    throw new CustomFault("Please provide the login id !");
    	}
    	
    	LoginRegistrationBO regBo=new LoginRegistrationBO();
    	ForgotPasswordRespContract response=new ForgotPasswordRespContract();
    	//JCB6622.sn
    	//Validate count of request for the user.
    	int count = regBo.getCountForgotPasswordForUser(request.getLoginId());
    	iLogger.info("Forgot password count : "+ request.getLoginId() + " : "+ count);//JCB6622.n
    	if(count>=5){
    	    String message1="You have reached maximum limit of 5 to reset password for today! Please try again tomorrow.";
    	    bLogger.error(message1);
    	    response.setMessage(message1);
    	}//JCB6622.en
    	else {
    	    ForgotPasswordImpl impl=regBo.getForgottenPassword(request.getLoginId());
    	    //JCB6622.sn
    	    //Update Password reset count in contact table for the user
    	    count++;
    	    regBo.incrementCountForgotPasswordForUser(request.getLoginId(), count);
    	    //JCB6622.en
    	    response.setPassword(impl.getPassword());
    	    response.setMessage(impl.getMessage());
    	    response.setPrimaryEmailID(impl.getPrimaryEmailID());
    	}
    	
    	//DF20180731 - KO369761 - Deleting existing token ids for the user.
    	new CommonUtil().deleteUserTokenIds(request.getLoginId());
    	
    	return response;
    	
    }
    //JCB6622.sn
    public String updateForgotPassCount() {
	Logger fLogger = FatalLoggerClass.logger;

	String status = "FAILURE";
	String sql = "UPDATE contact SET reset_pass_count=0 WHERE status=1 AND reset_pass_count>0";
	ConnectMySQL factory = new ConnectMySQL();
	try (Connection conn = factory.getConnection();
		Statement st = conn.createStatement()){
	    st.execute(sql);
	    status="SUCCESS";
	}catch (Exception e) {
	    e.printStackTrace();
	    fLogger.fatal("Exception occured : ", e.getMessage());
	}
	return status;
    }//JCB6622.en
}
