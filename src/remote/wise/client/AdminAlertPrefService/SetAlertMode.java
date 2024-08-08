
package remote.wise.client.AdminAlertPrefService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetAlertMode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetAlertMode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}adminAlertPrefRespContract" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetAlertMode", propOrder = {
    "reqObj"
})
public class SetAlertMode {

    protected List<AdminAlertPrefRespContract> reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reqObj property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReqObj().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdminAlertPrefRespContract }
     * 
     * 
     */
    public List<AdminAlertPrefRespContract> getReqObj() {
        if (reqObj == null) {
            reqObj = new ArrayList<AdminAlertPrefRespContract>();
        }
        return this.reqObj;
    }

}
