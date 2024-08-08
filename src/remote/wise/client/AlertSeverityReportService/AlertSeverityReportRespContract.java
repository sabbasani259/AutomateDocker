
package remote.wise.client.AlertSeverityReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for alertSeverityReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="alertSeverityReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alertDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="alertSeverity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="customerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="latestReceivedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "alertSeverityReportRespContract", propOrder = {
    "alertDescription",
    "alertSeverity",
    "customerName",
    "dealerName",
    "latestReceivedTime",
    "serialNumber",
    "zone",
    "profile",
    "model",
    "machineHrs",
    "PKTReceivedDate",
    "machineRollOffDate",
    "machineInstallationDate",
    "customerNumber"
})
public class AlertSeverityReportRespContract {

    protected String alertDescription;
    protected String alertSeverity;
    protected String customerName;
    protected String dealerName;
    protected String latestReceivedTime;
    protected String serialNumber;
    protected String zone;
    protected String profile;
    protected String model;
    protected String machineHrs;
    protected String pktReceivedDate;
    protected String machineRollOffDate;
    protected String machineInstallationDate;
    protected String customerNumber;

    
  /**
     * Gets the value of the customerNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerNumber() {
		return customerNumber;
	}
    /**
     * Sets the value of the customerNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	/**
     * Gets the value of the machineRollOffDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineRollOffDate() {
		return machineRollOffDate;
	}
    /**
     * Sets the value of the machineRollOffDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setMachineRollOffDate(String machineRollOffDate) {
		this.machineRollOffDate = machineRollOffDate;
	}
	/**
     * Gets the value of the machineInstallationDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    
    public String getMachineInstallationDate() {
		return machineInstallationDate;
	}
    /**
     * Sets the value of the machineInstallationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setMachineInstallationDate(String machineInstallationDate) {
		this.machineInstallationDate = machineInstallationDate;
	}
	/**
     * Gets the value of the pktReceivedDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
	public String getPktReceivedDate() {
		return pktReceivedDate;
	}
	/**
     * Sets the value of the pktReceivedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setPktReceivedDate(String pktReceivedDate) {
		this.pktReceivedDate = pktReceivedDate;
	}
	/**
     * Gets the value of the machineHRS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineHrs() {
		return machineHrs;
	}
    /**
     * Sets the value of the MachineHRS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setMachineHrs(String machineHrs) {
		this.machineHrs = machineHrs;
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
	public void setModel(String model) {
		this.model = model;
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
	public void setProfile(String profile) {
		this.profile = profile;
	}
	/**
     * Gets the value of the zone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    

    public String getZone() {
		return zone;
	}
    /**
     * Sets the value of the zone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
	public void setZone(String zone) {
		this.zone = zone;
	}

	/**
     * Gets the value of the alertDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlertDescription() {
        return alertDescription;
    }

    /**
     * Sets the value of the alertDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlertDescription(String value) {
        this.alertDescription = value;
    }

    /**
     * Gets the value of the alertSeverity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlertSeverity() {
        return alertSeverity;
    }

    /**
     * Sets the value of the alertSeverity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlertSeverity(String value) {
        this.alertSeverity = value;
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
     * Gets the value of the latestReceivedTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLatestReceivedTime() {
        return latestReceivedTime;
    }

    /**
     * Sets the value of the latestReceivedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLatestReceivedTime(String value) {
        this.latestReceivedTime = value;
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
