/**
 * 
 */
package remote.wise.service.implementation;

import remote.wise.businessobject.LoginRegistrationBO;
import remote.wise.exception.CustomFault;
import remote.wise.service.datacontract.ForgotLoginIDReqContract;
import remote.wise.service.datacontract.ForgotLoginIDRespContract;

/**
 * @author kprabhu5
 *
 */

public class ForgotLoginIDImpl {

	String message;
	String loginId;
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
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	/**
	 * @return the primaryEmailId
	 */
	public String getPrimaryEmailId() {
		return primaryEmailId;
	}

	/**
	 * @param primaryEmailId the primaryEmailId to set
	 */
	public void setPrimaryEmailId(String primaryEmailId) {
		this.primaryEmailId = primaryEmailId;
	}

	String primaryEmailId;
	public String authenicateLoginIDOrMobileNo(ForgotLoginIDReqContract reqObj) throws CustomFault {
		
		if((reqObj.getEmailID() == null || reqObj.getEmailID().equals("")) && (reqObj.getMobileNumber() == null || reqObj.getMobileNumber().equals(""))){
    		throw new CustomFault("Provide either email id or mobile no.");
    		
    	}
		String message=null;
		LoginRegistrationBO regBo=new LoginRegistrationBO();
    	 message=regBo.authenicateLoginIDOrMobileNo(reqObj);
		return message;
	}
	public ForgotLoginIDRespContract getForgottenLoginId(ForgotLoginIDReqContract reqObj) throws CustomFault {
    	
    	if((reqObj.getEmailID() == null || reqObj.getEmailID().equals("")) && (reqObj.getMobileNumber() == null || reqObj.getMobileNumber().equals(""))){
    		throw new CustomFault("Provide either email id or mobile no.");
    		
    	}
    	if(reqObj.getQuestionId1() == 0){
    		throw new CustomFault("Provide Question ID 1");
    		
    	}
    	if(reqObj.getQuestionId2() == 0){
    		throw new CustomFault("Provide Question ID 2");
    		
    	}
    	if(reqObj.getAnswer1() == null || reqObj.getAnswer1().equals("")){
    		throw new CustomFault("Provide Answer 1");
    		
    	}
    	if(reqObj.getAnswer2() == null || reqObj.getAnswer2().equals("")){
    		throw new CustomFault("Provide Answer 2");
    		
    	}
    	LoginRegistrationBO regBo=new LoginRegistrationBO();
    	ForgotLoginIDImpl userDetails=regBo.getForgottenLoginId(reqObj);
    	
    	ForgotLoginIDRespContract response=new ForgotLoginIDRespContract();
    	
    	response.setMessage(userDetails.getMessage());
    	response.setLoginID(userDetails.getLoginId());
    	response.setPrimaryEmailID(userDetails.getPrimaryEmailId());
    	
    	return response;
    }
}
