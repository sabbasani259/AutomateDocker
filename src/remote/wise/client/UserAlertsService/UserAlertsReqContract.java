
package remote.wise.client.UserAlertsService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for userAlertsReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="userAlertsReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alertSeverity" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="alertTypeId" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="history" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ownTenancyAlerts" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="pageNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="roleId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="userTenancyIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userAlertsReqContract", propOrder = {
    "alertSeverity",
    "alertTypeId",
    "history",
    "loginId",
    "ownTenancyAlerts",
    "pageNumber",
    "roleId",
    "serialNumber",
    "startDate",
    "tenancyIdList",
    "userTenancyIdList"
})
public class UserAlertsReqContract {

    @XmlElement(nillable = true)
    protected List<String> alertSeverity;
    @XmlElement(nillable = true)
    protected List<Integer> alertTypeId;
    protected boolean history;
    protected String loginId;
    protected boolean ownTenancyAlerts;
    protected int pageNumber;
    protected int roleId;
    protected String serialNumber;
    protected String startDate;
    @XmlElement(nillable = true)
    protected List<Integer> tenancyIdList;
    @XmlElement(nillable = true)
    protected List<Integer> userTenancyIdList;

    /**
     * Gets the value of the alertSeverity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alertSeverity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlertSeverity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAlertSeverity() {
        if (alertSeverity == null) {
            alertSeverity = new ArrayList<String>();
        }
        return this.alertSeverity;
    }

    /**
     * Gets the value of the alertTypeId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alertTypeId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlertTypeId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getAlertTypeId() {
        if (alertTypeId == null) {
            alertTypeId = new ArrayList<Integer>();
        }
        return this.alertTypeId;
    }

    /**
     * Gets the value of the history property.
     * 
     */
    public boolean isHistory() {
        return history;
    }

    /**
     * Sets the value of the history property.
     * 
     */
    public void setHistory(boolean value) {
        this.history = value;
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
     * Gets the value of the ownTenancyAlerts property.
     * 
     */
    public boolean isOwnTenancyAlerts() {
        return ownTenancyAlerts;
    }

    /**
     * Sets the value of the ownTenancyAlerts property.
     * 
     */
    public void setOwnTenancyAlerts(boolean value) {
        this.ownTenancyAlerts = value;
    }

    /**
     * Gets the value of the pageNumber property.
     * 
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets the value of the pageNumber property.
     * 
     */
    public void setPageNumber(int value) {
        this.pageNumber = value;
    }

    /**
     * Gets the value of the roleId property.
     * 
     */
    public int getRoleId() {
        return roleId;
    }

    /**
     * Sets the value of the roleId property.
     * 
     */
    public void setRoleId(int value) {
        this.roleId = value;
    }

    /**
     * Gets the value of the serialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the value of the serialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNumber(String value) {
        this.serialNumber = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDate(String value) {
        this.startDate = value;
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
     * Gets the value of the userTenancyIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userTenancyIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserTenancyIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getUserTenancyIdList() {
        if (userTenancyIdList == null) {
            userTenancyIdList = new ArrayList<Integer>();
        }
        return this.userTenancyIdList;
    }

}
