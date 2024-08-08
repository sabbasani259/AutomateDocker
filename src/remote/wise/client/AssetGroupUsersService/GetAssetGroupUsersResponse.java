
package remote.wise.client.AssetGroupUsersService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetAssetGroupUsersResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetAssetGroupUsersResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://webservice.service.wise.remote/}assetGroupUsersRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetAssetGroupUsersResponse", propOrder = {
    "_return"
})
public class GetAssetGroupUsersResponse {

    @XmlElement(name = "return")
    protected AssetGroupUsersRespContract _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link AssetGroupUsersRespContract }
     *     
     */
    public AssetGroupUsersRespContract getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetGroupUsersRespContract }
     *     
     */
    public void setReturn(AssetGroupUsersRespContract value) {
        this._return = value;
    }

}
