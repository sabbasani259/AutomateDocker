
package remote.wise.client.ServiceHistoryDetailsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for serviceScheduleRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="serviceScheduleRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetGpId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetGpName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="assetGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dealerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="durationSchedule" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="engineHoursSchedule" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="hoursToNextSvc" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="jobCardNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="scheduleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceSchedule" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UOM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceScheduleRespContract", propOrder = {
    "assetGpId",
    "assetGpName",
    "assetGroupId",
    "dealerName",
    "durationSchedule",
    "engineHoursSchedule",
    "hoursToNextSvc",
    "jobCardNumber",
    "scheduleName",
    "serialNumber",
    "serviceDate",
    "serviceName",
    "serviceSchedule",
    "uom"
})
public class ServiceScheduleRespContract {

    protected int assetGpId;
    protected String assetGpName;
    protected int assetGroupId;
    protected String dealerName;
    protected int durationSchedule;
    protected long engineHoursSchedule;
    protected long hoursToNextSvc;
    protected String jobCardNumber;
    protected String scheduleName;
    protected String serialNumber;
    protected String serviceDate;
    protected String serviceName;
    protected String serviceSchedule;
    @XmlElement(name = "UOM")
    protected String uom;

    /**
     * Gets the value of the assetGpId property.
     * 
     */
    public int getAssetGpId() {
        return assetGpId;
    }

    /**
     * Sets the value of the assetGpId property.
     * 
     */
    public void setAssetGpId(int value) {
        this.assetGpId = value;
    }

    /**
     * Gets the value of the assetGpName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetGpName() {
        return assetGpName;
    }

    /**
     * Sets the value of the assetGpName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetGpName(String value) {
        this.assetGpName = value;
    }

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
     * Gets the value of the durationSchedule property.
     * 
     */
    public int getDurationSchedule() {
        return durationSchedule;
    }

    /**
     * Sets the value of the durationSchedule property.
     * 
     */
    public void setDurationSchedule(int value) {
        this.durationSchedule = value;
    }

    /**
     * Gets the value of the engineHoursSchedule property.
     * 
     */
    public long getEngineHoursSchedule() {
        return engineHoursSchedule;
    }

    /**
     * Sets the value of the engineHoursSchedule property.
     * 
     */
    public void setEngineHoursSchedule(long value) {
        this.engineHoursSchedule = value;
    }

    /**
     * Gets the value of the hoursToNextSvc property.
     * 
     */
    public long getHoursToNextSvc() {
        return hoursToNextSvc;
    }

    /**
     * Sets the value of the hoursToNextSvc property.
     * 
     */
    public void setHoursToNextSvc(long value) {
        this.hoursToNextSvc = value;
    }

    /**
     * Gets the value of the jobCardNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJobCardNumber() {
        return jobCardNumber;
    }

    /**
     * Sets the value of the jobCardNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJobCardNumber(String value) {
        this.jobCardNumber = value;
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
     * Gets the value of the serviceDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceDate() {
        return serviceDate;
    }

    /**
     * Sets the value of the serviceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceDate(String value) {
        this.serviceDate = value;
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
     * Gets the value of the serviceSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceSchedule() {
        return serviceSchedule;
    }

    /**
     * Sets the value of the serviceSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceSchedule(String value) {
        this.serviceSchedule = value;
    }

    /**
     * Gets the value of the uom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUOM() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUOM(String value) {
        this.uom = value;
    }

}
