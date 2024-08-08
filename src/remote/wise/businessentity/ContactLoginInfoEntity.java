/**
 * 
 */
package remote.wise.businessentity;

import java.io.Serializable;

/**
 * @author kprabhu5
 *
 */
public class ContactLoginInfoEntity extends BaseBusinessEntity implements Serializable{

	
	private ContactEntity contactId;
	private SecretQuestionEntity questionId;
	private String answer;
	
	public ContactEntity getContactId() {
		return contactId;
	}
	public void setContactId(ContactEntity contactId) {
		this.contactId = contactId;
	}
	public SecretQuestionEntity getQuestionId() {
		return questionId;
	}
	public void setQuestionId(SecretQuestionEntity questionId) {
		this.questionId = questionId;
	}

	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	
	
	
}