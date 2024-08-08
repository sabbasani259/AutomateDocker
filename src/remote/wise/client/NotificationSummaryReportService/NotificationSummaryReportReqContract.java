
package remote.wise.client.NotificationSummaryReportService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for notificationSummaryReportReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="notificationSummaryReportReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alertTypeIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="fromDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loginTenancyIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineGroup" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="machineGroupIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineProfile" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="machineProfileIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="model" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="modelList" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelidList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="period" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "notificationSummaryReportReqContract", propOrder = {
    "alertTypeIdList",
    "fromDate",
    "loginId",
    "loginTenancyIdList",
    "machineGroup",
    "machineGroupIdList",
    "machineProfile",
    "machineProfileIdList",
    "model",
    "modelList",
    "modelidList",
    "period",
    "tenancyIdList",
    "toDate"
})
public class NotificationSummaryReportReqContract {

    @XmlElement(nillable = true)
    protected List<Integer> alertTypeIdList;
    protected String fromDate;
    protected String loginId;
    @XmlElement(nillable = true)
    protected List<Integer> loginTenancyIdList;
    protected boolean machineGroup;
    @XmlElement(nillable = true)
    protected List<Integer> machineGroupIdList;
    protected boolean machineProfile;
    @XmlElement(nillable = true)
    protected List<Integer> machineProfileIdList;
    protected boolean model;
    protected String modelList;
    @XmlElement(nillable = true)
    protected List<Integer> modelidList;
    protected String period;
    @XmlElement(nillable = true)
    protected List<Integer> tenancyIdList;
    protected String toDate;

    /**
     * Gets the value of the alertTypeIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alertTypeIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlertTypeIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getAlertTypeIdList() {
        if (alertTypeIdList == null) {
            alertTypeIdList = new ArrayList<Integer>();
        }
        return this.alertTypeIdList;
    }

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
     * Gets the value of the machineGroup property.
     * 
     */
    public boolean isMachineGroup() {
        return machineGroup;
    }

    /**
     * Sets the value of the machineGroup property.
     * 
     */
    public void setMachineGroup(boolean value) {
        this.machineGroup = value;
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
     * Gets the value of the machineProfile property.
     * 
     */
    public boolean isMachineProfile() {
        return machineProfile;
    }

    /**
     * Sets the value of the machineProfile property.
     * 
     */
    public void setMachineProfile(boolean value) {
        this.machineProfile = value;
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
     * Gets the value of the model property.
     * 
     */
    public boolean isModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     */
    public void setModel(boolean value) {
        this.model = value;
    }

    /**
     * Gets the value of the modelList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelList() {
        return modelList;
    }

    /**
     * Sets the value of the modelList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelList(String value) {
        this.modelList = value;
    }

    /**
     * Gets the value of the modelidList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modelidList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModelidList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getModelidList() {
        if (modelidList == null) {
            modelidList = new ArrayList<Integer>();
        }
        return this.modelidList;
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
     * Gets the value of the tenancyIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tenancyIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTenancyIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getTenancyIdList() {
        if (tenancyIdList == null) {
            tenancyIdList = new ArrayList<Integer>();
        }
        return this.tenancyIdList;
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
