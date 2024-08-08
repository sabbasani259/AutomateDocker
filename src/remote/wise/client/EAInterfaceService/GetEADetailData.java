
package remote.wise.client.EAInterfaceService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetEADetailData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetEADetailData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}eaInterfaceDetailReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetEADetailData", propOrder = {
    "reqObj"
})
public class GetEADetailData {

    protected EaInterfaceDetailReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link EaInterfaceDetailReqContract }
     *     
     */
    public EaInterfaceDetailReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link EaInterfaceDetailReqContract }
     *     
     */
    public void setReqObj(EaInterfaceDetailReqContract value) {
        this.reqObj = value;
    }

}
