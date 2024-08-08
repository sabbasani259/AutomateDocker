
package remote.wise.client.UtilizationDetailReportService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for utilizationDetailReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="utilizationDetailReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dateInString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dayInString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="daysWithNoEngineRun" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="engineOffDuration" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="engineRunDuration" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="engineWorkingDuration" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="machineUtilizationPerct" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="nickName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeMachineStatusMap">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "utilizationDetailReportRespContract", propOrder = {
    "dateInString",
    "dayInString",
    "daysWithNoEngineRun",
    "engineOffDuration",
    "engineRunDuration",
    "engineWorkingDuration",
    "machineUtilizationPerct",
    "nickName",
    "serialNumber",
    "timeMachineStatusMap"
})
public class UtilizationDetailReportRespContract {

    protected String dateInString;
    protected String dayInString;
    protected int daysWithNoEngineRun;
    protected double engineOffDuration;
    protected double engineRunDuration;
    protected double engineWorkingDuration;
    protected double machineUtilizationPerct;
    protected String nickName;
    protected String serialNumber;
    @XmlElement(required = true)
    protected UtilizationDetailReportRespContract.TimeMachineStatusMap timeMachineStatusMap;

    /**
     * Gets the value of the dateInString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateInString() {
        return dateInString;
    }

    /**
     * Sets the value of the dateInString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateInString(String value) {
        this.dateInString = value;
    }

    /**
     * Gets the value of the dayInString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDayInString() {
        return dayInString;
    }

    /**
     * Sets the value of the dayInString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDayInString(String value) {
        this.dayInString = value;
    }

    /**
     * Gets the value of the daysWithNoEngineRun property.
     * 
     */
    public int getDaysWithNoEngineRun() {
        return daysWithNoEngineRun;
    }

    /**
     * Sets the value of the daysWithNoEngineRun property.
     * 
     */
    public void setDaysWithNoEngineRun(int value) {
        this.daysWithNoEngineRun = value;
    }

    /**
     * Gets the value of the engineOffDuration property.
     * 
     */
    public double getEngineOffDuration() {
        return engineOffDuration;
    }

    /**
     * Sets the value of the engineOffDuration property.
     * 
     */
    public void setEngineOffDuration(double value) {
        this.engineOffDuration = value;
    }

    /**
     * Gets the value of the engineRunDuration property.
     * 
     */
    public double getEngineRunDuration() {
        return engineRunDuration;
    }

    /**
     * Sets the value of the engineRunDuration property.
     * 
     */
    public void setEngineRunDuration(double value) {
        this.engineRunDuration = value;
    }

    /**
     * Gets the value of the engineWorkingDuration property.
     * 
     */
    public double getEngineWorkingDuration() {
        return engineWorkingDuration;
    }

    /**
     * Sets the value of the engineWorkingDuration property.
     * 
     */
    public void setEngineWorkingDuration(double value) {
        this.engineWorkingDuration = value;
    }

    /**
     * Gets the value of the machineUtilizationPerct property.
     * 
     */
    public double getMachineUtilizationPerct() {
        return machineUtilizationPerct;
    }

    /**
     * Sets the value of the machineUtilizationPerct property.
     * 
     */
    public void setMachineUtilizationPerct(double value) {
        this.machineUtilizationPerct = value;
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
     * Gets the value of the timeMachineStatusMap property.
     * 
     * @return
     *     possible object is
     *     {@link UtilizationDetailReportRespContract.TimeMachineStatusMap }
     *     
     */
    public UtilizationDetailReportRespContract.TimeMachineStatusMap getTimeMachineStatusMap() {
        return timeMachineStatusMap;
    }

    /**
     * Sets the value of the timeMachineStatusMap property.
     * 
     * @param value
     *     allowed object is
     *     {@link UtilizationDetailReportRespContract.TimeMachineStatusMap }
     *     
     */
    public void setTimeMachineStatusMap(UtilizationDetailReportRespContract.TimeMachineStatusMap value) {
        this.timeMachineStatusMap = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "entry"
    })
    public static class TimeMachineStatusMap {

        protected List<UtilizationDetailReportRespContract.TimeMachineStatusMap.Entry> entry;

        /**
         * Gets the value of the entry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEntry().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link UtilizationDetailReportRespContract.TimeMachineStatusMap.Entry }
         * 
         * 
         */
        public List<UtilizationDetailReportRespContract.TimeMachineStatusMap.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<UtilizationDetailReportRespContract.TimeMachineStatusMap.Entry>();
            }
            return this.entry;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "key",
            "value"
        })
        public static class Entry {

            protected String key;
            protected String value;

            /**
             * Gets the value of the key property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKey() {
                return key;
            }

            /**
             * Sets the value of the key property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKey(String value) {
                this.key = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

        }

    }

}
