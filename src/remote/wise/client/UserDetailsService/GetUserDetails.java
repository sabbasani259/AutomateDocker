
package remote.wise.client.UserDetailsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetUserDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetUserDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="userReqContract" type="{http://webservice.service.wise.remote/}userDetailsReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetUserDetails", propOrder = {
    "userReqContract"
})
public class GetUserDetails {

    protected UserDetailsReqContract userReqContract;

    /**
     * Gets the value of the userReqContract property.
     * 
     * @return
     *     possible object is
     *     {@link UserDetailsReqContract }
     *     
     */
    public UserDetailsReqContract getUserReqContract() {
        return userReqContract;
    }

    /**
     * Sets the value of the userReqContract property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserDetailsReqContract }
     *     
     */
    public void setUserReqContract(UserDetailsReqContract value) {
        this.userReqContract = value;
    }

}
