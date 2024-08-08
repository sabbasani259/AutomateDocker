
package remote.wise.client.AssetServiceScheduleService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for assetServiceScheduleGetRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assetServiceScheduleGetRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dealerId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dealerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="engineHoursSchedule" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="eventId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="hoursToNextService" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="scheduleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="scheduledDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceScheduleId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *          &lt;element name="extendedWarrantyType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assetServiceScheduleGetRespContract", propOrder = {
    "dealerId",
    "dealerName",
    "engineHoursSchedule",
    "eventId",
    "hoursToNextService",
    "scheduleName",
    "scheduledDate",
    "serialNumber",
    "serviceName",
    "serviceScheduleId",
    "extendedWarrantyType"
})
public class AssetServiceScheduleGetRespContract {

    protected int dealerId;
    protected String dealerName;
    protected Long engineHoursSchedule;
    protected int eventId;
    protected Long hoursToNextService;
    protected String scheduleName;
    protected String scheduledDate;
    protected String serialNumber;
    protected String serviceName;
    protected int serviceScheduleId;
    //ramu B added extendedWarrantyType
    protected String extendedWarrantyType;

    public String getExtendedWarrantyType() {
		return extendedWarrantyType;
	}

	public void setExtendedWarrantyType(String extendedWarrantyType) {
		this.extendedWarrantyType = extendedWarrantyType;
	}

    /**
     * Gets the value of the dealerId property.
     * 
     */
    public int getDealerId() {
        return dealerId;
    }

    /**
     * Sets the value of the dealerId property.
     * 
     */
    public void setDealerId(int value) {
        this.dealerId = value;
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
     * Gets the value of the engineHoursSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEngineHoursSchedule() {
        return engineHoursSchedule;
    }

    /**
     * Sets the value of the engineHoursSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEngineHoursSchedule(Long value) {
        this.engineHoursSchedule = value;
    }

    /**
     * Gets the value of the eventId property.
     * 
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * Sets the value of the eventId property.
     * 
     */
    public void setEventId(int value) {
        this.eventId = value;
    }

    /**
     * Gets the value of the hoursToNextService property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getHoursToNextService() {
        return hoursToNextService;
    }

    /**
     * Sets the value of the hoursToNextService property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setHoursToNextService(Long value) {
        this.hoursToNextService = value;
    }

    /**
     * Gets the value of the scheduleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScheduleName() {
        return scheduleName;
    }

    /**
     * Sets the value of the scheduleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScheduleName(String value) {
        this.scheduleName = value;
    }

    /**
     * Gets the value of the scheduledDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScheduledDate() {
        return scheduledDate;
    }

    /**
     * Sets the value of the scheduledDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScheduledDate(String value) {
        this.scheduledDate = value;
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
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceName(String value) {
        this.serviceName = value;
    }

    /**
     * Gets the value of the serviceScheduleId property.
     * 
     */
    public int getServiceScheduleId() {
        return serviceScheduleId;
    }

    /**
     * Sets the value of the serviceScheduleId property.
     * 
     */
    public void setServiceScheduleId(int value) {
        this.serviceScheduleId = value;
    }

}
