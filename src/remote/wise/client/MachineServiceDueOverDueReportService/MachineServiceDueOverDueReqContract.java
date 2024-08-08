
package remote.wise.client.MachineServiceDueOverDueReportService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for machineServiceDueOverDueReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="machineServiceDueOverDueReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="loggedInTenancyId" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineGroupIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineProfileIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="modelIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="period" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "machineServiceDueOverDueReqContract", propOrder = {
    "loggedInTenancyId",
    "machineGroupIdList",
    "machineProfileIdList",
    "modelIdList",
    "period"
})
public class MachineServiceDueOverDueReqContract {

    @XmlElement(nillable = true)
    protected List<Integer> loggedInTenancyId;
    @XmlElement(nillable = true)
    protected List<Integer> machineGroupIdList;
    @XmlElement(nillable = true)
    protected List<Integer> machineProfileIdList;
    @XmlElement(nillable = true)
    protected List<Integer> modelIdList;
    protected String period;

    /**
     * Gets the value of the loggedInTenancyId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the loggedInTenancyId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLoggedInTenancyId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getLoggedInTenancyId() {
        if (loggedInTenancyId == null) {
            loggedInTenancyId = new ArrayList<Integer>();
        }
        return this.loggedInTenancyId;
    }

    /**
     * Gets the value of the machineGroupIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machineGroupIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachineGroupIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getMachineGroupIdList() {
        if (machineGroupIdList == null) {
            machineGroupIdList = new ArrayList<Integer>();
        }
        return this.machineGroupIdList;
    }

    /**
     * Gets the value of the machineProfileIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machineProfileIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachineProfileIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getMachineProfileIdList() {
        if (machineProfileIdList == null) {
            machineProfileIdList = new ArrayList<Integer>();
        }
        return this.machineProfileIdList;
    }

    /**
     * Gets the value of the modelIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modelIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModelIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getModelIdList() {
        if (modelIdList == null) {
            modelIdList = new ArrayList<Integer>();
        }
        return this.modelIdList;
    }

    /**
     * Gets the value of the period property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeriod() {
        return period;
    }

    /**
     * Sets the value of the period property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeriod(String value) {
        this.period = value;
    }

	public void setLoggedInTenancyId(List<Integer> loggedInTenancyId) {
		this.loggedInTenancyId = loggedInTenancyId;
	}

	public void setMachineGroupIdList(List<Integer> machineGroupIdList) {
		this.machineGroupIdList = machineGroupIdList;
	}

	public void setMachineProfileIdList(List<Integer> machineProfileIdList) {
		this.machineProfileIdList = machineProfileIdList;
	}

	public void setModelIdList(List<Integer> modelIdList) {
		this.modelIdList = modelIdList;
	}

}
