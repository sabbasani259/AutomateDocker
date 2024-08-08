
package remote.wise.client.AlertThresholdSettingsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetAlertThreshold complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetAlertThreshold">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObject" type="{http://webservice.service.wise.remote/}alertThresholdReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetAlertThreshold", propOrder = {
    "reqObject"
})
public class GetAlertThreshold {

    protected AlertThresholdReqContract reqObject;

    /**
     * Gets the value of the reqObject property.
     * 
     * @return
     *     possible object is
     *     {@link AlertThresholdReqContract }
     *     
     */
    public AlertThresholdReqContract getReqObject() {
        return reqObject;
    }

    /**
     * Sets the value of the reqObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link AlertThresholdReqContract }
     *     
     */
    public void setReqObject(AlertThresholdReqContract value) {
        this.reqObject = value;
    }

}
