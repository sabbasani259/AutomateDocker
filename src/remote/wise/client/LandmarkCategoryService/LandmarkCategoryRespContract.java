
package remote.wise.client.LandmarkCategoryService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for landmarkCategoryRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="landmarkCategoryRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="color_Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="landmark_Category_Color_Code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="landmark_Category_ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="landmark_Category_Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="login_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancy_ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "landmarkCategoryRespContract", propOrder = {
    "colorCode",
    "landmarkCategoryColorCode",
    "landmarkCategoryID",
    "landmarkCategoryName",
    "loginId",
    "tenancyID"
})
public class LandmarkCategoryRespContract {

    @XmlElement(name = "color_Code")
    protected String colorCode;
    @XmlElement(name = "landmark_Category_Color_Code")
    protected String landmarkCategoryColorCode;
    @XmlElement(name = "landmark_Category_ID")
    protected int landmarkCategoryID;
    @XmlElement(name = "landmark_Category_Name")
    protected String landmarkCategoryName;
    @XmlElement(name = "login_id")
    protected String loginId;
    @XmlElement(name = "tenancy_ID")
    protected int tenancyID;

    /**
     * Gets the value of the colorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * Sets the value of the colorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColorCode(String value) {
        this.colorCode = value;
    }

    /**
     * Gets the value of the landmarkCategoryColorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLandmarkCategoryColorCode() {
        return landmarkCategoryColorCode;
    }

    /**
     * Sets the value of the landmarkCategoryColorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLandmarkCategoryColorCode(String value) {
        this.landmarkCategoryColorCode = value;
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
     * Gets the value of the tenancyID property.
     * 
     */
    public int getTenancyID() {
        return tenancyID;
    }

    /**
     * Sets the value of the tenancyID property.
     * 
     */
    public void setTenancyID(int value) {
        this.tenancyID = value;
    }

}
