
package remote.wise.client.MapService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mapReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="mapReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alertSeverityList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="alertTypeIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="landmarkCategory_IdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="landmark_IdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loginUserTenancyList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineGroupIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineProfileIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="modelIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ownStock" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="serialNumberList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tenancy_ID" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mapReqContract", propOrder = {
    "alertSeverityList",
    "alertTypeIdList",
    "landmarkCategoryIdList",
    "landmarkIdList",
    "loginId",
    "loginUserTenancyList",
    "machineGroupIdList",
    "machineProfileIdList",
    "modelIdList",
    "ownStock",
    "serialNumberList",
    "tenancyID"
})
public class MapReqContract {

    @XmlElement(nillable = true)
    protected List<String> alertSeverityList;
    @XmlElement(nillable = true)
    protected List<Integer> alertTypeIdList;
    @XmlElement(name = "landmarkCategory_IdList", nillable = true)
    protected List<Integer> landmarkCategoryIdList;
    @XmlElement(name = "landmark_IdList", nillable = true)
    protected List<Integer> landmarkIdList;
    protected String loginId;
    @XmlElement(nillable = true)
    protected List<Integer> loginUserTenancyList;
    @XmlElement(nillable = true)
    protected List<Integer> machineGroupIdList;
    @XmlElement(nillable = true)
    protected List<Integer> machineProfileIdList;
    @XmlElement(nillable = true)
    protected List<Integer> modelIdList;
    protected boolean ownStock;
    @XmlElement(nillable = true)
    protected List<String> serialNumberList;
    @XmlElement(name = "tenancy_ID", nillable = true)
    protected List<Integer> tenancyID;

    /**
     * Gets the value of the alertSeverityList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alertSeverityList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlertSeverityList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAlertSeverityList() {
        if (alertSeverityList == null) {
            alertSeverityList = new ArrayList<String>();
        }
        return this.alertSeverityList;
    }

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
     * Gets the value of the landmarkCategoryIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the landmarkCategoryIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLandmarkCategoryIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getLandmarkCategoryIdList() {
        if (landmarkCategoryIdList == null) {
            landmarkCategoryIdList = new ArrayList<Integer>();
        }
        return this.landmarkCategoryIdList;
    }

    /**
     * Gets the value of the landmarkIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the landmarkIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLandmarkIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getLandmarkIdList() {
        if (landmarkIdList == null) {
            landmarkIdList = new ArrayList<Integer>();
        }
        return this.landmarkIdList;
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
     * Gets the value of the loginUserTenancyList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the loginUserTenancyList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLoginUserTenancyList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getLoginUserTenancyList() {
        if (loginUserTenancyList == null) {
            loginUserTenancyList = new ArrayList<Integer>();
        }
        return this.loginUserTenancyList;
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
     * Gets the value of the ownStock property.
     * 
     */
    public boolean isOwnStock() {
        return ownStock;
    }

    /**
     * Sets the value of the ownStock property.
     * 
     */
    public void setOwnStock(boolean value) {
        this.ownStock = value;
    }

    /**
     * Gets the value of the serialNumberList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serialNumberList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSerialNumberList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSerialNumberList() {
        if (serialNumberList == null) {
            serialNumberList = new ArrayList<String>();
        }
        return this.serialNumberList;
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

}
