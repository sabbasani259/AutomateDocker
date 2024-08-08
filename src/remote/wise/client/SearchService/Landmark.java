
package remote.wise.client.SearchService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for landmark complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="landmark">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="landmarkID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="landmarkName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "landmark", propOrder = {
    "landmarkID",
    "landmarkName"
})
public class Landmark {

    protected int landmarkID;
    protected String landmarkName;

    /**
     * Gets the value of the landmarkID property.
     * 
     */
    public int getLandmarkID() {
        return landmarkID;
    }

    /**
     * Sets the value of the landmarkID property.
     * 
     */
    public void setLandmarkID(int value) {
        this.landmarkID = value;
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

}
