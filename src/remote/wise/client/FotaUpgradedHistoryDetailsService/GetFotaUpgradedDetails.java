
package remote.wise.client.FotaUpgradedHistoryDetailsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getFotaUpgradedDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getFotaUpgradedDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObj" type="{http://webservice.service.wise.remote/}fotaUpgradedHistoryDetailsReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getFotaUpgradedDetails", propOrder = {
    "reqObj"
})
public class GetFotaUpgradedDetails {

    protected FotaUpgradedHistoryDetailsReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link FotaUpgradedHistoryDetailsReqContract }
     *     
     */
    public FotaUpgradedHistoryDetailsReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link FotaUpgradedHistoryDetailsReqContract }
     *     
     */
    public void setReqObj(FotaUpgradedHistoryDetailsReqContract value) {
        this.reqObj = value;
    }

}
