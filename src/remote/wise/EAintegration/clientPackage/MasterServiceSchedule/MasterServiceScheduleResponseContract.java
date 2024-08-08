
package remote.wise.EAintegration.clientPackage.MasterServiceSchedule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for masterServiceScheduleResponseContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="masterServiceScheduleResponseContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="asset_group_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="asset_type_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dbmsPartCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="durationSchedule" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="engineHoursSchedule" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="engineTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="engineTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="scheduleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "masterServiceScheduleResponseContract", propOrder = {
    "assetGroupId",
    "assetTypeId",
    "assetGroupName",
    "assetTypeName",
    "dbmsPartCode",
    "durationSchedule",
    "engineHoursSchedule",
    "engineTypeId",
    "engineTypeName",
    "scheduleName",
    "serviceName"
})
public class MasterServiceScheduleResponseContract {

    protected int assetGroupId;
    protected int assetTypeId;
    @XmlElement(name = "asset_group_name")
    protected String assetGroupName;
    @XmlElement(name = "asset_type_name")
    protected String assetTypeName;
    protected String dbmsPartCode;
    protected int durationSchedule;
    protected long engineHoursSchedule;
    protected int engineTypeId;
    protected String engineTypeName;
    protected String scheduleName;
    protected String serviceName;

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
     * Gets the value of the assetGroupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetGroupName() {
        return assetGroupName;
    }

    /**
     * Sets the value of the assetGroupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetGroupName(String value) {
        this.assetGroupName = value;
    }

    /**
     * Gets the value of the assetTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetTypeName() {
        return assetTypeName;
    }

    /**
     * Sets the value of the assetTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetTypeName(String value) {
        this.assetTypeName = value;
    }

    /**
     * Gets the value of the dbmsPartCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDbmsPartCode() {
        return dbmsPartCode;
    }

    /**
     * Sets the value of the dbmsPartCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDbmsPartCode(String value) {
        this.dbmsPartCode = value;
    }

    /**
     * Gets the value of the durationSchedule property.
     * 
     */
    public int getDurationSchedule() {
        return durationSchedule;
    }

    /**
     * Sets the value of the durationSchedule property.
     * 
     */
    public void setDurationSchedule(int value) {
        this.durationSchedule = value;
    }

    /**
     * Gets the value of the engineHoursSchedule property.
     * 
     */
    public long getEngineHoursSchedule() {
        return engineHoursSchedule;
    }

    /**
     * Sets the value of the engineHoursSchedule property.
     * 
     */
    public void setEngineHoursSchedule(long value) {
        this.engineHoursSchedule = value;
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
     * Gets the value of the engineTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEngineTypeName() {
        return engineTypeName;
    }

    /**
     * Sets the value of the engineTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEngineTypeName(String value) {
        this.engineTypeName = value;
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

    /**
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceName(String value) {
        this.serviceName = value;
    }

}
