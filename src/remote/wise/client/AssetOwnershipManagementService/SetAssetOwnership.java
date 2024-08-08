
package remote.wise.client.AssetOwnershipManagementService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetAssetOwnership complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetAssetOwnership">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObject" type="{http://webservice.service.wise.remote/}assetOwnershipRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetAssetOwnership", propOrder = {
    "reqObject"
})
public class SetAssetOwnership {

    protected AssetOwnershipRespContract reqObject;

    /**
     * Gets the value of the reqObject property.
     * 
     * @return
     *     possible object is
     *     {@link AssetOwnershipRespContract }
     *     
     */
    public AssetOwnershipRespContract getReqObject() {
        return reqObject;
    }

    /**
     * Sets the value of the reqObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetOwnershipRespContract }
     *     
     */
    public void setReqObject(AssetOwnershipRespContract value) {
        this.reqObject = value;
    }

}
