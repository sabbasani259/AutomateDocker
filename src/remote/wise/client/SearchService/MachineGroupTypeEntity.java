
package remote.wise.client.SearchService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for machineGroupTypeEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="machineGroupTypeEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="machineGroupList" type="{http://webservice.service.wise.remote/}machineGroupEntity" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineGroupTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="machineGroupTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "machineGroupTypeEntity", propOrder = {
    "machineGroupList",
    "machineGroupTypeId",
    "machineGroupTypeName"
})
public class MachineGroupTypeEntity {

    @XmlElement(nillable = true)
    protected List<MachineGroupEntity> machineGroupList;
    protected int machineGroupTypeId;
    protected String machineGroupTypeName;

    /**
     * Gets the value of the machineGroupList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machineGroupList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachineGroupList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MachineGroupEntity }
     * 
     * 
     */
    public List<MachineGroupEntity> getMachineGroupList() {
        if (machineGroupList == null) {
            machineGroupList = new ArrayList<MachineGroupEntity>();
        }
        return this.machineGroupList;
    }

    /**
     * Gets the value of the machineGroupTypeId property.
     * 
     */
    public int getMachineGroupTypeId() {
        return machineGroupTypeId;
    }

    /**
     * Sets the value of the machineGroupTypeId property.
     * 
     */
    public void setMachineGroupTypeId(int value) {
        this.machineGroupTypeId = value;
    }

    /**
     * Gets the value of the machineGroupTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineGroupTypeName() {
        return machineGroupTypeName;
    }

    /**
     * Sets the value of the machineGroupTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineGroupTypeName(String value) {
        this.machineGroupTypeName = value;
    }

}
