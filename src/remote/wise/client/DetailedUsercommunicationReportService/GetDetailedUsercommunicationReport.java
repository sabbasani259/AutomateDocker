
package remote.wise.client.DetailedUsercommunicationReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetDetailedUsercommunicationReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetDetailedUsercommunicationReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}detailedUsercommunicationReportInputContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetDetailedUsercommunicationReport", propOrder = {
    "reqObj"
})
public class GetDetailedUsercommunicationReport {

    protected DetailedUsercommunicationReportInputContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link DetailedUsercommunicationReportInputContract }
     *     
     */
    public DetailedUsercommunicationReportInputContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link DetailedUsercommunicationReportInputContract }
     *     
     */
    public void setReqObj(DetailedUsercommunicationReportInputContract value) {
        this.reqObj = value;
    }

}
