
package remote.wise.client.MachineActivityReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetMachineActivityReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMachineActivityReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReqObj" type="{http://webservice.service.wise.remote/}machineActivityReportReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMachineActivityReport", propOrder = {
    "reqObj"
})
public class GetMachineActivityReport {

    @XmlElement(name = "ReqObj")
    protected MachineActivityReportReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link MachineActivityReportReqContract }
     *     
     */
    public MachineActivityReportReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link MachineActivityReportReqContract }
     *     
     */
    public void setReqObj(MachineActivityReportReqContract value) {
        this.reqObj = value;
    }

}
