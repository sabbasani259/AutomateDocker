
package remote.wise.client.MachineServiceDueOverDueReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for machineServiceDueOverDueRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="machineServiceDueOverDueRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dealerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerTenancyId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineCountDueForService" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="machineCountOverdueForService" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "machineServiceDueOverDueRespContract", propOrder = {
    "dealerName",
    "dealerTenancyId",
    "machineCountDueForService",
    "machineCountOverdueForService"
})
public class MachineServiceDueOverDueRespContract {

    protected String dealerName;
    protected String dealerTenancyId;
    protected long machineCountDueForService;
    protected long machineCountOverdueForService;

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
     * Gets the value of the dealerTenancyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDealerTenancyId() {
        return dealerTenancyId;
    }

    /**
     * Sets the value of the dealerTenancyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDealerTenancyId(String value) {
        this.dealerTenancyId = value;
    }

    /**
     * Gets the value of the machineCountDueForService property.
     * 
     */
    public long getMachineCountDueForService() {
        return machineCountDueForService;
    }

    /**
     * Sets the value of the machineCountDueForService property.
     * 
     */
    public void setMachineCountDueForService(long value) {
        this.machineCountDueForService = value;
    }

    /**
     * Gets the value of the machineCountOverdueForService property.
     * 
     */
    public long getMachineCountOverdueForService() {
        return machineCountOverdueForService;
    }

    /**
     * Sets the value of the machineCountOverdueForService property.
     * 
     */
    public void setMachineCountOverdueForService(long value) {
        this.machineCountOverdueForService = value;
    }

}
