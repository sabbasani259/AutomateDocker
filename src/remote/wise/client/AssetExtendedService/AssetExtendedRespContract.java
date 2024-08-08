
package remote.wise.client.AssetExtendedService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for assetExtendedRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assetExtendedRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="application_timestamp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cmhLoginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cmhrflag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deviceStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="driverContactNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="driverName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FWVersionNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firmware_timestamp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="offset" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operatingEndTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operatingStartTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="previousCMHR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="primaryOwnerId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="usageCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assetExtendedRespContract", propOrder = {
    "applicationTimestamp",
    "cmhLoginId",
    "cmhrflag",
    "deviceStatus",
    "driverContactNumber",
    "driverName",
    "fwVersionNumber",
    "firmwareTimestamp",
    "notes",
    "offset",
    "operatingEndTime",
    "operatingStartTime",
    "previousCMHR",
    "primaryOwnerId",
    "serialNumber",
    "usageCategory"
})
public class AssetExtendedRespContract {

    @XmlElement(name = "application_timestamp")
    protected String applicationTimestamp;
    protected String cmhLoginId;
    protected String cmhrflag;
    protected String deviceStatus;
    protected String driverContactNumber;
    protected String driverName;
    @XmlElement(name = "FWVersionNumber")
    protected String fwVersionNumber;
    /**
	 * @return the fwVersionNumber
	 */
	public String getFwVersionNumber() {
		return fwVersionNumber;
	}

	/**
	 * @param fwVersionNumber the fwVersionNumber to set
	 */
	public void setFwVersionNumber(String fwVersionNumber) {
		this.fwVersionNumber = fwVersionNumber;
	}

	@XmlElement(name = "firmware_timestamp")
    protected String firmwareTimestamp;
    protected String notes;
    protected String offset;
    protected String operatingEndTime;
    protected String operatingStartTime;
    protected String previousCMHR;
    protected int primaryOwnerId;
    protected String serialNumber;
    protected String usageCategory;

    /**
     * Gets the value of the applicationTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplicationTimestamp() {
        return applicationTimestamp;
    }

    /**
     * Sets the value of the applicationTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplicationTimestamp(String value) {
        this.applicationTimestamp = value;
    }

    /**
     * Gets the value of the cmhLoginId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCmhLoginId() {
        return cmhLoginId;
    }

    /**
     * Sets the value of the cmhLoginId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCmhLoginId(String value) {
        this.cmhLoginId = value;
    }

    /**
     * Gets the value of the cmhrflag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCmhrflag() {
        return cmhrflag;
    }

    /**
     * Sets the value of the cmhrflag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCmhrflag(String value) {
        this.cmhrflag = value;
    }

    /**
     * Gets the value of the deviceStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceStatus() {
        return deviceStatus;
    }

    /**
     * Sets the value of the deviceStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceStatus(String value) {
        this.deviceStatus = value;
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
     * Gets the value of the fwVersionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFWVersionNumber() {
        return fwVersionNumber;
    }

    /**
     * Sets the value of the fwVersionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFWVersionNumber(String value) {
        this.fwVersionNumber = value;
    }

    /**
     * Gets the value of the firmwareTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirmwareTimestamp() {
        return firmwareTimestamp;
    }

    /**
     * Sets the value of the firmwareTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirmwareTimestamp(String value) {
        this.firmwareTimestamp = value;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the offset property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOffset() {
        return offset;
    }

    /**
     * Sets the value of the offset property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOffset(String value) {
        this.offset = value;
    }

    /**
     * Gets the value of the operatingEndTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatingEndTime() {
        return operatingEndTime;
    }

    /**
     * Sets the value of the operatingEndTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatingEndTime(String value) {
        this.operatingEndTime = value;
    }

    /**
     * Gets the value of the operatingStartTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatingStartTime() {
        return operatingStartTime;
    }

    /**
     * Sets the value of the operatingStartTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatingStartTime(String value) {
        this.operatingStartTime = value;
    }

    /**
     * Gets the value of the previousCMHR property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreviousCMHR() {
        return previousCMHR;
    }

    /**
     * Sets the value of the previousCMHR property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreviousCMHR(String value) {
        this.previousCMHR = value;
    }

    /**
     * Gets the value of the primaryOwnerId property.
     * 
     */
    public int getPrimaryOwnerId() {
        return primaryOwnerId;
    }

    /**
     * Sets the value of the primaryOwnerId property.
     * 
     */
    public void setPrimaryOwnerId(int value) {
        this.primaryOwnerId = value;
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
     * Gets the value of the usageCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsageCategory() {
        return usageCategory;
    }

    /**
     * Sets the value of the usageCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsageCategory(String value) {
        this.usageCategory = value;
    }

}
