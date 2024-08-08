
package remote.wise.client.SearchService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for landmarkCategory complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="landmarkCategory">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="landmark" type="{http://webservice.service.wise.remote/}landmark" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="landmarkCategoryId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="landmarkCategoryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "landmarkCategory", propOrder = {
    "landmark",
    "landmarkCategoryId",
    "landmarkCategoryName"
})
public class LandmarkCategory {

    @XmlElement(nillable = true)
    protected List<Landmark> landmark;
    protected int landmarkCategoryId;
    protected String landmarkCategoryName;

    /**
     * Gets the value of the landmark property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the landmark property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLandmark().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Landmark }
     * 
     * 
     */
    public List<Landmark> getLandmark() {
        if (landmark == null) {
            landmark = new ArrayList<Landmark>();
        }
        return this.landmark;
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

}
