
package remote.wise.client.MachineHoursReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for machineHoursReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="machineHoursReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="durationInCurrentStatus" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="lastEngineRun" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastReported" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="machineName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineProfileId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineProfileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="modelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tenancyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalMachineHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "machineHoursReportRespContract", propOrder = {
    "assetGroupName",
    "durationInCurrentStatus",
    "lastEngineRun",
    "lastReported",
    "location",
    "machineGroupId",
    "machineGroupName",
    "machineHours",
    "machineName",
    "machineProfileId",
    "machineProfileName",
    "modelId",
    "modelName",
    "serialNumber",
    "status",
    "tenancyId",
    "tenancyName",
    "totalMachineHours"
})
public class MachineHoursReportRespContract {

    protected String assetGroupName;
    protected long durationInCurrentStatus;
    protected String lastEngineRun;
    protected String lastReported;
    protected String location;
    protected int machineGroupId;
    protected String machineGroupName;
    protected double machineHours;
    protected String machineName;
    protected int machineProfileId;
    protected String machineProfileName;
    protected int modelId;
    protected String modelName;
    protected String serialNumber;
    protected String status;
    protected int tenancyId;
    protected String tenancyName;
    protected double totalMachineHours;

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
     * Gets the value of the durationInCurrentStatus property.
     * 
     */
    public long getDurationInCurrentStatus() {
        return durationInCurrentStatus;
    }

    /**
     * Sets the value of the durationInCurrentStatus property.
     * 
     */
    public void setDurationInCurrentStatus(long value) {
        this.durationInCurrentStatus = value;
    }

    /**
     * Gets the value of the lastEngineRun property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastEngineRun() {
        return lastEngineRun;
    }

    /**
     * Sets the value of the lastEngineRun property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastEngineRun(String value) {
        this.lastEngineRun = value;
    }

    /**
     * Gets the value of the lastReported property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastReported() {
        return lastReported;
    }

    /**
     * Sets the value of the lastReported property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastReported(String value) {
        this.lastReported = value;
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
     * Gets the value of the machineName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * Sets the value of the machineName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineName(String value) {
        this.machineName = value;
    }

    /**
     * Gets the value of the machineProfileId property.
     * 
     */
    public int getMachineProfileId() {
        return machineProfileId;
    }

    /**
     * Sets the value of the machineProfileId property.
     * 
     */
    public void setMachineProfileId(int value) {
        this.machineProfileId = value;
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
     * Gets the value of the modelId property.
     * 
     */
    public int getModelId() {
        return modelId;
    }

    /**
     * Sets the value of the modelId property.
     * 
     */
    public void setModelId(int value) {
        this.modelId = value;
    }

    /**
     * Gets the value of the modelName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the value of the modelName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelName(String value) {
        this.modelName = value;
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
     * Gets the value of the totalMachineHours property.
     * 
     */
    public double getTotalMachineHours() {
        return totalMachineHours;
    }

    /**
     * Sets the value of the totalMachineHours property.
     * 
     */
    public void setTotalMachineHours(double value) {
        this.totalMachineHours = value;
    }

}
