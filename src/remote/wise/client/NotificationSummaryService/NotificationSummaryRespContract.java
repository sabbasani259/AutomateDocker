
package remote.wise.client.NotificationSummaryService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for notificationSummaryRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notificationSummaryRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="machineGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineProfileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notificationTypeIdList" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="notificationTypeNameList" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notificationcountcount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancy_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notificationSummaryRespContract", propOrder = {
    "machineGroupName",
    "machineProfileName",
    "notificationTypeIdList",
    "notificationTypeNameList",
    "notificationcountcount",
    "serialNumber",
    "tenancyName"
})
public class NotificationSummaryRespContract {

    protected String machineGroupName;
    protected String machineProfileName;
    protected int notificationTypeIdList;
    protected String notificationTypeNameList;
    protected int notificationcountcount;
    protected String serialNumber;
    @XmlElement(name = "tenancy_name")
    protected String tenancyName;

    /**
     * Gets the value of the machineGroupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineGroupName() {
        return machineGroupName;
    }

    /**
     * Sets the value of the machineGroupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineGroupName(String value) {
        this.machineGroupName = value;
    }

    /**
     * Gets the value of the machineProfileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineProfileName() {
        return machineProfileName;
    }

    /**
     * Sets the value of the machineProfileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineProfileName(String value) {
        this.machineProfileName = value;
    }

    /**
     * Gets the value of the notificationTypeIdList property.
     * 
     */
    public int getNotificationTypeIdList() {
        return notificationTypeIdList;
    }

    /**
     * Sets the value of the notificationTypeIdList property.
     * 
     */
    public void setNotificationTypeIdList(int value) {
        this.notificationTypeIdList = value;
    }

    /**
     * Gets the value of the notificationTypeNameList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationTypeNameList() {
        return notificationTypeNameList;
    }

    /**
     * Sets the value of the notificationTypeNameList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationTypeNameList(String value) {
        this.notificationTypeNameList = value;
    }

    /**
     * Gets the value of the notificationcountcount property.
     * 
     */
    public int getNotificationcountcount() {
        return notificationcountcount;
    }

    /**
     * Sets the value of the notificationcountcount property.
     * 
     */
    public void setNotificationcountcount(int value) {
        this.notificationcountcount = value;
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

    /**
     * Gets the value of the tenancyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTenancyName() {
        return tenancyName;
    }

    /**
     * Sets the value of the tenancyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTenancyName(String value) {
        this.tenancyName = value;
    }

}
