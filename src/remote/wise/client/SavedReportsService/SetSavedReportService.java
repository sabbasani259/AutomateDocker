
package remote.wise.client.SavedReportsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetSavedReportService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetSavedReportService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="respObj" type="{http://webservice.service.wise.remote/}savedReportRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetSavedReportService", propOrder = {
    "respObj"
})
public class SetSavedReportService {

    protected SavedReportRespContract respObj;

    /**
     * Gets the value of the respObj property.
     * 
     * @return
     *     possible object is
     *     {@link SavedReportRespContract }
     *     
     */
    public SavedReportRespContract getRespObj() {
        return respObj;
    }

    /**
     * Sets the value of the respObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link SavedReportRespContract }
     *     
     */
    public void setRespObj(SavedReportRespContract value) {
        this.respObj = value;
    }

}
