
package remote.wise.client.MachineHoursServiceReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for machineHoursServiceReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="machineHoursServiceReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="approximateServiceDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hoursToNextService" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="lastServiceHour" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="location" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineProfileId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineProfileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="modelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nextService" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nickName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="scheduleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="severity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tenancyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "machineHoursServiceReportRespContract", propOrder = {
    "approximateServiceDate",
    "dealerName",
    "hoursToNextService",
    "lastServiceHour",
    "location",
    "machineGroupId",
    "machineGroupName",
    "machineProfileId",
    "machineProfileName",
    "modelId",
    "modelName",
    "nextService",
    "nickName",
    "scheduleName",
    "serialNumber",
    "serviceDate",
    "serviceName",
    "severity",
    "status",
    "tenancyId",
    "tenancyName",
    "totalMachineLifeHours"
})
public class MachineHoursServiceReportRespContract {

    protected String approximateServiceDate;
    protected String dealerName;
    protected double hoursToNextService;
    protected double lastServiceHour;
    protected String location;
    protected int machineGroupId;
    protected String machineGroupName;
    protected int machineProfileId;
    protected String machineProfileName;
    protected int modelId;
    protected String modelName;
    protected String nextService;
    protected String nickName;
    protected String scheduleName;
    protected String serialNumber;
    protected String serviceDate;
    protected String serviceName;
    protected String severity;
    protected String status;
    protected int tenancyId;
    protected String tenancyName;
    protected double totalMachineLifeHours;

    /**
     * Gets the value of the approximateServiceDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApproximateServiceDate() {
        return approximateServiceDate;
    }

    /**
     * Sets the value of the approximateServiceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApproximateServiceDate(String value) {
        this.approximateServiceDate = value;
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
     * Gets the value of the hoursToNextService property.
     * 
     */
    public double getHoursToNextService() {
        return hoursToNextService;
    }

    /**
     * Sets the value of the hoursToNextService property.
     * 
     */
    public void setHoursToNextService(double value) {
        this.hoursToNextService = value;
    }

    /**
     * Gets the value of the lastServiceHour property.
     * 
     */
    public double getLastServiceHour() {
        return lastServiceHour;
    }

    /**
     * Sets the value of the lastServiceHour property.
     * 
     */
    public void setLastServiceHour(double value) {
        this.lastServiceHour = value;
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
     * Gets the value of the nextService property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNextService() {
        return nextService;
    }

    /**
     * Sets the value of the nextService property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNextService(String value) {
        this.nextService = value;
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
