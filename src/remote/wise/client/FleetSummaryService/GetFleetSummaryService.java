
package remote.wise.client.FleetSummaryService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetFleetSummaryService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetFleetSummaryService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="request" type="{http://webservice.service.wise.remote/}fleetSummaryReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetFleetSummaryService", propOrder = {
    "request"
})
public class GetFleetSummaryService {

    protected FleetSummaryReqContract request;

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link FleetSummaryReqContract }
     *     
     */
    public FleetSummaryReqContract getRequest() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link FleetSummaryReqContract }
     *     
     */
    public void setRequest(FleetSummaryReqContract value) {
        this.request = value;
    }

}
