
package remote.wise.client.AssetOwnershipManagementService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetAssetOwnership complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetAssetOwnership">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqObject" type="{http://webservice.service.wise.remote/}assetOwnershipReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetAssetOwnership", propOrder = {
    "reqObject"
})
public class GetAssetOwnership {

    protected AssetOwnershipReqContract reqObject;

    /**
     * Gets the value of the reqObject property.
     * 
     * @return
     *     possible object is
     *     {@link AssetOwnershipReqContract }
     *     
     */
    public AssetOwnershipReqContract getReqObject() {
        return reqObject;
    }

    /**
     * Sets the value of the reqObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetOwnershipReqContract }
     *     
     */
    public void setReqObject(AssetOwnershipReqContract value) {
        this.reqObject = value;
    }

}
