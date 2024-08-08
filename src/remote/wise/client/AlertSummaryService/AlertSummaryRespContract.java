
package remote.wise.client.AlertSummaryService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for alertSummaryRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="alertSummaryRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="redThresholdValue" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="yellowThresholdValue" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "alertSummaryRespContract", propOrder = {
    "redThresholdValue",
    "yellowThresholdValue"
})
public class AlertSummaryRespContract {

    protected int redThresholdValue;
    protected int yellowThresholdValue;

    /**
     * Gets the value of the redThresholdValue property.
     * 
     */
    public int getRedThresholdValue() {
        return redThresholdValue;
    }

    /**
     * Sets the value of the redThresholdValue property.
     * 
     */
    public void setRedThresholdValue(int value) {
        this.redThresholdValue = value;
    }

    /**
     * Gets the value of the yellowThresholdValue property.
     * 
     */
    public int getYellowThresholdValue() {
        return yellowThresholdValue;
    }

    /**
     * Sets the value of the yellowThresholdValue property.
     * 
     */
    public void setYellowThresholdValue(int value) {
        this.yellowThresholdValue = value;
    }

}
