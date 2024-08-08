
package remote.wise.client.MachineActivityReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for machineActivityReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="machineActivityReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetGroup_ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="duration_in_status" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineGroup_Id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineGroup_Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="profileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancy_ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="totalMachineLifeHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "machineActivityReportRespContract", propOrder = {
    "assetGroupID",
    "assetTypeId",
    "assetTypeName",
    "durationInStatus",
    "location",
    "machineGroupId",
    "machineGroupName",
    "machineHours",
    "profileName",
    "serialNumber",
    "status",
    "tenancyName",
    "tenancyID",
    "totalMachineLifeHours"
})
public class MachineActivityReportRespContract {

    @XmlElement(name = "assetGroup_ID")
    protected int assetGroupID;
    protected int assetTypeId;
    protected String assetTypeName;
    @XmlElement(name = "duration_in_status")
    protected long durationInStatus;
    protected String location;
    @XmlElement(name = "machineGroup_Id")
    protected int machineGroupId;
    @XmlElement(name = "machineGroup_Name")
    protected String machineGroupName;
    protected double machineHours;
    protected String profileName;
    protected String serialNumber;
    protected String status;
    protected String tenancyName;
    @XmlElement(name = "tenancy_ID")
    protected int tenancyID;
    protected double totalMachineLifeHours;

    /**
     * Gets the value of the assetGroupID property.
     * 
     */
    public int getAssetGroupID() {
        return assetGroupID;
    }

    /**
     * Sets the value of the assetGroupID property.
     * 
     */
    public void setAssetGroupID(int value) {
        this.assetGroupID = value;
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
     * Gets the value of the durationInStatus property.
     * 
     */
    public long getDurationInStatus() {
        return durationInStatus;
    }

    /**
     * Sets the value of the durationInStatus property.
     * 
     */
    public void setDurationInStatus(long value) {
        this.durationInStatus = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(String value) {
        this.location = value;
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
     * Gets the value of the machineHours property.
     * 
     */
    public double getMachineHours() {
        return machineHours;
    }

    /**
     * Sets the value of the machineHours property.
     * 
     */
    public void setMachineHours(double value) {
        this.machineHours = value;
    }

    /**
     * Gets the value of the profileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * Sets the value of the profileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfileName(String value) {
        this.profileName = value;
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
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
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
     * Gets the value of the tenancyID property.
     * 
     */
    public int getTenancyID() {
        return tenancyID;
    }

    /**
     * Sets the value of the tenancyID property.
     * 
     */
    public void setTenancyID(int value) {
        this.tenancyID = value;
    }

    /**
     * Gets the value of the totalMachineLifeHours property.
     * 
     */
    public double getTotalMachineLifeHours() {
        return totalMachineLifeHours;
    }

    /**
     * Sets the value of the totalMachineLifeHours property.
     * 
     */
    public void setTotalMachineLifeHours(double value) {
        this.totalMachineLifeHours = value;
    }

}
