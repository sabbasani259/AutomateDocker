
package remote.wise.client.MachineServiceDueOverDueReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetMachineServiceDueOverDueCount complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMachineServiceDueOverDueCount">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}machineServiceDueOverDueReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMachineServiceDueOverDueCount", propOrder = {
    "reqObj"
})
public class GetMachineServiceDueOverDueCount {

    protected MachineServiceDueOverDueReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link MachineServiceDueOverDueReqContract }
     *     
     */
    public MachineServiceDueOverDueReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link MachineServiceDueOverDueReqContract }
     *     
     */
    public void setReqObj(MachineServiceDueOverDueReqContract value) {
        this.reqObj = value;
    }

}
