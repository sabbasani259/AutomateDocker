
package remote.wise.client.DetailedUsercommunicationReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for detailedUsercommunicationReportOutputContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="detailedUsercommunicationReportOutputContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alertDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventGeneratedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "detailedUsercommunicationReportOutputContract", propOrder = {
    "alertDesc",
    "eventGeneratedTime",
    "serialNumber"
})
public class DetailedUsercommunicationReportOutputContract {

    protected String alertDesc;
    protected String eventGeneratedTime;
    protected String serialNumber;

    /**
     * Gets the value of the alertDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlertDesc() {
        return alertDesc;
    }

    /**
     * Sets the value of the alertDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlertDesc(String value) {
        this.alertDesc = value;
    }

    /**
     * Gets the value of the eventGeneratedTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventGeneratedTime() {
        return eventGeneratedTime;
    }

    /**
     * Sets the value of the eventGeneratedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventGeneratedTime(String value) {
        this.eventGeneratedTime = value;
    }

    /**
     * Gets the value of the serialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the value of the serialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNumber(String value) {
        this.serialNumber = value;
    }

}
