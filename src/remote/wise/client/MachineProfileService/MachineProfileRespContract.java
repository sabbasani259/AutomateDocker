
package remote.wise.client.MachineProfileService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for machineProfileRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="machineProfileRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="asseetOperatingEndTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="asseetOperatingStartTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="assetGroupCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="assetGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="profileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "machineProfileRespContract", propOrder = {
    "asseetOperatingEndTime",
    "asseetOperatingStartTime",
    "assetGroupCode",
    "assetGroupId",
    "profileName"
})
public class MachineProfileRespContract {

    protected String asseetOperatingEndTime;
    protected String asseetOperatingStartTime;
    protected String assetGroupCode;
    protected int assetGroupId;
    protected String profileName;

    /**
     * Gets the value of the asseetOperatingEndTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAsseetOperatingEndTime() {
        return asseetOperatingEndTime;
    }

    /**
     * Sets the value of the asseetOperatingEndTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAsseetOperatingEndTime(String value) {
        this.asseetOperatingEndTime = value;
    }

    /**
     * Gets the value of the asseetOperatingStartTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAsseetOperatingStartTime() {
        return asseetOperatingStartTime;
    }

    /**
     * Sets the value of the asseetOperatingStartTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAsseetOperatingStartTime(String value) {
        this.asseetOperatingStartTime = value;
    }

    /**
     * Gets the value of the assetGroupCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    /**
     * Sets the value of the assetGroupCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetGroupCode(String value) {
        this.assetGroupCode = value;
    }

    /**
     * Gets the value of the assetGroupId property.
     * 
     */
    public int getAssetGroupId() {
        return assetGroupId;
    }

    /**
     * Sets the value of the assetGroupId property.
     * 
     */
    public void setAssetGroupId(int value) {
        this.assetGroupId = value;
    }

    /**
     * Gets the value of the profileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * Sets the value of the profileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfileName(String value) {
        this.profileName = value;
    }

}
