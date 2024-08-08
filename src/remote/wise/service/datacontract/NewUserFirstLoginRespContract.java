/**
 * 
 */
package remote.wise.service.datacontract;

/**
 * @author kprabhu5
 *
 */
public class NewUserFirstLoginRespContract {
	
String contactId;
	
	int questionId1;
	int questionId2;
	
	String question1;
	String question2;
	
	String answer1;
	String answer2;
	
	String DOB;
	String nativeState;
	
	String newPassword;

	/**
	 * @return the contactId
	 */
	public String getContactId() {
		return contactId;
	}
	/**
	 * @param contactId the contactId to set
	 */
	public void setContactId(String contactId) {
		this.contactId = contactId;
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
	/**
	 * @return the dOB
	 */
	public String getDOB() {
		return DOB;
	}
	/**
	 * @param dOB the dOB to set
	 */
	public void setDOB(String dOB) {
		DOB = dOB;
	}
	/**
	 * @return the nativeState
	 */
	public String getNativeState() {
		return nativeState;
	}
	/**
	 * @param nativeState the nativeState to set
	 */
	public void setNativeState(String nativeState) {
		this.nativeState = nativeState;
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
	 * @return the question1
	 */
	public String getQuestion1() {
		return question1;
	}
	/**
	 * @param question1 the question1 to set
	 */
	public void setQuestion1(String question1) {
		this.question1 = question1;
	}
	/**
	 * @return the question2
	 */
	public String getQuestion2() {
		return question2;
	}
	/**
	 * @param question2 the question2 to set
	 */
	public void setQuestion2(String question2) {
		this.question2 = question2;
	}


}
