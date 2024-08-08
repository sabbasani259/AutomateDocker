
package remote.wise.client.MapService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mapRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mapRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="activeAlert" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="engineStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastReportedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="latitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="longitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nickname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operatingEndTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operatingStartTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profileCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="profileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="severity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalMachineHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mapRespContract", propOrder = {
    "activeAlert",
    "engineStatus",
    "lastReportedTime",
    "latitude",
    "longitude",
    "nickname",
    "operatingEndTime",
    "operatingStartTime",
    "profileCode",
    "profileName",
    "serialNumber",
    "severity",
    "totalMachineHours"
})
public class MapRespContract {

    protected Long activeAlert;
    protected String engineStatus;
    protected String lastReportedTime;
    protected String latitude;
    protected String longitude;
    protected String nickname;
    protected String operatingEndTime;
    protected String operatingStartTime;
    protected int profileCode;
    protected String profileName;
    protected String serialNumber;
    protected String severity;
    protected String totalMachineHours;

    /**
     * Gets the value of the activeAlert property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getActiveAlert() {
        return activeAlert;
    }

    /**
     * Sets the value of the activeAlert property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setActiveAlert(Long value) {
        this.activeAlert = value;
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
     * Gets the value of the nickname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the value of the nickname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNickname(String value) {
        this.nickname = value;
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
     * Gets the value of the profileCode property.
     * 
     */
    public int getProfileCode() {
        return profileCode;
    }

    /**
     * Sets the value of the profileCode property.
     * 
     */
    public void setProfileCode(int value) {
        this.profileCode = value;
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
     * Gets the value of the severity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Sets the value of the severity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeverity(String value) {
        this.severity = value;
    }

    /**
     * Gets the value of the totalMachineHours property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalMachineHours() {
        return totalMachineHours;
    }

    /**
     * Sets the value of the totalMachineHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalMachineHours(String value) {
        this.totalMachineHours = value;
    }

}
