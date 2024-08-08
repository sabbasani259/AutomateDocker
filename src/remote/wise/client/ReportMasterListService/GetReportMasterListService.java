
package remote.wise.client.ReportMasterListService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetReportMasterListService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetReportMasterListService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqContract" type="{http://webservice.service.wise.remote/}reportMasterListReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetReportMasterListService", propOrder = {
    "reqContract"
})
public class GetReportMasterListService {

    protected ReportMasterListReqContract reqContract;

    /**
     * Gets the value of the reqContract property.
     * 
     * @return
     *     possible object is
     *     {@link ReportMasterListReqContract }
     *     
     */
    public ReportMasterListReqContract getReqContract() {
        return reqContract;
    }

    /**
     * Sets the value of the reqContract property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportMasterListReqContract }
     *     
     */
    public void setReqContract(ReportMasterListReqContract value) {
        this.reqContract = value;
    }

}
