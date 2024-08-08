
package remote.wise.client.MachineHourMeterTrendDataReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getMachineTrendDataForAllZones complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMachineTrendDataForAllZones">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}machineHourMeterTrendDataReportReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMachineTrendDataForAllZones", propOrder = {
    "reqObj"
})
public class GetMachineTrendDataForAllZones {

    protected MachineHourMeterTrendDataReportReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link MachineHourMeterTrendDataReportReqContract }
     *     
     */
    public MachineHourMeterTrendDataReportReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link MachineHourMeterTrendDataReportReqContract }
     *     
     */
    public void setReqObj(MachineHourMeterTrendDataReportReqContract value) {
        this.reqObj = value;
    }

}
