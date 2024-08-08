
package remote.wise.client.LoginRegistrationService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for loginRegistrationReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="loginRegistrationReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="answer1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="answer2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DOB" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nativeState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="questionId1" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="questionId2" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loginRegistrationReqContract", propOrder = {
    "answer1",
    "answer2",
    "dob",
    "loginId",
    "nativeState",
    "questionId1",
    "questionId2"
})
public class LoginRegistrationReqContract {

    protected String answer1;
    protected String answer2;
    @XmlElement(name = "DOB")
    protected String dob;
    protected String loginId;
    protected String nativeState;
    protected int questionId1;
    protected int questionId2;

    /**
     * Gets the value of the answer1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnswer1() {
        return answer1;
    }

    /**
     * Sets the value of the answer1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnswer1(String value) {
        this.answer1 = value;
    }

    /**
     * Gets the value of the answer2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnswer2() {
        return answer2;
    }

    /**
     * Sets the value of the answer2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnswer2(String value) {
        this.answer2 = value;
    }

    /**
     * Gets the value of the dob property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOB() {
        return dob;
    }

    /**
     * Sets the value of the dob property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOB(String value) {
        this.dob = value;
    }

    /**
     * Gets the value of the loginId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * Sets the value of the loginId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoginId(String value) {
        this.loginId = value;
    }

    /**
     * Gets the value of the nativeState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNativeState() {
        return nativeState;
    }

    /**
     * Sets the value of the nativeState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNativeState(String value) {
        this.nativeState = value;
    }

    /**
     * Gets the value of the questionId1 property.
     * 
     */
    public int getQuestionId1() {
        return questionId1;
    }

    /**
     * Sets the value of the questionId1 property.
     * 
     */
    public void setQuestionId1(int value) {
        this.questionId1 = value;
    }

    /**
     * Gets the value of the questionId2 property.
     * 
     */
    public int getQuestionId2() {
        return questionId2;
    }

    /**
     * Sets the value of the questionId2 property.
     * 
     */
    public void setQuestionId2(int value) {
        this.questionId2 = value;
    }

}
