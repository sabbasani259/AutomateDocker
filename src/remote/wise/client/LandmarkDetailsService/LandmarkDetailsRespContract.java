
package remote.wise.client.LandmarkDetailsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for landmarkDetailsRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="landmarkDetailsRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isArrival" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="isDeparture" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="landmark_Category_ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="landmark_Category_Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="landmark_Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="landmark_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="latitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="login_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="longitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="radius" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "landmarkDetailsRespContract", propOrder = {
    "address",
    "isArrival",
    "isDeparture",
    "landmarkCategoryID",
    "landmarkCategoryName",
    "landmarkName",
    "landmarkId",
    "latitude",
    "loginId",
    "longitude",
    "radius"
})
public class LandmarkDetailsRespContract {

    protected String address;
    protected int isArrival;
    protected int isDeparture;
    @XmlElement(name = "landmark_Category_ID")
    protected int landmarkCategoryID;
    @XmlElement(name = "landmark_Category_Name")
    protected String landmarkCategoryName;
    @XmlElement(name = "landmark_Name")
    protected String landmarkName;
    @XmlElement(name = "landmark_id")
    protected int landmarkId;
    protected String latitude;
    @XmlElement(name = "login_id")
    protected String loginId;
    protected String longitude;
    protected double radius;

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the isArrival property.
     * 
     */
    public int getIsArrival() {
        return isArrival;
    }

    /**
     * Sets the value of the isArrival property.
     * 
     */
    public void setIsArrival(int value) {
        this.isArrival = value;
    }

    /**
     * Gets the value of the isDeparture property.
     * 
     */
    public int getIsDeparture() {
        return isDeparture;
    }

    /**
     * Sets the value of the isDeparture property.
     * 
     */
    public void setIsDeparture(int value) {
        this.isDeparture = value;
    }

    /**
     * Gets the value of the landmarkCategoryID property.
     * 
     */
    public int getLandmarkCategoryID() {
        return landmarkCategoryID;
    }

    /**
     * Sets the value of the landmarkCategoryID property.
     * 
     */
    public void setLandmarkCategoryID(int value) {
        this.landmarkCategoryID = value;
    }

    /**
     * Gets the value of the landmarkCategoryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLandmarkCategoryName() {
        return landmarkCategoryName;
    }

    /**
     * Sets the value of the landmarkCategoryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLandmarkCategoryName(String value) {
        this.landmarkCategoryName = value;
    }

    /**
     * Gets the value of the landmarkName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLandmarkName() {
        return landmarkName;
    }

    /**
     * Sets the value of the landmarkName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLandmarkName(String value) {
        this.landmarkName = value;
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
     * Gets the value of the latitude property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLatitude(String value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the loginId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * Sets the value of the loginId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoginId(String value) {
        this.loginId = value;
    }

    /**
     * Gets the value of the longitude property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLongitude(String value) {
        this.longitude = value;
    }

    /**
     * Gets the value of the radius property.
     * 
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Sets the value of the radius property.
     * 
     */
    public void setRadius(double value) {
        this.radius = value;
    }

}
