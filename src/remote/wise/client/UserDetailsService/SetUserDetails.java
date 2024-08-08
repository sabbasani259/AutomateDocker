
package remote.wise.client.UserDetailsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetUserDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetUserDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="setUserDetails" type="{http://webservice.service.wise.remote/}userDetailsRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetUserDetails", propOrder = {
    "setUserDetails"
})
public class SetUserDetails {

    protected UserDetailsRespContract setUserDetails;

    /**
     * Gets the value of the setUserDetails property.
     * 
     * @return
     *     possible object is
     *     {@link UserDetailsRespContract }
     *     
     */
    public UserDetailsRespContract getSetUserDetails() {
        return setUserDetails;
    }

    /**
     * Sets the value of the setUserDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserDetailsRespContract }
     *     
     */
    public void setSetUserDetails(UserDetailsRespContract value) {
        this.setUserDetails = value;
    }

}
