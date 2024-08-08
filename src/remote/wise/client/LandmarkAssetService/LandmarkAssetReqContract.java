
package remote.wise.client.LandmarkAssetService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for landmarkAssetReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="landmarkAssetReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="landmark_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="login_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serial_numbers" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "landmarkAssetReqContract", propOrder = {
    "landmarkId",
    "loginId",
    "serialNumbers"
})
public class LandmarkAssetReqContract {

    @XmlElement(name = "landmark_id")
    protected int landmarkId;
    @XmlElement(name = "login_id")
    protected String loginId;
    @XmlElement(name = "serial_numbers", nillable = true)
    protected List<String> serialNumbers;

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
     * Gets the value of the serialNumbers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serialNumbers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSerialNumbers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSerialNumbers() {
        if (serialNumbers == null) {
            serialNumbers = new ArrayList<String>();
        }
        return this.serialNumbers;
    }

}
