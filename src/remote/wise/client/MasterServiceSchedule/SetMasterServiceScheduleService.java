
package remote.wise.client.MasterServiceSchedule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetMasterServiceScheduleService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetMasterServiceScheduleService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="setServiceSchedule" type="{http://webservice.service.wise.remote/}masterServiceScheduleResponseContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetMasterServiceScheduleService", propOrder = {
    "setServiceSchedule"
})
public class SetMasterServiceScheduleService {

    protected MasterServiceScheduleResponseContract setServiceSchedule;

    /**
     * Gets the value of the setServiceSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link MasterServiceScheduleResponseContract }
     *     
     */
    public MasterServiceScheduleResponseContract getSetServiceSchedule() {
        return setServiceSchedule;
    }

    /**
     * Sets the value of the setServiceSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link MasterServiceScheduleResponseContract }
     *     
     */
    public void setSetServiceSchedule(MasterServiceScheduleResponseContract value) {
        this.setServiceSchedule = value;
    }

}
