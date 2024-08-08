
package remote.wise.client.ServiceDueOverDueService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetServiceDueOverDueDetailsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetServiceDueOverDueDetailsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://webservice.service.wise.remote/}serviceDueOverDueRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetServiceDueOverDueDetailsResponse", propOrder = {
    "_return"
})
public class GetServiceDueOverDueDetailsResponse {

    @XmlElement(name = "return")
    protected ServiceDueOverDueRespContract _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceDueOverDueRespContract }
     *     
     */
    public ServiceDueOverDueRespContract getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceDueOverDueRespContract }
     *     
     */
    public void setReturn(ServiceDueOverDueRespContract value) {
        this._return = value;
    }

}
