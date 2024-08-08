
package remote.wise.client.AlertThresholdSettingsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for alertThresholdRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="alertThresholdRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eventName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="eventTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="eventTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="redThreshold" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="redThresholdVal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="yellowThreshold" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="yellowThresholdVal" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "alertThresholdRespContract", propOrder = {
    "eventId",
    "eventName",
    "eventTypeId",
    "eventTypeName",
    "redThreshold",
    "redThresholdVal",
    "yellowThreshold",
    "yellowThresholdVal"
})
public class AlertThresholdRespContract {

    protected int eventId;
    protected String eventName;
    protected int eventTypeId;
    protected String eventTypeName;
    protected boolean redThreshold;
    protected int redThresholdVal;
    protected boolean yellowThreshold;
    protected int yellowThresholdVal;

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
     * Gets the value of the eventName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the value of the eventName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventName(String value) {
        this.eventName = value;
    }

    /**
     * Gets the value of the eventTypeId property.
     * 
     */
    public int getEventTypeId() {
        return eventTypeId;
    }

    /**
     * Sets the value of the eventTypeId property.
     * 
     */
    public void setEventTypeId(int value) {
        this.eventTypeId = value;
    }

    /**
     * Gets the value of the eventTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventTypeName() {
        return eventTypeName;
    }

    /**
     * Sets the value of the eventTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventTypeName(String value) {
        this.eventTypeName = value;
    }

    /**
     * Gets the value of the redThreshold property.
     * 
     */
    public boolean isRedThreshold() {
        return redThreshold;
    }

    /**
     * Sets the value of the redThreshold property.
     * 
     */
    public void setRedThreshold(boolean value) {
        this.redThreshold = value;
    }

    /**
     * Gets the value of the redThresholdVal property.
     * 
     */
    public int getRedThresholdVal() {
        return redThresholdVal;
    }

    /**
     * Sets the value of the redThresholdVal property.
     * 
     */
    public void setRedThresholdVal(int value) {
        this.redThresholdVal = value;
    }

    /**
     * Gets the value of the yellowThreshold property.
     * 
     */
    public boolean isYellowThreshold() {
        return yellowThreshold;
    }

    /**
     * Sets the value of the yellowThreshold property.
     * 
     */
    public void setYellowThreshold(boolean value) {
        this.yellowThreshold = value;
    }

    /**
     * Gets the value of the yellowThresholdVal property.
     * 
     */
    public int getYellowThresholdVal() {
        return yellowThresholdVal;
    }

    /**
     * Sets the value of the yellowThresholdVal property.
     * 
     */
    public void setYellowThresholdVal(int value) {
        this.yellowThresholdVal = value;
    }

}
