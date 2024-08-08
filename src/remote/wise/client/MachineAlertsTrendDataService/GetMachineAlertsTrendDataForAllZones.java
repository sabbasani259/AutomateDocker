
package remote.wise.client.MachineAlertsTrendDataService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getMachineAlertsTrendDataForAllZones complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMachineAlertsTrendDataForAllZones">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}machineAlertsTrendDataReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMachineAlertsTrendDataForAllZones", propOrder = {
    "reqObj"
})
public class GetMachineAlertsTrendDataForAllZones {

    protected MachineAlertsTrendDataReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link MachineAlertsTrendDataReqContract }
     *     
     */
    public MachineAlertsTrendDataReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link MachineAlertsTrendDataReqContract }
     *     
     */
    public void setReqObj(MachineAlertsTrendDataReqContract value) {
        this.reqObj = value;
    }

}
