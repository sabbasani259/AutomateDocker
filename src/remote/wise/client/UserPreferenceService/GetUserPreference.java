
package remote.wise.client.UserPreferenceService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetUserPreference complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetUserPreference">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UserPreferenceReqContract" type="{http://webservice.service.wise.remote/}userPreferenceReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetUserPreference", propOrder = {
    "userPreferenceReqContract"
})
public class GetUserPreference {

    @XmlElement(name = "UserPreferenceReqContract")
    protected UserPreferenceReqContract userPreferenceReqContract;

    /**
     * Gets the value of the userPreferenceReqContract property.
     * 
     * @return
     *     possible object is
     *     {@link UserPreferenceReqContract }
     *     
     */
    public UserPreferenceReqContract getUserPreferenceReqContract() {
        return userPreferenceReqContract;
    }

    /**
     * Sets the value of the userPreferenceReqContract property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserPreferenceReqContract }
     *     
     */
    public void setUserPreferenceReqContract(UserPreferenceReqContract value) {
        this.userPreferenceReqContract = value;
    }

}
