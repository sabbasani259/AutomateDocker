
package remote.wise.client.UserAlertsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for userAlertsRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="userAlertsRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alertCounter" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="alertDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="alertSeverity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="alertTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="alertTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="assetEventId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="latestReceivedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="remarks" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "userAlertsRespContract", propOrder = {
    "alertCounter",
    "alertDescription",
    "alertSeverity",
    "alertTypeId",
    "alertTypeName",
    "assetEventId",
    "latestReceivedTime",
    "remarks",
    "serialNumber"
})
public class UserAlertsRespContract {

    protected int alertCounter;
    protected String alertDescription;
    protected String alertSeverity;
    protected int alertTypeId;
    protected String alertTypeName;
    protected int assetEventId;
    protected String latestReceivedTime;
    protected String remarks;
    protected String serialNumber;

    /**
     * Gets the value of the alertCounter property.
     * 
     */
    public int getAlertCounter() {
        return alertCounter;
    }

    /**
     * Sets the value of the alertCounter property.
     * 
     */
    public void setAlertCounter(int value) {
        this.alertCounter = value;
    }

    /**
     * Gets the value of the alertDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlertDescription() {
        return alertDescription;
    }

    /**
     * Sets the value of the alertDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlertDescription(String value) {
        this.alertDescription = value;
    }

    /**
     * Gets the value of the alertSeverity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlertSeverity() {
        return alertSeverity;
    }

    /**
     * Sets the value of the alertSeverity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlertSeverity(String value) {
        this.alertSeverity = value;
    }

    /**
     * Gets the value of the alertTypeId property.
     * 
     */
    public int getAlertTypeId() {
        return alertTypeId;
    }

    /**
     * Sets the value of the alertTypeId property.
     * 
     */
    public void setAlertTypeId(int value) {
        this.alertTypeId = value;
    }

    /**
     * Gets the value of the alertTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlertTypeName() {
        return alertTypeName;
    }

    /**
     * Sets the value of the alertTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlertTypeName(String value) {
        this.alertTypeName = value;
    }

    /**
     * Gets the value of the assetEventId property.
     * 
     */
    public int getAssetEventId() {
        return assetEventId;
    }

    /**
     * Sets the value of the assetEventId property.
     * 
     */
    public void setAssetEventId(int value) {
        this.assetEventId = value;
    }

    /**
     * Gets the value of the latestReceivedTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLatestReceivedTime() {
        return latestReceivedTime;
    }

    /**
     * Sets the value of the latestReceivedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLatestReceivedTime(String value) {
        this.latestReceivedTime = value;
    }

    /**
     * Gets the value of the remarks property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the value of the remarks property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemarks(String value) {
        this.remarks = value;
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
