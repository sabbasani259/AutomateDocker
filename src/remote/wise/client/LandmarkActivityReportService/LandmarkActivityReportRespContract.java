
package remote.wise.client.LandmarkActivityReportService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for landmarkActivityReportRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="landmarkActivityReportRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="landMarkCategoryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="landMarkName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="landmarkCategoryId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="landmarkId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="longestDurationAtLandmarkInMinutes" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="machineGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nickname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numberOfArrivals" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="numberOfdepartures" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalDurationAtLandmarkInMinutes" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "landmarkActivityReportRespContract", propOrder = {
    "landMarkCategoryName",
    "landMarkName",
    "landmarkCategoryId",
    "landmarkId",
    "longestDurationAtLandmarkInMinutes",
    "machineGroupId",
    "machineGroupName",
    "nickname",
    "numberOfArrivals",
    "numberOfdepartures",
    "serialNumber",
    "totalDurationAtLandmarkInMinutes"
})
public class LandmarkActivityReportRespContract {

    protected String landMarkCategoryName;
    protected String landMarkName;
    protected int landmarkCategoryId;
    protected int landmarkId;
    protected long longestDurationAtLandmarkInMinutes;
    protected int machineGroupId;
    protected String machineGroupName;
    protected String nickname;
    protected int numberOfArrivals;
    protected int numberOfdepartures;
    protected String serialNumber;
    protected long totalDurationAtLandmarkInMinutes;

    /**
     * Gets the value of the landMarkCategoryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLandMarkCategoryName() {
        return landMarkCategoryName;
    }

    /**
     * Sets the value of the landMarkCategoryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLandMarkCategoryName(String value) {
        this.landMarkCategoryName = value;
    }

    /**
     * Gets the value of the landMarkName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLandMarkName() {
        return landMarkName;
    }

    /**
     * Sets the value of the landMarkName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLandMarkName(String value) {
        this.landMarkName = value;
    }

    /**
     * Gets the value of the landmarkCategoryId property.
     * 
     */
    public int getLandmarkCategoryId() {
        return landmarkCategoryId;
    }

    /**
     * Sets the value of the landmarkCategoryId property.
     * 
     */
    public void setLandmarkCategoryId(int value) {
        this.landmarkCategoryId = value;
    }

    /**
     * Gets the value of the landmarkId property.
     * 
     */
    public int getLandmarkId() {
        return landmarkId;
    }

    /**
     * Sets the value of the landmarkId property.
     * 
     */
    public void setLandmarkId(int value) {
        this.landmarkId = value;
    }

    /**
     * Gets the value of the longestDurationAtLandmarkInMinutes property.
     * 
     */
    public long getLongestDurationAtLandmarkInMinutes() {
        return longestDurationAtLandmarkInMinutes;
    }

    /**
     * Sets the value of the longestDurationAtLandmarkInMinutes property.
     * 
     */
    public void setLongestDurationAtLandmarkInMinutes(long value) {
        this.longestDurationAtLandmarkInMinutes = value;
    }

    /**
     * Gets the value of the machineGroupId property.
     * 
     */
    public int getMachineGroupId() {
        return machineGroupId;
    }

    /**
     * Sets the value of the machineGroupId property.
     * 
     */
    public void setMachineGroupId(int value) {
        this.machineGroupId = value;
    }

    /**
     * Gets the value of the machineGroupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineGroupName() {
        return machineGroupName;
    }

    /**
     * Sets the value of the machineGroupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineGroupName(String value) {
        this.machineGroupName = value;
    }

    /**
     * Gets the value of the nickname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the value of the nickname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNickname(String value) {
        this.nickname = value;
    }

    /**
     * Gets the value of the numberOfArrivals property.
     * 
     */
    public int getNumberOfArrivals() {
        return numberOfArrivals;
    }

    /**
     * Sets the value of the numberOfArrivals property.
     * 
     */
    public void setNumberOfArrivals(int value) {
        this.numberOfArrivals = value;
    }

    /**
     * Gets the value of the numberOfdepartures property.
     * 
     */
    public int getNumberOfdepartures() {
        return numberOfdepartures;
    }

    /**
     * Sets the value of the numberOfdepartures property.
     * 
     */
    public void setNumberOfdepartures(int value) {
        this.numberOfdepartures = value;
    }

    /**
     * Gets the value of the serialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the value of the serialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNumber(String value) {
        this.serialNumber = value;
    }

    /**
     * Gets the value of the totalDurationAtLandmarkInMinutes property.
     * 
     */
    public long getTotalDurationAtLandmarkInMinutes() {
        return totalDurationAtLandmarkInMinutes;
    }

    /**
     * Sets the value of the totalDurationAtLandmarkInMinutes property.
     * 
     */
    public void setTotalDurationAtLandmarkInMinutes(long value) {
        this.totalDurationAtLandmarkInMinutes = value;
    }

}
