
package remote.wise.client.MachinePerformanceReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for machinePerformanceReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="machinePerformanceReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="asset_group_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customMachineGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="customMachineGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="engineOff" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="engineOn" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="finishEngineRunHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="finishEngineRunHoursLife" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="finishFuelLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="finishFuelLevelLife" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fuelUsedIdleLitres" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="fuelUsedIdleLitresLife" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="fuelUsedLitres" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="fuelUsedLitresLife" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="idleTime" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="modelId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="modelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="overallFuelConsumptionLitres" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="powerBandHigh" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="powerBandLow" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="powerBandMedium" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="startingEngineRunHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="startingEngineRunHoursLife" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="tenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tenancyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="workingTime" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "machinePerformanceReportRespContract", propOrder = {
    "assetGroupId",
    "assetGroupName",
    "customMachineGroupId",
    "customMachineGroupName",
    "engineOff",
    "engineOn",
    "finishEngineRunHours",
    "finishEngineRunHoursLife",
    "finishFuelLevel",
    "finishFuelLevelLife",
    "fuelUsedIdleLitres",
    "fuelUsedIdleLitresLife",
    "fuelUsedLitres",
    "fuelUsedLitresLife",
    "idleTime",
    "modelId",
    "modelName",
    "overallFuelConsumptionLitres",
    "powerBandHigh",
    "powerBandLow",
    "powerBandMedium",
    "serialNumber",
    "startingEngineRunHours",
    "startingEngineRunHoursLife",
    "tenancyId",
    "tenancyName",
    "workingTime"
})
public class MachinePerformanceReportRespContract {

    protected int assetGroupId;
    @XmlElement(name = "asset_group_name")
    protected String assetGroupName;
    protected int customMachineGroupId;
    protected String customMachineGroupName;
    protected double engineOff;
    protected double engineOn;
    protected double finishEngineRunHours;
    protected double finishEngineRunHoursLife;
    protected String finishFuelLevel;
    protected String finishFuelLevelLife;
    protected double fuelUsedIdleLitres;
    protected double fuelUsedIdleLitresLife;
    protected double fuelUsedLitres;
    protected double fuelUsedLitresLife;
    protected double idleTime;
    protected int modelId;
    protected String modelName;
    protected double overallFuelConsumptionLitres;
    protected double powerBandHigh;
    protected double powerBandLow;
    protected double powerBandMedium;
    protected String serialNumber;
    protected double startingEngineRunHours;
    protected double startingEngineRunHoursLife;
    protected int tenancyId;
    protected String tenancyName;
    protected double workingTime;

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
     * Gets the value of the customMachineGroupId property.
     * 
     */
    public int getCustomMachineGroupId() {
        return customMachineGroupId;
    }

    /**
     * Sets the value of the customMachineGroupId property.
     * 
     */
    public void setCustomMachineGroupId(int value) {
        this.customMachineGroupId = value;
    }

    /**
     * Gets the value of the customMachineGroupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomMachineGroupName() {
        return customMachineGroupName;
    }

    /**
     * Sets the value of the customMachineGroupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomMachineGroupName(String value) {
        this.customMachineGroupName = value;
    }

    /**
     * Gets the value of the engineOff property.
     * 
     */
    public double getEngineOff() {
        return engineOff;
    }

    /**
     * Sets the value of the engineOff property.
     * 
     */
    public void setEngineOff(double value) {
        this.engineOff = value;
    }

    /**
     * Gets the value of the engineOn property.
     * 
     */
    public double getEngineOn() {
        return engineOn;
    }

    /**
     * Sets the value of the engineOn property.
     * 
     */
    public void setEngineOn(double value) {
        this.engineOn = value;
    }

    /**
     * Gets the value of the finishEngineRunHours property.
     * 
     */
    public double getFinishEngineRunHours() {
        return finishEngineRunHours;
    }

    /**
     * Sets the value of the finishEngineRunHours property.
     * 
     */
    public void setFinishEngineRunHours(double value) {
        this.finishEngineRunHours = value;
    }

    /**
     * Gets the value of the finishEngineRunHoursLife property.
     * 
     */
    public double getFinishEngineRunHoursLife() {
        return finishEngineRunHoursLife;
    }

    /**
     * Sets the value of the finishEngineRunHoursLife property.
     * 
     */
    public void setFinishEngineRunHoursLife(double value) {
        this.finishEngineRunHoursLife = value;
    }

