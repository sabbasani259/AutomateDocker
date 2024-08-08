/**
 * 
 */
package remote.wise.service.datacontract;

/**
 * @author kprabhu5
 *
 */
public class ForgotLoginIDReqContract {
	
	String emailID;
	String mobileNumber;
	
	int questionId1;
	String answer1;
	
	int questionId2;
	String answer2;
	/**
	 * @return the emailID
	 */
	public String getEmailID() {
		return emailID;
	}
	/**
	 * @param emailID the emailID to set
	 */
	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}
	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}
	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	/**
	 * @return the questionId1
	 */
	public int getQuestionId1() {
		return questionId1;
	}
	/**
	 * @param questionId1 the questionId1 to set
	 */
	public void setQuestionId1(int questionId1) {
		this.questionId1 = questionId1;
	}
	/**
	 * @return the answer1
	 */
	public String getAnswer1() {
		return answer1;
	}
	/**
	 * @param answer1 the answer1 to set
	 */
	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}
	/**
	 * @return the questionId2
	 */
	public int getQuestionId2() {
		return questionId2;
	}
	/**
	 * @param questionId2 the questionId2 to set
	 */
	public void setQuestionId2(int questionId2) {
		this.questionId2 = questionId2;
	}
	/**
	 * @return the answer2
	 */
	public String getAnswer2() {
		return answer2;
	}
	/**
	 * @param answer2 the answer2 to set
	 */
	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	} 
	

}
