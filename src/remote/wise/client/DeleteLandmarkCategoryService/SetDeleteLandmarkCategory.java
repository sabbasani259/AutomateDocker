
package remote.wise.client.DeleteLandmarkCategoryService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetDeleteLandmarkCategory complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetDeleteLandmarkCategory">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="setDeleteLandmarkCategory" type="{http://webservice.service.wise.remote/}deleteLandmarkCategoryRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetDeleteLandmarkCategory", propOrder = {
    "setDeleteLandmarkCategory"
})
public class SetDeleteLandmarkCategory {

    protected DeleteLandmarkCategoryRespContract setDeleteLandmarkCategory;

    /**
     * Gets the value of the setDeleteLandmarkCategory property.
     * 
     * @return
     *     possible object is
     *     {@link DeleteLandmarkCategoryRespContract }
     *     
     */
    public DeleteLandmarkCategoryRespContract getSetDeleteLandmarkCategory() {
        return setDeleteLandmarkCategory;
    }

    /**
     * Sets the value of the setDeleteLandmarkCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeleteLandmarkCategoryRespContract }
     *     
     */
    public void setSetDeleteLandmarkCategory(DeleteLandmarkCategoryRespContract value) {
        this.setDeleteLandmarkCategory = value;
    }

}