    /**
     * Gets the value of the finishFuelLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinishFuelLevel() {
        return finishFuelLevel;
    }

    /**
     * Sets the value of the finishFuelLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinishFuelLevel(String value) {
        this.finishFuelLevel = value;
    }

    /**
     * Gets the value of the finishFuelLevelLife property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinishFuelLevelLife() {
        return finishFuelLevelLife;
    }

    /**
     * Sets the value of the finishFuelLevelLife property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinishFuelLevelLife(String value) {
        this.finishFuelLevelLife = value;
    }

    /**
     * Gets the value of the fuelUsedIdleLitres property.
     * 
     */
    public double getFuelUsedIdleLitres() {
        return fuelUsedIdleLitres;
    }

    /**
     * Sets the value of the fuelUsedIdleLitres property.
     * 
     */
    public void setFuelUsedIdleLitres(double value) {
        this.fuelUsedIdleLitres = value;
    }

    /**
     * Gets the value of the fuelUsedIdleLitresLife property.
     * 
     */
    public double getFuelUsedIdleLitresLife() {
        return fuelUsedIdleLitresLife;
    }

    /**
     * Sets the value of the fuelUsedIdleLitresLife property.
     * 
     */
    public void setFuelUsedIdleLitresLife(double value) {
        this.fuelUsedIdleLitresLife = value;
    }

    /**
     * Gets the value of the fuelUsedLitres property.
     * 
     */
    public double getFuelUsedLitres() {
        return fuelUsedLitres;
    }

    /**
     * Sets the value of the fuelUsedLitres property.
     * 
     */
    public void setFuelUsedLitres(double value) {
        this.fuelUsedLitres = value;
    }

    /**
     * Gets the value of the fuelUsedLitresLife property.
     * 
     */
    public double getFuelUsedLitresLife() {
        return fuelUsedLitresLife;
    }

    /**
     * Sets the value of the fuelUsedLitresLife property.
     * 
     */
    public void setFuelUsedLitresLife(double value) {
        this.fuelUsedLitresLife = value;
    }

    /**
     * Gets the value of the idleTime property.
     * 
     */
    public double getIdleTime() {
        return idleTime;
    }

    /**
     * Sets the value of the idleTime property.
     * 
     */
    public void setIdleTime(double value) {
        this.idleTime = value;
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
     * Gets the value of the overallFuelConsumptionLitres property.
     * 
     */
    public double getOverallFuelConsumptionLitres() {
        return overallFuelConsumptionLitres;
    }

    /**
     * Sets the value of the overallFuelConsumptionLitres property.
     * 
     */
    public void setOverallFuelConsumptionLitres(double value) {
        this.overallFuelConsumptionLitres = value;
    }

    /**
     * Gets the value of the powerBandHigh property.
     * 
     */
    public double getPowerBandHigh() {
        return powerBandHigh;
    }

    /**
     * Sets the value of the powerBandHigh property.
     * 
     */
    public void setPowerBandHigh(double value) {
        this.powerBandHigh = value;
    }

    /**
     * Gets the value of the powerBandLow property.
     * 
     */
    public double getPowerBandLow() {
        return powerBandLow;
    }

    /**
     * Sets the value of the powerBandLow property.
     * 
     */
    public void setPowerBandLow(double value) {
        this.powerBandLow = value;
    }

    /**
     * Gets the value of the powerBandMedium property.
     * 
     */
    public double getPowerBandMedium() {
        return powerBandMedium;
    }

    /**
     * Sets the value of the powerBandMedium property.
     * 
     */
    public void setPowerBandMedium(double value) {
        this.powerBandMedium = value;
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
     * Gets the value of the startingEngineRunHours property.
     * 
     */
    public double getStartingEngineRunHours() {
        return startingEngineRunHours;
    }

    /**
     * Sets the value of the startingEngineRunHours property.
     * 
     */
    public void setStartingEngineRunHours(double value) {
        this.startingEngineRunHours = value;
    }

    /**
     * Gets the value of the startingEngineRunHoursLife property.
     * 
     */
    public double getStartingEngineRunHoursLife() {
        return startingEngineRunHoursLife;
    }

    /**
     * Sets the value of the startingEngineRunHoursLife property.
     * 
     */
    public void setStartingEngineRunHoursLife(double value) {
        this.startingEngineRunHoursLife = value;
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
     * Gets the value of the workingTime property.
     * 
     */
    public double getWorkingTime() {
        return workingTime;
    }

    /**
     * Sets the value of the workingTime property.
     * 
     */
    public void setWorkingTime(double value) {
        this.workingTime = value;
    }

}
