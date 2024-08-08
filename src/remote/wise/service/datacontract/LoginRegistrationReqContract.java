package remote.wise.service.datacontract;

public class LoginRegistrationReqContract {
	
	String loginId;
	
	int questionId1;
	int questionId2;
	
	String answer1;
	String answer2;
	
	String DOB;
	String nativeState;
	
	
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
}
