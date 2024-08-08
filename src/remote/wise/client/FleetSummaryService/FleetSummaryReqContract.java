
package remote.wise.client.FleetSummaryService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fleetSummaryReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fleetSummaryReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alertSeverity" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="alertTypeIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="assetGroupIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="assetTypeIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="contactId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customAssetGroupIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="landmarkIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineGroupId" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="notificationDimensionID" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "fleetSummaryReqContract", propOrder = {
    "alertSeverity",
    "alertTypeIdList",
    "assetGroupIdList",
    "assetTypeIdList",
    "contactId",
    "customAssetGroupIdList",
    "landmarkIdList",
    "machineGroupId",
    "notificationDimensionID",
    "ownStock",
    "period",
    "tenancyIdList"
})
public class FleetSummaryReqContract {

    @XmlElement(nillable = true)
    protected List<String> alertSeverity;
    @XmlElement(nillable = true)
    protected List<Integer> alertTypeIdList;
    @XmlElement(nillable = true)
    protected List<Integer> assetGroupIdList;
    @XmlElement(nillable = true)
    protected List<Integer> assetTypeIdList;
    protected String contactId;
    @XmlElement(nillable = true)
    protected List<Integer> customAssetGroupIdList;
    @XmlElement(nillable = true)
    protected List<Integer> landmarkIdList;
    @XmlElement(nillable = true)
    protected List<Integer> machineGroupId;
    @XmlElement(nillable = true)
    protected List<Integer> notificationDimensionID;
    protected boolean ownStock;
    protected String period;
    @XmlElement(nillable = true)
    protected List<Integer> tenancyIdList;

    /**
     * Gets the value of the alertSeverity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alertSeverity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlertSeverity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAlertSeverity() {
        if (alertSeverity == null) {
            alertSeverity = new ArrayList<String>();
        }
        return this.alertSeverity;
    }

    /**
     * Gets the value of the alertTypeIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alertTypeIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlertTypeIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getAlertTypeIdList() {
        if (alertTypeIdList == null) {
            alertTypeIdList = new ArrayList<Integer>();
        }
        return this.alertTypeIdList;
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
     * Gets the value of the customAssetGroupIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the customAssetGroupIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustomAssetGroupIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getCustomAssetGroupIdList() {
        if (customAssetGroupIdList == null) {
            customAssetGroupIdList = new ArrayList<Integer>();
        }
        return this.customAssetGroupIdList;
    }

    /**
     * Gets the value of the landmarkIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the landmarkIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLandmarkIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getLandmarkIdList() {
        if (landmarkIdList == null) {
            landmarkIdList = new ArrayList<Integer>();
        }
        return this.landmarkIdList;
    }

    /**
     * Gets the value of the machineGroupId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machineGroupId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachineGroupId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getMachineGroupId() {
        if (machineGroupId == null) {
            machineGroupId = new ArrayList<Integer>();
        }
        return this.machineGroupId;
    }

    /**
     * Gets the value of the notificationDimensionID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the notificationDimensionID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNotificationDimensionID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getNotificationDimensionID() {
        if (notificationDimensionID == null) {
            notificationDimensionID = new ArrayList<Integer>();
        }
        return this.notificationDimensionID;
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
