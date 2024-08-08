
package remote.wise.client.MachineBillingReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for machineBillingReportOutputContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="machineBillingReportOutputContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="actualMachineCount" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="billingCalculation" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="installDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="invoicedAmount" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="model" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="newRolledMachine" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="oldNew" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="oldSerialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="previousBilledCount" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="profile" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rollOffDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "machineBillingReportOutputContract", propOrder = {
    "actualMachineCount",
    "billingCalculation",
    "installDate",
    "invoicedAmount",
    "model",
    "newRolledMachine",
    "oldNew",
    "oldSerialNumber",
    "previousBilledCount",
    "profile",
    "rollOffDate",
    "serialNumber"
})
public class MachineBillingReportOutputContract {

    protected Long actualMachineCount;
    protected Long billingCalculation;
    protected String installDate;
    protected Long invoicedAmount;
    protected String model;
    protected Long newRolledMachine;
    protected String oldNew;
    protected String oldSerialNumber;
    protected Long previousBilledCount;
    protected String profile;
    protected String rollOffDate;
    protected String serialNumber;

    /**
     * Gets the value of the actualMachineCount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getActualMachineCount() {
        return actualMachineCount;
    }

    /**
     * Sets the value of the actualMachineCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setActualMachineCount(Long value) {
        this.actualMachineCount = value;
    }

    /**
     * Gets the value of the billingCalculation property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBillingCalculation() {
        return billingCalculation;
    }

    /**
     * Sets the value of the billingCalculation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBillingCalculation(Long value) {
        this.billingCalculation = value;
    }

    /**
     * Gets the value of the installDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstallDate() {
        return installDate;
    }

    /**
     * Sets the value of the installDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstallDate(String value) {
        this.installDate = value;
    }

    /**
     * Gets the value of the invoicedAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getInvoicedAmount() {
        return invoicedAmount;
    }

    /**
     * Sets the value of the invoicedAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setInvoicedAmount(Long value) {
        this.invoicedAmount = value;
    }

    /**
     * Gets the value of the model property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModel(String value) {
        this.model = value;
    }

    /**
     * Gets the value of the newRolledMachine property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNewRolledMachine() {
        return newRolledMachine;
    }

    /**
     * Sets the value of the newRolledMachine property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNewRolledMachine(Long value) {
        this.newRolledMachine = value;
    }

    /**
     * Gets the value of the oldNew property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldNew() {
        return oldNew;
    }

    /**
     * Sets the value of the oldNew property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldNew(String value) {
        this.oldNew = value;
    }

    /**
     * Gets the value of the oldSerialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldSerialNumber() {
        return oldSerialNumber;
    }

    /**
     * Sets the value of the oldSerialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldSerialNumber(String value) {
        this.oldSerialNumber = value;
    }

    /**
     * Gets the value of the previousBilledCount property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPreviousBilledCount() {
        return previousBilledCount;
    }

    /**
     * Sets the value of the previousBilledCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPreviousBilledCount(Long value) {
        this.previousBilledCount = value;
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
     * Gets the value of the rollOffDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRollOffDate() {
        return rollOffDate;
    }

    /**
     * Sets the value of the rollOffDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRollOffDate(String value) {
        this.rollOffDate = value;
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
