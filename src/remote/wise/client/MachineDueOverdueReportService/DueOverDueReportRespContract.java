
package remote.wise.client.MachineDueOverdueReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dueOverDueReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dueOverDueReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customerContactNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerContactNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dueOrOverDueDays" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dueOrOverDueHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="machineGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineProfileId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineProfileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="modelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operatorContactNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operatorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="plannedServiceDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="plannedServiceHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="scheduleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "dueOverDueReportRespContract", propOrder = {
    "customerContactNumber",
    "customerName",
    "dealerContactNumber",
    "dealerName",
    "dueOrOverDueDays",
    "dueOrOverDueHours",
    "machineGroupId",
    "machineGroupName",
    "machineName",
    "machineProfileId",
    "machineProfileName",
    "modelId",
    "modelName",
    "operatorContactNumber",
    "operatorName",
    "plannedServiceDate",
    "plannedServiceHours",
    "scheduleName",
    "serialNumber",
    "serviceName",
    "tenancyId",
    "tenancyName",
    "totalMachineHours"
})
public class DueOverDueReportRespContract {

    protected String customerContactNumber;
    protected String customerName;
    protected String dealerContactNumber;
    protected String dealerName;
    protected int dueOrOverDueDays;
    protected double dueOrOverDueHours;
    protected int machineGroupId;
    protected String machineGroupName;
    protected String machineName;
    protected int machineProfileId;
    protected String machineProfileName;
    protected int modelId;
    protected String modelName;
    protected String operatorContactNumber;
    protected String operatorName;
    protected String plannedServiceDate;
    protected double plannedServiceHours;
    protected String scheduleName;
    protected String serialNumber;
    protected String serviceName;
    protected int tenancyId;
    protected String tenancyName;
    protected double totalMachineHours;

    /**
     * Gets the value of the customerContactNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerContactNumber() {
        return customerContactNumber;
    }

    /**
     * Sets the value of the customerContactNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerContactNumber(String value) {
        this.customerContactNumber = value;
    }

    /**
     * Gets the value of the customerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the value of the customerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerName(String value) {
        this.customerName = value;
    }

    /**
     * Gets the value of the dealerContactNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDealerContactNumber() {
        return dealerContactNumber;
    }

    /**
     * Sets the value of the dealerContactNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDealerContactNumber(String value) {
        this.dealerContactNumber = value;
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
     * Gets the value of the dueOrOverDueDays property.
     * 
     */
    public int getDueOrOverDueDays() {
        return dueOrOverDueDays;
    }

    /**
     * Sets the value of the dueOrOverDueDays property.
     * 
     */
    public void setDueOrOverDueDays(int value) {
        this.dueOrOverDueDays = value;
    }

    /**
     * Gets the value of the dueOrOverDueHours property.
     * 
     */
    public double getDueOrOverDueHours() {
        return dueOrOverDueHours;
    }

    /**
     * Sets the value of the dueOrOverDueHours property.
     * 
     */
    public void setDueOrOverDueHours(double value) {
        this.dueOrOverDueHours = value;
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
     * Gets the value of the operatorContactNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatorContactNumber() {
        return operatorContactNumber;
    }

    /**
     * Sets the value of the operatorContactNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatorContactNumber(String value) {
        this.operatorContactNumber = value;
    }

    /**
     * Gets the value of the operatorName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * Sets the value of the operatorName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatorName(String value) {
        this.operatorName = value;
    }

    /**
     * Gets the value of the plannedServiceDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlannedServiceDate() {
        return plannedServiceDate;
    }

    /**
     * Sets the value of the plannedServiceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlannedServiceDate(String value) {
        this.plannedServiceDate = value;
    }

    /**
     * Gets the value of the plannedServiceHours property.
     * 
     */
    public double getPlannedServiceHours() {
        return plannedServiceHours;
    }

    /**
     * Sets the value of the plannedServiceHours property.
     * 
     */
    public void setPlannedServiceHours(double value) {
        this.plannedServiceHours = value;
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
