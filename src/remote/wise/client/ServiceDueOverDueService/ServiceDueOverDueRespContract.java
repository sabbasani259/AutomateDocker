
package remote.wise.client.ServiceDueOverDueService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for serviceDueOverDueRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="serviceDueOverDueRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serviceDueCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="serviceOverDueCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceDueOverDueRespContract", propOrder = {
    "serviceDueCount",
    "serviceOverDueCount"
})
public class ServiceDueOverDueRespContract {

    protected int serviceDueCount;
    protected int serviceOverDueCount;

    /**
     * Gets the value of the serviceDueCount property.
     * 
     */
    public int getServiceDueCount() {
        return serviceDueCount;
    }

    /**
     * Sets the value of the serviceDueCount property.
     * 
     */
    public void setServiceDueCount(int value) {
        this.serviceDueCount = value;
    }

    /**
     * Gets the value of the serviceOverDueCount property.
     * 
     */
    public int getServiceOverDueCount() {
        return serviceOverDueCount;
    }

    /**
     * Sets the value of the serviceOverDueCount property.
     * 
     */
    public void setServiceOverDueCount(int value) {
        this.serviceOverDueCount = value;
    }

}
