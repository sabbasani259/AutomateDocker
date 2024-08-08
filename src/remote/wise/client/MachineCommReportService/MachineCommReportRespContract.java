
package remote.wise.client.MachineCommReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for machineCommReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="machineCommReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerAccountId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dealerCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fwVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineHours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ownerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pktCreatedTimestamp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pktReceivedDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pktReceivedTimestamp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rolledOffDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "machineCommReportRespContract", propOrder = {
    "city",
    "dealerAccountId",
    "dealerCode",
    "dealerName",
    "fwVersion",
    "machineHours",
    "ownerName",
    "pktCreatedTimestamp",
    "pktReceivedDate",
    "pktReceivedTimestamp",
    "rolledOffDate",
    "serialNumber",
    "state"
})
public class MachineCommReportRespContract {

    protected String city;
    protected int dealerAccountId;
    protected String dealerCode;
    protected String dealerName;
    protected String fwVersion;
    protected String machineHours;
    protected String ownerName;
    protected String pktCreatedTimestamp;
    protected String pktReceivedDate;
    protected String pktReceivedTimestamp;
    protected String rolledOffDate;
    protected String serialNumber;
    protected String state;

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the dealerAccountId property.
     * 
     */
    public int getDealerAccountId() {
        return dealerAccountId;
    }

    /**
     * Sets the value of the dealerAccountId property.
     * 
     */
    public void setDealerAccountId(int value) {
        this.dealerAccountId = value;
    }

    /**
     * Gets the value of the dealerCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDealerCode() {
        return dealerCode;
    }

    /**
     * Sets the value of the dealerCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDealerCode(String value) {
        this.dealerCode = value;
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
     * Gets the value of the fwVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFwVersion() {
        return fwVersion;
    }

    /**
     * Sets the value of the fwVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFwVersion(String value) {
        this.fwVersion = value;
    }

    /**
     * Gets the value of the machineHours property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineHours() {
        return machineHours;
    }

    /**
     * Sets the value of the machineHours property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineHours(String value) {
        this.machineHours = value;
    }

    /**
     * Gets the value of the ownerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Sets the value of the ownerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnerName(String value) {
        this.ownerName = value;
    }

    /**
     * Gets the value of the pktCreatedTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPktCreatedTimestamp() {
        return pktCreatedTimestamp;
    }

    /**
     * Sets the value of the pktCreatedTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPktCreatedTimestamp(String value) {
        this.pktCreatedTimestamp = value;
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
    public void setPktReceivedDate(String value) {
        this.pktReceivedDate = value;
    }

    /**
     * Gets the value of the pktReceivedTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPktReceivedTimestamp() {
        return pktReceivedTimestamp;
    }

    /**
     * Sets the value of the pktReceivedTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPktReceivedTimestamp(String value) {
        this.pktReceivedTimestamp = value;
    }

    /**
     * Gets the value of the rolledOffDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRolledOffDate() {
        return rolledOffDate;
    }

    /**
     * Sets the value of the rolledOffDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRolledOffDate(String value) {
        this.rolledOffDate = value;
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
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

}
