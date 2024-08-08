
package remote.wise.client.UserAuthenticationService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UserAuthentication complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserAuthentication">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqobj" type="{http://webservice.service.wise.remote/}userAuthenticationReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserAuthentication", propOrder = {
    "reqobj"
})
public class UserAuthentication {

    protected UserAuthenticationReqContract reqobj;

    /**
     * Gets the value of the reqobj property.
     * 
     * @return
     *     possible object is
     *     {@link UserAuthenticationReqContract }
     *     
     */
    public UserAuthenticationReqContract getReqobj() {
        return reqobj;
    }

    /**
     * Sets the value of the reqobj property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserAuthenticationReqContract }
     *     
     */
    public void setReqobj(UserAuthenticationReqContract value) {
        this.reqobj = value;
    }

}
