
package remote.wise.client.SearchService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mgLandMarkRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mgLandMarkRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="landmarkCategoryList" type="{http://webservice.service.wise.remote/}landmarkCategory" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineGroupTypeList" type="{http://webservice.service.wise.remote/}machineGroupTypeEntity" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mgLandMarkRespContract", propOrder = {
    "landmarkCategoryList",
    "machineGroupTypeList"
})
public class MgLandMarkRespContract {

    @XmlElement(nillable = true)
    protected List<LandmarkCategory> landmarkCategoryList;
    @XmlElement(nillable = true)
    protected List<MachineGroupTypeEntity> machineGroupTypeList;

    /**
     * Gets the value of the landmarkCategoryList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the landmarkCategoryList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLandmarkCategoryList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LandmarkCategory }
     * 
     * 
     */
    public List<LandmarkCategory> getLandmarkCategoryList() {
        if (landmarkCategoryList == null) {
            landmarkCategoryList = new ArrayList<LandmarkCategory>();
        }
        return this.landmarkCategoryList;
    }

    /**
     * Gets the value of the machineGroupTypeList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machineGroupTypeList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachineGroupTypeList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MachineGroupTypeEntity }
     * 
     * 
     */
    public List<MachineGroupTypeEntity> getMachineGroupTypeList() {
        if (machineGroupTypeList == null) {
            machineGroupTypeList = new ArrayList<MachineGroupTypeEntity>();
        }
        return this.machineGroupTypeList;
    }

}
