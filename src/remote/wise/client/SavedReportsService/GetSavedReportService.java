
package remote.wise.client.SavedReportsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetSavedReportService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetSavedReportService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}savedReportReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetSavedReportService", propOrder = {
    "reqObj"
})
public class GetSavedReportService {

    protected SavedReportReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link SavedReportReqContract }
     *     
     */
    public SavedReportReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link SavedReportReqContract }
     *     
     */
    public void setReqObj(SavedReportReqContract value) {
        this.reqObj = value;
    }

}
