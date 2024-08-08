
package remote.wise.client.MachineRPMBandTrendDataService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getMachineRPMBandTrendDataForAllZones complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMachineRPMBandTrendDataForAllZones">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}machineRPMBandDataReportReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMachineRPMBandTrendDataForAllZones", propOrder = {
    "reqObj"
})
public class GetMachineRPMBandTrendDataForAllZones {

    protected MachineRPMBandDataReportReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link MachineRPMBandDataReportReqContract }
     *     
     */
    public MachineRPMBandDataReportReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link MachineRPMBandDataReportReqContract }
     *     
     */
    public void setReqObj(MachineRPMBandDataReportReqContract value) {
        this.reqObj = value;
    }

}
