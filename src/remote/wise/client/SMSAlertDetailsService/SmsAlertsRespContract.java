
package remote.wise.client.SMSAlertDetailsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for smsAlertsRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="smsAlertsRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="blockedAirFilterStatus" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cmhr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="failureReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="highCoolantTempStatus" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="lowEngineOilPressureStatus" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="smsReceivedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="towawayStatus" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="waterInFuelStatus" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "smsAlertsRespContract", propOrder = {
    "blockedAirFilterStatus",
    "cmhr",
    "failureReason",
    "highCoolantTempStatus",
    "lowEngineOilPressureStatus",
    "serialNumber",
    "smsReceivedTime",
    "towawayStatus",
    "waterInFuelStatus"
})
public class SmsAlertsRespContract {

    protected int blockedAirFilterStatus;
    protected String cmhr;
    protected String failureReason;
    protected int highCoolantTempStatus;
    protected int lowEngineOilPressureStatus;
    protected String serialNumber;
    protected String smsReceivedTime;
    protected int towawayStatus;
    protected int waterInFuelStatus;

    /**
     * Gets the value of the blockedAirFilterStatus property.
     * 
     */
    public int getBlockedAirFilterStatus() {
        return blockedAirFilterStatus;
    }

    /**
     * Sets the value of the blockedAirFilterStatus property.
     * 
     */
    public void setBlockedAirFilterStatus(int value) {
        this.blockedAirFilterStatus = value;
    }

    /**
     * Gets the value of the cmhr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCmhr() {
        return cmhr;
    }

    /**
     * Sets the value of the cmhr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCmhr(String value) {
        this.cmhr = value;
    }

    /**
     * Gets the value of the failureReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFailureReason() {
        return failureReason;
    }

    /**
     * Sets the value of the failureReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFailureReason(String value) {
        this.failureReason = value;
    }

    /**
     * Gets the value of the highCoolantTempStatus property.
     * 
     */
    public int getHighCoolantTempStatus() {
        return highCoolantTempStatus;
    }

    /**
     * Sets the value of the highCoolantTempStatus property.
     * 
     */
    public void setHighCoolantTempStatus(int value) {
        this.highCoolantTempStatus = value;
    }

    /**
     * Gets the value of the lowEngineOilPressureStatus property.
     * 
     */
    public int getLowEngineOilPressureStatus() {
        return lowEngineOilPressureStatus;
    }

    /**
     * Sets the value of the lowEngineOilPressureStatus property.
     * 
     */
    public void setLowEngineOilPressureStatus(int value) {
        this.lowEngineOilPressureStatus = value;
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
     * Gets the value of the smsReceivedTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmsReceivedTime() {
        return smsReceivedTime;
    }

    /**
     * Sets the value of the smsReceivedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmsReceivedTime(String value) {
        this.smsReceivedTime = value;
    }

    /**
     * Gets the value of the towawayStatus property.
     * 
     */
    public int getTowawayStatus() {
        return towawayStatus;
    }

    /**
     * Sets the value of the towawayStatus property.
     * 
     */
    public void setTowawayStatus(int value) {
        this.towawayStatus = value;
    }

    /**
     * Gets the value of the waterInFuelStatus property.
     * 
     */
    public int getWaterInFuelStatus() {
        return waterInFuelStatus;
    }

    /**
     * Sets the value of the waterInFuelStatus property.
     * 
     */
    public void setWaterInFuelStatus(int value) {
        this.waterInFuelStatus = value;
    }

}
