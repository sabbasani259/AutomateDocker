
package remote.wise.client.RolledOffMachinesService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.logging.log4j.Logger;




/**
 * <p>Java class for rolledOffMachinesRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rolledOffMachinesRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="engineName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="engineTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="enginehours" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fuelLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IMEINumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastReportedTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="latitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="longitude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="machineName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="simNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rolledOffMachinesRespContract", propOrder = {
    "assetGroupId",
    "assetTypeId",
    "engineName",
    "engineTypeId",
    "enginehours",
    "fuelLevel",
    "imeiNumber",
    "lastReportedTime",
    "latitude",
    "longitude",
    "machineName",
    "modelName",
    "profileName",
    "regDate",
    "serialNumber",
    "simNumber",
    //DF20200429 - Zakir : New coloumn update over new machine tab
    "comment",
  //CR481-20240731-Sai Divya-Status,proposedFWVersion,fWVersion columns are added to NMT
//    "status",
//    "proposedFWVersion",
//    "fWVersion"
})
public class RolledOffMachinesRespContract {

    protected int assetGroupId;
    protected int assetTypeId;
    protected String engineName;
    protected int engineTypeId;
    protected String enginehours;
    protected String fuelLevel;
    @XmlElement(name = "IMEINumber")
    protected String imeiNumber;
    protected String lastReportedTime;
    protected String latitude;
    protected String longitude;
    protected String machineName;
    protected String modelName;
    protected String profileName;
    protected String regDate;
    protected String serialNumber;
    protected String simNumber;
  //DF20200429 - Zakir : New coloumn update over new machine tab
    private String comment;
  //CR481-20240731-Sai Divya-Status,proposedFWVersion,fWVersion columns are added to NMT
//    private String status;
//	private String proposedFWVersion;
//	private String fWVersion;
//
//    public String getfWVersion() {
//		return fWVersion;
//	}
//	public void setfWVersion(String fWVersion) {
//		this.fWVersion = fWVersion;
//	}
//	public String getStatus() {
//		return status;
//	}
//	public void setStatus(String status) {
//		this.status = status;
//	}
//	public String getProposedFWVersion() {
//		return proposedFWVersion;
//	}
//	public void setProposedFWVersion(String proposedFWVersion) {
//		this.proposedFWVersion = proposedFWVersion;
//	}
	
	public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
	 * @return the imeiNumber
	 */
	public String getImeiNumber() {
		return imeiNumber;
	}

	/**
	 * @param imeiNumber the imeiNumber to set
	 */
	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	/**
     * Gets the value of the assetGroupId property.
     * 
     */
    public int getAssetGroupId() {
        return assetGroupId;
    }

    /**
     * Sets the value of the assetGroupId property.
     * 
     */
    public void setAssetGroupId(int value) {
        this.assetGroupId = value;
    }

    /**
     * Gets the value of the assetTypeId property.
     * 
     */
    public int getAssetTypeId() {
        return assetTypeId;
    }

    /**
     * Sets the value of the assetTypeId property.
     * 
     */
    public void setAssetTypeId(int value) {
        this.assetTypeId = value;
    }

    /**
     * Gets the value of the engineName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEngineName() {
        return engineName;
    }

    /**
     * Sets the value of the engineName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEngineName(String value) {
        this.engineName = value;
    }

    /**
     * Gets the value of the engineTypeId property.
     * 
     */
    public int getEngineTypeId() {
        return engineTypeId;
    }

    /**
     * Sets the value of the engineTypeId property.
     * 
     */
    public void setEngineTypeId(int value) {
        this.engineTypeId = value;
    }

    /**
     * Gets the value of the enginehours property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnginehours() {
        return enginehours;
    }

    /**
     * Sets the value of the enginehours property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnginehours(String value) {
        this.enginehours = value;
    }

    /**
     * Gets the value of the fuelLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFuelLevel() {
        return fuelLevel;
    }

    /**
     * Sets the value of the fuelLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFuelLevel(String value) {
        this.fuelLevel = value;
    }

    /**
     * Gets the value of the imeiNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMEINumber() {
        return imeiNumber;
    }

    /**
     * Sets the value of the imeiNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMEINumber(String value) {
        this.imeiNumber = value;
    }

    /**
     * Gets the value of the lastReportedTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastReportedTime() {
        return lastReportedTime;
    }

    /**
     * Sets the value of the lastReportedTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastReportedTime(String value) {
        this.lastReportedTime = value;
    }

    /**
     * Gets the value of the latitude property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLatitude(String value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the longitude property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLongitude(String value) {
        this.longitude = value;
    }

    /**
     * Gets the value of the machineName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachineName() {
        return machineName;
    }

    /**
     * Sets the value of the machineName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachineName(String value) {
        this.machineName = value;
    }

    /**
     * Gets the value of the modelName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the value of the modelName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelName(String value) {
        this.modelName = value;
    }

    /**
     * Gets the value of the profileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * Sets the value of the profileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfileName(String value) {
        this.profileName = value;
    }

    /**
     * Gets the value of the regDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegDate() {
        return regDate;
    }

    /**
     * Sets the value of the regDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegDate(String value) {
        this.regDate = value;
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
     * Gets the value of the simNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSimNumber() {
        return simNumber;
    }

    /**
     * Sets the value of the simNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSimNumber(String value) {
        this.simNumber = value;
    }
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((fWVersion == null) ? 0 : fWVersion.hashCode());
//		result = prime * result + ((proposedFWVersion == null) ? 0 : proposedFWVersion.hashCode());
//		result = prime * result + ((status == null) ? 0 : status.hashCode());
//		return result;
//	}
//	@Override
//	public String toString() {
//		return "RolledOffMachinesRespContract [comment=" + comment + ", status=" + status + ", proposedFWVersion="
//				+ proposedFWVersion + ", fWVersion=" + fWVersion + "]";
//	}
	
	
    

}
