
package remote.wise.EAintegration.clientPackage.ZonalInformationService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for setZonalDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="setZonalDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="zonalName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="zonalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fileRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="process" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reprocessJobCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setZonalDetails", propOrder = {
    "zonalName",
    "zonalCode",
    "messageId",
    "fileRef",
    "process",
    "reprocessJobCode"
})
public class SetZonalDetails {

    protected String zonalName;
    protected String zonalCode;
    protected String messageId;
    protected String fileRef;
    protected String process;
    protected String reprocessJobCode;

    /**
     * Gets the value of the zonalName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZonalName() {
        return zonalName;
    }

    /**
     * Sets the value of the zonalName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZonalName(String value) {
        this.zonalName = value;
    }

    /**
     * Gets the value of the zonalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZonalCode() {
        return zonalCode;
    }

    /**
     * Sets the value of the zonalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZonalCode(String value) {
        this.zonalCode = value;
    }

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the fileRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileRef() {
        return fileRef;
    }

    /**
     * Sets the value of the fileRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileRef(String value) {
        this.fileRef = value;
    }

    /**
     * Gets the value of the process property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcess() {
        return process;
    }

    /**
     * Sets the value of the process property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcess(String value) {
        this.process = value;
    }

    /**
     * Gets the value of the reprocessJobCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReprocessJobCode() {
        return reprocessJobCode;
    }

    /**
     * Sets the value of the reprocessJobCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReprocessJobCode(String value) {
        this.reprocessJobCode = value;
    }

}
