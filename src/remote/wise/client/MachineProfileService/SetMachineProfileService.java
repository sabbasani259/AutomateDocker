
package remote.wise.client.MachineProfileService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetMachineProfileService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetMachineProfileService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="respObj" type="{http://webservice.service.wise.remote/}machineProfileRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetMachineProfileService", propOrder = {
    "respObj"
})
public class SetMachineProfileService {

    protected MachineProfileRespContract respObj;

    /**
     * Gets the value of the respObj property.
     * 
     * @return
     *     possible object is
     *     {@link MachineProfileRespContract }
     *     
     */
    public MachineProfileRespContract getRespObj() {
        return respObj;
    }

    /**
     * Sets the value of the respObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link MachineProfileRespContract }
     *     
     */
    public void setRespObj(MachineProfileRespContract value) {
        this.respObj = value;
    }

}
