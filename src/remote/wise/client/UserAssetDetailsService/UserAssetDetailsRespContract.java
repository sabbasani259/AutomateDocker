
package remote.wise.client.UserAssetDetailsService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for userAssetDetailsRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="userAssetDetailsRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetClassName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="assetCustomGroupName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="assetGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="assetTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customerEmailId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customerPhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerEmailId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerPhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerTenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="driverContactNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="driverName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="imeiNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lifeHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="make" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="renewalDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="simNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userAssetDetailsRespContract", propOrder = {
    "assetClassName",
    "assetCustomGroupName",
    "assetGroupName",
    "assetTypeName",
    "customerEmailId",
    "customerName",
    "customerPhoneNumber",
    "dealerEmailId",
    "dealerName",
    "dealerPhoneNumber",
    "dealerTenancyId",
    "driverContactNumber",
    "driverName",
    "imeiNumber",
    "lifeHours",
    "make",
    "renewalDate",
    "serialNumber",
    "simNumber"
})
public class UserAssetDetailsRespContract {

    protected String assetClassName;
    @XmlElement(nillable = true)
    protected List<String> assetCustomGroupName;
    protected String assetGroupName;
    protected String assetTypeName;
    protected String customerEmailId;
    protected String customerName;
    protected String customerPhoneNumber;
    protected String dealerEmailId;
    protected String dealerName;
    protected String dealerPhoneNumber;
    protected int dealerTenancyId;
    protected String driverContactNumber;
    protected String driverName;
    protected String imeiNumber;
    protected String lifeHours;
    protected int make;
    protected String renewalDate;
    protected String serialNumber;
    protected String simNumber;

    /**
     * Gets the value of the assetClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetClassName() {
        return assetClassName;
    }

    /**
     * Sets the value of the assetClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetClassName(String value) {
        this.assetClassName = value;
    }

    /**
     * Gets the value of the assetCustomGroupName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assetCustomGroupName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssetCustomGroupName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAssetCustomGroupName() {
        if (assetCustomGroupName == null) {
            assetCustomGroupName = new ArrayList<String>();
        }
        return this.assetCustomGroupName;
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
     * Gets the value of the customerEmailId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerEmailId() {
        return customerEmailId;
    }

    /**
     * Sets the value of the customerEmailId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerEmailId(String value) {
        this.customerEmailId = value;
    }

    /**
     * Gets the value of the customerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the value of the customerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerName(String value) {
        this.customerName = value;
    }

    /**
     * Gets the value of the customerPhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    /**
     * Sets the value of the customerPhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerPhoneNumber(String value) {
        this.customerPhoneNumber = value;
    }

    /**
     * Gets the value of the dealerEmailId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDealerEmailId() {
        return dealerEmailId;
    }

    /**
     * Sets the value of the dealerEmailId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDealerEmailId(String value) {
        this.dealerEmailId = value;
    }

    /**
     * Gets the value of the dealerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDealerName() {
        return dealerName;
    }

    /**
     * Sets the value of the dealerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDealerName(String value) {
        this.dealerName = value;
    }

    /**
     * Gets the value of the dealerPhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDealerPhoneNumber() {
        return dealerPhoneNumber;
    }

    /**
     * Sets the value of the dealerPhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDealerPhoneNumber(String value) {
        this.dealerPhoneNumber = value;
    }

    /**
     * Gets the value of the dealerTenancyId property.
     * 
     */
    public int getDealerTenancyId() {
        return dealerTenancyId;
    }

    /**
     * Sets the value of the dealerTenancyId property.
     * 
     */
    public void setDealerTenancyId(int value) {
        this.dealerTenancyId = value;
    }

    /**
     * Gets the value of the driverContactNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDriverContactNumber() {
        return driverContactNumber;
    }

    /**
     * Sets the value of the driverContactNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDriverContactNumber(String value) {
        this.driverContactNumber = value;
    }

    /**
     * Gets the value of the driverName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * Sets the value of the driverName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDriverName(String value) {
        this.driverName = value;
    }

    /**
     * Gets the value of the imeiNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImeiNumber() {
        return imeiNumber;
    }

    /**
     * Sets the value of the imeiNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImeiNumber(String value) {
        this.imeiNumber = value;
    }

    /**
     * Gets the value of the lifeHours property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLifeHours() {
        return lifeHours;
    }

    /**
     * Sets the value of the lifeHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLifeHours(String value) {
        this.lifeHours = value;
    }

    /**
     * Gets the value of the make property.
     * 
     */
    public int getMake() {
        return make;
    }

    /**
     * Sets the value of the make property.
     * 
     */
    public void setMake(int value) {
        this.make = value;
    }

    /**
     * Gets the value of the renewalDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRenewalDate() {
        return renewalDate;
    }

    /**
     * Sets the value of the renewalDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRenewalDate(String value) {
        this.renewalDate = value;
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
     * Gets the value of the simNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSimNumber() {
        return simNumber;
    }

    /**
     * Sets the value of the simNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSimNumber(String value) {
        this.simNumber = value;
    }

}
