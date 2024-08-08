
package remote.wise.client.AssetDashboardService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for assetDashboardRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assetDashboardRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetImage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="connectivityStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dueForService" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="engineStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="engineTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="externalBatteryInVolts" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="externalBatteryStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fuelLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="highCoolantTemperature" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastPktReceivedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastReportedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="latitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lifeHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="longitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lowEngineOilPressure" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nickName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assetDashboardRespContract", propOrder = {
    "assetImage",
    "connectivityStatus",
    "dueForService",
    "engineStatus",
    "engineTypeName",
    "externalBatteryInVolts",
    "externalBatteryStatus",
    "fuelLevel",
    "highCoolantTemperature",
    "lastPktReceivedTime",
    "lastReportedTime",
    "latitude",
    "lifeHours",
    "longitude",
    "lowEngineOilPressure",
    "machineStatus",
    "modelName",
    "nickName",
    "notes",
    "profileName",
    "serialNumber"
})
public class AssetDashboardRespContract {

    protected String assetImage;
    protected String connectivityStatus;
    protected String dueForService;
    protected String engineStatus;
    protected String engineTypeName;
    protected String externalBatteryInVolts;
    protected String externalBatteryStatus;
    protected String fuelLevel;
    protected String highCoolantTemperature;
    protected String lastPktReceivedTime;
    protected String lastReportedTime;
    protected String latitude;
    protected String lifeHours;
    protected String longitude;
    protected String lowEngineOilPressure;
    protected String machineStatus;
    protected String modelName;
    protected String nickName;
    protected String notes;
    protected String profileName;
    protected String serialNumber;

    /**
     * Gets the value of the assetImage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetImage() {
        return assetImage;
    }

    /**
     * Sets the value of the assetImage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetImage(String value) {
        this.assetImage = value;
    }

    /**
     * Gets the value of the connectivityStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectivityStatus() {
        return connectivityStatus;
    }

    /**
     * Sets the value of the connectivityStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectivityStatus(String value) {
        this.connectivityStatus = value;
    }

    /**
     * Gets the value of the dueForService property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDueForService() {
        return dueForService;
    }

    /**
     * Sets the value of the dueForService property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDueForService(String value) {
        this.dueForService = value;
    }

    /**
     * Gets the value of the engineStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEngineStatus() {
        return engineStatus;
    }

    /**
     * Sets the value of the engineStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEngineStatus(String value) {
        this.engineStatus = value;
    }

    /**
     * Gets the value of the engineTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEngineTypeName() {
        return engineTypeName;
    }

    /**
     * Sets the value of the engineTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEngineTypeName(String value) {
        this.engineTypeName = value;
    }

    /**
     * Gets the value of the externalBatteryInVolts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalBatteryInVolts() {
        return externalBatteryInVolts;
    }

    /**
     * Sets the value of the externalBatteryInVolts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalBatteryInVolts(String value) {
        this.externalBatteryInVolts = value;
    }

    /**
     * Gets the value of the externalBatteryStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalBatteryStatus() {
        return externalBatteryStatus;
    }

    /**
     * Sets the value of the externalBatteryStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalBatteryStatus(String value) {
        this.externalBatteryStatus = value;
    }

    /**
     * Gets the value of the fuelLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFuelLevel() {
        return fuelLevel;
    }

    /**
     * Sets the value of the fuelLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFuelLevel(String value) {
        this.fuelLevel = value;
    }

    /**
     * Gets the value of the highCoolantTemperature property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHighCoolantTemperature() {
        return highCoolantTemperature;
    }

    /**
     * Sets the value of the highCoolantTemperature property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHighCoolantTemperature(String value) {
        this.highCoolantTemperature = value;
    }

    /**
     * Gets the value of the lastPktReceivedTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastPktReceivedTime() {
        return lastPktReceivedTime;
    }

    /**
     * Sets the value of the lastPktReceivedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastPktReceivedTime(String value) {
        this.lastPktReceivedTime = value;
    }

    /**
     * Gets the value of the lastReportedTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastReportedTime() {
        return lastReportedTime;
    }

    /**
     * Sets the value of the lastReportedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastReportedTime(String value) {
        this.lastReportedTime = value;
    }

    /**
     * Gets the value of the latitude property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLatitude(String value) {
        this.latitude = value;
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
     * Gets the value of the longitude property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLongitude(String value) {
        this.longitude = value;
    }

    /**
     * Gets the value of the lowEngineOilPressure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLowEngineOilPressure() {
        return lowEngineOilPressure;
    }

    /**
     * Sets the value of the lowEngineOilPressure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLowEngineOilPressure(String value) {
        this.lowEngineOilPressure = value;
    }

    /**
     * Gets the value of the machineStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineStatus() {
        return machineStatus;
    }

    /**
     * Sets the value of the machineStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineStatus(String value) {
        this.machineStatus = value;
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

}
