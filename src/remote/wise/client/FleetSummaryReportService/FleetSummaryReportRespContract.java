
package remote.wise.client.FleetSummaryReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fleetSummaryReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fleetSummaryReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="averageFuelConsumption" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="engineOff" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="fuelUsedInIdle" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="fuelused" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="idleTime" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="machineGroupIdList" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="machineName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineProfile" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="modelIdList" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="modelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="powerBandLow" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="powerBandMedium" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="powerBandhigh" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="profile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenanctIdList" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tenancyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalMachineLifeHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
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
@XmlType(name = "fleetSummaryReportRespContract", propOrder = {
    "averageFuelConsumption",
    "engineOff",
    "fuelUsedInIdle",
    "fuelused",
    "idleTime",
    "machineGroupIdList",
    "machineGroupName",
    "machineHours",
    "machineName",
    "machineProfile",
    "modelIdList",
    "modelName",
    "powerBandLow",
    "powerBandMedium",
    "powerBandhigh",
    "profile",
    "serialNumber",
    "tenanctIdList",
    "tenancyName",
    "totalMachineLifeHours",
    "workingTime"
})
public class FleetSummaryReportRespContract {

    protected double averageFuelConsumption;
    protected double engineOff;
    protected double fuelUsedInIdle;
    protected double fuelused;
    protected double idleTime;
    protected int machineGroupIdList;
    protected String machineGroupName;
    protected double machineHours;
    protected String machineName;
    protected int machineProfile;
    protected int modelIdList;
    protected String modelName;
    protected double powerBandLow;
    protected double powerBandMedium;
    protected double powerBandhigh;
    protected String profile;
    protected String serialNumber;
    protected int tenanctIdList;
    protected String tenancyName;
    protected double totalMachineLifeHours;
    protected double workingTime;

    /**
     * Gets the value of the averageFuelConsumption property.
     * 
     */
    public double getAverageFuelConsumption() {
        return averageFuelConsumption;
    }

    /**
     * Sets the value of the averageFuelConsumption property.
     * 
     */
    public void setAverageFuelConsumption(double value) {
        this.averageFuelConsumption = value;
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
     * Gets the value of the fuelUsedInIdle property.
     * 
     */
    public double getFuelUsedInIdle() {
        return fuelUsedInIdle;
    }

    /**
     * Sets the value of the fuelUsedInIdle property.
     * 
     */
    public void setFuelUsedInIdle(double value) {
        this.fuelUsedInIdle = value;
    }

    /**
     * Gets the value of the fuelused property.
     * 
     */
    public double getFuelused() {
        return fuelused;
    }

    /**
     * Sets the value of the fuelused property.
     * 
     */
    public void setFuelused(double value) {
        this.fuelused = value;
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
     * Gets the value of the machineGroupIdList property.
     * 
     */
    public int getMachineGroupIdList() {
        return machineGroupIdList;
    }

    /**
     * Sets the value of the machineGroupIdList property.
     * 
     */
    public void setMachineGroupIdList(int value) {
        this.machineGroupIdList = value;
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
     * Gets the value of the machineProfile property.
     * 
     */
    public int getMachineProfile() {
        return machineProfile;
    }

    /**
     * Sets the value of the machineProfile property.
     * 
     */
    public void setMachineProfile(int value) {
        this.machineProfile = value;
    }

    /**
     * Gets the value of the modelIdList property.
     * 
     */
    public int getModelIdList() {
        return modelIdList;
    }

    /**
     * Sets the value of the modelIdList property.
     * 
     */
    public void setModelIdList(int value) {
        this.modelIdList = value;
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
     * Gets the value of the powerBandhigh property.
     * 
     */
    public double getPowerBandhigh() {
        return powerBandhigh;
    }

    /**
     * Sets the value of the powerBandhigh property.
     * 
     */
    public void setPowerBandhigh(double value) {
        this.powerBandhigh = value;
    }

    /**
     * Gets the value of the profile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfile(String value) {
        this.profile = value;
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
     * Gets the value of the tenanctIdList property.
     * 
     */
    public int getTenanctIdList() {
        return tenanctIdList;
    }

    /**
     * Sets the value of the tenanctIdList property.
     * 
     */
    public void setTenanctIdList(int value) {
        this.tenanctIdList = value;
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
