/**
 * 
 */
package remote.wise.businessentity;

/**
 * @author kprabhu5
 *
 */
public class SecretQuestionEntity extends BaseBusinessEntity {

	
	private int question_Id;
	private String question;
	
	/**
	 * @return the question_Id
	 */
	public int getQuestion_Id() {
		return question_Id;
	}
	/**
	 * @param question_Id the question_Id to set
	 */
	public void setQuestion_Id(int question_Id) {
		this.question_Id = question_Id;
	}
	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}
	/**
	 * @param question the question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}
}
