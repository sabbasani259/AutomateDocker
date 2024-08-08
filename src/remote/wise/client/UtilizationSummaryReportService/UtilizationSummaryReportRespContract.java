
package remote.wise.client.UtilizationSummaryReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for utilizationSummaryReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="utilizationSummaryReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="engineOffDurationInMin" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="engineRunDurationInMin" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="engineWorkingDurationInMin" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="machineGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineProfileId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineProfileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineUtilizationPercentage" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="modelId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="modelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tenancyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "utilizationSummaryReportRespContract", propOrder = {
    "engineOffDurationInMin",
    "engineRunDurationInMin",
    "engineWorkingDurationInMin",
    "machineGroupId",
    "machineGroupName",
    "machineName",
    "machineProfileId",
    "machineProfileName",
    "machineUtilizationPercentage",
    "modelId",
    "modelName",
    "serialNumber",
    "tenancyId",
    "tenancyName"
})
public class UtilizationSummaryReportRespContract {

    protected double engineOffDurationInMin;
    protected Double engineRunDurationInMin;
    protected double engineWorkingDurationInMin;
    protected int machineGroupId;
    protected String machineGroupName;
    protected String machineName;
    protected int machineProfileId;
    protected String machineProfileName;
    protected Double machineUtilizationPercentage;
    protected int modelId;
    protected String modelName;
    protected String serialNumber;
    protected int tenancyId;
    protected String tenancyName;

    /**
     * Gets the value of the engineOffDurationInMin property.
     * 
     */
    public double getEngineOffDurationInMin() {
        return engineOffDurationInMin;
    }

    /**
     * Sets the value of the engineOffDurationInMin property.
     * 
     */
    public void setEngineOffDurationInMin(double value) {
        this.engineOffDurationInMin = value;
    }

    /**
     * Gets the value of the engineRunDurationInMin property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getEngineRunDurationInMin() {
        return engineRunDurationInMin;
    }

    /**
     * Sets the value of the engineRunDurationInMin property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setEngineRunDurationInMin(Double value) {
        this.engineRunDurationInMin = value;
    }

    /**
     * Gets the value of the engineWorkingDurationInMin property.
     * 
     */
    public double getEngineWorkingDurationInMin() {
        return engineWorkingDurationInMin;
    }

    /**
     * Sets the value of the engineWorkingDurationInMin property.
     * 
     */
    public void setEngineWorkingDurationInMin(double value) {
        this.engineWorkingDurationInMin = value;
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
     * Gets the value of the machineUtilizationPercentage property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMachineUtilizationPercentage() {
        return machineUtilizationPercentage;
    }

    /**
     * Sets the value of the machineUtilizationPercentage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMachineUtilizationPercentage(Double value) {
        this.machineUtilizationPercentage = value;
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

}
