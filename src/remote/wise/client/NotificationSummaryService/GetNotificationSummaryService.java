
package remote.wise.client.NotificationSummaryService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetNotificationSummaryService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetNotificationSummaryService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="reqCont" type="{http://webservice.service.wise.remote/}notificationSummaryReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetNotificationSummaryService", propOrder = {
    "reqCont"
})
public class GetNotificationSummaryService {

    protected NotificationSummaryReqContract reqCont;

    /**
     * Gets the value of the reqCont property.
     * 
     * @return
     *     possible object is
     *     {@link NotificationSummaryReqContract }
     *     
     */
    public NotificationSummaryReqContract getReqCont() {
        return reqCont;
    }

    /**
     * Sets the value of the reqCont property.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationSummaryReqContract }
     *     
     */
    public void setReqCont(NotificationSummaryReqContract value) {
        this.reqCont = value;
    }

}
