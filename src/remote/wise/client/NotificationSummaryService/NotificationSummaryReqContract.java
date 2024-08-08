
package remote.wise.client.NotificationSummaryService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for notificationSummaryReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notificationSummaryReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="activeAlerts" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="assetGroupIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="assetTypeIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="contactId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventSeverityList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="eventTypeIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="notificationIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="notificationTypeIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ownStock" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="period" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "notificationSummaryReqContract", propOrder = {
    "activeAlerts",
    "assetGroupIdList",
    "assetTypeIdList",
    "contactId",
    "eventSeverityList",
    "eventTypeIdList",
    "notificationIdList",
    "notificationTypeIdList",
    "ownStock",
    "period",
    "tenancyIdList"
})
public class NotificationSummaryReqContract {

    protected boolean activeAlerts;
    @XmlElement(nillable = true)
    protected List<Integer> assetGroupIdList;
    @XmlElement(nillable = true)
    protected List<Integer> assetTypeIdList;
    protected String contactId;
    @XmlElement(nillable = true)
    protected List<String> eventSeverityList;
    @XmlElement(nillable = true)
    protected List<Integer> eventTypeIdList;
    @XmlElement(nillable = true)
    protected List<Integer> notificationIdList;
    @XmlElement(nillable = true)
    protected List<Integer> notificationTypeIdList;
    protected boolean ownStock;
    protected String period;
    @XmlElement(nillable = true)
    protected List<Integer> tenancyIdList;

    /**
     * Gets the value of the activeAlerts property.
     * 
     */
    public boolean isActiveAlerts() {
        return activeAlerts;
    }

    /**
     * Sets the value of the activeAlerts property.
     * 
     */
    public void setActiveAlerts(boolean value) {
        this.activeAlerts = value;
    }

    /**
     * Gets the value of the assetGroupIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assetGroupIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssetGroupIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getAssetGroupIdList() {
        if (assetGroupIdList == null) {
            assetGroupIdList = new ArrayList<Integer>();
        }
        return this.assetGroupIdList;
    }

    /**
     * Gets the value of the assetTypeIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assetTypeIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssetTypeIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getAssetTypeIdList() {
        if (assetTypeIdList == null) {
            assetTypeIdList = new ArrayList<Integer>();
        }
        return this.assetTypeIdList;
    }

    /**
     * Gets the value of the contactId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactId() {
        return contactId;
    }

    /**
     * Sets the value of the contactId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactId(String value) {
        this.contactId = value;
    }

    /**
     * Gets the value of the eventSeverityList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventSeverityList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventSeverityList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getEventSeverityList() {
        if (eventSeverityList == null) {
            eventSeverityList = new ArrayList<String>();
        }
        return this.eventSeverityList;
    }

    /**
     * Gets the value of the eventTypeIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventTypeIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventTypeIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getEventTypeIdList() {
        if (eventTypeIdList == null) {
            eventTypeIdList = new ArrayList<Integer>();
        }
        return this.eventTypeIdList;
    }

    /**
     * Gets the value of the notificationIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notificationIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotificationIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getNotificationIdList() {
        if (notificationIdList == null) {
            notificationIdList = new ArrayList<Integer>();
        }
        return this.notificationIdList;
    }

    /**
     * Gets the value of the notificationTypeIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notificationTypeIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotificationTypeIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getNotificationTypeIdList() {
        if (notificationTypeIdList == null) {
            notificationTypeIdList = new ArrayList<Integer>();
        }
        return this.notificationTypeIdList;
    }

    /**
     * Gets the value of the ownStock property.
     * 
     */
    public boolean isOwnStock() {
        return ownStock;
    }

    /**
     * Sets the value of the ownStock property.
     * 
     */
    public void setOwnStock(boolean value) {
        this.ownStock = value;
    }

    /**
     * Gets the value of the period property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeriod() {
        return period;
    }

    /**
     * Sets the value of the period property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeriod(String value) {
        this.period = value;
    }

    /**
     * Gets the value of the tenancyIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tenancyIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTenancyIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getTenancyIdList() {
        if (tenancyIdList == null) {
            tenancyIdList = new ArrayList<Integer>();
        }
        return this.tenancyIdList;
    }

}
