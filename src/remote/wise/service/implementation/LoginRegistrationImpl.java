package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
////import org.apache.log4j.Logger;
import remote.wise.businessobject.LoginRegistrationBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.LoginRegistrationRespContract;
import remote.wise.service.datacontract.SecretQuestionsRespContract;
//import remote.wise.util.WiseLogger;

public class LoginRegistrationImpl {
	
	
	
	
	//DefectId:1337 - Suprava - 20130923 - Log4j Changes - Using static logger object all throughout the application

	//public static WiseLogger businessError = WiseLogger.getLogger("LoginRegistrationImpl:","businessError");

	/*static Logger fatalError = Logger.getLogger("fatalErrorLogger");
	static Logger businessError = Logger.getLogger("businessErrorLogger");
	static Logger infoLogger = Logger.getLogger("infoLogger");*/
	
	int QID;
	String question;
	String loginID;
	String message;	
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getLoginID() {
		return loginID;
	}
	
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public int getQID() {
		return QID;
	}

	public void setQID(int qID) {
		QID = qID;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
	
	/**
	 * method to get secret questions  
	 * @return List<SecretQuestionsRespContract>
	 */
	public List<SecretQuestionsRespContract> getSecretQuestions() 
	{
	  LoginRegistrationBO loginDetails = new LoginRegistrationBO();
	  List<LoginRegistrationImpl> questionsList = loginDetails.getSecretQs();
	  Iterator<LoginRegistrationImpl> iter=questionsList.iterator();
	  List<SecretQuestionsRespContract> secretQsList=new ArrayList<SecretQuestionsRespContract>();
	  SecretQuestionsRespContract resp=null;
	  LoginRegistrationImpl question=null;
	  while(iter.hasNext()){
		  question = iter.next();
		  resp = new SecretQuestionsRespContract();
		  resp.setQID(question.getQID());
		  resp.setQuestion(question.getQuestion());
		  secretQsList.add(resp);  
	  }
	  return secretQsList;
	}
	/**
	 * method to set secret q's value and new password for new user first login
	 * @return String
	 * 
	 */
	
    public String setSecretQuestions(LoginRegistrationRespContract response) throws CustomFault{
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		
    	if(response.getLoginId()==null || response.getLoginId().equals("")){
    		bLogger.error("Provide Login ID  ");
    		throw new CustomFault("Provide Login ID  ");
    	}
    	
    	if(response.getQuestionId1()==0){
    		bLogger.error("Provide Question ID 1");
    		throw new CustomFault("Provide Question ID 1");
    	}
    	if(response.getAnswer1()==null || response.getAnswer1().equals("")){
    		bLogger.error("Provide Answer 1");
    		throw new CustomFault("Provide Answer 1");
    	}
    	if(response.getQuestionId2()==0){
    		bLogger.error("Provide Question ID 1");
    		throw new CustomFault("Provide Question ID 1");
    	}
    	if(response.getAnswer2()==null || response.getAnswer2().equals("")){
    		bLogger.error("Provide Answer 2");
    		throw new CustomFault("Provide Answer 2");
    	}
    	
    	if(response.getQuestionId1() == response.getQuestionId2()){
    		bLogger.error("Please select two different questions !");
    		throw new CustomFault("Please select two different questions !");
    	}
    	if(response.getDOB()==null || response.getDOB().equals("") ){
    		bLogger.error("Provide DOB");
    		throw new CustomFault("Provide DOB");
    	}
    	/*if(response.getNativeState()==null || response.getNativeState().equals("")){
    		businessError.error("Provide Native State");
    		throw new CustomFault("Provide Native State");
    	}*/
    	
    	String message=null;
    	LoginRegistrationBO regBo=new LoginRegistrationBO();
    	message = regBo.setSecretQuestions(response.getLoginId(),response.getQuestionId1(),response.getAnswer1(),response.getQuestionId2(),
    			response.getAnswer2(),response.getDOB(),response.getNativeState());
    	
    	return message;
    	
    }
    
    /**
     * method to get secret question set by the user
     * @param contactID
     * @return List<SecretQuestionsRespContract>
     * @throws CustomFault
     */
    public List<SecretQuestionsRespContract> getSecretQuestionsForUser(String contactID) throws CustomFault{
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		
	    	if(contactID == null || contactID.equals("")){
	    		bLogger.error("Provide contact ID");    
	    		throw new CustomFault("Provide contact ID");    		
	    	}

    	  LoginRegistrationBO regBo=new LoginRegistrationBO();
	    	
	      List<LoginRegistrationImpl> questionsList = regBo.getSecretQsForUser(contactID);
	  	  Iterator<LoginRegistrationImpl> iter=questionsList.iterator();
	  	  List<SecretQuestionsRespContract> secretQsList=new ArrayList<SecretQuestionsRespContract>();
	  	  SecretQuestionsRespContract resp=null;
	  	  LoginRegistrationImpl question=null;
	  	  while(iter.hasNext()){
	  		  question = iter.next();
	  		  resp = new SecretQuestionsRespContract();
	  		  resp.setQID(question.getQID());
	  		  resp.setQuestion(question.getQuestion());
	  		  secretQsList.add(resp);  
	  	  }
  	  return secretQsList;
    }   
}
