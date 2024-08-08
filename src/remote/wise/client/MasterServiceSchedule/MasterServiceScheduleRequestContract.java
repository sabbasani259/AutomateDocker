
package remote.wise.client.MasterServiceSchedule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for masterServiceScheduleRequestContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="masterServiceScheduleRequestContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="engineTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="scheduleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "masterServiceScheduleRequestContract", propOrder = {
    "assetGroupId",
    "assetTypeId",
    "engineTypeId",
    "scheduleName"
})
public class MasterServiceScheduleRequestContract {

    protected int assetGroupId;
    protected int assetTypeId;
    protected int engineTypeId;
    protected String scheduleName;

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
     * Gets the value of the assetTypeId property.
     * 
     */
    public int getAssetTypeId() {
        return assetTypeId;
    }

    /**
     * Sets the value of the assetTypeId property.
     * 
     */
    public void setAssetTypeId(int value) {
        this.assetTypeId = value;
    }

    /**
     * Gets the value of the engineTypeId property.
     * 
     */
    public int getEngineTypeId() {
        return engineTypeId;
    }

    /**
     * Sets the value of the engineTypeId property.
     * 
     */
    public void setEngineTypeId(int value) {
        this.engineTypeId = value;
    }

    /**
     * Gets the value of the scheduleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScheduleName() {
        return scheduleName;
    }

    /**
     * Sets the value of the scheduleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScheduleName(String value) {
        this.scheduleName = value;
    }

}
