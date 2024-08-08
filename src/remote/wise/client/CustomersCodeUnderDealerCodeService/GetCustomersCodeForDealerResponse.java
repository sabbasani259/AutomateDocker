
package remote.wise.client.CustomersCodeUnderDealerCodeService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetCustomersCodeForDealerResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCustomersCodeForDealerResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://webservice.service.wise.remote/}customerCodeForDealerCodeRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCustomersCodeForDealerResponse", propOrder = {
    "_return"
})
public class GetCustomersCodeForDealerResponse {

    @XmlElement(name = "return")
    protected CustomerCodeForDealerCodeRespContract _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerCodeForDealerCodeRespContract }
     *     
     */
    public CustomerCodeForDealerCodeRespContract getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerCodeForDealerCodeRespContract }
     *     
     */
    public void setReturn(CustomerCodeForDealerCodeRespContract value) {
        this._return = value;
    }

}
