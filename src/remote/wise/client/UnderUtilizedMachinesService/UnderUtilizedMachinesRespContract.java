
package remote.wise.client.UnderUtilizedMachinesService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for underUtilizedMachinesRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="underUtilizedMachinesRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="engine_Off_Hours_Perct" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="serial_no" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="workingTimePercentage" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="working_Time" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "underUtilizedMachinesRespContract", propOrder = {
    "engineOffHoursPerct",
    "serialNo",
    "workingTimePercentage",
    "workingTime"
})
public class UnderUtilizedMachinesRespContract {

    @XmlElement(name = "engine_Off_Hours_Perct")
    protected double engineOffHoursPerct;
    @XmlElement(name = "serial_no")
    protected String serialNo;
    protected double workingTimePercentage;
    @XmlElement(name = "working_Time")
    protected double workingTime;

    /**
     * Gets the value of the engineOffHoursPerct property.
     * 
     */
    public double getEngineOffHoursPerct() {
        return engineOffHoursPerct;
    }

    /**
     * Sets the value of the engineOffHoursPerct property.
     * 
     */
    public void setEngineOffHoursPerct(double value) {
        this.engineOffHoursPerct = value;
    }

    /**
     * Gets the value of the serialNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * Sets the value of the serialNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNo(String value) {
        this.serialNo = value;
    }

    /**
     * Gets the value of the workingTimePercentage property.
     * 
     */
    public double getWorkingTimePercentage() {
        return workingTimePercentage;
    }

    /**
     * Sets the value of the workingTimePercentage property.
     * 
     */
    public void setWorkingTimePercentage(double value) {
        this.workingTimePercentage = value;
    }

    /**
     * Gets the value of the workingTime property.
     * 
     */
    public double getWorkingTime() {
        return workingTime;
    }

    /**
     * Sets the value of the workingTime property.
     * 
     */
    public void setWorkingTime(double value) {
        this.workingTime = value;
    }

}
