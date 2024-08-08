
package remote.wise.client.DeleteLandmarkService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetDeleteLandmark complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetDeleteLandmark">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespObj" type="{http://webservice.service.wise.remote/}deleteLandmarkRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetDeleteLandmark", propOrder = {
    "respObj"
})
public class SetDeleteLandmark {

    @XmlElement(name = "RespObj")
    protected DeleteLandmarkRespContract respObj;

    /**
     * Gets the value of the respObj property.
     * 
     * @return
     *     possible object is
     *     {@link DeleteLandmarkRespContract }
     *     
     */
    public DeleteLandmarkRespContract getRespObj() {
        return respObj;
    }

    /**
     * Sets the value of the respObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeleteLandmarkRespContract }
     *     
     */
    public void setRespObj(DeleteLandmarkRespContract value) {
        this.respObj = value;
    }

}
