
package remote.wise.client.ServiceHistoryDetailsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetServiceHistoryDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetServiceHistoryDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="setServiceSchedule" type="{http://webservice.service.wise.remote/}serviceScheduleRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetServiceHistoryDetails", propOrder = {
    "setServiceSchedule"
})
public class SetServiceHistoryDetails {

    protected ServiceScheduleRespContract setServiceSchedule;

    /**
     * Gets the value of the setServiceSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceScheduleRespContract }
     *     
     */
    public ServiceScheduleRespContract getSetServiceSchedule() {
        return setServiceSchedule;
    }

    /**
     * Sets the value of the setServiceSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceScheduleRespContract }
     *     
     */
    public void setSetServiceSchedule(ServiceScheduleRespContract value) {
        this.setServiceSchedule = value;
    }

}
