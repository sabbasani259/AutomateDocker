
package remote.wise.client.AssetDashboardService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for assetDashboardReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assetDashboardReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alertSeverityList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="alertTypeIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="landmarkCategoryIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="landmarkIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineGroupIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineGroupTypeIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="machineProfileIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="modelList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ownStock" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="pageNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "assetDashboardReqContract", propOrder = {
    "alertSeverityList",
    "alertTypeIdList",
    "landmarkCategoryIdList",
    "landmarkIdList",
    "loginId",
    "machineGroupIdList",
    "machineGroupTypeIdList",
    "machineProfileIdList",
    "modelList",
    "ownStock",
    "pageNumber",
    "serialNumber",
    "tenancyIdList",
    "userTenancyIdList"
})
public class AssetDashboardReqContract {

    @XmlElement(nillable = true)
    protected List<String> alertSeverityList;
    @XmlElement(nillable = true)
    protected List<Integer> alertTypeIdList;
    @XmlElement(nillable = true)
    protected List<Integer> landmarkCategoryIdList;
    @XmlElement(nillable = true)
    protected List<Integer> landmarkIdList;
    protected String loginId;
    @XmlElement(nillable = true)
    protected List<Integer> machineGroupIdList;
    @XmlElement(nillable = true)
    protected List<Integer> machineGroupTypeIdList;
    @XmlElement(nillable = true)
    protected List<Integer> machineProfileIdList;
    @XmlElement(nillable = true)
    protected List<Integer> modelList;
    protected boolean ownStock;
    protected int pageNumber;
    protected String serialNumber;
    @XmlElement(nillable = true)
    protected List<Integer> tenancyIdList;
    @XmlElement(nillable = true)
    protected List<Integer> userTenancyIdList;

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
	 * @param alertSeverityList the alertSeverityList to set
	 */
	public void setAlertSeverityList(List<String> alertSeverityList) {
		this.alertSeverityList = alertSeverityList;
	}

	/**
	 * @param alertTypeIdList the alertTypeIdList to set
	 */
	public void setAlertTypeIdList(List<Integer> alertTypeIdList) {
		this.alertTypeIdList = alertTypeIdList;
	}

	/**
	 * @param landmarkCategoryIdList the landmarkCategoryIdList to set
	 */
	public void setLandmarkCategoryIdList(List<Integer> landmarkCategoryIdList) {
		this.landmarkCategoryIdList = landmarkCategoryIdList;
	}

	/**
	 * @param landmarkIdList the landmarkIdList to set
	 */
	public void setLandmarkIdList(List<Integer> landmarkIdList) {
		this.landmarkIdList = landmarkIdList;
	}

	/**
	 * @param machineGroupIdList the machineGroupIdList to set
	 */
	public void setMachineGroupIdList(List<Integer> machineGroupIdList) {
		this.machineGroupIdList = machineGroupIdList;
	}

	/**
	 * @param machineGroupTypeIdList the machineGroupTypeIdList to set
	 */
	public void setMachineGroupTypeIdList(List<Integer> machineGroupTypeIdList) {
		this.machineGroupTypeIdList = machineGroupTypeIdList;
	}

	/**
	 * @param machineProfileIdList the machineProfileIdList to set
	 */
	public void setMachineProfileIdList(List<Integer> machineProfileIdList) {
		this.machineProfileIdList = machineProfileIdList;
	}

	/**
	 * @param modelList the modelList to set
	 */
	public void setModelList(List<Integer> modelList) {
		this.modelList = modelList;
	}

	/**
	 * @param tenancyIdList the tenancyIdList to set
	 */
	public void setTenancyIdList(List<Integer> tenancyIdList) {
		this.tenancyIdList = tenancyIdList;
	}

	/**
	 * @param userTenancyIdList the userTenancyIdList to set
	 */
	public void setUserTenancyIdList(List<Integer> userTenancyIdList) {
		this.userTenancyIdList = userTenancyIdList;
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
     * Gets the value of the machineGroupTypeIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machineGroupTypeIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachineGroupTypeIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getMachineGroupTypeIdList() {
        if (machineGroupTypeIdList == null) {
            machineGroupTypeIdList = new ArrayList<Integer>();
        }
        return this.machineGroupTypeIdList;
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
     * Gets the value of the modelList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modelList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModelList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getModelList() {
        if (modelList == null) {
            modelList = new ArrayList<Integer>();
        }
        return this.modelList;
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
