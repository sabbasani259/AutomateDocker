
package remote.wise.client.FleetSummaryService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fleetSummaryRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fleetSummaryRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="totalIdleHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="totalOffHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="totalWorkingHours" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fleetSummaryRespContract", propOrder = {
    "totalIdleHours",
    "totalOffHours",
    "totalWorkingHours"
})
public class FleetSummaryRespContract {

    protected double totalIdleHours;
    protected double totalOffHours;
    protected double totalWorkingHours;

    /**
     * Gets the value of the totalIdleHours property.
     * 
     */
    public double getTotalIdleHours() {
        return totalIdleHours;
    }

    /**
     * Sets the value of the totalIdleHours property.
     * 
     */
    public void setTotalIdleHours(double value) {
        this.totalIdleHours = value;
    }

    /**
     * Gets the value of the totalOffHours property.
     * 
     */
    public double getTotalOffHours() {
        return totalOffHours;
    }

    /**
     * Sets the value of the totalOffHours property.
     * 
     */
    public void setTotalOffHours(double value) {
        this.totalOffHours = value;
    }

    /**
     * Gets the value of the totalWorkingHours property.
     * 
     */
    public double getTotalWorkingHours() {
        return totalWorkingHours;
    }

    /**
     * Sets the value of the totalWorkingHours property.
     * 
     */
    public void setTotalWorkingHours(double value) {
        this.totalWorkingHours = value;
    }

}
