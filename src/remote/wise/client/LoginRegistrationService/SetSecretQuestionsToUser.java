
package remote.wise.client.LoginRegistrationService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetSecretQuestionsToUser complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetSecretQuestionsToUser">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}loginRegistrationReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetSecretQuestionsToUser", propOrder = {
    "reqObj"
})
public class SetSecretQuestionsToUser {

    protected LoginRegistrationReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link LoginRegistrationReqContract }
     *     
     */
    public LoginRegistrationReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoginRegistrationReqContract }
     *     
     */
    public void setReqObj(LoginRegistrationReqContract value) {
        this.reqObj = value;
    }

}
