
package remote.wise.client.DeleteLandmarkCategoryService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteLandmarkCategoryRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteLandmarkCategoryRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="landmark_Category_id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteLandmarkCategoryRespContract", propOrder = {
    "landmarkCategoryId"
})
public class DeleteLandmarkCategoryRespContract {

    @XmlElement(name = "landmark_Category_id")
    protected int landmarkCategoryId;

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

}
