
package remote.wise.client.LandmarkAssetService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetLandmarkAsset complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetLandmarkAsset">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="setLandmarkAsset" type="{http://webservice.service.wise.remote/}landmarkAssetRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetLandmarkAsset", propOrder = {
    "setLandmarkAsset"
})
public class SetLandmarkAsset {

    protected LandmarkAssetRespContract setLandmarkAsset;

    /**
     * Gets the value of the setLandmarkAsset property.
     * 
     * @return
     *     possible object is
     *     {@link LandmarkAssetRespContract }
     *     
     */
    public LandmarkAssetRespContract getSetLandmarkAsset() {
        return setLandmarkAsset;
    }

    /**
     * Sets the value of the setLandmarkAsset property.
     * 
     * @param value
     *     allowed object is
     *     {@link LandmarkAssetRespContract }
     *     
     */
    public void setSetLandmarkAsset(LandmarkAssetRespContract value) {
        this.setLandmarkAsset = value;
    }

}
