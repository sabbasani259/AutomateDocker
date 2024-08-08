
package remote.wise.client.DetailMachineInfoService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDetailMachineInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDetailMachineInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}detailMachineInfoReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDetailMachineInfo", propOrder = {
    "reqObj"
})
public class GetDetailMachineInfo {

    protected DetailMachineInfoReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link DetailMachineInfoReqContract }
     *     
     */
    public DetailMachineInfoReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link DetailMachineInfoReqContract }
     *     
     */
    public void setReqObj(DetailMachineInfoReqContract value) {
        this.reqObj = value;
    }

}
