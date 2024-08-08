
package remote.wise.client.MachineActivityReportService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for machineActivityReportReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="machineActivityReportReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fromDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="groupingOnAssetGroup" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loginTenancyIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineGroupType_ID" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineGroup_ID" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineProfile_ID" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="maxThreshold" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="minThreshold" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="model_ID" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="period" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancy_ID" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="toDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "machineActivityReportReqContract", propOrder = {
    "fromDate",
    "groupingOnAssetGroup",
    "loginId",
    "loginTenancyIdList",
    "machineGroupTypeID",
    "machineGroupID",
    "machineProfileID",
    "maxThreshold",
    "minThreshold",
    "modelID",
    "period",
    "tenancyID",
    "toDate"
})
public class MachineActivityReportReqContract {

    protected String fromDate;
    protected boolean groupingOnAssetGroup;
    protected String loginId;
    @XmlElement(nillable = true)
    protected List<Integer> loginTenancyIdList;
    @XmlElement(name = "machineGroupType_ID", nillable = true)
    protected List<Integer> machineGroupTypeID;
    @XmlElement(name = "machineGroup_ID", nillable = true)
    protected List<Integer> machineGroupID;
    @XmlElement(name = "machineProfile_ID", nillable = true)
    protected List<Integer> machineProfileID;
    protected int maxThreshold;
    protected int minThreshold;
    @XmlElement(name = "model_ID", nillable = true)
    protected List<Integer> modelID;
    protected String period;
    @XmlElement(name = "tenancy_ID", nillable = true)
    protected List<Integer> tenancyID;
    protected String toDate;

    /**
     * Gets the value of the fromDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromDate() {
        return fromDate;
    }

    /**
     * Sets the value of the fromDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromDate(String value) {
        this.fromDate = value;
    }

    /**
     * Gets the value of the groupingOnAssetGroup property.
     * 
     */
    public boolean isGroupingOnAssetGroup() {
        return groupingOnAssetGroup;
    }

    /**
     * Sets the value of the groupingOnAssetGroup property.
     * 
     */
    public void setGroupingOnAssetGroup(boolean value) {
        this.groupingOnAssetGroup = value;
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
     * Gets the value of the loginTenancyIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the loginTenancyIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLoginTenancyIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getLoginTenancyIdList() {
        if (loginTenancyIdList == null) {
            loginTenancyIdList = new ArrayList<Integer>();
        }
        return this.loginTenancyIdList;
    }

    /**
     * Gets the value of the machineGroupTypeID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machineGroupTypeID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachineGroupTypeID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getMachineGroupTypeID() {
        if (machineGroupTypeID == null) {
            machineGroupTypeID = new ArrayList<Integer>();
        }
        return this.machineGroupTypeID;
    }

    /**
     * Gets the value of the machineGroupID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machineGroupID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachineGroupID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getMachineGroupID() {
        if (machineGroupID == null) {
            machineGroupID = new ArrayList<Integer>();
        }
        return this.machineGroupID;
    }

    /**
     * Gets the value of the machineProfileID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machineProfileID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachineProfileID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getMachineProfileID() {
        if (machineProfileID == null) {
            machineProfileID = new ArrayList<Integer>();
        }
        return this.machineProfileID;
    }

    /**
     * Gets the value of the maxThreshold property.
     * 
     */
    public int getMaxThreshold() {
        return maxThreshold;
    }

    /**
     * Sets the value of the maxThreshold property.
     * 
     */
    public void setMaxThreshold(int value) {
        this.maxThreshold = value;
    }

    /**
     * Gets the value of the minThreshold property.
     * 
     */
    public int getMinThreshold() {
        return minThreshold;
    }

    /**
     * Sets the value of the minThreshold property.
     * 
     */
    public void setMinThreshold(int value) {
        this.minThreshold = value;
    }

    /**
     * Gets the value of the modelID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modelID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModelID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getModelID() {
        if (modelID == null) {
            modelID = new ArrayList<Integer>();
        }
        return this.modelID;
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

    /**
     * Gets the value of the tenancyID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tenancyID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTenancyID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getTenancyID() {
        if (tenancyID == null) {
            tenancyID = new ArrayList<Integer>();
        }
        return this.tenancyID;
    }

    /**
     * Gets the value of the toDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToDate() {
        return toDate;
    }

    /**
     * Sets the value of the toDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToDate(String value) {
        this.toDate = value;
    }

}
