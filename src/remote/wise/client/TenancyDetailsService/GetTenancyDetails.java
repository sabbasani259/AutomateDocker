
package remote.wise.client.TenancyDetailsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetTenancyDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetTenancyDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}tenancyDetailsReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetTenancyDetails", propOrder = {
    "reqObj"
})
public class GetTenancyDetails {

    protected TenancyDetailsReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link TenancyDetailsReqContract }
     *     
     */
    public TenancyDetailsReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link TenancyDetailsReqContract }
     *     
     */
    public void setReqObj(TenancyDetailsReqContract value) {
        this.reqObj = value;
    }

}
