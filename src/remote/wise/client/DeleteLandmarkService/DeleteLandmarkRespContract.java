
package remote.wise.client.DeleteLandmarkService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteLandmarkRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteLandmarkRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="landmark_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteLandmarkRespContract", propOrder = {
    "landmarkId"
})
public class DeleteLandmarkRespContract {

    @XmlElement(name = "landmark_id")
    protected int landmarkId;

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

}
