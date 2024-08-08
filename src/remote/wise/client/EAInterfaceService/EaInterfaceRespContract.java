
package remote.wise.client.EAInterfaceService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eaInterfaceRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eaInterfaceRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="interfaceFileName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="interfaceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processedRecord" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="reasonForRejection" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rejectedRecord" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eaInterfaceRespContract", propOrder = {
    "fileName",
    "interfaceFileName",
    "interfaceName",
    "processedRecord",
    "reasonForRejection",
    "rejectedRecord",
    "status"
})
public class EaInterfaceRespContract {

    protected String fileName;
    protected String interfaceFileName;
    protected String interfaceName;
    protected int processedRecord;
    protected String reasonForRejection;
    protected int rejectedRecord;
    protected String status;

    /**
     * Gets the value of the fileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Gets the value of the interfaceFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterfaceFileName() {
        return interfaceFileName;
    }

    /**
     * Sets the value of the interfaceFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterfaceFileName(String value) {
        this.interfaceFileName = value;
    }

    /**
     * Gets the value of the interfaceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * Sets the value of the interfaceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterfaceName(String value) {
        this.interfaceName = value;
    }

    /**
     * Gets the value of the processedRecord property.
     * 
     */
    public int getProcessedRecord() {
        return processedRecord;
    }

    /**
     * Sets the value of the processedRecord property.
     * 
     */
    public void setProcessedRecord(int value) {
        this.processedRecord = value;
    }

    /**
     * Gets the value of the reasonForRejection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReasonForRejection() {
        return reasonForRejection;
    }

    /**
     * Sets the value of the reasonForRejection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonForRejection(String value) {
        this.reasonForRejection = value;
    }

    /**
     * Gets the value of the rejectedRecord property.
     * 
     */
    public int getRejectedRecord() {
        return rejectedRecord;
    }

    /**
     * Sets the value of the rejectedRecord property.
     * 
     */
    public void setRejectedRecord(int value) {
        this.rejectedRecord = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

}
