
package remote.wise.client.LandmarkDetailsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for landmarkDetailsReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="landmarkDetailsReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="landmark_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "landmarkDetailsReqContract", propOrder = {
    "landmarkId",
    "loginId",
    "tenancyID"
})
public class LandmarkDetailsReqContract {

    @XmlElement(name = "landmark_id")
    protected int landmarkId;
    @XmlElement(name = "login_id")
    protected String loginId;
    @XmlElement(name = "tenancy_ID")
    protected int tenancyID;

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
