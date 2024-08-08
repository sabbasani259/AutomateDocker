
package remote.wise.client.AssetServiceScheduleService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetAssetServiceScheduleService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetAssetServiceScheduleService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="setAssetServiceSchedule" type="{http://webservice.service.wise.remote/}assetServiceScheduleRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetAssetServiceScheduleService", propOrder = {
    "setAssetServiceSchedule"
})
public class SetAssetServiceScheduleService {

    protected AssetServiceScheduleRespContract setAssetServiceSchedule;

    /**
     * Gets the value of the setAssetServiceSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link AssetServiceScheduleRespContract }
     *     
     */
    public AssetServiceScheduleRespContract getSetAssetServiceSchedule() {
        return setAssetServiceSchedule;
    }

    /**
     * Sets the value of the setAssetServiceSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssetServiceScheduleRespContract }
     *     
     */
    public void setSetAssetServiceSchedule(AssetServiceScheduleRespContract value) {
        this.setAssetServiceSchedule = value;
    }

}
