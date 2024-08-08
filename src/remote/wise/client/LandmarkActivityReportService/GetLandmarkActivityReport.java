
package remote.wise.client.LandmarkActivityReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetLandmarkActivityReport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetLandmarkActivityReport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReqObj" type="{http://webservice.service.wise.remote/}landmarkActivityReportReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetLandmarkActivityReport", propOrder = {
    "reqObj"
})
public class GetLandmarkActivityReport {

    @XmlElement(name = "ReqObj")
    protected LandmarkActivityReportReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link LandmarkActivityReportReqContract }
     *     
     */
    public LandmarkActivityReportReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link LandmarkActivityReportReqContract }
     *     
     */
    public void setReqObj(LandmarkActivityReportReqContract value) {
        this.reqObj = value;
    }

}
