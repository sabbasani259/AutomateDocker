
package remote.wise.client.NotificationSummaryReportService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for notificationSummaryReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notificationSummaryReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="assetTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="count" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="machineGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nameCount">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="nickName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notificationTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "notificationSummaryReportRespContract", propOrder = {
    "assetGroupId",
    "assetGroupName",
    "assetTypeId",
    "assetTypeName",
    "count",
    "machineGroupId",
    "machineGroupName",
    "nameCount",
    "nickName",
    "notificationTypeName",
    "serialNumber",
    "tenancyId",
    "tenancyName"
})
public class NotificationSummaryReportRespContract {

    protected int assetGroupId;
    protected String assetGroupName;
    protected int assetTypeId;
    protected String assetTypeName;
    protected Long count;
    protected int machineGroupId;
    protected String machineGroupName;
    @XmlElement(required = true)
    protected NotificationSummaryReportRespContract.NameCount nameCount;
    protected String nickName;
    protected String notificationTypeName;
    protected String serialNumber;
    protected int tenancyId;
    @XmlElement(name = "tenancy_name")
    protected String tenancyName;

    /**
     * Gets the value of the assetGroupId property.
     * 
     */
    public int getAssetGroupId() {
        return assetGroupId;
    }

    /**
     * Sets the value of the assetGroupId property.
     * 
     */
    public void setAssetGroupId(int value) {
        this.assetGroupId = value;
    }

    /**
     * Gets the value of the assetGroupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetGroupName() {
        return assetGroupName;
    }

    /**
     * Sets the value of the assetGroupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetGroupName(String value) {
        this.assetGroupName = value;
    }

    /**
     * Gets the value of the assetTypeId property.
     * 
     */
    public int getAssetTypeId() {
        return assetTypeId;
    }

    /**
     * Sets the value of the assetTypeId property.
     * 
     */
    public void setAssetTypeId(int value) {
        this.assetTypeId = value;
    }

    /**
     * Gets the value of the assetTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetTypeName() {
        return assetTypeName;
    }

    /**
     * Sets the value of the assetTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetTypeName(String value) {
        this.assetTypeName = value;
    }

    /**
     * Gets the value of the count property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCount(Long value) {
        this.count = value;
    }

    /**
     * Gets the value of the machineGroupId property.
     * 
     */
    public int getMachineGroupId() {
        return machineGroupId;
    }

    /**
     * Sets the value of the machineGroupId property.
     * 
     */
    public void setMachineGroupId(int value) {
        this.machineGroupId = value;
    }

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
     * Gets the value of the nameCount property.
     * 
     * @return
     *     possible object is
     *     {@link NotificationSummaryReportRespContract.NameCount }
     *     
     */
    public NotificationSummaryReportRespContract.NameCount getNameCount() {
        return nameCount;
    }

    /**
     * Sets the value of the nameCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationSummaryReportRespContract.NameCount }
     *     
     */
    public void setNameCount(NotificationSummaryReportRespContract.NameCount value) {
        this.nameCount = value;
    }

    /**
     * Gets the value of the nickName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Sets the value of the nickName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNickName(String value) {
        this.nickName = value;
    }

    /**
     * Gets the value of the notificationTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationTypeName() {
        return notificationTypeName;
    }

    /**
     * Sets the value of the notificationTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationTypeName(String value) {
        this.notificationTypeName = value;
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
     * Gets the value of the tenancyId property.
     * 
     */
    public int getTenancyId() {
        return tenancyId;
    }

    /**
     * Sets the value of the tenancyId property.
     * 
     */
    public void setTenancyId(int value) {
        this.tenancyId = value;
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


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "entry"
    })
    public static class NameCount {

        protected List<NotificationSummaryReportRespContract.NameCount.Entry> entry;

        /**
         * Gets the value of the entry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEntry().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NotificationSummaryReportRespContract.NameCount.Entry }
         * 
         * 
         */
        public List<NotificationSummaryReportRespContract.NameCount.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<NotificationSummaryReportRespContract.NameCount.Entry>();
            }
            return this.entry;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "key",
            "value"
        })
        public static class Entry {

            protected String key;
            protected Long value;

            /**
             * Gets the value of the key property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKey() {
                return key;
            }

            /**
             * Sets the value of the key property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKey(String value) {
                this.key = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link Long }
             *     
             */
            public Long getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link Long }
             *     
             */
            public void setValue(Long value) {
                this.value = value;
            }

        }

    }

}
