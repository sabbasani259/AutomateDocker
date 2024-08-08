
package remote.wise.client.DetailMachineInfoService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for detailMachineInfoRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="detailMachineInfoRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="current_Owner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FW_Version_Number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="last_Reported" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineHour" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rollOff_Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "detailMachineInfoRespContract", propOrder = {
    "currentOwner",
    "fwVersionNumber",
    "lastReported",
    "machineHour",
    "rollOffDate",
    "serialNumber"
})
public class DetailMachineInfoRespContract {

    @XmlElement(name = "current_Owner")
    protected String currentOwner;
    @XmlElement(name = "FW_Version_Number")
    protected String fwVersionNumber;
    @XmlElement(name = "last_Reported")
    protected String lastReported;
    protected String machineHour;
    @XmlElement(name = "rollOff_Date")
    protected String rollOffDate;
    protected String serialNumber;

    /**
     * Gets the value of the currentOwner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentOwner() {
        return currentOwner;
    }

    /**
     * Sets the value of the currentOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentOwner(String value) {
        this.currentOwner = value;
    }

    /**
     * Gets the value of the fwVersionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFWVersionNumber() {
        return fwVersionNumber;
    }

    /**
     * Sets the value of the fwVersionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFWVersionNumber(String value) {
        this.fwVersionNumber = value;
    }

    /**
     * Gets the value of the lastReported property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastReported() {
        return lastReported;
    }

    /**
     * Sets the value of the lastReported property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastReported(String value) {
        this.lastReported = value;
    }

    /**
     * Gets the value of the machineHour property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineHour() {
        return machineHour;
    }

    /**
     * Sets the value of the machineHour property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineHour(String value) {
        this.machineHour = value;
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
